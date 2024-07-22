package com.bflgroup.warehouse.ui.transferbluetotote.eurobox;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.transferbluetotote.eurobox.ToteidDetails;

import java.sql.SQLException;
import java.util.ArrayList;

public class TransferBlueToEuroBoxFragment extends Fragment {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private TransferBlueToEuroBoxControl objTransferBlueEuroControl = new TransferBlueToEuroBoxControl();
    private BluetoteEuroBoxSharedRef objBluetoteEuroRef;

    ArrayList<ToteidDetails> ToteIdDetails = new ArrayList<ToteidDetails>();
    MyTransferStatusAdp objTransferStatusAdp = null;

    private EditText et_transfers_toteid;
    private Button bt_transfers_toteid_scan;
    private TextView tv_pallet_no;
    private Button bt_div_clear;
    private Button bt_div_save;
    private ListView lv_div_tote_details;
    private BlueToteEuroBoxGlobal objBlueToteEuroBoxGlobal = BlueToteEuroBoxGlobal.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blue_to_euro_box, container, false);

        et_transfers_toteid = (EditText) view.findViewById(R.id.et_transfers_toteid);
        bt_transfers_toteid_scan = (Button) view.findViewById(R.id.bt_transfers_toteid_scan);
        tv_pallet_no = (TextView) view.findViewById(R.id.tv_pallet_no);
        bt_div_clear = (Button) view.findViewById(R.id.bt_div_clear);
        bt_div_save = (Button) view.findViewById(R.id.bt_div_save);
        lv_div_tote_details = (ListView) view.findViewById(R.id.lv_div_tote_details);

        objBluetoteEuroRef = new BluetoteEuroBoxSharedRef(getContext());

        if(objBluetoteEuroRef.LoadPalletType() != ""){
            ToteIdDetails = objTransferBlueEuroControl.GetTote();
            objTransferStatusAdp = new MyTransferStatusAdp(ToteIdDetails);
            lv_div_tote_details.setAdapter(objTransferStatusAdp);

        }

        et_transfers_toteid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_transfers_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(ScanToteId()) {
                        et_transfers_toteid.requestFocus();
                        et_transfers_toteid.setFocusable(true);
                        et_transfers_toteid.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        bt_transfers_toteid_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ScanToteId()) {
                    et_transfers_toteid.requestFocus();
                    et_transfers_toteid.setFocusable(true);
                    et_transfers_toteid.setText("");
                }
            }
        });

        bt_div_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(objTransferStatusAdp!=null) {
                        if (objTransferBlueEuroControl.InsertUpcBox()) {
                            tv_pallet_no.setText(objBlueToteEuroBoxGlobal.getPalletNo());
                            okMessage("SUCCESS", "Inserted Successfully with palletNo - " +objBlueToteEuroBoxGlobal.palletNo);
//                            et_transfers_toteid.setText("");
//                            if(objTransferBlueEuroControl.deletetemp()){
//                                lv_div_tote_details.setAdapter(null);
//                            }
                            clear();
                        } else {
                            okMessage("Alert", objGlobal.getErrorMessage());
                        }
                    }else{
                        okMessage("Alert", "Pls scan Toteid");
                    }
                } catch (SQLException e) {
                    okMessage("Alert", e.getMessage().toString());
                }
            }
        });


        bt_div_clear.setOnClickListener(new View.OnClickListener() {
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

        return view;

    }


    public boolean ScanToteId(){
        String toteid = et_transfers_toteid.getText().toString();
        if(et_transfers_toteid.getText().toString().isEmpty()) {

            AlertDialog(getContext(), "Please Scan Toteid");
            return false;
        }else{
            ToteIdDetails = objTransferBlueEuroControl.ScanTote(toteid, getActivity());
            objTransferStatusAdp = new MyTransferStatusAdp(ToteIdDetails);
            lv_div_tote_details.setAdapter(objTransferStatusAdp);
            et_transfers_toteid.requestFocus();
            et_transfers_toteid.setText("");


            return true;
        }

    }

    public void AlertDialog(Context context, String message) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private class MyTransferStatusAdp extends BaseAdapter {
        public  ArrayList<ToteidDetails> ToteidDetailsList;

        public MyTransferStatusAdp(ArrayList<ToteidDetails> ToteidDetails) {
            this.ToteidDetailsList = ToteidDetails;
        }

        @Override
        public int getCount() {
            return ToteidDetailsList.size();
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
            View myView = mInflater.inflate(R.layout.toteid_eurobox_details, null);
            final ToteidDetails s = ToteidDetailsList.get(position);
            TextView tv_Itemcode_details = (TextView) myView.findViewById(R.id.tv_Itemcode_details);
            tv_Itemcode_details.setText(String.valueOf(s.Toteid));
            TextView tv_boxno_details = (TextView) myView.findViewById(R.id.tv_boxno_details);
            tv_boxno_details.setText(String.valueOf(s.BoxNo));
            TextView tv_pallettype = (TextView) myView.findViewById(R.id.tv_pallettype);
            tv_pallettype.setText(String.valueOf(s.PalletType));
            TextView tv_tote_id_qty = (TextView) myView.findViewById(R.id.tv_tote_id_qty);
            tv_tote_id_qty.setText(String.valueOf(s.Qty));
            return myView;
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

    private void clear(){

         et_transfers_toteid.setText("");

         //tv_pallet_no.setText("");
        objBluetoteEuroRef.savePalletType("");
        objBlueToteEuroBoxGlobal.setPalletType("");
        if(objTransferBlueEuroControl.deletetemp()){
            lv_div_tote_details.setAdapter(null);
        }


    }

}