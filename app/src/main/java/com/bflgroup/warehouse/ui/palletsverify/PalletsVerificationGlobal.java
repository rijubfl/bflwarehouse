package com.bflgroup.warehouse.ui.palletsverify;

public class PalletsVerificationGlobal {

    public static PalletsVerificationGlobal instance;
    private static String scanCount;
    private  String warehouseFrom;
    private  String warehouseTo;

    public static String getScanCount() { return scanCount; }
    public String getWarehouseFrom() { return warehouseFrom; }
    public String getWarehouseTo() { return warehouseTo; }
    public  void setScanCount(String scancount) { scanCount = scancount; }
    public  void setWarehouseFrom(String warehousefrom) { warehouseFrom = warehousefrom; }
    public  void setWarehouseTo(String warehouseto) { warehouseTo = warehouseto; }

    public static synchronized PalletsVerificationGlobal getInstance() {
        if (instance == null) {
            instance = new PalletsVerificationGlobal();
        }
        return instance;
    }
}
