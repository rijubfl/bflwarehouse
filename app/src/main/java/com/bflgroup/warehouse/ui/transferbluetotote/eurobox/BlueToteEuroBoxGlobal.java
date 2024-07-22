package com.bflgroup.warehouse.ui.transferbluetotote.eurobox;


public class BlueToteEuroBoxGlobal {
    public static BlueToteEuroBoxGlobal instance;
    public static String palletType = "";
    private String boxNo;
    public static String palletNo;
    public static String PalletSno;

    public static synchronized BlueToteEuroBoxGlobal getInstance() {
        if (instance == null) {
            instance = new BlueToteEuroBoxGlobal();
        }
        return instance;
    }
    public static String getPalletNo() {return palletNo;  }

    public static void setPalletNo(String palletNo) { BlueToteEuroBoxGlobal.palletNo = palletNo;    }

    public static String getPalletSno() {return PalletSno;    }

    public static void setPalletSno(String palletSno) {PalletSno = palletSno;}

    public String getBoxNo() { return boxNo; }
    public void setBoxNo(String boxNo) { this.boxNo = boxNo; }

    public static String getPalletType() {return palletType;}

    public static void setPalletType(String palletType) {BlueToteEuroBoxGlobal.palletType = palletType;}




}
