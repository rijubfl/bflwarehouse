package com.bflgroup.warehouse.ui.rackstocktake;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.boxreconcilation.PalletBoxCountFragment;
import com.bflgroup.warehouse.ui.boxreconcilation.PalletBoxCountGlobal;
import com.bflgroup.warehouse.ui.rackstocktake.model.RackHistoryModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class StockTakeFragment extends Fragment {


    private Spinner sp_rack_in_out_warehouse;
    private TextView et_rack_in_out_cellno;
    private ArrayAdapter<String> arrayAdpAction;

    private ArrayAdapter<Integer> arrayAdp;
    private StocktakeControl objStocktakeControl;
    private EditText et_rack_in_out_rack;
    private EditText et_rack_in_out_pallettop;
    private EditText et_rack_in_out_palletdown;
    private TextView listempty;
    private TextView TextView4;
    private TextView TextView5;
    StockTakeShared objstocktakeShared;
    ListView lv_rack_in_out_details;
    ArrayList<RackHistoryModel> listRackHistoryItem = new ArrayList<RackHistoryModel>();
    private Button bt_rack_in_out_save;
    private Button bt_search;
    private Button bt_rack_in_out_chear;
    private Boolean isvalidrack = false;
    private Boolean  isvalidpallet = false;
    RackItemAdapter rackItemAdapter = null;
    TextView box_count_pallet1;
    TextView box_count_pallet2;
    private Global objGlobal = Global.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocktake, container, false);
        sp_rack_in_out_warehouse = view.findViewById(R.id.sp_rack_in_out_warehouse);
        et_rack_in_out_rack = view.findViewById(R.id.et_rack_in_out_rack);
        bt_rack_in_out_save = view.findViewById(R.id.bt_rack_in_out_save);
        bt_rack_in_out_chear = view.findViewById(R.id.bt_rack_in_out_chear);
        TextView4 = view.findViewById(R.id.TextView4);
        TextView5 = view.findViewById(R.id.TextView5);
        bt_search = view.findViewById(R.id.bt_search);
        lv_rack_in_out_details = (ListView) view.findViewById(R.id.lv_rack_in_out_details);
        listempty = (TextView)  view.findViewById(R.id.listempty);
        et_rack_in_out_cellno = (TextView) view.findViewById(R.id.et_rack_in_out_cellno);
        objStocktakeControl = new StocktakeControl(getContext());
      //  et_rack_in_out_rack.getText().toString();
        objstocktakeShared=new StockTakeShared(getContext());
        et_rack_in_out_pallettop = view.findViewById(R.id.et_rack_in_out_pallettop);
        et_rack_in_out_palletdown = view.findViewById(R.id.et_rack_in_out_palletdown);
        box_count_pallet1 = (TextView)  view.findViewById(R.id.box_count_pallet1);
        box_count_pallet2 = (TextView)  view.findViewById(R.id.box_count_pallet2);


        loadWarehouse();
        loadrack();
        isValidPallet();
        et_rack_in_out_rack.setFocusable(true);
        et_rack_in_out_rack.requestFocus();

        et_rack_in_out_pallettop.setEnabled(false);
        et_rack_in_out_palletdown.setEnabled(false);



        bt_rack_in_out_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton();
            }
        });

        bt_rack_in_out_chear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rackvalid()){
                    isvalidrack = true;
                    sp_rack_in_out_warehouse.setEnabled(false);
                    sp_rack_in_out_warehouse.setClickable(false);
                    objstocktakeShared.saveWarehouse(sp_rack_in_out_warehouse.getSelectedItem().toString());

                    et_rack_in_out_pallettop.setEnabled(true);
                    et_rack_in_out_pallettop.requestFocus();
                }
                else{
                    isvalidrack = false;

                }
            }
        });

       if(!objstocktakeShared.loadWarehouse().equals("")) {


           sp_rack_in_out_warehouse.setSelection(arrayAdpAction.getPosition(objstocktakeShared.loadWarehouse().toString()));
           sp_rack_in_out_warehouse.setEnabled(false);
           loadRackHistoryItem();
       }
       sp_rack_in_out_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(sp_rack_in_out_warehouse.getSelectedItem().equals("BINRACK") || sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK") ||  sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN")){
                    TextView4.setText("Toteid");
                    TextView5.setVisibility(View.GONE);
                    et_rack_in_out_palletdown.setVisibility(View.GONE);
                    box_count_pallet2.setVisibility(View.GONE);
                }
                else{
                    TextView4.setText("Pallet 1");
                    TextView5.setVisibility(View.VISIBLE);
                    et_rack_in_out_palletdown.setVisibility(View.VISIBLE);
                    box_count_pallet2.setVisibility(View.VISIBLE);
                }

                if(sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN") || sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZAFLR")){
                    et_rack_in_out_cellno.setVisibility(View.VISIBLE);
                   // loadCellno();
                }
                else{
                    et_rack_in_out_cellno.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadRackHistoryItem();

    }


    private void loadWarehouse() {
        List<String> arr;
        arr = new ArrayList<String>();
        arr = objStocktakeControl.loadWarehouse();
        arrayAdpAction = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_rack_in_out_warehouse.setAdapter(arrayAdpAction);
    }

