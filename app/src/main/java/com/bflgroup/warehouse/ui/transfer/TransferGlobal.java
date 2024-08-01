package com.bflgroup.warehouse.ui.transfer;

public class TransferGlobal {

    private static String shopName;
    private static String scanBarcode;
    private static String trfRecNo;
    private static int totalScan;
    private static String robooDcBuild;

    private static String ptrfno;
    private static String pboxno;
    private static String pshopname;
    private static String pqty;
    private static String pdeldate;
    private static String ptrfdate;
    private static String ptoteid;
    private static String premarks;
    private static String ppreparedby;

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

    public static String getPtrfno() {
        return ptrfno;
    }

    public static void setPtrfno(String ptrfno) {
        TransferGlobal.ptrfno = ptrfno;
    }

    public static String getPboxno() {
        return pboxno;
    }

    public static void setPboxno(String pboxno) {
        TransferGlobal.pboxno = pboxno;
    }

    public static String getPshopname() {
        return pshopname;
    }

    public static void setPshopname(String pshopname) {
        TransferGlobal.pshopname = pshopname;
    }

    public static String getPqty() {
        return pqty;
    }

    public static void setPqty(String pqty) {
        TransferGlobal.pqty = pqty;
    }

    public static String getPdeldate() {
        return pdeldate;
    }

    public static void setPdeldate(String pdeldate) {
        TransferGlobal.pdeldate = pdeldate;
    }

    public static String getPtrfdate() {
        return ptrfdate;
    }

    public static void setPtrfdate(String ptrfdate) {
        TransferGlobal.ptrfdate = ptrfdate;
    }

    public static String getPtoteid() {
        return ptoteid;
    }

    public static void setPtoteid(String ptoteid) {
        TransferGlobal.ptoteid = ptoteid;
    }

    public static String getPremarks() {
        return premarks;
    }

    public static void setPremarks(String premarks) {
        TransferGlobal.premarks = premarks;
    }

    public static String getPpreparedby() {
        return ppreparedby;
    }

    public static void setPpreparedby(String ppreparedby) {
        TransferGlobal.ppreparedby = ppreparedby;
    }

    public static TransferGlobal instance;

    public static synchronized TransferGlobal getInstance() {
        if (instance == null) {
            instance = new TransferGlobal();
        }
        return instance;
    }

}
