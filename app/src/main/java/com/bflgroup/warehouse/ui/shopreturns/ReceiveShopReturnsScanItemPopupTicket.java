package com.bflgroup.warehouse.ui.shopreturns;

public class ReceiveShopReturnsScanItemPopupTicket {
    public String itemCode;
    public String actions;
    public int scanQty;

    public ReceiveShopReturnsScanItemPopupTicket(String itemCode, String actions, int scanQty) {
        this.itemCode = itemCode;
        this.actions = actions;
        this.scanQty = scanQty;
    }
}
