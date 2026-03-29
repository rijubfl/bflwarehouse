package com.bflgroup.warehouse.ui.updateTransfersFromPallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.ui.Updateboxfrompallet.model.BoxInOutRequestData;

import java.util.ArrayList;
import java.util.List;

public class AddOrRemoveTransferInPalletFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private EditText etBoxNo;
    private EditText etPalletNo;
    private RadioGroup rgAddRemove;
    TransferInAndOutControl transferInAndOutControl = new TransferInAndOutControl();
    private RadioButton rbAdd;
    private RadioButton rbRemove;
    private Button btnScan;
    private List<BoxInOutRequestData> boxInOutDataList;
    private List<BoxInOutRequestData> newBoxInOutDataList = new ArrayList<>();
    private ListView lvBoxInOut;
    private Button btClear;
    private Button btnBoxScan;
    private Button btnClear;
    private TransferInOutAdapter transferInOutAdapter;
    private BoxInOutRequestData boxInOutData;
    private Button btnSave;
    private TextView tvTotalCount;
    private TextView tvTotalQty;
    private int totalQty;
    private ListView lvBoxUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_or_remove_transfer_in_pallet, container, false);
        initViews();
        setOnclickListeners();
        setViews();
        return rootView;
    }

    private void setViews() {
        boxInOutDataList = transferInAndOutControl.checkTempData();
        if (boxInOutDataList != null && !boxInOutDataList.isEmpty()) {
            etPalletNo.setText(boxInOutDataList.get(0).selectedPalletNo);
            etBoxNo.setText(boxInOutDataList.get(0).selectedBoxNo);
            transferInOutAdapter = new TransferInOutAdapter(boxInOutDataList);
            lvBoxInOut.setAdapter(transferInOutAdapter);
            newBoxInOutDataList = transferInAndOutControl.checkNewTempData();
            transferInOutAdapter = new TransferInOutAdapter(newBoxInOutDataList);
            lvBoxUpdate.setAdapter(transferInOutAdapter);
            totalQty = 0;
            for (int i = 0; i < boxInOutDataList.size(); i++) {
                totalQty = totalQty + boxInOutDataList.get(i).Qty;
            }
            for (int i = 0; i < newBoxInOutDataList.size(); i++) {
                if (newBoxInOutDataList.get(i).type == 1)
                    totalQty = totalQty + newBoxInOutDataList.get(i).Qty;
                else if (newBoxInOutDataList.get(i).type == 2)
                    totalQty = totalQty - newBoxInOutDataList.get(i).Qty;
            }
            tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
            tvTotalQty.setText(String.valueOf(totalQty));
        }


    }

    private void setOnclickListeners() {
        btnScan.setOnClickListener(this);
        btnBoxScan.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void requestFocusForEditText(EditText editText) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
//                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//                }
            }
        });
    }

    private void initViews() {
        etBoxNo = rootView.findViewById(R.id.et_box_no);
        etPalletNo = rootView.findViewById(R.id.et_pallet);
        rgAddRemove = rootView.findViewById(R.id.rg_add_remove);
        rbAdd = rootView.findViewById(R.id.rb_add);
        rbRemove = rootView.findViewById(R.id.rb_remove);
        btnScan = rootView.findViewById(R.id.btn_scan);
        lvBoxInOut = rootView.findViewById(R.id.lv_box_in_out);
        lvBoxUpdate = rootView.findViewById(R.id.lv_box_update);
        btnBoxScan = rootView.findViewById(R.id.btn_scan_box);
        btnClear = rootView.findViewById(R.id.btn_clear);
        btnSave = rootView.findViewById(R.id.btn_save);
        tvTotalCount = rootView.findViewById(R.id.tv_total_count);
        tvTotalQty = rootView.findViewById(R.id.tv_total_qty);
        etPalletNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!etPalletNo.getText().toString().equals("")) {
                        btnScan.performClick();
                    }
                }
                return false;
            }
        });
        etBoxNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (!etBoxNo.getText().toString().equals("")) {
                        btnBoxScan.performClick();
                    }
                }
                return false;
            }
        });
        etPalletNo.requestFocus();

        etBoxNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });
    }

    private ProgressDialog mWaitDialog;

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:

                showAlert("Warning", "Are you sure you want make changes?", 1);


                break;
            case R.id.btn_scan:
                if (validateFields()) {
                    boxInOutDataList = transferInAndOutControl.palletsDetails(etPalletNo.getText().toString());
                    transferInOutAdapter = new TransferInOutAdapter(boxInOutDataList);
                    lvBoxInOut.setAdapter(transferInOutAdapter);
                    totalQty = 0;
                    for (int i = 0; i < boxInOutDataList.size(); i++) {
                        totalQty = totalQty + boxInOutDataList.get(i).Qty;
                    }
                    tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
                    tvTotalQty.setText(String.valueOf(totalQty));

                }
                break;
            case R.id.btn_scan_box:
                if (!etPalletNo.getText().toString().equals("")) {
                    if (validBoxFields()) {
                        if (rbAdd.isChecked()) {
                            boxInOutData = transferInAndOutControl.insertBoxToTempDB(etBoxNo.getText().toString(), etPalletNo.getText().toString(), 1);
                            if (boxInOutData != null) {
                                newBoxInOutDataList.add(0, boxInOutData);
                                transferInOutAdapter = new TransferInOutAdapter(newBoxInOutDataList);
                                lvBoxUpdate.setAdapter(transferInOutAdapter);
                                totalQty = totalQty + boxInOutData.Qty;
                                tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
                                tvTotalQty.setText(String.valueOf(totalQty));
                                etBoxNo.setText("");
                                requestFocusForEditText(etBoxNo);

                            } else {
                                showAlert("Error!!", "Box does not have any quantity. Please check", 0);
                                etBoxNo.setText("");
                                requestFocusForEditText(etBoxNo);
                            }


//                        showAlert("Warning!!", "Are you sure you want to add the box " + etBoxNo.getText().toString()
//                                + " to the pallet" + etPalletNo.getText().toString() + " ?", 1);

                        } else {


                            boolean dataStatus = transferInAndOutControl.checkTempDataForDelete(etBoxNo.getText().toString());
                            if (dataStatus) {
                                BoxInOutRequestData boxInOutRequestData = transferInAndOutControl.deleteBoxFromTempDB(etBoxNo.getText().toString(), etPalletNo.getText().toString());
                                if (boxInOutRequestData != null) {
                                    newBoxInOutDataList.add(0, boxInOutRequestData);
                                    transferInOutAdapter = new TransferInOutAdapter(newBoxInOutDataList);
                                    lvBoxUpdate.setAdapter(transferInOutAdapter);
//                            boxInOutAdapter = new BoxInOutAdapter(boxInOutDataList);
//                            lvBoxInOut.setAdapter(boxInOutAdapter);
                                    totalQty = totalQty - boxInOutRequestData.Qty;
                                    tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
                                    tvTotalQty.setText(String.valueOf(totalQty));
                                }
                                etBoxNo.setText("");
                                requestFocusForEditText(etBoxNo);
                            } else {
                                showAlert("Error!!", "Box " + etBoxNo.getText().toString() + " is not in the pallet " + etPalletNo.getText().toString(), 0);
                                etBoxNo.setText("");
                                requestFocusForEditText(etBoxNo);
                            }
//                            showAlert("Warning!!", "Are you sure you want to remove the box " + etBoxNo.getText().toString()
//                                    + " from the pallet" + etPalletNo.getText().toString() + " ?", 1);
//                        } else
//                            showAlert("Error!!", "Box " + etBoxNo.getText().toString() + " is not in the pallet " + etPalletNo.getText().toString(), 0);


                        }


                    }
                }
                break;
            case R.id.btn_clear:
                showAlert("Warning", "Are you sure you want to clear it?", 2);

                break;
        }
    }

    private void clearFields() {
        if (transferInAndOutControl.clearTempTable()) {
            boxInOutDataList.clear();
            newBoxInOutDataList.clear();
            tvTotalCount.setText("0");
            tvTotalQty.setText("0");
            transferInOutAdapter = new TransferInOutAdapter(new ArrayList<>());
            lvBoxInOut.setAdapter(transferInOutAdapter);
            transferInOutAdapter = new TransferInOutAdapter(new ArrayList<>());
            lvBoxUpdate.setAdapter(transferInOutAdapter);
            etBoxNo.setText("");
            etPalletNo.setText("");
            etPalletNo.setFocusable(true);
            etPalletNo.requestFocus();
        } else {
            okMessage("Error!!", "Something went wrong. Please try again");
            vibrate(500);
        }
    }

    private class TransferInOutAdapter extends BaseAdapter {
        public List<BoxInOutRequestData> boxInOutList;

        public TransferInOutAdapter(List<BoxInOutRequestData> boxInOutList) {
            this.boxInOutList = boxInOutList;
        }

        @Override
        public int getCount() {
            return boxInOutList.size();
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
            View myView = mInflater.inflate(R.layout.view_box_in_out, null);
            TextView tv_sr_no = myView.findViewById(R.id.tv_sr_no);
            TextView tv_box_no = myView.findViewById(R.id.tv_box_no);
            TextView tv_toteid = myView.findViewById(R.id.tv_toteid);
            TextView tv_qty = myView.findViewById(R.id.tv_qty);
            TextView tv_pallet_type = myView.findViewById(R.id.tv_pallet_type);
            TextView tv_group_code = myView.findViewById(R.id.tv_group_code);
            if (boxInOutList.get(position).type == 0) {
                tv_sr_no.setText(boxInOutList.get(position).SrNo);
            } else if (boxInOutList.get(position).type == 1) {
                tv_sr_no.setText("ADD");
            } else if (boxInOutList.get(position).type == 2) {
                tv_sr_no.setText("REMOVE");
            }
            tv_box_no.setText(boxInOutList.get(position).boxNo);
            tv_toteid.setText(boxInOutList.get(position).toteid);
            tv_qty.setText(String.valueOf(boxInOutList.get(position).Qty));
            tv_pallet_type.setText(boxInOutList.get(position).PalletType);
            tv_group_code.setText(boxInOutList.get(position).GroupCode);
            return myView;
        }
    }

    private boolean validBoxFields() {
        boolean status = true;
        if (etBoxNo.getText().toString().equals("")) {
            status = false;
            okMessage("Error!!", "Please enter the box number");
            vibrate(500);
        } else if (transferInAndOutControl.boxCheckInTempDB(etBoxNo.getText().toString())) {
            if (rbAdd.isChecked()) {
                status = false;
                okMessage("Error!!", "Box already is in the same pallet");
                vibrate(500);
                etBoxNo.setText("");
                requestFocusForEditText(etBoxNo);
            }

        } else if (!transferInAndOutControl.boxChecking(etBoxNo.getText().toString())) {
            status = false;
            okMessage("Error!!", "Invalid Box or Box is already closed");
            etBoxNo.setText("");
            requestFocusForEditText(etBoxNo);
            vibrate(500);
        } else if (rbAdd.isChecked()) {
            if (!transferInAndOutControl.addBoxChecking(boxInOutDataList.get(0).selectedPalletNo, etBoxNo.getText().toString())) {
                status = false;
                okMessage("Error!!", "Cannot proceed,Mix pallet type not allowed");
                etBoxNo.setText("");
                requestFocusForEditText(etBoxNo);
                vibrate(500);
            }
        } else if (rbRemove.isChecked()) {
//            if (!boxInAndOutControl.removeBoxChecking(etPalletNo.getText().toString(), etBoxNo.getText().toString())) {
//                status = false;
//                okMessage("Error!!", "Invalid box or pallet");
//                vibrate(500);
//            }
        }
        return status;
    }

    private boolean validateFields() {
        boolean status = true;
        if (etPalletNo.getText().toString().equals("")) {
            status = false;
            etPalletNo.setText("");
            requestFocusForEditText(etPalletNo);
            okMessage("Error!!", "Please enter the pallet number");

            vibrate(500);
        } else if (transferInAndOutControl.palletChecking(etPalletNo.getText().toString()) == null) {
            status = false;

            okMessage("Error!!", "Invalid Pallet or Pallet is already closed");
            etPalletNo.setText("");
            requestFocusForEditText(etPalletNo);
            vibrate(500);
        }
        return status;
    }

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showAlert(String title, String msg, int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(msg)
                .setTitle(title);

        // Add the OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (flag == 1) {
                    mWaitDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                    mWaitDialog.setCancelable(false);
                    StringBuilder boxes = new StringBuilder();
                    StringBuilder failedBoxes = new StringBuilder();
                    String successText = "";
                    String failureText = "";
                    StringBuilder removeBoxes = new StringBuilder();
                    StringBuilder removeFailedBoxes = new StringBuilder();
                    String removeSuccessText = "";
                    String removeFailureText = "";
                    for (int i = 0; i < newBoxInOutDataList.size(); i++) {

                        int sn = transferInAndOutControl.palletChecking(newBoxInOutDataList.get(i).selectedPalletNo);
                        //    boxInAndOutControl.updateSn(boxInOutDataList.get(0).boxNo, sn);
                        boolean status;
                        if (newBoxInOutDataList.get(i).type == 1) {
                            status = transferInAndOutControl.saveAddedBoxToDB(newBoxInOutDataList.get(i).selectedBoxNo, newBoxInOutDataList.get(i).selectedPalletNo);
                            if (status)
                                boxes.append(newBoxInOutDataList.get(i).boxNo).append("\n");
                            else
                                failedBoxes.append(newBoxInOutDataList.get(i).boxNo).append("\n");


//                        if (status) {
//                            showAlert("Success!!", "The Box " + newBoxInOutDataList.get(i).selectedBoxNo + "is added to the pallet" + newBoxInOutDataList.get(i).selectedPalletNo, 0);
//                            clearFields();
//                        } else {
//                            showAlert("Failed!!", "Something went wrong. Please try again", 0);
//                        }


                        } else if (newBoxInOutDataList.get(i).type == 2) {
                            transferInAndOutControl.updateSn(newBoxInOutDataList.get(i).boxNo, sn);
                            status = transferInAndOutControl.updatePallets(newBoxInOutDataList.get(i).boxNo, newBoxInOutDataList.get(i).selectedPalletNo, "REMOVE");
                            if (status)
                                removeBoxes.append(newBoxInOutDataList.get(i).boxNo).append("\n");
                            else
                                removeFailedBoxes.append(newBoxInOutDataList.get(i).boxNo).append("\n");


//                        if (status) {
//                            showAlert("Success!!", "The Box " + newBoxInOutDataList.get(i).selectedBoxNo + "is removed from the pallet" + newBoxInOutDataList.get(i).selectedPalletNo, 0);
//                            clearFields();
//                        } else {
//                            showAlert("Failed!!", "Something went wrong. Please try again", 1);
//                        }
                        }


                        if (i == newBoxInOutDataList.size() - 1) {
                            closeWaitDialog();
                            if (!boxes.toString().isEmpty())
                                successText = "Success!! \n" + boxes + " are added to the pallet - " + newBoxInOutDataList.get(i).selectedPalletNo + "\n";
                            if (!failedBoxes.toString().isEmpty())
                                failureText = "Failed!! \n" + failedBoxes + " are failed to add to the pallet - " + newBoxInOutDataList.get(i).selectedPalletNo + "\n";
                            if (!removeBoxes.toString().isEmpty())
                                removeSuccessText = "Success!! \n" + removeBoxes + " are removed from the pallet - " + newBoxInOutDataList.get(i).selectedPalletNo + "\n";
                            if (!removeFailedBoxes.toString().isEmpty())
                                removeFailureText = "Failed!! \n" + removeFailureText + " are failed to remove from the pallet - " + newBoxInOutDataList.get(i).selectedPalletNo + "\n";

                            showAlert("Message", successText + failureText + removeSuccessText + removeFailureText, 0);
                            clearFields();
                        }


                    }


//                    if (rbAdd.isChecked()) {
//                        boxInOutData = boxInAndOutControl.insertBoxToTempDB(etBoxNo.getText().toString(), etPalletNo.getText().toString(), 1);
//                        if (boxInOutData != null) {
//                            newBoxInOutDataList.add(0, boxInOutData);
//                            boxInOutAdapter = new BoxInOutAdapter(newBoxInOutDataList);
//                            lvBoxUpdate.setAdapter(boxInOutAdapter);
//                            totalQty = totalQty + boxInOutData.Qty;
//                            tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
//                            tvTotalQty.setText(String.valueOf(totalQty));
//                            etBoxNo.setText("");
//                            etBoxNo.requestFocus();
//                        } else {
//                            showAlert("Error!!", "Box does not have any quantity. Please check", 1);
//                            etBoxNo.setText("");
//                            etBoxNo.requestFocus();
//                        }
//                    } else {
//                     //  boxInOutDataList.clear();
//                        BoxInOutRequestData boxInOutRequestData = boxInAndOutControl.deleteBoxFromTempDB(etBoxNo.getText().toString(), etPalletNo.getText().toString());
//                        if (boxInOutRequestData != null){
//                            newBoxInOutDataList.add(0, boxInOutRequestData);
//                            boxInOutAdapter = new BoxInOutAdapter(newBoxInOutDataList);
//                            lvBoxUpdate.setAdapter(boxInOutAdapter);
////                            boxInOutAdapter = new BoxInOutAdapter(boxInOutDataList);
////                            lvBoxInOut.setAdapter(boxInOutAdapter);
//                            totalQty = totalQty - boxInOutRequestData.Qty;
//                            tvTotalCount.setText(String.valueOf(boxInOutDataList.size()));
//                            tvTotalQty.setText(String.valueOf(totalQty));
//                        }
//                        etBoxNo.setText("");
//                        etBoxNo.requestFocus();
//
//
//                    }
                } else if (flag == 2) {
                    clearFields();
                }
                dialog.dismiss();
            }
        });

        if (flag != 0) {
            // Add the Cancel button
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked Cancel button
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });
        }


        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}