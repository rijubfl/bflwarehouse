package com.bflgroup.warehouse.ui.rfidtagregister;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bflgroup.warehouse.R;

public class RfidTagregisterFragment extends Fragment {

    public RfidTagregisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rfid_tagregister, container, false);

        return view;
    }
}