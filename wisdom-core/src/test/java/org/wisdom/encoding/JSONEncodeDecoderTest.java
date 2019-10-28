package org.wisdom.encoding;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.ArrayList;
import java.util.List;

public class JSONEncodeDecoderTest {
    private static final JSONEncodeDecoder encodeDecoder = new JSONEncodeDecoder();
    private static final byte[] testHash = HashUtil.sha256("abc".getBytes());

    private Block getTestBlock(){
        Block b = new Block();
        b.nVersion = BigEndian.MAX_UINT_32;
        b.hashPrevBlock = testHash;
        b.hashMerkleRoot = testHash;
        b.hashMerkleState = testHash;
        b.nHeight = BigEndian.MAX_UINT_32;
        b.nTime = BigEndian.MAX_UINT_32;
        b.nBits = BigEndian.encodeUint256(BigEndian.MAX_UINT_256);
        b.nNonce = testHash;
        return b;
    }

    @Test
    public void testHeaderToJson(){
        System.out.println(
                new String(encodeDecoder.encodeBlock(getTestBlock()))
        );
    }

    @Test
    public void testHeaderDecodeJson() throws Exception{
        Resource resource = new ClassPathResource("genesis/wisdom-encoding-test-header.json");
        Block header = encodeDecoder.decodeBlock(IOUtils.toByteArray(resource.getInputStream()));
        assert header.nVersion == 4294967295L;
        assert header.nHeight == 4294967295L;
        assert header.nTime == 4294967295L;
        assert Hex.encodeHexString(header.hashMerkleRoot).equals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        System.out.println(
                new String(encodeDecoder.encodeBlock(header))
        );
    }

    @Test
    public void testBlockToJson(){
        JSONEncodeDecoder encodeDecoder = new JSONEncodeDecoder();
        Block block = getTestBlock();
        System.out.println(
                new String(encodeDecoder.encodeBlock(block))
        );
    }

    @Test
    public void testBlockDecodeJson() throws Exception{
        Resource resource = new ClassPathResource("genesis/wisdom-encoding-test-block.json");
        Block  header = encodeDecoder.decodeBlock(IOUtils.toByteArray(resource.getInputStream()));
        assert header.nHeight  == 0;
        System.out.println(
                new String(encodeDecoder.encodeBlock(header))
        );
    }

    @Test
    public void testEncodeBlockAsHeader(){
        JSONEncodeDecoder encodeDecoder = new JSONEncodeDecoder();
        Block block = getTestBlock();
        System.out.println(
                new String(encodeDecoder.encodeBlock(block))
        );
    }

    @Test
    public void testEncodeHashes(){
        List<byte[]> hashes = new ArrayList<>();
        hashes.add(testHash);
        hashes.add(testHash);
        hashes.add(testHash);
        System.out.println(new String(encodeDecoder.encodeHashes(hashes)));
    }

    @Test
    public void testdecodeHashes(){
        List<byte[]> hashes = new ArrayList<>();
        hashes.add(testHash);
        hashes.add(testHash);
        hashes.add(testHash);
        System.out.println(new String(encodeDecoder.encodeHashes(encodeDecoder.decodeHashes(encodeDecoder.encodeHashes(hashes)))));
    }

    @Test
    public void test3() throws Exception{
        System.out.println(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(Hex.decodeHex("7ddf093b90c62b0b698f6ee1e78c499ed38fabaf15fe5f78aac6e20def11f051")))));
    }
}
