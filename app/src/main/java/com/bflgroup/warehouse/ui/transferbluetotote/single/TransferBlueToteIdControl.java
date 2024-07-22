package com.bflgroup.warehouse.ui.transferbluetotote.single;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;

public class TransferBlueToteIdControl {


    private boolean b_Result;
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;

    public TransferBlueToteIdControl() {
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Local Connection error");
        }
        objGlobal.setCloudDbName("BFLDATA");
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoiceControl.SalesInvoiceControl : Fetch Time error");
        }
    }

    public boolean validateCartonboxno(String cartonboxtoteTrfId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(cartonboxtoteTrfId)) {
            objGlobal.setErrorMessage("Carton box  tote Id is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from BlueToteIDMaster  where   left(ToteID ,1)<>'B' and   toteid='" + cartonboxtoteTrfId + "' ", objGlobal.getConnection());

            if (rs==null) {
                objGlobal.setErrorMessage("Carton box is invalid");
                return false;
            }
            rs = dbConnection.getResultSet("select * from upcboxhead where  ToteID ='"+cartonboxtoteTrfId+"' and closed='N'", objGlobal.getConnection());

            if (rs.next()) {
                objGlobal.setErrorMessage("Carton box tote Id is invalid");
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    public boolean saveTransferboxno(String blueboxtoteTrfId,String cartonboxtoteTrfId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(blueboxtoteTrfId)) {
            objGlobal.setErrorMessage("Blue box  tote Id is empty");
            return false;
        }
        if (TextUtils.isEmpty(cartonboxtoteTrfId)) {
            objGlobal.setErrorMessage("Carton box  tote Id is empty");
            return false;
        }
        try {

            b_Result = dbConnection.insertUpdate("Insert into ToteIDTransferLog (OldBoxNo,NewBoxNo,warehouse,UserId,Trndate ) values ('" + blueboxtoteTrfId+ "','"+cartonboxtoteTrfId + "','yoto',"+objGlobal.getUserId() +  ",'" + objGlobal.getServerDate()+  "'", objGlobal.getConnection());


            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("update  upcboxhead  set ToteID='"+cartonboxtoteTrfId+"'  where   ToteID ='"+blueboxtoteTrfId+"' and closed='N'" +
                    " ", objGlobal.getConnection());

            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }

        } catch (Exception e) {
            objGlobal.setErrorMessage(e+"");
        }
        return true;
    }


    public boolean validateBluetoteID(String blueboxtoteTrfId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(blueboxtoteTrfId)) {
            objGlobal.setErrorMessage("Blue tote Id is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from upcboxhead where ToteID ='" + blueboxtoteTrfId + "' and closed='N'", objGlobal.getConnection());

            if (!rs.next()) {
                objGlobal.setErrorMessage("Blue tote Id is invalid");
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }


    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoiceControl.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoiceControl.connectCloudDb : Connection error");
                return false;
            }
        }
        return true;
    }
}
