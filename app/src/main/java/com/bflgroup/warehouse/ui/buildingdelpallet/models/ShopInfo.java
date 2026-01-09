package com.bflgroup.warehouse.ui.buildingdelpallet.models;

public class ShopInfo {
    private String shopName;
    private String dataName;

    public ShopInfo(String shopName, String dataName) {
        this.shopName = shopName;
        this.dataName = dataName;
    }

    public String getShopName() { return shopName; }
    public String getDataName() { return dataName; }
}
