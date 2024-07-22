package com.bflgroup.warehouse.ui.departmentgrn;

public class BoxItemList {


    public BoxItemList(int srno, String palletno, String boxNoOriginal,String toteid, String verified) {
        Srno = srno;
        Palletno = palletno;
        this.boxNoOriginal = boxNoOriginal;
        this.toteid = toteid;
        this.verified = verified;
    }

    public int Srno;
    public String Palletno;
    public String boxNoOriginal;
    public String toteid;
    public String verified;




}
