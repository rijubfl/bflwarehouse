package com.bflgroup.warehouse.ui.buildingdelpallet;

import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.setPalletNo;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.setPalletSn;
import static com.bflgroup.warehouse.ui.buildingdelpallet.BuildingDeliveryPalletGlobal.setPltCount;
import static com.loopj.android.http.AsyncHttpClient.log;

import android.content.Context;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.buildingdelpallet.models.ShopInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BuildingDeliveryPalletControl {


    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    private ArrayList<String> dataname;
    private ArrayList<String> CostCodeTo;
    Integer palletSN;
    String PalletNo = "";
    Integer Count = 0;


    public BuildingDeliveryPalletControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PltScantransferControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("PltScantransferControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }
    public List<String> loadShopsFromRoute(int routeId){
        List<String> shopNames = new ArrayList<>();
        try{
            rs = dbConnection.getResultSet("select ShopName from bfldata.dbo.datasettings where routeid = '"+routeId+"'", objGlobal.getConnection());
            while (rs.next()) {
                shopNames.add(rs.getString("ShopName"));
            }
            return shopNames;
        }
        catch (Exception e){
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }
    public List<ShopInfo> loadShopsFromTransfers(String trfNo){
        List<ShopInfo> shopNames = new ArrayList<>();
        try{
            if (objGlobal.getWarehouseCountry().equals("UAE")) {
                rs = dbConnection.getResultSet("select ShopName,Dataname from bfldata.dbo.TransferNoReturn where trfno = '" + trfNo + "'", objGlobal.getConnection());
                while (rs.next()) {
                    shopNames.add(new ShopInfo(
                            rs.getString("ShopName"),
                            rs.getString("Dataname")
                    ));
                }
            }
            return shopNames;
        }
        catch (Exception e){
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public List<Integer> loadRoute() {
        List<Integer> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<Integer>();
            arr.add(0);
            rs = dbConnection.getResultSet("SELECT Distinct RouteId FROM bfldata.dbo.DataSettings where isNull(RouteId, '')<>''", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getInt("RouteId"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }
    public Integer loadKsaRoute(String shopName) {
        int arr = 0;
        if (!checkConnection()) {
            return null;
        }
        try {
            if (shopName.startsWith("MUY")) {
                 shopName = shopName.substring(3, shopName.length());
            }
            rs = dbConnection.getResultSet("select distinct RouteId from bfldata.dbo.DataSettings where ShopName = '"+shopName+"'", objGlobal.getConnection());
            while (rs.next()) {
                arr = rs.getInt("RouteId");
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }
    public List<String> loadKsaShops() {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            arr.add("0");
            rs = dbConnection.getResultSet("select distinct ShopName,RouteId from bfldata.dbo.DataSettings where CountryCode = 'KSA'", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("ShopName")+" ( "+rs.getInt("RouteId")+" )");
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public Integer transferCount(String trfno){
        rs = dbConnection.getResultSet("select count(*) as trfCount from bfldata..transfernoreturn where trfno = '"+trfno+"'",objGlobal.getConnection());
        try {
            if (rs.next()){
                return rs.getInt("trfCount");
            }
            else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String LoadShops(int route_id) {
        String shops = "";
        String shopInShop = "";
        dataname = new ArrayList<String>();
        CostCodeTo = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        try {
            rs = dbConnection.getResultSet("SELECT ShopName, DataName, shopInShop, CostCodeTo FROM bfldata.dbo.DataSettings where Routeid=" + route_id, objGlobal.getConnection());
            while (rs.next()) {
                shops += rs.getString("ShopName") + " | ";
                dataname.add(rs.getString("DataName").toString());
                CostCodeTo.add(rs.getString("CostCodeTo").toString());
                shopInShop = rs.getString("shopInShop");

                if (shopInShop.equals("Y")) {

                    ResultSet  rs1 = dbConnection.getResultSet("select * from bfldata.dbo.datasettings where ShopName in(select mainshop from bfldata.dbo.shopinshop " +
                            "where subshop = '" + rs.getString("ShopName") + "')", objGlobal.getConnection());
                    if (rs1.next()) {

                        //shops += rs1.getString("ShopName") + " | ";
                        dataname.add(rs1.getString("dataName"));
                        CostCodeTo.add(rs1.getString("costCodeTo"));
//                        costCodeFrom = rs.getString("costCodeTo");
//                        locCodeTo = rs.getString("locCodeTo");
                    }
                }
            }

            return shops;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public List<String> GetShops(Context context, String transferno, int Routeid) {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            arr.add("Select the shopname");
            String query = "";
            for (int i = 0; i < dataname.size(); i++) {
                query += "select TrfNo, StoreIssue, (Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "')) as Shopname from " + dataname.get(i) + "..TransferHeader where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "')  and CostCodeTo = '"+CostCodeTo.get(i)+"' and (trfType = 'R' or trftype = 'T') ) Union ";
                if (dataname.get(i) == dataname.get(dataname.size() - 1)) {
                    query += "select TrfNo, StoreIssue, (Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "')) as Shopname from " + dataname.get(i) + "..TransferHeader where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "')  and CostCodeTo = '"+CostCodeTo.get(i)+"' and (trfType = 'R' or trftype = 'T') ) ";
                }
            }
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("Shopname"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public ArrayList<PalletScanDeliveryItem> ScanTransfer(Context context, String transferno, int get_route_id, String android_id,ShopInfo selectedShopName) {
        ArrayList<PalletScanDeliveryItem> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<PalletScanDeliveryItem>();
            String query = "select TrfNo,StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + selectedShopName.getDataName() + "..transferHeader.CostCodeTo and LocCodeTo = " + selectedShopName.getDataName() + "..transferHeader.LocCodeTo) and DataName = '" + selectedShopName.getDataName() + "' )), 0), (select SUM(Quantity) from " + selectedShopName.getDataName() + "..TransferDetail with(NOLOCK) where TrfNo ='" + transferno + "' ) as Quantity from " + selectedShopName.getDataName() + "..TransferHeader with(NOLOCK) where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and (trfType = 'R' or trftype = 'T')) ";

//            for (int i = 0; i < dataname.size(); i++) {
//                query += "select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings  where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "' )), 0), (select SUM(Quantity) from " + dataname.get(i) + "..TransferDetail with(NOLOCK) where TrfNo ='" + transferno + "' ) as Quantity  from " + dataname.get(i) + "..TransferHeader with(NOLOCK) where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and (trfType = 'R' or trftype = 'T') and CostCodeTo = '" + CostCodeTo.get(i) + "' ) Union ";
//
//                if (dataname.get(i) == dataname.get(dataname.size() - 1)) {
//                    query += "select TrfNo,StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "' )), 0), (select SUM(Quantity) from " + dataname.get(i) + "..TransferDetail with(NOLOCK) where TrfNo ='" + transferno + "' ) as Quantity from " + dataname.get(i) + "..TransferHeader with(NOLOCK) where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and (trfType = 'R' or trftype = 'T') and CostCodeTo = '" + CostCodeTo.get(i) + "' ) ";
//                }
//            }

            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs.next()) {
                String Shopname = rs.getString("ShopName").toString();
                if (Shopname == "0" || rs.getString("ShopName").toString() == null || rs.getString("ShopName").toString() == "") {
                    okMessage("", "This Trf No / Toteid is not in the same Route - "+transferno, context);

                } else {
                    String shopname = rs.getString("ShopName");
                    if (dataname.get(0).equals("BFLOMAN")) {
                        String query7 = "select * from bfldata..TransferNoReturn where TrfNo='" + rs.getString("TrfNo") + "' and dataname = 'BFLOMAN' and shopname in (select shopname from bfldata..datasettings where routeid = '" + get_route_id + "')";
                        ResultSet rs1 = dbConnection.getResultSet(query7, objGlobal.getConnection());
                        if (rs1.next()) {
                            shopname = rs1.getString("Shopname");
                            arr = TmpGinScan(context, android_id, rs.getString("TrfNo"), rs.getString("TrfDate"), rs.getString("StoreIssue"), selectedShopName.getShopName(), String.valueOf(get_route_id), rs.getString("CartonNo"), rs.getString("PreparedBy"), rs.getString("Narration"), rs.getString("Quantity"), selectedShopName.getShopName());
                        }else{
                            okMessage("", "This Trf No / Toteid is not in the same Route - " + transferno, context);
                        }
                    } else {
                        arr = TmpGinScan(context, android_id, rs.getString("TrfNo"), rs.getString("TrfDate"), rs.getString("StoreIssue"), selectedShopName.getShopName(), String.valueOf(get_route_id), rs.getString("CartonNo"), rs.getString("PreparedBy"), rs.getString("Narration"), rs.getString("Quantity"),selectedShopName.getShopName());
                    }
                }

            }
            else
            {
                okMessage("", "This Trf No / Toteid is not in the same Route - " + transferno, context);
            }

            String query1 = "select * from BFLDATA..tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'";
            ResultSet result = dbConnection.getResultSet(query1, objGlobal.getConnection());
            while (result.next()) {
                arr.add(new PalletScanDeliveryItem(result.getString("TrfNo"), result.getString("ToteId"), result.getString("ShopName"), result.getString("Qty"), result.getInt("RouteId")));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            okMessage("Alert", e.toString(), context);
            return null;
        }
    }



    public ArrayList<PalletScanDeliveryItem> ScanTransfer2(Context context, String transferno, int get_route_id, String android_id, String shopname2) {
        ArrayList<PalletScanDeliveryItem> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<PalletScanDeliveryItem>();
            String query = "";

            String querynew = "select dataname, costcodeto, ShopName from bfldata..datasettings where shopname = '"+shopname2+"'";
            rs = dbConnection.getResultSet(querynew, objGlobal.getConnection());

            if(rs.next()) {
                query = "select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + rs.getString("dataname") + "..transferHeader.CostCodeTo and LocCodeTo = " + rs.getString("dataname") + "..transferHeader.LocCodeTo) and DataName = '" + rs.getString("dataname") + "' and RouteId = '" + get_route_id + "' )), '0'), (select SUM(Quantity) from " + rs.getString("dataname") + "..TransferDetail with(NOLOCK) where TrfNo ='" + transferno + "' ) as Quantity  from " + rs.getString("dataname") + "..TransferHeader  with(NOLOCK) where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and (trfType = 'R' or trftype = 'T') and CostCodeTo = '" + rs.getString("costcodeto") + "')";
                // and trfDate >= DATEADD(day,-60,GETDATE()))";


//            for (int i = 0; i < dataname.size(); i++) {
//                query += "select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "' and RouteId = '" + get_route_id + "' )), '0'), (select SUM(Quantity) from " + dataname.get(i) + "..TransferDetail where TrfNo ='" + transferno + "' ) as Quantity  from " + dataname.get(i) + "..TransferHeader  where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and trfType = 'R' and CostCodeTo = '" + CostCodeTo.get(i) + "' and trfDate >= DATEADD(day,-10,GETDATE())) Union ";
//
//                if (dataname.get(i) == dataname.get(dataname.size() - 1)) {
//                    query += "select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "' and RouteId = '" + get_route_id + "' )), '0'), (select SUM(Quantity) from " + dataname.get(i) + "..TransferDetail where TrfNo ='" + transferno + "' ) as Quantity from " + dataname.get(i) + "..TransferHeader where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and trfType = 'R' and CostCodeTo = '" + CostCodeTo.get(i) + "' and trfDate >= DATEADD(day,-10,GETDATE())) ";
//                }
//            }
                Log.e("Select query", query);
                ResultSet rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs1.next()) {
                    String Shopname = rs1.getString("ShopName").toString();
                    if (Shopname == "0" || rs1.getString("ShopName").toString() == null || rs1.getString("ShopName").toString() == "") {
                        okMessage("", "This Trf No / Toteid is not in the same Route - "+transferno, context);

                    } else {

                        arr = TmpGinScan(context, android_id, rs1.getString("TrfNo"), rs1.getString("TrfDate"), rs1.getString("StoreIssue"), shopname2, String.valueOf(get_route_id), rs1.getString("CartonNo"), rs1.getString("PreparedBy"), rs1.getString("Narration"), rs1.getString("Quantity"),shopname2);
                    }

                } else {
                    String query1 = "select * from BFLDATA..tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'";
                    ResultSet result = dbConnection.getResultSet(query1, objGlobal.getConnection());
                    while (result.next()) {
                        arr.add(new PalletScanDeliveryItem(result.getString("TrfNo"), result.getString("ToteId"), result.getString("ShopName"), result.getString("Qty"),result.getInt("RouteId")));
                    }
                    okMessage("", "This Trf No / Toteid is not in the same Route - "+transferno, context);
                }

            }

            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }


    public ArrayList<PalletScanDeliveryItem> TmpGinScan(Context context, String android_id, String TrfNo, String Trfdate, String ToteId, String ShopName, String Route_id, String BoxNo, String PreparedBy, String Narration, String Quantity, String selectedShop) throws SQLException {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<PalletScanDeliveryItem> arrayList = new ArrayList<>();
        String query = "select * from BFLdata..tmpPalletScan where (TrfNo='" + TrfNo + "' or ToteId = '" + TrfNo + "') and shopname = '"+selectedShop+"' and DeviceName = '" + objGlobal.getDeviceName() + "'";
        String query2 = "select * from BFLDATA..vGoodsIssuePlt  with(NOLOCK) where TrfNo='" + TrfNo + "' and ShopIssue='" + ShopName + "'";
        String query3 = "select * from BFLDATA..vGoodsIssue with(NOLOCK) where TrfNo='" + TrfNo + "' and Actualshop='" + ShopName + "'";

        Log.e("Query", query);
        Log.e("Query", query2);
        // Log.e("Query", query3);
        ResultSet rs1 = dbConnection.getResultSet(query2, objGlobal.getConnection());
        ResultSet rs2= dbConnection.getResultSet(query3, objGlobal.getConnection());
        rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (rs.next()) {
            okMessage("", "Duplicate Trf No/Toteid - "+TrfNo, context);
        }
        else {
            if (rs2.next()) {
                okMessage("", "Pallet Already Built for Trf No/Toteid - " +TrfNo + " , Pallet - " +rs2.getString("palletno"), context);
            }
            else {
                if (rs1.next()) {
                    okMessage("", "Gin Already Built for Trf No/Toteid - " +TrfNo + " , Ginno - " +rs1.getString("srno"), context);
                }
                else {
                    query = "insert into BFLDATA..tmpPalletScan values ('" + objGlobal.getDeviceName() + "','" + TrfNo + "'" +
                            ",'" + ToteId + "','" + ShopName + "','" + Route_id + "', '" + BoxNo + "','" + Math.round(Float.parseFloat(Quantity)) + "', '" + PreparedBy + "', '" + Narration + "', '" + Trfdate + "', '')";
                    if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();
                    }
                    else{
                        Count = setPltCount(Count+1);
                    }
                }
            }
        }
