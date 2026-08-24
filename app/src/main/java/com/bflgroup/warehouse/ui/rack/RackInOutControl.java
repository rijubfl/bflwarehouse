package com.bflgroup.warehouse.ui.rack;

import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.rack.model.RackHistoryModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class RackInOutControl {

    private DBConnection dbConnection = new DBConnection();
    private boolean b_Result;
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;

    public RackInOutControl() {
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

    public String getPalletDetails(String pallettop, String palletDown, String wareHouse) {

        String rowno = "";
        String cellno = "";
        String rackno = "";

//        ArrayList<String> arraylist = new ArrayList<String>();
        try {
            if (wareHouse.equals("JAFZA")) {

                //  rackNumber[1] = Integer.valueOf(rackNumber[1]).toString();
                rs = dbConnection.getResultSet("select * from racks.dbo.tmpwhracks where  (PalletNo1 = '" + pallettop + "' or  palletNo2 = '" + palletDown + "') and (palletno1 <> '' or palletno2 <> '')", objGlobal.getConnection());

                if (rs.next()) {
                    rowno = rs.getString("rowno");
                    cellno = rs.getString("cellno");


                    if (rowno.equals("") || rowno == "") {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return rackno;
                    }
                } else {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid Rack");
                    return rackno;
                }

                rackno = rowno + "-" + cellno;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rackno;
    }

    public ArrayList<String> getRackOutPallet(String rackNum, String warehouse) {

        String sRackRowNo = "";
        String iRackCellNo = "";
        String[] aRackNumber = null;
        String ColName = "";
        if (warehouse.equals("TECHNO-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
            aRackNumber = rackNum.split("-");
            sRackRowNo = aRackNumber[0] + "-" + aRackNumber[1] + "-" + aRackNumber[2] + "-" + aRackNumber[3];
            iRackCellNo = Integer.valueOf(aRackNumber[4]).toString();
            ColName = "Cell" + iRackCellNo;
        } else {
            aRackNumber = rackNum.split("-");
            Log.e("Racknum", aRackNumber.toString());
            sRackRowNo = aRackNumber[0];
            // aRackNumber[1] = Integer.valueOf(aRackNumber[1]).toString();
            iRackCellNo = Integer.valueOf(aRackNumber[1]).toString();
            ColName = "Cell" + iRackCellNo;
        }
        String palletNo1 = "";
        String palletNo2 = "";

        ArrayList<String> arraylist = new ArrayList<String>();
        try {

            if (warehouse.equals("TECHNO")) {

                rs = dbConnection.getResultSet("select * from racks.dbo.TechnoRackDet where rowno='" + sRackRowNo + "' and cellno= '" + Integer.valueOf(aRackNumber[1]) + "'", objGlobal.getConnection());
                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1");
                    palletNo2 = rs.getString("PalletNo2");


                    if (palletNo1.equals("") || palletNo2.equals("")) {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return arraylist;
                    }

                } else {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return arraylist;
                }
                arraylist.add(palletNo1);
                arraylist.add(palletNo2);
            } else {


                rs = dbConnection.getResultSet("select * from racks.dbo.WarehouseRackDet where warehouse='" + warehouse + "' and rowno='" + sRackRowNo + "' and cellno= '" + iRackCellNo + "'", objGlobal.getConnection());
                if (rs.next()) {
                    palletNo1 = rs.getString("PalletNo1");
                    palletNo2 = rs.getString("PalletNo2");


                    if (palletNo1.equals("") || palletNo2.equals("")) {
                        objGlobal.setErrorMessage("Can not Proceed, Selected Rack is empty");
                        return arraylist;
                    } else {

                    }
                } else {
                    objGlobal.setErrorMessage("Can not Proceed, Invalid pallet or invalid rack");
                    return arraylist;
                }
                arraylist.add(palletNo1);
                arraylist.add(palletNo2);

            }
        } catch (SQLException e) {
            objGlobal.setErrorMessage("getRackOutPallet:getRackOutPallet:ex3:" + e.toString());
        }

        return arraylist;
    }

    public boolean saveRackDetails(String rackNum, String warehouse, String palletUp, String palletDown, String inOutItem) {
        String sRackRowNo = "";
        int iRackCellNo = 0;
        String[] aRackNumber = null;
        String ColName = "";
        if (warehouse.equals("TECHNO-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
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
                                objGlobal.setErrorMessage("Pallet Number " + palletUp + " is closed/deleted already");
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
                                objGlobal.setErrorMessage("Pallet Number " + palletDown + " is closed/deleted already");
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
                objGlobal.setErrorMessage("Invalid Rack Number (isValidRackTechno) (" + rackNumber[0] + ")");
                return false;
            }
            if (inOutItem.equalsIgnoreCase("in")) {
                rs = dbConnection.getResultSet("SELECT TOP 1 Direction, SourceLocation FROM (SELECT Direction,TrnDate,TrnTime, Location AS SourceLocation FROM " +
                        "racks..BinPutAwayHistory WHERE BoxNo IN ('" + palletUp + "', '" + palletDown + "') UNION ALL SELECT Direction,TrnDate,TrnTime, RackNo AS SourceLocation FROM " +
                        "racks..TechnoRacksHistory WHERE PalletNo1 IN ('" + palletUp + "', '" + palletDown + "') OR PalletNo2 IN ('" + palletUp + "', '" + palletDown + "') UNION ALL SELECT Direction," +
                        "TrnDate,TrnTime,RackNo AS SourceLocation FROM racks..WarehouseRackHistory WHERE PalletNo1 IN ('" + palletUp + "', '" + palletDown + "') OR PalletNo2 IN " +
                        "('" + palletUp + "', '" + palletDown + "')) X ORDER BY TrnDate DESC, TrnTime DESC", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getString("Direction").equals("IN")) {
                        objGlobal.setErrorMessage("The box/pallet is already found in "+rs.getString("SourceLocation"));
                        return false;
                    }
                }
                rs = dbConnection.getResultSet("select * from racks.dbo.warehouserackdet where (palletno1='" + palletUp + "' or palletno2='" + palletUp + "' or palletno1='" + palletDown + "' or palletno2='" + palletDown + "') ", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed, Pallets found in warehouse: TECHNO-E, Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }

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

    public boolean isValidRack(String rackNum, String warehouse, String palletUp, String palletDown, String inOutItem) {
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
            if (warehouse.equals("TECHNO-E") || warehouse.equals("YOTO-SF") || warehouse.equals("YOTO-BU")) {
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
                objGlobal.setErrorMessage("Invalid Rack Number (isValidRack) (" + warehouse + "," + sRackRowNo + ")");
                return false;
            }
            if (inOutItem.equalsIgnoreCase("in")) {
                rs = dbConnection.getResultSet("SELECT TOP 1 Direction, SourceLocation FROM (SELECT Direction,TrnDate,TrnTime, Location AS SourceLocation FROM " +
                        "racks..BinPutAwayHistory WHERE BoxNo IN ('" + palletUp + "', '" + palletDown + "') UNION ALL SELECT Direction,TrnDate,TrnTime, RackNo AS SourceLocation FROM " +
                        "racks..TechnoRacksHistory WHERE PalletNo1 IN ('" + palletUp + "', '" + palletDown + "') OR PalletNo2 IN ('" + palletUp + "', '" + palletDown + "') UNION ALL SELECT Direction," +
                        "TrnDate,TrnTime,RackNo AS SourceLocation FROM racks..WarehouseRackHistory WHERE PalletNo1 IN ('" + palletUp + "', '" + palletDown + "') OR PalletNo2 IN " +
                        "('" + palletUp + "', '" + palletDown + "')) X ORDER BY TrnDate DESC, TrnTime DESC", objGlobal.getConnection());
                if (rs.next()) {
                    if (rs.getString("Direction").equals("IN")) {
                        objGlobal.setErrorMessage("The box/pallet is already found in "+rs.getString("SourceLocation"));
                        return false;
                    }
                }


                rs = dbConnection.getResultSet("select * from racks.dbo.technorackdet where ( palletno1='" + palletUp + "' or palletno2='" + palletUp + "' or palletno1='" + palletDown + "' or palletno2='" + palletDown + "')", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Can not Proceed Pallet2, is already found in warehouse:TECHNO, Rack: " + rs.getString("RowNo") + "-" + rs.getInt("CellNo"));
                    return false;
                }

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

    public ArrayList<RackHistoryModel> loadRackhistoryTechno() {
        ArrayList<RackHistoryModel> listRackhistoryItem = new ArrayList<RackHistoryModel>();
        try {
            rs = dbConnection.getResultSet("select top 50 * from racks.dbo.TechnoRacksHistory  where UserName='" + objGlobal.getUserName() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                RackHistoryModel rackHistoryModel = new RackHistoryModel();
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

    public ArrayList<RackHistoryModel> loadRackhistory(String warehouse) {
        ArrayList<RackHistoryModel> listRackhistoryItem = new ArrayList<RackHistoryModel>();
        try {
            rs = dbConnection.getResultSet("select top 50 * from racks.dbo.WarehouseRackHistory where warehouse='" + warehouse + "' and UserName='" + objGlobal.getUserName() + "'  order by trndate desc,trntime desc", objGlobal.getConnection());
            while (rs.next()) {
                RackHistoryModel rackHistoryModel = new RackHistoryModel();
                rackHistoryModel.setRackNo(rs.getString("RackNo"));
                rackHistoryModel.setDirection(rs.getString("Direction"));
                rackHistoryModel.setPalletNo1(rs.getString("PalletNo1"));
                rackHistoryModel.setPalletNo2(rs.getString("PalletNo2"));
                rackHistoryModel.setTrnDate(rs.getString("TrnDate"));
                rackHistoryModel.setTrnTime(rs.getString("TrnTime"));
                rackHistoryModel.setRemarks(rs.getString("Remarks"));
                rackHistoryModel.setWarehouse(rs.getString("warehouse"));
                listRackhistoryItem.add(rackHistoryModel);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
            return listRackhistoryItem;
        }
        return listRackhistoryItem;
    }

}
