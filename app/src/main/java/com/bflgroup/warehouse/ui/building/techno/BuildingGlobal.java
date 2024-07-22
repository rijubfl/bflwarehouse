package com.bflgroup.warehouse.ui.building.techno;

public class BuildingGlobal {

    public static BuildingGlobal instance;
    private static String chuteLastInOut;
    private static int totBuildQty;
    private static String boxNo;

    public String getBoxNo() { return boxNo; }

    public void setBoxNo(String boxNo) { BuildingGlobal.boxNo = boxNo; }

    public static int getTotBuildQty() { return totBuildQty; }

    public static void setTotBuildQty(int totBuildQty) { BuildingGlobal.totBuildQty = totBuildQty; }

    public static String getChuteLastInOut() { return chuteLastInOut; }

    public static void setChuteLastInOut(String chuteLastInOut) { BuildingGlobal.chuteLastInOut = chuteLastInOut; }

    public static void setInstance(BuildingGlobal instance) {
        BuildingGlobal.instance = instance;
    }

    public static synchronized BuildingGlobal getInstance() {
        if (instance == null) {
            instance = new BuildingGlobal();
        }
        return instance;
    }
}
