package com.bflgroup.warehouse.ui.generatebarcode;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class GenerateBarcodeControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    private GenerateBarcodeGlobal objGenerateBarcodeGlobal = GenerateBarcodeGlobal.getInstance();

    public GenerateBarcodeControl(Context context) {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("GenerateBarcodeControl : Connection error");
        }

    }
    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("GenerateBarcodeControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }



    public String generateBarcode(){
        String barcode = "";





     return barcode;
    }


    public Boolean validateBoxno(String Boxno){
        try {
        if (!checkConnection()) {
            return false;
        }
        rs = dbConnection.getResultSet("select * from usa..vupcBoxDet where boxno = '"+Boxno.trim()+"' and closed = 'N'", objGlobal.getConnection());

            if (rs.next()){
                return true;
            }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

        return false;
    }

    public Boolean validateItemcode(String itemcode){
        try {
        if (!checkConnection()) {
            return false;
        }
        rs = dbConnection.getResultSet("select * from HODATA..itemMaster where itemcode = '"+itemcode.trim()+"'", objGlobal.getConnection());

            if (rs.next()){
                return true;
            }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return false;
    }




    public Boolean validateBoxItemcode(String itemcode,String Boxno){
        try {
            if (!checkConnection()) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from usa..vupcBoxDet where boxno = '"+Boxno.trim()+"' and itemcode = '"+itemcode.trim()+"' and closed = 'N'", objGlobal.getConnection());

            if (rs.next()){
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public int getCode(){

        int num = 0;



        return num;
    }


    public boolean getNoAuto() {
        int pltSn = 0;
        try {
            rs = dbConnection.getResultSet("select sn=isnull(Max(sn)+1,1) from bfldata..generateBarcode", objGlobal.getConnection());
            if (rs.next()) {
                objGenerateBarcodeGlobal.setPalletSno(String.format("%04d",Integer.parseInt(rs.getString("sn").toString())));
            }
            rs = dbConnection.getResultSet("select sn=isnull(max(substring(generatedBarcode,7,10))+1, 1) from bfldata..generateBarcode", objGlobal.getConnection());
            if (rs.next()) {
                pltSn = Integer.parseInt(rs.getString("sn").toString());
            }
            objGenerateBarcodeGlobal.setPalletNo(String.format("%04d", pltSn));
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UpdateBarcode:GetUpdateBarcode:" + ex.toString());
            return false;
        }
        return true;
    }


    public ArrayList<BarcodeGeneratedItem> UpdateBarcode(String itemcode, String Boxno){
        ArrayList<BarcodeGeneratedItem> arr = new ArrayList<BarcodeGeneratedItem>();
        ArrayList<BarcodeGeneratedItem> arrayList = new ArrayList<>();
        Date d = new Date();
        CharSequence s  = DateFormat.format("ddMM ", d.getTime());
        Log.e("getdate",s.toString());
        int getCode;

        getNoAuto();
        try {

            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }

            String generatedBarcode = "UPC"+s.toString().trim()+objGenerateBarcodeGlobal.getPalletSno();

            String query1 = "insert into BFLDATA..generateBarcode(Date,time,itemcode,generatedBarcode,Boxno,userid,deviceId) " +
                    "values('"+objGlobal.getServerDate()+"', '"+objGlobal.getServerTime()+"', '"+itemcode+"', '"+generatedBarcode+"', '"+Boxno+"', '"+objGlobal.getUserName()+"', '"+objGlobal.getDeviceName()+"')";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                Log.e("Error Query", query1);
            }

            String Query = "select top 50 * from BFLDATA..generateBarcode where deviceId ='"+objGlobal.getDeviceName()+"' order by time desc";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());

            while (rs.next()){
                arr.add(new BarcodeGeneratedItem(rs.getString("Date"),rs.getString("itemcode"),rs.getString("generatedBarcode")));
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return arr;
    }

    public void CloseBox(String Boxno){
        try {

            rs = dbConnection.getResultSet("select * from  BFLDATA..generateBarcode where '" + Boxno + "' and deviceId ='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }




}
