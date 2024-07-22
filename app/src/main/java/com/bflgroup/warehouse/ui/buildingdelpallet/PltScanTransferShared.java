package com.bflgroup.warehouse.ui.buildingdelpallet;

import android.content.Context;
import android.content.SharedPreferences;

public class PltScanTransferShared {
    SharedPreferences sharedRef;
    public PltScanTransferShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void Routeidsave(String routeid){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("pltrouteid",routeid);
        editor.commit();
    }

    public String Routeidload(){ return sharedRef.getString("pltrouteid",""); }
}
