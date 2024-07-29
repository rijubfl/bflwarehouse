package com.bflgroup.warehouse.ui.chuteconfiguration.techno;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationTechnoFragment extends Fragment {

    private TextView tv_chute_status_techno_config_chuteid;
    private TextView tv_chute_status_techno_config_shopname;
    private Spinner sp_chute_status_techno_config_status;
    private EditText et_chute_status_techno_config_totid;
    private Button bt_chute_status_techno_config_clear;
    private Button bt_chute_status_techno_config_update;
    private ListView lv_chute_status_techno_config_update;

    private Global objGlobal = Global.getInstance();

    private boolean b_Result;

    private ChuteConfigurationTechnoControl objChuteConfigurationTechnoControl = new ChuteConfigurationTechnoControl();

    public ChuteConfigurationTechnoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chute_configuration_techno, container, false);

        tv_chute_status_techno_config_chuteid = (TextView) view.findViewById(R.id.tv_chute_status_techno_config_chuteid);
        tv_chute_status_techno_config_shopname = (TextView) view.findViewById(R.id.tv_chute_status_techno_config_shopname);
        sp_chute_status_techno_config_status = (Spinner) view.findViewById(R.id.sp_chute_status_techno_config_status);
        et_chute_status_techno_config_totid = (EditText) view.findViewById(R.id.et_chute_status_techno_config_totid);
        bt_chute_status_techno_config_clear = (Button) view.findViewById(R.id.bt_chute_status_techno_config_clear);
        bt_chute_status_techno_config_update = (Button) view.findViewById(R.id.bt_chute_status_techno_config_update);
        lv_chute_status_techno_config_update = (ListView) view.findViewById(R.id.lv_chute_status_techno_config_update);

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("Normal");
        arr.add("Full");
        arr.add("Disable");
        arr.add("Unknown");
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_chute_status_techno_config_status.setAdapter(arrayAdp);

        bt_chute_status_techno_config_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_chute_status_techno_config_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_chute_status_techno_config_chuteid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLists("C");
            }
        });
        tv_chute_status_techno_config_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLists("S");
            }
        });
        return view;
    }

    void loadLists(String type) {
        Dialog dialog;
        List<String> arraylist;
        arraylist = objChuteConfigurationTechnoControl.loadLists(type);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.searchable_shopname);
        dialog.getWindow().setLayout(600, 1000);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arraylist);
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("S")) {
                    tv_chute_status_techno_config_shopname.setText(adapter.getItem(position));
                }
                if (type.equals("C")) {
                    tv_chute_status_techno_config_chuteid.setText(adapter.getItem(position));
                    b_Result = objChuteConfigurationTechnoControl.getChuteDetails(tv_chute_status_techno_config_chuteid.getText().toString());
                    if(b_Result){

                    } else {

                    }
                }
                dialog.dismiss();
            }
        });
    }

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

}