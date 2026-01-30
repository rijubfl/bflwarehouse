package com.bflgroup.warehouse.ui.supplierboxgrn;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public boolean scanCarton(String contscan, String contid,String cartonId, String audit, String po) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (contscan.equals("Y")) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpSupplierBoxGrn select '" + objGlobal.getDeviceName() + "',ContNo,bol,'',itemcode,orgqty,'',null,'N' " +
                        "from usa.dbo.USAOrgFile where ContNo='" + contid + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            } else {
                rs = dbConnection.getResultSet("select top 1 * from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and ContId='" + contid + "' and " +
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

    public ArrayList<SupplierBoxGRNScannedBoxTicket> loadSupplierBoxGRNScannedBox(String ContId) {
        ArrayList<SupplierBoxGRNScannedBoxTicket> listSupplierBoxGRNScannedBoxTicket = new ArrayList<SupplierBoxGRNScannedBoxTicket>();
        try {
            SupplierBoxGRNGlobal.setTotalScanQty(0);
            SupplierBoxGRNGlobal.setTotalScanBoxCnt(0);
            rs = dbConnection.getResultSet("select ContId,CartonId,PO,OrgQty=sum(OrgQty),AuditReq,ScanDtTm from bfldata.dbo.tmpSupplierBoxGrn where " +
                    "DeviceId='" + objGlobal.getDeviceName() + "' and scan='Y' and ContId='" + ContId + "' group by ContId,CartonId,PO,AuditReq,ScanDtTm order by ScanDtTm desc", objGlobal.getConnection());
            while (rs.next()) {
                listSupplierBoxGRNScannedBoxTicket.add(new SupplierBoxGRNScannedBoxTicket(rs.getString("CartonId"),rs.getString("PO"),rs.getInt("OrgQty"),rs.getString("AuditReq")));
            }
            rs = dbConnection.getResultSet("select cnt=count(distinct CartonId),qty=sum(OrgQty) from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "scan='Y' and ContId='" + ContId + "'", objGlobal.getConnection());
            if (rs.next()) {
                SupplierBoxGRNGlobal.setTotalScanQty(rs.getInt("qty"));
                SupplierBoxGRNGlobal.setTotalScanBoxCnt(rs.getInt("cnt"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:listSupplierBoxGRNScannedBoxTicket:" + ex);
        }
        return listSupplierBoxGRNScannedBoxTicket;
    }

    public boolean clearAll() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpSupplierBoxGrn where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SupplierBoxGRNControl:grnSave:ex2:" + ex);
            return false;
        }
    }

    public boolean validateSupplierBoxGrn(String contId) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.SuppBoxGrnHeader where ContainerID='" + contId + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Container ID (" + contId + ") is already saved");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 contno from usa.dbo.usaorgfile where contno='" + contId + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Container ID (" + contId + ") is Invalid");
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
