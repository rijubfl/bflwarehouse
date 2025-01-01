package com.bflgroup.warehouse.ui.r1whgrn;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class R1WhGrnControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private R1WhGrnGlobal objR1WhGrnGlobal = R1WhGrnGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public R1WhGrnControl() {
        objGlobal.setDbName("USA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("R1WhGrnControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("USA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("R1WhGrnControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    boolean validateTransferScanWarehouse(String trfNo) {
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
            rs = dbConnection.getResultSet("select top 1 refno=TrfNo,Toteid=StoreIssue,TrfDate=convert(varchar,TrfDate,103),qty=0 from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where " +
                    "trfno='" + trfNo + "' order by TrfDate desc", objGlobal.getConnection());
            if (rs.next()) {
                trfNoM = rs.getString("refno").toString();
                toteIdM = rs.getString("Toteid").toString();
                qtyM = rs.getInt("qty");
                trfDateM = rs.getString("TrfDate");
            } else {
                rs = dbConnection.getResultSet("select top 1 refno=TrfNo,Toteid=StoreIssue,TrfDate=convert(varchar,TrfDate,103),qty=0 from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where " +
                        "storeissue='" + trfNo + "' order by TrfDate desc", objGlobal.getConnection());
                if (rs.next()) {
                    trfNoM = rs.getString("refno").toString();
                    toteIdM = rs.getString("Toteid").toString();
                    qtyM = rs.getInt("qty");
                    trfDateM = rs.getString("TrfDate");
                } else {
                    rs = dbConnection.getResultSet("select refno=boxno,Toteid=toteid,TrfDate=trndate,qty=sum(qty) from usa.dbo.vupcboxdet where BoxNo='" + trfNo + "' and closed='N' " +
                            "group by boxno,toteid,trndate ", objGlobal.getConnection());
                    if (rs.next()) {
                        trfNoM = rs.getString("refno").toString();
                        toteIdM = rs.getString("Toteid").toString();
                        qtyM = rs.getInt("qty");
                        trfDateM = rs.getString("TrfDate");
                    } else {
                        rs = dbConnection.getResultSet("select refno=boxno,Toteid=toteid,TrfDate=trndate,qty=sum(qty) from usa.dbo.vupcboxdet where ToteID='" + trfNo + "' and closed='N' " +
                                "group by boxno,toteid,trndate", objGlobal.getConnection());
                        if (rs.next()) {
                            trfNoM = rs.getString("refno").toString();
                            toteIdM = rs.getString("Toteid").toString();
                            qtyM = rs.getInt("qty");
                            trfDateM = rs.getString("TrfDate");
                        } else {
                            objGlobal.setErrorMessage("validateTransfer:Invalid Transfer number");
                            return false;
                        }
                    }
                }
            }
            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.CloseR1pallet where palletno='" + trfNoM + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("validateTransfer: Transfer No. / Box No. " + trfNoM + " already closed");
                return false;
            }
            rs = dbConnection.getResultSet("select * from tempdata.dbo.SIMProdReadyPalletsList where (PalletNo='" + trfNoM + "' or BoxNo='" + trfNoM + "')", objGlobal.getConnection());
            if (rs.next()) {
                objR1WhGrnGlobal.setPalletStatus("PRODUCTION");
                objR1WhGrnGlobal.setSimProcessId("-" + rs.getString("ProcessNo").toString());
            } else {
                objR1WhGrnGlobal.setPalletStatus("RACK");
                objR1WhGrnGlobal.setSimProcessId("");
            }
            if (!dbConnection.insertUpdate("delete from BFLDATA.dbo.tmpR1WhGrn where trfno='" + trfNoM + "'", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from USA.dbo.R1WHGRNdetail where trfno='" + trfNoM + "'", objGlobal.getConnection());
            if (rs.next()) {
                saved = "Y";
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

    ArrayList<R1WhGrnTicket> loadScannedTransfers() {
        ArrayList<R1WhGrnTicket> listScannedTransfers = new ArrayList<R1WhGrnTicket>();
        try {
            listScannedTransfers.clear();
            rs = dbConnection.getResultSet("select TrfNo,ToteId,TrfDate=convert(varchar,TrfDate,103),qty,PltStatus,ForSave from bfldata.dbo.tmpR1WhGrn where " +
                    "device='" + objGlobal.getDeviceName() + "' order by scandt desc", objGlobal.getConnection());
            while (rs.next()) {
                listScannedTransfers.add(new R1WhGrnTicket(rs.getString("TrfNo").toString(), rs.getString("ToteId").toString(),
                        rs.getString("TrfDate").toString(), rs.getString("qty").toString(),
                        rs.getString("PltStatus").toString(),rs.getString("ForSave").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("R1WhGrnControl:loadScannedTransfers:" + ex.toString());
            return null;
        }
        return listScannedTransfers;
    }

    boolean saveR1WhGrn(String remarks) {
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
            if (!dbConnection.insertUpdate("insert into R1WHGRNHeader(Sn,Trndate,UserId,Remarks,Country) values ('" + sno + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserId() + "'," +
                    "'" + remarks + "','" + objGlobal.getCountryCode() + "')", objGlobal.getConnection())) {
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
                objGlobal.setErrorMessage("R1WhGrnControl:save:ex1:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("R1WhGrnControl:save:ex2:" + e.toString());
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
            objGlobal.setErrorMessage("R1WhGrnControl:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }
}
