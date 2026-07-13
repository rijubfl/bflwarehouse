package com.bflgroup.warehouse.ui.usaboxbuilding;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsaBoxBuildingControl {
    private DBConnection dbConnection = new DBConnection();
    private Controls objControls = new Controls();
    private Global objGlobal = Global.getInstance();
    private UsaBoxBuildingGlobal objUsaBoxBuildingGlobal = UsaBoxBuildingGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public UsaBoxBuildingControl() {
        objGlobal.setDbName("USA");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("USA");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("BinBatchInControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    List<String> loadSpinner(String type) {
        String str = "";
        List<String> arr;
        arr = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        if (type.equals("PT"))
            str = "select pallettype+' - '+TypeName " + type + " from bfldata.dbo.PalletType Order By pallettype";
        if (type.equals("SZ"))//size
            str = "select size " + type + " from usa.dbo.SizeForPda Order By size";
        if (type.equals("TS"))//done
            str = "select Distinct FType " + type + " From BFLDATA.dbo.FactoryEmployee order by FType";
        if (type.equals("DN"))
            str = "select cast(ID as varchar) +' - '+EmpName " + type + " From BFLDATA.dbo.FactoryEmployee where Active='Y' order by EmpName";
        try {
            arr.add("N/A");
            rs = dbConnection.getResultSet(str, objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString(type));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:loadSpinner:" + ex);
            return null;
        }
        return arr;
    }

    public boolean validateMain(String printer, String palletType, String groupCode, String catCode, String remarks, String taskType, String doneBy, String fSize, String gender, String toteID, String allowMix, String buildType, String euro, String spcitems) {
        if (TextUtils.isEmpty(objGlobal.getWarehouse())) {
            objGlobal.setErrorNo("savePallet:Warehouse is empty");
            return false;
        }
        if (TextUtils.isEmpty(printer)) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:validateMain: Printer is blank, can't proceed");
            return false;
        }
        if (TextUtils.isEmpty(palletType)) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:validateMain: Pallet Type is blank, can't proceed");
            return false;
        }
        try {
            if (!checkConnection()) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Please Scan Item");
                return false;
            }
            boolean blueBox = false;
            if (objUsaBoxBuildingGlobal.getNeedBlueBox().equals("Y")) blueBox = true;
            if (euro.equals("Y")) blueBox = false;
            if (blueBox) {
                if (TextUtils.isEmpty(toteID)) {
                    objGlobal.setErrorMessage("Please enter ToteID");
                    return false;
                }
            }
            if (!TextUtils.isEmpty(toteID)) {
                if (objGlobal.getWorkLocation().equals("UAE")) {
                    rs = dbConnection.getResultSet("select * from bfldata.dbo.BlueToteIDMaster where ToteID='" + toteID + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("ToteID " + toteID + " is invalid");
                        return false;
                    }
                } else {
                    rs = dbConnection.getResultSet("select * from " + objGlobal.getCountryDbName() + ".dbo.ToteIDMaster where ToteID='" + toteID + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("ToteID " + toteID + " is invalid");
                        return false;
                    }
                }
                rs = dbConnection.getResultSet("select BoxNo from usa.dbo.upcboxhead where ToteID='" + toteID + "' and closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("UsaBoxBuildingControl:validateMain: Tote is used already in another box, Box:" + rs.getString("BoxNo"));
                    return false;
                }
                rs = dbConnection.getResultSet("select distinct a.Boxno from bfldata.dbo.TcmboxesHeader a, bfldata.dbo.TCMBoxes b where a.boxno=b.BoxNo and a.TotId='" + toteID + "' and b.Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("UsaBoxBuildingControl:validateMain: Tote is used already in another box, Box:" + rs.getString("BoxNo"));
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:validateMain:" + ex);
            return false;
        }
    }

    public boolean validateItemcode(boolean edit, String itemcode, String selGroupCode, String selCategory, String selPalletype, String gender, int qty, String allowMix, String boxType, String selitems, String contno) {
        if (itemcode.trim().equals("")) {
            objGlobal.setErrorMessage("Empty itemcode, please rescan");
            return false;
        }
        if (qty == 0) {
            if (!edit) {
                objGlobal.setErrorMessage("Qty is 0");
                return false;
            }
        }
        try {
            if (itemcode.startsWith("0")) {
                itemcode = itemcode.replaceFirst("^0+", "");
            }
            rs = dbConnection.getResultSet("select top 1 itemcode from bfldata.dbo.ScanWrongItem where (itemcode like '%" + itemcode + "%' or itemcode like '%" + itemcode + "%')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Invalid itemcode. Please check" + itemcode);
                return false;
            }
            String groupCode = "", division = "";
            rs = dbConnection.getResultSet("select top 1 itemcode,groupcode from usa.dbo.upcbarcodes where upc='" + itemcode + "' order by trndate desc", objGlobal.getConnection());
            if (rs.next()) {
                itemcode = rs.getString("itemcode").toString();
                groupCode = rs.getString("groupcode");
            } else {
                itemcode = objControls.seperateBarcode(objControls.replaceString(itemcode));
            }
            rs = dbConnection.getResultSet("select top 1 divisionY from usa..usapriority where groupcode = '" + groupCode + "'", objGlobal.getConnection());
            if (rs.next()) {
                division = rs.getString("divisionY");
                    rs = dbConnection.getResultSet("select top 1 division from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        if (!rs.getString("Division").equals("LFL") && division.equals("LFL")) {
                            objGlobal.setErrorMessage("The scanned item " + itemcode + " is LFL item. Please build with LFL items only");
                            return false;
                        }
                        if (rs.getString("Division").equals("LFL") && !division.equals("LFL")) {
                            objGlobal.setErrorMessage("The scanned item " + itemcode + " is not LFL item. Please build with non-LFL items");
                            return false;
                        }
                    }
            }
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (edit) {
                    if (qty == 0) {
                        if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                            return false;
                        }
                    } else {
                        if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set qty=" + qty + " where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                            return false;
                        }
                    }
                } else {
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set qty=qty+" + qty + " where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        return false;
                    }
                }
            } else {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpScanItemsBox(DeviceId,Itemcode,itemname,GroupCode,season,qty,Division,Department) " +
                        "values('" + objGlobal.getDeviceName() + "','" + itemcode + "','','',''," + qty + ",'','')", objGlobal.getConnection())) {
                    return false;
                }
                if (objGlobal.getWorkLocation().equals("UAE")) {
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.Description,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                            "HODATA.dbo.ItemMaster b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                        return false;
                    }
                } else {
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.Description,''),groupcode=isnull(b.groupcode,'') from " +
                            "bfldata.dbo.tmpScanItemsBox a," + objGlobal.getCountryDbName() + ".dbo.ItemMaster b where a.DeviceId='" + objGlobal.getDeviceName() + "' and " +
                            "a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                        return false;
                    }
                }

                if (selPalletype.equals("MB")) {
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.itemName,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                            "usa.dbo.upcbarcodes b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.itemName,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                            "usa.dbo.usaOrgFile b where a.DeviceId='" + objGlobal.getDeviceName() + "' and contno = '" + contno + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.itemName,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                            "usa.dbo.usaOrgFile b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                        return false;
                    }
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.Description,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "consignment.dbo.ItemMaster b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'') ='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.itemname,''),groupcode=isnull(b.groupcode,''),season=isnull(b.season,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "online.dbo.ManualUPCSize b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.upc and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.Itemname,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "usa.dbo.GenerateBarcode b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.barcode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set season=isnull(b.itemtype,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "usa.dbo.upcbarcodes b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.Itemcode and isnull(a.season,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set season=isnull(b.itemtype,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "abudata.dbo.itemtypelib b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.itemcode and isnull(a.season,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set Season=isnull((select top 1 Season from usa.dbo.upcshorts where upc=a.itemcode order by TRndate desc, time1 desc),'') " +
                        "from bfldata.dbo.tmpScanItemsBox a where a.DeviceId='" + objGlobal.getDeviceName() + "' and isnull(a.Season,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set department=b.Department,updatediv=b.updatedept,BuildingCategory=b.BuildingCategory from bfldata.dbo.tmpScanItemsBox a,usa.dbo.USAPriority b where " +
                        "a.deviceid='" + objGlobal.getDeviceName() + "' and a.Itemcode='" + itemcode + "' and isnull(a.department,'')='' and a.GroupCode=b.groupCode", objGlobal.getConnection())) {
                    return false;
                }
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set Division=b.Division from bfldata.dbo.tmpScanItemsBox a,BFLDATA.dbo.DeptStock b where " +
                        "a.deviceid='" + objGlobal.getDeviceName() + "' and a.Itemcode='" + itemcode + "' and isnull(a.Division,'')='' and a.department=b.department", objGlobal.getConnection())) {
                    return false;
                }
            }
            boolean valid = true;
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and (isnull(itemcode,'')='' or isnull(itemname,'')='' or isnull(GroupCode,'')='' or " +
                    "isnull(BuildingCategory,'')='')", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getString("itemcode").isEmpty()) {
                    objGlobal.setErrorMessage("Item code is empty");
                    valid = false;
                } else if (rs.getString("itemname").isEmpty()) {
                    objGlobal.setErrorMessage("Item code (" + itemcode + ") is invalid");
                    valid = false;
                } else if (rs.getString("GroupCode").isEmpty()) {
                    objGlobal.setErrorMessage("Group code  is missing.");
                    valid = false;
                } else if (rs.getString("BuildingCategory").isEmpty()) {
                    objGlobal.setErrorMessage("Building Category is missing.");
                    valid = false;
                }
            }
