package com.bflgroup.warehouse.ui.buildingdelpallet;

public class BuildingDeliveryPalletGlobal {

    public static synchronized BuildingDeliveryPalletGlobal getInstance() {
        if(instance == null){
            instance = new BuildingDeliveryPalletGlobal();
        }
        return instance;
    }

    public static void setInstance(BuildingDeliveryPalletGlobal instance) {
        BuildingDeliveryPalletGlobal.instance = instance;
    }

    public static BuildingDeliveryPalletGlobal instance;

    public static String getPalletNo() {
        return PalletNo;
    }

    public static String setPalletNo(String palletNo) {
        PalletNo = palletNo;
        return palletNo;
    }
    public static String setPalletSn(String palletsN) {
        palletSN = palletsN;
        return palletSN;
    }
    public static String getPalletSn() {
        return palletSN;
    }

    public static String PalletNo;
    public static String palletSN;

    public static int getPltCount() {
        return Count;
    }

    public static int setPltCount(int count) {
        Count = count;
        return count;
    }

    public static String getRouteid() {
        return Routeid;
    }

    public static String setRouteid(String routeid) {
        Routeid = routeid;
        return Routeid;
    }

    public static int Count;
    public static String Routeid;

}
