package com.bflgroup.warehouse.ui.building.techno;

public class BuildingItemTicket {
    public String itemCode;
    public String description;
    public int quantity;

    public BuildingItemTicket(String itemCode, String description, int quantity) {
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
    }
}
