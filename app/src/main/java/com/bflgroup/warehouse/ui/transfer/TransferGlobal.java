package com.bflgroup.warehouse.ui.transfer;

public class TransferGlobal {

    private static String shopName;
    private static String scanBarcode;
    private static String trfRecNo;
    private static int totalScan;
    private static String robooDcBuild;

    public static String getScanBarcode() {
        return scanBarcode;
    }

    public static void setScanBarcode(String scanBarcode) {
        TransferGlobal.scanBarcode = scanBarcode;
    }

    public static String getTrfRecNo() {
        return trfRecNo;
    }

    public static void setTrfRecNo(String trfRecNo) {
        TransferGlobal.trfRecNo = trfRecNo;
    }

    public static int getTotalScan() {
        return totalScan;
    }

    public static void setTotalScan(int totalScan) {
        TransferGlobal.totalScan = totalScan;
    }

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        TransferGlobal.shopName = shopName;
    }

    public static String getRobooDcBuild() {
        return robooDcBuild;
    }

    public static void setRobooDcBuild(String robooDcBuild) {
        TransferGlobal.robooDcBuild = robooDcBuild;
    }

    public static TransferGlobal instance;

    public static synchronized TransferGlobal getInstance() {
        if (instance == null) {
            instance = new TransferGlobal();
        }
        return instance;
    }

}
