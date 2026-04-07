package com.bflgroup.warehouse.ui.binstoragewavepick;

import android.content.Context;
import android.content.SharedPreferences;

public class BinStorageWavePickShared {
    SharedPreferences sharedRef;

    public BinStorageWavePickShared(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveLocation(String location) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("location", location);
        editor.commit();
    }

    public String loadLocation() {
        return sharedRef.getString("location", "");
    }


}
