package com.bflgroup.warehouse.ui.binstorageputaway;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BinPutAwayControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BinPutAwayGlobal objBinPutAwayGlobal = BinPutAwayGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public BinPutAwayControl() {
        objGlobal.setDbName("RACKS");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("BinPutAwayControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("RACKS");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("BinPutAwayControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateToteid(String warehouse,String direction, String toteId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(direction.trim())) {
            objGlobal.setErrorMessage("Chose direction");
            return false;
        }
        if (TextUtils.isEmpty(toteId.trim())) {
            objGlobal.setErrorMessage("Tote Id is empty");
            return false;
        }
        try {
            objBinPutAwayGlobal.setBoxNo("");
            rs = dbConnection.getResultSet("select boxno from usa.dbo.upcboxhead where ToteID='" + toteId + "' and Closed='N'", objGlobal.getConnection());
            if (rs.next()) {
                objBinPutAwayGlobal.setBoxNo(rs.getString("boxno"));
            } else {
                rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                        "and b.TotId='" + toteId + "' and a.Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    objBinPutAwayGlobal.setBoxNo(rs.getString("boxno"));
                } else {
                    rs = dbConnection.getResultSet("select TrfNo from " + objGlobal.getCountryDbName() + ".dbo.transferheader where (trfno='" + toteId + "' or StoreIssue='" + toteId + "')", objGlobal.getConnection());
                    if(rs.next()){
                        objBinPutAwayGlobal.setBoxNo(rs.getString("TrfNo"));
                    } else {
                        objGlobal.setErrorMessage("Invalid box or box is closed");
                        return false;
                    }
                }
            }
            if (direction.equals("IN")) {
                if (!objGlobal.getSkipBatchIn()) {
                    rs = dbConnection.getResultSet("select * from BinBatchIn where Status='' and toteid='" + toteId + "' and " +
                            "userid=" + objGlobal.getUserId(), objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("ToteID not found in Batch in process");
                        return false;
                    }
                }
                rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and Toteid='" + toteId + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID found in location, " + rs.getString("location").toString());
                    return false;
                }
            }
            if (direction.equals("OUT")) {
                rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and Toteid='" + toteId + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Location is empty for this");
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateToteid:" + ex);
            return false;
        }
    }

    public boolean validateLocation(String warehouse,String location, String direction, String toteId, String boxNo) {
        String rack = "", horizontal = "", vertical = "", fLocation = "";
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(direction)) {
            objGlobal.setErrorMessage("Chose direction");
            return false;
        }
        if (TextUtils.isEmpty(toteId)) {
            objGlobal.setErrorMessage("Tote is empty");
            return false;
        }
        if (TextUtils.isEmpty(boxNo)) {
            objGlobal.setErrorMessage("Box is empty");
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            objGlobal.setErrorMessage("Location is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from BinRackMaster where Warehouse='" + warehouse + "' and Barcode='" + location + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Location, " + location);
                return false;
            } else {
                objBinPutAwayGlobal.setDoubleDeep(rs.getString("DoubleDeep").toString());
                rack = rs.getString("rack").toString();
                horizontal = rs.getString("horizontal").toString();
                vertical = rs.getString("vertical").toString();
            }
            if (direction.equals("IN")) {
                rs = dbConnection.getResultSet("SELECT TOP 1 Direction, SourceLocation FROM (SELECT Direction,TrnDate,TrnTime, Location AS SourceLocation FROM " +
                        "racks..BinPutAwayHistory WHERE BoxNo ='"+boxNo+"' UNION ALL SELECT Direction,TrnDate,TrnTime, RackNo AS SourceLocation FROM " +
                        "racks..TechnoRacksHistory WHERE PalletNo1 ='"+boxNo+"'  OR PalletNo2 ='"+boxNo+"' UNION ALL SELECT Direction," +
                        "TrnDate,TrnTime,RackNo AS SourceLocation FROM racks..WarehouseRackHistory WHERE PalletNo1 ='"+boxNo+"' OR PalletNo2 = " +
                        "'"+boxNo+"') X ORDER BY TrnDate DESC, TrnTime DESC", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getString("Direction").equals("IN")) {
                        objGlobal.setErrorMessage("The box/pallet is already found in "+rs.getString("SourceLocation"));
                        return false;
                    }
                }
                if (objBinPutAwayGlobal.getDoubleDeep().equals("0")) {
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and location in(select barcode from BinRackMaster where rack='" + rack + "' and " +
                            "horizontal='" + horizontal + "' and vertical='" + vertical + "' and doubledeep='1')", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("Double deep Location is empty");
                        return false;
                    }
                }
                if (objBinPutAwayGlobal.getDoubleDeep().equals("1")) {
                    //add
                }
                rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and location='" + location + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Location is used, ToteID:" + rs.getString("toteid").toString());
                    return false;
                }
            }
            if (direction.equals("OUT")) {
                rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and location='" + location + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Location is empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and location='" + location + "' and toteid='" + toteId + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Location and tote id is not match");
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateLocation:" + ex.toString());
            return false;
        }
    }

    public boolean saveBinInOutSingle(String warehouse,String toteId, String boxNo, String direction, String location, String dBeep,String waveId) {
        String rack = "", horizontal = "", vertical = "", fLocation = "";
        b_Result = validateToteid(warehouse,direction, toteId);
        if (!b_Result) {
            return false;
        }
        b_Result = validateLocation(warehouse,location, direction, toteId, boxNo);
        if (!b_Result) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("saveBinInOut:001:");
                return false;
            }
            rs = dbConnection.getResultSet("select * from BinRackMaster where barcode='" + location + "'", objGlobal.getConnection());
            if (rs.next()) {
                rack = rs.getString("rack").toString();
                horizontal = rs.getString("horizontal").toString();
                vertical = rs.getString("vertical").toString();
            }
            rs = dbConnection.getResultSet("select barcode from BinRackMaster where rack='" + rack + "' and horizontal='" + horizontal + "' and " +
                    "vertical='" + vertical + "' and doubledeep='0'", objGlobal.getConnection());
            if (rs.next()) {
                fLocation = rs.getString("barcode").toString();
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into BinPutAwayHistory(Warehouse,TrnDate,TrnTime,ToteId,BoxNo,Direction,Location,UserId,UserName," +
                    "DeviceId) values ('" + warehouse + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + toteId + "','" + boxNo + "'," +
                    "'" + direction + "','" + location + "'," + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "'," +
                    "'" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (direction.equals("IN")) {
                if (!dbConnection.insertUpdate("update BinBatchIn set status='" + location + "',InDateTime=getdate() where toteid='" + toteId + "' and " +
                        "userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into BinRack(Warehouse,Location,ToteId,BoxNo) values ('" + warehouse + "','" + location + "','" + toteId + "'," +
                        "'" + boxNo + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            if (direction.equals("OUT")) {
                if (!dbConnection.insertUpdate("insert into BinRackBck(Trndate,UserId,Warehouse,Location,ToteId,BoxNo,aut) select getdate()," + objGlobal.getUserId() + ",*,'' from " +
                        "BinRack where Warehouse='" + warehouse + "' and location='" + location + "' and ToteId='" + toteId + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("delete from BinRack where Warehouse='" + warehouse + "' and Location='" + location + "' and ToteId='" + toteId + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (dBeep.equals("1")) {
                    if (!dbConnection.insertUpdate("insert into BinRackBck(Trndate,UserId,Warehouse,Location,ToteId,BoxNo,aut) select getdate()," + objGlobal.getUserId() + ",*,'AUTO' from " +
                            "BinRack where Warehouse='" + warehouse + "' and location='" + fLocation + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update BinRack set location='" + location + "' where Warehouse='" + warehouse + "' and location='" + fLocation + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                }
                if (!waveId.equals("N/A")) {
                    if (!dbConnection.insertUpdate("update RACKS.dbo.WavePicking set PickedDate=getdate(),PickedUserName='" + objGlobal.getUserName() + "' where WaveNo='" + waveId + "' and BoxNo='" + boxNo + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:ex:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:e:" + e.toString());
                return false;
            }
            return false;
        }
    }

    ArrayList<BinPutAwayPendingToteIdTicket> loadBinPutAwayPendingToteIdTicket() {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<BinPutAwayPendingToteIdTicket> listBinPutAwayPendingToteIdTicket = new ArrayList<BinPutAwayPendingToteIdTicket>();
        try {
            listBinPutAwayPendingToteIdTicket.clear();
            rs = dbConnection.getResultSet("select distinct a.BatchId,a.toteid,a.boxid,b.remarks,a.trntime from BinBatchIn a,usa.dbo.upcboxhead b where " +
                    "a.boxid=b.BoxNo and a.status='' and a.userid=" + objGlobal.getUserId() + " union select distinct a.BatchId,a.toteid,a.boxid,b.remarks,a.trntime from BinBatchIn a," +
                    "bfldata.dbo.tcmboxes b where a.boxid=b.BoxNo and a.status='' and a.userid=" + objGlobal.getUserId(), objGlobal.getConnection());
            while (rs.next()) {
                listBinPutAwayPendingToteIdTicket.add(new BinPutAwayPendingToteIdTicket(rs.getString("BatchId").toString(),
                        rs.getString("toteid").toString(), rs.getString("boxid").toString(),
                        rs.getString("trntime").toString().substring(0, 8), rs.getString("remarks")));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayControl:loadTransferItemsAll:" + ex.toString());
            return null;
        }
        return listBinPutAwayPendingToteIdTicket;
    }

    ArrayList<BinPutAwayHistoryTicket> loadBinPutAwayUserHistory() {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<BinPutAwayHistoryTicket> listBinPutAwayHistoryTicket = new ArrayList<BinPutAwayHistoryTicket>();
        try {
            listBinPutAwayHistoryTicket.clear();
            rs = dbConnection.getResultSet("select top 100 Toteid,boxno,direction,location,trntime from BinPutAwayHistory where " +
                    "userid=" + objGlobal.getUserId() + " order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                listBinPutAwayHistoryTicket.add(new BinPutAwayHistoryTicket(rs.getString("Toteid").toString(),
                        rs.getString("boxno").toString(), rs.getString("direction").toString(),
                        rs.getString("location").toString(), rs.getString("trntime").toString().toString().substring(0, 8)));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayControl:loadBinPutAwayUserHistory:" + ex.toString());
            return null;
        }
        return listBinPutAwayHistoryTicket;
    }
}
