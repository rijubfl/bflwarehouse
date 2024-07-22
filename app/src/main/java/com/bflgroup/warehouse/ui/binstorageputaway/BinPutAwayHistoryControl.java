package com.bflgroup.warehouse.ui.binstorageputaway;


import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;

public class BinPutAwayHistoryControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private ResultSet rs;

    public BinPutAwayHistoryControl() {
        objGlobal.setDbName("RACKS");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BinStorageWavePickControl : Connection error");
            }
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("RACKS");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BinStorageWavePickControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

   /* ArrayList<BinStorageWavePickTicket> loadBinStorageWaveDetails(String zoneId) {
        ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket = new ArrayList<BinStorageWavePickTicket>();
        try {
            listBinStorageWavePickTicket.clear();
            rs = dbConnection.getResultSet("select top 10 b.ToteId,b.BoxNo,a.BoxPerc,c.Barcode,c.Text,c.Color,c.PickOrder,c.Zones,c.DoubleDeep,a.CheckingType," +
                    "rowNo=(ROW_NUMBER() OVER(ORDER BY c.PickOrder)) from tempdata.dbo.SIMProdReadyPalletsList a,racks.dbo.BinRack b," +
                    "racks.dbo.BinRackMaster c where a.rack=b.Location and a.Rack=c.Barcode and c.Zones='" + zoneId + "' order by c.PickOrder,c.Barcode", objGlobal.getConnection());
            while (rs.next()) {
                listBinStorageWavePickTicket.add(new BinStorageWavePickTicket(rs.getString("toteId").toString(),
                        rs.getString("boxNo").toString(), rs.getString("boxPerc").toString(),
                        rs.getString("barcode").toString(), rs.getString("text").toString(),
                        rs.getString("color").toString(), rs.getString("pickOrder").toString(),
                        rs.getString("zones").toString(),rs.getString("DoubleDeep").toString(),
                        rs.getString("rowNo").toString(),rs.getString("CheckingType").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickControl:loadBinStorageWaveDetails:" + ex.toString());
            return null;
        }
        return listBinStorageWavePickTicket;
    }*/

}
