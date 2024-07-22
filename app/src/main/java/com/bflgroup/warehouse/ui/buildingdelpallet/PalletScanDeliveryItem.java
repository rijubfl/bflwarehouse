package com.bflgroup.warehouse.ui.buildingdelpallet;

public class PalletScanDeliveryItem {
    public String TransferNo;
    public String Toteid;
    public String ShopName;
    public String Qty;

    public PalletScanDeliveryItem(String transferNo, String toteid, String shopName, String qty) {
        TransferNo = transferNo;
        Toteid = toteid;
        ShopName = shopName;
        Qty = qty;
    }

}
