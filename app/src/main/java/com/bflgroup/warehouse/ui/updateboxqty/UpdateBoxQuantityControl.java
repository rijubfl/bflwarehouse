package com.bflgroup.warehouse.ui.updateboxqty;


import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UpdateBoxQuantityControl {

    // private DBConnection dbConnection = new DBConnection();

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private ResultSet rs;

    public UpdateBoxQuantityControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("UpdateBoxQuantityControl : Connection error");
        }

    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("UpdateBoxQuantityControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public ArrayList<UpdateBoxItem> ScanToteId(Context context, String Toteid) {
        ResultSet rs1;
        ArrayList<UpdateBoxItem> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            String condition="BoxNo like 'U%'";
            if(objGlobal.getWarehouse().equals("RUKOON") || objGlobal.getWarehouse().equals("BFLKSA")) condition="BoxNo like 'S%'";
            if(objGlobal.getWarehouse().equals("BFLKUWAIT")) condition="BoxNo like 'K%'";
            arr = new ArrayList<UpdateBoxItem>();
            String query1 = "select * from BFLdata..tmpUpdateBoxQty where Toteid='" + Toteid + "' and DeviceName = '" + objGlobal.getDeviceName() + "'";
            rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
            if (!rs.next()) {
                String que1 = "select count(*) count from usa..upcboxHead where (toteid = '" + Toteid + "' or BoxNo = '" + Toteid + "' ) and (" + condition + ") and closed = 'N'";
                Log.e("Alter", que1);
                ResultSet resultSet = dbConnection.getResultSet(que1, objGlobal.getConnection());
                int Count = 0;
                while (resultSet.next()) {
                    Count = resultSet.getInt(1);
                }
                if(Count > 1){
                    Log.e("size",Count+"");
                    okMessage("Alert", "Multiple Boxno found for this tote", context);
                }
                else {
                    String query = "select * from usa..upcboxHead where (toteid = '" + Toteid + "' or BoxNo = '" + Toteid + "' ) and ((" + condition + ") or " +
                            "boxno in (select BoxNo from usa.dbo.BoxAllowForEdit where contno not in (select Contno from bfldata..BuildingCompletion)) ) and closed = 'N'";
                    ResultSet rs2 = dbConnection.getResultSet(query, objGlobal.getConnection());
                    Log.e("Query select", query);
                    if (rs2.next()) {
                        String query3 = "select * from usa..upcboxdet where BoxNo = '" + rs2.getString("BoxNo") + "' and ((" + condition + ") or boxno in (select BoxNo from usa.dbo.BoxAllowForEdit where contno not in (select Contno from bfldata..BuildingCompletion)))";
                        Log.e("Query select 3", query3);
                        rs1 = dbConnection.getResultSet(query3, objGlobal.getConnection());
                        while (rs1.next()) {
                            arr = TmpInsertItem(context, Toteid, rs1.getString("Itemcode"), Integer.parseInt(rs1.getString("Qty")), 0, rs2.getString("BoxNo"));
                        }
                        UpdateBoxSharedRef.saveToteid(Toteid);
                    } else {
                        okMessage("Alert", "Toteid/Boxno is Invalid or Box found in Building Completion ", context);
                        return null;
                    }
                }
            }
            String query2 = "select * from BFLDATA..tmpUpdateBoxQty where Toteid='" + Toteid + "' and DeviceName = '" + objGlobal.getDeviceName() + "'";
            Log.e("Query select 1",query2);
            ResultSet result = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (result.next()) {
                arr.add(new UpdateBoxItem(result.getString("Itemcode"), result.getString("BoxQuantity"), result.getString("Qty"), result.getString("NewBoxQty")));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public ArrayList<UpdateBoxItem> ScanItemCode(Context context, String Toteid, String Itemcode, String addminus) throws SQLException {
        ResultSet rs1;
        String query1;
        ArrayList<UpdateBoxItem> arr = new ArrayList<UpdateBoxItem>();
        String query = "select * from BFLdata..tmpUpdateBoxQty where (Toteid='" + Toteid + "' and Itemcode = '" + Itemcode + "') and DeviceName = '" + objGlobal.getDeviceName() + "'";
        Log.e("Select query", query);
        rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (rs.next()) {
            int qty = 0;
            if (addminus.equals("Add")) {
                qty = Integer.parseInt(rs.getString("qty")) + 1;
            } else {
                qty = Integer.parseInt(rs.getString("qty")) - 1;
            }
            int NewBoxQty = Integer.parseInt(rs.getString("BoxQuantity")) + qty;
            if (NewBoxQty >= 0) {
                query1 = "Update BFLdata..tmpUpdateBoxQty set qty = '" + qty + "', NewBoxQty = '" + NewBoxQty + "' where (Toteid='" + Toteid + "' and Itemcode = '" + Itemcode + "' and BoxNo = '" + rs.getString("BoxNo") + "') and DeviceName = '" + objGlobal.getDeviceName() + "'";
                Log.e("update query", query1);
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                }
            }
        } else {
            if (addminus.equals("Add")) {
                String query2 = "Select * from Hodata..viTemMaster where itemcode  = '" + Itemcode + "'";
                rs1 = dbConnection.getResultSet(query2, objGlobal.getConnection());
                if (rs1.next()) {
                    String que = "insert into BFLdata..tmpUpdateBoxQty select '" + Toteid + "', '" + Itemcode + "', Boxno , 0, 1, '" + objGlobal.getDeviceName() + "', 1, 1 from usa..upcboxHead where (toteid = '" + Toteid + "' or BoxNo = '" + Toteid + "') and Closed = 'N'";
                    Log.e("queryinsert", que);
                    if (!dbConnection.insertUpdate(que, objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                    }
                } else {
                    okMessage("Alert", "Invalid Itemcode", context);
                }
            }else {

                String query2 = "Select * from usa..vupcboxdet where itemcode  = '" + Itemcode + "' and (boxno='" + Toteid + "' or toteid='" + Toteid + "')";
                rs1 = dbConnection.getResultSet(query2, objGlobal.getConnection());
                if (rs1.next()) {
                    String que = "insert into BFLdata..tmpUpdateBoxQty select '" + Toteid + "', '" + Itemcode + "', Boxno , 0, 1, '" + objGlobal.getDeviceName() + "', 1, 1 from usa..upcboxHead where (toteid = '" + Toteid + "' or BoxNo = '" + Toteid + "') and Closed = 'N'";
                    Log.e("queryinsert", que);
                    if (!dbConnection.insertUpdate(que, objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                    }
                } else {
                    okMessage("Alert", "Invalid Itemcode", context);
                }
            }
        }
        String query2 = "select * from BFLdata..tmpUpdateBoxQty where Toteid='" + Toteid + "' and DeviceName = '" + objGlobal.getDeviceName() + "' ";
        Log.e("select all query", query2);
        rs1 = dbConnection.getResultSet(query2, objGlobal.getConnection());
        while (rs1.next()) {
            arr.add(new UpdateBoxItem(rs1.getString("Itemcode"), rs1.getString("BoxQuantity"), rs1.getString("Qty"), rs1.getString("NewBoxQty")));
        }
        return arr;
    }

    public ArrayList<UpdateBoxItem> TmpInsertItem(Context context, String Toteid, String itemcode, Integer BoxQuantity, Integer Qty, String BoxNo) throws SQLException {
        Log.e("here", "Reached here");
        ArrayList<UpdateBoxItem> arrayList = new ArrayList<>();
        ResultSet rs1;
        try {
            String query = "insert into BFLDATA..tmpUpdateBoxQty values ('" + Toteid + "','" + itemcode + "','" + BoxNo + "','" + BoxQuantity + "','" + Qty + "', '" + objGlobal.getDeviceName() + "', '', '"+BoxQuantity+"')";
            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
            }
        } catch (Exception e) {
            Log.e("Error message", e.toString());
        }
        return arrayList;
    }

    void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public boolean InsertDetails(String toteid, String itemcode) throws SQLException {
        try {
            String query1 = "select * from BFLdata..tmpUpdateBoxQty where (Toteid='" + toteid + "' or BoxNo = '" + toteid + "') and (Qty < 0 or Qty >0) and DeviceName = '" + objGlobal.getDeviceName() + "'";
            rs = dbConnection.getResultSet(query1, objGlobal.getConnection());

            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into usa..upcboxDet (BoxNo,Itemcode,Qty,QtyIssued,Status,UPC)  select boxno, itemcode, NewBoxQty,0,'',itemcode  from BFLdata..tmpUpdateBoxQty where itemcode NOT In (select itemcode from usa..upcboxDet where boxno IN (select boxno from usa..upcboxhead where (toteid = '" + toteid + "' or BoxNo = '" + toteid + "')))and toteid = '" + toteid + "' and NewBoxQty>0 and DeviceName = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            while (rs.next()) {
                Integer newBoxqty = Integer.parseInt(rs.getString("BoxQuantity")) + Integer.parseInt(rs.getString("qty"));
                String query = "update usa..upcboxDet set qty = " + newBoxqty + " where BoxNo IN (select BoxNo from usa..upcboxHead where (toteid = '" + toteid + "' or BoxNo = '" + toteid + "') and closed = 'N' and itemcode = '" + rs.getString("itemcode") + "') ";
                if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                    Log.e("Query1", query + "");
                    objGlobal.getConnection().rollback();
                    return false;
                }
                String queryNew = "Insert into BFLDATA..updateboxqtydetails values('" + objGlobal.getUserId() + "', '" + toteid + "', '" + rs.getString("itemcode") + "','" + rs.getString("qty") + "','" + rs.getString("NewBoxQty") + "', '" + objGlobal.getServerDate() + "','" + rs.getString("OldQty") + "', '" + objGlobal.getServerTime() + "' )";
                if (!dbConnection.insertUpdate(queryNew, objGlobal.getConnection())) {
                    Log.e("Query1", queryNew + "");
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (Integer.parseInt(rs.getString("Qty")) < 0 && Integer.parseInt(rs.getString("NewBoxQty")) == 0) {
                    String query3 = "delete from usa..upcboxDet where BoxNo IN (select BoxNo from usa..upcboxHead where (toteid = '" + toteid + "' or BoxNo = '" + toteid + "') and closed = 'N' and itemcode = '" + rs.getString("itemcode") + "') ";
                    if (!dbConnection.insertUpdate(query3, objGlobal.getConnection())) {
                        Log.e("Query1", queryNew + "");
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
        } catch (Exception e) {
            Log.e("Alert", e.toString());
        }
        return true;
    }

    public int GetTotalQty(String ToteId){
        int qty = 0;
        String Query = "select sum(BoxQuantity) from bfldata..tmpUpdateBoxQty where toteid = '"+ToteId+"' and DeviceName = '" + objGlobal.getDeviceName() + "'";
        Log.e("Sum(Qty)", Query);
        rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                qty = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qty;
    }

    public int ReducedQty(String ToteId){
        int qty = 0;
        String Query = "select sum(Qty) from bfldata..tmpUpdateBoxQty where toteid = '"+ToteId+"' and DeviceName = '" + objGlobal.getDeviceName() + "'";
        Log.e("Sum(Qty)", Query);
        rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                qty = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qty;
    }
    public int NewBoxQty(String ToteId){
        int qty = 0;
        String Query = "select sum(BoxQuantity) + sum(Qty) from bfldata..tmpUpdateBoxQty where toteid = '"+ToteId+"' and DeviceName = '" + objGlobal.getDeviceName() + "'";
        Log.e("Sum(Qty)", Query);
        rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                qty = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qty;
    }
    public boolean deletetemp() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpUpdateBoxQty where DeviceName = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("tmpPalletScan:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }
}
