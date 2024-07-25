package com.bflgroup.warehouse.ui.ginverifylocal;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GinVerificationlocalControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private GinVerificationlocalGlobal objGinVerificationGlobal = GinVerificationlocalGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public GinVerificationlocalControl() {
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
            rs = dbConnection.getResultSet("select * from bfldata..vgoodsissuePLt where srno =" + ginNo, objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("GIN Number is invalid");
                return false;
            }
//            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.contreceiptExport where GinNo='" + ginNo + "'", objGlobal.getConnection());
//            if (!rs.next()) {
//                objGlobal.setErrorMessage("Container not received yet");
//                return false;
//            }
            rs = dbConnection.getResultSet("select * from verifyGinlocal where GinNo=" + ginNo, objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GIN Verification already done");
                return false;
            }
            if(forSave){
               /* rs = dbConnection.getResultSet("select cnt=isnull(count(*),0) from tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and GinNo='" + ginNo + "' and " +
                        "Verified='' having isnull(count(*),0)>0", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage(rs.getString("cnt").toString()+" transfers are not yet verified");
                    return false;
                }*/
            } else {
                if (!dbConnection.insertUpdate("delete from tmpGinVerifylocal where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                ResultSet rsDet;
                String toteId = "";

                /*rs = dbConnection.getResultSet("select ShopName,PalletNo,TrfNo,dbName=(select dataname from DataSettings where ShopName=a.ShopIssue) " +
                        "from vGoodsIssuePlt a where SrNo=" + ginNo, objGlobal.getConnection());*/

                rs = dbConnection.getResultSet("select ShopIssue,PalletNo,TrfNo,dbName=(select dataname from bfldata.dbo.DataSettings where ShopName=a.shopissue) from " +
                        "bfldata.dbo.vgoodsissuePLt a where srno=" + ginNo, objGlobal.getConnection());
                while (rs.next()) {
                    toteId = "";
                    rsDet = dbConnection.getResultSet("select StoreIssue from " + rs.getString("dbName").toString() + ".dbo.TransferHeader " +
                            "where trfno='" + rs.getString("TrfNo").toString() + "'", objGlobal.getConnection());
                    if (rsDet.next()) {
                        toteId = rsDet.getString("StoreIssue").toString();
                    }
                    if (!dbConnection.insertUpdate("insert into tmpGinVerifylocal(DeviceId,GinNo,ShopName,PalletNo,TrfNo,ToteId,Verified) values " +
                            "('" + objGlobal.getDeviceName() + "'," + ginNo + ",'" + rs.getString("ShopIssue").toString() + "'," +
                            "'" + rs.getString("PalletNo").toString() + "','" + rs.getString("TrfNo").toString() + "'," +
                            "'" + toteId + "','')", objGlobal.getConnection())) {
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
            rs = dbConnection.getResultSet("select sn=isnull(max(sn),0)+1 from verifyGinlocal", objGlobal.getConnection());
            if (rs.next()) {
                ginVerifyId = rs.getString("sn").toString();
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into verifyGinlocal select " + ginVerifyId + ",'" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ",GinNo,TrfNo,ToteId,PalletNo,Verified from tmpGinVerifylocal " +
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
            if (!dbConnection.insertUpdate("delete from tmpGinVerifylocal where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
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
            objGlobal.setErrorMessage("Transfer / Tote is empty");
            return false;
        }
        try {
//            if(trfToteId.substring(0,2).equals("FN") || trfToteId.substring(0,2).equals("FT")){
            rs = dbConnection.getResultSet("select * from tmpGinVerifylocal where (trfno='" + trfToteId + "' or toteid = '"+trfToteId+"')", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Transfer/Tote Id is not found in GIN - " +trfToteId);
                return false;
            }
//            } else {
//                rs = dbConnection.getResultSet("select * from tmpGinVerify where toteid='" + trfToteId + "'", objGlobal.getConnection());
//                if (!rs.next()) {
//                    objGlobal.setErrorMessage("Transfer/Tote Id is not found in GIN");
//                    return false;
//                }
//            }
            if (!dbConnection.insertUpdate("update tmpGinVerifylocal set VerifyTime=getdate(),Verified='Y' where deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "(trfno='" + trfToteId + "' or toteid='" + trfToteId + "') and ginno=" + ginNo, objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateTrfno:" + ex.toString());
            return false;
        }
    }

    ArrayList<GinVerificationlocalTicket> loadGinVerifyDetails() {
        ArrayList<GinVerificationlocalTicket> listGinVerificationTicket = new ArrayList<GinVerificationlocalTicket>();
        int tCnt = 0, yCnt = 0;
        try {
            listGinVerificationTicket.clear();
            rs = dbConnection.getResultSet("select * from tmpGinVerifylocal where deviceid='" + objGlobal.getDeviceName() + "' order by VerifyTime desc,trfNo", objGlobal.getConnection());
            while (rs.next()) {
                listGinVerificationTicket.add(new GinVerificationlocalTicket(rs.getString("ginNo"), rs.getString("shopName"),
                        rs.getString("palletNo"), rs.getString("trfNo"), rs.getString("toteId"),
                        rs.getString("verified")));
                tCnt++;
                if (rs.getString("verified").equals("Y")) yCnt++;
            }
            objGinVerificationGlobal.setScanCount(yCnt + "(" + tCnt + ")");
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:loadGinVerifyDetails:" + ex.toString());
            return null;
        }
        return listGinVerificationTicket;
    }

}