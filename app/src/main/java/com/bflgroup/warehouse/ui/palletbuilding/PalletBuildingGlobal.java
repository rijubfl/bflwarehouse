package com.bflgroup.warehouse.ui.palletbuilding;

public class PalletBuildingGlobal {
    public static PalletBuildingGlobal instance;
    private static String palletNo;
    private static String palletSno;
    private static int totQty;
    private static int totCnt;

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

    public static synchronized PalletBuildingGlobal getInstance() {
        if (instance == null) {
            instance = new PalletBuildingGlobal();
        }
        return instance;
    }
}
