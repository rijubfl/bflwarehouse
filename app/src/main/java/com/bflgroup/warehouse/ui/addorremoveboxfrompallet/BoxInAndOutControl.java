package com.bflgroup.warehouse.ui.addorremoveboxfrompallet;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.addorremoveboxfrompallet.model.BoxInOutRequestData;
import com.bflgroup.warehouse.ui.binstorageputaway.BinPutAwayGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BoxInAndOutControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BinPutAwayGlobal objBinPutAwayGlobal = BinPutAwayGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    private int sn;

    public BoxInAndOutControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("WarehouseDeliveryControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("WarehouseDeliveryControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }


    public int GetGrnNum() {
        int num = 0;
        try {
            String query2 = "select srno = max(isnull(srno,0)) + 1 from bfldata..WHDepartmentGRNHead";
            rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
            while (rs.next()) {

                num = rs.getInt("srno");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);   // return false;
        }
        return num;
    }


    void okMessage(Activity activity, String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }


    public boolean boxChecking(String boxNo) {
        String query = "select * from usa.dbo.upcboxhead where (boxno='" + boxNo + "' or toteid='" + boxNo + "') and closed='N'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean boxCheckInTempDB(String boxNo) {
        String query = "select * from ITWORK..tmpBoxInAndOut where (BoxNo = '" + boxNo + "' or toteid = '" + boxNo + "') and deviceId = '"+objGlobal.getDeviceName()+"'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkTempDataForDelete(String boxNo) {
        String query = "select * from ITWORK..tmpBoxInAndOut Where (BoxNo = '" + boxNo + "' or toteid = '" + boxNo + "') and deviceId = '"+objGlobal.getDeviceName()+"' ";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                return true;
            } else return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public BoxInOutRequestData deleteBoxFromTempDB(String boxNo, String palletNo) {
        BoxInOutRequestData boxInOutRequestData = null;
        List<BoxInOutRequestData> boxInOutRequestDataList = new ArrayList<>();
        String query = "update ITWORK..tmpBoxInAndOut set  ActivityType = 2 where (BoxNo = '" + boxNo + "' or toteid = '" + boxNo + "') and deviceId = '"+objGlobal.getDeviceName()+"'";
        //   String query = "delete from ITWORK..tmpBoxInAndOut where BoxNo = '" + boxNo + "'";
        if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
            return null;
        } else {
            String query1 = "select * from ITWORK..tmpBoxInAndOut where (BoxNo = '" + boxNo + "' or toteid = '" + boxNo + "') and deviceId = '"+objGlobal.getDeviceName()+"'";
            ResultSet rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
            try {
                while (rs.next()) {
                    boxInOutRequestData = new BoxInOutRequestData(
                            "", String.valueOf(rs.getInt("Sn")), "",
                            "", rs.getInt("Qty"), rs.getString("BoxNo"),rs.getString("toteid"), rs.getString("IsNewBox"), palletNo, rs.getString("BoxNo"), rs.getInt("ActivityType")
                    );

                    // boxInOutRequestDataList.add(boxInOutRequestData);
                }


            } catch (Exception e) {
            }
        }
        return boxInOutRequestData;
    }

