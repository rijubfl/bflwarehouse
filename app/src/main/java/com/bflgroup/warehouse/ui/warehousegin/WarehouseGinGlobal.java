package com.bflgroup.warehouse.ui.warehousegin;

public class WarehouseGinGlobal {

    public static WarehouseGinGlobal instance;

    public static synchronized WarehouseGinGlobal getInstance() {
        if (instance == null) {
            instance = new WarehouseGinGlobal();
        }
        return instance;
    }



}
