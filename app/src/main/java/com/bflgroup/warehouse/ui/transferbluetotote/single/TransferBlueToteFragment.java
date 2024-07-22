package com.bflgroup.warehouse.ui.transferbluetotote.single;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.databinding.FragmentTransferBlueToteBinding;

public class TransferBlueToteFragment extends Fragment {


    public TransferBlueToteFragment() {
        // Required empty public constructor
    }

    FragmentTransferBlueToteBinding binding;
    private boolean b_Result;
    Global objGlobal = Global.getInstance();
    TransferBlueToteIdControl transferBlueToteIdControl = new TransferBlueToteIdControl();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_blue_tote, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentTransferBlueToteBinding.bind(view);

        onClick();
    }

    private void onClick() {

        binding.btnSavetoteid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transferBlueToteIdControl.saveTransferboxno(binding.edtBlueboxId.getText().toString(), binding.edtCartonboxId.getText().toString())) {
                    okMessage("", "Tranfered Successfully");
                    vibrate(500);
                } else {
                    okMessage("Blue tote", objGlobal.getErrorMessage());
                    vibrate(500);
                }
            }
        });
        binding.edtBlueboxId.setOnTouchListener(new View.OnTouchListener() {
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
        binding.edtCartonboxId.setOnTouchListener(new View.OnTouchListener() {
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

        binding.edtBlueboxId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String blueboxtoteTrfId = binding.edtBlueboxId.getText().toString();
                    b_Result = transferBlueToteIdControl.validateBluetoteID(blueboxtoteTrfId);
                    if (b_Result == false) {
                        okMessage("", objGlobal.getErrorMessage());
                        vibrate(500);
                        binding.edtBlueboxId.setText("");
                        binding.edtBlueboxId.requestFocus();
                        return false;
                    }
                }
                return false;
            }
        });
        binding.edtCartonboxId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String cartonboxtoteTrfId = binding.edtCartonboxId.getText().toString();
                    b_Result = transferBlueToteIdControl.validateCartonboxno(cartonboxtoteTrfId);
                    if (b_Result == false) {
                        okMessage("Carton box", objGlobal.getErrorMessage());
                        vibrate(500);
                        binding.edtBlueboxId.setText("");
                        binding.edtBlueboxId.requestFocus();
                        return false;
                    }
                }
                return false;
            }
        });
    }


    void okMessage(String title, String message) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    void okMessage(String title, String message, boolean cancel) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(cancel);
        alert.create().show();
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }
}