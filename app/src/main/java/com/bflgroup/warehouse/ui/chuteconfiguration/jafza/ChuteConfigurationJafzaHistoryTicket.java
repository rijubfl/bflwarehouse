package com.bflgroup.warehouse.ui.chuteconfiguration.jafza;

public class ChuteConfigurationJafzaHistoryTicket {
    String chuteId;
    String trnDate;
    String shopName;
    String status;
    String toteId;
    String username;

    public ChuteConfigurationJafzaHistoryTicket(String chuteId, String trnDate, String shopName, String status, String toteId, String username) {
        chuteId = chuteId;
        trnDate = trnDate;
        shopName = shopName;
        status = status;
        toteId = toteId;
        this.username = username;
    }
}
