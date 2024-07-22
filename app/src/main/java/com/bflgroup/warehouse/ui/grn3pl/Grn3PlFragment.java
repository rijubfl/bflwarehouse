package com.bflgroup.warehouse.ui.grn3pl;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;

public class Grn3PlFragment extends Fragment {

    private Button bt_3pl_wh_grn_clear;
    private Button bt_3pl_wh_grn_save;
    private Button bt_3pl_wh_grn_scan;
    private EditText et_3pl_wh_grn_trfno;
    private ListView lv_3pl_wh_grn_details;
    private EditText et_3pl_wh_grn_remarks;
    private TextView tv_3pl_wh_grn_trfno;
    private TextView tv_3pl_wh_grn_result;

    Global objGlobal = Global.getInstance();
    private Grn3PlGlobal objGrn3PlGlobal = Grn3PlGlobal.getInstance();
    Grn3PlControl objGrn3PlControl = new Grn3PlControl();
    Grn3PlFragment.MyGrn3PlFragmentAdp objMyGrn3PlFragmentAdp;
    Controls objControls = new Controls();

    private boolean b_Result;
    private String s_Result;

    public Grn3PlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grn3_pl, container, false);

        bt_3pl_wh_grn_clear = (Button) view.findViewById(R.id.bt_3pl_wh_grn_clear);
        bt_3pl_wh_grn_save = (Button) view.findViewById(R.id.bt_3pl_wh_grn_save);
        bt_3pl_wh_grn_scan = (Button) view.findViewById(R.id.bt_3pl_wh_grn_scan);
        et_3pl_wh_grn_trfno = (EditText) view.findViewById(R.id.et_3pl_wh_grn_trfno);
        lv_3pl_wh_grn_details = (ListView) view.findViewById(R.id.lv_3pl_wh_grn_details);
        et_3pl_wh_grn_remarks = (EditText) view.findViewById(R.id.et_3pl_wh_grn_remarks);
        tv_3pl_wh_grn_trfno = (TextView) view.findViewById(R.id.tv_3pl_wh_grn_trfno);
        tv_3pl_wh_grn_result = (TextView) view.findViewById(R.id.tv_3pl_wh_grn_result);

        ArrayList<Grn3PlTicket> listLoadItems = objGrn3PlControl.loadScannedTransfers();
        objMyGrn3PlFragmentAdp = new Grn3PlFragment.MyGrn3PlFragmentAdp(listLoadItems);
        lv_3pl_wh_grn_details.setAdapter(objMyGrn3PlFragmentAdp);

        et_3pl_wh_grn_trfno.setOnTouchListener(new View.OnTouchListener() {
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

        et_3pl_wh_grn_trfno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String trfno = objControls.replaceString(et_3pl_wh_grn_trfno.getText().toString()).toUpperCase();
                    b_Result = objGrn3PlControl.validateTransferScan3Pl(trfno);
                    if (!b_Result) {
                        okMessage("3pl WH Grn", objGlobal.getErrorMessage());
                        et_3pl_wh_grn_trfno.setText("");
                        et_3pl_wh_grn_trfno.requestFocus();
                        return true;
                    } else {
                        ArrayList<Grn3PlTicket> listLoadItems = objGrn3PlControl.loadScannedTransfers();
                        objMyGrn3PlFragmentAdp = new Grn3PlFragment.MyGrn3PlFragmentAdp(listLoadItems);
                        tv_3pl_wh_grn_trfno.setText(trfno);
                        tv_3pl_wh_grn_result.setText(objGrn3PlGlobal.getPalletStatus());
                        lv_3pl_wh_grn_details.setAdapter(objMyGrn3PlFragmentAdp);
                        et_3pl_wh_grn_trfno.setText("");
                        et_3pl_wh_grn_trfno.requestFocus();
                    }
                }
                return false;
            }
        });

        bt_3pl_wh_grn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trfno = objControls.replaceString(et_3pl_wh_grn_trfno.getText().toString()).toUpperCase();
                b_Result = objGrn3PlControl.validateTransferScan3Pl(trfno);
                if (!b_Result) {
                    okMessage("3pl WH Grn", objGlobal.getErrorMessage());
                    et_3pl_wh_grn_trfno.setText("");
                    et_3pl_wh_grn_trfno.requestFocus();
                } else {
                    ArrayList<Grn3PlTicket> listLoadItems = objGrn3PlControl.loadScannedTransfers();
                    objMyGrn3PlFragmentAdp = new Grn3PlFragment.MyGrn3PlFragmentAdp(listLoadItems);
                    lv_3pl_wh_grn_details.setAdapter(objMyGrn3PlFragmentAdp);
                    et_3pl_wh_grn_trfno.setText("");
                    et_3pl_wh_grn_trfno.requestFocus();
                }
            }
        });

        bt_3pl_wh_grn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objGrn3PlControl.clearTable();
                                if (b_Result) {
                                    ArrayList<Grn3PlTicket> listLoadItems = objGrn3PlControl.loadScannedTransfers();
                                    objMyGrn3PlFragmentAdp = new Grn3PlFragment.MyGrn3PlFragmentAdp(listLoadItems);
                                    lv_3pl_wh_grn_details.setAdapter(objMyGrn3PlFragmentAdp);
                                }
                                et_3pl_wh_grn_remarks.setText("");
                                et_3pl_wh_grn_trfno.setText("");
                                et_3pl_wh_grn_trfno.requestFocus();
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

        bt_3pl_wh_grn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarks = et_3pl_wh_grn_remarks.getText().toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objGrn3PlControl.save3plWhGrn(remarks);
                                if (!b_Result) {
                                    okMessage("3pl WH Grn", objGlobal.getErrorMessage());
                                } else {
                                    if (!objGrn3PlControl.clearTable()) {
                                        okMessage("3pl WH Grn", objGlobal.getErrorMessage());
                                    } else {
                                        ArrayList<Grn3PlTicket> listLoadItems = objGrn3PlControl.loadScannedTransfers();
                                        objMyGrn3PlFragmentAdp = new Grn3PlFragment.MyGrn3PlFragmentAdp(listLoadItems);
                                        lv_3pl_wh_grn_details.setAdapter(objMyGrn3PlFragmentAdp);
                                        et_3pl_wh_grn_remarks.setText("");
                                        et_3pl_wh_grn_trfno.setText("");
                                        tv_3pl_wh_grn_result.setText("");
                                        tv_3pl_wh_grn_trfno.setText("");
                                        et_3pl_wh_grn_trfno.requestFocus();
                                    }
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
        et_3pl_wh_grn_trfno.requestFocus();

        return view;
    }

    private class MyGrn3PlFragmentAdp extends BaseAdapter {
        public ArrayList<Grn3PlTicket> list3plWhGrnTicketTicket;

        public MyGrn3PlFragmentAdp(ArrayList<Grn3PlTicket> list3plWhGrnTicketTicket) {
            this.list3plWhGrnTicketTicket = list3plWhGrnTicketTicket;
        }

        @Override
        public int getCount() {
            return list3plWhGrnTicketTicket.size();
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
            View myView = mInflater.inflate(R.layout.grn_3pl_wh_ticket, null);
            final Grn3PlTicket s = list3plWhGrnTicketTicket.get(position);
            TextView tv_3pl_wh_grn_ticket_trfno = (TextView) myView.findViewById(R.id.tv_3pl_wh_grn_ticket_trfno);
            tv_3pl_wh_grn_ticket_trfno.setText(String.valueOf(s.trfNo));
            TextView tv_3pl_wh_grn_ticket_toteid = (TextView) myView.findViewById(R.id.tv_3pl_wh_grn_ticket_toteid);
            tv_3pl_wh_grn_ticket_toteid.setText(String.valueOf(s.storeIssue));
            TextView tv_3pl_wh_grn_ticket_status = (TextView) myView.findViewById(R.id.tv_3pl_wh_grn_ticket_status);
            tv_3pl_wh_grn_ticket_status.setText(String.valueOf(s.pltStatus));
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