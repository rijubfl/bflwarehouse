package com.bflgroup.warehouse.ui.rack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.databinding.FragmentRackInRackOutBinding;
import com.bflgroup.warehouse.ui.rack.model.RackHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class RackInRackOutFragment extends Fragment {

    FragmentRackInRackOutBinding binding;
    ArrayAdapter<String> adapter;
    private Global objGlobal = Global.getInstance();

    RackInOutControl rackInOutControl;

    ArrayList<RackHistoryModel> listRackHistoryItem = new ArrayList<RackHistoryModel>();
    ArrayList listPalletItems = new ArrayList<>();
    RackItemAdapter rackItemAdapter;

    public RackInRackOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rack_in_rack_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRackInRackOutBinding.bind(view);
        rackInOutControl = new RackInOutControl();
        List<String> arr;
        arr = new ArrayList<String>();
        String warehouse = objGlobal.getWarehouse();
        //arr.add("YOTO-BU");
        arr.add(warehouse);
        if(objGlobal.getWarehouse().equals("TECHNO")){
            arr.add("TECHNO-E");
        }
        if(objGlobal.getWarehouse().equals("YOTO")){
            arr.add("YOTO-SF");
            arr.add("YOTO-BU");
        }
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        binding.spRackInOutWarehouse.setAdapter(arrayAdp);
        onClick();
        setKeyListner();
        loadRackHistoryItem();
        binding.etRackInOutRack.requestFocus();
    }

    private void setKeyListner() {
        binding.etRackInOutPallettop.setOnTouchListener(new View.OnTouchListener() {
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

        binding.etRackInOutPalletdown.setOnTouchListener(new View.OnTouchListener() {
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

        binding.etRackInOutRack.setOnTouchListener(new View.OnTouchListener() {
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

        binding.spRackInOutWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                listRackHistoryItem.clear();
                if(binding.spRackInOutWarehouse.getSelectedItem().toString().equals("TECHNO")) {
                    listRackHistoryItem = rackInOutControl.loadRackhistoryTechno();
                } else {
                    listRackHistoryItem = rackInOutControl.loadRackhistory(binding.spRackInOutWarehouse.getSelectedItem().toString());
                }
                if (!listRackHistoryItem.isEmpty()) {
                    if (binding.listempty.getVisibility() == View.VISIBLE) {
                        binding.listempty.setVisibility(View.GONE);
                    }
                    rackItemAdapter = new RackItemAdapter(getActivity(), listRackHistoryItem);
                    binding.lvRackInOutDetails.setAdapter(rackItemAdapter);
                } else {
                    if (binding.listempty.getVisibility() == View.GONE) {
                        binding.listempty.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        binding.etRackInOutPalletdown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    binding.etRackInOutPalletdown.setText(binding.etRackInOutPalletdown.getText().toString().toUpperCase());
                    return true;
                }
                return false;
            }
        });

        binding.etRackInOutPallettop.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    binding.etRackInOutPallettop.setText(binding.etRackInOutPallettop.getText().toString().toUpperCase());
                    return true;
                }
                return false;
            }
        });

        binding.etRackInOutRack.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (binding.etRackInOutRack.getText().toString().toUpperCase().isEmpty()){
                        okMessage("Alert", "Can not Proceed, Please scan location");
                    } else {
                        binding.etRackInOutRack.setText(binding.etRackInOutRack.getText().toString().toUpperCase());
                        if (binding.spRackInOutInout.getSelectedItem().toString().equals("OUT")) {
                            listPalletItems = rackInOutControl.getRackOutPallet(binding.etRackInOutRack.getText().toString(), binding.spRackInOutWarehouse.getSelectedItem().toString());
                            if (listPalletItems.isEmpty()) {
                                okMessage("Alert", "Can not Proceed, Selected Rack is empty");
                            } else {
                                binding.etRackInOutPallettop.setText(listPalletItems.get(0).toString());
                                binding.etRackInOutPalletdown.setText(listPalletItems.get(1).toString());
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void loadRackHistoryItem() {
        try {
            listRackHistoryItem.clear();
            if(binding.spRackInOutWarehouse.getSelectedItem().toString().equals("TECHNO")) {
                listRackHistoryItem = rackInOutControl.loadRackhistoryTechno();
            } else {
                listRackHistoryItem = rackInOutControl.loadRackhistory(binding.spRackInOutWarehouse.getSelectedItem().toString());
            }
            if (!listRackHistoryItem.isEmpty()) {
                if (binding.listempty.getVisibility() == View.VISIBLE) {
                    binding.listempty.setVisibility(View.GONE);
                }
                rackItemAdapter = new RackItemAdapter(getActivity(), listRackHistoryItem);
                binding.lvRackInOutDetails.setAdapter(rackItemAdapter);
            } else {
                if (binding.listempty.getVisibility() == View.GONE) {
                    binding.listempty.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
        }
    }

    private void onClick() {
        binding.etRackInOutPallettop.addTextChangedListener(new TextWatcher() {
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
        binding.btRackInOutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validFields()) {  //validating fileds empty or not
                    rackInOutControl = new RackInOutControl();
                    boolean isValidRack=false;
                    if(binding.spRackInOutWarehouse.getSelectedItem().toString().equals("TECHNO")) {
                        isValidRack = rackInOutControl.isValidRackTechno(binding.etRackInOutRack.getText().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString());
                    } else {
                        isValidRack = rackInOutControl.isValidRack(binding.etRackInOutRack.getText().toString(),binding.spRackInOutWarehouse.getSelectedItem().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString());
                    }
                    if (isValidRack) {
                        if (binding.etRackInOutPallettop.getText().toString().equalsIgnoreCase(binding.etRackInOutPalletdown.getText().toString())) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Pallet Nos. are same, proceed ?")
                                    .setTitle("Conformation")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(binding.spRackInOutWarehouse.getSelectedItem().toString().equals("TECHNO")) {
                                                if (rackInOutControl.saveRackDetailsTechno(binding.etRackInOutRack.getText().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString())) {
                                                    loadRackHistoryItem();
                                                    rackItemAdapter.notifyDataSetChanged();
                                                    clearAll();
                                                } else {
                                                    okMessage("Rack", objGlobal.getErrorMessage());
                                                }
                                            } else {
                                                if (rackInOutControl.saveRackDetails(binding.etRackInOutRack.getText().toString(),binding.spRackInOutWarehouse.getSelectedItem().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString())) {
                                                    loadRackHistoryItem();
                                                    rackItemAdapter.notifyDataSetChanged();
                                                    clearAll();
                                                } else {
                                                    okMessage("Rack", objGlobal.getErrorMessage());
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
                                    .setTitle("Conformation")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(binding.spRackInOutWarehouse.getSelectedItem().toString().equals("TECHNO")) {
                                                if (rackInOutControl.saveRackDetailsTechno(binding.etRackInOutRack.getText().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString())) {
                                                    loadRackHistoryItem();
                                                    rackItemAdapter.notifyDataSetChanged();
                                                    clearAll();
                                                }
                                            } else {
                                                if (rackInOutControl.saveRackDetails(binding.etRackInOutRack.getText().toString(),binding.spRackInOutWarehouse.getSelectedItem().toString(), binding.etRackInOutPallettop.getText().toString(), binding.etRackInOutPalletdown.getText().toString(), binding.spRackInOutInout.getSelectedItem().toString())) {
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
                }
            }
        });
        binding.btRackInOutChear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clearAll();
            }
        });
    }

    void clearAll(){
        binding.etRackInOutPalletdown.setText("");
        binding.etRackInOutPallettop.setText("");
        binding.etRackInOutRack.setText("");
        binding.etRackInOutRack.requestFocus();
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
        if (binding.etRackInOutPallettop.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please Scan Pallet Top", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etRackInOutPalletdown.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please Scan Pallet Down", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (binding.spRackInOutInout.getTag() == null && binding.spRackInOutInout.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select IN/OUT", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (binding.etRackInOutRack.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please scan Rack", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}