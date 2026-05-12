package com.bflgroup.warehouse.ui.chutestatusinout.techno;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TransferReceipt {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private InOutGlobal objInOutGlobal = InOutGlobal.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    private String s_Result;
    Connection con = null;

    public TransferReceipt() {
        con = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        con = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("ChuteCheckInCheckOutControl:tmpConnectDb : "+ objGlobal.getRoboServerIP());
        }
        return true;
    }

    public boolean transferReceipt(String chuteId, String toteId, String shopId, String shopName,String lpmDt) {
        String dataName = "", trfRecNo = "", costCodeFrom = "", costCodeTo = "", locCodeFrom = "", locCodeTo = "", debitAc = "410005", creditAc = "129999", narration = "USA-New", fcCode = "AED", shopInShop = "";
        String approvedBy = "UHO-", preparedBy = "[" + objGlobal.getEmpCode() + "]", storeIssue = toteId, trfType = "R", palletNo = "", cartonNo = "1", empName = "", labelInfo="";
        int totalQty = 0;
        float totalAmt = 0, fcRate = 1;
        if (!checkConnection()) {
            return false;
        }
        try {
            objInOutGlobal.setTrfRecNo("");
            if (!dbConnection.insertUpdate("delete from tmpTransfer where DeviceName='" + objGlobal.getDeviceName() + "'", con)) {
                objGlobal.setErrorNo("transferReceipt:001: "+objGlobal.getConnection().getClientInfo());
                return false;
            }
            if (!dbConnection.insertUpdate("insert into tmpTransfer(itemcode,qty,rate,description,groupcode,catcode,UserId,unitcode,SalesRate,Trf,ItemType,DeviceName) " +
                    "select itemcode,sum(qty),0.01,'','',''," + objGlobal.getUserId() + ",'',0,'','','" + objGlobal.getDeviceName() + "' from SortingConformationDetail where TransferNo='' " +
                    "and ChuiteId='" + chuteId + "' and ShopId='" + shopId + "' group by itemcode", con)) {
                objGlobal.setErrorNo("transferReceipt:002");
                return false;
            }
            rs = dbConnection.getResultSet("select amount=round(sum(qty*rate),2),qty=(sum(qty)) from tmpTransfer where DeviceName='" + objGlobal.getDeviceName() + "'", con);
            if (rs.next()) {
                totalAmt = rs.getFloat("amount");
                totalQty = rs.getInt("qty");
            } else {
                objGlobal.setErrorNo("transferReceipt: No Records found");
                return false;
            }
            if (!dbConnection.getServerDateTime(con)) {
                objGlobal.setErrorNo("transferReceipt:007");
                return false;
            }
            if (TextUtils.isEmpty(objGlobal.getDelDate())) {
                objGlobal.setErrorMessage("Delivery Date not set");
                objGlobal.setErrorNo("transferReceipt:031");
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.datasettings where shopname='" + shopName + "'", con);
            if (rs.next()) {
                dataName = rs.getString("dataName");
                costCodeTo = rs.getString("costCodeTo");
                costCodeFrom = rs.getString("costCodeTo");
                locCodeTo = rs.getString("locCodeTo");
                shopInShop = rs.getString("shopInShop");
            }
            if (shopInShop.equals("Y")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.datasettings where ShopName in(select mainshop from bfldata.dbo.shopinshop " +
                        "where subshop='" + shopName + "')", con);
                if (rs.next()) {
                    dataName = rs.getString("dataName");
                    costCodeTo = rs.getString("costCodeTo");
                    costCodeFrom = rs.getString("costCodeTo");
                    locCodeTo = rs.getString("locCodeTo");
                }
            }
            if (TextUtils.isEmpty(dataName) || TextUtils.isEmpty(costCodeTo) || TextUtils.isEmpty(locCodeTo)) {
                objGlobal.setErrorMessage("Invalid Dataname");
                objGlobal.setErrorNo("transferReceipt:008");
                return false;
            }
            if(lpmDt.isEmpty()){
                rs = dbConnection.getResultSet("SELECT LpmDt=CONVERT(varchar(10),DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0),103)", objGlobal.getConnection());
                if (rs.next()) {
                    lpmDt = rs.getString("LpmDt");
                }
            }
            objInOutGlobal.setChuteNo(getChuteNo(shopId));
            cartonNo = getCartonNo(dataName, objGlobal.getServerDate(), costCodeTo, locCodeTo);
            if (TextUtils.isEmpty(cartonNo)) {
                objGlobal.setErrorMessage("Box Number is wrong");
                objGlobal.setErrorNo("transferReceipt:008-01");
                return false;
            }
            trfRecNo = getLatestTrfNo(dataName);
            if (TextUtils.isEmpty(trfRecNo)) {
                objGlobal.setErrorMessage("Transfer Receipt number is wrong");
                objGlobal.setErrorNo("transferReceipt:008-02");
                return false;
            }
            if (totalAmt <= 0 || totalQty <= 0) {
                objGlobal.setErrorNo("transferReceipt: Quantity or Amount is 0");
                return false;
            }
            con.setAutoCommit(false);
            //insert transfer detail start *****************************************
            if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.transferdetail (trfno,itemcode,unitcode,quantity,rate,batchno,basicqty,basicrate,srno,upc," +
                    "ItemType) select '" + trfRecNo + "',itemcode,unitcode,qty,rate,'',qty,rate,(ROW_NUMBER() OVER(ORDER BY itemcode ASC)),itemcode,'' from tmpTransfer " +
                    "where DeviceName='" + objGlobal.getDeviceName() + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:015");
                return false;
            }
            //insert into transferheader *****************************************
            if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.transferheader (TrfNo,TrfDate,CostCodeFrom,LocCodeFrom,CostCodeTo,LocCodeTo,Accode,Narration," +
                    "NetAmount,UserId,TrfType,FCCode,FCRate,ApprovedBy,PreparedBy,ConsumeReturn,JobNo,StoreIssue,StoreReceipt,EntryMode,ShipNo,CartonNo," +
                    "PalletNo,Starttime,LpmDt) values ('" + trfRecNo + "','" + objGlobal.getServerDate() + "','" + costCodeFrom + "','" + locCodeFrom + "','" + costCodeTo + "','" + locCodeTo + "','" + debitAc + "'," +
                    "'" + narration + "'," + totalAmt + "," + objGlobal.getUserId() + ",'" + trfType + "','" + fcCode + "'," + fcRate + ",'" + approvedBy + "','" + preparedBy + "'," +
                    "'N',convert(varchar(15),getdate(),108),'" + storeIssue + "','" + objGlobal.getEmpName() + "','A','" + objGlobal.getDelDate() + "','" + cartonNo + "','" + palletNo + "'," +
                    "'" + objGlobal.getServerTime() + "','" + lpmDt + "')", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:018");
                return false;
            }
            //Rfpair
            if (!dbConnection.insertUpdate("update " + dataName + ".dbo.rfpair set TrfNo='" + trfRecNo + "' where rfid in(select Rfid from " +
                    "SortingConformationDetail where TransferNo='' and ChuiteId='" + chuteId + "' and ShopId='" + shopId + "' and rfid<>'')", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:022");
                return false;
            }
            //rfpairdetail
            if (!dbConnection.insertUpdate("update bfldata.dbo.RFPairDetail set TrfNo='" + trfRecNo + "',trfdate='" + objGlobal.getServerDate() + "',PairSn=0 where TrfNo='' and " +
                    "ShopName='" + shopName + "' and rfid in(select Rfid from SortingConformationDetail where TransferNo='' and ChuiteId='" + chuteId + "' and " +
                    "ShopId='" + shopId + "' and rfid<>'')", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:023");
                return false;
            }
            //insert transfer no return************************************
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.TransferNoReturn (TrnDate,ShopName,TrfNo,RetNo,Quantity,UserId,Dept,Dataname,isImport,ImpDateTime,isCountUpdate,CountUpdateDateTime,TrnTime) " +
                    "values ('" + objGlobal.getServerDate() + "','" + shopName + "','" + trfRecNo + "',''," + totalQty + "," + objGlobal.getUserId() + ",'USA','" + dataName + "','N',null,'N',null,'" + objGlobal.getServerTime() + "')", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:025");
                return false;
            }
            //insert chute status *****************************************
            if (!dbConnection.insertUpdate("insert into ChuteCheckout values('" + chuteId + "','" + toteId + "','" + shopId + "','" + shopName + "','" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "','" + trfRecNo + "'," + objGlobal.getUserId() + ")", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:026");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteConfigurationlog (ChuteId,ShopId,ShopName,TotId,Trndate,userId,direction) " +
                    "select ChuteId,ShopId,ShopName,TotId,getdate(),'" + objGlobal.getUserId() + "','OUT' from ChuteConfiguration where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteConfiguration set TotId='' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:026-1");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteStatusLog select ChuteId,Status,'2','" + objGlobal.getUserId() + "',getdate() " +
                    "from ChuteIdMaster where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:029.01");
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteIdMaster set Status='2' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:029.02");
                return false;
            }
            if (!dbConnection.insertUpdate("update SortingConformationDetail set TransferNo='" + trfRecNo + "' where TransferNo='' and " +
                    "ChuiteId='" + chuteId + "' and ShopId='" + shopId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:028");
                return false;
            }
            if (!dbConnection.insertUpdate("delete from ROBOTICS.dbo.BlockChuteForScan where Chuteid='" + toteId + "' or Device='" + objGlobal.getDeviceName() + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:031");
                return false;
            }
            labelInfo="{\"Shop\":\"" + shopName + "\",\"TrfNo\":\"" + trfRecNo + "\",\"TrfDate\":\"" + objGlobal.getServerDate() + "\",\"DeliveryDate\":\"" +
                    objGlobal.getDelDate() + "\",\"Remarks\":\"" + toteId + "\",\"PreparedBy\":\"" + objGlobal.getUserName() + "\",\"BoxNo\":\"" + cartonNo + "\",\"Quantity\":\"" + totalQty + "\"}";
            if (!dbConnection.insertUpdate("insert into SortTask values('" + objGlobal.getServerDate() + "','" + toteId + "','" + objInOutGlobal.getChuteNo() + "'," +
                    "'" + shopId + "','" + shopName + "','" + trfRecNo + "'," + objGlobal.getUserId() + ",'" + chuteId + "','N','N','" + objInOutGlobal.getLabelInfo() + "')", con)) {
                con.rollback();
                objGlobal.setErrorNo("transferReceipt:029");
                return false;
            }
            con.commit();
            con.setAutoCommit(true);
            objInOutGlobal.setTrfRecNo(trfRecNo);
            objInOutGlobal.setBatchCode(objGlobal.getDelDate() + "-" + cartonNo);
            objInOutGlobal.setTrfTotQty(totalQty);
            objInOutGlobal.setLabelInfo(labelInfo);
            return true;
        } catch (Exception exception) {
            try {
                con.rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("transferReceipt:sqlException:1: " + sqlException);
                return false;
            }
            objGlobal.setErrorMessage(":transferReceipt:exception:2: " + exception);
            return false;
        }
    }

    private String getChuteNo(String shopId) {
        return dbConnection.stringReturn(con, "DeliveryChuteConfiguration", "ChuteNo", "shopid", shopId);
    }

    private String getCartonNo(String dataName, String delDate, String costCodeTo, String locCodeTo) {
        String str = "1";
        try {
            rs = dbConnection.getResultSet("select cartonno=isnull(max(cartonno),0)+1 from " + dataName + ".dbo.transferheader where trfdate='" + delDate + "' and " +
                    "costcodeto='" + costCodeTo + "' and loccodeto='" + locCodeTo + "'", con);
            if (rs.next()) {
                str = rs.getString("cartonno");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferReceipt:getChuteNo:" + ex.toString());
            return "";
        }
        return str;
    }

    private String getLatestTrfNo(String dataName) {
        int autoSn = 0;
        String trfPrefix="";
        try {
            trfPrefix = objGlobal.getTransferPrefixRobo();
            rs = dbConnection.getResultSet("select TransferPrefixRobo from BFLDATA.dbo.TransferPrefix where warehouse='" + objGlobal.getWarehouse() + "' and dataname='" + dataName + "'", con);
            if(rs.next()){
                trfPrefix = rs.getString("TransferPrefixRobo");
            }
            rs = dbConnection.getResultSet("select en=isnull(max(cast(right(trfno,7) as int)),0)+1 from " + dataName + ".dbo.transferheader where left(trfno,2)='" + trfPrefix + "' and (trftype='R') " +
                    "and substring(trfno,3,1)<>'D'", con);
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            return trfPrefix + String.format("%07d", autoSn);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferReceipt:getLatestTrfNo:" + ex);
            return "";
        }
    }
}