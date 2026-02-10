package com.bflgroup.warehouse.ui.boxreconcilation;



import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class PalletBoxCountControl {


    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public PalletBoxCountControl() {
        objGlobal.setDbName("bfldata");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("BinBatchInControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("bfldata");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BinBatchInControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public ArrayList<BoxItemList> loadBoxespallet(String Palletno, String Warehouse) {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String query = "select BoxOriginal,isnull(toteid,''),BoxScanned from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "'";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs.next()) {
            } else {
                String query4 = "select distinct Boxno from usa..vUPCBoxDet where palletno = '" + Palletno + "' and Closed = 'N'";
                rs = dbConnection.getResultSet(query4, objGlobal.getConnection());
                if (rs.next()) {
                    String query1 = "insert into tmpScannedBoxes(warehouse,palletno,BoxOriginal,BoxScanned,username, userid,deviceid, Date, toteid) " +
                            "select Distinct '" + Warehouse + "','" + Palletno + "',Boxno,'', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "', toteid from usa..vUPCBoxDet where palletno = '" + Palletno + "' and Closed = 'N'";
                    if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {

                    }
                } else {
                    String query5 = "select distinct Boxno from vR1Pallet where palletno = '" + Palletno + "' and Closed = 'N'";
                    rs = dbConnection.getResultSet(query5, objGlobal.getConnection());
                    if (rs.next()) {
                        String query1 = "insert into tmpScannedBoxes(warehouse,palletno,BoxOriginal,BoxScanned,username, userid,deviceid, Date,toteid) " +
                                "select Distinct '" + Warehouse + "','" + Palletno + "',Boxno,'', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "',totid from bfldata..vR1Pallet where palletno = '" + Palletno + "' and Closed = 'N'";
                        if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {

                        }
                    } else {
                        String query6 = "select distinct Boxno from USA..KNBboxes where palletno = '" + Palletno + "' and Closed = 'N'";
                        rs = dbConnection.getResultSet(query6, objGlobal.getConnection());
                        if (rs.next()) {
                            String query1 = "insert into tmpScannedBoxes(warehouse,palletno,BoxOriginal,BoxScanned,username, userid,deviceid, Date,toteid) " +
                                    "select Distinct '" + Warehouse + "','" + Palletno + "',Boxno,'', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','' from USA..KNBboxes where palletno = '" + Palletno + "' and Closed = 'N'";
                            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {

                            }
                        }
                        else {
                            String query7 = "select distinct trfno from bfldata..vGoodsIssue where palletno = '" + Palletno + "'";
                            rs = dbConnection.getResultSet(query7, objGlobal.getConnection());
                            if (rs.next()) {
                                String query1 = "insert into tmpScannedBoxes(warehouse,palletno,BoxOriginal,BoxScanned,username, userid,deviceid, Date,toteid) " +
                                        "select Distinct '" + Warehouse + "','" + Palletno + "',trfno,'', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','' from bfldata..vGoodsIssue where palletno = '" + Palletno + "'";
                                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {

                                }
                            }
                        }
                    }

                }
            }
            int srno = 0;
            String query2 = "select BoxOriginal,toteid=isnull(toteid,''),BoxScanned from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "'";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxOriginal"),rs.getString("toteid"), rs.getString("BoxScanned")));
            }
            return boxItemLists;

        } catch (SQLException e) {
            throw new RuntimeException(e);// return null;
        }
        // return boxItemLists;
    }

    public int getCountBoxesScanned(String palletno, String warehouse) {
        int Count = 0;
        try {
            String query2 = "select Count(*) from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + palletno + "' and warehouse = '" + warehouse + "' and isnull(BoxScanned,'')<>''";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                Count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //PalletBoxCountShared.savePalletCount(Count);
        return Count;

    }


    public Boolean deletetmp() {
        // int Count = 0;
        try {
            String query1 = "delete from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "'";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {

                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;

    }


    public Boolean BoxesInPallets(String Boxno, String Palletno, String Warehouse) throws SQLException {

        String box = "";
        String toteid = "";
        String Query = "select distinct boxno,toteid from usa.dbo.vupcboxDet with(NOLOCK) where (BoxNo='" + Boxno.trim() + "'  or toteid = '" + Boxno.trim() + "' )  and Closed='N'";
        rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
        if (rs.next()) {
            box = rs.getString("boxno");
            toteid = rs.getString("toteid");
            PalletBoxCountGlobal.setBoxNo(box);
            PalletBoxCountGlobal.setToteId(toteid);
        } else {
            ResultSet rs1 = dbConnection.getResultSet("select distinct a.boxno, TotiD from bfldata.dbo.TCMBoxes a with(NOLOCK),bfldata.dbo.TcmboxesHeader b with(NOLOCK) where a.BoxNo=b.Boxno " +
                    "and (b.BoxNo='" + Boxno + "' or TotiD = '" + Boxno + "' )   and a.Closed='N'", objGlobal.getConnection());
            if (rs1.next()) {
                box = rs1.getString("boxno");
                toteid = rs1.getString("TotiD");
                PalletBoxCountGlobal.setBoxNo(box);
                PalletBoxCountGlobal.setToteId(toteid);

            } else {
                ResultSet rs2 = dbConnection.getResultSet("select distinct boxno from usa..knbboxes with(NOLOCK) where BoxNo = '" + Boxno + "' and closed = 'N'", objGlobal.getConnection());
                if (rs2.next()) {
                    box = rs2.getString("boxno");
                    PalletBoxCountGlobal.setBoxNo(box);
                    PalletBoxCountGlobal.setToteId(toteid);

                } else {
                    ResultSet rs3 = dbConnection.getResultSet("select * from bfldata..vGoodsIssue with(NOLOCK) where trfno = '" + Boxno + "' and palletno =  '"+Palletno+"' ", objGlobal.getConnection());
                    if (rs3.next()) {
                        box = rs3.getString("trfno");
                        PalletBoxCountGlobal.setBoxNo(box);
                        PalletBoxCountGlobal.setToteId(toteid);
                    }else {
                        box = "";
                    }
                }
            }
        }
        String query = "select * from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and (BoxOriginal = '" + box + "' or BoxScanned = '" + box + "') and warehouse = '" + Warehouse + "'";
        ResultSet rs2 = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (rs2.next()) {
            return true;
        }

        return false;
    }

    ArrayList<BoxItemList> UpdateLoadBoxes(String Boxno, String Palletno, String Warehouse, Context context) {
        int srno = 0;
        String box = "";
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String query1 = "select * from tmpScannedBoxes where BoxScanned = '" + PalletBoxCountGlobal.getBoxNo() + "' and DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and BoxOriginal = '" + PalletBoxCountGlobal.getBoxNo() + "' and warehouse = '" + Warehouse + "'";
            rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
            if (rs.next()) {
                okMessage("Alert", "Box already scanned - " + Boxno, context);
                //  return false;
            } else {
                String query3 = "update tmpScannedBoxes set BoxScanned = '" + PalletBoxCountGlobal.getBoxNo() + "', updateTime= '" + objGlobal.getServerTime() + "' where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and BoxOriginal = '" + PalletBoxCountGlobal.getBoxNo() + "' and warehouse = '" + Warehouse + "'";
                if (!dbConnection.insertUpdate(query3, objGlobal.getConnection())) {
                    //  return false;
                }
            }
            String query2 = "select BoxOriginal,toteid=isnull(toteid,''),BoxScanned from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxOriginal"), rs.getString("toteid"), rs.getString("BoxScanned")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return boxItemLists;
    }

    public ArrayList<BoxItemList> InsertloadBoxes(String Boxno, String Palletno, String Warehouse) {
        int srno = 0;
        String box = "";
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }

            String query1 = "insert into tmpScannedBoxes(warehouse,palletno,BoxOriginal,BoxScanned,username, userid,deviceid, Date,updateTime, toteid) values( '" + Warehouse + "','" + Palletno + "','','" + PalletBoxCountGlobal.getBoxNo() + "', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "', '" + PalletBoxCountGlobal.getToteId() + "')";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                //return false;
            }
