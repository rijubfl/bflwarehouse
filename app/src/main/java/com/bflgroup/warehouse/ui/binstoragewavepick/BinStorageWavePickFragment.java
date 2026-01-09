//package com.bflgroup.warehouse.ui.binstoragewavepick;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.bflgroup.warehouse.R;
//import com.bflgroup.warehouse.comm.Global;
//import com.bflgroup.warehouse.ui.binstorageputaway.BinPutAwayControl;
//import com.bflgroup.warehouse.ui.binstorageputawaymultipletote.BinPutAwayMultipleToteControl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BinStorageWavePickFragment extends Fragment {
//
//    Global objGlobal = Global.getInstance();
//    BinStorageWavePickControl objBinStorageWavePickControl = new BinStorageWavePickControl();
//    BinPutAwayControl objBinPutAwayControl= new BinPutAwayControl();
//    BinPutAwayMultipleToteControl objBinPutAwayMultipleToteControl= new BinPutAwayMultipleToteControl();
//    BinStorageWavePickFragment.MyBinStorageWavePickFragmentAdp objMyBinStorageWavePickFragmentAdp;
//
//    private Spinner sp_bin_storage_wave_pick_rack;
//    private Spinner sp_bin_storage_wave_pick_type;
//    private Spinner sp_bin_storage_wave_pick_tote_type;
//    private Spinner sp_bin_storage_wave_pick_div;
//    private Button bt_bin_storage_wave_pick_load;
//    private ListView lv_bin_storage_wave_pick_details;
//
//    private TextView et_bin_storage_wave_pick_out_toteid;
//    private TextView et_bin_storage_wave_pick_out_location;
//    private Button bt_bin_storage_wave_pick_out_proceed;
//    private Button bt_bin_storage_wave_pick_out_close;
//
//    private boolean b_Result;
//    public BinStorageWavePickFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_bin_storage_wave_pick, container, false);
//
//        sp_bin_storage_wave_pick_rack = (Spinner) view.findViewById(R.id.sp_bin_storage_wave_pick_rack);
//        sp_bin_storage_wave_pick_type = (Spinner) view.findViewById(R.id.sp_bin_storage_wave_pick_type);
//        sp_bin_storage_wave_pick_tote_type = (Spinner) view.findViewById(R.id.sp_bin_storage_wave_pick_tote_type);
//        sp_bin_storage_wave_pick_div = (Spinner) view.findViewById(R.id.sp_bin_storage_wave_pick_div);
//        bt_bin_storage_wave_pick_load = (Button) view.findViewById(R.id.bt_bin_storage_wave_pick_load);
//        lv_bin_storage_wave_pick_details = (ListView) view.findViewById(R.id.lv_bin_storage_wave_pick_details);
//
//        try {
//            List<String> arr = objBinStorageWavePickControl.loadBinStorageWavePickRack("");
//            ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//            sp_bin_storage_wave_pick_rack.setAdapter(arrayAdp);
//
//            List<String> arr1 = objBinStorageWavePickControl.loadPickType();
//            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
//            sp_bin_storage_wave_pick_type.setAdapter(arrayAdp1);
//
//            List<String> arr2 = objBinStorageWavePickControl.loadPickDivision();
//            ArrayAdapter<String> arrayAdp2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr2);
//            sp_bin_storage_wave_pick_div.setAdapter(arrayAdp2);
//
//            List<String> arrTypeType;
//            arrTypeType = new ArrayList<String>();
//            arrTypeType.add("ALL");
//            if (objGlobal.getWorkLocation().equals("UAE")) {
//                arrTypeType.add("B");
//                arrTypeType.add("E,J");
//            }
//            ArrayAdapter<String> arrayAdpToteType = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arrTypeType);
//            sp_bin_storage_wave_pick_tote_type.setAdapter(arrayAdpToteType);
//
//            String rack = sp_bin_storage_wave_pick_rack.getSelectedItem().toString().trim();
//            ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket = objBinStorageWavePickControl.loadBinStorageWaveDetails(rack, "", "");
//            objMyBinStorageWavePickFragmentAdp = new BinStorageWavePickFragment.MyBinStorageWavePickFragmentAdp(listBinStorageWavePickTicket);
//            lv_bin_storage_wave_pick_details.setAdapter(objMyBinStorageWavePickFragmentAdp);
//        } catch (Exception e){
//            okMessage("BinStorageWavePick", e.getMessage());
//        }
//        bt_bin_storage_wave_pick_load.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String rack = sp_bin_storage_wave_pick_rack.getSelectedItem().toString().trim();
//                String pickType = sp_bin_storage_wave_pick_type.getSelectedItem().toString().trim();
//                String div = sp_bin_storage_wave_pick_div.getSelectedItem().toString().trim();
//                if (TextUtils.isEmpty(rack)) {
//                    okMessage("BinStorageWavePick", "Please select rack");
//                    sp_bin_storage_wave_pick_rack.requestFocus();
//                    vibrate(250);
//                } else {
//                    ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket = objBinStorageWavePickControl.loadBinStorageWaveDetails(rack, pickType, div);
//                    objMyBinStorageWavePickFragmentAdp = new BinStorageWavePickFragment.MyBinStorageWavePickFragmentAdp(listBinStorageWavePickTicket);
//                    lv_bin_storage_wave_pick_details.setAdapter(objMyBinStorageWavePickFragmentAdp);
//                }
//            }
//        });
//
//        sp_bin_storage_wave_pick_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                sp_bin_storage_wave_pick_div.setEnabled(true);
//                sp_bin_storage_wave_pick_tote_type.setEnabled(true);
//                if (sp_bin_storage_wave_pick_type.getSelectedItem().toString().equals("SKIPPED BOXES")) {
//                    List<String> arr = objBinStorageWavePickControl.loadBinStorageWavePickRack("SKIPPED BOXES");
//                    ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//                    sp_bin_storage_wave_pick_rack.setAdapter(arrayAdp);
//                }
//                if (sp_bin_storage_wave_pick_type.getSelectedItem().toString().equals("OVERRIDE BOXES")){
//                    List<String> arr = objBinStorageWavePickControl.loadBinStorageWavePickRack("OVERRIDE BOXES");
//                    ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//                    sp_bin_storage_wave_pick_rack.setAdapter(arrayAdp);
//                }
//                else{
//                    List<String> arr = objBinStorageWavePickControl.loadBinStorageWavePickRack("");
//                    ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//                    sp_bin_storage_wave_pick_rack.setAdapter(arrayAdp);
//                }
//                if(sp_bin_storage_wave_pick_type.getSelectedItem().toString().equals("ALL WINTER")){
//                    sp_bin_storage_wave_pick_div.setSelection(0);
//                    sp_bin_storage_wave_pick_tote_type.setSelection(0);
//                    sp_bin_storage_wave_pick_div.setEnabled(false);
//                    sp_bin_storage_wave_pick_tote_type.setEnabled(false);
//                }
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//        });
//
//        return view;
//    }
//
//    private class MyBinStorageWavePickFragmentAdp extends BaseAdapter {
//        public ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket;
//
//        public MyBinStorageWavePickFragmentAdp(ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket) {
//            this.listBinStorageWavePickTicket = listBinStorageWavePickTicket;
//        }
//
//        @Override
//        public int getCount() {
//            return listBinStorageWavePickTicket.size();
//        }
//
//        @Override
//        public String getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater mInflater = getLayoutInflater();
//            View myView = mInflater.inflate(R.layout.bin_storage_wave_pick_ticket, null);
//            final BinStorageWavePickTicket s = listBinStorageWavePickTicket.get(position);
//
//            TextView tv_bin_storage_wave_pick_ticket_toteid = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_toteid);
//            tv_bin_storage_wave_pick_ticket_toteid.setText("Tote.ID : " + String.valueOf(s.toteId));
//
//            TextView tv_bin_storage_wave_pick_ticket_boxno = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_boxno);
//            tv_bin_storage_wave_pick_ticket_boxno.setText("Box.No : " + String.valueOf(s.boxNo));
//
//            TextView tv_bin_storage_wave_pick_ticket_location = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_location);
//            tv_bin_storage_wave_pick_ticket_location.setText("Location : " + String.valueOf(s.text));
//
//            TextView tv_bin_storage_wave_pick_ticket_color = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_color);
//            tv_bin_storage_wave_pick_ticket_color.setText("Color : " + String.valueOf(s.color));
//            if(s.color.equals("Green")) {
//                tv_bin_storage_wave_pick_ticket_color.setBackgroundColor(Color.GREEN);
//            }
//            if(s.color.equals("Yellow")) {
//                tv_bin_storage_wave_pick_ticket_color.setBackgroundColor(Color.YELLOW);
//            }
//
//            TextView tv_bin_storage_wave_pick_ticket_order = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_order);
//            tv_bin_storage_wave_pick_ticket_order.setText("Pick Order : " + String.valueOf(s.pickOrder));
//
//            TextView tv_bin_storage_wave_pick_ticket_zone = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_zone);
//            tv_bin_storage_wave_pick_ticket_zone.setText("Zone : " + String.valueOf(s.zones));
//
//            TextView tv_bin_storage_wave_pick_ticket_checkingtype = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_checkingtype);
//            tv_bin_storage_wave_pick_ticket_checkingtype.setText("Checking Type : " + String.valueOf(s.checkingType));
//
//            TextView tv_bin_storage_wave_pick_ticket_slno = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_slno);
//            tv_bin_storage_wave_pick_ticket_slno.setText(String.valueOf(s.rowNo));
//
//            TextView tv_bin_storage_wave_pick_ticket_perc = (TextView) myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_perc);
//            tv_bin_storage_wave_pick_ticket_perc.setText(String.valueOf("Box % : " + s.boxPerc));
//
//            Button bt_bin_storage_wave_pick_ticket_out = (Button) myView.findViewById(R.id.bt_bin_storage_wave_pick_ticket_out);
//            Button bt_bin_storage_wave_pick_ticket_skip = (Button) myView.findViewById(R.id.bt_bin_storage_wave_pick_ticket_skip);
//            if(!s.rowNo.equals("1")){
//                bt_bin_storage_wave_pick_ticket_out.setEnabled(false);
//                bt_bin_storage_wave_pick_ticket_skip.setEnabled(false);
//            }
//            bt_bin_storage_wave_pick_ticket_out.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    openPopupWindow(s.toteId, s.boxNo, "OUT", s.location, s.dDeep);
//                }
//            });
//
//            bt_bin_storage_wave_pick_ticket_skip.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                    alert.setMessage("Are you sure to skip this location?")
//                            .setTitle("Conformation")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    b_Result = objBinStorageWavePickControl.skipWavePick(s.location);
//                                    if (b_Result) {
//                                        if (objMyBinStorageWavePickFragmentAdp.getCount() != 1) {
//                                            clearAll(sp_bin_storage_wave_pick_rack.getSelectedItem().toString());
//                                        }
//                                        else
//                                            clearAll("");
//                                    } else {
//                                        okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
//                                        vibrate(250);
//                                    }
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//                }
//            });
//
//            return myView;
//        }
//    }
//
//    void openPopupWindow(String toteId, String boxNo, String direction, String location, String dBeep) {
//        Dialog myDialog;
//        myDialog = new Dialog(getContext());
//        myDialog.setCancelable(false);
//        myDialog.setContentView(R.layout.bin_storage_wave_pick_out_ticket);
//        et_bin_storage_wave_pick_out_toteid = (TextView) myDialog.findViewById(R.id.et_bin_storage_wave_pick_out_toteid);
//        et_bin_storage_wave_pick_out_location = (EditText) myDialog.findViewById(R.id.et_bin_storage_wave_pick_out_location);
//        bt_bin_storage_wave_pick_out_proceed = (Button) myDialog.findViewById(R.id.bt_bin_storage_wave_pick_out_proceed);
//        bt_bin_storage_wave_pick_out_close = (Button) myDialog.findViewById(R.id.bt_bin_storage_wave_pick_out_close);
//
//        /*et_bin_storage_wave_pick_out_toteid.setText(toteId);
//        et_bin_storage_wave_pick_out_location.setText(location);
//
//        et_bin_storage_wave_pick_out_toteid.setEnabled(false);
//        et_bin_storage_wave_pick_out_location.setEnabled(false);*/
//
//        et_bin_storage_wave_pick_out_toteid.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.onTouchEvent(motionEvent);
//                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                return objGlobal.getHideKeyPad();
//            }
//        });
//
//        et_bin_storage_wave_pick_out_location.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.onTouchEvent(motionEvent);
//                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                return objGlobal.getHideKeyPad();
//            }
//        });
//
//        et_bin_storage_wave_pick_out_toteid.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
//                    if (TextUtils.isEmpty(et_bin_storage_wave_pick_out_toteid.getText().toString().trim())) {
//                        okMessage("Popup", "Cant Blank toteid");
//                        vibrate(250);
//                        et_bin_storage_wave_pick_out_toteid.requestFocus();
//                    } else {
//                        if (toteId.equals(et_bin_storage_wave_pick_out_toteid.getText().toString().trim())) {
//                            et_bin_storage_wave_pick_out_location.requestFocus();
//                            return true;
//                        } else {
//                            if (boxNo.equals(et_bin_storage_wave_pick_out_toteid.getText().toString().trim())) {
//                                et_bin_storage_wave_pick_out_location.requestFocus();
//                                return true;
//                            } else {
//                                okMessage("BinPutAwayFragment", "Selected tote and scanned totes is not matching");
//                                et_bin_storage_wave_pick_out_toteid.setText("");
//                                vibrate(250);
//                                et_bin_storage_wave_pick_out_toteid.requestFocus();
//                            }
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//
//        et_bin_storage_wave_pick_out_location.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
//                    if (TextUtils.isEmpty(et_bin_storage_wave_pick_out_location.getText().toString().trim())) {
//                        okMessage("Popup", "Cant Blank location");
//                        vibrate(250);
//                        et_bin_storage_wave_pick_out_location.requestFocus();
//                    } else if (!location.equals(et_bin_storage_wave_pick_out_location.getText().toString().trim())) {
//                        okMessage("BinPutAwayFragment", "Selected location and scanned location is not matching");
//                        et_bin_storage_wave_pick_out_location.setText("");
//                        et_bin_storage_wave_pick_out_location.requestFocus();
//                        vibrate(250);
//                    } else {
//                        bt_bin_storage_wave_pick_out_proceed.requestFocus();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        bt_bin_storage_wave_pick_out_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDialog.dismiss();
//            }
//        });
//
//        bt_bin_storage_wave_pick_out_proceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String scanTote = et_bin_storage_wave_pick_out_toteid.getText().toString().trim();
//                String scanLocation = et_bin_storage_wave_pick_out_location.getText().toString().trim();
//                String location1 = location.replace("-","");
//                scanLocation = scanLocation.replace("-","");
//                if (TextUtils.isEmpty(scanTote)) {
//                    okMessage("BinPutAwayFragment", "Please scan tote");
//                    vibrate(250);
//                    et_bin_storage_wave_pick_out_toteid.requestFocus();
//                } else if (TextUtils.isEmpty(scanLocation)) {
//                    okMessage("BinPutAwayFragment", "Please scan location");
//                    vibrate(250);
//                    et_bin_storage_wave_pick_out_location.requestFocus();
//                } else if (!scanTote.equals(toteId) && !scanTote.equals(boxNo)) {
//                    okMessage("BinPutAwayFragment", "Selected tote and scanned totes are not matching");
//                    vibrate(250);
//                    et_bin_storage_wave_pick_out_toteid.requestFocus();
//                } else if (!scanLocation.equals(location1)) {
//                    okMessage("BinPutAwayFragment", "Selected location and scanned totes are not matching");
//                    vibrate(250);
//                    et_bin_storage_wave_pick_out_location.requestFocus();
//                } else {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                    alert.setMessage("Are you sure to save?")
//                            .setTitle("Conformation")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //b_Result = objBinPutAwayMultipleToteControl.saveBinInOutMultiple(objGlobal.getWarehouse(), direction, location);
//                                    b_Result = objBinPutAwayControl.saveBinInOutSingle(toteId, boxNo, direction, location, dBeep);
//                                    if (b_Result) {
//                                        if (objMyBinStorageWavePickFragmentAdp.getCount() != 1) {
//                                            clearAll(sp_bin_storage_wave_pick_rack.getSelectedItem().toString());
//                                        }
//                                        else
//                                            clearAll("");
//                                        myDialog.dismiss();
//                                    } else {
//                                        okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
//                                        vibrate(250);
//                                    }
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//                }
//            }
//        });
//        myDialog.show();
//        et_bin_storage_wave_pick_out_toteid.requestFocus();
//    }
//
//    boolean clearAll(String zone) {
//        String rack = sp_bin_storage_wave_pick_rack.getSelectedItem().toString().trim();
//        String pickType = sp_bin_storage_wave_pick_type.getSelectedItem().toString().trim();
//        String div = sp_bin_storage_wave_pick_div.getSelectedItem().toString().trim();
//        ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket = objBinStorageWavePickControl.loadBinStorageWaveDetails(rack, pickType, div);
//        objMyBinStorageWavePickFragmentAdp = new BinStorageWavePickFragment.MyBinStorageWavePickFragmentAdp(listBinStorageWavePickTicket);
//        lv_bin_storage_wave_pick_details.setAdapter(objMyBinStorageWavePickFragmentAdp);
//            List<String> arr = objBinStorageWavePickControl.loadBinStorageWavePickRack("");
//            ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
//            sp_bin_storage_wave_pick_rack.setAdapter(arrayAdp);
//        if (!zone.equals("")){
//            int pos = arrayAdp.getPosition(zone);
//            sp_bin_storage_wave_pick_rack.setSelection(pos);
//            bt_bin_storage_wave_pick_load.performClick();
//        }
//        return true;
//    }
//
//    void vibrate(int duration) {
//        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
//        assert v != null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(duration,
//                    VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            v.vibrate(duration);
//        }
//    }
//
//    void okMessage(String title, String message) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//        alert.setMessage(message);
//        alert.setTitle(title);
//        alert.setPositiveButton("OK", null);
//        alert.setCancelable(true);
//        alert.create().show();
//    }
//}

