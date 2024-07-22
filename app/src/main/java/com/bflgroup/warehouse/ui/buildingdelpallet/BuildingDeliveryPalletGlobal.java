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

    public static String PalletNo;

    public static Integer getPltCount() {
        return Count;
    }

    public static Integer setPltCount(Integer count) {
        Count = count;
        return count;
    }

    public static Integer Count;

}
