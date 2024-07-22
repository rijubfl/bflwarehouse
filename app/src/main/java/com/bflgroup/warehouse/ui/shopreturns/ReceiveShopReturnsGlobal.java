package com.bflgroup.warehouse.ui.shopreturns;

public class ReceiveShopReturnsGlobal {

    private String shopName;
    private String category;
    private String scanItemDescription;
    private String scanItemGroup;
    private String scanItemDepartment;
    private String scanItemDivision;
    private String autoBuildPalletType;
    private int totalScanQty;
    private int totalTrfQty;
    private int totalDiffQty;
    private int totalExcess;
    private int totalMissing;

    private String boxNo;

    public static ReceiveShopReturnsGlobal instance;

    public static synchronized ReceiveShopReturnsGlobal getInstance() {
        if (instance == null) {
            instance = new ReceiveShopReturnsGlobal();
        }
        return instance;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getScanItemDescription() {
        return scanItemDescription;
    }

    public void setScanItemDescription(String scanItemDescription) {
        this.scanItemDescription = scanItemDescription;
    }

    public String getScanItemGroup() {
        return scanItemGroup;
    }

    public void setScanItemGroup(String scanItemGroup) {
        this.scanItemGroup = scanItemGroup;
    }

    public int getTotalScanQty() {
        return totalScanQty;
    }

    public void setTotalScanQty(int totalScanQty) {
        this.totalScanQty = totalScanQty;
    }

    public int getTotalTrfQty() {
        return totalTrfQty;
    }

    public void setTotalTrfQty(int totalTrfQty) {
        this.totalTrfQty = totalTrfQty;
    }

    public int getTotalDiffQty() {
        return totalDiffQty;
    }

    public void setTotalDiffQty(int totalDiffQty) {
        this.totalDiffQty = totalDiffQty;
    }

    public int getTotalExcess() {
        return totalExcess;
    }

    public void setTotalExcess(int totalExcess) {
        this.totalExcess = totalExcess;
    }

    public int getTotalMissing() {
        return totalMissing;
    }

    public void setTotalMissing(int totalMissing) {
        this.totalMissing = totalMissing;
    }

    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }

    public String getScanItemDepartment() {
        return scanItemDepartment;
    }

    public void setScanItemDepartment(String scanItemDepartment) {
        this.scanItemDepartment = scanItemDepartment;
    }

    public String getScanItemDivision() {
        return scanItemDivision;
    }

    public String getAutoBuildPalletType() {
        return autoBuildPalletType;
    }

    public void setAutoBuildPalletType(String autoBuildPalletType) {
        this.autoBuildPalletType = autoBuildPalletType;
    }

    public void setScanItemDivision(String scanItemDivision) {
        this.scanItemDivision = scanItemDivision;
    }
}
