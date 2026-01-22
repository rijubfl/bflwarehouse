package com.bflgroup.warehouse.ui.buildingdelpallet;

import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.getPltCount;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.getRouteid;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.setPltCount;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.setRouteid;
import static com.loopj.android.http.AsyncHttpClient.log;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.BarcodePrinting;
import com.bflgroup.warehouse.comm.BluetoothDevices;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.buildingdelpallet.models.ShopInfo;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

public class BuildingDeliveryPalletFragment extends Fragment {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private Vector<BluetoothDevice> remoteDevices;

    private BluetoothAdapter mBluetoothAdapter;
    private BuildingDeliveryPalletGlobal objpalletbuilding = BuildingDeliveryPalletGlobal.getInstance();
    private BuildingDeliveryPalletControl objbuildingdelPalletControl = new BuildingDeliveryPalletControl();
    private BluetoothPort bluetoothPort;
    private TextView tv_plt_route_id;
    private TextView tv_shopnames_col;
    private EditText et_plt_shop_transferno;
    private TextView tv_count;
    private Spinner sp_transfer_printer;
    private EditText Remarks;
    private TextView tv_plt_shopname;
    private Button bt_transfer_scan;
    private Button bt_shop_return_scan;
    private Button bt_div_Clear;
    private Button bt_status_build_plt;

    private BroadcastReceiver discoveryResult;
    private ListView lv_div_seperate_details;
    private String transferno = "";
    private int get_route_id;
    public boolean b_Result;
    String remark = "";
    Integer count = 0;
    String android_id;
    Boolean strflg = false;
    private static final int REQUEST_ENABLE_BT = 2;
    ArrayList<PalletScanDeliveryItem> PalletScanDeliveryItem = new ArrayList<PalletScanDeliveryItem>();
    MyTransferStatusPltAdp objTransferStatusPltAdp = null;
    PltScanTransferShared PltScanTransferShared;
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();
    //    private CheckBox ch_transfer_printer;
    private Spinner sp_transfer_print_copies;
    private Thread btThread;
    private BarcodePrinting objSample_Print;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver connectDevice;
    private BroadcastReceiver searchStart;

    private ArrayAdapter<String> adapter;
    private boolean searchflags;

    private boolean testPrint = false;
    private ListView lvShops;
    private Button btCancel;
    private List<ShopInfo> shopNamesFromTransfers;
    private ShopFromTransferAdp objShopFromTransferAdp;
    Dialog myDialog;
    private String get_shop_names;
    private ShopInfo selectedShopName ;

    public BuildingDeliveryPalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_delivery_pallet, container, false);

        tv_plt_route_id = (TextView) view.findViewById(R.id.tv_plt_route_id);
        tv_shopnames_col = (TextView) view.findViewById(R.id.tv_shopnames_col);
        et_plt_shop_transferno = (EditText) view.findViewById(R.id.et_plt_shop_transferno);
        Remarks = (EditText) view.findViewById(R.id.et_remarks);
        tv_plt_shopname = (TextView) view.findViewById(R.id.tv_plt_shopname);
        bt_transfer_scan = (Button) view.findViewById(R.id.bt_transfer_scan);
        // bt_shop_return_scan = (Button) view.findViewById(R.id.bt_shop_return_scan);
        bt_status_build_plt = (Button) view.findViewById(R.id.bt_status_build_plt);
        lv_div_seperate_details = (ListView) view.findViewById(R.id.lv_div_seperate_det);
        bt_div_Clear = (Button) view.findViewById(R.id.bt_status_clear);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        sp_transfer_printer = (Spinner) view.findViewById(R.id.sp_transfer_printer);
//        ch_transfer_printer = (CheckBox) view.findViewById(R.id.ch_transfer_printer);
        PltScanTransferShared = new PltScanTransferShared(getContext());
        sp_transfer_print_copies = (Spinner) view.findViewById(R.id.sp_transfer_print_copies);

        searchflags = false;
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Transfer", objGlobal.getErrorMessage(), getContext());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevices());
            sp_transfer_printer.setAdapter(arrayAdpYellow);
            if (PltScanTransferShared.loadPrinter() != "") {
                sp_transfer_printer.setSelection(arrayAdpYellow.getPosition(PltScanTransferShared.loadPrinter()));
            }
        }
        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("1");
        arr.add("2");
        arr.add("3");
        arr.add("4");

        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_transfer_print_copies.setAdapter(arrayAdp);
        if (PltScanTransferShared.loadPrintCopies().equals("")) {
            PltScanTransferShared.savePrintCopies("1");
        }
        sp_transfer_print_copies.setSelection(arrayAdp.getPosition(PltScanTransferShared.loadPrintCopies().toString()));


