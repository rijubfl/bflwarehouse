package com.bflgroup.warehouse.ui.palletbuilding;

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.transfer.TransferFragment;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    private TextView tv_pallet_building_tot_count;
    private TextView tv_pallet_building_tot_qty;
    private Spinner sp_pallet_building_category;
    private Spinner sp_pallet_building_printer;
    private CheckBox ch_pallet_building_printer;
    private Spinner sp_pallet_building_print_copies;
    private EditText et_buildpallet_popup_reprint_palletno;
    private Button bt_buildpallet_popup_reprint_print;
    private Button bt_buildpallet_popup_reprint_close;

    private boolean b_Result;
    private String s_Result;

    private BarcodePrinting objSample_Print;
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();
    private BluetoothPort bluetoothPort;
    private BroadcastReceiver connectDevice;
    private Thread btThread;
    private boolean testPrint=false;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver searchFinish;
    private Vector<BluetoothDevice> remoteDevices;
    private ArrayAdapter<String> adapter;
    private boolean searchflags;
    private static final int REQUEST_ENABLE_BT = 2;

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
        sp_pallet_building_category = (Spinner) view.findViewById(R.id.sp_pallet_building_category);
        sp_pallet_building_printer = (Spinner) view.findViewById(R.id.sp_pallet_building_printer);
        tv_pallet_building_tot_count = (TextView) view.findViewById(R.id.tv_pallet_building_tot_count);
        tv_pallet_building_tot_qty = (TextView) view.findViewById(R.id.tv_pallet_building_tot_qty);
        ch_pallet_building_printer = (CheckBox) view.findViewById(R.id.ch_pallet_building_printer);
        sp_pallet_building_print_copies = (Spinner) view.findViewById(R.id.sp_pallet_building_print_copies);

        ArrayList<PalletBuildingBoxTicket> listPalletBuildingBoxTicket = objPalletBuildingControl.loadPalletBuildBoxDetail();
        objMyPalletBuildingBoxTicketAdp = new PalletBuildingFragment.MyPalletBuildingBoxTicketAdp(listPalletBuildingBoxTicket);
        lv_pallet_building_details.setAdapter(objMyPalletBuildingBoxTicketAdp);
        tv_pallet_building_tot_count.setText(String.valueOf(objPalletBuildingGlobal.getTotCnt()));
        tv_pallet_building_tot_qty.setText(String.valueOf(objPalletBuildingGlobal.getTotQty()));

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("USA");
        arr.add("TCM");
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_pallet_building_category.setAdapter(arrayAdp);

        arr = new ArrayList<String>();
        arr.add("1");
        arr.add("2");
        arr.add("3");
        arr.add("4");
        arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_pallet_building_print_copies.setAdapter(arrayAdp);

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Transfer",objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_pallet_building_printer.setAdapter(arrayAdpYellow);
        }

        ch_pallet_building_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupReprint();
                }
            }
        });

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
                    if (sp_pallet_building_category.getSelectedItem().toString().equals("TCM")) { }
                    if (sp_pallet_building_category.getSelectedItem().toString().equals("USA")) {
                        b_Result = objPalletBuildingControl.validateBoxTotUsa(toteId);
                    }
                    if (!b_Result) {
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
                if (sp_pallet_building_category.getSelectedItem().toString().equals("TCM")) { }
                if (sp_pallet_building_category.getSelectedItem().toString().equals("USA")) {
                    b_Result = objPalletBuildingControl.validateBoxTotUsa(toteId);
                }
                if (!b_Result) {
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
                                    if (sp_pallet_building_category.getSelectedItem().toString().equals("TCM")) { }
                                    if (sp_pallet_building_category.getSelectedItem().toString().equals("USA")) {
                                        b_Result = objPalletBuildingControl.savePalletUsa(et_pallet_building_remarks.getText().toString().trim().toUpperCase());
                                    }
                                    if (!b_Result) {
                                        okMessage("Pallet", objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = clearAll();
                                        if (!b_Result) {
                                            okMessage("Pallet:clearAll", objGlobal.getErrorMessage());
                                        } else {
                                            b_Result = objPalletBuildingControl.forPrint(objPalletBuildingGlobal.getPalletNo());
                                            if (!b_Result) {
                                                okMessage("Pallet", "transferReceipt: " + objGlobal.getErrorMessage());
                                            } else {
                                                if (!printSticker(sp_pallet_building_printer.getSelectedItem().toString())) {
                                                    okMessage("Pallet", "Printer Error, Pleasse reprint..");
                                                    vibrate(100);
                                                }
                                            }
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

        searchflags = false;
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();

        return view;
    }

    private void openPopupReprint() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_pallet_reprint);

        et_buildpallet_popup_reprint_palletno = (EditText) myDialog.findViewById(R.id.et_buildpallet_popup_reprint_palletno);
        bt_buildpallet_popup_reprint_print = (Button) myDialog.findViewById(R.id.bt_buildpallet_popup_reprint_print);
        bt_buildpallet_popup_reprint_close = (Button) myDialog.findViewById(R.id.bt_buildpallet_popup_reprint_close);
        et_buildpallet_popup_reprint_palletno.requestFocus();

        bt_buildpallet_popup_reprint_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reprintPallert()) {
                    ch_pallet_building_printer.setChecked(false);
                    myDialog.dismiss();
                }
            }
        });

        bt_buildpallet_popup_reprint_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch_pallet_building_printer.setChecked(false);
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    private boolean reprintPallert() {
        String scan = et_buildpallet_popup_reprint_palletno.getText().toString().toUpperCase();
        if (scan.isEmpty()) {
            okMessage("Pallet", "Please Enter Pallet number");
            et_buildpallet_popup_reprint_palletno.requestFocus();
            vibrate(100);
            return false;
        }
        b_Result = objPalletBuildingControl.forPrint(scan);
        if (!b_Result) {
            okMessage("Pallet", objGlobal.getErrorMessage());
            return false;
        }
        if (!printSticker(sp_pallet_building_printer.toString())) {
            okMessage("Transfer", "Printer Error, Pleasse reprint..");
            return false;
        }
        return true;
    }

    boolean clearAll() {
        try {
            b_Result = objPalletBuildingControl.clearTable();
            if (!b_Result) {
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
        } catch (Exception ex) {
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
                        okMessage("IOException",e.toString());
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException",e.toString());
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
        new PalletBuildingFragment.connBT().execute(btDev);
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
                printData = objSample_Print.getUsaPalletPrint(objPalletBuildingGlobal.getpPalletno(), objPalletBuildingGlobal.getpBoxcnt(),
                        objPalletBuildingGlobal.getpRemarks(), objPalletBuildingGlobal.getpPallettype(),objPalletBuildingGlobal.getpTypename(),
                        objPalletBuildingGlobal.getpGroupname(),objPalletBuildingGlobal.getpPreparedby(), objPalletBuildingGlobal.getpDate(),
                        objPalletBuildingGlobal.getpTime(),sp_pallet_building_print_copies.getSelectedItem().toString());
            }
            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString());
            return false;
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