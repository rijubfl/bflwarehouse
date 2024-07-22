package com.bflgroup.warehouse.ui.addorremoveboxfrompallet.model;

public class BoxInOutRequestData {
    public String SrNo;
    public String PalletType;
    public String GroupCode;
    public String palletno;
    public int Qty;
    public String boxNo;
    public String toteid;
    public String isNewBox;
    public String selectedBoxNo;
    public String selectedPalletNo;
    public int type;


    public BoxInOutRequestData(String palletno, String SrNo, String GroupCode, String PalletType, int Qty,
                               String boxNo,String toteid,String isNewBox,String selectedPalletNo,String selectedBoxNo,int type) {
        this.palletno = palletno;
        this.SrNo = SrNo;
        this.GroupCode = GroupCode;
        this.PalletType = PalletType;
        this.Qty = Qty;
        this.boxNo = boxNo;
        this.toteid = toteid;
        this.isNewBox = isNewBox;
        this.selectedPalletNo = selectedPalletNo;
        this.selectedBoxNo = selectedBoxNo;
        this.type= type;
    }


}



