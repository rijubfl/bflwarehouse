package com.bflgroup.warehouse.ui.boxreconcilation;

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

import java.sql.SQLException;
import java.util.ArrayList;


public class PalletBoxCountFragment extends Fragment {

    MyListBoxAdapter objMyListBoxAdapter;
    PalletBoxCountControl objPalletBoxCountControl;
    ListView lv_details;
    EditText et_rack_in_out_pallettop;
    EditText et_rack_in_out_BoxCount;
    //  Spinner sp_rack_in_out_warehouse;
    TextView box_count_pallet1;
    Button bt_rack_in_out_clear;
    Button bt_rack_in_out_save;
    //    EditText et_rack_in_out_rack;
    TextView tv_rack_in_out_warehouse;

    //    Button bt_search;
//    Button btn_Box_search;
    private Boolean isvalidrack = false;

    Boolean strflg = false;
    public ArrayList<BoxItemList> listBinScanToteId = new ArrayList<>();
    private Global objGlobal = Global.getInstance();

    PalletBoxCountShared objPalletBoxCountShared;
    public PalletBoxCountFragment() {
        // Required empty public constructor
    }

    public static PalletBoxCountFragment newInstance(String param1, String param2) {
        PalletBoxCountFragment fragment = new PalletBoxCountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pallet_box_count, container, false);

        lv_details =  view.findViewById(R.id.lv_details);
        et_rack_in_out_pallettop =  view.findViewById(R.id.et_rack_in_out_pallettop);
        tv_rack_in_out_warehouse = view.findViewById(R.id.tv_rack_in_out_warehouse);

        box_count_pallet1 = view.findViewById(R.id.box_count_pallet1);
        et_rack_in_out_BoxCount = view.findViewById(R.id.et_rack_in_out_BoxCount);
        bt_rack_in_out_clear = view.findViewById(R.id.bt_rack_in_out_clear);
        bt_rack_in_out_save = view.findViewById(R.id.bt_rack_in_out_save);
        //  btn_Box_search = view.findViewById(R.id.btn_Box_search);
        objPalletBoxCountControl = new PalletBoxCountControl();

        objPalletBoxCountShared=new PalletBoxCountShared(getContext());

        tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());
       // tv_rack_in_out_warehouse.setText("YOTO");

        et_rack_in_out_pallettop.requestFocus();
        et_rack_in_out_pallettop.setFocusable(true);


        if(!objPalletBoxCountShared.loadPalletno().equals("")){

            et_rack_in_out_pallettop.setText(objPalletBoxCountShared.loadPalletno());

            listBinScanToteId = objPalletBoxCountControl.loadPalletDetails(objPalletBoxCountShared.loadPalletno(),tv_rack_in_out_warehouse.getText().toString());

            et_rack_in_out_pallettop.setEnabled(false);
            tv_rack_in_out_warehouse.setEnabled(false);

            objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
            lv_details.setAdapter(objMyListBoxAdapter);
            try {
                String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());

                int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString());
                objMyListBoxAdapter.notifyDataSetChanged();
                box_count_pallet1.setText(Count +"/" + BoxCountPallet1);
                et_rack_in_out_BoxCount.setFocusable(true);
                et_rack_in_out_BoxCount.requestFocus();
            }catch(Exception e){
                okMessage("ALERT", e.toString());
            }
        }

        bt_rack_in_out_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_rack_in_out_pallettop.getText().toString().equals("") ) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Do you want to Save this pallet - " + objPalletBoxCountShared.loadPalletno() )
                            .setTitle("Confirmation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (objPalletBoxCountControl.SavePalletDetails(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString())) {

                                        okMessage("Success", "Pallet Saved Successfully");
                                        Clear();
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
                else{
                    okMessage("Alert", "Palletno or rack no is Empty");
                }
            }
        });


        et_rack_in_out_pallettop.setOnTouchListener(new View.OnTouchListener() {
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
        et_rack_in_out_BoxCount.setOnTouchListener(new View.OnTouchListener() {
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


        et_rack_in_out_pallettop.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String pallet = et_rack_in_out_pallettop.getText().toString();
                    if(pallet.equals("")){
                        okMessage("Alert", "Pallet number should not be empty");
                    }else {
                        if (isPalletvalid(pallet)) {
                            //isvalidrack = true;

                            String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                            box_count_pallet1.setText(BoxCountPallet1);
                            ArrayList<BoxItemList> listBinScanToteId = objPalletBoxCountControl.loadBoxespallet(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString());
                            objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                            lv_details.setAdapter(objMyListBoxAdapter);
                            et_rack_in_out_BoxCount.setEnabled(true);
                            et_rack_in_out_BoxCount.setFocusable(true);


                            return true;
                        } else {
                            et_rack_in_out_pallettop.setText("");
                            et_rack_in_out_pallettop.requestFocus();
                            // isvalidrack = false;

                        }
                    }

                } else {
                    return false;
                }
                return false;

            }
        });

        et_rack_in_out_BoxCount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {

                    if (BoxScanResult()) {
                        et_rack_in_out_BoxCount.requestFocus();
                        et_rack_in_out_BoxCount.setFocusable(true);
                        strflg = true;
                        return true;
                    } else {
                        et_rack_in_out_BoxCount.requestFocus();
                        et_rack_in_out_BoxCount.setFocusable(true);
                        return false;
                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            //et_rack_in_out_BoxCount.requestFocus();
                            et_rack_in_out_BoxCount.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
//                    //return false;
                }
                // return false;
            }
        });

        bt_rack_in_out_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure you want to clear this pallet - "+et_rack_in_out_pallettop.getText().toString())
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Clear();
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

