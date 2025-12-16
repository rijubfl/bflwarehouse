package com.bflgroup.warehouse.ui.warehousegin;

import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;

public class WarehouseGinControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public WarehouseGinControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("WarehouseGinControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("WarehouseGinControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean loadScanPalletFromAPI(String palletno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpWarehouseGINPallets where deviceid='" + objGlobal.getDeviceName() + "' and palletno='" + palletno + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGINPallets(DeviceId,PalletNo) values ('" + objGlobal.getDeviceName() + "','" + palletno + "')", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinDetails:" + ex);
            return false;
        }
    }

    public boolean clearAll() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpWarehouseGINPallets where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinDetails:" + ex);
            return false;
        }
    }

    public ArrayList<WarehouseGINScanTicket> loadGinScan() {
        ArrayList<WarehouseGINScanTicket> listGinGrnScan = new ArrayList<WarehouseGINScanTicket>();
        try {
            rs = dbConnection.getResultSet("select PalletNo from bfldata.dbo.tmpWarehouseGINPallets where DeviceId='" + objGlobal.getDeviceName() + "' order by ScanDate desc,ScanTime desc", objGlobal.getConnection());
            while (rs.next()) {
                listGinGrnScan.add(new WarehouseGINScanTicket(rs.getString("PalletNo")));
            }
            return listGinGrnScan;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinGrnScan:" + ex.toString());
            return listGinGrnScan;
        }
    }

    public JSONObject loadScanForSave(String warehousefrom,String warehouseto, String deldate,String trailerno,String remarks) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("ginlocation", objGlobal.getCountryCode());
            jsonRequest.put("warehouse", objGlobal.getWarehouse());
            jsonRequest.put("deldate", deldate);
            jsonRequest.put("trailerno", trailerno);
            jsonRequest.put("remarks", "PDA:-"+remarks);
            jsonRequest.put("emailsend", "");
            jsonRequest.put("UserId", objGlobal.getUserId());
            jsonRequest.put("warehousefrom", warehousefrom);
            jsonRequest.put("warehouseto", warehouseto);
            JSONArray detailArray = new JSONArray();
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpWarehouseGINPallets where deviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                JSONObject detailObj = new JSONObject();
                detailObj.put("palletno", rs.getString("palletno"));
                detailArray.put(detailObj);
            }
            jsonRequest.put("detail", detailArray);
            return jsonRequest;
        } catch (Exception e) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadScanGinForApi:" + e);
            return null;
        }
    }

}
