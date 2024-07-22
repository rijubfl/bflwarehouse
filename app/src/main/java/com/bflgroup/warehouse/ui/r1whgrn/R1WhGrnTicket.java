package com.bflgroup.warehouse.ui.r1whgrn;

public class R1WhGrnTicket {
    public String trfNo;
    public String storeIssue;
    public String trfDate;
    public String qty;
    public String pltStatus;
    public String forSave;

    public R1WhGrnTicket(String trfNo, String storeIssue, String trfDate, String qty, String pltStatus,String forSave) {
        this.trfNo = trfNo;
        this.storeIssue = storeIssue;
        this.trfDate = trfDate;
        this.qty = qty;
        this.pltStatus = pltStatus;
        this.forSave = forSave;
    }
}