//    public boolean insertToNewTable(String boxNo, int Qty,  String selectedPalletNo, String selectedBoxNo) {
//
//                String query1 = "INSERT INTO ITWORK..tmpNewBoxInOrOut (BoxNo, Qty, IsNewBox, selectedPalletNo, selectedBoxNo, Sn) VALUES ('"+boxNo+"', '"+Qty+"', 'Y', '"+selectedPalletNo+"', '"+selectedBoxNo+"', 0)";
//                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
//                    return false;
//                }
//                else return true;
//    }

    public BoxInOutRequestData insertBoxToTempDB(String boxNo, String palletNo, int type) {
        String query = "select BoxNo,toteid,SUM(Qty) as Qty,IsNewBox = 'Y' from usa.dbo.vupcboxdet where (BoxNo='" + boxNo + "' or toteid='" + boxNo + "') and closed = 'N' GROUP BY  BoxNo,toteid";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                String query1 = "insert into ITWORK..tmpBoxInAndOut ( Boxno,Qty,IsnewBox,SelectedPalletno,SelectedBoxno,Sn,ActivityType,DeviceId,toteid) select BoxNo,SUM(Qty),IsNewBox = 'Y',selectedPalletNo ='" + palletNo + "',BoxNo,Sn = 0,ActivityType = '" + type + "',deviceId = '"+objGlobal.getDeviceName()+"',toteid from usa.dbo.vupcboxdet where (boxno = '" + boxNo + "' or toteid = '" + boxNo + "') and closed = 'N' GROUP BY  BoxNo,toteid";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    return null;
                } else {
                    BoxInOutRequestData boxInOutRequestData = new BoxInOutRequestData(
                            "", "", "",
                            "", rs.getInt("Qty"), rs.getString("BoxNo"),rs.getString("toteid"), rs.getString("IsNewBox"), palletNo, rs.getString("BoxNo"), type
                    );
                    return boxInOutRequestData;
                }
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer palletChecking(String palletNo) {
        String query = "select * from bfldata.dbo.usapallets where palletno='" + palletNo + "' and closed='N'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                sn = rs.getInt("Sn");
                return sn;
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addBoxChecking(String palletNo, String boxNo) {
        String cShopEligiblePlt = "";
        String cShopEligibleBox = "";
        String query = "Select ShopEligible from BFLDATA..PalletType where PalletType in(select top 1 PalletType from usa.dbo.vupcboxdet where palletno='" + palletNo + "')";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                cShopEligiblePlt = rs.getString("ShopEligible");
            }
            String query1 = "Select ShopEligible from BFLDATA..PalletType where PalletType in(select PalletType from usa.dbo.upcboxhead where (Boxno='" + boxNo + "' or toteid='" + boxNo + "') and closed = 'N')";
            ResultSet rs1 = dbConnection.getResultSet(query1, objGlobal.getConnection());
            if (rs1.next()) {
                cShopEligibleBox = rs1.getString("ShopEligible");
            }
            if (cShopEligiblePlt.equals(cShopEligibleBox) && (!cShopEligiblePlt.equals("") && !cShopEligibleBox.equals(""))) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeBoxChecking(String palletNo, String boxNo) {
        try {
            String query = "select * from usa.dbo.vupcboxdet where boxno='" + boxNo + "' and palletno='" + palletNo + "'";
            ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean clearTempTable() {
        String query = "delete from ITWORK..tmpBoxInAndOut where deviceId = '"+objGlobal.getDeviceName()+"'";
        if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
            return false;
        } else {
//            String query1 = "delete from ITWORK..tmpNewBoxInOrOut";
//            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
//                return false;
//            }
            return true;
        }
    }

    public List<BoxInOutRequestData> checkNewTempData() {
        List<BoxInOutRequestData> boxInOutRequestDataList = new ArrayList<>();
        String query = "select * from ITWORK..tmpBoxInAndOut where IsNewBox = 'Y' OR ActivityType = 2 and deviceId = '"+objGlobal.getDeviceName()+"'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        BoxInOutRequestData boxInOutRequestData = null;
        try {
            while (rs.next()) {
                boxInOutRequestData = new BoxInOutRequestData(
                        "", rs.getString("Sn"), "",
                        "", rs.getInt("Qty"), rs.getString("BoxNo"),rs.getString("toteid"), rs.getString("IsNewBox"), rs.getString("selectedPalletNo"),
                        rs.getString("selectedBoxNo"), rs.getInt("ActivityType")
                );
                if (rs.getString("selectedBoxNo").equals(rs.getString("BoxNo")))
                    boxInOutRequestDataList.add(0, boxInOutRequestData);
                else
                    boxInOutRequestDataList.add(boxInOutRequestData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return boxInOutRequestDataList;
    }

    public List<BoxInOutRequestData> checkTempData() {
        List<BoxInOutRequestData> boxInOutRequestDataList = new ArrayList<>();
        String query = "select * from ITWORK..tmpBoxInAndOut where IsNewBox = 'N' and deviceId = '"+objGlobal.getDeviceName()+"'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        BoxInOutRequestData boxInOutRequestData = null;
        try {
            while (rs.next()) {
                boxInOutRequestData = new BoxInOutRequestData(
                        "", rs.getString("Sn"), "",
                        "", rs.getInt("Qty"), rs.getString("BoxNo"), rs.getString("toteid"), rs.getString("IsNewBox"), rs.getString("selectedPalletNo"),
                        rs.getString("selectedBoxNo"), rs.getInt("ActivityType")
                );
                if (rs.getString("selectedBoxNo").equals(rs.getString("BoxNo")))
                    boxInOutRequestDataList.add(0, boxInOutRequestData);
                else
                    boxInOutRequestDataList.add(boxInOutRequestData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return boxInOutRequestDataList;
    }

    public List<BoxInOutRequestData> palletsDetails(String palletNo) {
        List<BoxInOutRequestData> boxInOutRequestDataList = new ArrayList<>();
        String query = "SELECT BoxNo,toteid,SUM(Qty) AS TotalQty FROM usa.dbo.vupcboxdet WHERE palletno = '" + palletNo + "' GROUP BY  BoxNo,toteid";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
//            if (rs.next()) {

            //  }
            boolean isFirstRun = true;
            while (rs.next()) {
                if (isFirstRun) {
                    String insertQuery = "insert into ITWORK..tmpBoxInAndOut( Boxno,Qty,IsnewBox,SelectedPalletno,SelectedBoxno,Sn,ActivityType,DeviceId, toteid) SELECT BoxNo,SUM(Qty) AS TotalQty,IsNewBox = 'N',selectedPalletNo = '" + palletNo + "',selectedBoxNo = '',Sn = 0,ActivityType = 0,deviceId = '"+objGlobal.getDeviceName()+"',toteid FROM usa.dbo.vupcboxdet WHERE palletno = '" + palletNo + "' GROUP BY  BoxNo,toteid";
                    if (!dbConnection.insertUpdate(insertQuery, objGlobal.getConnection())) {
                        return null;
                    }
                    isFirstRun = false;
                }
                String query2 = "select * from bfldata.dbo.USAPallets A, bfldata.dbo.USAPalletsDet B WHERE A.SN = B.Sn AND InvNo = '" + rs.getString("BoxNo") + "' and Closed ='N'";
                ResultSet rs2 = dbConnection.getResultSet(query2, objGlobal.getConnection());
                if (rs2.next()) {
                    String query3 = "Update ITWORK..tmpBoxInAndOut set Sn = '" + rs2.getInt("Sn") + "' where BoxNo = '" + rs.getString("BoxNo") + "' and deviceId = '"+objGlobal.getDeviceName()+"'";
                    if (!dbConnection.insertUpdate(query3, objGlobal.getConnection())) {
                        return null;
                    } else {
                        BoxInOutRequestData boxInOutRequestData = new BoxInOutRequestData(
                                "", String.valueOf(rs2.getInt("Sn")), "",
                                "", rs.getInt("TotalQty"), rs.getString("BoxNo"),rs.getString("toteid"), "N", palletNo, "", 0
                        );
                        boxInOutRequestDataList.add(boxInOutRequestData);
                    }

                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return boxInOutRequestDataList;
    }

    public boolean updateSn(String boxNo, int sn) {

        String query1 = "insert into usa..BoXPalletDel select *,'"+sn+"-Andr-PDA' from usa..BoxPallet where boxno='" + boxNo + "'";
        if(!dbConnection.insertUpdate(query1, objGlobal.getConnection())) return false;
        else {
            String query = "update bfldata..usapalletsdet set sn=285999 where invno='" + boxNo + "' and sn='" + sn + "'";
            if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) return false;
            else {
                String query2 = "delete from  usa..BoXPallet where boxno='" + boxNo + "'";
                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) return false;
                else  return true;
            }

        }
    }


    public boolean addChecking(String boxNo){
        boolean status = false;
        String query = "select sn from bfldata.dbo.USAPallets where closed='N' and sn in(select sn from bfldata.dbo.USAPalletsDet where InvNo='" + boxNo + "')";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()){
                status = true;
            }
            else  status = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public boolean saveAddedBoxToDB(String boxNo, String palletNo) {
        boolean status = true;
        String query = "select sn from bfldata.dbo.USAPallets where (closed='N' or sn = 285999) and sn in(select sn from bfldata.dbo.USAPalletsDet where InvNo='" + boxNo + "')";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                int eSn = rs.getInt("sn");
                String query1 = "insert into bfldata..usapalletsdet_bck (Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details) select Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details from bfldata.dbo.USAPalletsDet where InvNo='" + boxNo + "' and sn=" + eSn;
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection()))  status = false;
                else {
                    String query2 = "update bfldata..USAPalletsDet set sn=" + sn + " where invno='" + boxNo + "' and sn=" + eSn;
                    if (!dbConnection.insertUpdate(query2, objGlobal.getConnection()))  status = false;
                    else status = true;
                }
            }else {
                String query1 = "insert into bfldata..USAPalletsDet(Sn,InvNo,JobNo,ItemCategory,Qty,CountedBy,ItemType,Details,ToteID) select top 1 " + sn + ",boxno,'"+palletNo+"',left(Remarks,50),qty,PreparedBy,'',left(Remarks,100),toteid from usa.dbo.vUPCBoxDet where boxno='" + boxNo + "'";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    status = false;    }
                else status = true;
            }

            if(status) {
                String query3 = "insert into  bfldata..VerifyPalletDetail  (SNo, PalletNo, BoxNo, Build, Scan) select SNo, PalletNo, '" + boxNo + "',1,1 from BFLDATA..VerifyPalletHeader where PalletNo='" + palletNo + "'";
                status = dbConnection.insertUpdate(query3, objGlobal.getConnection());
                if (status) {
                    String query4 = "Delete from usa..BoXPallet where boxno ='" + boxNo + "'";
                    status = dbConnection.insertUpdate(query4, objGlobal.getConnection());
                    if (status) {
                        String query5 = "insert into usa..BoXPallet (boxno, palletno) values ('" + boxNo + "','" + palletNo + "')";
                        status = dbConnection.insertUpdate(query5, objGlobal.getConnection());
                        if (status) {
                            updatePallets(boxNo, palletNo, "ADD");
                        }
                    }
                }
                // saveNewPalletToDB(boxNo,palletNo,"ADD");
            }


//
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public boolean updatePallets(String boxNo, String palletNo, String actionText) {
        String cDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String cTime = new SimpleDateFormat("hh:mm:ss").format(new Date());

        boolean status = false;
        String query = "select PLTQty = sum(qty) from usa..vUPCBoxDet where Closed='N' and Palletno='" + palletNo.toUpperCase() + "'";
        ResultSet rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorNo("transferReceipt:007");
        }
        try {
            if (rs.next()) {
                int pltQty = rs.getInt("PLTQty");
                String query4 = "insert into bfldata..BoxInOutHistory (Typ,PalletNo,BoxNo,Trndate,Userid,TrnTime,PLTQty) values('" + actionText + "','" + palletNo + "','" + boxNo + "','"+objGlobal.getServerDate()+"','" + objGlobal.getUserId() + "','"+objGlobal.getServerTime()+"'," + pltQty + ")";
                status = dbConnection.insertUpdate(query4, objGlobal.getConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

}