//            }

            String query2 = "select BoxOriginal,toteid=isnull(toteid,''),BoxScanned from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxOriginal"), rs.getString("toteid"), rs.getString("BoxScanned")));
            }
            return boxItemLists;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // return true;

    }


    public boolean isPalletSaved(String palletno, String warehouse) throws SQLException {
        ResultSet rs1 = dbConnection.getResultSet("select * from bfldata..WarehouseScannedBoxes with(NOLOCK) where Palletno = '" + palletno + "' and warehouse = '"+warehouse+"'", objGlobal.getConnection());
        if(rs1.next())
        {
            return true;
        }
        return false;
    }

    public ArrayList<BoxItemList> LoadPalletDetails(String palletno, String warehouse) throws SQLException {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        int srno = 0;
        String query2 = "select * from bfldata..WarehouseScannedBoxes with(NOLOCK) where Palletno = '" + palletno + "' and warehouse = '"+warehouse+"'";
        rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
        while (rs.next()) {
            srno++;
            boxItemLists.add(new BoxItemList(srno, palletno, rs.getString("BoxOriginal"),rs.getString("toteid"), rs.getString("BoxScanned")));
        }
        return boxItemLists;
    }

    public Boolean isValidPallet(String palletno, Context context){
        try {

            ResultSet rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead with(NOLOCK) where palletno='" + palletno + "' and closed='N'", objGlobal.getConnection());

            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets with(NOLOCK) where palletno='" + palletno + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets with(NOLOCK) where palletno='" + palletno + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead with(NOLOCK) where palletno='" + palletno + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall with(NOLOCK)  where palletno='" + palletno + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                objGlobal.setErrorMessage("Pallet Number " + palletno + " is closed already");
                                okMessage("Alert", "Pallet Number " + palletno + " is closed already", context);
                                return false;
                            }
                        }
                    }
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public Boolean isValidTote(String toteId, Context context){
        try {
            String Query = "select * from usa.dbo.upcboxhead where ToteID='" + toteId.trim() + "' and Closed='N'";
            ResultSet resultSet = dbConnection.getResultSet(Query, objGlobal.getConnection());

            if (resultSet.next()) {// objGlobal.setErrorMessage(objGlobal.getErrorMessage().toString() );
                return true;
            }
            else {
                rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                        "and b.TotId='" + toteId + "' and a.Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                } else {
                    objGlobal.setErrorMessage("Invalid box or box is closed");
                    return false;
                }
            }

//            rs = dbConnection.getResultSet("select * from racks..WarehouseRackDetStockTake where Warehouse='BINRACK' and Toteid='" + toteId + "'", objGlobal.getConnection());
//            if (rs.next()) {
//                objGlobal.setErrorMessage("ToteID found in location, " + rs.getString("location").toString());
//                return false;
//            }

            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateToteid:" + ex.toString());
            return false;
        }

    }

    public Boolean isValidbox(String BoxNo, Context context){
        try {
            String Query = "select * from usa.dbo.vupcboxDet with(NOLOCK) where (BoxNo='" + BoxNo.trim() + "'  or toteid = '"+BoxNo.trim()+"' ) and Closed='N'";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a with(NOLOCK),bfldata.dbo.TcmboxesHeader b with(NOLOCK) where a.BoxNo=b.Boxno " +
                        "and (b.BoxNo='" + BoxNo + "' or TotiD = '"+BoxNo+"' )and a.Closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa..knbboxes with(NOLOCK) where BoxNo = '"+BoxNo+"' and closed = 'N'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from BFLDATA..vGoodsIssue with(NOLOCK)  where trfno = '"+BoxNo.trim()+"' ", objGlobal.getConnection());
                        if (!rs.next()) {
                            objGlobal.setErrorMessage("Invalid box or box is closed - " + BoxNo);
                            return false;
                        }
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateToteid:" + ex.toString());
            return false;
        }
    }


    public ArrayList<BoxItemList> loadPalletDetails(String Palletno, String Warehouse){
        int srno = 0;
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            String query2 = "select BoxOriginal,toteid=isnull(toteid,''),BoxScanned from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and warehouse = '"+Warehouse+"' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxOriginal"),rs.getString("toteid"), rs.getString("BoxScanned")));

            }
        }catch (Exception e){
            Log.e("Alert",e.getMessage());

        }

        return boxItemLists;

    }
    public Boolean SavePalletDetails( String palletNo, String Warehouse) {
        try {
            ResultSet rs3 = dbConnection.getResultSet("select cnt=count(*) from bfldata..tmpScannedBoxes where Palletno='" + palletNo + "' and deviceId='" + objGlobal.getDeviceName() + "' and BoxScanned=''", objGlobal.getConnection());
            if (rs3.next()) {
                if (rs3.getInt("cnt") > 0) {
                    objGlobal.setErrorMessage("(" + rs3.getString("cnt") + ") Boxes not verified, please double check");
                    return false;
                }
            }
            if (isPalletSaved(palletNo, Warehouse)) {
                String query = "Delete from bfldata..WarehouseScannedBoxes where palletno = '" + palletNo + "' and warehouse = '" + Warehouse + "'";
                if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            String query = "insert into bfldata..WarehouseScannedBoxes(Warehouse,Palletno,BoxOriginal,BoxScanned,username,userid,Date,toteid) " +
                    "select Warehouse,Palletno,BoxOriginal,BoxScanned,username,userid,Date,toteid from bfldata..tmpScannedBoxes where palletno = '" + palletNo + "' and  deviceId = '" + objGlobal.getDeviceName() + "' and Warehouse = '" + Warehouse + "'";
            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);   // return false;
        }
        return true;
    }


