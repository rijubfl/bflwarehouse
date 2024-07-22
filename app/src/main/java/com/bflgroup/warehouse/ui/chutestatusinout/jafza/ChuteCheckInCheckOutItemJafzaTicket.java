package com.bflgroup.warehouse.ui.chutestatusinout.jafza;

public class ChuteCheckInCheckOutItemJafzaTicket {
    public String itemCode;
    public String description;
    public int quantity;

    public ChuteCheckInCheckOutItemJafzaTicket(String itemCode, String description, int quantity) {
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
    }
}
