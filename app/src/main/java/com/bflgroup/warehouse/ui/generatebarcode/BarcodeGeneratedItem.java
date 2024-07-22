package com.bflgroup.warehouse.ui.generatebarcode;

public class BarcodeGeneratedItem {


    public String Date;
    public String Upc;
    public String GeneratedBarcode;


    public BarcodeGeneratedItem(String date, String upc, String generatedBarcode) {
        Date = date;
        Upc = upc;
        GeneratedBarcode = generatedBarcode;
    }

}
