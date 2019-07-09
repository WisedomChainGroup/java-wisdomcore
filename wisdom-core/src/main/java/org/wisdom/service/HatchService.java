package org.wisdom.service;

public interface HatchService {

    long getBalance(String pubkeyhash);

    long getNonce(String pubkeyhash);

    Object getTransfer(int height);

    Object getHatch(int height);

    Object getInterest(int height);

    Object getShare(int height);
}
