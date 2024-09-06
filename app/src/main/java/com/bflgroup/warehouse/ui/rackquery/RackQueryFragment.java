package com.bflgroup.warehouse.ui.rackquery;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.ui.rackquery.model.RackDetailsData;

import java.util.ArrayList;
import java.util.List;


public class RackQueryFragment extends Fragment implements View.OnClickListener {


    private View rootView;
    private EditText etRackLocation;
    private Button btScan;

    private RackQueryControl rackQueryControl;
    private ListView lvRackDetails;
    private Button btClear;
    private List<RackDetailsData> rackDetailsList;
    private RackQueryAdapter rackQueryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_rack_query, container, false);
        initViews();
        setOnClickListeners();
        return rootView;
    }

    private void setOnClickListeners() {
        btScan.setOnClickListener(this);
        btClear.setOnClickListener(this);
    }

    private void initViews() {
        etRackLocation = rootView.findViewById(R.id.et_rack_location);
        btScan = rootView.findViewById(R.id.bt_scan);
        btClear = rootView.findViewById(R.id.bt_clear);
        lvRackDetails = rootView.findViewById(R.id.lv_rack_details);
        rackQueryControl = new RackQueryControl();
        etRackLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!etRackLocation.getText().toString().equals("")) {
                        btScan.performClick();
                    }
                }
                return false;
            }
        });

        etRackLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("Focus", "Lost Focus");
                    etRackLocation.setText(etRackLocation.getText().toString().toUpperCase());
                }
            }
        });

        etRackLocation.requestFocus();


        if (rackQueryControl.tempDataCount() > 0) {

            rackDetailsList = rackQueryControl.tempData();
            etRackLocation.setText(rackDetailsList.get(0).location);
            rackQueryAdapter = new RackQueryAdapter(rackDetailsList);
            lvRackDetails.setAdapter(rackQueryAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_scan:
                fetchRackDetailsFromTable();
                break;
            case R.id.bt_clear:
                clearFields();
                break;
            default:
                break;
        }
    }

    void okMessage(String title, String message,int flag) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Your action here
                clearFields();
            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    private void clearFields() {
        if (rackQueryControl.tempDataClear()) {
            etRackLocation.setText("");
            rackDetailsList.clear();
            rackQueryAdapter = new RackQueryAdapter(new ArrayList<>());
            lvRackDetails.setAdapter(rackQueryAdapter);
        } else {
            okMessage("Error","Something went wrong. please try later",0);
            vibrate(500);
        }

    }

    private void fetchRackDetailsFromTable() {
        rackDetailsList = rackQueryControl.rackDetails(etRackLocation.getText().toString());
        if (!rackDetailsList.isEmpty()){
            if (rackDetailsList.size() == 1){
                if (rackDetailsList.get(0).warehouse.equals("")&&
                        rackDetailsList.get(0).toteId.equals("")&&
                        rackDetailsList.get(0).boxNo.equals("")){
                    okMessage("Error","Something went wrong, please try again",1);
                    vibrate(500);
                }
                else{
                    rackQueryAdapter = new RackQueryAdapter(rackDetailsList);
                    lvRackDetails.setAdapter(rackQueryAdapter);
                }
            }
            else{
                rackQueryAdapter = new RackQueryAdapter(rackDetailsList);
                lvRackDetails.setAdapter(rackQueryAdapter);
            }

        }
        else {
            okMessage("Error","There is no boxes in this "+etRackLocation.getText().toString() +" location / " +
                    "location is invalid",1);
            vibrate(500);
        }


    }

    private class RackQueryAdapter extends BaseAdapter {
        public List<RackDetailsData> rackDetailsDataList;

        public RackQueryAdapter(List<RackDetailsData> rackDetailsDataList) {
            this.rackDetailsDataList = rackDetailsDataList;
        }

        @Override
        public int getCount() {
            return rackDetailsDataList.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.view_rack_query, null);
            TextView tvWarehouse = myView.findViewById(R.id.tv_warehouse);
            TextView tvBoxNo = myView.findViewById(R.id.tv_box_no);
            TextView tvToteId = myView.findViewById(R.id.tv_tote_id);
            tvWarehouse.setText(rackDetailsDataList.get(position).warehouse);
            tvBoxNo.setText(rackDetailsDataList.get(position).boxNo);
            tvToteId.setText(rackDetailsDataList.get(position).toteId);
            return myView;
        }
    }
}