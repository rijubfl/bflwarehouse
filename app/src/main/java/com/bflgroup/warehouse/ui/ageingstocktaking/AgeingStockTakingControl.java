package com.bflgroup.warehouse.ui.ageingstocktaking;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingReports;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AgeingStockTakingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public AgeingStockTakingControl() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("STOCKTAKING");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Fetch Time error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("STOCKTAKING");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingStockTakingControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean loadZone() {
        List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return false;
        }
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select zone from stocktakingzone order by zone", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("zone"));
            }
            objAgeingStockTakingGlobal.setZoneList(arr);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingStockTakingControl.loadZone : " + e.toString());
            return false;
        }
    }

    public ArrayList<AgeingStockTakingReports> loadAgingStockTakingRpt(String ord) {
        ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports = new ArrayList<AgeingStockTakingReports>();
        objAgeingStockTakingGlobal.setTotal(0);
        double tot=0;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return null;
        }
        try {
            String ords="";
            if(ord.equals("User")) ords=" order by username";
            if(ord.equals("Zone")) ords=" order by zoneid";
            if(ord.equals("Quantity")) ords=" order by qty";
            rs = dbConnection.getResultSet("select username,zoneid,qty=sum(quantity) from stocktaking group by zoneid,username " + ords, objGlobal.getConnection());
            while (rs.next()) {
                listAgeingStockTakingReports.add(new AgeingStockTakingReports(rs.getString("username").toString(),
                        rs.getString("zoneid").toString(), rs.getInt("qty")));
                tot=tot+rs.getInt("qty");
            }
            objAgeingStockTakingGlobal.setTotal(tot);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadAgingStockTakingRpt:" + ex.toString());
            return null;
        }
        return listAgeingStockTakingReports;
    }


}
