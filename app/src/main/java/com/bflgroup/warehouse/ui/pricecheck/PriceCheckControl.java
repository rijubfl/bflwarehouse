package com.bflgroup.warehouse.ui.pricecheck;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.Statement;

public class PriceCheckControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    //private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private PriceCheckScanDetail objPriceCheckScanDetail = PriceCheckScanDetail.getInstance();

    private boolean b_Result;

    Statement stmt;
    boolean result;
    private ResultSet rs;

    public PriceCheckControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PriceCheckControl : Local Connection error");
        }
        objGlobal.setCloudDbName("BFLDATA");
        result = dbConnection.connectCloudDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error 1.0");
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
        if (dbConnection.checkCloudConnectionClosed()==false) {
            objGlobal.setCloudDbName("BFLDATA");
            result = dbConnection.connectCloudDb();
            if (result == false) {
                objGlobal.setErrorMessage("PriceCheckControl.checkConnection : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }
    String seperateBarcode(String barcode)
    { String[] parts;
        String part1;
        int i;
        if(barcode.contains("/"))
        { parts = barcode.split("/"); part1=parts[0]; }
        else { part1=barcode;
        }
        for (i = 0; i < part1.length() - 1; i++)
        {
            if (part1.charAt(i) != '0') { break; }
        }
        return part1.substring(i);
    }

    public boolean getItemDetails(String scan) {
        if (!checkConnection()) {
            return false;
        }
        try {
            String itemcode = "", groupcode = "", message="";
            float finalPrice = 0, discPer = 0;
//            if (scan.contains("/")) {
//                String[] scanAr = scan.split("/");
//                itemcode = scanAr[0];
//            } else {
//                itemcode = scan;
//            }

            itemcode = seperateBarcode(scan);


//            rs = dbConnection.getResultSet("select itemcode from rfpair where rfid='" + itemcode + "'", objGlobal.getConnection());
//            if (rs.next()) {
//                itemcode = rs.getString("itemcode");
//            }
            rs = dbConnection.getResultSet("select itemcode,description,ItemType,groupcode,GrpName=isnull((select description from bflksa..itemgroup where groupcode=a.groupcode),''),SalesPrice=isnull((select Top 1 Salesrate from bflksa..Salesprice " +
                    "where Itemcode=a.Itemcode and costcode = '005'),0),discount=isnull((select discount from bflksa..itemdisc where itemcode=a.itemcode),0),stock=(select quantity from bflksa..locstock " +
                    "where itemcode=a.itemcode and costcode = '005') from BFLKSA..itemmaster a where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objPriceCheckScanDetail.setItemcode(rs.getString("itemcode"));
                objPriceCheckScanDetail.setDescription(rs.getString("description"));
                objPriceCheckScanDetail.setGroup(rs.getString("GrpName"));
                objPriceCheckScanDetail.setOldPrice(rs.getFloat("SalesPrice"));
                objPriceCheckScanDetail.setItemType(rs.getString("ItemType"));
                objPriceCheckScanDetail.setStock(rs.getInt("stock"));
                discPer = rs.getFloat("discount");
//                if (objPosGlobal.getApplyItemDiscount().equals("N")) {
//                    discPer = 0;
//                }
                finalPrice = objPriceCheckScanDetail.getOldPrice() - (objPriceCheckScanDetail.getOldPrice() * discPer / 100);
                objPriceCheckScanDetail.setDiscPerc(discPer);
                objPriceCheckScanDetail.setPrice(finalPrice);
                groupcode = rs.getString("groupcode");
                objPriceCheckScanDetail.setDepartment("");
                objPriceCheckScanDetail.setDivision("");

                rs = dbConnection.getResultSet("select department,DivisionY from usa..usapriority where groupcode='" + groupcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objPriceCheckScanDetail.setDepartment(rs.getString("department"));
                    objPriceCheckScanDetail.setDivision(rs.getString("DivisionY"));
                }
                objPriceCheckScanDetail.setMessage("");
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where itemcode='" + itemcode + "' and country='" + objGlobal.getCountryCode() + "'", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where itemcode='" + itemcode + "' and shop = 'KSA WH' ", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                objPriceCheckScanDetail.setMessage(message);
            } else {
                objGlobal.setErrorMessage("Invalid itemcode / Barcode ");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }
}
