package com.bflgroup.warehouse.ui.buildingdelgin;

public class PalletScanItem {
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public int routeId;
    public String shopname;

    public int getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(int palletCount) {
        this.palletCount = palletCount;
    }

    public int palletCount;
}
