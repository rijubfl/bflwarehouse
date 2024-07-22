package com.bflgroup.warehouse.ui.generatebarcode;

import android.content.Context;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.printclass.SunmiPrintHelper;

import java.util.ArrayList;

public class GenerateBarcodeFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private EditText et_div_boxNo;
    private EditText et_div_scan_upc;
    private Button bt_generate_barcode ;
    private GenerateBarcodeControl objGenerateBarcodeControl;
    ArrayList<BarcodeGeneratedItem> generateBarcodeItemStatus = new ArrayList<BarcodeGeneratedItem>();
    private ListView lv_details;
    MyTransferStatusAdp objTransferStatusAdp = null;
    GenerateBarcodePrint objGenerateBarcodePrint = new GenerateBarcodePrint();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_generate_barcode, container, false);

        et_div_boxNo = (EditText) view.findViewById(R.id.et_div_boxNo);
        et_div_scan_upc = (EditText) view.findViewById(R.id.et_div_scan_upc);
        bt_generate_barcode = (Button) view.findViewById(R.id.bt_generate_barcode);
        lv_details = (ListView) view.findViewById(R.id.lv_details);

        objGenerateBarcodeControl = new GenerateBarcodeControl(getContext());
        SunmiPrintHelper.getInstance().initSunmiPrinterService(getContext());

        et_div_boxNo.setOnTouchListener(new View.OnTouchListener() {
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

        et_div_boxNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(ValidBoxNo()){
                        et_div_boxNo.setEnabled(false);
                        et_div_scan_upc.requestFocus();
                        et_div_scan_upc.setFocusable(true);
                       // strflg = true;
                        return true;
                    }else {
                        et_div_boxNo.setText("");
                        et_div_boxNo.setEnabled(true);
                        et_div_boxNo.requestFocus();
                        return false;
                    }
                }
                return false;
            }
        });

        et_div_scan_upc.setOnTouchListener(new View.OnTouchListener() {
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

        et_div_scan_upc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(ValidItemCode()){
                        et_div_boxNo.setEnabled(false);
                        et_div_scan_upc.requestFocus();
                        et_div_scan_upc.setFocusable(true);
                        // strflg = true;
                        return true;
                    }else {
                        et_div_boxNo.setText("");
                        et_div_boxNo.setEnabled(true);
                        et_div_boxNo.requestFocus();
                        return false;
                    }
                }
                return false;
            }
        });

        bt_generate_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    objGenerateBarcodePrint.printGenerateBarcode("ERTN/23/00006");
                    //generateBarcodeItemStatus = objGenerateBarcodeControl.UpdateBarcode(et_div_scan_upc.getText().toString(), et_div_boxNo.getText().toString());
                    //objTransferStatusAdp = new MyTransferStatusAdp(generateBarcodeItemStatus);
                    //lv_details.setAdapter(objTransferStatusAdp);
                }catch(Exception e){
                    okMessage("Alert", e.getMessage(), getContext());
                }
            }
        });

        return view;
    }

    void okMessage(String title, String message, Context context) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public Boolean ValidBoxNo(){
        if(!et_div_boxNo.getText().toString().isEmpty()){
            if(objGenerateBarcodeControl.validateBoxno(et_div_boxNo.getText().toString())){
                et_div_scan_upc.setText("");
                et_div_scan_upc.requestFocus();
            }else{
                okMessage("ALERT", "Please enter valid boxno - " +et_div_boxNo.getText().toString(), getContext());
                et_div_boxNo.setText("");
                et_div_boxNo.requestFocus();
            }
        }
          return false;
    }

    public Boolean ValidItemCode(){
        if(ValidBoxNo()){
            if(!et_div_scan_upc.getText().toString().isEmpty()){
                if(objGenerateBarcodeControl.validateBoxItemcode(et_div_scan_upc.getText().toString(), et_div_boxNo.getText().toString())){
                    et_div_scan_upc.setText("");
                    et_div_scan_upc.requestFocus();
                }else{
                    okMessage("Alert", "Item("+et_div_scan_upc.getText().toString()+") is Not found in this Boxno - "+et_div_boxNo.getText().toString()+" ",getContext());
                    et_div_scan_upc.setText("");
                    et_div_scan_upc.requestFocus();
                }
            }
        }else {
            if (!et_div_scan_upc.getText().toString().isEmpty()) {
                 if(objGenerateBarcodeControl.validateItemcode(et_div_scan_upc.getText().toString())){
                     et_div_scan_upc.setText("");
                     et_div_scan_upc.requestFocus();
                }else{
                     okMessage("Alert","Please scan valid UPC"+et_div_scan_upc.getText().toString(), getContext());
                     et_div_scan_upc.setText("");
                     et_div_scan_upc.requestFocus();
                 }
            }
        }
        return false;
    }

    private class MyTransferStatusAdp extends BaseAdapter {
        public  ArrayList<BarcodeGeneratedItem> BarcodeGeneratedItemList;

        public MyTransferStatusAdp(ArrayList<BarcodeGeneratedItem> BarcodeGeneratedItem) {
            this.BarcodeGeneratedItemList = BarcodeGeneratedItem;
        }

        @Override
        public int getCount() {
            return BarcodeGeneratedItemList.size();
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
            View myView = mInflater.inflate(R.layout.generate_barcode_item_details, null);
            final BarcodeGeneratedItem s = BarcodeGeneratedItemList.get(position);
            TextView tv_date = (TextView) myView.findViewById(R.id.tv_date);
            tv_date.setText(String.valueOf(s.Date));
            TextView tv_upc = (TextView) myView.findViewById(R.id.tv_upc);
            tv_upc.setText(String.valueOf(s.Upc));
            TextView tv_generate_barcode = (TextView) myView.findViewById(R.id.tv_generate_barcode);
            tv_generate_barcode.setText(String.valueOf(s.GeneratedBarcode));
            return myView;
        }
    }
}