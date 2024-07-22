package com.bflgroup.warehouse.ui.transfervalidation;


public class ValidateTransferGlobal {

    public static synchronized ValidateTransferGlobal getInstance() {
        if(instance == null){
            instance = new ValidateTransferGlobal();
        }
        return instance;
    }
    public static void setInstance(ValidateTransferGlobal instance) {
        ValidateTransferGlobal.instance = instance;
    }


    public static ValidateTransferGlobal instance;


    public static Integer getTrfTotal() {
        return TrfTotal;
    }

    public static Integer TrfTotal = 0;

    public static Integer setTrfTotal(Integer count) {
        TrfTotal = count;
        return count;
    }

    public static Integer ScanTotal = 0;

    public static Integer setScanTotal(Integer count) {
        ScanTotal = count;
        return count;
    }

    public static Integer getScanTotal() {
        return ScanTotal;
    }


    public static Integer getDiffTotal() {
        return DiffTotal;
    }

    public static Integer DiffTotal = 0;

    public static Integer setDiffTotal(Integer count) {
        DiffTotal = count;
        return count;
    }


}
