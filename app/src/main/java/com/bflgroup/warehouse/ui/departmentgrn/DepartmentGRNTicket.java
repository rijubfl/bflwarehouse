package com.bflgroup.warehouse.ui.departmentgrn;

public class DepartmentGRNTicket {


    public DepartmentGRNTicket(int srno, String palletno, String boxNoOriginal, String boxNoScanned) {
        Srno = srno;
        Palletno = palletno;
        this.boxNoOriginal = boxNoOriginal;
        this.boxNoScanned = boxNoScanned;
    }

    public int Srno;
    public String Palletno;
    public String boxNoOriginal;
    public String boxNoScanned;




}
