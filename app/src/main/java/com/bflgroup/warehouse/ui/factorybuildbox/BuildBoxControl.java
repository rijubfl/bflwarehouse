package com.bflgroup.warehouse.ui.factorybuildbox;

import android.content.Context;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BuildBoxControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BuildBoxGlobal objBuildBoxGlobal = BuildBoxGlobal.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    BuildBoxShared ObjBuildBoxShared;




    public BuildBoxControl(Context context) {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("BuildBoxControl : Connection error");
        }
        ObjBuildBoxShared = new BuildBoxShared(context);

    }


    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BuildBoxControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean checkitemcode(String itemcode, Context context){
        try {
            if (!checkConnection()) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from BFLDATA..factorygenerateBarcode where generatedBarcode = '"+itemcode.trim()+"'", objGlobal.getConnection());
            if (rs.next()){
                ResultSet rs1 = dbConnection.getResultSet("select * from BFLDATA..tmpBuildBox where generatedBarcode = '"+itemcode.trim()+"' and deviceId = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection());
                    if(!rs1.next()){
                        return true;
                    }else{
                        okMessage("Alert","This item - " +itemcode + " is already scanned",context);
                        return false;

                    }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        okMessage("Alert", "No Barcode found - " + itemcode, context);
        return false;
    }

    public boolean checkTote(String toteid, Context context, String status){
        try {
            if (!checkConnection()) {
                return false;
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            rs = dbConnection.getResultSet("select * from bfldata..BlueToteIDMaster where ToteID = '"+toteid.trim()+"'", objGlobal.getConnection());
            if (rs.next()){
                ResultSet rs1 = dbConnection.getResultSet("select * from usa..upcBoxHead where ToteID ='"+toteid.trim()+"' and closed = 'N'", objGlobal.getConnection());
                if(rs1.next()){

                    okMessage("Alert","This item - " +toteid + " is not closed. Box no is - "+rs1.getString("Boxno"),context);
                    return false;
                }else{
                    ResultSet res = dbConnection.getResultSet("select * from bfldata..tmpbuildbox where ToteID ='"+toteid.trim()+"' and deviceId = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection());
                    {
                        if(!res.next()){
                            String query = "Insert into bfldata..tmpBuildBox(Date,time,userid,deviceId, toteid, status) " +
                                    "values('"+objGlobal.getServerDate()+"', '"+objGlobal.getServerTime()+"', '"+objGlobal.getUserName()+"', '"+objGlobal.getDeviceName()+"', '"+toteid.trim()+"', '"+status+"')";
                            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                                Log.e("Error Query", query);
                            }
                            return true;
                        }else{
                            okMessage("Alert","Duplicate Totes not allowed - " +toteid ,context);
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        okMessage("Alert","This item - " +toteid + " is not valid",context);
        return false;
    }
    public boolean deleteitemcode(String GeneratedBarcode){
        try {
            if (!checkConnection()) {
                return false;
            }
            String query="delete from BFLDATA..tmpBuildBox where generatedBarcode = '"+GeneratedBarcode+"' and deviceID = '"+objGlobal.getDeviceName()+"'";

            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                Log.e("Error Query", query);
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    void okMessage(String title, String message, Context context) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
    public ArrayList<BuildBoxitem> insertitemCode(String itemcode, String status, String toteid, Context context){
        ArrayList<BuildBoxitem> arr = new ArrayList<BuildBoxitem>();

        try {
            String query ="";
            if (!checkConnection()) {
            }

            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }
            String OldItemcode = "";
            String Query = "select top 1 itemcode from BFLDATA..factorygenerateBarcode b where generatedBarcode = '"+itemcode+"' ";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
            if (rs.next()){
                OldItemcode = rs.getString("itemcode");
            }
            String Query2 = "select * from bfldata..factorybarcodequalitycheckdet where generatedBarcode = '"+itemcode+"'";
            rs = dbConnection.getResultSet(Query2, objGlobal.getConnection());
            if (rs.next()){
               okMessage("Alert", "Pallet is already build for this upc - " +itemcode,context);
            }else {

                query = "Insert into bfldata..tmpBuildBox(Date,time,generatedBarcode,userid,deviceId,status, itemcode, toteid) " +
                        "values('" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "', '" + itemcode + "', '" + objGlobal.getUserName() + "', '" + objGlobal.getDeviceName() + "','" + status + "','" + OldItemcode + "', '" + toteid + "')";
                if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                    Log.e("Error Query", query);
                }
                if (status.equals("Good")) {
                    GoodsCount();
                } else if (status.equals("Asis")) {
                    AsisCount();
                } else {
                    WriteOffCount();
                }
            }

            String Query1 = "select * from BFLDATA..tmpBuildBox a where deviceID  = '"+objGlobal.getDeviceName()+"' and itemcode <> '' order by a.Date, a.time desc";
            rs = dbConnection.getResultSet(Query1, objGlobal.getConnection());
            while (rs.next()){
                arr.add(new BuildBoxitem(rs.getString("itemcode"),rs.getString("generatedBarcode"),rs.getString("status")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return arr;
    }

    public boolean ClearAll(Context context){

       String query = "Delete from bfldata..tmpBuildBox where userid = '"+objGlobal.getUserName()+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {

            okMessage("Alert", objGlobal.getErrorMessage().toString(), context);
            return false;
        }

        return true;
    }
    private boolean getBoxNumber() {
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                return false;
            }
            int autoSn = 0;
            String suff = "";
            Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(objGlobal.getServerDate());
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(dt);
            String yr = String.valueOf(year.substring(2, 4));
            suff = "U" + yr + "/";
            rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from usa.dbo.UPCBoxHead where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            objBuildBoxGlobal.setBoxNo(suff + String.format("%06d", autoSn));
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("objBlueToteEuroBoxGlobal:getBoxNumber:" + ex.toString());
            return false;
        }
    }

    private boolean getSn() {
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                return false;
            }
            int autoSn = 0;
            String suff = "";

            rs = dbConnection.getResultSet("select sn=max(Sn)+1 from bfldata.dbo.factorybarcodequalitycheck", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("sn").toString());
            }
            objBuildBoxGlobal.setSn(String.format(String.valueOf(autoSn)));
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("factorybarcodequalitycheckDet:getSn:" + ex.toString());
            return false;
        }
    }

    public boolean ClearItem(String Status,Context context){

        String query1 = "delete from bfldata..tmpfactorygenerateBarcode select * from bfldata..tmpBuildBox where Status = '"+Status+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
            okMessage("Alert", objGlobal.getErrorMessage().toString(), context);
            return false;
        }

        String query = "Delete from bfldata..tmpBuildBox where Status = '"+Status+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
            okMessage("Alert", objGlobal.getErrorMessage().toString(), context);
            return false;
        }
        return true;
    }

    public boolean SaveBox(String toteid, String status, Context context, String Remarks) {

        getBoxNumber();
        String query1;
        String PalletType = "";
        if (status.equals("Good")) {
            PalletType = "R1";
        } else if (status.equals("Asis")) {
            PalletType = "AS";
        } else {
            PalletType = "WF";
        }
        getPalletNoAutoUsa();
        getSn();

        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select count = count(status) from bfldata..tmpBuildBox where status ='"+status+"' and itemcode <> '' and deviceId = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                if(rs.getInt("count") > 0){

                }
                else{
                    okMessage("Alert", "Please scan itemcode for this - "+status, context);
                    return false;
                }
            }


            objGlobal.getConnection().setAutoCommit(false);

        String query = "insert into usa..upcBoxHead(BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2,WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID)" +
                " values('" + objBuildBoxGlobal.getBoxNo() + "', '" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "', '', '" + objGlobal.getUserName() + "', 'ANDRPDA - "+Remarks+"', '" + objGlobal.getUserId() + "', '" + PalletType + "', 'N', '', '','" + objGlobal.getUserId() + "', '" + objGlobal.getUserId() + "', '" + objGlobal.getWarehouse() + "', '', '" + objGlobal.getUserName() + "', '" + PalletType + "','', '', '" + toteid + "')";
        if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
            objGlobal.getConnection().rollback();
            objGlobal.getConnection().setAutoCommit(true);

            return false;
        }

//        if (status.equals("Good")) {
//            query1 = "insert into usa..upcBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objBuildBoxGlobal.getBoxNo() + "',itemcode,qty=count(itemcode),0,'',itemcode from bfldata..tmpBuildBox where status = 'Good'  and itemcode <> ''  and deviceId = '" + objGlobal.getDeviceName() + "' group by itemcode";
//            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
//                okMessage("Alert", objGlobal.getErrorMessage().toString(), context);
//                objGlobal.getConnection().rollback();
//                objGlobal.getConnection().setAutoCommit(true);
//
//                return false;
//            }
//
//        }
//        else if (status.equals("Asis")) {
            query1 = "insert into usa..upcBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objBuildBoxGlobal.getBoxNo() + "',itemcode,qty=count(itemcode),0,'',itemcode from bfldata..tmpBuildBox where status = '"+status+"'  and itemcode <> ''  and deviceId = '" + objGlobal.getDeviceName() + "' group by itemcode";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                okMessage("Alert",objGlobal.getErrorMessage().toString(), context);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);

                return false;
            }
//        }
//        else  {
//            query1 = "insert into usa..upcBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objBuildBoxGlobal.getBoxNo() + "',itemcode,qty=count(itemcode),0,'',itemcode from bfldata..tmpBuildBox where status = 'writeoff' and itemcode <> '' and deviceId = '" + objGlobal.getDeviceName() + "' group by itemcode";
//            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
//                okMessage("Alert",objGlobal.getErrorMessage().toString(), context);
//                objGlobal.getConnection().rollback();
//                objGlobal.getConnection().setAutoCommit(true);
//
//                return false;
//            }
//        }
            if(!status.equals("Good")) {
                if (!dbConnection.insertUpdate("insert into usapallets(Sn,TrnDate,PalletNo,UserId,Remarks,Closed,ContNo,WHouse) " +
                        "values (" + objBuildBoxGlobal.getPalletSno() + ",'" + objGlobal.getServerDate() + "','" + objBuildBoxGlobal.getPalletNo() + "'," +
                        "" + objGlobal.getUserId() + ",'ANDRPDA - "+Remarks+"','N','','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usapalletsdet(Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID) select " + objBuildBoxGlobal.getPalletSno() + ",'" + objBuildBoxGlobal.getBoxNo() + "','" + objBuildBoxGlobal.getPalletNo() + "','ANDRPDA - "+Remarks+"','1','','','',ToteID from bfldata..tmpBuildBox a where " +
                        "DeviceId='" + objGlobal.getDeviceName() + "' and status = '" + status + "' group by ToteID", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.BoXPallet select '" + objBuildBoxGlobal.getBoxNo() + "','" + objBuildBoxGlobal.getPalletNo() + "' from bfldata..tmpBuildBox where " +
                        "DeviceId='" + objGlobal.getDeviceName() + "' and status = '" + status + "' group by ToteID", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                String query2 =  "insert into bfldata..factorybarcodequalitycheck(Sn,userid,date,time,Status,PalletType, boxno, palletno) values('"+objBuildBoxGlobal.getSn() +"','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "', '"+status+"', '"+PalletType+"', '"+objBuildBoxGlobal.getBoxNo()+"', '"+objBuildBoxGlobal.getPalletSno() +"')";
                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    okMessage("Alert",objGlobal.getErrorMessage().toString(), context);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.PrintFromPda(warehouse,PSystemName,PType,PItem,ReqUser,ReqDate,ReqTime,Printed) values ('" + objGlobal.getWarehouse() + "'," +
                        "'" + objGlobal.getUserPrinterName() + "','UB','" + objBuildBoxGlobal.getBoxNo() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "','N')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            if(status.equals("Good")) {
                String query2 = "insert into bfldata..factorybarcodequalitycheck(Sn,userid,date,time,Status,PalletType, boxno) values('" + objBuildBoxGlobal.getSn() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "', '" + status + "', '" + PalletType + "', '" + objBuildBoxGlobal.getBoxNo() + "')";
                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    okMessage("Alert", objGlobal.getErrorMessage().toString(), context);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.PrintFromPda(warehouse,PSystemName,PType,PItem,ReqUser,ReqDate,ReqTime,Printed) values ('" + objGlobal.getWarehouse() + "'," +
                        "'" + objGlobal.getUserPrinterName() + "','UB','" + objBuildBoxGlobal.getBoxNo() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "','N')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }

            String query3 =  "insert into bfldata..factorybarcodequalitycheckDet(sn, itemcode , generatedbarcode , toteid) select '"+objBuildBoxGlobal.getSn() +"', itemcode,generatedbarcode,toteid from bfldata..tmpBuildBox where status = '"+status+"'  and itemcode <> ''  and deviceId = '" + objGlobal.getDeviceName() + "'";
            if (!dbConnection.insertUpdate(query3, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                okMessage("Alert",objGlobal.getErrorMessage().toString(), context);
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);

        }catch (Exception e){
                okMessage("ALERT", e.getMessage(),context);
        }

        return true;
    }


    public boolean getPalletNoAutoUsa() {
        int pltSn = 0;
        try {
            rs = dbConnection.getResultSet("select sn=Max(sn)+1 from USAPallets", objGlobal.getConnection());
            if (rs.next()) {
                objBuildBoxGlobal.setPalletSno(rs.getString("sn").toString());
            }
            rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7))+1 from USApallets where palletno like 'USA%'", objGlobal.getConnection());
            if (rs.next()) {
                pltSn = Integer.parseInt(rs.getString("sn").toString());
            }
            objBuildBoxGlobal.setPalletNo("USA" + String.format("%06d", pltSn));
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:getPalletNoAutoUsa:" + ex.toString());
            return false;
        }
        return true;
    }



    public ArrayList<BuildBoxitem> checkitems(){
        ArrayList<BuildBoxitem> arr = new ArrayList<BuildBoxitem>();
        try {
         //   String Query = "select a.generatedBarcode, b.itemcode,a.status from BFLDATA..tmpBuildBox a,BFLDATA..factorygenerateBarcode b where a.generatedBarcode = b.generatedBarcode  order by a.Date, a.time desc";
            String Query = "select a.generatedBarcode, a.itemcode,a.status from BFLDATA..tmpBuildBox a where deviceID = '"+objGlobal.getDeviceName()+"' and itemcode <> ''  order by a.Date, a.time desc";
            rs = dbConnection.getResultSet(Query, objGlobal.getConnection());
            while (rs.next()){
                arr.add(new BuildBoxitem(rs.getString("itemcode"),rs.getString("generatedBarcode"),rs.getString("status")));
            }
            GoodsCount();
            AsisCount();
            WriteOffCount();

        }catch(Exception e){

        }
        return arr;
    }


    public int GoodsCount(){
        int goodCount = 0;
        try {
            ResultSet resultSet;
            resultSet = dbConnection.getResultSet("select count=count(*) from bfldata..tmpBuildBox where status = 'Good' and itemcode <> '' and deviceid = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection());
            if (resultSet.next()) {
                goodCount = Integer.valueOf(resultSet.getInt("count"));
                objBuildBoxGlobal.setGoodsCount(Integer.valueOf(resultSet.getInt("count")));
            }

        }catch(Exception e){
            Log.e("Alert", e.toString());
        }
        return goodCount;
    }
    public int AsisCount(){
        int AsisCount = 0;
        try {
            ResultSet resultSet;
            resultSet = dbConnection.getResultSet("select count=count(*) from bfldata..tmpBuildBox where status = 'Asis' and itemcode <> '' and deviceid = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection());
            if (resultSet.next()) {
                AsisCount = Integer.valueOf(resultSet.getInt("count"));
                objBuildBoxGlobal.setAsisCount(Integer.valueOf(resultSet.getInt("count")));
            }

        }catch(Exception e){
            Log.e("Alert", e.toString());
        }
        return AsisCount;
    }
    public int WriteOffCount(){
        int WriteoffCount = 0;
        try {
            ResultSet resultSet;
            resultSet = dbConnection.getResultSet("select count=count(*) from bfldata..tmpBuildBox where status = 'writeoff' and itemcode <> '' and deviceid = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection());
            if (resultSet.next()) {
                WriteoffCount = Integer.valueOf(resultSet.getInt("count"));
                objBuildBoxGlobal.setWriteoffCount(Integer.valueOf(resultSet.getString("count")));
            }

        }catch(Exception e){
            Log.e("Alert", e.toString());
        }
        return WriteoffCount;
    }



}
