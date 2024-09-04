package com.bflgroup.warehouse.ui.rackquery.model;

public class RackDetailsData {
    public String warehouse;
    public String toteId;
    public String boxNo;
    public String location;

    public RackDetailsData(String warehouse, String toteId, String boxNo,String location) {
        this.warehouse = warehouse;
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.location = location;
    }
}
