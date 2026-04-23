package com.bflgroup.warehouse.ui.rfidtagregister;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RfidTagRegisterControl {
    private boolean b_Result;
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

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

    public boolean saveRFIDTags(List<String> epcRfidTags) {
        String scanEpcOrg = "", scanEpc = "";
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("drop table if exists #newRfids", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("create table #newRfids(orgrfid varchar(200),rfid varchar(200),supplier varchar(150),stktype varchar(2),rfidtype varchar(2))", objGlobal.getConnection())) {
                return false;
            }
            for (int i = 0; i < epcRfidTags.size() - 1; i++) {
                scanEpc = epcRfidTags.get(i).toUpperCase().trim();
                if (!scanEpc.isEmpty()) {
                    if (scanEpc.startsWith("4000") || scanEpc.startsWith("4400") || scanEpc.startsWith("5400") || scanEpc.startsWith("3000") || scanEpc.startsWith("3400"))
                        scanEpcOrg = scanEpc;
                    else
                        scanEpcOrg = scanEpc.substring(4);
                    if (!dbConnection.insertUpdate("insert into #newRfids values('" + scanEpc + "','" + scanEpcOrg + "','','','')", objGlobal.getConnection())) {
                        return false;
                    }
                }
            }
            if (!dbConnection.insertUpdate("update #newRfids set stktype=b.stktype,rfidtype=b.rfidtype,supplier=b.supplier from #newRfids a,bfldata.dbo.RFIDTypeMaster b where substring(a.rfid,1,4)=b.RFIDSeries", objGlobal.getConnection())) {
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.RFIDMaster(RFID,TrnDate,UserId,StkType,Supplier,OrgRFID,ExportDate,BoxNo,ContNo,RFIDType) " +
                    "select RFID,convert(varchar,getdate(),103)," + objGlobal.getUserId() + ",StkType,Supplier,OrgRFID,convert(varchar,getdate(),103),'','',RFIDType from #newRfids where " +
                    "rfid not in(select rfid from BFLDATA.dbo.RFIDMaster)", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
        } catch (Exception exception) {
            try {
                objGlobal.getConnection().rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("saveRFIDTags:sqlException: " + sqlException);
                return false;
            }
            objGlobal.setErrorMessage("saveRFIDTags: exception: " + exception);
            return false;
        }
        return true;
    }

}
