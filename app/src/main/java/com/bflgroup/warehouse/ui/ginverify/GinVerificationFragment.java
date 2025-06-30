package com.bflgroup.warehouse.ui.ginverify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;

public class GinVerificationFragment extends Fragment {

    private Button bt_gin_verification_load;
    private EditText et_gin_verification_ginno;
    private CheckBox ch_gin_verification_verifiedonly;
    private EditText et_gin_verification_trf_tote_id;
    private ListView lv_gin_verification_details;
    private TextView tv_gin_verification_verify;
    private Button bt_gin_verification_clear;
    private Button bt_gin_verification_save;

    Global objGlobal = Global.getInstance();
    GinVerificationShared objGinVerificationShared;
    GinVerificationControl objGinVerificationControl = new GinVerificationControl();
    GinVerificationGlobal objGinVerificationGlobal = GinVerificationGlobal.getInstance();
    MyGinVerificationAdp objMyGinVerificationAdp;

    private boolean b_Result;
    private String s_Result;

    public GinVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gin_verification, container, false);

        bt_gin_verification_load = (Button) view.findViewById(R.id.bt_gin_verification_load);
        et_gin_verification_ginno = (EditText) view.findViewById(R.id.et_gin_verification_ginno);
        ch_gin_verification_verifiedonly= (CheckBox) view.findViewById(R.id.ch_gin_verification_verifiedonly);
        et_gin_verification_trf_tote_id = (EditText) view.findViewById(R.id.et_gin_verification_trf_tote_id);
        lv_gin_verification_details = (ListView) view.findViewById(R.id.lv_gin_verification_details);
        bt_gin_verification_clear = (Button) view.findViewById(R.id.bt_gin_verification_clear);
        bt_gin_verification_save = (Button) view.findViewById(R.id.bt_gin_verification_save);
        tv_gin_verification_verify = (TextView) view.findViewById(R.id.tv_gin_verification_verify);

        objGinVerificationShared=new GinVerificationShared(getContext());

        et_gin_verification_ginno.requestFocus();

        if (objGinVerificationShared.loadVerifyGinNo()!="") {
            et_gin_verification_ginno.setText(objGinVerificationShared.loadVerifyGinNo());
            loadScanedDetail();
            et_gin_verification_ginno.setEnabled(false);
            bt_gin_verification_load.setEnabled(false);
            et_gin_verification_trf_tote_id.requestFocus();
        }

        ch_gin_verification_verifiedonly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loadScanedDetail();
            }
        });

        bt_gin_verification_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadTransfers().execute();
            }
        });

        et_gin_verification_trf_tote_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String toteTrfId = et_gin_verification_trf_tote_id.getText().toString().trim().toUpperCase();
                    b_Result = objGinVerificationControl.validateTrfno(toteTrfId, et_gin_verification_ginno.getText().toString());
                    if (b_Result == false) {
                        okMessage("GinVerificationFragment:et_gin_verification_trf_tote_id", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_gin_verification_trf_tote_id.setText("");
                        et_gin_verification_trf_tote_id.requestFocus();
                        return false;
                    } else {
                        loadScanedDetail();
                        et_gin_verification_trf_tote_id.setText("");
                        et_gin_verification_trf_tote_id.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        bt_gin_verification_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b_Result = objGinVerificationControl.validateGin(et_gin_verification_ginno.getText().toString(), true);
                if (!b_Result) {
                    okMessage("bt_bin_batch_in_save", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objGinVerificationControl.saveGinVerification(et_gin_verification_ginno.getText().toString());
                                    if (!b_Result) {
                                        okMessage("bt_bin_batch_in_save", objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = clearAll();
                                        if (!b_Result) {
                                            okMessage("bt_bin_batch_in_save", objGlobal.getErrorMessage());
                                        } else {
                                            okMessage("bt_gin_verification_save", "Done");
                                        }
                                        et_gin_verification_ginno.requestFocus();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        bt_gin_verification_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result=clearAll();
                                if(b_Result){
                                    et_gin_verification_ginno.requestFocus();
                                }
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
        return view;
    }

    boolean clearAll(){
        try {
            b_Result=objGinVerificationControl.clearTable();
            if(b_Result==false){
                okMessage("BinBatchInFragment:clearAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            } else {
                loadScanedDetail();
                et_gin_verification_ginno.setText("");
                tv_gin_verification_verify.setText("");
                objGinVerificationShared.saveVerifyGinNo("");
                et_gin_verification_trf_tote_id.setText("");
                et_gin_verification_ginno.setEnabled(true);
                bt_gin_verification_load.setEnabled(true);
            }
        }  catch(Exception ex) {
            objGlobal.setErrorMessage("BinBatchInFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    void loadScanedDetail(){
        String checked="";
        if(ch_gin_verification_verifiedonly.isChecked()){
            checked="Y";
        }
        ArrayList<GinVerificationTicket> listGinVerificationDetail = objGinVerificationControl.loadGinVerifyDetails(checked);
        objMyGinVerificationAdp = new MyGinVerificationAdp(listGinVerificationDetail);
        lv_gin_verification_details.setAdapter(objMyGinVerificationAdp);
        tv_gin_verification_verify.setText(objGinVerificationGlobal.getScanCount());
    }

    private class LoadTransfers extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        public LoadTransfers() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objGinVerificationControl.validateGin(et_gin_verification_ginno.getText().toString(),false);
                if (b_Result == false) {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==0){
                okMessage("GIN Verification", objGlobal.getErrorMessage());
            } else {
                loadScanedDetail();
                objGinVerificationShared.saveVerifyGinNo(et_gin_verification_ginno.getText().toString());
                et_gin_verification_ginno.setEnabled(false);
                bt_gin_verification_load.setEnabled(false);
                et_gin_verification_trf_tote_id.requestFocus();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class MyGinVerificationAdp extends BaseAdapter {
        public ArrayList<GinVerificationTicket> listGinVerificationTicket;

        public MyGinVerificationAdp(ArrayList<GinVerificationTicket> listGinVerificationTicket) {
            this.listGinVerificationTicket = listGinVerificationTicket;
        }

        @Override
        public int getCount() {
            return listGinVerificationTicket.size();
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
            View myView = mInflater.inflate(R.layout.gin_verification_ticket, null);
            final GinVerificationTicket s = listGinVerificationTicket.get(position);

            TextView tv_gin_verification_ticket_ginno = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_ginno);
            tv_gin_verification_ticket_ginno.setText(String.valueOf(s.ginNo));

            TextView tv_gin_verification_ticket_palletno = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_palletno);
            tv_gin_verification_ticket_palletno.setText(String.valueOf(s.palletNo));

            TextView tv_gin_verification_ticket_toteid = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_toteid);
            tv_gin_verification_ticket_toteid.setText(String.valueOf(s.toteId));

            TextView tv_gin_verification_ticket_trfno = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_trfno);
            tv_gin_verification_ticket_trfno.setText(String.valueOf(s.trfNo));

            TextView tv_gin_verification_ticket_status = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_status);
            tv_gin_verification_ticket_status.setText(String.valueOf(s.verified));

            return myView;
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