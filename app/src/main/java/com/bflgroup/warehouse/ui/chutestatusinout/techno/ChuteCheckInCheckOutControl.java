package com.bflgroup.warehouse.ui.chutestatusinout.techno;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChuteCheckInCheckOutControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private InOutGlobal objInOutGlobal = InOutGlobal.getInstance();
    private TransferReceipt objTransferReceipt = new TransferReceipt();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    Connection conRobo = null;

    public ChuteCheckInCheckOutControl() {
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
        return true;
    }

    public boolean validateCheckInOut(String direction, String chuteId, String toteId, String shopId, String shopName, String status, String shopToteType) {
        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(conRobo)) {
            objGlobal.setErrorNo("validateCheckInOut:001:");
            return false;
        }
        if (TextUtils.isEmpty(chuteId)) {
            objGlobal.setErrorMessage("ChuteId is empty");
            return false;
        }
        if (TextUtils.isEmpty(toteId)) {
            objGlobal.setErrorMessage("Tote is empty");
            return false;
        }
        if (!checkValidChuteId(chuteId)) {
            objGlobal.setErrorMessage("Invalid Chute ID, " + chuteId);
            return false;
        }
        if (!checkValidToteId(toteId)) {
            objGlobal.setErrorMessage("Invalid Tote ID, " + chuteId);
            return false;
        }
        if (TextUtils.isEmpty(shopId) || TextUtils.isEmpty(shopName)) {
            objGlobal.setErrorMessage("Shop Id or Shop name is empty");
            return false;
        }
        if (TextUtils.isEmpty(shopToteType)) {
            objGlobal.setErrorMessage("Shop tote type is blank");
            return false;
        }
        if (TextUtils.isEmpty(status)) {
            objGlobal.setErrorMessage("Chute Status is Empty");
            return false;
        }
        if (TextUtils.isEmpty(getShopIdFromChuteId(chuteId))) {
            objGlobal.setErrorMessage("Shop Id is empty, Please check chute configuration, " + chuteId);
            return false;
        }
        if (validateChuteError(chuteId)) {
            objGlobal.setErrorMessage("ChuteId Error: " + chuteId + ", Please contact IT");
            return false;
        }
        if (validateTotidUsed(toteId)) {
            objGlobal.setErrorMessage("Totid is alrady used, Totid: " + toteId + "");
            return false;
        }
        if (direction == "IN") {
            if (!TextUtils.isEmpty(getToteIdFromChuteId(chuteId))) {
                objGlobal.setErrorMessage("Tot Id is used in another chute, Please check, " + chuteId);
                return false;
            }
            if (transferPendingInChuteId(chuteId, shopId)) {
                objGlobal.setErrorMessage("Pending transfer found, " + chuteId);
                return false;
            }
            if (!TextUtils.isEmpty(getTotIdAlreadyAssignFromChuteId(toteId))) {
                objGlobal.setErrorMessage("Tot id found in another chute, " + toteId);
                return false;
            }
        }
        if (direction == "OUT") {
            s_Result = getToteIdFromChuteId(chuteId);
            if (TextUtils.isEmpty(s_Result)) {
                objGlobal.setErrorMessage("Tot Id is empty, Please check, " + chuteId);
                return false;
            }
            if (!TextUtils.equals(s_Result, toteId)) {
                objGlobal.setErrorMessage("Tot Id and Chute Id is not match, Please check");
                return false;
            }
            if (!transferPendingInChuteId(chuteId, shopId)) {
                objGlobal.setErrorMessage("No record found for transfer, " + chuteId);
                return false;
            }
        }
        return true;
    }

    public boolean saveChuteIn(String chuteId, String toteId, String shopId, String shopName, String status) {
        Connection con;
        con = conRobo;
        try {
            if (!dbConnection.getServerDateTime(conRobo)) {
                objGlobal.setErrorNo("saveChuteIn:001:");
                return false;
            }
            con.setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into ChuteCheckin values('" + chuteId + "','" + toteId + "','" + shopId + "','" + shopName + "'," +
                    "'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ")", con)) {
                con.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteConfigurationlog (ChuteId,ShopId,ShopName,TotId,Trndate,userId,direction) " +
                    "select ChuteId,ShopId,ShopName,TotId,getdate(),'" + objGlobal.getUserId() + "','IN' from ChuteConfiguration where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteConfiguration set TotId='" + toteId + "' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteStatusLog select ChuteId,Status,'" + status + "','" + objGlobal.getUserId() + "',getdate() " +
                    "from ChuteIdMaster where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteIdMaster set Status='" + status + "' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                return false;
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception exception) {
            try {
                con.rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("inChuteStatus:sqlException: " + sqlException.toString());
                return false;
            }
            objGlobal.setErrorMessage("inChuteStatus: exception: " + exception.toString());
            return false;
        }
        return true;
    }

    public boolean saveChuteOut(String chuteId, String toteId, String shopId, String shopName) {
        return objTransferReceipt.transferReceipt(chuteId, toteId, shopId, shopName);
    }

    public boolean checkValidChuteId(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select * from ChuteIdMaster where ChuteId='" + chuteId + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:checkPendingTransferInChuteId:" + ex.toString());
            return false;
        }
    }

    public boolean checkValidToteId(String toteId) {
        try {
            rs = dbConnection.getResultSet("select * from TotIdMaster where TotId='" + toteId + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:checkPendingTransferInChuteId:" + ex.toString());
            return false;
        }
    }

    public boolean validateChuteError(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select * from ErrChuteId where ChuteId='" + chuteId + "' and ErrType='DGR'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:checkPendingTransferInChuteId:" + ex.toString());
            return false;
        }
    }

    public boolean validateTotidUsed(String toteId) {
        try {
            rs = dbConnection.getResultSet("select * from SortTask where ToteId='" + toteId + "' and TrnDate='" + objGlobal.getServerDate() + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:validateTotidUsed:" + ex.toString());
            return false;
        }
    }

    public String getShopIdFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ShopId", "ChuteId", chuteId);
    }

    public String getTotIdAlreadyAssignFromChuteId(String totId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ChuteId", "TotId", totId);
    }

    public String getShopnameFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ShopName", "ChuteId", chuteId);
    }

    public String getShopToteidType(String shopname) {
        String totetype = "";
        try {
            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.ToteTypes where shopname='" + shopname + "'", conRobo);
            while (rs.next()) {
                if (!totetype.equals("")) totetype = totetype + ", ";
                totetype = rs.getString("ToteType");
            }
            return totetype;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:validateTotidUsed:" + ex);
            return "";
        }
    }

    public String getToteIdFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "TotId", "ChuteId", chuteId);
    }

    public String getStatusFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteIdMaster", "Status", "ChuteId", chuteId);
    }

    public boolean getLastChuteInOut(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select top 1 direction=direction+' - '+left(cast(TrnTime as varchar),8) from vChuteInOut " +
                    "where ChuteId='"+chuteId+"' order by TrnDate desc,TrnTime desc", conRobo);
            if (rs.next()) {
                objInOutGlobal.setChuteLastInOut(rs.getString("direction").toString());
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:getLastChuteInOut:" + ex);
            return false;
        }
    }

    public String getChuteStatus(String chuteId) {
        switch (getStatusFromChuteId(chuteId)) {
            case "0":
                return "Normal";
            case "1":
                return "Full";
            case "2":
                return "Disable";
            case "3":
                return "Unknown";
        }
        return "";
    }

    public boolean transferPendingInChuteId(String chuteId, String shopId) {
        try {
            rs = dbConnection.getResultSet("select * from SortingConformationDetail where TransferNo='' and ChuiteId='" + chuteId + "' and ShopId='" + shopId + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:checkPendingTransferInChuteId:" + ex.toString());
            return false;
        }
    }

    public boolean updateChuteApi(String updtField, String ShopId, String TrfNo, String ChuteNo) {
        b_Result = (dbConnection.insertUpdate("update SortTask set " + updtField + "='Y' where ShopId='" + ShopId + "' and TrfNo='" + TrfNo + "' and ChuteNo='" + ChuteNo + "'", conRobo));
        return b_Result;
    }

    public boolean updateChuteApiLog(String prcess, String ShopId, String TrfNo, String ChuteNo, String labelInfo, String toteid, String shopname,String chuteid, String status,String msg) {
        b_Result = (dbConnection.insertUpdate("insert into ROBOTICS.dbo.SortTaskLog(Process,TrnDate,TrnTime,ToteId,TrfNo,ShopName,UserName,ChuteId,ChuteNo,LabelInfo,Status,Msg) values " +
                "('" + prcess + "',convert(varchar,getdate(),103),convert(varchar,getdate(),8),'"+toteid+"','" + TrfNo + "','" + shopname + "','" + objGlobal.getUserName() + "'," +
                "'" + chuteid + "','" + ChuteNo + "','" + labelInfo + "','" + status + "','" + msg + "')", conRobo));
        return b_Result;
    }

    public ArrayList<ChuteCheckInCheckOutItemTicket> itemsForPL(String chuteId, String shopId) {
        ArrayList<ChuteCheckInCheckOutItemTicket> listChuteCheckInCheckOutItemTicket = new ArrayList<ChuteCheckInCheckOutItemTicket>();
        int totQty = 0;
        try {
            rs = dbConnection.getResultSet("select itemcode,description='',qty=sum(qty) from SortingConformationDetail where " +
                    "TransferNo='' and ChuiteId='" + chuteId + "' and ShopId='" + shopId + "' group by itemcode", conRobo);
            while (rs.next()) {
                listChuteCheckInCheckOutItemTicket.add(new ChuteCheckInCheckOutItemTicket(rs.getString("itemcode").toString(),
                        rs.getString("description").toString(), rs.getInt("qty")));
                totQty = totQty + rs.getInt("qty");
            }
            objInOutGlobal.setTrfTotQty(totQty);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:loadTransferItemsAll:" + ex);
            return listChuteCheckInCheckOutItemTicket;
        }
        return listChuteCheckInCheckOutItemTicket;
    }
}