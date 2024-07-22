package com.bflgroup.warehouse.ui.updateboxqty;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateBoxSharedRef {

    static SharedPreferences sharedRef;
    public UpdateBoxSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public static void saveToteid(String Toteid){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Toteid",Toteid);
        editor.commit();
    }
    public String loadToteid(){ return sharedRef.getString("Toteid",""); }

}