//
//    private void loadCellno() {
//        List<Integer> arr;
//        arr = new ArrayList<Integer>();
//        arr = objStocktakeControl.LoadCellno();
//        arrayAdp = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//        sp_rack_in_out_cellno.setAdapter(arrayAdp);
//    }




    private void loadrack() {
        et_rack_in_out_rack.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(rackvalid()){
                        isvalidrack = true;
                        sp_rack_in_out_warehouse.setEnabled(false);
                        sp_rack_in_out_warehouse.setClickable(false);
                        objstocktakeShared.saveWarehouse(sp_rack_in_out_warehouse.getSelectedItem().toString());
                        

                        return true;
                    }
                    else{
                        isvalidrack = false;
                    }
                } else {
                    return false;
                }
            return false;

            }
        });

    }


    private void isValidPallet() {
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
        et_rack_in_out_palletdown.setOnTouchListener(new View.OnTouchListener() {
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
                    if (!et_rack_in_out_pallettop.getText().toString().trim().equals("")){
                        if (isValidPallet(et_rack_in_out_pallettop.getText().toString(), "TOP")) {
                            if (!(sp_rack_in_out_warehouse.getSelectedItem().equals("BINRACK") || sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK") || sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN"))) {
                                String BoxCountPallet1 = objStocktakeControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().trim().toUpperCase());
                                box_count_pallet1.setText(BoxCountPallet1);

                                isvalidpallet = true;
                                return true;
                            }
                        } else {

                            isvalidpallet = false;
                            return false;
                            //    okMessage("ALERT", "Pallet is not valid - "+et_rack_in_out_pallettop.getText().toString());
                        }
                }else{
                        et_rack_in_out_pallettop.setText("");
                        et_rack_in_out_pallettop.setFocusable(true);
                        et_rack_in_out_pallettop.requestFocus();
                    okMessage("Alert","Pallets cannot be empty");
                }

                } else {

                    return false;
                }

                return false;

            }
        });
        et_rack_in_out_palletdown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (sp_rack_in_out_warehouse.getSelectedItem().equals("BINRACK")|| sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK") ||  sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN")) {
                        isvalidpallet = true;
                        return true;
                    }else {
                        if (!et_rack_in_out_palletdown.getText().toString().trim().equals("")) {

                            if (isValidPallet(et_rack_in_out_palletdown.getText().toString(), "DOWN")) {
                                if (!(sp_rack_in_out_warehouse.getSelectedItem().equals("BINRACK")|| sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK") ||  sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN"))) {
                                    String BoxCountPallet2 = objStocktakeControl.BoxPalletCount(et_rack_in_out_palletdown.getText().toString().toUpperCase());
                                    box_count_pallet2.setText(BoxCountPallet2);
                                    isvalidpallet = true;
                                    return true;
                                }
                            } else {

                                isvalidpallet = false;
                                return false;
                                //  okMessage("ALERT", "Pallet is not valid - "+et_rack_in_out_palletdown.getText().toString());
                            }
                        }else{

                            et_rack_in_out_palletdown.setText("");
                            et_rack_in_out_palletdown.setFocusable(true);
                            et_rack_in_out_palletdown.requestFocus();
                            okMessage("Alert","Pallets cannot be empty");
                        }
                    }
                } else {
                    return false;
                }
                return false;

            }
        });

    }


    private void saveButton(){
        if(sp_rack_in_out_warehouse.getSelectedItem().toString().equals("BINRACK") || sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK") ||  sp_rack_in_out_warehouse.getSelectedItem().equals("JAFZA_DRIVEIN")){
            if (isvalidrack && (!et_rack_in_out_pallettop.getText().toString().equals("") && isvalidpallet)) {
                if (objStocktakeControl.saveRackDetails(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), box_count_pallet1.getText().toString(), box_count_pallet2.getText().toString())) {
                    //okMessage("Success", "Pallet Saved Successfully");
                    Toast.makeText(getContext(), "Pallet Saved Successfully", Toast.LENGTH_LONG).show();
                    loadRackHistoryItem();
                    clear();

                } else {
                    okMessage("Alert", "Please Try again. Pallet Not Saved ");
                }
            }
        }

        else {
            if (isvalidrack && isvalidpallet && (!et_rack_in_out_pallettop.getText().toString().equals("") && (!et_rack_in_out_palletdown.getText().toString().equals("")))){
                if (objStocktakeControl.saveRackDetails(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), box_count_pallet1.getText().toString(), box_count_pallet2.getText().toString())) {

                    Toast.makeText(getContext(), "Pallet Saved Successfully", Toast.LENGTH_LONG).show();
                    loadRackHistoryItem();
                    clear();

                } else {
                    okMessage("Alert", "Please Try again. Pallet Not Saved ");
                }
            } else {
                okMessage("Alert", "Enter a valid rack/Pallet");
            }
        }
    }

    private Boolean rackvalid(){
        try {
            String cellno = "0";
            if(et_rack_in_out_cellno.getVisibility() == getView().VISIBLE){
                 cellno = et_rack_in_out_cellno.getText().toString();
            }else{
                cellno = "0";
            }
//            if(sp_rack_in_out_warehouse.getSelectedItem().toString().equals("JAFZA_DRIVEIN")){
//                if (objStocktakeControl.ValidRackJAFZA(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(), et_rack_in_out_cellno.getText().toString())) {
//                    if (objStocktakeControl.ClearRack(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString())) {
////
//                        et_rack_in_out_pallettop.setClickable(true);
//                        et_rack_in_out_palletdown.setClickable(true);
//                        et_rack_in_out_pallettop.setEnabled(true);
//                        et_rack_in_out_palletdown.setEnabled(true);
//                        et_rack_in_out_cellno.setText(StocktakeGlobal.getCellNo()+"");
//                        return true;
//
//                    } else {
//                        et_rack_in_out_pallettop.setClickable(false);
//                        et_rack_in_out_palletdown.setClickable(false);
//                        et_rack_in_out_pallettop.setEnabled(false);
//                        et_rack_in_out_palletdown.setEnabled(false);
//
//                        okMessage("Alert", "Rack is not Cleared." + et_rack_in_out_rack.getText().toString() + ". Please ask the supervisor to clear the rack.");
//                    }
//                }
//                else{
//                    et_rack_in_out_rack.setText("");
//                    okMessage("Alert", "Rack is not valid - " + et_rack_in_out_rack.getText().toString());
//                }
//            }else {

            if(objStocktakeControl.RackScanned(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(), getContext())){
                et_rack_in_out_pallettop.setText(StocktakeGlobal.getPalletNo1());
                et_rack_in_out_palletdown.setText(StocktakeGlobal.getPalletNo2());
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("This Rack is already saved. Do you want to scan this Rack again? -" +et_rack_in_out_rack.getText().toString())
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                objStocktakeControl.RemoveRack(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(),getContext());
                                et_rack_in_out_pallettop.setText("");
                                et_rack_in_out_palletdown.setText("");
                                et_rack_in_out_pallettop.setFocusable(true);
                                et_rack_in_out_pallettop.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                et_rack_in_out_rack.setText("");
                                et_rack_in_out_pallettop.setText("");
                                et_rack_in_out_palletdown.setText("");
                                et_rack_in_out_rack.requestFocus();
                            }
                        })
                        .show();
                return true;
            }else {
                if (objStocktakeControl.ValidRack(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString(), getContext())) {
                    if (objStocktakeControl.ClearRack(sp_rack_in_out_warehouse.getSelectedItem().toString(), et_rack_in_out_rack.getText().toString())) {
//

                        et_rack_in_out_pallettop.setClickable(true);
                        et_rack_in_out_palletdown.setClickable(true);
                        et_rack_in_out_pallettop.setEnabled(true);
                        et_rack_in_out_palletdown.setEnabled(true);
                        et_rack_in_out_cellno.setText(StocktakeGlobal.getCellNo()+"");
                       // isvalidrack = true;
                        return true;
                    } else {
                        et_rack_in_out_pallettop.setClickable(false);
                        et_rack_in_out_palletdown.setClickable(false);
                        et_rack_in_out_pallettop.setEnabled(false);
                        et_rack_in_out_palletdown.setEnabled(false);
                        et_rack_in_out_cellno.setText("");
                       // isvalidrack = false;
                        okMessage("Alert", "Rack is not Cleared." + et_rack_in_out_rack.getText().toString() + ". Please ask the supervisor to clear the rack.");

                    }
                } else {
                    //  okMessage("Alert", "Rack is not valid - " + et_rack_in_out_rack.getText().toString()+" Please clear.");

                    et_rack_in_out_rack.setText("");
//                    et_rack_in_out_rack.requestFocus();
//                    et_rack_in_out_rack.setFocusable(true);
                    isvalidrack = false;
                    // et_rack_in_out_rack.requestFocus();

                    //  et_rack_in_out_rack.setText("");
                }
            }
