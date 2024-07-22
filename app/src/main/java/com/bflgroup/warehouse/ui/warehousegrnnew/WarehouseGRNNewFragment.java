package com.bflgroup.warehouse.ui.warehousegrnnew;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.sql.SQLException;
import java.util.ArrayList;

public class WarehouseGRNNewFragment extends Fragment {

    private EditText et_wh_grn_ginno;
    private TextView tv_wh_grn_gindate;
    private Button bt_wh_grn_scan_gin;
    private TextView et_wh_grn_warehouse_from;
    private TextView et_wh_grn_warehouse_to;
    private Button bt_wh_grn_scan_item;
    private ListView lv_wh_grn_details;
    private Button bt_wh_grn_clear;
    private Button bt_wh_grn_save;
    private TextView tv_wh_grn_total_plt_count;
    private TextView tv_wh_grn_scan_plt_count;
    private TextView tv_wh_grn_total_plt_box_count;
    private TextView tv_wh_grn_total_plt_box_scan_count;

    private EditText et_warehouse_grn_new_popup_scan;
    private Button bt_warehouse_grn_new_popup_scan;
    private TextView tv_warehouse_grn_new_popup_last_scan;
    private ListView lv_warehouse_grn_new_popup_scandetail;
    private Button bt_warehouse_grn_new_popup_ok;
    MyScanWarehouseGrnTotalCountAdp objMyScanWarehouseGrnTotalCountAdp;
    MyScanWarehouseGrnScanAdp objMyScanWarehouseGrnScanAdp;

    WarehouseGRNNewSharedRef saredRef;
    WarehouseGRNNewControl objWarehouseGRNNewControl = new WarehouseGRNNewControl();
    WarehouseGRNNewGlobal objWarehouseGRNNewGlobal = WarehouseGRNNewGlobal.getInstance();
    private boolean b_Result;
    ArrayList<WarehouseGRNNewScanCountTicket> listWarehouseGRNNewScanCountTicket = new ArrayList<WarehouseGRNNewScanCountTicket>();
    ArrayList<WarehouseGRNNewScanTicket> listWarehouseGRNNewScanTicket = new ArrayList<WarehouseGRNNewScanTicket>();
    private Global objGlobal = Global.getInstance();