//        String query1 = "select * from BFLDATA..tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'";
//        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
//        while (rs.next()) {
//            arrayList.add(new PalletScanDeliveryItem(rs.getString("TrfNo"), rs.getString("ToteId"), rs.getString("ShopName"), rs.getString("Qty")));
//        }
        return arrayList;
    }

    public ArrayList<PalletScanDeliveryItem> LoadPltData(){
        ArrayList<PalletScanDeliveryItem> arrayList = new ArrayList<>();
        try{
            String query1 = "select * from BFLDATA..tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'";

            rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new PalletScanDeliveryItem(rs.getString("TrfNo"), rs.getString("ToteId"), rs.getString("ShopName"), rs.getString("Qty"), rs.getInt("RouteId")));
            }
            log.e("BFLdata Pallet", arrayList + "");
            return arrayList;
        }
        catch (Exception e){
            return null;
        }

    }



    public boolean InsertPalletDetails(String remark, String routeid) throws SQLException, ParseException {
        Log.e("routeid", "ANDRPLT - " + routeid);

        try {
            String pallet = "";
            String queryselect = "select distinct ShopName from bfldata..tmpPalletScan where devicename = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs2 = dbConnection.getResultSet(queryselect, objGlobal.getConnection());
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
                return false;
            }
            while (rs2.next()) {
                pallet = GeneratePallet(rs2.getString("ShopName"));

                String query1 = "Update bfldata..tmpPalletScan set PalletNo  = '" + pallet + "' where ShopName = '" + rs2.getString("ShopName") + "'";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    Log.e("Error Query", query1);
                }

                Log.e("PalletSn", palletSN + "");

            }
            setPalletSn(palletSN.toString());
            pallet = setPalletNo(pallet);
            Log.e("PalletNo", pallet + "");

            String query = "select * from bfldata..tmpPalletScan where devicename = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());

            objGlobal.getConnection().setAutoCommit(false);

            String Shopname = "";
            while (rs1.next()) {
                Shopname = rs1.getString("ShopName");

                String boxno = "0";

                if (rs1.getString("BoxNo") == "" || rs1.getString("BoxNo") == null) {
                    boxno = "0";
                } else {
                    boxno = rs1.getString("BoxNo");
                }


                SimpleDateFormat cDateF = new SimpleDateFormat("dd/MM/yyyy");
                String cDate = cDateF.format(rs1.getDate("Trfdate"));
                Log.e("dateTime", cDate + "");
                String querynew = "insert into bfldata.dbo.GoodsIssueDet  values (" + palletSN + ",'" + rs1.getString("TrfNo") + "'" +
                        ",'" + cDate + "'," + boxno + "," + rs1.getInt("Qty") + ", '" + rs1.getString("PreparedBy") + "','" + rs1.getString("Narration") + "', '" + rs1.getString("shopname") + "')";
                Log.e("Insert goodsIssuedet", querynew);
                if (!dbConnection.insertUpdate(querynew, objGlobal.getConnection())) {
                    Log.e("Error Query", querynew);
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            String query2 = "Insert into bfldata.dbo.GoodsIssueHead (Sn,DelDate,ShopName,EntryDate,Remarks,InCharge,UserId,PalletNo,PalletType,Issued) Values(" + palletSN + ",'" + objGlobal.getServerDate() + "', '" + Shopname + "', '" + objGlobal.getServerDate() + "','" + remark + "', 'ANDRPLT - "+ objGlobal.getUserName() + " - " +routeid+"', " + objGlobal.getUserId() + ", '" + pallet + "', 'MIX', 'N')";
            Log.e("Insert GoodsIssueHead", query2);
            if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                Log.e("Error Query", query2);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            } else {

            }

        }catch(Exception e){
            Log.e("Error message", e.toString());
        }


        objGlobal.getConnection().commit();
        objGlobal.getConnection().setAutoCommit(true);
        deletetemp();
        return true;

    }


    public String GeneratePallet(String ShopName) throws SQLException {
        ResultSet rs2;
        PalletNo = "";
        Integer Pallet = 0;
        String PalletPrefix = "";
        palletSN = 0;
        String Q1 = "select isnull(max(sn),0) + 1 as palletSN from bfldata.dbo.GoodsIssueHead";
        rs2 = dbConnection.getResultSet(Q1, objGlobal.getConnection());
        if (rs2.next()) {
            palletSN = rs2.getInt("palletSN");
        }
        String query = "select PalletPrefix from BFLDATA..DataSettings where  shopname='" + ShopName + "'";
        rs2 = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (rs2.next()) {
            PalletPrefix = rs2.getString("PalletPrefix");
        }
        String query1 = "select replace(palletno, '" + PalletPrefix + "/', '') as palletno from bfldata.dbo.GoodsIssueHead  where  (PalletNo like '" + PalletPrefix + "%') order by sn desc";
        rs2 = dbConnection.getResultSet(query1, objGlobal.getConnection());
        if (rs2.next()) {
            Pallet = rs2.getInt("palletno");
        }
        Pallet = Pallet + 1;
        PalletNo = PalletPrefix + "/" + Pallet;
        return PalletNo;

    }

    void okMessage(String title, String message, Context context) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public boolean deletetemp() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("tmpPalletScan:clearTable:" + ex.toString());
            return false;
        }
        Count = 0;
        Count = setPltCount(Count);
        return true;
    }

    public Integer LoadPltDataCount() throws SQLException {
        //Integer Count = 0;
        String query1 = "select count(*) from BFLDATA..tmpPalletScan where DeviceName = '" + objGlobal.getDeviceName() + "'";

        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        while (rs.next()) {
            Count = rs.getInt(1);
        }
        log.e("BFLdata Count", Count + "");

        Count = setPltCount(Count);
        return Count;
    }

    public Integer LoadPltDataCount(String palletno) throws SQLException {
        //Integer Count = 0;
        String query1 = "select count(*) from BFLDATA..vGoodsIssue where palletno = '"+palletno+"'";

        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        while (rs.next()) {
            Count = rs.getInt(1);
        }
        log.e("BFLdata Count", Count + "");

        return Count;
    }

}
