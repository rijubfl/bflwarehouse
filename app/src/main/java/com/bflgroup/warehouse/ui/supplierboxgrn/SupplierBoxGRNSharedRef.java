package com.bflgroup.warehouse.ui.supplierboxgrn;

import android.content.Context;
import android.content.SharedPreferences;

public class SupplierBoxGRNSharedRef {
    SharedPreferences sharedRef;

    public SupplierBoxGRNSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void savePrinter(String Printer) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("Printer", Printer);
        editor.commit();
    }

    public String loadPrinter() {
        return sharedRef.getString("Printer", "");
    }

    public void savePrintCopies(String PrintCopies) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrintCopies", PrintCopies);
        editor.commit();
    }

    public String loadPrintCopies() {
        return sharedRef.getString("PrintCopies", "");
    }

    public void saveContainerID(String ContainerID) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ContainerID", ContainerID);
        editor.commit();
    }

    public String loadContainerID() {
        return sharedRef.getString("ContainerID", "");
    }

}
