package org.wisdom.ipc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.command.Configuration;

@Component
public class IpcConfig {

    @Value("${wisdom.version}")
    private String version;

    @Value("${p2p.max-blocks-per-transfer}")
    private int maxPeers;

    @Value("${p2p.mode}")
    private String p2pMode;

    @Value("${wisdom.ipc-config.queued-max-size}")
    private int queuedMaxSize;

    @Value("${wisdom.ipc-config.pending-max-size}")
    private int pendingMaxSize;

    // queued到pending的写入周期
    @Value("${wisdom.ipc-config.queued_account_slot_value}")
    private int queuedAccountSlotValue;

    @Value("${wisdom.ipc-config.pending_account_slot_value}")
    private int pendingAccountSlotValue;

    @Value("${wisdom.ipc-config.lifetime}")
    private long tranLifetime;

    @Value("${wisdom.ipc-config.queued_to_pending_cycle}")
    private String queuedToPendingCycle;

    // 格式是 cron字符串
    @Value("${wisdom.ipc-config.clear-cycle}")
    private String clearCycle;

    @Value("${wisdom.ipc-config.local-only}")
    private boolean localOnly;

    @Autowired
    Configuration configuration;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMaxPeers() {
        return maxPeers;
    }

    public void setMaxPeers(int maxPeers) {
        this.maxPeers = maxPeers;
    }

    public String getP2pMode() {
        return p2pMode;
    }

    public void setP2pMode(String p2pMode) {
        this.p2pMode = p2pMode;
    }

    public int getQueuedMaxSize() {
        return queuedMaxSize;
    }

    public void setQueuedMaxSize(int queuedMaxSize) {
        this.queuedMaxSize = queuedMaxSize;
    }

    public int getPendingMaxSize() {
        return pendingMaxSize;
    }

    public void setPendingMaxSize(int pendingMaxSize) {
        this.pendingMaxSize = pendingMaxSize;
    }

    public int getQueuedAccountSlotValue() {
        return queuedAccountSlotValue;
    }

    public void setQueuedAccountSlotValue(int queuedAccountSlotValue) {
        this.queuedAccountSlotValue = queuedAccountSlotValue;
    }

    public int getPendingAccountSlotValue() {
        return pendingAccountSlotValue;
    }

    public void setPendingAccountSlotValue(int pendingAccountSlotValue) {
        this.pendingAccountSlotValue = pendingAccountSlotValue;
    }

    public long getTranLifetime() {
        return tranLifetime;
    }

    public void setTranLifetime(long tranLifetime) {
        this.tranLifetime = tranLifetime;
    }


    public void setFeeLimit(int feeLimit) {
        configuration.setMin_procedurefee(feeLimit);
    }

    public String getQueuedToPendingCycle() {
        return queuedToPendingCycle;
    }

    public void setQueuedToPendingCycle(String queuedToPendingCycle) {
        this.queuedToPendingCycle = queuedToPendingCycle;
    }

    public String getClearCycle() {
        return clearCycle;
    }

    public void setClearCycle(String clearCycle) {
        this.clearCycle = clearCycle;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }
}
