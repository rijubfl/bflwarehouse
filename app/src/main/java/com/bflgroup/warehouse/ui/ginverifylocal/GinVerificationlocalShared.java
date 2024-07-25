package com.bflgroup.warehouse.ui.ginverifylocal;

import android.content.Context;
import android.content.SharedPreferences;

public class GinVerificationlocalShared {

    SharedPreferences sharedRef;

    public GinVerificationlocalShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveVerifyGinNo(String ginNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("VerifyGinNo",ginNo);
        editor.commit();
    }

    public String loadVerifyGinNo(){ return sharedRef.getString("VerifyGinNo",""); }

}