    public WarehouseGRNNewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouse_g_r_n_new, container, false);
        et_wh_grn_ginno = (EditText) view.findViewById(R.id.et_wh_grn_ginno);
        tv_wh_grn_gindate = (TextView) view.findViewById(R.id.tv_wh_grn_gindate);
        bt_wh_grn_scan_gin = (Button) view.findViewById(R.id.bt_wh_grn_scan_gin);
        et_wh_grn_warehouse_from = (TextView) view.findViewById(R.id.et_wh_grn_warehouse_from);
        et_wh_grn_warehouse_to = (TextView) view.findViewById(R.id.et_wh_grn_warehouse_to);
        bt_wh_grn_scan_item = (Button) view.findViewById(R.id.bt_wh_grn_scan_item);
        lv_wh_grn_details = (ListView) view.findViewById(R.id.lv_wh_grn_details);
        bt_wh_grn_clear = (Button) view.findViewById(R.id.bt_wh_grn_clear);
        bt_wh_grn_save = (Button) view.findViewById(R.id.bt_wh_grn_save);
        tv_wh_grn_total_plt_count = (TextView) view.findViewById(R.id.tv_wh_grn_total_plt_count);
        tv_wh_grn_scan_plt_count = (TextView) view.findViewById(R.id.tv_wh_grn_scan_plt_count);
        tv_wh_grn_total_plt_box_count = (TextView) view.findViewById(R.id.tv_wh_grn_total_plt_box_count);
        tv_wh_grn_total_plt_box_scan_count = (TextView) view.findViewById(R.id.tv_wh_grn_total_plt_box_scan_count);

        saredRef = new WarehouseGRNNewSharedRef(getContext());
        if (saredRef.loadGinNo() != "") {
            et_wh_grn_ginno.setText(saredRef.loadGinNo());
            tv_wh_grn_gindate.setText(saredRef.loadGinDate());
            et_wh_grn_warehouse_from.setText(saredRef.loadWHFrom());
            et_wh_grn_warehouse_to.setText(saredRef.loadWHTo());
            et_wh_grn_ginno.setEnabled(false);
            bt_wh_grn_scan_gin.setEnabled(false);
        } else {
            et_wh_grn_ginno.requestFocus();
        }
        bt_wh_grn_scan_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupScanItems();
            }
        });

        bt_wh_grn_scan_gin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ginNo = et_wh_grn_ginno.getText().toString();
                b_Result = objWarehouseGRNNewControl.validateGin(ginNo);
                if (b_Result) {
                    new WarehouseGRNNewFragment.LoadGinDetails().execute();
                } else {
                    okMessage("Warehouse GRN", objGlobal.getErrorMessage());
                }
            }
        });
        bt_wh_grn_clear.setOnClickListener(new View.OnClickListener() {
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
                                et_wh_grn_ginno.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bt_wh_grn_save.requestFocus();
                            }
                        })
                        .show();

            }
        });

        bt_wh_grn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ginNo = et_wh_grn_ginno.getText().toString();
                b_Result = objWarehouseGRNNewControl.validateGrn(ginNo);
                if (!b_Result) {
                    okMessage("Warehouse GRN", objGlobal.getErrorMessage());
                    bt_wh_grn_save.requestFocus();
                } else {
                    int tCnt = Integer.valueOf(tv_wh_grn_total_plt_box_count.getText().toString());
                    int sCnt = Integer.valueOf(tv_wh_grn_total_plt_box_scan_count.getText().toString());
                    if (tCnt != sCnt) {
                        okMessage("Warehouse GRN", "Can't save, Total Pallet (" + tCnt + ") Scan (" + sCnt + ")" );
                        vibrate(500);
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are You sure to save?")
                                .setTitle("Conformation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        b_Result = objWarehouseGRNNewControl.grnSave(ginNo, "");
                                        if (!b_Result) {
                                            okMessage("WarehouseGRNFragment:bt_wh_grn_save", objGlobal.getErrorMessage());
                                            vibrate(500);
                                        } else {
                                            clearAll();
                                            et_wh_grn_ginno.requestFocus();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bt_wh_grn_save.requestFocus();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
        loadScanTotal();
        et_wh_grn_ginno.requestFocus();

        return view;
    }

    private void openPopupScanItems() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.warehouse_grn_new_popup_items_scan);

        et_warehouse_grn_new_popup_scan = (EditText) myDialog.findViewById(R.id.et_warehouse_grn_new_popup_scan);
        bt_warehouse_grn_new_popup_scan = (Button) myDialog.findViewById(R.id.bt_warehouse_grn_new_popup_scan);
        tv_warehouse_grn_new_popup_last_scan = (TextView) myDialog.findViewById(R.id.tv_warehouse_grn_new_popup_last_scan);
        lv_warehouse_grn_new_popup_scandetail = (ListView) myDialog.findViewById(R.id.lv_warehouse_grn_new_popup_scandetail);
        bt_warehouse_grn_new_popup_ok = (Button) myDialog.findViewById(R.id.bt_warehouse_grn_new_popup_ok);
        et_warehouse_grn_new_popup_scan.setOnTouchListener(new View.OnTouchListener() {
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
        et_warehouse_grn_new_popup_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanItemcode();
                }
                return false;
            }
        });
        bt_warehouse_grn_new_popup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanItemcode();
            }
        });
        bt_warehouse_grn_new_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadScanTotal();
                myDialog.dismiss();
            }
        });
        loadScan();
        myDialog.show();
        et_warehouse_grn_new_popup_scan.requestFocus();
    }

    void scanItemcode() {
        String scanVal = et_warehouse_grn_new_popup_scan.getText().toString().trim().toUpperCase();
        tv_warehouse_grn_new_popup_last_scan.setText(scanVal);
        et_warehouse_grn_new_popup_scan.setText("");
        b_Result = objWarehouseGRNNewControl.validateScanPalletOrBox(scanVal);
        if (!b_Result) {
            okMessage("Warehouse GRN", objGlobal.getErrorMessage());
        }
        loadScan();
        et_warehouse_grn_new_popup_scan.requestFocus();
    }

    private class LoadGinDetails extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        String ginNo = et_wh_grn_ginno.getText().toString();

        public LoadGinDetails() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading GIN, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objWarehouseGRNNewControl.loadGinDetails(ginNo);
                if (b_Result == false) {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                okMessage("Warehouse GRN", objGlobal.getErrorMessage());
            } else {
                tv_wh_grn_gindate.setText(objWarehouseGRNNewGlobal.getGinDate());
                et_wh_grn_warehouse_from.setText(objWarehouseGRNNewGlobal.getWarehouseFrom());
                et_wh_grn_warehouse_to.setText(objWarehouseGRNNewGlobal.getWarehouseTo());
                saredRef.saveGinNo(ginNo);
                saredRef.saveGinDate(objWarehouseGRNNewGlobal.getGinDate());
                saredRef.saveWHFrom(objWarehouseGRNNewGlobal.getWarehouseFrom());
                saredRef.saveWHTo(objWarehouseGRNNewGlobal.getWarehouseTo());
                et_wh_grn_ginno.setEnabled(false);
                bt_wh_grn_scan_gin.setEnabled(false);
                loadScanTotal();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private boolean clearAll() {
        b_Result = objWarehouseGRNNewControl.grnClear();
        if (!b_Result) {
            okMessage("WarehouseGRNFragment:clearAll ", objGlobal.getErrorMessage());
            vibrate(500);
            return false;
        } else {
            et_wh_grn_ginno.setText("");
            tv_wh_grn_gindate.setText("");
            et_wh_grn_warehouse_to.setText("");
            et_wh_grn_warehouse_from.setText("");
            saredRef.saveGinNo("");
            saredRef.saveGinDate("");
            et_wh_grn_ginno.setEnabled(true);
            bt_wh_grn_scan_gin.setEnabled(true);
            loadScanTotal();
        }
        return true;
    }

    void loadScanTotal() {
        listWarehouseGRNNewScanCountTicket.clear();
        listWarehouseGRNNewScanCountTicket = objWarehouseGRNNewControl.loadGinGrnScanCount();
        objMyScanWarehouseGrnTotalCountAdp = new WarehouseGRNNewFragment.MyScanWarehouseGrnTotalCountAdp(listWarehouseGRNNewScanCountTicket);
        lv_wh_grn_details.setAdapter(objMyScanWarehouseGrnTotalCountAdp);
        tv_wh_grn_scan_plt_count.setText(String.valueOf(objWarehouseGRNNewGlobal.getScanPallets()));
        tv_wh_grn_total_plt_count.setText(String.valueOf(objWarehouseGRNNewGlobal.getTotalPallets()));
        tv_wh_grn_total_plt_box_scan_count.setText(String.valueOf(objWarehouseGRNNewGlobal.getScanBoxes()));
        tv_wh_grn_total_plt_box_count.setText(String.valueOf(objWarehouseGRNNewGlobal.getTotalBoxes()));
    }

    void loadScan() {
        listWarehouseGRNNewScanTicket.clear();
        listWarehouseGRNNewScanTicket = objWarehouseGRNNewControl.loadGinGrnScan();
        objMyScanWarehouseGrnScanAdp = new WarehouseGRNNewFragment.MyScanWarehouseGrnScanAdp(listWarehouseGRNNewScanTicket);
        lv_warehouse_grn_new_popup_scandetail.setAdapter(objMyScanWarehouseGrnScanAdp);
    }

    private class MyScanWarehouseGrnScanAdp extends BaseAdapter {
        public ArrayList<WarehouseGRNNewScanTicket> listWarehouseGRNNewScanTicket;

        public MyScanWarehouseGrnScanAdp(ArrayList<WarehouseGRNNewScanTicket> listWarehouseGRNNewScanTicket) {
            this.listWarehouseGRNNewScanTicket = listWarehouseGRNNewScanTicket;
        }

        @Override
        public int getCount() {
            return listWarehouseGRNNewScanTicket.size();
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
            View myView = mInflater.inflate(R.layout.warehouse_grn_new_load_items_ticket, null);
            final WarehouseGRNNewScanTicket s = listWarehouseGRNNewScanTicket.get(position);

            TextView tv_ticket_wh_grn_new_scan_palletno = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_scan_palletno);
            tv_ticket_wh_grn_new_scan_palletno.setText(String.valueOf(s.palletno));

            TextView tv_ticket_wh_grn_new_scan_boxno = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_scan_boxno);
            tv_ticket_wh_grn_new_scan_boxno.setText(String.valueOf(s.boxNo));

            TextView tv_ticket_wh_grn_new_scan_toteid = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_scan_toteid);
            tv_ticket_wh_grn_new_scan_toteid.setText(String.valueOf(s.toteID));

            Button bt_ticket_wh_grn_new_scan_delete=(Button)myView.findViewById(R.id.bt_ticket_wh_grn_new_scan_delete);
            bt_ticket_wh_grn_new_scan_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to delete the selected pallet (" + s.palletno + ")?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objWarehouseGRNNewControl.deleteSelectedPallet(s.palletno, s.boxNo);
                                    if (!b_Result) {
                                        okMessage("WarehouseGRNFragment:bt_wh_grn_save", objGlobal.getErrorMessage());
                                        vibrate(500);
                                    }
                                    loadScan();
                                    et_warehouse_grn_new_popup_scan.requestFocus();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bt_wh_grn_save.requestFocus();
                                }
                            })
                            .show();
                }
            });

            return myView;
        }
    }
    private class MyScanWarehouseGrnTotalCountAdp extends BaseAdapter {
        public ArrayList<WarehouseGRNNewScanCountTicket> listWarehouseGRNNewScanCountTicket;

        public MyScanWarehouseGrnTotalCountAdp(ArrayList<WarehouseGRNNewScanCountTicket> listWarehouseGRNNewScanCountTicket) {
            this.listWarehouseGRNNewScanCountTicket = listWarehouseGRNNewScanCountTicket;
        }

        @Override
        public int getCount() {
            return listWarehouseGRNNewScanCountTicket.size();
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
            View myView = mInflater.inflate(R.layout.warehouse_grn_new_load_ticket_main, null);
            final WarehouseGRNNewScanCountTicket s = listWarehouseGRNNewScanCountTicket.get(position);

            TextView tv_ticket_wh_grn_new_palletno = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_palletno);
            tv_ticket_wh_grn_new_palletno.setText(String.valueOf(s.palletno));

            TextView tv_ticket_wh_grn_new_tcount = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_tcount);
            tv_ticket_wh_grn_new_tcount.setText(String.valueOf(s.totBox));

            TextView tv_ticket_wh_grn_new_scount = (TextView) myView.findViewById(R.id.tv_ticket_wh_grn_new_scount);
            tv_ticket_wh_grn_new_scount.setText(String.valueOf(s.scanBox));

            Button bt_grn_direct_delivery_item_scan_Select=(Button)myView.findViewById(R.id.bt_ticket_wh_grn_view);
            bt_grn_direct_delivery_item_scan_Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            if (s.diff != 0) {
                tv_ticket_wh_grn_new_palletno.setTextColor(Color.RED);
                tv_ticket_wh_grn_new_tcount.setTextColor(Color.RED);
                tv_ticket_wh_grn_new_scount.setTextColor(Color.RED);
            }
            return myView;
        }
    }

    void vibrate(int duration){
        Vibrator v = (Vibrator) getContext().getSystemService(Context. VIBRATOR_SERVICE );
        assert v != null;
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            v.vibrate(VibrationEffect. createOneShot (duration , VibrationEffect. DEFAULT_AMPLITUDE )) ;
        } else {
            v.vibrate( duration ) ;
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