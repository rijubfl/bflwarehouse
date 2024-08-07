package com.bflgroup.warehouse.ui.palletbuilding;

public class PalletBuildingGlobal {
    public static PalletBuildingGlobal instance;
    private static String palletNo;
    private static String palletSno;
    private static int totQty;
    private static int totCnt;

    private static String pPalletno;
    private static String pBoxcnt;
    private static String pRemarks;
    private static String pPallettype;
    private static String pTypename;
    private static String pGroupname;
    private static String pPreparedby;
    private static String pDate;
    private static String pTime;

    public String getPalletNo() {
        return palletNo;
    }

    public void setPalletNo(String palletNo) {
        PalletBuildingGlobal.palletNo = palletNo;
    }

    public static String getPalletSno() {
        return palletSno;
    }

    public static void setPalletSno(String palletSno) {
        PalletBuildingGlobal.palletSno = palletSno;
    }

    public static int getTotQty() {
        return totQty;
    }

    public static void setTotQty(int totQty) {
        PalletBuildingGlobal.totQty = totQty;
    }

    public static int getTotCnt() {
        return totCnt;
    }

    public static void setTotCnt(int totCnt) {
        PalletBuildingGlobal.totCnt = totCnt;
    }

    public static String getpPalletno() {
        return pPalletno;
    }

    public static void setpPalletno(String pPalletno) {
        PalletBuildingGlobal.pPalletno = pPalletno;
    }

    public static String getpBoxcnt() {
        return pBoxcnt;
    }

    public static void setpBoxcnt(String pBoxcnt) {
        PalletBuildingGlobal.pBoxcnt = pBoxcnt;
    }

    public static String getpRemarks() {
        return pRemarks;
    }

    public static void setpRemarks(String pRemarks) {
        PalletBuildingGlobal.pRemarks = pRemarks;
    }

    public static String getpPallettype() {
        return pPallettype;
    }

    public static void setpPallettype(String pPallettype) {
        PalletBuildingGlobal.pPallettype = pPallettype;
    }

    public static String getpTypename() {
        return pTypename;
    }

    public static void setpTypename(String pTypename) {
        PalletBuildingGlobal.pTypename = pTypename;
    }

    public static String getpGroupname() {
        return pGroupname;
    }

    public static void setpGroupname(String pGroupname) {
        PalletBuildingGlobal.pGroupname = pGroupname;
    }

    public static String getpPreparedby() {
        return pPreparedby;
    }

    public static void setpPreparedby(String pPreparedby) {
        PalletBuildingGlobal.pPreparedby = pPreparedby;
    }

    public static String getpDate() {
        return pDate;
    }

    public static void setpDate(String pDate) {
        PalletBuildingGlobal.pDate = pDate;
    }

    public static String getpTime() {
        return pTime;
    }

    public static void setpTime(String pTime) {
        PalletBuildingGlobal.pTime = pTime;
    }

    public static synchronized PalletBuildingGlobal getInstance() {
        if (instance == null) {
            instance = new PalletBuildingGlobal();
        }
        return instance;
    }
}
