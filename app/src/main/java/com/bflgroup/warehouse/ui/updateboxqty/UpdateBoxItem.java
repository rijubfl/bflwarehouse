package com.bflgroup.warehouse.ui.updateboxqty;

public class UpdateBoxItem {
    public String ItemCode;
    public String BoxQuantity;
    public String Qty;
    public String UpdatedBoxQuantity;

    public UpdateBoxItem(String itemCode, String boxQuantity, String qty, String newBoxQty) {
        ItemCode = itemCode;
        BoxQuantity = boxQuantity;
        Qty = qty;
        UpdatedBoxQuantity = newBoxQty;
    }


}
