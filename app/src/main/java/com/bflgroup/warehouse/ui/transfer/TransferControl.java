package com.bflgroup.warehouse.ui.transfer;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.building.jafza.BoxBuildingAutoJafzaControl;
import com.bflgroup.warehouse.ui.chutestatusinout.jafza.TransferReceiptJafza;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransferControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private TransferGlobal objTransferGlobal = TransferGlobal.getInstance();
    private TransferReceiptJafza objTransferReceiptJafza = new TransferReceiptJafza();
    private BoxBuildingAutoJafzaControl objBoxBuildingAutoJafzaControl = new BoxBuildingAutoJafzaControl();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    private Connection tmpCon = null;

    public TransferControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("TransferControl : Connection error");
        }
    }

    boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("TransferControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    boolean checkTmpConnection() {
        objGlobal.setErrorMessage("");
        tmpCon = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(), "BFLDATA");
        if (tmpCon == null) {
            objGlobal.setErrorMessage("TransferControl:checkTmpConnection : Connection error TMP");
            return false;
        }
        return true;
    }

    public ArrayList<String> loadShops(String shopType) {
        ArrayList<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            if (shopType.equals("E")) {
                rs = dbConnection.getResultSet("select ShopName from bfldata.dbo.DataSettings where Dataname<>'' and ExportActive='Y' order by ShopName", objGlobal.getConnection());
            } else if (shopType.equals("D")) {
                rs = dbConnection.getResultSet("select ShopName=Result from bfldata.dbo.robodcresult order by ShopName", objGlobal.getConnection());
            } else if (shopType.equals("T")) {
                rs = dbConnection.getResultSet("select ShopName from bfldata.dbo.DataSettings where Concept='P2EXP' order by ShopName", objGlobal.getConnection());
            } else {
                rs = dbConnection.getResultSet("select ShopName from bfldata.dbo.DataSettings where Dataname<>'' order by ShopName", objGlobal.getConnection());
            }
            while (rs.next()) {
                arr.add(rs.getString("ShopName"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.loadShops: " + e);
            return null;
        }
    }

    public boolean validateTransfer(String scanType, String selShop) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and qty<=0", objGlobal.getConnection())) {
                return false;
            }
            boolean rcFound = false;
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                rcFound = true;
                if (scanType.equals("R")) //RFID and BARCODE
                    if (rs.getString("rfid").isEmpty()) {
                        if (!validateBarcode(true, rs.getString("barcode"), rs.getInt("qty"), selShop))
                            return false;
                    } else {
                        if (!validateRfid(true, rs.getString("rfid"), rs.getInt("qty"), selShop))
                            return false;
                    }
                if (scanType.equals("I"))
                    if (!validateItemcode(true, rs.getString("itemcode"), rs.getInt("qty"), selShop, scanType))
                        return false;
                if (scanType.equals("D")) //add validation if needed
                    return true;
                if (scanType.equals("P"))
                    return true;
                if (scanType.equals("T"))
                    return true;
            }
            if (!rcFound) {
                objGlobal.setErrorMessage("Records not found for Transfer");
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateTransfer : " + e);
            return false;
        }
    }

    public boolean deleteScan(String rfid) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (rfid.isEmpty()) {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and rfid='" + rfid + "'", objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateRfid : " + e);
            return false;
        }
    }

    public boolean reprintTransferShopName(String scan, String shopname) {
        Connection conRob = null;
        try {
            objTransferGlobal.setReprintTrfno("");
            objTransferGlobal.setReprintShop("");
            objTransferGlobal.setReprintToteid("");
            conRob = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(), "BFLDATA");
            if (conRob == null) {
                objGlobal.setErrorMessage("Connection error");
                return false;
            }
            if (shopname.equals("")) {
                rs = dbConnection.getResultSet("select top 1 ShopName,TrfNo,ToteId from ROBOTICS.dbo.SortTask where (ToteId='" + scan + "' or TrfNo='" + scan + "') order by trndate desc", conRob);
            } else {
                rs = dbConnection.getResultSet("select top 1 ShopName,TrfNo,ToteId from ROBOTICS.dbo.SortTask where ShopName='" + shopname + "' and (ToteId='" + scan + "' or " +
                        "TrfNo='" + scan + "') order by trndate desc", conRob);
            }
            if (rs.next()) {
                objTransferGlobal.setReprintTrfno(rs.getString("TrfNo"));
                objTransferGlobal.setReprintShop(rs.getString("ShopName"));
                objTransferGlobal.setReprintToteid(rs.getString("ToteId"));
            } else {
                objGlobal.setErrorMessage("Record not found");
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateRfid : " + e);
            return false;
        }
    }

    public boolean forPrint(String shopName, String trfno) {
        String dataname = "";
        Connection conRob = null;
        objTransferGlobal.setPtrfno("");
        objTransferGlobal.setPboxno("");
        objTransferGlobal.setPshopname("");
        objTransferGlobal.setPqty("");
        objTransferGlobal.setPdeldate("");
        objTransferGlobal.setPtrfdate("");
        objTransferGlobal.setPtoteid("");
        objTransferGlobal.setPremarks("");
        objTransferGlobal.setPpreparedby("");
        conRob = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(), "BFLDATA");
        if (conRob == null) {
            objGlobal.setErrorMessage("Connection error");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select dataname from bfldata.dbo.datasettings where ShopName='" + shopName + "'", conRob);
            if (rs.next()) {
                dataname = rs.getString("dataname");
            }
            if (dataname.equals("")) {
                rs = dbConnection.getResultSet("select * from BFLDATA.dbo.DataSettings where ShopName in(select MainShop from BFLDATA.dbo.ShopinShop where SubShop='" + shopName + "')", conRob);
                if (rs.next()) {
                    dataname = rs.getString("dataname");
                }
            }
            rs = dbConnection.getResultSet("select TrfNo,Cartonno,Shipno,TrfDate=convert(varchar,TrfDate,103),StoreIssue,Narration,PreparedBy,qty=(select FORMAT(SUM(Quantity),'#####') " +
                    "from " + dataname + ".dbo.TransferDetail where TrfNo=a.trfno) from " + dataname + ".dbo.transferheader a where (trfno='" + trfno + "' or StoreIssue='" + trfno + "' )", conRob);
            if (rs.next()) {
                objTransferGlobal.setPtrfno(rs.getString("TrfNo"));
                objTransferGlobal.setPboxno(rs.getString("Cartonno"));
                objTransferGlobal.setPshopname(shopName);
                objTransferGlobal.setPqty(rs.getString("qty"));
                objTransferGlobal.setPdeldate(rs.getString("Shipno"));
                objTransferGlobal.setPtrfdate(rs.getString("TrfDate"));
                objTransferGlobal.setPtoteid(rs.getString("StoreIssue"));
                objTransferGlobal.setPremarks(rs.getString("Narration"));
                objTransferGlobal.setPpreparedby(rs.getString("PreparedBy"));

                StringBuilder trfItemList = new StringBuilder();

                ResultSet rs1 = dbConnection.getResultSet("select itemcode from " + dataname + ".dbo.transferdetail where trfno = '" + rs.getString("TrfNo") + "'", conRob);
                while (rs1.next()) {
                    if (trfItemList.length() > 0) {
                        trfItemList.append(",");
                    }
                    trfItemList.append("'").append(rs1.getString("itemcode")).append("'");
                }
                String itemType = null;
                int count = 0;
                rs1 = dbConnection.getResultSet("select ItemType from usa.dbo.upcbarcodes where itemcode in (" + trfItemList + ")",objGlobal.getConnection());
                while(rs1.next()){
                    itemType = rs1.getString("itemtype");
                    count++;

                    if (count > 1) {
                        itemType = "Mix";
                        break;
                    }
                }
                if (itemType == null)
                    itemType = "";
                objTransferGlobal.setpSeason(itemType);
            } else {
                objGlobal.setErrorMessage("TransferControl : Invalid transfer number or toteid (" + trfno + ")");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferControl:forPrint:" + ex);
            return false;
        }
    }

    ArrayList<TransferScannedItems> loadScannedItems() {
        if (!checkConnection()) {
            return null;
        }
        int totCnt = 0;
        ArrayList<TransferScannedItems> listPalletStatusTicket = new ArrayList<TransferScannedItems>();
        try {
            listPalletStatusTicket.clear();
            rs = dbConnection.getResultSet("select itemcode,Description=isnull(Description,''),qty=isnull(sum(qty),0) from bfldata.dbo.tmpRfidPdaTransferItems where " +
                    "DeviceName='" + objGlobal.getDeviceName() + "' group by itemcode,Description", objGlobal.getConnection());
            while (rs.next()) {
                listPalletStatusTicket.add(new TransferScannedItems(rs.getString("itemcode").toString(), rs.getString("Description").toString(), rs.getInt("qty")));
                totCnt = totCnt + (rs.getInt("qty"));
            }
            objTransferGlobal.setTotalScan(totCnt);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferControl:loadScannedItems:" + ex);
            return null;
        }
        return listPalletStatusTicket;
    }

    public boolean validateRfid(boolean valid, String rfid, int qty, String selShop) {
        String description = "", shopName = "", itemCode = "", trfNo = "", trfDate = "", barcode = "", size = "";
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.RFIDMaster where rfid='" + rfid + "' and supplier<>''", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("RFID not found in the master, " + rfid);
                return false;
            }
            rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and ShopName<>'" + selShop + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getInt("cnt") > 0) {
                    objGlobal.setErrorMessage("Different shop not allowed");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select top 1 *,descr=(select Description from hodata.dbo.itemmaster where itemcode=a.itemcode) from BFLDATA.dbo.RFPairDetail a where " +
                    "rfid='" + rfid + "' order by entrydate desc,trntime desc", objGlobal.getConnection());
            if (rs.next()) {
                shopName = rs.getString("shopname");
                itemCode = rs.getString("ItemCode");
                barcode = rs.getString("Barcode");
                trfNo = rs.getString("TrfNo");
                trfDate = rs.getString("entrydate");
                description = rs.getString("descr");
            }

            if (shopName.isEmpty() || itemCode.isEmpty() || barcode.isEmpty() || trfDate.isEmpty() || description.isEmpty()) {
                objGlobal.setErrorMessage("Pair information is not found, rfid:" + rfid);
                return false;
            }
            if (!trfNo.isEmpty()) {
                objGlobal.setErrorMessage("RFID: " + rfid + " is already paired with " + itemCode + " and transfered - " + shopName + " " + trfNo + " on " + trfDate);
                return false;
            }
            if (!selShop.equals(shopName) && !selShop.isEmpty()) {
                objGlobal.setErrorMessage("Selected shop is (" + selShop + ") not match with paired shop (" + shopName + ")");
                return false;
            }
            if (!valid) {
                rs = dbConnection.getResultSet("select * from tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and rfid='" + rfid + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("RFID Already scan, " + rfid);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Description,Qty,ScanTime) values('" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','" + rfid + "','" + barcode + "','" + itemCode + "','" + description + "'," + qty + ",convert(varchar,getdate(),8))", objGlobal.getConnection())) {
                    return false;
                }
                objTransferGlobal.setScanBarcode(barcode);
            }
            objTransferGlobal.setShopName(shopName);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateRfid : " + e);
            return false;
        }
    }

    public boolean validateRoboDirectCheckingResult(boolean valid, String contno, String itemcode, int qty, String selShop) {
        String description = "", shopName = "", itemCode = "", trfNo = "", trfDate = "", rfid = "", barcode = "", groupcode = "", itemtype = "", department = "", division = "", finalresult = "";
        String robResultId = "", printFlag = "", excess = "";
        int rwupdate = 0;
        boolean found = false, allowExcess = false;
        if (!checkTmpConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and ShopName<>'" + selShop + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getInt("cnt") > 0) {
                    objGlobal.setErrorMessage("Different shop not allowed");
                    return false;
                }
            }
            if (!selShop.isEmpty()) {
                if (selShop.equals("SHOP")) allowExcess = true;
            }
            if (selShop.isEmpty()) {
                rs = dbConnection.getResultSet("select top 1 IdNo,PrintFlag,shopname=Result,ItemCode,TrfNo=RefNo,Barcode,TrnDate,descr=Itemname,excess from online.dbo.PhotoCheckingResult " +
                        "where ContNo='" + contno + "' and UPC='" + itemcode + "' and RStatus='N' order by TrnDate,Time1", tmpCon);
                if (rs.next()) {
                    found = true;
                }
            } else {
                rs = dbConnection.getResultSet("select top 1 IdNo,PrintFlag,shopname=Result,ItemCode,TrfNo=RefNo,Barcode,TrnDate,descr=Itemname,excess from online.dbo.PhotoCheckingResult " +
                        "where result='" + selShop + "' and ContNo='" + contno + "' and UPC='" + itemcode + "' and RStatus='N' order by TrnDate,Time1", tmpCon);
                if (rs.next()) {
                    found = true;
                }
            }
            if (!found) {
                if (allowExcess) {
                    if (dbConnection.insertUpdateInt("insert into online.dbo.PhotoCheckingResult(ContNo,TrnDate,Time1,UPC,Itemcode,GroupCode,Season,Department,Division,Result,FinalResult,ResultType,Qty," +
                            "QtyIssue,OrPrice,PrintFlag,RfidFlag,Company,ShopCode,Itemname,Barcode,SalesPrice,RefNo,Mark,Uid,RStatus,Excess,TcmContno) select top 1 ContNo,convert(varchar,getdate(),103)," +
                            "convert(varchar,getdate(),8),UPC,Itemcode,GroupCode,Season,Department,Division,'SHOP',FinalResult=(case when Season='W' then 'RW' else 'R1' end)," +
                            "ResultType=(case when Season='W' then 'RW' else 'R1' end),1,0,0,'','','','',Itemname,Itemcode,0,'','',0,'N','Y',TcmContno from online.dbo.PhotoCheckingResult where " +
                            "ContNo='" + contno + "' and UPC='" + itemcode + "'", tmpCon) <= 0) {
                        rs = dbConnection.getResultSet("select top 1 ItemCode,Description,GroupCode,itemtype='' from HODATA.dbo.ItemMaster where ItemCode='" + itemcode + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 ItemCode,Description=ItemName,GroupCode,itemtype=isnull(itemtype,'') from USA.dbo.UPCBarCodes where upc='" + itemcode + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                rs = dbConnection.getResultSet("select top 1 ItemCode,Description=englishname,GroupCode='',itemtype=isnull(itemtype,'') from abudata.dbo.itemtypelib where itemcode='" + itemcode + "'", objGlobal.getConnection());
                            }
                        }
                        if (rs.next()) {
                            groupcode = rs.getString("groupcode");
                            itemtype = rs.getString("itemtype");
                            if (itemtype.isEmpty()) itemtype = "S";
                            rs = dbConnection.getResultSet("select Grpname,Department,Division,BuildingCategory from usa.dbo.USAPriority where GroupCode='" + groupcode + "'", objGlobal.getConnection());
                            if (rs.next()) {
                                department = rs.getString("department");
                                division = rs.getString("division");
                            }
                            finalresult = "ER";
                            if (itemtype.equals("W")) finalresult = "EW";
                            if (!dbConnection.insertUpdate("insert into online.dbo.PhotoCheckingResult (ContNo,TrnDate,Time1,UPC,Itemcode,GroupCode,Season,Department,Division,Result,FinalResult,ResultType," +
                                    "Qty,QtyIssue,OrPrice,PrintFlag,RfidFlag,Company,ShopCode,Itemname,Barcode,SalesPrice,RefNo,Mark,Uid,RStatus,Excess,TcmContno) values ('" + contno + "'," +
                                    "convert(varchar,getdate(),103),convert(varchar,getdate(),8),'" + itemcode + "','" + itemcode + "','" + groupcode + "','" + itemtype + "','" + department + "'," +
                                    "'" + division + "','SHOP','" + finalresult + "','" + finalresult + "',1,0,0,'','','','','','" + itemcode + "',0,'','',0,'N','Y','')", objGlobal.getConnection())) {
                                return false;
                            }
                        }
                    }
                    rs = dbConnection.getResultSet("select top 1 IdNo,PrintFlag,shopname=Result,ItemCode,TrfNo=RefNo,Barcode,TrnDate,descr=Itemname,excess from online.dbo.PhotoCheckingResult " +
                            "where result='" + selShop + "' and ContNo='" + contno + "' and UPC='" + itemcode + "' and RStatus='N' order by TrnDate,Time1", tmpCon);
                    if (rs.next()) {
                        found = true;
                    }
                }
            }
            if (!found) {
                objGlobal.setErrorMessage("Invalid UPC or Result not found (" + contno + "," + itemcode + ")");
                return false;
            }
            printFlag = rs.getString("printFlag");
            robResultId = rs.getString("IdNo");
            shopName = rs.getString("shopname");
            itemCode = rs.getString("ItemCode");
            trfNo = rs.getString("TrfNo");
            barcode = rs.getString("Barcode");
            trfDate = rs.getString("TrnDate");
            description = rs.getString("descr");
            excess = rs.getString("excess");
            if (shopName.isEmpty() || itemCode.isEmpty() || trfDate.isEmpty() || barcode.isEmpty()) {
                objGlobal.setErrorMessage("(shopName(" + shopName + "), itemCode(" + itemCode + "), trfDate(" + trfDate + "), description(" + description + "), " +
                        "barcode(" + barcode + ")) information is not found, Itemcode:" + itemcode);
                return false;
            }
            if (!selShop.equals(shopName) && !selShop.isEmpty()) {
                objGlobal.setErrorMessage("Selected shop is (" + selShop + ") not match with paired shop (" + shopName + ")");
                return false;
            }
            if (!valid) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Description,Qty,ScanTime,RoboDcContNo,RoboDcResultId) values('" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','" + rfid + "','" + barcode + "','" + itemCode + "','" + description + "'," + qty + ",convert(varchar,getdate(),8),'" + contno + "','" + robResultId + "')", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update online.dbo.PhotoCheckingResult set QtyIssue=QtyIssue+1,RStatus='Y',RDateTime=getdate() where idno=" + robResultId, tmpCon)) {
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into robotics.dbo.directContPdaCheckLog values ('" + robResultId + "','" + itemcode + "','" + shopName + "',GETDATE()," +
                        "'" + objGlobal.getDeviceName() + "','" + objGlobal.getUserName() + "','" + excess + "')", tmpCon)) {
                    return false;
                }
                if (printFlag.equals("Y")) {
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.RFIDPBarcodeLog(ShopName,TrfNo,ItemCode,Barcode,Label,SystemName,CheckedBy,TrnDate,TrnTime,UserId,upc,RePrint," +
                            "PairStation,RFID,RFIDType,SystemNameRFID,RFRePrint) values ('" + robResultId + "','" + trfNo + "','" + itemcode + "','" + barcode + "','','" + objGlobal.getDeviceName() + "'," +
                            "'" + objGlobal.getUserName() + "',convert(varchar,getdate(),103),convert(varchar,getdate(),8),0,'" + itemcode + "','N','" + objGlobal.getDeviceName() + "','','','','')", tmpCon)) {
                        return false;
                    }
                }
                objTransferGlobal.setScanBarcode(barcode);
            }
            objTransferGlobal.setShopName(shopName);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateBarcode : " + e);
            return false;
        }
    }

    public boolean createTransfer(String shopName, String toteid) {
        String shopId = "", mainshop = "";
        if (!checkTmpConnection()) {
            return false;
        }
        try {
            objTransferGlobal.setRobooDcBuild("");
            rs = dbConnection.getResultSet("select Build from bfldata.dbo.robodcresult where Result='" + shopName + "'", objGlobal.getConnection());
            if (rs.next()) {
                objTransferGlobal.setRobooDcBuild(rs.getString("Build"));
            } else {
                objGlobal.setErrorMessage("Invalid shop(1) (" + shopName + ")");
                return false;
            }
            rs = dbConnection.getResultSet("select RoboShopId,shopname from BFLDATA.dbo.DataSettings where ShopName in(select SPalletType from BFLDATA.dbo.BuildingProcess_Settings " +
                    "where ResultType='" + shopName + "')", tmpCon);
            if (rs.next()) {
                mainshop = rs.getString("shopname");
                shopId = rs.getString("RoboShopId");
            } else {
                objGlobal.setErrorMessage("Invalid shop(2) (" + shopName + ")");
                return false;
            }
            if (objTransferGlobal.getRobooDcBuild().isEmpty() || shopId.isEmpty()) {
                objGlobal.setErrorMessage("build/shopId is empty");
                return false;
            }
            if (objTransferGlobal.getRobooDcBuild().equals("Y")) {
                return objBoxBuildingAutoJafzaControl.saveChuteBuilding(objGlobal.getDeviceName(), toteid, shopId, mainshop);
            } else {
                return objTransferReceiptJafza.transferReceipt(objGlobal.getDeviceName(), toteid, shopId, mainshop,"");
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.saveChuteOut : " + e);
            return false;
        }
    }

    public boolean transferCreatePairingSorting() {
        Connection conRob = null;
        Connection conLoc = null;
        conLoc = objGlobal.getConnection();
        conRob = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(), "BFLDATA");
        if (conRob == null || conLoc == null) {
            objGlobal.setErrorMessage("transferReceipt:transferCreatePairingSorting:Connection error");
            return false;
        }
        try {
            conRob.setAutoCommit(false);
            rs = dbConnection.getResultSet("select RoboDcContNo,RoboDcResultId from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "'", conLoc);
            while (rs.next()) {
                if (!dbConnection.insertUpdate("insert into ROBOTICS.dbo.PairDetail select FinalResult,'',Itemcode,convert(varchar,getdate(),103),Barcode,'N',convert(varchar,getdate(),8),'N'," +
                        "0,'',0,0,'PDA-RDC',0,'" + objGlobal.getUserName() + "',null,'" + objGlobal.getDeviceName() + "',idno,TcmContno,ContNo from online.dbo.PhotoCheckingResult " +
                        "where ContNo='" + rs.getString("RoboDcContNo") + "' and IdNo=" + rs.getString("RoboDcResultId"), conRob)) {
                    conRob.rollback();
                    objGlobal.setErrorNo("transferReceipt:transferCreatePairingSorting");
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into ROBOTICS.dbo.PairingConformationDetail(ChuteId,TotId,ShopId,Itemcode,Barcode,Rfid,Qty,StationId,TrnDate,TrnTime,ContNo,BoxNo," +
                        "resultid,username) select '" + objGlobal.getDeviceName() + "','',(select RoboShopId from BFLDATA.dbo.DataSettings where ShopName=a.FinalResult),Itemcode,Barcode,'',Qty,'PDA-RDC',convert(varchar,getdate(),103)," +
                        "convert(varchar,getdate(),8),tcmContNo,ContNo,IdNo,'" + objGlobal.getUserName() + "' from online.dbo.PhotoCheckingResult a WHERE ContNo='" + rs.getString("RoboDcContNo") + "' and " +
                        "IdNo=" + rs.getString("RoboDcResultId"), conRob)) {
                    conRob.rollback();
                    objGlobal.setErrorNo("transferReceipt:transferCreatePairingSorting");
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into ROBOTICS.dbo.SortingConformationDetail(SN,ChuiteId,ShopId,Itemcode,Barcode,Rfid,Qty,RefNo,TrnDate,Result,Message,TrnTime," +
                        "TotId,TransferNo,BflTote,ContNo,RoboId,ResultId) select 0,'" + objGlobal.getDeviceName() + "',(select RoboShopId from BFLDATA.dbo.DataSettings " +
                        "where ShopName=a.FinalResult),Itemcode,Barcode,'',Qty,RefNo,convert(varchar,getdate(),103),0,'',convert(varchar,getdate(),8),'PDA-RDC','',tcmContNo,ContNo,0,IdNo from " +
                        "online.dbo.PhotoCheckingResult a where ContNo='" + rs.getString("RoboDcContNo") + "' and IdNo=" + rs.getString("RoboDcResultId"), conRob)) {
                    conRob.rollback();
                    objGlobal.setErrorNo("transferReceipt:transferCreatePairingSorting");
                    return false;
                }
            }
            conRob.commit();
            conRob.setAutoCommit(true);
            return true;
        } catch (Exception exception) {
            try {
                conRob.rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("transferReceipt:transferCreatePairingSorting:sqlException:2: " + sqlException);
                return false;
            }
            objGlobal.setErrorMessage(":transferReceipt:transferCreatePairingSorting:exception:3: " + exception);
            return false;
        }
    }

    public boolean validateItemcode(boolean valid, String itemcode, int qty, String selShop, String scanType) {
        String description = "", shopName = "", itemCode = "", trfNo = "", trfDate = "", rfid = "", barcode = "", size = "";
        if (!checkConnection()) {
            return false;
        }
        String[] parts;
        String part1;
        int i;
        if (itemcode.contains("/")) {
            parts = itemcode.split("/");
            part1 = parts[0];
        } else {
            part1 = itemcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        itemcode = part1.substring(i);
        try {
            rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and ShopName<>'" + selShop + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getInt("cnt") > 0) {
                    objGlobal.setErrorMessage("Different shop not allowed");
                    return false;
                }
            }
            if (scanType.equals("I")) {
                rs = dbConnection.getResultSet("select top 1 *,size = isnull((select top 1 isnull(size1,'') from usa..UPCBarCodes where a.itemcode = itemcode and size1 in ('S','XS','XXS','XXXS')),'') from HODATA.dbo.itemMaster a where " +
                        "itemcode='" + itemcode + "' ", objGlobal.getConnection());
                if (rs.next()) {
                    shopName = selShop;
                    itemCode = rs.getString("ItemCode");
                    // trfNo = rs.getString("TrfNo");
                    barcode = rs.getString("ItemCode");
                    //trfDate = rs.getString("TrnDate");
                    description = rs.getString("Description");
                    size = rs.getString("size");
                }
                if (itemcode.isEmpty() || description.isEmpty()) {
                    rs = dbConnection.getResultSet("select top 1 *,size = isnull((select top 1 isnull(size1,'') from usa..UPCBarCodes where a.itemcode = itemcode and size1 in ('S','XS','XXS','XXXS')),'') from usa..UPCBarCodes a, HODATA..ItemMaster b where " +
                            " (upc='" + itemcode + "' or a.itemcode = '" + itemcode + "') and a.itemcode = b.itemcode ", objGlobal.getConnection());
                    if (rs.next()) {
                        shopName = selShop;
                        itemCode = rs.getString("ItemCode");
                        // trfNo = rs.getString("TrfNo");
                        barcode = rs.getString("ItemCode");
                        //trfDate = rs.getString("TrnDate");
                        description = rs.getString("Description");
                        size = rs.getString("size");
                    }
                }
                if (selShop.equals("P2KSA")) {
                    if (size.toUpperCase().equals("S") || size.toUpperCase().equals("XS") || size.toUpperCase().equals("XXS") || size.toUpperCase().equals("XXXS")) {
                        objGlobal.setErrorMessage("Size of item should not be S/XS/XXS/XXXS for '" + selShop + "' , Itemcode :" + itemcode + " Kindly Build for P2MYS");
                        return false;
                    }
                }
                if (shopName.isEmpty() || itemCode.isEmpty() || description.isEmpty() || barcode.isEmpty()) {
                    objGlobal.setErrorMessage("Itemcode information is not found (I), Itemcode:" + itemcode);
                    return false;
                }
                if (!selShop.equals(shopName) && !selShop.isEmpty()) {
                    objGlobal.setErrorMessage("Selected shop is (" + selShop + ") not match with paired shop (" + shopName + ")");
                    return false;
                }
                if (!valid) {
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Description,Qty,ScanTime) values('" + objGlobal.getDeviceName() + "'," +
                            "'" + shopName + "','" + rfid + "','" + barcode + "','" + itemCode + "','" + description + "'," + qty + ",convert(varchar,getdate(),8))", objGlobal.getConnection())) {
                        return false;
                    }
                    objTransferGlobal.setScanBarcode(barcode);
                }
            }
            objTransferGlobal.setShopName(shopName);
            return true;

        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateBarcode : " + e);
            return false;
        }
    }

    public boolean validateBarcode(boolean valid, String barcode, int qty, String selShop) {
        String description = "", shopName = "", itemCode = "", trfNo = "", trfDate = "", rfid = "";
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!barcode.contains("/")) {
                objGlobal.setErrorMessage("Barcode is not correct format, " + barcode);
                return false;
            }
            rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and ShopName<>'" + selShop + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getInt("cnt") > 0) {
                    objGlobal.setErrorMessage("Different shop not allowed");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select top 1 *,descr=(select Description from hodata.dbo.itemmaster where itemcode=a.itemcode) from BFLDATA.dbo.RFIDPBarcodeLog a where " +
                    "barcode='" + barcode + "' order by TrnDate desc,trntime desc", objGlobal.getConnection());
            if (rs.next()) {
                shopName = rs.getString("shopname");
                itemCode = rs.getString("ItemCode");
                trfNo = rs.getString("TrfNo");
                trfDate = rs.getString("TrnDate");
                description = rs.getString("descr");
            }
            if (shopName.isEmpty() || itemCode.isEmpty() || trfDate.isEmpty() || description.isEmpty()) {
                objGlobal.setErrorMessage("Barcode information is not found (B), barcode:" + barcode);
                return false;
            }
            if (!selShop.equals(shopName) && !selShop.isEmpty()) {
                objGlobal.setErrorMessage("Selected shop is (" + selShop + ") not match with paired shop (" + shopName + ")");
                return false;
            }
            if (!valid) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Description,Qty,ScanTime) values('" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','" + rfid + "','" + barcode + "','" + itemCode + "','" + description + "'," + qty + ",convert(varchar,getdate(),8))", objGlobal.getConnection())) {
                    return false;
                }
                objTransferGlobal.setScanBarcode(barcode);
            }
            objTransferGlobal.setShopName(shopName);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateBarcode : " + e);
            return false;
        }
    }

    public boolean validateBoxPallet(String scan) {
        String shopName = "", boxOrPalletNo = "", contno = "", pallettype = "", typeUsaTcm = "", lpmDt = "";
        if (!checkConnection()) {
            return false;
        }
        try {
            objTransferGlobal.setShopName("");
            objTransferGlobal.setBoxTrfBoxNo("");
            objTransferGlobal.setRegSIMExclude("");
            objTransferGlobal.setBoxTrfBoxNoPalletType("");
            objTransferGlobal.setLpmDt("");
            rs = dbConnection.getResultSet("SELECT DISTINCT BoxNo,PalletType,contno=CASE WHEN RoboContno <> '' THEN RoboContno WHEN CHARINDEX('-', BoxNo) > 0 AND LEN(LEFT(BoxNo, " +
                    "CHARINDEX('-', BoxNo) - 1)) >= 4 THEN LEFT(BoxNo, CHARINDEX('-', BoxNo) - 1) ELSE '' END,LPMDt=isnull(convert(varchar,LPMDt,103),'') FROM usa.dbo.vUPCBoxDet WHERE (BoxNo = '" + scan + "' OR ToteID = '" + scan + "') AND Closed = 'N'", objGlobal.getConnection());
            while (rs.next()) {
                typeUsaTcm = "USABOX";
                boxOrPalletNo = rs.getString("boxno");
                contno = rs.getString("contno");
                pallettype = rs.getString("PalletType");
                lpmDt = rs.getString("LPMDt");
                if (!contno.isEmpty()) {
                    rs = dbConnection.getResultSet("select top 1 * from usa.dbo.usapurchase where contno='" + contno + "' or BOLNO= '" + contno + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select top 1 * from usa.dbo.usapurchase where contno='KN" + contno + "' or contno='W" + contno + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            objGlobal.setErrorMessage("Please Purchase the container (" + contno + ") to proceed");
                            return false;
                        }
                    }
                }
            }
            if (typeUsaTcm.isEmpty()) {
                rs = dbConnection.getResultSet("select distinct BoxNo,PalletType,contno='' from bfldata.dbo.vR1Pallet where (BoxNo='" + scan + "' or TotId='" + scan + "') and Closed='N'", objGlobal.getConnection());
                while (rs.next()) {
                    typeUsaTcm = "TCMBOX";
                    boxOrPalletNo = rs.getString("boxno");
                    contno = rs.getString("contno");
                    pallettype = rs.getString("PalletType");
                }
            }
            if (typeUsaTcm.isEmpty()) {
                rs = dbConnection.getResultSet("select distinct BoxNo=palletno,PalletType,contno='' from bfldata.dbo.vR1Pallet where palletno='" + scan + "' and Closed='N'", objGlobal.getConnection());
                while (rs.next()) {
                    typeUsaTcm = "TCMPLT";
                    boxOrPalletNo = rs.getString("boxno");
                    contno = rs.getString("contno");
                    pallettype = rs.getString("PalletType");
                }
            }
            if (boxOrPalletNo.isEmpty()) {
                objGlobal.setErrorMessage("TransferControl.validateBoxPallet : Box No / Tote Id is not valid or Box is closed");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from USA.dbo.ExportTransfer where Palletno='" + boxOrPalletNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Transfer already done, " + boxOrPalletNo);
                return false;
            }
            rs = dbConnection.getResultSet("SELECT ShopName=(CASE WHEN CHARINDEX('-', TypeName) > 0 THEN LEFT(TypeName, CHARINDEX('-', TypeName) - 1) ELSE TypeName END),RegSIMExclude FROM " +
                    "BFLDATA.dbo.PalletType where PalletType='" + pallettype + "'", objGlobal.getConnection());
            if (rs.next()) {
                shopName = rs.getString("ShopName");
                objTransferGlobal.setRegSIMExclude(rs.getString("RegSIMExclude"));
                objTransferGlobal.setBoxTrfBoxNoPalletType(pallettype);
            } else {
                objGlobal.setErrorMessage("Pallet Type / Shopname not found from box(" + boxOrPalletNo + ")");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 Shopname from BFLDATA.dbo.DataSettings where ShopName='" + shopName + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Shopname is not valid, " + shopName);
                return false;
            }
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (typeUsaTcm.equals("USABOX")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Qty,ScanTime,HoQty) select '" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','',Itemcode,itemcode,sum(qty),convert(varchar,getdate(),8),0 from usa.dbo.vupcboxdet where boxno='" + boxOrPalletNo + "' and closed='N' group by Itemcode", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (typeUsaTcm.equals("TCMBOX")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Qty,ScanTime,HoQty) select '" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','',Itemcode,itemcode,sum(qty),convert(varchar,getdate(),8),0 from bfldata.dbo.vR1Pallet where boxno='" + boxOrPalletNo + "' and closed='N' group by Itemcode", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (typeUsaTcm.equals("TCMPLT")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Qty,ScanTime,HoQty) select '" + objGlobal.getDeviceName() + "'," +
                        "'" + shopName + "','',Itemcode,itemcode,sum(qty),convert(varchar,getdate(),8),0 from bfldata.dbo.vR1Pallet where palletno='" + boxOrPalletNo + "' and closed='N' group by Itemcode", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (objGlobal.getWorkLocation().equals("UAE")) {
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set HoQty=b.quantity from bfldata.dbo.tmpRfidPdaTransferItems a,hodata.dbo.locstock b where " +
                        "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode and b.costcode='001' and loccode='01'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set HoQty=b.quantity from bfldata.dbo.tmpRfidPdaTransferItems a," + objGlobal.getCountryDbName() + ".dbo.locstock b where " +
                        "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode and b.costcode='" + objGlobal.getExportCountryCostCode() + "' and loccode='" + objGlobal.getExportCountryLocCode() + "'", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set description=b.description from bfldata.dbo.tmpRfidPdaTransferItems a,hodata.dbo.itemmaster b where " +
                    "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "' and qty<=0", objGlobal.getConnection())) {
                return false;
            }
            String emptyDesc = "", zeroStock = "";
            rs = dbConnection.getResultSet("select itemcode,Description=isnull(Description,''),diffqty=isnull(HoQty,0)-qty,HoQty=isnull(HoQty,0) from bfldata.dbo.tmpRfidPdaTransferItems " +
                    "where (isnull(Description,'')='' or isnull(HoQty,0)<qty) and DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("Description").equals("")) {
                    if (emptyDesc.equals(""))
                        emptyDesc = rs.getString("itemcode");
                    else
                        emptyDesc = emptyDesc + ", " + rs.getString("itemcode");
                }
                if (rs.getInt("diffqty") <= 0) {
                    if (zeroStock.equals(""))
                        zeroStock = rs.getString("itemcode") + " (" + rs.getString("HoQty") + ")";
                    else
                        zeroStock = zeroStock + ", " + rs.getString("itemcode") + " (" + rs.getString("HoQty") + ")";
                }
            }
            if (!emptyDesc.equals("")) {
                objGlobal.setErrorMessage("Can't Proceed, Some items are invalid " + System.lineSeparator() + "(" + emptyDesc + ")");
                return false;
            }
            if (!zeroStock.equals("")) {
                objGlobal.setErrorMessage("Can't Proceed, HO Stock not available " + System.lineSeparator() + "(" + zeroStock + ")");
                return false;
            }
            objTransferGlobal.setShopName(shopName);
            objTransferGlobal.setBoxTrfBoxNo(boxOrPalletNo);
            objTransferGlobal.setTypeUsaTcm(typeUsaTcm);
            objTransferGlobal.setLpmDt(lpmDt);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateBoxPallet : " + e);
            return false;
        }
    }

    public boolean validateTransferToExp(String selshop, String scan) {
        String trfno = "";
        if (!checkConnection()) {
            return false;
        }
        try {
            objTransferGlobal.setShopName("");
            objTransferGlobal.setBoxTrfBoxNo("");
            rs = dbConnection.getResultSet("select top 1 trfno from " + objGlobal.getCountryDbName() + ".dbo.TransferHeader where (TrfNo='" + scan + "' or storeissue='" + scan + "') and " +
                    "CostCodeTo='" + objGlobal.getExportCountryCostCode() + "' and LocCodeTo='" + objGlobal.getExportCountryLocCode() + "' order by trfdate desc", objGlobal.getConnection());
            if (rs.next()) {
                trfno = rs.getString("trfno");
            } else {
                objGlobal.setErrorMessage("Invalid Transfer");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.CloseR1pallet where Palletno='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Can't proceed, Transfer (" + trfno + ") is already closed");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from RACKS.dbo.BinRack where Warehouse='" + objGlobal.getWarehouse() + "' and (ToteId='" + trfno + "' or BoxNo='" + trfno + "')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Can't proceed, Box/Transfer (" + trfno + ") is still in the RACK(" + rs.getString("Location") + "), please do out from rack");
                return false;
            }
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpRfidPdaTransferItems where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpRfidPdaTransferItems(DeviceName,ShopName,rfid,barcode,itemcode,Qty,ScanTime,HoQty) select '" + objGlobal.getDeviceName() + "','" + selshop + "','',Itemcode," +
                    "itemcode,sum(quantity),convert(varchar,getdate(),8),0 from " + objGlobal.getCountryDbName() + ".dbo.TransferDetail where TrfNo='" + trfno + "' group by Itemcode", objGlobal.getConnection())) {
                return false;
            }
            if (objGlobal.getWorkLocation().equals("UAE")) {
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set HoQty=b.quantity from bfldata.dbo.tmpRfidPdaTransferItems a,hodata.dbo.locstock b where " +
                        "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode and b.costcode='001' and loccode='01'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set HoQty=b.quantity from bfldata.dbo.tmpRfidPdaTransferItems a," + objGlobal.getCountryDbName() + ".dbo.locstock b where " +
                        "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode and b.costcode='" + objGlobal.getExportCountryCostCode() + "' and loccode='" + objGlobal.getExportCountryLocCode() + "'", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpRfidPdaTransferItems set description=b.description from bfldata.dbo.tmpRfidPdaTransferItems a,hodata.dbo.itemmaster b where " +
                    "a.DeviceName='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode", objGlobal.getConnection())) {
                return false;
            }
            String emptyDesc = "", zeroStock = "";
            rs = dbConnection.getResultSet("select itemcode,Description=isnull(Description,''),diffqty=isnull(HoQty,0)-qty,HoQty=isnull(HoQty,0) from bfldata.dbo.tmpRfidPdaTransferItems " +
                    "where (isnull(Description,'')='' or isnull(HoQty,0)<qty) and DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("Description").equals("")) {
                    if (emptyDesc.equals(""))
                        emptyDesc = rs.getString("itemcode");
                    else
                        emptyDesc = emptyDesc + ", " + rs.getString("itemcode");
                }
                if (rs.getInt("diffqty") <= 0) {
                    if (zeroStock.equals(""))
                        zeroStock = rs.getString("itemcode") + " (" + rs.getString("HoQty") + ")";
                    else
                        zeroStock = zeroStock + ", " + rs.getString("itemcode") + " (" + rs.getString("HoQty") + ")";
                }
            }
            if (!emptyDesc.equals("")) {
                objGlobal.setErrorMessage("Can't Proceed, Some items are invalid " + System.lineSeparator() + "(" + emptyDesc + ")");
                return false;
            }
            if (!zeroStock.equals("")) {
                objGlobal.setErrorMessage("Can't Proceed, HO Stock not available " + System.lineSeparator() + "(" + zeroStock + ")");
                return false;
            }
            objTransferGlobal.setShopName(selshop);
            objTransferGlobal.setBoxTrfBoxNo(trfno);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("TransferControl.validateBoxPallet : " + e);
            return false;
        }
    }
}