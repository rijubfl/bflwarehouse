package com.bflgroup.warehouse.ui.chuteconfiguration.jafza;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ChuteConfigurationJafzaFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    Controls objControls = new Controls();
    private ChuteConfigurationJafzaGlobal objChuteConfigurationJafzaGlobal=ChuteConfigurationJafzaGlobal.getInstance();
    private ChuteConfigurationJafzaControl objChuteConfigurationJafzaControl = new ChuteConfigurationJafzaControl();

    MyChuteConfigurationJafzaHistoryTicketAdp objMyChuteConfigurationJafzaHistoryTicketAdp;

    private EditText et_chute_status_config_chuteid;
    private Spinner sp_chute_status_config_shopname;
    private Spinner sp_chute_status_config_status;
    private EditText et_chute_status_config_totid;
    private Button bt_chute_status_config_clear;
    private Button bt_chute_status_config_update;
    private ListView lv_chute_status_config_update;

    private boolean b_Result;
    private String s_Result;
    private ProgressDialog mWaitDialog;

    public ChuteConfigurationJafzaFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chute_configuration_jafza, container, false);

        et_chute_status_config_chuteid = (EditText) view.findViewById(R.id.et_chute_status_config_chuteid);
        sp_chute_status_config_shopname = (Spinner) view.findViewById(R.id.sp_chute_status_config_shopname);
        sp_chute_status_config_status = (Spinner) view.findViewById(R.id.sp_chute_status_config_status);
        et_chute_status_config_totid = (EditText) view.findViewById(R.id.et_chute_status_config_totid);
        bt_chute_status_config_clear = (Button) view.findViewById(R.id.bt_chute_status_config_clear);
        bt_chute_status_config_update = (Button) view.findViewById(R.id.bt_chute_status_config_update);
        lv_chute_status_config_update = (ListView) view.findViewById(R.id.lv_chute_status_config_update);

        List<String> arr = objChuteConfigurationJafzaControl.loadShops();
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_chute_status_config_shopname.setAdapter(arrayAdp);

        ArrayList<ChuteConfigurationJafzaHistoryTicket> listChuteConfigurationJafzaHistoryTicket = objChuteConfigurationJafzaControl.loadChuteConfigurationJafzaHistory();
        objMyChuteConfigurationJafzaHistoryTicketAdp = new ChuteConfigurationJafzaFragment.MyChuteConfigurationJafzaHistoryTicketAdp(listChuteConfigurationJafzaHistoryTicket);
        lv_chute_status_config_update.setAdapter(objMyChuteConfigurationJafzaHistoryTicketAdp);

        List<String> arr1;
        arr1 = new ArrayList<String>();
        arr1.add("Normal");
        arr1.add("Full");
        arr1.add("Disable");
        arr1.add("Unknown");
        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
        sp_chute_status_config_status.setAdapter(arrayAdp1);

        et_chute_status_config_chuteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_chute_status_config_chuteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String chuteId = et_chute_status_config_chuteid.getText().toString().trim().toUpperCase();
                    chuteId = objControls.replaceString(chuteId);
                    b_Result = objChuteConfigurationJafzaControl.validateChutes(chuteId);
                    if (!b_Result) {
                        okMessage("ChuteConfigurationJafzaFragment:et_chute_status_config_chuteid", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_chute_status_config_chuteid.setText("");
                        et_chute_status_config_chuteid.requestFocus();
                        return false;
                    } else {
                        et_chute_status_config_totid.setText(objChuteConfigurationJafzaGlobal.getToteid());

                        List<String> arr = objChuteConfigurationJafzaControl.loadShops();
                        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
                        sp_chute_status_config_shopname.setAdapter(arrayAdp);

                        sp_chute_status_config_shopname.setSelection(arrayAdp.getPosition(objChuteConfigurationJafzaGlobal.getShopName()));
                        sp_chute_status_config_status.setSelection(objChuteConfigurationJafzaGlobal.getStatus());
                    }
                }
                return false;
            }
        });

        bt_chute_status_config_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
                et_chute_status_config_chuteid.requestFocus();
            }
        });

        bt_chute_status_config_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chuteId = et_chute_status_config_chuteid.getText().toString();
                String shopName = sp_chute_status_config_shopname.getSelectedItem().toString();
                String toteId = et_chute_status_config_totid.getText().toString().toUpperCase();
                int stsId;
                if (sp_chute_status_config_status.getSelectedItem().equals("Normal"))
                    stsId = 0;
                else if (sp_chute_status_config_status.getSelectedItem().equals("Full"))
                    stsId = 1;
                else if (sp_chute_status_config_status.getSelectedItem().equals("Disable"))
                    stsId = 2;
                else if (sp_chute_status_config_status.getSelectedItem().equals("Unknown"))
                    stsId = 3;
                else
                    stsId = 2;
                String shopId = objChuteConfigurationJafzaControl.getShopid(sp_chute_status_config_shopname.getSelectedItem().toString());
                if (shopId.isEmpty()) {
                    okMessage("Chute Configuration", objGlobal.getErrorMessage());
                } else {
                    b_Result = objChuteConfigurationJafzaControl.chutesStatusValidate(chuteId, shopName, shopId, stsId, toteId);
                    if (!b_Result) {
                        closeWaitDialog();
                        okMessage("Check In", objGlobal.getErrorMessage());
                        bt_chute_status_config_update.requestFocus();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are You sure to save?")
                                .setTitle("Conformation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            mWaitDialog = ProgressDialog.show(getContext(), null, "(1) Please wait...");
                                            mWaitDialog.setCancelable(false);
                                            b_Result = objChuteConfigurationJafzaControl.chutesStatusSave(chuteId, shopName, shopId, stsId, toteId);
                                            if (b_Result) {
                                                //updateChuteStatusApiOld(stsId, shopId);
                                                updateChuteStatusApiNew(stsId, chuteId, shopId, shopName);
                                            } else {
                                                clearAll();
                                                closeWaitDialog();
                                                okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener-2: " + objGlobal.getErrorMessage());
                                                bt_chute_status_config_update.requestFocus();
                                            }
                                        } catch (Exception e) {
                                            clearAll();
                                            closeWaitDialog();
                                            okMessage("Chute status(bt_chute_status_inout_in)", "bt_chute_status_inout_in.setOnClickListener:onFailure-2: " + e.toString());
                                            bt_chute_status_config_update.requestFocus();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        closeWaitDialog();
                                        bt_chute_status_config_update.requestFocus();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
        return view;
    }

    void updateChuteMapingApiNew(String chuteid, String shopId, String shopname) {
        try {
            final AsyncHttpClient client = new AsyncHttpClient();
            String json = "{ 'mapping': [{'chute_id': '" + chuteid + "', 'shop_id': '" + shopId + "', 'shop_name': '" + shopname + "' }]}";
            StringEntity entity = new StringEntity(json.replace("'", "\""), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.addHeader("Authorization", objGlobal.getRoboChuteMapingAPIToken());
            client.addHeader("Accept", "application/json");
            client.post(getContext(), objGlobal.getRoboChuteMapingAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            clearAll();
                            closeWaitDialog();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            closeWaitDialog();
                            okMessage("Chute status(updateChuteMapingApiNew)", "bt_chute_status_inout_in.setOnClickListener:onFailure-1: " + error.toString());
                            et_chute_status_config_chuteid.requestFocus();
                        }
                    });
        } catch (Exception e) {
            clearAll();
            closeWaitDialog();
            okMessage("Chute status(updateChuteMapingApiNew)", "bt_chute_status_inout_in.setOnClickListener:onFailure-2: " + e.toString());
            et_chute_status_config_chuteid.requestFocus();
        }
    }

    void updateChuteStatusApiNew(int stsId, String chuteid, String shopId, String shopname) {
        try {
            boolean upStatus;
            final AsyncHttpClient client = new AsyncHttpClient();
            JSONObject json = new JSONObject();
            json.put("chuteId", et_chute_status_config_chuteid.getText().toString());
            if (stsId == 0)
                upStatus=true;
            else
                upStatus=false;
            json.put("status", upStatus);
            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
            client.post(getContext(), objGlobal.getRoboChuteStatusAPI() , entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                if (statusCode == 200) {
                                    JSONObject jso = new JSONObject(new String(responseBody));
                                    boolean  status = jso.getBoolean("status");
                                    String msg = jso.getString("message");
                                    if (status) {
                                        updateChuteMapingApiNew(chuteid,shopId,shopname);
                                    } else {
                                        vibrate(500);
                                        closeWaitDialog();
                                        okMessage("Chute status (1)", msg);
                                    }
                                }
                            } catch (Exception e) {
                                clearAll();
                                closeWaitDialog();
                                okMessage("Chute status(updateChuteStatusApiNew)", "bt_chute_status_inout_in.setOnClickListener:onFailure-2: " + e.toString());
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            closeWaitDialog();
                            okMessage("Chute status(updateChuteStatusApiNew)", "bt_chute_status_inout_in.setOnClickListener:onFailure-1: " + error.toString());
                            et_chute_status_config_chuteid.requestFocus();
                        }
                    });
        } catch (Exception e) {
            clearAll();
            closeWaitDialog();
            okMessage("Chute status(updateChuteStatusApiNew)", "bt_chute_status_inout_in.setOnClickListener:onFailure-2: " + e.toString());
        }
    }

    private class MyChuteConfigurationJafzaHistoryTicketAdp extends BaseAdapter {
        public ArrayList<ChuteConfigurationJafzaHistoryTicket> listChuteConfigurationJafzaHistoryTicket;

        public MyChuteConfigurationJafzaHistoryTicketAdp(ArrayList<ChuteConfigurationJafzaHistoryTicket> listChuteConfigurationJafzaHistoryTicket) {
            this.listChuteConfigurationJafzaHistoryTicket = listChuteConfigurationJafzaHistoryTicket;
        }

        @Override
        public int getCount() {
            return listChuteConfigurationJafzaHistoryTicket.size();
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
            View myView = mInflater.inflate(R.layout.chute_onfiguration_jafza_history_ticket, null);
            final ChuteConfigurationJafzaHistoryTicket s = listChuteConfigurationJafzaHistoryTicket.get(position);

            TextView tv_chute_config_jafza_ticket_chuteid = (TextView) myView.findViewById(R.id.tv_chute_config_jafza_ticket_chuteid);
            tv_chute_config_jafza_ticket_chuteid.setText(String.valueOf(s.chuteId));

            TextView tv_chute_config_jafza_ticket_trndate = (TextView) myView.findViewById(R.id.tv_chute_config_jafza_ticket_trndate);
            tv_chute_config_jafza_ticket_trndate.setText(String.valueOf(s.trnDate));

            TextView tv_chute_config_jafza_ticket_shopname = (TextView) myView.findViewById(R.id.tv_chute_config_jafza_ticket_shopname);
            tv_chute_config_jafza_ticket_shopname.setText(String.valueOf(s.shopName));

            TextView tv_chute_config_jafza_ticket_status = (TextView) myView.findViewById(R.id.tv_chute_config_jafza_ticket_status);
            tv_chute_config_jafza_ticket_status.setText(String.valueOf(s.status));

            TextView tv_chute_config_jafza_ticket_toteid = (TextView) myView.findViewById(R.id.tv_chute_config_jafza_ticket_toteid);
            tv_chute_config_jafza_ticket_toteid.setText(String.valueOf(s.toteId));
            return myView;
        }
    }

    void clearAll() {
        et_chute_status_config_totid.setText("");
        et_chute_status_config_chuteid.setText("");
        sp_chute_status_config_status.setSelection(0);
        sp_chute_status_config_shopname.setSelection(0);
        ArrayList<ChuteConfigurationJafzaHistoryTicket> listChuteConfigurationJafzaHistoryTicket = objChuteConfigurationJafzaControl.loadChuteConfigurationJafzaHistory();
        objMyChuteConfigurationJafzaHistoryTicketAdp = new ChuteConfigurationJafzaFragment.MyChuteConfigurationJafzaHistoryTicketAdp(listChuteConfigurationJafzaHistoryTicket);
        lv_chute_status_config_update.setAdapter(objMyChuteConfigurationJafzaHistoryTicketAdp);
        et_chute_status_config_chuteid.requestFocus();
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
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