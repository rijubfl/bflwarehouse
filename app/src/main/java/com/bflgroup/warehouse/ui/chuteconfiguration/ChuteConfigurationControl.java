package com.bflgroup.warehouse.ui.chuteconfiguration;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChuteConfigurationControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private ResultSet rs;
    Connection conRobo = null;

    public ChuteConfigurationControl() {
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
            objGlobal.setErrorMessage("" + e);
            return null;
        }
    }


}
