package com.bflgroup.warehouse.ui.chutestatusinout.jafza;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.transfer.TransferControl;
import com.bflgroup.warehouse.ui.transfer.TransferFragment;
import com.bflgroup.warehouse.ui.transfer.TransferGlobal;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ChuteCheckInCheckOutJafzaFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private InOutJafzaGlobal objInOutJafzaGlobal = InOutJafzaGlobal.getInstance();
    private ChuteCheckInCheckOutJafzaControl objChuteCheckInCheckOutJafzaControl = new ChuteCheckInCheckOutJafzaControl();
    private TransferGlobal objTransferGlobal = TransferGlobal.getInstance();
    private TransferControl objTransferControl = new TransferControl();
    ChuteCheckInCheckOutJafzaFragment.MyChuteCheckInCheckOutTrfItemsAdp objMyChuteCheckInCheckOutTrfItemsAdp;

    ArrayList<ChuteCheckInCheckOutItemJafzaTicket> listChuteCheckInCheckOutItemTicket = new ArrayList<ChuteCheckInCheckOutItemJafzaTicket>();

    private EditText et_chute_status_inout_chuteid;
    private EditText et_chute_status_inout_totid;
    private TextView tv_chute_status_inout_status;
    private TextView tv_chute_status_inout_shopname;
    private TextView tv_chute_status_inout_shopid;
    private ListView lv_chute_status_inout_details;
    private TextView tv_chute_status_inout_trfno;
    private TextView tv_chute_status_inout_tot_qty;
    private Button bt_chute_status_inout_in;
    private Button bt_chute_status_inout_out;
    private Button bt_chute_status_inout_clear;
    private ProgressBar pr_chute_status_inout;
    private TextView tv_chute_checkinout_totid;
    private TextView tv_chute_checkinout_time;
    private TextView tv_chute_checkinout_shop_tote_type;
    private EditText et_transfer_popup_reprint_trfno;
    private Button bt_transfer_popup_reprint_fetch;
    private TextView tv_transfer_popup_reprint_shopname;
    private Button bt_transfer_popup_reprint_print;
    private Button bt_transfer_popup_reprint_close;
    private CheckBox ch_chute_status_inout_reprint_transfer;
    private Spinner sp_chute_status_inout_chuteid_printer;

    private boolean b_Result;
    private String s_Result;
    Boolean strflg = false;
    private ProgressDialog mWaitDialog;

    private BarcodePrinting objSample_Print;
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();
    private BluetoothPort bluetoothPort;
    private BroadcastReceiver connectDevice;
    private Thread btThread;
    private boolean testPrint = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver searchFinish;
    private Vector<BluetoothDevice> remoteDevices;
    private ArrayAdapter<String> adapter;
    private boolean searchflags;
    private static final int REQUEST_ENABLE_BT = 2;


    public ChuteCheckInCheckOutJafzaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chute_check_in_check_out_jafza, container, false);

        et_chute_status_inout_chuteid = (EditText) view.findViewById(R.id.et_chute_status_inout_chuteid);
        et_chute_status_inout_totid = (EditText) view.findViewById(R.id.et_chute_status_inout_totid);
        tv_chute_status_inout_status = (TextView) view.findViewById(R.id.tv_chute_status_inout_status);
        tv_chute_status_inout_shopname = (TextView) view.findViewById(R.id.tv_chute_status_inout_shopname);
        tv_chute_status_inout_shopid = (TextView) view.findViewById(R.id.tv_chute_status_inout_shopid);
        lv_chute_status_inout_details = (ListView) view.findViewById(R.id.lv_chute_status_inout_details);
        tv_chute_status_inout_trfno = (TextView) view.findViewById(R.id.tv_chute_status_inout_trfno);
        tv_chute_status_inout_tot_qty = (TextView) view.findViewById(R.id.tv_chute_status_inout_tot_qty);
        bt_chute_status_inout_in = (Button) view.findViewById(R.id.bt_chute_status_inout_in);
        bt_chute_status_inout_out = (Button) view.findViewById(R.id.bt_chute_status_inout_out);
        bt_chute_status_inout_clear = (Button) view.findViewById(R.id.bt_chute_status_inout_clear);
        pr_chute_status_inout = (ProgressBar) view.findViewById(R.id.pr_chute_status_inout);
        tv_chute_checkinout_totid = (TextView) view.findViewById(R.id.tv_chute_checkinout_totid);
        tv_chute_checkinout_time = (TextView) view.findViewById(R.id.tv_chute_checkinout_time);
        tv_chute_checkinout_shop_tote_type = (TextView) view.findViewById(R.id.tv_chute_checkinout_shop_tote_type);
        ch_chute_status_inout_reprint_transfer = (CheckBox) view.findViewById(R.id.ch_chute_status_inout_reprint_transfer);
        sp_chute_status_inout_chuteid_printer = (Spinner) view.findViewById(R.id.sp_chute_status_inout_chuteid_printer);

        clearAll();
        et_chute_status_inout_chuteid.requestFocus();

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_chute_status_inout_chuteid_printer.setAdapter(arrayAdpYellow);
        }

        et_chute_status_inout_chuteid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_chute_status_inout_totid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_chute_status_inout_chuteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_chute_status_inout_chuteid.setText(et_chute_status_inout_chuteid.getText().toString().toUpperCase());
                    //progressVisivle(true);
                    et_chute_status_inout_totid.setText("");
                    tv_chute_checkinout_totid.setText("");
                    b_Result = validateChuteId(et_chute_status_inout_chuteid.getText().toString());
                    if (b_Result) {
                        b_Result = loadItemsForPackingList(et_chute_status_inout_chuteid.getText().toString(), tv_chute_status_inout_shopid.getText().toString());
                        if (!b_Result) {
                            okMessage("Chute Status", objGlobal.getErrorMessage());
                            //et_chute_status_inout_chuteid.setFocusable(true);
                            //progressVisivle(false);
                            et_chute_status_inout_chuteid.setFocusable(true);
                            strflg = true;
                            return true;
                        } else {
                            et_chute_status_inout_totid.setFocusable(true);
                            //et_chute_status_inout_totid.requestFocus();
                            //progressVisivle(false);
                            //strflg=true;
                            return true;
                            //et_chute_status_inout_totid.requestFocus();
                        }
                    } else {
                        //progressVisivle(false);
                        clearAll();
                        et_chute_status_inout_chuteid.getText().clear();
                        et_chute_status_inout_chuteid.requestFocus();
                        et_chute_status_inout_chuteid.setFocusable(true);
                        strflg = true;
                        return true;
                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_chute_status_inout_chuteid.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }
        });

        et_chute_status_inout_chuteid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    et_chute_status_inout_chuteid.setText(et_chute_status_inout_chuteid.getText().toString().toUpperCase());
                }
            }
        });

        et_chute_status_inout_totid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(et_chute_status_inout_totid.getText())) {
                        if (et_chute_status_inout_totid.getText().length() > 8)
                            et_chute_status_inout_totid.setText(et_chute_status_inout_totid.getText().toString().substring(0, 8));
                    }
                    b_Result = validateToteId(et_chute_status_inout_totid.getText().toString(), tv_chute_checkinout_shop_tote_type.getText().toString());
                    if (!b_Result) {
                        et_chute_status_inout_totid.getText().clear();
                    }
                    et_chute_status_inout_totid.setFocusable(true);
                    return true;
                } else {
                    et_chute_status_inout_totid.setFocusable(true);
                    return false;
                }
            }
        });

        et_chute_status_inout_totid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //et_chute_status_inout_totid.setText(et_chute_status_inout_totid.getText().toString().substring(0, 7));
                }
            }
        });

        bt_chute_status_inout_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
                et_chute_status_inout_chuteid.requestFocus();
            }
        });

        bt_chute_status_inout_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chuteId = et_chute_status_inout_chuteid.getText().toString();
                String totId = et_chute_status_inout_totid.getText().toString();
                String shopName = tv_chute_status_inout_shopname.getText().toString();
                String status = tv_chute_status_inout_status.getText().toString();
                String shopId = tv_chute_status_inout_shopid.getText().toString();
                String shopToteType = tv_chute_checkinout_shop_tote_type.getText().toString();
                b_Result = objChuteCheckInCheckOutJafzaControl.validateCheckInOut("IN", chuteId, totId, shopId, shopName, status, shopToteType);
                if (!b_Result) {
                    closeWaitDialog();
                    okMessage("Check In", objGlobal.getErrorMessage());
                    bt_chute_status_inout_in.requestFocus();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //saveCheckInOld(chuteId, totId, shopId, shopName);
                                    saveCheckInNew(chuteId, totId, shopId, shopName);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closeWaitDialog();
                                    et_chute_status_inout_chuteid.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });

        bt_chute_status_inout_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chuteId = et_chute_status_inout_chuteid.getText().toString();
                String totId = et_chute_status_inout_totid.getText().toString();
                String shopName = tv_chute_status_inout_shopname.getText().toString();
                String status = tv_chute_status_inout_status.getText().toString();
                String shopId = tv_chute_status_inout_shopid.getText().toString();
                String shopToteType = tv_chute_checkinout_shop_tote_type.getText().toString();
                b_Result = objChuteCheckInCheckOutJafzaControl.validateCheckInOut("OUT", chuteId, totId, shopId, shopName, status, shopToteType);
                if (!b_Result) {
                    closeWaitDialog();
                    okMessage("Check In", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //saveCheckOutOld(chuteId, totId, shopId, shopName);
                                    saveCheckOutNew(chuteId, totId, shopId, shopName);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closeWaitDialog();
                                    et_chute_status_inout_chuteid.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });

        ch_chute_status_inout_reprint_transfer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupInvoiceReprint();
                } else {
                    // not checked
                }
            }
        });

        searchflags = false;
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();
        return view;
    }

    private void saveCheckInNew(String chuteId, String totId, String shopId, String shopName) {
        try {
            mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
            mWaitDialog.setCancelable(false);
            final AsyncHttpClient client = new AsyncHttpClient();
            JSONObject json = new JSONObject();
            json.put("chuteId", chuteId);
            json.put("status", true);
            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
            client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                if (statusCode == 200) {
                                    JSONObject jso = new JSONObject(new String(responseBody));
                                    boolean status = jso.getBoolean("status");
                                    String msg = jso.getString("message");
                                    if (status) {
                                        try {
                                            if (objChuteCheckInCheckOutJafzaControl.saveChuteIn(chuteId, totId, shopId, shopName, "0")) {
                                                clearAll();
                                                closeWaitDialog();
                                                et_chute_status_inout_chuteid.requestFocus();
                                            } else {
                                                vibrate(500);
                                                closeWaitDialog();
                                                okMessage("Chute Status IN", objGlobal.getErrorMessage());
                                                et_chute_status_inout_chuteid.requestFocus();
                                            }
                                            et_chute_status_inout_chuteid.requestFocus();
                                        } catch (Exception e) {
                                            clearAll();
                                            vibrate(500);
                                            closeWaitDialog();
                                            okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:try: " + e);
                                            et_chute_status_inout_chuteid.requestFocus();
                                        }
                                    } else {
                                        vibrate(500);
                                        closeWaitDialog();
                                        okMessage("Chute status (1)", msg);
                                        et_chute_status_inout_chuteid.requestFocus();
                                    }
                                }
                            } catch (Exception e) {
                                vibrate(500);
                                closeWaitDialog();
                                okMessage("Chute status (2)", e.toString());
                                et_chute_status_inout_chuteid.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            clearAll();
                            closeWaitDialog();
                            okMessage("Chute status (3)", error.toString());
                            et_chute_status_inout_chuteid.requestFocus();
                        }
                    });
        } catch (Exception e) {
            clearAll();
            closeWaitDialog();
            okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:onFailure-2: " + e);
            et_chute_status_inout_chuteid.requestFocus();
        }
    }

    private void saveCheckOutNew(String chuteId, String totId, String shopId, String shopName) {
        try {
            mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
            mWaitDialog.setCancelable(false);
            final AsyncHttpClient client = new AsyncHttpClient();
            JSONObject json = new JSONObject();
            json.put("chuteId", et_chute_status_inout_chuteid.getText().toString());
            json.put("status", false);
            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
            client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                if (statusCode == 200) {
                                    JSONObject jso = new JSONObject(new String(responseBody));
                                    boolean status = jso.getBoolean("status");
                                    String msg = jso.getString("message");
                                    if (status) {
                                        if (sortTask(chuteId, totId, shopId, shopName)) {
                                            closeWaitDialog();
                                            clearAll();
                                            et_chute_status_inout_chuteid.requestFocus();
                                        } else {
                                            vibrate(500);
                                            closeWaitDialog();
                                            okMessage("Chute Status OUT", objGlobal.getErrorMessage());
                                        }
                                        et_chute_status_inout_chuteid.requestFocus();
                                    } else {
                                        vibrate(500);
                                        closeWaitDialog();
                                        okMessage("Chute status (1)", msg);
                                    }
                                }
                            } catch (Exception e) {
                                clearAll();
                                vibrate(500);
                                closeWaitDialog();
                                okMessage("Chute Status OUT", "bt_chute_status_inout_in.setOnClickListener:try: " + e);
                                et_chute_status_inout_chuteid.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            okMessage("Chute status", "saveCheckOutOld:onFailure: " + error.toString());
                            closeWaitDialog();
                            et_chute_status_inout_chuteid.requestFocus();
                        }
                    });
        } catch (Exception e) {
            okMessage("Chute status", "validateChuteId:Exception: " + e);
            closeWaitDialog();
            et_chute_status_inout_chuteid.requestFocus();
        }
    }

    private void openPopupInvoiceReprint() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_transfer_reprint);

        tv_transfer_popup_reprint_shopname = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_reprint_shopname);
        et_transfer_popup_reprint_trfno = (EditText) myDialog.findViewById(R.id.et_transfer_popup_reprint_trfno);
        bt_transfer_popup_reprint_fetch = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_fetch);
        bt_transfer_popup_reprint_print = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_print);
        bt_transfer_popup_reprint_close = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_close);
        tv_transfer_popup_reprint_shopname.requestFocus();
        et_transfer_popup_reprint_trfno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String scan = et_transfer_popup_reprint_trfno.getText().toString().toUpperCase();
                    if (scan.isEmpty()) {
                        okMessage("Transfer", "Please Enter Toteid / Transfer number");
                        et_transfer_popup_reprint_trfno.requestFocus();
                        vibrate(100);
                    } else {
                        tv_transfer_popup_reprint_shopname.setText(objTransferControl.reprintTransferShopName(scan));
                    }
                }
                return false;
            }
        });
        bt_transfer_popup_reprint_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reprintTransfer()) {
                    ch_chute_status_inout_reprint_transfer.setChecked(false);
                    myDialog.dismiss();
                }
            }
        });
        bt_transfer_popup_reprint_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scan = et_transfer_popup_reprint_trfno.getText().toString().toUpperCase();
                if (scan.isEmpty()) {
                    okMessage("Transfer", "Please Enter Toteid / Transfer number");
                    et_transfer_popup_reprint_trfno.requestFocus();
                    vibrate(100);
                }
                tv_transfer_popup_reprint_shopname.setText(objTransferControl.reprintTransferShopName(scan));
            }
        });
        bt_transfer_popup_reprint_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch_chute_status_inout_reprint_transfer.setChecked(false);
                myDialog.dismiss();
            }
        });

        tv_transfer_popup_reprint_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                ArrayList<String> arraylist;
                arraylist = objChuteCheckInCheckOutJafzaControl.loadShops();
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.searchable_shopname);
                dialog.getWindow().setLayout(600, 1500);
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
                        tv_transfer_popup_reprint_shopname.setText(adapter.getItem(position));
                        dialog.dismiss();
                    }
                });

            }
        });
        myDialog.show();
    }

    private boolean sortTask(String chuteId, String totId, String shopId, String shopName) {
        b_Result = objChuteCheckInCheckOutJafzaControl.saveChuteOut(chuteId, totId, shopId, shopName); //transfer receipt
        if (!b_Result) {
            okMessage("Chute Status IN", "ERR NO: " + objGlobal.getErrorNo() + ", " + objGlobal.getErrorMessage());
            return false;
        }
        tv_chute_status_inout_trfno.setText(chuteId + "  ;  " + totId + "  ;  " + objInOutJafzaGlobal.getTrfRecNo() + "  ;  " + String.valueOf(objInOutJafzaGlobal.getTrfTotQty()));
        tv_chute_status_inout_tot_qty.setText(String.valueOf(objInOutJafzaGlobal.getTrfTotQty()));
        b_Result = objTransferControl.forPrint(shopName, objInOutJafzaGlobal.getTrfRecNo());
        if (!b_Result) {
            okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
            return false;
        }
        if (!printSticker(sp_chute_status_inout_chuteid_printer.getSelectedItem().toString())) {
            okMessage("Transfer", "Printer Error, Pleasse reprint..");
            return false;
        }
        return true;
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    boolean validateToteId(String toteId, String shopToteType) {
        if (TextUtils.isEmpty(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Please enter Tote Id, ");
            return false;
        }
        if (!objChuteCheckInCheckOutJafzaControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objChuteCheckInCheckOutJafzaControl.checkValidToteId(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Invalid Tote ID, " + toteId);
            return false;
        }
        String[] strTotetype = shopToteType.split(",");
        boolean blnTotetype=false;
        for (String part : strTotetype) {
            if (toteId.substring(0, part.length()).equals(part)) {
                blnTotetype=true;
                break;
            }
        }
        if (!blnTotetype) {
            vibrate(300);
            showMessage("Chute Status", "Tote type is not matching, Tote: " + toteId + ", Shop Tote Type is: " + shopToteType);
            return false;
        }
        return true;
    }

    boolean validateChuteId(String chuteId) {
        if (TextUtils.isEmpty(chuteId)) {
            vibrate(300);
            showMessage("Chute Status", "Please enter Chute ID");
            return false;
        }
        if (!objChuteCheckInCheckOutJafzaControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objChuteCheckInCheckOutJafzaControl.checkValidChuteId(chuteId)) {
            vibrate(300);
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }

        s_Result = objChuteCheckInCheckOutJafzaControl.getShopIdFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_inout_shopid.setText(s_Result);

        s_Result = objChuteCheckInCheckOutJafzaControl.getShopnameFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_inout_shopname.setText(s_Result);

        s_Result = objChuteCheckInCheckOutJafzaControl.getShopToteidType(tv_chute_status_inout_shopname.getText().toString());
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Tote type is not updated, Shop name is : " + tv_chute_status_inout_shopname.getText().toString());
            return false;
        }
        tv_chute_checkinout_shop_tote_type.setText(s_Result);
        b_Result = objChuteCheckInCheckOutJafzaControl.validForCheckBuildOrExport(tv_chute_status_inout_shopname.getText().toString());
        if (!b_Result) {
            vibrate(300);
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        s_Result = objChuteCheckInCheckOutJafzaControl.getToteIdFromChuteId(chuteId);
        if (!TextUtils.isEmpty(s_Result)) {
            //et_chute_status_inout_totid.setText(s_Result);
            tv_chute_checkinout_totid.setText(s_Result);
        }

        s_Result = objChuteCheckInCheckOutJafzaControl.getChuteStatus(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Chute Status Not valid, " + chuteId);
            return false;
        }

        tv_chute_status_inout_status.setText(s_Result);
        if (!objChuteCheckInCheckOutJafzaControl.getLastChuteInOut(chuteId)) {
            vibrate(300);
            showMessage("getLastChuteInOut", objGlobal.getErrorMessage());
            return false;
        }
        tv_chute_checkinout_time.setText(objInOutJafzaGlobal.getChuteLastInOut());
        return true;
    }

    private boolean loadItemsForPackingList(String chuteId, String shopId) {
        try {
            listChuteCheckInCheckOutItemTicket.clear();
            listChuteCheckInCheckOutItemTicket = objChuteCheckInCheckOutJafzaControl.itemsForPL(chuteId, shopId);
            objMyChuteCheckInCheckOutTrfItemsAdp = new ChuteCheckInCheckOutJafzaFragment.MyChuteCheckInCheckOutTrfItemsAdp(listChuteCheckInCheckOutItemTicket);
            lv_chute_status_inout_details.setAdapter(objMyChuteCheckInCheckOutTrfItemsAdp);
            tv_chute_status_inout_tot_qty.setText(String.valueOf(objInOutJafzaGlobal.getTrfTotQty()));
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsForPackingList:try: " + e.toString());
            return false;
        }
    }

    private boolean reprintTransfer() {
        String scan = et_transfer_popup_reprint_trfno.getText().toString().toUpperCase();
        String shopname = tv_transfer_popup_reprint_shopname.getText().toString().toUpperCase();
        if (shopname.isEmpty()) {
            okMessage("Chute Status", "Please select Shopname");
            tv_transfer_popup_reprint_shopname.requestFocus();
            vibrate(100);
            return false;
        }
        if (scan.isEmpty()) {
            okMessage("Chute Status", "Please Enter Toteid / Transfer number");
            et_transfer_popup_reprint_trfno.requestFocus();
            vibrate(100);
            return false;
        }
        b_Result = objTransferControl.forPrint(shopname, scan);
        if (!b_Result) {
            okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
            return false;
        }
        if (!printSticker(sp_chute_status_inout_chuteid_printer.getSelectedItem().toString())) {
            okMessage("Transfer", "Printer Error, Pleasse reprint..");
            return false;
        }
        et_transfer_popup_reprint_trfno.setText("");
        et_transfer_popup_reprint_trfno.requestFocus();
        return true;
    }

    void clearAll() {
        et_chute_status_inout_chuteid.setText("");
        et_chute_status_inout_totid.setText("");
        tv_chute_checkinout_totid.setText("");
        tv_chute_checkinout_time.setText("");
        tv_chute_checkinout_shop_tote_type.setText("");
        tv_chute_status_inout_status.setText("");
        tv_chute_status_inout_shopname.setText("");
        tv_chute_status_inout_shopid.setText("");
        listChuteCheckInCheckOutItemTicket.clear();
        objMyChuteCheckInCheckOutTrfItemsAdp = new ChuteCheckInCheckOutJafzaFragment.MyChuteCheckInCheckOutTrfItemsAdp(listChuteCheckInCheckOutItemTicket);
        lv_chute_status_inout_details.setAdapter(objMyChuteCheckInCheckOutTrfItemsAdp);
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

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private class MyChuteCheckInCheckOutTrfItemsAdp extends BaseAdapter {
        public ArrayList<ChuteCheckInCheckOutItemJafzaTicket> listChuteCheckInCheckOutItemTicket;

        public MyChuteCheckInCheckOutTrfItemsAdp(ArrayList<ChuteCheckInCheckOutItemJafzaTicket> listChuteCheckInCheckOutItemTicket) {
            this.listChuteCheckInCheckOutItemTicket = listChuteCheckInCheckOutItemTicket;
        }

        @Override
        public int getCount() {
            return listChuteCheckInCheckOutItemTicket.size();
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
            View myView = mInflater.inflate(R.layout.chute_check_in_check_out_item_ticket, null);
            final ChuteCheckInCheckOutItemJafzaTicket s = listChuteCheckInCheckOutItemTicket.get(position);
            TextView tv_check_in_check_out_tickte_itemcode = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_itemcode);
            tv_check_in_check_out_tickte_itemcode.setText(String.valueOf(s.itemCode));
            TextView tv_check_in_check_out_tickte_description = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_description);
            tv_check_in_check_out_tickte_description.setText(String.valueOf(s.description));
            TextView tv_check_in_check_out_tickte_qty = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_qty);
            tv_check_in_check_out_tickte_qty.setText(String.valueOf(s.quantity));
            return myView;
        }
    }


    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();
        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //"BlueTooth Connect"
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        okMessage("IOException", e.toString());
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException", e.toString());
                    }
                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice != null) {
                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        getActivity().registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void bluetoothSetup() {
        clearBtDevData();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new ChuteCheckInCheckOutJafzaFragment.connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);
                str_temp = params[0].getAddress();
                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result.intValue() == 0) {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                printBarCode();
            } else {
                alert
                        .setTitle("Error 4")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    private boolean printSticker(String device) {
        if (!bluetoothPort.isConnected()) {
            try {
                btConn(mBluetoothAdapter.getRemoteDevice(device));
            } catch (Exception e) {
                okMessage("Error 2", e.toString());
                return false;
            }
        }
        printBarCode();
        return true;
    }

    private boolean printBarCode() {
        try {
            //testPrint=true;
            byte[] printData = null;
            if (testPrint) {
                printData = objSample_Print.getLabelWasNowHoneyWellTestPrint();
            } else {
                printData = objSample_Print.getTransferPrint(
                        objTransferGlobal.getPshopname(), objTransferGlobal.getPtrfno(), objTransferGlobal.getPboxno(),
                        objTransferGlobal.getPqty(), objTransferGlobal.getPdeldate(), objTransferGlobal.getPtrfdate(),
                        objTransferGlobal.getPtoteid(), objTransferGlobal.getPremarks(), objTransferGlobal.getPpreparedby());
            }
            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString());
            return false;
        }
    }

}