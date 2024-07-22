package com.bflgroup.warehouse.ui.r1whgrn;

public class R1WhGrnGlobal {
    public static R1WhGrnGlobal instance;

    private static String palletStatus;
    private static String simProcessId;

    public static String getSimProcessId() {
        return simProcessId;
    }

    public static void setSimProcessId(String simProcessId) {
        R1WhGrnGlobal.simProcessId = simProcessId;
    }

    public static String getPalletStatus() {
        return palletStatus;
    }

    public static void setPalletStatus(String palletStatus) {
        R1WhGrnGlobal.palletStatus = palletStatus;
    }

    public static synchronized R1WhGrnGlobal getInstance() {
        if (instance == null) {
            instance = new R1WhGrnGlobal();
        }
        return instance;
    }

}
