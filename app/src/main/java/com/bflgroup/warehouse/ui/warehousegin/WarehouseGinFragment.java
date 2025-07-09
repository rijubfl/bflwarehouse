package com.bflgroup.warehouse.ui.warehousegin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.comm.wmsapi.WmsApi;
import com.bflgroup.warehouse.comm.wmsapi.WmsApiCallback;
import com.bflgroup.warehouse.comm.wmsapi.WmsAuthCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WarehouseGinFragment extends Fragment {

    private ListView lv_warehouse_gin_details;
    private Spinner sp_warehouse_gin_warehouse_from;
    private Spinner sp_warehouse_gin_warehouse_to;
    private EditText et_warehouse_gin_trailer_no;
    private EditText et_warehouse_gin_remarks;
    private TextView tv_warehouse_gin_del_date;
    private Button bt_warehouse_gin_clear;
    private Button bt_warehouse_gin_add_edit;
    private Button bt_warehouse_gin_save;

    private EditText et_warehouse_gin_popup_palletno;
    private Button bt_warehouse_gin_popup_scan;
    private TextView tv_warehouse_gin_popup_last_scan;
    private Button bt_warehouse_gin_popup_ok;

    MyScanWarehouseGINScanDetailsAdp objMyScanWarehouseGINScanDetailsAdp;

    WarehouseGINSharedRef saredRef;
    WarehouseGinControl objWarehouseGinControl = new WarehouseGinControl();
    WarehouseGinGlobal objWarehouseGinGlobal = WarehouseGinGlobal.getInstance();
    private boolean b_Result;
    ArrayList<WarehouseGINScanTicket> listWarehouseGINScanTicket = new ArrayList<WarehouseGINScanTicket>();
    /*ArrayList<WarehouseGRNNewScanTicket> listWarehouseGRNNewScanTicket = new ArrayList<WarehouseGRNNewScanTicket>();*/

    ArrayAdapter<String> arrayAdpWarehouses;

    private Global objGlobal = Global.getInstance();
    WmsApi objWMSApi = new WmsApi();

    public WarehouseGinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouse_gin, container, false);

        lv_warehouse_gin_details = (ListView) view.findViewById(R.id.lv_warehouse_gin_details);
        sp_warehouse_gin_warehouse_from = (Spinner) view.findViewById(R.id.sp_warehouse_gin_warehouse_from);
        sp_warehouse_gin_warehouse_to = (Spinner) view.findViewById(R.id.sp_warehouse_gin_warehouse_to);
        et_warehouse_gin_trailer_no = (EditText) view.findViewById(R.id.et_warehouse_gin_trailer_no);
        et_warehouse_gin_remarks = (EditText) view.findViewById(R.id.et_warehouse_gin_remarks);
        tv_warehouse_gin_del_date = (TextView) view.findViewById(R.id.tv_warehouse_gin_del_date);
        bt_warehouse_gin_clear = (Button) view.findViewById(R.id.bt_warehouse_gin_clear);
        bt_warehouse_gin_add_edit = (Button) view.findViewById(R.id.bt_warehouse_gin_add_edit);
        bt_warehouse_gin_save = (Button) view.findViewById(R.id.bt_warehouse_gin_save);

        new WarehouseGinFragment.ApiCallLoadWarehouse(getContext()).execute();

        bt_warehouse_gin_add_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateMain()) {
                    openPopupScanPallet();
                }
            }
        });

        bt_warehouse_gin_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = clearAll();
                                loadScan();
                                sp_warehouse_gin_warehouse_from.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        bt_warehouse_gin_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new WarehouseGinFragment.ApiCallSaveGIN(getContext()).execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        tv_warehouse_gin_del_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DatePickerDialog datePicker;
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // adding the selected date in the edittext
                        tv_warehouse_gin_del_date.setText(dayOfMonth + "/" + (month + 01) + "/" + year);
                    }
                }, year, month, day);

                // set maximum date to be selected as today
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                // show the dialog
                datePicker.show();
                return false;
            }
        });


        return view;
    }

    boolean validateMain(){
        if(sp_warehouse_gin_warehouse_from.getSelectedItem().toString().equals("")) {
            okMessage("GIN","Please select Warehouse From");
            return false;
        }
        if(sp_warehouse_gin_warehouse_to.getSelectedItem().toString().equals("")) {
            okMessage("GIN", "Please select Warehouse To");
            return false;
        }
        if(tv_warehouse_gin_del_date.getText().toString().equals("")){
            okMessage("GIN", "Please enter delivery date");
            return false;
        }
        saredRef.saveTrailerNo(et_warehouse_gin_trailer_no.getText().toString());
        saredRef.saveRemarks(et_warehouse_gin_remarks.getText().toString());
        saredRef.saveWHFrom(sp_warehouse_gin_warehouse_from.getSelectedItem().toString());
        saredRef.saveWHTo(sp_warehouse_gin_warehouse_to.getSelectedItem().toString());
        saredRef.saveTrailerNo(et_warehouse_gin_trailer_no.getText().toString());
        saredRef.saveDelDate( tv_warehouse_gin_del_date.getText().toString());
        saredRef.saveRemarks(et_warehouse_gin_remarks.getText().toString());
        sp_warehouse_gin_warehouse_from.setEnabled(true);
        sp_warehouse_gin_warehouse_to.setEnabled(true);
        et_warehouse_gin_trailer_no.setEnabled(true);
        et_warehouse_gin_remarks.setEnabled(true);
        return true;
    }

    private class ApiCallLoadWarehouse extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        private Context context;
        public ApiCallLoadWarehouse(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            // Do any non-UI background work if needed
            // We'll handle the async API call in onPostExecute
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Trigger your async API call here
            objWMSApi.postWMSAPIAuthToken(context, new WmsAuthCallback() {
                @Override
                public void onTokenReceived(String token) {
                    objWMSApi.getWMSAPICallWithToken(context, "deliverytowarehouse/warehouse", token, new WmsApiCallback() {
                        @Override
                        public void onJsonObjectReceived(JSONObject responseJson) {
                            try {
                                JSONArray jsArWarehouse = responseJson.getJSONArray("warehouse");
                                List<String> arr;
                                arr = new ArrayList<String>();
                                arr.add("");
                                for (int j = 0; j < jsArWarehouse.length(); j++) {
                                    JSONObject detailObj = jsArWarehouse.getJSONObject(j);
                                    arr.add(detailObj.getString("warehouse"));
                                }
                                arrayAdpWarehouses = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
                                sp_warehouse_gin_warehouse_from.setAdapter(arrayAdpWarehouses);
                                sp_warehouse_gin_warehouse_to.setAdapter(arrayAdpWarehouses);

                                saredRef = new WarehouseGINSharedRef(getContext());
                                if (saredRef.loadWHFrom() != "") {
                                    sp_warehouse_gin_warehouse_from.setSelection(arrayAdpWarehouses.getPosition(saredRef.loadWHFrom()));
                                    sp_warehouse_gin_warehouse_to.setSelection(arrayAdpWarehouses.getPosition(saredRef.loadWHTo()));
                                    et_warehouse_gin_trailer_no.setText(saredRef.loadTrailerNo());
                                    tv_warehouse_gin_del_date.setText(saredRef.loadDelDate());
                                    et_warehouse_gin_remarks.setText(saredRef.loadRemarks());
                                    sp_warehouse_gin_warehouse_from.setEnabled(false);
                                    sp_warehouse_gin_warehouse_to.setEnabled(false);
                                    et_warehouse_gin_trailer_no.setEnabled(false);
                                    et_warehouse_gin_remarks.setEnabled(false);
                                }
                                loadScan();
                                if (dialog.isShowing()) dialog.dismiss();
                            } catch (Exception e) {
                                if (dialog.isShowing()) dialog.dismiss();
                                okMessage("Second API Failed", e.toString());
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (dialog.isShowing()) dialog.dismiss();
                            okMessage("Second API Failed", errorMessage);
                        }
                    });
                    // You can chain other operations here
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    okMessage("WMS API Login Failed", errorMessage);
                }
            });
        }
    }

    boolean clearAll() {
        sp_warehouse_gin_warehouse_from.setSelection(arrayAdpWarehouses.getPosition(""));
        sp_warehouse_gin_warehouse_to.setSelection(arrayAdpWarehouses.getPosition(""));
        et_warehouse_gin_trailer_no.setText("");
        tv_warehouse_gin_del_date.setText("");
        et_warehouse_gin_remarks.setText("");
        saredRef.saveWHFrom("");
        saredRef.saveWHTo("");
        saredRef.saveTrailerNo("");
        saredRef.saveDelDate("");
        saredRef.saveRemarks("");
        sp_warehouse_gin_warehouse_from.setEnabled(true);
        sp_warehouse_gin_warehouse_to.setEnabled(true);
        et_warehouse_gin_trailer_no.setEnabled(true);
        et_warehouse_gin_remarks.setEnabled(true);
        if(!objWarehouseGinControl.clearAll()){
            okMessage("", objGlobal.getErrorMessage());
        }
        return true;
    }

    private void openPopupScanPallet() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.warehouse_gin_popup_scan_window);

        et_warehouse_gin_popup_palletno = (EditText) myDialog.findViewById(R.id.et_warehouse_gin_popup_palletno);
        bt_warehouse_gin_popup_scan = (Button) myDialog.findViewById(R.id.bt_warehouse_gin_popup_scan);
        tv_warehouse_gin_popup_last_scan = (TextView) myDialog.findViewById(R.id.tv_warehouse_gin_popup_last_scan);
        bt_warehouse_gin_popup_ok = (Button) myDialog.findViewById(R.id.bt_warehouse_gin_popup_ok);

        et_warehouse_gin_popup_palletno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        bt_warehouse_gin_popup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_warehouse_gin_popup_last_scan.setText("");
                new WarehouseGinFragment.ApiCallScanPallet(getContext()).execute();
            }
        });

        bt_warehouse_gin_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                loadScan();
            }
        });
        loadScan();
        myDialog.show();
        et_warehouse_gin_popup_palletno.requestFocus();
    }

    private class ApiCallScanPallet extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        private Context context;

        public ApiCallScanPallet(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            // Do any non-UI background work if needed
            // We'll handle the async API call in onPostExecute
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Trigger your async API call here
            if (et_warehouse_gin_popup_palletno.getText().toString().isEmpty()) {
                tv_warehouse_gin_popup_last_scan.setText("Please scan pallet");
                et_warehouse_gin_popup_palletno.requestFocus();
                if (dialog.isShowing()) dialog.dismiss();
            } else {
                String palletno = et_warehouse_gin_popup_palletno.getText().toString();
                et_warehouse_gin_popup_palletno.setText("");
                objWMSApi.postWMSAPIAuthToken(context, new WmsAuthCallback() {
                    @Override
                    public void onTokenReceived(String token) {
                        JSONObject jsonRequest = new JSONObject();
                        try {
                            jsonRequest.put("ginlocation", objGlobal.getCountryCode());
                            JSONArray palletsArray = new JSONArray();
                            palletsArray.put(palletno);
                            jsonRequest.put("pallets", palletsArray);
                        } catch (Exception e) {
                            if (dialog.isShowing()) dialog.dismiss();
                            tv_warehouse_gin_popup_last_scan.setText(e.getMessage());
                            return;
                        }
                        objWMSApi.postWMSAPICallWithToken(context, "deliverytowarehouse/gin/validate/pallet", token, jsonRequest, new WmsApiCallback() {
                            @Override
                            public void onJsonObjectReceived(JSONObject responseJson) {
                                try {
                                    JSONArray jsArPallets = responseJson.getJSONArray("pallets");
                                    for (int j = 0; j < jsArPallets.length(); j++) {
                                        JSONObject detailObj = jsArPallets.getJSONObject(j);
                                        String scPalletno = detailObj.getString("palletno");
                                        boolean status = detailObj.getBoolean("status");
                                        String message=detailObj.getString("message");
                                        if(status){
                                            if (!objWarehouseGinControl.loadScanPalletFromAPI(scPalletno)) {
                                                if (dialog.isShowing()) dialog.dismiss();
                                                tv_warehouse_gin_popup_last_scan.setTextColor(Color.RED);
                                                tv_warehouse_gin_popup_last_scan.setText(scPalletno+"\n"+objGlobal.getErrorMessage());
                                            } else {
                                                if (dialog.isShowing()) dialog.dismiss();
                                                tv_warehouse_gin_popup_last_scan.setTextColor(Color.BLACK);
                                                tv_warehouse_gin_popup_last_scan.setText(scPalletno);
                                                loadScan();
                                            }
                                        } else {
                                            if (dialog.isShowing()) dialog.dismiss();
                                            tv_warehouse_gin_popup_last_scan.setTextColor(Color.RED);
                                            tv_warehouse_gin_popup_last_scan.setText(scPalletno+"\n"+message);
                                        }
                                    }
                                } catch (Exception e) {
                                    if (dialog.isShowing()) dialog.dismiss();
                                    tv_warehouse_gin_popup_last_scan.setTextColor(Color.RED);
                                    tv_warehouse_gin_popup_last_scan.setText(e.toString());
                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                if (dialog.isShowing()) dialog.dismiss();
                                tv_warehouse_gin_popup_last_scan.setText(errorMessage);
                            }
                        });
                        // You can chain other operations here
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog.isShowing()) dialog.dismiss();
                        tv_warehouse_gin_popup_last_scan.setText(errorMessage);
                    }
                });
            }
        }
    }

    private class MyScanWarehouseGINScanDetailsAdp extends BaseAdapter {
        public ArrayList<WarehouseGINScanTicket> listWarehouseGINScanTicket;

        public MyScanWarehouseGINScanDetailsAdp(ArrayList<WarehouseGINScanTicket> listWarehouseGINScanTicket) {
            this.listWarehouseGINScanTicket = listWarehouseGINScanTicket;
        }

        @Override
        public int getCount() {
            return listWarehouseGINScanTicket.size();
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
            View myView = mInflater.inflate(R.layout.warehouse_gin_details_ticket, null);
            final WarehouseGINScanTicket s = listWarehouseGINScanTicket.get(position);

            TextView tv_warehouse_gin_details_ticket_palletno = (TextView) myView.findViewById(R.id.tv_warehouse_gin_details_ticket_palletno);
            tv_warehouse_gin_details_ticket_palletno.setText(String.valueOf(s.palletno));

            return myView;
        }
    }

    private class ApiCallSaveGIN extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        private Context context;

        public ApiCallSaveGIN(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            // Do any non-UI background work if needed
            // We'll handle the async API call in onPostExecute
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Trigger your async API call here
            objWMSApi.postWMSAPIAuthToken(context, new WmsAuthCallback() {
                @Override
                public void onTokenReceived(String token) {
                    String warehousefrom = sp_warehouse_gin_warehouse_from.getSelectedItem().toString();
                    String warehouseto = sp_warehouse_gin_warehouse_to.getSelectedItem().toString();
                    String deldate = tv_warehouse_gin_del_date.getText().toString();
                    String trailerno = et_warehouse_gin_trailer_no.getText().toString();
                    String remarks = et_warehouse_gin_remarks.getText().toString();
                    JSONObject jsonRequest = objWarehouseGinControl.loadScanForSave(warehousefrom, warehouseto, deldate, trailerno, remarks);
                    if (jsonRequest == null) {
                        if (dialog.isShowing()) dialog.dismiss();
                        okMessage("JSON Error", objGlobal.getErrorMessage());
                        return;
                    }
                    objWMSApi.postWMSAPICallWithToken(context, "deliverytowarehouse/gin", token, jsonRequest, new WmsApiCallback() {
                        @Override
                        public void onJsonObjectReceived(JSONObject responseJson) {
                            clearAll();
                            loadScan();
                            if (dialog.isShowing()) dialog.dismiss();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (dialog.isShowing()) dialog.dismiss();
                            okMessage("Second API Failed", errorMessage);
                        }
                    });
                    // You can chain other operations here
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    okMessage("WMS API Login Failed", errorMessage);
                }
            });
        }
    }


    void loadScan() {
        listWarehouseGINScanTicket.clear();
        listWarehouseGINScanTicket = objWarehouseGinControl.loadGinScan();
        objMyScanWarehouseGINScanDetailsAdp = new WarehouseGinFragment.MyScanWarehouseGINScanDetailsAdp(listWarehouseGINScanTicket);
        lv_warehouse_gin_details.setAdapter(objMyScanWarehouseGINScanDetailsAdp);
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
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
