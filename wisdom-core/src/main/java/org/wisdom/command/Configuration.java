package org.wisdom.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    @Value("${transaction.gas.wdc}")
    private long gas_wds;

    @Value("${transaction.gas.deposit}")
    private long gas_deposit;

    @Value("${transaction.gas.hatch}")
    private long gas_hatch;

    @Value("${transaction.day.count}")
    private int day_count;

    @Value("${hatch.rate}")
    private double hatch_rate;

    @Value("${block.count}")
    private int block_count;

    @Value("${block.down120rate}")
    private double block_down120rate;

    @Value("${block.down365rate}")
    private double block_down365rate;

    @Value("${min.procedurefee}")
    private long min_procedurefee;

    public long getMin_procedurefee() {
        return min_procedurefee;
    }

    public long getGas_wds() {
        return gas_wds;
    }

    public long getGas_deposit() {
        return gas_deposit;
    }

    public long getGas_hatch() {
        return gas_hatch;
    }

    public int getDay_count() {
        return day_count;
    }

    public double getHatch_rate() {
        return hatch_rate;
    }

    public int getBlock_count() {
        return block_count;
    }

    public double getBlock_down120rate() {
        return block_down120rate;
    }

    public double getBlock_down365rate() {
        return block_down365rate;
    }
}
