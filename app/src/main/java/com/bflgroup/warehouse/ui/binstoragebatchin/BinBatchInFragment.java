package com.bflgroup.warehouse.ui.binstoragebatchin;

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

import java.util.ArrayList;
import java.util.List;

public class BinBatchInFragment extends Fragment {

    private EditText et_bin_batch_in_toteid;
    private Button bt_bin_batch_in_scan;
    private ListView lv_bin_batch_in_details;
    private Button bt_bin_batch_in_clear;
    private Button bt_bin_batch_in_save;
    private TextView tv_bin_batch_in_warehouse;
    private TextView tv_bin_batch_in_palletno;
    private Spinner sp_bin_batch_in_zone;

    Global objGlobal = Global.getInstance();
    BinBatchInGlobal objBinBatchInGlobal = BinBatchInGlobal.getInstance();
    Controls objControls = new Controls();
    BinBatchInControl objBinBatchInControl = new BinBatchInControl();
    MyBinBatchInScanToteIdAdp objMyBinBatchInScanToteIdAdp;
    BinBatchInShared saredRef;

    private boolean b_Result;
    private String s_Result;
    Boolean strflg = false;

    public BinBatchInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_batch_in, container, false);

        et_bin_batch_in_toteid = (EditText) view.findViewById(R.id.et_bin_batch_in_toteid);
        bt_bin_batch_in_scan = (Button) view.findViewById(R.id.bt_bin_batch_in_scan);
        lv_bin_batch_in_details = (ListView) view.findViewById(R.id.lv_bin_batch_in_details);
        bt_bin_batch_in_clear = (Button) view.findViewById(R.id.bt_bin_batch_in_clear);
        bt_bin_batch_in_save = (Button) view.findViewById(R.id.bt_bin_batch_in_save);
        tv_bin_batch_in_warehouse = (TextView) view.findViewById(R.id.tv_bin_batch_in_warehouse);
        sp_bin_batch_in_zone = (Spinner) view.findViewById(R.id.sp_bin_batch_in_zone);
        tv_bin_batch_in_palletno = (TextView) view.findViewById(R.id.tv_bin_batch_in_palletno);
        saredRef = new BinBatchInShared(getContext());

        if (saredRef.loadPltNo() != "") {
            tv_bin_batch_in_palletno.setText(saredRef.loadPltNo());
        }

        et_bin_batch_in_toteid.requestFocus();
        tv_bin_batch_in_warehouse.setText(objGlobal.getWarehouse());

        List<String> arr = objBinBatchInControl.loadZone();
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_bin_batch_in_zone.setAdapter(arrayAdp);

        ArrayList<BinBatchInScanToteTicket> listBinScanToteId = objBinBatchInControl.loadBinScanToteId();
        objMyBinBatchInScanToteIdAdp = new MyBinBatchInScanToteIdAdp(listBinScanToteId);
        lv_bin_batch_in_details.setAdapter(objMyBinBatchInScanToteIdAdp);

        et_bin_batch_in_toteid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //return objGlobal.getHideKeyPad();
                return true;
            }
        });

        et_bin_batch_in_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String toteId = et_bin_batch_in_toteid.getText().toString().trim().toUpperCase();
                    toteId = objControls.replaceString(toteId);
                    b_Result = objBinBatchInControl.validateScanTote(toteId, tv_bin_batch_in_palletno.getText().toString().trim());
                    if (b_Result == false) {
                        okMessage("BinBatchInFragment:et_bin_batch_in_toteid", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_bin_batch_in_toteid.setText("");
                        et_bin_batch_in_toteid.requestFocus();
                        return false;
                    } else {
                        ArrayList<BinBatchInScanToteTicket> listBinScanToteId = objBinBatchInControl.loadBinScanToteId();
                        objMyBinBatchInScanToteIdAdp = new MyBinBatchInScanToteIdAdp(listBinScanToteId);
                        lv_bin_batch_in_details.setAdapter(objMyBinBatchInScanToteIdAdp);
                        tv_bin_batch_in_palletno.setText(objBinBatchInGlobal.getPalletno());
                        saredRef.savePltNo(objBinBatchInGlobal.getPalletno());
                        et_bin_batch_in_toteid.setText("");
                        et_bin_batch_in_toteid.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        bt_bin_batch_in_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objBinBatchInControl.validateScanTote(et_bin_batch_in_toteid.getText().toString().toUpperCase(),
                        tv_bin_batch_in_palletno.getText().toString());
                if (b_Result == false) {
                    okMessage("BinBatchInFragment:bt_bin_batch_in_scan", objGlobal.getErrorMessage());
                    vibrate(500);
                } else {
                    ArrayList<BinBatchInScanToteTicket> listBinScanToteId = objBinBatchInControl.loadBinScanToteId();
                    objMyBinBatchInScanToteIdAdp = new MyBinBatchInScanToteIdAdp(listBinScanToteId);
                    lv_bin_batch_in_details.setAdapter(objMyBinBatchInScanToteIdAdp);
                    tv_bin_batch_in_palletno.setText(objBinBatchInGlobal.getPalletno());
                    saredRef.savePltNo(objBinBatchInGlobal.getPalletno());
                }
                et_bin_batch_in_toteid.setText("");
                et_bin_batch_in_toteid.requestFocus();
            }
        });

        bt_bin_batch_in_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = clearAll();
                                if (b_Result) {
                                    ArrayList<BinBatchInScanToteTicket> listBinScanToteId = objBinBatchInControl.loadBinScanToteId();
                                    objMyBinBatchInScanToteIdAdp = new MyBinBatchInScanToteIdAdp(listBinScanToteId);
                                    lv_bin_batch_in_details.setAdapter(objMyBinBatchInScanToteIdAdp);
                                }
                                saredRef.savePltNo("");
                                et_bin_batch_in_toteid.setText("");
                                tv_bin_batch_in_palletno.setText("");
                                et_bin_batch_in_toteid.requestFocus();
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

        bt_bin_batch_in_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objBinBatchInControl.saveBatchIn(tv_bin_batch_in_palletno.getText().toString());
                                if (!b_Result) {
                                    okMessage("bt_bin_batch_in_save", objGlobal.getErrorMessage());
                                } else {
                                    b_Result = clearAll();
                                    if (!b_Result) {

                                    } else {
                                        saredRef.savePltNo("");
                                        et_bin_batch_in_toteid.setText("");
                                        tv_bin_batch_in_palletno.setText("");
                                        et_bin_batch_in_toteid.requestFocus();
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

        return view;
    }

    boolean clearAll() {
        try {
            b_Result = objBinBatchInControl.clearTable();
            if (b_Result == false) {
                okMessage("BinBatchInFragment:clearAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            } else {
                ArrayList<BinBatchInScanToteTicket> listBinScanToteId = objBinBatchInControl.loadBinScanToteId();
                objMyBinBatchInScanToteIdAdp = new MyBinBatchInScanToteIdAdp(listBinScanToteId);
                lv_bin_batch_in_details.setAdapter(objMyBinBatchInScanToteIdAdp);
                et_bin_batch_in_toteid.setText("");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    private class MyBinBatchInScanToteIdAdp extends BaseAdapter {
        public ArrayList<BinBatchInScanToteTicket> listBinBatchInScanToteTicket;

        public MyBinBatchInScanToteIdAdp(ArrayList<BinBatchInScanToteTicket> listBinBatchInScanToteTicket) {
            this.listBinBatchInScanToteTicket = listBinBatchInScanToteTicket;
        }

        @Override
        public int getCount() {
            return listBinBatchInScanToteTicket.size();
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
            View myView = mInflater.inflate(R.layout.bin_batch_in_ticket, null);
            final BinBatchInScanToteTicket s = listBinBatchInScanToteTicket.get(position);
            TextView tv_bin_batch_in_tickte_toteid = (TextView) myView.findViewById(R.id.tv_bin_batch_in_tickte_toteid);
            tv_bin_batch_in_tickte_toteid.setText(String.valueOf(s.toteId));
            TextView tv_bin_batch_in_tickte_boxno = (TextView) myView.findViewById(R.id.tv_bin_batch_in_tickte_boxno);
            tv_bin_batch_in_tickte_boxno.setText(String.valueOf(s.boxNo));
            TextView tv_bin_batch_in_tickte_scantime = (TextView) myView.findViewById(R.id.tv_bin_batch_in_tickte_scantime);
            tv_bin_batch_in_tickte_scantime.setText(String.valueOf(s.stime));
            TextView tv_bin_batch_in_tickte_boxremarks = (TextView) myView.findViewById(R.id.tv_bin_batch_in_tickte_boxremarks);
            tv_bin_batch_in_tickte_boxremarks.setText(String.valueOf(s.remarks));
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