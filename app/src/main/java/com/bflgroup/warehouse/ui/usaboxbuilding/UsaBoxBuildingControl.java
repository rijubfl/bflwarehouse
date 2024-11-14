package com.bflgroup.warehouse.ui.usaboxbuilding;

import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Controls;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        if (b_Result == false) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("USA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
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

    public boolean validateMain(String palletType, String groupCode, String catCode, String remarks, String taskType, String doneBy, String fSize, String gender, String toteID, String allowMix, String buildType, String euro) {
        boolean blueBox = false;
        if (TextUtils.isEmpty(objGlobal.getWarehouse())) {
            objGlobal.setErrorNo("savePallet:Warehouse is empty");
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
            rs = dbConnection.getResultSet("select BlueBox=isnull(BlueBox,'') from bfldata.dbo.PalletType where PalletType='" + palletType + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getString("BlueBox").equals("Y")) blueBox = true;
            }
            if (euro.equals("Y")) blueBox = false;
            if (blueBox) {
                if (TextUtils.isEmpty(toteID)) {
                    objGlobal.setErrorMessage("Please enter ToteID");
                    return false;
                }
            }
            if (!TextUtils.isEmpty(toteID)) {
                if(objGlobal.getWorkLocation().equals("KSA")) {
                    rs = dbConnection.getResultSet("select * from bflksa.dbo.ToteIDMaster where ToteID='" + toteID + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("ToteID " + toteID + " is invalid");
                        return false;
                    }
                }  else if(objGlobal.getWorkLocation().equals("BAHRAIN")) {
                    rs = dbConnection.getResultSet("select * from bflbahrain.dbo.ToteIDMaster where ToteID='" + toteID + "'", objGlobal.getConnection());
                    if (!rs.next()) {
                        objGlobal.setErrorMessage("ToteID " + toteID + " is invalid");
                        return false;
                    }
                }
                else {
                    rs = dbConnection.getResultSet("select * from bfldata.dbo.BlueToteIDMaster where ToteID='" + toteID + "'", objGlobal.getConnection());
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

    public boolean validateItemcode(boolean edit, String itemcode, String selGroupCode, String selCategory, String selPalletype, String gender, int qty, String allowMix, String boxType) {
        try {
            itemcode = objControls.seperateBarcode(objControls.replaceString(itemcode));
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (edit) {
                    if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set qty=" + qty + " where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                        return false;
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
                if (!dbConnection.insertUpdate("update bfldata.dbo.tmpScanItemsBox set ItemName=isnull(b.Description,''),groupcode=isnull(b.groupcode,'') from bfldata.dbo.tmpScanItemsBox a," +
                        "HODATA.dbo.ItemMaster b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.ItemCode and isnull(a.ItemName,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
                    objGlobal.setErrorMessage("UsaBoxBuildingControl:validateItemcode: Item not found -" + itemcode);
                    return false;
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
                        "usa.dbo.upcbarcodes b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.itemcode=b.upc and isnull(a.season,'')='' and a.itemcode='" + itemcode + "'", objGlobal.getConnection())) {
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
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and (isnull(itemname,'')='' or isnull(GroupCode,'')='' or " +
                    "isnull(BuildingCategory,'')='')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Item Name or Group Code or Building Category is blank - " + itemcode);
                valid = false;
            }
            if (!allowMix.equals("Y")) {
                rs = dbConnection.getResultSet("select cnt=count(distinct BuildingCategory) from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' having " +
                        "count(distinct BuildingCategory)>1", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Multiple Building Category is not allowed - " + itemcode);
                    valid = false;
                }
            }
            if (boxType.equals("TCM")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and Division<>'TCM'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found in TCM Division, itemcode: " + itemcode);
                    valid = false;
                }
            }
            if (boxType.equals("USA")) {
               /* rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and Division='TCM'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found in TCM Division, itemcode: " + itemcode);
                    valid = false;
                }*/
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
            if (selPalletype.equals("RW") || selPalletype.equals("YH") || selPalletype.equals("BX")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and season<>'W'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found Season Summer, itemcode: " + itemcode);
                    valid = false;
                }
            }
            if (selPalletype.equals("R1") || selPalletype.equals("YG") || selPalletype.equals("AX")) {
                rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "' and season='W'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("Some items found Season Winter, itemcode: " + itemcode);
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
            rs = dbConnection.getResultSet("select BuildCategoryMixAllow=isnull(BuildCategoryMixAllow,'') from bfldata.dbo.PalletType where PalletType='" + palletType + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (rs.getString("BuildCategoryMixAllow").equals("Y"))
                    objUsaBoxBuildingGlobal.setBuildCategoryMixAllow("Y");
//                if (objGlobal.getUserAllowMixCategoryBuild().equals("Y"))
//                    objUsaBoxBuildingGlobal.setBuildCategoryMixAllow("Y");
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getPalletTypeDetails:ex:" + ex);
            return false;
        }
    }

    public boolean saveBox(String palletType, String groupCode, String catCode, String remarks, String taskType, String doneBy, String fSize, String gender, String toteID, String buildTyp, String euro) {
        try {
            String boxPType = "", pltPType = "";
            String pltRemarks = "Euro Pallet Building in A-PDA/" + objGlobal.getUserName();
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
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.R1PalletHead(SN,PalletNo,TrnDate,Time1,NewPallet,PreparedBy,Remarks,UserId,PalletType,Closed,GrNo,PltNo,WHouse,FWType,FPreparedBy,FPalletType) " +
                            "values(" + objUsaBoxBuildingGlobal.getPalletSno() + ",'" + objUsaBoxBuildingGlobal.getPalletNo() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "',''," +
                            "'" + objGlobal.getUserName() + "','" + pltRemarks + "'," + objGlobal.getUserId() + ",'" + palletType + "','N',0,0,'" + objGlobal.getWarehouse() + "','','','" + palletType + "')", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.R1PalletDet(SN,Itemcode,Qty,QtyIssued,PalletNo1,Status,BoxNo,ToteID) select " + objUsaBoxBuildingGlobal.getPalletSno() + ",Itemcode,Qty,0," +
                            "'" + objUsaBoxBuildingGlobal.getPalletNo() + "','','','' from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    pltPType = "TP";
                } else {
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
                    boxPType = "TB";
                }
            } else {
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
                boxPType = "UB";
                if (euro.equals("Y")) {
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.usapallets(Sn,TrnDate,PalletNo,UserId,Remarks,Closed,ContNo,WHouse) values (" + objUsaBoxBuildingGlobal.getPalletSno() + "," +
                            "'" + objGlobal.getServerDate() + "','" + objUsaBoxBuildingGlobal.getPalletNo() + "'," + objGlobal.getUserId() + ",'" + pltRemarks + "','N','','" + objGlobal.getWarehouse() + "')", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into bfldata.dbo.usapalletsdet(Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID) select top 1 " + objUsaBoxBuildingGlobal.getPalletSno() + "," +
                            "'" + objUsaBoxBuildingGlobal.getBoxNo() + "',itemcode,Department,Qty,'" + objGlobal.getUserName() + "','','" + palletType + "','' from bfldata.dbo.tmpScanItemsBox where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("insert into usa.dbo.BoXPallet(Boxno,Palletno) values ('" + objUsaBoxBuildingGlobal.getBoxNo() + "','" + objUsaBoxBuildingGlobal.getPalletNo() + "')", objGlobal.getConnection())) {
                        return false;
                    }
                    pltPType = "UP";
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
                rs = dbConnection.getResultSet("select sn=Max(sn)+1 from bfldata.dbo.USAPallets", objGlobal.getConnection());
                if (rs.next()) {
                    objUsaBoxBuildingGlobal.setPalletSno(rs.getString("sn"));
                }
                rs = dbConnection.getResultSet("select sn=max(substring(palletno,4,7))+1 from bfldata.dbo.USApallets where palletno like 'USA%'", objGlobal.getConnection());
                if (rs.next()) {
                    pltSn = Integer.parseInt(rs.getString("sn"));
                }
                objUsaBoxBuildingGlobal.setPalletNo("USA" + String.format("%06d", pltSn));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("UsaBoxBuildingControl:getPalletNumber:" + ex);
            return false;
        }
        return true;
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


    /*public boolean forPrint(String boxno) {
        objPalletBuildingGlobal.setpPalletno("");
        objPalletBuildingGlobal.setpBoxcnt("");
        objPalletBuildingGlobal.setpRemarks("");
        objPalletBuildingGlobal.setpPallettype("");
        objPalletBuildingGlobal.setpTypename("");
        objPalletBuildingGlobal.setpGroupname("");
        objPalletBuildingGlobal.setpPreparedby("");
        objPalletBuildingGlobal.setpDate("");
        objPalletBuildingGlobal.setpTime("");
        String sn="";
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
    }*/
}