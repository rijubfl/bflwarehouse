package com.bflgroup.warehouse.ui.binstorageputaway;

public class BinPutAwayHistoryTicket {
    public String toteId;
    public String boxNo;
    public String inout;
    public String location;
    public String time;

    public BinPutAwayHistoryTicket(String toteId, String boxNo, String inout,String location,String time) {
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.inout = inout;
        this.location =location;
        this.time = time;
    }
}
