package com.bflgroup.warehouse.ui.chuteconfiguration.jafza;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationJafzaControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ChuteConfigurationJafzaGlobal objChuteConfigurationJafzaGlobal=ChuteConfigurationJafzaGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    Connection conRobo = null;
    public ChuteConfigurationJafzaControl() {
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteConfigurationJafzaControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteConfigurationJafzaControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
        return true;
    }

    public List<String> loadShops() {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select ShopName=' ' union all select ShopName from bfldata.dbo.DataSettings order by ShopName", conRobo);
            while (rs.next()) {
                arr.add(rs.getString("ShopName"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public boolean chutesStatusValidate(String chuteId,String shopName,String shopId,int status,String toteId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(chuteId)) {
            objGlobal.setErrorMessage("ChuteId is empty");
            return false;
        }
        if(status<=0 && status>=4 ){
            objGlobal.setErrorMessage("Invalid Status Code");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from ChuteIdMaster where ChuteId='" + chuteId + "'", conRobo);
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Chute ID, " + chuteId);
                return false;
            }
            rs = dbConnection.getResultSet("select * from TotIdMaster where TotId='" + toteId + "'", conRobo);
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Toteid ID, " + chuteId);
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteConfigurationJafzaControl:chutesStatusValidate:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean chutesStatusSave(String chuteId,String shopName,String shopId,int status,String toteId) {
        try {
            String strStatus="";
            if(status==0) strStatus="Normal";
            if(status==1) strStatus="Full";
            if(status==2) strStatus="Disable";
            if(status==3) strStatus="Unknown";
            conRobo.setAutoCommit(false);
            if (!dbConnection.insertUpdate("update ChuteConfiguration set ShopId=" + shopId + ",ShopName='" + shopName + "',TotId='" + toteId + "' where ChuteId='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteIdMaster set Status=" + status + " where ChuteId='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update SortingConformationDetail set TotId='" + toteId + "' where ChuiteId='" + chuteId + "' and TransferNo=''", conRobo)) {
                conRobo.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteConfigurationlogPda values('" + chuteId + "',getdate(),'" + shopName + "','" + strStatus + "','" + toteId + "','" + objGlobal.getUserName() + "')", conRobo)) {
                conRobo.rollback();
                return false;
            }
            conRobo.commit();
            conRobo.setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:ex:" + ex.toString());
                conRobo.rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:e:" + e.toString());
                return false;
            }
            return false;
        }
    }

    public String getShopid(String shopname){
        try {
            rs = dbConnection.getResultSet("select RoboShopId from bfldata.dbo.DataSettings where shopname='" + shopname + "'", conRobo);
            if (rs.next())
                return rs.getString("RoboShopId");
            else
                return "";
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteConfigurationJafzaControl:getShopid:" + ex.toString());
            return "";
        }
    }

    public boolean validateChutes(String chuteId) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(chuteId)) {
            objGlobal.setErrorMessage("ChuteId is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from ChuteIdMaster where ChuteId='" + chuteId + "'", conRobo);
            if (rs.next()) {
                objChuteConfigurationJafzaGlobal.setStatus(rs.getInt("status"));
            } else {
                objGlobal.setErrorMessage("Invalid Chute ID, " + chuteId);
                return false;
            }
            rs = dbConnection.getResultSet("select * from ChuteConfiguration where ChuteId='" + chuteId + "'", conRobo);
            if (rs.next()) {
                objChuteConfigurationJafzaGlobal.setShopId(rs.getInt("ShopId"));
                objChuteConfigurationJafzaGlobal.setShopName(rs.getString("ShopName"));
                objChuteConfigurationJafzaGlobal.setToteid(rs.getString("TotId"));
            } else {
                objGlobal.setErrorMessage("Invalid Chute ID, " + chuteId);
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ChuteConfigurationJafzaControl:checkValidChuteId("+chuteId+"):" + ex.toString());
            return false;
        }
        return true;
    }

    ArrayList<ChuteConfigurationJafzaHistoryTicket> loadChuteConfigurationJafzaHistory() {
        ArrayList<ChuteConfigurationJafzaHistoryTicket> listChuteConfigurationJafzaHistory = new ArrayList<ChuteConfigurationJafzaHistoryTicket>();
        try {
            listChuteConfigurationJafzaHistory.clear();
            rs = dbConnection.getResultSet("select top 10 * from ChuteConfigurationlogPda where username='" + objGlobal.getUserName() + "' order by trndate desc", conRobo);
            while (rs.next()) {
                listChuteConfigurationJafzaHistory.add(new ChuteConfigurationJafzaHistoryTicket(rs.getString("ChuteId").toString(),
                        "123", rs.getString("ShopName").toString(),
                        rs.getString("Status").toString(), rs.getString("ToteId").toString(), rs.getString("username").toString()));

                listChuteConfigurationJafzaHistory.add(new ChuteConfigurationJafzaHistoryTicket("123","123", "123",
                        "123", "123", "123"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:loadChuteConfigurationJafzaHistory:" + ex.toString());
            return null;
        }
        return listChuteConfigurationJafzaHistory;
    }


}
