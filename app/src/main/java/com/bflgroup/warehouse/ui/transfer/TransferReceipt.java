package com.bflgroup.warehouse.ui.transfer;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferReceipt {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private TransferGlobal objTransferGlobal = TransferGlobal.getInstance();
    private ResultSet rs;

    public boolean transferReceipt(String shopName, String palletBoxNo, String toteid, String trftype, String regSIMExclude,String typeUsaTcm) {
        String dataName = "", trfRecNo = "", costCodeFrom = "", costCodeTo = "", locCodeFrom = "", locCodeTo = "", debitAc = "410005", creditAc = "129999", narration = "USA-New", fcCode = "AED", shopInShop = "";
        String approvedBy = "UHO-", preparedBy = "[" + objGlobal.getEmpCode() + "]", trfType = "R", trfPalletNo = "", cartonNo = "1", empName = "", storeIssue = palletBoxNo, firstScanTime = "";
        if (!objGlobal.getWorkLocation().equals("UAE")) preparedBy = objGlobal.getUserName();
        int totalQty = 0;
        float totalAmt = 0, fcRate = 1;
        objTransferGlobal.setTrfRecNo("");
        Connection conRob = null;
        Connection conLoc = null;
        try {
            conLoc = objGlobal.getConnection();
            conRob = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(), "BFLDATA");
            if (conRob == null || conLoc == null) {
                objGlobal.setErrorMessage("Connection error");
                return false;
            }
            if (!dbConnection.insertUpdate("delete from robotics.dbo.tmpTransfer where DeviceName='" + objGlobal.getDeviceName() + "'", conRob)) {
                objGlobal.setErrorNo("transferReceipt:001");
                return false;
            }
            rs = dbConnection.getResultSet("select itemcode,qty=sum(qty) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' group by itemcode", objGlobal.getConnection());
            while (rs.next()) {
                if (!dbConnection.insertUpdate("insert into robotics.dbo.tmpTransfer(itemcode,qty,rate,description,groupcode,catcode,UserId,unitcode,SalesRate,Trf,ItemType,DeviceName) " +
                        "values('" + rs.getString("itemcode") + "'," + rs.getInt("qty") + ",0.01,'','',''," + objGlobal.getUserId() + ",'',0,'',''," +
                        "'" + objGlobal.getDeviceName() + "')", conRob)) {
                    objGlobal.setErrorNo("transferReceipt:002");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select top 1 ScanTime=convert(varchar,ScanTime,8) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' order by ScanTime", objGlobal.getConnection());
            if (rs.next()) {
                firstScanTime = rs.getString("ScanTime");
            }
            rs = dbConnection.getResultSet("select amount=round(sum(qty*rate),2),qty=(sum(qty)) from robotics.dbo.tmpTransfer where DeviceName='" + objGlobal.getDeviceName() + "'", conRob);
            if (rs.next()) {
                totalAmt = rs.getFloat("amount");
                totalQty = rs.getInt("qty");
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:003");
                return false;
            }
            if (TextUtils.isEmpty(objGlobal.getDelDate())) {
                objGlobal.setErrorMessage("Delivery Date not set");
                objGlobal.setErrorNo("transferReceipt:004");
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.datasettings where shopname='" + shopName + "'", objGlobal.getConnection());
            if (rs.next()) {
                dataName = rs.getString("dataName");
                costCodeTo = rs.getString("costCodeTo");
                costCodeFrom = rs.getString("costCodeTo");
                locCodeTo = rs.getString("locCodeTo");
                shopInShop = rs.getString("shopInShop");
            }
            if (shopInShop.equals("Y")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.datasettings where ShopName in(select mainshop from bfldata.dbo.shopinshop " +
                        "where subshop='" + shopName + "')", objGlobal.getConnection());
                if (rs.next()) {
                    dataName = rs.getString("dataName");
                    costCodeTo = rs.getString("costCodeTo");
                    costCodeFrom = rs.getString("costCodeTo");
                    locCodeTo = rs.getString("locCodeTo");
                }
            }
            if (TextUtils.isEmpty(dataName) || TextUtils.isEmpty(costCodeTo) || TextUtils.isEmpty(locCodeTo)) {
                objGlobal.setErrorMessage("Invalid Dataname");
                objGlobal.setErrorNo("transferReceipt:005");
                return false;
            }
            cartonNo = getCartonNo(conRob, dataName, objGlobal.getServerDate(), costCodeTo, locCodeTo);
            if (TextUtils.isEmpty(cartonNo)) {
                objGlobal.setErrorMessage("Box Number is wrong");
                objGlobal.setErrorNo("transferReceipt:006");
                return false;
            }
            trfRecNo = getLatestTrfNo(conRob, dataName);
            if (TextUtils.isEmpty(trfRecNo)) {
                objGlobal.setErrorMessage("Transfer Receipt number is wrong");
                objGlobal.setErrorNo("transferReceipt:007");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(":transferReceipt:exception:1: " + e.toString());
            return false;
        }
        try {
            conRob.setAutoCommit(false);
            conLoc.setAutoCommit(false);
            //insert transfer detail start *****************************************
            if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.transferdetail (trfno,itemcode,unitcode,quantity,rate,batchno,basicqty,basicrate,srno,upc," +
                    "ItemType) select '" + trfRecNo + "',itemcode,unitcode,qty,rate,'',qty,rate,(ROW_NUMBER() OVER(ORDER BY itemcode ASC)),itemcode,'' from robotics.dbo.tmpTransfer " +
                    "where DeviceName='" + objGlobal.getDeviceName() + "'", conRob)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:012");
                return false;
            }
            //insert into transferheader *****************************************
            if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.transferheader (TrfNo,TrfDate,CostCodeFrom,LocCodeFrom,CostCodeTo,LocCodeTo,Accode,Narration,NetAmount,UserId,TrfType,FCCode,FCRate," +
                    "ApprovedBy,PreparedBy,ConsumeReturn,JobNo,StoreIssue,StoreReceipt,EntryMode,ShipNo,CartonNo,PalletNo,Starttime) values ('" + trfRecNo + "','" + objGlobal.getServerDate() + "','" + costCodeFrom + "'," +
                    "'" + locCodeFrom + "','" + costCodeTo + "','" + locCodeTo + "','" + debitAc + "','" + narration + "'," + totalAmt + "," + objGlobal.getUserId() + ",'" + trfType + "','" + fcCode + "'," + fcRate + "," +
                    "'" + approvedBy + "','" + preparedBy + "','P',convert(varchar(15),getdate(),108),'" + storeIssue + "','" + objGlobal.getEmpName() + "','A','" + objGlobal.getDelDate() + "','" + cartonNo + "'," +
                    "'" + trfPalletNo + "','" + firstScanTime + "')", conRob)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:013");
                return false;
            }
            //Rfpair
            if (!dbConnection.insertUpdate("update " + dataName + ".dbo.rfpair set TrfNo='" + trfRecNo + "' where rfid in(select Rfid from bfldata.dbo.tmpRfidPdaTransferItems where " +
                    "DeviceName='" + objGlobal.getDeviceName() + "')", conLoc)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:014");
                return false;
            }
            //rfpairdetail
            if (!dbConnection.insertUpdate("update bfldata.dbo.RFPairDetail set TrfNo='" + trfRecNo + "',trfdate='" + objGlobal.getServerDate() + "',PairSn=0 where TrfNo='' and ShopName='" + shopName + "' and " +
                    "rfid in(select rfid from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "')", conLoc)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:015");
                return false;
            }
            //insert transfer no return************************************
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.TransferNoReturn (TrnDate,ShopName,TrfNo,RetNo,Quantity,UserId,Dept,Dataname,isImport,ImpDateTime,isCountUpdate,CountUpdateDateTime,TrnTime) " +
                    "values ('" + objGlobal.getServerDate() + "','" + shopName + "','" + trfRecNo + "',''," + totalQty + "," + objGlobal.getUserId() + ",'USA-APDA','" + dataName + "','N',null,'N',null,'" + objGlobal.getServerTime() + "')", conRob)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:016");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into robotics.dbo.SortTask values('" + objGlobal.getServerDate() + "','" + toteid + "','','','" + shopName + "'," +
                    "'" + trfRecNo + "'," + objGlobal.getUserId() + ",'','N','N','')", conRob)) {
                conRob.rollback();
                conLoc.rollback();
                conRob.setAutoCommit(true);
                conLoc.setAutoCommit(true);
                objGlobal.setErrorNo("transferReceipt:017");
                return false;
            }
            //insert for tmpPrintTransferRfidNew************************************
            if (objGlobal.getBluetoothDevicesAvailable().equals("N")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpPrintTransferRfidNew(ShopName,ToteId,TrfNo,Quantity,TrnDate,PrDateTime,pr,PrSystem,Whouse) values ('" + shopName + "','" + objGlobal.getUserName() + "'," +
                        "'" + trfRecNo + "'," + totalQty + ",'" + objGlobal.getServerDate() + "',null,'N','" + objGlobal.getUserPrinterName() + "','" + objGlobal.getWarehouse() + "')", conRob)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:018");
                    return false;
                }
            }
            //insert for CheckingTotesSummary************************************
            if (!objGlobal.getWorkLocation().equals("UAE")) {
                if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.CheckingTotesSummary(SNo,TrnDate,DataName,ToteID,ShopName,Qty,TrfReceiptNo,TrfIssueNo,TrfReceiptDateTime,BoxNoF,UserID) values " +
                        "((select max(SNo)+1 from " + dataName + ".dbo.CheckingTotesSummary),'" + objGlobal.getServerDate() + "','" + dataName + "','','" + shopName + "'," + totalQty + ",'" + trfRecNo + "',''," +
                        "cast(getdate() as smalldatetime),''," + objGlobal.getUserId() + ")", conLoc)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:019");
                    return false;
                }
            }
            if (trftype.equals("P")) {
                if (!dbConnection.insertUpdate("Insert into usa.dbo.ExportTransfer(Dep,DataName,PalletNo,TrfNo,TrfDate,TrfTime,PurInvNo,PurRetNo,PreparedBy,UserId,BoxNo,ShopName,CostCode) " +
                        "select 'USA','" + dataName + "',BoxNo,'" + trfRecNo + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','','','" + objGlobal.getUserName() + "'," +
                        "" + objGlobal.getUserId() + ",BoxNo,'" + shopName + "','" + costCodeTo + "' from usa.dbo.UPCBoxHead where BoxNo='" + palletBoxNo + "'", conLoc)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:020");
                    return false;
                }
                if(typeUsaTcm.equals("USABOX")) {
                    if (!dbConnection.insertUpdate("update usa.dbo.UPCBoxHead set Closed='Y' where boxno='" + palletBoxNo + "'", conLoc)) {
                        conRob.rollback();
                        conLoc.rollback();
                        conRob.setAutoCommit(true);
                        conLoc.setAutoCommit(true);
                        objGlobal.setErrorNo("transferReceipt:021");
                        return false;
                    }
                }
                if(typeUsaTcm.equals("TCMBOX")) {
                    if (!dbConnection.insertUpdate("update BFLDATA.dbo.TCMBoxes set Closed='Y' WHERE BoxNo='" + palletBoxNo + "'", conLoc)) {
                        conRob.rollback();
                        conLoc.rollback();
                        conRob.setAutoCommit(true);
                        conLoc.setAutoCommit(true);
                        objGlobal.setErrorNo("transferReceipt:021");
                        return false;
                    }
                }
                if(typeUsaTcm.equals("TCMPLT")) {
                    if (!dbConnection.insertUpdate("update BFLDATA.dbo.R1PalletHead set Closed='Y' WHERE PalletNo='" + palletBoxNo + "'", conLoc)) {
                        conRob.rollback();
                        conLoc.rollback();
                        conRob.setAutoCommit(true);
                        conLoc.setAutoCommit(true);
                        objGlobal.setErrorNo("transferReceipt:021");
                        return false;
                    }
                }
            }
            if (trftype.equals("P") || trftype.equals("T")) {
                if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.CloseR1pallet values('" + typeUsaTcm + "','" + palletBoxNo + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," +
                        "'" + objGlobal.getUserId() + "','" + objGlobal.getUserName() + "','','',0,0,0,'Auto Closed Transfer PDA (" + trfRecNo + ")')", conLoc)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:022");
                    return false;
                }
            }
            if (regSIMExclude.equals("Y")) {
                if (!dbConnection.insertUpdate("insert into " + dataName + ".dbo.Exclude_Transfers_Sim(Trfno,Trndate,Userid,Remarks) values ('" + trfRecNo + "'," +
                        "'" + objGlobal.getServerDate() + "'," + objGlobal.getUserId() + ",'SIM Exclude Transfer, Pallet Type("+objTransferGlobal.getBoxTrfBoxNoPalletType()+")')", conLoc)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:023");
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.specialsimtransferinclude(Trndate,TrfNo,BoxNo,PalletType,UserName) values (getdate(),'" + trfRecNo + "'," +
                        "'" + palletBoxNo + "','" + objTransferGlobal.getBoxTrfBoxNoPalletType() + "','" + objGlobal.getUserName() + "')", conLoc)) {
                    conRob.rollback();
                    conLoc.rollback();
                    conRob.setAutoCommit(true);
                    conLoc.setAutoCommit(true);
                    objGlobal.setErrorNo("transferReceipt:024");
                    return false;
                }
            }
            conRob.commit();
            conLoc.commit();
            conRob.setAutoCommit(true);
            conLoc.setAutoCommit(true);
            objTransferGlobal.setTrfRecNo(trfRecNo);
            return true;
        } catch (Exception exception) {
            try {
                conRob.rollback();
                conLoc.rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage(objGlobal.getErrorNo()+":transferReceipt:sqlException:2: " + sqlException);
                return false;
            }
            objGlobal.setErrorMessage(objGlobal.getErrorNo()+":transferReceipt:exception:3: " + exception);
            return false;
        }
    }

    private String getCartonNo(Connection conRob, String dataName, String delDate, String costCodeTo, String locCodeTo) {
        String str = "1";
        try {
            rs = dbConnection.getResultSet("select cartonno=isnull(max(cartonno),0)+1 from " + dataName + ".dbo.transferheader where trfdate='" + delDate + "' and " +
                    "costcodeto='" + costCodeTo + "' and loccodeto='" + locCodeTo + "'", conRob);
            if (rs.next()) {
                str = rs.getString("cartonno");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferReceipt:getCartonNo:" + ex);
            return "";
        }
        return str;
    }

    private String getLatestTrfNo(Connection conRob, String dataName) {
        int autoSn = 0;
        String trfPrefix = "";
        try {
            trfPrefix = objGlobal.getTransferPrefixPda();
            rs = dbConnection.getResultSet("select TransferPrefixPda from BFLDATA.dbo.TransferPrefix where warehouse='" + objGlobal.getWarehouse() + "' and dataname='" + dataName + "'", conRob);
            if (rs.next()) {
                trfPrefix = rs.getString("TransferPrefixPda");
            }
            rs = dbConnection.getResultSet("select en=isnull(max(cast(right(trfno,7) as int)),0)+1 from " + dataName + ".dbo.transferheader where " +
                    "left(trfno,2)='" + trfPrefix + "' and (trftype='R') and substring(trfno,3,1)<>'D'", conRob);
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en"));
            }
            return trfPrefix + String.format("%07d", autoSn);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferReceipt:getLatestTrfNo:" + ex);
            return "";
        }
    }
}