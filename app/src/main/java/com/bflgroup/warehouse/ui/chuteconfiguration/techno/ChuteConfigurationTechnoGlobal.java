package com.bflgroup.warehouse.ui.chuteconfiguration.techno;

public class ChuteConfigurationTechnoGlobal {
    public static ChuteConfigurationTechnoGlobal instance;

    private static int shopId;
    private static String shopName;
    private static int status;
    private static String toteid;
    public static void setInstance(ChuteConfigurationTechnoGlobal instance) {
        ChuteConfigurationTechnoGlobal.instance = instance;
    }

    public static synchronized ChuteConfigurationTechnoGlobal getInstance() {
        if (instance == null) {
            instance = new ChuteConfigurationTechnoGlobal();
        }
        return instance;
    }

    public static int getShopId() {
        return shopId;
    }

    public static void setShopId(int shopId) {
        ChuteConfigurationTechnoGlobal.shopId = shopId;
    }

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        ChuteConfigurationTechnoGlobal.shopName = shopName;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        ChuteConfigurationTechnoGlobal.status = status;
    }

    public static String getToteid() {
        return toteid;
    }

    public static void setToteid(String toteid) {
        ChuteConfigurationTechnoGlobal.toteid = toteid;
    }
}
