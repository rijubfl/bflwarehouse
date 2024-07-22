package com.bflgroup.warehouse.ui.jafzaracks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.jafzaracks.model.JafzaRackHistoryModel;
import com.bflgroup.warehouse.ui.rack.RackInOutGlobal;

import java.util.ArrayList;

public class JafzaRackInRackOutFragment extends Fragment {

    //    FragmentRackInRackOutBinding binding;
//    FragmentRackInRackOutJafzaBinding binding;
    ArrayAdapter<String> adapter;
    private Global objGlobal = Global.getInstance();

    JafzaRackInOutControl rackInOutControl;

    ArrayList<JafzaRackHistoryModel> listRackHistoryItem = new ArrayList<JafzaRackHistoryModel>();

    JafzaRackItemAdapter rackItemAdapter = null;
    String sp_rack;
    String warehouse = "";
    TextView tv_rack_in_out_warehouse;

    TextView box_count_pallet1;
    TextView box_count_pallet2;
    Spinner sp_rack_in_out_inout;
    EditText et_rack_in_out_rack;
    EditText et_rack_in_out_pallettop;
    EditText et_rack_in_out_palletdown;
    TextView listempty;
    ListView lv_rack_in_out_details;
    Button bt_rack_in_out_save;
    Button bt_rack_in_out_chear;
    Button bt_search;

    TextView et_rack_in_out_cellno;


    // warehouse = "JAFZA";
    public JafzaRackInRackOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // warehouse = objGlobal.getWarehouse();
         warehouse = "JAFZA";
        View view;
//       View view =  inflater.inflate(R.layout.fragment_rack_in_rack_out_jafza, container, false);


        if(warehouse.equals("JAFZA")) {
            view = inflater.inflate(R.layout.fragment_rack_in_rack_out_jafza, container, false);
            et_rack_in_out_cellno = (TextView) view.findViewById(R.id.et_rack_in_out_cellno);
        }
        else{
            view = inflater.inflate(R.layout.fragment_rack_in_rack_out, container, false);
            bt_search = (Button)  view.findViewById(R.id.bt_search);
        }

        tv_rack_in_out_warehouse = (TextView) view.findViewById(R.id.tv_rack_in_out_warehouse);
        sp_rack_in_out_inout = (Spinner) view.findViewById(R.id.sp_rack_in_out_inout);
        et_rack_in_out_rack = (EditText) view.findViewById(R.id.et_rack_in_out_rack);
        et_rack_in_out_pallettop = (EditText) view.findViewById(R.id.et_rack_in_out_pallettop);
        et_rack_in_out_palletdown = (EditText) view.findViewById(R.id.et_rack_in_out_palletdown);
        lv_rack_in_out_details = (ListView) view.findViewById(R.id.lv_rack_in_out_details);

        listempty = (TextView)  view.findViewById(R.id.listempty);
        bt_rack_in_out_save = (Button)  view.findViewById(R.id.bt_rack_in_out_save);
        bt_rack_in_out_chear = (Button)  view.findViewById(R.id.bt_rack_in_out_chear);
        box_count_pallet1 = (TextView)  view.findViewById(R.id.box_count_pallet1);
        box_count_pallet2 = (TextView)  view.findViewById(R.id.box_count_pallet2);


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if(warehouse == "JAFZA") {
//            et_rack_in_out_cellno.setVisibility(View.INVISIBLE);
//        }
//        else{
//            binding = FragmentRackInRackOutBinding.bind(view);
//        }
        //  binding = FragmentRackInRackOutBinding.bind(view);
//        setWarehouseSpinner();
        rackInOutControl = new JafzaRackInOutControl();
        onClick();
        setKeyListner();
        loadRackHistoryItem();




    }

    private void setKeyListner() {
        et_rack_in_out_pallettop.setOnTouchListener(new View.OnTouchListener() {
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

        et_rack_in_out_palletdown.setOnTouchListener(new View.OnTouchListener() {
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






        et_rack_in_out_palletdown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_rack_in_out_palletdown.setText(et_rack_in_out_palletdown.getText().toString().toUpperCase());
                    String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_palletdown.getText().toString().toUpperCase());
                    box_count_pallet2.setText(BoxCountPallet1);
                    return true;
                }
                return false;
            }
        });

        et_rack_in_out_pallettop.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_rack_in_out_pallettop.setText(et_rack_in_out_pallettop.getText().toString().toUpperCase());

                    String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                    box_count_pallet1.setText(BoxCountPallet1);


