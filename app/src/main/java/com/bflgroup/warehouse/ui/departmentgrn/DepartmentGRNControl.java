package com.bflgroup.warehouse.ui.departmentgrn;



import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DepartmentGRNControl {


    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public DepartmentGRNControl() {
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

    public ArrayList<BoxItemList> loadBoxespallet(String Palletno, String WarehouseTo, String WarehouseFrom) {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String query = "select * from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "'";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (!rs.next()) {
                String query4 = "select distinct Boxno, toteid from usa..vUPCBoxDet where (palletno = '" + Palletno + "' or boxno = '" + Palletno  + "' or toteid = '"+ Palletno +"') and Closed = 'N'";
                rs = dbConnection.getResultSet(query4, objGlobal.getConnection());
                if (rs.next()) {
                    String query1 = "insert into bfldata.dbo.tmpwhdepartmentGRN(warehouseTo,WarehouseFrom,palletno,BoxNo,toteid,username, userid,deviceid, Date, updateTime) " +
                            "select Distinct '" + WarehouseTo + "','"+WarehouseFrom+"','"+Palletno+"',Boxno, Toteid,'" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "' from usa..vUPCBoxDet where (palletno = '" + Palletno + "' or boxno = '" + Palletno  + "' or toteid = '"+ Palletno +"') and Closed = 'N'";
                    if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                        return null;
                    }
                } else {
                    String query5 = "select distinct Boxno,TotId from BFLDATA..vR1Pallet where (palletno = '" + Palletno + "' or Boxno = '" + Palletno+ "' or TotId = '"+ Palletno +"') and Closed = 'N'";
                    rs = dbConnection.getResultSet(query5, objGlobal.getConnection());
                    if (rs.next()) {
                        String query1 = "insert into bfldata.dbo.tmpwhdepartmentGRN(warehouseTo,warehouseFrom,palletno,BoxNo,toteid,username, userid,deviceid, Date,updateTime) " +
                                "select Distinct '" + WarehouseTo + "','"+WarehouseFrom+"','"+Palletno+"',Boxno, TotId, '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "' from bfldata..vR1Pallet where (palletno = '" + Palletno + "' or Boxno = '" + Palletno+ "' or TotId = '"+ Palletno +"') and Closed = 'N'";
                        if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                            return null;
                        }
                    } else {
                        String query6 = "select distinct Boxno from USA..KNBboxes where (palletno = '" + Palletno + "'  or Boxno = '"+Palletno +"') and Closed = 'N'";
                        rs = dbConnection.getResultSet(query6, objGlobal.getConnection());
                        if (rs.next()) {
                            String query1 = "insert into bfldata.dbo.tmpwhdepartmentGRN(warehouseTo,warehouseFrom,palletno,BoxNo,username, userid,deviceid, Date,updateTime) " +
                                    "select Distinct '" + WarehouseTo + "','"+WarehouseFrom+"','"+Palletno+"',Boxno, '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "' from USA..KNBboxes where (palletno = '" + Palletno + "'  or Boxno = '"+Palletno +"') and Closed = 'N'";
                            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                                return null;
                            }
                        }else {
                            //BFLDATA.dbo.vGoodsIssue
                            String query7 = "select distinct trfno from BFLDATA.dbo.vGoodsIssue where palletno = '"+ Palletno +"'";
                            rs = dbConnection.getResultSet(query7, objGlobal.getConnection());
                            if (rs.next()){
                                String query1 = "insert into bfldata.dbo.tmpwhdepartmentGRN(warehouseTo,warehouseFrom,palletno,BoxNo,username, userid,deviceid, Date,updateTime) " +
                                        "select Distinct '" + WarehouseTo + "','"+WarehouseFrom+"','"+Palletno+"',trfno, '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "' from BFLDATA.dbo.vGoodsIssue where palletno = '" + Palletno + "'";
                                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                                    return null;
                                }
                            }


                        }
                    }
                }
            }
            int srno = 0;
            String query2 = "select Palletno, Boxno, Toteid, verified = isnull(verified,'') from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and (palletno = '" + Palletno + "' or boxno = '"+Palletno+"' or toteid = '"+Palletno+"') and warehousefrom ='" +WarehouseFrom+"' and warehouseTO = '"+ WarehouseTo + "'";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));
            }
            return boxItemLists;

        } catch (SQLException e) {
            throw new RuntimeException(e);// return null;
        }
        // return boxItemLists;
    }


    public ArrayList<BoxItemList> InsertBox(String Toteid, String WarehouseTo, String WarehouseFrom) {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String query = "select * from WHDepartmentGRNHead where  palletno = '" + Toteid + "' and warehouseFrom = '"+WarehouseFrom+"' and warehouseTo = '"+WarehouseTo+"'  and Convert(varchar,GETDATE(),103) = Date ";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (!rs.next()) {
            int Srno = GetGrnNum();
            String query1 = "insert into bfldata..WHDepartmentGRNHead(WarehouseFrom,WarehouseTo,Palletno,username,userid,Date,updateTime, Srno) " +
                    "values ('" + WarehouseFrom + "','" + WarehouseTo + "','" + Toteid + "','" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "'," + Srno + " )";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return null;
            }
            String query4 = "select distinct Boxno, toteid from usa..vUPCBoxDet where (boxno = '" + Toteid + "' or toteid = '" + Toteid + "') and Closed = 'N'";
            rs = dbConnection.getResultSet(query4, objGlobal.getConnection());
            if (rs.next()) {
                String query2 = "insert into bfldata..WHDepartmentGRN(srno, Palletno,BoxNo,Toteid,username,userid) " +
                        "select distinct " + Srno + ", palletno,BoxNo,toteid,'" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "' from usa..vUPCBoxDet where (boxno = '" + Toteid + "' or toteid = '" + Toteid + "') and Closed = 'N' ";
                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return null;
                }
            }
            else {
                String query5 = "select distinct Boxno,TotId from BFLDATA..vR1Pallet where (Boxno = '" + Toteid+ "' or TotId = '"+ Toteid +"') and Closed = 'N'";
                rs = dbConnection.getResultSet(query5, objGlobal.getConnection());
                if (rs.next()) {
                    String query2 = "insert into bfldata..WHDepartmentGRN(srno, Palletno,BoxNo,Toteid,username,userid) " +
                            "select Distinct " + Srno + ", palletno,BoxNo,totid,'" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "' from bfldata..vR1Pallet where ( Boxno = '" + Toteid+ "' or TotId = '"+ Toteid +"') and Closed = 'N'";
                    if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                        return null;
                    }
                } else {
                    String query6 = "select distinct Boxno from USA..KNBboxes where ( Boxno = '"+Toteid +"') and Closed = 'N'";
                    rs = dbConnection.getResultSet(query6, objGlobal.getConnection());
                    if (rs.next()) {
                        String query2 = "insert into bfldata..WHDepartmentGRN(srno, Palletno,BoxNo,Toteid,username,userid) " +
                                "select Distinct " + Srno + ", '',BoxNo,'','" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "' from USA..KNBboxes where ( Boxno = '"+Toteid +"') and Closed = 'N'";
                        if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                            return null;
                        }
                    }else{
                        String query7 = "select * from bfldata..TransferNoReturn where TrfNo='" + Toteid + "'";
                        rs = dbConnection.getResultSet(query7, objGlobal.getConnection());
                        if (rs.next()) {
                            String query2 = "insert into bfldata..WHDepartmentGRN(srno, Palletno,BoxNo,Toteid,username,userid) " +
                                    "select Distinct " + Srno + ", '',TrfNo,'','" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "' from bfldata..TransferNoReturn where trfno = '"+ Toteid +"'";
                            if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                                return null;
                            }
                        }
                    }
                }
            }
        }
            int srno = 0;
            String query2 = "select top 25 Palletno, Boxno, Toteid, verified = 'Y' from BFLDATA..WHDepartmentGRN where username ='"+objGlobal.getUserName()+"' order by srno desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, rs.getString("Palletno"), rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));
            }
            return boxItemLists;


        } catch (SQLException e) {
            throw new RuntimeException(e);// return null;
        }
        // return boxItemLists;
    }


    public ArrayList<BoxItemList> loadTotehistory() {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }

            int srno = 0;
            String query2 = "select top 25 Palletno, Boxno, Toteid, verified = 'Y' from BFLDATA..WHDepartmentGRN where username ='"+objGlobal.getUserName()+"' order by srno desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, rs.getString("Palletno"), rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));
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
            String query2 = "select Count(*) from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and (palletno = '" + palletno + "' or boxno = '"+palletno+"' or toteid = '"+palletno+"') and warehouseTo = '" + warehouse + "' and isnull(verified,'')<>''";
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
            String query1 = "delete from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "'";
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
        String Query = "select boxno,toteid from usa.dbo.vupcboxDet where (BoxNo='" + Boxno.trim() + "'  or toteid = '" + Boxno.trim() + "' )  and Closed='N'";
        rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
        if (rs.next()) {
            box = rs.getString("boxno");
            toteid = rs.getString("toteid");
            DepartmentGRNGlobal.setBoxNo(box);
            DepartmentGRNGlobal.setToteid(toteid);
        } else {
            ResultSet rs1 = dbConnection.getResultSet("select distinct a.boxno, TotiD from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                    "and (b.BoxNo='" + Boxno + "' or TotiD = '" + Boxno + "' )   and a.Closed='N'", objGlobal.getConnection());
            if (rs1.next()) {
                box = rs1.getString("boxno");
                toteid = rs.getString("TotiD");
                DepartmentGRNGlobal.setBoxNo(box);
                DepartmentGRNGlobal.setToteid(toteid);

            } else {
                ResultSet rs2 = dbConnection.getResultSet("select * from usa..knbboxes where BoxNo = '" + Boxno + "' and closed = 'N'", objGlobal.getConnection());
                if (rs2.next()) {
                    box = rs2.getString("boxno");
                    DepartmentGRNGlobal.setBoxNo(box);
                    DepartmentGRNGlobal.setToteid("");
                } else {
                    ResultSet rs3 = dbConnection.getResultSet("select * from bfldata..vGoodsissue where trfno = '" + Boxno + "' and palletno = '"+ Palletno +"'", objGlobal.getConnection());
                    if (rs3.next()) {
                        box = rs3.getString("trfno");
                        DepartmentGRNGlobal.setBoxNo(box);
                        DepartmentGRNGlobal.setToteid("");
                    } else {
                        box = "";
                    }
                }
            }
        }
        String query = "select * from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and (BoxNo = '" + box + "') and warehouseTo = '" + Warehouse + "'";
        ResultSet rs2 = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (rs2.next()) {
            return true;
        }
        return false;
    }

    ArrayList<BoxItemList> UpdateLoadBoxes(String Boxno, String Palletno, String Warehouse) {
        int srno = 0;
        String box = "";
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String query1 = "update bfldata.dbo.tmpwhdepartmentGRN set Verified = 'Y', updateTime= '" + objGlobal.getServerTime() + "' where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "'  and (BoxNo = '" + Boxno + "' or Toteid = '" + Boxno + "') and warehouseTo = '" + Warehouse + "'";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                //  return false;
            }
            String query2 = "select Palletno, Boxno, Toteid, verified = isnull(verified,'') from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));
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

            String query1 = "insert into bfldata.dbo.tmpwhdepartmentGRN(warehouseTo,palletno,BoxNo,toteid,username, userid,deviceid, Date,updateTime) values( '" + Warehouse + "','" + Palletno + "','" + DepartmentGRNGlobal.getBoxNo() + "','" + DepartmentGRNGlobal.getToteid() + "', '" + objGlobal.getUserName() + "','" + objGlobal.getUserId() + "','" + objGlobal.getDeviceName() + "','" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "')";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                //objGlobal.getConnection().rollback();
                //return false;
            }
