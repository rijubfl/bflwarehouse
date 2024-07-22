package com.bflgroup.warehouse.ui.shopreturns;

public class ReceiveShopReturnsScanItemTicket {
    public String itemCode;
    public int scanQty;
    public int trfQty;
    public int diffQty;

    public ReceiveShopReturnsScanItemTicket(String itemCode, int scanQty, int trfQty, int diffQty) {
        this.itemCode = itemCode;
        this.scanQty = scanQty;
        this.trfQty = trfQty;
        this.diffQty = diffQty;
    }
}
