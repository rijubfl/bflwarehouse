package com.bflgroup.warehouse.comm.wmsapi;

import android.content.Context;

import com.bflgroup.warehouse.comm.Global;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class WmsApi {

    String urlWmsApi = "http://bfltp.dynalias.com:8076/api/";
    String authWMSAPIUsername = "test_apigee", authWMSAPIPassword = "apigee123";

    public void postWMSAPIAuthToken(Context context, WmsAuthCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", authWMSAPIUsername);
            jsonObject.put("password", authWMSAPIPassword);
            final AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, urlWmsApi + "Authentication/login", entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                if (json.getBoolean("isSuccess")) {
                                    String token = json.getString("token");
                                    callback.onTokenReceived(token);
                                } else {
                                    String msg = json.getString("message");
                                    callback.onFailure("1 "+msg);
                                }
                            } catch (Exception e) {
                                callback.onFailure("2 Parse error: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            callback.onFailure("3 Request failed: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onFailure("4 Exception: " + e.getMessage());
        }
    }

    public void postWMSAPICallWithToken(Context context, String url, String token, JSONObject jsonObject, WmsApiCallback callback) {
        try {
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", token);
            StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            client.post(context, urlWmsApi + url, entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                if (json.getBoolean("status")) {
                                    callback.onJsonObjectReceived(json);
                                } else {
                                    String msg = json.getString("message");
                                    callback.onFailure("5 "+msg);
                                }
                            } catch (Exception e) {
                                callback.onFailure("6 Parse error: " + e.getMessage());
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

    public void getWMSAPICallWithToken(Context context, String url, String token, WmsApiCallback callback) {
        try {
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", token);
            client.get(context, urlWmsApi + url,null, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                if (json.getBoolean("status")) {
                                    callback.onJsonObjectReceived(json);
                                } else {
                                    String msg = json.getString("message");
                                    callback.onFailure("5 "+msg);
                                }
                            } catch (Exception e) {
                                callback.onFailure("6 Parse error: " + e.getMessage());
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