package com.bflgroup.warehouse.ui.binstorageputaway;

public class BinPutAwayPendingToteIdTicket {

    public String batchId;
    public String toteId;
    public String boxNo;
    public String stime;
    public String remarks;

    public BinPutAwayPendingToteIdTicket(String batchId, String toteId, String boxNo, String stime, String remarks) {
        this.batchId = batchId;
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.stime = stime;
        this.remarks = remarks;
    }
}
