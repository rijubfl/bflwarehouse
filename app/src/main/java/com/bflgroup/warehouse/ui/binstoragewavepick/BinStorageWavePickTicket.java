package com.bflgroup.warehouse.ui.binstoragewavepick;

public class BinStorageWavePickTicket {
    public String toteId;
    public String boxNo;
    public String boxPerc;
    public String text;
    public String color;
    public String pickOrder;
    public String zones;
    public String dDeep;
    public String rowNo;
    public String checkingType;
    public String location;

    public BinStorageWavePickTicket(String toteId, String boxNo, String boxPerc, String text, String color, String pickOrder, String zones, String dDeep, String rowNo, String checkingType, String location) {
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.boxPerc = boxPerc;
        this.text = text;
        this.color = color;
        this.pickOrder = pickOrder;
        this.zones = zones;
        this.dDeep = dDeep;
        this.rowNo = rowNo;
        this.checkingType = checkingType;
        this.location = location;
    }

}
