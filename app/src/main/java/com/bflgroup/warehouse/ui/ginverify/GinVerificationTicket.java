package com.bflgroup.warehouse.ui.ginverify;

public class GinVerificationTicket {
    public String ginNo;
    public String shopName;
    public String palletNo;
    public String trfNo;
    public String toteId;
    public String verified;


    public GinVerificationTicket(String ginNo,String shopName,String palletNo,String trfNo,String toteId,String verified) {
        this.ginNo = ginNo;
        this.shopName = shopName;
        this.palletNo = palletNo;
        this.trfNo = trfNo;
        this.toteId = toteId;
        this.verified = verified;
    }
}
