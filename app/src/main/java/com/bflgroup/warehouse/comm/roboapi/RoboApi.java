package com.bflgroup.warehouse.comm.roboapi;

import android.content.Context;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.ui.chutestatusinout.techno.ChuteCheckInCheckOutControl;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class RoboApi {

    private Global objGlobal = Global.getInstance();

    private ChuteCheckInCheckOutControl objChuteCheckInCheckOutControl = new ChuteCheckInCheckOutControl();

    public void postChuteStatus(Context context, String chuteid,String strStatus, boolean blStatus, RoboApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ChuteId", chuteid);
            if (objGlobal.getWorkLocation().equals("UAE")) {
                jsonObject.put("status", strStatus);
            } else {
                jsonObject.put("status", blStatus);
            }
            final AsyncHttpClient client = new AsyncHttpClient();
            if (!objGlobal.getRoboChuteStatusAPIToken().isEmpty())
                client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                if (objGlobal.getWorkLocation().equals("UAE")) {
                                    callback.onSucess(statusCode);
                                } else {
                                    try {
                                        JSONObject jso = new JSONObject(new String(responseBody));
                                        boolean status = jso.getBoolean("status");
                                        String msg = jso.getString("message");
                                        if (status)
                                            callback.onSucess(statusCode);
                                        else
                                            callback.onFailure("postChuteStatus - 6.1 Parse error: " + msg);
                                    } catch (Exception e) {
                                        callback.onFailure("postChuteStatus - 6 Parse error: " + e.getMessage());
                                    }
                                }
                            } else {
                                callback.onFailure("postChuteStatus - 6.1Parse error: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("postChuteStatus - 7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("postChuteStatus - 8 Exception: " + e.getMessage());
        }
    }

    public void postLabelInfo(Context context,String chuteid,String shopid,String shopname,String toteId,String trfno,String messageid, String labelinfo, RoboApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("toteid", toteId);
            if (!messageid.isEmpty())  jsonObject.put("messageId", trfno);
            jsonObject.put("labelInfo", labelinfo);
            jsonObject.put("spare1", "");
            jsonObject.put("spare2", "");
            jsonObject.put("createtime", objGlobal.getServerDate());
            final AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            objChuteCheckInCheckOutControl.updateChuteApiLog("Label Info (1)", shopid, trfno, chuteid,labelinfo, toteId, shopname, chuteid, "", "Start");
            client.post(context, objGlobal.getRoboLabelInfoAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200) {
                                objChuteCheckInCheckOutControl.updateChuteApiLog("Label Info (2)", shopid, trfno, chuteid,labelinfo, toteId, shopname, chuteid, String.valueOf(statusCode), "onSuccess-"+responseBody.toString());
                                callback.onSucess(statusCode);
                            } else{
                                objChuteCheckInCheckOutControl.updateChuteApiLog("Label Info (3)", shopid, trfno, chuteid,labelinfo, toteId, shopname, chuteid, String.valueOf(statusCode), "onSuccess-Status is not 200-"+responseBody.toString());
                                callback.onFailure("postLabelInfo - 7.1 Request failed: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            objChuteCheckInCheckOutControl.updateChuteApiLog("Label Info (4)", shopid, trfno, chuteid,labelinfo, toteId, shopname, chuteid, String.valueOf(statusCode), "onFailure-"+responseBody.toString());
                            callback.onFailure("postLabelInfo - 7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            objChuteCheckInCheckOutControl.updateChuteApiLog("Label Info (5)", shopid, trfno, chuteid,labelinfo, toteId, shopname, chuteid, "", "Exception-"+e);
            callback.onFailure("postLabelInfo - 8 Exception: " + e.getMessage());
        }
    }

    public void postSortTask(Context context,String chuteid,String shopid,String shopname, String toteid,String chuteno,String trfno, String messageid,String batchcode, RoboApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("toteid", toteid);
            jsonObject.put("chuteno", chuteno);
            jsonObject.put("transferno", trfno);
            if (!messageid.isEmpty()) jsonObject.put("messageId", trfno);
            jsonObject.put("batchCode", batchcode);
            jsonObject.put("spare1", "");
            jsonObject.put("spare2", "");
            jsonObject.put("createtime", objGlobal.getServerDate());
            final AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            objChuteCheckInCheckOutControl.updateChuteApiLog("Sort Task (1)", shopid, trfno, chuteno,"", toteid, shopname, chuteid, "", "Start");
            client.post(context, objGlobal.getRoboSortTaskAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            objChuteCheckInCheckOutControl.updateChuteApiLog("Sort Task (2)", shopid, trfno, chuteno,"", toteid, shopname, chuteid, String.valueOf(statusCode), "onSuccess-"+responseBody.toString());
                            if(statusCode==200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                                    if (jsonArray.length() > 0) {
                                        JSONObject json = jsonArray.getJSONObject(0);
                                        if (json.getString("Result").equals("1")) {
                                            callback.onSucess(statusCode);
                                        } else {
                                            callback.onFailure(json.getString("errorMsg"));
                                        }
                                    }
                                } catch (Exception e) {
                                    objChuteCheckInCheckOutControl.updateChuteApiLog("Sort Task (3)", shopid, trfno, chuteno,"", toteid, shopname, chuteid, String.valueOf(statusCode), "Exception-"+e);
                                    callback.onFailure("postSortTask - 6 Parse error: " + e.getMessage());
                                }
                            } else {
                                callback.onFailure("postSortTask - 6.1 Parse error: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            objChuteCheckInCheckOutControl.updateChuteApiLog("Sort Task (4)", shopid, trfno, chuteno,"", toteid, shopname, chuteid, String.valueOf(statusCode), "onFailure-"+responseBody.toString()+"-"+error);
                            callback.onFailure("postSortTask - 7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("postSortTask - 8 Exception: " + e.getMessage());
        }
    }
}
