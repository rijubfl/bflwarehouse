package com.bflgroup.warehouse.ui.ageingstocktaking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingReports;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingReportsItemSearch;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingScanItems;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AgeingStockTakingFragment extends Fragment {

    private TextView tv_ageing_stock_taking_date;
    private TextView tv_ageing_stock_taking_user;
    private TextView tv_ageing_stock_taking_shop;
    private Spinner sp_ageing_stock_taking_zone;
    private Button bt_ageing_stock_taking_add;
    private Button bt_ageing_stock_taking_report;
    private Button bt_ageing_stock_taking_delete;
    private ListView lv_ageing_stock_taking;
    private TextView tv_ageing_stock_taking_total_scan;
    private TextView tv_ageing_stock_taking_total_exported;
    private Button bt_ageing_stock_taking_import;
    private Button bt_ageing_stock_taking_export;
    private Button bt_ageing_stock_taking_clear;
    private TextView tv_ageing_stock_taking_battery_percentage;

    private EditText et_ageing_stock_taking_popup_barcode;
    private EditText et_sales_invoice_popup_scan_item_qty;
    private TextView tv_ageing_stock_taking_popup_last_barcode;
    private TextView tv_ageing_stock_taking_popup_last_qty;
    private TextView tv_ageing_stock_taking_popup_result;
    private Button bt_ageing_stock_taking_popup_close;
    private Button bt_ageing_stock_taking_popup_add;

    private EditText et_ageing_stock_taking_popup_password;
    private Button bt_ageing_stock_taking_popup_password_ok;
    private Button bt_ageing_stock_taking_popup_password_close;

    private Button bt_ageing_stock_taking_popup_rpt_refresh;
    private ListView lv_sales_invoice_popup_rpt;
    private TextView tv_ageing_stock_taking_popup_rpt_total;
    private Button bt_ageing_stock_taking_popup_rpt_close;
    private CheckBox ch_ageing_stock_taking_popup_rpt_main_server;
    private Spinner sp_ageing_stock_taking_popup_rpt_sort;

    private EditText et_ageing_stock_taking_popup_delete_barcode;
    private Button bt_ageing_stock_taking_popup_delete_refresh;
    private ListView lv_sales_invoice_popup_delete;
    private TextView tv_ageing_stock_taking_popup_delete_total;
    private Button bt_ageing_stock_taking_popup_delete_close;

    private ProgressDialog mWaitDialog;

    private Controls objControls = new Controls();
    private Global objGlobal = Global.getInstance();
    private AgeingStockTakingControl objAgeingStockTakingControl = new AgeingStockTakingControl();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();
    AgeingStockTakingDbManager objAgeingStockTakingDbManager;

    ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems = new ArrayList<AgeingStockTakingScanItems>();
    ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports = new ArrayList<AgeingStockTakingReports>();
    ArrayList<AgeingStockTakingReportsItemSearch> listAgeingStockTakingReportsItemSearch = new ArrayList<AgeingStockTakingReportsItemSearch>();
    MyAgeingStockTakingItemAdp objMyAgeingStockTakingItemAdp;
    AgeingStockTakingReportAdp objAgeingStockTakingReportAdp;
    AgeingStockTakingReportSearchItemAdp objAgeingStockTakingReportSearchItemAdp;

    DecimalFormat formatter = new DecimalFormat("###,###");
    private boolean b_Result;
    private String s_Result;

    public AgeingStockTakingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_stock_taking, container, false);

        tv_ageing_stock_taking_date = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_date);
        tv_ageing_stock_taking_user = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_user);
        tv_ageing_stock_taking_shop = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_shop);
        sp_ageing_stock_taking_zone = (Spinner) view.findViewById(R.id.sp_ageing_stock_taking_zone);
        bt_ageing_stock_taking_add = (Button) view.findViewById(R.id.bt_ageing_stock_taking_add);
        bt_ageing_stock_taking_report = (Button) view.findViewById(R.id.bt_ageing_stock_taking_report);
        bt_ageing_stock_taking_delete = (Button) view.findViewById(R.id.bt_ageing_stock_taking_delete);
        lv_ageing_stock_taking = (ListView) view.findViewById(R.id.lv_ageing_stock_taking);
        tv_ageing_stock_taking_total_scan = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_total_scan);
        tv_ageing_stock_taking_total_exported = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_total_exported);
        bt_ageing_stock_taking_import = (Button) view.findViewById(R.id.bt_ageing_stock_taking_import);
        bt_ageing_stock_taking_export = (Button) view.findViewById(R.id.bt_ageing_stock_taking_export);
        bt_ageing_stock_taking_clear = (Button) view.findViewById(R.id.bt_ageing_stock_taking_clear);
        tv_ageing_stock_taking_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_battery_percentage);

        tv_ageing_stock_taking_shop.setText(objGlobal.getWarehouse());
        tv_ageing_stock_taking_user.setText(objGlobal.getUserName());
        tv_ageing_stock_taking_date.setText(objGlobal.getServerDate());

        objAgeingStockTakingDbManager=new AgeingStockTakingDbManager(getContext());

        b_Result = objAgeingStockTakingControl.loadZone();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        } else {
            List<String> arr;
            ArrayAdapter<String> arrayAdp;
            arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objAgeingStockTakingGlobal.getZoneList());
            sp_ageing_stock_taking_zone.setAdapter(arrayAdp);
        }
        b_Result = loadItemsStockTaking();
        if(!b_Result){
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        }

        bt_ageing_stock_taking_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupPassword();
            }
        });

        bt_ageing_stock_taking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zone="";
                if(sp_ageing_stock_taking_zone.getCount()>0){
                    zone = sp_ageing_stock_taking_zone.getSelectedItem().toString();
                }
                if(zone.isEmpty()){
                    okMessage("Stock Taking", "Please select zone");
                } else {
                    openPopupScan();
                }
            }
        });

        bt_ageing_stock_taking_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReports();
            }
        });

        bt_ageing_stock_taking_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDelete();
            }
        });

        bt_ageing_stock_taking_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to export main server?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWaitDialog = ProgressDialog.show(getContext(), "Export Data", "Please wait...");
                                mWaitDialog.setCancelable(false);
                                b_Result = objAgeingStockTakingDbManager.exportToMainServer(getActivity());
                                if(b_Result){
                                    closeWaitDialog();
                                    okMessage("Stock Taking","Done");
                                } else {
                                    closeWaitDialog();
                                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                                }
                                b_Result = loadItemsStockTaking();
                                if(!b_Result){
                                    closeWaitDialog();
                                    okMessage("Stock Taking", objGlobal.getErrorMessage());
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

    private void openPopupPassword() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_clear_password);

        et_ageing_stock_taking_popup_password = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_popup_password);
        bt_ageing_stock_taking_popup_password_ok = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_password_ok);
        bt_ageing_stock_taking_popup_password_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_password_close);

        bt_ageing_stock_taking_popup_password_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = et_ageing_stock_taking_popup_password.getText().toString();
                b_Result = objAgeingStockTakingDbManager.validateForDelete(pass);
                if (!b_Result) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to clear all?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objAgeingStockTakingDbManager.deleteAllLocalDb();
                                    if (!b_Result) {
                                        okMessage("Stock Taking", objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = loadItemsStockTaking();
                                        if (!b_Result) {
                                            okMessage("Stock Taking", objGlobal.getErrorMessage());
                                        }
                                        myDialog.dismiss();
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

        bt_ageing_stock_taking_popup_password_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        et_ageing_stock_taking_popup_password.requestFocus();
        myDialog.show();
    }

    private void openDelete() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_search_items);

        et_ageing_stock_taking_popup_delete_barcode = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_popup_delete_barcode);
        bt_ageing_stock_taking_popup_delete_refresh = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_delete_refresh);
        lv_sales_invoice_popup_delete = (ListView) myDialog.findViewById(R.id.lv_sales_invoice_popup_delete);
        tv_ageing_stock_taking_popup_delete_total = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_delete_total);
        bt_ageing_stock_taking_popup_delete_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_delete_close);

        et_ageing_stock_taking_popup_delete_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        bt_ageing_stock_taking_popup_delete_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemc = objControls.replaceString(et_ageing_stock_taking_popup_delete_barcode.getText().toString());
                listAgeingStockTakingReportsItemSearch.clear();
                listAgeingStockTakingReportsItemSearch = objAgeingStockTakingDbManager.loadAgingStockTakingRptItems(getContext(), itemc);
                objAgeingStockTakingReportSearchItemAdp = new AgeingStockTakingReportSearchItemAdp(listAgeingStockTakingReportsItemSearch);
                lv_sales_invoice_popup_delete.setAdapter(objAgeingStockTakingReportSearchItemAdp);
                tv_ageing_stock_taking_popup_delete_total.setText(String.valueOf(objAgeingStockTakingGlobal.getTotal()));
            }
        });

        bt_ageing_stock_taking_popup_delete_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        et_ageing_stock_taking_popup_delete_barcode.requestFocus();
        myDialog.show();
    }

    private void openReports() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_reports);

        bt_ageing_stock_taking_popup_rpt_refresh = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_rpt_refresh);
        lv_sales_invoice_popup_rpt = (ListView) myDialog.findViewById(R.id.lv_sales_invoice_popup_rpt);
        tv_ageing_stock_taking_popup_rpt_total = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_rpt_total);
        bt_ageing_stock_taking_popup_rpt_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_rpt_close);
        ch_ageing_stock_taking_popup_rpt_main_server = (CheckBox) myDialog.findViewById(R.id.ch_ageing_stock_taking_popup_rpt_main_server);
        sp_ageing_stock_taking_popup_rpt_sort = (Spinner) myDialog.findViewById(R.id.sp_ageing_stock_taking_popup_rpt_sort);

        List<String> arr;
        arr=new ArrayList<String>();
        arr.add("User");
        arr.add("Zone");
        arr.add("Quantity");
        ArrayAdapter<String> arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_ageing_stock_taking_popup_rpt_sort.setAdapter(arrayAdp);

        bt_ageing_stock_taking_popup_rpt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAgeingStockTakingReports.clear();
                if(ch_ageing_stock_taking_popup_rpt_main_server.isChecked()){
                    listAgeingStockTakingReports = objAgeingStockTakingControl.loadAgingStockTakingRpt(sp_ageing_stock_taking_popup_rpt_sort.getSelectedItem().toString());
                    objAgeingStockTakingReportAdp = new AgeingStockTakingReportAdp(listAgeingStockTakingReports);
                    lv_sales_invoice_popup_rpt.setAdapter(objAgeingStockTakingReportAdp);
                } else {
                    listAgeingStockTakingReports = objAgeingStockTakingDbManager.loadAgingStockTakingRpt(getActivity(),sp_ageing_stock_taking_popup_rpt_sort.getSelectedItem().toString());
                    objAgeingStockTakingReportAdp = new AgeingStockTakingReportAdp(listAgeingStockTakingReports);
                    lv_sales_invoice_popup_rpt.setAdapter(objAgeingStockTakingReportAdp);
                }
                tv_ageing_stock_taking_popup_rpt_total.setText(String.valueOf(objAgeingStockTakingGlobal.getTotal()));
            }
        });

        bt_ageing_stock_taking_popup_rpt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    private class AgeingStockTakingReportAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports;

        public AgeingStockTakingReportAdp(ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports) {
            this.listAgeingStockTakingReports = listAgeingStockTakingReports;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingReports.size();
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
            View myView = mInflater.inflate(R.layout.ticket_ageing_stock_taking_reports, null);
            final AgeingStockTakingReports s = listAgeingStockTakingReports.get(position);

            TextView tv_ticket_ageing_stock_taking_user = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_user);
            tv_ticket_ageing_stock_taking_user.setText(String.valueOf(s.user));

            TextView tv_ticket_ageing_stock_taking_zone = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_zone);
            tv_ticket_ageing_stock_taking_zone.setText(String.valueOf(s.zone));

            TextView tv_ticket_ageing_stock_taking_quantity = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_quantity);
            tv_ticket_ageing_stock_taking_quantity.setText(String.valueOf(s.qty));

            return myView;
        }
    }

    private class AgeingStockTakingReportSearchItemAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingReportsItemSearch> listAgeingStockTakingReportsItemSearch;

        public AgeingStockTakingReportSearchItemAdp(ArrayList<AgeingStockTakingReportsItemSearch> listAgeingStockTakingReportsItemSearch) {
            this.listAgeingStockTakingReportsItemSearch = listAgeingStockTakingReportsItemSearch;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingReportsItemSearch.size();
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
            View myView = mInflater.inflate(R.layout.ageing_stock_taking_items_delete_ticket, null);
            final AgeingStockTakingReportsItemSearch s = listAgeingStockTakingReportsItemSearch.get(position);

            TextView tv_aging_stock_taking_delete_itemdetails = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_delete_itemdetails);
            tv_aging_stock_taking_delete_itemdetails.setText(String.valueOf(s.itemcode));

            TextView tv_aging_stock_taking_delete_srid = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_delete_srid);
            tv_aging_stock_taking_delete_srid.setText(String.valueOf(s.srid));

            Button bt_aging_stock_taking_delete = (Button) myView.findViewById(R.id.bt_aging_stock_taking_delete);
            bt_aging_stock_taking_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to delete the selected item?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objAgeingStockTakingDbManager.deleteMainServer(getContext(),s.srid);
                                    if (!b_Result) {
                                        okMessage("Stock Taking", objGlobal.getErrorMessage());
                                    } else {
                                        listAgeingStockTakingReportsItemSearch.clear();
                                        listAgeingStockTakingReportsItemSearch = objAgeingStockTakingDbManager.loadAgingStockTakingRptItems(getContext(), s.itemcode);
                                        objAgeingStockTakingReportSearchItemAdp = new AgeingStockTakingReportSearchItemAdp(listAgeingStockTakingReportsItemSearch);
                                        lv_sales_invoice_popup_delete.setAdapter(objAgeingStockTakingReportSearchItemAdp);
                                        tv_ageing_stock_taking_popup_delete_total.setText(String.valueOf(objAgeingStockTakingGlobal.getTotal()));
                                        b_Result = loadItemsStockTaking();
                                        if(!b_Result){
                                            closeWaitDialog();
                                            okMessage("Stock Taking", objGlobal.getErrorMessage());
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
            });

            return myView;
        }
    }

    private void openPopupScan() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_scan_items);

        et_ageing_stock_taking_popup_barcode = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_popup_barcode);
        et_sales_invoice_popup_scan_item_qty = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_scan_item_qty);
        tv_ageing_stock_taking_popup_last_barcode = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_last_barcode);
        tv_ageing_stock_taking_popup_last_qty = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_last_qty);
        tv_ageing_stock_taking_popup_result = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_result);
        bt_ageing_stock_taking_popup_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_close);
        bt_ageing_stock_taking_popup_add = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_add);

        et_ageing_stock_taking_popup_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_ageing_stock_taking_popup_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanBarcode();
                }
                return false;
            }
        });

        bt_ageing_stock_taking_popup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        bt_ageing_stock_taking_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        et_ageing_stock_taking_popup_barcode.requestFocus();
        myDialog.show();
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    boolean scanBarcode() {
        String scan = objControls.replaceString(et_ageing_stock_taking_popup_barcode.getText().toString()).toUpperCase();
        String zoneId = sp_ageing_stock_taking_zone.getSelectedItem().toString();
        String result = "";
        if (zoneId.isEmpty()) {
            okMessage("Stock Taking", "Please scan Barcode");
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        if (scan.isEmpty()) {
            scan = "";
        }
        int qty = Integer.valueOf(et_sales_invoice_popup_scan_item_qty.getText().toString());
        if (qty == 0) {
            qty = 1;
        }
        if (scan.isEmpty()) {
            okMessage("Stock Taking", "Please scan Barcode");
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        if(scan.length()>25){
            okMessage("Stock Taking", "Please Double check the barcode that scan");
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        tv_ageing_stock_taking_popup_last_barcode.setText(scan);
        tv_ageing_stock_taking_popup_last_qty.setText(String.valueOf(qty));
        et_ageing_stock_taking_popup_barcode.setText("");
        et_sales_invoice_popup_scan_item_qty.setText("1");
        et_ageing_stock_taking_popup_barcode.requestFocus();

        b_Result = objAgeingStockTakingDbManager.saveScanToLocaldb(scan, qty, zoneId, result);
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        b_Result = objAgeingStockTakingDbManager.loadScannedCountTotal();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
            return false;
        }
        tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
        b_Result = objAgeingStockTakingDbManager.loadScannedCountExportTotal();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
            return false;
        }
        tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
        b_Result = loadItemsStockTaking();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
            return false;
        }
        return true;
    }

    private class MyAgeingStockTakingItemAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems;

        public MyAgeingStockTakingItemAdp(ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems) {
            this.listAgeingStockTakingScanItems = listAgeingStockTakingScanItems;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingScanItems.size();
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
            View myView = mInflater.inflate(R.layout.ageing_stock_taking_items_ticket, null);
            final AgeingStockTakingScanItems s = listAgeingStockTakingScanItems.get(position);

            TextView tv_aging_stock_taking_itemdetails = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_itemdetails);
            tv_aging_stock_taking_itemdetails.setText(String.valueOf(s.itemcode));

            TextView tv_aging_stock_taking_date = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_date);
            tv_aging_stock_taking_date.setText(String.valueOf(s.date));

            TextView tv_aging_stock_taking_time = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_time);
            tv_aging_stock_taking_time.setText(String.valueOf(s.time));

            TextView tv_aging_stock_taking_result = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_result);
            tv_aging_stock_taking_result.setText(String.valueOf(s.result));

            return myView;
        }
    }

    private boolean loadItemsStockTaking() {
        try {
            listAgeingStockTakingScanItems.clear();
            listAgeingStockTakingScanItems = objAgeingStockTakingDbManager.loadAgingStockTakingItems("50", getActivity());
            objMyAgeingStockTakingItemAdp = new MyAgeingStockTakingItemAdp(listAgeingStockTakingScanItems);
            lv_ageing_stock_taking.setAdapter(objMyAgeingStockTakingItemAdp);
            b_Result = objAgeingStockTakingDbManager.loadScannedCountTotal();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
            b_Result = objAgeingStockTakingDbManager.loadScannedCountExportTotal();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
            int bPer = objControls.getBatteryPercentage(getContext());
            tv_ageing_stock_taking_battery_percentage.setText(String.valueOf(bPer) + " %");
            if(bPer>=20)
                tv_ageing_stock_taking_battery_percentage.setTextColor(Color.rgb(0,145,0));
            else
                tv_ageing_stock_taking_battery_percentage.setTextColor(Color.RED);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
            return false;
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
        vibrate(500);
    }
}