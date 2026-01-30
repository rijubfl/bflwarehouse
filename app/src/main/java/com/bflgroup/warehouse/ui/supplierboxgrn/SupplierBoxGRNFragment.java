package com.bflgroup.warehouse.ui.supplierboxgrn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.warehousegrn.WarehouseGRNFragment;

import java.util.ArrayList;
import java.util.List;

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

    private SupplierBoxGRNSharedRef saredRef;

    private Global objGlobal = Global.getInstance();

    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();
    private SupplierBoxGRNControl objSupplierBoxGRNControl = new SupplierBoxGRNControl();

    ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxItems = new ArrayList<SupplierBoxGRNScannedBoxTicket>();

    MySupplierBoxGRNScannedBoxItemsAdp objMySupplierBoxGRNScannedBoxItemsAdp;

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

        if (saredRef.loadContainerID() != "") {
            et_supplier_box_grn_container_id.setText(saredRef.loadContainerID());
            et_supplier_box_grn_container_id.setEnabled(false);
            sp_supplier_box_grn_printer.setEnabled(false);
            sp_supplier_box_grn_printer_copies.setEnabled(false);
            listSupplierBoxGRNScannedBoxItems.clear();
            listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(et_supplier_box_grn_container_id.getText().toString());
            objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
            lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);
            et_supplier_box_grn_carton_id.requestFocus();
        }

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
                                b_Result = clearAll();
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
                b_Result = objSupplierBoxGRNControl.scanCarton(loadCont, contid, boxid, audit, po);
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
                sp_supplier_box_grn_printer.setEnabled(false);
                sp_supplier_box_grn_printer_copies.setEnabled(false);
                et_supplier_box_grn_container_id.setEnabled(false);
                bt_supplier_box_grn_load_cont.setEnabled(false);
                et_supplier_box_grn_carton_id.setText("");
                ch_supplier_box_grn_audit_req.setChecked(false);
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

            TextView tv_ticket_supplier_box_grn_carton_qty = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_carton_qty);
            tv_ticket_supplier_box_grn_carton_qty.setText(String.valueOf(s.cartonQty));

            TextView tv_ticket_supplier_box_grn_audit = (TextView) myView.findViewById(R.id.tv_ticket_supplier_box_grn_audit);
            tv_ticket_supplier_box_grn_audit.setText(String.valueOf(s.auditReq));

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
                    clearAll();
                    et_supplier_box_grn_container_id.requestFocus();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }


    private boolean clearAll() {
        b_Result = objSupplierBoxGRNControl.clearAll();
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
            sp_supplier_box_grn_printer.setEnabled(true);
            sp_supplier_box_grn_printer_copies.setEnabled(true);
            ch_supplier_box_grn_audit_req.setChecked(false);

            listSupplierBoxGRNScannedBoxItems.clear();
            listSupplierBoxGRNScannedBoxItems = objSupplierBoxGRNControl.loadSupplierBoxGRNScannedBox(et_supplier_box_grn_container_id.getText().toString());
            objMySupplierBoxGRNScannedBoxItemsAdp = new SupplierBoxGRNFragment.MySupplierBoxGRNScannedBoxItemsAdp(listSupplierBoxGRNScannedBoxItems);
            lv_supplier_box_grn.setAdapter(objMySupplierBoxGRNScannedBoxItemsAdp);

            tv_supplier_box_grn_total_boxes.setText("Total Boxes : " + SupplierBoxGRNGlobal.getTotalScanBoxCnt());
            tv_supplier_box_grn_total_qty.setText("Total Qty : " + SupplierBoxGRNGlobal.getTotalScanQty());
        }
        return true;
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