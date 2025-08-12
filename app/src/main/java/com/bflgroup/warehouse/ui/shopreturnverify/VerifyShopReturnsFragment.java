package com.bflgroup.warehouse.ui.shopreturnverify;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.ui.shopreturnverify.model.ShopReturnData;

import java.util.ArrayList;
import java.util.List;


public class VerifyShopReturnsFragment extends Fragment implements View.OnClickListener {


    private View rootView;
    private EditText etEntryNo;
    private Button btScan;
    private VerifyShopReturnControl verifyShopReturnControl = new VerifyShopReturnControl();
    private VerifyShopReturnsAdapter verifyShopReturnsAdapter;
    private ListView lvVerifyShopReturns;
    private Button btClear;
    private Button btSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_verify_shop_returns, container, false);
        initViews();
        setOnClickListener();
        return rootView;
    }

    private void setOnClickListener() {
        btScan.setOnClickListener(this);
        btClear.setOnClickListener(this);
        btSave.setOnClickListener(this);
    }

    private void initViews() {
        etEntryNo = rootView.findViewById(R.id.et_entry_no);
        btScan = rootView.findViewById(R.id.bt_scan);
        lvVerifyShopReturns = rootView.findViewById(R.id.lv_verify_shop_returns);
        btClear = rootView.findViewById(R.id.bt_clear);
        btSave = rootView.findViewById(R.id.bt_save);

        if (verifyShopReturnControl.countTempData() >0)
        {
            List<ShopReturnData> shopReturnData = verifyShopReturnControl.tempData();
            verifyShopReturnsAdapter =new VerifyShopReturnsAdapter(shopReturnData);
            lvVerifyShopReturns.setAdapter(verifyShopReturnsAdapter);
        }
        etEntryNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!etEntryNo.getText().toString().equals("")) {
                        btScan.performClick();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_scan:
                if (verifyShopReturnControl.tempData().isEmpty()) {
                    if (etEntryNo.getText().toString() != null && etEntryNo.getText().toString() != "") {
                        ShopReturnData shopDetail = verifyShopReturnControl.shopNameCheck(etEntryNo.getText().toString());
                        if (shopDetail.shopName != null) {
                            boolean insertData = verifyShopReturnControl.insertToTempDB(shopDetail.entryNo, shopDetail.shopName, shopDetail.category, shopDetail.username);
                            if (insertData) {
                                List<ShopReturnData> verifyShopReturns = verifyShopReturnControl.tempData();
                                verifyShopReturnsAdapter = new VerifyShopReturnsAdapter(verifyShopReturns);
                                lvVerifyShopReturns.setAdapter(verifyShopReturnsAdapter);

                            } else {
                                okMessage("Verification Shop return", "Data not ", 0);
                                vibrate(500);

                            }

                        } else {
                            okMessage("Verification Shop return", shopDetail.errorMessage, 0);
                            vibrate(500);

                        }
                    } else {
                        okMessage("Verification Shop return", "Please enter entry number", 0);
                        vibrate(500);
                        etEntryNo.setText("");
                    }
                }
                break;
            case R.id.bt_clear:
                if (verifyShopReturnControl.countTempData() > 0)
                    okMessage("Message", "Are you sure you want to clear the data?", 1);
                break;
            case R.id.bt_save:
                okMessage("Message","Are you sure you want to save it?",2);
            default:
                break;
        }
    }

    private void clearTempData() {
        if (verifyShopReturnControl.clearTempData()) {
            verifyShopReturnsAdapter = new VerifyShopReturnsAdapter(new ArrayList<>());
            lvVerifyShopReturns.setAdapter(verifyShopReturnsAdapter);
        } else {
            okMessage("Verify shop return", "something went wrong", 0);
        }
    }

    void okMessage(String title, String message, int flag) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        if (flag != 0)
            alert.setNegativeButton("Cancel", null);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag == 1)
                    clearTempData();
                if (flag == 2)
                {
                    boolean isSaved = verifyShopReturnControl.saveShopReturnsVerify();
                    if (isSaved){
                        if (verifyShopReturnControl.clearTempData()) {
                            verifyShopReturnsAdapter = new VerifyShopReturnsAdapter(new ArrayList<>());
                            lvVerifyShopReturns.setAdapter(verifyShopReturnsAdapter);
                        } else {
                            okMessage("Verify shop return", "Data saved, but something went wrong. please contact IT", 0);
                            vibrate(500);
                        }

                    }
                }
                // Your action here

            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }


    private class VerifyShopReturnsAdapter extends BaseAdapter {
        public List<ShopReturnData> shopReturnDataList;

        public VerifyShopReturnsAdapter(List<ShopReturnData> shopReturnDataList) {
            this.shopReturnDataList = shopReturnDataList;
        }

        @Override
        public int getCount() {
            return shopReturnDataList.size();
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
            View myView = mInflater.inflate(R.layout.view_shop_return_verify, null);
            TextView tvShopName = myView.findViewById(R.id.tv_shop_name);
            TextView tvEntryNo = myView.findViewById(R.id.tv_entry_no);
            tvShopName.setText(shopReturnDataList.get(position).shopName);
            tvEntryNo.setText(shopReturnDataList.get(position).entryNo);
            return myView;
        }
    }
}