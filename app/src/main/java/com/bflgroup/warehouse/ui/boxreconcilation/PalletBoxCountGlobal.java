package com.bflgroup.warehouse.ui.boxreconcilation;

public class PalletBoxCountGlobal {


    public static synchronized PalletBoxCountGlobal getInstance() {
        if(instance == null){
            instance = new PalletBoxCountGlobal();
        }
        return instance;
    }

    public static void setInstance(PalletBoxCountGlobal instance) {
        PalletBoxCountGlobal.instance = instance;
    }
    public static PalletBoxCountGlobal instance;

    public static Integer getPalletCount() {
        return palletCount;
    }

    public static Integer setPalletCount(Integer palletCount) {
        PalletBoxCountGlobal.palletCount = palletCount;
        return palletCount;
    }

    public static Integer palletCount = 0;

    public static String getBoxNo() {
        return BoxNo;
    }

    public static void setBoxNo(String boxNo) {
        BoxNo = boxNo;
    }

    public static String getToteId() {
        return ToteId;
    }

    public static void setToteId(String toteId) {
        ToteId = toteId;
    }
    private static String BoxNo;
    private static String ToteId;

}
