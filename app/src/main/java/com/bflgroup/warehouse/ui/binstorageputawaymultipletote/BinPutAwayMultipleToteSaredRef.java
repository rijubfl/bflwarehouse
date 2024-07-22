package com.bflgroup.warehouse.ui.binstorageputawaymultipletote;

import android.content.Context;
import android.content.SharedPreferences;

public class BinPutAwayMultipleToteSaredRef {
    SharedPreferences sharedRef;

    public BinPutAwayMultipleToteSaredRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveInOrOut(String inout){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("inout",inout);
        editor.commit();
    }

    public String loadInOrOut() {
        return sharedRef.getString("inout", "");
    }

    public void saveLocation(String location){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("location",location);
        editor.commit();
    }

    public String loadLocation() {
        return sharedRef.getString("location", "");
    }

}
