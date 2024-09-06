package com.bflgroup.warehouse.ui.shopreturns;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReceiveShopReturnsControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ReceiveShopReturnsGlobal objReceiveShopReturnsGlobal = ReceiveShopReturnsGlobal.getInstance();
    private boolean b_Result;
    private ResultSet rs;
    private ResultSet rs1;

    public ReceiveShopReturnsControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("1 ReceiveShopReturnsControl : Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("2 ReceiveShopReturnsControl : Cloud Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("1 ReceiveShopReturnsControl : Connection error");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("2 ReceiveShopReturnsControl : Cloud Connection error");
            }
        }
        return true;
    }

    public boolean validItemcode(String itemcode) {
        try {
            rs = dbConnection.getResultSet("select description,groupcode,department,division,season = (select IIF(itemType='W','WINTER','SUMMER') from HODATA..itemmaster where itemcode = a.itemcode) from hodata.dbo.vitemmaster a where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objReceiveShopReturnsGlobal.setScanItemDescription(rs.getString("description").toString());
                objReceiveShopReturnsGlobal.setScanItemGroup(rs.getString("groupcode").toString());
                objReceiveShopReturnsGlobal.setScanItemDepartment(rs.getString("department").toString());
                objReceiveShopReturnsGlobal.setScanItemDivision(rs.getString("division").toString());
                objReceiveShopReturnsGlobal.setScanItemSeason(rs.getString("season").toString());

            } else {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl:validItemcode : Itemcode is not valid, " + itemcode);
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:validItemcode:" + ex.toString());
            return false;
        }
    }

    public boolean validateShopReturnItem(Boolean entryScan, String entryNo, String itemcode, int scanQty, Boolean editFlag, String actions, boolean itemScan) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (entryScan) {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpShopRerturnScanItems where deviceid='" + objGlobal.getDeviceName() + "' and trfqty>0", objGlobal.getConnection())) {
                    return false;
                }
                double scanQtyItemScan=0;
                String scanActioItemScan="";
                rs = dbConnection.getResultSet("select * from " + objGlobal.getCloudDbName() + ".dbo.storedetail where entryno='" + entryNo + "'", objGlobal.getCloudCon());
                while (rs.next()) {
                    if(!itemScan) {
                        scanQtyItemScan = rs.getInt("quantity");
                        scanActioItemScan="USA Transfer to Shop";
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpShopRerturnScanItems(DeviceId,itemcode,itemname,TrfQty,ScanQty,actions) values('" + objGlobal.getDeviceName() + "'," +
                            "'" + rs.getString("itemcode").toString() + "',''," + rs.getInt("quantity") + "," + scanQtyItemScan + ",'" + scanActioItemScan + "')", objGlobal.getConnection())) {
                        return false;
                    }
                }
            } else {
                if (editFlag) {
                    if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpShopRerturnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                            "itemcode='" + itemcode + "' and actions='" + actions + "' and scanqty>0", objGlobal.getConnection())) {
                        return false;
                    }
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpShopRerturnScanItems(DeviceId,itemcode,itemname,TrfQty,ScanQty,actions) " +
                        "values('" + objGlobal.getDeviceName() + "','" + itemcode + "','',0," + scanQty + ",'" + actions + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:validateShopReturnItem :" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean validateMissingPassword(String password) {
        objGlobal.setCloudDbName("BFLDATA");
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.ZeroPass where type='RTMIS' and Pass='" + password + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopReturn 0: Please enter correct password");
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:validateMissingPassword 6 :" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean validateShopReturn(String entryNo, boolean itemScan, boolean autoBuild, boolean forSave, String toteid) {
        String cAutoBuild = "N";
        objGlobal.setCloudDbName("BFLDATA");
        objReceiveShopReturnsGlobal.setAutoBuildPalletType("");
        if (!checkConnection()) {
            return false;
        }
        try {
            if (forSave) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpShopRerturnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can't save, scan quantity is 0");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpShopRerturnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0 and Actions=''", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can't save, Some items found with empty actions, please check");
                    return false;
                }
                if (objGlobal.getWarehouse().equals("KSA")) {
                    if (autoBuild) {
                        if (toteid.isEmpty()) {
                            objGlobal.setErrorMessage("Please enter toteid");
                            return false;
                        }
                        if (objGlobal.getWarehouse().equals("KSA"))
                            rs = dbConnection.getResultSet("select * from BFLKSA.dbo.ToteIDMaster where ToteID='" + toteid + "'", objGlobal.getConnection());
                        else
                            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.BlueToteIDMaster where ToteID='" + toteid + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            objGlobal.setErrorMessage("Toteid is not valid - " + toteid);
                            return false;
                        }
                        rs = dbConnection.getResultSet("select top 1 boxno from usa.dbo.UPCBoxHead where ToteID='" + toteid + "' and Closed='N'", objGlobal.getConnection());
                        if (rs.next()) {
                            objGlobal.setErrorMessage("Toteid is already used to another box (" + rs.getString("boxno") + ") - " + toteid);
                            return false;
                        }
                    }
                }
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.ShopReturnHeader where ReturnNo='" + entryNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopReturn 0: Entry number already save, " + entryNo);
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.ShopToShopTransfer where EntryNo='" + entryNo + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopReturn 0: Entry number already save, " + entryNo);
                return false;
            }

            String[] arrOfStr = entryNo.split("/", 0);
            String shopLetter = arrOfStr[0].replace("RTN", "");
            rs = dbConnection.getResultSet("select DataName,ShopName from datasettings where ShopLetter='" + shopLetter + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setCloudDbName(rs.getString("DataName").toString());
                objReceiveShopReturnsGlobal.setShopName(rs.getString("ShopName").toString());
            } else {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopTransfer 1 : Shop Letter (" + shopLetter + ") not found in datasettings, " + entryNo);
                return false;
            }
            int totalExcess = 0;
            int totalMissing = 0;
            rs = dbConnection.getResultSet("select itemcode,diffqty=sum(scanqty-trfqty) from bfldata.dbo.tmpShopRerturnScanItems where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "' group by itemcode", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getInt("diffqty") > 0) totalExcess += Math.abs(rs.getInt("diffqty"));
                if (rs.getInt("diffqty") < 0) totalMissing += Math.abs(rs.getInt("diffqty"));
            }
            objReceiveShopReturnsGlobal.setTotalExcess(totalExcess);
            objReceiveShopReturnsGlobal.setTotalMissing(totalMissing);
            b_Result = dbConnection.connectCloudDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopTransfer 2 : Connection error");
                return false;
            }
            rs = dbConnection.getResultSet("select *,RcUid=isnull(RecUserId,0) from " + objGlobal.getCloudDbName() + ".dbo.storeheader where entryno='" + entryNo + "'", objGlobal.getCloudCon());
            if (!rs.next()) {
                objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopTransfer 4 : Invalid Entry number, " + entryNo);
                return false;
            } else {
                if (rs.getInt("RcUid") != 0) {
                    objGlobal.setErrorMessage("ReceiveShopReturnsControl.validateShopTransfer 5 : Entry Already saved, " + entryNo);
                    return false;
                }
                objReceiveShopReturnsGlobal.setCategory(rs.getString("TrfNo1"));
            }
            if (!itemScan) {
                rs = dbConnection.getResultSet("select EntryNo,AutoBuild,PalletType from bfldata.dbo.ShopReturnSkipSkuScan where EntryNo='" + entryNo + "'", objGlobal.getCloudCon());
                if (rs.next()) {
                    cAutoBuild = rs.getString("AutoBuild");
                    objReceiveShopReturnsGlobal.setAutoBuildPalletType(rs.getString("PalletType"));
                } else {
                    objGlobal.setErrorMessage(entryNo + " is not allowed for auto save");
                    return false;
                }
                if (autoBuild) {
                    if (cAutoBuild.equals("N")) {
                        objGlobal.setErrorMessage(entryNo + " is not allowed for Auto Building");
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:validateShopTransfer 6 :" + ex.toString());
            return false;
        }
        return true;
    }
    ArrayList<ReceiveShopReturnsScanItemPopupTicket> loadPopupScanItems() {
        ArrayList<ReceiveShopReturnsScanItemPopupTicket> listPopupScanItems = new ArrayList<ReceiveShopReturnsScanItemPopupTicket>();
        try {
            listPopupScanItems.clear();
            rs = dbConnection.getResultSet("select itemcode,actions,scanqty=sum(scanqty) from bfldata.dbo.tmpShopRerturnScanItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and scanqty>0 group by itemcode,actions", objGlobal.getConnection());
            while (rs.next()) {
                listPopupScanItems.add(new ReceiveShopReturnsScanItemPopupTicket(rs.getString("itemcode").toString(),rs.getString("actions").toString(),
                        rs.getInt("scanqty")));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:loadPopupScanItems:" + ex.toString());
            return null;
        }
        return listPopupScanItems;
    }

    ArrayList<ReceiveShopReturnsScanItemTicket> loadScanRetItems() {
        ArrayList<ReceiveShopReturnsScanItemTicket> listScanRetItems = new ArrayList<ReceiveShopReturnsScanItemTicket>();
        int totalScanQty = 0, totalTrfQty = 0, totalDiffQty = 0;
        try {
            listScanRetItems.clear();
            rs = dbConnection.getResultSet("select itemcode,scanqty=sum(scanqty),trfqty=sum(trfqty),diffqty=sum(scanqty-trfqty) from " +
                    "bfldata.dbo.tmpShopRerturnScanItems where deviceid='" + objGlobal.getDeviceName() + "' group by itemcode order by abs(sum(scanqty-trfqty)) desc", objGlobal.getConnection());
            while (rs.next()) {
                listScanRetItems.add(new ReceiveShopReturnsScanItemTicket(rs.getString("itemcode").toString(),
                        rs.getInt("scanqty"), rs.getInt("trfqty"), rs.getInt("diffqty")));
                totalScanQty = totalScanQty + rs.getInt("scanqty");
                totalTrfQty = totalTrfQty + rs.getInt("trfqty");
                totalDiffQty = totalDiffQty + rs.getInt("diffqty");
            }
            objReceiveShopReturnsGlobal.setTotalScanQty(totalScanQty);
            objReceiveShopReturnsGlobal.setTotalTrfQty(totalTrfQty);
            objReceiveShopReturnsGlobal.setTotalDiffQty(totalDiffQty);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:loadScanRetItems:" + ex.toString());
            return null;
        }
        return listScanRetItems;
    }

    public boolean clearTable() {
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpShopRerturnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpShopRerturnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ReceiveShopReturnsControl:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean saveShopTransfer(String entryNo, String shopName, String category, String remarks,boolean itemScan, boolean autoBuild, String autoBuildPalletType) {
        int slnoCloud = 0, slnoLocal = 0;
        String sItemScan="N";
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpShopRerturnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon())) {
                return false;
            }
            rs = dbConnection.getResultSet("select DeviceId,itemcode,itemname,TrfQty=sum(TrfQty),ScanQty=sum(ScanQty),Actions from bfldata.dbo.tmpShopRerturnScanItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' group by DeviceId,itemcode,itemname,Actions", objGlobal.getConnection());
            while (rs.next()) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpShopRerturnScanItems(DeviceId,itemcode,itemname,TrfQty,ScanQty,Actions) " +
                        "values('" + objGlobal.getDeviceName() + "','" + rs.getString("itemcode").toString() + "',''," + rs.getInt("TrfQty") + "," +
                        "" + rs.getInt("ScanQty") + ",'" + rs.getString("Actions").toString() + "')", objGlobal.getCloudCon())) {
                    return false;
                }
                if (!b_Result) {
                    return false;
                }
            }
            objReceiveShopReturnsGlobal.setBoxNo("");
            if(itemScan){
                sItemScan="Y";
            }
            if (autoBuild) {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpScanItemsBox(DeviceId,itemcode,qty) select '" + objGlobal.getDeviceName() + "',itemcode,ScanQty from bfldata.dbo.tmpShopRerturnScanItems " +
                        "where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0", objGlobal.getConnection())) {
                    return false;
                }
                if (autoBuildPalletType.isEmpty()) {
                    objGlobal.setErrorMessage("Pallet type should not empty");
                    return false;
                }
                b_Result = getBoxNumber();
                if (!b_Result) return false;
            }
            rs = dbConnection.getResultSet("select sno=isnull(max(sn),0) + 1 from bfldata.dbo.ShopReturnHeader", objGlobal.getCloudCon());
            if (rs.next()) {
                slnoCloud = rs.getInt("sno");
            }
            rs = dbConnection.getResultSet("select sno=isnull(max(sn),0) + 1 from bfldata.dbo.ShopReturnHeader", objGlobal.getConnection());
            if (rs.next()) {
                slnoLocal = rs.getInt("sno");
            }

            objGlobal.getCloudCon().setAutoCommit(false);
            objGlobal.getConnection().setAutoCommit(false);

            b_Result = dbConnection.insertUpdate("insert into StoreDetail select distinct '" + entryNo + "',itemcode,0,0,0 from bfldata.dbo.tmpShopRerturnScanItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and ScanQty>0 and itemcode not in(select itemcode from StoreDetail where entryno='" + entryNo + "') group by itemcode", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("update StoreDetail set RecQty=a.ScanQty from bfldata.dbo.tmpShopRerturnScanItems a,StoreDetail b where a.deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "b.EntryNo='" + entryNo + "' and a.ScanQty>0 and a.itemcode=b.itemcode", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("update StoreHeader set RecUserId=" + objGlobal.getUserId() + ",RecDateTime=getdate() where EntryNo='" + entryNo + "'", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into bfldata.dbo.ShopToShopTransfer(ShopName,EntryNo,Trndate,TrnTime,TargetShop,TrfIssueNo,TrfRecNo,Category,EntryWise,AutoBoxNo) select ShopFrom,EntryNo," +
                    "convert(varchar,getdate(),103),convert(varchar,getdate(),8),ShopName,'','',TrfNo1,'" + sItemScan + "','" + objReceiveShopReturnsGlobal.getBoxNo() + "' from StoreHeader where EntryNo='" + entryNo + "'", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }

            //localShopReturnHeader
            b_Result = dbConnection.insertUpdate("insert into BFLDATA.dbo.ShopReturnHeader(sn,ReturnNo,Edate,Category,ShoopName,TrfNo,RetNo,InvNo,TrfIssueNo,UserId,PrepareBy,Remarks) " +
                    "values (" + slnoLocal + ",'" + entryNo + "','" + objGlobal.getServerDate() + "','" + category + "','" + shopName + "','','','',''," + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','" + remarks + "')", objGlobal.getConnection());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into BFLDATA.dbo.ShopReturnDetail select " + slnoLocal + ",ItemCode,sum(scanqty),sum(scanqty),0.01,'001',actions,'','','','',itemcode from " +
                    "bfldata.dbo.tmpShopRerturnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0 group by ItemCode,actions", objGlobal.getConnection());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            //cloud ShopReturnHeader
            b_Result = dbConnection.insertUpdate("insert into BFLDATA.dbo.ShopReturnHeader(sn,ReturnNo,Edate,Category,ShoopName,TrfNo,RetNo,InvNo,TrfIssueNo,UserId,PrepareBy,Remarks) " +
                    "values (" + slnoCloud + ",'" + entryNo + "','" + objGlobal.getServerDate() + "','" + category + "','" + shopName + "','','','',''," + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "','" + remarks + "')", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into BFLDATA.dbo.ShopReturnDetail select " + slnoCloud + ",ItemCode,sum(scanqty),sum(scanqty),0.01,'001',actions,'','','','',itemcode,'" + entryNo + "' from " +
                    "bfldata.dbo.tmpShopRerturnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0 group by ItemCode,actions", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            //auto buil box start
            if (autoBuild) {
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxHead (BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2," +
                        "WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) values ('" + objReceiveShopReturnsGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "',''," +
                        "'" + objGlobal.getUserName() + "','" + entryNo + " / Winter Return / Auto','" + objGlobal.getUserId() + "','" + autoBuildPalletType + "','N','','','" + objGlobal.getUserId() + "'," +
                        "'" + objGlobal.getUserId() + "','" + objGlobal.getWarehouse() + "','','','" + autoBuildPalletType + "','','','')", objGlobal.getConnection())) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objReceiveShopReturnsGlobal.getBoxNo() + "',Itemcode,Qty,0,'',Itemcode from " +
                        "bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.PrintFromPda(warehouse,PSystemName,PType,PItem,ReqUser,ReqDate,ReqTime,Printed) values ('" + objGlobal.getWarehouse() + "'," +
                        "'" + objGlobal.getUserPrinterName() + "','UB','" + objReceiveShopReturnsGlobal.getBoxNo() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "','N')", objGlobal.getConnection())) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            //auto buil box end
            objGlobal.getCloudCon().commit();
            objGlobal.getCloudCon().setAutoCommit(true);
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("saveShopTransfer:sqlException:" + sqlException.toString());
                return false;
            }
            objGlobal.setErrorMessage("saveShopTransfer:E:" + e.toString());
            return false;
        }
    }

    private boolean getBoxNumber() {
        try {
            int autoSn = 0;
            String suff = "";
            Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(objGlobal.getServerDate());
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(dt);
            String yr = year.substring(2, 4);
            suff = objGlobal.getCountryWiseBoxPrefix() + yr + "/";
            rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from usa.dbo.UPCBoxHead where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            objReceiveShopReturnsGlobal.setBoxNo(suff + String.format("%06d", autoSn));
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getBoxNumber:" + ex.toString());
            return false;
        }
    }


}
