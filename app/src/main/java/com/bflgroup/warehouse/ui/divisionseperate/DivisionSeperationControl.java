package com.bflgroup.warehouse.ui.divisionseperate;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DivisionSeperationControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private DivisionSeperationGlobal objDivisionSeperationGlobal = DivisionSeperationGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public DivisionSeperationControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("1 DivisionSeperationControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("3 DivisionSeperationControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateTransfer(String shopName, String trfno, boolean save) {
        boolean delAll=false;
        if (TextUtils.isEmpty(trfno) || TextUtils.isEmpty(shopName)) {
            objGlobal.setErrorMessage("Transfer Number or Shop name is blank");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            objDivisionSeperationGlobal.setDatabase("");
            objDivisionSeperationGlobal.setCostcode("");
            rs = dbConnection.getResultSet("select * from DataSettings where ShopName='" + shopName + "'", objGlobal.getConnection());
            if (rs.next()) {
                objDivisionSeperationGlobal.setDatabase(rs.getString("Dataname").toString());
                objDivisionSeperationGlobal.setCostcode(rs.getString("CostcodeTo").toString());
                objDivisionSeperationGlobal.setLoccode(rs.getString("LoccodeTo").toString());
            } else {
                objGlobal.setErrorMessage("Invalid shop");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.Transferheader where " +
                    "TrfNo='" + trfno + "' and CostCodeTo='" + objDivisionSeperationGlobal.getCostcode() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Transfer");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.RemoveItemsFromTransferActivate where ShopName='" + shopName + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                delAll = true;
            } else {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.GINDeleteItems where ShopName='" + shopName + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Transfer removal already done!");
                    return false;
                }
            }
            if(delAll) {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.RemoveItemsFromTransfer where ShopName='" + shopName + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Transfer removal already done!");
                    return false;
                }
            }
            objDivisionSeperationGlobal.setDelall(delAll);
            rs = dbConnection.getResultSet("select top 1 * from DATA2004.dbo.ExportPost where ShipNo  in (select cast(srno as varchar(20)) from bfldata.dbo.vGoodsIssuePlt " +
                    "where ShopIssue = '" + shopName + "' and TrfNo = '" + trfno + "' )", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Cannot Delete the transfer - " + trfno + " Shopname - " + shopName + ", GIN already Posted!");
                return false;
            }
            rs = dbConnection.getResultSet("select top 1 * from bfldata.dbo.ExportPostNew where GinNo in (select cast(srno as varchar(20)) from bfldata.dbo.vGoodsIssuePlt where " +
                    "ShopIssue = '" + shopName + "' and TrfNo = '" + trfno + "' )", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Cannot Delete the transfer - " + trfno + " Shopname - " + shopName + ", GIN already Posted!");
                return false;
            }
            rs = dbConnection.getResultSet("select itemcode,trf=sum(trfqty),scan=sum(qty) from BFLDATA.dbo.tmpDivSepItems where " +
                    "Deviceid='" + objGlobal.getDeviceName() + "' group by itemcode having sum(qty)>sum(TrfQty)", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Scan quantity is more than transfer quantity, itemcode:" + rs.getString("itemcode").toString() + ", " +
                        "Transfer:" + rs.getString("trf").toString() + ", Scan:" + rs.getString("scan").toString());
                return false;
            }
            if (!save) {
                if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.tmpDivSepItems select '" + objGlobal.getDeviceName() + "',TrfNo,'" + shopName + "',ItemCode," +
                        "(select distinct division from deptstock where Department in(select Department from usa.dbo.USAPriority where groupcode=a.groupcode)),0,Quantity," +
                        "'N' from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.vTransferDetail a where TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (delAll) {
                    if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.tmpDivSepItems select '" + objGlobal.getDeviceName() + "',TrfNo,'" + shopName + "',ItemCode," +
                            "(select distinct division from deptstock where Department in(select Department from usa.dbo.USAPriority where groupcode=a.groupcode)),Quantity,0," +
                            "'N' from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.vTransferDetail a where TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                } else {
                    if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.tmpDivSepItems select '" + objGlobal.getDeviceName() + "',TrfNo,ShopName,Itemcode,(select division from " +
                            "HODATA.dbo.vItemMaster where Itemcode=a.Itemcode),ScanQty,0,'N' from BFLDATA.dbo.RemoveItemsFromTransfer a where ShopName='" + shopName + "' and TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DivisionSeperationControl:validateTransfer; " + e);
            return false;
        }
    }

    public boolean validateTransferItem(String shopname, String trfno, String itemcode, int scanQty) {
        if (TextUtils.isEmpty(objDivisionSeperationGlobal.getDatabase())) {
            objGlobal.setErrorMessage("Database is blank");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.vTransferDetail where " +
                    "TrfNo='" + trfno + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Transfer or item not found in transfer");
                return false;
            }
            rs = dbConnection.getResultSet("select * from BFLDATA.dbo.GINDeleteItems where shopname='" + shopname + "' and TrfNo='" + trfno + "' and " +
                    "itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("The itemcode (" + itemcode + ") is not found in the deletion list.");
                return false;
            }
            rs = dbConnection.getResultSet("select itemcode,trf=sum(trfqty),scan=sum(qty)+" + scanQty + " from BFLDATA.dbo.tmpDivSepItems where itemcode='" + itemcode + "' and " +
                    "Deviceid='" + objGlobal.getDeviceName() + "' group by itemcode having sum(qty)+" + scanQty + ">sum(TrfQty)", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Scan quantity is more than transfer quantity, itemcode:" + rs.getString("itemcode").toString() + ", " +
                        "Transfer:" + rs.getString("trf").toString() + ", Scan:" + rs.getString("scan").toString());
                return false;
            }
            if (!dbConnection.insertUpdate("insert into BFLDATA.dbo.tmpDivSepItems select '" + objGlobal.getDeviceName() + "',TrfNo,'" + shopname + "'," +
                    "ItemCode,(select divisiony from usa.dbo.USAPriority where groupcode=a.groupcode)," + scanQty + ",0,'Y' from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.vTransferDetail a " +
                    "where TrfNo='" + trfno + "' and ItemCode='" + itemcode + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DivisionSeperationControl:validateTransfer; " + e);
            return false;
        }
    }

    public boolean save() {
        double slno = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(sn),0)+1 from RemoveItemsFromTransfer", objGlobal.getConnection());
            if (rs.next()) {
                slno = rs.getInt("sn");
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.RemoveItemsFromTransfer select trfno,shopname,itemcode,sum(TrfQty),'" + objGlobal.getUserId() + "',getdate(),''," +
                    "sum(qty)," + slno + ",'" + objGlobal.getWarehouse() + "' from BFLDATA.dbo.tmpDivSepItems where NewScan='Y' and deviceid='" + objGlobal.getDeviceName() + "' group by trfno," +
                    "shopname,itemcode having sum(qty)>0", objGlobal.getConnection())) {
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:ex:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:e:" + e);
                return false;
            }
            return false;
        }
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from BFLDATA.dbo.tmpDivSepItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:boxValid:" + ex);
            return false;
        }
        return true;
    }

    public List<String> loadExportShops() {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select ShopName from BFLDATA.dbo.DataSettings where FCCode<>'ROB' and (FCCode='AED' or ExportActive='Y') order by 1", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("ShopName"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    ArrayList<DivisionSeperationItemTicket> loadDivSepItems() {
        ArrayList<DivisionSeperationItemTicket> listDivisionSeperationItemTicket = new ArrayList<DivisionSeperationItemTicket>();
        try {
            listDivisionSeperationItemTicket.clear();
            rs = dbConnection.getResultSet("select Itemcode,Division,TrfQty=sum(TrfQty),ScanQty=sum(Qty) from BFLDATA.dbo.tmpDivSepItems where Deviceid='" + objGlobal.getDeviceName() + "' group by " +
                    "Itemcode,Division having sum(Qty)>0", objGlobal.getConnection());
            while (rs.next()) {
                listDivisionSeperationItemTicket.add(new DivisionSeperationItemTicket(rs.getString("Itemcode").toString(),
                        rs.getString("Division").toString(), rs.getString("TrfQty").toString(), rs.getString("ScanQty").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferItemsAll:" + ex.toString());
            return null;
        }
        return listDivisionSeperationItemTicket;
    }

    public boolean saveOld(String trfno) {
        double trfAmt = 0;
        double slno = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(sn),0)+1 from RemoveItemsFromTransfer", objGlobal.getConnection());
            if (rs.next()) {
                slno = rs.getInt("sn");
            }
            objGlobal.getConnection().setAutoCommit(false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = sdf.parse(objGlobal.getServerDate());
            Date date2 = sdf.parse("01/01/2025");
            if (date1.compareTo(date2) >= 0) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.RemoveItemsFromTransfer select trfno,shopname,itemcode,sum(TrfQty),'" + objGlobal.getUserId() + "',getdate(),''," +
                        "sum(qty)," + slno + ",'" + objGlobal.getDeviceName() + "','" + objGlobal.getWarehouse() + "' from BFLDATA.dbo.tmpDivSepItems where deviceid='" + objGlobal.getDeviceName() + "' group by trfno," +
                        "shopname,itemcode having sum(qty)>0", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.RemoveItemsFromTransfer select trfno,shopname,itemcode,sum(TrfQty),'" + objGlobal.getUserId() + "',getdate(),''," +
                        "sum(qty)," + slno + ",'" + objGlobal.getDeviceName() + "','" + objGlobal.getWarehouse() + "' from BFLDATA.dbo.tmpDivSepItems where deviceid='" + objGlobal.getDeviceName() + "' group by trfno,shopname,itemcode having sum(qty)>0", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into " + objDivisionSeperationGlobal.getDatabase() + ".dbo.DelTransferHeader(TrfNo,TrfDate,CostCodeFrom,LocCodeFrom,CostCodeTo,LocCodeTo,ACCode,Narration," +
                        "NetAmount,UserId,TrfType,FCCode,FCRate,ApprovedBy,PreparedBy,ConsumeReturn,JobNo,EntryMode,StoreIssue,StoreReceipt,Shipno,Cartonno,Palletno,Starttime) select TrfNo,TrfDate,CostCodeFrom," +
                        "LocCodeFrom,CostCodeTo,LocCodeTo,ACCode,Narration+'-" + slno + "',NetAmount,UserId,TrfType,FCCode,FCRate,ApprovedBy,PreparedBy,ConsumeReturn,JobNo,EntryMode,StoreIssue,StoreReceipt,Shipno,Cartonno," +
                        "Palletno,Starttime from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferHeader where TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into " + objDivisionSeperationGlobal.getDatabase() + ".dbo.DelTransferDetail (TrfNo,ItemCode,UnitCode,Quantity,Rate,BatchNo,BasicQty,BasicRate,SrNo," +
                        "UPC,ItemType) select TrfNo,ItemCode,UnitCode,Quantity,Rate," + String.valueOf(slno) + ",BasicQty,BasicRate,SrNo,UPC,ItemType from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferDetail where " +
                        "TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("select trfno,shopname,itemcode,trf=sum(TrfQty),sc=sum(qty) into #removescqty from BFLDATA.dbo.tmpDivSepItems where deviceid='" + objGlobal.getDeviceName() + "' and " +
                        "TrfNo='" + trfno + "' and qty>0 group by trfno,shopname,itemcode", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferDetail set Quantity=b.Quantity-a.sc from #removescqty a," +
                        "" + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferDetail b where a.Itemcode=b.ItemCode and b.TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update " + objDivisionSeperationGlobal.getDatabase() + ".dbo.LocStock set quantity=b.quantity-a.sc from #removescqty a," + objDivisionSeperationGlobal.getDatabase() + ".dbo." +
                        "LocStock b where a.Itemcode=b.Itemcode and b.costcode='" + objDivisionSeperationGlobal.getCostcode() + "' and b.loccode='" + objDivisionSeperationGlobal.getLoccode() + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("delete from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferDetail where TrfNo='" + trfno + "' and Quantity<=0", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("drop table #removescqty", objGlobal.getConnection())) {
                    return false;
                }
                rs = dbConnection.getResultSet("select amt=sum(quantity*rate) from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferDetail where TrfNo='" + trfno + "'", objGlobal.getConnection());
                if (rs.next()) {
                    trfAmt = rs.getDouble("amt");
                }
                if (trfAmt == 0) {
                    if (!dbConnection.insertUpdate("delete from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferHeader where TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("delete from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.AccTrnHeader where RefNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("delete from " + objDivisionSeperationGlobal.getDatabase() + ".dbo.AccTrnDetail where RefNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                } else {
                    if (!dbConnection.insertUpdate("update " + objDivisionSeperationGlobal.getDatabase() + ".dbo.TransferHeader set NetAmount=" + trfAmt + " where " +
                            "TrfNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update " + objDivisionSeperationGlobal.getDatabase() + ".dbo.AccTrnHeader set TotalAmount=" + trfAmt + ",FCTotalAmount=" + trfAmt + " where " +
                            "RefNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update " + objDivisionSeperationGlobal.getDatabase() + ".dbo.AccTrnDetail set Amount=" + trfAmt + ",FCAmount=" + trfAmt + " where " +
                            "RefNo='" + trfno + "'", objGlobal.getConnection())) {
                        return false;
                    }
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:ex:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("DivisionSeperationControl:Save:e:" + e);
                return false;
            }
            return false;
        }
    }
}
