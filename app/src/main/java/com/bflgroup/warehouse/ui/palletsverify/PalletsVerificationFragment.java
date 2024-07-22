package com.bflgroup.warehouse.ui.palletsverify;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;

public class PalletsVerificationFragment extends Fragment {

    private Button bt_gin_verification_load;
    private EditText et_gin_verification_ginno;
    private EditText et_gin_verification_trf_tote_id;
    private ListView lv_gin_verification_details;
    private TextView tv_gin_verification_verify;
    private TextView et_wh_grn_warehouse_from3;
    private TextView et_wh_grn_warehouse_to3;
    private Button bt_gin_verification_clear;
    private Button bt_gin_verification_save;

    Global objGlobal = Global.getInstance();
    PalletsVerificationShared objPalletsVerificationShared;
    PalletsVerificationControl objPalletsVerificationControl = new PalletsVerificationControl();
    PalletsVerificationGlobal objPalletsVerificationGlobal = PalletsVerificationGlobal.getInstance();
    MyPalletsVerificationAdp objMyPalletsVerificationAdp;

    private boolean b_Result;
    private String s_Result;

    public PalletsVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pallets_verification, container, false);

        bt_gin_verification_load = (Button) view.findViewById(R.id.bt_gin_verification_load);
        et_gin_verification_ginno = (EditText) view.findViewById(R.id.et_gin_verification_ginno);
        et_gin_verification_trf_tote_id = (EditText) view.findViewById(R.id.et_gin_verification_trf_tote_id);

        et_wh_grn_warehouse_from3 = (TextView) view.findViewById(R.id.et_wh_grn_warehouse_from3);
        et_wh_grn_warehouse_to3 = (TextView) view.findViewById(R.id.et_wh_grn_warehouse_to3);

        lv_gin_verification_details = (ListView) view.findViewById(R.id.lv_gin_verification_details);
        bt_gin_verification_clear = (Button) view.findViewById(R.id.bt_gin_verification_clear);
        bt_gin_verification_save = (Button) view.findViewById(R.id.bt_gin_verification_save);
        tv_gin_verification_verify = (TextView) view.findViewById(R.id.tv_gin_verification_verify);

        objPalletsVerificationShared=new PalletsVerificationShared(getContext());

        et_gin_verification_ginno.requestFocus();

        if (objPalletsVerificationShared.loadVerifyGinNo()!="") {
            et_gin_verification_ginno.setText(objPalletsVerificationShared.loadVerifyGinNo());
            ArrayList<PalletsVerificationTicket> listPalletsVerificationDetail = objPalletsVerificationControl.loadGinVerifyDetails();
            objMyPalletsVerificationAdp = new MyPalletsVerificationAdp(listPalletsVerificationDetail);
            lv_gin_verification_details.setAdapter(objMyPalletsVerificationAdp);

            et_wh_grn_warehouse_from3.setText(objPalletsVerificationShared.loadWHFrom());
            et_wh_grn_warehouse_to3.setText(objPalletsVerificationShared.loadWHTo());

            tv_gin_verification_verify.setText(objPalletsVerificationGlobal.getScanCount());
            et_gin_verification_ginno.setEnabled(false);
            bt_gin_verification_load.setEnabled(false);
            et_gin_verification_trf_tote_id.requestFocus();
        }

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
                    b_Result = objPalletsVerificationControl.validateTrfno(toteTrfId, et_gin_verification_ginno.getText().toString());
                    if (b_Result == false) {
                        okMessage("GinVerificationFragment:et_gin_verification_trf_tote_id", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_gin_verification_trf_tote_id.setText("");
                        et_gin_verification_trf_tote_id.requestFocus();
                        return false;
                    } else {
                        ArrayList<PalletsVerificationTicket> listGinVerificationDetail = objPalletsVerificationControl.loadGinVerifyDetails();
                        objMyPalletsVerificationAdp = new MyPalletsVerificationAdp(listGinVerificationDetail);
                        lv_gin_verification_details.setAdapter(objMyPalletsVerificationAdp);
                        tv_gin_verification_verify.setText(objPalletsVerificationGlobal.getScanCount());
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
                b_Result = objPalletsVerificationControl.validateGin(et_gin_verification_ginno.getText().toString(), true);
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
                                    b_Result = objPalletsVerificationControl.saveGinVerification(et_gin_verification_ginno.getText().toString());
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
            b_Result=objPalletsVerificationControl.clearTable();
            if(b_Result==false){
                okMessage("BinBatchInFragment:clearAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            } else {
                ArrayList<PalletsVerificationTicket> listGinVerificationDetail = objPalletsVerificationControl.loadGinVerifyDetails();
                objMyPalletsVerificationAdp = new MyPalletsVerificationAdp(listGinVerificationDetail);
                lv_gin_verification_details.setAdapter(objMyPalletsVerificationAdp);
                tv_gin_verification_verify.setText(objPalletsVerificationGlobal.getScanCount());
                et_gin_verification_ginno.setText("");
                tv_gin_verification_verify.setText("");
                objPalletsVerificationShared.saveWHTo("");
                objPalletsVerificationShared.saveWHFrom("");
                et_wh_grn_warehouse_from3.setText("");
                et_wh_grn_warehouse_to3.setText("");
                objPalletsVerificationShared.saveVerifyGinNo("");
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
                b_Result = objPalletsVerificationControl.validateGin(et_gin_verification_ginno.getText().toString(),false);
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
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            } else {
                ArrayList<PalletsVerificationTicket> listGinVerificationDetail = objPalletsVerificationControl.loadGinVerifyDetails();
                objMyPalletsVerificationAdp = new MyPalletsVerificationAdp(listGinVerificationDetail);
                lv_gin_verification_details.setAdapter(objMyPalletsVerificationAdp);
                objPalletsVerificationShared.saveVerifyGinNo(et_gin_verification_ginno.getText().toString());


                objPalletsVerificationShared.saveWHFrom(objPalletsVerificationGlobal.getWarehouseFrom());
                objPalletsVerificationShared.saveWHTo(objPalletsVerificationGlobal.getWarehouseTo());

                et_wh_grn_warehouse_from3.setText(objPalletsVerificationGlobal.getWarehouseFrom()+"");
                et_wh_grn_warehouse_to3.setText(objPalletsVerificationGlobal.getWarehouseTo() +"");

                tv_gin_verification_verify.setText(objPalletsVerificationGlobal.getScanCount());
                et_gin_verification_ginno.setEnabled(false);
                bt_gin_verification_load.setEnabled(false);
                et_gin_verification_trf_tote_id.requestFocus();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class MyPalletsVerificationAdp extends BaseAdapter {
        public ArrayList<PalletsVerificationTicket> listPalletsVerificationTicket;

        public MyPalletsVerificationAdp(ArrayList<PalletsVerificationTicket> listPalletsVerificationTicket) {
            this.listPalletsVerificationTicket = listPalletsVerificationTicket;
        }

        @Override
        public int getCount() {
            return listPalletsVerificationTicket.size();
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
            View myView = mInflater.inflate(R.layout.pallets_verification_ticket, null);
            final PalletsVerificationTicket s = listPalletsVerificationTicket.get(position);

            TextView tv_gin_verification_ticket_ginno = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_ginno);
            tv_gin_verification_ticket_ginno.setText(String.valueOf(s.ginNo));

            TextView tv_gin_verification_ticket_palletno = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_palletno);
            tv_gin_verification_ticket_palletno.setText(String.valueOf(s.palletNo));

            TextView tv_gin_verification_ticket_toteid = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_toteid);
            tv_gin_verification_ticket_toteid.setText(String.valueOf(s.toteId));

//            TextView tv_gin_verification_ticket_qty = (TextView) myView.findViewById(R.id.tv_gin_verification_ticket_Qty);
//            tv_gin_verification_ticket_qty.setText(String.valueOf(s.qty));

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