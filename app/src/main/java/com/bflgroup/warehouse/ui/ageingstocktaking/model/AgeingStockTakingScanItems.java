package com.bflgroup.warehouse.ui.ageingstocktaking.model;

public class AgeingStockTakingScanItems {
    public String itemcode;
    public int qty;
    public String date;
    public String time;
    public String result;

    public AgeingStockTakingScanItems(String itemcode, int qty, String date, String time, String result) {
        this.itemcode = itemcode;
        this.qty = qty;
        this.date = date;
        this.time = time;
        this.result = result;
    }
}
