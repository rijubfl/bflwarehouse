package com.bflgroup.warehouse.ui.chuteconfiguration.techno;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationTechnoControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ChuteConfigurationTechnoGlobal objChuteConfigurationTechnoGlobal=ChuteConfigurationTechnoGlobal.getInstance();
    private boolean b_Result;
    private ResultSet rs;
    Connection conRobo = null;
    public ChuteConfigurationTechnoControl() {
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteConfigurationTechnoControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
    }
    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteConfigurationTechnoControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
        return true;
    }
    public List<String> loadLists(String type) {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            if(type.equals("S")) rs = dbConnection.getResultSet("select list=ShopName from bfldata.dbo.DataSettings order by list", conRobo);
            if(type.equals("C")) rs = dbConnection.getResultSet("select list=ChuteId from ROBOTICS.dbo.ChuteIdMaster order by list", conRobo);
            while (rs.next()) {
                arr.add(rs.getString("list"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("ChuteConfigurationTechnoControl:loadLists" + e);
            return null;
        }
    }

    public boolean getChuteDetails(String chuteid) {
        objChuteConfigurationTechnoGlobal.setShopId(0);
        objChuteConfigurationTechnoGlobal.setShopName("");
        objChuteConfigurationTechnoGlobal.setStatus(0);
        objChuteConfigurationTechnoGlobal.setToteid("");
        try {
            rs = dbConnection.getResultSet("select ShopId,ShopName,TotId,status=(select Status from ROBOTICS.dbo.ChuteIdMaster where ChuteId=a.ChuteId) from " +
                    "ROBOTICS.dbo.ChuteConfiguration a where ChuteId='" + chuteid + "'", conRobo);
            if (rs.next()) {
                objChuteConfigurationTechnoGlobal.setShopId(rs.getInt("ShopId"));
                objChuteConfigurationTechnoGlobal.setShopName(rs.getString("ShopName"));
                objChuteConfigurationTechnoGlobal.setStatus(rs.getInt("status"));
                objChuteConfigurationTechnoGlobal.setToteid(rs.getString("TotId"));
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("ChuteConfigurationTechnoControl:getChuteDetails" + e);
            return false;
        }
    }
}
