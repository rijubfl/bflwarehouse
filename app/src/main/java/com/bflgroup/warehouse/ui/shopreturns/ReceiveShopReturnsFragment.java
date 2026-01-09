package com.bflgroup.warehouse.ui.shopreturns;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.ui.usaboxbuilding.UsaBoxBuildingControl;
import com.bflgroup.warehouse.ui.usaboxbuilding.UsaBoxBuildingGlobal;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ReceiveShopReturnsFragment extends Fragment {

    private EditText et_shop_return_entryno;
    private CheckBox ch_shop_return_itemwise_scan;
    private CheckBox ch_shop_return_itemwise_autobuild;
    private TextView tv_shop_return_auto_build_pallettype;
    private EditText et_shop_return_remarks;
    private EditText et_shop_return_toteid;
    private Button bt_shop_return_scan;
    private TextView tv_shop_return_category;
    private TextView tv_shop_return_shopname;
    private ListView lv_shop_return_details;
    private TextView tv_shop_return_scan_total;
    private TextView tv_shop_return_trf_total;
    private TextView tv_shop_return_diff_total;
    private Button bt_shop_return_save;
    private Button bt_shop_return_clear;
    private EditText et_shop_return_popup_diff_excess_password_pass;
    private Button bt_shop_return_popup_diff_excess_password_close;
    private Button bt_shop_return_popup_diff_excess_password_ok;

    private Spinner sp_shop_return_popup_action;
    private Spinner sp_shop_return_printer;
    private Spinner sp_shop_return_printer_copies;
    private EditText et_shop_return_popup_itemcode;
    private EditText et_shop_return_popup_qty;
    private TextView tv_shop_return_popup_last_scan;
    private TextView tv_shop_return_popup_division;
    private TextView tv_shop_return_popup_season;
    private Button bt_shop_return_popup_scan;
    private ListView lv_shop_return_popup_scandetail;
    private Button bt_shop_return_popup_ok;
    private Button bt_shop_return_popup_close;

    private Global objGlobal = Global.getInstance();
    private ReceiveShopReturnsControl objReceiveShopReturnsControl = new ReceiveShopReturnsControl();
    private ReceiveShopReturnsGlobal objReceiveShopReturnsGlobal = ReceiveShopReturnsGlobal.getInstance();
    private Controls objControls = new Controls();

    ReceiveShopReturnsShared saredRef;
    private boolean b_Result;
    private String s_Result;
    private Boolean flagEdit;

    private ArrayAdapter<String> arrayAdpAction;

    MyLoadScanItemPopupAdp objMyLoadScanItemPopupAdp;
    MyLoadScanItemAdp objMyLoadScanItemAdp;

    private BarcodePrinting objSample_Print;
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();
    private BluetoothPort bluetoothPort;
    private BroadcastReceiver connectDevice;
    private Thread btThread;
    private boolean testPrint = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver searchFinish;
    private Vector<BluetoothDevice> remoteDevices;
    private ArrayAdapter<String> adapter;
    private boolean searchflags;
    private static final int REQUEST_ENABLE_BT = 2;

    public ReceiveShopReturnsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_shop_returns, container, false);

        et_shop_return_entryno = (EditText) view.findViewById(R.id.et_shop_return_entryno);
        sp_shop_return_printer = (Spinner) view.findViewById(R.id.sp_shop_return_printer);
        sp_shop_return_printer_copies= (Spinner) view.findViewById(R.id.sp_shop_return_printer_copies);
        ch_shop_return_itemwise_scan = (CheckBox) view.findViewById(R.id.ch_shop_return_itemwise_scan);
        ch_shop_return_itemwise_autobuild = (CheckBox) view.findViewById(R.id.ch_shop_return_itemwise_autobuild);
        tv_shop_return_auto_build_pallettype = (TextView) view.findViewById(R.id.tv_shop_return_auto_build_pallettype);
        et_shop_return_toteid = (EditText) view.findViewById(R.id.et_shop_return_toteid);
        et_shop_return_remarks = (EditText) view.findViewById(R.id.et_shop_return_remarks);
        bt_shop_return_scan = (Button) view.findViewById(R.id.bt_shop_return_scan);
        tv_shop_return_category = (TextView) view.findViewById(R.id.tv_shop_return_category);
        tv_shop_return_shopname = (TextView) view.findViewById(R.id.tv_shop_return_shopname);
        lv_shop_return_details = (ListView) view.findViewById(R.id.lv_shop_return_details);
        tv_shop_return_scan_total = (TextView) view.findViewById(R.id.tv_shop_return_scan_total);
        tv_shop_return_trf_total = (TextView) view.findViewById(R.id.tv_shop_return_trf_total);
        tv_shop_return_diff_total = (TextView) view.findViewById(R.id.tv_shop_return_diff_total);
        bt_shop_return_save = (Button) view.findViewById(R.id.bt_shop_return_save);
        bt_shop_return_clear = (Button) view.findViewById(R.id.bt_shop_return_clear);
        saredRef = new ReceiveShopReturnsShared(getContext());
        flagEdit = false;

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_shop_return_printer.setAdapter(arrayAdpYellow);
            if (saredRef.loadPrinter() != "") {
                sp_shop_return_printer.setSelection(arrayAdpYellow.getPosition(saredRef.loadPrinter()));
            }
        }

        List<String> arr8;
        arr8 = new ArrayList<String>();
        arr8.add("0");
        arr8.add("1");
        arr8.add("2");
        arr8.add("4");
        ArrayAdapter<String> arrayAdp8 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr8);
        sp_shop_return_printer_copies.setAdapter(arrayAdp8);
        sp_shop_return_printer_copies.setSelection(arrayAdp8.getPosition("2"));

        if (saredRef.loadEntryNo() != "") {
            et_shop_return_entryno.setText(saredRef.loadEntryNo());
            tv_shop_return_category.setText(saredRef.loadCategory());
            tv_shop_return_shopname.setText(saredRef.loadShop());
            tv_shop_return_auto_build_pallettype.setText(saredRef.loadAutoBuildPalletType());
            et_shop_return_entryno.setEnabled(false);
            ch_shop_return_itemwise_scan.setEnabled(false);
            ch_shop_return_itemwise_scan.setChecked(false);
            ch_shop_return_itemwise_autobuild.setEnabled(false);
            ch_shop_return_itemwise_autobuild.setChecked(false);
            if (saredRef.loadItemWiseScan().equals("Y"))
                ch_shop_return_itemwise_scan.setChecked(true);
            if (saredRef.loadAutoBuild().equals("Y"))
                ch_shop_return_itemwise_autobuild.setChecked(true);
            bt_shop_return_scan.requestFocus();
        } else {
            et_shop_return_entryno.requestFocus();
        }

        ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
        objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
        lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);
        tv_shop_return_trf_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalTrfQty()));
        tv_shop_return_scan_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalScanQty()));
        tv_shop_return_diff_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalDiffQty()));

        et_shop_return_entryno.setOnTouchListener(new View.OnTouchListener() {
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

        et_shop_return_entryno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    enoScan();
                }
                return false;
            }
        });

        bt_shop_return_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enoScan();
            }
        });

        bt_shop_return_clear.setOnClickListener(new View.OnClickListener() {
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
                                    okMessage("Shop Return", objGlobal.getErrorMessage());
                                } else {
                                    et_shop_return_entryno.requestFocus();
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

        bt_shop_return_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entryNo = et_shop_return_entryno.getText().toString();
                String shopName = tv_shop_return_shopname.getText().toString();
                String category = tv_shop_return_category.getText().toString();
                String remarks = et_shop_return_remarks.getText().toString();
                String toteid = et_shop_return_toteid.getText().toString().toUpperCase();
                boolean itemwiseScan = true, autoBuild = false;
                if (ch_shop_return_itemwise_scan.isChecked()) itemwiseScan = false;
                if (ch_shop_return_itemwise_autobuild.isChecked()) autoBuild = true;
                String autoBoxBuildPalletType = tv_shop_return_auto_build_pallettype.getText().toString();
                b_Result = objReceiveShopReturnsControl.validateShopReturn(entryNo, itemwiseScan, autoBuild, true, toteid);
                if (!b_Result) {
                    okMessage("Shop Return", objGlobal.getErrorMessage());
                } else {
                    if (objReceiveShopReturnsGlobal.getTotalExcess() > 0 || objReceiveShopReturnsGlobal.getTotalMissing() > 0) {
                        vibrate(1000);
                        openPopupDiffPasswordWindow(entryNo, shopName, category, remarks, itemwiseScan, autoBuild, autoBoxBuildPalletType);
                    } else {
                        saveShopReturns(entryNo, shopName, category, remarks, itemwiseScan, autoBuild, autoBoxBuildPalletType);
                    }
                }
            }
        });

        searchflags = false;
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();

        return view;
    }

    private void saveShopReturns(String entryNo, String shopName, String category, String remarks, boolean itemScan, boolean autoBoxBuild, String autoBoxBuildPalletType) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are You sure to save?")
                .setTitle("Conformation")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        b_Result = objReceiveShopReturnsControl.saveShopTransfer(entryNo, shopName, category, remarks, itemScan, autoBoxBuild, autoBoxBuildPalletType);
                        if (!b_Result) {
                            okMessage("Shop Return", objGlobal.getErrorMessage());
                        } else {
                            if (!objReceiveShopReturnsGlobal.getBoxNo().isEmpty()) {
                                if (objGlobal.getBluetoothDevicesAvailable().equals("Y")) {
                                    if (!sp_shop_return_printer_copies.getSelectedItem().toString().equals("0")) {
                                        String printer = sp_shop_return_printer.getSelectedItem().toString();
                                        b_Result = objReceiveShopReturnsControl.forPrint(objReceiveShopReturnsGlobal.getBoxNo());
                                        if (!b_Result) {
                                            okMessage("Upc Box", objGlobal.getErrorMessage());
                                        } else {
                                            if (!printSticker(printer)) {
                                                okMessage("Transfer", "Printer Error, Pleasse reprint..");
                                                vibrate(100);
                                            }
                                        }
                                    }
                                }
                            }
                            b_Result = clearAll();
                            if (!b_Result) {
                                okMessage("Shop Return", objGlobal.getErrorMessage());
                            }
                            et_shop_return_entryno.requestFocus();
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

    private void loadActions(String category) {
        List<String> arr;
        arr = new ArrayList<String>();
        if (category.equals("Recalls") || category.equals("Aged Recall")) {
            arr.add("USA Transfer to Shop");
        }
        if (category.equals("Online Returns")) arr.add("Shop Returns For Online");
        if (category.equals("Shop Transfer")) arr.add("Shop Transfer");
        if (category.equals("WH Transfer")) arr.add("WH Transfer");
        if (category.equals("Quality Issues") || category.equals("Quality issues"))
            arr.add("Quality Issues");
        if (category.equals("NO Barcode")) {
            arr.add("NO Barcode");
        }
        if (category.equals("Non Saleable It") || category.equals("Customer Damage") || category.equals("Damaged In Shop")) {
            arr.add("For Repair");
            arr.add("Totally Damaged Items");
        }
        arrayAdpAction = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_shop_return_popup_action.setAdapter(arrayAdpAction);
    }

    private boolean clearAll() {
        b_Result = objReceiveShopReturnsControl.clearTable();
        if (!b_Result) {
            okMessage("Shop Return", objGlobal.getErrorMessage());
            return false;
        }
        ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
        objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
        lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);
        tv_shop_return_trf_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalTrfQty()));
        tv_shop_return_scan_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalScanQty()));
        tv_shop_return_diff_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalDiffQty()));
        tv_shop_return_shopname.setText("");
        tv_shop_return_category.setText("");
        tv_shop_return_auto_build_pallettype.setText("");
        tv_shop_return_diff_total.setText("0");
        tv_shop_return_scan_total.setText("0");
        tv_shop_return_trf_total.setText("0");
        et_shop_return_entryno.setText("");
        et_shop_return_entryno.setEnabled(true);
        ch_shop_return_itemwise_scan.setEnabled(true);
        ch_shop_return_itemwise_autobuild.setEnabled(true);
        saredRef.saveShop("");
        saredRef.saveEntryNo("");
        saredRef.saveItemWiseScan("N");
        saredRef.saveAutoBuild("N");
        saredRef.saveAutoBuildPalletType("");
        ch_shop_return_itemwise_scan.setChecked(false);
        ch_shop_return_itemwise_autobuild.setChecked(false);
        saredRef.saveCategory("");
        return true;
    }

    private void enoScan() {
        String entryNo = objControls.replaceString(et_shop_return_entryno.getText().toString());
        if (TextUtils.isEmpty(entryNo)) {
            okMessage("Shop Return", "Please Enter Valid Entry Number");
            et_shop_return_entryno.requestFocus();
        } else {
            boolean itemwiseScan = true, autoBuild = false;
            if (ch_shop_return_itemwise_scan.isChecked()) itemwiseScan = false;
            if (ch_shop_return_itemwise_autobuild.isChecked()) autoBuild = true;
            b_Result = objReceiveShopReturnsControl.validateShopReturn(entryNo, itemwiseScan, autoBuild, false, "");
            if (!b_Result) {
                okMessage("Shop Return", objGlobal.getErrorMessage());
                et_shop_return_entryno.setText("");
                et_shop_return_entryno.requestFocus();
            } else {
                b_Result = objReceiveShopReturnsControl.validateShopReturnItem(true, entryNo, "", 0, flagEdit, "", itemwiseScan);
                if (!b_Result) {
                    okMessage("Shop Return", objGlobal.getErrorMessage());
                    et_shop_return_entryno.setText("");
                    et_shop_return_entryno.requestFocus();
                } else {
                    et_shop_return_entryno.setText(entryNo);
                    et_shop_return_entryno.setEnabled(false);
                    ch_shop_return_itemwise_scan.setEnabled(false);
                    ch_shop_return_itemwise_autobuild.setEnabled(false);
                    tv_shop_return_category.setText(objReceiveShopReturnsGlobal.getCategory());
                    tv_shop_return_shopname.setText(objReceiveShopReturnsGlobal.getShopName());
                    saredRef.saveEntryNo(entryNo);
                    saredRef.saveItemWiseScan("N");
                    saredRef.saveAutoBuild("N");
                    saredRef.savePrinter(sp_shop_return_printer.getSelectedItem().toString());
                    if (ch_shop_return_itemwise_scan.isChecked()) saredRef.saveItemWiseScan("Y");
                    if (ch_shop_return_itemwise_autobuild.isChecked()) saredRef.saveAutoBuild("Y");
                    saredRef.saveShop(objReceiveShopReturnsGlobal.getShopName());
                    saredRef.saveCategory(objReceiveShopReturnsGlobal.getCategory());
                    saredRef.saveAutoBuildPalletType(objReceiveShopReturnsGlobal.getAutoBuildPalletType());
                    tv_shop_return_auto_build_pallettype.setText(objReceiveShopReturnsGlobal.getAutoBuildPalletType());
                    ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
                    objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
                    lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);
                    tv_shop_return_trf_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalTrfQty()));
                    tv_shop_return_scan_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalScanQty()));
                    tv_shop_return_diff_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalDiffQty()));
                    if (itemwiseScan) openPopupWindow();
                }
                flagEdit = false;
            }
        }
    }

    private boolean itemScan() {
        String entryNo = et_shop_return_entryno.getText().toString();
        String itemcode = objControls.seperateBarcode(objControls.replaceString(et_shop_return_popup_itemcode.getText().toString()));
        String actions = "";
        if (sp_shop_return_popup_action.getSelectedItem() != null)
            actions = sp_shop_return_popup_action.getSelectedItem().toString();
        tv_shop_return_popup_last_scan.setText("");
        tv_shop_return_popup_division.setText("");
        tv_shop_return_popup_season.setText("");
        if (TextUtils.isEmpty(et_shop_return_popup_qty.getText())) {
            et_shop_return_popup_qty.setText("1");
        }
        int qty = Integer.parseInt(et_shop_return_popup_qty.getText().toString());
        if (TextUtils.isEmpty(entryNo)) {
            okMessage("Shop Return", "Entry Number is blank");
            vibrate(500);
            et_shop_return_popup_itemcode.setText("");
            et_shop_return_popup_itemcode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(itemcode)) {
            okMessage("Shop Return", "Itemcode is blank");
            vibrate(500);
            et_shop_return_popup_itemcode.setText("");
            et_shop_return_popup_itemcode.requestFocus();
            return false;
        }
        b_Result = objReceiveShopReturnsControl.validItemcode(itemcode);
        if (!b_Result) {
            okMessage("Shop Return", "Invalid Itemcode");
            vibrate(500);
            et_shop_return_popup_itemcode.setText("");
            et_shop_return_popup_itemcode.requestFocus();
            return false;
        }

        b_Result = objReceiveShopReturnsControl.validateShopReturnItem(false, entryNo, itemcode, qty, flagEdit, actions, true);
        if (!b_Result) {
            okMessage("Shop Return", "openPopupWindow:et_build_usabox_popup_itemcode:" + objGlobal.getErrorMessage());
            vibrate(500);
            et_shop_return_popup_itemcode.setText("");
            et_shop_return_popup_itemcode.requestFocus();
            return false;
        }
        tv_shop_return_popup_last_scan.setText(itemcode);
        tv_shop_return_popup_division.setText(objReceiveShopReturnsGlobal.getScanItemDivision());
        tv_shop_return_popup_season.setText(objReceiveShopReturnsGlobal.getScanItemSeason());
        ArrayList<ReceiveShopReturnsScanItemPopupTicket> listReceiveShopReturnsScanItemPopup = objReceiveShopReturnsControl.loadPopupScanItems();
        objMyLoadScanItemPopupAdp = new ReceiveShopReturnsFragment.MyLoadScanItemPopupAdp(listReceiveShopReturnsScanItemPopup);
        lv_shop_return_popup_scandetail.setAdapter(objMyLoadScanItemPopupAdp);

        ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
        objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
        lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);

        tv_shop_return_trf_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalTrfQty()));
        tv_shop_return_scan_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalScanQty()));
        tv_shop_return_diff_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalDiffQty()));

        et_shop_return_popup_itemcode.setEnabled(true);
        et_shop_return_popup_qty.setEnabled(false);
        sp_shop_return_popup_action.setEnabled(true);
        et_shop_return_popup_itemcode.setText("");
        et_shop_return_popup_qty.setText("1");
        flagEdit = false;
        if (objReceiveShopReturnsGlobal.getCategory().equals("NO Barcode")) {
            et_shop_return_popup_itemcode.setText("NOBARCODE");
            et_shop_return_popup_itemcode.setEnabled(false);
            et_shop_return_popup_qty.requestFocus();
        } else {
            et_shop_return_popup_itemcode.requestFocus();
        }
        return true;
    }

    private void openPopupWindow() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.shop_return_popup_items_scan_window);

        sp_shop_return_popup_action = (Spinner) myDialog.findViewById(R.id.sp_shop_return_popup_action);
        et_shop_return_popup_itemcode = (EditText) myDialog.findViewById(R.id.et_shop_return_popup_itemcode);
        et_shop_return_popup_qty = (EditText) myDialog.findViewById(R.id.et_shop_return_popup_qty);
        tv_shop_return_popup_last_scan = (TextView) myDialog.findViewById(R.id.tv_shop_return_popup_last_scan);
        tv_shop_return_popup_division = (TextView) myDialog.findViewById(R.id.tv_shop_return_popup_division);
        tv_shop_return_popup_season = (TextView) myDialog.findViewById(R.id.tv_shop_return_popup_season);
        bt_shop_return_popup_scan = (Button) myDialog.findViewById(R.id.bt_shop_return_popup_scan);
        lv_shop_return_popup_scandetail = (ListView) myDialog.findViewById(R.id.lv_shop_return_popup_scandetail);
        bt_shop_return_popup_ok = (Button) myDialog.findViewById(R.id.bt_shop_return_popup_ok);
        bt_shop_return_popup_close = (Button) myDialog.findViewById(R.id.bt_shop_return_popup_close);

        et_shop_return_popup_qty.setText("1");
        et_shop_return_popup_itemcode.setEnabled(true);
        if (objReceiveShopReturnsGlobal.getCategory().equals("NO Barcode")) {
            et_shop_return_popup_itemcode.setText("NOBARCODE");
            et_shop_return_popup_itemcode.setEnabled(false);
        }

        et_shop_return_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_shop_return_popup_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    itemScan();
                }
                return false;
            }
        });

        bt_shop_return_popup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemScan();
            }
        });

        bt_shop_return_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
                objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
                lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);
                tv_shop_return_trf_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalTrfQty()));
                tv_shop_return_scan_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalScanQty()));
                tv_shop_return_diff_total.setText(String.valueOf(objReceiveShopReturnsGlobal.getTotalDiffQty()));
                myDialog.dismiss();
            }
        });

        bt_shop_return_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItem = objReceiveShopReturnsControl.loadScanRetItems();
                objMyLoadScanItemAdp = new ReceiveShopReturnsFragment.MyLoadScanItemAdp(listReceiveShopReturnsScanItem);
                lv_shop_return_details.setAdapter(objMyLoadScanItemAdp);
                myDialog.dismiss();
            }
        });

        ArrayList<ReceiveShopReturnsScanItemPopupTicket> listReceiveShopReturnsScanItemPopup = objReceiveShopReturnsControl.loadPopupScanItems();
        objMyLoadScanItemPopupAdp = new ReceiveShopReturnsFragment.MyLoadScanItemPopupAdp(listReceiveShopReturnsScanItemPopup);
        lv_shop_return_popup_scandetail.setAdapter(objMyLoadScanItemPopupAdp);

        loadActions(objReceiveShopReturnsGlobal.getCategory());

        myDialog.show();
        et_shop_return_popup_itemcode.requestFocus();
    }

    void openPopupDiffPasswordWindow(String entryNo, String shopName, String category, String remarks, boolean itemScan, boolean autoBuild, String autoBuildPalletType) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.shop_return_popup_password_entry);
        et_shop_return_popup_diff_excess_password_pass = (EditText) myDialog.findViewById(R.id.et_shop_return_popup_diff_excess_password_pass);
        bt_shop_return_popup_diff_excess_password_close = (Button) myDialog.findViewById(R.id.bt_shop_return_popup_diff_excess_password_close);
        bt_shop_return_popup_diff_excess_password_ok = (Button) myDialog.findViewById(R.id.bt_shop_return_popup_diff_excess_password_ok);

        bt_shop_return_popup_diff_excess_password_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_shop_return_popup_diff_excess_password_pass.getText().toString().isEmpty()) {
                    okMessage("Shop Return", "Please enter missmatch password");
                    vibrate(500);
                    et_shop_return_popup_diff_excess_password_pass.requestFocus();
                } else {
                    b_Result = objReceiveShopReturnsControl.validateMissingPassword(et_shop_return_popup_diff_excess_password_pass.getText().toString());
                    if (!b_Result) {
                        okMessage("Shop Return", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_shop_return_popup_diff_excess_password_pass.requestFocus();
                    } else {
                        saveShopReturns(entryNo, shopName, category, remarks, itemScan, autoBuild, autoBuildPalletType);
                        myDialog.dismiss();
                    }
                }
            }
        });

        bt_shop_return_popup_diff_excess_password_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        et_shop_return_popup_diff_excess_password_pass.requestFocus();
    }

    private class MyLoadScanItemPopupAdp extends BaseAdapter {
        public ArrayList<ReceiveShopReturnsScanItemPopupTicket> listLoadScanItemPopupTicket;

        public MyLoadScanItemPopupAdp(ArrayList<ReceiveShopReturnsScanItemPopupTicket> listLoadScanItemPopupTicket) {
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
            View myView = mInflater.inflate(R.layout.shop_return_popup_scan_item_ticket, null);
            final ReceiveShopReturnsScanItemPopupTicket s = listLoadScanItemPopupTicket.get(position);

            TextView tv_shop_return_popup_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_shop_return_popup_ticket_itemcode);
            tv_shop_return_popup_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_shop_return_popup_ticket_qty = (TextView) myView.findViewById(R.id.tv_shop_return_popup_ticket_qty);
            tv_shop_return_popup_ticket_qty.setText(String.valueOf(s.scanQty));

            TextView tv_shop_return_popup_ticket_category = (TextView) myView.findViewById(R.id.tv_shop_return_popup_ticket_category);
            tv_shop_return_popup_ticket_category.setText(String.valueOf(s.actions));

            Button bt_shop_return_popup_ticket_edit = (Button) myView.findViewById(R.id.bt_shop_return_popup_ticket_edit);
            bt_shop_return_popup_ticket_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_shop_return_popup_itemcode.setText(String.valueOf(s.itemCode));
                    et_shop_return_popup_itemcode.setEnabled(false);
                    et_shop_return_popup_qty.setEnabled(true);
                    sp_shop_return_popup_action.setEnabled(false);
                    et_shop_return_popup_qty.setText(String.valueOf(s.scanQty));
                    int pos = arrayAdpAction.getPosition(s.actions);
                    sp_shop_return_popup_action.setSelection(pos);
                    et_shop_return_popup_qty.requestFocus();
                    flagEdit = true;
                }
            });

            return myView;
        }
    }

    private class MyLoadScanItemAdp extends BaseAdapter {
        public ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItemTicket;

        public MyLoadScanItemAdp(ArrayList<ReceiveShopReturnsScanItemTicket> listReceiveShopReturnsScanItemTicket) {
            this.listReceiveShopReturnsScanItemTicket = listReceiveShopReturnsScanItemTicket;
        }

        @Override
        public int getCount() {
            return listReceiveShopReturnsScanItemTicket.size();
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
            View myView = mInflater.inflate(R.layout.shop_returns_items_ticket, null);
            final ReceiveShopReturnsScanItemTicket s = listReceiveShopReturnsScanItemTicket.get(position);

            TextView tv_shop_return_itemcode_ticket = (TextView) myView.findViewById(R.id.tv_shop_return_itemcode_ticket);
            tv_shop_return_itemcode_ticket.setText(String.valueOf(s.itemCode));

            TextView tv_shop_return_scanqty_ticket = (TextView) myView.findViewById(R.id.tv_shop_return_scanqty_ticket);
            tv_shop_return_scanqty_ticket.setText(String.valueOf(s.scanQty));

            TextView tv_shop_return_trfqty_ticket = (TextView) myView.findViewById(R.id.tv_shop_return_trfqty_ticket);
            tv_shop_return_trfqty_ticket.setText(String.valueOf(s.trfQty));

            TextView tv_shop_return_diffqty_ticket = (TextView) myView.findViewById(R.id.tv_shop_return_diffqty_ticket);
            tv_shop_return_diffqty_ticket.setText(String.valueOf(s.diffQty));

            if (s.diffQty != 0) {
                tv_shop_return_itemcode_ticket.setTextColor(Color.RED);
                tv_shop_return_scanqty_ticket.setTextColor(Color.RED);
                tv_shop_return_trfqty_ticket.setTextColor(Color.RED);
                tv_shop_return_diffqty_ticket.setTextColor(Color.RED);
            }
            return myView;
        }
    }

    private boolean printSticker(String device) {
        if (!bluetoothPort.isConnected()) {
            try {
                btConn(mBluetoothAdapter.getRemoteDevice(device));
            } catch (Exception e) {
                okMessage("Error 2", e.toString());
                return false;
            }
        }
        printBarCode();
        return true;
    }

    private boolean printBarCode() {
        try {
            //testPrint=true;
            byte[] printData = null;
            if (testPrint) {
                printData = objSample_Print.getLabelWasNowHoneyWellTestPrint();
            } else {
                printData = objSample_Print.getUsaBoxPrint(objReceiveShopReturnsGlobal.getpBoxno(), objReceiveShopReturnsGlobal.getpPallettype(), objReceiveShopReturnsGlobal.getpTypename(),
                        objReceiveShopReturnsGlobal.getpQty(), objReceiveShopReturnsGlobal.getpDate(), objReceiveShopReturnsGlobal.getpTime(), objReceiveShopReturnsGlobal.getpRemarks(),
                        objReceiveShopReturnsGlobal.getpPreparedby(), sp_shop_return_printer_copies.getSelectedItem().toString());
            }
            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString());
            return false;
        }
    }

    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();
        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //"BlueTooth Connect"
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        okMessage("IOException", e.toString());
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException", e.toString());
                    }
                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice != null) {
                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        getActivity().registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void bluetoothSetup() {
        clearBtDevData();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    private void btConn(final BluetoothDevice btDev) throws IOException {
        new ReceiveShopReturnsFragment.connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);
                str_temp = params[0].getAddress();
                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result.intValue() == 0) {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                printBarCode();
            } else {
                alert
                        .setTitle("Error 4")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);
        }
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