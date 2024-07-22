package com.bflgroup.warehouse.ui.transfervalidation;

public class ValidateTransferScanItems {
    public String itemCode;
    public int scanQty;

    public ValidateTransferScanItems(String itemCode, int scanQty) {
        this.itemCode = itemCode;
        this.scanQty = scanQty;
    }
}
