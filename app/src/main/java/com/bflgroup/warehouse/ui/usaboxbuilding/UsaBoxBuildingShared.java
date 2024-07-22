package com.bflgroup.warehouse.ui.usaboxbuilding;

import android.content.Context;
import android.content.SharedPreferences;

public class UsaBoxBuildingShared {

    SharedPreferences sharedRef;
    public UsaBoxBuildingShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void savePltType(String PltType){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("PltType",PltType);
        editor.commit();
    }
    public String loadPltType(){ return sharedRef.getString("PltType",""); }

    public void saveAllowMixCategory(String mixCategory){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("AllowMixCategory",mixCategory);
        editor.commit();
    }
    public String loadAllowMixCategory(){ return sharedRef.getString("AllowMixCategory",""); }

    public void saveSize(String mixCategory){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("size",mixCategory);
        editor.commit();
    }
    public String loadSize(){ return sharedRef.getString("size",""); }

    public void saveGender(String mixCategory){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("gender",mixCategory);
        editor.commit();
    }
    public String loadGender(){ return sharedRef.getString("gender",""); }

    public void saveTask(String mixCategory){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("task",mixCategory);
        editor.commit();
    }
    public String loadTask(){ return sharedRef.getString("task",""); }

    public void saveDone(String mixCategory){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("done",mixCategory);
        editor.commit();
    }
    public String loadDone(){ return sharedRef.getString("done",""); }

    public void saveBuildType(String mixBuildType){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("BuildType",mixBuildType);
        editor.commit();
    }
    public String loadBuildType(){ return sharedRef.getString("BuildType",""); }

    public void saveEuro(String saveEuro){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Euro",saveEuro);
        editor.commit();
    }
    public String loadEuro(){ return sharedRef.getString("Euro",""); }

}
