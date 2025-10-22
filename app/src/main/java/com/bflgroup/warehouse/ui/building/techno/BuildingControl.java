package com.bflgroup.warehouse.ui.building.techno;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BuildingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BuildingGlobal objBuildingGlobal = BuildingGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    Connection conRobo = null;
    public BuildingControl() {
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("BuildingControl:tmpConnectDb(1) : "+ objGlobal.getRoboServerIP());
        }
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("BuildingControl.connectDb(1) : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        conRobo = dbConnection.tmpConnectDb(objGlobal.getRoboServerIP(),"ROBOTICS");
        if (!b_Result) {
            objGlobal.setErrorMessage("BuildingControl:tmpConnectDb(2) : "+ objGlobal.getRoboServerIP());
        }
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("BuildingControl.connectDb(2) : Connection error");
        }
        return b_Result;
    }

    public boolean validateCheckInOut(String direction, String chuteId, String toteId, String shopId, String shopName, String status,String shopToteType) {
        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(conRobo)) {
            objGlobal.setErrorNo("validateCheckInOut:001:");
            return false;
        }
        if (TextUtils.isEmpty(chuteId)) {
            objGlobal.setErrorMessage("ChuteId is empty");
            return false;
        }
        if (TextUtils.isEmpty(toteId)) {
            objGlobal.setErrorMessage("Tote is empty");
            return false;
        }
        if (!checkValidChuteId(chuteId)) {
            objGlobal.setErrorMessage("Invalid Chute ID, " + chuteId);
            return false;
        }
        if (!checkValidToteId(toteId)) {
            return false;
        }
        if (TextUtils.isEmpty(shopId) || TextUtils.isEmpty(shopName)) {
            objGlobal.setErrorMessage("Shop Id or Shop name is empty");
            return false;
        }
        if (TextUtils.isEmpty(status)) {
            objGlobal.setErrorMessage("Chute Status is Empty");
            return false;
        }
        if (TextUtils.isEmpty(shopToteType)) {
            objGlobal.setErrorMessage("Shop tote type is blank");
            return false;
        }
        if (TextUtils.isEmpty(getShopIdFromChuteId(chuteId))) {
            objGlobal.setErrorMessage("Shop Id is empty, Please check chute configuration, " + chuteId);
            return false;
        }
        if (validateChuteError(chuteId)) {
            objGlobal.setErrorMessage("ChuteId Error: " + chuteId + ", Please contact IT");
            return false;
        }
        if (validateTotidUsed(toteId)) {
            objGlobal.setErrorMessage("Totid is alrady used, Totid: " + toteId + "");
            return false;
        }
        if (validateTotIsUsed(toteId)) {
            objGlobal.setErrorMessage("Totid is alrady used in another Box, Totid: " + toteId + "");
            return false;
        }
        if (direction.equals("IN")) {
            if (!TextUtils.isEmpty(getToteIdFromChuteId(chuteId))) {
                objGlobal.setErrorMessage("Tot Id is used in another chute, Please check, " + chuteId);
                return false;
            }
            if (transferPendingInChuteId(chuteId, shopId)) {
                objGlobal.setErrorMessage("Pending transfer found, " + chuteId);
                return false;
            }
            if (!TextUtils.isEmpty(getTotIdAlreadyAssignFromChuteId(toteId))) {
                objGlobal.setErrorMessage("Tot id found in another chute, " + toteId);
                return false;
            }
        }
        if (direction.equals("OUT")) {
            s_Result = getToteIdFromChuteId(chuteId);
            if (TextUtils.isEmpty(s_Result)) {
                objGlobal.setErrorMessage("Tot Id is empty, Please check, " + chuteId);
                return false;
            }
            if (!TextUtils.equals(s_Result.trim(), toteId.trim())) {
                objGlobal.setErrorMessage("Tot Id and Chute Id is not match, Please check");
                return false;
            }
            if (!transferPendingInChuteId(chuteId, shopId)) {
                objGlobal.setErrorMessage("No record found for transfer, " + chuteId);
                return false;
            }
        }
        return true;
    }

    public boolean saveChuteIn(String chuteId, String toteId, String shopId, String shopName, String status) {
        Connection con = null;
        con = conRobo;
        try {
            if (!dbConnection.getServerDateTime(conRobo)) {
                objGlobal.setErrorNo("saveChuteIn:001:");
                return false;
            }
            con.setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into ChuteCheckin values('" + chuteId + "','" + toteId + "','" + shopId + "','" + shopName + "'," +
                    "'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ")", con)) {
                con.rollback();
                objGlobal.setErrorNo("saveChuteIn:002:");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteConfigurationlog (ChuteId,ShopId,ShopName,TotId,Trndate,userId,direction) " +
                    "select ChuteId,ShopId,ShopName,TotId,getdate(),'" + objGlobal.getUserId() + "','IN' from ChuteConfiguration where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("saveChuteIn:003:");
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteConfiguration set TotId='" + toteId + "' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("saveChuteIn:004:");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteStatusLog select ChuteId,Status,'" + status + "','" + objGlobal.getUserId() + "',getdate() " +
                    "from ChuteIdMaster where chuteid='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("saveChuteIn:005:");
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteIdMaster set Status='" + status + "' where ChuteId='" + chuteId + "'", con)) {
                con.rollback();
                objGlobal.setErrorNo("saveChuteIn:006:");
                return false;
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception exception) {
            try {
                con.rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("inChuteStatus:sqlException: " + sqlException.toString());
                return false;
            }
            objGlobal.setErrorMessage("inChuteStatus: exception: " + exception.toString());
            return false;
        }
        return true;
    }

    public boolean saveChuteBuilding(String chuteId, String toteId, String shopId, String shopName) {
        int totalBuildQty = 0;
        String shopInShop = "", palletTyp = "", div = "", buildtype="";
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from tmpBoxBuild where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.setErrorNo("saveChuteBuilding:001");
                return false;
            }
            rs = dbConnection.getResultSet("select itemcode,qty=sum(qty) from SortingConformationDetail where TransferNo='' and " +
                    "ChuiteId='" + chuteId + "' and ShopId='" + shopId + "' group by itemcode", conRobo);
            while (rs.next()) {
                if (!dbConnection.insertUpdate("insert into tmpBoxBuild(itemcode,qty,UserId,DeviceName) " +
                        "values('" + rs.getString("itemcode") + "'," + rs.getInt("qty") + "," + objGlobal.getUserId() + ",'" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                    objGlobal.setErrorNo("saveChuteBuilding:002");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select qty=(sum(qty)) from tmpBoxBuild where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objBuildingGlobal.setTotBuildQty(rs.getInt("qty"));
            } else {
                objGlobal.setErrorNo("saveChuteBuilding: No Records found");
                return false;
            }
            if (!dbConnection.getServerDateTime(conRobo)) {
                objGlobal.setErrorNo("saveChuteBuilding:003");
                return false;
            }
            rs = dbConnection.getResultSet("select pallettype,Division from bfldata.dbo.ShopinShop where SubShop='" + shopName + "'", conRobo);
            if (rs.next()) {
                palletTyp = rs.getString("pallettype");
                div = rs.getString("Division");
            } else {
                objGlobal.setErrorMessage("Invalid Dataname");
                objGlobal.setErrorNo("saveChuteBuilding:004");
                return false;
            }
            if (TextUtils.isEmpty(palletTyp) || TextUtils.isEmpty(div)) {
                objGlobal.setErrorMessage("Plallet Type or Division is empty");
                objGlobal.setErrorNo("saveChuteBuilding:004");
                return false;
            }
            if(div.equals("TCM")) buildtype="TCM";
            if(objGlobal.getWarehouseCountry().equals("KSA")) buildtype="USA";
            objBuildingGlobal.setBoxNo("");
            objBuildingGlobal.setBoxNo(getBoxNumber(buildtype));
            if (TextUtils.isEmpty(objBuildingGlobal.getBoxNo())) {
                objGlobal.setErrorMessage("Box number error");
                objGlobal.setErrorNo("saveChuteBuilding:005");
                return false;
            }
            conRobo.setAutoCommit(false);
            objGlobal.getConnection().setAutoCommit(false);
            if (buildtype.equals("TCM")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.TcmboxesHeader (Boxno,TrnDate,Time1,UserId,TotId,Whouse) values ('" + objBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ",'" + toteId + "','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                    conRobo.rollback();
                    objGlobal.getConnection().rollback();
                    objGlobal.setErrorNo("saveChuteBuilding:008");
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.TCMBoxes (BoxNo,TrnDate,Time1,countedby,Itemcode,Qty,PhaseType,Closed,Userid,Remarks,OldBoxNo) " +
                        "select '" + objBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + objGlobal.getUserName() + "',Itemcode,sum(Qty)," +
                        "'" + palletTyp + "','N'," + objGlobal.getUserId() + ",'" + shopName + "','AUTO' from tmpBoxBuild where devicename='" + objGlobal.getDeviceName() + "' group by Itemcode", objGlobal.getConnection())) {
                    conRobo.rollback();
                    objGlobal.getConnection().rollback();
                    objGlobal.setErrorNo("saveChuteBuilding:009");
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxHead(BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2," +
                        "WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) values ('" + objBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," +
                        "'','" + objGlobal.getUserName() + "','" + shopName + "'," + objGlobal.getUserId() + ",'" + palletTyp + "','N','','AUTO','','','" + objGlobal.getWarehouse() + "','" + palletTyp + "','" + objGlobal.getUserName() + "',''," +
                        "'','" + shopName + "','" + toteId + "')", objGlobal.getConnection())) {
                    conRobo.rollback();
                    objGlobal.getConnection().rollback();
                    objGlobal.setErrorNo("saveChuteBuilding:008");
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objBuildingGlobal.getBoxNo() + "',Itemcode,sum(Qty),0,'',itemcode from " +
                        "tmpBoxBuild where devicename='" + objGlobal.getDeviceName() + "' group by Itemcode", objGlobal.getConnection())) {
                    conRobo.rollback();
                    objGlobal.getConnection().rollback();
                    objGlobal.setErrorNo("saveChuteBuilding:009");
                    return false;
                }
            }
            //insert chute status *****************************************
            if (!dbConnection.insertUpdate("insert into ChuteCheckout values('" + chuteId + "','" + toteId + "','" + shopId + "','" + shopName + "','" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "','" + objBuildingGlobal.getBoxNo() + "'," + objGlobal.getUserId() + ")", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:010");
                return false;
            }
            if (!dbConnection.insertUpdate("insert into ChuteConfigurationlog (ChuteId,ShopId,ShopName,TotId,Trndate,userId,direction) " +
                    "select ChuteId,ShopId,ShopName,TotId,getdate(),'" + objGlobal.getUserId() + "','OUT' from ChuteConfiguration where chuteid='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:011");
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteConfiguration set TotId='' where ChuteId='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:012");
                return false;
            }
            /*if (!dbConnection.insertUpdate("insert into SortTask values('" + objGlobal.getServerDate() + "','" + toteId + "','N/A'," +
                    "'" + shopId + "','" + shopName + "','" + objBuildingGlobal.getBoxNo() + "'," + objGlobal.getUserId() + ",'" + chuteId + "','Y','Y','N/A')", con)) {
                con.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("transferReceipt:013");
                return false;
            }*/
            if (!dbConnection.insertUpdate("insert into ChuteStatusLog select ChuteId,Status,'2','" + objGlobal.getUserId() + "',getdate() " +
                    "from ChuteIdMaster where chuteid='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:014");
                return false;
            }
            if (!dbConnection.insertUpdate("update ChuteIdMaster set Status='2' where ChuteId='" + chuteId + "'", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:015");
                return false;
            }
            if (!dbConnection.insertUpdate("update SortingConformationDetail set TransferNo='" + objBuildingGlobal.getBoxNo() + "' where TransferNo='' and " +
                    "ChuiteId='" + chuteId + "' and ShopId='" + shopId + "'", conRobo)) {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
                objGlobal.setErrorNo("saveChuteBuilding:016");
                return false;
            }
            conRobo.commit();
            objGlobal.getConnection().commit();
            conRobo.setAutoCommit(true);
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception exception) {
            try {
                conRobo.rollback();
                objGlobal.getConnection().rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("transferReceipt:sqlException:1: " + sqlException.toString());
                return false;
            }
            objGlobal.setErrorMessage(":transferReceipt:exception:2: " + exception.toString());
            return false;
        }
    }

    private String getBoxNumber(String buildtype) {
        try {
            int autoSn = 0;
            String suff = "";
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(d);
            String yr = String.valueOf(year.substring(2, 4));
            if (buildtype.equals("TCM")) {
                suff = "T" + yr + "/";
                rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from bfldata.dbo.TcmboxesHeader where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
                if (rs.next()) {
                    autoSn = Integer.parseInt(rs.getString("en").toString());
                }
            } else {
                suff = "U" + yr + "/";
                if(objGlobal.getCountryCode().equals("KSA")) suff = "S" + yr + "/";
                rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from usa.dbo.UPCBoxHead where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
                if (rs.next()) {
                    autoSn = Integer.parseInt(rs.getString("en").toString());
                }
            }
            return suff + String.format("%06d", autoSn);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("CheckingControl:getBoxNumber:" + ex.toString());
            return "";
        }
    }

    public boolean checkValidChuteId(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select * from ChuteIdMaster where ChuteId='" + chuteId + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:checkValidChuteId:" + ex.toString());
            return false;
        }
    }

    public boolean checkValidToteId(String toteId) {
        try {
            if (objGlobal.getWorkLocation().equals("KSA"))
                rs = dbConnection.getResultSet("select * from ROBOTICS.dbo.TotIdMaster where TotId='" + toteId + "'", objGlobal.getConnection());
            else
                rs = dbConnection.getResultSet("select * from bfldata.dbo.BlueToteIDMaster where ToteID='" + toteId + "'", objGlobal.getConnection());
            if (rs.next()) {
                return true;
            } else {
                objGlobal.setErrorMessage("Invaid Tote Id, " + toteId);
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("validateChuteError:checkValidToteId:" + ex.toString());
            return false;
        }
    }

    public boolean validateChuteError(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select * from ErrChuteId where ChuteId='" + chuteId + "' and ErrType='DGR'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("validateChuteError:validateChuteError:" + ex.toString());
            return false;
        }
    }

    public boolean validateTotidUsed(String toteId) {
        try {
            rs = dbConnection.getResultSet("select * from SortTask where ToteId='" + toteId + "' and TrnDate='" + objGlobal.getServerDate() + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:validateTotidUsed:" + ex.toString());
            return false;
        }
    }

    public String getShopIdFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ShopId", "ChuteId", chuteId);
    }

    public String getTotIdAlreadyAssignFromChuteId(String totId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ChuteId", "TotId", totId);
    }

    public String getShopnameFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "ShopName", "ChuteId", chuteId);
    }

    public String getToteIdFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteConfiguration", "TotId", "ChuteId", chuteId);
    }

    public String getShopToteidType(String shopname) {
        StringBuilder totetype = new StringBuilder();
        try {
            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.ToteTypes where shopname='" + shopname + "'", conRobo);
            while (rs.next()) {
                if (totetype.length() > 0) totetype.append(",");
                totetype.append(rs.getString("ToteType"));
            }
            return totetype.toString();
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:validateTotidUsed:" + ex.toString());
            return "";
        }
    }

    public String getStatusFromChuteId(String chuteId) {
        return dbConnection.stringReturn(conRobo, "ChuteIdMaster", "Status", "ChuteId", chuteId);
    }

    public boolean getLastChuteInOut(String chuteId) {
        try {
            rs = dbConnection.getResultSet("select top 1 direction=direction+' - '+left(cast(TrnTime as varchar),8) from vChuteInOut where ChuteId='" + chuteId + "' order by TrnDate desc,TrnTime desc", conRobo);
            if (rs.next()) {
                objBuildingGlobal.setChuteLastInOut(rs.getString("direction").toString());
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:getLastChuteInOut:" + ex.toString());
            return false;
        }
    }

    public String getChuteStatus(String chuteId) {
        switch (getStatusFromChuteId(chuteId)) {
            case "0":
                return "Normal";
            case "1":
                return "Full";
            case "2":
                return "Disable";
            case "3":
                return "Unknown";
        }
        return "";
    }

    public boolean transferPendingInChuteId(String chuteId, String shopId) {
        try {
            rs = dbConnection.getResultSet("select * from SortingConformationDetail where TransferNo='' and " +
                    "ChuiteId='" + chuteId + "' and ShopId='" + shopId + "'", conRobo);
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:checkPendingTransferInChuteId:" + ex.toString());
            return false;
        }
    }

    public ArrayList<BuildingItemTicket> itemsForBuilding(String chuteId, String shopId) {
        ArrayList<BuildingItemTicket> listBuildingItemTicket = new ArrayList<BuildingItemTicket>();
        int totQty = 0;
        try {
            rs = dbConnection.getResultSet("select itemcode,description='',qty=sum(qty) from SortingConformationDetail where " +
                    "TransferNo='' and ChuiteId='" + chuteId + "' and ShopId='" + shopId + "' group by itemcode", conRobo);
            while (rs.next()) {
                listBuildingItemTicket.add(new BuildingItemTicket(rs.getString("itemcode").toString(),
                        rs.getString("description").toString(), rs.getInt("qty")));
                totQty = totQty + rs.getInt("qty");
            }
            objBuildingGlobal.setTotBuildQty(totQty);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:loadTransferItemsAll:" + ex.toString());
            return listBuildingItemTicket;
        }
        return listBuildingItemTicket;
    }

    public boolean validateTotIsUsed(String totId) {
        try {
            rs = dbConnection.getResultSet("select distinct BoxNo from usa.dbo.UPCBoxHead where Closed='N' and ToteID='" + totId + "' union all select distinct b.BoxNo from " +
                    "BFLDATA.dbo.TCMBoxes a,BFLDATA.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno and a.Closed='N' and b.TotId='" + totId + "'", objGlobal.getConnection());
            if (rs.next()) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BuildingControl:validateTotIsUsed:" + ex);
            return false;
        }
    }
}
