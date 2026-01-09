package com.bflgroup.warehouse.ui.palletstatus;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PalletStatusControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PalletStatusGlobal objPalletStatusGlobal = PalletStatusGlobal.getInstance();

    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public PalletStatusControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PalletStatusControl : Connection error");
        }
    }
    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("PalletStatusControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }
    boolean getPalletStatus(String scan, String warehouse) {
        String pltType = "", boxno = "", toteid = "", palletno = "", status = "", buildCategory = "", type = "", checkingType="", golden="N";
        boolean found = false;
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(scan)) {
            objGlobal.setErrorMessage("Please scan Pallet / Box / Tote");
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorMessage("getPalletStatus:Datetime Error");
                return false;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno,toteid=isnull(toteid,''),palletno=isnull(palletno,''),PalletType=isnull(PalletType,'') from usa.dbo.vupcboxdet where closed='N' and Toteid='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "UBOX";
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno,toteid=isnull(toteid,''),palletno=isnull(palletno,''),PalletType=isnull(PalletType,'') from usa.dbo.vupcboxdet where closed='N' and BoxNo='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "UBOX";
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno,toteid=isnull(TotId,''),palletno=isnull(palletno,''),PalletType=isnull(pallettype,'') from BFLDATA.dbo.vr1pallet where closed='N' and Totid='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "TBOX";
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno,toteid=isnull(TotId,''),palletno=isnull(palletno,''),PalletType=isnull(pallettype,'') from BFLDATA.dbo.vr1pallet where closed='N' and BoxNo='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "TBOX";
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno='',toteid='',palletno=isnull(palletno,''),PalletType=isnull(PalletType,'') from usa.dbo.vUPCBoxDet where closed='N' and PalletNo='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "UPLT";
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select top 1 boxno='',toteid='',palletno=isnull(palletno,''),PalletType=isnull(PalletType,'') from BFLDATA.dbo.vR1Pallet where closed='N' and PalletNo='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    boxno = rs.getString("boxno");
                    toteid = rs.getString("toteid");
                    palletno = rs.getString("palletno");
                    pltType = rs.getString("PalletType");
                    type = "TPLT";
                    found = true;
                }
            }
            if (!found) {
                objGlobal.setErrorMessage("Box/Tote/Pallet invalid or closed");
                return false;
            }
            if (type.equals("TBOX")) {
                buildCategory = "TCM";
            }
            if (!dbConnection.insertUpdate("drop table if exists #showcategory", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("create table #showcategory(BuildingCategory varchar(200),Division varchar(200),Department varchar(200))", objGlobal.getConnection())) {
                return false;
            }
            if (type.equals("UBOX")) {
                if (!dbConnection.insertUpdate("insert into #showcategory select BuildingCategory,division,department from hodata.dbo.vItemMaster where Itemcode in(select itemcode from usa.dbo.UPCBoxDet " +
                        "where BoxNo='" + boxno + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (type.equals("UPLT")) {
                if (!dbConnection.insertUpdate("insert into #showcategory select BuildingCategory,division,department from hodata.dbo.vItemMaster where Itemcode in(select itemcode from usa.dbo.vUPCBoxDet " +
                        "where PalletNo='" + palletno + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (type.equals("TPLT")) {
                if (!dbConnection.insertUpdate("insert into #showcategory select BuildingCategory,division,department from hodata.dbo.vItemMaster where Itemcode in(select itemcode from bfldata.dbo.vr1pallet " +
                        "where PalletNo='" + palletno + "')", objGlobal.getConnection())) {
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select distinct BuildingCategory from #showcategory", objGlobal.getConnection());
            while (rs.next()) {
                if (buildCategory.isEmpty()) {
                    buildCategory = rs.getString("BuildingCategory");
                } else {
                    buildCategory = buildCategory + " | " + rs.getString("BuildingCategory");
                }
            }
            rs = dbConnection.getResultSet("select * from #showcategory where upper(department) like '%LFL%' or upper(department) like '%HIGH%'", objGlobal.getConnection());
            if (rs.next()) {
                golden = "Y";
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.PalletType where PalletType='" + pltType + "' and DirectProduction='Y'", objGlobal.getConnection());
            if (rs.next()) {
                status = "PRODUCTION - D";
            } else {
                rs = dbConnection.getResultSet("select * from tempdata.dbo.SIMProdReadyPalletsList where (PalletNo='" + palletno + "' or boxno='" + boxno + "')", objGlobal.getConnection());
                if (rs.next()) {
                    status = "PRODUCTION - S";
                    checkingType = rs.getString("checkingType");
                } else {
                    rs = dbConnection.getResultSet("select * from usa..OverrideMaxQtyHeader where boxno='"+boxno+"' and DATEDIFF(DAY, GETDATE(), EntryDate) BETWEEN -2 AND 0 ", objGlobal.getConnection());
                    if (rs.next()) {
                        status = "BOX PICKING ";
                    } else {
                        status = "RACK ";
                    }
                   // status = "RACK";
                }
            }

            if (status.equals("RACK")) {
                String processNo = "", prodDate = "";
                rs = dbConnection.getResultSet("select top 1 ProcessNo,ProdDate=convert(varchar,getdate(),103) from TEMPDATA.dbo.SIMProdReadyPalletsList order by ProcessNo desc", objGlobal.getConnection());
                if (rs.next()) {
                    processNo = rs.getString("processNo");
                    prodDate = rs.getString("ProdDate");
                }
                rs = dbConnection.getResultSet("select * from TEMPDATA.dbo.DelSIMProdReadyPalletsList where ProdDate>=cast(getdate()-1 as date) and (BoxNo='" + boxno + "' or PalletNo='" + palletno + "')", objGlobal.getConnection());
                if (rs.next()) {
                    if (!dbConnection.insertUpdate("insert into TEMPDATA.dbo.SIMProdReadyPalletsList select " + processNo + ",'" + prodDate + "',PalletNo,BoxNo,PalletType,iDepartment,0,0,0,0,0,0,PLTItemType," +
                            "CheckingType,Warehouse,Rack,convert(varchar,getdate(),103),convert(varchar,getdate(),8),Active from TEMPDATA.dbo.DelSIMProdReadyPalletsList where " +
                            "ProdDate>=cast(getdate()-1 as date) and (BoxNo='" + boxno + "' or PalletNo='" + palletno + "')", objGlobal.getConnection())) {
                        return false;
                    }
                    checkingType = rs.getString("checkingType");
                    status = "PRODUCTION - A";
                }
            }

            if(!toteid.isEmpty()) {
                if (!dbConnection.insertUpdate("insert into racks.dbo.BinPutAwayHistory select Warehouse,convert(varchar,getdate(),103),convert(varchar,getdate(),8),ToteId,BoxNo,'OUT',Location,0,'STS-PDA','BLACKBOX' from " +
                        "RACKS.dbo.BinRack where Warehouse='BLACKBOX' and ToteId='" +toteid + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("delete from RACKS.dbo.BinRack where Warehouse='BLACKBOX' and ToteId='" + toteid + "'", objGlobal.getConnection())) {
                    return false;
                }
            }
            objPalletStatusGlobal.setPltType(pltType);
            objPalletStatusGlobal.setBoxno(boxno);
            objPalletStatusGlobal.setToteid(toteid);
            objPalletStatusGlobal.setPalletno(palletno);
            objPalletStatusGlobal.setStatus(status);
            objPalletStatusGlobal.setBuildcategory(buildCategory);
            objPalletStatusGlobal.setCheckingType(checkingType);
            objPalletStatusGlobal.setGolden(golden);

            if (!dbConnection.insertUpdate("insert into bfldata.dbo.PalletStatusScan (PalletNo,Status,UserId,TrnDate,TrnTime,Boxno,Toteid,warehouse,OrgPalletno) values ('" + scan + "','" + status + "'," + objGlobal.getUserId() + ",'" + objGlobal.getServerDate() + "'," +
                    "'" + objGlobal.getServerTime() + "', '"+ objPalletStatusGlobal.getBoxno() + "','"+ objPalletStatusGlobal.getToteid() + "', '"+warehouse+"' ,'" + objPalletStatusGlobal.getPalletno() + "')", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PalletStatusControl.getPalletStatus : " + e.toString());
            return false;
        }
    }

    public List<String> getWarehouse(String warehouse){
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();

            arr.add("Select Warehouse");
            rs = dbConnection.getResultSet("select distinct department from bfldata..warehouseDepartment where warehouse = '"+warehouse+"'", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("department"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }


    ArrayList<PalletStatusTicket> loadPalletStatus() {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<PalletStatusTicket> listPalletStatusTicket = new ArrayList<PalletStatusTicket>();
        try {
            listPalletStatusTicket.clear();
            rs = dbConnection.getResultSet("select top 10 palletno,status,dttime=convert(varchar,trndate,103)+' '+convert(varchar,trntime,8) from bfldata.dbo.PalletStatusScan " +
                    "where userid=" + objGlobal.getUserId() + " order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                listPalletStatusTicket.add(new PalletStatusTicket(rs.getString("palletno").toString(),
                        rs.getString("status").toString(), rs.getString("dttime").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletStatusControl:loadPalletStatus:" + ex.toString());
            return null;
        }
        return listPalletStatusTicket;
    }

}
