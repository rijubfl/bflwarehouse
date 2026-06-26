package com.bflgroup.warehouse.ui.palletbuilding;

import android.text.TextUtils;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PalletBuildingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PalletBuildingGlobal objPalletBuildingGlobal = PalletBuildingGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    private ResultSet rsDet;

    public PalletBuildingControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PalletBuildingControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("PalletBuildingControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateBoxTotUsa(String boxTot, int boxQty) {
        String box = "", tote = "", palletType = "", groupcode = "", qty = "0", remarks = "", boxPrepare = "",lpmDate = "",orapoNo;
        String isDutyPaid = "", contNo = "";
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(boxTot)) {
            objGlobal.setErrorMessage("Please scan Box or Tote");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select *,Qty=(select sum(qty) from usa.dbo.UPCBoxDet where BoxNo=a.boxno) from usa.dbo.upcboxhead a " +
                    "where (boxno='" + boxTot + "' or toteid='" + boxTot + "') and closed='N'", objGlobal.getConnection());
            if (rs.next()) {
                box = rs.getString("boxno").toString().toUpperCase();
                tote = rs.getString("toteid").toString().toUpperCase();
                palletType = rs.getString("pallettype").toString().toUpperCase();
                groupcode = rs.getString("groupcode").toString().toUpperCase();
                remarks = rs.getString("remarks").toString().toUpperCase();
                qty = rs.getString("Qty").toString();
                boxPrepare = rs.getString("PreparedBy").toString();
                lpmDate = rs.getString("LPMDt");
                orapoNo = rs.getString("OrapoNo");

//                // duty paid check
//                if (box.startsWith("U")) {
//                    isDutyPaid = "N";
//                } else {
//                    if (box.startsWith("R")) {
//                        rs = dbConnection.getResultSet("select top 1 RoboContno from usa..vUPCBoxDet where BoxNo = '" + box + "'", objGlobal.getConnection());
//                        if (rs.next()) {
//                            if (!rs.getString("RoboContno").equals("") && rs.getString("RoboContno") != null) {
//                                contNo = rs.getString("RoboContno");
//                            }
//                        }
//                    } else {
//                        contNo = box.split("-")[0];
//                    }
//                    rs = dbConnection.getResultSet("select top 1 DutyPaid from bfldata.dbo.ContColorHeader where Contno = '" + contNo + "'", objGlobal.getConnection());
//                    if (rs.next()) {
//                        isDutyPaid = rs.getString("DutyPaid");
//                    }
//                }
            } else {
                objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : Invalid Box or Box is closed, " + boxTot);
                return false;
            }
