package com.bflgroup.warehouse.ui.chutestatusinout.jafza;

public class InOutJafzaGlobal {

    public static InOutJafzaGlobal instance;
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
        InOutJafzaGlobal.chuteLastInOut = chuteLastInOut;
    }

    public static String getLabelInfo() {
        return labelInfo;
    }

    public static void setLabelInfo(String labelInfo) {
        InOutJafzaGlobal.labelInfo = labelInfo;
    }

    public String getTrfRecNo() {
        return trfRecNo;
    }

    public void setTrfRecNo(String trfRecNo) {
        InOutJafzaGlobal.trfRecNo = trfRecNo;
    }

    public int getTrfTotQty() {
        return trfTotQty;
    }

    public void setTrfTotQty(int trfTotQty) {
        InOutJafzaGlobal.trfTotQty = trfTotQty;
    }

    public static String getBatchCode() {
        return batchCode;
    }

    public String getChuteNo() {
        return chuteNo;
    }

    public void setChuteNo(String chuteNo) {
        InOutJafzaGlobal.chuteNo = chuteNo;
    }

    public static void setBatchCode(String batchCode) {
        InOutJafzaGlobal.batchCode = batchCode;
    }

    public static void setInstance(InOutJafzaGlobal instance) {
        InOutJafzaGlobal.instance = instance;
    }

    public static synchronized InOutJafzaGlobal getInstance() {
        if (instance == null) {
            instance = new InOutJafzaGlobal();
        }
        return instance;
    }
}
