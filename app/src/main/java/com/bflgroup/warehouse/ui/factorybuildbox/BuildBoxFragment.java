package com.bflgroup.warehouse.ui.factorybuildbox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

public class BuildBoxFragment extends Fragment {

    private Button scan_itemcode;
    private Button bt_clear_box;
//    private Button bt_scan_itemcode;
   // private Button bt_save_box;
    private Button bt_good_toteid;
    private Button bt_asis_toteid;
    private Global objGlobal = Global.getInstance();
    private Button bt_writeoff_toteid;

    private EditText et_popup_itemcode;
    private TextView tv_count_good;
    private TextView tv_count_asis;
    private TextView tv_count_writeoff;

    private EditText tv_good_toteid;
    private EditText tv_asis_toteid;
    private EditText tv_writeoff_toteid;
    private Button bt_build_popup_ok;
    private Button tv_good;
    private Button tv_asis;
    private Button tv_writeoff;
    private ListView lv_details;
    private TextView tv_remarks;
    private EditText et_remarks;
    private BuildBoxControl objBuildBoxControl;
    MyTransferStatusAdp objTransferStatusAdp = null;
    ArrayList<BuildBoxitem> getitemdetails = new ArrayList<BuildBoxitem>();
    private BuildBoxGlobal objBuildBoxGlobal = BuildBoxGlobal.getInstance();
    BuildBoxShared ObjBuildBoxShared;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_build_box_barcode, container, false);

       scan_itemcode = (Button) view.findViewById(R.id.bt_scan_newbarcode);
        lv_details = (ListView) view.findViewById(R.id.lv_building_details);
        objBuildBoxControl = new BuildBoxControl(getContext());
        tv_count_good = (TextView) view.findViewById(R.id.tv_count_good);
        tv_count_asis = (TextView) view.findViewById(R.id.tv_count_asis);
        tv_count_writeoff = (TextView) view.findViewById(R.id.tv_count_writeoff);

        tv_good_toteid = (EditText) view.findViewById(R.id.tv_good_toteid);
        tv_asis_toteid = (EditText) view.findViewById(R.id.tv_asis_toteid);
        tv_writeoff_toteid = (EditText) view.findViewById(R.id.tv_writeoff_toteid);

        ObjBuildBoxShared = new BuildBoxShared(getContext());

        bt_clear_box = (Button) view.findViewById(R.id.bt_clear_box);
      //  bt_save_box = (Button) view.findViewById(R.id.bt_save_box);
        bt_good_toteid = (Button) view.findViewById(R.id.bt_good_toteid);
        bt_asis_toteid = (Button) view.findViewById(R.id.bt_asis_toteid);
        bt_writeoff_toteid = (Button) view.findViewById(R.id.bt_writeoff_toteid);
        et_remarks = (EditText) view.findViewById(R.id.et_remarks);


        if(!ObjBuildBoxShared.loadGoodTote().equals("")) {
            tv_good_toteid.setText(ObjBuildBoxShared.loadGoodTote());
            bt_good_toteid.setText("CheckOut");
            tv_good_toteid.setEnabled(false);
        }
        if(!ObjBuildBoxShared.loadAsisTote().equals("")){
            tv_asis_toteid.setText(ObjBuildBoxShared.loadAsisTote());
            bt_asis_toteid.setText("CheckOut");
            tv_asis_toteid.setEnabled(false);

        }
        if(!ObjBuildBoxShared.loadwriteoffTote().equals("")){
            tv_writeoff_toteid.setText(ObjBuildBoxShared.loadwriteoffTote());
            bt_writeoff_toteid.setText("CheckOut");
            tv_writeoff_toteid.setEnabled(false);
        }

        scan_itemcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!tv_good_toteid.getText().toString().trim().equals("") || !tv_asis_toteid.getText().toString().trim().equals("")  || !tv_writeoff_toteid.getText().toString().trim().equals("") )  {
                    openPopupWindow();
                }else{
                    okMessage("Alert", "Please Scan Toteid First", getContext());
                }
            }
        });

        tv_good_toteid.setOnTouchListener(new View.OnTouchListener() {
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
        tv_asis_toteid.setOnTouchListener(new View.OnTouchListener() {
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
        tv_writeoff_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        bt_good_toteid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if (bt_good_toteid.getText().toString().equals("CheckOut")) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Are you sure you want to checkout Good?")
                                    .setTitle("Confirmation")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (objBuildBoxControl.SaveBox(tv_good_toteid.getText().toString().trim(), "Good", getContext(), et_remarks.getText().toString())) {
                                                objBuildBoxControl.ClearItem("good", getContext());

                                                objTransferStatusAdp.notifyDataSetChanged();
                                                getitemdetails = objBuildBoxControl.checkitems();
                                                objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                                                lv_details.setAdapter(objTransferStatusAdp);

                                                okMessage("Success", "Box Build Successfully, Box:"+objBuildBoxGlobal.getBoxNo()+"", getContext());
                                                bt_good_toteid.setText("CheckIN");
                                                tv_good_toteid.setEnabled(true);
                                                tv_good_toteid.setText("");
                                                et_remarks.setText("");
                                                ObjBuildBoxShared.saveGoodTote("");

                                                tv_count_good.setText("0");


                                            }
                                            else {
                                                bt_good_toteid.setText("CheckOut");
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();


                        }else {
                            if (validateTote("Good")) {
                                if (!tv_good_toteid.getText().toString().trim().equals("")) {
                                    ObjBuildBoxShared.saveGoodTote(tv_good_toteid.getText().toString().trim());
                                    tv_good_toteid.setEnabled(false);
                                    bt_good_toteid.setText("CheckOut");
                                }else {
                                    okMessage("Alert", "Please scan toteid for Good", getContext());
                                }
                            }
                        }


                }


        });

        bt_asis_toteid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if (bt_asis_toteid.getText().toString().equals("CheckOut")) {


                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Are you sure you want to checkout AS IS?")
                                    .setTitle("Confirmation")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (objBuildBoxControl.SaveBox(tv_asis_toteid.getText().toString().trim(), "Asis", getContext(), et_remarks.getText().toString())) {
                                                objBuildBoxControl.ClearItem("asis", getContext());
                                                objTransferStatusAdp.notifyDataSetChanged();
                                                bt_asis_toteid.setText("CheckIN");
                                                tv_asis_toteid.setEnabled(true);
                                                tv_count_asis.setText("0");
                                                et_remarks.setText("");
                                                getitemdetails = objBuildBoxControl.checkitems();
                                                objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                                                lv_details.setAdapter(objTransferStatusAdp);
                                                tv_asis_toteid.setText("");
                                                ObjBuildBoxShared.saveAsisTote("");

                                                okMessage("Success", "Pallet Build Successfully, Box:"+objBuildBoxGlobal.getBoxNo()+" and Palletno:"+objBuildBoxGlobal.getPalletNo() , getContext());
                                            }
                                            else {
                                                bt_asis_toteid.setText("CheckOut");
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();

                        }else {

                            if (validateTote("Asis")) {
                                if (!tv_asis_toteid.getText().toString().trim().equals("")) {
                                    ObjBuildBoxShared.saveAsisTote(tv_asis_toteid.getText().toString().trim());
                                    tv_asis_toteid.setEnabled(false);
                                    bt_asis_toteid.setText("CheckOut");
                                }else {
                                    okMessage("Alert", "Please scan toteid for As Is", getContext());
                                }

                            }

                        }



            }
        });

        bt_writeoff_toteid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(bt_writeoff_toteid.getText().toString().equals("CheckOut")){



                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are you sure you want to checkout Writeoff?")
                                .setTitle("Confirmation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(objBuildBoxControl.SaveBox(tv_writeoff_toteid.getText().toString().trim(), "Writeoff",getContext(), et_remarks.getText().toString())){
                                            objBuildBoxControl.ClearItem("writeoff", getContext());

                                            objTransferStatusAdp.notifyDataSetChanged();
                                            bt_writeoff_toteid.setText("CheckIN");
                                            tv_writeoff_toteid.setEnabled(true);
                                            tv_writeoff_toteid.setText("");
                                            et_remarks.setText("");
                                            tv_count_writeoff.setText("0");
                                            getitemdetails = objBuildBoxControl.checkitems();
                                            objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                                            lv_details.setAdapter(objTransferStatusAdp);

                                            okMessage("Success", "Pallet Build Successfully, Box:"+objBuildBoxGlobal.getBoxNo()+" and Palletno:"+objBuildBoxGlobal.getPalletNo() , getContext());
                                            ObjBuildBoxShared.saveWriteoffTote("");

                                        }
                                        else{
                                            bt_writeoff_toteid.setText("CheckOut");
                                            okMessage("ALERT", objGlobal.getErrorMessage(), getContext());

                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();



                    }else {
                        if (validateTote("writeoff")) {
                            if (!tv_writeoff_toteid.getText().toString().trim().equals("")) {
                                ObjBuildBoxShared.saveWriteoffTote(tv_writeoff_toteid.getText().toString().trim());
                                tv_writeoff_toteid.setEnabled(false);
                                bt_writeoff_toteid.setText("CheckOut");
                            }else {
                                okMessage("Alert", "Please scan toteid for Writeoff", getContext());
                            }
                        }

                    }
                }



        });



        bt_clear_box.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure you want to clear All?")
                            .setTitle("Confirmation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if( objBuildBoxControl.ClearAll(getContext())) {

                                        objTransferStatusAdp = null;
                                        lv_details.setAdapter(null);
                                        tv_count_good.setText("0");
                                        tv_count_asis.setText("0");
                                        tv_count_writeoff.setText("0");
                                        objBuildBoxGlobal.setGoodsCount(0);
                                        objBuildBoxGlobal.setAsisCount(0);
                                        objBuildBoxGlobal.setWriteoffCount(0);

                                        tv_asis_toteid.setText("");
                                        tv_good_toteid.setText("");
                                        tv_writeoff_toteid.setText("");

                                        tv_asis_toteid.setEnabled(true);
                                        tv_good_toteid.setEnabled(true);
                                        tv_writeoff_toteid.setEnabled(true);
                                        et_remarks.setText("");
                                        bt_writeoff_toteid.setText("CHECK IN");
                                        bt_asis_toteid.setText("CHECK IN");
                                        bt_good_toteid.setText("CHECK IN");

                                        ObjBuildBoxShared.saveGoodTote("");
                                        ObjBuildBoxShared.saveWriteoffTote("");
                                        ObjBuildBoxShared.saveAsisTote("");



                                    }else{
                                        okMessage("ALert", "Not cleared ", getContext());
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
        getitemdetails = objBuildBoxControl.checkitems();
        objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
        lv_details.setAdapter(objTransferStatusAdp);

        tv_count_good.setText(objBuildBoxGlobal.getGoodsCount().toString());
        tv_count_asis.setText(objBuildBoxGlobal.getAsisCount().toString());
        tv_count_writeoff.setText(objBuildBoxGlobal.getWriteoffCount().toString());
        return view;
    }




    private void openPopupWindow() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.scan_itemcode);
        bt_build_popup_ok = (Button) myDialog.findViewById(R.id.bt_build_popup_ok);
        tv_remarks = (TextView) myDialog.findViewById(R.id.tv_remarks);
