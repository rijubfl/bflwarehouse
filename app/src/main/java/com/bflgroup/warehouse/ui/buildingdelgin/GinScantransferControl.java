package com.bflgroup.warehouse.ui.buildingdelgin;

import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.setCount;
import static com.bflgroup.warehouse.ui.buildingdelgin.GinScanTransferGlobal.setPalletCount;
import static com.loopj.android.http.AsyncHttpClient.log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GinScantransferControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    private ArrayList<String> dataname;
    private ArrayList<String> CostCodeTo;
    Integer palletSN;
    String PalletNo = "";
    ResultSet rs1;
    Double getGinno;
    Integer Count = 0;
    Integer PalletCount = 0;
    GinScanTransferShared ObjGinScanTransferShared;

    public GinScantransferControl(Context context) {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("GinScantransferControl : Connection error");
        }
        ObjGinScanTransferShared=new GinScanTransferShared(context);
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("GinScantransferControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
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


    public String LoadShops(int route_id) {
        String shops = "";
        String shopInShop = "";
        dataname = new ArrayList<String>();
        CostCodeTo = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        try {
            rs = dbConnection.getResultSet("SELECT ShopName,shopInShop, DataName, CostCodeTo FROM bfldata.dbo.DataSettings where Routeid=" + route_id, objGlobal.getConnection());
            while (rs.next()) {
                shops += rs.getString("ShopName") + " | ";
                dataname.add(rs.getString("DataName").toString());
                CostCodeTo.add(rs.getString("CostCodeTo").toString());
                shopInShop = rs.getString("shopInShop").toString();
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


    public List<String> GetShops(Context context, String transferno, String palletno) {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            arr.add("Select the shopname");
            String query = "";
            for (int i = 0; i < dataname.size(); i++) {

                if (dataname.get(i) != dataname.get(dataname.size() - 1)) {
                    query += "select TrfNo, StoreIssue, (Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "')) as Shopname from " + dataname.get(i) + "..TransferHeader where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and CostCodeTo = '" + CostCodeTo.get(i) + "' and (trfType = 'R' or trfType = 'T') ) Union ";

                }else{
                    query += "select TrfNo, StoreIssue, (Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = " + dataname.get(i) + "..transferHeader.CostCodeTo and LocCodeTo = " + dataname.get(i) + "..transferHeader.LocCodeTo) and DataName = '" + dataname.get(i) + "')) as Shopname from " + dataname.get(i) + "..TransferHeader where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and CostCodeTo = '"+CostCodeTo.get(i)+"' and (trfType = 'R' or trfType = 'T') ) ";

                }
            }

            Log.e("Select shops",query);
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

    public ArrayList<GinScanItem> ScanTransfer(Context context, String transferno, int get_route_id, String android_id, String palletno) {
        ArrayList<GinScanItem> arr = new ArrayList<GinScanItem>();
        ArrayList<GinScanItem> arrayList = new ArrayList<>();
        ArrayList dataname = new ArrayList<>();
        ArrayList Shopname = new ArrayList<>();

        if (!checkConnection()) {
            return null;
        }

        try {

            String query = "";

            String[] arrOfStr = palletno.split("/");
            String query2 = "";
          try {
              query2 = "select Shopname, dataname from bfldata..datasettings where routeid = '"+get_route_id+"'";
              rs = dbConnection.getResultSet(query2, objGlobal.getConnection());
              while (rs.next()) {
                  dataname.add(rs.getString("dataname"));
                  Shopname.add(rs.getString("Shopname"));
              }



          }catch (Exception e){
              vibrate(1000, context);
              okMessage("ALET",e.toString(), context );
              //okMessage("ALET","", context );
              objGlobal.setErrorMessage("" + e.toString());
          }
          LoadGinData();


            try {

            if(!dataname.isEmpty()) {
                for (int i = 0; i < dataname.size(); i++) {

                    if (dataname.get(i) != dataname.get(dataname.size() - 1)) {
                        query += "select top 1 b.trfno, a.StoreIssue, ShopName,BoxNo, Qty, a.PreparedBy, a.Narration, a.TrfDate, b.actualshop  from " + dataname.get(i) + "..vTransferDetail a, vGoodsIssue b where (b.PalletNo = '" + palletno + "')  and (a.TrfNo = '" + transferno + "' or storeissue = '"+transferno+"')  and a.TrfNo = b.TrfNo Union ";

                    }
                    else{
                        query += "select top 1 b.trfno, a.StoreIssue, ShopName,BoxNo, Qty, a.PreparedBy, a.Narration, a.TrfDate, b.actualshop  from "+dataname.get(i)+"..vTransferDetail a, vGoodsIssue b where (b.PalletNo = '"+palletno+"') and (a.TrfNo = '" + transferno + "' or storeissue = '"+transferno+"')  and a.TrfNo = b.TrfNo";

                    }
                }

                //query = "select top 1 b.trfno, a.StoreIssue, ShopName,BoxNo, Qty, a.PreparedBy, a.Narration, a.TrfDate  from "+dataname+"..vTransferDetail a, vGoodsIssue b where (b.PalletNo = '"+palletno+"') and ShopName = '"+Shopname+"' and a.TrfNo = '"+transferno+"'  and a.TrfNo = b.TrfNo";
                Log.e("QueryLog", query);
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    arr = TmpGinScan(context, android_id, rs.getString("TrfNo"), rs.getString("TrfDate"), rs.getString("StoreIssue"), rs.getString("Actualshop"), String.valueOf(get_route_id), rs.getString("BoxNo"), rs.getString("PreparedBy"), rs.getString("Narration"), rs.getString("Qty"), palletno);

                } else {

                    String query1 = "select * from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "' order by scantime desc ";
                    ResultSet result = dbConnection.getResultSet(query1, objGlobal.getConnection());
                    while (result.next()) {
                        arr.add(new GinScanItem(result.getString("TrfNo"), result.getString("ToteId"), result.getString("ShopName"), result.getString("qty")));
                    }
                    vibrate(1000, context);
                    okMessage("", "This Transfer no " + transferno + " is not in the pallet - " + palletno, context);
                }
            }
            }catch (Exception e){
                okMessage("ALERT",e.toString(), context );
               // okMessage("ALERT",query, context );
                objGlobal.setErrorMessage("" + e.toString());
            }
            String query4 = "select * from BFLDATA..tmpGinScan where DeviceName = '" + objGlobal.getDeviceName() + "'";

            rs = dbConnection.getResultSet(query4, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new GinScanItem(rs.getString("TrfNo"), rs.getString("ToteId"), rs.getString("ShopName"), rs.getString("qty")));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public ArrayList<GinScanItem> ScanTransfer2(Context context, String transferno, int get_route_id, String android_id, String shopname2, String palletno) {
        ArrayList<GinScanItem> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<GinScanItem>();
            String query = "";

            String querynew = "select dataname, costcodeto, ShopName from bfldata..datasettings where shopname = '"+shopname2+"'";
            rs = dbConnection.getResultSet(querynew, objGlobal.getConnection());

            if(rs.next()) {
                query = "select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = a.CostCodeTo and LocCodeTo = a.LocCodeTo) and DataName = '" + rs.getString("dataname") + "' and RouteId = '" + get_route_id + "' )), '0'), (select SUM(Quantity) from " + rs.getString("dataname") + "..TransferDetail b where (b.trfno='" + transferno + "' or a.StoreIssue = '" + transferno + " and b.TrfNo = a.TrfNo') ) as Quantity  from " + rs.getString("dataname") + "..TransferHeader a  where ((trfno='" + transferno + "' or StoreIssue = '" + transferno + "') and (trfType = 'R' or trfType = 'T') and CostCodeTo = '" + rs.getString("costcodeto") + "' )";
               // select TrfNo, StoreIssue, CartonNo = isNUll(CartonNo, 0), PreparedBy, Narration, TrfDate, ShopName = isNull((Select ShopName from BFLDATA..DataSettings where ((CostCodeTo = a.CostCodeTo) and DataName = 'BFLDUBAI' and RouteId = '5' )), '0'), (select SUM(Quantity) from BFLDUBAI..TransferDetail b where (a.StoreIssue ='S019729' or b.trfno = 'S019729') and b.TrfNo = a.TrfNo ) as Quantity  from BFLDUBAI..TransferHeader a where ((trfno='S019729' or StoreIssue = 'S019729') and trfType = 'R' and CostCodeTo = '004' and trfDate >= DATEADD(day,-10,GETDATE()))

                Log.e("Select query", query);
                ResultSet rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs1.next()) {
                    String Shopname = rs1.getString("ShopName").toString();
                    if (Shopname == "0" || rs1.getString("ShopName").toString() == null || rs1.getString("ShopName").toString() == "")
                    {
                        vibrate(1000,context);
                        okMessage("", "This Trf No / Toteid is not in the same Route - " + transferno, context);

                    } else {

                        arr = TmpGinScan(context, android_id, rs1.getString("TrfNo"), rs1.getString("TrfDate"), rs1.getString("StoreIssue"), shopname2, String.valueOf(get_route_id), rs1.getString("CartonNo"), rs1.getString("PreparedBy"), rs1.getString("Narration"), rs1.getString("Quantity"), palletno);
                    }

                } else {
                    String query1 = "select * from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "' order by scantime desc";
                    ResultSet result = dbConnection.getResultSet(query1, objGlobal.getConnection());
                    while (result.next()) {
                        arr.add(new GinScanItem(result.getString("TrfNo"), result.getString("ToteId"), result.getString("ShopName"), result.getString("Qty")));
                    }
                    vibrate(1000,context);
                    okMessage("", "This Trf No / Toteid is not in the same Route - " + transferno, context);
                }

            }

            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public ArrayList<GinScanItem> TmpGinScan(Context context, String android_id, String TrfNo, String Trfdate, String ToteId, String ShopName, String Route_id, String BoxNo, String PreparedBy, String Narration, String Quantity, String palletNo) throws SQLException {
        ArrayList<GinScanItem> arrayList = new ArrayList<>();
        try {
            String query = "select * from BFLDATA..tmpGinRoute where (TrfNo='" + TrfNo + "' or ToteId = '" + TrfNo + "') and shopname = '"+ShopName+"' and DeviceName = '" + objGlobal.getDeviceName() + "'";
            String query2 = "select * from BFLDATA..vGoodsIssuePlt where TrfNo='" + TrfNo + "' and ShopIssue='" + ShopName + "'";

            Log.e("Query1", query);
            //Log.e("Query", query2);

            ResultSet rs1 = dbConnection.getResultSet(query2, objGlobal.getConnection());
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
            }

            if (rs.next()) {
                vibrate(1000,context);
                okMessage("", "Duplicate Trf No/Toteid - " + TrfNo   , context);
            } else {
                if (rs1.next()) {
                    vibrate(1000,context);
                    okMessage("", "Gin Already Built for Trf No/Toteid - " + TrfNo , context);
                } else {
                    query = "insert into BFLDATA..tmpGinRoute (DeviceName,TrfNo,ToteId,ShopName,RouteId,BoxNo,Qty,PreparedBy,Narration,TrfDate, Scantime, PalletScan)" +
                            "values ('" + objGlobal.getDeviceName() + "','" + TrfNo + "'" +
                            ",'" + ToteId + "','" + ShopName + "','" + Route_id + "', '" + BoxNo + "','" + Math.round(Float.parseFloat(Quantity)) + "', '" + PreparedBy + "', '" + Narration + "', '" + Trfdate + "', '"+objGlobal.getServerTime()+"', '"+palletNo+"')";
                    if (!dbConnection.insertUpdate(query, objGlobal.getConnection())) {
                        objGlobal.getConnection().rollback();

                    }
                    else{
                        // arrayList.add(new GinScanItem(TrfNo, ToteId, ShopName, Route_id));
                        Count = setCount(Count+1);
                        PalletCount = setPalletCount(PalletCount+1);
                        Log.e("PalletCount", PalletCount+"");

                        }
                }
            }

            String query3 = "select * from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "' order by scantime desc";

            rs = dbConnection.getResultSet(query3, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new GinScanItem(rs.getString("TrfNo"), rs.getString("ToteId"), rs.getString("ShopName"), rs.getString("qty")));
            }
        }catch(Exception e){
            Log.e("Get error",e.toString());
        }
        return arrayList;
    }

    public ArrayList<GinScanItem> LoadGinData() throws SQLException {
        ArrayList<GinScanItem> arrayList = new ArrayList<>();
        String query1 = "select * from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "' order by scantime desc";

        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        while (rs.next()) {
            arrayList.add(new GinScanItem(rs.getString("TrfNo"), rs.getString("ToteId"), rs.getString("ShopName"), rs.getString("qty")));
        }
        log.e("BFLdata gin", arrayList + "");
        return arrayList;
    }

    public Integer LoadPalletDataCount(String palletno) throws SQLException {
        //Integer Count = 0;
        String query1 = "select count(*) from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "' and PalletScan = '"+palletno+"' ";

        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        while (rs.next()) {
            PalletCount = rs.getInt(1);
        }
        log.e("BFLdata palletCount", PalletCount + "");
        log.e("BFLdata palletCount", query1);

        PalletCount = setPalletCount(PalletCount);
        return PalletCount;
    }
    public Integer LoadGinDataCount() throws SQLException {
        //Integer Count = 0;
        String query1 = "select count(*) from BFLDATA..tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "'";

        rs = dbConnection.getResultSet(query1, objGlobal.getConnection());
        while (rs.next()) {
            Count = rs.getInt(1);
        }
        log.e("BFLdata Count", Count + "");

        Count = setCount(Count);

        return Count;
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

    public boolean InsertPalletDetails(String DelDate, String remark, String Driver, String Carno, String Shipno, String Routeid) throws SQLException, ParseException {
        try {

            String pallet = "";
            String queryselect = "select distinct ShopName from bfldata..tmpGinRoute where devicename = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs2 = dbConnection.getResultSet(queryselect, objGlobal.getConnection());
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorNo("transferReceipt:007");
                return false;
            }
            while (rs2.next()) {
                pallet = GeneratePallet(rs2.getString("ShopName"));
                String query1 = "Update bfldata..tmpGinRoute set PalletNo  = '" + pallet + "' where ShopName = '" + rs2.getString("ShopName") + "'";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    Log.e("Error Query", query1);
                }


            }

            getGinno = GinScanTransferGlobal.setGinno(Double.valueOf(getGinno()));

            String query = "select * from bfldata..tmpGinRoute where devicename = '" + objGlobal.getDeviceName() + "'";
            rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());

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
            if(DelDate.equals("")){
                DelDate = objGlobal.getServerDate();
            }

            try {

                String query2 = "Insert into bfldata.dbo.GoodsIssueHead(Sn,DelDate,ShopName,EntryDate,Remarks,InCharge,UserId,PalletNo,PalletType,Issued, IssuedOn) Values(" + palletSN + ",'" + DelDate + "', '" + Shopname + "', '" + objGlobal.getServerDate() + "','" + remark + "', 'ANDRGIN - "+Routeid+"', " + objGlobal.getUserId() + ", '" + pallet + "', 'MIX', 'N', '" + objGlobal.getServerDate() + "')";
                Log.e("Insert GoodsIssueHead", query2);
                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                    Log.e("Error Query", query2);
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                } else {
                    try {
                        String query4 = "Insert Into bfldata.dbo.PltIssueDet values(" + getGinno + ", '" + pallet + "', '" + objGlobal.getServerDate() + "', '" + Shopname + "', 'MIX')";
                        Log.e("Insert PltIssueDet", query4);
                        if (!dbConnection.insertUpdate(query4, objGlobal.getConnection())) {
                            Log.e("Error Query", query4);
                            objGlobal.getConnection().rollback();
                            objGlobal.getConnection().setAutoCommit(true);
                            return false;

                        }
                    } catch (Exception e) {
                        Log.e("", "");
                    }
                }
            } catch (Exception e) {
                Log.e("Get exception", e.toString());
                objGlobal.setErrorMessage(e.toString());

            }
            palletSN = palletSN + 1;
            //getGinno =  getGinno + 1;

//        }


            String query2 = "Insert Into bfldata.dbo.PltIssueHead values(" + getGinno + ", '" + DelDate + "', '" + objGlobal.getServerDate() + "', '" + objGlobal.getServerTime() + "', 'ANDR - "+Routeid+"', '" + Driver + "','" + Carno + "','" + remark + "'," + objGlobal.getUserId() + ", '" + Shipno + "', '')";
            Log.e("Insert PltIssueHead", query2);
            if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                Log.e("Error Query", query2);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;

            }


        }catch (Exception e){
            Log.e("Error message",e.toString());
        }

        objGlobal.getConnection().commit();
        objGlobal.getConnection().setAutoCommit(true);
        deletetemp();
        return true;

    }

    public Boolean GinReminderDetails(int Ginno) throws SQLException {

    try {
    String query2 = "Insert Into bfldata.dbo.GinReminderDetails select " + Ginno + ",  DelDate, Shopissue,count(trfno),(select shopemail from bfldata..DataSettings where shopname=Shopissue),'', Null, ''  " +
            "  from BFLDATA..vGoodsIssuePlt where srno = " + Ginno + " group by ShopIssue,DelDate";
    if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
        Log.e("Error Query", query2);
        objGlobal.getConnection().rollback();
        objGlobal.getConnection().setAutoCommit(true);
        return false;
    }


//        String query = "select distinct Shopissue from BFLDATA..vGoodsIssuePlt where srno = "+Ginno;
//        rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());
//
//        objGlobal.getConnection().setAutoCommit(false);
//
//        String Shopname = "";
//        while (rs1.next()) {
//            Shopname = rs1.getString("Shopissue");
//            String query1 = "select count(TrfNo) from BFLDATA..vGoodsIssuePlt where ShopIssue = '" + Shopname+ "' and srno = "+Ginno;
//            ResultSet rs2 = dbConnection.getResultSet(query, objGlobal.getConnection());
//            while(rs2.next()) {
//                String query2 = "Insert Into bfldata.dbo.GinReminderDetails values('"+Ginno+"','','','')";
//            }
//        }


    }catch(Exception e){
        Log.e("Mail Sent",e.toString());
    }
        return true;
    }


    public Integer getGinno() {
        ResultSet resultSet;
        Integer Ginno = 0;
        String que = "select isnull(max(SrNo),0) + 1 as Ginno from bfldata.dbo.PltIssueHead";
        resultSet = dbConnection.getResultSet(que, objGlobal.getConnection());
        try {
            if (resultSet.next()) {
                Ginno = Integer.valueOf(resultSet.getString("Ginno"));
                Log.e("Add", Ginno + "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Ginno;
    }


    public void InsertGinDetails(Integer palletSN, String Date, String pallet, String Shopname) throws SQLException {
//        ResultSet resultSet;
//        Integer Ginno = 0;
//        String que = "select isnull(max(SrNo),0) + 1 as Ginno from bfldata.dbo.PltIssueHead";
//        resultSet = dbConnection.getResultSet(que, objGlobal.getConnection());
//        if (resultSet.next()) {
//            Ginno = Integer.valueOf(resultSet.getString("Ginno"));
//        }

        // getGinno = getGinno();


        try {
            String query1 = "Insert Into bfldata.dbo.PltIssueDet values(" + getGinno + ", '" + pallet + "', '" + Date + "', '" + Shopname + "', 'MIX')";
            if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
            }

        } catch (Exception e) {
            Log.e("Error message gin", e.toString());
        }


    }

    public int getpallet(Context context, String palletno, int routeid) throws SQLException {
        int getCountno = 0;
        ArrayList arrayList = new ArrayList();
        String query2 = "";
        try {
            query2 = "select PalletScan from bfldata..tmpGinRoute where PalletScan = '" + palletno + "' and DeviceName = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs2 = dbConnection.getResultSet(query2, objGlobal.getConnection());
            if (rs2.next()) {
                okMessage("MESSAGE","Duplicate Pallet", context );
                return 0;
            } else {

                String query = "select palletprefix from bfldata..datasettings where routeid = "+ routeid;
                ResultSet rs1 = dbConnection.getResultSet(query, objGlobal.getConnection());
                while (rs1.next()) {
                    arrayList.add(rs1.getString("palletprefix"));
                }
                String[] arrOfStr = palletno.split("/");
                Log.e("Error", query);
                if (arrayList.contains(arrOfStr[0])) {
                    String que = "select count(*) count from bfldata..GoodsIssueDet where Sn = (select Sn from bfldata..GoodsIssueHead where palletno = '" + palletno + "' )";
                    ResultSet rs5 = dbConnection.getResultSet(que, objGlobal.getConnection());

                    if (rs5.next()) {
                        getCountno = Integer.valueOf(rs5.getString("count"));
                        Log.e("getCountno", getCountno + "");
                        ObjGinScanTransferShared.savePalletno(palletno);
                        ObjGinScanTransferShared.savePalletCount(String.valueOf(getCountno));
                        ObjGinScanTransferShared.saveRouteid(String.valueOf(routeid));
                        PalletCount= 0;
                        setPalletCount(PalletCount);
                    }
                    else{
                        vibrate(1000, context);
                        okMessage("ALERT", "This PalletNo is not in the same route - " + palletno, context);
                        return 0;
                    }

                }
                else {
                    vibrate(1000, context);
                    okMessage("ALERT", "This PalletNo is not in the same route - " + palletno, context);
                    return 0;
                }
            }
            }catch(Exception e){
            vibrate(1000,context);
                okMessage("Alert", "Error message gin - " + e.toString(), context);
            }


    return getCountno;
    }

    public void AlertDialog(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpGinRoute where DeviceName = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }

        } catch (Exception ex) {
            objGlobal.setErrorMessage("tmpGinRoute:clearTable:" + ex.toString());
            return false;
        }
        Count = 0;
        PalletCount = 0;
        Count = setCount(Count);
        PalletCount = setPalletCount(PalletCount);
        return true;
    }
    public boolean deletePallettemp(String palletno) {
        if (!checkConnection()) {
            return false;
        }
        int getpallCountno = 0;
        String que = "select isnull(count(*), 0) count from bfldata.dbo.tmpGinRoute where palletScan = '"+palletno+"' and DeviceName = '" + objGlobal.getDeviceName() +"'";
        rs = dbConnection.getResultSet(que, objGlobal.getConnection());

        try {
            if (rs.next()) {
                getpallCountno = Integer.valueOf(rs.getString("count"));
                Log.e("getCountno", getpallCountno + "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpGinRoute where palletScan = '"+palletno+"' and DeviceName = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }

        } catch (Exception ex) {
            objGlobal.setErrorMessage("tmpGinRoute:clearTable:" + ex.toString());
            return false;
        }

        Count = Count - getpallCountno;
        PalletCount = 0;
        Count = setCount(Count);
        PalletCount = setPalletCount(PalletCount);
        return true;
    }

    void vibrate(int duration, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Uri notification = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.errorsound);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        audioManager.setStreamVolume(AudioManager.STREAM_RING,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);
        r.play();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }


    }
}
