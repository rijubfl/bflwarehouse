package com.bflgroup.warehouse.ui.usaboxbuilding;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class UsaBoxBuildingFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private UsaBoxBuildingControl objUsaBoxBuildingControl = new UsaBoxBuildingControl();
    private UsaBoxBuildingGlobal objUsaBoxBuildingGlobal = UsaBoxBuildingGlobal.getInstance();

    MyLoadScanItemPopupAdp objMyLoadScanItemPopupAdp;
    MyLoadScanItemAdp objMyLoadScanItemAdp;
    UsaBoxBuildingShared saredRef;
    Boolean flagEdit;
    private Spinner sp_usa_box_pallettype;
    private TextView tv_usa_box_pallettype;
    private TextView tv_usa_box_pallettype_allowmix;
    private TextView tv_usa_box_pallettype_build_sec;
    private Spinner sp_usa_box_size;
    private Spinner sp_usa_box_gender;
    private Spinner sp_usa_box_task;
    private Spinner sp_usa_box_done;
    private TextView tv_usa_box_building_category;
    private RadioButton rb_usa_box_usa_category;
    private RadioButton rb_usa_box_tcm_category;
    private CheckBox ch_usa_box_euro;
    private Button bt_usa_box_add_items;
    private Button bt_usa_box_clear;
    private Button bt_usa_box_save;
    private EditText et_usa_box_toteid;
    private EditText et_usa_box_remarks;
    private TextView tv_usa_box_last_box;
    private ListView lv_usa_box_details;
    private EditText et_build_usabox_popup_itemcode;
    private TextView tv_build_usabox_popup_last_scan;
    private TextView tv_build_usabox_popup_building_category;
    private TextView tv_build_usabox_popup_division;
    private EditText et_build_usabox_popup_qty;
    private Button bt_build_usabox_popup_scan;
    private ListView lv_build_usabox_popup_scandetail;
    private Button bt_build_usabox_popup_ok;
    private TextView tv_build_usabox_popup_total;

    private boolean b_Result;

    public UsaBoxBuildingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_usa_box_building, container, false);

        sp_usa_box_pallettype = (Spinner) view.findViewById(R.id.sp_usa_box_pallettype);
        tv_usa_box_pallettype = (TextView) view.findViewById(R.id.tv_usa_box_pallettype);
        tv_usa_box_pallettype_allowmix = (TextView) view.findViewById(R.id.tv_usa_box_pallettype_allowmix);
        tv_usa_box_pallettype_build_sec = (TextView) view.findViewById(R.id.tv_usa_box_pallettype_build_sec);
        sp_usa_box_size = (Spinner) view.findViewById(R.id.sp_usa_box_size);
        sp_usa_box_gender = (Spinner) view.findViewById(R.id.sp_usa_box_gender);
        sp_usa_box_task = (Spinner) view.findViewById(R.id.sp_usa_box_task);
        sp_usa_box_done = (Spinner) view.findViewById(R.id.sp_usa_box_done);
        bt_usa_box_add_items = (Button) view.findViewById(R.id.bt_usa_box_add_items);
        bt_usa_box_clear = (Button) view.findViewById(R.id.bt_usa_box_clear);
        bt_usa_box_save = (Button) view.findViewById(R.id.bt_usa_box_save);
        et_usa_box_toteid = (EditText) view.findViewById(R.id.et_usa_box_toteid);
        lv_usa_box_details = (ListView) view.findViewById(R.id.lv_usa_box_details);
        et_usa_box_remarks = (EditText) view.findViewById(R.id.et_usa_box_remarks);
        tv_usa_box_last_box = (TextView) view.findViewById(R.id.tv_usa_box_last_box);
        tv_usa_box_building_category = (TextView) view.findViewById(R.id.tv_usa_box_building_category);
        rb_usa_box_usa_category  = (RadioButton) view.findViewById(R.id.rb_usa_box_usa_category);
        rb_usa_box_tcm_category = (RadioButton) view.findViewById(R.id.rb_usa_box_tcm_category);
        ch_usa_box_euro = (CheckBox) view.findViewById(R.id.ch_usa_box_euro);

        flagEdit=false;
        saredRef = new UsaBoxBuildingShared(getContext());
        formLoad();
        et_usa_box_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        bt_usa_box_add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv_usa_box_pallettype.getText().toString())) {
                    okMessage("USABox Build", "Please select pallettype");
                    sp_usa_box_pallettype.requestFocus();
                } else {
                    sp_usa_box_pallettype.setEnabled(false);
                    sp_usa_box_size.setEnabled(false);
                    sp_usa_box_gender.setEnabled(false);
                    sp_usa_box_task.setEnabled(false);
                    sp_usa_box_done.setEnabled(false);
                    rb_usa_box_usa_category.setEnabled(false);
                    rb_usa_box_tcm_category.setEnabled(false);
                    ch_usa_box_euro.setEnabled(false);

                    saredRef.savePltType(sp_usa_box_pallettype.getSelectedItem().toString());
                    saredRef.saveSize(sp_usa_box_size.getSelectedItem().toString());
                    saredRef.saveGender(sp_usa_box_gender.getSelectedItem().toString());
                    saredRef.saveTask(sp_usa_box_task.getSelectedItem().toString());
                    saredRef.saveDone(sp_usa_box_done.getSelectedItem().toString());
                    if(rb_usa_box_usa_category.isChecked()) {
                        saredRef.saveBuildType("USA");
                    }
                    if(rb_usa_box_tcm_category.isChecked()) {
                        saredRef.saveBuildType("TCM");
                    }
                    saredRef.saveEuro("N");
                    if(ch_usa_box_euro.isChecked()) {
                        saredRef.saveEuro("Y");
                    }
                    openPopupWindow();
                }
            }
        });

        bt_usa_box_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all scanned items?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!clearAll()) {
                                    okMessage("USABox Build", "bt_usa_box_clear:" + objGlobal.getErrorMessage());
                                } else {

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

        bt_usa_box_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String palletType = tv_usa_box_pallettype.getText().toString();
                String allowMix = tv_usa_box_pallettype_allowmix.getText().toString();
                String spcitems = tv_usa_box_pallettype_build_sec.getText().toString();
                String remarks = et_usa_box_remarks.getText().toString();
                remarks = remarks + "/A-PDA";
                String nRemarks = remarks;
                String taskType = sp_usa_box_task.getSelectedItem().toString().replace("N/A", "");
                String doneBy = sp_usa_box_done.getSelectedItem().toString().replace("N/A", "");
                String fSize = sp_usa_box_size.getSelectedItem().toString().replace("N/A", "");
                String gender = sp_usa_box_gender.getSelectedItem().toString().replace("N/A", "");
                String toteID = et_usa_box_toteid.getText().toString().toUpperCase().trim();
                String buildType="", euro="";
                String finalBuildType, finalEuro;
                if(rb_usa_box_usa_category.isChecked()) buildType = "USA";
                if(rb_usa_box_tcm_category.isChecked()) buildType = "TCM";
                if(ch_usa_box_euro.isChecked()) euro="Y";
                finalBuildType = buildType;
                finalEuro = euro;
                b_Result = objUsaBoxBuildingControl.validateMain(palletType, "", "", nRemarks, taskType, doneBy, fSize, gender, toteID, allowMix, buildType, euro, spcitems);
                if (!b_Result) {
                    okMessage("USABox Build", "bt_usa_box_save:" + objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objUsaBoxBuildingControl.saveBox(palletType, "", "", nRemarks, taskType, doneBy, fSize, gender, toteID, finalBuildType, finalEuro);
                                    if (!b_Result) {
                                        okMessage("USABox Build", "bt_usa_box_save:" + objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = clearAll();
                                        if (!b_Result) {
                                            okMessage("USABox Build", "bt_usa_box_save:ClearAll:" + objGlobal.getErrorMessage());
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

        sp_usa_box_pallettype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selVal = sp_usa_box_pallettype.getSelectedItem().toString();
                tv_usa_box_pallettype.setText("");
                if (!TextUtils.equals(selVal, "N/A")) {
                    tv_usa_box_pallettype.setText(selVal.substring(0, 2).trim());
                    b_Result=objUsaBoxBuildingControl.getPalletTypeDetails(tv_usa_box_pallettype.getText().toString());
                    if(b_Result){
                        tv_usa_box_pallettype_allowmix.setText(objUsaBoxBuildingGlobal.getBuildCategoryMixAllow());
                        tv_usa_box_pallettype_build_sec.setText(objUsaBoxBuildingGlobal.getBuildSpecialPtype());
                    } else {
                        okMessage("USABox Build", objGlobal.getErrorMessage());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        return view;
    }

    private void openPopupWindow() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.build_usabox_popup_scan_window);

        et_build_usabox_popup_itemcode = (EditText) myDialog.findViewById(R.id.et_build_usabox_popup_itemcode);
        tv_build_usabox_popup_last_scan = (TextView) myDialog.findViewById(R.id.tv_build_usabox_popup_last_scan);
        tv_build_usabox_popup_building_category = (TextView) myDialog.findViewById(R.id.tv_build_usabox_popup_building_category);
        tv_build_usabox_popup_division = (TextView) myDialog.findViewById(R.id.tv_build_usabox_popup_division);
        et_build_usabox_popup_qty = (EditText) myDialog.findViewById(R.id.et_build_usabox_popup_qty);
        bt_build_usabox_popup_scan = (Button) myDialog.findViewById(R.id.bt_build_usabox_popup_scan);
        lv_build_usabox_popup_scandetail = (ListView) myDialog.findViewById(R.id.lv_build_usabox_popup_scandetail);
        bt_build_usabox_popup_ok = (Button) myDialog.findViewById(R.id.bt_build_usabox_popup_ok);
        tv_build_usabox_popup_total = (TextView) myDialog.findViewById(R.id.tv_build_usabox_popup_total);
        et_build_usabox_popup_qty.setText("1");

        et_build_usabox_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_build_usabox_popup_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanPopupItems();
                    return true;
                }
                return false;
            }
        });

        bt_build_usabox_popup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanPopupItems();
            }
        });

        bt_build_usabox_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<UsaBoxBuildingScanItemTicket> listUsaBoxBuildingScaItems = objUsaBoxBuildingControl.loadScanItems();
                objMyLoadScanItemAdp = new MyLoadScanItemAdp(listUsaBoxBuildingScaItems);
                lv_usa_box_details.setAdapter(objMyLoadScanItemAdp);
                tv_usa_box_building_category.setText(objUsaBoxBuildingGlobal.getBuildingCategory());
                myDialog.dismiss();
            }
        });

        ArrayList<UsaBoxBuildingScaItemsPopupTicket> listUsaBoxBuildingScaItemsPopup = objUsaBoxBuildingControl.loadPopupScanItems();
        objMyLoadScanItemPopupAdp = new MyLoadScanItemPopupAdp(listUsaBoxBuildingScaItemsPopup);
        lv_build_usabox_popup_scandetail.setAdapter(objMyLoadScanItemPopupAdp);
        tv_build_usabox_popup_total.setText(String.valueOf(objUsaBoxBuildingGlobal.getScanTotalQty()));

        myDialog.show();
        et_build_usabox_popup_itemcode.requestFocus();
    }

    private void scanPopupItems(){
        if (TextUtils.isEmpty(et_build_usabox_popup_qty.getText()) || et_build_usabox_popup_qty.getText().toString().equals("0")) {
            et_build_usabox_popup_qty.setText("1");
        }
        String itemcode = et_build_usabox_popup_itemcode.getText().toString();
        int qty = Integer.parseInt(et_build_usabox_popup_qty.getText().toString());
        String palletType = tv_usa_box_pallettype.getText().toString();
        String allowMix = tv_usa_box_pallettype_allowmix.getText().toString();
        String selitems = tv_usa_box_pallettype_build_sec.getText().toString();
        String gender = sp_usa_box_gender.getSelectedItem().toString().trim();
        String buildType="";
        if(rb_usa_box_usa_category.isChecked()) buildType = "USA";
        if(rb_usa_box_tcm_category.isChecked()) buildType = "TCM";
        tv_build_usabox_popup_last_scan.setText(itemcode);
        b_Result = objUsaBoxBuildingControl.validateItemcode(flagEdit, itemcode, "", "", palletType, gender, qty, allowMix, buildType, selitems);
        if (!b_Result) {
            tv_build_usabox_popup_last_scan.setText(objGlobal.getErrorMessage());
            vibrate(500);
            et_build_usabox_popup_itemcode.setText("");
            et_build_usabox_popup_itemcode.requestFocus();
        }
        ArrayList<UsaBoxBuildingScanItemTicket> listUsaBoxBuildingScaItems = objUsaBoxBuildingControl.loadScanItems();
        objMyLoadScanItemAdp = new MyLoadScanItemAdp(listUsaBoxBuildingScaItems);
        lv_usa_box_details.setAdapter(objMyLoadScanItemAdp);
        tv_usa_box_building_category.setText(objUsaBoxBuildingGlobal.getBuildingCategory());

        ArrayList<UsaBoxBuildingScaItemsPopupTicket> listUsaBoxBuildingScaItemsPopup = objUsaBoxBuildingControl.loadPopupScanItems();
        objMyLoadScanItemPopupAdp = new MyLoadScanItemPopupAdp(listUsaBoxBuildingScaItemsPopup);
        lv_build_usabox_popup_scandetail.setAdapter(objMyLoadScanItemPopupAdp);

        tv_build_usabox_popup_total.setText(String.valueOf(objUsaBoxBuildingGlobal.getScanTotalQty()));
        tv_build_usabox_popup_building_category.setText(objUsaBoxBuildingGlobal.getScanBuildingCategory());
        tv_build_usabox_popup_division.setText(objUsaBoxBuildingGlobal.getScanDepartment());
        et_build_usabox_popup_itemcode.setText("");
        et_build_usabox_popup_qty.setText("1");
        et_build_usabox_popup_itemcode.requestFocus();
    }

    private class MyLoadScanItemPopupAdp extends BaseAdapter {
        public ArrayList<UsaBoxBuildingScaItemsPopupTicket> listLoadScanItemPopupTicket;

        public MyLoadScanItemPopupAdp(ArrayList<UsaBoxBuildingScaItemsPopupTicket> listLoadScanItemPopupTicket) {
            this.listLoadScanItemPopupTicket = listLoadScanItemPopupTicket;
        }

        @Override
        public int getCount() {
            return listLoadScanItemPopupTicket.size();
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
            View myView = mInflater.inflate(R.layout.build_usabox_popup_scan_item_ticket, null);
            final UsaBoxBuildingScaItemsPopupTicket s = listLoadScanItemPopupTicket.get(position);
            TextView tv_build_usabox_popup_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_build_usabox_popup_ticket_itemcode);
            tv_build_usabox_popup_ticket_itemcode.setText(String.valueOf(s.itemcode));
            TextView tv_build_usabox_popup_ticket_qty = (TextView) myView.findViewById(R.id.tv_build_usabox_popup_ticket_qty);
            tv_build_usabox_popup_ticket_qty.setText(String.valueOf(s.qty));
            Button bt_build_usabox_popup_ticket_edit=(Button)myView.findViewById(R.id.bt_build_usabox_popup_ticket_edit);
            bt_build_usabox_popup_ticket_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_build_usabox_popup_itemcode.setText(String.valueOf(s.itemcode));
                    et_build_usabox_popup_itemcode.setEnabled(false);
                    et_build_usabox_popup_qty.setText(String.valueOf(s.qty));
                    et_build_usabox_popup_qty.setEnabled(true);
                    et_build_usabox_popup_qty.requestFocus();
                    flagEdit=true;
                }
            });
            return myView;
        }
    }

    private class MyLoadScanItemAdp extends BaseAdapter {
        public ArrayList<UsaBoxBuildingScanItemTicket> listLoadScanItemTicket;

        public MyLoadScanItemAdp(ArrayList<UsaBoxBuildingScanItemTicket> listLoadScanItemTicket) {
            this.listLoadScanItemTicket = listLoadScanItemTicket;
        }

        @Override
        public int getCount() {
            return listLoadScanItemTicket.size();
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
            View myView = mInflater.inflate(R.layout.build_usabox_scan_item_ticket, null);
            final UsaBoxBuildingScanItemTicket s = listLoadScanItemTicket.get(position);

            TextView tv_build_usabox_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_build_usabox_ticket_itemcode);
            tv_build_usabox_ticket_itemcode.setText(String.valueOf(s.itemcode));

            TextView tv_build_usabox_ticket_description = (TextView) myView.findViewById(R.id.tv_build_usabox_ticket_description);
            tv_build_usabox_ticket_description.setText(String.valueOf(s.description));

            TextView tv_build_usabox_ticket_qty = (TextView) myView.findViewById(R.id.tv_build_usabox_ticket_qty);
            tv_build_usabox_ticket_qty.setText(String.valueOf(s.qty));

            return myView;
        }
    }

    private boolean clearAll() {
        b_Result = objUsaBoxBuildingControl.clearTable();
        if (!b_Result) {
            okMessage("USABox Build", objGlobal.getErrorMessage());
            return false;
        }
        tv_usa_box_pallettype.setText("");
        tv_usa_box_pallettype_allowmix.setText("");
        tv_usa_box_pallettype_build_sec.setText("");
        et_usa_box_toteid.setText("");
        et_usa_box_remarks.setText("");
        tv_usa_box_building_category.setText("");
        saredRef.savePltType("");
        saredRef.saveAllowMixCategory("");
        saredRef.saveSize("");
        saredRef.saveGender("");
        saredRef.saveTask("");
        saredRef.saveDone("");
        saredRef.saveBuildType("");
        saredRef.saveEuro("");
        ch_usa_box_euro.setChecked(false);
        tv_usa_box_last_box.setText(objUsaBoxBuildingGlobal.getBoxNo());
        formLoad();
        return true;
    }

    private void formLoad() {
        sp_usa_box_pallettype.setEnabled(true);
        sp_usa_box_size.setEnabled(true);
        sp_usa_box_gender.setEnabled(true);
        sp_usa_box_task.setEnabled(true);
        sp_usa_box_done.setEnabled(true);
        rb_usa_box_usa_category.setEnabled(true);
        rb_usa_box_tcm_category.setEnabled(true);
        ch_usa_box_euro.setEnabled(true);

        List<String> arr1 = objUsaBoxBuildingControl.loadSpinner("PT");
        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
        sp_usa_box_pallettype.setAdapter(arrayAdp1);
        if (saredRef.loadPltType() != "") {
            sp_usa_box_pallettype.setSelection(arrayAdp1.getPosition(saredRef.loadPltType()));
            tv_usa_box_pallettype.setText(saredRef.loadPltType().substring(0, 2).trim());
            b_Result=objUsaBoxBuildingControl.getPalletTypeDetails(tv_usa_box_pallettype.getText().toString());
            if(b_Result){
                tv_usa_box_pallettype_allowmix.setText(objUsaBoxBuildingGlobal.getBuildCategoryMixAllow());
                tv_usa_box_pallettype_build_sec.setText(objUsaBoxBuildingGlobal.getBuildSpecialPtype());
            } else {
                okMessage("USABox Build", objGlobal.getErrorMessage());
            }
            sp_usa_box_pallettype.setEnabled(false);
        }

        List<String> arr4 = objUsaBoxBuildingControl.loadSpinner("SZ");
        ArrayAdapter<String> arrayAdp4 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr4);
        sp_usa_box_size.setAdapter(arrayAdp4);
        if (saredRef.loadSize() != "") {
            sp_usa_box_size.setSelection(arrayAdp4.getPosition(saredRef.loadSize()));
            sp_usa_box_size.setEnabled(false);
        }

        List<String> arr5 = objUsaBoxBuildingControl.loadSpinner("TS");
        ArrayAdapter<String> arrayAdp5 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr5);
        sp_usa_box_task.setAdapter(arrayAdp5);
        if (saredRef.loadTask() != "") {
            sp_usa_box_task.setSelection(arrayAdp5.getPosition(saredRef.loadTask()));
            sp_usa_box_task.setEnabled(false);
        }

        List<String> arr6;
        arr6 = new ArrayList<String>();
        arr6.add("N/A");
        arr6.add("MEN");
        arr6.add("WOMEN");
        arr6.add("CHILDREN");
        arr6.add("UNISEX");
        ArrayAdapter<String> arrayAdp6 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr6);
        sp_usa_box_gender.setAdapter(arrayAdp6);
        if (saredRef.loadGender() != "") {
            sp_usa_box_gender.setSelection(arrayAdp6.getPosition(saredRef.loadGender()));
            sp_usa_box_gender.setEnabled(false);
        }

        List<String> arr7 = objUsaBoxBuildingControl.loadSpinner("DN");
        ArrayAdapter<String> arrayAdp7 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr7);
        sp_usa_box_done.setAdapter(arrayAdp7);
        if (saredRef.loadDone() != "") {
            sp_usa_box_done.setSelection(arrayAdp7.getPosition(saredRef.loadDone()));
            sp_usa_box_done.setEnabled(false);
        }

        if (saredRef.loadEuro().equals("Y")) {
            ch_usa_box_euro.setChecked(true);
            ch_usa_box_euro.setEnabled(false);
        }
        if (saredRef.loadEuro().equals("N")) {
            ch_usa_box_euro.setChecked(false);
            ch_usa_box_euro.setEnabled(false);
        }

        if(saredRef.loadBuildType().equals("TCM")) {
            rb_usa_box_tcm_category.setChecked(true);
            rb_usa_box_usa_category.setEnabled(false);
            rb_usa_box_tcm_category.setEnabled(false);
        }
        if(saredRef.loadBuildType().equals("USA")) {
            rb_usa_box_usa_category.setChecked(true);
            rb_usa_box_usa_category.setEnabled(false);
            rb_usa_box_tcm_category.setEnabled(false);
        }

        ArrayList<UsaBoxBuildingScanItemTicket> listUsaBoxBuildingScaItems = objUsaBoxBuildingControl.loadScanItems();
        objMyLoadScanItemAdp = new MyLoadScanItemAdp(listUsaBoxBuildingScaItems);
        lv_usa_box_details.setAdapter(objMyLoadScanItemAdp);
        tv_usa_box_building_category.setText(objUsaBoxBuildingGlobal.getBuildingCategory());
    }

    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
}