//        btn_Box_search.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if (BoxScanResult()) {
//                    et_rack_in_out_BoxCount.requestFocus();
//                    et_rack_in_out_BoxCount.setFocusable(true);
//                    strflg = true;
//
//                } else {
//                    et_rack_in_out_BoxCount.requestFocus();
//                    et_rack_in_out_BoxCount.setFocusable(true);
//
//                }
//            }
//                //return false;
//        });

        return view;
    }



    public Boolean BoxScanResult(){

        if(et_rack_in_out_BoxCount.getText().toString().equals("")){
            okMessage("Alert", "Please scan the Boxno");
            return false;
        }
        if(et_rack_in_out_pallettop.getText().toString().equals("")){
            okMessage("Alert", "Please scan the Palletno");
            return false;
        }


        if(isBoxvalid(et_rack_in_out_BoxCount.getText().toString())){
            tv_rack_in_out_warehouse.setEnabled(false);
            tv_rack_in_out_warehouse.setClickable(false);

            try {
                if(objPalletBoxCountControl.BoxesInPallets(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString(),tv_rack_in_out_warehouse.getText().toString())){
                    listBinScanToteId = objPalletBoxCountControl.UpdateLoadBoxes(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString(),tv_rack_in_out_warehouse.getText().toString());
                    objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                    lv_details.setAdapter(objMyListBoxAdapter);
                    int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString());
                    objMyListBoxAdapter.notifyDataSetChanged();
                    box_count_pallet1.setText(Count +"/" + PalletBoxCountGlobal.getPalletCount());
                    et_rack_in_out_BoxCount.setText("");
                    et_rack_in_out_pallettop.setEnabled(false);
                    objPalletBoxCountShared.savePalletno(et_rack_in_out_pallettop.getText().toString());
                    strflg = true;

                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("This Box is not in the pallet. Are you sure to add this in the pallet - "+et_rack_in_out_pallettop.getText().toString())
                            .setTitle("Confirmation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listBinScanToteId = objPalletBoxCountControl.InsertloadBoxes(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString(),tv_rack_in_out_warehouse.getText().toString());
                                    objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                    lv_details.setAdapter(objMyListBoxAdapter);
                                    int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString());
                                    objMyListBoxAdapter.notifyDataSetChanged();
                                    box_count_pallet1.setText(Count +"/" + PalletBoxCountGlobal.getPalletCount());
                                    et_rack_in_out_pallettop.setEnabled(false);
//                                                    et_rack_in_out_rack.setEnabled(false);
                                    objPalletBoxCountShared.savePalletno(et_rack_in_out_pallettop.getText().toString());
//                                                    objPalletBoxCountShared.saveRackno(et_rack_in_out_rack.getText().toString());
                                    et_rack_in_out_BoxCount.setText("");

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                    et_rack_in_out_BoxCount.setText("");

                                }
                            })
                            .show();


                }