                    return true;
                }
                return false;
            }
        });







        if(!warehouse.equals("JAFZA")) {


            et_rack_in_out_rack.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        et_rack_in_out_rack.setText(et_rack_in_out_rack.getText().toString().toUpperCase());
                        sp_rack = sp_rack_in_out_inout.getSelectedItem().toString();

                        if (sp_rack.equals("OUT")) {

                            ArrayList<String> arrayList = rackInOutControl.getRackOutPallet(et_rack_in_out_rack.getText().toString().toUpperCase(), tv_rack_in_out_warehouse.getText().toString());
                            if(arrayList.equals(null) || arrayList.isEmpty()){

                                objGlobal.setErrorMessage("Pallets are empty");
                                return false;

                            }else {
                                et_rack_in_out_pallettop.setText(arrayList.get(0));
                                et_rack_in_out_palletdown.setText(arrayList.get(1));
                                Log.e("Pallet Up", et_rack_in_out_pallettop.getText() + "");
                                Log.e("Pallet Down", et_rack_in_out_palletdown.getText() + "");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
            et_rack_in_out_rack.setOnTouchListener(new View.OnTouchListener() {
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
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("search", "Here");
                    et_rack_in_out_rack.setText(et_rack_in_out_rack.getText().toString().toUpperCase());
                    if (et_rack_in_out_rack.getText().toString().equals("") || et_rack_in_out_rack.getText().toString() == "") {
                        Log.e("search", "inside if");

                        okMessage("Error", "Please Scan Rack Number!");

                    } else {
                        sp_rack = sp_rack_in_out_inout.getSelectedItem().toString();

                        if (sp_rack.equals("OUT")) {

                            ArrayList<String> arrayList = rackInOutControl.getRackOutPallet(et_rack_in_out_rack.getText().toString().toUpperCase(), tv_rack_in_out_warehouse.getText().toString());
                            if (arrayList.equals(null) || arrayList.isEmpty()) {
                                okMessage("Error", "Pallets are empty");


                            } else {
                                et_rack_in_out_pallettop.setText(arrayList.get(0));
                                et_rack_in_out_palletdown.setText(arrayList.get(1));
                                Log.e("Pallet Up", et_rack_in_out_pallettop.getText() + "");
                                Log.e("Pallet Down", et_rack_in_out_palletdown.getText() + "");
                            }
                        }

                    }
                }
            });
        }else{

            et_rack_in_out_rack.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        et_rack_in_out_rack.setText(et_rack_in_out_rack.getText().toString().toUpperCase());
                        sp_rack = sp_rack_in_out_inout.getSelectedItem().toString();
                        String rackNum = et_rack_in_out_rack.getText().toString();
                        String rackNumber[] = null;

                        if (sp_rack.equals("IN")) {
                            rackNumber = rackNum.split("-");
                           // rackNumber[1] = rackNumber[1];
                           // String ColName = "Cell" + rackNumber[1];
                            if(!rackNumber[1].matches(".*\\d.*")){
                          //  if(et_rack_in_out_rack.getText().toString().contains("FLR") || et_rack_in_out_rack.getText().toString().contains("FE")){
                                et_rack_in_out_cellno.setText(rackInOutControl.getAutoFLoor(et_rack_in_out_rack.getText().toString()) + "");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
            et_rack_in_out_rack.setOnTouchListener(new View.OnTouchListener() {
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
            et_rack_in_out_pallettop.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        et_rack_in_out_rack.setText( et_rack_in_out_rack.getText().toString().toUpperCase());
                        sp_rack = sp_rack_in_out_inout.getSelectedItem().toString();

                        if(!et_rack_in_out_pallettop.getText().toString().equals("") ||  !et_rack_in_out_palletdown.getText().toString().equals("") ) {
                            if (sp_rack.equals("OUT")) {
                                String rackno = rackInOutControl.getPalletDetails( et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(),tv_rack_in_out_warehouse.getText().toString());
                                if(rackno.equals("") || rackno.isEmpty()){
                                    objGlobal.setErrorMessage("Pallets not found in any rack");
                                    return false;

                                }else {
                                    et_rack_in_out_rack.setText(rackno);

                                }
                                String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                                box_count_pallet1.setText(BoxCountPallet1);
                                return true;
                            }

                        }
                        String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_pallettop.getText().toString().toUpperCase());
                        box_count_pallet1.setText(BoxCountPallet1);


                    }
                    return false;
                }
            });


            et_rack_in_out_palletdown.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        et_rack_in_out_rack.setText( et_rack_in_out_rack.getText().toString().toUpperCase());
                        sp_rack = sp_rack_in_out_inout.getSelectedItem().toString();

                        if(!et_rack_in_out_pallettop.getText().toString().equals("") ||  !et_rack_in_out_palletdown.getText().toString().equals("") ) {
                            if (sp_rack.equals("OUT")) {
                                String rackno = rackInOutControl.getPalletDetails( et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(),tv_rack_in_out_warehouse.getText().toString());
                                if(rackno.equals("") || rackno.isEmpty()){
                                    objGlobal.setErrorMessage("Pallets not found in any rack");
                                    return false;

                                }else {
                                    et_rack_in_out_rack.setText(rackno);


                                }
                                String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_palletdown.getText().toString().toUpperCase());
                                box_count_pallet2.setText(BoxCountPallet1);
                                return true;
                            }

                        }
                        String BoxCountPallet1 = rackInOutControl.BoxPalletCount(et_rack_in_out_palletdown.getText().toString().toUpperCase());
                        box_count_pallet2.setText(BoxCountPallet1);



                    }
                    return false;
                }
            });
        }
    }




    private void loadRackHistoryItem() {
        try {
            listRackHistoryItem.clear();
            listRackHistoryItem = new ArrayList<JafzaRackHistoryModel>();
            if(tv_rack_in_out_warehouse.getText().toString().equals("TECHNO")) {
                listRackHistoryItem = rackInOutControl.loadRackhistoryTechno();
            }
            else if(tv_rack_in_out_warehouse.getText().toString().equals("JAFZA")) {
                listRackHistoryItem = rackInOutControl.loadRack1history();
            }
            else {
                listRackHistoryItem = rackInOutControl.loadRackhistory(tv_rack_in_out_warehouse.getText().toString());
            }
            if (!listRackHistoryItem.isEmpty()) {
                if (listempty.getVisibility() == View.VISIBLE) {
                    listempty.setVisibility(View.GONE);
                }
                rackItemAdapter = new JafzaRackItemAdapter(getActivity(), listRackHistoryItem);
                lv_rack_in_out_details.setAdapter(rackItemAdapter);
            } else {
                if (listempty.getVisibility() == View.GONE) {
                    listempty.setVisibility(View.VISIBLE);
                    rackItemAdapter = new JafzaRackItemAdapter(getActivity(),null);
                }
            }
            //binding.tvRackInOutWarehouse.setText(objGlobal.getWarehouse());
          //  tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());
            tv_rack_in_out_warehouse.setText("JAFZA");


        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
        }
    }

    private void onClick() {

        et_rack_in_out_pallettop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bt_rack_in_out_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validFields()) {  //validating fileds empty or not
                 //   rackInOutControl = new JafzaRackInOutControl();
                    boolean isValidRack=false;
                    try {
                        if (tv_rack_in_out_warehouse.getText().toString().equals("TECHNO")) {
                            isValidRack = rackInOutControl.isValidRackTechno(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString());
                        } else if (tv_rack_in_out_warehouse.getText().toString().equals("JAFZA")) {
                            isValidRack = rackInOutControl.isValidRackJafza(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString());
                        } else {
                            isValidRack = rackInOutControl.isValidRack(et_rack_in_out_rack.getText().toString(), tv_rack_in_out_warehouse.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString());
                        }
                        if (isValidRack) {
                            if (et_rack_in_out_pallettop.getText().toString().equalsIgnoreCase(et_rack_in_out_palletdown.getText().toString())) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setMessage("Pallet Nos. are same, proceed ?")
                                        .setTitle("Confirmation")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (tv_rack_in_out_warehouse.getText().toString().equals("TECHNO")) {
                                                    if (rackInOutControl.saveRackDetailsTechno(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                } else if (tv_rack_in_out_warehouse.getText().toString().equals("JAFZA")) {
                                                    if (rackInOutControl.saveRackDetailsJafza(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                } else {
                                                    if (rackInOutControl.saveRackDetails(et_rack_in_out_rack.getText().toString(), tv_rack_in_out_warehouse.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setMessage("Are you sure to save ?")
                                        .setTitle("Confirmation")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (tv_rack_in_out_warehouse.getText().toString().equals("TECHNO")) {
                                                    if (rackInOutControl.saveRackDetailsTechno(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                } else if (tv_rack_in_out_warehouse.getText().toString().equals("JAFZA")) {
                                                    if (rackInOutControl.saveRackDetailsJafza(et_rack_in_out_rack.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                } else {
                                                    if (rackInOutControl.saveRackDetails(et_rack_in_out_rack.getText().toString(), tv_rack_in_out_warehouse.getText().toString(), et_rack_in_out_pallettop.getText().toString(), et_rack_in_out_palletdown.getText().toString(), sp_rack_in_out_inout.getSelectedItem().toString())) {
                                                        loadRackHistoryItem();
                                                        rackItemAdapter.notifyDataSetChanged();
                                                        clearAll();
                                                    }
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        } else {
                            okMessage("Error", objGlobal.getErrorMessage());
                        }
                    }catch (Exception e){
                        okMessage("Error",e.toString());
                    }
                }
            }
        });

        bt_rack_in_out_chear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAll();
            }
        });
    }

    void clearAll(){
        // binding.tvRackInOutWarehouse.setText(objGlobal.getWarehouse());
//       tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());
        tv_rack_in_out_warehouse.setText("JAFZA");
        et_rack_in_out_palletdown.setText("");
        et_rack_in_out_pallettop.setText("");
        et_rack_in_out_rack.setText("");
        RackInOutGlobal.setCellNo(0);
        et_rack_in_out_cellno.setText("");
        et_rack_in_out_pallettop.requestFocus();
        box_count_pallet1.setText("");
        box_count_pallet2.setText("");
    }

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private boolean validFields() {
        if (et_rack_in_out_pallettop.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please Scan Pallet Top", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_rack_in_out_palletdown.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please Scan Pallet Down", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (binding.spRackInOutInout.getTag() == null && binding.spRackInOutInout.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select IN/OUT", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (et_rack_in_out_rack.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please scan Rack", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}