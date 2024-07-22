package com.bflgroup.warehouse.ui.chutestatusinout.techno;

public class ChuteCheckInCheckOutItemTicket {
    public String itemCode;
    public String description;
    public int quantity;

    public ChuteCheckInCheckOutItemTicket(String itemCode, String description, int quantity) {
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
    }
}