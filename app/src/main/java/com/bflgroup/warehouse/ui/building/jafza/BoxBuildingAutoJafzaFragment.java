package com.bflgroup.warehouse.ui.building.jafza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class BoxBuildingAutoJafzaFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private BuildingJafzaGLobal objBuildingJafzaGLobal = BuildingJafzaGLobal.getInstance();
    private BoxBuildingAutoJafzaControl objBoxBuildingAutoJafzaControl = new BoxBuildingAutoJafzaControl();

    ArrayList<BuildingItemJafzaTicket> listBuildingItemTicket = new ArrayList<BuildingItemJafzaTicket>();
    BoxBuildingAutoJafzaFragment.MyBuildingItemsAdp objMyBuildingItemsAdp;

    private EditText et_building_chuteid;
    private EditText et_chute_building_totid;
    private TextView tv_building_totid;
    private TextView tv_building_time;
    private TextView tv_chute_building_status;
    private TextView tv_chute_status_building_shopid;
    private TextView tv_chute_status_building_shopname;
    private ListView lv_chute_status_building_details;
    private TextView tv_chute_status_building_trfno;
    private TextView tv_chute_status_building_tot_qty;
    private Button bt_chute_status_building_in;
    private Button bt_chute_status_building_build;
    private Button bt_chute_status_building_clear;
    private ProgressBar pr_chute_building_inout;

    private boolean b_Result;
    private String s_Result;
    Boolean strflg = false;
    private ProgressDialog mWaitDialog;

    public BoxBuildingAutoJafzaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_box_building_auto_jafza, container, false);

        et_building_chuteid = (EditText) view.findViewById(R.id.et_building_chuteid);
        et_chute_building_totid = (EditText) view.findViewById(R.id.et_chute_building_totid);
        tv_building_totid = (TextView) view.findViewById(R.id.tv_building_totid);
        tv_building_time = (TextView) view.findViewById(R.id.tv_building_time);
        tv_chute_building_status = (TextView) view.findViewById(R.id.tv_chute_building_status);
        tv_chute_status_building_shopid = (TextView) view.findViewById(R.id.tv_chute_status_building_shopid);
        tv_chute_status_building_shopname = (TextView) view.findViewById(R.id.tv_chute_status_building_shopname);
        lv_chute_status_building_details = (ListView) view.findViewById(R.id.lv_chute_status_building_details);
        tv_chute_status_building_trfno = (TextView) view.findViewById(R.id.tv_chute_status_building_trfno);
        tv_chute_status_building_tot_qty = (TextView) view.findViewById(R.id.tv_chute_status_building_tot_qty);
        bt_chute_status_building_in = (Button) view.findViewById(R.id.bt_chute_status_building_in);
        bt_chute_status_building_build = (Button) view.findViewById(R.id.bt_chute_status_building_build);
        bt_chute_status_building_clear = (Button) view.findViewById(R.id.bt_chute_status_building_clear);
        pr_chute_building_inout = (ProgressBar) view.findViewById(R.id.pr_chute_building_inout);

        clearAll();
        et_building_chuteid.requestFocus();
        et_building_chuteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_chute_building_totid.setOnTouchListener(new View.OnTouchListener() {
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

        et_building_chuteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_building_chuteid.setText(et_building_chuteid.getText().toString().toUpperCase());
                    //progressVisivle(true);
                    et_chute_building_totid.setText("");
                    tv_building_totid.setText("");
                    b_Result = validateChuteId(et_building_chuteid.getText().toString());
                    if (b_Result) {
                        b_Result = loadItemsForPackingList(et_building_chuteid.getText().toString(), tv_chute_status_building_shopid.getText().toString());
                        if (!b_Result) {
                            okMessage("Chute Status", objGlobal.getErrorMessage());
                            //et_chute_status_inout_chuteid.setFocusable(true);
                            //progressVisivle(false);
                            et_building_chuteid.setFocusable(true);
                            strflg = true;
                            return true;
                        } else {
                            et_chute_building_totid.setFocusable(true);
                            //et_chute_status_inout_totid.requestFocus();
                            //progressVisivle(false);
                            //strflg=true;
                            return true;
                            //et_chute_status_inout_totid.requestFocus();
                        }
                    } else {
                        //progressVisivle(false);
                        clearAll();
                        et_building_chuteid.getText().clear();
                        et_building_chuteid.requestFocus();
                        et_building_chuteid.setFocusable(true);
                        strflg = true;
                        return true;
                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_building_chuteid.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }
        });

        et_building_chuteid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //et_building_chuteid.setText(et_building_chuteid.getText().toString().toUpperCase());
                }
            }
        });

        et_chute_building_totid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(et_chute_building_totid.getText())) {
                        if (et_chute_building_totid.getText().length() > 8)
                            et_chute_building_totid.setText(et_chute_building_totid.getText().toString().substring(0, 8));
                    }
                    b_Result = validateToteId(et_chute_building_totid.getText().toString());
                    if (!b_Result) {
                        et_chute_building_totid.getText().clear();
                    }
                    et_chute_building_totid.requestFocus();
                    return true;
                } else {
                    et_chute_building_totid.requestFocus();
                    return false;
                }
            }
        });

        et_chute_building_totid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //et_chute_status_inout_totid.setText(et_chute_status_inout_totid.getText().toString().substring(0, 8));
                }
            }
        });

        bt_chute_status_building_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
                closeWaitDialog();
                et_building_chuteid.requestFocus();
            }
        });

        bt_chute_status_building_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chuteId = et_building_chuteid.getText().toString();
                String totId = et_chute_building_totid.getText().toString();
                String shopName = tv_chute_status_building_shopname.getText().toString();
                String status = tv_chute_building_status.getText().toString();
                String shopId = tv_chute_status_building_shopid.getText().toString();
                mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                mWaitDialog.setCancelable(false);
                b_Result = objBoxBuildingAutoJafzaControl.validateCheckInOut("IN", chuteId, totId, shopId, shopName, status);
                if (!b_Result) {
                    closeWaitDialog();
                    okMessage("Check In", objGlobal.getErrorMessage());
                    bt_chute_status_building_in.requestFocus();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        final AsyncHttpClient client = new AsyncHttpClient();
                                        JSONObject json = new JSONObject();
                                        json.put("ChuteId", chuteId);
                                        json.put("status", true);
                                        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                                        entity.setContentType("application/json");
                                        client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
                                        client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                                                new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        try {
                                                            if(statusCode==200){
                                                                JSONObject jso = new JSONObject(new String(responseBody));
                                                                boolean status = jso.getBoolean("status");
                                                                String msg = jso.getString("message");
                                                                if (status) {
                                                                    if (objBoxBuildingAutoJafzaControl.saveChuteIn(chuteId, totId, shopId, shopName, "0")) {
                                                                        clearAll();
                                                                        closeWaitDialog();
                                                                        et_building_chuteid.requestFocus();
                                                                    } else {
                                                                        vibrate(500);
                                                                        closeWaitDialog();
                                                                        okMessage("Chute status", "bt_chute_status_inout_in: " + objGlobal.getErrorMessage());
                                                                        et_building_chuteid.requestFocus();
                                                                    }
                                                                } else {
                                                                    vibrate(500);
                                                                    closeWaitDialog();
                                                                    okMessage("Chute status (1)", msg);
                                                                    et_building_chuteid.requestFocus();
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            vibrate(500);
                                                            closeWaitDialog();
                                                            okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:try: " + e);
                                                            et_building_chuteid.requestFocus();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        vibrate(500);
                                                        closeWaitDialog();
                                                        okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:onFailure: " + error.toString());
                                                        et_building_chuteid.requestFocus();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        vibrate(500);
                                        closeWaitDialog();
                                        okMessage("Chute status", "bt_chute_status_inout_in.setOnClickListener:onFailure: " + e.toString());
                                        et_building_chuteid.requestFocus();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closeWaitDialog();
                                    et_building_chuteid.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });

        bt_chute_status_building_build.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chuteId = et_building_chuteid.getText().toString();
                String totId = et_chute_building_totid.getText().toString();
                String shopName = tv_chute_status_building_shopname.getText().toString();
                String status = tv_chute_building_status.getText().toString();
                String shopId = tv_chute_status_building_shopid.getText().toString();
                mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                mWaitDialog.setCancelable(false);
                b_Result = objBoxBuildingAutoJafzaControl.validateCheckInOut("OUT", chuteId, totId, shopId, shopName, status);
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
                                    try {
                                        final AsyncHttpClient client = new AsyncHttpClient();
                                        JSONObject json = new JSONObject();
                                        json.put("ChuteId", et_building_chuteid.getText().toString());
                                        json.put("status", false);
                                        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
                                        entity.setContentType("application/json");
                                        if(!objGlobal.getRoboChuteStatusAPIToken().isEmpty()) client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
                                        client.post(getContext(), objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                                                new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        b_Result = objBoxBuildingAutoJafzaControl.saveChuteBuilding(chuteId, totId, shopId, shopName);
                                                        if (!b_Result) {
                                                            okMessage("Chute status:bt_chute_status_building_build", objGlobal.getErrorMessage() + " - " + objGlobal.getErrorNo());
                                                            closeWaitDialog();
                                                            et_building_chuteid.requestFocus();
                                                        } else {
                                                            clearAll();
                                                            tv_chute_status_building_trfno.setText(chuteId + "  ;  " + totId + "  ;  " + objBuildingJafzaGLobal.getBoxNo() + "  ;  " + String.valueOf(objBuildingJafzaGLobal.getTotBuildQty()));
                                                            closeWaitDialog();
                                                            et_building_chuteid.requestFocus();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        okMessage("Chute status", "validateChuteId:onFailure: " + error.toString());
                                                        closeWaitDialog();
                                                        et_building_chuteid.requestFocus();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        okMessage("Chute status", "validateChuteId:Exception: " + e.toString());
                                        closeWaitDialog();
                                        et_building_chuteid.requestFocus();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closeWaitDialog();
                                    et_building_chuteid.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });
        return view;
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    boolean validateToteId(String toteId) {
        if (TextUtils.isEmpty(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Please enter Tote Id, ");
            return false;
        }
        if (!objBoxBuildingAutoJafzaControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objBoxBuildingAutoJafzaControl.checkValidToteId(toteId)) {
            vibrate(300);
            showMessage("Chute Status", "Invalid Tote ID, " + toteId);
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
        if (!objBoxBuildingAutoJafzaControl.checkConnection()) {
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        if (!objBoxBuildingAutoJafzaControl.checkValidChuteId(chuteId)) {
            vibrate(300);
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        s_Result = objBoxBuildingAutoJafzaControl.getShopIdFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_building_shopid.setText(s_Result);
        s_Result = objBoxBuildingAutoJafzaControl.getShopnameFromChuteId(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Shop Not found from Chute Id, " + chuteId);
            return false;
        }
        tv_chute_status_building_shopname.setText(s_Result);
        s_Result = objBoxBuildingAutoJafzaControl.getToteIdFromChuteId(chuteId);
        if (!TextUtils.isEmpty(s_Result)) {
            tv_building_totid.setText(s_Result);
        }
        s_Result = objBoxBuildingAutoJafzaControl.getChuteStatus(chuteId);
        if (TextUtils.isEmpty(s_Result)) {
            vibrate(300);
            showMessage("Chute Status", "Chute Status Not valid, " + chuteId);
            return false;
        }
        tv_chute_building_status.setText(s_Result);
        if (!objBoxBuildingAutoJafzaControl.getLastChuteInOut(chuteId)) {
            vibrate(300);
            showMessage("getLastChuteInOut", objGlobal.getErrorMessage());
            return false;
        }
        b_Result = objBoxBuildingAutoJafzaControl.validForCheckBuildOrExport(tv_chute_status_building_shopname.getText().toString());
        if (!b_Result) {
            vibrate(300);
            showMessage("Chute Status", objGlobal.getErrorMessage());
            return false;
        }
        tv_building_time.setText(objBuildingJafzaGLobal.getChuteLastInOut());
        return true;
    }

    private boolean loadItemsForPackingList(String chuteId, String shopId) {
        try {
            listBuildingItemTicket.clear();
            listBuildingItemTicket = objBoxBuildingAutoJafzaControl.itemsForBuilding(chuteId, shopId);
            objMyBuildingItemsAdp = new BoxBuildingAutoJafzaFragment.MyBuildingItemsAdp(listBuildingItemTicket);
            lv_chute_status_building_details.setAdapter(objMyBuildingItemsAdp);
            tv_chute_status_building_tot_qty.setText(String.valueOf(objBuildingJafzaGLobal.getTotBuildQty()));
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsForPackingList:try: " + e.toString());
            return false;
        }
    }

    void clearAll() {
        et_building_chuteid.setText("");
        et_chute_building_totid.setText("");
        tv_building_totid.setText("");
        tv_building_time.setText("");
        tv_chute_building_status.setText("");
        tv_chute_status_building_shopname.setText("");
        tv_chute_status_building_shopid.setText("");
        listBuildingItemTicket.clear();
        objMyBuildingItemsAdp = new BoxBuildingAutoJafzaFragment.MyBuildingItemsAdp(listBuildingItemTicket);
        lv_chute_status_building_details.setAdapter(objMyBuildingItemsAdp);
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

    private class MyBuildingItemsAdp extends BaseAdapter {
        public ArrayList<BuildingItemJafzaTicket> listBuildingItemTicket;

        public MyBuildingItemsAdp(ArrayList<BuildingItemJafzaTicket> listBuildingItemTicket) {
            this.listBuildingItemTicket = listBuildingItemTicket;
        }

        @Override
        public int getCount() {
            return listBuildingItemTicket.size();
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
            final BuildingItemJafzaTicket s = listBuildingItemTicket.get(position);
            TextView tv_check_in_check_out_tickte_itemcode = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_itemcode);
            tv_check_in_check_out_tickte_itemcode.setText(String.valueOf(s.itemCode));
            TextView tv_check_in_check_out_tickte_description = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_description);
            tv_check_in_check_out_tickte_description.setText(String.valueOf(s.description));
            TextView tv_check_in_check_out_tickte_qty = (TextView) myView.findViewById(R.id.tv_check_in_check_out_tickte_qty);
            tv_check_in_check_out_tickte_qty.setText(String.valueOf(s.quantity));
            return myView;
        }
    }
}