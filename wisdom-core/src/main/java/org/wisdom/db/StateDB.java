package org.wisdom.db;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.core.Block;
import org.wisdom.core.BlocksCache;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.NewBlockEvent;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.core.state.StateFactory;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
public class StateDB implements ApplicationListener<AccountUpdatedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(StateDB.class);
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();
    private static final int BLOCKS_PER_UPDATE_LOWER_BOUNDS = 4096;

    public StateFactory getValidatorStateFactory() {
        return validatorStateFactory;
    }

    public EraLinkedStateFactory getTargetStateFactory() {
        return targetStateFactory;
    }

    private StateFactory validatorStateFactory;
    private EraLinkedStateFactory targetStateFactory;

    public ProposersFactory getProposersFactory() {
        return proposersFactory;
    }

    private ProposersFactory proposersFactory;


    // 区块相对于已持久化的账本产生状态变更的账户
    // block hash -> public key hash -> account
    private Map<String, Map<String, AccountState>> cache;

    // 最新确认的区块
    private Block latestConfirmed;

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final int CACHE_SIZE = 32;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private AccountDB accountDB;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private Block genesis;

    @Value("${wisdom.consensus.blocks-per-era}")
    int blocksPerEra;

    @Value("${wisdom.consensus.block-confirms}")
    private int blockConfirms;

    private ReadWriteLock readWriteLock;

    // 写入但未确认的区块
    private BlocksCache blocksCache;

    // 正在同步状态到数据库的区块
    private Block pendingBlock;

    // 等待写入的区块
    private BlocksCache writableBlocks;

    @Override
    public void onApplicationEvent(AccountUpdatedEvent event) {
        this.readWriteLock.writeLock().lock();
        try {
            if (Arrays.equals(event.getBlock().getHash(), pendingBlock.getHash())) {
                // 接收到状态更新完成事件后，将这个区块标记为状态已更新完成
                // 清除缓存
                blocksCache.getAll()
                        .stream().filter(b -> b.nHeight <= pendingBlock.nHeight)
                        .forEach(b -> {
                            blocksCache.deleteBlock(b);
                            cache.remove(getLRUCacheKey(b.getHash()));
                        });
                latestConfirmed = pendingBlock;
                pendingBlock = null;
            }
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Autowired
    public StateDB(
            ValidatorState validatorState,
            TargetState targetState,
            ProposersState proposersState,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${wisdom.consensus.pow-wait}")
                    int powWait,
            @Value("${miner.validators}") String validatorsFile,
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra
    ) throws Exception {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, AccountState>>()
                .maximumWeightedCapacity(CACHE_SIZE).build();
        this.blocksCache = new BlocksCache(CACHE_SIZE);
        this.writableBlocks = new BlocksCache(CACHE_SIZE);
        this.validatorStateFactory = new StateFactory(this, CACHE_SIZE, validatorState);
        this.targetStateFactory = new EraLinkedStateFactory(this, CACHE_SIZE, targetState, blocksPerEra);
        this.proposersFactory = new ProposersFactory(this, CACHE_SIZE, proposersState, blocksPerEra);
        this.proposersFactory.setPowWait(powWait);

        Resource resource;
        try {
            resource = new ClassPathResource(validatorsFile);
        } catch (Exception e) {
            resource = new FileSystemResource(validatorsFile);
        }


        this.proposersFactory.setInitialProposers(Arrays.stream(codec.decode(IOUtils.toByteArray(resource.getInputStream()), String[].class))
                .map(v -> {
                    try {
                        URI uri = new URI(v);
                        return Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList()));

        this.proposersFactory.setAllowMinerJoinEra(allowMinersJoinEra);
    }

    @PostConstruct
    public void init() {
        this.latestConfirmed = bc.getLastConfirmedBlock();
        Block last = genesis;
        int blocksPerUpdate = 0;
        while (blocksPerUpdate < BLOCKS_PER_UPDATE_LOWER_BOUNDS) {
            blocksPerUpdate += blocksPerEra;
        }
        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(last.nHeight + 1, blocksPerUpdate);
            if (blocks.size() < blocksPerUpdate) {
                break;
            }

            // TODO: remove assertion codes
            if (!Arrays.equals(last.getHash(), blocks.get(0).hashPrevBlock)) {
                logger.error("=================================== warning ======================");
            }
            while (blocks.size() > 0) {
                validatorStateFactory.initCache(last, blocks.subList(0, blocksPerEra));
                targetStateFactory.initCache(last, blocks.subList(0, blocksPerEra));
                proposersFactory.initCache(last, blocks.subList(0, blocksPerEra));
                last = blocks.get(blocksPerEra - 1);
                blocks = blocks.subList(blocksPerEra, blocks.size());
            }

        }
    }

    // 定时清除待写入队列
    @Scheduled(fixedDelay = 5000)
    public void writeLoop() {
        writableBlocks.getInitials()
                .stream()
                .findFirst()
                .ifPresent(b -> {
                    writableBlocks.deleteBlock(b);
                    writeBlock(b);
                });
    }

    public Block getBestBlock() {
        return blocksCache.getLeaves().stream()
                .max(Comparator.comparing(Block::getnHeight))
                .orElse(this.latestConfirmed);
    }

    public Block getHeader(byte[] hash) {
        if (Arrays.equals(latestConfirmed.getHash(), hash)) {
            return this.latestConfirmed;
        }
        return Optional.ofNullable(blocksCache.getBlock(hash))
                .orElseGet(() -> bc.getHeader(hash));
    }

    public Block findAncestorHeader(byte[] hash, long height) {
        Block bHeader = getHeader(hash);
        if (bHeader.nHeight < height) {
            return null;
        }
        while (bHeader.nHeight != height) {
            bHeader = getHeader(bHeader.hashPrevBlock);
        }
        return bHeader;
    }

    public List<Block> getAncestorBlocks(byte[] bhash, long anum) {
        Block b = blocksCache.getBlock(bhash);
        if (Arrays.equals(bhash, this.latestConfirmed.getHash())) {
            b = this.latestConfirmed;
        }
        if (b == null) {
            return bc.getAncestorBlocks(bhash, anum);
        }
        BlocksCache res = new BlocksCache();
        List<Block> blocks = blocksCache.getAncestors(b)
                .stream().filter(bl -> bl.nHeight >= anum).collect(Collectors.toList());
        res.addBlocks(Collections.singletonList(b));
        res.addBlocks(blocks);
        res.addBlocks(bc.getAncestorBlocks(res.getAll().get(0).hashPrevBlock, anum));
        List<Block> all = res.getAll();

        // TODO: remove code assertion
        if (all.size() != (b.nHeight - anum + 1) || all.get(0).nHeight != anum || !isChain(all)) {
            logger.error("fail!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            logger.info("success!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return all;
    }

    public static boolean isChain(List<Block> blocks) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            byte[] h1 = blocks.get(i).getHash();
            byte[] h2 = blocks.get(i + 1).hashPrevBlock;
            if (!Arrays.equals(h1, h2)) {
                return false;
            }
        }
        return true;
    }

    public Block getBlock(byte[] hash) {
        if (Arrays.equals(latestConfirmed.getHash(), hash)) {
            return this.latestConfirmed;
        }
        return Optional.ofNullable(blocksCache.getBlock(hash))
                .orElseGet(() -> bc.getBlock(hash));
    }

    public boolean hasBlock(byte[] hash) {
        return blocksCache.hasBlock(hash) ||
                Arrays.equals(latestConfirmed.getHash(), hash) ||
                bc.hasBlock(hash);
    }

    public boolean hasTransaction(byte[] blockHash, byte[] transactionHash) {
        readWriteLock.readLock().lock();
        try {
            return hasTransactionUnsafe(blockHash, transactionHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private boolean hasTransactionUnsafe(byte[] blockHash, byte[] transactionHash) {
        if (Arrays.equals(latestConfirmed.getHash(), blockHash)) {
            return bc.hasTransaction(transactionHash);
        }
        Block b = blocksCache.getBlock(blockHash);
        if (b == null) {
            return true;
        }
        if (b.body.stream().anyMatch(tx -> Arrays.equals(tx.getHash(), transactionHash))) {
            return true;
        }
        return hasTransactionUnsafe(b.hashPrevBlock, transactionHash);
    }

    public Block getLastConfirmed() {
        return latestConfirmed;
    }

    // 写入区块
    public void writeBlock(Block block) {
        this.readWriteLock.writeLock().lock();
        try {
            // 这个区块所在高度已经被确认了
            if (block.nHeight <= latestConfirmed.nHeight) {
                return;
            }
            // 判断是否是孤块
            if (!Arrays.equals(this.latestConfirmed.getHash(), block.hashPrevBlock) && !blocksCache.hasBlock(block.hashPrevBlock)) {
                return;
            }
            // 有区块正在更新状态 放到待写入队列中
            if (pendingBlock != null) {
                writableBlocks.addBlocks(Collections.singletonList(block));
                return;
            }
            writableBlocks.deleteBlock(block);
            blocksCache.addBlocks(Collections.singletonList(block));
            Optional<Block> confirmedBlock = blocksCache.getAncestors(block)
                    .stream().filter((b) -> b.nHeight == block.nHeight - blockConfirms)
                    .filter(b -> Arrays.equals(this.latestConfirmed.getHash(), b.hashPrevBlock))
                    .findFirst();
            confirmedBlock.ifPresent(b -> {
                // 被确认的区块不在主分支上面
                boolean writeResult = bc.writeBlock(b);
                if (!writeResult) {
                    // 区块写入失败
                    return;
                }
                pendingBlock = b;
                ctx.publishEvent(new NewBlockEvent(this, b));
                ctx.publishEvent(new NewBestBlockEvent(this, b));
                // 将这个区块标记为正在更新状态
            });
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    // 获取包含未确认的区块
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        this.readWriteLock.readLock().lock();
        try {
            BlocksCache c = new BlocksCache();
            List<Block> res = bc.getBlocks(startHeight, stopHeight, sizeLimit, clipInitial);
            if (res == null || res.size() >= sizeLimit) {
                return res;
            }
            c.addBlocks(res);
            blocksCache.getAll().stream()
                    .filter((b) -> b.nHeight >= startHeight && b.nHeight <= stopHeight)
                    .forEach((b) -> {
                        if (c.size() == sizeLimit) {
                            return;
                        }
                        c.addBlocks(Collections.singletonList(b));
                    });
            return c.getAll();
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }


    protected String getLRUCacheKey(byte[] hash) {
        return Hex.encodeHexString(hash);
    }

    public Map<String, AccountState> getAccountsUnsafe(byte[] blockHash, List<byte[]> publicKeyHashes) {
        Map<String, AccountState> res = new HashMap<>();
        for (byte[] h : publicKeyHashes) {
            AccountState account = getAccountUnsafe(blockHash, h);
            if (account == null) {
                continue;
            }
            res.put(Hex.encodeHexString(h), account);
        }
        return res;
    }

    public Map<String, AccountState> getAccounts(byte[] blockHash, List<byte[]> publicKeyHashes) {
        readWriteLock.writeLock().lock();
        try {
            return getAccountsUnsafe(blockHash, publicKeyHashes);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // 获取到某一区块（包含该区块)的某个账户的状态，用于对后续区块的事务进行验证
    public AccountState getAccountUnsafe(byte[] blockHash, byte[] publicKeyHash) {
        if (Arrays.equals(blockHash, latestConfirmed.getHash())) {
            return getAccount(publicKeyHash);
        }
        Block header = blocksCache.getBlock(blockHash);
        if (header == null || header.nHeight < latestConfirmed.nHeight) {
            return null;
        }
        // 判断新的区块是否在 main fork 上面;
//        Block ancestor = blocksCache.getAncestor(header, latestConfirmed.nHeight);
//        if (ancestor == null || !Arrays.equals(latestConfirmed.getHash(), ancestor.getHash())) {
//            return null;
//        }
        // 判断是否在缓存中
        String blockKey = getLRUCacheKey(blockHash);
        String accountKey = getLRUCacheKey(publicKeyHash);
        if (cache.containsKey(blockKey) && cache.get(blockKey).containsKey(accountKey)) {
            return cache.get(blockKey).get(accountKey).copy();
        }
        // 如果缓存不存在则进行回溯
        AccountState account = getAccountUnsafe(header.hashPrevBlock, publicKeyHash);
        if (account == null) {
            return null;
        }
        Block block = blocksCache.getBlock(blockHash);
        // 把这个区块的事务应用到上一个区块获取的 account，生成新的 account
        AccountState res = applyTransactions(block.body, account.copy());
        if (!cache.containsKey(blockKey)) {
            cache.put(blockKey, new ConcurrentHashMap<>());
        }
        cache.get(blockKey).put(accountKey, res);
        return res.copy();
    }

    // 获取已经持久化的账户
    public AccountState getAccount(byte[] publicKeyHash) {
        Account account = accountDB.selectaccount(publicKeyHash);
        if (account == null) {
            return null;
        }
        return new AccountState(account);
    }

    public List<Block> getAll() {
        readWriteLock.readLock().lock();
        try {
            return blocksCache.getAll();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private AccountState applyCoinbase(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        // 如果该账户不是 coinbase 地址退出
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyTransfer(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            account.setBalance(balance);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState applyIncubate(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.getFee();
            balance -= tx.amount;
            long incub = account.getIncubatecost();
            incub += tx.amount;
            account.setBalance(balance);
            account.setIncubatecost(incub);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(IncubatorAddress.resultpubhash(), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            long nonce = account.getNonce();
            nonce++;
            account.setBalance(balance);
            account.setNonce(nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState applyExtractInterest(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyExtractSharingProfit(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    // 调用前先执行深拷贝
    public AccountState applyTransaction(Transaction tx, AccountState accountState) throws Exception {
        int type = tx.type;
        switch (type) {
            case 0x00:
                return applyCoinbase(tx, accountState);
            case 0x01:
                return applyTransfer(tx, accountState);
            case 0x02:
                return applyVote(tx, accountState);
            case 0x03:
                return applyDeposit(tx, accountState);
            case 0x09:
                return applyIncubate(tx, accountState);
            case 0x0a:
                return applyExtractInterest(tx, accountState);
            case 0x0b:
                return applyExtractSharingProfit(tx, accountState);
            case 0x0c:
                return applyExtractCost(tx, accountState);
            case 0x0d:
                return applyCancelVote(tx, accountState);
            case 0x0e:
                return applyMortgage(tx, accountState);
            case 0x0f:
                return applyCancelMortgage(tx, accountState);
            default:
                throw new Exception("unsupported transaction type: " + Transaction.Type.values()[type].toString());
        }
    }

    private AccountState applyExtractCost(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long incub = account.getIncubatecost();
        incub -= tx.amount;
        account.setBalance(balance);
        account.setIncubatecost(incub);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyDeposit(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyVote(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote += tx.amount;
            account.setVote(vote);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState applyCancelVote(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote -= tx.amount;
            account.setVote(vote);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    public AccountState applyMortgage(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance -= tx.amount;
        long mortgage = account.getMortgage();
        mortgage += tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyCancelMortgage(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        long mortgage = account.getMortgage();
        mortgage -= tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    public AccountState applyTransactions(List<Transaction> txs, AccountState account) {
        for (Transaction transaction : txs) {
            try {
                if (account == null) {
                    return null;
                }
                account = applyTransaction(transaction, account);
            } catch (Exception e) {
                return null;
            }

        }
        return account;
    }
}
