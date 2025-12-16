package com.bflgroup.warehouse.ui.rfidtagregister;
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
    //reader relared
    public RFIDWithUHFUART mReader;
    public boolean loopFlag = false;
    private long time;
    public static HashMap<String, String> map;
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";
    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    public static List<String> tempDatas = new ArrayList<>();
    public static ArrayList<String> epcTidUser = new ArrayList<>();
    private int total;
    //MyRfidTagRegisterScanRfidsAdp objMyRfidTagRegisterScanRfidsAdp;
    public UhfInfo uhfInfo=new UhfInfo();
    //private PlaySoundThread playSoundThread;
    private AudioManager am;
    private float volumnRatio;
    private int  selectItem=-1;
    //reader relared

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

    private UhfLocationCanvasView llChart;
    private Object objectLock = new Object();

    private Global objGlobal = Global.getInstance();

    private RfidTagRegisterControl objRfidTagRegisterControl = new RfidTagRegisterControl();

    private boolean b_Result;
    private String msgHead = "RFID GRN";

    public RfidTagRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rfid_tag_register, container, false);

        /*tv_rfid_tag_register_scantime = (TextView) view.findViewById(R.id.tv_rfid_tag_register_scantime);
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
                if (initUHF() && initSound()) {
                    mReader.setPower(30);
                    bt_rfid_tag_register_connect.setText("Connected");
                } else {
                    bt_rfid_tag_register_connect.setText("Connect");
                }
            }
        });

        bt_rfid_tag_register_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearData();
                selectItem = -1;
                uhfInfo = new UhfInfo();
            }
        });

        bt_rfid_tag_register_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inventoryType = 1;
                if (rb_rfid_tag_register_single.isChecked()) inventoryType = 0;
                readTag(bt_rfid_tag_register_start.getText().toString(), inventoryType);
            }
        });

        bt_rfid_tag_register_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new UpdateScanedRfids().execute();
            }
        });*/
        return view;
    }

    /*private void saveRFIDTag() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are You sure to save?")
                .setTitle("Conformation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveGrn().execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private class SaveGrn extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public SaveGrn() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objRfidTagRegisterControl.grnSave(et_grn_transfer_rfid_ginno.getText().toString());
                if (!b_Result) {
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
                okMessage(objGlobal.getErrorMessage());
                vibrate(500);
            } else {
                //objGrnTransferNewSharedRef.saveLastSave(tv_grn_transfer_last_save.getText().toString());
                b_Result = clearAll();
                if (!b_Result) {
                    okMessage(objGlobal.getErrorMessage());
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }*/

   /* private void startLocation(String epc) {
        if (epc.equals("")) {
            UIHelper.ToastMessage(getContext(), "Faild");
            return;
        }
        boolean result = mReader.startLocation(getContext(), epc, IUHF.Bank_EPC, 32, new IUHFLocationCallback() {
            @Override
            public void getLocationValue(int i, boolean b) {
                llChart.setData(i);
                //Log.i(TAG, "value:" + i);
                if (i <= 10) {
                    playSoundThread.play(Integer.MAX_VALUE);
                } else if (i <= 30) {
                    playSoundThread.play(1600);
                } else if (i <= 50) {
                    playSoundThread.play(1100);
                } else if (i <= 70) {
                    playSoundThread.play(600);
                } else if (i <= 90) {
                    playSoundThread.play(100);
                }
            }

        });
        if (!result) {
            UIHelper.ToastMessage(getContext(), "Faild");
        }
    }*/

    /*public void stopLocation() {
        mReader.stopLocation();
    }

    private class PlaySoundThread extends Thread {
        private boolean isStop = false;
        int waitTime = Integer.MAX_VALUE;

        @Override
        public void run() {
            while (!isStop) {
                synchronized (objectLock) {
                    try {
                        objectLock.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                playSound(1);
            }
        }

        public void play(int waitTime) {
            this.waitTime = waitTime;
            synchronized (objectLock) {
                objectLock.notifyAll();
            }
        }


        public void stopPlay() {
            isStop = true;
            synchronized (objectLock) {
                objectLock.notifyAll();
            }
            interrupt();
        }
    }

    private void readTag(String scanType, int inventoryType) {
        if (scanType.equals("Start")) {
            switch (inventoryType) {
                case 0:// single
                    time = System.currentTimeMillis();
                    UHFTAGInfo uhftagInfo = mReader.inventorySingleTag();
                    if (uhftagInfo != null) {
                        String tid = uhftagInfo.getTid();
                        String epc = uhftagInfo.getEPC();
                        String user = uhftagInfo.getUser();
                        addDataToList(epc, mergeTidEpc(tid, epc, user), uhftagInfo.getRssi());
                        setTotalTime();
                        playSound(1);
                    } else {
                        UIHelper.ToastMessage(getContext(), "Inventory failure");
                    }
                    break;
                case 1:// loop
                    mReader.setInventoryCallback(new IUHFInventoryCallback() {
                        @Override
                        public void callback(UHFTAGInfo uhftagInfo) {
                            Message msg = handler.obtainMessage();
                            msg.obj = uhftagInfo;
                            msg.what = 1;
                            handler.sendMessage(msg);
                            playSound(1);
                        }
                    });
                    if (mReader.startInventoryTag()) {
                        bt_rfid_tag_register_start.setText("Stop");
                        loopFlag = true;
                        //setEnablePopup(false);
                        time = System.currentTimeMillis();
                        handler.sendEmptyMessageDelayed(2, 10);
                    } else {
                        stopInventory();
                        UIHelper.ToastMessage(getContext(), "Open failure");
                    }
                    break;
                default:
                    break;
            }
        } else {// 停止识别
            stopInventory();
            setTotalTime();
        }
    }

    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            //setEnablePopup(true);
            if (mReader.stopInventory()) {
                bt_rfid_tag_register_start.setText("Start");
            } else {
                UIHelper.ToastMessage(getContext(), "Stop failure");
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UHFTAGInfo info = (UHFTAGInfo) msg.obj;
                String tid = info.getTid();
                String epc = info.getEPC();
                String user = info.getUser();
                addDataToList(epc, mergeTidEpc(tid, epc, user), info.getRssi());
            } else if (msg.what == 2) {
                if (loopFlag) {
                    handler.sendEmptyMessageDelayed(2, 10);
                    setTotalTime();
                } else {
                    handler.removeMessages(2);
                }
            }
        }
    };

    private void setTotalTime() {
        float useTime = (System.currentTimeMillis() - time) / 1000.0F;
        tv_rfid_tag_register_scantime.setText(NumberTool.getPointDouble(1, useTime) + "s");
    }

    private void addDataToList(String epc, String epcAndTidUser, String rssi) {
        try {
            if (StringUtils.isNotEmpty(epc)) {
                int index = checkIsExist(epc);
                map = new HashMap<String, String>();
                map.put(TAG_EPC, epc);
                map.put(TAG_EPC_TID, epcAndTidUser);
                map.put(TAG_COUNT, String.valueOf(1));
                map.put(TAG_RSSI, rssi);
                if (index == -1) {
                    tagList.add(map);
                    tempDatas.add(epc);
                    tv_rfid_tag_register_epc_count.setText(String.valueOf(objMyRfidTagRegisterScanRfidsAdp.getCount()));
                } else {
                    int tagCount = Integer.parseInt(tagList.get(index).get(TAG_COUNT), 10) + 1;
                    map.put(TAG_COUNT, String.valueOf(tagCount));
                    map.put(TAG_EPC_TID, epcAndTidUser);
                    // epcTidUser.add(epcAndTidUser);
                    tagList.set(index, map);
                }
                tv_rfid_tag_register_total.setText(String.valueOf(++total));
                objMyRfidTagRegisterScanRfidsAdp.notifyDataSetChanged();
                //----------
                uhfInfo.setTempDatas(tempDatas);
                uhfInfo.setTagList(tagList);
                uhfInfo.setCount(total);
                uhfInfo.setTagNumber(objMyRfidTagRegisterScanRfidsAdp.getCount());
            }
        } catch (Exception e) {
            okMessage(e.getMessage());
        }
    }

    private String mergeTidEpc(String tid, String epc, String user) {
        epcTidUser.add(epc);
        String data = epc;
        if (!TextUtils.isEmpty(tid) && !tid.equals("0000000000000000") && !tid.equals("000000000000000000000000")) {
            epcTidUser.add(tid);
            data += "\nTID:" + tid;
        }
        if (user != null && user.length() > 0) {
            epcTidUser.add(user);
            data += "\nUSER:" + user;
        }
        return data;
    }

    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        for (int k = 0; k < tempDatas.size(); k++) {
            if (epc.equals(tempDatas.get(k))) {
                return k;
            }
        }
        return -1;
    }

    private boolean initSound() {
        try {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
            soundMap.put(1, soundPool.load(getContext(), R.raw.barcodebeep, 1));
            soundMap.put(2, soundPool.load(getContext(), R.raw.serror, 1));
            am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            okMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public void playSound(int id) {
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // Returns the maximum volume value of the current AudioManager object
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);// Returns the volume value of the current AudioManager object
        volumnRatio = audioCurrentVolume / audioMaxVolume;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // left channel volume
                    volumnRatio, // right channel volume
                    1, // Priority, 0 is the lowest
                    0, // Number of loops, 0 does not loop, -1 loops forever
                    1 // Playback speed, the value is between 0.5-2.0, 1 is normal speed
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            okMessage(ex.getMessage());
            return false;
        }
        if (mReader != null) {
            new InitTask().execute();
        }
        return true;
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (!result) {
                Toast.makeText(getContext(), "Connecting....", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(getContext());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    *//*private class UpdateScanedRfids extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public UpdateScanedRfids() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                //b_Result = objGrnTransferRfidControl.saveScannedRfid(tempDatas);
                if (!b_Result) return 0;
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                okMessage(objGlobal.getErrorMessage());
                vibrate(500);
            } else {
                if (!loadScanTrfRfidItems())
                    okMessage(objGlobal.getErrorMessage());
                else {
                    dialog.dismiss();
                    myDialogRfidScan.dismiss();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
                myDialogRfidScan.dismiss();
            }
        }
    }*//*

    public class MyRfidTagRegisterScanRfidsAdp extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyRfidTagRegisterScanRfidsAdp(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return tagList.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return tagList.get(arg0);
        }

        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.ticket_rfids_tag_register_scan, null);
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_epc);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_count);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_rss);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvEPCTID.setText((String) tagList.get(position).get(TAG_EPC_TID));
            holder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) tagList.get(position).get(TAG_RSSI));

            if (position == selectItem) {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorSkyBlue));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }

        public void setSelectItem(int select) {
            if (selectItem == select) {
                selectItem = -1;
                uhfInfo.setSelectItem("");
                uhfInfo.setSelectIndex(selectItem);
            } else {
                selectItem = select;
                uhfInfo.setSelectItem(tagList.get(select).get(TAG_EPC));
                uhfInfo.setSelectIndex(selectItem);
            }
        }
    }

    public final class ViewHolder {
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    void vibrateSound(int duration) {
        try {
            if (objGlobal.getUserName().equals("BFL")) {
                vibrate(500);
            } else {
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                Uri notification = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.errorsound);
                Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                r.play();
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(duration);
                }
            }
        } catch (Exception e) {
            okMessage(e.getMessage());
        }
    }

    private void okMessage(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(msgHead);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
        vibrate(500);
    }*/
}