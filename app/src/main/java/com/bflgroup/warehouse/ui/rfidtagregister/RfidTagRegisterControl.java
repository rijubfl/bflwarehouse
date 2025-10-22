package com.bflgroup.warehouse.ui.rfidtagregister;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RfidTagRegisterControl {
    private boolean b_Result;
    private ResultSet rs;
    private ResultSet rsDet;
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean firstGrn;

    public RfidTagRegisterControl() {
        checkConnection();
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferRfidControl : Local Connection error");
            return false;
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferRfidControl : Fetch Time error");
            return false;
        }
        return true;
    }



}
