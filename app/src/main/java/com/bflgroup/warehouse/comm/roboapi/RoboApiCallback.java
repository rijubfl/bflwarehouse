package com.bflgroup.warehouse.comm.roboapi;

public interface RoboApiCallback {
    void onSucess(int statuscode);
    void onFailure(String errorMessage);
}
