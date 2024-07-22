package com.bflgroup.warehouse.ui.binstorageputawaymultipletote;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class BinPutAwayMultipleToteFragment extends Fragment {

    private Spinner sp_bin_put_away_multiple_inout;
    private Spinner tv_bin_put_away_multiple_warehouse;
    private EditText et_bin_put_away_multiple_binlocation;
    private EditText et_bin_put_away_multiple_toteid;
    private ListView lv_bin_put_away_multiple_history;
    private Button bt_bin_put_away_multiple_clear;
    private Button bt_bin_put_away_multiple_save;
    private TextView tv_wh_grn_total_plt_count;

    BinPutAwayMultipleTotePendingSaveAdp objBinPutAwayMultipleTotePendingSaveAdp;
    private BinPutAwayMultipleToteGlobal objBinPutAwayMultipleToteGlobal = BinPutAwayMultipleToteGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;

    BinPutAwayMultipleToteSaredRef objBinPutAwayMultipleToteSaredRef;

    Global objGlobal = Global.getInstance();
    Controls objControls = new Controls();

    BinPutAwayMultipleToteControl objBinPutAwayMultipleToteControl = new BinPutAwayMultipleToteControl();

    public BinPutAwayMultipleToteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_put_away_multiple_tote, container, false);

            sp_bin_put_away_multiple_inout = (Spinner) view.findViewById(R.id.sp_bin_put_away_multiple_inout);
            //tv_bin_put_away_multiple_warehouse = (Spinner) view.findViewById(R.id.sp_bin_put_away_multiple_warehouse);
            et_bin_put_away_multiple_binlocation = (EditText) view.findViewById(R.id.et_bin_put_away_multiple_binlocation);
            et_bin_put_away_multiple_toteid = (EditText) view.findViewById(R.id.et_bin_put_away_multiple_toteid);
            lv_bin_put_away_multiple_history = (ListView) view.findViewById(R.id.lv_bin_put_away_multiple_history);
            bt_bin_put_away_multiple_clear = (Button) view.findViewById(R.id.bt_bin_put_away_multiple_clear);
            bt_bin_put_away_multiple_save = (Button) view.findViewById(R.id.bt_bin_put_away_multiple_save);
            tv_wh_grn_total_plt_count = (TextView) view.findViewById(R.id.tv_wh_grn_total_plt_count);

            objBinPutAwayMultipleToteSaredRef = new BinPutAwayMultipleToteSaredRef(getContext());

            List<String> arr;
            arr = new ArrayList<String>();
            arr.add("IN");
            arr.add("OUT");
        try {
            List<String> location;
            location = new ArrayList<String>();
            String warehouse = objGlobal.getWarehouse();
            // warehouse = "YOTO";
            location.add(warehouse);

            ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
            sp_bin_put_away_multiple_inout.setAdapter(arrayAdp);

            et_bin_put_away_multiple_binlocation.requestFocus();

            if (objGlobal.getWarehouse().equals("TECHNO")) {
                location.add("TECHNO-E");
            }
            ArrayAdapter<String> array = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, location);
            tv_bin_put_away_multiple_warehouse.setAdapter(array);
        } catch(Exception e){
            okMessage("",e.toString());
        }

            // tv_bin_put_away_multiple_warehouse.setText(objGlobal.getWarehouse());

            if (objBinPutAwayMultipleToteSaredRef.loadInOrOut() != "") {
                if (objBinPutAwayMultipleToteSaredRef.loadInOrOut().equals("IN")) {
                    sp_bin_put_away_multiple_inout.setSelection(0);
                } else {
                    sp_bin_put_away_multiple_inout.setSelection(1);
                }
                sp_bin_put_away_multiple_inout.setEnabled(false);
            }
            if (objBinPutAwayMultipleToteSaredRef.loadLocation() != "") {
                et_bin_put_away_multiple_binlocation.setText(objBinPutAwayMultipleToteSaredRef.loadLocation());
                et_bin_put_away_multiple_binlocation.setEnabled(false);
            }

            ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSaveTicket = objBinPutAwayMultipleToteControl.loadBinPutAwayMultipleTotePendingSave();
            objBinPutAwayMultipleTotePendingSaveAdp = new BinPutAwayMultipleTotePendingSaveAdp(listBinPutAwayMultipleTotePendingSaveTicket);
            lv_bin_put_away_multiple_history.setAdapter(objBinPutAwayMultipleTotePendingSaveAdp);
            tv_wh_grn_total_plt_count.setText(String.valueOf(objBinPutAwayMultipleToteGlobal.getScanCount()));

        et_bin_put_away_multiple_binlocation.setOnTouchListener(new View.OnTouchListener() {
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

        et_bin_put_away_multiple_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_bin_put_away_multiple_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String toteId = et_bin_put_away_multiple_toteid.getText().toString().trim().toUpperCase();
                    String location = et_bin_put_away_multiple_binlocation.getText().toString();
                    toteId = objControls.replaceString(toteId);
                    b_Result = objBinPutAwayMultipleToteControl.validateToteid(tv_bin_put_away_multiple_warehouse.getSelectedItem().toString(), sp_bin_put_away_multiple_inout.getSelectedItem().toString(), toteId, location);
                    if (!b_Result) {
                        okMessage("BinPutAwayMultipleToteFragment:et_bin_put_away_multiple_toteid", objGlobal.getErrorMessage());
                        vibrate(250);
                    }
                    ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSaveTicket = objBinPutAwayMultipleToteControl.loadBinPutAwayMultipleTotePendingSave();
                    objBinPutAwayMultipleTotePendingSaveAdp = new BinPutAwayMultipleTotePendingSaveAdp(listBinPutAwayMultipleTotePendingSaveTicket);
                    lv_bin_put_away_multiple_history.setAdapter(objBinPutAwayMultipleTotePendingSaveAdp);
                    tv_wh_grn_total_plt_count.setText(String.valueOf(objBinPutAwayMultipleToteGlobal.getScanCount()));
                    et_bin_put_away_multiple_toteid.setText("");
                    et_bin_put_away_multiple_toteid.requestFocus();
                }
                return false;
            }
        });

        et_bin_put_away_multiple_binlocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String location = et_bin_put_away_multiple_binlocation.getText().toString().trim().toUpperCase();
                    location = objControls.replaceString(location);
                    b_Result = objBinPutAwayMultipleToteControl.validateLocation(tv_bin_put_away_multiple_warehouse.getSelectedItem().toString(), location, sp_bin_put_away_multiple_inout.getSelectedItem().toString());
                    if (!b_Result) {
                        okMessage("BinPutAwayMultipleToteFragment:et_bin_put_away_multiple_binlocation", objGlobal.getErrorMessage());
                        vibrate(250);
                        et_bin_put_away_multiple_binlocation.setText("");
                        et_bin_put_away_multiple_binlocation.setEnabled(true);
                        sp_bin_put_away_multiple_inout.setEnabled(true);
                        et_bin_put_away_multiple_binlocation.requestFocus();
                    } else {
                        et_bin_put_away_multiple_binlocation.setText(objControls.replaceString(location));
                        et_bin_put_away_multiple_binlocation.setEnabled(false);
                        sp_bin_put_away_multiple_inout.setEnabled(false);
                        objBinPutAwayMultipleToteSaredRef.saveInOrOut(sp_bin_put_away_multiple_inout.getSelectedItem().toString());
                        objBinPutAwayMultipleToteSaredRef.saveLocation(et_bin_put_away_multiple_binlocation.getText().toString());
                        et_bin_put_away_multiple_toteid.requestFocus();
                    }
                }
                return false;
            }
        });

        bt_bin_put_away_multiple_clear.setOnClickListener(new View.OnClickListener() {
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
                                if (!b_Result) {
                                    okMessage("Rack In Out", objGlobal.getErrorMessage());
                                }
                                et_bin_put_away_multiple_binlocation.requestFocus();
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

        bt_bin_put_away_multiple_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objBinPutAwayMultipleToteControl.saveBinInOutMultiple(tv_bin_put_away_multiple_warehouse.getSelectedItem().toString(), sp_bin_put_away_multiple_inout.getSelectedItem().toString(), et_bin_put_away_multiple_binlocation.getText().toString(),"");
                                if (b_Result) {
                                    b_Result = clearAll();
                                    if (!b_Result) {
                                        okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
                                    }
                                    et_bin_put_away_multiple_binlocation.requestFocus();
                                } else {
                                    okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
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
            b_Result = objBinPutAwayMultipleToteControl.clearScaned();
            if(!b_Result){
                return false;
            }
            ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSaveTicket = objBinPutAwayMultipleToteControl.loadBinPutAwayMultipleTotePendingSave();
            objBinPutAwayMultipleTotePendingSaveAdp = new  BinPutAwayMultipleTotePendingSaveAdp(listBinPutAwayMultipleTotePendingSaveTicket);
            lv_bin_put_away_multiple_history.setAdapter(objBinPutAwayMultipleTotePendingSaveAdp);
            tv_wh_grn_total_plt_count.setText(String.valueOf(objBinPutAwayMultipleToteGlobal.getScanCount()));
            et_bin_put_away_multiple_toteid.setText("");
            et_bin_put_away_multiple_binlocation.setText("");
            objBinPutAwayMultipleToteSaredRef.saveInOrOut("");
            objBinPutAwayMultipleToteSaredRef.saveLocation("");
            et_bin_put_away_multiple_binlocation.setEnabled(true);
            sp_bin_put_away_multiple_inout.setEnabled(true);
        }  catch(Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    private class BinPutAwayMultipleTotePendingSaveAdp extends BaseAdapter {
        public ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSaveTicket;

        public BinPutAwayMultipleTotePendingSaveAdp(ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSaveTicket) {
            this.listBinPutAwayMultipleTotePendingSaveTicket = listBinPutAwayMultipleTotePendingSaveTicket;
        }

        @Override
        public int getCount() {
            return listBinPutAwayMultipleTotePendingSaveTicket.size();
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
            View myView = mInflater.inflate(R.layout.bin_put_away_multiple_tote_pending_save_ticket, null);
            final BinPutAwayMultipleTotePendingSaveTicket s = listBinPutAwayMultipleTotePendingSaveTicket.get(position);

            TextView tv_bin_putaway_multiple_tote_ticket_toteid = (TextView) myView.findViewById(R.id.tv_bin_putaway_multiple_tote_ticket_toteid);
            tv_bin_putaway_multiple_tote_ticket_toteid.setText(String.valueOf(s.toteId));

            TextView tv_bin_putaway_multiple_tote_ticket_boxno = (TextView) myView.findViewById(R.id.tv_bin_putaway_multiple_tote_ticket_boxno);
            tv_bin_putaway_multiple_tote_ticket_boxno.setText(String.valueOf(s.boxNoTrfNo));

            TextView tv_bin_putaway_multiple_tote_ticket_scantime = (TextView) myView.findViewById(R.id.tv_bin_putaway_multiple_tote_ticket_scantime);
            tv_bin_putaway_multiple_tote_ticket_scantime.setText(String.valueOf(s.dtTime));

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