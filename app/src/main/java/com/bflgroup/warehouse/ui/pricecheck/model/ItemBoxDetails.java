package com.bflgroup.warehouse.ui.pricecheck.model;

public class ItemBoxDetails {
    public String itemcode;
    public String boxno;
    public String caption;
    public String division;

    public ItemBoxDetails( String itemcode,String boxno, String caption, String division) {
        this.itemcode = itemcode;
        this.boxno = boxno;
        this.caption = caption;
        this.division = division;
    }
}
