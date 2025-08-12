package com.bflgroup.warehouse.ui.binstorageputawaymultipletote;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

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

    public boolean validateToteid(String warehouse, String direction, String toteId, String location) {
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
        if (TextUtils.isEmpty(toteId.trim())) {
            objGlobal.setErrorMessage("Tote Id is empty");
            return false;
        }
        b_Result = validateLocation(warehouse, location, direction);
        if (!b_Result) {
            return false;
        }
        try {
            objBinPutAwayMultipleToteGlobal.setBoxNo("");
            rs = dbConnection.getResultSet("select Boxno=boxno from usa.dbo.upcboxhead where (ToteID='" + toteId + "' or BoxNo='" + toteId + "') and Closed='N'", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select distinct Boxno=a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno and (b.TotId='" + toteId + "' or b.Boxno='" + toteId + "') and a.Closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.r1pallethead where palletno='" + toteId + "' and closed='N'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.usapallets where palletno='" + toteId + "' and closed='N'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select Boxno=palletno from usa.dbo.usapallets where palletno='" + toteId + "' and closed = 'N'", objGlobal.getConnection());
                            if (!rs.next()) {
                                rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.GoodsIssueHead where palletno='" + toteId + "' and palletno not in (select palletno from bfldata.dbo.closeR1pallet)", objGlobal.getConnection());
                                if (!rs.next()) {
                                    rs = dbConnection.getResultSet("select top 1 Boxno=palletno from abudata.dbo.tcmitemsall where palletno='" + toteId + "' and palletno not in (select palletno from bfldata.dbo.closeR1pallet)", objGlobal.getConnection());
                                    if (!rs.next()) {
                                        if (objGlobal.getWorkLocation().equals("KSA")) {
                                            if (direction.equals("OUT")) {
                                                rs = dbConnection.getResultSet("select top 1 Boxno=TrfNo from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where (storeissue='" + toteId + "' or " +
                                                        "trfno='" + toteId + "') and trfno not in (select palletno from bfldata.dbo.closeR1pallet) and LEFT(TrfNo, 2) not in ('SN','SR','SO') and " +
                                                        "LEFT(storeissue, 2) not in ('SG')  order by TrfDate desc", objGlobal.getConnection());
                                            } else {
                                                rs = dbConnection.getResultSet("select top 1 Boxno=TrfNo from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where (storeissue='" + toteId + "' or " +
                                                        "trfno='" + toteId + "') and trfno not in (select palletno from bfldata.dbo.closeR1pallet) and LEFT(TrfNo, 2) not in ('SN','SR','SO') and LEFT(storeissue, 2) not in ('SG') and " +
                                                        "trfno in (select trfno from bfldata.dbo.verifyGin where trfno = a.trfno) order by TrfDate desc", objGlobal.getConnection());
                                            }
                                            if (!rs.next()) {
                                                rs = dbConnection.getResultSet("select boxno from usa.dbo.upcboxhead where (ToteID='" + toteId + "' or BoxNo='" + toteId + "') " +
                                                        "union all " +
                                                        "select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b " +
                                                        "where a.BoxNo=b.Boxno and (b.TotId='" + toteId + "' or b.Boxno='" + toteId + "') " +
                                                        "union all " +
                                                        "select Boxno=palletno from bfldata.dbo.r1pallethead where palletno='" + toteId + "' " +
                                                        "union all " +
                                                        "select Boxno=palletno from bfldata.dbo.usapallets where palletno='" + toteId + "' " +
                                                        "union all " +
                                                        "select Boxno=palletno from usa.dbo.usapallets where palletno='" + toteId + "' " +
                                                        "union all " +
                                                        "select Boxno=palletno from bfldata.dbo.GoodsIssueHead where palletno='" + toteId + "' " +
                                                        "union all " +
                                                        "select top 1 Boxno=palletno from abudata.dbo.tcmitemsall where palletno='" + toteId + "' " +
                                                        "union all " +
                                                        "select top 1 TrfNo from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader where storeissue='" + toteId + "' or " +
                                                        "trfno='" + toteId + "'", objGlobal.getConnection());
                                                if (!rs.next()) {
                                                    objGlobal.setErrorMessage("Invalid Box / Pallet Number");
                                                    return false;
                                                } else {
                                                    rs = dbConnection.getResultSet("select palletno from bfldata.dbo.GoodsIssueHead where palletno='" + toteId + "'", objGlobal.getConnection());
                                                    if (!rs.next()) {
                                                        objGlobal.setErrorMessage("GRN not done for this box");
                                                        return false;
                                                    } else {
                                                        rs = dbConnection.getResultSet("select top 1 TrfNo from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader where storeissue='" + toteId + "' or " +
                                                                "trfno='" + toteId + "'", objGlobal.getConnection());
                                                        if (!rs.next()) {
                                                            objGlobal.setErrorMessage("Transfer not available");
                                                        } else {
                                                            objGlobal.setErrorMessage("Box is already closed");
                                                        }
                                                        return false;
                                                    }

                                                }
                                                // objGlobal.setErrorMessage("Invalid Box / Pallet Number is closed Or GRN is not DONE for - (" + toteId + ") ");

                                            } else
                                                objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                                        } else
                                            objGlobal.setErrorMessage("Invalid Box / Pallet Number is closed - (" + toteId + ") ");
                                    } else
                                        objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                                } else
                                    objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                            } else objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                        } else objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                    } else objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
                } else objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
            } else objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("Boxno"));
            if (direction.equals("IN")) {
                rs = dbConnection.getResultSet("select * from tmpwhracks where (palletno1='" + toteId + "' or palletno2='" + toteId + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }

                rs = dbConnection.getResultSet("select * from technorackDet where (palletno1='" + toteId + "' or palletno2='" + toteId + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }

                rs = dbConnection.getResultSet("select * from warehouserackDet where (palletno1='" + toteId + "' or palletno2='" + toteId + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("rowno") + "-" + rs.getString("cellno"));
                    return false;
                }

                if (!objBinPutAwayMultipleToteGlobal.getBoxNo().equals("")) {

                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + toteId + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());

                    if (rs.next()) {
                        objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("location").toString());
                        return false;
                    }
                } else {
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + toteId + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("ToteID/Pallet found in location, " + rs.getString("location").toString());
                        return false;
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
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "' and (Toteid='" + toteId + "' or BoxNo='" + toteId + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("location").equals(location)) {
                            objGlobal.setErrorMessage("Pallet/Box - " + toteId.toString() + " is found in location - " + rs.getString("location"));
                            return false;
                        }
                    } else {
                        objGlobal.setErrorMessage("Pallet/Box - " + toteId.toString() + " is found not in location - " + location + " or Pallet is already OUT");
                        return false;
                    }
                } else {
                    rs = dbConnection.getResultSet("select * from BinRack where Warehouse='" + warehouse + "'  and (Toteid='" + toteId + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("location").equals(location)) {
                            objGlobal.setErrorMessage("Pallet/Box - " + toteId.toString() + " is found in location - " + rs.getString("location"));
                            return false;
                        }
                    } else {
                        objGlobal.setErrorMessage("Pallet/Box - " + toteId.toString() + " is found not in location - " + location + " or Pallet is already OUT");
                        return false;
                    }
                }

            }
            if (!objBinPutAwayMultipleToteGlobal.getBoxNo().equals("")) {
                if (!dbConnection.insertUpdate("delete from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "' and (toteid = '" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "' or BoxNo='" + objBinPutAwayMultipleToteGlobal.getBoxNo() + "')", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from tmpToteScan where DeviceId='" + objGlobal.getDeviceName() + "' and (ToteId='" + toteId + "' or boxno  ='" + toteId + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (!dbConnection.insertUpdate("insert into tmpToteScan(DeviceId,ToteId,BoxNo,ScanDtTime,Direction,Location) values ('" + objGlobal.getDeviceName() + "','" + toteId + "'," +
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
            rs = dbConnection.getResultSet("select * from BinRackMaster where Warehouse='RUKOON' and Barcode='" + location + "'", objGlobal.getConnection());
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
            if (direction.equals("IN")) {
                if (!dbConnection.insertUpdate("insert into BinRack(Warehouse,Location,ToteId,BoxNo) select '" + warehouse + "',Location,ToteId,BoxNo from tmpToteScan " +
                        "where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            if (direction.equals("OUT")) {
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
