package com.bflgroup.warehouse.ui.binstoragebatchin;


public class BinBatchInGlobal {

    public static BinBatchInGlobal instance;
    private String palletno;

    public String getPalletno() {
        return palletno;
    }

    public void setPalletno(String palletno) {
        this.palletno = palletno;
    }

    public static synchronized BinBatchInGlobal getInstance() {
        if (instance == null) {
            instance = new BinBatchInGlobal();
        }
        return instance;
    }


}
