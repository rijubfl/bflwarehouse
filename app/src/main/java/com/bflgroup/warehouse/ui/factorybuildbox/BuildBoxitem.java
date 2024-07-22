package com.bflgroup.warehouse.ui.factorybuildbox;

public class BuildBoxitem {



    public String Upc;
    public String GeneratedBarcode;
    public String Status;


    public BuildBoxitem( String upc, String generatedBarcode, String status) {

        Upc = upc;
        GeneratedBarcode = generatedBarcode;
        Status = status;
    }
}
