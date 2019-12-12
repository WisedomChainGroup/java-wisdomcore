package org.wisdom.contract;

public interface RLPBeanInterface {

    byte[] RLPserialization();

    boolean RLPdeserialization(byte[] payload);
}
