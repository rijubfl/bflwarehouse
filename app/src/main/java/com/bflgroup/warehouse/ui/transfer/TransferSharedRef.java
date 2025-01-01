package com.bflgroup.warehouse.ui.transfer;

import android.content.Context;
import android.content.SharedPreferences;

public class TransferSharedRef {

    SharedPreferences sharedRef;

    public TransferSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveShopName(String ShopName){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("ShopName",ShopName);
        editor.commit();
    }

    public String loadShopName() {
        return sharedRef.getString("ShopName", "");
    }

    public void saveDeviceName(String DeviceName){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("DeviceName",DeviceName);
        editor.commit();
    }

    public String loadDeviceName() {
        return sharedRef.getString("ScanType", "");
    }

    public void saveScanType(String ScanType){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("ScanType",ScanType);
        editor.commit();
    }

    public String loadScanType() {
        return sharedRef.getString("ScanType", "");
    }

    public void savePallet(String Pallet){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Pallet",Pallet);
        editor.commit();
    }

    public String loadPallet() {
        return sharedRef.getString("Pallet", "");
    }

    public void savePrinter(String Printer){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Printer",Printer);
        editor.commit();
    }

    public String loadPrinter() {
        return sharedRef.getString("Printer", "");
    }

    public void savePrintCopies(String PrintCopies){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("PrintCopies",PrintCopies);
        editor.commit();
    }

    public String loadPrintCopies() {
        return sharedRef.getString("PrintCopies", "");
    }


}
