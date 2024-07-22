package com.bflgroup.warehouse.ui.buildingdelgin;

public class GinScanTransferGlobal {

    public static synchronized GinScanTransferGlobal getInstance() {
        if(instance == null){
            instance = new GinScanTransferGlobal();
        }
        return instance;
    }

    public static void setInstance(GinScanTransferGlobal instance) {
        GinScanTransferGlobal.instance = instance;
    }

    public static GinScanTransferGlobal instance;

    public static Double getGinno() {
        return ginno;
    }
    public static Integer getPalletCount() {
        return palletCount;
    }

    public static Integer setPalletCount(Integer palletCount) {
        GinScanTransferGlobal.palletCount = palletCount;
        return palletCount;
    }

    public static Double setGinno(Double ginno) {
        GinScanTransferGlobal.ginno = ginno;
        return ginno;
    }



    public static Integer getCount() {
        return Count;
    }

    public static Integer setCount(Integer count) {
        Count = count;
        return count;
    }


    public static Double ginno;
    public static Integer Count = 0;
    public static Integer palletCount = 0;

}
