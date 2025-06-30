package com.bflgroup.warehouse.ui.pricecheck;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.pricecheck.model.ItemBoxDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemSegregationControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PriceCheckScanDetail objPriceCheckScanDetail = PriceCheckScanDetail.getInstance();

    private boolean b_Result;

    Statement stmt;
    boolean result;
    private ResultSet rs;

    public ItemSegregationControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PriceCheckControl : Local Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("PriceCheckControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    String seperateBarcode(String barcode) {
        String[] parts;
        String part1;
        int i;
        if (barcode.contains("/")) {
            parts = barcode.split("/");
            part1 = parts[0];
        } else {
            part1 = barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }

    public boolean getBoxDetails(String scanBox) {
        if(scanBox.equals("")){
            objGlobal.setErrorMessage("Please scan box/tote ");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            String boxno="";
            if(!scanBox.equals("")) {
                if(boxno.equals("")) {
                    rs = dbConnection.getResultSet("select top 1 BoxNo from USA.dbo.UPCBoxHead where Closed='N' and (BoxNo='" + scanBox + "' or ToteID='" + scanBox + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        boxno = rs.getString("boxno");
                    }
                }
                if(boxno.equals("")) {
                    rs = dbConnection.getResultSet("select distinct b.boxno from BFLDATA.dbo.TCMBoxes a,BFLDATA.dbo.TcmboxesHeader b where a.Closed='N' and a.BoxNo=b.Boxno " +
                            "and (b.BoxNo='" + scanBox + "' or b.TotId='" + scanBox + "')", objGlobal.getConnection());
                    if (rs.next()) {
                        boxno = rs.getString("boxno");
                    }
                }
                if(boxno.equals("")) {
                    objGlobal.setErrorMessage("Invalid Box / Tote or box is already closed ");
                    return false;
                }
            }
            objPriceCheckScanDetail.setBoxno(boxno);
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean removeTempData(){
        if (dbConnection.insertUpdate("delete from bfldata.dbo.tempWhagedItems where deviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection()))
            return true;
        else{
            objGlobal.setErrorMessage("Something went wrong. Please try again");
            return false;
        }
    }

    public ArrayList<ItemBoxDetails> getTempData() throws SQLException {
        ArrayList<ItemBoxDetails> itemBoxDetailsArrayList = new ArrayList<>();
        rs = dbConnection.getResultSet("select * from bfldata.dbo.tempWhagedItems where deviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
        while (rs.next()){
            String division = "";
            if (rs.getString("groupcode") != null&& !rs.getString("groupcode").equals(""))
            {
                ResultSet rs1 = dbConnection.getResultSet("select divisionY from usa.dbo.usapriority where groupcode = '"+rs.getString("groupcode")+"'", objGlobal.getConnection());
                if (rs1.next())
                    division = rs1.getString("divisionY");
            }

            ItemBoxDetails itemBoxDetails = new ItemBoxDetails(rs.getString("itemcode"),rs.getString("boxno"),rs.getString("caption"),division);
            itemBoxDetailsArrayList.add(itemBoxDetails);
        }
        return itemBoxDetailsArrayList;
    }

    public boolean getItemDetails(String scanItem, String scanBox) {
        if (!checkConnection()) {
            return false;
        }
        try {
            String itemcode = "",boxno="", groupcode = "", message = "", where="";
            itemcode = seperateBarcode(scanItem);

            rs = dbConnection.getResultSet("select itemcode,description,ItemType='',groupcode,GroupName,SalesPrice=0,stock=0,Department,Division from HODATA.dbo.vItemMaster where ItemCode='" + itemcode + "'", objGlobal.getConnection());
            if(rs.next()){
                objPriceCheckScanDetail.setItemcode(rs.getString("itemcode"));
                objPriceCheckScanDetail.setDescription(rs.getString("description"));
                objPriceCheckScanDetail.setGroup(rs.getString("GroupName"));
                objPriceCheckScanDetail.setPrice(rs.getFloat("SalesPrice"));
                objPriceCheckScanDetail.setItemType(rs.getString("ItemType"));
                objPriceCheckScanDetail.setStock(rs.getInt("stock"));
                objPriceCheckScanDetail.setDepartment(rs.getString("department"));
                objPriceCheckScanDetail.setDivision(rs.getString("Division"));
                groupcode = rs.getString("groupcode");
            } else {
                objGlobal.setErrorMessage("Invalid itemcode / Barcode ");
                return false;
            }
            if(!scanBox.equals("")) {
                if(boxno.equals("")) {
                    rs = dbConnection.getResultSet("select top 1 * from usa..vupcboxdet where Closed='N' and (BoxNo='"+scanBox+"' or ToteID='"+scanBox+"') and itemcode = '"+scanItem+"'", objGlobal.getConnection());
                    if (rs.next()) {
                        boxno = rs.getString("boxno");
                        objPriceCheckScanDetail.setBoxType("USABOX");
                    }
                    else{
                        objGlobal.setErrorMessage("Invalid box or item not contained in this box");
                        return false;
                    }
                }
                if(boxno.equals("")) {
                    rs = dbConnection.getResultSet("select distinct b.boxno from BFLDATA.dbo.TCMBoxes a,BFLDATA.dbo.TcmboxesHeader b where a.Closed='N' and a.BoxNo=b.Boxno and (b.BoxNo='"+scanBox+"' or b.TotId='"+scanBox+"') and itemcode = '"+scanItem+"'", objGlobal.getConnection());
                    if (rs.next()) {
                        boxno = rs.getString("boxno");
                        objPriceCheckScanDetail.setBoxType("TCMBOX");
                    }
                    else{
                        objGlobal.setErrorMessage("Invalid box or item not contained in this box");
                        return false;
                    }
                }
                if(boxno.equals("")) {
                    objGlobal.setErrorMessage("Invalid Box / Tote ");
                    return false;
                }
            }
            if(!boxno.equals(""))
                where=" and boxno='" + boxno + "' ";
            else
                where=" and boxno='' ";

            objPriceCheckScanDetail.setBoxno(boxno);
            objPriceCheckScanDetail.setMessage("");
            if(message.isEmpty()){
                rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where itemcode='" + itemcode + "' and country='" + objGlobal.getWarehouse() + "'"+ where, objGlobal.getConnection());
                if(rs.next()) message=rs.getString("Caption");
            }
            if(message.isEmpty()){
                rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where groupcode='" + groupcode + "' and country='" + objGlobal.getCountryCode() + "'" + where, objGlobal.getConnection());
                if(rs.next()) message=rs.getString("Caption");
            }
            objPriceCheckScanDetail.setMessage(message);

            if (!dbConnection.insertUpdate("insert into bfldata.dbo.whAgeditems_Log values('" + boxno + "','" + itemcode + "','" + message + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserName() + "')", objGlobal.getConnection())) {
                objGlobal.setErrorMessage("Log not saved. Please try again");
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.tempWhagedItems values('" + boxno + "','" + itemcode + "','" + message + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserName() + "','" + objGlobal.getDeviceName() + "','"+groupcode+"')", objGlobal.getConnection())) {
                objGlobal.setErrorMessage("Data not cached. Please scan this item again.");
            }
            objPriceCheckScanDetail.setMessage(message);
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean closeBoxes(String boxno){
        Boolean status = false;
        String realBoxno = "";
        try {
            rs = dbConnection.getResultSet("Select * from usa.dbo.upcboxhead where boxno = '" + boxno + "' or toteId = '" + boxno + "' and closed = 'N'"
                    , objGlobal.getConnection());
            if (rs.next()) {
                realBoxno = rs.getString("boxno");
                objPriceCheckScanDetail.setBoxType("USABOX");
            }
            else{
                rs = dbConnection.getResultSet(  "select * from bfldata..tcmboxesheader where totid = '"+boxno+"' or totId = '"+boxno+"' and closed = 'N'",
                        objGlobal.getConnection());
                if (rs.next()){
                    realBoxno = rs.getString("boxno");
                    objPriceCheckScanDetail.setBoxType("TCMBOX");
                }
                else{
                    objGlobal.setErrorMessage("PriceCheckControl5: Box or Tote is invalid or already closed");
                }
            }
        }
        catch (Exception e){
            objGlobal.setErrorMessage("PriceCheckControl4: Box or Tote is invalid or already closed");
        }

        status = dbConnection.insertUpdate(
                "insert into bfldata.dbo.CloseR1pallet values('"+objPriceCheckScanDetail.getBoxType()+"','"+realBoxno+"',cast(getdate() as date),cast(getdate() as time),'"+objGlobal.getUserId()+"','"+objGlobal.getUserName()+"','','',0,0,0,'from pda items pullout')",
                objGlobal.getConnection()
        );
        if (status){
            if (objPriceCheckScanDetail.getBoxType().equals("USABOX")) {
                status = dbConnection.insertUpdate(
                        "update usa.dbo.upcboxhead set Closed = 'Y' Where boxno = '"+realBoxno+"' and closed = 'N'",
                        objGlobal.getConnection()
                );
                if (!status)
                    objGlobal.setErrorMessage("PriceCheckControl1: box or toteId is not closed, Contact IT");
            }
            else{
                status = dbConnection.insertUpdate(
                        "update bfldata.dbo.TCMBoxes set Closed = 'Y' Where boxno = '"+realBoxno+"' and closed = 'N'",
                        objGlobal.getConnection()
                );
                if (!status)
                    objGlobal.setErrorMessage("PriceCheckControl2: box or toteId is not closed, Contact IT");
            }
        }
        else
            objGlobal.setErrorMessage("PriceCheckControl3: box or toteId is not closed, Contact IT");
        return status;
    }
}