//        bt_scan_itemcode = (Button) myDialog.findViewById(R.id.bt_scan_itemcode);
        et_popup_itemcode = (EditText) myDialog.findViewById(R.id.et_popup_itemcode);
        tv_good = (Button) myDialog.findViewById(R.id.tv_good);
        tv_asis = (Button) myDialog.findViewById(R.id.tv_asis);
        tv_writeoff = (Button) myDialog.findViewById(R.id.tv_writeoff);


        et_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
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


//        et_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.onTouchEvent(motionEvent);
//                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                return objGlobal.getHideKeyPad();
//            }
//        });
//        bt_scan_itemcode.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if(validateItem()){
//
//                    et_popup_itemcode.requestFocus();
//                    et_popup_itemcode.setText("");
//                }else {
//                    et_popup_itemcode.requestFocus();
//                    et_popup_itemcode.setText("");
//                }
//            }
//        });


        tv_good.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                if(validateItem()){
                    getitemdetails = objBuildBoxControl.insertitemCode(et_popup_itemcode.getText().toString().trim(), "Good", tv_good_toteid.getText().toString().trim(), getContext());
                    objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                    lv_details.setAdapter(objTransferStatusAdp);

                    tv_count_good.setText(objBuildBoxGlobal.getGoodsCount().toString());
                    tv_remarks.setText(et_popup_itemcode.getText().toString().trim() + "Added Succesfully");


                    et_popup_itemcode.requestFocus();
                    et_popup_itemcode.setText("");

                }else {
                    et_popup_itemcode.requestFocus();
                    et_popup_itemcode.setText("");
                }
            }
            catch (Exception e){
                    okMessage("Alert", e.toString(),getContext());
            }
            }
        });
        tv_asis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    if (validateItem()) {


                        getitemdetails = objBuildBoxControl.insertitemCode(et_popup_itemcode.getText().toString().trim(), "Asis", tv_asis_toteid.getText().toString().trim(), getContext());
                        objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                        lv_details.setAdapter(objTransferStatusAdp);
                        tv_count_asis.setText(objBuildBoxGlobal.getAsisCount().toString());
                        tv_remarks.setText(et_popup_itemcode.getText().toString().trim() + " - Added Succesfully");
                        et_popup_itemcode.requestFocus();
                        et_popup_itemcode.setText("");


                    } else {
                        et_popup_itemcode.requestFocus();
                        et_popup_itemcode.setText("");
                    }
                }
                 catch (Exception e){
                        okMessage("Alert", e.toString(),getContext());
                    }
            }
        });

        tv_writeoff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    if (validateItem()) {

                        getitemdetails = objBuildBoxControl.insertitemCode(et_popup_itemcode.getText().toString().trim(), "writeoff",tv_writeoff_toteid.getText().toString().trim(), getContext());
                        objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                        lv_details.setAdapter(objTransferStatusAdp);
                        tv_count_writeoff.setText(objBuildBoxGlobal.getWriteoffCount().toString());
                        tv_remarks.setText(et_popup_itemcode.getText().toString().trim() + " - Added Succesfully");
                        et_popup_itemcode.requestFocus();
                        et_popup_itemcode.setText("");


                    } else {
                        et_popup_itemcode.requestFocus();
                        et_popup_itemcode.setText("");
                    }
                }
                catch (Exception e){
                        okMessage("Alert", e.toString(),getContext());
                    }
            }
        });
        bt_build_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }


    public Boolean validateItem(){

        String itemcode = et_popup_itemcode.getText().toString().trim();
        if(!itemcode.isEmpty()) {
            if (objBuildBoxControl.checkitemcode(itemcode, getContext())) {
            return true;
            } else {

            }
        }
        return false;
    }

    public Boolean validateTote(String Status){
        String toteid;

            if (Status.equals("Good")) {
                toteid = tv_good_toteid.getText().toString().trim();
            } else if (Status.equals("Asis")) {
                toteid = tv_asis_toteid.getText().toString().trim();
            } else {
                toteid = tv_writeoff_toteid.getText().toString().trim();
            }
            if (!toteid.isEmpty()) {
                if (objBuildBoxControl.checkTote(toteid, getContext(), Status)) {
                    return true;
                } else {
                    return false;
                }
            }

        return false;
    }

    public Boolean ClearAll(){

        String itemcode = et_popup_itemcode.getText().toString().trim();
        if(!itemcode.isEmpty()) {
            if (objBuildBoxControl.checkitemcode(itemcode, getContext())) {
                return true;
            } else {
                okMessage("Alert", "No New Barcode found - " + itemcode, getContext());
            }
        }
        return false;
    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }


    private class MyTransferStatusAdp extends BaseAdapter {
        public ArrayList<BuildBoxitem> BuildboxItemList;

        public MyTransferStatusAdp(ArrayList<BuildBoxitem> BuildBoxitem) {
            this.BuildboxItemList = BuildBoxitem;
        }

        @Override
        public int getCount() {
            return BuildboxItemList.size();
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
            View myView = mInflater.inflate(R.layout.generate_box_item_details, null);
            final BuildBoxitem s = BuildboxItemList.get(position);
            TextView tv_upc = (TextView) myView.findViewById(R.id.tv_upc);
            tv_upc.setText(String.valueOf(s.Upc));
            TextView tv_generate_barcode = (TextView) myView.findViewById(R.id.tv_generate_barcode);
            tv_generate_barcode.setText(String.valueOf(s.GeneratedBarcode));
            TextView tv_status = (TextView) myView.findViewById(R.id.tv_status);
            tv_status.setText(String.valueOf(s.Status));
            Button Delete = (Button) myView.findViewById(R.id.tv_delete);
            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String GeneratedBarcode = s.GeneratedBarcode.toString();
                    Log.e("UPC", GeneratedBarcode);

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure you want to Delete this upc - ?"+GeneratedBarcode)
                            .setTitle("Confirmation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(objBuildBoxControl.deleteitemcode(GeneratedBarcode)){
                                       // lv_details.notifyAll();
                                        notifyDataSetChanged();
                                        objTransferStatusAdp.notifyDataSetChanged();
                                        getitemdetails = objBuildBoxControl.checkitems();
                                        objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
                                        lv_details.setAdapter(objTransferStatusAdp);



                                        tv_count_good.setText(objBuildBoxGlobal.getGoodsCount().toString());
                                        tv_count_asis.setText(objBuildBoxGlobal.getAsisCount().toString());
                                        tv_count_writeoff.setText(objBuildBoxGlobal.getWriteoffCount().toString());

                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();


//                    getitemdetails = objBuildBoxControl.checkitems();
//                    objTransferStatusAdp = new MyTransferStatusAdp(getitemdetails);
//                    lv_details.setAdapter(objTransferStatusAdp);
                }
            });
            return myView;
        }
    }
}
