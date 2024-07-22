package com.bflgroup.warehouse.ui.ageingstocktaking;

import java.util.List;

public class AgeingStockTakingGlobal {

    public static AgeingStockTakingGlobal instance;
    private static double totalScan;
    private static double totalScanExport;
    private static double total;
    private static List<String> zoneList;

    public static double getTotalScan() {
        return totalScan;
    }

    public static void setTotalScan(double totalScan) {
        AgeingStockTakingGlobal.totalScan = totalScan;
    }

    public static double getTotalScanExport() {
        return totalScanExport;
    }

    public static void setTotalScanExport(double totalScanExport) {
        AgeingStockTakingGlobal.totalScanExport = totalScanExport;
    }

    public static List<String> getZoneList() {
        return zoneList;
    }

    public static void setZoneList(List<String> zoneList) {
        AgeingStockTakingGlobal.zoneList = zoneList;
    }

    public static double getTotal() {
        return total;
    }

    public static void setTotal(double total) {
        AgeingStockTakingGlobal.total = total;
    }

    public static synchronized AgeingStockTakingGlobal getInstance() {
        if (instance == null) {
            instance = new AgeingStockTakingGlobal();
        }
        return instance;
    }
}
