package com.bflgroup.warehouse.ui.palletstatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class PalletStatusFragment extends Fragment {

    private ListView lv_building_del_pallet_details;
    private EditText et_pallet_status_palletno;
    private TextView tv_pallet_status_message;
    private Button bt_pallet_status_scan;
    private TextView tv_pallet_status_result;
    private TextView tv_pallet_status_last_scan;
    private TextView tv_pallet_status_last_building_category;
    private TextView tv_pallet_status_last_palletno;
    private TextView tv_pallet_status_last_pallet_boxno;
    private TextView tv_pallet_status_last_pallet_toteid;
    private TextView tv_pallet_status_last_pallet_pallettype;
    private TextView tv_pallet_status_last_pallet_checking_type;
    private TextView tv_pallet_status_last_building_golden;
    private Spinner sp_rack_in_out_warehouse;
    private boolean b_Result;

    private Global objGlobal = Global.getInstance();
    private PalletStatusGlobal objPalletStatusGlobal = PalletStatusGlobal.getInstance();
    private PalletStatusControl objPalletStatusControl = new PalletStatusControl();
    PalletStatusFragment.MyPalletStatusAdp objMyPalletStatusAdp;
    List<String> arr;

    public PalletStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pallet_status, container, false);

        lv_building_del_pallet_details = (ListView) view.findViewById(R.id.lv_building_del_pallet_details);
        sp_rack_in_out_warehouse = (Spinner) view.findViewById(R.id.sp_rack_in_out_warehouse);
        et_pallet_status_palletno = (EditText) view.findViewById(R.id.et_pallet_status_palletno);
        tv_pallet_status_message = (TextView) view.findViewById(R.id.tv_pallet_status_message);
        bt_pallet_status_scan = (Button) view.findViewById(R.id.bt_pallet_status_scan);
        tv_pallet_status_last_scan = (TextView) view.findViewById(R.id.tv_pallet_status_last_scan);
        tv_pallet_status_last_building_category = (TextView) view.findViewById(R.id.tv_pallet_status_last_building_category);
        tv_pallet_status_result = (TextView) view.findViewById(R.id.tv_pallet_status_result);
        tv_pallet_status_last_palletno = (TextView) view.findViewById(R.id.tv_pallet_status_last_palletno);
        tv_pallet_status_last_pallet_boxno = (TextView) view.findViewById(R.id.tv_pallet_status_last_pallet_boxno);
        tv_pallet_status_last_pallet_toteid = (TextView) view.findViewById(R.id.tv_pallet_status_last_pallet_toteid);
        tv_pallet_status_last_pallet_pallettype = (TextView) view.findViewById(R.id.tv_pallet_status_last_pallet_pallettype);
        tv_pallet_status_last_pallet_checking_type = (TextView) view.findViewById(R.id.tv_pallet_status_last_pallet_checking_type);
        tv_pallet_status_last_building_golden = (TextView) view.findViewById(R.id.tv_pallet_status_last_building_golden);

        ArrayList<PalletStatusTicket> listPalletStatus = objPalletStatusControl.loadPalletStatus();
        objMyPalletStatusAdp = new PalletStatusFragment.MyPalletStatusAdp(listPalletStatus);
        lv_building_del_pallet_details.setAdapter(objMyPalletStatusAdp);
        arr = new ArrayList<String>();
        et_pallet_status_palletno.setOnTouchListener(new View.OnTouchListener() {
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




        et_pallet_status_palletno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(sp_rack_in_out_warehouse.getSelectedItemId() == 0){
                        objGlobal.setErrorMessage("Please select the Warehouse!");
                        okMessage("Alert", "Please select the Warehouse!");
                    }else {
                        loadStatus();
                    }
                }
                return false;
            }
        });
        String warehouse = objGlobal.getWarehouse();
        //arr.add(warehouse);

        List<String> arr = objPalletStatusControl.getWarehouse(warehouse);
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_rack_in_out_warehouse.setAdapter(arrayAdp);
        bt_pallet_status_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp_rack_in_out_warehouse.getSelectedItemId() == 0){
                    objGlobal.setErrorMessage("Please select the Warehouse!");
                    okMessage("Alert", "Please select the Warehouse!");
                }else {
                    loadStatus();
                }
            }
        });
        et_pallet_status_palletno.requestFocus();
        return view;
    }

    private void loadStatus(){
        new PalletStatusFragment.LoadStatus().execute();
    }

    private class MyPalletStatusAdp extends BaseAdapter {
        public ArrayList<PalletStatusTicket> listPalletStatusTicket;

        public MyPalletStatusAdp(ArrayList<PalletStatusTicket> listPalletStatusTicket) {
            this.listPalletStatusTicket = listPalletStatusTicket;
        }

        @Override
        public int getCount() {
            return listPalletStatusTicket.size();
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
            View myView = mInflater.inflate(R.layout.pallet_tatus_scan_ticket, null);
            final PalletStatusTicket s = listPalletStatusTicket.get(position);
            TextView tv_pallet_status_ticket_palletno = (TextView) myView.findViewById(R.id.tv_pallet_status_ticket_palletno);
            tv_pallet_status_ticket_palletno.setText(String.valueOf(s.palletno));
            TextView tv_pallet_status_ticket_status = (TextView) myView.findViewById(R.id.tv_pallet_status_ticket_status);
            tv_pallet_status_ticket_status.setText(String.valueOf(s.status));
            TextView tv_pallet_status_ticket_datetime = (TextView) myView.findViewById(R.id.tv_pallet_status_ticket_datetime);
            tv_pallet_status_ticket_datetime.setText(String.valueOf(s.dateTime));
            return myView;
        }
    }

    private class LoadStatus extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        String scan = et_pallet_status_palletno.getText().toString().trim().toUpperCase();

        public LoadStatus() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading Status, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                tv_pallet_status_last_scan.setText(scan);
                tv_pallet_status_result.setText("");
                tv_pallet_status_message.setText("");
                tv_pallet_status_last_pallet_boxno.setText("");
                tv_pallet_status_last_pallet_toteid.setText("");
                tv_pallet_status_last_palletno.setText("");
                tv_pallet_status_last_pallet_pallettype.setText("");
                tv_pallet_status_last_building_category.setText("");
                tv_pallet_status_last_pallet_checking_type.setText("");
                tv_pallet_status_last_building_golden.setText("");
                b_Result = objPalletStatusControl.getPalletStatus(scan, sp_rack_in_out_warehouse.getSelectedItem().toString());
                if (b_Result == false) {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                tv_pallet_status_message.setText(objGlobal.getErrorMessage());
            } else {
                tv_pallet_status_result.setText(objPalletStatusGlobal.getStatus());
                tv_pallet_status_last_pallet_boxno.setText(objPalletStatusGlobal.getBoxno());
                tv_pallet_status_last_pallet_toteid.setText(objPalletStatusGlobal.getToteid());
                tv_pallet_status_last_palletno.setText(objPalletStatusGlobal.getPalletno());
                tv_pallet_status_last_pallet_pallettype.setText(objPalletStatusGlobal.getPltType());
                tv_pallet_status_last_building_category.setText(objPalletStatusGlobal.getBuildcategory());
                tv_pallet_status_last_pallet_checking_type.setText(objPalletStatusGlobal.getCheckingType());
                tv_pallet_status_last_building_golden.setBackground(getActivity().getResources().getDrawable(R.drawable.et_custom));
                tv_pallet_status_last_building_golden.setText("");
                if(objPalletStatusGlobal.getGolden().equals("Y")){
                    tv_pallet_status_last_building_golden.setText("Golden Label");
                    tv_pallet_status_last_building_golden.setBackgroundColor(getActivity().getResources().getColor(R.color.colorGold));
                }
            }
            ArrayList<PalletStatusTicket> listPalletStatus = objPalletStatusControl.loadPalletStatus();
            objMyPalletStatusAdp = new PalletStatusFragment.MyPalletStatusAdp(listPalletStatus);
            lv_building_del_pallet_details.setAdapter(objMyPalletStatusAdp);
            et_pallet_status_palletno.setText("");
            et_pallet_status_palletno.requestFocus();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
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

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

}