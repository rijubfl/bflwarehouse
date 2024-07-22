package com.bflgroup.warehouse.ui.divisionseperate;

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
import com.bflgroup.warehouse.comm.SaredRef;

import java.util.ArrayList;
import java.util.List;

public class DivisionSeperationFragment extends Fragment {

    private Spinner sp_div_seperate_shopname;
    private EditText et_div_seperate_trfno;
    private EditText et_div_seperate_scan;
    private Button bt_div_seperate_clear;
    private Button bt_div_seperate_save;
    private ListView lv_div_seperate_details;

    Global objGlobal = Global.getInstance();
    Controls objControls = new Controls();
    DivisionSeperationControl objDivisionSeperationControl = new DivisionSeperationControl();
    MyDivisionSeperationItemTicketAdp objMyDivisionSeperationItemTicketAdp;
    DivisionSeperationShared saredRef;

    private boolean b_Result;
    private String s_Result;

    public DivisionSeperationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_division_seperation, container, false);

        sp_div_seperate_shopname = (Spinner) view.findViewById(R.id.sp_div_seperate_shopname);
        et_div_seperate_trfno = (EditText) view.findViewById(R.id.et_div_seperate_trfno);
        et_div_seperate_scan = (EditText) view.findViewById(R.id.et_div_seperate_scan);
        bt_div_seperate_clear = (Button) view.findViewById(R.id.bt_div_seperate_clear);
        bt_div_seperate_save = (Button) view.findViewById(R.id.bt_div_seperate_save);
        lv_div_seperate_details = (ListView) view.findViewById(R.id.lv_div_seperate_details);
        saredRef= new DivisionSeperationShared(getContext());

        List<String> arr = objDivisionSeperationControl.loadExportShops();
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_div_seperate_shopname.setAdapter(arrayAdp);

        ArrayList<DivisionSeperationItemTicket> listLoadItems = objDivisionSeperationControl.loadDivSepItems();
        objMyDivisionSeperationItemTicketAdp = new DivisionSeperationFragment.MyDivisionSeperationItemTicketAdp(listLoadItems);
        lv_div_seperate_details.setAdapter(objMyDivisionSeperationItemTicketAdp);

        if (saredRef.loadLastTrfNo()!="") {
            sp_div_seperate_shopname.setSelection(arr.indexOf(saredRef.loadLastShop()));
            et_div_seperate_trfno.setText(saredRef.loadLastTrfNo());
            sp_div_seperate_shopname.setEnabled(false);
            et_div_seperate_trfno.setEnabled(false);
        }

        et_div_seperate_trfno.setOnTouchListener(new View.OnTouchListener() {
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

        et_div_seperate_scan.setOnTouchListener(new View.OnTouchListener() {
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

        et_div_seperate_trfno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String shopname = sp_div_seperate_shopname.getSelectedItem().toString();
                    String trfno = et_div_seperate_trfno.getText().toString();
                    if (TextUtils.isEmpty(shopname)) {
                        okMessage("DIV", "Please select shopname");
                        sp_div_seperate_shopname.requestFocus();
                        return true;
                    } else if (TextUtils.isEmpty(trfno)) {
                        okMessage("DIV", "Please Enter transfer number");
                        et_div_seperate_trfno.requestFocus();
                        return true;
                    } else {
                        b_Result = objDivisionSeperationControl.validateTransfer(shopname, trfno,false);
                        if (!b_Result) {
                            okMessage("DIV", objGlobal.getErrorMessage());
                            et_div_seperate_trfno.setText("");
                            et_div_seperate_trfno.requestFocus();
                            return true;
                        } else {
                            saredRef.saveLastShop(shopname);
                            saredRef.saveLastTrfNo(trfno);
                            sp_div_seperate_shopname.setEnabled(false);
                            et_div_seperate_trfno.setEnabled(false);
                            ArrayList<DivisionSeperationItemTicket> listLoadItems = objDivisionSeperationControl.loadDivSepItems();
                            objMyDivisionSeperationItemTicketAdp = new DivisionSeperationFragment.MyDivisionSeperationItemTicketAdp(listLoadItems);
                            lv_div_seperate_details.setAdapter(objMyDivisionSeperationItemTicketAdp);
                            et_div_seperate_scan.setText("");
                            et_div_seperate_scan.requestFocus();
                        }
                    }
                }
                return false;
            }
        });

        et_div_seperate_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String shopname = sp_div_seperate_shopname.getSelectedItem().toString();
                    String trfno = et_div_seperate_trfno.getText().toString();
                    String scan= objControls.seperateBarcode(objControls.replaceString(et_div_seperate_scan.getText().toString()));
                    if (TextUtils.isEmpty(shopname)) {
                        okMessage("DIV", "Please select shopname");
                        sp_div_seperate_shopname.requestFocus();
                        return true;
                    } else if (TextUtils.isEmpty(trfno)) {
                        okMessage("DIV", "Please Enter transfer number");
                        et_div_seperate_trfno.requestFocus();
                        return true;
                    } else {
                        b_Result=objDivisionSeperationControl.validateTransferItem(shopname,trfno,scan,1);
                        if (!b_Result) {
                            okMessage("DIV", objGlobal.getErrorMessage());
                            et_div_seperate_scan.setText("");
                            et_div_seperate_scan.requestFocus();
                            return true;
                        } else {
                            ArrayList<DivisionSeperationItemTicket> listLoadItems = objDivisionSeperationControl.loadDivSepItems();
                            objMyDivisionSeperationItemTicketAdp = new DivisionSeperationFragment.MyDivisionSeperationItemTicketAdp(listLoadItems);
                            lv_div_seperate_details.setAdapter(objMyDivisionSeperationItemTicketAdp);
                            et_div_seperate_scan.setText("");
                            et_div_seperate_scan.requestFocus();
                        }
                    }
                }
                return false;
            }
        });

        bt_div_seperate_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result=objDivisionSeperationControl.clearTable();
                                if(b_Result){
                                    ArrayList<DivisionSeperationItemTicket> listLoadItems = objDivisionSeperationControl.loadDivSepItems();
                                    objMyDivisionSeperationItemTicketAdp = new DivisionSeperationFragment.MyDivisionSeperationItemTicketAdp(listLoadItems);
                                    lv_div_seperate_details.setAdapter(objMyDivisionSeperationItemTicketAdp);
                                }
                                et_div_seperate_scan.setText("");
                                et_div_seperate_trfno.setText("");
                                saredRef.saveLastShop("");
                                saredRef.saveLastTrfNo("");
                                sp_div_seperate_shopname.setEnabled(true);
                                et_div_seperate_trfno.setEnabled(true);
                                et_div_seperate_trfno.requestFocus();
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

        bt_div_seperate_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopname = sp_div_seperate_shopname.getSelectedItem().toString();
                String trfno = et_div_seperate_trfno.getText().toString();
                b_Result = objDivisionSeperationControl.validateTransfer(shopname, trfno,true);
                if (!b_Result) {
                    okMessage("DIV", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objDivisionSeperationControl.save(trfno);
                                    if (!b_Result) {
                                        okMessage("DIV", objGlobal.getErrorMessage());
                                    } else {
                                        if(!objDivisionSeperationControl.clearTable()){
                                            okMessage("DIV", objGlobal.getErrorMessage());
                                        } else {
                                            ArrayList<DivisionSeperationItemTicket> listLoadItems = objDivisionSeperationControl.loadDivSepItems();
                                            objMyDivisionSeperationItemTicketAdp = new DivisionSeperationFragment.MyDivisionSeperationItemTicketAdp(listLoadItems);
                                            lv_div_seperate_details.setAdapter(objMyDivisionSeperationItemTicketAdp);
                                            et_div_seperate_scan.setText("");
                                            et_div_seperate_trfno.setText("");
                                            saredRef.saveLastShop("");
                                            saredRef.saveLastTrfNo("");
                                            sp_div_seperate_shopname.setEnabled(true);
                                            et_div_seperate_trfno.setEnabled(true);
                                            et_div_seperate_trfno.requestFocus();
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

        return view;
    }

    private class MyDivisionSeperationItemTicketAdp extends BaseAdapter {
        public ArrayList<DivisionSeperationItemTicket> listDivisionSeperationItemTicket;

        public MyDivisionSeperationItemTicketAdp(ArrayList<DivisionSeperationItemTicket> listDivisionSeperationItemTicket) {
            this.listDivisionSeperationItemTicket = listDivisionSeperationItemTicket;
        }

        @Override
        public int getCount() {
            return listDivisionSeperationItemTicket.size();
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
            View myView = mInflater.inflate(R.layout.division_seperate_item_ticket, null);
            final DivisionSeperationItemTicket s = listDivisionSeperationItemTicket.get(position);

            TextView tv_div_seperate_tickte_itemcode = (TextView) myView.findViewById(R.id.tv_div_seperate_tickte_itemcode);
            tv_div_seperate_tickte_itemcode.setText(String.valueOf(s.itemcode));
            TextView tv_div_seperate_tickte_division = (TextView) myView.findViewById(R.id.tv_div_seperate_tickte_division);
            tv_div_seperate_tickte_division.setText(String.valueOf(s.division));
            TextView tv_div_seperate_tickte_trfqty = (TextView) myView.findViewById(R.id.tv_div_seperate_tickte_trfqty);
            tv_div_seperate_tickte_trfqty.setText(String.valueOf(s.trfQty));
            TextView tv_div_seperate_tickte_scanqty = (TextView) myView.findViewById(R.id.tv_div_seperate_tickte_scanqty);
            tv_div_seperate_tickte_scanqty.setText(String.valueOf(s.scanQty));
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