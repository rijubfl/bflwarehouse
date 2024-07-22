package com.bflgroup.warehouse.ui.building.jafza;


public class BuildingJafzaGLobal {
    public static BuildingJafzaGLobal instance;
    private static String chuteLastInOut;
    private static int totBuildQty;
    private static String boxNo;

    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        BuildingJafzaGLobal.boxNo = boxNo;
    }

    public static int getTotBuildQty() {
        return totBuildQty;
    }

    public static void setTotBuildQty(int totBuildQty) {
        BuildingJafzaGLobal.totBuildQty = totBuildQty;
    }

    public static String getChuteLastInOut() {
        return chuteLastInOut;
    }

    public static void setChuteLastInOut(String chuteLastInOut) {
        BuildingJafzaGLobal.chuteLastInOut = chuteLastInOut;
    }

    public static void setInstance(BuildingJafzaGLobal instance) {
        BuildingJafzaGLobal.instance = instance;
    }

    public static synchronized BuildingJafzaGLobal getInstance() {
        if (instance == null) {
            instance = new BuildingJafzaGLobal();
        }
        return instance;
    }
}
