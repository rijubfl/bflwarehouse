package com.bflgroup.warehouse.ui.divisionseperate;

public class DivisionSeperationGlobal {

    public static DivisionSeperationGlobal instance;
    private static String database;
    private static String costcode;
    private static String loccode;

    public static String getLoccode() {
        return loccode;
    }

    public static void setLoccode(String loccode) {
        DivisionSeperationGlobal.loccode = loccode;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        DivisionSeperationGlobal.database = database;
    }

    public static String getCostcode() {
        return costcode;
    }

    public static void setCostcode(String costcode) {
        DivisionSeperationGlobal.costcode = costcode;
    }

    public static synchronized DivisionSeperationGlobal getInstance() {
        if (instance == null) {
            instance = new DivisionSeperationGlobal();
        }
        return instance;
    }
}
