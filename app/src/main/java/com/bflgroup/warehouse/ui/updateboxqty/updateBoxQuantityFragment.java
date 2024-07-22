package com.bflgroup.warehouse.ui.updateboxqty;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bflgroup.warehouse.comm.Global;

import java.sql.SQLException;
import java.util.ArrayList;

public class updateBoxQuantityFragment extends Fragment {

    private Global objGlobal = Global.getInstance();

    EditText et_tote_id;
    EditText et_itemcode;
    Button bt_tote_process;
    Button bt_clear_field;
    Button bt_remove_itemcode;
    Button bt_status_Save;
    private String toteid = "";
    private String itemcode = "";
    Boolean strflg = false;
    private ListView lv_div_seperate_details;
    private TextView totalBoxQty;
    private TextView reducedQty;
    private TextView NewBoxTotal;
    private RadioGroup radioGroup;

    private String Radioselect = "";

    ArrayList<UpdateBoxItem> objUpdateboxItem = new ArrayList<UpdateBoxItem>();
    private UpdateBoxQuantityControl objUpdateBoxQuantityControl = new UpdateBoxQuantityControl();
    MyUpdateBoxQuantity objMyUpdateBoxQuantity = null;

    public updateBoxQuantityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_box_quantity, container, false);
        et_tote_id = view.findViewById(R.id.et_tote_id);
        et_itemcode = view.findViewById(R.id.et_item_code);
        bt_tote_process = view.findViewById(R.id.bt_tote_process);
        bt_remove_itemcode = view.findViewById(R.id.bt_remove_itemcode);
        bt_status_Save = view.findViewById(R.id.bt_status_Save);
        bt_clear_field = view.findViewById(R.id.bt_clear_field);
        lv_div_seperate_details = view.findViewById(R.id.lv_div_det);
        totalBoxQty = view.findViewById(R.id.totalBoxQty);
        reducedQty = view.findViewById(R.id.reducedQty);
        NewBoxTotal = view.findViewById(R.id.NewBoxTotal);
        radioGroup = view.findViewById(R.id.radioGroup);

        UpdateBoxSharedRef objUpdateBoxSharedRef = new UpdateBoxSharedRef(getContext());
        et_tote_id.requestFocus();
        if (objUpdateBoxSharedRef.loadToteid() != "") {
            toteid = objUpdateBoxSharedRef.loadToteid();
            et_tote_id.setText(toteid);
            et_tote_id.setEnabled(false);
            et_itemcode.requestFocus();
            GetScanresult();
            totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
            reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
            NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
        }
        toteid = et_tote_id.getText().toString();

        et_tote_id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (GetScanresult()) {
                        et_tote_id.requestFocus();
                        et_tote_id.setFocusable(true);
                        strflg = true;
                        return true;
                    } else {
                        et_tote_id.requestFocus();
                        et_tote_id.setFocusable(true);
                        return true;
                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_tote_id.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        });

        et_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        if (Updateitemcode()) {
                            strflg = true;
                            totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
                            reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
                            NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
                            return true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }
        });

        bt_tote_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GetScanresult()) {
                    et_tote_id.requestFocus();
                    et_tote_id.setFocusable(true);
                    strflg = true;

                } else {
                    et_tote_id.requestFocus();
                    et_tote_id.setFocusable(true);
                }
            }
        });

        bt_remove_itemcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Updateitemcode()) {
                        strflg = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        bt_clear_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clear();
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

        bt_status_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (objUpdateboxItem.size() >= 1 && toteid != "") {

                        if (objUpdateBoxQuantityControl.InsertDetails(toteid, itemcode)) {
                            Log.e("return", "Build");
                            okMessage("SUCCESS", "Successfully Updated Box Quantity", getContext());
                            clear();
                            lv_div_seperate_details.setAdapter(null);
                        } else {
                            okMessage("Error", objGlobal.getErrorMessage(), getContext());
                        }
                    } else {
                        Log.e("return", "Not Build");
                        okMessage("Alert", "Please Scan Toteid", getContext());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String RadioOption = rb.getText().toString();
                if (RadioOption == "Remove item Qty") {
                    Radioselect = "Remove";
                } else {
                    Radioselect = "Add";
                }

            }
        });
        return view;
    }

    public boolean GetScanresult() {
        toteid = et_tote_id.getText().toString();
        Log.e("transferno", toteid);
        if (et_tote_id.getText().toString().isEmpty()) {
            okMessage("Alert", "Please Scan Trf no/Toteid", getContext());
            return false;
        } else {

            objUpdateboxItem = objUpdateBoxQuantityControl.ScanToteId(getContext(), toteid);
            et_tote_id.requestFocus();

            if (objUpdateboxItem == null) {
                et_tote_id.requestFocus();
                et_tote_id.setEnabled(true);
                et_tote_id.setText("");
                return false;
            } else {
                objMyUpdateBoxQuantity = new MyUpdateBoxQuantity(objUpdateboxItem);
                lv_div_seperate_details.setAdapter(objMyUpdateBoxQuantity);
                et_tote_id.setEnabled(false);
                et_itemcode.requestFocus();
                totalBoxQty.setText(objUpdateBoxQuantityControl.GetTotalQty(toteid) + "");
                reducedQty.setText(objUpdateBoxQuantityControl.ReducedQty(toteid) + "");
                NewBoxTotal.setText(objUpdateBoxQuantityControl.NewBoxQty(toteid) + "");
                return true;

            }
        }
    }

    public boolean Updateitemcode() throws SQLException {
        toteid = et_tote_id.getText().toString();
        itemcode = et_itemcode.getText().toString();
        Log.e("itemcode", itemcode);
        if (et_itemcode.getText().toString().isEmpty() || et_tote_id.getText().toString().isEmpty()) {
            okMessage("Alert", "Please Scan ItemCode", getContext());
            return false;
        } else {
            if (Radioselect == "") {
                Radioselect = "Remove";
            }
            objUpdateboxItem = objUpdateBoxQuantityControl.ScanItemCode(getContext(), toteid, itemcode, Radioselect);
            objMyUpdateBoxQuantity = new MyUpdateBoxQuantity(objUpdateboxItem);
            lv_div_seperate_details.setAdapter(objMyUpdateBoxQuantity);
            et_itemcode.setText("");

        }
        return true;
    }

    private class MyUpdateBoxQuantity extends BaseAdapter {
        public ArrayList<UpdateBoxItem> UpdateBoxItem;

        public MyUpdateBoxQuantity(ArrayList<UpdateBoxItem> UpdateBoxItem) {
            this.UpdateBoxItem = UpdateBoxItem;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return UpdateBoxItem.size();
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
            View myView = mInflater.inflate(R.layout.itemcode_quantity_details, null);
            final UpdateBoxItem s = UpdateBoxItem.get(position);
            TextView tv_itemcode = (TextView) myView.findViewById(R.id.tv_itemcode);
            tv_itemcode.setText(String.valueOf(s.ItemCode));
            TextView tv_BoxQuantity = (TextView) myView.findViewById(R.id.tv_BoxQuantity);
            tv_BoxQuantity.setText(String.valueOf(s.BoxQuantity));
            TextView tv_qty = (TextView) myView.findViewById(R.id.tv_qty);
            if (Integer.parseInt(s.Qty) < 0) {
                String styledText = "<font color='red'>" + s.Qty + "</font>";
                tv_qty.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            } else if (Integer.parseInt(s.Qty) > 0) {
                String styledText = "<font color='#008a25'>" + s.Qty + "</font>";
                tv_qty.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            } else {
                tv_qty.setText(String.valueOf(s.Qty));
            }
            TextView new_qty = (TextView) myView.findViewById(R.id.new_qty);
            if (Integer.parseInt(s.UpdatedBoxQuantity) > 0) {
                new_qty.setText(Html.fromHtml("<b>" + s.UpdatedBoxQuantity + "</b>"), TextView.BufferType.SPANNABLE);
            } else
                new_qty.setText(String.valueOf(s.UpdatedBoxQuantity));
            if (Integer.parseInt(s.Qty) < 0 && Integer.parseInt(s.UpdatedBoxQuantity) == 0) {
                String styledText = "<s><font color='blue'>" + s.ItemCode + "</font></s>";
                tv_itemcode.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

                String boxqty = "<s><font color='blue'>" + s.BoxQuantity + "</font></s>";
                tv_BoxQuantity.setText(Html.fromHtml(boxqty), TextView.BufferType.SPANNABLE);

                String qty = "<s><font color='blue'>" + s.BoxQuantity + "</font></s>";
                tv_qty.setText(Html.fromHtml(qty), TextView.BufferType.SPANNABLE);

                String newqty = "<s><font color='blue'>" + s.UpdatedBoxQuantity + "</font></s>";
                new_qty.setText(Html.fromHtml(newqty), TextView.BufferType.SPANNABLE);
            }
            return myView;
        }
    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private void clear() {
        et_tote_id.clearFocus();
        et_itemcode.setText("");
        et_tote_id.setText("");
        toteid = "";
        itemcode = "";
        UpdateBoxSharedRef.saveToteid("");
        if (objUpdateBoxQuantityControl.deletetemp()) {
            lv_div_seperate_details.setAdapter(null);
        }
        et_tote_id.setEnabled(true);
        et_tote_id.requestFocus();
        totalBoxQty.setText("");
        reducedQty.setText("");
        NewBoxTotal.setText("");
        radioGroup.check(R.id.remove);
    }
}