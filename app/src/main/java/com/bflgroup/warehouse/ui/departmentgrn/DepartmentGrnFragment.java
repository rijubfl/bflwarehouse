package com.bflgroup.warehouse.ui.departmentgrn;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DepartmentGrnFragment extends Fragment {

    MyListBoxAdapter objMyListBoxAdapter;
    DepartmentGRNControl objPalletBoxCountControl;
    ListView lv_details;
    EditText et_rack_in_out_pallettop;

    EditText et_rack_in_out_toteid;
    EditText et_transfer;
    EditText et_rack_in_out_BoxCount;

    TextView TextView8;
    //  Spinner sp_rack_in_out_warehouse;
    TextView box_count_pallet1;
    TextView TextView4;

    TextView TextView5;
    Button bt_rack_in_out_clear;
    Button bt_rack_in_out_save;
    //    EditText et_rack_in_out_rack;
    Spinner sp_rack_in_out_warehouseFrom;
    Spinner sp_rack_in_out_warehouseTo;
    RadioButton rb_pallet_category;
    RadioButton rb_toteid_category;
    RadioGroup rg_pallet_building_category;
    RadioButton rb_transfer_category;

    //    Button bt_search;
    Button btn_Box_search;
    private Boolean isvalidrack = false;

    Boolean strflg = false;
    public ArrayList<BoxItemList> listBinScanToteId = new ArrayList<>();
    private Global objGlobal = Global.getInstance();

    DepartmentGRNShared objPalletBoxCountShared;
    public DepartmentGrnFragment() {
        // Required empty public constructor
    }

    public static DepartmentGrnFragment newInstance(String param1, String param2) {
        DepartmentGrnFragment fragment = new DepartmentGrnFragment();
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
        View view =  inflater.inflate(R.layout.fragment_wh_department_grn, container, false);

        lv_details =  view.findViewById(R.id.lv_details);
        et_rack_in_out_pallettop =  view.findViewById(R.id.et_rack_in_out_pallettop);
        et_rack_in_out_toteid =  view.findViewById(R.id.et_rack_in_out_toteid);
        sp_rack_in_out_warehouseFrom = (Spinner) view.findViewById(R.id.sp_rack_in_out_warehouseFrom);
        sp_rack_in_out_warehouseTo = (Spinner) view.findViewById(R.id.sp_rack_in_out_warehouseTo);
        box_count_pallet1 = view.findViewById(R.id.box_count_pallet1);
        et_rack_in_out_BoxCount = view.findViewById(R.id.et_rack_in_out_BoxCount);
        et_transfer = view.findViewById(R.id.et_transfer);
        bt_rack_in_out_clear = view.findViewById(R.id.bt_rack_in_out_clear);
        bt_rack_in_out_save = view.findViewById(R.id.bt_rack_in_out_save);
        rb_pallet_category = (RadioButton) view.findViewById(R.id.rb_pallet_category);
        rb_toteid_category = (RadioButton) view.findViewById(R.id.rb_toteid_category);
        rg_pallet_building_category = (RadioGroup) view.findViewById(R.id.rg_pallet_building_category);
        btn_Box_search = view.findViewById(R.id.btn_Box_search);
        TextView5 = view.findViewById(R.id.TextView5);
        TextView8 = view.findViewById(R.id.TextView8);
        TextView4 = view.findViewById(R.id.TextView4);
        rb_transfer_category = (RadioButton) view.findViewById(R.id.rb_transfer_category);
        objPalletBoxCountControl = new DepartmentGRNControl();

        objPalletBoxCountShared=new DepartmentGRNShared(getContext());

        //tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());

        et_rack_in_out_pallettop.requestFocus();
        et_rack_in_out_pallettop.setFocusable(true);

        String warehouse = objGlobal.getWarehouse();
        List<String> arr = objPalletBoxCountControl.getWarehouse(warehouse);
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_rack_in_out_warehouseFrom.setAdapter(arrayAdp);

        List<String> arr1 = objPalletBoxCountControl.getWarehouse(warehouse);
        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_rack_in_out_warehouseTo.setAdapter(arrayAdp1);



        rg_pallet_building_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.d("chk", "id" + checkedId);

                if (checkedId == R.id.rb_toteid_category) {
                    //R.id.a = RadioButton ID in layout

                  //  okMessage("","Checked");
                    et_rack_in_out_BoxCount.setVisibility(View.GONE);
                    TextView5.setVisibility(View.GONE);
                    btn_Box_search.setVisibility(View.GONE);

                    et_rack_in_out_pallettop.setVisibility(View.GONE);
                    TextView4.setVisibility(View.GONE);
                    box_count_pallet1.setVisibility(View.GONE);
                    et_rack_in_out_toteid.setVisibility(View.VISIBLE);
                    TextView8.setVisibility(View.VISIBLE);

                    et_rack_in_out_toteid.setFocusable(true);
                    et_rack_in_out_toteid.requestFocus();

                    bt_rack_in_out_save.setVisibility(View.GONE);
                    bt_rack_in_out_clear.setVisibility(View.GONE);
                    et_transfer.setVisibility(View.GONE);


                    //some code
                } else if (checkedId == R.id.rb_pallet_category) {
                    //some code
                    et_rack_in_out_BoxCount.setVisibility(View.VISIBLE);
                    TextView5.setVisibility(View.VISIBLE);
                    btn_Box_search.setVisibility(View.VISIBLE);
                    et_rack_in_out_pallettop.setVisibility(View.VISIBLE);
                    TextView4.setVisibility(View.VISIBLE);
                    box_count_pallet1.setVisibility(View.VISIBLE);

                    et_rack_in_out_toteid.setVisibility(View.GONE);
                    TextView8.setVisibility(View.GONE);
                    lv_details.setAdapter(null);
                    bt_rack_in_out_save.setVisibility(View.VISIBLE);
                    bt_rack_in_out_clear.setVisibility(View.VISIBLE);
                    et_transfer.setVisibility(View.GONE);
                }
                else if (checkedId == R.id.rb_transfer_category){

                    et_rack_in_out_BoxCount.setVisibility(View.GONE);
                    TextView5.setVisibility(View.GONE);
                    btn_Box_search.setVisibility(View.GONE);

                    et_rack_in_out_pallettop.setVisibility(View.GONE);
                    TextView4.setVisibility(View.GONE);
                    box_count_pallet1.setVisibility(View.GONE);
                    et_transfer.setVisibility(View.VISIBLE);
                    TextView8.setVisibility(View.VISIBLE);
                    TextView8.setText("Transfer No.");
                    et_transfer.setFocusable(true);
                    et_transfer.requestFocus();
                    bt_rack_in_out_save.setVisibility(View.GONE);
                    bt_rack_in_out_clear.setVisibility(View.GONE);
                    et_rack_in_out_toteid.setVisibility(View.GONE);



                }

                ArrayList<BoxItemList> listBinScanToteId = objPalletBoxCountControl.loadTotehistory();
                objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                lv_details.setAdapter(objMyListBoxAdapter);

            }

        });





        if(!objPalletBoxCountShared.loadPalletno().equals("")){

            et_rack_in_out_pallettop.setText(objPalletBoxCountShared.loadPalletno());

            sp_rack_in_out_warehouseFrom.setSelection(arrayAdp1.getPosition(objPalletBoxCountShared.loadWarehouseFrom().toString()));
            sp_rack_in_out_warehouseTo.setSelection(arrayAdp1.getPosition(objPalletBoxCountShared.loadWarehouseTO().toString()));

            listBinScanToteId = objPalletBoxCountControl.loadPalletDetails(objPalletBoxCountShared.loadPalletno(),sp_rack_in_out_warehouseTo.getSelectedItem().toString());

            et_rack_in_out_pallettop.setEnabled(false);
            sp_rack_in_out_warehouseTo.setEnabled(false);
            sp_rack_in_out_warehouseFrom.setEnabled(false);
            rb_toteid_category.setEnabled(false);

            objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
            lv_details.setAdapter(objMyListBoxAdapter);
            try {
                String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());

                int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), sp_rack_in_out_warehouseTo.getSelectedItem().toString());
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
                    if (objPalletBoxCountControl.isVerified(et_rack_in_out_pallettop.getText().toString(), sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString())) {

                        alert.setMessage("Do you want to Save this pallet - " + objPalletBoxCountShared.loadPalletno())
                                .setTitle("Confirmation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (objPalletBoxCountControl.SavePalletDetails(et_rack_in_out_pallettop.getText().toString(), sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString())) {
                                            okMessage("Success", "GRN for this pallet - "+ et_rack_in_out_pallettop.getText().toString()+" is saved Successfully");
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
                    }else{
                        okMessage("Alert","Please verify all the boxes");
                    }
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
        et_rack_in_out_toteid.setOnTouchListener(new View.OnTouchListener() {
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
        et_transfer.setOnTouchListener(new View.OnTouchListener() {
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




        et_transfer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String Transfer = et_transfer.getText().toString();

                    if (sp_rack_in_out_warehouseTo.getSelectedItemId()==0 || sp_rack_in_out_warehouseFrom.getSelectedItemId()==0) {
                        okMessage("Alert", "Please select the Warehouse");
                        et_rack_in_out_toteid.setText("");
                        et_rack_in_out_toteid.requestFocus();
                    } else {
                        if (Transfer.equals("")) {
                            okMessage("Alert", "Pallet number should not be empty");
                        } else {
                            if (isValidTransfer(Transfer.trim())) {
                                //isvalidrack = true;

                               // String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                               // box_count_pallet1.setText(BoxCountPallet1);
                                ArrayList<BoxItemList> listBinScanToteId = objPalletBoxCountControl.InsertBox(et_transfer.getText().toString().trim(), sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                lv_details.setAdapter(objMyListBoxAdapter);
                                et_rack_in_out_BoxCount.setEnabled(true);
                                et_rack_in_out_BoxCount.setFocusable(true);
                                objPalletBoxCountShared.saveWarehouseTo(sp_rack_in_out_warehouseTo.getSelectedItem().toString());
                                objPalletBoxCountShared.saveWarehouseFrom(sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                et_transfer.setText("");
                                et_transfer.requestFocus();
                                et_transfer.setFocusable(true);
                                return true;
                            } else {
                                et_transfer.setText("");
                                et_transfer.requestFocus();
                                et_transfer.setFocusable(true);
                                // isvalidrack = false;

                            }
                        }

                    }
                }
                else{
                   return false;
                }
                return false;

            }
        });

        et_rack_in_out_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String Toteid = et_rack_in_out_toteid.getText().toString();

                    if (sp_rack_in_out_warehouseTo.getSelectedItemId()==0 || sp_rack_in_out_warehouseFrom.getSelectedItemId()==0) {
                        okMessage("Alert", "Please select the Warehouse");
                        et_rack_in_out_toteid.setText("");
                        et_rack_in_out_toteid.requestFocus();
                    } else {
                        if (Toteid.equals("")) {
                            okMessage("Alert", "Pallet number should not be empty");
                        } else {
                            if (isPalletvalid(Toteid.trim())) {
                                //isvalidrack = true;

                               // String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                               // box_count_pallet1.setText(BoxCountPallet1);
                                ArrayList<BoxItemList> listBinScanToteId = objPalletBoxCountControl.InsertBox(et_rack_in_out_toteid.getText().toString().trim(), sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                lv_details.setAdapter(objMyListBoxAdapter);
                                et_rack_in_out_BoxCount.setEnabled(true);
                                et_rack_in_out_BoxCount.setFocusable(true);
                                objPalletBoxCountShared.saveWarehouseTo(sp_rack_in_out_warehouseTo.getSelectedItem().toString());
                                objPalletBoxCountShared.saveWarehouseFrom(sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                et_rack_in_out_toteid.setText("");
                                et_rack_in_out_toteid.requestFocus();
                                et_rack_in_out_toteid.setFocusable(true);
                                return true;
                            } else {
                                et_rack_in_out_toteid.setText("");
                                et_rack_in_out_toteid.requestFocus();
                                et_rack_in_out_toteid.setFocusable(true);
                                // isvalidrack = false;

                            }
                        }

                    }
                }
                else{
                   return false;
                }
                return false;

            }
        });


        et_rack_in_out_pallettop.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String pallet = et_rack_in_out_pallettop.getText().toString();

                    if (sp_rack_in_out_warehouseTo.getSelectedItemId()==0 || sp_rack_in_out_warehouseFrom.getSelectedItemId()==0) {
                        okMessage("Alert", "Please select the Warehouse");
                        et_rack_in_out_pallettop.setText("");
                        et_rack_in_out_pallettop.requestFocus();
                    } else {
                        if (pallet.equals("")) {
                            okMessage("Alert", "Pallet number should not be empty");
                        } else {
                            if (isPalletvalid(pallet)) {
                                //isvalidrack = true;

                                String BoxCountPallet1 = objPalletBoxCountControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                                box_count_pallet1.setText(BoxCountPallet1);
                                ArrayList<BoxItemList> listBinScanToteId = objPalletBoxCountControl.loadBoxespallet(et_rack_in_out_pallettop.getText().toString(), sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                                lv_details.setAdapter(objMyListBoxAdapter);
                                et_rack_in_out_BoxCount.setEnabled(true);
                                et_rack_in_out_BoxCount.setFocusable(true);
                                objPalletBoxCountShared.saveWarehouseTo(sp_rack_in_out_warehouseTo.getSelectedItem().toString());
                                objPalletBoxCountShared.saveWarehouseFrom(sp_rack_in_out_warehouseFrom.getSelectedItem().toString());
                                sp_rack_in_out_warehouseTo.setEnabled(false);
                                sp_rack_in_out_warehouseFrom.setEnabled(false);

                                return true;
                            } else {
                                et_rack_in_out_pallettop.setText("");
                                et_rack_in_out_pallettop.requestFocus();
                                et_rack_in_out_pallettop.setFocusable(true);
                                // isvalidrack = false;

                            }
                        }

                    }
                }
                else{
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
                alert.setMessage("Are you sure to remove this pallet - "+et_rack_in_out_pallettop.getText().toString())
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

        btn_Box_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (BoxScanResult()) {
                    et_rack_in_out_BoxCount.requestFocus();
                    et_rack_in_out_BoxCount.setFocusable(true);
                    strflg = true;

                } else {
                    et_rack_in_out_BoxCount.requestFocus();
                    et_rack_in_out_BoxCount.setFocusable(true);

                }
            }
                //return false;
        });

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




        if(isBoxvalid(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString())){


            try {
                if(objPalletBoxCountControl.BoxesInPallets(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString(),sp_rack_in_out_warehouseTo.getSelectedItem().toString())){
                    listBinScanToteId = objPalletBoxCountControl.UpdateLoadBoxes(et_rack_in_out_BoxCount.getText().toString(),et_rack_in_out_pallettop.getText().toString(),sp_rack_in_out_warehouseTo.getSelectedItem().toString());
                    objMyListBoxAdapter = new MyListBoxAdapter(listBinScanToteId);
                    lv_details.setAdapter(objMyListBoxAdapter);
                    int Count = objPalletBoxCountControl.getCountBoxesScanned(et_rack_in_out_pallettop.getText().toString(), sp_rack_in_out_warehouseTo.getSelectedItem().toString());
                    objMyListBoxAdapter.notifyDataSetChanged();
                    box_count_pallet1.setText(Count +"/" + DepartmentGRNGlobal.getPalletCount());
                    et_rack_in_out_BoxCount.setText("");
                    et_rack_in_out_pallettop.setEnabled(false);
                    rb_toteid_category.setEnabled(false);
                    objPalletBoxCountShared.savePalletno(et_rack_in_out_pallettop.getText().toString());

                    strflg = true;

                }
                else{
                    okMessage("Alert","This Box - " + et_rack_in_out_BoxCount.getText().toString() + " is not the Pallet");

                }



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

    private Boolean isValidTransfer(String Transfer)
    {
        try {
        if(objPalletBoxCountControl.isPalletSaved(Transfer, sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString())){

            okMessage("Alert","Pallet no/tote id already saved in WH department GRN - " + Transfer.toString());
            return false;
        }else {
            if (objPalletBoxCountControl.isValidTransfer(Transfer, getContext())) {
                return true;
            }
            else {
                okMessage("Alert 1", "Transfer number " + Transfer.trim() + " is Invalid");
                et_rack_in_out_toteid.setText("");
                et_rack_in_out_toteid.requestFocus();
                et_rack_in_out_toteid.setFocusable(true);
                et_rack_in_out_pallettop.setText("");
                et_rack_in_out_pallettop.requestFocus();
                et_rack_in_out_pallettop.setFocusable(true);
                return false;
            }
        }
    }catch (Exception e){

        return false;
    }
    }

    private Boolean isPalletvalid(String pallet){
        try {

            if(objPalletBoxCountControl.isPalletSaved(pallet, sp_rack_in_out_warehouseTo.getSelectedItem().toString(), sp_rack_in_out_warehouseFrom.getSelectedItem().toString())){

                okMessage("Alert","Pallet no/tote id already saved in WH department GRN - " + pallet.toString());
                //et_rack_in_out_toteid.setText("");
               // et_rack_in_out_toteid.requestFocus();
              //  et_rack_in_out_toteid.setFocusable(true);

//                et_rack_in_out_pallettop.setText("");
//                et_rack_in_out_pallettop.requestFocus();
//                et_rack_in_out_pallettop.setFocusable(true);

                return false;
            }else {
                if (objPalletBoxCountControl.isValidPallet(pallet, getContext())) {
                    return true;
                }
                else if (objPalletBoxCountControl.isValidbox(et_rack_in_out_BoxCount.getText().toString(),pallet, getContext())) {
                    return true;
                }
                else {
                    okMessage("Alert 1", "Pallet Number/Toteid " + pallet.trim() + " is closed already");
                    et_rack_in_out_toteid.setText("");
                    et_rack_in_out_toteid.requestFocus();
                    et_rack_in_out_toteid.setFocusable(true);
                    et_rack_in_out_pallettop.setText("");
                    et_rack_in_out_pallettop.requestFocus();
                    et_rack_in_out_pallettop.setFocusable(true);
                    return false;
                }
            }

        }catch (Exception e){

            return false;
        }
    }

    private Boolean isBoxvalid(String Boxno,String PalletNo){
        try {
            if (objPalletBoxCountControl.isValidbox(Boxno,PalletNo, getContext())) {
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
            View myView = mInflater.inflate(R.layout.pallet_item_list, null);
            final BoxItemList s = itBoxItemList.get(position);

            TextView tv_srno = (TextView) myView.findViewById(R.id.tv_srno);
            tv_srno.setText(String.valueOf(s.Srno));

            TextView tv_palletno = (TextView) myView.findViewById(R.id.tv_palletno);
            tv_palletno.setText(String.valueOf(s.Palletno));

            TextView tv_boxno_original = (TextView) myView.findViewById(R.id.tv_boxno_original);
            tv_boxno_original.setText(String.valueOf(s.boxNoOriginal));

            TextView tv_toteid = (TextView) myView.findViewById(R.id.tv_toteid);
            tv_toteid.setText(String.valueOf(s.toteid));

            TextView tv_verified = (TextView) myView.findViewById(R.id.tv_verified);
            tv_verified.setText(String.valueOf(s.verified));



            return myView;
        }
    }


    public void Clear(){
        et_rack_in_out_BoxCount.setText("");
        et_rack_in_out_pallettop.setText("");
        et_rack_in_out_pallettop.setEnabled(true);
        objPalletBoxCountShared.savePalletno("");
        sp_rack_in_out_warehouseTo.setEnabled(true);
        sp_rack_in_out_warehouseFrom.setEnabled(true);
        rb_toteid_category.setEnabled(true);
        objPalletBoxCountShared.savePalletno("");
        objPalletBoxCountShared.saveWarehouseTo("");
        objPalletBoxCountShared.saveWarehouseFrom("");



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