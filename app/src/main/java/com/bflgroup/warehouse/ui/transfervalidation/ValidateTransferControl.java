package com.bflgroup.warehouse.ui.transfervalidation;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ValidateTransferControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();
    //  PosGlobal objPosGlobal = PosGlobal.getInstance();

    boolean result;
    boolean firstGrn;
    String query;
    String trfDate;
    String grnRfEnGlb;
    String missingBarcodeEntryNo;
    String trfNo;

    Statement stmt;
    ResultSet rs;

    public String getGrnRfEnGlb() {
        return grnRfEnGlb;
    }

    public void setGrnRfEnGlb(String grnRfEnGlb) {
        this.grnRfEnGlb = grnRfEnGlb;
    }

    public String getTrfNo() {
        return trfNo;
    }

    public void setTrfNo(String trfNo) {
        this.trfNo = trfNo;
    }

    public String getMissingBarcodeEntryNo() {
        return missingBarcodeEntryNo;
    }

    public void setMissingBarcodeEntryNo(String missingBarcodeEntryNo) {
        this.missingBarcodeEntryNo = missingBarcodeEntryNo;
    }

    public ValidateTransferControl() {
        result = dbConnection.connectDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Local Connection error 1.0");
        }
        objGlobal.setCloudDbName("BFLDATA");
        result = dbConnection.connectCloudDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error 1.0");
        }
    }

    public ArrayList<String> loadShops() {
        ArrayList<String> arr = null;
        try {
            arr = new ArrayList<String>();

            rs = dbConnection.getResultSet("select shopname = concat(Shopcode,' - ',shopname) from bfldata..datasettings where (Active = 'Y' or ExportActive = 'Y')", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("shopname"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }


    public ArrayList<ValidateTransferScanItemsAll> validateTransferNumber(String trfNo, Boolean view, String Shopname) {
        ArrayList arr = new ArrayList<ValidateTransferScanItemsAll>();
        if (dbConnection.checkConnectionClosed() == false) {
            result = dbConnection.connectDb();
            if (result == false) {
                objGlobal.setErrorMessage("GrnTransferControl.validateTransferNumber : Connection error");
                return null;
            }
        }
        String shopname = Shopname;
        String shop[] =  shopname.split("-");
        if (TextUtils.isEmpty(trfNo)) {
            objGlobal.setErrorMessage("Please enter transfer number");
            return null;
        }
        try {
            String Dataname = "";
            String Costcode = "";
            String Loccode = "";
            rs = dbConnection.getResultSet("select DataName,ShopName,shopletter,CostCodeTo,LocCodeTo from bfldata.dbo.datasettings where Shopname='" + shop[1].trim() + "'", objGlobal.getConnection());
            if (rs.next()) {
                Dataname = rs.getString("Dataname");
                Costcode = rs.getString("CostCodeTo");
                Loccode = rs.getString("LocCodeTo");
                objGlobal.setCloudDbName(rs.getString("DataName").toString());
               // objGlobal.setShopLetter(rs.getString("shopLetter"));
            } else {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer1 : Shop not found in datasettings, " + shop[1].trim());
                return null;
            }

            query = "select top 1 * from " + Dataname + "..transferheader where costcodeTo = '"+Costcode+"' and  (trfno='" + trfNo + "' or StoreIssue='" + trfNo + "') order by trfdate desc";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                //objGlobal.setToteTrfNo(rs.getString("trfno"));
                java.util.Date date = rs.getDate("trfdate");
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                trfDate = df.format(date);
            } else {
                objGlobal.setErrorMessage("Invalid Transfer Number, " + trfNo);
                return null;
            }

           return TransferDetails(trfNo,Dataname,shop[1].trim(), Costcode, Loccode, view);

        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex.toString());
            return null;
        }

    }

    public ArrayList<ValidateTransferScanItemsAll> loadTransferScan(String trfNo) {
        ArrayList<ValidateTransferScanItemsAll> listTransferScan = new ArrayList<ValidateTransferScanItemsAll>();
        try {
            rs = dbConnection.getResultSet("select distinct itemcode, quantity, scanQty,Diff,Scandate,time from BFLDATA..tmpvalidatetransfer where trfno = '" + trfNo + "' and deviceID = '"+objGlobal.getDeviceName()+"' order by scandate,time desc", objGlobal.getConnection());
            while (rs.next()) {
                listTransferScan.add(new ValidateTransferScanItemsAll(rs.getString("itemcode"), rs.getInt("quantity"), rs.getInt("scanQty"), rs.getInt("Diff")));
            }
                return listTransferScan;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinGrnScan:" + ex.toString());
            return listTransferScan;
        }
    }

    public boolean validItemcode(String trfno, String itemcode) {
        try {

            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                return false;
            }
            query = "select * from BFLDATA..tmpvalidatetransfer where itemcode='" + itemcode + "' and trfno = '"+trfno+"' and deviceId = '"+objGlobal.getDeviceName() +"'";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {

                String query1 = "update BFLDATA..tmpvalidatetransfer set scanQty = scanQty + 1, Diff =  (Quantity - (ScanQty + 1)), scanDate = '"+objGlobal.getServerDate()+"' ,time = '"+objGlobal.getServerTime() +"' where itemcode='" + itemcode + "' and trfno = '"+trfno+"' and deviceId = '"+objGlobal.getDeviceName() +"'";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                        ScanTotalCount(trfNo);
                        DiffTotalCount(trfNo);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex.toString());
            return false;
        }
    }

    public boolean validItemQuantity(String trfno, String itemcode) {
        try {

            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                return false;
            }
            query = "select * from BFLDATA..tmpvalidatetransfer where itemcode='" + itemcode + "' and trfno = '"+trfno+"' and deviceId = '"+objGlobal.getDeviceName() +"' and quantity >= scanqty ";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex.toString());
            return false;
        }
    }



    public String validateManagerVerify(String password) {
        String retVal = "";
        if (password.isEmpty()) {
            objGlobal.setErrorMessage("Please enter password");
            return retVal;
        }
        try {
            query = "select mgrname from dbmall.dbo.managercode where pwd='" + password + "' and isnull(mgrname,'')<>''";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                retVal = rs.getString("mgrname");
            } else {
                objGlobal.setErrorMessage("Invalid password");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex);
        }
        return retVal;
    }

    public ArrayList<ValidateTransferScanItemsAll> TransferDetails (String trfNo, String Dataname, String Shopname, String Costcode, String Loccode, Boolean view){
        ArrayList arr = new ArrayList<ValidateTransferScanItemsAll>();
        if (result == false) {
            return null;
        }

        //firstGrn = true;
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return null;
        }
        try {
//            rs = dbConnection.getResultSet("select itemcode, quantity, scanQty=0,Diff=0,trfdate from " + Dataname + "..vTransferDetail where trfno = '" + trfNo + "' and costcodeto = '"+Costcode+"'", objGlobal.getConnection());
//            if(!rs.next()){
//                objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + "Transfer Not found in this shop");
//            }else {


//            }
//            else {
            String query5 = "select * from BFLDATA..validatetransferHead where trfno = '" + trfNo + "' and shopname = '"+Shopname+"'";
            rs = dbConnection.getResultSet(query5, objGlobal.getConnection());
            if (!rs.next()) {

                String query4 = "select * from BFLDATA..tmpvalidatetransfer where trfno = '" + trfNo + "' and deviceId = '" + objGlobal.getDeviceName() + "'";
                rs = dbConnection.getResultSet(query4, objGlobal.getConnection());
                if (!rs.next()) {
                    String query1 = "insert into BFLDATA..tmpvalidatetransfer(trfno,itemcode,shopname,userId,username,trfdate,scanDate,time,deviceId,quantity,scanqty,Diff,costcode) " +
                            "select '" + trfNo + "', itemcode, '" + Shopname + "','" + objGlobal.getUserId() + "','" + objGlobal.getUserName() + "',trfdate,'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'" +
                            ",'" + objGlobal.getDeviceName() + "', quantity, 0,0,'" + Costcode + "' from " + Dataname + "..vTransferDetail where trfno = '" + trfNo + "' and costcodeto = '" + Costcode + "'";
                    if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return null;
                    }
                }


            rs = dbConnection.getResultSet("select distinct itemcode, quantity, scanQty,Diff,scandate,time from BFLDATA..tmpvalidatetransfer where trfno = '" + trfNo + "' and deviceId = '" +objGlobal.getDeviceName()+ "' order by scandate,time desc", objGlobal.getConnection());
            while (rs.next()) {

                arr.add(new ValidateTransferScanItemsAll(rs.getString("itemcode"), rs.getInt("quantity"), rs.getInt("scanQty"), rs.getInt("Diff")));
                // result = dbConnection.insertUpdate("", objGlobal.getConnection());

//                if (result == false) {
//                    objGlobal.getConnection().rollback();
//                    return null;
//                }
            }
            }else{

                objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + "Transfer is already scanned for this shop - "+trfNo);
                return null;
            }
           // view = true;
        }catch (Exception ex){

            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex);
            return null;
        }


        return arr;
    }


    public boolean saveGrn(String trfNo, ArrayList<ValidateTransferScanItemsAll> listValidateTransferScanItemsAll, String Shopname) {
        // result = validateTransferNumber(trfNo, false, Shopname);

        firstGrn = true;
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }

        try {
            int sn = 0;
            rs = dbConnection.getResultSet("select sn=isnull(max(entryno),0)+1 from bfldata.dbo.validatetransferHead", objGlobal.getConnection());

            if (rs.next()) {
                sn = rs.getInt("sn");
            }

            if (sn == 0) {
                objGlobal.setErrorMessage("Wrong SN");
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.validatetransferHead(EntryNo,EntryDate,UserId,TrfNo,CostCode,Shopname) " +
                    "select distinct '" + sn + "', '"+objGlobal.getServerDate()+"',username,trfno,costcode,shopname from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.validatetransferDet (EntryNo,GINNo,TrfNo,Itemcode,RfId,ScanMode,TrfQty,ScanQty,Diff) select distinct '"+sn+"',0,trfno,itemcode,Itemcode,0,Quantity,ScanQty,Diff  from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"' and deviceId = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("WarehouseGRNNewControl:grnSave:ex1:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("WarehouseGRNNewControl:grnSave:ex2:" + e);
                return false;
            }
            return false;
        }

    }


    public int TrfTotalCount(String trfNo){
        int a = 0;
        String query1 = "select qty = sum(quantity) from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        try {
            if (rs.next()) {
                a = rs.getInt("qty");
                ValidateTransferGlobal.setTrfTotal(rs.getInt("qty"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return a;

    }

    public int ScanTotalCount(String trfNo){
        int a = 0;
        String query1 = "select qty = sum(scanqty) from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        try {
            if (rs.next()) {
                a = rs.getInt("qty");
                ValidateTransferGlobal.setScanTotal(rs.getInt("qty"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return a;

    }

    public int DiffTotalCount(String trfNo){
        int a = 0;
        String query1 = "select qty = sum(quantity) - sum(scanQty) from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"' and deviceId = '"+objGlobal.getDeviceName()+"'";
        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        try {
            if (rs.next()) {
                a = rs.getInt("qty");
                ValidateTransferGlobal.setDiffTotal(rs.getInt("qty"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return a;

    }

    public Boolean DeleteTmp (String trfNo){

        if (!dbConnection.insertUpdate("delete from BFLDATA..tmpvalidatetransfer where trfno = '"+trfNo+"' and deviceId = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())) {
            return false;
        }
        return true;
    }



}
