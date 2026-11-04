package com.bflgroup.warehouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.comm.SaredRef;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    Global objGlobal = Global.getInstance();
    DBConnection dbConnection = new DBConnection();
    // DBConnection dbConnection = new DBConnection();
    Controls objControls = new Controls();
    SaredRef saredRef;
    private String query;
    private ResultSet rs;
    private ResultSet rs1;
    private EditText signInUserId;
    private EditText signInPasssword;
    private Spinner signInWarehouse;
    private Button signInButton;
    private ProgressDialog mWaitDialog;
    private final ExecutorService loginExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        signInUserId = (EditText) findViewById(R.id.sign_in_userid);
        signInPasssword = (EditText) findViewById(R.id.sign_in_password);
        signInWarehouse = (Spinner) findViewById(R.id.sign_in_warehouse);
        signInButton = (Button) findViewById(R.id.sign_in_btn);
        saredRef = new SaredRef(this);

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("");
        arr.add("UAE");
        arr.add("OMAN");
        arr.add("KUWAIT");
        arr.add("QATAR");
        arr.add("KSA");
        arr.add("BAHRAIN");
        arr.add("MALAYSIA");
        arr.add("3PL");
        objGlobal.setWarehouseCountry(arr);
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
        signInWarehouse.setAdapter(arrayAdp);
        if (saredRef.loadWorkLocation() != "") {
            int pos = arrayAdp.getPosition(saredRef.loadWorkLocation());
            signInWarehouse.setSelection(pos);
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object selectedWarehouse = signInWarehouse.getSelectedItem();
                if (selectedWarehouse == null || TextUtils.isEmpty(selectedWarehouse.toString().trim())) {
                    okMessage("Login", "Please select a warehouse");
                    signInWarehouse.requestFocus();
                    vibrate(500);
                    return;
                }
                String username = signInUserId.getText().toString();
                String password = signInPasssword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    okMessage("Login", "Please enter username");
                    signInUserId.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    okMessage("Login", "Please enter password");
                    signInPasssword.requestFocus();
                    return;
                }
                if (!signInButton.isEnabled()) return;
                signInButton.setEnabled(false);
                mWaitDialog = new ProgressDialog(LoginActivity.this);
                mWaitDialog.setTitle("Signing in");
                mWaitDialog.setMessage("Please wait...");
                mWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mWaitDialog.setIndeterminate(true);
                mWaitDialog.setCancelable(false);
                mWaitDialog.show();
                String warehouse = selectedWarehouse.toString().trim();
                loginExecutor.execute(() -> {
                    boolean result;
                    try {
                        result = setLocate(warehouse)
                                && dbConnection.connectDb()
                                && dbConnection.connectCloudDb()
                                && objControls.getControlMain()
                                && validateUser(username, password)
                                && objControls.getControl();
                        if (result) objGlobal.setDelDate(getDelDate());
                    } catch (Exception exception) {
                        objGlobal.setErrorMessage("Unable to sign in. Please try again.");
                        result = false;
                    }
                    boolean loginSucceeded = result;
                    String errorMessage = objGlobal.getErrorMessage();
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        closeWaitDialog();
                        signInButton.setEnabled(true);
                        if (loginSucceeded) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            okMessage("Login", errorMessage);
                            vibrate(500);
                        }
                    });
                });
            }
        });
    }

    private boolean setLocate(String tps) {
        try {
            objGlobal.setWmsId("1");
            objGlobal.setDbName("BFLDATA");
            saredRef.saveWorkLocation(tps);
            if (!tps.equals("3PL")) {
                saredRef.saveWorkLocationSub(tps);
            }
            objGlobal.setWorkLocation(tps);
            objGlobal.setServerUid("BFLPDA");
            objGlobal.setServerPass("5U83zBc9V$05");
            if (tps.equals("UAE")) {
                objGlobal.setServerIP("192.168.5.51");
            }
            if (tps.equals("OMAN")) {
                objGlobal.setServerIP("192.168.5.51");
            }
            if (tps.equals("KUWAIT")) {
                objGlobal.setServerIP("10.40.240.2");
            }
            if (tps.equals("QATAR")) {
                objGlobal.setServerIP("10.50.240.51");
            }
            if (tps.equals("KSA")) {
                objGlobal.setServerIP("10.70.240.51");
            }
            if (tps.equals("BAHRAIN")) {
                objGlobal.setServerIP("10.60.240.51");
            }
            if (tps.equals("MALAYSIA")) {
                objGlobal.setServerIP("10.90.240.51");
            }
            if (tps.equals("3PL")) {
                objGlobal.setServerIP("bfl-db-prod.cxavfbgorqyp.me-south-1.rds.amazonaws.com");
                objGlobal.setServerUid("BFL");
                objGlobal.setServerPass("LFBmct1971");
            }
            return true;
        } catch(Exception e){
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        closeWaitDialog();
        loginExecutor.shutdownNow();
        super.onDestroy();
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    private boolean isVersionAtLeast(String installedVersion, String requiredVersion) {
        String[] installedParts = installedVersion.trim().split("\\.", -1);
        String[] requiredParts = requiredVersion.trim().split("\\.", -1);
        int partCount = Math.max(installedParts.length, requiredParts.length);
        for (int i = 0; i < partCount; i++) {
            int installedPart = i < installedParts.length ? Integer.parseInt(installedParts[i]) : 0;
            int requiredPart = i < requiredParts.length ? Integer.parseInt(requiredParts[i]) : 0;
            if (installedPart != requiredPart) {
                return installedPart > requiredPart;
            }
        }
        return true;
    }

    private boolean validateUser(String username, String password) {
        String pdaVerActive = "", pdaVerDb = "";
        if (!deviceInfo()) return false;
        try {
            rs = dbConnection.getResultSet("select * from BFLDATA.Dbo.appversion where app='BFLWarehouse'", objGlobal.getCloudCon());
            if (rs.next()) {
                pdaVerActive = rs.getString("active");
                pdaVerDb = rs.getString("version");
            }
            if (pdaVerActive.equals("Y")) {
                if (!isVersionAtLeast(getApplicationContext().getString(R.string.app_version), pdaVerDb)) {
                    objGlobal.setErrorMessage("Please update the app. Minimum required version is - " + pdaVerDb);
                    return false;
                }
            }
            if (objGlobal.getWorkLocation().equals("3PL")) {
                query = "select userid,username,SealPrint,FcCode,PrntName,empCode=username,Shop from pdausers a where username='" + username + "' and pass='" + password + "'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setUserId(rs.getInt("userid"));
                    objGlobal.setUserName(rs.getString("username"));
                    objGlobal.setShopName(rs.getString("Shop"));
                    rs = dbConnection.getResultSet("select * from DataSettings where ShopName='" + objGlobal.getShopName() + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setFcCode(rs.getString("FcCode"));
                        objGlobal.setBranchLetter(rs.getString("ShopLetter"));
                        objGlobal.setDbName(rs.getString("DataName"));
                        objGlobal.setCountryCode(rs.getString("countrycode"));
                        objGlobal.setExportCountryCode(rs.getString("ExportCountryCode"));
                    } else {
                        objGlobal.setErrorMessage("DataSettings (Shop name not found(" + objGlobal.getShopName() + "))");
                        return false;
                    }
                    dbConnection.insertUpdate("insert into bfldata.dbo.tmpLoginPda values(" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','" + objGlobal.getDeviceName() + "')", objGlobal.getConnection());

                } else {
                    objGlobal.setErrorMessage("Invalid username or password");
                    return false;
                }
                if (objGlobal.getFcCode().isEmpty() || objGlobal.getBranchLetter().isEmpty() || objGlobal.getDbName().isEmpty() || objGlobal.getCountryCode().isEmpty() || objGlobal.getExportCountryCode().isEmpty()) {
                    objGlobal.setErrorMessage("Empty values, getFcCode=" + objGlobal.getFcCode() + ", getBranchLetter=" + objGlobal.getBranchLetter() + ", getDbName=" + objGlobal.getDbName() + ", " +
                            "getCountryCode=" + objGlobal.getCountryCode() + ", getExportCountryCode=" + objGlobal.getExportCountryCode());
                    return false;
                }
            } else {
                query = "select userid,username,SealPrint,FcCode,PrntName,allB=isnull(UserAllowMixCategoryBuild,'N'),empCode=(select RecStartingNo from fabsmain.dbo.[user] where userid=a.mainuserid) from pdausers a where username='" + username + "' and pass='" + password + "'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setUserId(rs.getInt("userid"));
                    objGlobal.setUserName(rs.getString("username"));
                    objGlobal.setSealPrinterName(rs.getString("SealPrint"));
                    objGlobal.setFcCode(rs.getString("FcCode"));
                    objGlobal.setEmpCode(rs.getString("empCode"));
                    objGlobal.setUserPrinterName(rs.getString("PrntName"));
                    objGlobal.setUserAllowMixCategoryBuild(rs.getString("allB"));
                    objGlobal.setEmpName(dbConnection.stringReturn(objGlobal.getConnection(), "payroll.dbo.employee", "empname", "empcode", rs.getString("empCode")));
                } else {
                    objGlobal.setErrorMessage("Invalid username or password");
                    return false;
                }
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) return false;

            if(!dbConnection.insertUpdate("insert into bfldata.dbo.WHPdaUserVersion(userid,username,DeviceVersion,loginDate,Logintime,warehouse,devicename,macid,bluetoothid," +
                    "serialnumber) values(" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','" + getResources().getString(R.string.app_version) + "','" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "','" + objGlobal.getWarehouse() + "','" + objGlobal.getDeviceName() + "','" + Global.getDeviceMacId() + "','" + Global.getDeviceBluToothId() + "'," +
                    "'" + Global.getDeviceSerialNo() + "')", objGlobal.getConnection())) return false;

            query = "select * from bfldata..LoginUserPda where Username = '" + objGlobal.getUserName() + "'  and Active = 'Y' and  CONVERT(DATE, trndate) = CONVERT(DATE, getdate()) ";
            rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs1.next()) {
                query = "select * from bfldata..LoginUserPda where Username = '" + objGlobal.getUserName() + "' and PDADevicename = '" + objGlobal.getDeviceName() + "' and Active = 'Y'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    dbConnection.insertUpdate("delete from bfldata.dbo.LoginUserPda where Username = '" + objGlobal.getUserName() + "'  and Active = 'Y'", objGlobal.getConnection());
                    dbConnection.insertUpdate("insert into bfldata.dbo.LoginUserPda (userid, Username, PDADevicename, Trndate, Active) values(" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "'," +
                            "'" + objGlobal.getDeviceName() + "',(select getdate()), 'Y')", objGlobal.getConnection());
                    return true;
                }
                objGlobal.setErrorMessage("User Already logged In to another Device on - " + rs1.getString("Trndate"));
                return false;
            } else {
                if(!dbConnection.insertUpdate("insert into bfldata.dbo.LoginUserPda (userid, Username, PDADevicename, Trndate, Active) values(" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "'," +
                        "'" + objGlobal.getDeviceName() + "',(select getdate()), 'Y')", objGlobal.getConnection())) return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:validateUser:" + ex);
            return false;
        }
    }

    private String getDelDate() {
        try {
            rs = dbConnection.getResultSet("select top 1 DelDate=convert(varchar, DelDate, 103) from usa.dbo.usacountchk where closed='N' and " +
                    "deldate>=convert(varchar, getdate(), 103) order by deldate", objGlobal.getConnection());
            if (rs.next()) {
                return rs.getString("DelDate");
            }
            return "";
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:getLatestGrnRf:" + ex);
            return "";
        }
    }

    public boolean deviceInfo() {
        objGlobal.setDeviceName("");
        Global.setDeviceMacId("");
        Global.setDeviceBluToothId("");
        Global.setDeviceSerialNo("");
        try {
            objGlobal.setDeviceName(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            if (objGlobal.getDeviceName().isEmpty()) {
                objGlobal.setErrorMessage("objGlobal.getDeviceName() is empty");
                return false;
            }
            Global.setDeviceMacId("N/A");
            if (Global.getDeviceMacId().isEmpty()) {
                objGlobal.setErrorMessage("objGlobal.getDeviceMacId() is empty");
                return false;
            }
            Global.setDeviceBluToothId("N/A");
            if (Global.getDeviceBluToothId().isEmpty()) {
                objGlobal.setErrorMessage("objGlobal.getDeviceBluToothId() is empty");
                return false;
            }
            //original device name
            Global.setDeviceSerialNo(Settings.Global.getString(this.getContentResolver(),"device_name"));
            if (Global.getDeviceSerialNo().isEmpty()) {
                objGlobal.setErrorMessage("objGlobal.getDeviceSerialNo() is empty");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:deviceInfo:" + ex);
            return false;
        }
    }

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }



}
