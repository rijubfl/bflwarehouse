package com.bflgroup.warehouse.ui.binstorageputaway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bflgroup.warehouse.R;

public class BinPutAwayHistoryFragment extends Fragment {

    public BinPutAwayHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_put_away_history, container, false);

        return view;
    }
}