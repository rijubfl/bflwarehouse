package com.bflgroup.warehouse.ui.transfer;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.building.jafza.BuildingJafzaGLobal;
import com.bflgroup.warehouse.ui.palletbuilding.PalletBuildingBoxTicket;
import com.bflgroup.warehouse.ui.palletbuilding.PalletBuildingFragment;
import com.sewoo.jpos.command.ZPLConst;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TransferFragment extends Fragment {
    private TextView tv_transfer_shopname;
    private Button bt_transfer_scan;
    private ListView lv_transfer_items;
    private TextView tv_transfer_total;
    private TextView tv_transfer_last_transfer;
    private Button bt_transfer_transfer;
    private Button bt_transfer_clear;
    private Button bt_transfer_delete;
    private Spinner sp_transfer_printer;
    private Spinner sp_transfer_type;
    private Spinner sp_transfer_print_copies;
    private CheckBox ch_transfer_printer;
    private TextView tv_transfer_popup_barcode_rfid;
    private TextView tv_transfer_popup_robo_dc_palletno;
    private Button bt_transfer_popup_clear_robo_dc;
    private TextView tv_transfer_popup_barcode_rfid_last_scan;
    private TextView tv_transfer_popup_barcode_rfid_last_scan_barcode;
    private TextView tv_transfer_popup_barcode_rfid_last_scan_totqty;
    private TextView tv_transfer_popup_barcode_rfid_last_result;
    private Button bt_transfer_popup_add;
    private Button bt_transfer_popup_close;
    private TextView tv_transfer_popup_delete_rfid;
    private Button bt_transfer_popup_delete_scan;
    private Button bt_transfer_popup_delete_close;
    private Button bt_transfer_popup_delete_delete;
    private EditText et_transfer_pallet_box_no;
    private EditText et_transfer_popup_reprint_scan;
    private TextView tv_transfer_popup_reprint_shopname;
    private TextView tv_transfer_popup_reprint_trfno;
    private Button bt_transfer_popup_reprint_print;
    private Button bt_transfer_popup_reprint_close;
    private Button bt_transfer_popup_reprint_fetch;
    private Global objGlobal = Global.getInstance();
    private Controls objControls = new Controls();
    private TransferSharedRef saredRef;
    private TransferControl objTransferControl = new TransferControl();
    private TransferGlobal objTransferGlobal = TransferGlobal.getInstance();
    private TransferReceipt objTransferReceipt = new TransferReceipt();
    private BuildingJafzaGLobal objBuildingJafzaGLobal = BuildingJafzaGLobal.getInstance();
    ArrayList<TransferScannedItems> listTransferScannedItems = new ArrayList<TransferScannedItems>();
    TransferScannedItemsAdp objTransferScannedItemsAdp;
    private boolean b_Result;
    private boolean allowChangeShop;

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

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        tv_transfer_shopname = (TextView) view.findViewById(R.id.tv_transfer_shopname);
        et_transfer_pallet_box_no = (EditText) view.findViewById(R.id.et_transfer_pallet_box_no);
        bt_transfer_scan = (Button) view.findViewById(R.id.bt_transfer_scan);
        lv_transfer_items = (ListView) view.findViewById(R.id.lv_transfer_items);
        tv_transfer_total = (TextView) view.findViewById(R.id.tv_transfer_total);
        tv_transfer_last_transfer = (TextView) view.findViewById(R.id.tv_transfer_last_transfer);
        bt_transfer_transfer = (Button) view.findViewById(R.id.bt_transfer_transfer);
        bt_transfer_delete = (Button) view.findViewById(R.id.bt_transfer_delete);
        bt_transfer_clear = (Button) view.findViewById(R.id.bt_transfer_clear);
        sp_transfer_printer = (Spinner) view.findViewById(R.id.sp_transfer_printer);
        sp_transfer_type = (Spinner) view.findViewById(R.id.sp_transfer_type);
        sp_transfer_print_copies = (Spinner) view.findViewById(R.id.sp_transfer_print_copies);
        ch_transfer_printer = (CheckBox) view.findViewById(R.id.ch_transfer_printer);

        allowChangeShop = true;
        saredRef = new TransferSharedRef(getContext());

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_transfer_printer.setAdapter(arrayAdpYellow);
            if (saredRef.loadPrinter() != "") {
                sp_transfer_printer.setSelection(arrayAdpYellow.getPosition(saredRef.loadPrinter()));
            }
        }

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("RFID");//0 R
        arr.add("Barcode");//1 B
        arr.add("Itemcode");//2 I
        arr.add("Box");//3 P
        if (objGlobal.getUserName().equals("RIJU")) arr.add("ROBO Direct");//4 D
        if (objGlobal.getWarehouse().equals("JAFZA")) arr.add("ROBO Direct");//4 D
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_transfer_type.setAdapter(arrayAdp);

        arr = new ArrayList<String>();
        arr.add("1");
        arr.add("2");
        arr.add("3");
        arr.add("4");
        arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_transfer_print_copies.setAdapter(arrayAdp);
        if (saredRef.loadPrintCopies().equals("")) {
            saredRef.savePrintCopies("1");
        }
        sp_transfer_print_copies.setSelection(arrayAdp.getPosition(saredRef.loadPrintCopies().toString()));

        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        listTransferScannedItems.clear();
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));

        et_transfer_pallet_box_no.setEnabled(false);
        if (saredRef.loadShopName() != "") {
            if (saredRef.loadScanType().equals("R")) sp_transfer_type.setSelection(0);
            if (saredRef.loadScanType().equals("B")) sp_transfer_type.setSelection(1);
            if (saredRef.loadScanType().equals("I")) sp_transfer_type.setSelection(2);
            if (saredRef.loadScanType().equals("P")) sp_transfer_type.setSelection(3);
            if (saredRef.loadScanType().equals("D")) sp_transfer_type.setSelection(4);
            sp_transfer_type.setEnabled(false);
            tv_transfer_shopname.setText(saredRef.loadShopName());
            et_transfer_pallet_box_no.setText(saredRef.loadPallet());
            tv_transfer_shopname.setEnabled(false);
            allowChangeShop = false;
        }

        sp_transfer_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 3) {
                    et_transfer_pallet_box_no.setHint("Box / Tote");
                    et_transfer_pallet_box_no.setEnabled(true);
                    tv_transfer_shopname.setEnabled(false);
                } else {
                    et_transfer_pallet_box_no.setHint("");
                    et_transfer_pallet_box_no.setEnabled(false);
                    tv_transfer_shopname.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        bt_transfer_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boxPallet = et_transfer_pallet_box_no.getText().toString();
                String shop = tv_transfer_shopname.getText().toString();
                String printer = sp_transfer_printer.getSelectedItem().toString();
                if (printer.isEmpty() || printer.toUpperCase().contains("SELECT")) {
                    okMessage("Transfer", "Please select printer");
                } else {
                    saredRef.savePrintCopies(sp_transfer_print_copies.getSelectedItem().toString());
                    saredRef.savePrinter(sp_transfer_printer.getSelectedItem().toString());
                    if (sp_transfer_type.getSelectedItemId() == 0 || sp_transfer_type.getSelectedItemId() == 1) {
                        openPopupScanBarcodeRfid();
                    } else if (sp_transfer_type.getSelectedItemId() == 2) {
                        b_Result = scanItemcode(shop);
                        if (b_Result) openPopupScanBarcodeRfid();
                    } else if (sp_transfer_type.getSelectedItemId() == 3) {
                        b_Result = scanBoxPallet(boxPallet);
                    } else if (sp_transfer_type.getSelectedItemId() == 4) {
                        b_Result = scanRoboDc(shop);
                        if (b_Result) openPopupScanBarcodeRfid();
                    }
                }

            }
        });

        bt_transfer_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all scanned items?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearAll();
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

        bt_transfer_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to make transfer fot the scanned items?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transfer();
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

        ch_transfer_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupReprint();
                } else {
                    // not checked
                }
            }
        });

        tv_transfer_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowChangeShop) {
                    Dialog dialog;
                    ArrayList<String> arraylist;
                    if (sp_transfer_type.getSelectedItemId() == 3)
                        arraylist = objTransferControl.loadShops("E");
                    else if (sp_transfer_type.getSelectedItemId() == 4)
                        arraylist = objTransferControl.loadShops("D");
                    else
                        arraylist = objTransferControl.loadShops("");
                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.searchable_shopname);
                    dialog.getWindow().setLayout(600, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arraylist);
                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            tv_transfer_shopname.setText(adapter.getItem(position));
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        bt_transfer_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupDelete();
            }
        });

        searchflags = false;
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();

        return view;
    }

    private boolean transfer() {
        String shopname = tv_transfer_shopname.getText().toString();
        String pallet = et_transfer_pallet_box_no.getText().toString();
        String printer = sp_transfer_printer.getSelectedItem().toString();
        String toteid = "";
        String selType = "";
        objBuildingJafzaGLobal.setBoxNo("");
        objTransferGlobal.setTrfRecNo("");
        try {
            if (sp_transfer_type.getSelectedItemId() == 0) selType = "R";
            if (sp_transfer_type.getSelectedItemId() == 1) selType = "B";
            if (sp_transfer_type.getSelectedItemId() == 2) selType = "I";
            if (sp_transfer_type.getSelectedItemId() == 3) selType = "P";
            if (sp_transfer_type.getSelectedItemId() == 4) selType = "D";
            if (shopname.isEmpty()) {
                okMessage("Transfer", "Shop Name is empty");
                return false;
            }
            if (printer.isEmpty() || printer.toUpperCase().contains("SELECT")) {
                objGlobal.setErrorMessage("Please select printer");
                b_Result = false;
            }
            b_Result = objTransferControl.validateTransfer(selType, shopname);
            if (!b_Result) {
                okMessage("Transfer", "validateTransfer: " + objGlobal.getErrorMessage());
                return false;
            }
            if (selType.equals("D")) {
                b_Result = objTransferControl.transferCreatePairingSorting();
                if (!b_Result) {
                    okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
                    return false;
                }
                b_Result = objTransferControl.createTransfer(shopname, toteid);
                if (!b_Result) {
                    okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
                    return false;
                }
            } else {
                b_Result = objTransferReceipt.transferReceipt(shopname, pallet, toteid);
                if (!b_Result) {
                    okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
                    return false;
                }
            }
            if (!objBuildingJafzaGLobal.getBoxNo().isEmpty()) {
                tv_transfer_last_transfer.setText("Box.No.: " + objBuildingJafzaGLobal.getBoxNo() + ", Shop Name.: " + shopname);
            }
            if (!objTransferGlobal.getTrfRecNo().isEmpty()) {
                tv_transfer_last_transfer.setText("Trf.No.: " + objTransferGlobal.getTrfRecNo() + ", Shop Name.: " + shopname);
                b_Result = objTransferControl.forPrint(shopname, objTransferGlobal.getTrfRecNo());
                if (!b_Result) {
                    okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
                    vibrate(100);
                }
                if (objGlobal.getBluetoothDevicesAvailable().equals("Y")) {
                    if (!printSticker(printer)) {
                        okMessage("Transfer", "Printer Error, Pleasse reprint..");
                        vibrate(100);
                    }
                }
            }
            clearAll();
            return true;
        } catch (Exception e) {
            okMessage("Transfer", e.toString());
            return false;
        }
    }

    private void openPopupReprint() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_transfer_reprint);

        tv_transfer_popup_reprint_shopname = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_reprint_shopname);
        tv_transfer_popup_reprint_trfno = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_reprint_trfno);
        et_transfer_popup_reprint_scan = (EditText) myDialog.findViewById(R.id.et_transfer_popup_reprint_scan);
        bt_transfer_popup_reprint_fetch = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_fetch);
        bt_transfer_popup_reprint_print = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_print);
        bt_transfer_popup_reprint_close = (Button) myDialog.findViewById(R.id.bt_transfer_popup_reprint_close);
        et_transfer_popup_reprint_scan.requestFocus();

        et_transfer_popup_reprint_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String scan = et_transfer_popup_reprint_scan.getText().toString().toUpperCase();
                    String shopname = tv_transfer_popup_reprint_shopname.getText().toString().toUpperCase();
                    if (scan.isEmpty()) {
                        okMessage("Transfer", "Please Enter Toteid / Transfer number");
                        et_transfer_popup_reprint_scan.requestFocus();
                        vibrate(100);
                    } else {
                        b_Result = objTransferControl.reprintTransferShopName(scan, shopname);
                        tv_transfer_popup_reprint_shopname.setText(objTransferGlobal.getReprintShop());
                        tv_transfer_popup_reprint_trfno.setText(objTransferGlobal.getReprintTrfno());
                        if (!b_Result) {
                            okMessage("Transfer Re-print", objGlobal.getErrorMessage());
                            et_transfer_popup_reprint_scan.requestFocus();
                            vibrate(100);
                        }
                    }
                }
                return false;
            }
        });

        bt_transfer_popup_reprint_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reprintTransfer()) {
                    ch_transfer_printer.setChecked(false);
                    myDialog.dismiss();
                }
            }
        });

        bt_transfer_popup_reprint_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scan = et_transfer_popup_reprint_scan.getText().toString().toUpperCase();
                String shopname = tv_transfer_popup_reprint_shopname.getText().toString().toUpperCase();
                if (scan.isEmpty()) {
                    okMessage("Transfer", "Please Enter Toteid / Transfer number");
                    et_transfer_popup_reprint_scan.requestFocus();
                    vibrate(100);
                }
                b_Result = objTransferControl.reprintTransferShopName(scan, shopname);
                tv_transfer_popup_reprint_shopname.setText(objTransferGlobal.getReprintShop());
                tv_transfer_popup_reprint_trfno.setText(objTransferGlobal.getReprintTrfno());
                if (!b_Result) {
                    okMessage("Transfer Re-print", objGlobal.getErrorMessage());
                    et_transfer_popup_reprint_scan.requestFocus();
                    vibrate(100);
                }
            }
        });

        bt_transfer_popup_reprint_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch_transfer_printer.setChecked(false);
                myDialog.dismiss();
            }
        });

        tv_transfer_popup_reprint_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                ArrayList<String> arraylist;
                arraylist = objTransferControl.loadShops("");
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.searchable_shopname);
                dialog.getWindow().setLayout(600, 1500);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arraylist);
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tv_transfer_popup_reprint_shopname.setText(adapter.getItem(position));
                        tv_transfer_popup_reprint_trfno.setText("");
                        dialog.dismiss();
                    }
                });

            }
        });

        myDialog.show();
    }

    private void openPopupDelete() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_transfer_delete);

        tv_transfer_popup_delete_rfid = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_delete_rfid);
        bt_transfer_popup_delete_scan = (Button) myDialog.findViewById(R.id.bt_transfer_popup_delete_scan);
        bt_transfer_popup_delete_close = (Button) myDialog.findViewById(R.id.bt_transfer_popup_delete_close);
        bt_transfer_popup_delete_delete = (Button) myDialog.findViewById(R.id.bt_transfer_popup_delete_delete);

        bt_transfer_popup_delete_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteScanRfid()) {
                    myDialog.dismiss();
                }
            }
        });

        bt_transfer_popup_delete_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        bt_transfer_popup_delete_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scanRfid();
            }
        });
        myDialog.show();
    }

    private void openPopupScanBarcodeRfid() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_transfer_scan);

        tv_transfer_popup_barcode_rfid = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_barcode_rfid);
        tv_transfer_popup_robo_dc_palletno = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_robo_dc_palletno);
        tv_transfer_popup_barcode_rfid_last_scan = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_barcode_rfid_last_scan);
        tv_transfer_popup_barcode_rfid_last_scan_barcode = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_barcode_rfid_last_scan_barcode);
        tv_transfer_popup_barcode_rfid_last_scan_totqty = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_barcode_rfid_last_scan_totqty);
        tv_transfer_popup_barcode_rfid_last_result = (TextView) myDialog.findViewById(R.id.tv_transfer_popup_barcode_rfid_last_result);
        bt_transfer_popup_clear_robo_dc = (Button) myDialog.findViewById(R.id.bt_transfer_popup_clear_robo_dc);
        bt_transfer_popup_add = (Button) myDialog.findViewById(R.id.bt_transfer_popup_add);
        bt_transfer_popup_close = (Button) myDialog.findViewById(R.id.bt_transfer_popup_close);

        sp_transfer_type.setEnabled(false);
        tv_transfer_shopname.setEnabled(false);
        tv_transfer_popup_robo_dc_palletno.setEnabled(false);
        tv_transfer_popup_robo_dc_palletno.setHint("");
        bt_transfer_popup_clear_robo_dc.setEnabled(false);

        if (sp_transfer_type.getSelectedItemId() == 0) saredRef.saveScanType("R");
        if (sp_transfer_type.getSelectedItemId() == 1) saredRef.saveScanType("B");
        if (sp_transfer_type.getSelectedItemId() == 2) saredRef.saveScanType("I");
        if (sp_transfer_type.getSelectedItemId() == 3) saredRef.saveScanType("P");
        if (sp_transfer_type.getSelectedItemId() == 4) {
            tv_transfer_popup_robo_dc_palletno.setEnabled(true);
            bt_transfer_popup_clear_robo_dc.setEnabled(true);
            tv_transfer_popup_robo_dc_palletno.setHint("ROBO DC Cont.No. / Pallet No.");
            saredRef.saveScanType("D");
        }
        tv_transfer_popup_barcode_rfid.setOnTouchListener(new View.OnTouchListener() {
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

        tv_transfer_popup_barcode_rfid.setSingleLine(true);
        tv_transfer_popup_robo_dc_palletno.setSingleLine(true);
        tv_transfer_popup_barcode_rfid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("Linoop", "Before "+s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("Linoop", "onTextChanged "+s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String scan = s.toString();
                if (scan.length() > 10) {
                    tv_transfer_popup_robo_dc_palletno.setEnabled(false);
                    scanRfidBarcode(scan);
                }
            }
        });

        bt_transfer_popup_clear_robo_dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_transfer_popup_robo_dc_palletno.setEnabled(true);
                tv_transfer_popup_robo_dc_palletno.setText("");
                tv_transfer_popup_robo_dc_palletno.requestFocus();
            }
        });
        bt_transfer_popup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rfid = tv_transfer_popup_barcode_rfid.getText().toString();
                scanRfidBarcode(rfid);
            }
        });

        bt_transfer_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        tv_transfer_popup_barcode_rfid_last_scan_totqty.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        if (sp_transfer_type.getSelectedItemId() == 4)
            tv_transfer_popup_robo_dc_palletno.requestFocus();
        else
            tv_transfer_popup_barcode_rfid.requestFocus();
        myDialog.show();
    }

    boolean scanRoboDc(String shop) {
        sp_transfer_type.setEnabled(false);
        tv_transfer_shopname.setEnabled(false);
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        return true;
    }

    boolean scanItemcode(String shop) {
        if (shop.isEmpty()) {
            okMessage("Transfer Box/Pallet", "Please Select Shop");
            tv_transfer_shopname.requestFocus();
            return false;
        }
        sp_transfer_type.setEnabled(false);
        tv_transfer_shopname.setEnabled(false);
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        return true;
    }

    boolean scanBoxPallet(String boxPallet) {
        if (boxPallet.isEmpty()) {
            okMessage("Transfer Box/Pallet", "Please Enter Box/Pallet");
            et_transfer_pallet_box_no.requestFocus();
            return false;
        }
        b_Result = objTransferControl.validateBoxPallet(boxPallet);
        if (!b_Result) {
            okMessage("Transfer Box/Pallet/Tote", objGlobal.getErrorMessage());
            et_transfer_pallet_box_no.requestFocus();
            return false;
        }
        et_transfer_pallet_box_no.setText(objTransferGlobal.getBoxTrfBoxNo());
        tv_transfer_shopname.setText(objTransferGlobal.getShopName());
        sp_transfer_type.setEnabled(false);
        tv_transfer_shopname.setEnabled(false);
        et_transfer_pallet_box_no.setEnabled(false);
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        return true;
    }

    boolean scanRfidBarcode(String rfid) {
        tv_transfer_popup_barcode_rfid_last_scan.setText("");
        tv_transfer_popup_barcode_rfid_last_scan_barcode.setText("");
        tv_transfer_popup_barcode_rfid_last_result.setText("");
        String scan = objControls.replaceString(rfid.toUpperCase());
        String shop = tv_transfer_shopname.getText().toString();
        String contno = tv_transfer_popup_robo_dc_palletno.getText().toString();
        String scanType = "";
        if (sp_transfer_type.getSelectedItemId() == 0) scanType = "R";
        if (sp_transfer_type.getSelectedItemId() == 1) scanType = "B";
        if (sp_transfer_type.getSelectedItemId() == 2) scanType = "I";
        if (sp_transfer_type.getSelectedItemId() == 3) scanType = "P";
        if (sp_transfer_type.getSelectedItemId() == 4) scanType = "D";
        saredRef.savePrinter(sp_transfer_printer.getSelectedItem().toString());
        saredRef.saveShopName(shop);
        int qty = 1;
        if (scan.isEmpty()) {
            scan = "";
        }
        if (scanType.equals("D")) {
            if (contno.isEmpty()) {
                tv_transfer_popup_barcode_rfid_last_result.setText("Please scan ROBO Direct checking container / pallet number");
                tv_transfer_popup_barcode_rfid_last_result.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
                vibrate(100);
                tv_transfer_popup_robo_dc_palletno.setText("");
                return false;
            }
        }
        if (scan.isEmpty()) {
            tv_transfer_popup_barcode_rfid_last_result.setText("Please scan RFID / Barcode / Itemcde");
            tv_transfer_popup_barcode_rfid_last_result.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            tv_transfer_popup_barcode_rfid.setText("");
            return false;
        }
        if (scan.length() > 50) {
            tv_transfer_popup_barcode_rfid_last_result.setText("Please Double check the scan RFID / Barcode / Itemcode");
            tv_transfer_popup_barcode_rfid_last_result.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            tv_transfer_popup_barcode_rfid.setText("");
            return false;
        }
        tv_transfer_popup_barcode_rfid_last_scan.setText(scan);
        if (scanType.equals("R"))
            b_Result = objTransferControl.validateRfid(false, scan, qty, shop);
        if (scanType.equals("B"))
            b_Result = objTransferControl.validateBarcode(false, scan, qty, shop);
        if (scanType.equals("I"))
            b_Result = objTransferControl.validateItemcode(false, scan, qty, shop, scanType);
        if (scanType.equals("D"))
            b_Result = objTransferControl.validateRoboDirectCheckingResult(false, contno, scan, qty, shop);
        if (!b_Result) {
            if (objGlobal.getErrorMessage().contains("TransferControl")) {
                okMessage("Transfer", objGlobal.getErrorMessage());
            } else {
                tv_transfer_popup_barcode_rfid_last_result.setText(objGlobal.getErrorMessage());
                tv_transfer_popup_barcode_rfid_last_result.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
                vibrate(100);
            }
            tv_transfer_popup_barcode_rfid.setText("");
            return false;
        }
        if (objTransferGlobal.getShopName().isEmpty()) {
            okMessage("Transfer", "Shop name is empty");
            return false;
        }
        allowChangeShop = false;
        tv_transfer_shopname.setText(objTransferGlobal.getShopName());
        saredRef.saveShopName(objTransferGlobal.getShopName());
        tv_transfer_popup_barcode_rfid_last_scan_barcode.setText(objTransferGlobal.getScanBarcode());
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        tv_transfer_popup_barcode_rfid_last_scan_totqty.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        tv_transfer_popup_barcode_rfid.setText("");
        return true;
    }

    private void clearAll() {
        b_Result = objTransferControl.deleteScan("");
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage());
        }
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        tv_transfer_shopname.setText("");
        et_transfer_pallet_box_no.setText("");
        allowChangeShop = true;
        sp_transfer_type.setEnabled(true);
        tv_transfer_shopname.setEnabled(true);
        et_transfer_pallet_box_no.setEnabled(false);
        if (sp_transfer_type.getSelectedItemId() == 3) et_transfer_pallet_box_no.setEnabled(true);
        saredRef.saveScanType("");
        saredRef.saveShopName("");
        saredRef.savePallet("");
        saredRef.savePrinter("");
    }

    private boolean reprintTransfer() {
        String scan = et_transfer_popup_reprint_scan.getText().toString().toUpperCase();
        String shopname = tv_transfer_popup_reprint_shopname.getText().toString().toUpperCase();
        String trfno = tv_transfer_popup_reprint_trfno.getText().toString().toUpperCase();
        if (trfno.isEmpty()) {
            okMessage("Transfer", "Please Enter Transfer number");
            tv_transfer_popup_reprint_trfno.requestFocus();
            vibrate(100);
            return false;
        }
        if (shopname.isEmpty()) {
            okMessage("Transfer", "Please select Shopname");
            tv_transfer_popup_reprint_shopname.requestFocus();
            vibrate(100);
            return false;
        }
        if (scan.isEmpty()) {
            okMessage("Transfer", "Please Enter Toteid / Transfer number");
            et_transfer_popup_reprint_scan.requestFocus();
            vibrate(100);
            return false;
        }
        b_Result = objTransferControl.forPrint(shopname, trfno);
        if (!b_Result) {
            okMessage("Transfer", "transferReceipt: " + objGlobal.getErrorMessage());
            return false;
        }
        if (objGlobal.getBluetoothDevicesAvailable().equals("Y")) {
            if (!printSticker(sp_transfer_printer.getSelectedItem().toString())) {
                okMessage("Transfer", "Printer Error, Pleasse reprint..");
                return false;
            }
        }
        return true;
    }

    private boolean deleteScanRfid() {
        String scan = objControls.replaceString(tv_transfer_popup_delete_rfid.getText().toString());
        if (scan.isEmpty()) {
            okMessage("Transfer", "Please scan RFID");
            vibrate(100);
            return false;
        }
        b_Result = objTransferControl.deleteScan(scan);
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage());
            return false;
        }
        listTransferScannedItems.clear();
        listTransferScannedItems = objTransferControl.loadScannedItems();
        objTransferScannedItemsAdp = new TransferFragment.TransferScannedItemsAdp(listTransferScannedItems);
        lv_transfer_items.setAdapter(objTransferScannedItemsAdp);
        tv_transfer_total.setText(String.valueOf(objTransferGlobal.getTotalScan()));
        return true;
    }

    private class TransferScannedItemsAdp extends BaseAdapter {
        public ArrayList<TransferScannedItems> listTransferScannedItems;

        public TransferScannedItemsAdp(ArrayList<TransferScannedItems> listTransferScannedItems) {
            this.listTransferScannedItems = listTransferScannedItems;
        }

        @Override
        public int getCount() {
            return listTransferScannedItems.size();
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
            View myView = mInflater.inflate(R.layout.transfer_scan_items_ticket, null);
            final TransferScannedItems s = listTransferScannedItems.get(position);

            TextView tv_transfer_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_transfer_ticket_itemcode);
            tv_transfer_ticket_itemcode.setText(String.valueOf(s.itemcode));

            TextView tv_transfer_ticket_description = (TextView) myView.findViewById(R.id.tv_transfer_ticket_description);
            tv_transfer_ticket_description.setText(String.valueOf(s.description));

            TextView tv_transfer_ticket_qty = (TextView) myView.findViewById(R.id.tv_transfer_ticket_qty);
            tv_transfer_ticket_qty.setText(String.valueOf(s.qty));

            return myView;
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
        new TransferFragment.connBT().execute(btDev);
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
                printData = objSample_Print.getTransferPrint(
                        objTransferGlobal.getPshopname(), objTransferGlobal.getPtrfno(), objTransferGlobal.getPboxno(), objTransferGlobal.getPqty(),
                        objTransferGlobal.getPdeldate(), objTransferGlobal.getPtrfdate(), objTransferGlobal.getPtoteid(), objTransferGlobal.getPremarks(),
                        objTransferGlobal.getPpreparedby(), sp_transfer_print_copies.getSelectedItem().toString());
            }
            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString());
            return false;
        }
    }

    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
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
        vibrate(500);
    }
}