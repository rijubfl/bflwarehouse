package com.bflgroup.warehouse.comm;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;

import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Controls {

    private Global objGlobal = Global.getInstance();
    DBConnection objDBConnection=new DBConnection();
    private ResultSet rs;

    public boolean getControl() {
        try {
            List<String> arr;
            arr = new ArrayList<String>();
            rs = objDBConnection.getResultSet("select menuid,menuname from bfldata.dbo.PdaMenuAccess where Access='Y' and UserId=" + objGlobal.getUserId() + " and " +
                    "MenuId in(select MenuId from bfldata.dbo.PdaMenuMaster where Access='Y')", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("menuid").toString());
            }
            objGlobal.setActiveMenuByUser(arr);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:getControl:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean getControlMain() {
        try {
            objGlobal.setSkipBatchIn(false);
            objGlobal.setEnterQty(false);
            objGlobal.setHideKeyPad(true);
            objGlobal.setMaxTotInBin(1);
            objGlobal.setCountryWiseBoxPrefix("U");
            objGlobal.setWarehouse("");
            objGlobal.setCountryCode("");
            objGlobal.setTransferPrefixPda("");
            objGlobal.setTransferPrefixRobo("");
            objGlobal.setValidateGinCustomsClearance("Y");
            if (objGlobal.getWorkLocation().equals("3PL")) {
                objGlobal.setWarehouse("3PL");
                return true;
            }
            rs = objDBConnection.getResultSet("select BFLDATA.dbo.getClientlocdetails('Warehouse')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setWarehouse(rs.getString(1));
            } else {
                objGlobal.setErrorMessage("Invalid Warehouse, Contact IT");
                return false;
            }
            if (objGlobal.getWarehouse().equals("")) {
                objGlobal.setErrorMessage("Warehouse / IP Changed, Contact IT");
                return false;
            }

            rs = objDBConnection.getResultSet("select BFLDATA.dbo.getClientlocdetails('Country')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setCountryCode(rs.getString(1));
            } else {
                objGlobal.setErrorMessage("Invalid Country, Contact IT");
                return false;
            }
            if (objGlobal.getCountryCode().equals("")) {
                objGlobal.setErrorMessage("Warehouse / Country Code / IP Changed, Contact IT");
                return false;
            }

            if (!objGlobal.getCountryCode().equals("UAE")) {
                rs = objDBConnection.getResultSet("select ShopName,ExportCountryCode,Dataname,CostCodeTo,LocCodeTo from bfldata.dbo.DataSettings where countrycode='" + objGlobal.getCountryCode() + "' and " +
                        "ExportWH='Y'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setExportCountryStoreName(rs.getString("ShopName"));
                    objGlobal.setExportCountryCode(rs.getString("ExportCountryCode"));
                    objGlobal.setExportCountryCostCode(rs.getString("CostCodeTo"));
                    objGlobal.setExportCountryLocCode(rs.getString("LocCodeTo"));
                    objGlobal.setCountryDbName(rs.getString("Dataname"));
                } else {
                    objGlobal.setErrorMessage("LoginActivity:getControl:Invalid ExportCountryCode, Contact IT");
                    return false;
                }
            }

            rs = objDBConnection.getResultSet("select * from fabsmain.dbo.settings", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("descr").toUpperCase().equals("SKIPBATCHIN"))
                    if (rs.getString("status").toString().equals("Y"))
                        objGlobal.setSkipBatchIn(true);
                if (rs.getString("descr").toUpperCase().equals("MAXTOTINBIN"))
                    objGlobal.setMaxTotInBin(rs.getInt("status"));
                if (rs.getString("descr").toUpperCase().equals("BOXPREFIX"))
                    objGlobal.setCountryWiseBoxPrefix(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("GINCUSTOMCLEAR"))
                    objGlobal.setValidateGinCustomsClearance(rs.getString("status"));
            }

            rs = objDBConnection.getResultSet("select distinct Warehouse,TransferPrefix,TransferPrefixPda,TransferPrefixRobo " +
                    "from BFLDATA.dbo.LocationMapping where Warehouse='" + objGlobal.getWarehouse() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setTransferPrefixPda(rs.getString("TransferPrefixPda"));
                objGlobal.setTransferPrefixRobo(rs.getString("TransferPrefixRobo"));
            }

            if (objGlobal.getTransferPrefixPda().equals("") || objGlobal.getTransferPrefixRobo().equals("")) {
                objGlobal.setErrorMessage("Transfer Prefix not set, Please Contact IT");
                return false;
            }

            //robo api details
            String roboChuteStatusAPI = "", roboChuteMapingAPI = "", roboSortTaskAPI = "", roboLabelInfoAPI = "", roboServerIP = "";
            String roboChuteStatusAPIToken = "", roboChuteMapingAPIToken = "";
            if (objGlobal.getCountryCode().equals("UAE")) {
                if (objGlobal.getWarehouse().equals("TECHNO")) {
                    //TECHNO ROBO
                    roboServerIP = "192.168.11.67";
                    roboChuteStatusAPI = "http://192.168.8.13:8511/api/wms-send-chute-status-to-wcs/";
                    roboChuteMapingAPI = "http://192.168.8.13:8511/api/wms-sort-plan/";
                    roboSortTaskAPI = "http://192.168.8.14:18151/Conveyor/WCS151/";
                    roboLabelInfoAPI = "http://192.168.8.14:18153/Conveyor/WCS153/";
                    roboChuteStatusAPIToken = "";
                    roboChuteMapingAPIToken = "";
                } else if (objGlobal.getWarehouse().equals("JAFZA")) {
                    //JAFZA ROBO
                    roboServerIP = "10.23.8.251";
                    roboChuteStatusAPI = "http://bfljbl.fortiddns.com:3001/api/v1/wms/chute/";
                    roboChuteMapingAPI = "https://api.iqhybrid.com/api/v3/wes/bfl/chutes/mapping/";
                    roboSortTaskAPI = "";
                    roboLabelInfoAPI = "";
                    roboChuteStatusAPIToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDMzMTgyNzEsImlkIjoiaXEiLCJ1c2VybmFtZSI6ImlxIiwidXVpZCI6IjAwM2ZhYWFiLTYxMjYtNGExZi05OTQ0LTZlYmVkZGY4ZjIzNyJ9.SHn1sVspVECVdemXaZ7HK0iAAj_Owax8sOnhfgZBHrE";
                    roboChuteMapingAPIToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxMSIsImp0aSI6IjJmMzZiODM5MjAxZDAwZjhjNWViNzc0YTMxOWJjZDA3ZjMwODFiMmIzYWU0ZWYzYmQ3NzUzYTQzMWU0OGU3ODQxMTRjYjRhYmIxMDFjYmE4IiwiaWF0IjoxNzY5MjM5OTUzLjcyMzQ2NiwibmJmIjoxNzY5MjM5OTUzLjcyMzQ2NywiZXhwIjoxODMyMzExOTUzLjcxOTEwOSwic3ViIjoiMTQzIiwic2NvcGVzIjpbXX0.cg_hwNSyi3zOXvixJPRK_FQObQLks4-fH1YVtepp0YpZfnNhtstosTSwH3mWJP45JMmSfGzCnj1MiSmOi7zKXRtYHgYjdo0Bfzs-AIYa7yxjEfmQrLvKoRTiRT07AzTQRcS_b6AB-fCkuzojnSvwScB_-d9hKkkhey234YHLLGBJpNDMTtrsUIZiQEo7OSEEWZ4FpfxrNlyJIJMMT9gg3OvFfEklR8oqMiTZOgsOSsOIbBYPb0J1n6H7qyuGB5reSIj9uzDNXMjTmQtQJR-THVOJga7LSajP48ncHJfMimNrfoUDf6FnIX7WyeeFd--D4aZ44P7geKZ0Yqp5K2888PcB7FpBgSo1_VKu4X5S7-NNDVmv8Y6NA5WMj0l-EKhEWzAQxoJEENHU6C9DJqrMUC5SJDkTN8_5X7YvUxUbMOIpT2Vx8MXLCYwLzobvx8q8iZfwTLM7Pz7jHuJyA1qSA4H-pan37T9JrgGnsMz0fthFOOKv_8hJ2hoRbkHK13tf98pSFlbH19u4-6D5zR83hgbNQZNf_AsdL2SWwYibo85yziSl_tPWdQOU4Ipk_QCybKGu27QIaDKyY0xAWII8cKktZwtQa8FSyyo5kdsDNrfsfDzYK00iLxPXeLs8Tvi6vCiZ6bplBQexcOTGTmV95zYD4yb_nrS31m88x2TIn0M";
                } else if (objGlobal.getWarehouse().equals("YOTO")) {
                    //YOTO ROBO
                    roboServerIP = "192.168.11.67";
                    roboChuteStatusAPI = "";
                    roboChuteMapingAPI = "";
                    roboSortTaskAPI = "";
                    roboLabelInfoAPI = "";
                    roboChuteStatusAPIToken = "";
                    roboChuteMapingAPIToken = "";
                } else {
                    roboServerIP = "";
                    roboChuteStatusAPI = "";
                    roboChuteMapingAPI = "";
                    roboSortTaskAPI = "";
                    roboLabelInfoAPI = "";
                    roboChuteStatusAPIToken = "";
                    roboChuteMapingAPIToken = "";
                }
            } else if (objGlobal.getCountryCode().equals("KSA")) {
                //KSA ROBO
                roboServerIP = "10.70.240.223";
                roboChuteStatusAPI = "http://bflksawh1.fortiddns.com:3001/api/v1/wms/chute/";
                roboChuteMapingAPI = "https://api.iqhybrid.com/api/v3/wes/bfl/chutes/mapping/";
                roboSortTaskAPI = "http://10.70.8.13:3001/api/v1/wms/tote/details/";
                roboLabelInfoAPI = "http://10.70.8.13:3001/api/v1/wms/print/";
                roboChuteStatusAPIToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDMzMTgyNzEsImlkIjoiaXEiLCJ1c2VybmFtZSI6ImlxIiwidXVpZCI6IjAwM2ZhYWFiLTYxMjYtNGExZi05OTQ0LTZlYmVkZGY4ZjIzNyJ9.SHn1sVspVECVdemXaZ7HK0iAAj_Owax8sOnhfgZBHrE";
                    roboChuteMapingAPIToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxMSIsImp0aSI6IjJmMzZiODM5MjAxZDAwZjhjNWViNzc0YTMxOWJjZDA3ZjMwODFiMmIzYWU0ZWYzYmQ3NzUzYTQzMWU0OGU3ODQxMTRjYjRhYmIxMDFjYmE4IiwiaWF0IjoxNzY5MjM5OTUzLjcyMzQ2NiwibmJmIjoxNzY5MjM5OTUzLjcyMzQ2NywiZXhwIjoxODMyMzExOTUzLjcxOTEwOSwic3ViIjoiMTQzIiwic2NvcGVzIjpbXX0.cg_hwNSyi3zOXvixJPRK_FQObQLks4-fH1YVtepp0YpZfnNhtstosTSwH3mWJP45JMmSfGzCnj1MiSmOi7zKXRtYHgYjdo0Bfzs-AIYa7yxjEfmQrLvKoRTiRT07AzTQRcS_b6AB-fCkuzojnSvwScB_-d9hKkkhey234YHLLGBJpNDMTtrsUIZiQEo7OSEEWZ4FpfxrNlyJIJMMT9gg3OvFfEklR8oqMiTZOgsOSsOIbBYPb0J1n6H7qyuGB5reSIj9uzDNXMjTmQtQJR-THVOJga7LSajP48ncHJfMimNrfoUDf6FnIX7WyeeFd--D4aZ44P7geKZ0Yqp5K2888PcB7FpBgSo1_VKu4X5S7-NNDVmv8Y6NA5WMj0l-EKhEWzAQxoJEENHU6C9DJqrMUC5SJDkTN8_5X7YvUxUbMOIpT2Vx8MXLCYwLzobvx8q8iZfwTLM7Pz7jHuJyA1qSA4H-pan37T9JrgGnsMz0fthFOOKv_8hJ2hoRbkHK13tf98pSFlbH19u4-6D5zR83hgbNQZNf_AsdL2SWwYibo85yziSl_tPWdQOU4Ipk_QCybKGu27QIaDKyY0xAWII8cKktZwtQa8FSyyo5kdsDNrfsfDzYK00iLxPXeLs8Tvi6vCiZ6bplBQexcOTGTmV95zYD4yb_nrS31m88x2TIn0M";
            } else {
                //Other Countries
                roboServerIP = objGlobal.getServerIP();
                roboChuteStatusAPI = "";
                roboChuteMapingAPI = "";
                roboSortTaskAPI = "";
                roboLabelInfoAPI = "";
                roboChuteStatusAPIToken = "";
                roboChuteMapingAPIToken = "";
            }
            objGlobal.setRoboServerIP(roboServerIP);
            objGlobal.setRoboChuteStatusAPI(roboChuteStatusAPI);
            objGlobal.setRoboChuteMapingAPI(roboChuteMapingAPI);
            objGlobal.setRoboSortTaskAPI(roboSortTaskAPI);
            objGlobal.setRoboLabelInfoAPI(roboLabelInfoAPI);
            objGlobal.setRoboChuteStatusAPIToken(roboChuteStatusAPIToken);
            objGlobal.setRoboChuteMapingAPIToken(roboChuteMapingAPIToken);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:getControlMain4:" + ex);
            return false;
        }
        return true;
    }

    public String replaceString(String str){
        str=str.replaceAll("\n","");
        str=str.replaceAll("\r","");
        return str;
    }

    public String seperateBarcode(String barcode){
        String[] parts;
        String part1;
        int i;
        if(barcode.contains("/")) {
            parts = barcode.split("/");
            part1=parts[0];
        } else {
            part1=barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }

    public static int getBatteryPercentage(Context context) {
        /*if (Build.VERSION.SDK_INT >= 21) {
            BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {*/
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        double batteryPct = level / (double) scale;
        return (int) (batteryPct * 100);
        //}
    }

    public String getCountryDb(String country) {
        String db = "";
        switch (country) {
            case "UAE":
                db = "BFLDATA";
                break;
            case "OMAN":
                db = "BFLOMAN";
                break;
            case "KUWAIT":
                db = "BFLKUWAIT";
                break;
            case "QATAR":
                db = "BFLQATAR";
                break;
            case "KSA":
                db = "BFLKSA";
                break;
            case "BAHRAIN":
                db = "BFLBAHRAIN";
                break;
            case "MALAYSIA":
                db = "BFLMYS";
                break;
            case "3PL":
                db = "BFLDATA";
                break;
            default:
                db = "BFLDATA";
        }
        return db;
    }
}
