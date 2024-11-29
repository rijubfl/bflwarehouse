package com.bflgroup.warehouse.ui.updateboxqty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.palletbuilding.PalletBuildingFragment;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class updateBoxQuantityFragment extends Fragment {

    private Global objGlobal = Global.getInstance();

    EditText et_tote_id;
    EditText et_itemcode;
    Button bt_tote_process;
    Button bt_clear_field;
    Button bt_remove_itemcode;
    Button bt_status_Save;
    private String toteid = "";
    private String itemcode = "";
    Boolean strflg = false;
    private ListView lv_div_seperate_details;
    private TextView totalBoxQty;
    private TextView reducedQty;
    private TextView NewBoxTotal;
    private RadioGroup radioGroup;

    private String Radioselect = "";
    private RadioButton rbAdd;
    private RadioButton rbRemove;

    ArrayList<UpdateBoxItem> objUpdateboxItem = new ArrayList<UpdateBoxItem>();
    private UpdateBoxQuantityControl objUpdateBoxQuantityControl = new UpdateBoxQuantityControl();
    MyUpdateBoxQuantity objMyUpdateBoxQuantity = null;

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

    public updateBoxQuantityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_box_quantity, container, false);
        et_tote_id = view.findViewById(R.id.et_tote_id);
        et_itemcode = view.findViewById(R.id.et_item_code);
        bt_tote_process = view.findViewById(R.id.bt_tote_process);
        bt_remove_itemcode = view.findViewById(R.id.bt_remove_itemcode);
        rbAdd = view.findViewById(R.id.add);
        rbRemove = view.findViewById(R.id.remove);
        bt_status_Save = view.findViewById(R.id.bt_status_Save);
        bt_clear_field = view.findViewById(R.id.bt_clear_field);
        lv_div_seperate_details = view.findViewById(R.id.lv_div_det);
        totalBoxQty = view.findViewById(R.id.totalBoxQty);
        reducedQty = view.findViewById(R.id.reducedQty);
        NewBoxTotal = view.findViewById(R.id.NewBoxTotal);
        radioGroup = view.findViewById(R.id.radioGroup);

        UpdateBoxSharedRef objUpdateBoxSharedRef = new UpdateBoxSharedRef(getContext());
        et_tote_id.requestFocus();
        if (objUpdateBoxSharedRef.loadToteid() != "") {
            toteid = objUpdateBoxSharedRef.loadToteid();
            et_tote_id.setText(toteid);
            et_tote_id.setEnabled(false);
            et_itemcode.requestFocus();
            GetScanresult();
            totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
            reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
            NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
        }
        toteid = et_tote_id.getText().toString();

        et_tote_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (GetScanresult()) {
                        et_tote_id.requestFocus();
                        et_tote_id.setFocusable(true);
                        strflg = true;
                        return true;
                    } else {
                        et_tote_id.requestFocus();
                        et_tote_id.setFocusable(true);
                        return true;
                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_tote_id.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        });

        et_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        if (Updateitemcode()) {
                            strflg = true;
                            totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
                            reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
                            NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
                            return true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }
        });

        bt_tote_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GetScanresult()) {
                    et_tote_id.requestFocus();
                    et_tote_id.setFocusable(true);
                    strflg = true;

                } else {
                    et_tote_id.requestFocus();
                    et_tote_id.setFocusable(true);
                }
            }
        });

        bt_remove_itemcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Updateitemcode()) {
                        strflg = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        bt_clear_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clear();
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

        bt_status_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (objUpdateboxItem.size() >= 1 && toteid != "") {

                        if (objUpdateBoxQuantityControl.InsertDetails(toteid, itemcode)) {
                            Log.e("return", "Build");
                            okMessage("SUCCESS", "Successfully Updated Box Quantity", getContext());
                            clear();
                            lv_div_seperate_details.setAdapter(null);
                        } else {
                            okMessage("Error", objGlobal.getErrorMessage(), getContext());
                        }
                    } else {
                        Log.e("return", "Not Build");
                        okMessage("Alert", "Please Scan Toteid", getContext());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String RadioOption = rb.getText().toString();
                if (RadioOption == "Remove item Qty") {
                    Radioselect = "Remove";
                } else {
                    Radioselect = "Add";
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

    public boolean GetScanresult() {
        toteid = et_tote_id.getText().toString();
        Log.e("transferno", toteid);
        if (et_tote_id.getText().toString().isEmpty()) {
            okMessage("Alert", "Please Scan Trf no/Toteid", getContext());
            return false;
        } else {
            try {
                objUpdateboxItem = objUpdateBoxQuantityControl.ScanToteId(getContext(), toteid);
                et_tote_id.requestFocus();

                if (objUpdateboxItem == null) {
                    et_tote_id.requestFocus();
                    et_tote_id.setEnabled(true);
                    et_tote_id.setText("");
                    return false;
                } else {
                    objMyUpdateBoxQuantity = new MyUpdateBoxQuantity(objUpdateboxItem);
                    lv_div_seperate_details.setAdapter(objMyUpdateBoxQuantity);
                    et_tote_id.setEnabled(false);
                    et_itemcode.requestFocus();
                    totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
                    reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
                    NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
                    return true;

                }
            } catch (Exception ex) {
                objGlobal.setErrorMessage(ex.toString());
                return false;
            }
        }
    }

    public boolean Updateitemcode() throws SQLException {
        toteid = et_tote_id.getText().toString();
        itemcode = et_itemcode.getText().toString();
        Log.e("itemcode", itemcode);
        if (et_itemcode.getText().toString().isEmpty() || et_tote_id.getText().toString().isEmpty()) {
            okMessage("Alert", "Please Scan ItemCode", getContext());
            return false;
        } else {
            if (rbRemove.isChecked()) {
                Radioselect = "Remove";
            }else {
                Radioselect = "Add";
            }
            objUpdateboxItem = objUpdateBoxQuantityControl.ScanItemCode(getContext(), toteid, itemcode, Radioselect);
            objMyUpdateBoxQuantity = new MyUpdateBoxQuantity(objUpdateboxItem);
            lv_div_seperate_details.setAdapter(objMyUpdateBoxQuantity);
            et_itemcode.setText("");

        }
        return true;
    }

    private class MyUpdateBoxQuantity extends BaseAdapter {
        public ArrayList<UpdateBoxItem> UpdateBoxItem;

        public MyUpdateBoxQuantity(ArrayList<UpdateBoxItem> UpdateBoxItem) {
            this.UpdateBoxItem = UpdateBoxItem;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return UpdateBoxItem.size();
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
            View myView = mInflater.inflate(R.layout.itemcode_quantity_details, null);
            final UpdateBoxItem s = UpdateBoxItem.get(position);
            TextView tv_itemcode = (TextView) myView.findViewById(R.id.tv_itemcode);
            tv_itemcode.setText(String.valueOf(s.ItemCode));
            TextView tv_BoxQuantity = (TextView) myView.findViewById(R.id.tv_BoxQuantity);
            tv_BoxQuantity.setText(String.valueOf(s.BoxQuantity));
            TextView tv_qty = (TextView) myView.findViewById(R.id.tv_qty);
            if (Integer.parseInt(s.Qty) < 0) {
                String styledText = "<font color='red'>" + s.Qty + "</font>";
                tv_qty.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            } else if (Integer.parseInt(s.Qty) > 0) {
                String styledText = "<font color='#008a25'>" + s.Qty + "</font>";
                tv_qty.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            } else {
                tv_qty.setText(String.valueOf(s.Qty));
            }
            TextView new_qty = (TextView) myView.findViewById(R.id.new_qty);
            if (Integer.parseInt(s.UpdatedBoxQuantity) > 0) {
                new_qty.setText(Html.fromHtml("<b>" + s.UpdatedBoxQuantity + "</b>"), TextView.BufferType.SPANNABLE);
            } else
                new_qty.setText(String.valueOf(s.UpdatedBoxQuantity));
            if (Integer.parseInt(s.Qty) < 0 && Integer.parseInt(s.UpdatedBoxQuantity) == 0) {
                String styledText = "<s><font color='blue'>" + s.ItemCode + "</font></s>";
                tv_itemcode.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

                String boxqty = "<s><font color='blue'>" + s.BoxQuantity + "</font></s>";
                tv_BoxQuantity.setText(Html.fromHtml(boxqty), TextView.BufferType.SPANNABLE);

                String qty = "<s><font color='blue'>" + s.BoxQuantity + "</font></s>";
                tv_qty.setText(Html.fromHtml(qty), TextView.BufferType.SPANNABLE);

                String newqty = "<s><font color='blue'>" + s.UpdatedBoxQuantity + "</font></s>";
                new_qty.setText(Html.fromHtml(newqty), TextView.BufferType.SPANNABLE);
            }
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
                        okMessage("IOException",e.toString(),getContext());
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException",e.toString(),getContext());
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
        new updateBoxQuantityFragment.connBT().execute(btDev);
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
                okMessage("Error 2", e.toString(),getContext());
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
                /*printData = objSample_Print.getUsaBoxPrint(.getpPalletno(),
                        objPalletBuildingGlobal.getpBoxcnt(), objPalletBuildingGlobal.getpRemarks(),
                        objPalletBuildingGlobal.getpPallettype(),objPalletBuildingGlobal.getpTypename(),
                        objPalletBuildingGlobal.getpGroupname(),objPalletBuildingGlobal.getpPreparedby(),
                        objPalletBuildingGlobal.getpDate(),objPalletBuildingGlobal.getpTime());*/
            }
            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString(),getContext());
            return false;
        }
    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private void clear() {
        et_tote_id.clearFocus();
        et_itemcode.setText("");
        et_tote_id.setText("");
        toteid = "";
        itemcode = "";
        UpdateBoxSharedRef.saveToteid("");
        if (objUpdateBoxQuantityControl.deletetemp()) {
            lv_div_seperate_details.setAdapter(null);
        }
        et_tote_id.setEnabled(true);
        et_tote_id.requestFocus();
        totalBoxQty.setText("");
        reducedQty.setText("");
        NewBoxTotal.setText("");
        radioGroup.check(R.id.remove);
    }
}