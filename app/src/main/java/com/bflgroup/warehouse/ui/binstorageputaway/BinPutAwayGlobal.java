package com.bflgroup.warehouse.ui.binstorageputaway;

public class BinPutAwayGlobal {

    public static BinPutAwayGlobal instance;
    private static String boxNo;
    private static String doubleDeep;

    public static String getBoxNo() {
        return boxNo;
    }

    public static void setBoxNo(String boxNo) {
        BinPutAwayGlobal.boxNo = boxNo;
    }

    public static String getDoubleDeep() {
        return doubleDeep;
    }

    public static void setDoubleDeep(String doubleDeep) {
        BinPutAwayGlobal.doubleDeep = doubleDeep;
    }

    public static synchronized BinPutAwayGlobal getInstance() {
        if (instance == null) {
            instance = new BinPutAwayGlobal();
        }
        return instance;
    }
}
