package com.bflgroup.warehouse.ui.supplierboxgrn;

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
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.ageingstocktaking.AgeingStockTakingFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SupplierBoxGRNFragment extends Fragment {

    private Button bt_supplier_box_grn_load_cont;
    private Spinner sp_supplier_box_grn_printer;
    private Spinner sp_supplier_box_grn_printer_copies;
    private EditText et_supplier_box_grn_container_id;
    private EditText et_supplier_box_grn_carton_id;
    private CheckBox ch_supplier_box_grn_audit_req;
    private Button bt_supplier_box_grn_scan;
    private Button bt_supplier_box_grn_generate_carton;
    private ListView lv_supplier_box_grn;
    private Button bt_supplier_box_grn_clear;
    private TextView tv_supplier_box_grn_total_boxes;
    private TextView tv_supplier_box_grn_total_qty;
    private Button bt_supplier_box_grn_save;
    private Button bt_supplier_box_grn_completed;


    private Spinner sp_supplier_box_grn_popup_po;
    private TextInputEditText tv_supplier_box_grn_popup_log_boxno;
    private Button bt_supplier_box_grn_popup_print;
    private Button bt_supplier_box_grn_popup_close;

    private SupplierBoxGRNSharedRef saredRef;

    private Global objGlobal = Global.getInstance();

    private SupplierBoxGRNControl objSupplierBoxGRNControl = new SupplierBoxGRNControl();

    ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxItems = new ArrayList<SupplierBoxGRNScannedBoxTicket>();
    MySupplierBoxGRNScannedBoxItemsAdp objMySupplierBoxGRNScannedBoxItemsAdp;

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

    private boolean b_Result;
    private String loadCont="";

    public SupplierBoxGRNFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supplier_box_g_r_n, container, false);

        bt_supplier_box_grn_load_cont = (Button) view.findViewById(R.id.bt_supplier_box_grn_load_cont);
        sp_supplier_box_grn_printer = (Spinner) view.findViewById(R.id.sp_supplier_box_grn_printer);
        sp_supplier_box_grn_printer_copies = (Spinner) view.findViewById(R.id.sp_supplier_box_grn_printer_copies);
        et_supplier_box_grn_container_id = (EditText) view.findViewById(R.id.et_supplier_box_grn_container_id);
        et_supplier_box_grn_carton_id = (EditText) view.findViewById(R.id.et_supplier_box_grn_carton_id);
        ch_supplier_box_grn_audit_req = (CheckBox) view.findViewById(R.id.ch_supplier_box_grn_audit_req);
        bt_supplier_box_grn_scan = (Button) view.findViewById(R.id.bt_supplier_box_grn_scan);
        bt_supplier_box_grn_generate_carton = (Button) view.findViewById(R.id.bt_supplier_box_grn_generate_carton);
        lv_supplier_box_grn = (ListView) view.findViewById(R.id.lv_supplier_box_grn);
        bt_supplier_box_grn_clear = (Button) view.findViewById(R.id.bt_supplier_box_grn_clear);
        tv_supplier_box_grn_total_boxes = (TextView) view.findViewById(R.id.tv_supplier_box_grn_total_boxes);
        tv_supplier_box_grn_total_qty = (TextView) view.findViewById(R.id.tv_supplier_box_grn_total_qty);
        bt_supplier_box_grn_save = (Button) view.findViewById(R.id.bt_supplier_box_grn_save);
        bt_supplier_box_grn_completed = (Button) view.findViewById(R.id.bt_supplier_box_grn_completed);

        saredRef = new SupplierBoxGRNSharedRef(getContext());

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Supplier Box GRN", objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_supplier_box_grn_printer.setAdapter(arrayAdpYellow);
            if (saredRef.loadPrinter() != "") {
                sp_supplier_box_grn_printer.setSelection(arrayAdpYellow.getPosition(saredRef.loadPrinter()));
            }
        }
        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("1");
        arr.add("2");
        arr.add("3");
        arr.add("4");
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_supplier_box_grn_printer_copies.setAdapter(arrayAdp);
        if (saredRef.loadPrintCopies()!="") {
            sp_supplier_box_grn_printer_copies.setSelection(arrayAdp.getPosition(saredRef.loadPrintCopies().toString()));
        }

        et_supplier_box_grn_carton_id.setEnabled(false);
        bt_supplier_box_grn_scan.setEnabled(false);
        bt_supplier_box_grn_generate_carton.setEnabled(false);
        ch_supplier_box_grn_audit_req.setEnabled(true);

        et_supplier_box_grn_container_id.setEnabled(true);
        bt_supplier_box_grn_load_cont.setEnabled(true);

        if (saredRef.loadContainerID() != "") {
            et_supplier_box_grn_container_id.setText(saredRef.loadContainerID());
            et_supplier_box_grn_container_id.setEnabled(false);
            bt_supplier_box_grn_load_cont.setEnabled(false);

            et_supplier_box_grn_carton_id.setEnabled(true);
            bt_supplier_box_grn_scan.setEnabled(true);
            bt_supplier_box_grn_generate_carton.setEnabled(true);
            ch_supplier_box_grn_audit_req.setEnabled(true);

            listSupplierBoxGRNScannedBoxItems.clear();
            listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(et_supplier_box_grn_container_id.getText().toString());
            objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
            lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);
            et_supplier_box_grn_carton_id.requestFocus();
        }

        bt_supplier_box_grn_generate_carton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupGenerateNewBox();
            }
        });

        bt_supplier_box_grn_load_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_supplier_box_grn_container_id.getText().toString().isEmpty()) {
                    okMessage("Supplier Box GRN","Please enter Container ID");
                    et_supplier_box_grn_container_id.requestFocus();
                } else {
                    loadCont="Y";
                    et_supplier_box_grn_container_id.setText(et_supplier_box_grn_container_id.getText().toString().toUpperCase());
                    new SupplierBoxGRNFragment.LoadBoxNumber(getContext()).execute();
                }
            }
        });

        bt_supplier_box_grn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_supplier_box_grn_container_id.getText().toString().isEmpty()) {
                    okMessage("Supplier Box GRN","Please enter Container ID");
                    et_supplier_box_grn_container_id.requestFocus();
                } else if (et_supplier_box_grn_carton_id.getText().toString().isEmpty()) {
                    okMessage("Supplier Box GRN","Please scan Carton ID");
                    et_supplier_box_grn_carton_id.requestFocus();
                } else {
                    loadCont="N";
                    new LoadBoxNumber(getContext()).execute();
                }
            }
        });

        et_supplier_box_grn_carton_id.setOnTouchListener(new View.OnTouchListener() {
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

        et_supplier_box_grn_carton_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (et_supplier_box_grn_container_id.getText().toString().isEmpty()) {
                        okMessage("Supplier Box GRN","Please enter Container ID");
                        et_supplier_box_grn_container_id.requestFocus();
                    } else if (et_supplier_box_grn_carton_id.getText().toString().isEmpty()) {
                        okMessage("Supplier Box GRN","Please scan Carton ID");
                        et_supplier_box_grn_carton_id.requestFocus();
                    } else {
                        loadCont="N";
                        new LoadBoxNumber(getContext()).execute();
                    }
                }
                return false;
            }
        });

        bt_supplier_box_grn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = clearAll(true);
                                et_supplier_box_grn_container_id.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bt_supplier_box_grn_save.requestFocus();
                            }
                        })
                        .show();
            }
        });

        bt_supplier_box_grn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SaveSupplierBoxGRN(getContext()).execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bt_supplier_box_grn_save.requestFocus();
                            }
                        })
                        .show();
            }
        });

        bt_supplier_box_grn_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bt_supplier_box_grn_completed.requestFocus();
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

    private class LoadBoxNumber extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        String contid, boxid, audit, po;

        public LoadBoxNumber(Context context) {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
            contid = et_supplier_box_grn_container_id.getText().toString();
            boxid = et_supplier_box_grn_carton_id.getText().toString();
            audit = ch_supplier_box_grn_audit_req.isChecked() ? "Y" : "N";
            po = "";
        }

        @Override
        protected Integer doInBackground(Void... args) {
            if (audit.isEmpty()) audit = "N";
            try {
                if (loadCont.equals("Y")) {
                    b_Result = objSupplierBoxGRNControl.validateSupplierBoxGrn(contid);
                    if (!b_Result) return 0;
                }
                b_Result = objSupplierBoxGRNControl.scanCarton(loadCont, contid, boxid, audit);
                if (!b_Result) return 0;
            } catch (Exception e) {
                objGlobal.setErrorMessage(e.toString());
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                okMessage("Supplier Box GRN", objGlobal.getErrorMessage());
                if (loadCont.equals("Y")) {
                    et_supplier_box_grn_container_id.setText("");
                    et_supplier_box_grn_container_id.requestFocus();
                } else {
                    et_supplier_box_grn_carton_id.setText("");
                    et_supplier_box_grn_carton_id.requestFocus();
                }
            } else {
                SupplierBoxGRNGlobal.setTotalScanBoxCnt(0);
                SupplierBoxGRNGlobal.setTotalScanQty(0);

                listSupplierBoxGRNScannedBoxItems.clear();
                listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(contid);
                objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
                lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);

                tv_supplier_box_grn_total_boxes.setText("Total Boxes : " + SupplierBoxGRNGlobal.getTotalScanBoxCnt());
                tv_supplier_box_grn_total_qty.setText("Total Qty : " + SupplierBoxGRNGlobal.getTotalScanQty());

                saredRef.saveContainerID(et_supplier_box_grn_container_id.getText().toString());
                saredRef.savePrinter(sp_supplier_box_grn_printer.getSelectedItem().toString());
                saredRef.savePrintCopies(sp_supplier_box_grn_printer_copies.getSelectedItem().toString());
                et_supplier_box_grn_carton_id.setText("");

                et_supplier_box_grn_container_id.setEnabled(false);
                bt_supplier_box_grn_load_cont.setEnabled(false);

                et_supplier_box_grn_carton_id.setEnabled(true);
                bt_supplier_box_grn_scan.setEnabled(true);
                bt_supplier_box_grn_generate_carton.setEnabled(true);
                ch_supplier_box_grn_audit_req.setEnabled(true);

                et_supplier_box_grn_carton_id.requestFocus();

                if (dialog.isShowing()) dialog.dismiss();
            }
        }
    }

    private class MySupplierBoxGRNScannedBoxItemsAdp extends BaseAdapter {
        public ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxItems;

        public MySupplierBoxGRNScannedBoxItemsAdp(ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxItems) {
            this.listSupplierBoxGRNScannedBoxItems = listSupplierBoxGRNScannedBoxItems;
        }

        @Override
        public int getCount() {
            return listSupplierBoxGRNScannedBoxItems.size();
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
            View myView = mInflater.inflate(R.layout.supplier_box_g_r_n_list_ticket, null);
            final SupplierBoxGRNScannedBoxTicket s = listSupplierBoxGRNScannedBoxItems.get(position);

            TextView tv_ticket_supplier_box_grn_container_id = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_container_id);
            tv_ticket_supplier_box_grn_container_id.setText(String.valueOf(s.cartonId));

            TextView tv_ticket_supplier_box_grn_po = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_po);
            tv_ticket_supplier_box_grn_po.setText("PO : " + s.po);

            TextView tv_ticket_supplier_box_grn_carton_qty = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_carton_qty);
            tv_ticket_supplier_box_grn_carton_qty.setText("Qty : " + s.cartonQty);

            TextView tv_ticket_supplier_box_grn_audit = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_audit);
            tv_ticket_supplier_box_grn_audit.setText("QC : " + s.auditReq);

            Button bt_ticket_supplier_box_grn_delete = (Button) myView.findViewById(R.id.bt_ticket_supplier_box_grn_delete);
            bt_ticket_supplier_box_grn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to delete the selected item?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //b_Result = objSupplierBoxGRNControl.deleteMainServer(getContext(), s.srid);
                                    if (!b_Result) {
                                        okMessage("Stock Taking", objGlobal.getErrorMessage());
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

            return myView;
        }
    }

    private class SaveSupplierBoxGRN extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        String contid, remarks;

        public SaveSupplierBoxGRN(Context context) {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
            contid = et_supplier_box_grn_container_id.getText().toString();
            remarks="";
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objSupplierBoxGRNControl.validateSupplierBoxGrn(contid);
                if (!b_Result) return 0;
            } catch (Exception e) {
                objGlobal.setErrorMessage(e.toString());
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                okMessage("Supplier Box GRN", objGlobal.getErrorMessage());
            } else {
                b_Result = objSupplierBoxGRNControl.saveSupplierBoxGrn(contid, remarks);
                if (!b_Result) {
                    okMessage("Supplier Box GRN", objGlobal.getErrorMessage());
                    vibrate(500);
                } else {
                    clearAll(false);
                    et_supplier_box_grn_container_id.requestFocus();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    private class GenerateNewLogisticBox extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        String contid, audit, po;

        public GenerateNewLogisticBox(Context context) {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
            contid = et_supplier_box_grn_container_id.getText().toString();
            po = sp_supplier_box_grn_popup_po.getSelectedItem().toString();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            audit = "Y";
            try {
                b_Result = objSupplierBoxGRNControl.generateLogisticBoxAndPrint(contid, po, audit);
                if (!b_Result) {
                    return 0;
                }
            } catch (Exception e) {
                objGlobal.setErrorMessage(e.toString());
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                okMessage("Supplier Box GRN", objGlobal.getErrorMessage());
                et_supplier_box_grn_carton_id.setText("");
                et_supplier_box_grn_carton_id.requestFocus();
            } else {
                SupplierBoxGRNGlobal.setTotalScanBoxCnt(0);
                SupplierBoxGRNGlobal.setTotalScanQty(0);

                listSupplierBoxGRNScannedBoxItems.clear();
                listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(contid);
                objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
                lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);

                tv_supplier_box_grn_total_boxes.setText("Total Boxes : " + SupplierBoxGRNGlobal.getTotalScanBoxCnt());
                tv_supplier_box_grn_total_qty.setText("Total Qty : " + SupplierBoxGRNGlobal.getTotalScanQty());

                sp_supplier_box_grn_popup_po.setSelection(0);

                if (dialog.isShowing()) dialog.dismiss();

                if (objGlobal.getBluetoothDevicesAvailable().equals("Y")) {
                    String printer = sp_supplier_box_grn_printer.getSelectedItem().toString();
                    if (!printSticker(printer)) {
                        okMessage("Supplier Box GRN", "Printer Error, Pleasse reprint..");
                        vibrate(100);
                    }
                }
            }
        }
    }

    private boolean clearAll(boolean includeLogBox) {
        b_Result = objSupplierBoxGRNControl.clearAll(et_supplier_box_grn_container_id.getText().toString(),includeLogBox);
        if (!b_Result) {
            okMessage("Supplier Box GRN ", objGlobal.getErrorMessage());
            vibrate(500);
            return false;
        } else {
            et_supplier_box_grn_container_id.setText("");
            et_supplier_box_grn_carton_id.setText("");
            saredRef.saveContainerID("");

            et_supplier_box_grn_container_id.setEnabled(true);
            bt_supplier_box_grn_load_cont.setEnabled(true);

            et_supplier_box_grn_carton_id.setEnabled(false);
            bt_supplier_box_grn_scan.setEnabled(false);
            bt_supplier_box_grn_generate_carton.setEnabled(false);
            ch_supplier_box_grn_audit_req.setEnabled(false);

            listSupplierBoxGRNScannedBoxItems.clear();
            listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(et_supplier_box_grn_container_id.getText().toString());
            objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
            lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);

            tv_supplier_box_grn_total_boxes.setText("Total Boxes : " + SupplierBoxGRNGlobal.getTotalScanBoxCnt());
            tv_supplier_box_grn_total_qty.setText("Total Qty : " + SupplierBoxGRNGlobal.getTotalScanQty());
        }
        return true;
    }

    private void openPopupGenerateNewBox() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_supplier_box_grn_generate_carton);

        sp_supplier_box_grn_popup_po = (Spinner) myDialog.findViewById(R.id.sp_supplier_box_grn_popup_po);
        tv_supplier_box_grn_popup_log_boxno = (TextInputEditText) myDialog.findViewById(R.id.tv_supplier_box_grn_popup_log_boxno);
        bt_supplier_box_grn_popup_print = (Button) myDialog.findViewById(R.id.bt_supplier_box_grn_popup_print);
        bt_supplier_box_grn_popup_close = (Button) myDialog.findViewById(R.id.bt_supplier_box_grn_popup_close);

        List<String> arr1 = objSupplierBoxGRNControl.loadPo(et_supplier_box_grn_container_id.getText().toString());
        ArrayAdapter<String> arrayAdp5 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
        sp_supplier_box_grn_popup_po.setAdapter(arrayAdp5);

        sp_supplier_box_grn_popup_po.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tv_supplier_box_grn_popup_log_boxno.setText("");
                if (position != 0) {
                    tv_supplier_box_grn_popup_log_boxno.setText(objSupplierBoxGRNControl.generateLogisticBox(et_supplier_box_grn_container_id.getText().toString(),
                            sp_supplier_box_grn_popup_po.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        bt_supplier_box_grn_popup_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_supplier_box_grn_popup_po.getSelectedItem().toString().isEmpty() || sp_supplier_box_grn_popup_po.getSelectedItem().toString().equals("--Select PO--") ) {
                    okMessage("Supplier Box GRN","Please enter PO");
                    sp_supplier_box_grn_popup_po.requestFocus();
                } else {
                    loadCont="N";
                    new GenerateNewLogisticBox(getContext()).execute();
                }
            }
        });

        bt_supplier_box_grn_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        sp_supplier_box_grn_popup_po.requestFocus();
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
        new SupplierBoxGRNFragment.connBT().execute(btDev);
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
                String contno = et_supplier_box_grn_container_id.getText().toString();
                String palletno = SupplierBoxGRNGlobal.getLogNewBoxNo();
                String grpnm = "";
                String cnt = sp_supplier_box_grn_printer_copies.getSelectedItem().toString();
                String remrks = "";
                printData = objSample_Print.getLogisticPalletPrint(contno, palletno, grpnm, cnt, remrks);
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