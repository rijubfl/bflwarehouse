package com.bflgroup.warehouse.ui.rackquery;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.rackquery.model.RackDetailsData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RackQueryControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

    private boolean b_Result;

    public RackQueryControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("WarehouseDeliveryControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("WarehouseDeliveryControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    List<RackDetailsData> rackDetails(String rackLocation) {
        List<RackDetailsData> rackDetailsDataList = new ArrayList<>();
        String query = "select * from RACKS..BinRack where Location = '" + rackLocation + "'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());

        try {
            while (rs.next()) {
                RackDetailsData rackDetailsData = new RackDetailsData(
                        rs.getString("Warehouse"), rs.getString("ToteId"), rs.getString("BoxNo"),rs.getString("Location")
                );
                rackDetailsDataList.add(rackDetailsData);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        if (!rackDetailsDataList.isEmpty()){
            if (!insertToTempDB(rackLocation)){
                rackDetailsDataList.clear();
                RackDetailsData rackDetailsData = new RackDetailsData("","","","");
                rackDetailsDataList.add(rackDetailsData);

            }
        }
        return rackDetailsDataList;
    }
    List<RackDetailsData> tempData() {
        List<RackDetailsData> rackDetailsDataList = new ArrayList<>();
        String query = "select * from TEMPDATA..rackdetailsksa where UserId ='"+objGlobal.getUserId()+"' and DeviceId = '"+objGlobal.getDeviceName()+"'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());

        try {
            while (rs.next()) {
                RackDetailsData rackDetailsData = new RackDetailsData(
                        rs.getString("Warehouse"), rs.getString("ToteId"), rs.getString("BoxNo"),rs.getString("Location")
                );
                rackDetailsDataList.add(rackDetailsData);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return rackDetailsDataList;
    }

    private boolean insertToTempDB(String rackLocation) {
        boolean status;
        String insertQuery = "INSERT INTO TEMPDATA..rackdetailsksa (Warehouse,Location, ToteId, BoxNo, UserId, DeviceId) SELECT Warehouse,Location, ToteId, BoxNo, '"+objGlobal.getUserId()+"', '"+objGlobal.getDeviceName()+"' FROM RACKS..BinRack WHERE Location = '"+rackLocation+"'";
        status = dbConnection.insertUpdate(insertQuery, objGlobal.getConnection());

        return  status;

    }

    boolean tempDataClear(){
        String query = "delete from TEMPDATA..rackdetailsksa where UserId ='"+objGlobal.getUserId()+"' and DeviceId = '"+objGlobal.getDeviceName()+"'";
        return dbConnection.insertUpdate(query, objGlobal.getConnection());
    }
    public int tempDataCount(){
        String query = "SELECT COUNT(*) as Count FROM TEMPDATA..rackdetailsksa where UserId ='"+objGlobal.getUserId()+"' and DeviceId = '"+objGlobal.getDeviceName()+"'";
        ResultSet rs = dbConnection.getResultSet(query,objGlobal.getConnection());
        try {
            if (rs.next()){
                return rs.getInt("Count");
            }
            else{
                return  0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
