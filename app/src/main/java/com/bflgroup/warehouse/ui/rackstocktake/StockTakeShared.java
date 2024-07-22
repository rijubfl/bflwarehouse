package com.bflgroup.warehouse.ui.rackstocktake;

import android.content.Context;
import android.content.SharedPreferences;

public class StockTakeShared {

    SharedPreferences sharedRef;
    public StockTakeShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveWarehouse(String warehouse){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("warehouse",warehouse);
        editor.commit();
    }

    public String loadWarehouse(){ return sharedRef.getString("warehouse",""); }

    public void savePalletno(String palletno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Palletno",palletno);
        editor.commit();
    }

    public String loadPalletno(){ return sharedRef.getString("Palletno",""); }





}
