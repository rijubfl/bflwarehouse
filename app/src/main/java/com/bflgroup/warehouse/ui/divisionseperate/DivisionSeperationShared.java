package com.bflgroup.warehouse.ui.divisionseperate;

import android.content.Context;
import android.content.SharedPreferences;

public class DivisionSeperationShared {

    SharedPreferences sharedRef;

    public DivisionSeperationShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveLastTrfNo(String trfNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("LastTrfNo",trfNo);
        editor.commit();
    }
    public String loadLastTrfNo(){ return sharedRef.getString("LastTrfNo",""); }

    public void saveLastShop(String shopname){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("LastShop",shopname);
        editor.commit();
    }
    public String loadLastShop(){ return sharedRef.getString("LastShop",""); }

}