//                et_rack_in_out_BoxCount.setFocusable(true);
//                et_rack_in_out_BoxCount.requestFocus();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return true;

        }
        else{

            strflg = true;
            okMessage("Alert", "Box is Closed/Invalid - " + et_rack_in_out_BoxCount.getText().toString());
            et_rack_in_out_BoxCount.setText("");

            return false;
        }

    }

    private Boolean isPalletvalid(String pallet){
        try {

            if(objPalletBoxCountControl.isPalletSaved(pallet)){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("This Pallet is already saved. Do you want to make Changes to this Pallet again? -" +et_rack_in_out_pallettop.getText().toString())
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    listBinScanToteId = objPalletBoxCountControl.LoadPalletDetails(et_rack_in_out_pallettop.getText().toString());
                                    objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                    lv_details.setAdapter(objMyListBoxAdapter);
                                    int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), tv_rack_in_out_warehouse.getText().toString());
                                    objMyListBoxAdapter.notifyDataSetChanged();
                                    box_count_pallet1.setText(Count +"/" + PalletBoxCountGlobal.getPalletCount());
                                    et_rack_in_out_BoxCount.setText("");
                                    et_rack_in_out_pallettop.setEnabled(false);
                                    objPalletBoxCountShared.savePalletno(et_rack_in_out_pallettop.getText().toString());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                et_rack_in_out_pallettop.setText("");
                            }
                        })
                        .show();
                return true;
            }else {
                if (objPalletBoxCountControl.isValidPallet(pallet, getContext())) {
                    return true;
                } else {

                    return false;
                }
            }

        }catch (Exception e){

            return false;
        }
    }

    private Boolean isBoxvalid(String Boxno){
        try {
            if (objPalletBoxCountControl.isValidbox(Boxno, getContext())) {
                // strflg = true;
                return true;
            } else {
                //strflg = true;
                return false;
            }

        }catch (Exception e){

            return false;
        }
    }


    private class MyListBoxAdapter extends BaseAdapter {
        public ArrayList<BoxItemList> itBoxItemList;
        public MyListBoxAdapter(ArrayList<BoxItemList> itBoxItemList) {
            this.itBoxItemList = itBoxItemList;
        }
        @Override
        public int getCount() {
            return itBoxItemList.size();
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
            View myView = mInflater.inflate(R.layout.box_pallet_item_list, null);
            final BoxItemList s = itBoxItemList.get(position);

            TextView tv_srno = (TextView) myView.findViewById(R.id.tv_srno);
            tv_srno.setText(String.valueOf(s.Srno));

            TextView tv_palletno = (TextView) myView.findViewById(R.id.tv_palletno);
            tv_palletno.setText(String.valueOf(s.Palletno));

            TextView tv_boxno_original = (TextView) myView.findViewById(R.id.tv_boxno_original);
            tv_boxno_original.setText(String.valueOf(s.boxNoOriginal));

            TextView tv_toteid = (TextView) myView.findViewById(R.id.tv_toteid);
            tv_toteid.setText(String.valueOf(s.toteId));

            TextView tv_boxno_scanned = (TextView) myView.findViewById(R.id.tv_boxno_scanned);
            tv_boxno_scanned.setText(String.valueOf(s.boxNoScanned));



            return myView;
        }
    }


    public void Clear(){
        et_rack_in_out_BoxCount.setText("");
        et_rack_in_out_pallettop.setText("");
        et_rack_in_out_pallettop.setEnabled(true);
        objPalletBoxCountShared.savePalletno("");
        box_count_pallet1.setText("");
        if (objPalletBoxCountControl.deletetmp()) {
            lv_details.setAdapter(null);
        }
        et_rack_in_out_pallettop.requestFocus();
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