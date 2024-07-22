package com.bflgroup.warehouse.ui.generatebarcode;

public class GenerateBarcodeGlobal {
    public static GenerateBarcodeGlobal instance;

    private static String No;
    private static String Sno;

    public String getPalletNo() {
        return No;
    }

    public void setPalletNo(String palletNo) {
        GenerateBarcodeGlobal.No = palletNo;
    }

    public static String getPalletSno() {
        return Sno;
    }

    public static void setPalletSno(String palletSno) {
        GenerateBarcodeGlobal.Sno = palletSno;
    }


    public static synchronized GenerateBarcodeGlobal getInstance() {
        if (instance == null) {
            instance = new GenerateBarcodeGlobal();
        }
        return instance;
    }
}
