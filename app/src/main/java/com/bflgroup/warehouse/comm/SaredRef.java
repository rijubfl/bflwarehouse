package com.bflgroup.warehouse.comm;

import android.content.Context;
import android.content.SharedPreferences;

public class SaredRef {

    SharedPreferences sharedRef;

    public SaredRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveWorkLocation(String server){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("worklocation",server);
        editor.commit();
    }

    public String loadWorkLocation() {
        return sharedRef.getString("worklocation", "");
    }

    public void saveWorkLocationSub(String server){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("worklocationSub",server);
        editor.commit();
    }

    public String loadWorkLocationSub() {
        return sharedRef.getString("worklocationSub", "");
    }

}

