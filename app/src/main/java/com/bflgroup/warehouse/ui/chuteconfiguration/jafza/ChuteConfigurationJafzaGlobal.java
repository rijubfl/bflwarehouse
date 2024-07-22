package com.bflgroup.warehouse.ui.chuteconfiguration.jafza;

public class ChuteConfigurationJafzaGlobal {

    public static ChuteConfigurationJafzaGlobal instance;
    private static int shopId;
    private static String shopName;
    private static int status;
    private static String toteid;

    public static void setInstance(ChuteConfigurationJafzaGlobal instance) {
        ChuteConfigurationJafzaGlobal.instance = instance;
    }

    public static synchronized ChuteConfigurationJafzaGlobal getInstance() {
        if (instance == null) {
            instance = new ChuteConfigurationJafzaGlobal();
        }
        return instance;
    }

    public static int getShopId() {
        return shopId;
    }

    public static void setShopId(int shopId) {
        ChuteConfigurationJafzaGlobal.shopId = shopId;
    }

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        ChuteConfigurationJafzaGlobal.shopName = shopName;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        ChuteConfigurationJafzaGlobal.status = status;
    }

    public static String getToteid() {
        return toteid;
    }

    public static void setToteid(String toteid) {
        ChuteConfigurationJafzaGlobal.toteid = toteid;
    }
}
