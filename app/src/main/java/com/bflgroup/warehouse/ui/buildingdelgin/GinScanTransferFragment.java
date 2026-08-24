package com.bflgroup.warehouse.ui.buildingdelgin;

import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.getCount;
import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.getPalletCount;
import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.setCount;
import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.setPalletCount;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class GinScanTransferFragment extends Fragment {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

    private GinScanTransferGlobal objGinScanTransfer = GinScanTransferGlobal.getInstance();
    private GinScantransferControl objGinScanTransferControl;
    GinScanTransferShared GinScanTransferShared;

    private Spinner sp_gin_route_id;
    private TextView tv_shopnames_col;
    private TextView tvscancount;
    private EditText et_shop_transferno;
    private EditText et_g_pallet;

    private EditText driver_name;
    private EditText et_car_no;
    private EditText et_Ship_no;
    private EditText Remarks;
    private EditText et_date;
    //    private Spinner sp_gin_shopname;
    private Button bt_transfer_return_scan;
    private Button bt_shop_return_scan;
    private Button bt_div_Clear;
    private Button bt_clear_pallet;
    private Button bt_div_build_gin;
    //private Button bt_status_pallet_next;
    private ListView lv_div_seperate_details;
    private String transferno = "";
    private String palletno = "";
    private int get_route_id;
    public boolean b_Result;
    String remark = "";
    String car_no = "";
    String driver = "";
    String ship_no = "";
    String android_id;
    TextView tv_count;
    Integer count = 0;
    Integer palletcount = 0;
    Boolean strflg = false;
    String etDate;


    final Calendar myCalendar = Calendar.getInstance();
    ArrayList<GinScanItem> GinScanItemStatus = new ArrayList<GinScanItem>();
    DatePickerDialog datePicker;
    MyTransferStatusAdp objTransferStatusAdp = null;
    private TextView tv_gin_route_id;
    private EditText et_way_slips;
    private Spinner sp_vehicle_type;
    private Spinner sp_vendor;

    public GinScanTransferFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        if (objGlobal.getCountryCode().equals("KSA")) {
            view = inflater.inflate(R.layout.fragment_gin_scan_transfers_ksa, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_gin_scan_transfers, container, false);
        }
        sp_gin_route_id = (Spinner) view.findViewById(R.id.sp_gin_route_id);
        tv_gin_route_id = (TextView) view.findViewById(R.id.tv_gin_route_id);
        tv_shopnames_col = (TextView) view.findViewById(R.id.tv_shopnames_col);
        et_shop_transferno = (EditText) view.findViewById(R.id.et_shop_transferno);
        et_g_pallet = (EditText) view.findViewById(R.id.et_g_pallet);
        Remarks = (EditText) view.findViewById(R.id.et_remarks);
        driver_name = (EditText) view.findViewById(R.id.et_driver);
        et_car_no = (EditText) view.findViewById(R.id.et_car_no);
        et_Ship_no = (EditText) view.findViewById(R.id.et_Ship_no);
//        sp_gin_shopname = (Spinner) view.findViewById(R.id.sp_gin_shopname);
        tvscancount = (TextView) view.findViewById(R.id.tvscancount);
        bt_transfer_return_scan = (Button) view.findViewById(R.id.bt_transfer_return_scan);
        // bt_shop_return_scan = (Button) view.findViewById(R.id.bt_shop_return_scan);
        bt_div_build_gin = (Button) view.findViewById(R.id.bt_status_build_Gin);
        lv_div_seperate_details = (ListView) view.findViewById(R.id.lv_div_seperate_det);
        bt_div_Clear = (Button) view.findViewById(R.id.bt_status_clear);
        //  bt_status_pallet_next = (Button) view.findViewById(R.id.bt_status_pallet_next);
        bt_clear_pallet = (Button) view.findViewById(R.id.bt_clear_pallet);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        et_date = (EditText) view.findViewById(R.id.et_date);
        et_way_slips = (EditText) view.findViewById(R.id.et_way_slips);
        sp_vehicle_type = (Spinner) view.findViewById(R.id.sp_vehicle_type);
        sp_vendor = (Spinner) view.findViewById(R.id.sp_vendor);

        android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Calendar calendar = Calendar.getInstance();

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        et_g_pallet.requestFocus();
        et_g_pallet.setFocusable(true);

        objGinScanTransferControl = new GinScantransferControl(getContext());
        GinScanTransferShared = new GinScanTransferShared(getContext());
        if (objGlobal.getCountryCode().equals("KSA")){
            List<String> vehicleVendorList = objGinScanTransferControl.loadVehicleVendorForKsa();
            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, vehicleVendorList);
            sp_vendor.setAdapter(arrayAdp1);
        }

        List<Integer> arr1 = objGinScanTransferControl.loadRoute();
        ArrayAdapter<Integer> arrayAdp1 = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
        sp_gin_route_id.setAdapter(arrayAdp1);


