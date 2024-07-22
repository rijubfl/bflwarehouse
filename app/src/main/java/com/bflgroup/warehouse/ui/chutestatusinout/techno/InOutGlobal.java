package com.bflgroup.warehouse.ui.chutestatusinout.techno;

public class InOutGlobal {

    public static InOutGlobal instance;
    private static String trfRecNo;
    private static String batchCode;
    private static int trfTotQty;
    private static String chuteNo;
    private static String labelInfo;
    private static String chuteLastInOut;

    public static String getChuteLastInOut() {
        return chuteLastInOut;
    }

    public static void setChuteLastInOut(String chuteLastInOut) {
        InOutGlobal.chuteLastInOut = chuteLastInOut;
    }

    public static String getLabelInfo() {
        return labelInfo;
    }

    public static void setLabelInfo(String labelInfo) {
        InOutGlobal.labelInfo = labelInfo;
    }

    public String getTrfRecNo() {
        return trfRecNo;
    }

    public void setTrfRecNo(String trfRecNo) {
        InOutGlobal.trfRecNo = trfRecNo;
    }

    public int getTrfTotQty() {
        return trfTotQty;
    }

    public void setTrfTotQty(int trfTotQty) {
        InOutGlobal.trfTotQty = trfTotQty;
    }

    public static String getBatchCode() {
        return batchCode;
    }

    public String getChuteNo() {
        return chuteNo;
    }

    public void setChuteNo(String chuteNo) {
        InOutGlobal.chuteNo = chuteNo;
    }

    public static void setBatchCode(String batchCode) {
        InOutGlobal.batchCode = batchCode;
    }

    public static void setInstance(InOutGlobal instance) {
        InOutGlobal.instance = instance;
    }

    public static synchronized InOutGlobal getInstance() {
        if (instance == null) {
            instance = new InOutGlobal();
        }
        return instance;
    }
}
