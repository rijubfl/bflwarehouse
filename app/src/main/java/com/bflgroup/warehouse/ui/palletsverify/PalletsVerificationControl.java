package com.bflgroup.warehouse.ui.palletsverify;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PalletsVerificationControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PalletsVerificationGlobal objPalletsVerificationGlobal = PalletsVerificationGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public PalletsVerificationControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("GinVerificationControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("GinVerificationControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateGin(String ginNo, boolean forSave) {
        if (TextUtils.isEmpty(ginNo)) {
            objGlobal.setErrorMessage("Gin Number is empty");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorMessage("saveGinVerification:001:");
                return false;
            }
            rs = dbConnection.getResultSet("select *,edt=convert(varchar,EntryDate,103) from bfldata.dbo.PLTDeliveryHead where SrNo=" + ginNo, objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("GIN Number is invalid"+ ginNo);
                return false;
            }
            objPalletsVerificationGlobal.setWarehouseFrom(rs.getString("warehouseFrom"));
            objPalletsVerificationGlobal.setWarehouseTo(rs.getString("warehouseTo"));
            rs = dbConnection.getResultSet("select * from VerifyWHGin where GinNo=" + ginNo, objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GIN Verification already done");
                return false;
            }
            if(forSave){
                rs = dbConnection.getResultSet("select cnt = isnull(count(*),0) from tmpWHGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and GinNo='" + ginNo + "' and " +
                        "Verified='' having isnull(count(*),0)>0", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage(rs.getString("cnt").toString()+" transfers are not yet verified");
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from tmpWHGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                ResultSet rsDet;
                String toteId = "";


                rs = dbConnection.getResultSet("select srno,Palletno,Boxno " +
                        "from PLTDeliveryDetails where SrNo=" + ginNo +" group by palletno,srno,boxno", objGlobal.getConnection());
                while (rs.next()) {
                    toteId = "";

                    if (!dbConnection.insertUpdate("insert into tmpWHGinVerify(DeviceId,GinNo,PalletNo,ToteId,verifyTime,Verified) values " +
                            "('" + objGlobal.getDeviceName() + "'," + ginNo + "," +
                            "'" + rs.getString("PalletNo").toString() + "'," +
                            "'" + toteId + "'," +
                            " '"+objGlobal.getServerDate() +"','')", objGlobal.getConnection())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateGin:" + ex.toString());
            return false;
        }
    }

    public boolean saveGinVerification(String ginno) {
        String ginVerifyId = "";
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(sn),0)+1 from VerifyWHGin", objGlobal.getConnection());
            if (rs.next()) {
                ginVerifyId = rs.getString("sn").toString();
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into VerifyWHGin select " + ginVerifyId + ",'" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ",GinNo,ToteId,PalletNo,Verified from tmpWhGinVerify " +
                    "where GinNo='" + ginno + "' and DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("GinVerificationControl:saveGinVerification:ex:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("GinVerificationControl:saveGinVerification:e:" + e.toString());
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
            if (!dbConnection.insertUpdate("delete from tmpWhGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean validateTrfno(String trfToteId,String ginNo) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(trfToteId)) {
            objGlobal.setErrorMessage("Pallet no / Tote is empty");
            return false;
        }
        try {

            rs = dbConnection.getResultSet("select * from tmpWHGinVerify where Ginno = '"+ginNo+"' and (palletno='" + trfToteId + "' or toteid = '"+trfToteId+"') and deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Pallet no is not found in GIN - " +trfToteId);
                return false;
            }

            rs = dbConnection.getResultSet("select * from racks..binrack where (boxno='" + trfToteId + "' or toteid = '"+trfToteId+"')", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from racks..tmpwhracks where (palletno1='" + trfToteId + "' or palletno2 = '"+trfToteId+"')", objGlobal.getConnection());
                if (!rs.next()) {

                }else {
                    objGlobal.setErrorMessage("Pallet no is found in location - " + rs.getString("rowno") + "-" +rs.getString("cellno"));
                return false;
                }
            }else {objGlobal.setErrorMessage("Pallet no is found in DRIVE-IN location - " + rs.getString("location"));
                return false;
            }


            if (!dbConnection.insertUpdate("update tmpWHGinVerify set VerifyTime=getdate(),Verified='Y' where deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "(palletno='" + trfToteId + "' or toteid='" + trfToteId + "') and ginno=" + ginNo, objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletVerificationControl:validatePallet:" + ex.toString());
            return false;
        }
    }

    ArrayList<PalletsVerificationTicket> loadGinVerifyDetails() {
        ArrayList<PalletsVerificationTicket> listGinVerificationTicket = new ArrayList<PalletsVerificationTicket>();
        int tCnt = 0, yCnt = 0;
        try {
            listGinVerificationTicket.clear();
            rs = dbConnection.getResultSet("select * from tmpWHGinVerify where deviceid='" + objGlobal.getDeviceName() + "' order by VerifyTime desc,palletno", objGlobal.getConnection());
            while (rs.next()) {
                listGinVerificationTicket.add(new PalletsVerificationTicket(rs.getString("ginNo"),
                        rs.getString("palletNo"),  rs.getString("toteId"),rs.getString("verified")));
                tCnt++;
                if (rs.getString("verified").equals("Y")) yCnt++;
            }
            objPalletsVerificationGlobal.setScanCount(yCnt + "(" + tCnt + ")");
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:loadGinVerifyDetails:" + ex.toString());
            return null;
        }
        return listGinVerificationTicket;
    }

}