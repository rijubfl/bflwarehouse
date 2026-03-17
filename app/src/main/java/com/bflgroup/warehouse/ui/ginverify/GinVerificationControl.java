package com.bflgroup.warehouse.ui.ginverify;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GinVerificationControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private GinVerificationGlobal objGinVerificationGlobal = GinVerificationGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public GinVerificationControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GinVerificationControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("GinVerificationControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateGin(String ginNo, boolean forSave) {
        boolean allowMismatch = false;
        boolean skipGinCustomsClearance = false;
        String mfcsFromLoc = "", mfcsToLoc = "";
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
            rs = dbConnection.getResultSet("select cnt=count(distinct TrfNo) from bfldata.dbo.tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "' and Verified='Y'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("No record found for Save");
                return false;
            }
            if (objGlobal.getCountryCode().equals("UAE")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.vgoodsissuePLt where srno=" + ginNo, objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number");
                    return false;
                }
            } else {
                mfcsFromLoc = "JAFZA";
                rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssue where Sn=" + ginNo, objGlobal.getCloudCon());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from BFLDATA.dbo.contreceiptExport where GinNo='" + ginNo + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Container not received yet");
                    return false;
                }
                if (objGlobal.getValidateGinCustomsClearance().equals("Y")) {
                    rs = dbConnection.getResultSet("Select * from BFLDATA.dbo.SkipGinCustomsClearance where GinNo='" + ginNo + "' and mfcsFromLoc='" + mfcsFromLoc + "'", objGlobal.getConnection());
                    if (rs.next()) skipGinCustomsClearance = true;
                    if (!skipGinCustomsClearance) {
                        rs = dbConnection.getResultSet("Select * from BFLDATA.dbo.GINCUSTOMSCLEARANCE where GinNo='" + ginNo + "' and MFCSFROMLOC_PHY='" + mfcsFromLoc + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            objGlobal.setErrorMessage("Customs clearance has not yet been completed for this GIN.");
                            return false;
                        }
                    }
                }
            }
            if (forSave) {
                rs = dbConnection.getResultSet("select * from BFLDATA.dbo.WhGrnAllowMissing where Ginno='" + ginNo + "'", objGlobal.getConnection());
                if (rs.next()) allowMismatch = true;
                if (!allowMismatch) {
                    rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "' and Verified<>'Y'", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("cnt").equals("0")) {
                            objGlobal.setErrorMessage(rs.getString("cnt") + " boxes are not scanned yet. Please scan them before you try to save.");
                            return false;
                        }
                    }
                }
            } else {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                ResultSet rsDet;
                String toteId = "";
                if (objGlobal.getCountryCode().equals("UAE")) {
                    rs = dbConnection.getResultSet("select ShopName=ShopIssue,PalletNo,TrfNo,dbName=(select dataname from bfldata.dbo.DataSettings where ShopName=a.ShopIssue) from " +
                            "bfldata.dbo.vGoodsIssuePlt a where SrNo=" + ginNo, objGlobal.getConnection());
                } else {
                    rs = dbConnection.getResultSet("select ShopName=ActualShop,PalletNo,TrfNo,dbName=(select dataname from bfldata.dbo.DataSettings where ShopName=a.ActualShop) from " +
                            "bfldata.dbo.GoodsIssue a where sn=" + ginNo, objGlobal.getCloudCon());
                }
                while (rs.next()) {
                    toteId = "";
                    rsDet = dbConnection.getResultSet("select StoreIssue from " + rs.getString("dbName").toString() + ".dbo.TransferHeader " +
                            "where trfno='" + rs.getString("TrfNo").toString() + "'", objGlobal.getConnection());
                    if (rsDet.next()) {
                        toteId = rsDet.getString("StoreIssue").toString();
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpGinVerify(DeviceId,GinNo,ShopName,PalletNo,TrfNo,ToteId,Verified) values " +
                            "('" + objGlobal.getDeviceName() + "'," + ginNo + ",'" + rs.getString("ShopName").toString() + "'," +
                            "'" + rs.getString("PalletNo").toString() + "','" + rs.getString("TrfNo").toString() + "'," +
                            "'" + toteId + "','')", objGlobal.getConnection())) {
                        return false;
                    }
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpGinVerify set Verified=b.Verified from bfldata.dbo.tmpGinVerify a,BFLDATA.dbo.VerifyGin b where a.DeviceId='" + objGlobal.getDeviceName() + "' and " +
                        "a.TrfNo=b.TrfNo and b.GinNo=" + ginNo, objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateGin:" + ex);
            return false;
        }
    }

    public boolean saveGinVerification(String ginno) {
        String ginVerifyId = "";
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(sn),0)+1 from VerifyGin", objGlobal.getConnection());
            if (rs.next()) {
                ginVerifyId = rs.getString("sn").toString();
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into VerifyGin select " + ginVerifyId + ",'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ",GinNo," +
                    "TrfNo,ToteId,PalletNo,Verified from bfldata.dbo.tmpGinVerify where GinNo='" + ginno + "' and DeviceId='" + objGlobal.getDeviceName() + "' and Verified='Y' and VerifyTime is not null", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("GinVerificationControl:saveGinVerification:ex:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("GinVerificationControl:saveGinVerification:e:" + e);
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
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
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
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpGinVerify where (trfno='" + trfToteId + "' or toteid = '" + trfToteId + "')", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Transfer/Tote Id is not found in GIN - " + trfToteId);
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.VerifyGin where GinNo=" + ginNo + " and (trfno='" + trfToteId + "' or toteid = '" + trfToteId + "')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Transfer/Tote Id is already verified");
                return false;
            }
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpGinVerify set VerifyTime=getdate(),Verified='Y' where deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "(trfno='" + trfToteId + "' or toteid='" + trfToteId + "') and ginno=" + ginNo, objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateTrfno:" + ex);
            return false;
        }
    }

    ArrayList<GinVerificationTicket> loadGinVerifyDetails(String checked) {
        ArrayList<GinVerificationTicket> listGinVerificationTicket = new ArrayList<GinVerificationTicket>();
        int tCnt = 0, yCnt = 0;
        try {
            listGinVerificationTicket.clear();
            if(checked.equals("Y")){
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpGinVerify where Verified='Y' and deviceid='" + objGlobal.getDeviceName() + "' order by VerifyTime desc,trfNo", objGlobal.getConnection());
            } else {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' order by VerifyTime desc,trfNo", objGlobal.getConnection());
            }
            while (rs.next()) {
                listGinVerificationTicket.add(new GinVerificationTicket(rs.getString("ginNo"), rs.getString("shopName"),
                        rs.getString("palletNo"), rs.getString("trfNo"), rs.getString("toteId"),
                        rs.getString("verified")));
                if (rs.getString("verified").equals("Y")) yCnt++;
            }
            rs = dbConnection.getResultSet("select toto=count(*) from bfldata.dbo.tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                tCnt=rs.getInt("toto");
            }
            objGinVerificationGlobal.setScanCount(yCnt + "(" + tCnt + ")");
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:loadGinVerifyDetails:" + ex.toString());
            return null;
        }
        return listGinVerificationTicket;
    }

}