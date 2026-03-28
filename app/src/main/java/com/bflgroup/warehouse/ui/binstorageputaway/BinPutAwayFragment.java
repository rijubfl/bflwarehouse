package com.bflgroup.warehouse.ui.binstorageputaway;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;


public class BinPutAwayFragment extends Fragment {

    private EditText et_bin_put_away_toteid;
    private TextView tv_bin_put_away_boxno;
    private EditText et_bin_put_away_binlocation;
    private TextView tv_bin_put_away_doublebeep;
    private TextView tv_bin_put_away_warehouse;
    private Spinner sp_bin_put_away_inout;
    private Button bt_bin_put_away_save;
    private Button bt_bin_put_away_clear;
    private ListView lv_bin_put_away_details;
    private ListView lv_bin_put_away_history;

    private ProgressBar pr_bin_put_away;

    private boolean b_Result;
    private String s_Result;

    Global objGlobal = Global.getInstance();
    Controls objControls = new Controls();
    BinPutAwayGlobal objBinPutAwayGlobal = BinPutAwayGlobal.getInstance();
    BinPutAwayControl objBinPutAwayControl = new BinPutAwayControl();
    BinPutAwayFragment.MyBinPutAwayPendingToteAdp objMyBinPutAwayPendingToteAdp;
    BinPutAwayFragment.MyBinPutAwayHistoryAdp objMyBinPutAwayHistoryAdp;

