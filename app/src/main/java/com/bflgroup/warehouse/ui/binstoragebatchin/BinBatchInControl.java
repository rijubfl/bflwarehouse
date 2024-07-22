package com.bflgroup.warehouse.ui.binstoragebatchin;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BinBatchInControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BinBatchInGlobal objBinBatchInGlobal = BinBatchInGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public BinBatchInControl() {
        objGlobal.setDbName("RACKS");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("BinBatchInControl : Connection error");
        }
    }
    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("RACKS");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BinBatchInControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }
    public boolean validateScanTote(String toteId,String pltNo) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(toteId)) {
            objGlobal.setErrorMessage("Tote Id is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select distinct BatchID from BinBatchIn where UserId=" + objGlobal.getUserId() + " and Status=''", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Pending batch, Batch Id:" + rs.getString("BatchID").toString());
                return false;
            }
            rs = dbConnection.getResultSet("select * from BinRack where toteid='" + toteId + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Toteid is found in Location: " + rs.getString("Location").toString());
                return false;
            }
            String boxno = "";
            rs = dbConnection.getResultSet("select boxno,remarks,closed,plt=isnull((select distinct palletno from usa.dbo.vUPCBoxDet where boxno=a.boxno and Closed='N'),'') from " +
                    "usa.dbo.upcboxhead a where ToteID='" + toteId + "' and Closed='N'", objGlobal.getConnection());
            if (rs.next()) {
                /*if (!pltNo.isEmpty()) {
                    if (!rs.getString("plt").toString().equals(pltNo)) {
                        objGlobal.setErrorMessage("Different pallets not allowed for same batchin,: " + rs.getString("plt").toString());
                        return false;
                    }
                }*/
                boxno = rs.getString("boxno").toString();
                objBinBatchInGlobal.setPalletno(rs.getString("plt").toString());
                if (!dbConnection.insertUpdate("delete from tmpBatchInTote where ToteId='" + toteId + "' and Userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into tmpBatchInTote(Userid,DeviceId,ToteId,BoxNo,BoxRemarks,sn,time1,PalletNo) values(" + objGlobal.getUserId() + "," +
                        "'" + objGlobal.getDeviceName() + "','" + toteId + "','" + rs.getString("boxno").toString() + "'," +
                        "'" + rs.getString("remarks").toString() + "',(select isnull(max(isnull(sn,0)),0)+1 from tmpBatchInTote where " +
                        "userid=" + objGlobal.getUserId() + "),convert(varchar,getdate(),8),'" + objBinBatchInGlobal.getPalletno() + "')", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                rs = dbConnection.getResultSet("select distinct a.boxno,a.remarks,a.closed,plt=isnull((select distinct palletno from bfldata.dbo.vr1pallet where boxno=a.boxno and Closed='N'),'') from " +
                        "bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno and b.TotId='" + toteId + "' and a.Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    if (!pltNo.isEmpty()) {
                        if (!rs.getString("plt").toString().equals(pltNo)) {
                            objGlobal.setErrorMessage("Multiple pallet is not allowed,: " + rs.getString("plt").toString());
                            return false;
                        }
                    }
                    boxno = rs.getString("boxno").toString();
                    objBinBatchInGlobal.setPalletno(rs.getString("plt").toString());
                    if (!dbConnection.insertUpdate("delete from tmpBatchInTote where ToteId='" + toteId + "' and Userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into tmpBatchInTote(Userid,DeviceId,ToteId,BoxNo,BoxRemarks,sn,time1,PalletNo) values(" + objGlobal.getUserId() + "," +
                            "'" + objGlobal.getDeviceName() + "','" + toteId + "','" + rs.getString("boxno").toString() + "'," +
                            "'" + rs.getString("remarks").toString() + "',(select isnull(max(isnull(sn,0)),0)+1 from tmpBatchInTote where " +
                            "userid=" + objGlobal.getUserId() + "),convert(varchar,getdate(),8),'" + objBinBatchInGlobal.getPalletno() + "')", objGlobal.getConnection())) {
                        return false;
                    }
                } else {
                    objGlobal.setErrorMessage("Invalid box or box is closed");
                    return false;
                }
            }
            if(!TextUtils.isEmpty(objBinBatchInGlobal.getPalletno())) { //validate pallet is closed or not
                rs = dbConnection.getResultSet("select closed from BFLDATA.dbo.R1PalletHead where PalletNo='" + objBinBatchInGlobal.getPalletno() + "' and Closed='N' union all " +
                        "select closed from BFLDATA.dbo.USAPallets where PalletNo='"+objBinBatchInGlobal.getPalletno()+"' and Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    if (!dbConnection.insertUpdate("delete from tmpBatchInTote where ToteId='" + toteId + "' and Userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                        return false;
                    }
                    objGlobal.setErrorMessage("Pallet is not yet closed, please contact supervisor, "+objBinBatchInGlobal.getPalletno());
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select * from tempdata.dbo.SIMProdReadyPalletsList where BoxNo='" + boxno + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (!dbConnection.insertUpdate("delete from tmpBatchInTote where ToteId='" + toteId + "' and Userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                    return false;
                }
                objGlobal.setErrorMessage("Box is found in SIM List, please give for production");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:boxValid:" + ex.toString());
            return false;
        }
    }

    boolean validateBatchIn(String pltNo) {
        try {
            if (!checkConnection()) {
                return false;
            }
           /* rs = dbConnection.getResultSet("select cnt=count(distinct BoxNo) from usa.dbo.vUPCBoxDet where BoxNo not in(select BoxNo from RACKS.dbo.tmpBatchInTote where " +
                    "userid=" + objGlobal.getUserId() + ") and palletno='" + pltNo + "' having count(distinct BoxNo)>0", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("BinBatchInControl:validateBatchIn: " + rs.getString("cnt").toString() + " boxes not found in Batch in process");
                return false;
            }*/
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateBatchIn:" + ex.toString());
            return false;
        }
    }

    public boolean saveBatchIn(String pltNo) {
        String batchId = "";
        b_Result = validateBatchIn(pltNo);
        if (!b_Result) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("validateCheckInOut:001:");
                return false;
            }
            rs = dbConnection.getResultSet("select batchid=isnull(max(batchid),0)+1 from binBatchIn", objGlobal.getConnection());
            if (rs.next()) {
                batchId = rs.getString("batchid").toString();
            }
            //objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into BinBatchIn(BatchID,Warehouse,TrnDate,TrnTime,ToteId,BoxId,UserId,DeviceId,Status,InDateTime,PalletNo) " +
                    "select '" + batchId + "','" + objGlobal.getWarehouse() + "','" + objGlobal.getServerDate() + "',time1,ToteId,boxno,UserId,DeviceId,'',null,PalletNo from " +
                    "tmpBatchInTote where userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
           /* if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.CloseR1pallet select 'USA',PalletNo,'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," +
                    "" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','','',0,0,0,'AUTO CLOSE FROM BINSTORAGE' from BFLDATA.dbo.USAPallets where palletno='" + pltNo + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.CloseR1pallet select 'TCM',PalletNo,'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," +
                    "" + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','','',0,0,0,'AUTO CLOSE FROM BINSTORAGE' from BFLDATA.dbo.R1PalletHead where palletno='" + pltNo + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update BFLDATA.dbo.USAPallets set Closed='Y' where palletno='" + pltNo + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update BFLDATA.dbo.R1PalletHead set Closed='Y' where palletno='" + pltNo + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }*/
            //objGlobal.getConnection().commit();
            return true;
        } catch (Exception ex) {
            //try {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:ex:" + ex.toString());
                //objGlobal.getConnection().rollback();
            //} catch (SQLException e) {
                //objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:e:" + e.toString());
            //    return false;
           // }
            return false;
        }
    }

    public boolean validateZoneDivision(String location,String boxno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            String division = "";
            rs = dbConnection.getResultSet("select * from DivisionAllocateZone where Division in(select distinct Division from bfldata.dbo.DeptStock where Department in(select Department from " +
                    "usa.dbo.USAPriority where groupCode in(select GroupCode from usa.dbo.UPCBoxHead where BoxNo='" + boxno + "')))", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Toteid is found in Location: " + rs.getString("Location").toString());
                return false;
            }

            rs = dbConnection.getResultSet("", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Toteid is found in Location: " + rs.getString("Location").toString());
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:boxValid:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from tmpBatchInTote where Userid=" + objGlobal.getUserId(), objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:boxValid:" + ex.toString());
            return false;
        }
        return true;
    }

    public List<String> loadZone() {
        List<String> arr;
        arr = new ArrayList<String>();
        try {
            rs = dbConnection.getResultSet("select distinct Zones from racks.dbo.BinRackMaster order by 1", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("zones").toString());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickRackTicket:loadBinStorageWavePickRack:" + ex.toString());
            return null;
        }
        return arr;
    }

    ArrayList<BinBatchInScanToteTicket> loadBinScanToteId() {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<BinBatchInScanToteTicket> listBinScanToteId = new ArrayList<BinBatchInScanToteTicket>();
        try {
            listBinScanToteId.clear();
            rs = dbConnection.getResultSet("select * from tmpBatchInTote where userid=" + objGlobal.getUserId() + " order by sn desc", objGlobal.getConnection());
            while (rs.next()) {
                listBinScanToteId.add(new BinBatchInScanToteTicket(rs.getString("ToteId").toString(),rs.getString("BoxNo").toString(),
                        rs.getString("time1").toString().substring(0,8),rs.getString("BoxRemarks").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferItemsAll:" + ex.toString());
            return null;
        }
        return listBinScanToteId;
    }
}
