package com.bflgroup.warehouse.ui.transfer;

public class TransferScannedItems {
    public String itemcode;
    public String description;
    public int qty;

    public TransferScannedItems(String itemcode, String description, int qty) {
        this.itemcode = itemcode;
        this.description = description;
        this.qty = qty;
    }
}