//        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int day) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH,month);
//                myCalendar.set(Calendar.DAY_OF_MONTH,day);
////                datePicker.setMinDate(System.currentTimeMillis() - 1000);
//                updateLabel();
//            }
//        };

//
//        et_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });


        // initialising the datepickerdialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePicker = new DatePickerDialog(getContext());
        }

        // click on edittext to set the value
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // adding the selected date in the edittext
                        et_date.setText(dayOfMonth + "/" + (month + 01) + "/" + year);
                    }
                }, year, month, day);

                // set maximum date to be selected as today
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                // show the dialog
                datePicker.show();
            }
        });


        if (GinScanTransferShared.loadRouteid() != "") {

            sp_gin_route_id.setSelection(arrayAdp1.getPosition(Integer.parseInt(GinScanTransferShared.loadRouteid().toString())));
            sp_gin_route_id.setEnabled(false);
            palletno = GinScanTransferShared.loadPalletno();
            try {
                GinScanItemStatus = objGinScanTransferControl.LoadGinData();
                count = Integer.valueOf(objGinScanTransferControl.LoadGinDataCount().toString());
                palletcount = Integer.valueOf(objGinScanTransferControl.LoadPalletDataCount(palletno).toString());
                count = getCount();

                tv_count.setText(count + "");

                objTransferStatusAdp = new MyTransferStatusAdp(GinScanItemStatus);
                lv_div_seperate_details.setAdapter(objTransferStatusAdp);
                Log.e("Gin item", "reached");
            } catch (SQLException e) {
                Log.e("Log", e.toString());
            }
            Log.e("palletcount", palletcount + "");
            if (GinScanTransferShared.loadPalletno() != "") {

                et_g_pallet.setText(GinScanTransferShared.loadPalletno());

                tvscancount.setText(palletcount + "/" + GinScanTransferShared.loadPalletCount() + "");
                et_g_pallet.setEnabled(false);
                if (objGlobal.getCountryCode().equals("KSA"))
                    tv_gin_route_id.setText(GinScanTransferShared.loadRouteid());
                tv_shopnames_col.setText(GinScanTransferShared.loadShopnames());
            }

            et_shop_transferno.requestFocus();
            et_shop_transferno.setFocusable(true);
            get_route_id = Integer.parseInt(GinScanTransferShared.loadRouteid());

        }

