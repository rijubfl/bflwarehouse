package com.bflgroup.warehouse.ui.shuttletask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ShuttleTaskFragment extends Fragment {

    private Spinner sp_shuttle_task_type;
    private EditText et_shuttle_task_toteid;
    private Button bt_shuttle_task_scan;
    private ListView lv_shuttle_task;
    private Button bt_shuttle_task_clear;
    private Button bt_shuttle_task_save;

    private boolean b_Result;

    private Global objGlobal = Global.getInstance();
    private ShuttleTaskControl objShuttleTaskControl = new ShuttleTaskControl();
    ArrayList<ShuttleTaskScanTicket> listShuttleTaskScanTicket = new ArrayList<ShuttleTaskScanTicket>();
    ShuttleTaskFragment.MyShuttleTaskScanTicketAdp objMyShuttleTaskScanTicketAdp;

    public ShuttleTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shuttle_task, container, false);
        sp_shuttle_task_type = (Spinner) view.findViewById(R.id.sp_shuttle_task_type);
        et_shuttle_task_toteid = (EditText) view.findViewById(R.id.et_shuttle_task_toteid);
        bt_shuttle_task_scan = (Button) view.findViewById(R.id.bt_shuttle_task_scan);
        lv_shuttle_task = (ListView) view.findViewById(R.id.lv_shuttle_task);
        bt_shuttle_task_clear = (Button) view.findViewById(R.id.bt_shuttle_task_clear);
        bt_shuttle_task_save = (Button) view.findViewById(R.id.bt_shuttle_task_save);

        List<String> arr6;
        arr6 = new ArrayList<String>();
        arr6.add("Inbound");
        //arr6.add("Outbound");
        ArrayAdapter<String> arrayAdp6 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr6);
        sp_shuttle_task_type.setAdapter(arrayAdp6);

        et_shuttle_task_toteid.setOnTouchListener(new View.OnTouchListener() {
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

        et_shuttle_task_toteid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String sel = sp_shuttle_task_type.getSelectedItem().toString();
                    String toteid = et_shuttle_task_toteid.getText().toString();
                    b_Result = objShuttleTaskControl.validateToteId(toteid, sel);
                    if (!b_Result) {
                        okMessage("ShuttleTaskFragment:et_shuttle_task_toteid", objGlobal.getErrorMessage());
                        vibrate(500);
                    }
                    et_shuttle_task_toteid.setText("");
                    et_shuttle_task_toteid.requestFocus();
                    loadScanPending();
                    return true;
                }
                return false;
            }
        });

        bt_shuttle_task_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel = sp_shuttle_task_type.getSelectedItem().toString();
                String toteid = et_shuttle_task_toteid.getText().toString();
                b_Result = objShuttleTaskControl.validateToteId(toteid, sel);
                if (!b_Result) {
                    okMessage("ShuttleTaskFragment:et_shuttle_task_toteid", objGlobal.getErrorMessage());
                    vibrate(500);
                }
                et_shuttle_task_toteid.setText("");
                et_shuttle_task_toteid.requestFocus();
                loadScanPending();
            }
        });

        bt_shuttle_task_clear.setOnClickListener(new View.OnClickListener() {
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
                                    okMessage("ShuttleTaskFragment:clearAll ", objGlobal.getErrorMessage());
                                    vibrate(500);
                                }
                                loadScanPending();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                et_shuttle_task_toteid.requestFocus();
                            }
                        })
                        .show();
            }
        });

        bt_shuttle_task_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objShuttleTaskControl.validateTotes();
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
                                    new ShuttleTaskFragment.SaveShuttle().execute();
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
        et_shuttle_task_toteid.requestFocus();
        return view;
    }

    private boolean clearAll() {
        b_Result = objShuttleTaskControl.clearAll();
        if (!b_Result) {
            okMessage("ShuttleTaskFragment:clearAll ", objGlobal.getErrorMessage());
            vibrate(500);
            return false;
        } else {
            et_shuttle_task_toteid.setText("");
        }
        return true;
    }

    void loadScanPending() {
        listShuttleTaskScanTicket.clear();
        listShuttleTaskScanTicket = objShuttleTaskControl.loadShuttleTaskScanTicket();
        objMyShuttleTaskScanTicketAdp = new ShuttleTaskFragment.MyShuttleTaskScanTicketAdp(listShuttleTaskScanTicket);
        lv_shuttle_task.setAdapter(objMyShuttleTaskScanTicketAdp);
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
                b_Result = objShuttleTaskControl.postApi(getContext(),"T1805202300310821","T1805202300310821","1","B0008260","1","18/05/2023 13:22:00","B6148-0544",1,1,"");
                //b_Result = objShuttleTaskControl.saveShuttleSystem();
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
                et_shuttle_task_toteid.requestFocus();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class MyShuttleTaskScanTicketAdp extends BaseAdapter {
        public ArrayList<ShuttleTaskScanTicket> listShuttleTaskScanTicket;

        public MyShuttleTaskScanTicketAdp(ArrayList<ShuttleTaskScanTicket> listShuttleTaskScanTicket) {
            this.listShuttleTaskScanTicket = listShuttleTaskScanTicket;
        }

        @Override
        public int getCount() {
            return listShuttleTaskScanTicket.size();
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
            View myView = mInflater.inflate(R.layout.shuttle_task_system_pending_ticket_main, null);
            final ShuttleTaskScanTicket s = listShuttleTaskScanTicket.get(position);

            TextView tv_shuttle_task_system_pending_ticket_main_toteid = (TextView) myView.findViewById(R.id.tv_shuttle_task_system_pending_ticket_main_toteid);
            tv_shuttle_task_system_pending_ticket_main_toteid.setText(String.valueOf(s.toteId));

            TextView tv_shuttle_task_system_pending_ticket_main_status = (TextView) myView.findViewById(R.id.tv_shuttle_task_system_pending_ticket_main_status);
            tv_shuttle_task_system_pending_ticket_main_status.setText(String.valueOf(s.status));

            Button bt_shuttle_task_system_pending_ticket_main_delete=(Button)myView.findViewById(R.id.bt_shuttle_task_system_pending_ticket_main_delete);
            bt_shuttle_task_system_pending_ticket_main_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to delete? "+ s.toteId)
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objShuttleTaskControl.deleteSelected(s.toteId);
                                    if (!b_Result) {
                                        okMessage("ShuttleTaskFragment:bt_shuttle_task_system_pending_ticket_main_delete", objGlobal.getErrorMessage());
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