//            }

        }catch(Exception e){
            objGlobal.getErrorMessage();
            okMessage("Alert",e.toString());
        }

        return false;

    }
    private Boolean isValidPallet(String pallet, String position){
        try {



                if (!(sp_rack_in_out_warehouse.getSelectedItem().equals("BINRACK") || sp_rack_in_out_warehouse.getSelectedItem().equals("YOTO-BINRACK"))) {

                    if (objStocktakeControl.isValidpallet(pallet, position, getContext())) {
                        isvalidpallet = true;
                        return true;
                    } else {

                        isvalidpallet = false;
                        return false;
                    }
                } else {
                    if (objStocktakeControl.isValidTote(pallet, getContext())) {
                        isvalidpallet = true;
                        return true;
                    } else {
                        okMessage("Binrack:Validate Toteid", objGlobal.getErrorMessage());

                        isvalidpallet = false;
                        return false;
                    }
                }

        }catch (Exception e){
            okMessage("ALERT",objGlobal.getErrorMessage());
            return false;
        }
    }

    private void clear(){
        et_rack_in_out_rack.setText("");
        et_rack_in_out_palletdown.setText("");
        et_rack_in_out_pallettop.setText("");
        et_rack_in_out_cellno.setText("");
        StocktakeGlobal.setCellNo(0);
        sp_rack_in_out_warehouse.setEnabled(true);
        sp_rack_in_out_warehouse.setClickable(true);

        box_count_pallet1.setText("");
        box_count_pallet2.setText("");
        et_rack_in_out_rack.setFocusable(true);
        et_rack_in_out_rack.requestFocus();

    }



    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }


   // listRackHistoryItem = objStocktakeControl.loadRackhistory(tv_rack_in_out_warehouse.getText().toString());


    private void loadRackHistoryItem() {
        try {
            listRackHistoryItem.clear();
            listRackHistoryItem = new ArrayList<RackHistoryModel>();

                listRackHistoryItem = objStocktakeControl.loadRackhistory(objstocktakeShared.loadWarehouse());

            if (!listRackHistoryItem.isEmpty()) {
                if (listempty.getVisibility() == View.VISIBLE) {
                    listempty.setVisibility(View.GONE);
                }
                rackItemAdapter = new RackItemAdapter(getActivity(), listRackHistoryItem);
                lv_rack_in_out_details.setAdapter(rackItemAdapter);
            } else {
                if (listempty.getVisibility() == View.GONE) {
                    listempty.setVisibility(View.VISIBLE);
                    rackItemAdapter = new RackItemAdapter(getActivity(),null);
                }
            }
            //binding.tvRackInOutWarehouse.setText(objGlobal.getWarehouse());
          //  tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());
            //  tv_rack_in_out_warehouse.setText("JAFZA");


        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
        }
    }



}
