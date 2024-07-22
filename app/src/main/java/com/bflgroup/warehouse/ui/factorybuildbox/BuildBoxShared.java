package com.bflgroup.warehouse.ui.factorybuildbox;

import android.content.Context;
import android.content.SharedPreferences;

public class BuildBoxShared {

        SharedPreferences sharedRef;
        public BuildBoxShared(Context context){
            sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
        }

        public void save(String item){
            SharedPreferences.Editor editor=sharedRef.edit();
            editor.putString("item",item);
            editor.commit();
        }

        public String load(){ return sharedRef.getString("item",""); }

    public void saveGoodTote(String tote){
            SharedPreferences.Editor editor=sharedRef.edit();
            editor.putString("goodtote",tote);
            editor.commit();
        }

        public String loadGoodTote(){ return sharedRef.getString("goodtote",""); }

    public void saveAsisTote(String tote){
            SharedPreferences.Editor editor=sharedRef.edit();
            editor.putString("asistote",tote);
            editor.commit();
        }

        public String loadAsisTote(){ return sharedRef.getString("asistote",""); }

    public void saveWriteoffTote(String tote){
            SharedPreferences.Editor editor=sharedRef.edit();
            editor.putString("writeofftote",tote);
            editor.commit();
        }

        public String loadwriteoffTote(){ return sharedRef.getString("writeofftote",""); }

}
