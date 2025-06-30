package com.bflgroup.warehouse.ui.warehousegrn;

import android.content.Context;
import android.content.SharedPreferences;

public class WarehouseGRNSharedRef {
    SharedPreferences sharedRef;
    public WarehouseGRNSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveGinNo(String GinNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("GinNo",GinNo);
        editor.commit();
    }
    public String loadGinNo(){ return sharedRef.getString("GinNo",""); }

    public void saveGinDate(String GinDate){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("GinDate",GinDate);
        editor.commit();
    }
    public String loadGinDate(){ return sharedRef.getString("GinDate",""); }

    public void saveWHFrom(String WHFrom){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("WHFrom",WHFrom);
        editor.commit();
    }
    public String loadWHFrom(){ return sharedRef.getString("WHFrom",""); }

    public void saveWHTo(String WHTo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("WHTo",WHTo);
        editor.commit();
    }
    public String loadWHTo(){ return sharedRef.getString("WHTo",""); }

    public void saveCountry(String WHTo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("country",WHTo);
        editor.commit();
    }
    public String loadCountry(){ return sharedRef.getString("country",""); }

}
