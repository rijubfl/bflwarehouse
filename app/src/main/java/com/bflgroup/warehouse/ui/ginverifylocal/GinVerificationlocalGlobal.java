package com.bflgroup.warehouse.ui.ginverifylocal;

public class GinVerificationlocalGlobal {

    public static GinVerificationlocalGlobal instance;
    private static String scanCount;

    public static String getScanCount() { return scanCount; }
    public static void setScanCount(String scanCount) { GinVerificationlocalGlobal.scanCount = scanCount; }

    public static synchronized GinVerificationlocalGlobal getInstance() {
        if (instance == null) {
            instance = new GinVerificationlocalGlobal();
        }
        return instance;
    }
}
