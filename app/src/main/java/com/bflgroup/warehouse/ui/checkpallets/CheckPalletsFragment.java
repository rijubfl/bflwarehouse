package com.bflgroup.warehouse.ui.checkpallets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;

import java.util.ArrayList;


public class CheckPalletsFragment extends Fragment {


    String warehouse = "";
    private Global objGlobal = Global.getInstance();
    TextView tv_rack_in_out_warehouse;
    TextView tv_pallet_count;
    Spinner sp_racks;
    private CheckPalletsControl objCheckPalletsControl;
    String racks;
    private ListView lv_div_details;
    ArrayList<CheckPalletsItems> ItemStatus = new ArrayList<CheckPalletsItems>();
    MyStatusAdp objStatusAdp = null;
    Button ButtonClick;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_check_pallet, container, false);

        warehouse = objGlobal.getWarehouse();
        tv_rack_in_out_warehouse = (TextView) view.findViewById(R.id.tv_rack_in_out_warehouse);
        sp_racks = (Spinner) view.findViewById(R.id.sp_racks);
        lv_div_details = (ListView) view.findViewById(R.id.lv_rack_in_out_details);
        ButtonClick = (Button) view.findViewById(R.id.bt_show_pallets);
        tv_pallet_count = (TextView) view.findViewById(R.id.tv_pallet_count);
        objCheckPalletsControl = new CheckPalletsControl(getContext());

        loadRackHistoryItem();

        ButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonClick();
            }
        });

        return view;
    }

    private class MyStatusAdp extends BaseAdapter {
        public  ArrayList<CheckPalletsItems> CheckPalletsItemList;

        public MyStatusAdp(ArrayList<CheckPalletsItems> CheckPalletsItems) {
            this.CheckPalletsItemList = CheckPalletsItems;
        }

        @Override
        public int getCount() {
            return CheckPalletsItemList.size();
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
            View myView = mInflater.inflate(R.layout.pallets_details, null);
            final CheckPalletsItems s = CheckPalletsItemList.get(position);
            TextView tv_transfer_no_details = (TextView) myView.findViewById(R.id.tv_rowno);
            tv_transfer_no_details.setText(String.valueOf(s.Rackno));
            TextView tv_toteid_details = (TextView) myView.findViewById(R.id.tv_date);
            tv_toteid_details.setText(String.valueOf(s.Date));
            TextView tv_shopname_details = (TextView) myView.findViewById(R.id.tv_palletno1);
            tv_shopname_details.setText(String.valueOf(s.Pallet1));
            TextView tv_gin_qty = (TextView) myView.findViewById(R.id.tv_palletno2);
            tv_gin_qty.setText(String.valueOf(s.Pallet2));
            return myView;
        }
    }

    private void loadRackHistoryItem() {
        try {

            if(tv_rack_in_out_warehouse.getText().toString().equals("TECHNO")) {
                ArrayList<String> arr1 = objCheckPalletsControl.loadRackhistoryTechno();
                ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
                sp_racks.setAdapter(arrayAdp1);
                sp_racks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        racks = sp_racks.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
           else if(tv_rack_in_out_warehouse.getText().toString().equals("JAFZA")) {
                ArrayList<String> arr1 = objCheckPalletsControl.loadRackhistoryJafza();
                ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
                sp_racks.setAdapter(arrayAdp1);
                sp_racks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        racks = sp_racks.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
           else  {
                ArrayList<String> arr1 = objCheckPalletsControl.loadRackhistory();
                ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
                sp_racks.setAdapter(arrayAdp1);
                sp_racks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        racks = sp_racks.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
            tv_rack_in_out_warehouse.setText(objGlobal.getWarehouse());
//           tv_rack_in_out_warehouse.setText("JAFZA");


        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
        }
    }


    private void ButtonClick(){
        if(warehouse.equals("TECHNO")) {
            ItemStatus = objCheckPalletsControl.loadpalletstechno(racks);
            String tv_count = objCheckPalletsControl.loadpalletscount(racks, "TECHNO");

            tv_pallet_count.setText(tv_count);

            objStatusAdp = new MyStatusAdp(ItemStatus);
            lv_div_details.setAdapter(objStatusAdp);
//            int Count = objCheckPalletsControl.loadpalletstechno(racks);
//            int Count = tv_pallet_count;
        }
        else if(warehouse.equals("JAFZA")) {
            ItemStatus = objCheckPalletsControl.loadpalletsJafza(racks);
            objStatusAdp = new MyStatusAdp(ItemStatus);
            lv_div_details.setAdapter(objStatusAdp);
            String tv_count = objCheckPalletsControl.loadpalletscount(racks, "JAFZA");
            tv_pallet_count.setText(tv_count);
        }else {
            ItemStatus = objCheckPalletsControl.loadpallets(racks);
            objStatusAdp = new MyStatusAdp(ItemStatus);
            lv_div_details.setAdapter(objStatusAdp);
            String tv_count = objCheckPalletsControl.loadpalletscount(racks, "YOTO");
            tv_pallet_count.setText(tv_count);
        }
    }



}
