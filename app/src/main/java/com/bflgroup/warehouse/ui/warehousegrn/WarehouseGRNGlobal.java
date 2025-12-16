package com.bflgroup.warehouse.ui.warehousegrn;
public class WarehouseGRNGlobal {

    public static WarehouseGRNGlobal instance;

    private String ginDate;
    private String warehouseFrom;
    private String warehouseTo;
    private String autoPost;
    private String palletWise;
    private int totalPallets;
    private int scanPallets;
    private int totalBoxes;
    private int scanBoxes;

    public String getGinDate() {
        return ginDate;
    }

    public void setGinDate(String ginDate) {
        this.ginDate = ginDate;
    }

    public String getWarehouseFrom() {
        return warehouseFrom;
    }

    public void setWarehouseFrom(String warehouseFrom) {
        this.warehouseFrom = warehouseFrom;
    }

    public String getWarehouseTo() {
        return warehouseTo;
    }

    public String getAutoPost() {
        return autoPost;
    }

    public void setAutoPost(String autoPost) {
        this.autoPost = autoPost;
    }

    public String getPalletWise() {
        return palletWise;
    }

    public void setPalletWise(String palletWise) {
        this.palletWise = palletWise;
    }

    public void setWarehouseTo(String warehouseTo) {
        this.warehouseTo = warehouseTo;
    }

    public int getTotalPallets() {
        return totalPallets;
    }

    public void setTotalPallets(int totalPallets) {
        this.totalPallets = totalPallets;
    }

    public int getScanPallets() {
        return scanPallets;
    }

    public void setScanPallets(int scanPallets) {
        this.scanPallets = scanPallets;
    }

    public int getTotalBoxes() {
        return totalBoxes;
    }

    public void setTotalBoxes(int totalBoxes) {
        this.totalBoxes = totalBoxes;
    }

    public int getScanBoxes() {
        return scanBoxes;
    }

    public void setScanBoxes(int scanBoxes) {
        this.scanBoxes = scanBoxes;
    }

    public static synchronized WarehouseGRNGlobal getInstance() {
        if (instance == null) {
            instance = new WarehouseGRNGlobal();
        }
        return instance;
    }

}