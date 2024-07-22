package com.bflgroup.warehouse.ui.transferbluetotote.eurobox;

import android.content.Context;
import android.content.SharedPreferences;

public class BluetoteEuroBoxSharedRef {

    static SharedPreferences sharedRef;


    public BluetoteEuroBoxSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void savePalletType(String pallettype){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("pallettype",pallettype);
        editor.commit();
    }

    public static String LoadPalletType(){ return
            sharedRef.getString("pallettype", ""); }

}
