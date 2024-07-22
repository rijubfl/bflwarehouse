package com.bflgroup.warehouse.ui.divisionseperate;

public class DivisionSeperationItemTicket {
    public String itemcode;
    public String division;
    public String trfQty;
    public String scanQty;

    public DivisionSeperationItemTicket(String itemcode, String division, String trfQty,String scanQty) {
        this.itemcode = itemcode;
        this.division = division;
        this.trfQty = trfQty;
        this.scanQty = scanQty;
    }
}
