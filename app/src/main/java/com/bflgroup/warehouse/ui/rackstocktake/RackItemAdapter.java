package com.bflgroup.warehouse.ui.rackstocktake;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.ui.rackstocktake.model.RackHistoryModel;

import java.util.ArrayList;

public class RackItemAdapter extends BaseAdapter {
    public ArrayList<RackHistoryModel> listStockTakingItem;
    Activity activity;

    public RackItemAdapter(Activity activity, ArrayList<RackHistoryModel> listStockTakingItem) {
        this.activity = activity;
        this.listStockTakingItem = listStockTakingItem;
    }

    @Override
    public int getCount() {
        return listStockTakingItem.size();
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
        LayoutInflater mInflater = activity.getLayoutInflater();
        View myView = mInflater.inflate(R.layout.rack_list_item_stocktake, null);

        TextView tv_palletno1 = (TextView) myView.findViewById(R.id.tv_palletno1);
        TextView tv_palletno2 = (TextView) myView.findViewById(R.id.tv_palletno2);
        TextView tv_rack = (TextView) myView.findViewById(R.id.tv_rack);
        TextView tv_warehouse = (TextView) myView.findViewById(R.id.tv_warehouse);
        TextView tv_datetime = (TextView) myView.findViewById(R.id.tv_datetime);

        String[] date = listStockTakingItem.get(position).getTrnDate().split(" ");


        tv_palletno1.setText(listStockTakingItem.get(position).getPalletNo1());
        tv_palletno2.setText(listStockTakingItem.get(position).getPalletNo2());
        tv_rack.setText(listStockTakingItem.get(position).getRackNo());
        tv_warehouse.setText(listStockTakingItem.get(position).getWarehouse());
        tv_datetime.setText(date[0] + "-" + listStockTakingItem.get(position).getTrnTime());

        return myView;
    }
}