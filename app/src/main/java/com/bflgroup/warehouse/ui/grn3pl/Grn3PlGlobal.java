package com.bflgroup.warehouse.ui.grn3pl;

public class Grn3PlGlobal {
    public static Grn3PlGlobal instance;

    private static String palletStatus;
    private static String simProcessId;

    public static String getSimProcessId() {
        return simProcessId;
    }

    public static void setSimProcessId(String simProcessId) {
        Grn3PlGlobal.simProcessId = simProcessId;
    }

    public static String getPalletStatus() {
        return palletStatus;
    }

    public static void setPalletStatus(String palletStatus) {
        Grn3PlGlobal.palletStatus = palletStatus;
    }

    public static synchronized Grn3PlGlobal getInstance() {
        if (instance == null) {
            instance = new Grn3PlGlobal();
        }
        return instance;
    }

}
