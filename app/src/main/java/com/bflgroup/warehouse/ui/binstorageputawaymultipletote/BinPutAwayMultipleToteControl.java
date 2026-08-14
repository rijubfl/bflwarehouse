package com.bflgroup.warehouse.ui.binstorageputawaymultipletote;

import android.text.TextUtils;

import com.bflgroup.warehouse.BuildConfig;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BinPutAwayMultipleToteControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BinPutAwayMultipleToteGlobal objBinPutAwayMultipleToteGlobal = BinPutAwayMultipleToteGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public BinPutAwayMultipleToteControl() {
        objGlobal.setDbName("RACKS");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("RACKS");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("BinPutAwayMultipleToteControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateToteid(String warehouse, String direction, String scan, String location) {
        boolean found = false;
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(direction.trim())) {
            objGlobal.setErrorMessage("Chose direction");
            return false;
        }
        if (TextUtils.isEmpty(location.trim())) {
            objGlobal.setErrorMessage("Scan Location");
            return false;
        }
        if (TextUtils.isEmpty(scan.trim())) {
            objGlobal.setErrorMessage("Tote Id is empty");
            return false;
        }
        b_Result = validateLocation(warehouse, location, direction);
        if (!b_Result) {
            return false;
        }
        try {
            objBinPutAwayMultipleToteGlobal.setBoxNo("");
            objBinPutAwayMultipleToteGlobal.setToteId("");
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=boxno,toteid=ToteID,closed from usa.dbo.upcboxhead where (ToteID='" + scan + "' or BoxNo='" + scan + "') order by TrnDate desc,Time1 desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=a.boxno,toteid=b.TotId,closed from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                        "and (b.TotId='" + scan + "' or b.Boxno='" + scan + "') order by a.TrnDate desc,a.Time1 desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=palletno,toteid=palletno,closed from bfldata.dbo.r1pallethead where palletno='" + scan + "' order by TrnDate desc,Time1 desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=palletno,toteid=palletno,closed from bfldata.dbo.usapallets where palletno='" + scan + "' order by TrnDate desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=palletno,toteid=palletno,closed from usa.dbo.usapallets where palletno='" + scan + "' order by TrnDate desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=palletno,toteid=palletno,closed='N' from bfldata.dbo.GoodsIssueHead where palletno='" + scan + "' order by EntryDate desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 Boxno=palletno,toteid=palletno,closed='N' from abudata.dbo.tcmitemsall where palletno='" + scan + "' order by TrnDate desc", objGlobal.getConnection());
                if (rs.next()) found = true;
            }
            if (!found) {
                if (!objGlobal.getWorkLocation().equals("UAE")) {
                    rs = dbConnection.getResultSet("select top 1 Boxno=TrfNo,toteid=storeissue,closed='N' from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where (storeissue='" + scan + "' or " +
                            "trfno='" + scan + "') order by TrfDate desc", objGlobal.getConnection());
                    if (rs.next()) found = true;
                }
            }
            if (!found) {
                objGlobal.setErrorMessage("Invalid Box / Pallet / Tote ID - (" + scan + ")");
                return false;
            }
            String closed = rs.getString("Closed");
            if (closed.equals("Y")) {
                objGlobal.setErrorMessage("Box / Pallet is already closed - (" + objBinPutAwayMultipleToteGlobal.getBoxNo() + " - " + scan + ")");
                return false;
            }
            objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
            objBinPutAwayMultipleToteGlobal.setToteId(rs.getString("toteid"));
            rs = dbConnection.getResultSet("select top 1 palletno from bfldata.dbo.closer1pallet where palletno='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Box / Pallet is already closed - (" + objBinPutAwayMultipleToteGlobal.getBoxNo() + " - " + scan + ")");
                return false;
            }
            if (!objGlobal.getWorkLocation().equals("UAE")) {
                if (objBinPutAwayMultipleToteGlobal.getBoxNo().startsWith("F")) {
                    rs = dbConnection.getResultSet("select top 1 sn,Trndate from bfldata.dbo.VerifyGin where TrfNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("The GRN has not been completed yet for box (" + objBinPutAwayMultipleToteGlobal.getBoxNo() + " - " + scan + ")");
                        return false;
                    }
                }
            }
            if (objGlobal.getWorkLocation().equals("KSA")) {
                if (objBinPutAwayMultipleToteGlobal.getToteId().startsWith("SG")) {
                    objGlobal.setErrorMessage("This transfer is for the shop, so bin rack put-away is not allowed.");
                    return false;
                }
            }
            if (direction.equals("IN")) {
                rs = dbConnection.getResultSet("SELECT TOP 1 Direction, SourceLocation FROM (SELECT Direction,TrnDate,TrnTime, Location AS SourceLocation FROM " +
                        "racks..BinPutAwayHistory WHERE BoxNo ='"+scan+"' UNION ALL SELECT Direction,TrnDate,TrnTime, RackNo AS SourceLocation FROM " +
                        "racks..TechnoRacksHistory WHERE PalletNo1 ='"+scan+"'  OR PalletNo2 ='"+scan+"' UNION ALL SELECT Direction," +
                        "TrnDate,TrnTime,RackNo AS SourceLocation FROM racks..WarehouseRackHistory WHERE PalletNo1 ='"+scan+"' OR PalletNo2 = " +
                        "'"+scan+"') X ORDER BY TrnDate DESC, TrnTime DESC", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getString("Direction").equals("IN")) {
                        objGlobal.setErrorMessage("The box/pallet is already found in "+rs.getString("SourceLocation"));
                        return false;
                    }
                }
                rs = dbConnection.getResultSet("select * from tmpwhracks where (palletno1='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "' or palletno2='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from technorackDet where (palletno1='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "' or palletno2='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from warehouserackDet where (palletno1='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "' or palletno2='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }
                if (!objBinPutAwayMultipleToteGlobal.getBoxNo().equals("")) {
                    if (!objBinPutAwayMultipleToteGlobal.getToteId().trim().equals("")) {
                        rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + objBinPutAwayMultipleToteGlobal.getToteId() + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                    } else {
                        rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "'", objGlobal.getConnection());
                    }

                    if (rs.next()) {
                        objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("location").toString());
                        return false;
                    }
                } else {
                    if (!objBinPutAwayMultipleToteGlobal.getToteId().equals("")) {
                        rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + objBinPutAwayMultipleToteGlobal.getToteId() + "')", objGlobal.getConnection());
                        if (rs.next()) {
                            objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("location").toString());
                            return false;
                        }
                    }
                }
                int totTotCnt = 1;
                rs = dbConnection.getResultSet("select tot=count(*) from BinRack where Warehouse='" + warehouse + "' and Location='" + location + "'", objGlobal.getConnection());
                if (rs.next()) {
                    totTotCnt = totTotCnt + rs.getInt("tot");
                }
                rs = dbConnection.getResultSet("select tot=count(*) from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (rs.next()) {
                    totTotCnt = totTotCnt + rs.getInt("tot");
                }
                if (totTotCnt > objGlobal.getMaxTotInBin()) {
                    objGlobal.setErrorMessage("Maximum is allowed only " + objGlobal.getMaxTotInBin());
                    return false;
                }
            }
            if (direction.equals("OUT")) {
                if (objBinPutAwayMultipleToteGlobal.getBoxNo().equals("")) {
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + objBinPutAwayMultipleToteGlobal.getToteId() + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("location").equals(location)) {
                            objGlobal.setErrorMessage("Pallet/Box - " + objBinPutAwayMultipleToteGlobal.getToteId() + " is found in location - " + rs.getString("location"));
                            return false;
                        }
                    } else {
                        objGlobal.setErrorMessage("Pallet/Box - " + objBinPutAwayMultipleToteGlobal.getToteId() + " is not found in - " + location + " / Pallet is already OUT");
                        return false;
                    }
                } else {
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "'  and (Toteid='" + objBinPutAwayMultipleToteGlobal.getToteId() + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("location").equals(location)) {
                            objGlobal.setErrorMessage("Pallet/Box - " + objBinPutAwayMultipleToteGlobal.getToteId() + " is found in location - " + rs.getString("location"));
                            return false;
                        }
                    } else {
                        objGlobal.setErrorMessage("Pallet/Box - " + objBinPutAwayMultipleToteGlobal.getToteId() + " is found not in location - " + location + " or Pallet is already OUT");
                        return false;
                    }
                }
            }
            if (!objBinPutAwayMultipleToteGlobal.getBoxNo().equals("")) {
                if (!dbConnection.insertUpdate("delete from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "' and (toteid = '" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "' and (ToteId='" + objBinPutAwayMultipleToteGlobal.getToteId() + "' or boxno  ='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (objBinPutAwayMultipleToteGlobal.getToteId().trim().isEmpty()) {
                objBinPutAwayMultipleToteGlobal.setToteId(objBinPutAwayMultipleToteGlobal.getBoxNo());
            }
            if (!dbConnection.insertUpdate("insert into tmpToteScan(DeviceId,ToteId,BoxNo,ScanDtTime,Direction,Location) values ('" + objGlobal.getDeviceName() + "','" + objBinPutAwayMultipleToteGlobal.getToteId() + "'," +
                    "'" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "',getdate(),'" + direction + "','" + location + "')", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:validateToteid:" + ex.toString());
            return false;
        }
    }

    public boolean validateLocation(String warehouse, String location, String direction) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(direction)) {
            objGlobal.setErrorMessage("Chose direction");
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            objGlobal.setErrorMessage("Location is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from Racks.dbo.BinRackMaster where Warehouse='" + warehouse + "' and Barcode='" + location + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Location, " + location);
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:validateLocation:" + ex.toString());
            return false;
        }
    }

    public boolean clearScaned() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:clearScaned:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean saveBinInOutMultiple(String warehouse, String direction, String location, String toteid) {
        b_Result = validateLocation(warehouse, location, direction);
        if (!b_Result) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("saveBinInOut:001:");
                return false;
            }

            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into BinPutAwayHistory(Warehouse,TrnDate,TrnTime,ToteId,BoxNo,Direction,Location,UserId,UserName,DeviceId) " +
                    "select '" + warehouse + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "',ToteId," +
                    "BoxNo,Direction,Location," + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','" + objGlobal.getDeviceName() + "' from tmpToteScan where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (objGlobal.getWorkLocation().equals("KSA")) {
                if (!dbConnection.insertUpdate("INSERT INTO RACKS..BinRackActivityLog" +
                        "(" +
                        "    Warehouse, Direction, Location, ToteId, BoxNo," +
                        "    ActionName, RowsAffected," +
                        "    UserId, UserName, DeviceId, Remarks" +
                        ")" +
                        "SELECT" +
                        "    '" + warehouse + "'," +
                        "    Direction," +
                        "    Location," +
                        "    ToteId," +
                        "    BoxNo," +
                        "    'HISTORY_INSERT'," +
                        "    1," +
                        "  " + objGlobal.getUserId() + "," +
                        "   '" + objGlobal.getUserName() + "'," +
                        "   '" + objGlobal.getDeviceName() + "'," +
                        "    'Inserted into BinPutAwayHistory-version:" + BuildConfig.VERSION_NAME + "'" +
                        " FROM tmpToteScan" +
                        " WHERE DeviceId =  '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                }
            }
            if (direction.equals("IN")) {
                if (!dbConnection.insertUpdate("insert into BinRack(Warehouse,Location,ToteId,BoxNo) select '" + warehouse + "',Location,ToteId,BoxNo from tmpToteScan " +
                        "where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (objGlobal.getWorkLocation().equals("KSA")) {
                    if (!dbConnection.insertUpdate("INSERT INTO RACKS..BinRackActivityLog" +
                            "(" +
                            "    Warehouse, Direction, Location, ToteId, BoxNo," +
                            "    ActionName, RowsAffected," +
                            "    UserId, UserName, DeviceId, Remarks" +
                            ")" +
                            "SELECT" +
                            "    '" + warehouse + "'," +
                            "    Direction," +
                            "    Location," +
                            "    ToteId," +
                            "    BoxNo," +
                            "    'INSERT_BOX/TOTE'," +
                            "    1," +
                            "  " + objGlobal.getUserId() + "," +
                            "   '" + objGlobal.getUserName() + "'," +
                            "   '" + objGlobal.getDeviceName() + "'," +
                            "    'Inserted into BINRACK-version:" + BuildConfig.VERSION_NAME + "'" +
                            " FROM tmpToteScan" +
                            " WHERE DeviceId =  '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    }
                }
            }
            if (direction.equals("OUT")) {
                if (!objGlobal.getWorkLocation().equals("KSA")) {
                    if (toteid.isEmpty()) {
                        if (!dbConnection.insertUpdate("delete from BinRack where Warehouse='" + warehouse + "' and Location='" + location + "' and (toteid in(select ToteId from tmpToteScan where " +
                                "DeviceId='" + objGlobal.getDeviceName() + "') or  boxno in(select BoxNo from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "'))", objGlobal.getConnection())) {
                            objGlobal.getConnection().rollback();
                            return false;
                        }
                    } else {
                        if (!dbConnection.insertUpdate("delete from BinRack where Warehouse='" + warehouse + "' and Location='" + location + "' and (toteid='" + toteid + "' or boxno='" + toteid + "')", objGlobal.getConnection())) {
                            objGlobal.getConnection().rollback();
                            return false;
                        }
                    }
                }
                else{
                    if (!dbConnection.insertUpdate("delete from racks..BinRack WHERE boxno in (select boxno from racks..tmpToteScan where DeviceId =  '"+objGlobal.getDeviceName()+"')" +
                            "or ToteId in (select ToteId from racks..tmpToteScan where DeviceId =  '"+objGlobal.getDeviceName()+"')", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    if (!dbConnection.insertUpdate("INSERT INTO RACKS..BinRackActivityLog" +
                            "(" +
                            "    Warehouse, Direction, Location, ToteId, BoxNo," +
                            "    ActionName, RowsAffected," +
                            "    UserId, UserName, DeviceId, Remarks" +
                            ")" +
                            "SELECT" +
                            "    '"+warehouse+"'," +
                            "    Direction," +
                            "    Location," +
                            "    ToteId," +
                            "    BoxNo," +
                            "    'DELETE_BOX/TOTE'," +
                            "    1," +
                            "  "+objGlobal.getUserId() +"," +
                            "   '"+objGlobal.getUserName()+"'," +
                            "   '"+objGlobal.getDeviceName()+"'," +
                            "    'Deleted into BINRACK-' + CONVERT(VARCHAR, GETDATE(), 120) + '-" + BuildConfig.VERSION_NAME + "'" +
                            " FROM tmpToteScan" +
                            " WHERE DeviceId =  '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())) {
                    }
                }


            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:saveBatchIn:ex:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:saveBatchIn:e:" + e.toString());
                return false;
            }
            return false;
        }
    }







//    public boolean saveBinInOutMultiple(String warehouse, String direction, String location, String toteid) {
//        boolean b_Result = validateLocation(warehouse, location, direction);
//        if (!b_Result) {
//            return false;
//        }
//
//        Connection con = objGlobal.getConnection();
//        String dir = direction == null ? "" : direction.trim();
//        String tote = toteid == null ? "" : toteid.trim();
//
//        try {
//            if (!dbConnection.getServerDateTime(con)) {
//                objGlobal.setErrorNo("saveBinInOut:001:");
//                return false;
//            }
//
//            con.setAutoCommit(false);
//
//            String historyQry =
//                    "insert into BinPutAwayHistory " +
//                            "(Warehouse, TrnDate, TrnTime, ToteId, BoxNo, Direction, Location, UserId, UserName, DeviceId) " +
//                            "select '" + warehouse + "', '" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "', " +
//                            "ToteId, BoxNo, Direction, Location, " + objGlobal.getUserId() + ", '" + objGlobal.getUserName() + "', '" + objGlobal.getDeviceName() + "' " +
//                            "from tmpToteScan " +
//                            "where DeviceId = '" + objGlobal.getDeviceName() + "'";
//
//            if (!dbConnection.insertUpdate(historyQry, con)) {
//                con.rollback();
//                objGlobal.setErrorMessage("Failed to insert into BinPutAwayHistory");
//                return false;
//            }
//
//            if ("IN".equalsIgnoreCase(dir)) {
//
//                String binRackInsertQry =
//                        "insert into BinRack (Warehouse, Location, ToteId, BoxNo) " +
//                                "select '" + warehouse + "', Location, ToteId, BoxNo " +
//                                "from tmpToteScan " +
//                                "where DeviceId = '" + objGlobal.getDeviceName() + "'";
//
//                if (!dbConnection.insertUpdate(binRackInsertQry, con)) {
//                    con.rollback();
//                    objGlobal.setErrorMessage("Failed to insert into BinRack");
//                    return false;
//                }
//
//            } else if ("OUT".equalsIgnoreCase(dir)) {
//
//                String deleteQry;
//
//                if (tote.isEmpty()) {
//                    deleteQry =
//                            "delete from BinRack " +
//                                    "where Warehouse = '" + warehouse + "' " +
//                                    "and Location = '" + location + "' " +
//                                    "and (ToteId in (select ToteId from tmpToteScan where DeviceId = '" + objGlobal.getDeviceName() + "') " +
//                                    "or BoxNo in (select BoxNo from tmpToteScan where DeviceId = '" + objGlobal.getDeviceName() + "'))";
//                } else {
//                    deleteQry =
//                            "delete from BinRack " +
//                                    "where Warehouse = '" + warehouse + "' " +
//                                    "and Location = '" + location + "' " +
//                                    "and (ToteId = '" + tote + "' or BoxNo = '" + tote + "')";
//                }
//
//                if (!dbConnection.insertUpdate(deleteQry, con)) {
//                    con.rollback();
//                    objGlobal.setErrorMessage("Failed to delete from BinRack");
//                    return false;
//                }
//
//            } else {
//                con.rollback();
//                objGlobal.setErrorMessage("Invalid direction value: " + direction);
//                return false;
//            }
//
//            con.commit();
//            return true;
//
//        } catch (Exception ex) {
//            try {
//                con.rollback();
//            } catch (SQLException e) {
//                objGlobal.setErrorMessage("Rollback failed: " + e.toString());
//            }
//
//            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:saveBinInOutMultiple: " + ex.toString());
//            return false;
//
//        } finally {
//            try {
//                con.setAutoCommit(true);
//            } catch (SQLException e) {
//                objGlobal.setErrorMessage("Failed to reset auto-commit: " + e.toString());
//            }
//        }
//    }


//    public boolean saveBinInOutMultiple(String warehouse, String direction, String location, String toteid) {
//        boolean b_Result = validateLocation(warehouse, location, direction);
//        if (!b_Result) {
//            return false;
//        }
//
//        Connection con = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        String dir = direction == null ? "" : direction.trim();
//        String tote = toteid == null ? "" : toteid.trim();
//        String deviceId = objGlobal.getDeviceName();
//
//        try {
//            con = objGlobal.getConnection();
//
//            if (con == null) {
//                objGlobal.setErrorMessage("Database connection is null");
//                return false;
//            }
//
//            if (!dbConnection.getServerDateTime(con)) {
//                objGlobal.setErrorNo("saveBinInOut:001:");
//                objGlobal.setErrorMessage("Unable to get server date/time");
//                return false;
//            }
//
//            con.setAutoCommit(false);
//
//            // ---------------------------------------------------
//            // 1) Check temp data exists for this device
//            // ---------------------------------------------------
//            String countTmpQry =
//                    "select count(*) as cnt " +
//                            "from tmpToteScan " +
//                            "where DeviceId = ?";
//
//            ps = con.prepareStatement(countTmpQry);
//            ps.setString(1, deviceId);
//            rs = ps.executeQuery();
//
//            int tmpCount = 0;
//            if (rs.next()) {
//                tmpCount = rs.getInt("cnt");
//            }
//            rs.close();
//            ps.close();
//
//            if (tmpCount <= 0) {
//                con.rollback();
//                objGlobal.setErrorMessage("No data found in tmpToteScan for device: " + deviceId);
//                return false;
//            }
//
//            // ---------------------------------------------------
//            // 2) Validate temp rows
//            // ---------------------------------------------------
//            String validateTmpQry =
//                    "select top 1 ToteId, BoxNo, Location " +
//                            "from tmpToteScan " +
//                            "where DeviceId = ? " +
//                            "and (isnull(ToteId, '') = '' or isnull(BoxNo, '') = '' or isnull(Location, '') = '')";
//
//            ps = con.prepareStatement(validateTmpQry);
//            ps.setString(1, deviceId);
//            rs = ps.executeQuery();
//
//            if (rs.next()) {
//                String badTote = rs.getString("ToteId");
//                String badBox = rs.getString("BoxNo");
//                String badLoc = rs.getString("Location");
//
//                rs.close();
//                ps.close();
//
//                con.rollback();
//                objGlobal.setErrorMessage(
//                        "Invalid data found in tmpToteScan. " +
//                                "ToteId=" + badTote + ", BoxNo=" + badBox + ", Location=" + badLoc
//                );
//                return false;
//            }
//            rs.close();
//            ps.close();
//
//            // ---------------------------------------------------
//            // 3) Insert into history
//            // ---------------------------------------------------
//            String historyQry =
//                    "insert into BinPutAwayHistory " +
//                            "(Warehouse, TrnDate, TrnTime, ToteId, BoxNo, Direction, Location, UserId, UserName, DeviceId) " +
//                            "select ?, ?, ?, ToteId, BoxNo, Direction, Location, ?, ?, ? " +
//                            "from tmpToteScan " +
//                            "where DeviceId = ?";
//
//            ps = con.prepareStatement(historyQry);
//            ps.setString(1, warehouse);
//            ps.setString(2, objGlobal.getServerDate());
//            ps.setString(3, objGlobal.getServerTime());
//            ps.setInt(4, objGlobal.getUserId());
//            ps.setString(5, objGlobal.getUserName());
//            ps.setString(6, deviceId);
//            ps.setString(7, deviceId);
//
//            int historyRows = ps.executeUpdate();
//            ps.close();
//
//            if (historyRows <= 0) {
//                con.rollback();
//                objGlobal.setErrorMessage("Failed to insert into BinPutAwayHistory");
//                return false;
//            }
//
//            // ---------------------------------------------------
//            // 4) Direction-wise processing
//            // ---------------------------------------------------
//            if ("IN".equalsIgnoreCase(dir)) {
//
//                // Check duplicate rows already present in BinRack
//                String duplicateQry =
//                        "select top 1 t.ToteId, t.BoxNo, b.Location" +
//                                "from tmpToteScan t" +
//                                "inner join BinRack b" +
//                                "on b.Warehouse = ?" +
//                                "and (b.ToteId = t.ToteId or b.BoxNo = t.BoxNo)" +
//                                "where t.DeviceId = ?";
//
//                ps = con.prepareStatement(duplicateQry);
//                ps.setString(1, warehouse);
//                ps.setString(2, deviceId);
//                rs = ps.executeQuery();
//
//                if (rs.next()) {
//                    String dupTote = rs.getString("ToteId");
//                    String dupBox = rs.getString("BoxNo");
//                    String dupLoc = rs.getString("Location");
//
//                    rs.close();
//                    ps.close();
//
//                    con.rollback();
//                    objGlobal.setErrorMessage(
//                            "Duplicate record found in BinRack. " +
//                                    "ToteId=" + dupTote + ", BoxNo=" + dupBox + ", Location=" + dupLoc
//                    );
//                    return false;
//                }
//                rs.close();
//                ps.close();
//
//                // Insert only valid rows into BinRack
//                String binRackInsertQry =
//                        "insert into BinRack (Warehouse, Location, ToteId, BoxNo) " +
//                                "select ?, Location, ToteId, BoxNo " +
//                                "from tmpToteScan " +
//                                "where DeviceId = ?";
//
//                ps = con.prepareStatement(binRackInsertQry);
//                ps.setString(1, warehouse);
//                ps.setString(2, deviceId);
//
//                int binRackRows = ps.executeUpdate();
//                ps.close();
//
//                if (binRackRows <= 0) {
//                    con.rollback();
//                    objGlobal.setErrorMessage("No rows inserted into BinRack");
//                    return false;
//                }
//
//            } else if ("OUT".equalsIgnoreCase(dir)) {
//
//                String deleteQry;
//
//                if (tote.isEmpty()) {
//                    deleteQry =
//                            "delete from BinRack " +
//                                    "where Warehouse = ? " +
//                                    "and Location = ? " +
//                                    "and (ToteId in (select ToteId from tmpToteScan where DeviceId = ?) " +
//                                    "or BoxNo in (select BoxNo from tmpToteScan where DeviceId = ?))";
//
//                    ps = con.prepareStatement(deleteQry);
//                    ps.setString(1, warehouse);
//                    ps.setString(2, location);
//                    ps.setString(3, deviceId);
//                    ps.setString(4, deviceId);
//
//                } else {
//                    deleteQry =
//                            "delete from BinRack " +
//                                    "where Warehouse = ? " +
//                                    "and Location = ? " +
//                                    "and (ToteId = ? or BoxNo = ?)";
//
//                    ps = con.prepareStatement(deleteQry);
//                    ps.setString(1, warehouse);
//                    ps.setString(2, location);
//                    ps.setString(3, tote);
//                    ps.setString(4, tote);
//                }
//
//                int deleteRows = ps.executeUpdate();
//                ps.close();
//
//                if (deleteRows <= 0) {
//                    con.rollback();
//                    objGlobal.setErrorMessage("No matching rows found to delete from BinRack");
//                    return false;
//                }
//
//            } else {
//                con.rollback();
//                objGlobal.setErrorMessage("Invalid direction value: " + direction);
//                return false;
//            }
//
//            // ---------------------------------------------------
//            // 5) Commit
//            // ---------------------------------------------------
//            con.commit();
//            return true;
//
//        } catch (Exception ex) {
//            try {
//                if (con != null) {
//                    con.rollback();
//                }
//            } catch (SQLException e) {
//                objGlobal.setErrorMessage("Rollback failed: " + e.toString());
//            }
//
//            objGlobal.setErrorMessage("saveBinInOutMultiple error: " + ex.toString());
//            return false;
//
//        } finally {
//            try {
//                if (rs != null) rs.close();
//            } catch (Exception e) {
//            }
//
//            try {
//                if (ps != null) ps.close();
//            } catch (Exception e) {
//            }
//
//            try {
//                if (con != null) {
//                    con.setAutoCommit(true);
//                }
//            } catch (Exception e) {
//                objGlobal.setErrorMessage("Failed to reset auto-commit: " + e.toString());
//            }
//        }
//    }



















    ArrayList<BinPutAwayMultipleTotePendingSaveTicket> loadBinPutAwayMultipleTotePendingSave() {
        if (!checkConnection()) {
            return null;
        }
        int tCount = 0;
        ArrayList<BinPutAwayMultipleTotePendingSaveTicket> listBinPutAwayMultipleTotePendingSave = new ArrayList<BinPutAwayMultipleTotePendingSaveTicket>();
        try {
            listBinPutAwayMultipleTotePendingSave.clear();
            rs = dbConnection.getResultSet("select * from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "' order by ScanDtTime desc", objGlobal.getConnection());
            while (rs.next()) {
                listBinPutAwayMultipleTotePendingSave.add(new BinPutAwayMultipleTotePendingSaveTicket(rs.getString("ToteId").toString(),
                        rs.getString("BoxNo").toString(), rs.getString("ScanDtTime").toString()));
                tCount++;
            }
            objBinPutAwayMultipleToteGlobal.setScanCount(tCount);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinPutAwayMultipleToteControl:loadBinPutAwayMultipleTotePendingSave:" + ex.toString());
            return null;
        }
        return listBinPutAwayMultipleTotePendingSave;
    }


}
