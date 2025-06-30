package com.bflgroup.warehouse.comm.wmsapi;

import org.json.JSONObject;

public interface WmsApiCallback {
    void onJsonObjectReceived(JSONObject objJSONObject);
    void onFailure(String errorMessage);
}
