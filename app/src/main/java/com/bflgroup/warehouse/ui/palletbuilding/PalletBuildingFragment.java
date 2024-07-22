package com.bflgroup.warehouse.ui.palletbuilding;

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;

public class PalletBuildingFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    Controls objControls = new Controls();
    private PalletBuildingGlobal objPalletBuildingGlobal = PalletBuildingGlobal.getInstance();
    private PalletBuildingControl objPalletBuildingControl = new PalletBuildingControl();

    PalletBuildingFragment.MyPalletBuildingBoxTicketAdp objMyPalletBuildingBoxTicketAdp;

    private EditText et_pallet_building_box_toteid;
    private EditText et_pallet_building_remarks;
    private Button bt_pallet_building_scan;
    private TextView tv_pallet_building_lastsave;
    private Button bt_pallet_building_clear;
    private Button bt_pallet_building_save;
    private ListView lv_pallet_building_details;
    private RadioButton rb_pallet_building_usa_category;
    private RadioButton rb_pallet_building_tcm_category;
    private TextView tv_pallet_building_tot_count;
    private TextView tv_pallet_building_tot_qty;

    private boolean b_Result;
    private String s_Result;

    public PalletBuildingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pallet_building, container, false);
        et_pallet_building_box_toteid = (EditText) view.findViewById(R.id.et_pallet_building_box_toteid);
        et_pallet_building_remarks = (EditText) view.findViewById(R.id.et_pallet_building_remarks);
        bt_pallet_building_scan = (Button) view.findViewById(R.id.bt_pallet_building_scan);
        tv_pallet_building_lastsave = (TextView) view.findViewById(R.id.tv_pallet_building_lastsave);
        bt_pallet_building_clear = (Button) view.findViewById(R.id.bt_pallet_building_clear);
        bt_pallet_building_save = (Button) view.findViewById(R.id.bt_pallet_building_save);
        lv_pallet_building_details = (ListView) view.findViewById(R.id.lv_pallet_building_details);
        rb_pallet_building_tcm_category = (RadioButton) view.findViewById(R.id.rb_pallet_building_tcm_category);
        rb_pallet_building_usa_category = (RadioButton) view.findViewById(R.id.rb_pallet_building_usa_category);
        tv_pallet_building_tot_count = (TextView) view.findViewById(R.id.tv_pallet_building_tot_count);
        tv_pallet_building_tot_qty = (TextView) view.findViewById(R.id.tv_pallet_building_tot_qty);

        ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket = objPalletBuildingControl.loadPalletBuildBoxDetail();
        objMyPalletBuildingBoxTicketAdp = new PalletBuildingFragment.MyPalletBuildingBoxTicketAdp(listPalletBuildingBoxTicket);
        lv_pallet_building_details.setAdapter(objMyPalletBuildingBoxTicketAdp);
        tv_pallet_building_tot_count.setText(String.valueOf(objPalletBuildingGlobal.getTotCnt()));
        tv_pallet_building_tot_qty.setText(String.valueOf(objPalletBuildingGlobal.getTotQty()));

        et_pallet_building_box_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_pallet_building_box_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String toteId = et_pallet_building_box_toteid.getText().toString().trim().toUpperCase();
                    toteId = objControls.replaceString(toteId);
                    if (rb_pallet_building_tcm_category.isChecked()) {

                    }
                    if (rb_pallet_building_usa_category.isChecked()) {
                        b_Result = objPalletBuildingControl.validateBoxTotUsa(toteId);
                    }
                    if (b_Result == false) {
                        okMessage("BinBatchInFragment:et_bin_batch_in_toteid", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_pallet_building_box_toteid.setText("");
                        et_pallet_building_box_toteid.requestFocus();
                        return false;
                    } else {
                        ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket = objPalletBuildingControl.loadPalletBuildBoxDetail();
                        objMyPalletBuildingBoxTicketAdp = new PalletBuildingFragment.MyPalletBuildingBoxTicketAdp(listPalletBuildingBoxTicket);
                        lv_pallet_building_details.setAdapter(objMyPalletBuildingBoxTicketAdp);
                        tv_pallet_building_tot_count.setText(String.valueOf(objPalletBuildingGlobal.getTotCnt()));
                        tv_pallet_building_tot_qty.setText(String.valueOf(objPalletBuildingGlobal.getTotQty()));
                        et_pallet_building_box_toteid.setText("");
                        et_pallet_building_box_toteid.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        bt_pallet_building_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toteId = et_pallet_building_box_toteid.getText().toString().trim().toUpperCase();
                toteId = objControls.replaceString(toteId);
                if (rb_pallet_building_tcm_category.isChecked()) {

                }
                if (rb_pallet_building_usa_category.isChecked()) {
                    b_Result = objPalletBuildingControl.validateBoxTotUsa(toteId);
                }
                if (b_Result == false) {
                    okMessage("BinBatchInFragment:bt_pallet_building_scan", objGlobal.getErrorMessage());
                    vibrate(500);
                    et_pallet_building_box_toteid.setText("");
                    et_pallet_building_box_toteid.requestFocus();
                } else {
                    ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket = objPalletBuildingControl.loadPalletBuildBoxDetail();
                    objMyPalletBuildingBoxTicketAdp = new PalletBuildingFragment.MyPalletBuildingBoxTicketAdp(listPalletBuildingBoxTicket);
                    lv_pallet_building_details.setAdapter(objMyPalletBuildingBoxTicketAdp);
                    tv_pallet_building_tot_count.setText(String.valueOf(objPalletBuildingGlobal.getTotCnt()));
                    tv_pallet_building_tot_qty.setText(String.valueOf(objPalletBuildingGlobal.getTotQty()));
                    et_pallet_building_box_toteid.setText("");
                    et_pallet_building_box_toteid.requestFocus();
                }
            }
        });

        bt_pallet_building_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b_Result = objPalletBuildingControl.validateMainUsa();
                if (!b_Result) {
                    okMessage("bt_bin_batch_in_save11", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (rb_pallet_building_tcm_category.isChecked()) {
                                    }
                                    if (rb_pallet_building_usa_category.isChecked()) {
                                        b_Result = objPalletBuildingControl.savePalletUsa(et_pallet_building_remarks.getText().toString().trim().toUpperCase());
                                    }
                                    if (!b_Result) {
                                        okMessage("bt_bin_batch_in_save", objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = clearAll();
                                        if (!b_Result) {
                                            okMessage("bt_bin_batch_in_save:clearAll", objGlobal.getErrorMessage());
                                        } else {
                                            tv_pallet_building_lastsave.setText(objPalletBuildingGlobal.getPalletNo());
                                            et_pallet_building_box_toteid.requestFocus();
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
            }
        });


        bt_pallet_building_clear.setOnClickListener(new View.OnClickListener(){
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
                                if(!b_Result){
                                    okMessage("PalletBuildingFragment:bt_pallet_building_clear", objGlobal.getErrorMessage());
                                    vibrate(500);
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
            b_Result=objPalletBuildingControl.clearTable();
            if(b_Result==false){
                okMessage("PalletBuildingFragment:clearAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            } else {
                ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket = objPalletBuildingControl.loadPalletBuildBoxDetail();
                objMyPalletBuildingBoxTicketAdp = new PalletBuildingFragment.MyPalletBuildingBoxTicketAdp(listPalletBuildingBoxTicket);
                lv_pallet_building_details.setAdapter(objMyPalletBuildingBoxTicketAdp);
                tv_pallet_building_tot_count.setText(String.valueOf(objPalletBuildingGlobal.getTotCnt()));
                tv_pallet_building_tot_qty.setText(String.valueOf(objPalletBuildingGlobal.getTotQty()));
                et_pallet_building_remarks.setText("");
                et_pallet_building_box_toteid.setText("");
            }
        }  catch(Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    private class MyPalletBuildingBoxTicketAdp extends BaseAdapter {
        public ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket;

        public MyPalletBuildingBoxTicketAdp(ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket) {
            this.listPalletBuildingBoxTicket = listPalletBuildingBoxTicket;
        }

        @Override
        public int getCount() {
            return listPalletBuildingBoxTicket.size();
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
            View myView = mInflater.inflate(R.layout.pallet_building_item_ticket, null);
            final PalletBuildingBoxTicket s = listPalletBuildingBoxTicket.get(position);

            TextView tv_building_pallet_ticket_toteid = (TextView) myView.findViewById(R.id.tv_building_pallet_ticket_toteid);
            tv_building_pallet_ticket_toteid.setText(String.valueOf(s.toteId));

            TextView tv_building_pallet_ticket_boxno = (TextView) myView.findViewById(R.id.tv_building_pallet_ticket_boxno);
            tv_building_pallet_ticket_boxno.setText(String.valueOf(s.boxNo));

            TextView tv_building_pallet_ticket_pallettype = (TextView) myView.findViewById(R.id.tv_building_pallet_ticket_pallettype);
            tv_building_pallet_ticket_pallettype.setText(String.valueOf(s.pallettype));

            TextView tv_building_pallet_ticket_boxremarks = (TextView) myView.findViewById(R.id.tv_building_pallet_ticket_boxremarks);
            tv_building_pallet_ticket_boxremarks.setText(String.valueOf(s.boxRemarks));

            TextView tv_building_pallet_ticket_qty = (TextView) myView.findViewById(R.id.tv_building_pallet_ticket_qty);
            tv_building_pallet_ticket_qty.setText(String.valueOf(s.qty));
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