//            if (!allowMix.equals("Y")) {
//                rs = dbConnection.getResultSet("select cnt=count(distinct BuildingCategory) from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' having " +
//                        "count(distinct BuildingCategory)>1", objGlobal.getConnection());
//                if (rs.next()) {
//                    objGlobal.setErrorMessage("Multiple Building Category is not allowed - " + itemcode);
//                    valid = false;
//                }
//            }
            if (boxType.equals("TCM")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and Division<>'TCM'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found in TCM Division, itemcode: " + itemcode);
                    valid = false;
                }
            }
            if (selPalletype.equals("PS") || selPalletype.equals("PW") || selPalletype.equals("PT") || selPalletype.equals("PV")) {
                rs = dbConnection.getResultSet("select * from online.dbo.PhotoShoot where upc='" + itemcode + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("ItemCode is not found for Photo Shoot - " + itemcode);
                    valid = false;
                } else {
                    if (!gender.equals(rs.getString("gender").toString().trim())) {
                        objGlobal.setErrorMessage("Please check, ItemCode " + itemcode + " - is not matching with Gender mentioned in Box!");
                        valid = false;
                    }
                }
            }
            if (selPalletype.equals("PC")) {
                rs = dbConnection.getResultSet("select * from usa..USAPriceChange where itemcode = '" + itemcode + "' and NewPrice > 1", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Cannot build with Price Checking(PC) type. The item " + itemcode + " already has a selling price.");
                    valid = false;
                }
            }

            if (selPalletype.equals("RW") || selPalletype.equals("YH") || selPalletype.equals("BX") || selPalletype.equals("BZ") || selPalletype.equals("OW")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and season<>'W'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found Season Summer, itemcode: " + itemcode);
                    valid = false;
                }
            }
            if (selPalletype.equals("R1") || selPalletype.equals("YG") || selPalletype.equals("AX") || selPalletype.equals("BZ") || selPalletype.equals("OH")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and season='W'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found Season Winter, itemcode: " + itemcode);
                    valid = false;
                }
            }
            if (selitems.equals("Y")) {
                rs = dbConnection.getResultSet("select PalletTypes from bfldata.dbo.BuildSpecialType where itemcode='" + itemcode + "' and PalletTypes='" + selPalletype + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Itemcode (" + itemcode + ") is not allowed to build this pallet type (" + selPalletype + ")");
                    valid = false;
                }
            } else {
                rs = dbConnection.getResultSet("select PalletTypes from bfldata.dbo.BuildSpecialType where itemcode='" + itemcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Itemcode (" + itemcode + ") is not allowed to build this pallet type (" + selPalletype + ")");
                    valid = false;
                }
            }
            objUsaBoxBuildingGlobal.setScanBuildingCategory("");
            objUsaBoxBuildingGlobal.setScanDepartment("");
            objUsaBoxBuildingGlobal.setScanDivision("");
            rs = dbConnection.getResultSet("select buildingcategory=isnull(buildingcategory,''),department=isnull(department,''),division=isnull(division,'')," +
                    "season=(case when season='W' then 'WINTER' else 'SUMMER' end) from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and " +
                    "Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objUsaBoxBuildingGlobal.setScanBuildingCategory(rs.getString("buildingcategory") + " - " + rs.getString("season"));
                objUsaBoxBuildingGlobal.setScanDepartment(rs.getString("department"));
                objUsaBoxBuildingGlobal.setScanDivision(rs.getString("division"));
            }
            if (objUsaBoxBuildingGlobal.getValidateHoStock().equals("Y")) {
                int hoQty = 0;
                rs = dbConnection.getResultSet("select quantity from HODATA.dbo.LocStock where Itemcode='" + itemcode + "' and COSTCODE='001' and LOCCODE='01'", objGlobal.getConnection());
                if (rs.next()) {
                    hoQty = rs.getInt("quantity");
                }
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "' and qty>" + hoQty, objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Cannot proceed, itemcode(" + itemcode + "). Scanned quantity is " + rs.getString("qty") + ", but only " + hoQty + " are available in HO.");
                    valid = false;
                }
            }
            if (!valid) {
                if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpScanItemsBox where itemcode='" + itemcode + "' and DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    valid = false;
                }
            }
            return valid;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: " + ex);
            return false;
        }
    }

    ArrayList<UsaBoxBuildingScaItemsPopupTicket> loadPopupScanItems() {
        ArrayList<UsaBoxBuildingScaItemsPopupTicket> listPopupScanItems = new ArrayList<UsaBoxBuildingScaItemsPopupTicket>();
        try {
            listPopupScanItems.clear();
            int scanTotQty = 0;
            rs = dbConnection.getResultSet("select itemcode,itemname,qty from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' order by sn desc", objGlobal.getConnection());
            while (rs.next()) {
                listPopupScanItems.add(new UsaBoxBuildingScaItemsPopupTicket(rs.getString("itemcode").toString(), rs.getString("qty").toString()));
                scanTotQty = scanTotQty + rs.getInt("qty");
            }
            objUsaBoxBuildingGlobal.setScanTotalQty(scanTotQty);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:loadPopupScanItems:" + ex);
            return null;
        }
        return listPopupScanItems;
    }

    ArrayList<UsaBoxBuildingScanItemTicket> loadScanItems() {
        ArrayList<UsaBoxBuildingScanItemTicket> listScanItems = new ArrayList<UsaBoxBuildingScanItemTicket>();
        try {
            listScanItems.clear();
            objUsaBoxBuildingGlobal.setBuildingCategory("");

            rs = dbConnection.getResultSet("select itemcode,itemname=isnull(itemname,''),qty,Buildingcategory=isnull(Buildingcategory,'') from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' order by sn desc", objGlobal.getConnection());
            while (rs.next()) {
                objUsaBoxBuildingGlobal.setBuildingCategory(rs.getString("Buildingcategory"));
                listScanItems.add(new UsaBoxBuildingScanItemTicket(rs.getString("itemcode"), rs.getString("itemname"), rs.getString("qty")));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:loadScanItems:" + ex);
            return null;
        }
        return listScanItems;
    }

    public boolean getPalletTypeDetails(String palletType) {
        try {
            objUsaBoxBuildingGlobal.setBuildCategoryMixAllow("N");
            objUsaBoxBuildingGlobal.setBuildSpecialPtype("");
            objUsaBoxBuildingGlobal.setBuildingSeason("");
            objUsaBoxBuildingGlobal.setNeedBlueBox("N");
            objUsaBoxBuildingGlobal.setValidateHoStock("N");
            objUsaBoxBuildingGlobal.setAllowInvalidItem("N");
            rs = dbConnection.getResultSet("select BuildCategoryMixAllow=isnull(BuildCategoryMixAllow,''),season=isnull(season,''),BuildSelItems=isnull(BuildSelItems,''),BlueBox=isnull(BlueBox,'')," +
                    "ValidateHoStock=isnull(ValidateHoStock,''),AllowInvalidItem=isnull(AllowInvalidItem,'') from bfldata.dbo.PalletType where PalletType='" + palletType + "'", objGlobal.getConnection());
            if (rs.next()) {
                objUsaBoxBuildingGlobal.setBuildingSeason(rs.getString("season"));
                if (rs.getString("BuildCategoryMixAllow").equals("Y"))
                    objUsaBoxBuildingGlobal.setBuildCategoryMixAllow("Y");
                if (rs.getString("BuildSelItems").equals("Y"))
                    objUsaBoxBuildingGlobal.setBuildSpecialPtype("Y");
                if (rs.getString("BlueBox").equals("Y"))
                    objUsaBoxBuildingGlobal.setNeedBlueBox("Y");
                if (rs.getString("ValidateHoStock").equals("Y"))
                    objUsaBoxBuildingGlobal.setValidateHoStock("Y");
                if (rs.getString("AllowInvalidItem").equals("Y"))
                    objUsaBoxBuildingGlobal.setAllowInvalidItem("Y");
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getPalletTypeDetails:ex:" + ex);
            return false;
        }
    }

    public boolean saveBox(String palletType, String groupCode, String catCode, String remarks, String taskType, String doneBy, String fSize, String gender, String toteID, String buildTyp, String euro) {
        try {
            String boxPType = "";
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) return false;
            b_Result = getBoxNumber(buildTyp);
            if (!b_Result) return false;
            if (euro.equals("Y")) {
                b_Result = getPalletNumber(buildTyp);
                if (!b_Result) return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (buildTyp.equals("TCM")) {
                if (euro.equals("Y")) {
                    boxPType = "TP";
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.R1PalletHead(SN,PalletNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,UserId,PalletType,Closed,GrNo,PltNo,WHouse,FWType,FPreparedBy,FPalletType) " +
                            "values(" + objUsaBoxBuildingGlobal.getPalletSno() + ",'" + objUsaBoxBuildingGlobal.getPalletNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "',''," +
                            "'" + objGlobal.getUserName() + "','" + remarks + "'," + objGlobal.getUserId() + ",'" + palletType + "','N',0,0,'" + objGlobal.getWarehouse() + "','','','" + palletType + "')", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.R1PalletDet(SN,Itemcode,Qty,QtyIssued,PalletNo1,Status,BoxNo,ToteID) select " + objUsaBoxBuildingGlobal.getPalletSno() + ",Itemcode,Qty,0," +
                            "'" + objUsaBoxBuildingGlobal.getPalletNo() + "','','','' from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                } else {
                    boxPType = "TB";
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.tcmboxesheader(Boxno,TrnDate,Time1,UserId,TotId,Whouse) values ('" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "'," +
                            "'" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ",'" + toteID + "','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.TCMBoxes(BoxNo,TrnDate,Time1,countedby,Itemcode,Qty,PhaseType,Closed,Userid,Remarks,OldBoxNo) " +
                            "select '" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','" + objGlobal.getUserName() + "',Itemcode,Qty," +
                            "'" + palletType + "','N'," + objGlobal.getUserId() + ",'" + remarks + "','' from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                }
            } else {
                LocalTime time = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    time = LocalTime.now();
                }
                int hour = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    hour = time.getHour();
                }
                String hrFiled = FindHour(hour);
                if (palletType.equals("MB")) {
                    int scanQty = 0;
                    rs = dbConnection.getResultSet("select Qty = isnull(sum(Qty),0) from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        scanQty = rs.getInt("Qty");
                    }
                    rs = dbConnection.getResultSet("select * from ONLINE.dbo.RFPairingCountPhotoCheckBuild where type='" + palletType + "' and trndate='" + objGlobal.getServerDate() + "' and empcode='" + objGlobal.getUserName() + "' and Warehouse='" + objGlobal.getWarehouse() + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        if (!dbConnection.insertUpdate("insert into ONLINE.dbo.RFPairingCountPhotoCheckBuild (empcode,type,trndate," + hrFiled + ",Warehouse)  values( '" + objGlobal.getUserName() + "','" + palletType + "','" + objGlobal.getServerDate() + "'," + scanQty + ",'" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                            objGlobal.getConnection().rollback();
                            return false;
                        }
                    } else {
                        if (!dbConnection.insertUpdate("update ONLINE.dbo.RFPairingCountPhotoCheckBuild set " + hrFiled + "=" + hrFiled + "+" + scanQty + " where type='" + palletType + "' and empcode='" + objGlobal.getUserName() + "' and trndate='" + objGlobal.getServerDate() + "' and Warehouse='" + objGlobal.getWarehouse() + "'", objGlobal.getConnection())) {
                            objGlobal.getConnection().rollback();
                            return false;
                        }
                    }
                }
                boxPType = "UB";
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxHead (BoxNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,Userid,PalletType,Closed,GroupCode,OldBoxNo,Prepared1,Prepared2," +
                        "WHouse,FWType,FPreparedBy,FPalletType,ISize,Gender,ToteID) values ('" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','','" + objGlobal.getUserName() + "'," +
                        "'" + remarks + "','" + objGlobal.getUserId() + "','" + palletType + "','N','" + groupCode + "','" + catCode + "','" + objGlobal.getUserId() + "','" + objGlobal.getUserId() + "','" + objGlobal.getWarehouse() + "'," +
                        "'" + taskType + "','" + doneBy + "','" + palletType + "','" + fSize + "','" + gender + "','" + toteID + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into usa.dbo.UPCBoxDet(BoxNo,Itemcode,Qty,QtyIssued,Status,UPC) select '" + objUsaBoxBuildingGlobal.getBoxNo() + "',Itemcode,Qty,0,'',Itemcode from " +
                        "bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (euro.equals("Y")) {
                    boxPType = "UP";
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.usapallets(Sn,TrnDate,PalletNo,UserId,Remarks,Closed,ContNo,WHouse) values (" + objUsaBoxBuildingGlobal.getPalletSno() + "," +
                            "'" + objGlobal.getServerDate() + "','" + objUsaBoxBuildingGlobal.getPalletNo() + "'," + objGlobal.getUserId() + ",'" + remarks + "','N','','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.usapalletsdet(Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID) select top 1 " + objUsaBoxBuildingGlobal.getPalletSno() + "," +
                            "'" + objUsaBoxBuildingGlobal.getBoxNo() + "',itemcode,Department,Qty,'" + objGlobal.getUserName() + "','','" + palletType + "','' from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into usa.dbo.BoXPallet(Boxno,Palletno) values ('" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objUsaBoxBuildingGlobal.getPalletNo() + "')", objGlobal.getConnection())) {
                        return false;
                    }
                }
            }
            if (!boxPType.isEmpty()) {
                if (!dbConnection.insertUpdate("insert into bfldata.dbo.PrintFromPda(warehouse,PSystemName,PType,PItem,ReqUser,ReqDate,ReqTime,Printed) values ('" + objGlobal.getWarehouse() + "'," +
                        "'" + objGlobal.getUserPrinterName() + "','" + boxPType + "','" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "'," +
                        "'" + objGlobal.getServerTime() + "','N')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("UsaBoxBuildingControl:saveBox:ex:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("UsaBoxBuildingControl:saveBox:e:" + e);
                return false;
            }
            return false;
        }
    }

    private String FindHour(int Hour) {
        String FindHour = "";
        switch (Hour) {
            case 6:
                FindHour = "HR0A";
                break;
            case 7:
                FindHour = "HR1A";
                break;
            case 8:
                FindHour = "HR2A";
                break;
            case 9:
                FindHour = "HR3A";
                break;
            case 10:
                FindHour = "HR4A";
                break;
            case 11:
                FindHour = "HR5A";
                break;
            case 12:
                FindHour = "HR6A";
                break;
            case 13:
                FindHour = "HR7A";
                break;
            case 14:
                FindHour = "HR8A";
                break;
            case 15:
                FindHour = "HR9A";
                break;
            case 16:
                FindHour = "HR10A";
                break;
            case 17:
                FindHour = "HR11A";
                break;
            case 18:
                FindHour = "HR12A";
                break;
            case 19:
                FindHour = "HR13A";
                break;
            case 20:
                FindHour = "HR14A";
                break;
            case 21:
                FindHour = "HR15A";
                break;
            case 22:
                FindHour = "HR16A";
                break;
            case 23:
                FindHour = "HR17A";
                break;
            case 0:
                FindHour = "HR18A";
                break;
            case 1:
                FindHour = "HR19A";
                break;
            case 2:
                FindHour = "HR20A";
                break;
            case 3:
                FindHour = "HR21A";
                break;
            case 4:
                FindHour = "HR22A";
                break;
            case 5:
                FindHour = "HR23A";
                break;
        }
        return FindHour;
    }

    private boolean getBoxNumber(String buildType) {
        try {
            int autoSn = 0;
            String suff = "";
            Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(objGlobal.getServerDate());
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(dt);
            String yr = year.substring(2, 4);
            if (buildType.equals("TCM")) {
                suff = "T" + yr + "/";
                rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from bfldata.dbo.tcmboxesheader where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
            } else {
                suff = objGlobal.getCountryWiseBoxPrefix() + yr + "/";
                rs = dbConnection.getResultSet("select en=isnull(max(substring(boxno,5,6)),0)+1 from usa.dbo.UPCBoxHead where left(boxno,4)='" + suff + "'", objGlobal.getConnection());
            }
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en"));
            }
            objUsaBoxBuildingGlobal.setBoxNo(suff + String.format("%06d", autoSn));
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getBoxNumber:" + ex);
            return false;
        }
    }

    public boolean getPalletNumber(String buildType) {
        int pltSn = 0;
        try {
            if (buildType.equals("TCM")) {
                rs = dbConnection.getResultSet("select sn=max(sn)+1 from bfldata.dbo.R1PalletHead", objGlobal.getConnection());
                if (rs.next()) {
                    objUsaBoxBuildingGlobal.setPalletSno(rs.getString("sn"));
                }
                rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7)) from bfldata.dbo.R1PalletHead where palletno like 'PLT%'", objGlobal.getConnection());
                if (rs.next()) {
                    pltSn = Integer.parseInt(rs.getString("sn").toString().substring(0, 6));
                    if (rs.getString("sn").toString().substring(rs.getString("sn").toString().length() - 1).equals("B")) {
                        objUsaBoxBuildingGlobal.setPalletNo("PLT" + String.format("%06d", pltSn + 1) + "A");
                    } else {
                        objUsaBoxBuildingGlobal.setPalletNo("PLT" + String.format("%06d", pltSn) + "B");
                    }
                }
            } else {
                //String palletPrefix = "USA";
                String palletPrefix = "UAE";
                rs = dbConnection.getResultSet("select sn=Max(sn)+1 from bfldata.dbo.USAPallets", objGlobal.getConnection());
                if (rs.next()) {
                    objUsaBoxBuildingGlobal.setPalletSno(rs.getString("sn"));
                }
                rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7))+1 from bfldata.dbo.USApallets where palletno like '" + palletPrefix + "%'", objGlobal.getConnection());
                if (rs.next()) {
                    pltSn = Integer.parseInt(rs.getString("sn"));
                }
                objUsaBoxBuildingGlobal.setPalletNo(palletPrefix + String.format("%06d", pltSn));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getPalletNumber:" + ex);
            return false;
        }
        return true;
    }

    public boolean fetchBoxNo(String scan) {
        objUsaBoxBuildingGlobal.setpBoxno("");
        if (!checkConnection()) {
            return false;
        }
        if (scan.isEmpty()) {
            objGlobal.setErrorMessage("Please scan box number or toteid");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select top 1 BoxNo from usa.dbo.upcboxhead where closed='N' and (boxno='" + scan + "' or toteid='" + scan + "') order by trndate", objGlobal.getConnection());
            if (rs.next()) {
                objUsaBoxBuildingGlobal.setpBoxno(rs.getString("BoxNo"));
            } else {
                objGlobal.setErrorMessage("Invalid boxn number number or toteid - " + scan + "");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferControl:forPrint:" + ex);
            return false;
        }
    }

    public boolean forPrint(String boxno) {
        objUsaBoxBuildingGlobal.setpDate("");
        objUsaBoxBuildingGlobal.setpBoxno("");
        objUsaBoxBuildingGlobal.setpDate("");
        objUsaBoxBuildingGlobal.setpQty("");
        objUsaBoxBuildingGlobal.setpTime("");
        objUsaBoxBuildingGlobal.setpPallettype("");
        objUsaBoxBuildingGlobal.setpPreparedby("");
        objUsaBoxBuildingGlobal.setpRemarks("");
        objUsaBoxBuildingGlobal.setpTypename("");
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select BoxNo,Toteid,TrnDate=convert(varchar,TrnDate,103),Time1=convert(varchar,Time1,8),PalletType,PreparedBy,Remarks," +
                    "typename=(select typename from bfldata.dbo.pallettype where pallettype=a.pallettype),Qty=ISNULL((select sum(qty) from usa.dbo.upcboxdet where " +
                    "boxno=a.boxno),0) from usa.dbo.upcboxhead a where closed='N' and boxno='" + boxno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objUsaBoxBuildingGlobal.setpBoxno(rs.getString("BoxNo"));
                objUsaBoxBuildingGlobal.setpToteid(rs.getString("Toteid"));
                objUsaBoxBuildingGlobal.setpDate(rs.getString("TrnDate"));
                objUsaBoxBuildingGlobal.setpQty(rs.getString("Qty"));
                objUsaBoxBuildingGlobal.setpTime(rs.getString("Time1"));
                objUsaBoxBuildingGlobal.setpPallettype(rs.getString("typename"));
                objUsaBoxBuildingGlobal.setpPreparedby(rs.getString("PreparedBy"));
                objUsaBoxBuildingGlobal.setpRemarks(rs.getString("Remarks"));
                objUsaBoxBuildingGlobal.setpTypename(rs.getString("typename"));
            } else {
                objGlobal.setErrorMessage("TransferControl : Invalid boxn number number or toteid (" + boxno + ")");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("TransferControl:forPrint:" + ex);
            return false;
        }
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpScanItemsBox where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("PalletBuildingControl:clearTable:" + ex);
            return false;
        }
        return true;
    }
}