//        if (objGlobal.getCountryCode().equals("KSA")) {
//            List<String> arr1 = objbuildingdelPalletControl.loadKsaShops();
//            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
//            tv_plt_route_id.setAdapter(arrayAdp1);
//            if (PltScanTransferShared.Routeidload() != "") {
//                tv_plt_route_id.setSelection(arrayAdp1.getPosition(PltScanTransferShared.Routeidload().toString()));
//                tv_plt_route_id.setEnabled(false);
//                try {
//                    PalletScanDeliveryItem = objbuildingdelPalletControl.LoadPltData();
//                    count = Integer.valueOf(objbuildingdelPalletControl.LoadPltDataCount().toString());
//                    count = getPltCount();
//                    tv_count.setText(count + "");
//                    objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
//                    lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
//                    Log.e("item", "reached");
//                } catch (SQLException e) {
//                    Log.e("Log", e.toString());
//                }
//            }
//        } else {
//            List<Integer> arr1 = objbuildingdelPalletControl.loadRoute();
//            ArrayAdapter<Integer> arrayAdp1 = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
//            tv_plt_route_id.setText(arrayAdp1);
//            Log.e("ROUTEIDLOAD -- UAE", PltScanTransferShared.Routeidload());
//            if (PltScanTransferShared.Routeidload() != "") {
//                tv_plt_route_id.setSelection(arrayAdp1.getPosition(Integer.parseInt(PltScanTransferShared.Routeidload().toString())));
//                tv_plt_route_id.setEnabled(false);
//                try {
//                    PalletScanDeliveryItem = objbuildingdelPalletControl.LoadPltData();
//                    count = Integer.valueOf(objbuildingdelPalletControl.LoadPltDataCount().toString());
//                    count = Integer.valueOf(getPltCount());
//                    tv_count.setText(count + "");
//                    objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
//                    lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
//                    Log.e("item", "reached");
//                } catch (SQLException e) {
//                    Log.e("Log", e.toString());
//                }
//
//            }
        //    }

//        ch_transfer_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (buttonView.isChecked()) {
//                    //openPopupReprint();
//                } else {
//                    // not checked
//                }
//            }
//        });

//        tv_plt_route_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//                if (objGlobal.getCountryCode().equals("KSA")) {
//
//
//                    get_route_id = objbuildingdelPalletControl.loadKsaRoute(tv_plt_route_id.getSelectedItem().toString().split("\\(")[0]);
//                    String get_shop_names = objbuildingdelPalletControl.LoadShops(get_route_id);
//                    tv_shopnames_col.setText(get_shop_names);
//
//                } else {
//                    get_route_id = Integer.parseInt(tv_plt_route_id.getSelectedItem().toString());
//
//                    String get_shop_names = objbuildingdelPalletControl.LoadShops(get_route_id);
//                    tv_shopnames_col.setText(get_shop_names);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });

        et_plt_shop_transferno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_plt_shop_transferno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    //  if (getPltCount() == 0)
                    if (objbuildingdelPalletControl.transferCount(et_plt_shop_transferno.getText().toString().trim()) == 0) {
                        objGlobal.setErrorMessage("Transfer is Invalid");
                        okMessage("Message", objGlobal.getErrorMessage(), getActivity());
                        et_plt_shop_transferno.setText("");
                    } else if (PalletScanDeliveryItem != null && PalletScanDeliveryItem.size() != 0) {
                        boolean isRouteMatch = false;
                        List<ShopInfo> tempShopFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                      shopNamesFromTransfers = new ArrayList<>();
                      for (ShopInfo tempShop : tempShopFromTransfers){
                          int tempRouteId = objbuildingdelPalletControl.loadKsaRoute(tempShop.getShopName());
                          if (tempRouteId == PalletScanDeliveryItem.get(0).RouteId){
                              isRouteMatch = true;
                              shopNamesFromTransfers.add(tempShop);
                          }
                      }
                      if (isRouteMatch){
                          if (shopNamesFromTransfers.size() == 1)
                              trfDetail(shopNamesFromTransfers.get(0));
                          else
                              openPopupShopsFromTransfers();
                      }
                      else{
                          objGlobal.setErrorMessage("This Trf No / Toteid is not in the same Route - " + transferno);
                          okMessage("Message", objGlobal.getErrorMessage(), getActivity());
                          et_plt_shop_transferno.setText("");
                          return false;
                      }

                    } else if (objbuildingdelPalletControl.transferCount(et_plt_shop_transferno.getText().toString().trim()) == 1) {
                        shopNamesFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                        trfDetail(shopNamesFromTransfers.get(0));
                    } else
                    {
                        shopNamesFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                        if (shopNamesFromTransfers.size() == 1)
                            trfDetail(shopNamesFromTransfers.get(0));
                        else
                            openPopupShopsFromTransfers();
                    }