    public BinPutAwayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_put_away, container, false);

        et_bin_put_away_toteid = (EditText) view.findViewById(R.id.et_bin_put_away_toteid);
        tv_bin_put_away_boxno = (TextView) view.findViewById(R.id.tv_bin_put_away_boxno);
        tv_bin_put_away_warehouse = (TextView) view.findViewById(R.id.tv_bin_put_away_warehouse);
        et_bin_put_away_binlocation = (EditText) view.findViewById(R.id.et_bin_put_away_binlocation);
        tv_bin_put_away_doublebeep = (TextView) view.findViewById(R.id.tv_bin_put_away_doublebeep);
        sp_bin_put_away_inout = (Spinner) view.findViewById(R.id.sp_bin_put_away_inout);
        bt_bin_put_away_save = (Button) view.findViewById(R.id.bt_bin_put_away_save);
        bt_bin_put_away_clear = (Button) view.findViewById(R.id.bt_bin_put_away_clear);
        lv_bin_put_away_details = (ListView) view.findViewById(R.id.lv_bin_put_away_details);
        lv_bin_put_away_history = (ListView) view.findViewById(R.id.lv_bin_put_away_history);
        pr_bin_put_away = (ProgressBar) view.findViewById(R.id.pr_bin_put_away);

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("IN");
        arr.add("OUT");
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_bin_put_away_inout.setAdapter(arrayAdp);

        clearAll();
        et_bin_put_away_toteid.requestFocus();
        tv_bin_put_away_warehouse.setText(objGlobal.getWarehouse());

        et_bin_put_away_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_bin_put_away_binlocation.setOnTouchListener(new View.OnTouchListener() {
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

        et_bin_put_away_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String toteId = et_bin_put_away_toteid.getText().toString().trim().toUpperCase();
                    toteId=objControls.replaceString(toteId);
                    b_Result = objBinPutAwayControl.validateToteid(sp_bin_put_away_inout.getSelectedItem().toString(), toteId);
                    if (!b_Result) {
                        okMessage("BinPutAwayFragment:et_bin_put_away_toteid", objGlobal.getErrorMessage());
                        vibrate(250);
                        tv_bin_put_away_boxno.setText("");
                        et_bin_put_away_toteid.setText("");
                        et_bin_put_away_toteid.requestFocus();
                    } else {
                        et_bin_put_away_toteid.setText(objControls.replaceString(toteId));
                        tv_bin_put_away_boxno.setText(objControls.replaceString(objBinPutAwayGlobal.getBoxNo()));
                        et_bin_put_away_binlocation.requestFocus();
                    }
                }
                return false;
            }
        });

        et_bin_put_away_binlocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String location = et_bin_put_away_binlocation.getText().toString().trim().toUpperCase();
                    String toteId=et_bin_put_away_toteid.getText().toString().trim().toUpperCase();
                    location=objControls.replaceString(location);
                    toteId=objControls.replaceString(toteId);
                    b_Result = objBinPutAwayControl.validateLocation(location, sp_bin_put_away_inout.getSelectedItem().toString(),toteId,
                            tv_bin_put_away_boxno.getText().toString());
                    if (!b_Result) {
                        okMessage("BinPutAwayFragment:et_bin_put_away_binlocation", objGlobal.getErrorMessage());
                        vibrate(250);
                        tv_bin_put_away_doublebeep.setText("");
                        et_bin_put_away_binlocation.setText("");
                        et_bin_put_away_binlocation.requestFocus();
                    } else {
                        tv_bin_put_away_doublebeep.setText(objBinPutAwayGlobal.getDoubleDeep());
                        et_bin_put_away_binlocation.setText(objControls.replaceString(location));
                        bt_bin_put_away_save.requestFocus();
                    }
                }
                return false;
            }
        });

        bt_bin_put_away_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = clearAll();
                                et_bin_put_away_toteid.requestFocus();
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

        bt_bin_put_away_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String direction = sp_bin_put_away_inout.getSelectedItem().toString().trim();
                                String toteId = et_bin_put_away_toteid.getText().toString().trim();
                                String boxNo = tv_bin_put_away_boxno.getText().toString().trim();
                                String location = et_bin_put_away_binlocation.getText().toString().trim();
                                String dBeep = tv_bin_put_away_doublebeep.getText().toString().trim();
                                b_Result = objBinPutAwayControl.saveBinInOutSingle(objControls.replaceString(toteId), boxNo, direction, objControls.replaceString(location),dBeep,"");
                                if (b_Result) {
                                    clearAll();
                                    et_bin_put_away_toteid.requestFocus();
                                } else {
                                    okMessage("BinPutAwayFragment",objGlobal.getErrorMessage());
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

    private class MyBinPutAwayPendingToteAdp extends BaseAdapter {
        public ArrayList<BinPutAwayPendingToteIdTicket> listBinPutAwayPendingToteTicket;

        public MyBinPutAwayPendingToteAdp(ArrayList<BinPutAwayPendingToteIdTicket> listBinPutAwayPendingToteTicket) {
            this.listBinPutAwayPendingToteTicket = listBinPutAwayPendingToteTicket;
        }

        @Override
        public int getCount() {
            return listBinPutAwayPendingToteTicket.size();
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
            View myView = mInflater.inflate(R.layout.bin_putaway_pending_totes_ticket, null);
            final BinPutAwayPendingToteIdTicket s = listBinPutAwayPendingToteTicket.get(position);

            TextView tv_bin_putaway_pending_ticket_batchid = (TextView) myView.findViewById(R.id.tv_bin_putaway_pending_ticket_batchid);
            tv_bin_putaway_pending_ticket_batchid.setText(String.valueOf(s.batchId));

            TextView tv_bin_putaway_pending_ticket_toteid = (TextView) myView.findViewById(R.id.tv_bin_putaway_pending_ticket_toteid);
            tv_bin_putaway_pending_ticket_toteid.setText(String.valueOf(s.toteId));

            TextView tv_bin_putaway_pending_ticket_boxno = (TextView) myView.findViewById(R.id.tv_bin_putaway_pending_ticket_boxno);
            tv_bin_putaway_pending_ticket_boxno.setText(String.valueOf(s.boxNo));

            TextView tv_bin_putaway_pending_ticket_time = (TextView) myView.findViewById(R.id.tv_bin_putaway_pending_ticket_time);
            tv_bin_putaway_pending_ticket_time.setText(String.valueOf(s.stime));

            TextView tv_bin_putaway_pending_ticket_boxremarks = (TextView) myView.findViewById(R.id.tv_bin_putaway_pending_ticket_boxremarks);
            tv_bin_putaway_pending_ticket_boxremarks.setText(String.valueOf(s.remarks));

            return myView;
        }
    }

    private class MyBinPutAwayHistoryAdp extends BaseAdapter {
        public ArrayList<BinPutAwayHistoryTicket> listBinPutAwayHistoryTicket;

        public MyBinPutAwayHistoryAdp(ArrayList<BinPutAwayHistoryTicket> listBinPutAwayHistoryTicket) {
            this.listBinPutAwayHistoryTicket = listBinPutAwayHistoryTicket;
        }

        @Override
        public int getCount() {
            return listBinPutAwayHistoryTicket.size();
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
            View myView = mInflater.inflate(R.layout.bin_putaway_history_ticket, null);
            final BinPutAwayHistoryTicket s = listBinPutAwayHistoryTicket.get(position);

            TextView tv_bin_putaway_history_ticket_toteid = (TextView) myView.findViewById(R.id.tv_bin_putaway_history_ticket_toteid);
            tv_bin_putaway_history_ticket_toteid.setText(String.valueOf(s.toteId));

            TextView tv_bin_putaway_history_ticket_boxno = (TextView) myView.findViewById(R.id.tv_bin_putaway_history_ticket_boxno);
            tv_bin_putaway_history_ticket_boxno.setText(String.valueOf(s.boxNo));

            TextView tv_bin_putaway_history_ticket_direction = (TextView) myView.findViewById(R.id.tv_bin_putaway_history_ticket_direction);
            tv_bin_putaway_history_ticket_direction.setText(String.valueOf(s.inout));

            TextView tv_bin_putaway_history_ticket_location = (TextView) myView.findViewById(R.id.tv_bin_putaway_history_ticket_location);
            tv_bin_putaway_history_ticket_location.setText(String.valueOf(s.location));

            TextView tv_bin_putaway_history_ticket_time = (TextView) myView.findViewById(R.id.tv_bin_putaway_history_ticket_time);
            tv_bin_putaway_history_ticket_time.setText(String.valueOf(s.time));

            return myView;
        }
    }

    boolean clearAll(){
        try {
            ArrayList<BinPutAwayPendingToteIdTicket> listBinBatchInScanToteTicket = objBinPutAwayControl.loadBinPutAwayPendingToteIdTicket();
            objMyBinPutAwayPendingToteAdp = new BinPutAwayFragment.MyBinPutAwayPendingToteAdp(listBinBatchInScanToteTicket);
            lv_bin_put_away_details.setAdapter(objMyBinPutAwayPendingToteAdp);

            ArrayList<BinPutAwayHistoryTicket> listBinPutAwayHistoryTicket = objBinPutAwayControl.loadBinPutAwayUserHistory();
            objMyBinPutAwayHistoryAdp = new BinPutAwayFragment.MyBinPutAwayHistoryAdp(listBinPutAwayHistoryTicket);
            lv_bin_put_away_history.setAdapter(objMyBinPutAwayHistoryAdp);

            et_bin_put_away_toteid.setText("");
            tv_bin_put_away_boxno.setText("");
            et_bin_put_away_binlocation.setText("");
            tv_bin_put_away_doublebeep.setText("");
        }  catch(Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
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