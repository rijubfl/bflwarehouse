package com.bflgroup.warehouse.ui.shopreturnverify;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.shopreturnverify.model.ShopReturnData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VerifyShopReturnControl {
    private ResultSet rs;
    private Global objGlobal = Global.getInstance();
    private DBConnection dbConnection = new DBConnection();
    private boolean b_Result;

    public VerifyShopReturnControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("1 ReceiveShopReturnsControl : Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("2 ReceiveShopReturnsControl : Cloud Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("1 ReceiveShopReturnsControl : Connection error");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("2 ReceiveShopReturnsControl : Cloud Connection error");
            }
        }
        return true;
    }

    public ShopReturnData shopNameCheck(String entryNumber) {
        String shopName = null;
        String category = null;
        String username = null;
        ShopReturnData shopReturnData = null;
        String shopLetter = entryNumber.substring(0, 2);
        b_Result = dbConnection.connectCloudDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("VerifyShopReturnControl : Connection error");
        } else {
            ResultSet rs1 = dbConnection.getResultSet("select * from BFLDATA.dbo.DataSettings where ShopLetter = '" + shopLetter + "'", objGlobal.getConnection());
            try {
                if (rs1.next()) {
                    shopName = rs1.getString("ShopName");
                    rs = dbConnection.getResultSet("select * from BFLKSA.dbo.StoreHeader where EntryNo = '" + entryNumber + "'", objGlobal.getCloudCon());
                    if (rs.next()) {
                        try {
                            if (rs.getString("shopname").endsWith("R1") || rs.getString("shopname").isEmpty()) {
                                category = rs.getString("TrfNo1");
                                username = String.valueOf(objGlobal.getUserName());
                                rs = dbConnection.getResultSet("select * from BFLDATA.dbo.ShopReturnVerify where entryno = '" + entryNumber + "'", objGlobal.getConnection());
                                if (rs.next()) {
                                    objGlobal.setErrorMessage("VerifyShopReturnControl:validateentry: The entry " + entryNumber + " is already received");
                                    shopReturnData = new ShopReturnData(null, null, objGlobal.getErrorMessage(), null, null);
                                } else {
                                    rs = dbConnection.getResultSet("select * from TEMPDATA.dbo.tmpVerifyShopReturns where entryno = '" + entryNumber + "' and deviceId = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                                    if (rs.next()) {
                                        objGlobal.setErrorMessage("VerifyShopReturnControl:validateentry: The entry " + entryNumber + " is already scanned");
                                        shopReturnData = new ShopReturnData(null, null, objGlobal.getErrorMessage(), null, null);
                                    } else
                                        shopReturnData = new ShopReturnData(entryNumber, shopName, "", category, username);
                                }
                            }
                            else{
                                objGlobal.setErrorMessage("VerifyShopReturnControl:validateentry: The entry " + entryNumber + " is not for the warehouse");
                                shopReturnData = new ShopReturnData(null, null, objGlobal.getErrorMessage(), null, null);
                            }

                        } catch (SQLException e) {
                            objGlobal.setErrorMessage("VerifyShopReturnControl:" + e);
                        }
                    } else {
                        objGlobal.setErrorMessage("VerifyShopReturnControl:validateentry: The entry" + entryNumber + " is not found");
                        shopReturnData = new ShopReturnData(null, null, objGlobal.getErrorMessage(), null, null);
                    }
                } else {
                    objGlobal.setErrorMessage("VerifyShopReturnControl:validateshopname : Shop name is not found for " + entryNumber);
                    shopReturnData = new ShopReturnData(null, null, objGlobal.getErrorMessage(), null, null);
                }
            } catch (SQLException e) {
                objGlobal.setErrorMessage("VerifyShopReturnControl:" + e);
            }
        }

        return shopReturnData;
    }

    public List<ShopReturnData> tempData() {
        List<ShopReturnData> shopReturnDataList = new ArrayList<>();
        rs = dbConnection.getResultSet("select * from TEMPDATA..tmpVerifyShopReturns where deviceId = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
        try {
            while (rs.next()) {
                try {
                    ShopReturnData shopReturnData = new ShopReturnData(rs.getString("entryNo"), rs.getString("shopName"), null,
                            rs.getString("category"), rs.getString("username"));
                    shopReturnDataList.add(shopReturnData);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return shopReturnDataList;
    }

    public boolean insertToTempDB(String entryNo, String shopName, String category, String username) {
        boolean status;
        String insertQuery = "insert into TEMPDATA..tmpVerifyShopReturns values('" + entryNo + "','" + shopName + "','" + category + "','" + username + "','"+objGlobal.getDeviceName()+"')";
        status = dbConnection.insertUpdate(insertQuery, objGlobal.getConnection());
        return status;

    }

    public boolean clearTempData() {
        String query = "DELETE FROM TEMPDATA..tmpVerifyShopReturns where deviceId = '" + objGlobal.getDeviceName() + "'";
        return dbConnection.insertUpdate(query, objGlobal.getConnection());
    }

    public int countTempData() {
        int count = 0;
        rs = dbConnection.getResultSet("select count(*) from TEMPDATA..tmpVerifyShopReturns where deviceId = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
        try {
            if (rs.next()) {
                count = rs.getInt(1); // Retrieve the count from the first column
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public boolean saveShopReturnsVerify() {

        rs = dbConnection.getResultSet("select entryno from TEMPDATA.dbo.tmpVerifyShopReturns where entryno in(select entryno from bfldata.dbo.ShopReturnVerify) and deviceId = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
        try {
            if (!rs.next()) {
                String insertQuery = "INSERT INTO BFLDATA.dbo.ShopReturnVerify (trndate, trntime, entryno, shopname, category, username) SELECT  CONVERT(VARCHAR, GETDATE(), 103), CONVERT(VARCHAR(8), GETDATE(), 108), entryno,shopname,category,username FROM TEMPDATA.dbo.tmpVerifyShopReturns where deviceId = '" + objGlobal.getDeviceName() + "'";
                return dbConnection.insertUpdate(insertQuery, objGlobal.getConnection());
            } else {
                StringBuilder message = new StringBuilder();
                do {
                    if (message.length() > 0) message.append(",");
                    message.append(rs.getString("entryno"));
                } while (rs.next());
                objGlobal.setErrorMessage("VerifyShopReturnControl:" + "The entries "+message + " already received.");
                return false;
            }
        } catch (SQLException e) {
            objGlobal.setErrorMessage("VerifyShopReturnControl:" + e);
            return false;
        }

    }
}
