package com.bflgroup.warehouse.comm.roboapi;

import android.content.Context;

import com.bflgroup.warehouse.comm.Global;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class RoboApi {

    private Global objGlobal = Global.getInstance();

    public void postChuteStatusOld(Context context, String chuteid,String strStatus, RoboApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ChuteId", chuteid);
            jsonObject.put("Status", strStatus);
            final AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200) {
                                callback.onSucess(statusCode);
                            } else {
                                callback.onFailure("7.1 Request failed: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("8 Exception: " + e.getMessage());
        }
    }

    public void postChuteStatusNew(Context context, String chuteid,boolean blStatus, RoboApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ChuteId", chuteid);
            jsonObject.put("status", blStatus);
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", objGlobal.getRoboChuteStatusAPIToken());
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, objGlobal.getRoboChuteStatusAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200) {
                                try {
                                    JSONObject jso = new JSONObject(new String(responseBody));
                                    boolean status = jso.getBoolean("status");
                                    String msg = jso.getString("message");
                                    if (status)
                                        callback.onSucess(statusCode);
                                    else
                                        callback.onFailure("6.1 Parse error: " + msg);
                                } catch (Exception e) {
                                    callback.onFailure("6 Parse error: " + e.getMessage());
                                }
                            } else {
                                callback.onFailure("6.1 Parse error: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("8 Exception: " + e.getMessage());
        }
    }

    public void postLabelInfo(Context context,String toteId,String trfno,String messageid, String labelinfo, RoboApiCallback callback) {
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
            client.post(context, objGlobal.getRoboLabelInfoAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200) {
                                callback.onSucess(statusCode);
                            } else{
                                callback.onFailure("7.1 Request failed: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("8 Exception: " + e.getMessage());
        }
    }

    public void postSortTask(Context context, String toteid,String chuteno,String trfno, String messageid,String batchcode, RoboApiCallback callback) {
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
            client.post(context, objGlobal.getRoboSortTaskAPI(), entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                                    callback.onFailure("6 Parse error: " + e.getMessage());
                                }
                            } else {
                                callback.onFailure("6.1 Parse error: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("7 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("8 Exception: " + e.getMessage());
        }
    }



}
