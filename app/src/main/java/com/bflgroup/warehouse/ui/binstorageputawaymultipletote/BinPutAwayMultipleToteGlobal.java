package com.bflgroup.warehouse.ui.binstorageputawaymultipletote;

public class BinPutAwayMultipleToteGlobal {

    public static BinPutAwayMultipleToteGlobal instance;
    private static String boxNo;
    private static String doubleDeep;
    private static int scanCount;
    public static String getBoxNo() {
        return boxNo;
    }

    public static void setBoxNo(String boxNo) {
        BinPutAwayMultipleToteGlobal.boxNo = boxNo;
    }

    public static String getDoubleDeep() {
        return doubleDeep;
    }

    public static void setDoubleDeep(String doubleDeep) {
        BinPutAwayMultipleToteGlobal.doubleDeep = doubleDeep;
    }

    public static int getScanCount() {
        return scanCount;
    }

    public static void setScanCount(int scanCount) {
        BinPutAwayMultipleToteGlobal.scanCount = scanCount;
    }

    public static synchronized BinPutAwayMultipleToteGlobal getInstance() {
        if (instance == null) {
            instance = new BinPutAwayMultipleToteGlobal();
        }
        return instance;
    }

}
