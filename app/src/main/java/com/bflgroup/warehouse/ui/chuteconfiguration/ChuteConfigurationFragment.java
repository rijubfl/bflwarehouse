package com.bflgroup.warehouse.ui.chuteconfiguration;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationFragment extends Fragment {

    private Global objGlobal = Global.getInstance();

    private ChuteConfigurationControl objChuteConfigurationControl = new ChuteConfigurationControl();

    private TextView tv_chute_status_config_warehouse;
    private TextView tv_chute_status_config_shopid;
    private TextView tv_chute_status_config_shopname;
    private EditText et_chute_status_config_contno;
    private Button bt_chute_status_config_load_contno;
    private ListView lv_chute_status_config_chute_list;
    private Button bt_chute_status_config_clear;
    private Button bt_chute_status_config_update;


    public ChuteConfigurationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chute_configuration, container, false);

        tv_chute_status_config_warehouse = (TextView) view.findViewById(R.id.tv_chute_status_config_warehouse);
        tv_chute_status_config_warehouse= (TextView) view.findViewById(R.id.tv_chute_status_config_warehouse);
        tv_chute_status_config_shopid= (TextView) view.findViewById(R.id.tv_chute_status_config_shopid);
        tv_chute_status_config_shopname= (TextView) view.findViewById(R.id.tv_chute_status_config_shopname);
        et_chute_status_config_contno=(EditText) view.findViewById(R.id.et_chute_status_config_contno);
        bt_chute_status_config_load_contno=(Button) view.findViewById(R.id.bt_chute_status_config_load_contno);
        lv_chute_status_config_chute_list=(ListView) view.findViewById(R.id.lv_chute_status_config_chute_list);
        bt_chute_status_config_clear= (Button) view.findViewById(R.id.bt_chute_status_config_clear);
        bt_chute_status_config_update= (Button) view.findViewById(R.id.bt_chute_status_config_update);
        tv_chute_status_config_warehouse.setText(objGlobal.getWarehouse());

        bt_chute_status_config_load_contno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> arr1 = objChuteConfigurationControl.loadShops();
            }
        });

        bt_chute_status_config_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*clearAll();
                et_building_chuteid.requestFocus();*/
            }
        });

        bt_chute_status_config_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_chute_status_config_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                List<String> arr1 = objChuteConfigurationControl.loadShops();
                if (arr1.isEmpty()) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                } else {
                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.searchable_spinner);
                    dialog.getWindow().setLayout(500, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arr1);
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
                            String[] parts = adapter.getItem(position).split("/");
                            tv_chute_status_config_shopid.setText(parts[0]);
                            tv_chute_status_config_shopname.setText(parts[1]);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        tv_chute_status_config_shopid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                List<String> arr1 = objChuteConfigurationControl.loadShops();
                if (arr1.isEmpty()) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                } else {
                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.searchable_spinner);
                    dialog.getWindow().setLayout(500, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arr1);
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
                            String[] parts = adapter.getItem(position).split("/");
                            tv_chute_status_config_shopid.setText(parts[0]);
                            tv_chute_status_config_shopname.setText(parts[1]);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }


    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
}