package com.bflgroup.warehouse.ui.ginverify;

import android.content.Context;
import android.content.SharedPreferences;

public class GinVerificationShared {

    SharedPreferences sharedRef;

    public GinVerificationShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveVerifyGinNo(String ginNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("VerifyGinNo",ginNo);
        editor.commit();
    }

    public String loadVerifyGinNo(){ return sharedRef.getString("VerifyGinNo",""); }

}
