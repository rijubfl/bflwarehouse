package com.bflgroup.warehouse.comm.wmsapi;

public interface WmsAuthCallback {
    void onTokenReceived(String token);
    void onFailure(String errorMessage);
}