//        bt_status_pallet_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vibrate(500, getContext());
//                if (palletcount != Integer.parseInt(GinScanTransferShared.loadPalletCount()) && palletcount!=0) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                    alert.setMessage("Are you sure to proceed this pallet - " + palletno + "? Scan Boxes are not equal to to total Transfers")
//                            .setTitle("Confirmation")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    et_g_pallet.setText("");
//                                    tvscancount.setText("0/0");
//                                    palletcount = 0;
//                                    et_g_pallet.setEnabled(true);
//                                    setPalletCount(0);
//                                    GinScanTransferShared.savePalletno("");
//                                    GinScanTransferShared.savePalletCount("0");
//                                    et_g_pallet.setFocusable(true);
//                                    et_g_pallet.requestFocus();
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//                }
//                else{
//                    et_g_pallet.setText("");
//                    tvscancount.setText("0/0");
//                    palletcount = 0;
//                    et_g_pallet.setEnabled(true);
//                    GinScanTransferShared.savePalletno("");
//                    GinScanTransferShared.savePalletCount("0");
//                    et_g_pallet.setFocusable(true);
//                    setPalletCount(0);
//                    et_g_pallet.requestFocus();
//                }
//            }
//        });


        sp_gin_route_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (objGlobal.getCountryCode().equals("KSA"))
                    get_route_id = Integer.parseInt(tv_gin_route_id.toString());
                else
                    get_route_id = Integer.parseInt(sp_gin_route_id.getSelectedItem().toString());
                String get_shop_names = objGinScanTransferControl.LoadShops(get_route_id);
                if (objGlobal.getCountryCode().equals("KSA"))
                    GinScanTransferShared.saveShopnames(get_shop_names);
                tv_shopnames_col.setText(get_shop_names);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        et_g_pallet.setOnTouchListener(new View.OnTouchListener() {
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

        et_shop_transferno.setOnTouchListener(new View.OnTouchListener() {
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

        et_g_pallet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (GetScanPallet() != 0) {
                        et_g_pallet.setEnabled(false);
                        et_shop_transferno.requestFocus();
                        et_shop_transferno.setFocusable(true);
                        sp_gin_route_id.setEnabled(false);
                        strflg = true;
                        return true;
                    } else {

                        et_g_pallet.setText("");
                        et_g_pallet.setEnabled(true);
                        et_g_pallet.requestFocus();
                        return false;
                    }
                }
                return false;
            }
        });

        et_shop_transferno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (objGinScanTransferControl.isBlockingGin(getContext())){
                        okMessage("",objGlobal.getErrorMessage(),getContext());
                        return false;
                    }
                    else{
                        boolean resultStatus;
                        if (objGlobal.getCountryCode().equals("KSA"))
                            resultStatus = GetScanresultKSA();
                        else
                            resultStatus = GetScanresult();

                        if (resultStatus) {
                            et_shop_transferno.requestFocus();
                            et_shop_transferno.setFocusable(true);
                            strflg = true;
                            return true;
                        } else {
                            et_shop_transferno.requestFocus();
                            et_shop_transferno.setFocusable(true);
                            return true;
                        }
                    }

                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_shop_transferno.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }


        });

        et_shop_transferno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("Focus", "Lost Focus");
                    et_shop_transferno.setText(et_shop_transferno.getText().toString().toUpperCase());
                }
            }
        });

        bt_transfer_return_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objGinScanTransferControl.isBlockingGin(getContext())){
                    okMessage("",objGlobal.getErrorMessage(),getContext());
                }
                else{
                    transferno = et_shop_transferno.getText().toString();
                    boolean resultStatus;
                    if (objGlobal.getCountryCode().equals("KSA"))
                        resultStatus = GetScanresultKSA();
                    else
                        resultStatus = GetScanresult();

                    if (resultStatus) {
                        Log.e("Error", "Reached here");
                        et_shop_transferno.requestFocus();
                    }
                }

            }
        });


        bt_div_build_gin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (objGinScanTransferControl.isBlockingGin(getContext())){
                    okMessage("",objGlobal.getErrorMessage(),getContext());
                }
                else{
                    if (palletcount == Integer.parseInt(GinScanTransferShared.loadPalletCount())) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are you sure you want to Build GIN?")
                                .setTitle("Confirmation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            remark = Remarks.getText().toString();
                                            driver = driver_name.getText().toString();
                                            car_no = et_car_no.getText().toString();
                                            ship_no = et_Ship_no.getText().toString();
                                            etDate = et_date.getText().toString();
                                            if (GinScanItemStatus.size() >= 1) {

                                                try {
                                                    String routeId;
                                                    if (objGlobal.getCountryCode().equals("KSA"))
                                                        routeId  = tv_gin_route_id.getText().toString();
                                                    else
                                                        routeId  = sp_gin_route_id.getSelectedItem().toString();
                                                    if (objGinScanTransferControl.InsertPalletDetails(etDate, remark, driver, car_no, ship_no,routeId )) {
                                                        Log.e("return", "Build");
                                                        vibrate(500, getContext());
                                                        int Ginno = Math.round(Float.parseFloat(String.valueOf(GinScanTransferGlobal.getGinno())));
                                                        if (objGlobal.getCountryCode().equals("KSA")) {
                                                            String vendor = sp_vendor.getSelectedItem().toString();
                                                            String subVendor = vendor.length() >= 3 ? vendor.substring(0, 3) : vendor;
                                                            if (!objGinScanTransferControl.waySlipsPost(Ginno, subVendor+et_way_slips.getText().toString(), sp_vehicle_type.getSelectedItem().toString(),
                                                                    sp_vendor.getSelectedItem().toString())) {
                                                                okMessage("Alert", "Way slips, vehicle type, vendor is not updated. Please contact IT", getContext());
                                                            }
                                                        }
                                                        okMessage("SUCCESS", "Build Pallet and Gin Successfully Gin Number is - " + Math.round(Float.parseFloat(String.valueOf(GinScanTransferGlobal.getGinno()))), getContext());
                                                        objGinScanTransferControl.GinReminderDetails(Ginno);

                                                        //AlertDialog(getContext(), "Build Pallet and Gin Successfully Gin Number is - "+ Math.round(Float.parseFloat(String.valueOf(GinScanTransferGlobal.getGinno()))));
                                                        clear();
                                                        lv_div_seperate_details.setAdapter(null);
                                                        // Toast.makeText(getContext(), "Value 0325    Inserted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        vibrate(500, getContext());
                                                        AlertDialog(getContext(), objGlobal.getErrorMessage());
                                                    }


                                                } catch (ParseException e) {
                                                    Log.e("Error message", e.toString());
                                                }
                                            } else {
                                                Log.e("return", "Not Build");
                                                AlertDialog(getContext(), "Please Scan Trf No/Tote id before building Gin");
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    } else {
                        okMessage("Alert", "Please scan all transfers in this Pallet - " + palletno, getContext());
                    }
                }

            }
        });


        bt_div_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(500, getContext());
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Confirmation")
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

        bt_clear_pallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  vibrate(500,getContext());
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear this pallet - " + palletno + "?")
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearPallet();
                                et_g_pallet.setFocusable(true);
                                et_g_pallet.requestFocus();
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

        if (objGlobal.getCountryCode().equals("KSA"))
            et_way_slips.setEnabled(true);


        return view;
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        et_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    public int GetScanPallet() {
        palletno = et_g_pallet.getText().toString();
        int countpallet = 0;
        if (et_g_pallet.getText().toString().isEmpty()) {
            vibrate(500, getContext());
            AlertDialog(getContext(), "Please scan pallet no");

        } else {
            if (!objGlobal.getCountryCode().equals("KSA")) {
                try {
                    if (GinScanTransferShared.loadRouteid() == "") {
                        countpallet = objGinScanTransferControl.getpallets(getActivity(), palletno, get_route_id);
                    } else {
                        countpallet = objGinScanTransferControl.getpallets(getActivity(), palletno, Integer.parseInt(GinScanTransferShared.loadRouteid()));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                tvscancount.setText(getPalletCount() + " / " + Integer.toString(countpallet));
            }
            else{
                try {
                    int routeId = -1;

                    if (!tv_gin_route_id.getText().toString().equals("") && tv_gin_route_id.getText().toString() != null)
                        routeId = Integer.parseInt(tv_gin_route_id.getText().toString());
                    PalletScanItem palletScanItem = objGinScanTransferControl.getpallet(getActivity(), palletno, routeId);
                    countpallet = palletScanItem.getPalletCount();
                    tv_gin_route_id.setText(String.valueOf(palletScanItem.getRouteId()));
                    String get_shop_names = objGinScanTransferControl.LoadShops(palletScanItem.getRouteId());
                    GinScanTransferShared.saveShopnames(get_shop_names);
                    tv_shopnames_col.setText(get_shop_names);


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                tvscancount.setText(getPalletCount() + " / " + Integer.toString(countpallet));

                //ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1)
            }

        }
        return countpallet;
    }


    public boolean GetScanresultKSA() {
        transferno = et_shop_transferno.getText().toString();
        palletno = et_g_pallet.getText().toString();
        Log.e("transferno", transferno);
        if (et_g_pallet.getText().toString().isEmpty()) {
            vibrate(500, getContext());
            AlertDialog(getContext(), "Please Scan Pallet no");
            et_shop_transferno.setText("");
            return false;
        }

        if (et_shop_transferno.getText().toString().isEmpty() && transferno != "") {
            vibrate(500, getContext());
            AlertDialog(getContext(), "Please Scan Trf no/Toteid");
            return false;
        } else {
            if (!tv_gin_route_id.getText().toString().equals("")) {
                try {
                    GinScanItemStatus = objGinScanTransferControl.ScanTransfer(getActivity(), transferno, Integer.parseInt(tv_gin_route_id.getText().toString()), android_id, palletno);
                    et_shop_transferno.requestFocus();
                    objTransferStatusAdp = new MyTransferStatusAdp(GinScanItemStatus);
                    lv_div_seperate_details.setAdapter(objTransferStatusAdp);
                    if (!et_g_pallet.getText().equals("")) {
                        GinScanTransferShared.saveRouteid(tv_gin_route_id.getText().toString());
                        sp_gin_route_id.setEnabled(false);
                        sp_gin_route_id.setClickable(false);
                        et_shop_transferno.requestFocus();
                    }

                    et_shop_transferno.setFocusable(true);
                    count = getCount();
                    palletcount = (getPalletCount());
                    tv_count.setText(count + "");
                    // tvscancount.setText(palletcount + "");
                    tvscancount.setText(palletcount + "/" + GinScanTransferShared.loadPalletCount() + "");
                    if (palletcount == Integer.parseInt(GinScanTransferShared.loadPalletCount())) {
                        vibrate(500, getContext());
                        okMessage("Message", "All Transfers Scanned for palletno -" + palletno + ". Please Scan another Pallet for Building GIN", getContext());
                        et_g_pallet.setText("");
                        tvscancount.setText("0/0");
                        et_g_pallet.setEnabled(true);
                        GinScanTransferShared.savePalletno("");
                        palletcount = 0;
                        GinScanTransferShared.savePalletCount("0");
                        setPalletCount(0);
                        et_g_pallet.requestFocus();
                        et_g_pallet.setFocusable(true);

                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }

                et_shop_transferno.setText("");
//                        sp_gin_shopname.setAdapter(null);
//                    }
//                }
//            else {
//                    AlertDialog(getContext(), "Please Select Route");
//                    return false;
//                }
                // Log.e("getCount spinner", SpinnerCount + "");
            } else {
                vibrate(500, getContext());
                okMessage("MESSAGE", "PLEASE Select Route first", getContext());
            }
            // sp_gin_shopname.setAdapter(null);
            et_shop_transferno.requestFocus();
            et_shop_transferno.setFocusable(true);
            return false;
        }


    }

    public boolean GetScanresult() {
        transferno = et_shop_transferno.getText().toString();
        palletno = et_g_pallet.getText().toString();
        Log.e("transferno", transferno);
        if (et_g_pallet.getText().toString().isEmpty()) {
            vibrate(500, getContext());
            AlertDialog(getContext(), "Please Scan Pallet no");
            et_shop_transferno.setText("");
            return false;
        }

        if (et_shop_transferno.getText().toString().isEmpty() && transferno != "") {
            vibrate(500, getContext());
            AlertDialog(getContext(), "Please Scan Trf no/Toteid");
            return false;
        } else {
            if (sp_gin_route_id.getSelectedItemId() != 0) {
                try {
                    GinScanItemStatus = objGinScanTransferControl.ScanTransfer(getActivity(), transferno, get_route_id, android_id, palletno);
                    et_shop_transferno.requestFocus();
                    objTransferStatusAdp = new MyTransferStatusAdp(GinScanItemStatus);
                    lv_div_seperate_details.setAdapter(objTransferStatusAdp);
                    if (!et_g_pallet.getText().equals("")) {
                        GinScanTransferShared.saveRouteid(sp_gin_route_id.getSelectedItem().toString());
                        sp_gin_route_id.setEnabled(false);
                        sp_gin_route_id.setClickable(false);
                        et_shop_transferno.requestFocus();
                    }

                    et_shop_transferno.setFocusable(true);
                    count = getCount();
                    palletcount = (getPalletCount());
                    tv_count.setText(count + "");
                    // tvscancount.setText(palletcount + "");
                    tvscancount.setText(palletcount + "/" + GinScanTransferShared.loadPalletCount() + "");
                    if (palletcount == Integer.parseInt(GinScanTransferShared.loadPalletCount())) {
                        vibrate(500, getContext());
                        okMessage("Message", "All Transfers Scanned for palletno -" + palletno + ". Please Scan another Pallet for Building GIN", getContext());
                        et_g_pallet.setText("");
                        tvscancount.setText("0/0");
                        et_g_pallet.setEnabled(true);
                        GinScanTransferShared.savePalletno("");
                        palletcount = 0;
                        GinScanTransferShared.savePalletCount("0");
                        setPalletCount(0);
                        et_g_pallet.requestFocus();
                        et_g_pallet.setFocusable(true);

                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }

                et_shop_transferno.setText("");
//                        sp_gin_shopname.setAdapter(null);
//                    }
//                }
//            else {
//                    AlertDialog(getContext(), "Please Select Route");
//                    return false;
//                }
                // Log.e("getCount spinner", SpinnerCount + "");
            } else {
                vibrate(500, getContext());
                okMessage("MESSAGE", "PLEASE Select Route first", getContext());
            }
            // sp_gin_shopname.setAdapter(null);
            et_shop_transferno.requestFocus();
            et_shop_transferno.setFocusable(true);
            return false;
        }


    }


    private class MyTransferStatusAdp extends BaseAdapter {
        public ArrayList<GinScanItem> GinScanItemsList;

        public MyTransferStatusAdp(ArrayList<GinScanItem> GinScanItems) {
            this.GinScanItemsList = GinScanItems;
        }

        @Override
        public int getCount() {
            return GinScanItemsList.size();
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
            View myView = mInflater.inflate(R.layout.gin_scan_transfer_details, null);
            final GinScanItem s = GinScanItemsList.get(position);
            TextView tv_transfer_no_details = (TextView) myView.findViewById(R.id.tv_transfer_no_details);
            tv_transfer_no_details.setText(String.valueOf(s.TransferNo));
            TextView tv_toteid_details = (TextView) myView.findViewById(R.id.tv_toteid_details);
            tv_toteid_details.setText(String.valueOf(s.Toteid));
            TextView tv_shopname_details = (TextView) myView.findViewById(R.id.tv_shopname_details);
            tv_shopname_details.setText(String.valueOf(s.ShopName));
            TextView tv_gin_qty = (TextView) myView.findViewById(R.id.tv_gin_qty);
            tv_gin_qty.setText(String.valueOf(s.Qty));
            return myView;
        }
    }

    public void AlertDialog(Context context, String message) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void clear() {
        sp_gin_route_id.clearFocus();
        if (objGlobal.getCountryCode().equals("KSA"))
            tv_gin_route_id.setText("");
        et_shop_transferno.setText("");
        et_car_no.setText("");
        Remarks.setText("");
        driver_name.setText("");
        et_Ship_no.setText("");
        //sp_gin_shopname.setAdapter(null);
        sp_gin_route_id.setSelection(0);
        transferno = "";
        tv_shopnames_col.setText("");
        sp_gin_route_id.setEnabled(true);
        sp_gin_route_id.setClickable(true);
        GinScanTransferShared.saveRouteid("");
        count = 0;
        et_g_pallet.setText("");
        tvscancount.setText("");
        et_g_pallet.setEnabled(true);
        GinScanTransferShared.savePalletno("");
        GinScanTransferShared.savePalletCount("0");
        palletcount = 0;
        setPalletCount(palletcount);
        setCount(count);
        tv_count.setText("");
        if (objGinScanTransferControl.deletetemp()) {
            // Clear collection..
            lv_div_seperate_details.setAdapter(null);


        }
        if (!objGlobal.getCountryCode().equals("KSA"))
        {
            String get_shop_names = objGinScanTransferControl.LoadShops(get_route_id);
            tv_shopnames_col.setText(get_shop_names);
        }

        et_g_pallet.setFocusable(true);

    }

    private void clearPallet() {

        objGinScanTransferControl.deletePallettemp(palletno);
        et_g_pallet.setText("");
        tvscancount.setText("0/0");
        count = count - palletcount;
        setCount(count);
        palletcount = 0;
        et_g_pallet.setEnabled(true);
        setPalletCount(0);
        GinScanTransferShared.savePalletno("");
        GinScanTransferShared.savePalletCount("0");

    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();

    }

    void vibrate(int duration, Context context) {
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        Uri notification = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.errorsound2);
//        Ringtone r = RingtoneManager.getRingtone(context, notification);
//        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
//        r.play();
//
//        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        assert v != null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(duration,
//                    VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            v.vibrate(duration);
//        }


    }


//    @Override
//    public void onPause() {
//        clear();
//        super.onPause();
//
//    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        clear();
//
//
//    }
}