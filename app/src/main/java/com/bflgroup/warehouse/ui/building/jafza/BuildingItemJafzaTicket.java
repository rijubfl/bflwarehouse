package com.bflgroup.warehouse.ui.building.jafza;

public class BuildingItemJafzaTicket {
    public String itemCode;
    public String description;
    public int quantity;

    public BuildingItemJafzaTicket(String itemCode, String description, int quantity) {
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
    }
}