//            if (boxQty >= 1) {
//                rs = dbConnection.getResultSet("select top 1 DutyPaid from tmpPalletBuild where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
//                if (rs.next()) {
//                    if (!isDutyPaid.equals(rs.getString("DutyPaid"))) {
//                        if (isDutyPaid.equals("Y"))
//                            objGlobal.setErrorMessage("Duty Paid box: "+box+"; \nDuty-paid boxes cannot be build with non-duty-paid boxes.");
//                        else
//                            objGlobal.setErrorMessage("Non Duty Paid box: "+box+"; \nNon-duty-paid boxes cannot be build with Duty-paid boxes.");
//                        return false;
//                    }
//                }
//            }
            rs = dbConnection.getResultSet("select Location from racks..BinRack where boxno = '"+box+"'",objGlobal.getConnection());
            if (rs.next()){
                String location = rs.getString("Location");
                objGlobal.setErrorMessage("The box is located in "+location+". Please rack it out.");
                return false;
            }
            if (Global.getLpmOrapoChecking().equals("Y")){

                rs = dbConnection.getResultSet("select top 1 LPMDt,OrapoNo from bfldata..tmpPalletBuild where DeviceId = '"+objGlobal.getDeviceName()+"'",objGlobal.getConnection());
                if (rs.next()){
                    String palletLPMDate = rs.getString("LPMDt");
                    String palletOrapoNo = rs.getString("OrapoNo");
                    if (lpmDate != null && palletLPMDate !=null) {
                        if (!lpmDate.equals(palletLPMDate)) {
                            objGlobal.setErrorMessage("The LPM date for this box is different: " +lpmDate+ ". Please use another box with an LPM date of " + palletLPMDate + ".");
                            return false;
                        }
                    }
                    else if (lpmDate != null && palletLPMDate ==null){
                        objGlobal.setErrorMessage("The LPM date for this box is different: " + lpmDate + ". Please build it using boxes that do not have an LPM date.");
                        return false;
                    }
                    else if (lpmDate == null && palletLPMDate !=null){
                        objGlobal.setErrorMessage("This box does not have an LPM date. Please use another box with an LPM date of " + palletLPMDate + ".");
                        return false;
                    }


                    if (orapoNo != null && palletOrapoNo !=null) {
                        if (!orapoNo.equals(palletOrapoNo)) {
                            objGlobal.setErrorMessage("The Po Number for this box is different: " +orapoNo+ ". Please use another box with Po " + palletOrapoNo + ".");
                            return false;
                        }
                    }
                    else if (orapoNo != null && palletOrapoNo ==null){
                        objGlobal.setErrorMessage("The Po number for this box is different: " + orapoNo + ". Please build it using boxes that do not have Po number.");
                        return false;
                    }
                    else if (orapoNo == null && palletOrapoNo !=null){
                        objGlobal.setErrorMessage("This box does not have Po Number. Please use another box with Po number" + palletOrapoNo + ".");
                        return false;
                    }
                }

            }
            rs = dbConnection.getResultSet("select * from usa.dbo.BoXPallet where boxno='" + box + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : Box is already found in pallet, " + box);
                return false;
            }
            rs = dbConnection.getResultSet("select * from usa.dbo.openR1pallet where boxno='" + box + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : The Box No " + box + " is already opened for checking");
                return false;
            }
            rs = dbConnection.getResultSet("select cnt=count(distinct PalletType) from tmpPalletBuild where DeviceId='" + objGlobal.getDeviceName() + "' having " +
                    "count(distinct PalletType)>1", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Multiple Building Category is not allowed - " + box);
                if (!dbConnection.insertUpdate("delete from tmpPalletBuild where (toteid='" + boxTot + "' or boxno='" + boxTot + "') and " +
                        "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                return false;
            }
            if (!dbConnection.insertUpdate("delete from tmpPalletBuild where (toteid='" + boxTot + "' or boxno='" + boxTot + "') and " +
                    "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.palletType where report like '%online%' and pallettype = '" + palletType + "'", objGlobal.getConnection());
            if (rs.next()) {
                rs = dbConnection.getResultSet("select Boxno1 = LEFT(BoxNo, LEN(BoxNo) - CHARINDEX('-', REVERSE(BoxNo))) from bfldata.dbo.tmpPalletBuild where  BoxNo like '%AEINT%' and DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                String[] box1 = box.split("-");
                Log.e("Box", box1[0]);
                if (rs.next()) {
                    Log.e("tmpBuildBox", rs.getString("Boxno1"));
                    if (!box1[0].equals(rs.getString("Boxno1"))) {
                        objGlobal.setErrorMessage("Multiple Container is not allowed  for PB PalletType- " + box);
                        return false;
                    }
                }
            }

            if (!dbConnection.insertUpdate("insert into tmpPalletBuild (DeviceId,UserId,ToteId,BoxNo,BoxRemarks,Qty,PalletType,GroupCode,BoxPrepare,DutyPaid,LPMDt,OraPoNo) values ('" + objGlobal.getDeviceName() + "'," +
                    "" + objGlobal.getUserId() + ",'" + tote + "','" + box + "','" + remarks + "'," + qty + ",'" + palletType + "','" + groupcode + "','" + boxPrepare + "','" + isDutyPaid + "',NULLIF('" + lpmDate + "','null'),NULLIF('" + orapoNo + "','null'))", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select cnt=count(distinct PalletType) from tmpPalletBuild where DeviceId='" + objGlobal.getDeviceName() + "' having " +
                    "count(distinct PalletType)>1", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Multiple Building Category is not allowed - " + box);
                if (!dbConnection.insertUpdate("delete from tmpPalletBuild where (toteid='" + boxTot + "' or boxno='" + boxTot + "') and " +
                        "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : " + e.toString());
            return false;
        }
    }

    public boolean validateMainUsa() {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select top 1 * from tmpPalletBuild where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : Please scan the boxes for the build");
                return false;
            }
            if (!dbConnection.insertUpdate("delete from tmpPltBldItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into tmpPltBldItems (userid,deviceid,itemcode) select distinct " + objGlobal.getUserId() + "," +
                    "'" + objGlobal.getDeviceName() + "',itemcode from usa.dbo.UPCBoxDet where boxno in(select boxno from tmpPalletBuild where " +
                    "deviceid='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set groupcode=b.groupcode from tmpPltBldItems a,usa.dbo.upcbarcodes b where " +
                    "a.deviceid='" + objGlobal.getDeviceName() + "' and a.itemcode= b.upc and isnull(a.groupcode,'')=''", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set groupcode=b.groupcode from tmpPltBldItems a,online.dbo.ManualUPCSize b where " +
                    "a.deviceid='" + objGlobal.getDeviceName() + "' and a.itemcode= b.upc and isnull(a.groupcode,'')=''", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set groupcode=b.groupcode from tmpPltBldItems a,hodata.dbo.ItemMaster b where " +
                    "a.deviceid='" + objGlobal.getDeviceName() + "' and a.itemcode= b.ItemCode and isnull(a.groupcode,'')=''", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set groupcode=b.groupcode from tmpPltBldItems a,usa.dbo.GenerateBarcode b where " +
                    "a.deviceid='" + objGlobal.getDeviceName() + "' and a.itemcode= b.barcode and isnull(a.groupcode,'') =''", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set groupcode=b.groupcode from tmpPltBldItems a,consignment.dbo.ItemMaster b where " +
                    "a.deviceid='" + objGlobal.getDeviceName() + "' and a.itemcode= b.ItemCode and isnull(a.groupcode,'')=''", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set department=b.department from tmpPltBldItems a,usa.dbo.USAPriority b where " +
                    "a.groupcode=b.groupCode and a.deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpPltBldItems set division=b.Division from tmpPltBldItems a,DeptStock b where a.department=b.department " +
                    "and a.deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            /*int nDivCount = 0;
            boolean bMuymuchoDiv = false;
            rs = dbConnection.getResultSet("select division,cnt=count(*) from tmpPltBldItems where division is not null and deviceid='" + objGlobal.getDeviceName() + "' group by division", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("division").toString().toUpperCase().equals("MUYMUCHO"))
                    bMuymuchoDiv = true;
                else
                    nDivCount++;
            }
            if (nDivCount > 0 && bMuymuchoDiv == true) {
                objGlobal.setErrorMessage("Mix not allowed for MUYMUCHO items");
                return false;
            }*/

            String boxno = "", pallettype = "", season = "", cShopEligible = "", cFShopEligible = "";
            int nLFLBUFFERitem = 0, nNonLFLBUFFERitem = 0;
            int nWinterCnt = 0, nSummerCnt = 0, nOthCnt = 0, mTotCnt = 0;

            if (!dbConnection.insertUpdate("update tmpPalletBuild set season=isnull(b.Season,''),ShopEligible=b.ShopEligible from tmpPalletBuild a, PalletType b where a.DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "a.pallettype=b.PalletType", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from tmpPalletBuild where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                boxno = rs.getString("boxno").toString().toUpperCase();
                pallettype = rs.getString("pallettype").toString().toUpperCase();
                season = rs.getString("season");
                season = (season != null) ? season.toUpperCase() : "";
                cShopEligible = rs.getString("ShopEligible");
                cShopEligible = (cShopEligible != null) ? cShopEligible.toUpperCase() : "";
                rsDet = dbConnection.getResultSet("select * from usa.dbo.BoXPallet where boxno='" + boxno + "'", objGlobal.getConnection());
                if (rsDet.next()) {
                    objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : Box is already found in pallet, " + boxno);
                    return false;
                }
                rsDet = dbConnection.getResultSet("select * from usa.dbo.openR1pallet where boxno='" + boxno + "'", objGlobal.getConnection());
                if (rsDet.next()) {
                    objGlobal.setErrorMessage("PalletBuildingControl.validateBoxTot : The Box No " + boxno + " is already opened for checking");
                    return false;
                }
                if (TextUtils.isEmpty(season)) {
                    season = "OTH";
                }
                if (pallettype.equals("LF")) {
                    nLFLBUFFERitem++;
                } else {
                    nNonLFLBUFFERitem++;
                }
                if (pallettype.equals("RF") || pallettype.equals("OH") || pallettype.equals("FW") || pallettype.equals("OW")) {
                } else {
                    if (season.trim().equals("W"))
                        nWinterCnt++;
                    if (season.trim().equals("S"))
                        nSummerCnt++;
                    if (season.trim().equals("OTH"))
                        nOthCnt++;
                }
                if (TextUtils.isEmpty(cFShopEligible)) cFShopEligible = cShopEligible;
                if (!cFShopEligible.equals(cShopEligible)) {
                    objGlobal.setErrorMessage("PalletBuildingControl.validateSave : Cannot proceed,Mix pallet type not allowed");
                    return false;
                }
                mTotCnt++;
            }
            if (mTotCnt == 0) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateSave : Please scan box or tote");
                return false;
            }
            if (nWinterCnt > 0 && nSummerCnt > 0) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateSave : Cannot proceed,Mix season not allowed for same Pallet");
                return false;
            }
            if (nLFLBUFFERitem > 0 && nNonLFLBUFFERitem > 0) {
                objGlobal.setErrorMessage("PalletBuildingControl.validateSave : Cannot proceed,Pallet type mix to LFLBUFFER not allowed for same Pallet");
                return false;
            }
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("savePallet:getServerDateTime:error");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:savePallet1:ex:" + ex.toString());
            return false;
        }
    }

    public boolean savePalletUsa(String remarks) {
        if (TextUtils.isEmpty(remarks)) {
            remarks = "Box Building in A-PDA/" + objGlobal.getUserName();
        } else {
            remarks = remarks + "/Box Building in A-PDA/" + objGlobal.getUserName();
        }
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(objGlobal.getWarehouse())) {
            objGlobal.setErrorNo("savePallet:Warehouse is empty");
            return false;
        }
        getPalletNoAutoUsa();
        if (TextUtils.isEmpty(objPalletBuildingGlobal.getPalletSno())) {
            objGlobal.setErrorNo("savePallet:Pallet SNo. Is empty");
            return false;
        }
        if (TextUtils.isEmpty(objPalletBuildingGlobal.getPalletNo())) {
            objGlobal.setErrorNo("savePallet:Pallet No. Is empty");
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into usapallets(Sn,TrnDate,PalletNo,UserId,Remarks,Closed,ContNo,WHouse) " +
                    "values (" + objPalletBuildingGlobal.getPalletSno() + ",'" + objGlobal.getServerDate() + "','" + objPalletBuildingGlobal.getPalletNo() + "'," +
                    "" + objGlobal.getUserId() + ",'" + remarks + "','N','','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into usapalletsdet(Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID) select " + objPalletBuildingGlobal.getPalletSno() + "," +
                    "BoxNo,'" + objPalletBuildingGlobal.getPalletNo() + "',BoxRemarks,Qty,BoxPrepare,'',BoxRemarks,ToteID from tmpPalletBuild a where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into usa.dbo.BoXPallet select BoxNo,'" + objPalletBuildingGlobal.getPalletNo() + "' from tmpPalletBuild where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into PrintFromPda(warehouse,PSystemName,PType,PItem,ReqUser,ReqDate,ReqTime,Printed) " +
                    "values ('" + objGlobal.getWarehouse() + "','" + objGlobal.getUserPrinterName() + "','UP','" + objPalletBuildingGlobal.getPalletNo() + "','" + objGlobal.getUserName() + "'," +
                    "'" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','N')", objGlobal.getConnection())) {
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("PalletBuildingControl:savePallet2:ex:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("PalletBuildingControl:savePallet3:e:" + e.toString());
                return false;
            }
            return false;
        }
    }

    public boolean getPalletNoAutoTcm() {
        int pltSn = 0;
        try {
            rs = dbConnection.getResultSet("select sn=max(sn)+1 from R1PalletHead", objGlobal.getConnection());
            if (rs.next()) {
                objPalletBuildingGlobal.setPalletSno(rs.getString("sn").toString());
            }
            rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7)) from R1PalletHead where palletno like 'PLT%'", objGlobal.getConnection());
            if (rs.next()) {
                pltSn = Integer.parseInt(rs.getString("sn").toString().substring(0, 6));
                if (rs.getString("sn").toString().substring(rs.getString("sn").toString().length() - 1).equals("B")) {
                    objPalletBuildingGlobal.setPalletNo("PLT" + String.format("%06d", pltSn + 1) + "A");
                } else {
                    objPalletBuildingGlobal.setPalletNo("PLT" + String.format("%06d", pltSn) + "B");
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:getPalletNoAutoTcm:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean getPalletNoAutoUsa() {
        int pltSn = 0;
        String palletPrefix = "USA";
        if (objGlobal.getWorkLocation().equals("UAE")) {
            palletPrefix = "UAE";
        }
        try {
            rs = dbConnection.getResultSet("select sn=Max(sn)+1 from USAPallets", objGlobal.getConnection());
            if (rs.next()) {
                objPalletBuildingGlobal.setPalletSno(rs.getString("sn").toString());
            }
            rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7))+1 from USApallets where palletno like '" + palletPrefix + "%'", objGlobal.getConnection());
            if (rs.next()) {
                pltSn = Integer.parseInt(rs.getString("sn").toString());
            }
            objPalletBuildingGlobal.setPalletNo(palletPrefix + String.format("%06d", pltSn));
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:getPalletNoAutoUsa:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean forPrint(String palletno) {
        objPalletBuildingGlobal.setpPalletno("");
        objPalletBuildingGlobal.setpBoxcnt("");
        objPalletBuildingGlobal.setpRemarks("");
        objPalletBuildingGlobal.setpPallettype("");
        objPalletBuildingGlobal.setpTypename("");
        objPalletBuildingGlobal.setpGroupname("");
        objPalletBuildingGlobal.setpPreparedby("");
        objPalletBuildingGlobal.setpDate("");
        objPalletBuildingGlobal.setpTime("");
        String sn = "";
        try {
            rs = dbConnection.getResultSet("select sn,PalletNo,Remarks,BoxCnt=isnull((select count(distinct InvNo) from BFLDATA.dbo.USAPalletsDet where Sn=a.Sn),0),preparedby=(select UserName from " +
                    "BFLDATA.dbo.PdaUsers where UserId=a.UserId),dt=convert(varchar,getdate(),103),tm=convert(varchar,getdate(),8) from bfldata.dbo.USAPallets a where palletno='" + palletno + "'", objGlobal.getConnection());
            if (rs.next()) {
                sn = rs.getString("sn");
                objPalletBuildingGlobal.setpPalletno(rs.getString("PalletNo"));
                objPalletBuildingGlobal.setpBoxcnt(rs.getString("BoxCnt"));
                objPalletBuildingGlobal.setpRemarks(rs.getString("Remarks"));
                objPalletBuildingGlobal.setpPreparedby(rs.getString("preparedby"));
                objPalletBuildingGlobal.setpDate(rs.getString("dt"));
                objPalletBuildingGlobal.setpTime(rs.getString("tm"));
            }
            rs = dbConnection.getResultSet("select groupnm=isnull((select Description from HODATA.dbo.ItemGroup where GroupCode=a.GroupCode),''),typename=isnull((select typename from " +
                    "BFLDATA.dbo.PalletType where PalletType=a.pallettype),'') from USA.dbo.UPCBoxHead a where BoxNo in(select top 1 InvNo from BFLDATA.dbo.USAPalletsDet where Sn=" + sn + ")", objGlobal.getConnection());
            if (rs.next()) {
                objPalletBuildingGlobal.setpTypename(rs.getString("typename"));
                objPalletBuildingGlobal.setpGroupname(rs.getString("groupnm"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:forPrint:" + ex);
            return false;
        }
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from tmpPalletBuild where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:clearTable:" + ex.toString());
            return false;
        }
        return true;
    }

    ArrayList<PalletBuildingBoxTicket> loadPalletBuildBoxDetail() {
        ArrayList<PalletBuildingBoxTicket> listPalletBuildBoxDetail = new ArrayList<PalletBuildingBoxTicket>();
        try {
            listPalletBuildBoxDetail.clear();
            rs = dbConnection.getResultSet("select * from tmpPalletBuild where deviceid='" + objGlobal.getDeviceName() + "' order by id desc", objGlobal.getConnection());
            while (rs.next()) {
                listPalletBuildBoxDetail.add(new PalletBuildingBoxTicket(rs.getString("toteId").toString(),
                        rs.getString("boxNo").toString(), rs.getString("palletType").toString(),
                        rs.getString("boxRemarks").toString(), rs.getString("qty").toString()));
            }
            objPalletBuildingGlobal.setTotQty(0);
            objPalletBuildingGlobal.setTotCnt(0);
            rs = dbConnection.getResultSet("select TotQty=sum(qty),TotCnt=count(*) from tmpPalletBuild where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objPalletBuildingGlobal.setTotQty(rs.getInt("TotQty"));
                objPalletBuildingGlobal.setTotCnt(rs.getInt("TotCnt"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:loadPalletBuildBoxDetail:" + ex.toString());
            return null;
        }
        return listPalletBuildBoxDetail;
    }


}
