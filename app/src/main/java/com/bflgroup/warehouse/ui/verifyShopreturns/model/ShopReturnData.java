package com.bflgroup.warehouse.ui.verifyShopreturns.model;


public class ShopReturnData {
    public String entryNo;
    public String shopName;
    public String category;
    public String username;
    public String errorMessage ;


    public ShopReturnData(String entryNo, String shopName, String errorMessage,String category,String username ) {
        this.entryNo = entryNo;
        this.shopName = shopName;
        this.errorMessage = errorMessage;
        this.category = category;
        this.username  = username;
    }
}
