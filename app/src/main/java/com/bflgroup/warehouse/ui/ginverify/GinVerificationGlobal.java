package com.bflgroup.warehouse.ui.ginverify;

public class GinVerificationGlobal {

    public static GinVerificationGlobal instance;
    private static String scanCount;

    public static String getScanCount() { return scanCount; }
    public static void setScanCount(String scanCount) { GinVerificationGlobal.scanCount = scanCount; }

    public static synchronized GinVerificationGlobal getInstance() {
        if (instance == null) {
            instance = new GinVerificationGlobal();
        }
        return instance;
    }
}
