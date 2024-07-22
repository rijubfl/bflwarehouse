package com.bflgroup.warehouse.ui.moveshuttlesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

public class MoveToShuttleSystemFragment extends Fragment {

    private Spinner sp_move_shuttle_type;
    private EditText et_move_shuttle_toteid;
    private Button bt_move_shuttle_scan;
    private ListView lv_move_shuttle;
    private Button bt_move_shuttle_clear;
    private Button bt_move_shuttle_save;

    private boolean b_Result;

    private Global objGlobal = Global.getInstance();
    private MoveToShuttleSystemControl objMoveToShuttleSystemControl = new MoveToShuttleSystemControl();
    ArrayList<MoveToShuttleSystemScanTicket> listMoveToShuttleSystemScanTicket = new ArrayList<MoveToShuttleSystemScanTicket>();
    MyMoveToShuttleSystemScanTicketAdp objMyMoveToShuttleSystemScanTicketAdp;
    public MoveToShuttleSystemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_move_to_shuttle_system, container, false);
        sp_move_shuttle_type = (Spinner) view.findViewById(R.id.sp_move_shuttle_type);
        et_move_shuttle_toteid = (EditText) view.findViewById(R.id.et_move_shuttle_toteid);
        bt_move_shuttle_scan = (Button) view.findViewById(R.id.bt_move_shuttle_scan);
        lv_move_shuttle = (ListView) view.findViewById(R.id.lv_move_shuttle);
        bt_move_shuttle_clear = (Button) view.findViewById(R.id.bt_move_shuttle_clear);
        bt_move_shuttle_save = (Button) view.findViewById(R.id.bt_move_shuttle_save);

        List<String> arr6;
        arr6 = new ArrayList<String>();
        arr6.add("Inbound");
        //arr6.add("Outbound");
        ArrayAdapter<String> arrayAdp6 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr6);
        sp_move_shuttle_type.setAdapter(arrayAdp6);

        et_move_shuttle_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_move_shuttle_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String sel = sp_move_shuttle_type.getSelectedItem().toString();
                    String toteid = et_move_shuttle_toteid.getText().toString();
                    b_Result = objMoveToShuttleSystemControl.validateToteId(toteid, sel);
                    if (!b_Result) {
                        okMessage("MoveToShuttleSystemFragment:et_move_shuttle_toteid", objGlobal.getErrorMessage());
                        vibrate(500);
                    }
                    et_move_shuttle_toteid.setText("");
                    et_move_shuttle_toteid.requestFocus();
                    loadScanPending();
                    return true;
                }
                return false;
            }
        });

        bt_move_shuttle_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel = sp_move_shuttle_type.getSelectedItem().toString();
                String toteid = et_move_shuttle_toteid.getText().toString();
                b_Result = objMoveToShuttleSystemControl.validateToteId(toteid, sel);
                if (!b_Result) {
                    okMessage("MoveToShuttleSystemFragment:et_move_shuttle_toteid", objGlobal.getErrorMessage());
                    vibrate(500);
                }
                et_move_shuttle_toteid.setText("");
                et_move_shuttle_toteid.requestFocus();
                loadScanPending();
            }
        });

        bt_move_shuttle_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = clearAll();
                                if (!b_Result) {
                                    okMessage("MoveToShuttleSystemFragment:clearAll ", objGlobal.getErrorMessage());
                                    vibrate(500);
                                }
                                loadScanPending();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                et_move_shuttle_toteid.requestFocus();
                            }
                        })
                        .show();
            }
        });

        bt_move_shuttle_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objMoveToShuttleSystemControl.validateTotes();
                if (!b_Result) {
                    okMessage("Warehouse GRN", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new MoveToShuttleSystemFragment.SaveShuttle().execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   // bt_wh_grn_save.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });
        loadScanPending();
        et_move_shuttle_toteid.requestFocus();
        return view;
    }

    private boolean clearAll() {
        b_Result = objMoveToShuttleSystemControl.clearAll();
        if (!b_Result) {
            okMessage("MoveToShuttleSystemFragment:clearAll ", objGlobal.getErrorMessage());
            vibrate(500);
            return false;
        } else {
            et_move_shuttle_toteid.setText("");
        }
        return true;
    }

    void loadScanPending() {
        listMoveToShuttleSystemScanTicket.clear();
        listMoveToShuttleSystemScanTicket = objMoveToShuttleSystemControl.loadMoveToShuttleSystemScanTicket();
        objMyMoveToShuttleSystemScanTicketAdp = new MoveToShuttleSystemFragment.MyMoveToShuttleSystemScanTicketAdp(listMoveToShuttleSystemScanTicket);
        lv_move_shuttle.setAdapter(objMyMoveToShuttleSystemScanTicketAdp);
    }

    private class SaveShuttle extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public SaveShuttle() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Save / Posting API, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objMoveToShuttleSystemControl.postApi(getContext(),"T1805202300310821","T1805202300310821","1","B0008260","1","18/05/2023 13:22:00","B6148-0544",1,1,"");
                //b_Result = objMoveToShuttleSystemControl.saveShuttleSystem();
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
                okMessage("Shuttle", objGlobal.getErrorMessage());
            } else {
                //call apis
                clearAll();
                loadScanPending();
                et_move_shuttle_toteid.requestFocus();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class MyMoveToShuttleSystemScanTicketAdp extends BaseAdapter {
        public ArrayList<MoveToShuttleSystemScanTicket> listMoveToShuttleSystemScanTicket;

        public MyMoveToShuttleSystemScanTicketAdp(ArrayList<MoveToShuttleSystemScanTicket> listMoveToShuttleSystemScanTicket) {
            this.listMoveToShuttleSystemScanTicket = listMoveToShuttleSystemScanTicket;
        }

        @Override
        public int getCount() {
            return listMoveToShuttleSystemScanTicket.size();
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
            View myView = mInflater.inflate(R.layout.move_shuttle_system_pending_ticket_main, null);
            final MoveToShuttleSystemScanTicket s = listMoveToShuttleSystemScanTicket.get(position);

            TextView tv_move_shuttle_system_pending_ticket_main_toteid = (TextView) myView.findViewById(R.id.tv_move_shuttle_system_pending_ticket_main_toteid);
            tv_move_shuttle_system_pending_ticket_main_toteid.setText(String.valueOf(s.toteId));

            TextView tv_move_shuttle_system_pending_ticket_main_status = (TextView) myView.findViewById(R.id.tv_move_shuttle_system_pending_ticket_main_status);
            tv_move_shuttle_system_pending_ticket_main_status.setText(String.valueOf(s.status));

            Button bt_move_shuttle_system_pending_ticket_main_delete=(Button)myView.findViewById(R.id.bt_move_shuttle_system_pending_ticket_main_delete);
            bt_move_shuttle_system_pending_ticket_main_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to delete? "+ s.toteId)
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objMoveToShuttleSystemControl.deleteSelected(s.toteId);
                                    if (!b_Result) {
                                        okMessage("MoveToShuttleSystemFragment:bt_move_shuttle_system_pending_ticket_main_delete", objGlobal.getErrorMessage());
                                        vibrate(500);
                                    }
                                    loadScanPending();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // bt_wh_grn_save.requestFocus();
                                }
                            })
                            .show();
                }
            });
            return myView;
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