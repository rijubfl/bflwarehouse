package com.bflgroup.warehouse.ui.warehousegrn;

import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WarehouseGRNControl {

    private DBConnection dbConnection = new DBConnection();
    private Controls objControls= new Controls();
    private Global objGlobal = Global.getInstance();
    private WarehouseGRNGlobal objWarehouseGRNNewGlobal = WarehouseGRNGlobal.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public WarehouseGRNControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl : Connection error");
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
                objGlobal.setErrorMessage("WarehouseGRNNewControl.checkConnection : Connection error");
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

    public boolean validateGin(String country, String ginNo) {
        if (ginNo.isEmpty()) {
            objGlobal.setErrorMessage("Please enter GIN number.");
            return false;
        }
        if (country.isEmpty()) {
            objGlobal.setErrorMessage("Please enter GIN number.");
            return false;
        }
        String db = objControls.getCountryDb(country);
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select *,edt=convert(varchar,EntryDate,103) from " + db + ".dbo.PLTDeliveryHead where SrNo=" + ginNo, objGlobal.getConnection());
            if (rs.next()) {
                objWarehouseGRNNewGlobal.setGinDate(rs.getString("edt"));
                objWarehouseGRNNewGlobal.setWarehouseFrom(rs.getString("warehouseFrom"));
                objWarehouseGRNNewGlobal.setWarehouseTo(rs.getString("warehouseTo"));
            } else {
                objGlobal.setErrorMessage("Invalid GIN Number, " + ginNo);
                return false;
            }
            rs = dbConnection.getResultSet("select * from " + db + ".dbo.WHGRNDetails where GINNo='" + ginNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GIN Number: " + ginNo + ", already found in GRN");
                return false;
            }
            if (objWarehouseGRNNewGlobal.getGinDate().isEmpty()) {
                objGlobal.setErrorMessage("Empty Gin Date");
                return false;
            }
            if (objWarehouseGRNNewGlobal.getWarehouseFrom().isEmpty()) {
                objGlobal.setErrorMessage("Empty Warehouse From");
                return false;
            }
            if (objWarehouseGRNNewGlobal.getWarehouseTo().isEmpty()) {
                objGlobal.setErrorMessage("Empty Warehouse TO");
                return false;
            }
            objWarehouseGRNNewGlobal.setAutoPost("N");
            rs = dbConnection.getResultSet("select AutoPost=isnull(AutoPost,'N') from bfldata.dbo.WarehouseGrnAutoPosting where WarehouseFrom='" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "' and " +
                    "WarehouseTo='" + objWarehouseGRNNewGlobal.getWarehouseTo() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objWarehouseGRNNewGlobal.setAutoPost(rs.getString("AutoPost"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:validateGin:" + ex);
            return false;
        }
    }

    public boolean loadGinDetails(String country,String ginNo) {
        String db = objControls.getCountryDb(country);
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpWarehouseGrnScanNew where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (country.equals("UAE")) {
                //usa.dbo.vUPCPalletBoxDet
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),isnull(ToteID,''),1,0 from usa.dbo.vUPCPalletBoxDet where closed='N' and " +
                        "palletno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,''),isnull(ToteID,'')", objGlobal.getConnection())) {
                    return false;
                }
                //usa.dbo.vUPCPalletBoxDet
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),isnull(ToteID,''),1,0 from usa.dbo.vUPCPalletBoxDet where closed='N' and " +
                        "Boxno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,''),isnull(ToteID,'')", objGlobal.getConnection())) {
                    return false;
                }
                //BFLDATA.dbo.vR1Pallet
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),isnull(TotID,''),1,0 from BFLDATA.dbo.vR1Pallet where closed='N' and " +
                        "palletno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,''),isnull(TotID,'')", objGlobal.getConnection())) {
                    return false;
                }
                //abudata.dbo.tcmitemsall
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(palletno,''),'',1,0 from abudata.dbo.tcmitemsall where " +
                        "palletno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(palletno,'')", objGlobal.getConnection())) {
                    return false;
                }
                //usa.dbo.KNBBoxes
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),'',1,0 from usa.dbo.KNBBoxes where closed='N' and " +
                        "palletno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,'')", objGlobal.getConnection())) {
                    return false;
                }
                //BFLDATA.dbo.vGoodsIssue
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(TrfNo,''),'',1,0 from BFLDATA.dbo.vGoodsIssue where " +
                        "palletno in(select palletno from BFLDATA.dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(TrfNo,''),isnull(BoxNo,'')", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                //usa.dbo.vUPCPalletBoxDet
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),isnull(ToteID,''),1,0 from " + db + ".dbo.vUPCPalletBoxDet where closed='N' and " +
                        "palletno in(select palletno from " + db + ".dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,''),isnull(ToteID,'')", objGlobal.getConnection())) {
                    return false;
                }
                //usa.dbo.vUPCPalletBoxDet
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(boxno,''),isnull(ToteID,''),1,0 from " + db + ".dbo.vUPCPalletBoxDet where closed='N' and " +
                        "Boxno in(select palletno from " + db + ".dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(boxno,''),isnull(ToteID,'')", objGlobal.getConnection())) {
                    return false;
                }
                //BFLDATA.dbo.vGoodsIssue
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "select '" + objGlobal.getDeviceName() + "','" + ginNo + "','" + objWarehouseGRNNewGlobal.getGinDate() + "','" + objWarehouseGRNNewGlobal.getWarehouseFrom() + "'," +
                        "'" + objWarehouseGRNNewGlobal.getWarehouseTo() + "',palletno,isnull(TrfNo,''),'',1,0 from " + db + ".dbo.vGoodsIssue where " +
                        "palletno in(select palletno from " + db + ".dbo.PLTDeliveryDetails where SrNo=" + ginNo + ") group by palletno,isnull(TrfNo,''),isnull(BoxNo,'')", objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinDetails:" + ex.toString());
            return false;
        }
    }

    public boolean loadGinDetailsFromAPI(String country,String ginNo,ArrayList<WarehouseGRNDetailAPICallTicket> objWarehouseGRNNewDetailAPICallTicket) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpWarehouseGrnScanNew where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            for (WarehouseGRNDetailAPICallTicket list : objWarehouseGRNNewDetailAPICallTicket) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpWarehouseGrnScanNew(DeviceId,GINNo,GINDate,WareHouseFrom,WareHouseTo,PalletNo,BoxNo,ToteId,TCount,SCount) " +
                        "values ('" + objGlobal.getDeviceName() + "','" + list.getGinno() + "','" + list.getGinDate() + "','" + list.getWareHouseFrom() + "','" + list.getWareHouseTo() + "'," +
                        "'" + list.getPalletNo() + "','" + list.getBoxNo() + "','" + list.getToteId() + "',1,0)", objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinDetails:" + ex.toString());
            return false;
        }
    }

    public boolean grnClear() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:grnSave:ex2:" + ex);
            return false;
        }
    }

    public boolean validateScanPalletOrBox(String scanValue) {
        if (!checkConnection()) {
            return false;
        }
        if (scanValue.isEmpty()) {
            objGlobal.setErrorMessage("Please scan Pallet No. / Box No. / Tote No.");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "(PalletNo='" + scanValue + "' or boxno='" + scanValue + "' or toteid='" + scanValue + "')", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Pallet number or Box number is not found in the GIN");
                return false;
            }
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpWarehouseGrnScanNew set scount=1,ScanDate=getdate(),ScanTime=getdate() where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "(PalletNo='" + scanValue + "' or boxno='" + scanValue + "' or toteid='" + scanValue + "')", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNControl:validateGin:" + ex);
            return false;
        }
    }

    public boolean validateGrn(String country,String ginNo, String autoPost, String whFrom,String whTo) {
        String db = objControls.getCountryDb(country);
        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.WHGRNDetails where GINNo='" + ginNo + "' and WareHouseFrom='" + whFrom + "' and WareHouseTo='" + whTo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GIN Number: " + ginNo + ", already found in GRN");
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpWarehouseGrnScanNew where scount>0 and DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("No record found for Save");
                return false;
            }
            rs = dbConnection.getResultSet("select cnt=count(distinct PalletNo) from bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "' and SCount=0", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("No record found for Save");
                return false;
            }
            if (autoPost.equals("Y")) {
                /*rs = dbConnection.getResultSet("select cnt=count(*) from bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "' and SCount=1 and BoxNo not in(select BoxNo " +
                        "from " + db + ".dbo.UPCBoxHead where Closed='N')", objGlobal.getConnection());
                if (rs.next()) {
                    if(rs.getInt("cnt")>0){
                        objGlobal.setErrorMessage("Can't proceed, Boxes (" + rs.getString("cnt") + ") are invalid or closed");
                        return false;
                    }
                }*/
                rs = dbConnection.getResultSet("select cnt=count(*) from usa.dbo.UPCBoxHead where BoxNo in(select BoxNo from bfldata.dbo.tmpWarehouseGrnScanNew where " +
                        "DeviceId='" + objGlobal.getDeviceName() + "' and SCount=1)", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getInt("cnt") > 0) {
                        objGlobal.setErrorMessage("Can't proceed, Boxes (" + rs.getString("cnt") + ") already found in HO");
                        return false;
                    }
                }
                rs = dbConnection.getResultSet("select cnt=count(*) from BFLDATA.dbo.ShopReturnHeader where ReturnNo in(select BoxNo from bfldata.dbo.tmpWarehouseGrnScanNew where " +
                        "DeviceId='" + objGlobal.getDeviceName() + "' and SCount=1)", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getInt("cnt") > 0) {
                        objGlobal.setErrorMessage("Can't proceed, Boxes (" + rs.getString("cnt") + ") already done posting");
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:validateGrn:" + ex);
            return false;
        }
    }

    public boolean deleteSelectedPallet(String palletno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpWarehouseGrnScanNew set SCount=0 where DeviceId='" + objGlobal.getDeviceName() + "' and PalletNo='" + palletno + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:validateGrn:" + ex.toString());
            return false;
        }
    }

    public boolean grnSave(String country, String remarks,String autopost, String whFrom, String whTo) {
        int grnno = 0;
        String db = objControls.getCountryDb(country);
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(grnno),0)+1 from bfldata.dbo.WHGRNHeader", objGlobal.getConnection());
            if (rs.next()) {
                grnno = rs.getInt("sn");
            }
            if (grnno == 0) {
                objGlobal.setErrorMessage("Wrong SN");
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.WHGRNHeader(GRNNo,GRNDate,GRNTime,UserName,Remarks,RecWarehouse,AutoPost,GinLocation) values (" + grnno + ",'" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "','" + objGlobal.getUserName() + "','" + remarks + "','" + objGlobal.getWarehouse() + "','" + autopost + "','" + country + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.WHGRNDetails select " + grnno + ",GINNo,GINDate,PalletNo,WareHouseFrom,WareHouseTo,BoxNo,0,'',ToteId from " +
                    "bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "' and SCount>0", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            //auto box and posting
            if (autopost.equals("Y")) {
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxHead(BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2," +
                        "WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) select BoxNo,'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "',NewPallet,PreparedBy,Remarks," +
                        "Userid,'R1','N',GroupCode,OldBoxNo,Prepared1,Prepared2,WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID from " + db + ".DBO.UPCBoxHead where BoxNo in(select BoxNo from " +
                        "bfldata.dbo.tmpWarehouseGrnScanNew where SCount=1 and DeviceId='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC,imgfile) select BoxNo,Itemcode,Qty,QtyIssued,Status,UPC,imgfile from " + db + ".DBO.UPCBoxDet " +
                        "where BoxNo in(select BoxNo from bfldata.dbo.tmpWarehouseGrnScanNew where SCount=1 and DeviceId='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxHead(BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2," +
                        "WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) select TrfNo,'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','',PreparedBy,'KSA to YOTO Transfers'," +
                        "0,'R1','N','',StoreIssue,0,0,'" + objGlobal.getWorkLocation() + "','','','R1','','',StoreIssue from " + db + ".dbo.TransferHeader where TrfNo in(select BoxNo from " +
                        "bfldata.dbo.tmpWarehouseGrnScanNew where SCount=1 and DeviceId='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC,imgfile) select TrfNo,ItemCode,Quantity,0,'',ItemCode,'' " +
                        "from " + db + ".dbo.TransferDetail where TrfNo in(select BoxNo from bfldata.dbo.tmpWarehouseGrnScanNew where SCount=1 and DeviceId='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
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

    public ArrayList<WarehouseGRNScanTicket> loadGinGrnScan() {
        ArrayList<WarehouseGRNScanTicket> listGinGrnScan = new ArrayList<WarehouseGRNScanTicket>();
        try {
            rs = dbConnection.getResultSet("select PalletNo,BoxNo,ToteId from bfldata.dbo.tmpWarehouseGrnScanNew where DeviceId='" + objGlobal.getDeviceName() + "' and scount>0 order by ScanDate desc,ScanTime desc", objGlobal.getConnection());
            while (rs.next()) {
                listGinGrnScan.add(new WarehouseGRNScanTicket(rs.getString("PalletNo"), rs.getString("BoxNo"), rs.getString("ToteId")));
            }
            return listGinGrnScan;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinGrnScan:" + ex.toString());
            return listGinGrnScan;
        }
    }

    public ArrayList<WarehouseGRNScanCountTicket> loadGinGrnScanCount() {
        ArrayList<WarehouseGRNScanCountTicket> listGinGrnScan = new ArrayList<WarehouseGRNScanCountTicket>();
        try {
            objWarehouseGRNNewGlobal.setScanBoxes(0);
            objWarehouseGRNNewGlobal.setScanPallets(0);
            objWarehouseGRNNewGlobal.setTotalBoxes(0);
            objWarehouseGRNNewGlobal.setScanBoxes(0);
            rs = dbConnection.getResultSet("select palletno,TCount=sum(TCount),SCount=sum(SCount),diff=sum(TCount-SCount) from bfldata.dbo.tmpWarehouseGrnScanNew where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "' group by palletno order by palletno", objGlobal.getConnection());
            while (rs.next()) {
                listGinGrnScan.add(new WarehouseGRNScanCountTicket(rs.getString("palletno"), rs.getInt("TCount"), rs.getInt("SCount"), rs.getInt("diff")));
            }
            rs = dbConnection.getResultSet("select ptcount=isnull(count(distinct palletno),0),btcount=isnull(sum(tcount),0) from bfldata.dbo.tmpWarehouseGrnScanNew where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objWarehouseGRNNewGlobal.setTotalPallets(rs.getInt("ptcount"));
                objWarehouseGRNNewGlobal.setTotalBoxes(rs.getInt("btcount"));
            }
            rs = dbConnection.getResultSet("select pscount=isnull(count(distinct palletno),0),bscount=isnull(sum(scount),0) from bfldata.dbo.tmpWarehouseGrnScanNew where deviceid='" + objGlobal.getDeviceName() + "' and scount>0", objGlobal.getConnection());
            if (rs.next()) {
                objWarehouseGRNNewGlobal.setScanPallets(rs.getInt("pscount"));
                objWarehouseGRNNewGlobal.setScanBoxes(rs.getInt("bscount"));
            }
            return listGinGrnScan;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadGinGrnScanCount:" + ex.toString());
            return listGinGrnScan;
        }
    }

    public JSONObject loadScanGinForApi(String remarks,String gincountry) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("username", objGlobal.getUserName());
            jsonRequest.put("remarks", remarks);
            jsonRequest.put("isclosed", "N");
            jsonRequest.put("warehouse", objGlobal.getWarehouse());
            jsonRequest.put("ginlocation", gincountry);
            JSONArray detailArray = new JSONArray();
            rs = dbConnection.getResultSet("select ginno,gindate=convert(varchar,gindate,103),palletno,wareHousefrom,wareHouseto,boxno,simno='0',remarks='',toteid from bfldata.dbo.tmpWarehouseGrnScanNew where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "' and SCount>0", objGlobal.getConnection());
            while (rs.next()) {
                JSONObject detailObj = new JSONObject();
                detailObj.put("ginno", rs.getString("ginno"));
                detailObj.put("gindate", rs.getString("gindate"));
                detailObj.put("palletno", rs.getString("palletno"));
                detailObj.put("wareHousefrom", rs.getString("wareHousefrom"));
                detailObj.put("wareHouseto", rs.getString("wareHouseto"));
                detailObj.put("boxno", rs.getString("boxno"));
                detailObj.put("simno", rs.getString("palletno"));
                detailObj.put("remarks", rs.getString("remarks"));
                detailObj.put("toteid", rs.getString("toteid"));
                detailArray.put(detailObj);
            }
            jsonRequest.put("detail", detailArray);
            return jsonRequest;
        } catch (Exception e) {
            objGlobal.setErrorMessage("WarehouseGRNNewControl:loadScanGinForApi:" + e);
            return null;
        }
    }
}
