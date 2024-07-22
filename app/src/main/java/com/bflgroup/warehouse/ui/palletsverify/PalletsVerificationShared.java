package com.bflgroup.warehouse.ui.palletsverify;

import android.content.Context;
import android.content.SharedPreferences;

public class PalletsVerificationShared {

    SharedPreferences sharedRef;

    public PalletsVerificationShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveVerifyGinNo(String ginNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("VerifyGinNo",ginNo);
        editor.commit();
    }

    public String loadVerifyGinNo(){ return sharedRef.getString("VerifyGinNo",""); }

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

}
