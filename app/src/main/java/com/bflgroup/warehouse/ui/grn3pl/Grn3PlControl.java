package com.bflgroup.warehouse.ui.grn3pl;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Grn3PlControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private Grn3PlGlobal objR1WhGrnGlobal = Grn3PlGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public Grn3PlControl() {
        objGlobal.setDbName("USA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("Grn3PlControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("USA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("Grn3PlControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    boolean validateTransferScan3Pl(String trfNo) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(trfNo)) {
            objGlobal.setErrorMessage("Please scan Transfer No. / Box No. / Tote ID.");
            return false;
        }
        String trfNoM = "", toteIdM = "", trfDateM = "", saved = "N";
        int qtyM = 0;
        try {
            objR1WhGrnGlobal.setPalletStatus("RACK");
            objR1WhGrnGlobal.setSimProcessId("");
            trfNoM=trfNo;
            if (!dbConnection.insertUpdate("delete from BFLDATA.dbo.tmpR1WhGrn where trfno='" + trfNoM + "'", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from USA.dbo.R1WHGRNdetail where trfno='" + trfNoM + "'", objGlobal.getConnection());
            if (rs.next()) {
                saved = "Y";
                objGlobal.setErrorMessage("Transfer number " + trfNoM + " GRN already done");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.tmpR1WhGrn values('" + objGlobal.getDeviceName() + "','" + trfNoM + "','" +
                    toteIdM + "','" + trfDateM + "'," + qtyM + ",'" + objR1WhGrnGlobal.getPalletStatus() + objR1WhGrnGlobal.getSimProcessId() + "','" + saved + "',getdate())", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PalletStatusControl.getPalletStatus : " + e.toString());
            return false;
        }
    }

    ArrayList<Grn3PlTicket> loadScannedTransfers() {
        ArrayList<Grn3PlTicket> listScannedTransfers = new ArrayList<Grn3PlTicket>();
        try {
            listScannedTransfers.clear();
            rs = dbConnection.getResultSet("select TrfNo,ToteId,TrfDate=convert(varchar,TrfDate,103),qty,PltStatus,ForSave from bfldata.dbo.tmpR1WhGrn where " +
                    "device='" + objGlobal.getDeviceName() + "' order by scandt desc", objGlobal.getConnection());
            while (rs.next()) {
                listScannedTransfers.add(new Grn3PlTicket(rs.getString("TrfNo").toString(), rs.getString("ToteId").toString(),
                        rs.getString("TrfDate").toString(), rs.getString("qty").toString(),
                        rs.getString("PltStatus").toString(),rs.getString("ForSave").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("Grn3PlControl:loadScannedTransfers:" + ex.toString());
            return null;
        }
        return listScannedTransfers;
    }

    boolean save3plWhGrn(String remarks) {
        String sno = "",prefix=objGlobal.getExportCountryCode();
        try {
            if (!checkConnection()) {
                return false;
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorMessage("saveR1WhGrn:001:");
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpR1WhGrn where ForSave='N' and device='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Nothing scan");
                return false;
            }
            prefix = "3PL" + objGlobal.getExportCountryCode();
            rs = dbConnection.getResultSet("select trfno from R1WHGRNdetail where trfno in(select trfno from bfldata.dbo.tmpR1WhGrn where device='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Transfer number " + rs.getString("trfno").toString() + " GRN already done");
                return false;
            }
            rs = dbConnection.getResultSet("select sn=isnull(max(replace(sn,'" + prefix + "','')),0)+1 from usa.dbo.R1WHGRNHeader where left(sn,"+prefix.length()+")='" + prefix + "'", objGlobal.getConnection());
            if (rs.next()) {
                sno = prefix + String.format("%06d", rs.getInt("sn"));
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into R1WHGRNHeader(Sn,Trndate,UserId,Remarks,Country,Warehouse) values ('" + sno + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserId() + "'," +
                    "'" + remarks + "','" + objGlobal.getCountryCode() + "','" + objGlobal.getWorkLocation() + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into R1WHGRNdetail(SN,TRFNO,ToteId,Remarks) select '" + sno + "',trfno,toteid,PltStatus from bfldata.dbo.tmpR1WhGrn where ForSave='N' and " +
                    "device='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("Grn3PlControl:save:ex1:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("Grn3PlControl:save:ex2:" + e.toString());
                return false;
            }
            return false;
        }
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpR1WhGrn where Device='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("Grn3PlControl:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }
}
