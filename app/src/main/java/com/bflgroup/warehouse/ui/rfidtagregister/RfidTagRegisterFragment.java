package com.bflgroup.warehouse.ui.rfidtagregister;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.rfidreadercw.UhfInfo;
import com.bflgroup.warehouse.rfidreadercw.tools.NumberTool;
import com.bflgroup.warehouse.rfidreadercw.tools.StringUtils;
import com.bflgroup.warehouse.rfidreadercw.tools.UIHelper;
import com.bflgroup.warehouse.ui.rfidtagregister.view.UhfLocationCanvasView;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHF;
//import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import com.rscja.deviceapi.interfaces.IUHFLocationCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RfidTagRegisterFragment extends Fragment {

    private RadioButton rb_rfid_tag_register_single;
    private RadioButton rb_rfid_tag_register_auto;
    private Button bt_rfid_tag_register_connect;
    private Button bt_rfid_tag_register_start;
    private Button bt_rfid_tag_register_options;
    private ListView lv_rfid_tag_register_rfids;
    private Button bt_rfid_tag_register_clear;
    private Button bt_rfid_tag_register_save;
    private TextView tv_rfid_tag_register_scantime;
    private TextView tv_rfid_tag_register_epc_count;
    private TextView tv_rfid_tag_register_total;

    public RfidTagRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rfid_tag_register, container, false);

        tv_rfid_tag_register_scantime = (TextView) view.findViewById(R.id.tv_rfid_tag_register_scantime);
        tv_rfid_tag_register_epc_count = (TextView) view.findViewById(R.id.tv_rfid_tag_register_epc_count);
        tv_rfid_tag_register_total = (TextView) view.findViewById(R.id.tv_rfid_tag_register_total);
        rb_rfid_tag_register_single = (RadioButton) view.findViewById(R.id.rb_rfid_tag_register_single);
        rb_rfid_tag_register_auto = (RadioButton) view.findViewById(R.id.rb_rfid_tag_register_auto);
        bt_rfid_tag_register_start = (Button) view.findViewById(R.id.bt_rfid_tag_register_start);
        bt_rfid_tag_register_options = (Button) view.findViewById(R.id.bt_rfid_tag_register_options);
        lv_rfid_tag_register_rfids = (ListView) view.findViewById(R.id.lv_rfid_tag_register_rfids);
        bt_rfid_tag_register_clear = (Button) view.findViewById(R.id.bt_rfid_tag_register_clear);
        bt_rfid_tag_register_save = (Button) view.findViewById(R.id.bt_rfid_tag_register_save);

        bt_rfid_tag_register_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (initUHF() && initSound()) {
                    mReader.setPower(30);
                    bt_rfid_tag_register_connect.setEnabled(false);
                    bt_rfid_tag_register_options.setEnabled(true);
                    bt_rfid_tag_register_start.setEnabled(true);
                    rb_rfid_tag_register_single.setEnabled(true);
                    rb_rfid_tag_register_auto.setEnabled(true);
                    bt_rfid_tag_register_connect.setText("Connected");
                } else {
                    bt_rfid_tag_register_connect.setEnabled(true);
                    bt_rfid_tag_register_options.setEnabled(false);
                    bt_rfid_tag_register_start.setEnabled(false);
                    rb_rfid_tag_register_single.setEnabled(false);
                    rb_rfid_tag_register_auto.setEnabled(false);
                    bt_rfid_tag_register_connect.setText("Connect");
                }*/
            }
        });

        bt_rfid_tag_register_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_rfid_tag_register_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_rfid_tag_register_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

}