//            }

            String query2 = "select Palletno, Boxno, Toteid, verified = isnull(verified,'') from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));
            }
            return boxItemLists;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // return true;

    }


    public boolean isPalletSaved(String palletno, String warehouseTo, String warehouseFrom) throws SQLException {
        ResultSet rs1 = dbConnection.getResultSet("select * from bfldata..WHDepartmentGRNHead where Palletno = '" + palletno + "' and warehouseTo = '"+warehouseTo+"' and warehouseFrom = '"+warehouseFrom+"' and Convert(varchar,GETDATE(),103) = Date", objGlobal.getConnection());
        if(rs1.next())
        {
            return true;
        }
        return false;
    }

    public ArrayList<BoxItemList> LoadPalletDetails(String palletno) throws SQLException {
        ArrayList<BoxItemList> boxItemLists = new ArrayList<>();
        int srno = 0;
        String query2 = "select * from bfldata..WHDepartmentGRN where Palletno = '" + palletno + "'";
        rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
        while (rs.next()) {
            srno++;
            boxItemLists.add(new BoxItemList(srno, palletno, rs.getString("BoxNo"),rs.getString("Toteid"),"Y"));
        }
        return boxItemLists;
    }

    public Boolean isValidTransfer(String Trfno, Context context){
        try{
            ResultSet rs = dbConnection.getResultSet("select * from bfldata..TransferNoReturn where TrfNo='" + Trfno + "' ", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Transfer Number " + Trfno + " is Invalid");
                return false;
            }
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }
        return true;
    }
    public Boolean isValidPallet(String palletno, Context context){
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
                               // String Query = "select * from usa.dbo.vupcboxDet where (BoxNo='" + palletno.trim() + "'  or toteid = '"+palletno.trim()+"' ) and Closed='N'";
                                rs = dbConnection.getResultSet("select * from usa.dbo.vupcboxDet where (BoxNo='" + palletno + "'  or toteid = '"+palletno+"' ) and Closed='N'", objGlobal.getConnection());
                                if (!rs.next()) {
                                    rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                                            "and (b.BoxNo='" + palletno + "' or TotiD = '"+palletno+"' )and a.Closed='N'", objGlobal.getConnection());
                                    if (!rs.next()) {
                                        rs = dbConnection.getResultSet("select * from usa..knbboxes where BoxNo = '"+palletno+"' and closed = 'N'", objGlobal.getConnection());
                                        if (!rs.next()) {


                                            objGlobal.setErrorMessage("Invalid Pallet/Box number or Pallet/Box is closed already - " +palletno);
                                            return false;
                                        }
                                    }
                                }

                               // objGlobal.setErrorMessage("Pallet Number " + palletno + " is closed already");

                               // return false;
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

    public boolean isVerified(String Palletno, String WarehouseTo, String WarehouseFrom) {
        try {
            String query2 = "select Palletno, Boxno, Toteid, verified = isnull(verified,'') from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and warehouseTo = '"+WarehouseTo+"' and warehouseFrom = '"+WarehouseFrom+"' and isnull(verified,'')='' ";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            if (rs.next()) {
                return false;
            }
        }
        catch(Exception exeception){
            return false;
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

            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:validateToteid:" + ex.toString());
            return false;
        }

    }

    public Boolean isValidbox(String BoxNo, String PalletNo, Context context){
        try {
            String Query = "select * from usa.dbo.vupcboxDet where (BoxNo='" + BoxNo.trim() + "'  or toteid = '"+BoxNo.trim()+"' ) and Closed='N'";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                        "and (b.BoxNo='" + BoxNo + "' or TotiD = '"+BoxNo+"' )and a.Closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa..knbboxes where BoxNo = '"+BoxNo+"' and closed = 'N'", objGlobal.getConnection());

                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata..vGoodsIssue where trfno = '"+BoxNo+"' and palletno = '"+PalletNo+"'", objGlobal.getConnection());
                        if (!rs.next()) {
                            objGlobal.setErrorMessage("Invalid box or box is closed");
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


            String query2 = "select Palletno, Boxno, Toteid, verified = isnull(verified,'') from bfldata.dbo.tmpwhdepartmentGRN where DeviceId = '" + objGlobal.getDeviceName() + "' and palletno = '" + Palletno + "' and warehouseTo = '"+Warehouse+"' order by Date,updatetime desc";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {
                srno++;
                boxItemLists.add(new BoxItemList(srno, Palletno, rs.getString("BoxNo"),rs.getString("Toteid"), rs.getString("verified")));

            }
        }catch (Exception e){
            Log.e("Alert",e.getMessage());

        }

        return boxItemLists;

    }


    public int GetGrnNum(){
        int num = 0;
        try {
        String query2 = "select srno = max(isnull(srno,0)) + 1 from bfldata..WHDepartmentGRNHead";
        rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
        while (rs.next()) {

           num =  rs.getInt("srno");

        }
        } catch (Exception e) {
            throw new RuntimeException(e);   // return false;
        }
        return num;
    }

    public Boolean SavePalletDetails( String palletNo, String WarehouseTo, String WarehouseFrom){
        try {

            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            int Srno = GetGrnNum();
            String query = "insert into bfldata..WHDepartmentGRNHead(WarehouseFrom,WarehouseTo,Palletno,username,userid,Date,updateTime, Srno) " +
                    "select distinct '"+WarehouseFrom+"','"+WarehouseTo+"',Palletno,username,userid,'"+ objGlobal.getServerDate()  +"', '"+ objGlobal.getServerTime() +"',"+Srno+" from bfldata.dbo.tmpwhdepartmentGRN where palletno = '" + palletNo + "' and  deviceId = '" + objGlobal.getDeviceName() + "' ";
            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }

            String query1 = "insert into bfldata..WHDepartmentGRN(srno, Palletno,BoxNo,Toteid,username,userid) " +
                    "select "+Srno+", Palletno,BoxNo,Toteid,username,userid from bfldata.dbo.tmpwhdepartmentGRN a where palletno = '" + palletNo + "'  and deviceId = '" + objGlobal.getDeviceName() + "' ";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);   // return false;
        }
        return true;
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
            }else if(palletNo.substring(0,3).equals("KNB")){
                String query = "select count = count(distinct Boxno) from usa..knbboxes where palletno = '" + palletNo + "' and closed = 'N'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }else{
                String query = "select count = count(distinct trfno) from bfldata..vGoodsissue where palletno = '" + palletNo + "'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }
            DepartmentGRNGlobal.setPalletCount(Integer.valueOf(Boxcount));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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


    public List<String> getWarehouse(String warehouse){
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();

            arr.add("Select Warehouse");
            rs = dbConnection.getResultSet("select distinct department from bfldata..warehouseDepartment where warehouse = '"+warehouse+"'", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("department"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

}