package com.bflgroup.warehouse.ui.binstoragewavepick;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.binstorageputaway.BinPutAwayControl;
import com.bflgroup.warehouse.ui.binstorageputawaymultipletote.BinPutAwayMultipleToteControl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BinStorageWavePickFragment extends Fragment {

    Global objGlobal = Global.getInstance();
    BinStorageWavePickControl objBinStorageWavePickControl = new BinStorageWavePickControl();
    BinPutAwayControl objBinPutAwayControl = new BinPutAwayControl();
    BinPutAwayMultipleToteControl objBinPutAwayMultipleToteControl = new BinPutAwayMultipleToteControl();

    private Spinner sp_bin_storage_wave_pick_rack;
    private Spinner sp_bin_storage_wave_pick_type;
    private Spinner sp_bin_storage_wave_pick_tote_type;
    private Spinner sp_bin_storage_wave_pick_div;
    private Button bt_bin_storage_wave_pick_load;
    private ListView lv_bin_storage_wave_pick_details;

    private TextView et_bin_storage_wave_pick_out_toteid;
    private EditText et_bin_storage_wave_pick_out_location;
    private Button bt_bin_storage_wave_pick_out_proceed;
    private Button bt_bin_storage_wave_pick_out_close;

    private boolean b_Result;

    private MyBinStorageWavePickFragmentAdp adapter;

    // Background executor
    private ExecutorService ioExecutor;

    // Loader dialog
    private AlertDialog loadingDialog;

    public BinStorageWavePickFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_storage_wave_pick, container, false);

        sp_bin_storage_wave_pick_rack = view.findViewById(R.id.sp_bin_storage_wave_pick_rack);
        sp_bin_storage_wave_pick_type = view.findViewById(R.id.sp_bin_storage_wave_pick_type);
        sp_bin_storage_wave_pick_tote_type = view.findViewById(R.id.sp_bin_storage_wave_pick_tote_type);
        sp_bin_storage_wave_pick_div = view.findViewById(R.id.sp_bin_storage_wave_pick_div);
        bt_bin_storage_wave_pick_load = view.findViewById(R.id.bt_bin_storage_wave_pick_load);
        lv_bin_storage_wave_pick_details = view.findViewById(R.id.lv_bin_storage_wave_pick_details);

        ioExecutor = Executors.newSingleThreadExecutor();

        // Fast UI: empty adapter first
        adapter = new MyBinStorageWavePickFragmentAdp(new ArrayList<>());
        lv_bin_storage_wave_pick_details.setAdapter(adapter);

        setupToteTypeSpinner();       // local only
        loadInitialSpinnersAsync();   // DB calls async

        bt_bin_storage_wave_pick_load.setOnClickListener(v -> loadDetailsAsync());

        sp_bin_storage_wave_pick_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                handleTypeChangeAsync();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // no-op
            }
        });

        return view;
    }

    // ----------------------------
    // Loading UI
    // ----------------------------
    private void showLoading(@NonNull String msg) {
        if (!isAdded()) return;
        if (loadingDialog != null && loadingDialog.isShowing()) return;
        loadingDialog = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage(msg)
                .create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            try { loadingDialog.dismiss(); } catch (Exception ignore) {}
        }
        loadingDialog = null;
    }

    // ----------------------------
    // Spinner setup
    // ----------------------------
    private void setupToteTypeSpinner() {
        List<String> arrTypeType = new ArrayList<>();
        arrTypeType.add("ALL");
        if ("UAE".equals(objGlobal.getWorkLocation())) {
            arrTypeType.add("B");
            arrTypeType.add("E,J");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, arrTypeType);
        sp_bin_storage_wave_pick_tote_type.setAdapter(adp);
    }

    private void loadInitialSpinnersAsync() {
        showLoading("Loading...");
        bt_bin_storage_wave_pick_load.setEnabled(false);

        ioExecutor.execute(() -> {
            try {
                List<String> racks = objBinStorageWavePickControl.loadBinStorageWavePickRack("");
                List<String> types = objBinStorageWavePickControl.loadPickType();
                List<String> divs  = objBinStorageWavePickControl.loadPickDivision();

                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        sp_bin_storage_wave_pick_rack.setAdapter(new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_dropdown_item_1line, racks));
                        sp_bin_storage_wave_pick_type.setAdapter(new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_dropdown_item_1line, types));
                        sp_bin_storage_wave_pick_div.setAdapter(new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_dropdown_item_1line, divs));
                    } finally {
                        hideLoading();
                        bt_bin_storage_wave_pick_load.setEnabled(true);
                    }

                    // IMPORTANT: Do NOT auto-load details here. Page loads fast.
                    // If you want auto-load, call loadDetailsAsync() here.
                });

            } catch (Exception e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    bt_bin_storage_wave_pick_load.setEnabled(true);
                    okMessage("BinStorageWavePick", e.getMessage());
                });
            }
        });
    }

    private void handleTypeChangeAsync() {
        if (!isAdded()) return;

        sp_bin_storage_wave_pick_div.setEnabled(true);
        sp_bin_storage_wave_pick_tote_type.setEnabled(true);

        String type = String.valueOf(sp_bin_storage_wave_pick_type.getSelectedItem());

        if ("ALL WINTER".equals(type)) {
            sp_bin_storage_wave_pick_div.setSelection(0);
            sp_bin_storage_wave_pick_tote_type.setSelection(0);
            sp_bin_storage_wave_pick_div.setEnabled(false);
            sp_bin_storage_wave_pick_tote_type.setEnabled(false);
        }

        showLoading("Loading racks...");
        bt_bin_storage_wave_pick_load.setEnabled(false);

        ioExecutor.execute(() -> {
            try {
                List<String> racks;
                if ("SKIPPED BOXES".equals(type)) {
                    racks = objBinStorageWavePickControl.loadBinStorageWavePickRack("SKIPPED BOXES");
                } else if ("OVERRIDE BOXES".equals(type)) {
                    racks = objBinStorageWavePickControl.loadBinStorageWavePickRack("OVERRIDE BOXES");
                } else {
                    racks = objBinStorageWavePickControl.loadBinStorageWavePickRack("");
                }

                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        sp_bin_storage_wave_pick_rack.setAdapter(new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_dropdown_item_1line, racks));
                    } finally {
                        hideLoading();
                        bt_bin_storage_wave_pick_load.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    bt_bin_storage_wave_pick_load.setEnabled(true);
                    okMessage("BinStorageWavePick", e.getMessage());
                });
            }
        });
    }

    // ----------------------------
    // Load tickets async
    // ----------------------------
    private void loadDetailsAsync() {
        if (!isAdded()) return;

        String rack = String.valueOf(sp_bin_storage_wave_pick_rack.getSelectedItem()).trim();
        String pickType = String.valueOf(sp_bin_storage_wave_pick_type.getSelectedItem()).trim();
        String div = String.valueOf(sp_bin_storage_wave_pick_div.getSelectedItem()).trim();

        if (TextUtils.isEmpty(rack)) {
            okMessage("BinStorageWavePick", "Please select rack");
            sp_bin_storage_wave_pick_rack.requestFocus();
            vibrate(250);
            return;
        }

        showLoading("Loading Data...");
        bt_bin_storage_wave_pick_load.setEnabled(false);

        ioExecutor.execute(() -> {
            ArrayList<BinStorageWavePickTicket> list =
                    objBinStorageWavePickControl.loadBinStorageWaveDetails(rack, pickType, div);

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                try {
                    if (list == null) {
                        okMessage("BinStorageWavePick", objGlobal.getErrorMessage());
                        vibrate(250);
                        return;
                    }
                    adapter.setData(list);
                } finally {
                    hideLoading();
                    bt_bin_storage_wave_pick_load.setEnabled(true);
                }
            });
        });
    }

    // ----------------------------
    // Adapter (optimized: reuse views)
    // ----------------------------
    private class MyBinStorageWavePickFragmentAdp extends BaseAdapter {
        private ArrayList<BinStorageWavePickTicket> list;

        MyBinStorageWavePickFragmentAdp(ArrayList<BinStorageWavePickTicket> list) {
            this.list = list;
        }

        void setData(ArrayList<BinStorageWavePickTicket> newList) {
            this.list = (newList == null) ? new ArrayList<>() : newList;
            notifyDataSetChanged();
        }

        @Override public int getCount() { return list == null ? 0 : list.size(); }
        @Override public Object getItem(int position) { return list.get(position); }
        @Override public long getItemId(int position) { return position; }

        class VH {
            TextView toteId, boxNo, locationText, color, order, zone, checkingType, slno, perc;
            Button outBtn, skipBtn;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = convertView;
            VH vh;

            if (myView == null) {
                myView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bin_storage_wave_pick_ticket, parent, false);
                vh = new VH();
                vh.toteId = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_toteid);
                vh.boxNo = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_boxno);
                vh.locationText = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_location);
                vh.color = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_color);
                vh.order = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_order);
                vh.zone = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_zone);
                vh.checkingType = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_checkingtype);
                vh.slno = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_slno);
                vh.perc = myView.findViewById(R.id.tv_bin_storage_wave_pick_ticket_perc);
                vh.outBtn = myView.findViewById(R.id.bt_bin_storage_wave_pick_ticket_out);
                vh.skipBtn = myView.findViewById(R.id.bt_bin_storage_wave_pick_ticket_skip);
                myView.setTag(vh);
            } else {
                vh = (VH) myView.getTag();
            }

            final BinStorageWavePickTicket s = list.get(position);

            vh.toteId.setText("Tote.ID : " + String.valueOf(s.toteId));
            vh.boxNo.setText("Box.No : " + String.valueOf(s.boxNo));
            vh.locationText.setText("Location : " + String.valueOf(s.text));
            vh.color.setText("Color : " + String.valueOf(s.color));

            // Reset background first (important for reused views)
            vh.color.setBackgroundColor(Color.TRANSPARENT);
            if ("Green".equalsIgnoreCase(s.color)) vh.color.setBackgroundColor(Color.GREEN);
            if ("Yellow".equalsIgnoreCase(s.color)) vh.color.setBackgroundColor(Color.YELLOW);

            vh.order.setText("Pick Order : " + String.valueOf(s.pickOrder));
            vh.zone.setText("Zone : " + String.valueOf(s.zones));
            vh.checkingType.setText("Checking Type : " + String.valueOf(s.checkingType));
            vh.slno.setText(String.valueOf(s.rowNo));
            vh.perc.setText("Box % : " + String.valueOf(s.boxPerc));

            boolean firstRow = "1".equals(String.valueOf(s.rowNo));
            vh.outBtn.setEnabled(firstRow);
            vh.skipBtn.setEnabled(firstRow);

            vh.outBtn.setOnClickListener(v -> openPopupWindow(s.toteId, s.boxNo, "OUT", s.location, s.dDeep));

            vh.skipBtn.setOnClickListener(v -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setMessage("Are you sure to skip this location?")
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            b_Result = objBinStorageWavePickControl.skipWavePick(s.location);
                            if (b_Result) {
                                // Reload list async (fast)
                                clearAllAsync(sp_bin_storage_wave_pick_rack.getSelectedItem().toString(),
                                        sp_bin_storage_wave_pick_type.getSelectedItem().toString(),
                                        sp_bin_storage_wave_pick_div.getSelectedItem().toString(),
                                        (getCount() != 1) ? sp_bin_storage_wave_pick_rack.getSelectedItem().toString() : "");
                            } else {
                                okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
                                vibrate(250);
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {})
                        .show();
            });

            return myView;
        }
    }

    // ----------------------------
    // Popup
    // ----------------------------
    void openPopupWindow(String toteId, String boxNo, String direction, String location, String dBeep) {
        Dialog myDialog = new Dialog(requireContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.bin_storage_wave_pick_out_ticket);

        et_bin_storage_wave_pick_out_toteid = myDialog.findViewById(R.id.et_bin_storage_wave_pick_out_toteid);
        et_bin_storage_wave_pick_out_location = myDialog.findViewById(R.id.et_bin_storage_wave_pick_out_location);
        bt_bin_storage_wave_pick_out_proceed = myDialog.findViewById(R.id.bt_bin_storage_wave_pick_out_proceed);
        bt_bin_storage_wave_pick_out_close = myDialog.findViewById(R.id.bt_bin_storage_wave_pick_out_close);

        // Keep keypad hidden if required
        View.OnTouchListener hideKbTouch = (v, event) -> {
            v.onTouchEvent(event);
            InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return objGlobal.getHideKeyPad();
        };

        et_bin_storage_wave_pick_out_toteid.setOnTouchListener(hideKbTouch);
        et_bin_storage_wave_pick_out_location.setOnTouchListener(hideKbTouch);

        et_bin_storage_wave_pick_out_toteid.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String scan = et_bin_storage_wave_pick_out_toteid.getText().toString().trim();
                if (TextUtils.isEmpty(scan)) {
                    okMessage("Popup", "Cant Blank toteid");
                    vibrate(250);
                    et_bin_storage_wave_pick_out_toteid.requestFocus();
                    return true;
                }
                if (scan.equals(toteId) || scan.equals(boxNo)) {
                    et_bin_storage_wave_pick_out_location.requestFocus();
                    return true;
                } else {
                    okMessage("BinPutAwayFragment", "Selected tote and scanned totes is not matching");
                    et_bin_storage_wave_pick_out_toteid.setText("");
                    vibrate(250);
                    et_bin_storage_wave_pick_out_toteid.requestFocus();
                    return true;
                }
            }
            return false;
        });

        et_bin_storage_wave_pick_out_location.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String scanLoc = et_bin_storage_wave_pick_out_location.getText().toString().trim();
                if (TextUtils.isEmpty(scanLoc)) {
                    okMessage("Popup", "Cant Blank location");
                    vibrate(250);
                    et_bin_storage_wave_pick_out_location.requestFocus();
                    return true;
                }
                if (!location.equals(scanLoc)) {
                    okMessage("BinPutAwayFragment", "Selected location and scanned location is not matching");
                    et_bin_storage_wave_pick_out_location.setText("");
                    et_bin_storage_wave_pick_out_location.requestFocus();
                    vibrate(250);
                    return true;
                }
                bt_bin_storage_wave_pick_out_proceed.requestFocus();
                return true;
            }
            return false;
        });

        bt_bin_storage_wave_pick_out_close.setOnClickListener(v -> myDialog.dismiss());

        bt_bin_storage_wave_pick_out_proceed.setOnClickListener(v -> {
            String scanTote = et_bin_storage_wave_pick_out_toteid.getText().toString().trim();
            String scanLocation = et_bin_storage_wave_pick_out_location.getText().toString().trim();

            String location1 = location.replace("-", "");
            scanLocation = scanLocation.replace("-", "");

            if (TextUtils.isEmpty(scanTote)) {
                okMessage("BinPutAwayFragment", "Please scan tote");
                vibrate(250);
                et_bin_storage_wave_pick_out_toteid.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(scanLocation)) {
                okMessage("BinPutAwayFragment", "Please scan location");
                vibrate(250);
                et_bin_storage_wave_pick_out_location.requestFocus();
                return;
            }
            if (!scanTote.equals(toteId) && !scanTote.equals(boxNo)) {
                okMessage("BinPutAwayFragment", "Selected tote and scanned totes are not matching");
                vibrate(250);
                et_bin_storage_wave_pick_out_toteid.requestFocus();
                return;
            }
            if (!scanLocation.equals(location1)) {
                okMessage("BinPutAwayFragment", "Selected location and scanned location is not matching");
                vibrate(250);
                et_bin_storage_wave_pick_out_location.requestFocus();
                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setMessage("Are you sure to save?")
                    .setTitle("Confirmation")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        b_Result = objBinPutAwayControl.saveBinInOutSingle(toteId, boxNo, direction, location, dBeep);
                        if (b_Result) {
                            // Refresh list async
                            clearAllAsync(
                                    sp_bin_storage_wave_pick_rack.getSelectedItem().toString(),
                                    sp_bin_storage_wave_pick_type.getSelectedItem().toString(),
                                    sp_bin_storage_wave_pick_div.getSelectedItem().toString(),
                                    (adapter.getCount() != 1) ? sp_bin_storage_wave_pick_rack.getSelectedItem().toString() : ""
                            );
                            myDialog.dismiss();
                        } else {
                            okMessage("BinPutAwayFragment", objGlobal.getErrorMessage());
                            vibrate(250);
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {})
                    .show();
        });

        myDialog.show();
        et_bin_storage_wave_pick_out_toteid.requestFocus();
    }

    // ----------------------------
    // Refresh list async (instead of blocking UI)
    // ----------------------------
    private void clearAllAsync(String rack, String pickType, String div, String zoneToSelect) {
        if (!isAdded()) return;

        showLoading("Refreshing...");
        ioExecutor.execute(() -> {
            ArrayList<BinStorageWavePickTicket> list =
                    objBinStorageWavePickControl.loadBinStorageWaveDetails(rack, pickType, div);

            List<String> racks = objBinStorageWavePickControl.loadBinStorageWavePickRack("");

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                try {
                    if (list != null) adapter.setData(list);

                    ArrayAdapter<String> rackAdp = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, racks);
                    sp_bin_storage_wave_pick_rack.setAdapter(rackAdp);

                    if (!TextUtils.isEmpty(zoneToSelect)) {
                        int pos = rackAdp.getPosition(zoneToSelect);
                        if (pos >= 0) sp_bin_storage_wave_pick_rack.setSelection(pos);
                        // auto load after selection
                        loadDetailsAsync();
                    }
                } finally {
                    hideLoading();
                }
            });
        });
    }

    // ----------------------------
    // Utils
    // ----------------------------
    void vibrate(int duration) {
        if (!isAdded()) return;
        Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    void okMessage(String title, String message) {
        if (!isAdded()) return;
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        if (ioExecutor != null) {
            ioExecutor.shutdownNow();
            ioExecutor = null;
        }
    }
}
