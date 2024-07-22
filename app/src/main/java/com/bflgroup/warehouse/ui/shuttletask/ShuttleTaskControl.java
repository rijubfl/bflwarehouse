package com.bflgroup.warehouse.ui.shuttletask;

import android.content.Context;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.sql.ResultSet;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ShuttleTaskControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

    private boolean b_Result;
    private boolean apiResult;
    private ResultSet rs;

    public ShuttleTaskControl() {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("ShuttleTaskControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("ShuttleTaskControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateToteId(String toteid, String shuttleType) {
        if (!checkConnection()) {
            return false;
        }
        if (toteid.isEmpty()) {
            objGlobal.setErrorMessage("Please Scan Tote.");
            return false;
        }
        try {
            String boxno = "", sts = "";
            rs = dbConnection.getResultSet("select top 1 BoxNo from usa.dbo.UPCBoxHead where Closed='N' and ToteID='" + toteid + "' order by trndate desc", objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet("select top 1 a.BoxNo from bfldata.dbo.TcmboxesHeader a,bfldata.dbo.TCMBoxes b where b.Closed='N' and a.Boxno=b.BoxNo and a.TotId='" + toteid + "' order by a.trndate desc", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("ShuttleTaskControl.validateToteId1 : Invalid Tote or Box is closed");
                    return false;
                }
            }
            boxno = rs.getString("BoxNo");
            if (boxno.isEmpty()) {
                objGlobal.setErrorMessage("ShuttleTaskControl.validateToteId2 : Invalid Tote or Box is closed");
                return false;
            }
            rs = dbConnection.getResultSet("select * from racks.dbo.BinRack where ToteId='" + toteid + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (sts.isEmpty())
                    sts = "Location, WH: " + rs.getString("warehouse") + ", " + rs.getString("Location");
                else
                    sts = sts + ", Location, WH: " + rs.getString("warehouse") + ", " + rs.getString("Location");
            }
            rs = dbConnection.getResultSet("select * from tempdata.dbo.SIMProdReadyPalletsList where BoxNo='" + boxno + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (sts.isEmpty())
                    sts = "Found in SIM List";
                else
                    sts = sts + ", Found in SIM List";
            }
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpMoveShuttleToteScan where DeviceName='" + objGlobal.getDeviceName() + "' and ToteId='" + toteid + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into bfldata.dbo.tmpMoveShuttleToteScan values ('" + objGlobal.getDeviceName() + "','" + toteid + "','" + boxno + "','" + shuttleType + "',getdate(),'" + sts + "')", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.validateToteId:" + ex);
            return false;
        }
    }

    public boolean deleteSelected(String toteid) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpMoveShuttleToteScan where DeviceName='" + objGlobal.getDeviceName() + "' and ToteId='" + toteid + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.deleteSelected:" + ex.toString());
            return false;
        }
    }

    ArrayList<ShuttleTaskScanTicket> loadShuttleTaskScanTicket() {
        if (!checkConnection()) {
            return null;
        }
        int tCount = 0;
        ArrayList<ShuttleTaskScanTicket> listShuttleTaskScanTicket = new ArrayList<ShuttleTaskScanTicket>();
        try {
            listShuttleTaskScanTicket.clear();
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpMoveShuttleToteScan where DeviceName='" + objGlobal.getDeviceName() + "' order by ScanTime desc", objGlobal.getConnection());
            while (rs.next()) {
                listShuttleTaskScanTicket.add(new ShuttleTaskScanTicket(rs.getString("ToteId"),
                        rs.getString("BoxNo") + " | " + rs.getString("ShuttleType") + " | " + rs.getString("detail")));
                tCount++;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.loadShuttleTaskScanTicket:" + ex.toString());
            return null;
        }
        return listShuttleTaskScanTicket;
    }

    public boolean clearAll() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from bfldata.dbo.tmpMoveShuttleToteScan where DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.validateToteId:" + ex.toString());
            return false;
        }
    }

    public boolean saveShuttleSystem() {
        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            ResultSet rsDet;
            String requestId = "";
            int dTaskIdNum = 0;
            rs = dbConnection.getResultSet("select * from bfldata.dbo.tmpMoveShuttleToteScan where ShuttleType='Inbound' and DeviceName='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                rsDet = dbConnection.getResultSet("select top 1 TaskIdNum from (select ISNULL(max(RequestIdNum),0)+1 as TaskIdNum from BLACKBOX.dbo.WCS_InboundDataRequest union " +
                        "select ISNULL(max(TaskIdNum),0)+1 as TaskIdNum from BLACKBOX.dbo.WCS_OutboundTaskRequest) A order by TaskIdNum desc", objGlobal.getConnection());
                if (rsDet.next()) {
                    dTaskIdNum = rsDet.getInt("TaskIdNum");
                }
                requestId = objGlobal.getServerDate().replace("/", "") + String.format("%08d", dTaskIdNum);
                if (rs.getString("ShuttleType").equals("Inbound")) {
                    if (!dbConnection.insertUpdate("insert into blackbox.dbo.WCS_InboundDataRequest(RequestId,TaskNo,RequestIdNum,TaskType,ContCode,Priority,RequestTime,Sku,Qty,lot01,lot02) values " +
                            "('" + requestId + "','T" + requestId + "'," + dTaskIdNum + ",'1','" + rs.getString("ToteId") + "'," +
                            "'1',FORMAT(getdate(),'dd/MM/yyyy HH:mm:ss'),'" + rs.getString("boxno") + "',1,'1','')", objGlobal.getConnection())) {
                        return false;
                    }
                }
                if (rs.getString("ShuttleType").equals("Outbound")) {

                }
                if (!dbConnection.insertUpdate("insert into blackbox.dbo.MoveShuttleManual(ToteId,BoxNo,TrnDate,TrnTime,ReqType,RequestId,TaskNo,RequestIdNum,IsProcessed) values " +
                        "('" + rs.getString("ToteId") + "','" + rs.getString("boxno") + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," +
                        "'" + rs.getString("ShuttleType") + "','" + requestId + "','T" + requestId + "','" + dTaskIdNum + "','N')", objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.saveShuttleSystem:" + ex.toString());
            return false;
        }
    }

    public boolean validateTotes() {
        if (!checkConnection()) {
            return false;
        }
        try {
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShuttleTaskControl.validateTotes:" + ex.toString());
            return false;
        }
    }

    public boolean postApi(Context context, String reqId, String taskNo, String taskType, String containerCode, String priority, String requestTime, String sku, int qty, int lot01, String lot02) {
        apiResult = false;
        try {
            final AsyncHttpClient client = new AsyncHttpClient();
            String json = "{ 'reqId': '" + reqId + "', 'taskNo': '" + taskNo + "', 'taskType': '" + taskType + "', 'containerCode': '" + containerCode + "', 'priority': '" + priority + "', " +
                    "'requestTime': '" + requestTime + "', 'taskDetails': [{ 'sku': '" + sku + "', 'qty': " + qty + ", 'lot01': " + lot01 + ", 'lot02': '" + lot02 + "' }]}";
            StringEntity entity = new StringEntity(json.replace("'", "\""), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, objGlobal.getBlackBoxApiServerIP() + "ent/integration/wcs/InBoundTaskController/", entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            objGlobal.setErrorMessage("statusCode: "+statusCode+", headers: "+headers+", responseBody: "+responseBody);
                            apiResult = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            objGlobal.setErrorMessage("ShuttleTaskControl:postApi:onFailure: Status: " + statusCode + ", responseBody: " + responseBody);
                            apiResult = false;
                        }
                    });
        } catch (Exception e) {
            objGlobal.setErrorMessage("ShuttleTaskControl:postApi:catch: " + e);
            apiResult = false;
        }
        return apiResult;
    }

}
