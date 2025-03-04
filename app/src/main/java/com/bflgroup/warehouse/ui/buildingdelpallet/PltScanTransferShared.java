package com.bflgroup.warehouse.ui.buildingdelpallet;

import android.content.Context;
import android.content.SharedPreferences;

public class PltScanTransferShared {
    SharedPreferences sharedRef;
    public PltScanTransferShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void Routeidsave(String routeid){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("pltrouteid",routeid);
        editor.commit();
    }

    public String Routeidload(){ return sharedRef.getString("pltrouteid",""); }

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

    public void savePrinter(String Printer){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Printer",Printer);
        editor.commit();
    }
}
