package com.bflgroup.warehouse.ui.palletstatus;

public class PalletStatusGlobal {

    public static PalletStatusGlobal instance;
    private static String pltType;
    private static String boxno;
    private static String toteid;
    private static String palletno;

    private static  String status;
    private static  String buildcategory;
    private static String checkingType;
    private static String golden;
    public static String getPltType() {
        return pltType;
    }

    public static void setPltType(String pltType) {
        PalletStatusGlobal.pltType = pltType;
    }

    public static String getBoxno() {
        return boxno;
    }

    public static void setBoxno(String boxno) {
        PalletStatusGlobal.boxno = boxno;
    }

    public static String getToteid() {
        return toteid;
    }

    public static void setToteid(String toteid) {
        PalletStatusGlobal.toteid = toteid;
    }

    public static String getPalletno() {
        return palletno;
    }

    public static void setPalletno(String palletno) {
        PalletStatusGlobal.palletno = palletno;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        PalletStatusGlobal.status = status;
    }

    public static String getBuildcategory() {
        return buildcategory;
    }

    public static void setBuildcategory(String buildcategory) {
        PalletStatusGlobal.buildcategory = buildcategory;
    }

    public static String getCheckingType() {
        return checkingType;
    }

    public static void setCheckingType(String checkingType) {
        PalletStatusGlobal.checkingType = checkingType;
    }

    public static String getGolden() {
        return golden;
    }

    public static void setGolden(String golden) {
        PalletStatusGlobal.golden = golden;
    }

    public static synchronized PalletStatusGlobal getInstance() {
        if (instance == null) {
            instance = new PalletStatusGlobal();
        }
        return instance;
    }
}
