package com.bflgroup.warehouse.ui.simleft;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bflgroup.warehouse.R;

public class SimLeftFragment extends Fragment {
    public SimLeftFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sim_left, container, false);

        return view;
    }
}