package com.bflgroup.warehouse.ui.usaboxbuilding;

public class UsaBoxBuildingGlobal {
    public static UsaBoxBuildingGlobal instance;
    private String boxNo;
    private String palletNo;
    private String palletSno;
    private String buildCategoryMixAllow;
    private String buildingCategory;
    private double scanTotalQty;
    private String scanDepartment;
    private String scanBuildingCategory;
    private String scanDivision;

    public String getBoxNo() { return boxNo; }
    public void setBoxNo(String boxNo) { this.boxNo = boxNo; }

    public static synchronized UsaBoxBuildingGlobal getInstance() {
        if (instance == null) {
            instance = new UsaBoxBuildingGlobal();
        }
        return instance;
    }

    public String getBuildCategoryMixAllow() {
        return buildCategoryMixAllow;
    }

    public void setBuildCategoryMixAllow(String buildCategoryMixAllow) {
        this.buildCategoryMixAllow = buildCategoryMixAllow;
    }

    public String getBuildingCategory() {
        return buildingCategory;
    }

    public void setBuildingCategory(String buildingCategory) {
        this.buildingCategory = buildingCategory;
    }

    public double getScanTotalQty() {
        return scanTotalQty;
    }

    public void setScanTotalQty(double scanTotalQty) {
        this.scanTotalQty = scanTotalQty;
    }

    public String getPalletNo() {
        return palletNo;
    }

    public void setPalletNo(String palletNo) {
        this.palletNo = palletNo;
    }

    public String getPalletSno() {
        return palletSno;
    }

    public void setPalletSno(String palletSno) {
        this.palletSno = palletSno;
    }

    public String getScanDivision() {
        return scanDivision;
    }

    public void setScanDivision(String scanDivision) {
        this.scanDivision = scanDivision;
    }

    public String getScanDepartment() {
        return scanDepartment;
    }

    public void setScanDepartment(String scanDepartment) {
        this.scanDepartment = scanDepartment;
    }

    public String getScanBuildingCategory() {
        return scanBuildingCategory;
    }

    public void setScanBuildingCategory(String scanBuildingCategory) {
        this.scanBuildingCategory = scanBuildingCategory;
    }
}
