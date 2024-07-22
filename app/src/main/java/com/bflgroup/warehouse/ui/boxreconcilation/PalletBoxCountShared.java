package com.bflgroup.warehouse.ui.boxreconcilation;

import android.content.Context;
import android.content.SharedPreferences;

public class PalletBoxCountShared {

    SharedPreferences sharedRef;
    public PalletBoxCountShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveWarehouse(String warehouse){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("warehouse",warehouse);
        editor.commit();
    }

    public String loadWarehouse(){ return sharedRef.getString("warehouse",""); }

    public void savePalletno(String Palletno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("palletno",Palletno);
        editor.commit();
    }

    public String loadPalletno(){ return sharedRef.getString("palletno",""); }

    public void saveRackno(String Rackno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Rackno",Rackno);
        editor.commit();
    }

    public String loadRackno(){ return sharedRef.getString("Rackno",""); }

    public void savePalletCount(int PalletCount){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putInt("PalletCount",PalletCount);
        editor.commit();
    }

    public int LoadPalletCount(){ return sharedRef.getInt("PalletCount",0); }

}
