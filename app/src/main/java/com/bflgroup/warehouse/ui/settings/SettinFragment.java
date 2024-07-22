package com.bflgroup.warehouse.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.printclass.SunmiPrintHelper;
import com.bflgroup.warehouse.ui.generatebarcode.GenerateBarcodePrint;

public class SettinFragment extends Fragment {

    Global objGlobal=Global.getInstance();

    private Switch sw_settings_keyboard;
    private TextView tv_rfid_device;
    private Button bt_scan_rfid_device;

    private Button bt_settings_popup_close;
    private ListView lv_settings_popup_load;

    GenerateBarcodePrint objGenerateBarcodePrint = new GenerateBarcodePrint();

    public SettinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settin, container, false);

        sw_settings_keyboard=(Switch) view.findViewById(R.id.sw_settings_keyboard);
        tv_rfid_device=(TextView) view.findViewById(R.id.tv_rfid_device);
        bt_scan_rfid_device=(Button) view.findViewById(R.id.bt_scan_rfid_device);

        SunmiPrintHelper.getInstance().initSunmiPrinterService(getContext());

        sw_settings_keyboard.setChecked(false);
        if(objGlobal.getHideKeyPad()) {
            sw_settings_keyboard.setChecked(true);
        }

        sw_settings_keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    objGlobal.setHideKeyPad(true);
                }
                else {
                    objGlobal.setHideKeyPad(false);
                }
            }
        });

        bt_scan_rfid_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPopupScan();
                objGenerateBarcodePrint.printMainInvoice("ERTN/23/00006");

            }
        });



        return view;
    }


    private void openPopupScan() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.settings_popup_device_list);

        bt_settings_popup_close = (Button) myDialog.findViewById(R.id.bt_settings_popup_close);
        lv_settings_popup_load = (ListView) myDialog.findViewById(R.id.lv_settings_popup_load);

        bt_settings_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

}