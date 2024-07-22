package com.bflgroup.warehouse.ui.warehousegrnnew;

public class WarehouseGRNNewScanCountTicket {
    public String palletno;
    public int totBox;
    public int scanBox;
    public int diff;
    public WarehouseGRNNewScanCountTicket(String palletno, int totBox, int scanBox, int diff) {
        this.palletno = palletno;
        this.totBox = totBox;
        this.scanBox = scanBox;
        this.diff = diff;
    }
}
