package com.bflgroup.warehouse.ui.boxreconcilation;

public class BoxItemList {


    public BoxItemList(int srno, String palletno, String boxNoOriginal, String toteId, String boxNoScanned) {
        Srno = srno;
        Palletno = palletno;
        this.boxNoOriginal = boxNoOriginal;
        this.toteId = toteId;
        this.boxNoScanned = boxNoScanned;
    }

    public int Srno;
    public String Palletno;
    public String boxNoOriginal;
    public String toteId;
    public String boxNoScanned;




}
