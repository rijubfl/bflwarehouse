package com.bflgroup.warehouse.ui.buildingdelgin;

import android.content.Context;
import android.content.SharedPreferences;

public class GinScanTransferShared {

        SharedPreferences sharedRef;
        public GinScanTransferShared(Context context){
            sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
        }

    public void saveRouteid(String routeid){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("routeid",routeid);
        editor.commit();
    }

    public String loadRouteid(){ return sharedRef.getString("routeid",""); }

    public void saveShopnames(String shopnames){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("shopname",shopnames);
        editor.commit();
    }

    public String loadShopnames(){ return sharedRef.getString("shopname",""); }


    public void savePalletCount(String palletcount){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("palletcount",palletcount);
        editor.commit();
    }
    public void savePalletno(String palletno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("palletno",palletno);
        editor.commit();
    }

    public String loadPalletCount(){ return sharedRef.getString("palletcount","0"); }

    public String loadPalletno(){ return sharedRef.getString("palletno",""); }




}


