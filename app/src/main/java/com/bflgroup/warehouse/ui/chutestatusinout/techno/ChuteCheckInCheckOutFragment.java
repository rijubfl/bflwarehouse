package com.bflgroup.warehouse.ui.chutestatusinout.techno;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ChuteCheckInCheckOutFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private InOutGlobal objInOutGlobal = InOutGlobal.getInstance();
    private ChuteCheckInCheckOutControl objChuteCheckInCheckOutControl = new ChuteCheckInCheckOutControl();
    MyChuteCheckInCheckOutTrfItemsAdp objMyChuteCheckInCheckOutTrfItemsAdp;

    ArrayList<ChuteCheckInCheckOutItemTicket> listChuteCheckInCheckOutItemTicket = new ArrayList<ChuteCheckInCheckOutItemTicket>();

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
    private TextView tv_chute_checkinout_totid;
    private TextView tv_chute_checkinout_time;
    private TextView tv_chute_checkinout_shop_tote_type;

    private boolean b_Result;
    private String s_Result;
    Boolean strflg = false;

    private ProgressDialog mWaitDialog;

    public ChuteCheckInCheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chute_check_in_check_out, container, false);

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
        tv_chute_checkinout_totid = (TextView) view.findViewById(R.id.tv_chute_checkinout_totid);
        tv_chute_checkinout_time = (TextView) view.findViewById(R.id.tv_chute_checkinout_time);
        tv_chute_checkinout_shop_tote_type = (TextView) view.findViewById(R.id.tv_chute_checkinout_shop_tote_type);

        clearAll();
        et_chute_status_inout_chuteid.requestFocus();
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
                    et_chute_status_inout_totid.setText("");
                    tv_chute_checkinout_totid.setText("");
                    b_Result = validateChuteId(et_chute_status_inout_chuteid.getText().toString());
                    if (b_Result) {
                        b_Result = loadItemsForPackingList(et_chute_status_inout_chuteid.getText().toString(), tv_chute_status_inout_shopid.getText().toString());
                        if (!b_Result) {
                            okMessage("Chute Status", objGlobal.getErrorMessage());
                            //et_chute_status_inout_chuteid.setFocusable(true);
                            et_chute_status_inout_chuteid.setFocusable(true);
                            strflg = true;
                            return true;
                        } else {
                            et_chute_status_inout_totid.setFocusable(true);
                            //et_chute_status_inout_totid.requestFocus();
                            //strflg=true;
                            return true;
                            //et_chute_status_inout_totid.requestFocus();
                        }
                    } else {
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
                b_Result = objChuteCheckInCheckOutControl.validateCheckInOut("IN", chuteId, totId, shopId, shopName, status, shopToteType);
                if (!b_Result) {
                    okMessage("Check In", objGlobal.getErrorMessage());
                    bt_chute_status_inout_in.requestFocus();
                } else {
                    try {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are You sure to save?")
                                .setTitle("Conformation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                                            mWaitDialog.setCancelable(false);
                                            final AsyncHttpClient client = new AsyncHttpClient();
                                            JSONObject json = new JSONObject();
                                            json.put("ChuteId", chuteId);
                                            if (objGlobal.getWorkLocation().equals("UAE"))
                                                json.put("Status", "0");
                                            else
                                                json.put("status", true);
                                            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                                            entity.setContentType("application/json");
                                            if (!objGlobal.getRoboChuteStatusAPIToken().isEmpty()) client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
                                            client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                                                    new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            try {
                                                                if (objChuteCheckInCheckOutControl.saveChuteIn(chuteId, totId, shopId, shopName, "0")) {
                                                                    clearAll();
                                                                } else {
                                                                    closeWaitDialog();
                                                                    vibrate(500);
                                                                    okMessage("Chute Status IN", objGlobal.getErrorMessage());
                                                                }
                                                                et_chute_status_inout_chuteid.requestFocus();
                                                            } catch (Exception e) {
                                                                clearAll();
                                                                vibrate(500);
                                                                okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:try: " + e.toString());
                                                                et_chute_status_inout_chuteid.requestFocus();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            clearAll();
                                                            okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:onFailure: " + error.toString());
                                                            et_chute_status_inout_chuteid.requestFocus();
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            clearAll();
                                            okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:onFailure: " + e.toString());
                                            et_chute_status_inout_chuteid.requestFocus();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        et_chute_status_inout_chuteid.requestFocus();
                                    }
                                })
                                .show();
                    } catch (Exception e) {
                        okMessage("Chute status(updateChuteStatusApiNew)", "bt_chute_status_inout_out.setOnClickListener:onFailure-2: " + e);
                    }
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
                b_Result = objChuteCheckInCheckOutControl.validateCheckInOut("OUT", chuteId, totId, shopId, shopName, status, shopToteType);
                if (!b_Result) {
                    okMessage("Check In", objGlobal.getErrorMessage());
                } else {
                    try {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are You sure to save?")
                                .setTitle("Conformation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                                            mWaitDialog.setCancelable(false);
                                            final AsyncHttpClient client = new AsyncHttpClient();
                                            JSONObject json = new JSONObject();
                                            json.put("ChuteId", et_chute_status_inout_chuteid.getText().toString());
                                            if (objGlobal.getWorkLocation().equals("UAE"))
                                                json.put("Status", "2");
                                            else
                                                json.put("status", false);
                                            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                                            entity.setContentType("application/json");
                                            if (!objGlobal.getRoboChuteStatusAPIToken().isEmpty()) client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
                                            client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                                                    new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            labelInfo(shopId, totId, objInOutGlobal.getLabelInfo(), chuteId, shopName, chuteId);
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            closeWaitDialog();
                                                            okMessage("Chute status", "validateChuteId:onFailure: " + error.toString());
                                                            et_chute_status_inout_chuteid.requestFocus();
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            closeWaitDialog();
                                            okMessage("Chute status", "validateChuteId:Exception: " + e.toString());
                                            et_chute_status_inout_chuteid.requestFocus();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        et_chute_status_inout_chuteid.requestFocus();
                                    }
                                })
                                .show();
                    } catch (Exception e) {
                        okMessage("Chute status(updateChuteStatusApiNew)", "bt_chute_status_inout_out.setOnClickListener:onFailure-2: " + e);
                    }
                }
            }
        });
        return view;
    }

    private void sortTask(String totId, String shopId,String shopName,String chuteid) {
        if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Sort-Task", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Start", "")) {
            clearAll();
            vibrate(500);
            okMessage("Chute status", "sortTask:objChuteCheckInCheckOutControl.updateChuteApiLog:" + objGlobal.getErrorMessage());
        } else {
            try {
                final AsyncHttpClient client = new AsyncHttpClient();
                JSONObject json = new JSONObject();
                json.put("toteid", totId);
                json.put("chuteno", objInOutGlobal.getChuteNo());
                json.put("transferno", objInOutGlobal.getTrfRecNo());
                if (objGlobal.getWorkLocation().equals("KSA"))
                    json.put("messageId", objInOutGlobal.getTrfRecNo());
                json.put("batchCode", objInOutGlobal.getBatchCode());
                json.put("spare1", "");
                json.put("spare2", "");
                json.put("createtime", objGlobal.getServerDate());
                StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                objChuteCheckInCheckOutControl.updateChuteApiLog("Sort-Task", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                        objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Start-1",json.toString());
                client.post(getContext(), objGlobal.getRoboSortTaskAPI(), entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONArray jsonArray = new JSONArray(new String(responseBody));
                            if (jsonArray.length() > 0) {
                                JSONObject obj = jsonArray.getJSONObject(0);
                                if (obj.getString("Result").equals("1")) {
                                    if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Sort-Task", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                                            objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, String.valueOf(statusCode), "onSuccess-" + obj.getString("Result"))) {
                                        clearAll();
                                        vibrate(500);
                                        okMessage("Chute status", "sortTask:objChuteCheckInCheckOutControl.updateChuteApiLog:onSuccess:" + objGlobal.getErrorMessage());
                                    } else {
                                        if (!objChuteCheckInCheckOutControl.updateChuteApi("SortTaskApi", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo())) {
                                            clearAll();
                                            vibrate(500);
                                            okMessage("Chute status", "sortTask:objChuteCheckInCheckOutControl.updateChuteApi:onSuccess:" + objGlobal.getErrorMessage());
                                        } else {
                                            clearAll();
                                            et_chute_status_inout_chuteid.requestFocus();
                                        }
                                    }
                                } else {
                                    if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Sort-Task", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                                            objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, String.valueOf(statusCode), "onSuccess-" + obj.getString("Result"))) {
                                        clearAll();
                                        vibrate(500);
                                        okMessage("Chute status", "sortTask:objChuteCheckInCheckOutControl.updateChuteApiLog:onSuccess:" + objGlobal.getErrorMessage());
                                    } else {
                                        clearAll();
                                        okMessage("Chute Status", "sortTask:else " + obj.getString("errorMsg"));
                                        et_chute_status_inout_chuteid.requestFocus();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            clearAll();
                            okMessage("Chute Status", "sortTask:try: " + e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Sort-Task", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                                objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, String.valueOf(statusCode), error.getMessage())) {
                            okMessage("Chute status", "sortTask:objChuteCheckInCheckOutControl.updateChuteApiLog:onFailure:" + objGlobal.getErrorMessage());
                        } else {
                            okMessage("Chute status", "sortTask:onFailure: " + error.getMessage());
                        }
                        clearAll();
                        vibrate(500);
                    }
                });
            } catch (Exception e) {
                if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                        objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Exception", String.valueOf(e.getMessage()))) {
                    okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApiLog:Exception:" + objGlobal.getErrorMessage());
                } else {
                    okMessage("Chute status", "sortTask:Exception: " + e.getMessage());
                }
                clearAll();
                vibrate(500);
            }
        }
    }

    private void labelInfo(String shopId, String totId, String labelInfo,String chuteid,String shopName,String chuteId) {
        b_Result = objChuteCheckInCheckOutControl.saveChuteOut(chuteId, totId, shopId, shopName); //transfer receipt
        if (!b_Result) {
            clearAll();
            vibrate(500);
            okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.saveChuteOut:" + objGlobal.getErrorMessage());
        } else {
            tv_chute_status_inout_trfno.setText(chuteId + "  ;  " + totId + "  ;  " + objInOutGlobal.getTrfRecNo() + "  ;  " + objInOutGlobal.getTrfTotQty());
            tv_chute_status_inout_tot_qty.setText(String.valueOf(objInOutGlobal.getTrfTotQty()));
            if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                    objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Start", "")) {
                clearAll();
                vibrate(500);
                okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApiLog:" + objGlobal.getErrorMessage());
            } else {
                try {
                    final AsyncHttpClient client = new AsyncHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("toteid", totId);
                    if (objGlobal.getWorkLocation().equals("KSA"))
                        json.put("messageId", objInOutGlobal.getTrfRecNo());
                    json.put("labelInfo", labelInfo);
                    json.put("spare1", "");
                    json.put("spare2", "");
                    json.put("createtime", objGlobal.getServerDate());
                    StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                            objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Start-1",json.toString());
                    client.post(getContext(), objGlobal.getRoboLabelInfoAPI(), entity, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                                    objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, String.valueOf(statusCode), "onSuccess")) {
                                clearAll();
                                vibrate(500);
                                okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApiLog:onSuccess:" + objGlobal.getErrorMessage());
                            } else {
                                if (!objChuteCheckInCheckOutControl.updateChuteApi("LabelInfokApi", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo())) {
                                    clearAll();
                                    vibrate(500);
                                    okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApi:onSuccess:" + objGlobal.getErrorMessage());
                                } else {
                                    sortTask(totId, shopId, shopName, chuteid);
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                                    objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, String.valueOf(statusCode), error.getMessage())) {
                                okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApiLog:onFailure:" + objGlobal.getErrorMessage());
                            } else {
                                okMessage("Chute status", "labelInfo:onFailure: " + error.getMessage());
                            }
                            clearAll();
                            vibrate(500);
                        }
                    });
                } catch (Exception e) {
                    if (!objChuteCheckInCheckOutControl.updateChuteApiLog("Label-Info", shopId, objInOutGlobal.getTrfRecNo(), objInOutGlobal.getChuteNo(),
                            objInOutGlobal.getLabelInfo(), totId, shopName, chuteid, "Exception", String.valueOf(e.getMessage()))) {
                        okMessage("Chute status", "labelInfo:objChuteCheckInCheckOutControl.updateChuteApiLog:Exception:" + objGlobal.getErrorMessage());
                    } else {
                        okMessage("Chute status", "labelInfo:Exception: " + e.getMessage());
                    }
                    clearAll();
                    vibrate(500);
                }
            }
        }
    }

    boolean validateToteId(String toteId, String shopToteType) {
        if (TextUtils.isEmpty(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Please enter Tote Id, ");
            return false;
        }
        if (!objChuteCheckInCheckOutControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objChuteCheckInCheckOutControl.checkValidToteId(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Invalid Tote ID, " + toteId);
            return false;
        }
        if (!toteId.substring(0, 1).equals(shopToteType)) {
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
        if (!objChuteCheckInCheckOutControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objChuteCheckInCheckOutControl.checkValidChuteId(chuteId)) {
            vibrate(300);
            showMessage("Chute Status", "Invalid Chute ID, " + chuteId);
            return false;
        }

        s_Result = objChuteCheckInCheckOutControl.getShopIdFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_inout_shopid.setText(s_Result);

        s_Result = objChuteCheckInCheckOutControl.getShopnameFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_inout_shopname.setText(s_Result);

        s_Result = objChuteCheckInCheckOutControl.getShopToteidType(tv_chute_status_inout_shopname.getText().toString());
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Tote type is not updated, Shop name is : " + tv_chute_status_inout_shopname.getText().toString());
            return false;
        }
        tv_chute_checkinout_shop_tote_type.setText(s_Result.toString());

        s_Result = objChuteCheckInCheckOutControl.getToteIdFromChuteId(chuteId);
        if (!TextUtils.isEmpty(s_Result)) {
            //et_chute_status_inout_totid.setText(s_Result);
            tv_chute_checkinout_totid.setText(s_Result);
        }

        s_Result = objChuteCheckInCheckOutControl.getChuteStatus(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Chute Status Not valid, " + chuteId);
            return false;
        }

        tv_chute_status_inout_status.setText(s_Result);
        if (!objChuteCheckInCheckOutControl.getLastChuteInOut(chuteId)) {
            vibrate(300);
            showMessage("getLastChuteInOut", objGlobal.getErrorMessage());
            return false;
        }
        tv_chute_checkinout_time.setText(objInOutGlobal.getChuteLastInOut());
        return true;
    }

    private boolean loadItemsForPackingList(String chuteId, String shopId) {
        try {
            listChuteCheckInCheckOutItemTicket.clear();
            listChuteCheckInCheckOutItemTicket = objChuteCheckInCheckOutControl.itemsForPL(chuteId, shopId);
            objMyChuteCheckInCheckOutTrfItemsAdp = new MyChuteCheckInCheckOutTrfItemsAdp(listChuteCheckInCheckOutItemTicket);
            lv_chute_status_inout_details.setAdapter(objMyChuteCheckInCheckOutTrfItemsAdp);
            tv_chute_status_inout_tot_qty.setText(String.valueOf(objInOutGlobal.getTrfTotQty()));
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsForPackingList:try: " + e);
            return false;
        }
    }

    void clearAll() {
        closeWaitDialog();
        et_chute_status_inout_chuteid.setText("");
        et_chute_status_inout_totid.setText("");
        tv_chute_checkinout_totid.setText("");
        tv_chute_checkinout_time.setText("");
        tv_chute_checkinout_shop_tote_type.setText("");
        tv_chute_status_inout_status.setText("");
        tv_chute_status_inout_shopname.setText("");
        tv_chute_status_inout_shopid.setText("");
        listChuteCheckInCheckOutItemTicket.clear();
        objMyChuteCheckInCheckOutTrfItemsAdp = new MyChuteCheckInCheckOutTrfItemsAdp(listChuteCheckInCheckOutItemTicket);
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
        alert.setCancelable(false);
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
        public ArrayList<ChuteCheckInCheckOutItemTicket> listChuteCheckInCheckOutItemTicket;

        public MyChuteCheckInCheckOutTrfItemsAdp(ArrayList<ChuteCheckInCheckOutItemTicket> listChuteCheckInCheckOutItemTicket) {
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
            final ChuteCheckInCheckOutItemTicket s = listChuteCheckInCheckOutItemTicket.get(position);
            TextView tv_check_in_check_out_tickte_itemcode = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_itemcode);
            tv_check_in_check_out_tickte_itemcode.setText(String.valueOf(s.itemCode));
            TextView tv_check_in_check_out_tickte_description = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_description);
            tv_check_in_check_out_tickte_description.setText(String.valueOf(s.description));
            TextView tv_check_in_check_out_tickte_qty = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_qty);
            tv_check_in_check_out_tickte_qty.setText(String.valueOf(s.quantity));
            return myView;
        }
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }
}