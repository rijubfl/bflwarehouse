package com.bflgroup.warehouse.ui.departmentgrn;

public class DepartmentGRNGlobal {


    public static synchronized DepartmentGRNGlobal getInstance() {
        if(instance == null){
            instance = new DepartmentGRNGlobal();
        }
        return instance;
    }

    public static void setInstance(DepartmentGRNGlobal instance) {
        DepartmentGRNGlobal.instance = instance;
    }
    public static DepartmentGRNGlobal instance;

    public static Integer getPalletCount() {
        return palletCount;
    }

    public static Integer setPalletCount(Integer palletCount) {
        DepartmentGRNGlobal.palletCount = palletCount;
        return palletCount;
    }

    public static Integer palletCount = 0;

    public static String getBoxNo() {
        return BoxNo;
    }
    public static String getToteid() {
        return Toteid;
    }

    public static void setBoxNo(String boxNo) {
        BoxNo = boxNo;
    }

    public static void setToteid(String toteid) {
        Toteid = toteid;
    }
    private static String BoxNo;
    private static String Toteid;

}
