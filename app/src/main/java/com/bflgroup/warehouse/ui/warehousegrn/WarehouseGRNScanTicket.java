package com.bflgroup.warehouse.ui.warehousegrn;

public class WarehouseGRNScanTicket {
    public String palletno;
    public String boxNo;
    public String toteID;
    public WarehouseGRNScanTicket(String palletno, String boxNo, String toteID) {
        this.palletno = palletno;
        this.boxNo = boxNo;
        this.toteID = toteID;
    }
}
