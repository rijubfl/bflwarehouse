package com.bflgroup.warehouse.ui.shopreturns;

import android.content.Context;
import android.content.SharedPreferences;

public class ReceiveShopReturnsShared {

    SharedPreferences sharedRef;
    public ReceiveShopReturnsShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveEntryNo(String EntryNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("EntryNo",EntryNo);
        editor.commit();
    }
    public String loadEntryNo(){ return sharedRef.getString("EntryNo",""); }

    public void saveShop(String shop){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("shop",shop);
        editor.commit();
    }
    public String loadShop(){ return sharedRef.getString("shop",""); }

    public void saveCategory(String category){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("category",category);
        editor.commit();
    }
    public String loadCategory(){ return sharedRef.getString("category",""); }

    public void saveItemWiseScan(String ItemWiseScan){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("ItemWiseScan",ItemWiseScan);
        editor.commit();
    }
    public String loadItemWiseScan(){ return sharedRef.getString("ItemWiseScan",""); }

    public void saveAutoBuild(String AutoBuild){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("AutoBuild",AutoBuild);
        editor.commit();
    }
    public String loadAutoBuild(){ return sharedRef.getString("AutoBuild",""); }

    public void saveAutoBuildPalletType(String AutoBuildPalletType){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("AutoBuildPalletType",AutoBuildPalletType);
        editor.commit();
    }
    public String loadAutoBuildPalletType(){ return sharedRef.getString("AutoBuildPalletType",""); }

    public void savePrinter(String savePrinter){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("savePrinter",savePrinter);
        editor.commit();
    }
    public String loadPrinter(){ return sharedRef.getString("loadPrinter",""); }

}