//    public String BoxPalletCount(String palletNo){
//        String Boxcount = "0";
//        try {
//            if(palletNo.substring(0,3).equals("USA") || palletNo.substring(0,3).equals("FAC") ) {
//                String query = "select count = count(distinct Boxno) from USA..vUPCBOXdet where palletno = '" + palletNo + "' and closed = 'N'";
//                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
//                if (rs.next()) {
//                    Boxcount = rs.getString("count");
//                }
//            }else if(palletNo.substring(0,3).equals("PLT")){
//                String query = "select count = count(distinct Boxno) from BFLDATA..vR1Pallet where palletno = '" + palletNo + "' and closed = 'N'";
//                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
//                if (rs.next()) {
//                    Boxcount = rs.getString("count");
//                }
//            }
//            else if(palletNo.substring(0,3).equals("KNB") || palletNo.substring(0,5).equals("AEINT") || palletNo.substring(0,5).equals("AELOC")){
//                String query = "select count = count(distinct Boxno) from usa..knbboxes where palletno = '" + palletNo + "' and closed = 'N'";
//                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
//                if (rs.next()) {
//                    Boxcount = rs.getString("count");
//                }
//            }else{
//                String query = "select count = count(distinct trfno) from Bfldata..vGoodsIssue where palletno = '" + palletNo + "'";
//                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
//                if (rs.next()) {
//                    Boxcount = rs.getString("count");
//                }
//            }
//            PalletBoxCountGlobal.setPalletCount(Integer.valueOf(Boxcount));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return Boxcount;
//    }


