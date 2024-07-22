package com.bflgroup.warehouse.ui.jafzaracks;

import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.jafzaracks.model.JafzaRackHistoryModel;
import com.bflgroup.warehouse.ui.rack.RackInOutGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class JafzaRackInOutControl {

    private DBConnection dbConnection = new DBConnection();
    private boolean b_Result;
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;

    public JafzaRackInOutControl() {
        objGlobal.setServerIP("192.168.5.51");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("RackInOutControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("RackInOutControl : Fetch Time error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setServerIP("192.168.5.51");
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("RackInOutControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean saveRackDetailsTechno(String rackNum, String palletUp, String palletDown, String inOutItem) {
        String RowNo = "";
        int CellNo = 0;
        String[] rackNumber = rackNum.split("-");
        rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
        String ColName = "Cell" + rackNumber[1];
        if (!checkConnection()) {
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            if (inOutItem.equalsIgnoreCase("in")) {
                b_Result = dbConnection.insertUpdate("insert into racks.dbo.TechnoRackDet values('" + rackNumber[0] + "','" + rackNumber[1] + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "','" + palletUp + "','" + palletDown + "')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update racks.dbo.TechnoRacks set " + ColName + "='" + palletUp + "/" + palletDown + "' where rowno='" + rackNumber[0] + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            } else if (inOutItem.equalsIgnoreCase("out")) {
                b_Result = dbConnection.insertUpdate("update racks.dbo.TechnoRacks set " + ColName + "='' where rowno='" + rackNumber[0] + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("delete from racks.dbo.TechnoRackDet where rowno='" + rackNumber[0] + "' and cellno='" + rackNumber[1] + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("Insert into racks.dbo.TechnoRacksHistory values  ('" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp + "'," +
                    "'" + palletDown + "','" + inOutItem.toUpperCase(Locale.ROOT) + "','" + rackNum + "','" + objGlobal.getUserName() + "','')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
        } catch (Exception e) {
            try {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex2:" + e.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException ex) {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex3:" + ex.toString());
                return false;
            }
        }
        return true;
    }

    public boolean saveRackDetails(String rackNum,String warehouse, String palletUp, String palletDown, String inOutItem) {
        String sRackRowNo = "";
        int iRackCellNo = 0;
        String[] aRackNumber = null;
        String ColName = "";
        if (warehouse.equals("TECHNO-E")) {
            aRackNumber = rackNum.split("-");
            sRackRowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            iRackCellNo = Integer.valueOf(aRackNumber[4]);
            ColName = "Cell" + iRackCellNo;
        } else {
            aRackNumber = rackNum.split("-");
            sRackRowNo = aRackNumber[0];
            iRackCellNo = Integer.valueOf(aRackNumber[1]);
            ColName = "Cell" + iRackCellNo;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            if (inOutItem.equalsIgnoreCase("in")) {
                b_Result = dbConnection.insertUpdate("insert into racks.dbo.WarehouseRackDet values('" + warehouse + "','" + sRackRowNo + "','" + iRackCellNo +
                        "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp + "','" + palletDown + "')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update racks.dbo.WarehouseRacks set " + ColName + "='" + palletUp + "/" + palletDown + "' where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            } else if (inOutItem.equalsIgnoreCase("out")) {
                b_Result = dbConnection.insertUpdate("update racks.dbo.WarehouseRacks set " + ColName + "='' where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("delete from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "' and cellno='" + iRackCellNo + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("Insert into racks.dbo.WarehouseRackHistory values ('" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp +
                    "','" + palletDown + "','" + inOutItem.toUpperCase(Locale.ROOT) + "','" + warehouse + "','" + rackNum + "','" + objGlobal.getUserName() + "','')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
        } catch (Exception e) {
            try {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex2:" + e.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException ex) {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex3:" + ex.toString());
                return false;
            }
        }
        return true;
    }

    public boolean isValidRackTechno(String rackNum, String palletUp, String palletDown, String inOutItem) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletUp + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletUp + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletUp + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                objGlobal.setErrorMessage("Pallet Number " + palletUp + " is closed already");
                                return false;
                            }
                        }
                    }
                }
            }

            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletDown + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletDown + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletDown + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                objGlobal.setErrorMessage("Pallet Number " + palletDown + " is closed already");
                                return false;
                            }
                        }
                    }
                }
            }
            String[] rackNumber = rackNum.split("-");
            rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
            String ColName = "Cell" + rackNumber[1];
            rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRacks where RowNo='" + rackNumber[0] + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Rack Number");
                return false;
            }
            if (inOutItem.equalsIgnoreCase("in")) {
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where palletno1='" + palletUp + "' or palletno2='" + palletUp + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed Pallet2, is already found in warehouse:TECHNO, Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where palletno1='" + palletDown + "' or palletno2='" + palletDown + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed Pallet2, is already found in warehouse:TECHNO, Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where rowno='" + rackNumber[0] + "' and cellno=" + rackNumber[1], objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is not empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRacks where rowno='" + rackNumber[0] + "' and " + ColName + "<>''", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is not empty");
                    return false;
                }
            }
            if (inOutItem.equalsIgnoreCase("out")) {
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where palletno1='" + palletUp + "' and palletno2='" + palletDown + "' and rowno='" + rackNumber[0] + "' and cellno= '" + rackNumber[1] + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRacks where RowNo='" + rackNumber[0] + "' and '" + rackNumber[1] + "'<>''", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where RowNo='" + rackNumber[0] + "' and cellno='" + rackNumber[1] + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRacks where RowNo='" + rackNumber[0] + "' and '" + ColName + "'<>''", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e + "");
            return false;
        }
        return true;
    }

    public boolean isValidRack(String rackNum,String warehouse, String palletUp, String palletDown, String inOutItem) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletUp + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletUp + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletUp + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                objGlobal.setErrorMessage("Pallet Number " + palletUp + " is closed already");
                                return false;
                            }
                        }
                    }
                }
            }

            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletDown + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletDown + "'", objGlobal.getConnection());
                        if (!rs.next()) {
                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletDown + "'", objGlobal.getConnection());
                            if (!rs.next()) {
                                objGlobal.setErrorMessage("Pallet Number " + palletDown + " is closed already");
                                return false;
                            }
                        }
                    }
                }
            }
            String sRackRowNo = "";
            int iRackCellNo = 0;
            String[] aRackNumber = null;
            String ColName = "";
            if (warehouse.equals("TECHNO-E")) {
                aRackNumber = rackNum.split("-");
                sRackRowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
                iRackCellNo = Integer.valueOf(aRackNumber[4]);
                ColName = "Cell" + iRackCellNo;
            } else {
                aRackNumber = rackNum.split("-");
                sRackRowNo = aRackNumber[0];
                iRackCellNo = Integer.valueOf(aRackNumber[1]);
                ColName = "Cell" + iRackCellNo;
            }
            rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRacks where warehouse='" + warehouse + "' and RowNo='" + sRackRowNo + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Rack Number");
                return false;
            }
            if (inOutItem.equalsIgnoreCase("in")) {
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and palletno1='" + palletUp + "' or palletno2='" + palletUp + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed Pallet2, is already found in warehouse:" + rs.getString("warehouse") + ", Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and palletno1='" + palletDown + "' or palletno2='" + palletDown + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed Pallet2, is already found in warehouse:" + rs.getString("warehouse") + ", Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "' and cellno=" + iRackCellNo, objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is not empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRacks where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "' and " + ColName + "<>''", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is not empty");
                    return false;
                }
            }
            if (inOutItem.equalsIgnoreCase("out")) {
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and palletno1='" + palletUp + "' and palletno2='" + palletDown + "' and rowno='" + sRackRowNo + "' and cellno= '" + iRackCellNo + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRacks where warehouse='" + warehouse + "' and RowNo='" + sRackRowNo + "' and '" + iRackCellNo + "'<>''", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and RowNo='" + sRackRowNo + "' and cellno='" + iRackCellNo + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRacks where warehouse='" + warehouse + "' and RowNo='" + sRackRowNo + "' and " + ColName + "<>''", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                    return false;
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e + "");
            return false;
        }
        return true;
    }

    public ArrayList<JafzaRackHistoryModel> loadRackhistoryTechno() {
        ArrayList<JafzaRackHistoryModel> listRackhistoryItem = new ArrayList<JafzaRackHistoryModel>();
        try {
            rs = dbConnection.getResultSet("select top 50 * from racks.dbo.TechnoRacksHistory  where UserName='" + objGlobal.getUserName() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                JafzaRackHistoryModel rackHistoryModel = new JafzaRackHistoryModel();
                rackHistoryModel.setRackNo(rs.getString("RackNo"));
                rackHistoryModel.setDirection(rs.getString("Direction"));
                rackHistoryModel.setPalletNo1(rs.getString("PalletNo1"));
                rackHistoryModel.setPalletNo2(rs.getString("PalletNo2"));
                rackHistoryModel.setTrnDate(rs.getString("TrnDate"));
                rackHistoryModel.setTrnTime(rs.getString("TrnTime"));
                rackHistoryModel.setRemarks(rs.getString("Remarks"));
                rackHistoryModel.setWarehouse("TECHNO");
                listRackhistoryItem.add(rackHistoryModel);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
            return listRackhistoryItem;
        }
        return listRackhistoryItem;
    }

    public ArrayList<JafzaRackHistoryModel> loadRackhistory(String warehouse) {
        ArrayList<JafzaRackHistoryModel> listRackhistoryItem = new ArrayList<JafzaRackHistoryModel>();
        try {
            rs = dbConnection.getResultSet("select top 50 * from racks.dbo.WarehouseRackHistory where warehouse='" + warehouse + "' and UserName='" + objGlobal.getUserName() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                JafzaRackHistoryModel JafzaRackHistoryModel = new JafzaRackHistoryModel();
                JafzaRackHistoryModel.setRackNo(rs.getString("RackNo"));
                JafzaRackHistoryModel.setDirection(rs.getString("Direction"));
                JafzaRackHistoryModel.setPalletNo1(rs.getString("PalletNo1"));
                JafzaRackHistoryModel.setPalletNo2(rs.getString("PalletNo2"));
                JafzaRackHistoryModel.setTrnDate(rs.getString("TrnDate"));
                JafzaRackHistoryModel.setTrnTime(rs.getString("TrnTime"));
                JafzaRackHistoryModel.setRemarks(rs.getString("Remarks"));
                JafzaRackHistoryModel.setWarehouse(rs.getString("warehouse"));
                listRackhistoryItem.add(JafzaRackHistoryModel);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
            return listRackhistoryItem;
        }
        return listRackhistoryItem;
    }

    public String BoxPalletCount(String palletNo){
        String Boxcount = "0";
        try {
            if(palletNo.substring(0,3).equals("USA")) {
                String query = "select count = count(distinct Boxno) from USA..vUPCBOXdet where palletno = '" + palletNo + "'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }else if(palletNo.substring(0,3).equals("PLT")){
                String query = "select count = count(distinct Boxno) from BFLDATA..vR1Pallet where palletno = '" + palletNo + "'";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Boxcount = rs.getString("count");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Boxcount;
    }

    public ArrayList<JafzaRackHistoryModel> loadRack1history() {
        ArrayList<JafzaRackHistoryModel> listRackhistoryItem = new ArrayList<JafzaRackHistoryModel>();
        try {
            rs = dbConnection.getResultSet("select top 50 * from racks.dbo.R1History where remarks like '%Saved%' and Remarks1 like '%" + objGlobal.getUserName() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                JafzaRackHistoryModel rackHistoryModel = new JafzaRackHistoryModel();
                rackHistoryModel.setRackNo(rs.getString("RackNo"));
                rackHistoryModel.setDirection(rs.getString("Direction"));
                rackHistoryModel.setPalletNo1(rs.getString("Box1"));
                rackHistoryModel.setPalletNo2(rs.getString("Box2"));
                rackHistoryModel.setTrnDate(rs.getString("TrnDate"));
                rackHistoryModel.setTrnTime(rs.getString("TrnTime"));
                rackHistoryModel.setRemarks(rs.getString("Remarks"));
                rackHistoryModel.setWarehouse("JAFZA");
                listRackhistoryItem.add(rackHistoryModel);
            }

        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
            return listRackhistoryItem;
        }
        return listRackhistoryItem;
    }

    public ArrayList<String> getRackOutPallet(String rackNum, String wareHouse){
        String[] rackNumber = null;

        rackNumber = rackNum.split("-");

        Log.e("Error", rackNumber+"");
        objGlobal.setErrorMessage(rackNumber+"");
        rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
        String ColName = "Cell" + rackNumber[1];
        String palletNo1 = "";
        String palletNo2 = "";

        ArrayList<String> arraylist = new ArrayList<String>();
        try {
            if(wareHouse.equals("JAFZA")) {


                int p=rackNum.lastIndexOf("-");
                String row = rackNum.substring(0, p);
                String cell= rackNum.substring(p+1).toString();
                if(cell.equals("FLR") || cell.equals("FE") || !cell.matches(".*\\d.*")){
                    row = row+"-"+cell;
                    cell = String.valueOf(getAutoFLoor(row));
                }else {
                    cell = Integer.valueOf(rackNum.substring(p + 1)).toString();
                }

                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where rowno='" + row + "' and cellno=" + cell, objGlobal.getConnection());

                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1");
                    palletNo2 = rs.getString("PalletNo2");

                    if (palletNo1.equals("") || palletNo2.equals("") || palletNo1 == "" || palletNo2 =="") {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return arraylist;
                    }
                } else {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid Rack");
                    return arraylist;
                }
                arraylist.add(palletNo1);
                arraylist.add(palletNo2);
            }else if(wareHouse.equals("TECHNO")){
                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where rowno='" + rackNumber[0] + "' and cellno= '" + rackNumber[1] + "'", objGlobal.getConnection());
                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1");
                    palletNo2 = rs.getString("PalletNo2");


                    if (palletNo1.equals("") || palletNo2.equals("")) {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return arraylist;
                    }

                }
                else{
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return arraylist;
                }
                arraylist.add(palletNo1);
                arraylist.add(palletNo2);
            }else{
                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='"+wareHouse+"' and rowno='" + rackNumber[0] + "' and cellno= '" + rackNumber[1] + "'", objGlobal.getConnection());
                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1");
                    palletNo2 = rs.getString("PalletNo2");


                    if (palletNo1.equals("") || palletNo2.equals("")) {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return arraylist;
                    } else {

                    }
                }
                else{
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return arraylist;
                }
                arraylist.add(palletNo1);
                arraylist.add(palletNo2);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arraylist;
    }

    public boolean isValidRackJafza(String rackNum, String palletUp, String palletDown, String inOutItem) {
        if (!checkConnection()) {
            return false;
        }
        try {
//            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
//            if (!rs.next()) {
//                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletUp + "' and closed='N'", objGlobal.getConnection());
//                if (!rs.next()) {
//                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletUp + "'", objGlobal.getConnection());
//                    if (!rs.next()) {
//                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletUp + "'", objGlobal.getConnection());
//                        if (!rs.next()) {
//                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletUp + "'", objGlobal.getConnection());
//                            if (!rs.next()) {
//                                objGlobal.setErrorMessage("Pallet Number " + palletUp + " is closed already");
//                                return false;
//                            }
//                        }
//                    }
//                }
//            }
//
//            rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
//            if (!rs.next()) {
//                rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets where palletno='" + palletDown + "' and closed='N'", objGlobal.getConnection());
//                if (!rs.next()) {
//                    rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where palletno='" + palletDown + "'", objGlobal.getConnection());
//                    if (!rs.next()) {
//                        rs = dbConnection.getResultSet("select * from bfldata.dbo.GoodsIssueHead where palletno='" + palletDown + "'", objGlobal.getConnection());
//                        if (!rs.next()) {
//                            rs = dbConnection.getResultSet("select top 1 * from abudata.dbo.tcmitemsall where palletno='" + palletDown + "'", objGlobal.getConnection());
//                            if (!rs.next()) {
//                                objGlobal.setErrorMessage("Pallet Number " + palletDown + " is closed already");
//                                return false;
//                            }
//                        }
//                    }
//                }
//            }
            String[] rackNumber = rackNum.split("-");
//            int p=rackNum.lastIndexOf("-");
//            String row = rackNum.substring(0, p);
//            String cell= Integer.valueOf(rackNum.substring(p+1)).toString();
//
//            Log.e("first", row+"");
//            Log.e("Last", cell+"");

            int p=rackNum.lastIndexOf("-");
            String row = rackNum.substring(0, p);
            String cell= rackNum.substring(p+1).toString();
            if(cell.equals("FLR") || cell.equals("FE") || !cell.matches(".*\\d.*")){
                row = row+"-"+cell;
                cell = String.valueOf(getAutoFLoor(row));
            }else {
                cell = Integer.valueOf(rackNum.substring(p + 1)).toString();
            }


//
//           String rackNumb = Integer.valueOf(rackNumber[rackNumber.length-1]).toString();
//            String ColName = "Cell" + rackNumber[1];
            String palletNo1 = "";
            String palletNo2 = "";
            String rowno = "";
            String cellno = "";

            if (inOutItem.equalsIgnoreCase("in")) {

                rs = dbConnection.getResultSet("select * from racks.dbo.binrack where warehouse = 'JAFZA' and (toteid = '"+palletUp+"' or toteid = '"+palletDown+"')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Pallets found in rack location - "+rs.getString("location"));
                    return false;
                }

                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where rowno='" + row + "' and cellno=" + cell, objGlobal.getConnection());
                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1") ;
                    palletNo2 = rs.getString("PalletNo2");
                    if(!palletNo1.equals("") || !palletNo2.equals("")){
                        objGlobal.setErrorMessage("Can not Proceed, Rack is already occupied");
                        return false;
                    }
                }
                else{
                    objGlobal.setErrorMessage("Can not Proceed, Invalid Rack - " +rackNum);
                    return false;
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where PalletNo1='" + palletUp + "' and palletNo2='" + palletDown + "'", objGlobal.getConnection());
                if (rs.next()) {
                    rowno = rs.getString("rowno") ;
                    cellno = rs.getString("cellno");
                    if(!rowno.equals("") || !cellno.equals("")){
                        objGlobal.setErrorMessage("Can not Proceed, pallet is already in Rack - "+rowno +"-"+cellno);
                        return false;
                    }
                }

                int found = 0;
                rs = dbConnection.getResultSet("select * from usa.dbo.usapallets where closed='N' and (palletno='" +palletUp+ "' or palletno='" + palletDown + "')", objGlobal.getConnection());
                if (!rs.next()) {
                    rs = dbConnection.getResultSet("select * from bfldata.dbo.openr1pallet  where (palletno='" + palletUp + "' or palletno='" + palletDown + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("Pallet is already opened (USA)");
                        found = 0;
                        return false;
                    }else{
                        found = 1;
                    }
                    rs = dbConnection.getResultSet("select * from bfldata.dbo.r1pallethead  where (palletno='" + palletUp + "' or palletno='" + palletDown + "') and closed='Y'", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("Pallet is already opened (TCM)");
                        found = 0;
                        return false;
                    }else{
                        found = 1;
                    }
                    rs = dbConnection.getResultSet("select * from bfldata.dbo.usapallets  where (palletno='" + palletUp + "' or palletno='" + palletDown + "') and closed='Y'", objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("Pallet is already Closed (USA)");
                        found = 0;
                        return false;
                    }else{
                        found = 1;
                    }
                    if(found == 1){

                    }else{
                        objGlobal.setErrorMessage("Invalid palletno / Pallet is closed");
                    }

                }
            }
            else if (inOutItem.equalsIgnoreCase("out")){
                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where (PalletNo1='" + palletUp + "' or palletNo2='" + palletDown + "') and (palletno1 <> '' or palletno2 <> '')", objGlobal.getConnection());
                if (rs.next()) {
                    rowno = rs.getString("rowno") ;
                    cellno = rs.getString("cellno");
                    if(rowno.equals("") ){
                        objGlobal.setErrorMessage("Can not Proceed, Pallets not found - " + palletUp + " & " +palletDown );
                        return false;
                    }
                }
                else{
                    objGlobal.setErrorMessage("Can not Proceed, Pallets not found - " + palletUp + " & " +palletDown);
                    return false;
                }
            }


        } catch (Exception e) {
            objGlobal.setErrorMessage(e + "");
            return false;
        }
        return true;
    }

    public boolean saveRackDetailsJafza(String rackNum, String palletUp, String palletDown, String inOutItem) {
        String RowNo = "";
        int CellNo = 0;
//        String[] rackNumber = rackNum.split("-");
//       // rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
//        String rackNumb = Integer.valueOf(rackNumber[rackNumber.length-1]).toString();
        // String ColName = "Cell" + rackNumber[1];
        if (!checkConnection()) {
            return false;
        }


        String[] rackNumber = rackNum.split("-");
//        int p=rackNum.lastIndexOf("-");
//        String row = rackNum.substring(0, p);
//        String cell= Integer.valueOf(rackNum.substring(p+1)).toString();
//
//        String newRack = row + "/" +  cell;

        int p=rackNum.lastIndexOf("-");
        String row = rackNum.substring(0, p);
        String cell= rackNum.substring(p+1).toString();
        if(cell.equals("FLR") || cell.equals("FE") || !cell.matches(".*\\d.*")){
            row = row+"-"+cell;
            cell = String.valueOf(getAutoFLoor(row));
        }else {
            cell = Integer.valueOf(rackNum.substring(p + 1)).toString();
        }
        String newRack = row + "/" +  cell;

        try {
            objGlobal.getConnection().setAutoCommit(false);
            b_Result = dbConnection.insertUpdate("Insert into racks.dbo.R1History values  ('" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp +
                    "','" + palletDown + "','"+inOutItem.toUpperCase(Locale.ROOT)+"','" + newRack + "','STARTED-" + objGlobal.getUserName() + "','PDA-"+objGlobal.getUserName()+"')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }


            if (inOutItem.equalsIgnoreCase("out")) {
                b_Result = dbConnection.insertUpdate("insert into racks.dbo.R1History values('" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp +"','" + palletDown + "','OUT','" + newRack + "','OUT-Saved-" + objGlobal.getUserName() + "','PDA-"+objGlobal.getUserName()+"')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("Insert into racks.dbo.DelTMPWHRacks select *,'" + objGlobal.getServerDate() +  "','" + objGlobal.getServerTime() + "' from racks.dbo.tmpwhracksnew where rowno='" +row+ "' and cellno="+cell,objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update racks.dbo.tmpwhracks set palletno1='',palletno2='' where rowno='" +row+ "' and cellno="+cell,objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }


            } else if (inOutItem.equalsIgnoreCase("in")) {

                b_Result = dbConnection.insertUpdate("insert into racks.dbo.R1History values('" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + palletUp +"','" + palletDown + "','IN','" + newRack + "','IN-Saved" + objGlobal.getUserName() + "','PDA-"+objGlobal.getUserName()+"')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("Update  racks..tmpwhracks set palletno1='" + palletUp + "',palletno2='" + palletDown + "' where rowno='" + row + "' and cellno=" + cell,objGlobal.getConnection());

                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }

            }

            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
        } catch (Exception e) {
            try {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex2:" + e.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException ex) {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex3:" + ex.toString());
                return false;
            }
        }
        return true;
    }

    public String getPalletDetails(String pallettop, String palletDown, String wareHouse){

        String rowno = "";
        String cellno = "";
        String rackno = "";

//        ArrayList<String> arraylist = new ArrayList<String>();
        try {
            if(wareHouse.equals("JAFZA")) {

                //  rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where  (PalletNo1 = '" + pallettop +"' or  palletNo2 = '"+palletDown +"') and (palletno1 <> '' or palletno2 <> '')", objGlobal.getConnection());

                if (rs.next()) {
                    rowno = rs.getString("rowno");
                    cellno = rs.getString("cellno");


                    if (rowno.equals("")  || rowno == "" ) {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return rackno;
                    }
                } else {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid Rack");
                    return rackno;
                }

                rackno = rowno + "-"+ cellno;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rackno;
    }

    public Integer getAutoFLoor(String rowno) {
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }

        ResultSet resultSet;
        Integer num = 0;
        String que = "select isnull(min(cellno),0) as num from racks.dbo.tmpwhracks where rowno = '"+rowno+"' and (PalletNo1='' and palletno2='')";
        resultSet = dbConnection.getResultSet(que, objGlobal.getConnection());
        try {
            if (resultSet.next()) {
                num = Integer.valueOf(resultSet.getString("num"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        RackInOutGlobal.setCellNo(num);
        return num;
    }

}
