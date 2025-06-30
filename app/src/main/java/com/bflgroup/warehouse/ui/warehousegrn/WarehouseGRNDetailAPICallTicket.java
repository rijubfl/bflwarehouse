package com.bflgroup.warehouse.ui.warehousegrn;

public class WarehouseGRNDetailAPICallTicket {
    private String ginno;
    private String ginDate;
    private String wareHouseFrom;
    private String wareHouseTo;
    private String palletNo;
    private String boxNo;
    private String toteId;

    public WarehouseGRNDetailAPICallTicket(String ginno, String ginDate, String wareHouseFrom, String wareHouseTo, String palletNo, String boxNo, String toteId) {
        this.ginno = ginno;
        this.ginDate = ginDate;
        this.wareHouseFrom = wareHouseFrom;
        this.wareHouseTo = wareHouseTo;
        this.palletNo = palletNo;
        this.boxNo = boxNo;
        this.toteId = toteId;
    }

    public String getGinno() { return ginno; }
    public String getGinDate() { return ginDate; }
    public String getWareHouseFrom() { return wareHouseFrom; }
    public String getWareHouseTo() { return wareHouseTo; }
    public String getPalletNo() { return palletNo; }
    public String getBoxNo() { return boxNo; }
    public String getToteId() { return toteId; }
}