//    public String BoxPalletCount(String palletNo) {
//        String Boxcount = "0";
//        try {
//            String Query = "select count = count(distinct Boxno) from USA..vUPCBOXdet where palletno = '" + palletNo + "' and closed = 'N'";
//            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
//            if (rs.next()) {
//                Boxcount = rs.getString("count");
//            } else {
//                ResultSet rs1 = dbConnection.getResultSet("select count = count(distinct Boxno) from BFLDATA..vR1Pallet where palletno = '" + palletNo + "' and closed = 'N'", objGlobal.getConnection());
//                if (rs1.next()) {
//                    Boxcount = rs.getString("count");
//
//                } else {
//                    ResultSet rs2 = dbConnection.getResultSet("select count = count(distinct Boxno) from usa..knbboxes  where palletno = '" + palletNo + "' and closed = 'N'", objGlobal.getConnection());
//                    if (rs2.next()) {
//                        Boxcount = rs.getString("count");
//
//                    } else {
//                        ResultSet rs3 = dbConnection.getResultSet("select count = count(distinct trfno) from BFLDATA..vGoodsIssue where palletno = '" + palletNo + "'", objGlobal.getConnection());
//                        if (rs3.next()) {
//                            Boxcount = rs.getString("count");
//                        } else {
//                            Boxcount = "";
//                        }
//                    }
//                }
//            }
//            PalletBoxCountGlobal.setPalletCount(Integer.valueOf(Boxcount));
//        } catch (SQLException e) {
////            throw new RuntimeException(e);
////        }
//
//        }
//        return Boxcount;
//    }

    public String BoxPalletCount(String palletno, String warehouse) {
        String Boxcount = "0";
        //int Count = 0;
        try {
            String query2 = "select count = Count(distinct boxOriginal) from tmpScannedBoxes where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + palletno + "' and warehouse = '" + warehouse + "' and isnull(boxOriginal,'')<>''";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                //Count = rs.getInt(1);
                Boxcount = rs.getString("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PalletBoxCountGlobal.setPalletCount(Integer.valueOf(Boxcount));
        //PalletBoxCountShared.savePalletCount(Count);
        return Boxcount;

    }

    private void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public List<String> loadWarehouse(){

        List<String> arr;
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select distinct WareHouse from racks..WarehouseRackMaster", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("WareHouse"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }

    }


}
