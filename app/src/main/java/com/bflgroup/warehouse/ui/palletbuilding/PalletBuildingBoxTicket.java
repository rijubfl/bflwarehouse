package com.bflgroup.warehouse.ui.palletbuilding;

public class PalletBuildingBoxTicket {
    public String toteId;
    public String boxNo;
    public String pallettype;
    public String boxRemarks;
    public String qty;

    public PalletBuildingBoxTicket(String toteId, String boxNo, String pallettype, String boxRemarks, String qty) {
        this.toteId = toteId;
        this.boxNo = boxNo;
        this.pallettype = pallettype;
        this.boxRemarks = boxRemarks;
        this.qty = qty;
    }
}
