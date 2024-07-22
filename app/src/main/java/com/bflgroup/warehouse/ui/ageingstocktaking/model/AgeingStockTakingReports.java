package com.bflgroup.warehouse.ui.ageingstocktaking.model;

public class AgeingStockTakingReports {
    public String user;
    public String zone;
    public int qty;

    public AgeingStockTakingReports(String user, String zone, int qty) {
        this.user = user;
        this.zone = zone;
        this.qty = qty;
    }
}