//                    else {
//                        if (PalletScanDeliveryItem != null && PalletScanDeliveryItem.size() != 0)
//                            get_route_id = objbuildingdelPalletControl.loadKsaRoute(PalletScanDeliveryItem.get(0).ShopName);
//                        GetScanresult();
//                    }
                    return true;
//                    if (GetScanresult()) {
//                        et_plt_shop_transferno.requestFocus();
//                        et_plt_shop_transferno.setFocusable(true);
//                        strflg = true;
//                        return true;
//                    } else {
//                        et_plt_shop_transferno.requestFocus();
//                        et_plt_shop_transferno.setFocusable(true);
//                        return true;
//                    }
                } else {
                    if (strflg) {
                        strflg = false;
                        return true;
                    } else {
                        if (i == 1011) {
                            et_plt_shop_transferno.setFocusable(true);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    //return false;
                }
            }


        });

        et_plt_shop_transferno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

//        et_plt_shop_transferno.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
//                    if (GetScanresult()) {
//                        et_plt_shop_transferno.requestFocus();
//                        et_plt_shop_transferno.setFocusable(true);
//                        strflg = true;
//                        return true;
//                    } else {
//                        et_plt_shop_transferno.requestFocus();
//                        et_plt_shop_transferno.setFocusable(true);
//                        return true;
//                    }
//                } else {
//                    if (strflg) {
//                        strflg = false;
//                        return true;
//                    } else {
//                        if (i == 1011) {
//                            et_plt_shop_transferno.setFocusable(true);
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                    //return false;
//                }
//            }
//
//
//        });

        bt_transfer_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objbuildingdelPalletControl.transferCount(et_plt_shop_transferno.getText().toString().trim()) == 0) {
                    objGlobal.setErrorMessage("Transfer is Invalid");
                    okMessage("Message", objGlobal.getErrorMessage(), getActivity());
                    et_plt_shop_transferno.setText("");
                } else if (PalletScanDeliveryItem != null && PalletScanDeliveryItem.size() != 0) {
                    boolean isRouteMatch = false;
                    List<ShopInfo> tempShopFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                    shopNamesFromTransfers = new ArrayList<>();
                    for (ShopInfo tempShop : tempShopFromTransfers){
                        int tempRouteId = objbuildingdelPalletControl.loadKsaRoute(tempShop.getShopName());
                        if (tempRouteId == PalletScanDeliveryItem.get(0).RouteId){
                            isRouteMatch = true;
                            shopNamesFromTransfers.add(tempShop);

                        }
                    }
                    if (isRouteMatch){
                        if (shopNamesFromTransfers.size() == 1)
                            trfDetail(shopNamesFromTransfers.get(0));
                        else
                            openPopupShopsFromTransfers();
                    }
                    else{
                        objGlobal.setErrorMessage("This Trf No / Toteid is not in the same Route - " + transferno);
                        okMessage("Message", objGlobal.getErrorMessage(), getActivity());
                        et_plt_shop_transferno.setText("");
                    }

                } else if (objbuildingdelPalletControl.transferCount(et_plt_shop_transferno.getText().toString().trim()) == 1) {
                    shopNamesFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                    trfDetail(shopNamesFromTransfers.get(0));
                } else
                {
                    shopNamesFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString().trim());
                    if (shopNamesFromTransfers.size() == 1)
                        trfDetail(shopNamesFromTransfers.get(0));
                    else
                        openPopupShopsFromTransfers();
                }
