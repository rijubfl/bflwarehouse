package com.bflgroup.warehouse.ui.palletsverify;

public class PalletsVerificationTicket {
    public String ginNo;

    public String palletNo;


    public String toteId;
    public String verified;


    public PalletsVerificationTicket(String ginNo, String palletNo,  String toteId,  String verified) {
        this.ginNo = ginNo;
        this.palletNo = palletNo;


        this.toteId = toteId;
        this.verified = verified;
    }
}
