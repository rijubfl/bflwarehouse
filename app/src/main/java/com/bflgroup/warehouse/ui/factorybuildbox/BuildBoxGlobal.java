package com.bflgroup.warehouse.ui.factorybuildbox;


public class BuildBoxGlobal {
    public static BuildBoxGlobal instance;
    private String boxNo;
    private String sn;
    private static String palletNo;
    private static String palletSno;
    public static Integer getGoodsCount() {
        return GoodsCount;
    }

    public static void setGoodsCount(Integer goodsCount) {
        GoodsCount = goodsCount;
    }

    public static Integer GoodsCount = 0;

    public static Integer getAsisCount() {
        return AsisCount;
    }

    public static void setAsisCount(Integer asisCount) {
        AsisCount = asisCount;
    }

    public static Integer getWriteoffCount() {
        return WriteoffCount;
    }

    public static void setWriteoffCount(Integer writeoffCount) {
        WriteoffCount = writeoffCount;
    }
    public String getBoxNo() { return boxNo; }
    public String getSn() { return sn; }
    public void setBoxNo(String boxNo) { this.boxNo = boxNo; }
    public void setSn(String Sn) { this.sn = Sn; }


    public static String getPalletSno() {
        return palletSno;
    }
    public String getPalletNo() {
        return palletNo;
    }

    public void setPalletNo(String palletno) {
        palletNo = palletno;
    }


    public static void setPalletSno(String palletsno) {
        palletSno = palletsno;
    }

    public static Integer AsisCount = 0;
    public static Integer WriteoffCount = 0;



    public static synchronized BuildBoxGlobal getInstance() {
        if (instance == null) {
            instance = new BuildBoxGlobal();
        }
        return instance;
    }


}
