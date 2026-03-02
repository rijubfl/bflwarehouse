package com.bflgroup.warehouse.ui.supplierboxgrn;

public class SupplierBoxGRNGlobal {
    public static SupplierBoxGRNGlobal instance;

    private static double totalScanQty;
    private static int totalScanBoxCnt;
    private static String logNewBoxNo;

    public static synchronized SupplierBoxGRNGlobal getInstance() {
        if (instance == null) {
            instance = new SupplierBoxGRNGlobal();
        }
        return instance;
    }

    public static double getTotalScanQty() {
        return totalScanQty;
    }

    public static void setTotalScanQty(double totalScanQty) {
        SupplierBoxGRNGlobal.totalScanQty = totalScanQty;
    }

    public static int getTotalScanBoxCnt() {
        return totalScanBoxCnt;
    }

    public static void setTotalScanBoxCnt(int totalScanBoxCnt) {
        SupplierBoxGRNGlobal.totalScanBoxCnt = totalScanBoxCnt;
    }

    public static String getLogNewBoxNo() {
        return logNewBoxNo;
    }

    public static void setLogNewBoxNo(String logNewBoxNo) {
        SupplierBoxGRNGlobal.logNewBoxNo = logNewBoxNo;
    }
}
