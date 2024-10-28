package com.bflgroup.warehouse.ui.pricecheck;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.text.DecimalFormat;

public class PriceCheckFragment extends Fragment {

    private EditText et_price_check_scan;
    private Button bt_price_check_scan;
    private TextView tv_price_check_scan;
    private TextView tv_price_check_itemcode;
    private TextView tv_price_check_message;
    private TextView tv_price_check_description;
    private TextView tv_price_check_group;
    private TextView tv_price_check_current_price;
    private TextView tv_price_check_disc_perc;
    private TextView tv_price_check_new_price;
    private TextView tv_price_check_stock;
    private TextView tv_price_check_department;
    private TextView tv_price_check_division;
    private TextView tv_price_check_itemtype;

    private Global objGlobal = Global.getInstance();
    private PriceCheckControl objPriceCheckControl = new PriceCheckControl();
    private PriceCheckScanDetail objPriceCheckScanDetail = PriceCheckScanDetail.getInstance();

    DecimalFormat num_format = new DecimalFormat("###,###");
    DecimalFormat curr_format = new DecimalFormat("###,###0.00");

    public PriceCheckFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_check, container, false);

        et_price_check_scan = (EditText) view.findViewById(R.id.et_price_check_scan);
        bt_price_check_scan = (Button) view.findViewById(R.id.bt_price_check_scan);
        tv_price_check_scan = (TextView) view.findViewById(R.id.tv_price_check_scan);
        tv_price_check_itemcode = (TextView) view.findViewById(R.id.tv_price_check_itemcode);
        tv_price_check_message = (TextView) view.findViewById(R.id.tv_price_check_message);
        tv_price_check_description = (TextView) view.findViewById(R.id.tv_price_check_description);
        tv_price_check_group = (TextView) view.findViewById(R.id.tv_price_check_group);
        tv_price_check_current_price = (TextView) view.findViewById(R.id.tv_price_check_current_price);
        tv_price_check_disc_perc = (TextView) view.findViewById(R.id.tv_price_check_disc_perc);
        tv_price_check_new_price = (TextView) view.findViewById(R.id.tv_price_check_new_price);
        tv_price_check_stock = (TextView) view.findViewById(R.id.tv_price_check_stock);
        tv_price_check_department = (TextView) view.findViewById(R.id.tv_price_check_department);
        tv_price_check_division = (TextView) view.findViewById(R.id.tv_price_check_division);
        tv_price_check_itemtype=(TextView) view.findViewById(R.id.tv_price_check_itemtype);

        et_price_check_scan.requestFocus();

        bt_price_check_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getScanDetails(et_price_check_scan.getText().toString())){
                    okMessage("Price Check", objGlobal.getErrorMessage());
                    vibrate(500);
                    et_price_check_scan.requestFocus();
                }
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

        et_price_check_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(!getScanDetails(et_price_check_scan.getText().toString())){
                        okMessage("Price Check", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_price_check_scan.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private boolean getScanDetails(String scan){
        if(scan.isEmpty()){
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
        tv_price_check_disc_perc.setText("");
        tv_price_check_new_price.setText("");
        tv_price_check_stock.setText("");
        tv_price_check_department.setText("");
        tv_price_check_division.setText("");
        tv_price_check_itemtype.setText("");
        if(!objPriceCheckControl.getItemDetails(scan)){
            okMessage("Price Check", objGlobal.getErrorMessage());
            return false;
        }
        try {
            tv_price_check_scan.setText(scan);
            tv_price_check_itemcode.setText(objPriceCheckScanDetail.getItemcode());
            tv_price_check_message.setText(objPriceCheckScanDetail.getMessage());
            tv_price_check_description.setText(objPriceCheckScanDetail.getDescription());
            tv_price_check_group.setText(objPriceCheckScanDetail.getGroup());
            tv_price_check_current_price.setText(curr_format.format(objPriceCheckScanDetail.getOldPrice()));
            tv_price_check_disc_perc.setText(curr_format.format(objPriceCheckScanDetail.getDiscPerc()));
            tv_price_check_new_price.setText(curr_format.format(objPriceCheckScanDetail.getPrice()));
            tv_price_check_stock.setText(num_format.format(objPriceCheckScanDetail.getStock()));
            tv_price_check_department.setText(objPriceCheckScanDetail.getDepartment());
            tv_price_check_division.setText(objPriceCheckScanDetail.getDivision());
            tv_price_check_itemtype.setText(objPriceCheckScanDetail.getItemType());
            if (!objPriceCheckScanDetail.getMessage().isEmpty()) {
                vibrateSound(1000);
            }
        } catch (Exception e){
            okMessage("Price Check 1",e.toString());
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