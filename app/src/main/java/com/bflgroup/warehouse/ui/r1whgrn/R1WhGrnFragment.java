package com.bflgroup.warehouse.ui.r1whgrn;

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
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;


public class R1WhGrnFragment extends Fragment {

    private Button bt_r1_wh_grn_clear;
    private Button bt_r1_wh_grn_save;
    private Button bt_r1_wh_grn_scan;
    private EditText et_r1_wh_grn_trfno;
    private ListView lv_r1_wh_grn_details;
    private EditText et_r1_wh_grn_remarks;
    private TextView tv_r1_wh_grn_trfno;
    private TextView tv_r1_wh_grn_result;

    Global objGlobal = Global.getInstance();
    private R1WhGrnGlobal objR1WhGrnGlobal = R1WhGrnGlobal.getInstance();
    R1WhGrnControl objR1WhGrnControl = new R1WhGrnControl();
    R1WhGrnFragment.MyR1WhGrnFragmentAdp objMyR1WhGrnFragmentAdp;
    Controls objControls = new Controls();

    private boolean b_Result;
    private String s_Result;

    public R1WhGrnFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r1_wh_grn, container, false);

        bt_r1_wh_grn_clear = (Button) view.findViewById(R.id.bt_r1_wh_grn_clear);
        bt_r1_wh_grn_save = (Button) view.findViewById(R.id.bt_r1_wh_grn_save);
        bt_r1_wh_grn_scan = (Button) view.findViewById(R.id.bt_r1_wh_grn_scan);
        et_r1_wh_grn_trfno = (EditText) view.findViewById(R.id.et_r1_wh_grn_trfno);
        lv_r1_wh_grn_details = (ListView) view.findViewById(R.id.lv_r1_wh_grn_details);
        et_r1_wh_grn_remarks = (EditText) view.findViewById(R.id.et_r1_wh_grn_remarks);
        tv_r1_wh_grn_trfno = (TextView) view.findViewById(R.id.tv_r1_wh_grn_trfno);
        tv_r1_wh_grn_result = (TextView) view.findViewById(R.id.tv_r1_wh_grn_result);

        ArrayList<R1WhGrnTicket> listLoadItems = objR1WhGrnControl.loadScannedTransfers();
        objMyR1WhGrnFragmentAdp = new R1WhGrnFragment.MyR1WhGrnFragmentAdp(listLoadItems);
        lv_r1_wh_grn_details.setAdapter(objMyR1WhGrnFragmentAdp);

        et_r1_wh_grn_trfno.setOnTouchListener(new View.OnTouchListener() {
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

        et_r1_wh_grn_trfno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String trfno = objControls.replaceString(et_r1_wh_grn_trfno.getText().toString()).toUpperCase();
                    b_Result = objR1WhGrnControl.validateTransferScanWarehouse(trfno);
                    if (!b_Result) {
                        okMessage("R1 WH Grn", objGlobal.getErrorMessage());
                        et_r1_wh_grn_trfno.setText("");
                        et_r1_wh_grn_trfno.requestFocus();
                        return true;
                    } else {
                        ArrayList<R1WhGrnTicket> listLoadItems = objR1WhGrnControl.loadScannedTransfers();
                        objMyR1WhGrnFragmentAdp = new R1WhGrnFragment.MyR1WhGrnFragmentAdp(listLoadItems);
                        tv_r1_wh_grn_trfno.setText(trfno);
                        tv_r1_wh_grn_result.setText(objR1WhGrnGlobal.getPalletStatus());
                        lv_r1_wh_grn_details.setAdapter(objMyR1WhGrnFragmentAdp);
                        et_r1_wh_grn_trfno.setText("");
                        et_r1_wh_grn_trfno.requestFocus();
                    }
                }
                return false;
            }
        });

        bt_r1_wh_grn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trfno = objControls.replaceString(et_r1_wh_grn_trfno.getText().toString()).toUpperCase();
                b_Result = objR1WhGrnControl.validateTransferScanWarehouse(trfno);
                if (!b_Result) {
                    okMessage("R1 WH Grn", objGlobal.getErrorMessage());
                    et_r1_wh_grn_trfno.setText("");
                    et_r1_wh_grn_trfno.requestFocus();
                } else {
                    ArrayList<R1WhGrnTicket> listLoadItems = objR1WhGrnControl.loadScannedTransfers();
                    objMyR1WhGrnFragmentAdp = new R1WhGrnFragment.MyR1WhGrnFragmentAdp(listLoadItems);
                    lv_r1_wh_grn_details.setAdapter(objMyR1WhGrnFragmentAdp);
                    et_r1_wh_grn_trfno.setText("");
                    et_r1_wh_grn_trfno.requestFocus();
                }
            }
        });

        bt_r1_wh_grn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objR1WhGrnControl.clearTable();
                                if (b_Result) {
                                    ArrayList<R1WhGrnTicket> listLoadItems = objR1WhGrnControl.loadScannedTransfers();
                                    objMyR1WhGrnFragmentAdp = new R1WhGrnFragment.MyR1WhGrnFragmentAdp(listLoadItems);
                                    lv_r1_wh_grn_details.setAdapter(objMyR1WhGrnFragmentAdp);
                                }
                                et_r1_wh_grn_remarks.setText("");
                                et_r1_wh_grn_trfno.setText("");
                                et_r1_wh_grn_trfno.requestFocus();
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

        bt_r1_wh_grn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarks = et_r1_wh_grn_remarks.getText().toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objR1WhGrnControl.saveR1WhGrn(remarks);
                                if (!b_Result) {
                                    okMessage("R1 WH Grn", objGlobal.getErrorMessage());
                                } else {
                                    if (!objR1WhGrnControl.clearTable()) {
                                        okMessage("R1 WH Grn", objGlobal.getErrorMessage());
                                    } else {
                                        ArrayList<R1WhGrnTicket> listLoadItems = objR1WhGrnControl.loadScannedTransfers();
                                        objMyR1WhGrnFragmentAdp = new R1WhGrnFragment.MyR1WhGrnFragmentAdp(listLoadItems);
                                        lv_r1_wh_grn_details.setAdapter(objMyR1WhGrnFragmentAdp);
                                        et_r1_wh_grn_remarks.setText("");
                                        et_r1_wh_grn_trfno.setText("");
                                        tv_r1_wh_grn_result.setText("");
                                        tv_r1_wh_grn_trfno.setText("");
                                        et_r1_wh_grn_trfno.requestFocus();
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
        et_r1_wh_grn_trfno.requestFocus();
        return view;
    }

    private class MyR1WhGrnFragmentAdp extends BaseAdapter {
        public ArrayList<R1WhGrnTicket> listR1WhGrnTicketTicket;

        public MyR1WhGrnFragmentAdp(ArrayList<R1WhGrnTicket> listR1WhGrnTicketTicket) {
            this.listR1WhGrnTicketTicket = listR1WhGrnTicketTicket;
        }

        @Override
        public int getCount() {
            return listR1WhGrnTicketTicket.size();
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
            View myView = mInflater.inflate(R.layout.r1_wh_grn_ticket, null);
            final R1WhGrnTicket s = listR1WhGrnTicketTicket.get(position);
            TextView tv_r1_wh_grn_ticket_trfno = (TextView) myView.findViewById(R.id.tv_r1_wh_grn_ticket_trfno);
            tv_r1_wh_grn_ticket_trfno.setText(String.valueOf(s.trfNo));
            TextView tv_r1_wh_grn_ticket_toteid = (TextView) myView.findViewById(R.id.tv_r1_wh_grn_ticket_toteid);
            tv_r1_wh_grn_ticket_toteid.setText(String.valueOf(s.storeIssue));
            TextView tv_r1_wh_grn_ticket_status = (TextView) myView.findViewById(R.id.tv_r1_wh_grn_ticket_status);
            tv_r1_wh_grn_ticket_status.setText(String.valueOf(s.pltStatus));
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