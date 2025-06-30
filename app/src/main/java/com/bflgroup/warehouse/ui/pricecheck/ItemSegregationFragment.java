package com.bflgroup.warehouse.ui.pricecheck;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.pricecheck.model.ItemBoxDetails;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemSegregationFragment extends Fragment {

    private EditText et_price_check_boxno;
    private Button bt_price_check_fetchbox;
    private Button bt_price_check_clear_box;
    private EditText et_price_check_scan;
    private Button bt_price_check_scan;
    private TextView tv_price_check_scan;
    private TextView tv_price_check_itemcode;
    private TextView tv_price_check_message;
    private TextView tv_price_check_description;
    private TextView tv_price_check_group;
    private TextView tv_price_check_current_price;
    private TextView tv_price_check_class;
    private TextView tv_price_check_subclass;
    private TextView tv_price_check_stock;
    private TextView tv_price_check_department;
    private TextView tv_price_check_division;
    private TextView tv_price_check_itemtype;

    private Global objGlobal = Global.getInstance();
    private ItemSegregationControl objPriceCheckControl = new ItemSegregationControl();
    private PriceCheckScanDetail objPriceCheckScanDetail = PriceCheckScanDetail.getInstance();

    DecimalFormat num_format = new DecimalFormat("###,###");
    DecimalFormat curr_format = new DecimalFormat("###,###0.00");
    private ListView lv_item_details;
    private ArrayList<ItemBoxDetails> tempData;
    private ItemSegAdapter itemSegAdapter;
    private Button bt_box_close;
    private Button bt_clear;

    public ItemSegregationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_seggregation, container, false);

        et_price_check_boxno = (EditText) view.findViewById(R.id.et_price_check_boxno);
        bt_price_check_fetchbox= (Button) view.findViewById(R.id.bt_price_check_fetchbox);
        bt_price_check_clear_box= (Button) view.findViewById(R.id.bt_price_check_clear_box);
        et_price_check_scan = (EditText) view.findViewById(R.id.et_price_check_scan);
        bt_price_check_scan = (Button) view.findViewById(R.id.bt_price_check_scan);
        tv_price_check_scan = (TextView) view.findViewById(R.id.tv_price_check_scan);
        tv_price_check_itemcode = (TextView) view.findViewById(R.id.tv_price_check_itemcode);
        tv_price_check_message = (TextView) view.findViewById(R.id.tv_price_check_message);
        tv_price_check_description = (TextView) view.findViewById(R.id.tv_price_check_description);
        tv_price_check_group = (TextView) view.findViewById(R.id.tv_price_check_group);
        tv_price_check_current_price = (TextView) view.findViewById(R.id.tv_price_check_current_price);
        tv_price_check_class = (TextView) view.findViewById(R.id.tv_price_check_class);
        tv_price_check_subclass = (TextView) view.findViewById(R.id.tv_price_check_subclass);
        tv_price_check_stock = (TextView) view.findViewById(R.id.tv_price_check_stock);
        tv_price_check_department = (TextView) view.findViewById(R.id.tv_price_check_department);
        tv_price_check_division = (TextView) view.findViewById(R.id.tv_price_check_division);
        tv_price_check_itemtype=(TextView) view.findViewById(R.id.tv_price_check_itemtype);
        lv_item_details=(ListView) view.findViewById(R.id.lv_item_details);
        bt_box_close=(Button) view.findViewById(R.id.bt_box_close);
        bt_clear=(Button) view.findViewById(R.id.bt_clear);

        et_price_check_boxno.requestFocus();

        bt_price_check_fetchbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!objPriceCheckControl.getBoxDetails(et_price_check_boxno.getText().toString())) {
                    okMessage("Price Check", objGlobal.getErrorMessage());
                    vibrate(500);
                    bt_price_check_fetchbox.requestFocus();
                } else {
                    et_price_check_boxno.setEnabled(false);
                    bt_price_check_fetchbox.setEnabled(false);
                    et_price_check_scan.requestFocus();
                }
            }
        });

        bt_price_check_clear_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_clear.performClick();
            }
        });

        bt_price_check_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getScanDetails(et_price_check_scan.getText().toString(),et_price_check_boxno.getText().toString())){
                    okMessage("Price Check", objGlobal.getErrorMessage());
                    vibrate(500);
                    bt_price_check_scan.requestFocus();
                }
            }
        });

        et_price_check_boxno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_price_check_scan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_price_check_boxno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(!et_price_check_boxno.getText().toString().equals("")){
                        bt_price_check_fetchbox.performClick();
                        et_price_check_scan.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        et_price_check_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(!getScanDetails(et_price_check_scan.getText().toString(),et_price_check_boxno.getText().toString())){
                        okMessage("Item Check", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_price_check_boxno.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        try {
            tempData = objPriceCheckControl.getTempData();
            if (!tempData.isEmpty()){
                itemSegAdapter = new ItemSegAdapter(tempData);
                lv_item_details.setAdapter(itemSegAdapter);
                et_price_check_boxno.setText(tempData.get(tempData.size()-1).boxno);
                bt_price_check_fetchbox.performClick();
                et_price_check_scan.requestFocus();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear the data?")
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(objPriceCheckControl.removeTempData()) {
                                    itemSegAdapter = new ItemSegAdapter(new ArrayList<>());
                                    lv_item_details.setAdapter(itemSegAdapter);
                                    et_price_check_scan.setText("");
                                    et_price_check_boxno.setText("");
                                    et_price_check_boxno.setEnabled(true);
                                    bt_price_check_fetchbox.setEnabled(true);
                                    et_price_check_boxno.requestFocus();
                                }
                                else{
                                    okMessage("Something went wrong. Please try again","");
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
        bt_box_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to close the box: "+objPriceCheckScanDetail.getBoxno()+"?")
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(objPriceCheckControl.closeBoxes(et_price_check_boxno.getText().toString())) {
                                    objPriceCheckControl.removeTempData();
                                    itemSegAdapter = new ItemSegAdapter(new ArrayList<>());
                                    lv_item_details.setAdapter(itemSegAdapter);
                                    et_price_check_boxno.setText("");
                                    et_price_check_scan.setText("");
                                    et_price_check_boxno.requestFocus();
                                    okMessage("Success","Box "+et_price_check_boxno.getText().toString()+" is closed");
                                }
                                else{
                                    okMessage("Error",objGlobal.getErrorMessage());
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



    private class ItemSegAdapter extends BaseAdapter {
        public List<ItemBoxDetails> itemBoxDetails;

        public ItemSegAdapter(List<ItemBoxDetails> rackDetailsDataList) {
            this.itemBoxDetails = rackDetailsDataList;
        }

        @Override
        public int getCount() {
            return itemBoxDetails.size();
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
            View myView = mInflater.inflate(R.layout.view_item_seggregation, null);
            TextView tvItemcode = myView.findViewById(R.id.tv_itemcode);
            TextView tvBoxNo = myView.findViewById(R.id.tv_box_no);
            TextView tvCaption = myView.findViewById(R.id.tv_caption);
            TextView tvDivision = myView.findViewById(R.id.tv_division);
            tvItemcode.setText(itemBoxDetails.get(position).itemcode);
            tvBoxNo.setText(itemBoxDetails.get(position).boxno);
            tvCaption.setText(itemBoxDetails.get(position).caption);
            tvDivision.setText(itemBoxDetails.get(position).division);
            return myView;
        }
    }

    private boolean getScanDetails(String scan,String boxNo) {
        if (scan.isEmpty()) {
            objGlobal.setErrorMessage("Please scan");
            return false;
        }
        et_price_check_scan.setText("");
        tv_price_check_scan.setText(scan);
        tv_price_check_itemcode.setText("");
        tv_price_check_message.setText("");
        tv_price_check_description.setText("");
        tv_price_check_group.setText("");
        tv_price_check_current_price.setText("");
        tv_price_check_class.setText("");
        tv_price_check_subclass.setText("");
        tv_price_check_stock.setText("");
        tv_price_check_department.setText("");
        tv_price_check_division.setText("");
        tv_price_check_itemtype.setText("");
        if (!objPriceCheckControl.getItemDetails(scan, boxNo)) return false;
        try {
            tempData = objPriceCheckControl.getTempData();
            itemSegAdapter = new ItemSegAdapter(tempData);
            lv_item_details.setAdapter(itemSegAdapter);



//            tv_price_check_scan.setText(scan);
//            tv_price_check_itemcode.setText(objPriceCheckScanDetail.getItemcode());
//            et_price_check_boxno.setText(objPriceCheckScanDetail.getBoxno());
//            tv_price_check_message.setText(objPriceCheckScanDetail.getMessage());
//            tv_price_check_description.setText(objPriceCheckScanDetail.getDescription());
//            tv_price_check_group.setText(objPriceCheckScanDetail.getGroup());
//            tv_price_check_current_price.setText(curr_format.format(objPriceCheckScanDetail.getPrice()));
//            tv_price_check_stock.setText(num_format.format(objPriceCheckScanDetail.getStock()));
//            tv_price_check_department.setText(objPriceCheckScanDetail.getDepartment());
//            tv_price_check_division.setText(objPriceCheckScanDetail.getDivision());
//            tv_price_check_itemtype.setText(objPriceCheckScanDetail.getItemType());
//            if (!objPriceCheckScanDetail.getMessage().isEmpty()) {
//                vibrateSound(1000);
//            }
        } catch (Exception e) {
            okMessage("Price Check 1", e.toString());
            return false;
        }
        return true;
    }

    String seperateBarcode(String barcode){
        String[] parts;
        String part1;
        int i;
        if(barcode.contains("/")) {
            parts = barcode.split("/");
            part1=parts[0];
        } else {
            part1=barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
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

    void vibrateSound(int duration) {
        try {
            if (objGlobal.getUserName().equals("BFL")) {
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(duration);
                }
            } else {
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                Uri notification = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.errorsound);
                Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                r.play();
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(duration);
                }
            }
        } catch (Exception e) {
            okMessage("Error vibrateSound", e.toString());
        }
    }
}