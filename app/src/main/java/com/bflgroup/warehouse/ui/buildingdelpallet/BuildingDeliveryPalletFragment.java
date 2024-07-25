package com.bflgroup.warehouse.ui.buildingdelpallet;

import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.setCount;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.getPltCount;
import static com.loopj.android.http.AsyncHttpClient.log;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BuildingDeliveryPalletFragment extends Fragment {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

    private BuildingDeliveryPalletGlobal objpalletbuilding = BuildingDeliveryPalletGlobal.getInstance();
    private BuildingDeliveryPalletControl objbuildingdelPalletControl = new BuildingDeliveryPalletControl();

    private Spinner sp_plt_route_id;
    private TextView tv_shopnames_col;
    private EditText et_plt_shop_transferno;
    private TextView tv_count;
    private EditText Remarks;
    private Spinner sp_plt_shopname;
    private Button bt_transfer_scan;
    private Button bt_shop_return_scan;
    private Button bt_div_Clear;
    private Button bt_status_build_plt;
    private ListView lv_div_seperate_details;
    private String transferno = "";
    private  int get_route_id;
    public boolean b_Result;
    String remark = "";
    Integer count = 0;
    String android_id;
    Boolean strflg = false;

    ArrayList<PalletScanDeliveryItem> PalletScanDeliveryItem = new ArrayList<PalletScanDeliveryItem>();
    MyTransferStatusPltAdp objTransferStatusPltAdp = null;
    PltScanTransferShared PltScanTransferShared;


    public BuildingDeliveryPalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_delivery_pallet, container, false);

        sp_plt_route_id =  (Spinner) view.findViewById(R.id.sp_plt_route_id);
        tv_shopnames_col = (TextView) view.findViewById(R.id.tv_shopnames_col);
        et_plt_shop_transferno = (EditText) view.findViewById(R.id.et_plt_shop_transferno);
        Remarks = (EditText) view.findViewById(R.id.et_remarks);
        sp_plt_shopname = (Spinner) view.findViewById(R.id.sp_plt_shopname);
        bt_transfer_scan = (Button) view.findViewById(R.id.bt_transfer_scan);
        // bt_shop_return_scan = (Button) view.findViewById(R.id.bt_shop_return_scan);
        bt_status_build_plt = (Button) view.findViewById(R.id.bt_status_build_plt);
        lv_div_seperate_details = (ListView) view.findViewById(R.id.lv_div_seperate_det);
        bt_div_Clear = (Button) view.findViewById(R.id.bt_status_clear);
        tv_count = (TextView) view.findViewById(R.id.tv_count);

        PltScanTransferShared=new PltScanTransferShared(getContext());

        if ( objGlobal.getCountryCode().equals("KSA")){
            List<String> arr1 = objbuildingdelPalletControl.loadKsaShops();
            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
            sp_plt_route_id.setAdapter(arrayAdp1);
            if (PltScanTransferShared.Routeidload() != "") {


                sp_plt_route_id.setSelection(arrayAdp1.getPosition(PltScanTransferShared.Routeidload().toString()));
                sp_plt_route_id.setEnabled(false);

                try {

                    PalletScanDeliveryItem = objbuildingdelPalletControl.LoadPltData();

                    count = Integer.valueOf(objbuildingdelPalletControl.LoadPltDataCount().toString());
                    count = getPltCount();
                    tv_count.setText(count+"");

                    objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
                    lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
                    Log.e("item","reached");
                } catch (SQLException e) {
                    Log.e("Log",e.toString());
                }

            }
        }
        else{
            List<Integer> arr1 = objbuildingdelPalletControl.loadRoute();
            ArrayAdapter<Integer> arrayAdp1 = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
            sp_plt_route_id.setAdapter(arrayAdp1);
            Log.e("ROUTEIDLOAD -- UAE",PltScanTransferShared.Routeidload());
            if (PltScanTransferShared.Routeidload() != "") {


                sp_plt_route_id.setSelection(arrayAdp1.getPosition(Integer.parseInt(PltScanTransferShared.Routeidload().toString())));
                sp_plt_route_id.setEnabled(false);

                try {

                    PalletScanDeliveryItem = objbuildingdelPalletControl.LoadPltData();

                    count = Integer.valueOf(objbuildingdelPalletControl.LoadPltDataCount().toString());
                    count = getPltCount();
                    tv_count.setText(count+"");

                    objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
                    lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
                    Log.e("item","reached");
                } catch (SQLException e) {
                    Log.e("Log",e.toString());
                }

            }
        }


        sp_plt_route_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (objGlobal.getCountryCode().equals("KSA")){




                    get_route_id = objbuildingdelPalletControl.loadKsaRoute(sp_plt_route_id.getSelectedItem().toString().split("\\(")[0]);
                    String get_shop_names = objbuildingdelPalletControl.LoadShops(get_route_id);
                    tv_shopnames_col.setText(get_shop_names);

                }
                else
                {
                    get_route_id = Integer.parseInt(sp_plt_route_id.getSelectedItem().toString());

                    String get_shop_names = objbuildingdelPalletControl.LoadShops(get_route_id);
                    tv_shopnames_col.setText(get_shop_names);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        et_plt_shop_transferno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_plt_shop_transferno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(GetScanresult()){
                        et_plt_shop_transferno.requestFocus();
                        et_plt_shop_transferno.setFocusable(true);
                        strflg = true;
                        return true;
                    }else {
                        et_plt_shop_transferno.requestFocus();
                        et_plt_shop_transferno.setFocusable(true);
                        return true;
                    }
                }
                else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_plt_shop_transferno.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }


        });

        et_plt_shop_transferno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_plt_shop_transferno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(GetScanresult()){
                        et_plt_shop_transferno.requestFocus();
                        et_plt_shop_transferno.setFocusable(true);
                        strflg = true;
                        return true;
                    }else {
                        et_plt_shop_transferno.requestFocus();
                        et_plt_shop_transferno.setFocusable(true);
                        return true;
                    }
                }
                else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_plt_shop_transferno.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }


        });

        bt_transfer_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GetScanresult()){
                    Log.e("Error","Reached here");
                    et_plt_shop_transferno.requestFocus();
                }
            }
        });

        et_plt_shop_transferno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("Focus","Lost Focus");
                    et_plt_shop_transferno.setText(et_plt_shop_transferno.getText().toString().toUpperCase());
                }
            }
        });



        bt_status_build_plt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                try {
                    remark = Remarks.getText().toString();
                    if(sp_plt_shopname.getSelectedItemPosition() == 0){
                        okMessage("Alert", "Pls Select Shopname First", getContext());
                    }else {
                        if (PalletScanDeliveryItem.size() >= 1) {

                            try {
                                if (objbuildingdelPalletControl.InsertPalletDetails(remark, sp_plt_route_id.getSelectedItem().toString())) {
                                    Log.e("return", "Build");
                                    okMessage("SUCCESS", "Build Pallet Successfully Pallet Number is - " + BuildingDeliveryPalletGlobal.getPalletNo(), getContext());
                                    //AlertDialog(getContext(), "Build Pallet and Gin Successfully Gin Number is - "+ Math.round(Float.parseFloat(String.valueOf(GinScanTransferGlobal.getGinno()))));
                                    clear();
                                    lv_div_seperate_details.setAdapter(null);
                                    // Toast.makeText(getContext(), "Value 0325    Inserted", Toast.LENGTH_SHORT).show();
                                } else {
                                    okMessage("Alert", objGlobal.getErrorMessage(), getContext());
                                }
                            } catch (ParseException e) {
                                log.e("Error message", e.toString());
                            }
                        } else {
                            Log.e("return", "Not Build");
                            okMessage("Alert", "Please Scan Trf No/Tote id before building Pallet", getContext());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        bt_div_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clear();


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
        sp_plt_shopname.setAdapter(null);




        return view;
    }




    public boolean GetScanresult(){
        transferno = et_plt_shop_transferno.getText().toString();
        Log.e("transferno", transferno);
        if(et_plt_shop_transferno.getText().toString().isEmpty()) {
            okMessage("Alert",  "Please Scan Trf no/Toteid", getContext());
            return false;
        }else {
            if (sp_plt_route_id.getSelectedItemId() != 0) {
                List<String> arr1 = objbuildingdelPalletControl.GetShops(getActivity(), transferno, get_route_id);
                ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1)
                {
                    @Override
                    public boolean isEnabled(int position){
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return position != 0;
                    }
                    @Override
                    public View getDropDownView(
                            int position, View convertView,
                            @NonNull ViewGroup parent) {

                        // Get the item view
                        View view = super.getDropDownView(
                                position, convertView, parent);
                        TextView textView = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            textView.setTextColor(Color.GRAY);
                        }
                        else { textView.setTextColor(Color.BLACK); }
                        return view;
                    }
                };

                sp_plt_shopname.setAdapter(arrayAdp1);
                //sp_plt_shopname.setSelection(arrayAdp1.getPosition(Integer.parseInt(PltScanTransferShared.Routeidload().toString())));
                //sp_plt_shopname.setEnabled(true);

                int SpinnerCount = sp_plt_shopname.getAdapter().getCount();


                PalletScanDeliveryItem = new ArrayList<>();
               // sp_plt_shopname.setSelection(0);
                if (SpinnerCount > 2) {

                    Log.e("Loaded", "Here");
                    sp_plt_shopname.setEnabled(true);



                        sp_plt_shopname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override

                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                Log.e("Loaded2", "Here");
                                if(sp_plt_shopname.getSelectedItemId() != 0){
                                    String shopname = sp_plt_shopname.getSelectedItem().toString();
                                PalletScanDeliveryItem = objbuildingdelPalletControl.ScanTransfer2(getActivity(), transferno, get_route_id, android_id, shopname);
                                objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
                                lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
                                if (objTransferStatusPltAdp != null) {
                                    PltScanTransferShared.Routeidsave(sp_plt_route_id.getSelectedItem().toString());
                                    sp_plt_route_id.setEnabled(false);
                                    sp_plt_route_id.setClickable(false);
                                    et_plt_shop_transferno.requestFocus();
                                }
                                count = getPltCount();
                                tv_count.setText(count + "");
                                et_plt_shop_transferno.setText("");
                                et_plt_shop_transferno.requestFocus();
                            }
                                  else{

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                            }

                        });

                } else {
                    sp_plt_shopname.setEnabled(false);
                    PalletScanDeliveryItem = objbuildingdelPalletControl.ScanTransfer(getActivity(), transferno, get_route_id, android_id);
                    et_plt_shop_transferno.requestFocus();
                    objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
                    lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
                    if (PalletScanDeliveryItem.size() != 0) {
                        PltScanTransferShared.Routeidsave(sp_plt_route_id.getSelectedItem().toString());
                        sp_plt_route_id.setEnabled(false);
                        sp_plt_route_id.setClickable(false);
                        et_plt_shop_transferno.requestFocus();
                        count = getPltCount();
                        tv_count.setText(count+"");
                        et_plt_shop_transferno.setText("");
                        sp_plt_shopname.setAdapter(null);
                    }
                }
            }
            else{
                okMessage("Alert", "Please Select Route", getContext());
                return false;
            }
            // Log.e("getCount spinner", SpinnerCount + "");


            et_plt_shop_transferno.setText("");
            et_plt_shop_transferno.requestFocus();
            return false;
        }

    }




    private class MyTransferStatusPltAdp extends BaseAdapter {
        public  ArrayList<PalletScanDeliveryItem> pltScanItemsList;

        public MyTransferStatusPltAdp(ArrayList<PalletScanDeliveryItem> PltScanItems) {
            this.pltScanItemsList = PltScanItems;
        }

        @Override
        public int getCount() {
            return pltScanItemsList.size();
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
            View myView = mInflater.inflate(R.layout.pallet_transfer_details, null);
            final PalletScanDeliveryItem s = pltScanItemsList.get(position);
            TextView tv_transfer_no_details = (TextView) myView.findViewById(R.id.tv_transfer_no_details);
            tv_transfer_no_details.setText(String.valueOf(s.TransferNo));
            TextView tv_toteid_details = (TextView) myView.findViewById(R.id.tv_toteid_details);
            tv_toteid_details.setText(String.valueOf(s.Toteid));
            TextView tv_shopname_details = (TextView) myView.findViewById(R.id.tv_shopname_details);
            tv_shopname_details.setText(String.valueOf(s.ShopName));
            TextView tv_Pallet_qty = (TextView) myView.findViewById(R.id.tv_Pallet_qty);
            tv_Pallet_qty.setText(String.valueOf(s.Qty));
            return myView;
        }
    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private void clear(){
        sp_plt_route_id.clearFocus();
        et_plt_shop_transferno.setText("");
        Remarks.setText("");
        sp_plt_shopname.setAdapter(null);
        sp_plt_route_id.setSelection(0);
        transferno = "";
        tv_shopnames_col.setText("");
        sp_plt_route_id.setEnabled(true);
        sp_plt_route_id.setClickable(true);
        PltScanTransferShared.Routeidsave("");
        count = 0;
        setCount(count);
        tv_count.setText("");
        if(objbuildingdelPalletControl.deletetemp()){
            // Clear collection..
            lv_div_seperate_details.setAdapter(null);
        }
    }
}