package com.bflgroup.warehouse.ui.transfervalidation;

import android.content.Context;
import android.content.SharedPreferences;

public class ValidateTransferSharedRef {
    SharedPreferences sharedRef;

    public ValidateTransferSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveTrfNo(String trfNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("TrfNo", trfNo);
        editor.commit();
    }
    public void saveShopname(String shopnmae) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("shopnmae", shopnmae);
        editor.commit();
    }

    public void saveShopTrf(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("tick", tick);
        editor.commit();
    }

    public void saveViewTick(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("view", tick);
        editor.commit();
    }



    public String loadTrfNo() {
        return sharedRef.getString("TrfNo", "");
    }
    public String loadShopname() {
        return sharedRef.getString("Shopname", "");
    }

    public boolean loadTickShopTrf() {
        return sharedRef.getBoolean("tick", false);
    }

    public boolean loadTickView() {
        return sharedRef.getBoolean("view", false);
    }

}
