package com.bflgroup.warehouse.ui.usaboxbuilding;

public class UsaBoxBuildingGlobal {
    public static UsaBoxBuildingGlobal instance;
    private String boxNo;
    private String palletNo;
    private String palletSno;
    private String buildCategoryMixAllow;
    private String buildSpecialPtype;
    private String buildingCategory;
    private String buildingSeason;
    private double scanTotalQty;
    private String scanDepartment;
    private String scanBuildingCategory;
    private String scanDivision;
    private String needBlueBox;
    private String validateHoStock;
    private String allowInvalidItem;

    private String pBoxno;
    private String pToteid;
    private String pPallettype;
    private String pTypename;
    private String pQty;
    private String pDate;
    private String pTime;
    private String pRemarks;
    private String pPreparedby;


    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }

    public static synchronized UsaBoxBuildingGlobal getInstance() {
        if (instance == null) {
            instance = new UsaBoxBuildingGlobal();
        }
        return instance;
    }

    public String getpToteid() {
        return pToteid;
    }

    public void setpToteid(String pToteid) {
        this.pToteid = pToteid;
    }

    public String getpBoxno() {
        return pBoxno;
    }

    public void setpBoxno(String pBoxno) {
        this.pBoxno = pBoxno;
    }

    public String getpPallettype() {
        return pPallettype;
    }

    public void setpPallettype(String pPallettype) {
        this.pPallettype = pPallettype;
    }

    public String getpTypename() {
        return pTypename;
    }

    public void setpTypename(String pTypename) {
        this.pTypename = pTypename;
    }

    public String getpQty() {
        return pQty;
    }

    public void setpQty(String pQty) {
        this.pQty = pQty;
    }

    public String getpDate() {
        return pDate;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpRemarks() {
        return pRemarks;
    }

    public void setpRemarks(String pRemarks) {
        this.pRemarks = pRemarks;
    }

    public String getpPreparedby() {
        return pPreparedby;
    }

    public void setpPreparedby(String pPreparedby) {
        this.pPreparedby = pPreparedby;
    }

    public String getNeedBlueBox() {
        return needBlueBox;
    }

    public void setNeedBlueBox(String needBlueBox) {
        this.needBlueBox = needBlueBox;
    }

    public String getValidateHoStock() {
        return validateHoStock;
    }

    public void setValidateHoStock(String validateHoStock) {
        this.validateHoStock = validateHoStock;
    }

    public String getBuildSpecialPtype() {
        return buildSpecialPtype;
    }

    public void setBuildSpecialPtype(String buildSpecialPtype) {
        this.buildSpecialPtype = buildSpecialPtype;
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

    public String getBuildingSeason() {
        return buildingSeason;
    }

    public void setBuildingSeason(String buildingSeason) {
        this.buildingSeason = buildingSeason;
    }

    public String getAllowInvalidItem() {
        return allowInvalidItem;
    }

    public void setAllowInvalidItem(String allowInvalidItem) {
        this.allowInvalidItem = allowInvalidItem;
    }
}