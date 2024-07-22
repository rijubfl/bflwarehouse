package com.bflgroup.warehouse.ui.binstoragebatchin;

public class BinBatchInScanToteTicket {
    public String toteId;
    public String boxNo;
    public String stime;
    public String remarks;

    public BinBatchInScanToteTicket(String toteId, String boxNo, String stime, String remarks) {
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.stime = stime;
        this.remarks = remarks;
    }
}
