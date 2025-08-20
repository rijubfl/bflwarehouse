package com.bflgroup.warehouse.ui.transferbluetotote.eurobox;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.transferbluetotote.eurobox.ToteidDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransferBlueToEuroBoxControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    private String PalletType = "";
    int count = 0;
    private BlueToteEuroBoxGlobal objBlueToteEuroBoxGlobal = BlueToteEuroBoxGlobal.getInstance();



    public TransferBlueToEuroBoxControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("TransferBlueToEuroBoxControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("TransferBlueToEuroBoxControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public ArrayList<ToteidDetails> ScanTote(String Toteid, Context context){
        BluetoteEuroBoxSharedRef objBluetoteEuroBoxSharedRef = new BluetoteEuroBoxSharedRef(context);


        ArrayList<ToteidDetails> arr;
        if (!checkConnection()) {
            return null;
        }
        arr = new ArrayList<ToteidDetails>();
        try {
            String que = "select * from bfldata..tmpToteEuroBox where (toteid = '"+Toteid+"' or Boxno = '" + Toteid + "') and Deviceid = '" + objGlobal.getDeviceName()+"'";



//            String query = "select toteid,h.BoxNo,pallettype,qty,itemcode  from  usa..upcboxhead h, usa..upcboxdet d where ToteID = '"+Toteid+"' and closed = 'N' and h.boxno=d.boxno";
            String query = "select Boxno,Pallettype, qty = isnull(sum(qty), 0), ToteID  from usa..vupcBoxdet where (toteid = '"+Toteid+"' or Boxno = '" + Toteid + "') and closed = 'N' and (palletno IS NULL or palletno = '') group by ToteID, Boxno, Pallettype";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            ResultSet rs1 = dbConnection.getResultSet(que, objGlobal.getConnection());
            if(!rs1.next()) {
                int Count = 0;
                while (rs.next()) {
                    Log.e("Result", "Here");
                    Count++;
                }
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                    if(Count == 1){
                        if (rs.next()) {
                            Log.e("Pallettype database", rs.getString("Pallettype").trim());
                            Log.e("toteid database", rs.getString("toteid").trim());
                            Log.e("Boxno database", rs.getString("Boxno"));
                            Log.e("Qty database", rs.getString("Qty"));

                            if (objBluetoteEuroBoxSharedRef.LoadPalletType() == "") {
                                String query1 = "Insert into bfldata..tmpToteEuroBox values('" + rs.getString("toteid") + "','" + rs.getString("Boxno") + "','" + rs.getString("PalletType") + "', '" + rs.getString("Qty") + "', '" + objGlobal.getDeviceName() + "')";
                                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                                    // objGlobal.getConnection().rollback();
                                    objBluetoteEuroBoxSharedRef.savePalletType("");
                                }
                                objBluetoteEuroBoxSharedRef.savePalletType(rs.getString("palletType"));
                            } else if (objBluetoteEuroBoxSharedRef.LoadPalletType().equals(rs.getString("palletType").trim())) {
                                String query1 = "Insert into bfldata..tmpToteEuroBox values('" + rs.getString("toteid") + "','" + rs.getString("Boxno") + "','" + rs.getString("PalletType") + "', '" + rs.getString("Qty") + "', '" + objGlobal.getDeviceName() + "')";
                                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                                    //objGlobal.getConnection().rollback();
                                }
                            } else if (objBluetoteEuroBoxSharedRef.LoadPalletType() != rs.getString("palletType")) {
                                okMessage("Alert", "PalletType is not same ", context);
                            }
                        }
                    }
                    else if(Count > 1){
                        Log.e("size",Count+"");
                        okMessage("Alert", "Multiple Boxno found for this tote", context);
                    }


                if(Count == 0) {
                    okMessage("Alert", "Invalid Toteid", context);
                }
            }else{
                okMessage("Alert", "Duplicate Tote id", context);
            }
            String query2 = "select * from bfldata..tmpToteEuroBox where Deviceid = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs3 = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs3.next()) {
                arr.add(new ToteidDetails(rs3.getString("toteid"), rs3.getString("Boxno"), rs3.getString("PalletType"), rs3.getString("Qty")));
            }

        }catch(Exception e){
            Log.e("Error at toteiddetails", e.toString());
        }

        return arr;
    }


    public ArrayList<ToteidDetails> GetTote(){
        ArrayList<ToteidDetails> arr;
        if (!checkConnection()) {
            return null;
        }
        arr = new ArrayList<ToteidDetails>();
        try {
            String query = "select * from bfldata..tmpToteEuroBox where Deviceid = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs3 = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs3.next()) {
                arr.add(new ToteidDetails(rs3.getString("toteid"), rs3.getString("Boxno"), rs3.getString("PalletType"), rs3.getString("Qty")));
            }
        }catch(Exception e){
            Log.e("Error at GetTote", e.toString());
        }
        return arr;
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
            objBlueToteEuroBoxGlobal.setBoxNo(suff + String.format("%06d", autoSn));
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("objBlueToteEuroBoxGlobal:getBoxNumber:" + ex.toString());
            return false;
        }
    }


    public boolean InsertUpcBox() throws SQLException {


        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(objGlobal.getWarehouse())) {
            objGlobal.setErrorNo("savePallet:Warehouse is empty");
            return false;
        }
        getPalletNoAuto();
        if (TextUtils.isEmpty(objBlueToteEuroBoxGlobal.getPalletSno())) {
            objGlobal.setErrorNo("savePallet:Pallet SNo. Is empty");
            return false;
        }
        if (TextUtils.isEmpty(objBlueToteEuroBoxGlobal.getPalletNo())) {
            objGlobal.setErrorNo("savePallet:Pallet No. Is empty");
            return false;
        }
        getBoxNumber();
            if(TextUtils.isEmpty(objBlueToteEuroBoxGlobal.getBoxNo())){
            objGlobal.setErrorNo("savePallet:bOXnO No. Is empty");
            Log.e("Boxno = ",objBlueToteEuroBoxGlobal.getBoxNo());
            return false;
        }

        if(!dbConnection.insertUpdate("insert into bfldata..closeR1pallet(Contno,Palletno,Trndate,Time1,UserId,ClosedBy,PurchNo,RtnNo,MissQty,MissAmt,ZeroQty,remarks) select 'USABOX', BoxNo , '"+objGlobal.getServerDate()+"', '"+objGlobal.getServerTime()+"', '"+objGlobal.getUserId()+"', '"+objGlobal.getUserName()+"', '', '','', 0, 0, '' from bfldata..tmptoteEuroBox where deviceId = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())){
            return false;
        }



            String query1 = "Insert into usa..upcBoxDet(Boxno,itemcode, Qty, QtyIssued, Status, UPC) select '"+objBlueToteEuroBoxGlobal.getBoxNo()+"',itemcode,sum(qty) qty,sum(qtyIssued) qtyIssued, '',itemcode UPC from usa..UPCBOXDet where BoxNo IN (select BoxNo from bfldata..tmptoteEuroBox where deviceId = '"+objGlobal.getDeviceName()+"') group by itemcode";
            if(!dbConnection.insertUpdate(query1, objGlobal.getConnection())){
                //objGlobal.getConnection().rollback();
                return false;
            }

        String query = "Update usa..upcBoxHead set closed = 'Y' where toteid IN (select Toteid from bfldata..tmptoteEuroBox where deviceId = '"+objGlobal.getDeviceName()+"')";
            if(!dbConnection.insertUpdate(query, objGlobal.getConnection())){
                return false;
            }

            String query3 = "Insert into bfldata..UpdateToteEuroBox (boxno,Toteid,trndate,time,preparedby) select boxno, toteid, '"+objGlobal.getServerDate()+"', '"+objGlobal.getServerTime()+"', '"+objGlobal.getUserName()+"' from bfldata..tmptoteEuroBox where deviceId = '"+objGlobal.getDeviceName()+"'  ";
        if(!dbConnection.insertUpdate(query3, objGlobal.getConnection())){
            return false;
        }

        String query2  = "Insert into usa..upcboxHead(BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2,WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) " +
                "values('"+objBlueToteEuroBoxGlobal.getBoxNo()+"', '"+objGlobal.getServerDate()+"', '"+objGlobal.getServerTime()+"', '', '"+objGlobal.getUserName()+"', 'ANDRPDA', '"+objGlobal.getUserId()+"', '"+BluetoteEuroBoxSharedRef.LoadPalletType()+"', 'N', '', '','"+objGlobal.getUserId()+"', '"+objGlobal.getUserId()+"', '"+objGlobal.getWarehouse()+"', '', '"+objGlobal.getUserName()+"', '"+BluetoteEuroBoxSharedRef.LoadPalletType()+"','', '', '' )";
        if(!dbConnection.insertUpdate(query2, objGlobal.getConnection())){
           // objGlobal.getConnection().rollback();
            return false;
        }

        if (!dbConnection.insertUpdate("Insert into bfldata..usaPallets (sn, TrnDate,PalletNo,UserId,Remarks,Closed,ContNo,WHouse) values('"+objBlueToteEuroBoxGlobal.getPalletSno()+"', '"+objGlobal.getServerDate()+"', '"+objBlueToteEuroBoxGlobal.getPalletNo()+"', '"+objGlobal.getUserId()+"', '' , 'N', '','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())){
            return false;
        }
        if (!dbConnection.insertUpdate("Insert into bfldata..USAPalletsDet (Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID ) values('"+objBlueToteEuroBoxGlobal.getPalletSno()+"', '"+objBlueToteEuroBoxGlobal.getBoxNo()+"', '"+objBlueToteEuroBoxGlobal.getPalletNo()+"','', 1, '"+objGlobal.getUserName()+"','', '' , '')", objGlobal.getConnection())){
            return false;
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

    //getPalletNumber
    public boolean getPalletNoAuto() {
        int pltSn = 0;
        //String palletPrefix = "USA";
        String palletPrefix = "UAE";
        try {
            rs = dbConnection.getResultSet("select sn=Max(sn)+1 from USAPallets", objGlobal.getConnection());
            if (rs.next()) {
                objBlueToteEuroBoxGlobal.setPalletSno(rs.getString("sn").toString());
            }
            rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7))+1 from USApallets where palletno like '" +  palletPrefix + "%'", objGlobal.getConnection());
            if (rs.next()) {
                pltSn = Integer.parseInt(rs.getString("sn").toString());
            }
            objBlueToteEuroBoxGlobal.setPalletNo(palletPrefix + String.format("%06d", pltSn));
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:getPalletNoAutoUsa:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean deletetemp() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata..tmpToteEuroBox where Deviceid = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("tmpPalletScan:clearTable:" + ex.toString());
            return false;
        }

        return true;
    }



}
