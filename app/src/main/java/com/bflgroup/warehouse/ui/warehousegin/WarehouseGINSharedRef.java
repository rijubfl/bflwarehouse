package com.bflgroup.warehouse.ui.warehousegin;

import android.content.Context;
import android.content.SharedPreferences;

public class WarehouseGINSharedRef {
    SharedPreferences sharedRef;
    public WarehouseGINSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

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


    public void saveTrailerNo(String TrailerNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("TrailerNo",TrailerNo);
        editor.commit();
    }
    public String loadTrailerNo(){ return sharedRef.getString("TrailerNo",""); }

    public void saveDelDate(String DelDate){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("DelDate",DelDate);
        editor.commit();
    }
    public String loadDelDate(){ return sharedRef.getString("DelDate",""); }

    public void saveRemarks(String Remarks){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Remarks",Remarks);
        editor.commit();
    }
    public String loadRemarks(){ return sharedRef.getString("Remarks",""); }


}
