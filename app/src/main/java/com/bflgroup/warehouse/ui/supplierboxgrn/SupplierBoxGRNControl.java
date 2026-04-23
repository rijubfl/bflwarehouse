package com.bflgroup.warehouse.ui.supplierboxgrn;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierBoxGRNControl {

    private final DBConnection dbConnection = new DBConnection();
    private final Global objGlobal = Global.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public SupplierBoxGRNControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("SupplierBoxGRNControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean scanCarton(String contscan, String contid,String cartonId, String audit) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (contscan.equals("Y")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpSupplierBoxGrn(DeviceId,ContId,CartonId,PO,LogBox,OrgQty,AuditReq,ScanDtTm,scan,saveScan) select '" + objGlobal.getDeviceName() + "'," +
                        "ContNo,bol,ORAPONo,'N',sum(orgqty),'',null,'N','N' from usa.dbo.USAOrgFile where ContNo='" + contid + "' group by ContNo,bol,ORAPONo", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpSupplierBoxGrn set AuditReq=b.AuditRequired,ScanDtTm=getdate(),saveScan='Y' from bfldata.dbo.tmpSupplierBoxGrn a," +
                        "bfldata.dbo.SuppBoxGrnDetail b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.ContId='" + contid + "' and b.ContainerID='" + contid + "' and a.CartonId=b.CartonID", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            } else {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and ContId='" + contid + "' and " +
                        "CartonId='" + cartonId + "'", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getString("scan").equals("Y")) {
                        objGlobal.setErrorMessage("The Carton ID (" + cartonId + ") has already been scanned");
                        return false;
                    }
                } else {
                    objGlobal.setErrorMessage("Carton ID is not in the manifest or Invalid");
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpSupplierBoxGrn set scan='Y',ScanDtTm=getdate(),AuditReq='" + audit + "' where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                        "ContId='" + contid + "' and CartonId='" + cartonId + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:validateGrn:" + ex);
            return false;
        }
    }

    List<String> loadPo(String contno) {
        if (!checkConnection()) {
            return null;
        }
        List<String> arr;
        arr = new ArrayList<String>();
        try {
            rs = dbConnection.getResultSet("select PO='--Select PO--' union all select distinct PO from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "ContId='" + contno + "' order by po", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("PO"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:loadPo:" + ex);
            return null;
        }
        return arr;
    }

    public String generateLogisticBox(String contid, String po) {
        if (!checkConnection()) {
            return "";
        }
        try {
            String logPalletNo = "";
            int autoSn = 0;
            rs = dbConnection.getResultSet("select en=isnull(max(cast(REPLACE(right(palletno,3),'/','')as int)),0)+1 from usa.dbo.UsaPallets where contno='" + contid + "'", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en"));
            }
            logPalletNo = contid + "-" + po + "/" + String.format("%03d", autoSn);
            return logPalletNo;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:validateGrn:" + ex);
            return "";
        }
    }

    public boolean generateLogisticBoxAndPrint(String contid, String po,String audit) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("validateCheckInOut:001:");
                return false;
            }
            SupplierBoxGRNGlobal.setLogNewBoxNo("");
            String logPalletNo = generateLogisticBox(contid, po);
            if (logPalletNo.isEmpty()) return false;
            SupplierBoxGRNGlobal.setLogNewBoxNo(logPalletNo);

            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpSupplierBoxGrn(DeviceId,ContId,CartonId,PO,LogBox,OrgQty,AuditReq,ScanDtTm,scan,saveScan) " +
                    "values ('" + objGlobal.getDeviceName() + "','" + contid + "','" + SupplierBoxGRNGlobal.getLogNewBoxNo() + "','" + po + "','Y',0,'" + audit + "',getdate(),'Y','N')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into usa.dbo.UsaPallets(Contno,PalletNo,trndate,userid,Closed,GroupName,Remarks,whouse) values ('" + contid + "','"+ SupplierBoxGRNGlobal.getLogNewBoxNo() +"'," +
                    "'" + objGlobal.getServerDate() + "'," + objGlobal.getUserId() + ",'N','','Auto pallet created from Supplier Box GRN','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:saveSupplierBoxGrn:ex1:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:saveSupplierBoxGrn:ex2:" + e);
                return false;
            }
            return false;
        }
    }

    public ArrayList<SupplierBoxGRNScannedBoxTicket> loadSupplierBoxGRNScannedBox(String ContId) {
        ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxTicket = new ArrayList<SupplierBoxGRNScannedBoxTicket>();
        try {
            SupplierBoxGRNGlobal.setTotalScanQty(0);
            SupplierBoxGRNGlobal.setTotalScanBoxCnt(0);
            rs = dbConnection.getResultSet("select ContId,CartonId,PO,LogBox,OrgQty,AuditReq,ScanDtTm from bfldata.dbo.tmpSupplierBoxGrn where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "' and (scan='Y' or saveScan='Y') and ContId='" + ContId + "' order by ScanDtTm desc", objGlobal.getConnection());
            while (rs.next()) {
                listSupplierBoxGRNScannedBoxTicket.add(new SupplierBoxGRNScannedBoxTicket(rs.getString("CartonId"),rs.getString("PO"),rs.getInt("OrgQty"),rs.getString("AuditReq")));
            }
            rs = dbConnection.getResultSet("select cnt=count(CartonId),qty=sum(OrgQty) from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "(scan='Y' or saveScan='Y') and ContId='" + ContId + "'", objGlobal.getConnection());
            if (rs.next()) {
                SupplierBoxGRNGlobal.setTotalScanQty(rs.getInt("qty"));
                SupplierBoxGRNGlobal.setTotalScanBoxCnt(rs.getInt("cnt"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:listSupplierBoxGRNScannedBoxTicket:" + ex);
        }
        return listSupplierBoxGRNScannedBoxTicket;
    }

    public boolean clearAll(String contid,boolean includeLogBox) {
        if (!checkConnection()) {
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (includeLogBox) {
                if (!dbConnection.insertUpdate("delete from usa.dbo.UsaPallets where contno='" + contid + "' and palletno in(select CartonId from bfldata.dbo.tmpSupplierBoxGrn " +
                        "where ContId='" + contid + "' and DeviceId='" + objGlobal.getDeviceName() + "' and scan='Y' and LogBox='Y')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:clearAll:ex1:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:clearAll:ex2:" + e);
                return false;
            }
            return false;
        }
    }

    public boolean deleteCartonID(String contid,String cartonId) {
        if (!checkConnection()) {
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("update bfldata.dbo.tmpSupplierBoxGrn set AuditReq='',scan='N',ScanDtTm=null where DeviceId='9a2b32eaba20abe1' and CartonId='2475198282'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("delete from usa.dbo.UsaPallets where contno='" + contid + "' and palletno='" + cartonId + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:deleteCartonID:ex1:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:deleteCartonID:ex2:" + e);
                return false;
            }
            return false;
        }
    }

    public boolean validateSupplierBoxGrn(String contId) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.ContColorHeader where Contno='" + contId + "' and EDI='Y'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Container ID (" + contId + ") EDI not enabled");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 contno from usa.dbo.usaorgfile where contno='" + contId + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Container ID (" + contId + ") is Invalid");
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.SuppBoxGrnHeader where GrnCompleted is not null and ContainerID='" + contId + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GRN for Container ID (" + contId + ") has already been completed");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:validateSupplierBoxGrn:ex1:" + ex);
            return false;
        }
    }

    public boolean saveSupplierBoxGrn(String contId,String remarks) {
        int srno = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("validateCheckInOut:001:");
                return false;
            }
            rs = dbConnection.getResultSet("select sn=isnull(max(srno),0)+1 from bfldata.dbo.SuppBoxGrnHeader", objGlobal.getConnection());
            if (rs.next()) {
                srno = rs.getInt("sn");
            }
            if (srno == 0) {
                objGlobal.setErrorMessage("Wrong SN");
                return false;
            }

            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.SuppBoxGrnHeader(SrNo,ContainerID,CreateDate,UserName,Remarks,Warehouse) " +
                    "values (" + srno + ",'" + contId + "',getdate(),'" + objGlobal.getUserName() + "','" + remarks + "','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.SuppBoxGrnDetail(SrNo,ContainerID,CartonID,PONumber,AuditRequired,CartonQty,AuditStatus) select " + srno + ",ContId,CartonID,po," +
                    "AuditReq,sum(orgqty),'' from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and scan='Y' and contid='" + contId + "' group by ContId,CartonID,po,AuditReq", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:saveSupplierBoxGrn:ex1:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("SupplierBoxGRNControl:saveSupplierBoxGrn:ex2:" + e);
                return false;
            }
            return false;
        }
    }

}
