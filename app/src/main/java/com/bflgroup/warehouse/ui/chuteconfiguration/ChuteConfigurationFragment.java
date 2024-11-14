package com.bflgroup.warehouse.ui.chuteconfiguration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationFragment extends Fragment {

/*
    private Global objGlobal = Global.getInstance();

    private ChuteConfigurationControl objChuteConfigurationControl = new ChuteConfigurationControl();

    private TextView tv_chute_status_config_warehouse;
    private EditText et_chute_status_config_chuteid;
    private Spinner sp_chute_status_config_shopname;
    private Spinner sp_chute_status_config_status;
    private EditText et_chute_status_config_totid;
    private Button bt_chute_status_techno_config_clear;
    private Button bt_chute_status_techno_config_update
*/

    public ChuteConfigurationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chute_configuration, container, false);
/*

        tv_chute_status_config_warehouse = (TextView) view.findViewById(R.id.tv_chute_status_config_warehouse);
        et_chute_status_config_chuteid = (EditText) view.findViewById(R.id.et_chute_status_config_chuteid);

        sp_chute_status_config_status = (Spinner) view.findViewById(R.id.sp_chute_status_config_status);
        et_chute_status_config_totid = (EditText) view.findViewById(R.id.et_chute_status_config_totid);
        bt_chute_status_techno_config_clear = (Button) view.findViewById(R.id.bt_chute_status_techno_config_clear);
        bt_chute_status_techno_config_update = (Button) view.findViewById(R.id.bt_chute_status_techno_config_update);

        tv_chute_status_config_warehouse.setText(objGlobal.getWarehouse());

        List<String> arr = objChuteConfigurationControl.loadShops();
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_chute_status_config_shopname.setAdapter(arrayAdp);

        List<String> arr1;
        arr1 = new ArrayList<String>();
        arr1.add("Normal");
        arr1.add("Full");
        arr1.add("Disable");
        arr1.add("Unknown");
        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
        sp_chute_status_config_status.setAdapter(arrayAdp1);
*/

        return view;
    }
}