//                    else {
//                        if (PalletScanDeliveryItem != null && PalletScanDeliveryItem.size() != 0)
//                            get_route_id = objbuildingdelPalletControl.loadKsaRoute(PalletScanDeliveryItem.get(0).ShopName);
//                        GetScanresult();
//                    }
            }
        });

        et_plt_shop_transferno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("Focus", "Lost Focus");
                    et_plt_shop_transferno.setText(et_plt_shop_transferno.getText().toString().trim().toUpperCase());
                }
            }
        });


        bt_status_build_plt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    String printer = sp_transfer_printer.getSelectedItem().toString();
                    remark = Remarks.getText().toString();
//                    if (tv_plt_shopname.getText() != null && !tv_plt_shopname.getText().equals("")) {
//                        okMessage("Alert", "Pls Select Shopname First", getContext());
//                    } else {
                    if (printer.isEmpty() || printer.toUpperCase().contains("SELECT")) {
                        okMessage("Message", "Please select printer", getContext());
                        b_Result = false;
                    } else {
                        PltScanTransferShared.savePrintCopies(sp_transfer_print_copies.getSelectedItem().toString());
                        PltScanTransferShared.savePrinter(sp_transfer_printer.getSelectedItem().toString());
                        if (PalletScanDeliveryItem.size() >= 1) {

                            try {
                                if (objbuildingdelPalletControl.InsertPalletDetails(remark, tv_plt_route_id.getText().toString())) {
                                    Log.e("return", "Build");
                                    okMessage("SUCCESS", "Build Pallet Successfully Pallet Number is - " + BuildingDeliveryPalletGlobal.getPalletNo(), getContext());
                                    if (objGlobal.getBluetoothDevicesAvailable().equals("Y")) {
                                        if (!printSticker(printer)) {
                                            okMessage("Pallet Delivery", "Printer Error, Please reprint..", getContext());

                                        }
                                    }

                                    clear();

                                    lv_div_seperate_details.setAdapter(null);
                                    // Toast.makeText(getContext(), "Value 0325    Inserted", Toast.LENGTH_SHORT).show();
                                } else {
                                    okMessage("Alert", objGlobal.getErrorMessage(), getContext());
                                }
                            } catch (ParseException e) {
                                log.e("Error message", e.toString());
                            }
                        } else {
                            Log.e("return", "Not Build");
                            okMessage("Alert", "Please Scan Trf No/Tote id before building Pallet", getContext());
                        }
                    }
                    //  }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        bt_div_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        //  sp_plt_shopname.setAdapter(null);

        if (objbuildingdelPalletControl.LoadPltData() != null && objbuildingdelPalletControl.LoadPltData().size() != 0) {
            PalletScanDeliveryItem = objbuildingdelPalletControl.LoadPltData();
            tv_plt_route_id.setText(String.valueOf(PalletScanDeliveryItem.get(0).RouteId));
            get_route_id = PalletScanDeliveryItem.get(0).RouteId;
            get_shop_names = objbuildingdelPalletControl.LoadShops(PalletScanDeliveryItem.get(0).RouteId);
            tv_shopnames_col.setText(get_shop_names);
            count = PalletScanDeliveryItem.size();
            tv_count.setText(count + "");
            setPltCount(count);
            objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
            lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
        }
        return view;
    }


    private List<String> matchedShops(List<String> transferShops, String selectedShops) {


        for (String shop : selectedShops.split("\\|")) {
            shop = shop.trim();
            for (String transferShop : transferShops) {
                if (shop.equalsIgnoreCase(transferShop.trim())) {
                    transferShops.add(shop);
                    break;
                }
            }
        }

        if (transferShops.isEmpty()) {
            transferShops = null;
        }
        return transferShops;

    }


    private void openPopupShopsFromTransfers() {
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.view_shops_from_transfer);
        lvShops = (ListView) myDialog.findViewById(R.id.lv_shops);
        btCancel = (Button) myDialog.findViewById(R.id.bt_cancel);
        shopNamesFromTransfers = objbuildingdelPalletControl.loadShopsFromTransfers(et_plt_shop_transferno.getText().toString());
        objShopFromTransferAdp = new ShopFromTransferAdp(shopNamesFromTransfers);
        lvShops.setAdapter(objShopFromTransferAdp);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    private class ShopFromTransferAdp extends BaseAdapter {
        public List<ShopInfo> shopNames;

        public ShopFromTransferAdp(List<ShopInfo> shopNames) {
            this.shopNames = shopNames;
        }

        @Override
        public int getCount() {
            return shopNames.size();
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
            View myView = mInflater.inflate(R.layout.view_shops, null);
            TextView tvShopname = (TextView) myView.findViewById(R.id.tv_shop_name);
            tvShopname.setText(shopNames.get(position).getShopName());

            tvShopname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trfDetail(shopNames.get(position));
                    myDialog.dismiss();
                }
            });
            return myView;
        }
    }

    private void trfDetail(ShopInfo selectedShop) {
        get_route_id = objbuildingdelPalletControl.loadKsaRoute(selectedShop.getShopName());
        PltScanTransferShared.shopNameSave(selectedShop.getShopName());
        transferno = et_plt_shop_transferno.getText().toString().trim();
        String shopname = tv_plt_shopname.getText().toString();
        get_shop_names = objbuildingdelPalletControl.LoadShops(get_route_id);
        selectedShopName = selectedShop;
        if (GetScanresult()) {
            Log.e("Error", "Reached here");
            et_plt_shop_transferno.requestFocus();
            PalletScanDeliveryItem = objbuildingdelPalletControl.ScanTransfer2(getActivity(), transferno, get_route_id, android_id, shopname);
            objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
            lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
            if (objTransferStatusPltAdp != null) {
                if (PalletScanDeliveryItem != null && !PalletScanDeliveryItem.isEmpty()) {
                    setRouteid(tv_plt_route_id.getText().toString());
                    tv_plt_route_id.setEnabled(false);
                    tv_plt_route_id.setClickable(false);
                    et_plt_shop_transferno.requestFocus();
                }

            }

            count = Integer.valueOf(getPltCount());
            tv_count.setText(count + "");
            et_plt_shop_transferno.setText("");
            et_plt_shop_transferno.requestFocus();
        }
    }


    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    private void bluetoothSetup() {
        clearBtDevData();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();
        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //"BlueTooth Connect"
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        okMessage("IOException", e.toString(), context);
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException", e.toString(), context);
                    }
                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice != null) {
                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        getActivity().registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private boolean printSticker(String device) {
        if (!bluetoothPort.isConnected()) {
            try {
                btConn(mBluetoothAdapter.getRemoteDevice(device));
            } catch (Exception e) {
                okMessage("Error 2", e.toString(), getContext());
                return false;
            }
        }
        printBarCode();
        // clear();
        return true;
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new BuildingDeliveryPalletFragment.connBT().execute(btDev);
    }


    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);
                str_temp = params[0].getAddress();
                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result.intValue() == 0) {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                printBarCode();
                //clear();
            } else {
                alert
                        .setTitle("Error 4")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    private boolean printBarCode() {
        try {
            //testPrint=true;
            byte[] printData = null;
            if (testPrint) {
                printData = objSample_Print.getLabelWasNowHoneyWellTestPrint();
            } else {
                //okMessage("ALERT - ", "Count= "+objbuildingdelPalletControl.LoadPltDataCount(objpalletbuilding.getPalletNo()).toString()+", Routeid = "+getRouteid().toString()+", PLTSN = "+objpalletbuilding.getPalletSn(), getContext());
                printData = objSample_Print.getRoutePalletPrint(objpalletbuilding.getPalletNo(), objGlobal.getUserName(), getRouteid().toString(), objGlobal.getServerDate(), objbuildingdelPalletControl.LoadPltDataCount(objpalletbuilding.getPalletNo()).toString(), objGlobal.getServerDate(), objpalletbuilding.getPalletSn().toString(), sp_transfer_print_copies.getSelectedItem().toString());
            }

            return objSample_Print.PrintBarcodeByte(printData);
        } catch (Exception e) {
            okMessage("Error 3", e.toString(), getContext());
            return false;
        }
    }

    public boolean GetScanresult() {
        PltScanTransferShared.Routeidsave(tv_plt_route_id.getText().toString());
        transferno = et_plt_shop_transferno.getText().toString().trim();
        Log.e("transferno", transferno);
        if (et_plt_shop_transferno.getText().toString().trim().isEmpty()) {
            okMessage("Alert", "Please Scan Trf no/Toteid", getContext());
            return false;
        } else {
            if (PalletScanDeliveryItem != null && PalletScanDeliveryItem.size() != 0) {
                if (get_route_id != PalletScanDeliveryItem.get(0).RouteId) {
                    objGlobal.setErrorMessage("This Trf No / Toteid is not in the same Route - " + transferno);
                    okMessage("Message", objGlobal.getErrorMessage(), getActivity());
                    return false;
                }
            }
            PalletScanDeliveryItem = new ArrayList<>();
            PalletScanDeliveryItem = objbuildingdelPalletControl.ScanTransfer(getActivity(), transferno, get_route_id, android_id,selectedShopName);
            et_plt_shop_transferno.requestFocus();
            objTransferStatusPltAdp = new MyTransferStatusPltAdp(PalletScanDeliveryItem);
            lv_div_seperate_details.setAdapter(objTransferStatusPltAdp);
            if (PalletScanDeliveryItem.size() != 0) {
                PltScanTransferShared.Routeidsave(tv_plt_route_id.getText().toString());
                setRouteid(tv_plt_route_id.getText().toString());
                tv_plt_route_id.setEnabled(false);
                tv_plt_route_id.setClickable(false);
                et_plt_shop_transferno.requestFocus();
                setPltCount(PalletScanDeliveryItem.size());
                count = getPltCount();
                if (count == 1) {
                    tv_plt_route_id.setText(String.valueOf(get_route_id));
                    tv_shopnames_col.setText(get_shop_names);
                    PltScanTransferShared.shopNameSave(selectedShopName.getShopName());
                    PltScanTransferShared.Routeidsave(String.valueOf(get_route_id));
                    tv_plt_shopname.setText(selectedShopName.getShopName());
                }
                tv_count.setText(count + "");
                et_plt_shop_transferno.setText("");
                //     tv_plt_shopname.setText("");
                //       sp_plt_shopname.setAdapter(null);
            }

            // }
//            } else {
//                okMessage("Alert", "Please Select Route", getContext());
//                return false;
//            }
            // Log.e("getCount spinner", SpinnerCount + "");


            et_plt_shop_transferno.setText("");
            et_plt_shop_transferno.requestFocus();
            return false;
        }

    }


    private class MyTransferStatusPltAdp extends BaseAdapter {
        public ArrayList<PalletScanDeliveryItem> pltScanItemsList;

        public MyTransferStatusPltAdp(ArrayList<PalletScanDeliveryItem> PltScanItems) {
            this.pltScanItemsList = PltScanItems;
        }

        @Override
        public int getCount() {
            return pltScanItemsList.size();
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
            View myView = mInflater.inflate(R.layout.pallet_transfer_details, null);
            final PalletScanDeliveryItem s = pltScanItemsList.get(position);
            TextView tv_transfer_no_details = (TextView) myView.findViewById(R.id.tv_transfer_no_details);
            tv_transfer_no_details.setText(String.valueOf(s.TransferNo));
            TextView tv_toteid_details = (TextView) myView.findViewById(R.id.tv_toteid_details);
            tv_toteid_details.setText(String.valueOf(s.Toteid));
            TextView tv_shopname_details = (TextView) myView.findViewById(R.id.tv_shopname_details);
            tv_shopname_details.setText(String.valueOf(s.ShopName));
            TextView tv_Pallet_qty = (TextView) myView.findViewById(R.id.tv_Pallet_qty);
            tv_Pallet_qty.setText(String.valueOf(s.Qty));
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
        tv_plt_route_id.clearFocus();
        et_plt_shop_transferno.setText("");
        Remarks.setText("");
        // sp_plt_shopname.setAdapter(null);
        tv_plt_shopname.setText("");
        tv_plt_route_id.setText("");
        transferno = "";
        tv_shopnames_col.setText("");
        tv_plt_route_id.setEnabled(true);
        tv_plt_route_id.setClickable(true);
        PltScanTransferShared.Routeidsave("");
        count = 0;
        setPltCount(count);
        tv_count.setText("");
        if (objbuildingdelPalletControl.deletetemp()) {
            // Clear collection..
            lv_div_seperate_details.setAdapter(null);
            PalletScanDeliveryItem = new ArrayList<>();
        }
        PltScanTransferShared.savePrinter("");

    }
}
