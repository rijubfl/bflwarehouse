package com.bflgroup.warehouse.ui.rfidtagregister;

import android.content.Context;
import android.content.SharedPreferences;

public class RfidTagRegisterSharedRef {

    SharedPreferences sharedRef;

    public RfidTagRegisterSharedRef(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }


    public void saveFrequencyMode(String FrequencyMode){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("FrequencyMode",FrequencyMode);
        editor.commit();
    }

    public String loadFrequencyMode() {
        return sharedRef.getString("FrequencyMode", "ETSI Standard(865~868MHz)");
    }


    public void saveOutputPower(String OutputPower){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("OutputPower",OutputPower);
        editor.commit();
    }

    public String loadOutputPower() {
        return sharedRef.getString("OutputPower", "30");
    }

    public void saveMemoryBank(String MemoryBank){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("MemoryBank",MemoryBank);
        editor.commit();
    }

    public String loadMemoryBank() {
        return sharedRef.getString("MemoryBank", "EPC");
    }

}
