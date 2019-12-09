package org.wisdom.contract;

public class Asset {

    private final static String Asset_changeowner="changeowner";
    private final static String Asset_transfer="transfer";
    private final static String Asset_increased="increased";

    private String code;
    private long offering;
    private long totalamount;
    private byte[] createuser;
    private byte[] owner;
    private int allowincrease;

}
