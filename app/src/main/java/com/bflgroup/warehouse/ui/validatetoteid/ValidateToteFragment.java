package com.bflgroup.warehouse.ui.validatetoteid;

import android.content.Context;
import android.graphics.Color;
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

import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;


public class ValidateToteFragment  extends Fragment {
    private EditText et_tote_id;
    private TextView tv_message;
    private Button btn_tote_id;


    private Global objGlobal = Global.getInstance();
    private ValidateToteControl objValidateToteControl = new ValidateToteControl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_validatetote, container, false);

        et_tote_id = view.findViewById(R.id.et_tote_id);
        tv_message = view.findViewById(R.id.tv_message);
        btn_tote_id = view.findViewById(R.id.btn_tote_id);
        et_tote_id.requestFocus();

        et_tote_id.setOnTouchListener(new View.OnTouchListener() {
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


        et_tote_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String boxno = objValidateToteControl.checkTote(String.valueOf(et_tote_id.getText().toString().trim()), getContext());
                    if(boxno.equals("INVALID")){
                        tv_message.setText("This Tote - " +et_tote_id.getText().toString() + "  is INVALID");
                        et_tote_id.setText("");
                        tv_message.setTextColor(Color.RED);
                        et_tote_id.requestFocus();
                        vibrate(500, getContext());
                    }else if(boxno.equals("")){


                        tv_message.setText(et_tote_id.getText().toString() + " - Toteid status is closed and VALID to use.");
                        et_tote_id.setText("");

                        tv_message.setTextColor(Color.BLACK);
                        et_tote_id.requestFocus();



                    }

                    else{

                        tv_message.setText("This Tote - " +et_tote_id.getText().toString() + "  is already opened with Box no - " + boxno);
                        et_tote_id.setText("");
                        tv_message.setTextColor(Color.RED);

                        et_tote_id.requestFocus();
                        vibrate(500,  getContext());

                    }



                }
                return false;
            }
        });

        btn_tote_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String boxno = objValidateToteControl.checkTote(String.valueOf(et_tote_id.getText().toString().trim()), getContext());
                if(boxno.equals("INVALID")){
                    tv_message.setText("This Tote - " +et_tote_id.getText().toString() + "  is INVALID");
                    et_tote_id.setText("");
                    tv_message.setTextColor(Color.RED);
                    et_tote_id.requestFocus();
                    vibrate(500, getContext());
                }else if(boxno.equals("")){


                    tv_message.setText(et_tote_id.getText().toString() + " - Toteid status is closed and VALID to use.");
                    et_tote_id.setText("");

                    tv_message.setTextColor(Color.BLACK);
                    et_tote_id.requestFocus();



                }

                else{

                    tv_message.setText("This Tote - " +et_tote_id.getText().toString() + "  is already opened with Box - " + boxno);
                    et_tote_id.setText("");
                    tv_message.setTextColor(Color.RED);

                    et_tote_id.requestFocus();
                    vibrate(500,  getContext());

                }
            }
        });




        return view;
    }

    void vibrate(int duration, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Uri notification = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.errorsound);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        audioManager.setStreamVolume(AudioManager.STREAM_RING,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);
        r.play();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }

    }

}
