package com.bflgroup.warehouse.comm;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Controls {

    private Global objGlobal = Global.getInstance();
    DBConnection objDBConnection=new DBConnection();
    private ResultSet rs;

    public boolean getControl(){
        try{
            List<String> arr;
            arr = new ArrayList<String>();
            rs = objDBConnection.getResultSet("select menuid,menuname from bfldata.dbo.PdaMenuAccess where Access='Y' and UserId="+ objGlobal.getUserId() +" and " +
                    "MenuId in(select MenuId from bfldata.dbo.PdaMenuMaster where Access='Y')", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("menuid").toString());
            }
            objGlobal.setActiveMenuByUser(arr);
        }catch (Exception ex) {
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
            if (objGlobal.getWorkLocation().equals("3PL")) {
                return true;
            }
            if (objGlobal.getWorkLocation().equals("KSA")) {
                objGlobal.setCountryWiseBoxPrefix("S");
            }
            if (objGlobal.getWorkLocation().equals("BAHRAIN")) {
                objGlobal.setCountryWiseBoxPrefix("B");
            }

            rs = objDBConnection.getResultSet("select BFLDATA.dbo.getClientlocdetails('Warehouse')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setWarehouse(rs.getString(1));
               // objGlobal.setWarehouse("JAFZA");
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
                rs = objDBConnection.getResultSet("select ExportCountryCode,Dataname from bfldata.dbo.DataSettings where countrycode='" + objGlobal.getCountryCode() + "' and " +
                        "ExportWH='Y'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setExportCountryCode(rs.getString("ExportCountryCode"));
                    objGlobal.setCountryDbName(rs.getString("Dataname"));
                } else {
                    objGlobal.setErrorMessage("LoginActivity:getControl:Invalid ExportCountryCode, Contact IT");
                    return false;
                }
            }

            rs = objDBConnection.getResultSet("select * from fabsmain.dbo.settings where descr='SKIPBATCHIN'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getString("status").toString().equals("Y")) objGlobal.setSkipBatchIn(true);
            }
            rs = objDBConnection.getResultSet("select * from fabsmain.dbo.settings where descr='MAXTOTINBIN'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setMaxTotInBin(rs.getInt("status"));
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
                    roboChuteMapingAPIToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxMSIsImp0aSI6IjViMDgwZTZkMGIxNzlkZTQyYTdkMzIyMGQ0NGEwOGZmODlmZTViNDlhOWFiMDU5ZjJjYWExYjhhN2EzYTI4Zjk0MTFmYzg1OTU3NmVkOGI1IiwiaWF0IjoxNzA2MDEzNDU1LjA5MzU2MiwibmJmIjoxNzA2MDEzNDU1LjA5MzU2NCwiZXhwIjoxNzY5MTcxODU1LjA4NzUyLCJzdWIiOiIxNDMiLCJzY29wZXMiOltdfQ.ZO7waPZ_wrOXt0uylS3gCFTTtj8A5R4D270iFECLMILDNN6kVQC2GiS87AuYx3367frzKsvmizdnX1sX0mqZRTqLTjyRLSknaFVPq8wpXn1b_KhZf_EnmwWuXr8aWbEa3YXrwHEd8mi2LzmpmsRVsktwkAk6gawyUbWGqSK51fMEN3MJPSpCmB8QreNfYXDugg47XJtJ_SqmUP7wqinCqsGiT_4uCbN5-bZhdv5b8hbwoLo1JEpI69VDL0iolwtlHXDD6AVnxfUjDJpPfHRfrm6-HRIhcG5oo7CTOmj24iOq9F8ZsH0K8R5W1iI2b8LfXR6YxPHSZ39mbKWAI4rQ-YwIoLx4Vlqk8DPzlPcvwJD_G2KIlZjvOa2DRspB_strOYYHoTQ9uj4CY5WcAeezHneGfvywBkciYLlb0Ot5Tmqbj-rIk-u4JFy1mc01S_c5nHtzuPspvzpwvv1Auo3XYvHDYhcNG5jDVL3vid59S5yfMGHaaeC7F3lj79w6Qow2Vc0jaQ5f0_HdaStxtMxDyIX8IjJ59D4C2pheGPFzk_3SzGStAg5-sBZOk5sSUJgLBRzVfkBNfWnAM0BaxfX-O4KkMuM2_AghnTvIZ-BRw7JPsib2tq_DfX9IbXGkpEOOrtfxD29ntqHxsqRjpldhKbB1cOGbPtReoV_ZYtmyC8Y";
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
            } else {
                //KSA ROBO
                roboServerIP = "10.70.240.223";
                roboChuteStatusAPI = "http://bflksawh1.fortiddns.com:3001/api/v1/wms/chute/";
                roboChuteMapingAPI = "https://api.iqhybrid.com/api/v3/wes/bfl/chutes/mapping/";
                roboSortTaskAPI = "http://10.70.8.13:3001/api/v1/wms/tote/details/";
                roboLabelInfoAPI = "http://10.70.8.13:3001/api/v1/wms/print/";
                roboChuteStatusAPIToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDMzMTgyNzEsImlkIjoiaXEiLCJ1c2VybmFtZSI6ImlxIiwidXVpZCI6IjAwM2ZhYWFiLTYxMjYtNGExZi05OTQ0LTZlYmVkZGY4ZjIzNyJ9.SHn1sVspVECVdemXaZ7HK0iAAj_Owax8sOnhfgZBHrE";
                roboChuteMapingAPIToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxMSIsImp0aSI6ImI3YWE4NTBkN2NkYjc1MjQ2MGQyZjQzMDkyMTVhMTkwZDVjMDAwNmE4MjczZDA1YjQ2Yjg3ZjA5YmNiY2JhYzdkNTA3N2UyNGY0OWFiNjk2IiwiaWF0IjoxNzEzNDQyMzY0LjM1NDcyNSwibmJmIjoxNzEzNDQyMzY0LjM1NDcyNiwiZXhwIjoxNzc2NTE0MzY0LjM1MDY4Miwic3ViIjoiMTk2Iiwic2NvcGVzIjpbXX0.ZY8qQqdO_wr1NrrsYNlFa1wva-1nVlZYC-n8YpqLcnho4655FqlGyP87yttxGffegJH2oeIhoRjnnth-p1SityNJ47dCtPPB9dxy5IahslG3xOscMoI1OzxXeisd1Fb-SezlogB3RDxaACqU8p-96qWZ0bZ5Y_QkkMm1eAHRYcfzt_W2gSEiQrFrfuA7nk4Gn0nAUNU9BWdBdPZe02J8eYKJ7qi9w1Jq43lzPfHNtAU4u2EVyf-Ku3vMQ9nKjE6sBrfZqtzKTpXsccZll_0jgDm1H9x_iicIJWQuzgABDh2C8i3zZUJ9oiSO5XH4CR3yZySpl74HI6JSY9Xi-yOoW8XJOSU9kQez44tWRCz1towHsibLCD1IGpl_KEp7mOAeDQitk8hZjbU6cr7FfAA4w8vzTJcBUR9kk5B3ce7ETSCQjKPgZd_Lr4obfWfX2tClaLaptl1agn8_wVp6mIS4rfCLo8fQW_zcuy80uO-acyIBExs5rfZUwGwRvbIcw6Pd4HCxZJSNpXNp05DmEzc7C3Y_BNKPBCKfLZ1RXP8s05XORI_yGaIYlBB19fP_Qv6IY3FySkvuRzsKCKdmPG03vtxSsod-Uozi_952U7ZwUHJZVS05BsiRBnBWYzI_fbjeRvDpdvRjyXi6-qprJcAJqarZIvo-DnhZNuv6tdOzeCg";
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
}
