package com.bflgroup.warehouse.ui.binstoragebatchin;

import android.content.Context;
import android.content.SharedPreferences;

public class BinBatchInShared {
    SharedPreferences sharedRef;
    public BinBatchInShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void savePltNo(String PltNo){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("PltNo",PltNo);
        editor.commit();
    }
    public String loadPltNo(){ return sharedRef.getString("PltNo",""); }

}
