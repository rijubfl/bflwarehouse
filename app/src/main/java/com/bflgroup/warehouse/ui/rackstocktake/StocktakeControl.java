package com.bflgroup.warehouse.ui.rackstocktake;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.rackstocktake.model.RackHistoryModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StocktakeControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;
    private boolean b_Result;

    public StocktakeControl(Context context) {
        objGlobal.setDbName("Racks");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("StocktakeControl : Connection error");
        }
    }


    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("Racks");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("StocktakeControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }
    public List<String> loadWarehouse(){
        List<String> arr = null;
        try {
            arr = new ArrayList<String>();

            rs = dbConnection.getResultSet("select distinct WareHouse  from racks..WarehouseRackMaster", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("WareHouse"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }


    public Boolean isValidpallet(String palletno, String position, Context context){
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }
        try {




       ResultSet rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletno + "' and closed='N'", objGlobal.getConnection());

            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletno + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletno + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletno + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletno + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                               // objGlobal.setErrorMessage("Pallet Number " + palletno + " is closed already");
                                okMessage("Alert","Pallet Number " + palletno + " is closed already or invalid", context );
                                return false;
                            }
                        }
                    }
                }
            }

            String query = "select * from racks..WarehouseRackDetStockTake  where (palletno1='" + palletno + "' or palletno2='"+palletno+"')and trndate >= DATEADD(day,-3,GETDATE())";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (!rs.next()) {
                return true;
            }
            else{
                okMessage("Alert","Pallet Number " + palletno + " is already Scanned in rack - "+rs.getString("rowno") +"-"+rs.getString("cellno"), context );
                // objGlobal.setErrorMessage();
                return false;
            }

        } catch (SQLException e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
      //  return true;
    }

    public String BoxPalletCount(String palletNo){
        String Boxcount = "0";
        try {

            if(palletNo.substring(0,3).equals("USA")) {
                String query = "select count = count(distinct Boxno) from USA..vUPCBOXdet where palletno = '" + palletNo + "' and closed = 'N'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }else if(palletNo.substring(0,3).equals("PLT")){
                String query = "select count = count(distinct Boxno) from BFLDATA..vR1Pallet where palletno = '" + palletNo + "' and closed = 'N'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Boxcount;
    }
    public Boolean isValidTote(String toteId, Context context){
            try {
                StocktakeGlobal.setBoxNo("");
                rs = dbConnection.getResultSet("select top 1 Boxno=TrfNo from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader a where (storeissue='" + toteId + "' or trfno='" + toteId + "') order by TrfDate desc", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select Boxno=boxno from usa.dbo.upcboxhead where (ToteID='" + toteId + "' or BoxNo='" + toteId + "') and Closed='N'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select distinct Boxno=a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno and (b.TotId='" + toteId + "' or b.Boxno='" + toteId + "') and a.Closed='N'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.r1pallethead where palletno='" + toteId + "' and closed='N'", objGlobal.getConnection());
                            if (!rs.next()) {
                                rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.usapallets where palletno='" + toteId + "' and closed='N'", objGlobal.getConnection());
                                if (!rs.next()) {
                                    rs = dbConnection.getResultSet("select Boxno=palletno from usa.dbo.usapallets where palletno='" + toteId + "'", objGlobal.getConnection());
                                    if (!rs.next()) {
                                        rs = dbConnection.getResultSet("select Boxno=palletno from bfldata.dbo.GoodsIssueHead where palletno='" + toteId + "'", objGlobal.getConnection());
                                        if (!rs.next()) {
                                            rs = dbConnection.getResultSet("select top 1 Boxno=palletno from abudata.dbo.tcmitemsall where palletno='" + toteId + "'", objGlobal.getConnection());
                                            if (!rs.next()) {
                                                objGlobal.setErrorMessage("Invalid Box / Pallet Number is closed or Invalid (" + toteId + ")");
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                StocktakeGlobal.setBoxNo(rs.getString("Boxno"));
                    rs = dbConnection.getResultSet("select * from racks..WarehouseRackDetStockTake where (Warehouse='BINRACK' or warehouse = 'YOTO-BINRACK') and palletno1='" + toteId + "' and trndate >= DATEADD(day,-3,GETDATE())  ", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("ToteID found in location, " + rs.getString("rowno").toString());
                        return false;
                    }
                return true;
            } catch (Exception ex) {
                objGlobal.setErrorMessage("BinBatchInControl:validateToteid:" + ex.toString());
                return false;
            }

    }


   public Boolean saveRackDetails(String warehouse, String rackno, String pallettop, String PalletDown, String Boxcount1, String Boxcount2){

        String rowno = "";
                String cellno = "";
                String query = "";
        if(warehouse.equals("BINRACK") || warehouse.equals("YOTO-BINRACK")){
            rowno = rackno;
            PalletDown = StocktakeGlobal.getBoxNo();
        }else if(warehouse.equals("JAFZAVNA")){
            int p=rackno.lastIndexOf("-");
            rowno = rackno.substring(0, p);
            cellno= Integer.valueOf(rackno.substring(p+1)).toString();
        }

        else if (warehouse.equals("JAFZAFLR")){
            //int p=rackno.lastIndexOf("-");
            rowno = rackno;
            // String cell= Integer.valueOf(rackno.substring(p+1)).toString();
            cellno = String.valueOf(getAutoFLoor(warehouse, rowno));
        }
        else if (warehouse.equals("JAFZA_DRIVEIN")){
           // int p=rackno.lastIndexOf("-");
            rowno = rackno;
            PalletDown = pallettop;
            cellno = String.valueOf(getAutoFLoor(warehouse, rowno));
        }
        else if (warehouse.equals("Techno-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
            String aRackNumber[];
            aRackNumber = rackno.split("-");
            rowno = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            cellno = String.valueOf(Integer.valueOf(aRackNumber[4]));
        }
        else {
            String[] rackNumber = rackno.split("-");
            rowno = rackNumber[0].toString();
            cellno = rackNumber[1];
        }

       if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
           objGlobal.setErrorNo("transferReceipt:007");
       }

    try {
    query = "insert into racks..WarehouseRackDetStockTake (Warehouse,RowNo,CellNo,TrnDate,TrnTime,PalletNo1,PalletNo2,Userid,  BoxCount1, BoxCount2)values('" + warehouse + "','" + rowno + "', '" + cellno + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "' ,'" + pallettop + "','" + PalletDown + "', '" + objGlobal.getUserId() + "', '"+Boxcount1+"', '"+Boxcount2+"')";
    if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
        return false;

    }

    }catch(Exception e){
    Log.e("Error", e.toString());
    }
       return true;

    }


    private void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public Boolean ValidRack(String warehouse, String rackno, Context context){
        String RowNo = "";
        int CellNo = 0;

        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }


        if (!checkConnection()) {
            return false;
        }



        if(warehouse.equals("BINRACK") || warehouse.equals("YOTO-BINRACK")){
            RowNo = rackno;

            try {
                String query = "select *  from racks..WarehouseRackMaster where warehouse = '"+warehouse+"' and rowno = '"+rackno+"'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (!rs.next()) {
                    okMessage("Alert", "Rack is invalid - " +RowNo, context);
                    return false;
                }
                return true;
            } catch (SQLException e) {
                objGlobal.setErrorMessage(e.toString());
                return false;
            }

        }
        else if (warehouse.equals("JAFZAVNA")) {

            int p = rackno.lastIndexOf("-");
            RowNo = rackno.substring(0, p);

            CellNo = Integer.valueOf(rackno.substring(p + 1));
        }
        else if (warehouse.equals("JAFZAFLR") || warehouse.equals("JAFZA_DRIVEIN")) {
            RowNo = rackno;

            CellNo = getAutoFLoor(warehouse, rackno);
        }
        else if (warehouse.equals("Techno-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
            String aRackNumber[];
            aRackNumber = rackno.split("-");
            RowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            CellNo = Integer.valueOf(aRackNumber[4]);
        }

        else {
            String[] rackNumber1 = rackno.split("-");
            rackNumber1[1] = Integer.valueOf(rackNumber1[1]).toString();
            RowNo = rackNumber1[0];
            CellNo = Integer.parseInt(rackNumber1[1]);
        }
            try {
                String query = "select *  from racks..WarehouseRackMaster where warehouse = '"+warehouse+"' and rowno = '"+RowNo+"' and cellno = '"+CellNo+"'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());

                if (!rs.next()) {
                    okMessage("Alert", "Rack is invalid - " +RowNo, context);
                    return false;
                }

            } catch (SQLException e) {
                objGlobal.setErrorMessage(e.toString());
                return false;
            }
            return true;

        }

    public Boolean RackScanned(String warehouse, String rackno, Context context){
        String RowNo = "";
        int CellNo = 0;

        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }


        if(warehouse.equals("BINRACK") || warehouse.equals("YOTO-BINRACK")){
            RowNo = rackno;
            try {
                String query1 = "select *  from racks..WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+RowNo+"' and trndate >= DATEADD(day,-3,GETDATE())";
                ResultSet rs1 = dbConnection.getResultSet(query1, objGlobal.getConnection());
                if(rs1.next()){
                    StocktakeGlobal.setPalletNo1(rs1.getString("palletno1"));
                    StocktakeGlobal.setPalletNo2(rs1.getString("palletno2"));
                  //  okMessage("Alert", "Rack is already Scanned - " +RowNo+'-'+CellNo, context);
                    return true;
                }

            } catch (SQLException e) {

                objGlobal.setErrorMessage(e.toString());
                return false;
            }


        }
        else if (warehouse.equals("JAFZAVNA")) {

            int p = rackno.lastIndexOf("-");
            RowNo = rackno.substring(0, p);

            CellNo = Integer.valueOf(rackno.substring(p + 1));
        }
        else if (warehouse.equals("JAFZAFLR") || warehouse.equals("JAFZA_DRIVEIN")) {
            RowNo = rackno;

            CellNo = getAutoFLoor(warehouse, rackno);
        }
        else if (warehouse.equals("Techno-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
            String aRackNumber[];
            aRackNumber = rackno.split("-");
            RowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            CellNo = Integer.valueOf(aRackNumber[4]);
        }

        else {
            String[] rackNumber1 = rackno.split("-");
            rackNumber1[1] = Integer.valueOf(rackNumber1[1]).toString();
            RowNo = rackNumber1[0];
            CellNo = Integer.parseInt(rackNumber1[1]);
        }
        try {


            String query1 = "select *  from racks..WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+RowNo+"' and cellno = '"+CellNo+"' and trndate >= DATEADD(day,-3,GETDATE())";
            ResultSet rs1 = dbConnection.getResultSet(query1, objGlobal.getConnection());
            if(rs1.next()){
              //  okMessage("Alert", "Rack is already Scanned - " +RowNo+'-'+CellNo, context);
                StocktakeGlobal.setPalletNo1(rs1.getString("palletno1"));
                StocktakeGlobal.setPalletNo2(rs1.getString("palletno2"));
                return true;
            }

        } catch (SQLException e) {
            objGlobal.setErrorMessage(e.toString());
            return false;

        }

        return false;

    }

    public Boolean RemoveRack(String warehouse, String rackno, Context context){
        String RowNo = "";
        int CellNo = 0;

        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }


        if(warehouse.equals("BINRACK") || warehouse.equals("YOTO-BINRACK")){
            RowNo = rackno;
            String query1 = "delete  from racks..WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+RowNo+"' and trndate >= DATEADD(day,-3,GETDATE())";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                return false;

            }

        }
        else if (warehouse.equals("JAFZAVNA")) {

            int p = rackno.lastIndexOf("-");
            RowNo = rackno.substring(0, p);

            CellNo = Integer.valueOf(rackno.substring(p + 1));
        }
        else if (warehouse.equals("JAFZAFLR") || warehouse.equals("JAFZA_DRIVEIN")) {
            RowNo = rackno;

            CellNo = getAutoFLoor(warehouse, rackno);
        }
        else if (warehouse.equals("Techno-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
            String aRackNumber[];
            aRackNumber = rackno.split("-");
            RowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            CellNo = Integer.valueOf(aRackNumber[4]);
        }

        else {
            String[] rackNumber1 = rackno.split("-");
            rackNumber1[1] = Integer.valueOf(rackNumber1[1]).toString();
            RowNo = rackNumber1[0];
            CellNo = Integer.parseInt(rackNumber1[1]);
        }


            String query1 = "delete  from racks..WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+RowNo+"' and cellno = '"+CellNo+"' and trndate >= DATEADD(day,-3,GETDATE())";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                return false;

            }


        return true;

    }









    public Integer getAutoFLoor(String warehouse, String rowno) {
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }

        ResultSet resultSet;
        Integer num = 0;
        String que = "select isnull(max(cellno),0) + 1 as num from racks.dbo.WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+rowno+"' and trndate >= DATEADD(day,-3,GETDATE())";
        resultSet = dbConnection.getResultSet(que, objGlobal.getConnection());
        try {
            if (resultSet.next()) {
                num = Integer.valueOf(resultSet.getString("num"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StocktakeGlobal.setCellNo(num);
        return num;
    }
//    public Integer getAutoDriveIn(String warehouse, String rowno) {
//        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
//            objGlobal.setErrorNo("transferReceipt:007");
//        }
//
//        ResultSet resultSet;
//        Integer num = 0;
//        String que = "select isnull(max(cellno),0) + 1 as num from racks.dbo.WarehouseRackDetStockTake where warehouse = '"+warehouse+"' and rowno = '"+rowno+"' and trndate = '"+objGlobal.getServerDate()+"'";
//        resultSet = dbConnection.getResultSet(que, objGlobal.getConnection());
//        try {
//            if (resultSet.next()) {
//                num = Integer.valueOf(resultSet.getString("num"));
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        StocktakeGlobal.setCellNo(num);
//        return num;
//    }


    public ArrayList<Integer> LoadCellno(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        try {
            String Query = "select distinct cellno from racks..WarehouseRackMaster where warehouse = 'JAFZA_DRIVEIN'";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());
            while (resultSet.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                arrayList.add(resultSet.getInt("cellno"));
            }
        }catch (Exception e){
            Log.e("ALERT", e.toString());
        }
        return arrayList;
    }

    public Boolean ClearRack(String warehouse, String rackno){
        try {
        String RowNo = "";
            String CellNo = "";
            if (!checkConnection()) {
                return false;
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
        if(warehouse.equals("BINRACK") || warehouse.equals("YOTO-BINRACK")){
            if(warehouse.equals("BINRACK")) {
                RowNo = rackno.substring(0, 1);
            }else {
                RowNo = rackno.substring(0, 4);
                Log.e("Rack ", RowNo);
            }
            CellNo = rackno.substring(1,3);
            String Query = "select distinct rack  from racks..ClearBinRackHead where (warehouse = 'BINRACK' or warehouse = 'YOTO-BINRACK') and rack = '"+RowNo+"' and  entryDate >= DATEADD(day,-3,GETDATE())";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());

            if (!resultSet.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return false;
            }
            else{
                return true;
            }

        }
        else if (warehouse.equals("JAFZA_DRIVEIN")){
            RowNo = rackno.substring(0, 1);
            CellNo = rackno.substring(1,2);
            //RowNo = rackno;
            String Query = "select distinct rack  from racks..ClearBinRackHead where (warehouse = 'JAFZA_DRIVEIN') and rack = '"+RowNo+"'  and  entryDate >= DATEADD(day,-3,GETDATE())";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());

            if (!resultSet.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return false;
            }
            else{
                return true;
            }
            }

        else if( (warehouse.equals("JAFZAFLR"))){
            RowNo = rackno;
          //  CellNo = Integer.valueOf(rackno.substring(1,3));
            String Query = "select *  from racks..RackClearingHead where (warehouse = 'JAFZA_DRIVEIN' or warehouse = 'JAFZAVNA' or warehouse = 'JAFZAFLR') and rack = '"+RowNo+"' and  entryDate >= DATEADD(day,-3,GETDATE())";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());

            if (!resultSet.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return false;
            }
            else{
                return true;
            }
        }else if (warehouse.equals("Techno-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
               String aRackNumber[];
                aRackNumber = rackno.split("-");
            RowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            CellNo = String.valueOf(Integer.valueOf(aRackNumber[4]));
            String Query = "select *  from racks..RackClearingHead where (warehouse = 'Techno-E' or warehouse = 'YOTO-SF') and rack = '"+RowNo+"' and  entryDate >= DATEADD(day,-3,GETDATE())";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());

            if (!resultSet.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return false;
            }
            else{
                return true;
            }
            }
        else{
            int p=rackno.lastIndexOf("-");
             RowNo = rackno.substring(0, p);
            String cell= Integer.valueOf(rackno.substring(p+1)).toString();
            String Query = "select *  from racks..RackClearingHead where warehouse = '"+warehouse+"' and rack = '"+RowNo+"' and  entryDate >= DATEADD(day,-3,GETDATE())";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
            if (!rs.next()) {
                // objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return false;
            }
            else{
                return true;
            }
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<RackHistoryModel> loadRackhistory(String warehouse){
        ArrayList<RackHistoryModel> listRackhistoryItem = new ArrayList<RackHistoryModel>();
        rs = dbConnection.getResultSet("select top 50 * from racks.dbo.WarehouseRackDetStockTake where warehouse='" + warehouse + "' and userid='" + objGlobal.getUserId() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
        try {
        while (rs.next()) {
            RackHistoryModel rackHistoryModel = new RackHistoryModel();
            String rackno = rs.getString("rowno") +"-"+rs.getString("cellno");
            rackHistoryModel.setRackNo(rackno);
            rackHistoryModel.setDirection("IN");
            rackHistoryModel.setPalletNo1(rs.getString("PalletNo1"));
            rackHistoryModel.setPalletNo2(rs.getString("PalletNo2"));
            rackHistoryModel.setTrnDate(rs.getString("TrnDate"));
            rackHistoryModel.setTrnTime(rs.getString("TrnTime"));
            rackHistoryModel.setRemarks("");
            rackHistoryModel.setWarehouse(rs.getString("warehouse"));
            listRackhistoryItem.add(rackHistoryModel);
        }
    } catch (Exception ex) {
        objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
        return listRackhistoryItem;
    }
        return listRackhistoryItem;

    }


}
