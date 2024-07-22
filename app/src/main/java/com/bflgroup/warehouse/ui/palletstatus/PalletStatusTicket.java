package com.bflgroup.warehouse.ui.palletstatus;

public class PalletStatusTicket {
    public String palletno;
    public String status;
    public String dateTime;

    public PalletStatusTicket(String palletno, String status, String dateTime) {
        this.palletno = palletno;
        this.status = status;
        this.dateTime = dateTime;
    }
}
