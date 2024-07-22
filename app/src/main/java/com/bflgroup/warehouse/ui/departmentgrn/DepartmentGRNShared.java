package com.bflgroup.warehouse.ui.departmentgrn;

import android.content.Context;
import android.content.SharedPreferences;

public class DepartmentGRNShared {

    SharedPreferences sharedRef;
    public DepartmentGRNShared(Context context){
        sharedRef=context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveWarehouseTo(String warehouse){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("warehouseTo",warehouse);
        editor.commit();
    }

    public void saveWarehouseFrom(String warehouse){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("warehouseFrom",warehouse);
        editor.commit();
    }

    public String loadWarehouseTO(){ return sharedRef.getString("warehouseTo",""); }
    public String loadWarehouseFrom(){ return sharedRef.getString("warehouseFrom",""); }

    public void savePalletno(String Palletno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("palletno",Palletno);
        editor.commit();
    }

    public String loadPalletno(){ return sharedRef.getString("palletno",""); }

    public void saveRackno(String Rackno){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putString("Rackno",Rackno);
        editor.commit();
    }

    public String loadRackno(){ return sharedRef.getString("Rackno",""); }

    public void savePalletCount(int PalletCount){
        SharedPreferences.Editor editor=sharedRef.edit();
        editor.putInt("PalletCount",PalletCount);
        editor.commit();
    }

    public int LoadPalletCount(){ return sharedRef.getInt("PalletCount",0); }

}
