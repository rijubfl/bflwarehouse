package com.bflgroup.warehouse.ui.transfervalidation;

public class ValidateTransferScanItemsAll {
    public String itemCode;
    public int trfQty;
    public int scanQty;
    public int diffQty;

    public ValidateTransferScanItemsAll(String itemCode, int scanQty, int trfQty, int diffQty) {
        this.itemCode = itemCode;
        this.trfQty = trfQty;
        this.scanQty = scanQty;
        this.diffQty = diffQty;
    }
}
