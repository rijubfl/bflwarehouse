package com.bflgroup.warehouse.ui.supplierboxgrn;

public class SupplierBoxGRNScannedBoxTicket {
    public String contId;
    public String cartonId;
    public String po;
    public int cartonQty;
    public String auditReq;
    public String saveScan;
    public String logBox;

    public SupplierBoxGRNScannedBoxTicket(String contId, String cartonId, String po, int cartonQty, String auditReq, String saveScan, String logBox) {
        this.contId = contId;
        this.cartonId = cartonId;
        this.po = po;
        this.cartonQty = cartonQty;
        this.auditReq = auditReq;
        this.saveScan = saveScan;
        this.logBox = logBox;
    }
}