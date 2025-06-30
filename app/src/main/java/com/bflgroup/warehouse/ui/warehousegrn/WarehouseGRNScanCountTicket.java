package com.bflgroup.warehouse.ui.warehousegrn;

public class WarehouseGRNScanCountTicket {
    public String palletno;
    public int totBox;
    public int scanBox;
    public int diff;
    public WarehouseGRNScanCountTicket(String palletno, int totBox, int scanBox, int diff) {
        this.palletno = palletno;
        this.totBox = totBox;
        this.scanBox = scanBox;
        this.diff = diff;
    }
}
