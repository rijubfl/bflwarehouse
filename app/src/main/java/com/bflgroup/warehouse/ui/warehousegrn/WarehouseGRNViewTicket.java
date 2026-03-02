package com.bflgroup.warehouse.ui.warehousegrn;

public class WarehouseGRNViewTicket {
    public String palletno;
    public String boxNo;
    public String toteID;
    public int scanCount;
    public WarehouseGRNViewTicket(String palletno, String boxNo, String toteID, int scanCount) {
        this.palletno = palletno;
        this.boxNo = boxNo;
        this.toteID = toteID;
        this.scanCount = scanCount;
    }
}
