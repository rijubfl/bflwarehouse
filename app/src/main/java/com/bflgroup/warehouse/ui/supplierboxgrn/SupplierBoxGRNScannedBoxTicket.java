package com.bflgroup.warehouse.ui.supplierboxgrn;

public class SupplierBoxGRNScannedBoxTicket {
    public String cartonId;
    public String po;
    public int cartonQty;
    public String auditReq;

    public SupplierBoxGRNScannedBoxTicket(String cartonId, String po, int cartonQty, String auditReq) {
        this.cartonId = cartonId;
        this.po = po;
        this.cartonQty = cartonQty;
        this.auditReq = auditReq;
    }
}
