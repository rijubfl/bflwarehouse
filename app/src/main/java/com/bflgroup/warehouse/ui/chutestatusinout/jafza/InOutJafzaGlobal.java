package com.bflgroup.warehouse.ui.chutestatusinout.jafza;

public class InOutJafzaGlobal {

    public static InOutJafzaGlobal instance;
    private static String trfRecNo;
    private static String batchCode;
    private static int trfTotQty;
    private static String chuteNo;
    private static String labelInfo;
    private static String chuteLastInOut;

    private static String ptrfno;
    private static String pboxno;
    private static String pshopname;
    private static String pqty;
    private static String pdeldate;
    private static String ptrfdate;
    private static String ptoteid;
    private static String premarks;
    private static String ppreparedby;

    private static String reprintTrfno;
    private static String reprintShop;
    private static String reprintToteid;

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

    public static String getPtrfno() {
        return ptrfno;
    }

    public static void setPtrfno(String ptrfno) {
        InOutJafzaGlobal.ptrfno = ptrfno;
    }

    public static String getPboxno() {
        return pboxno;
    }

    public static void setPboxno(String pboxno) {
        InOutJafzaGlobal.pboxno = pboxno;
    }

    public static String getPshopname() {
        return pshopname;
    }

    public static void setPshopname(String pshopname) {
        InOutJafzaGlobal.pshopname = pshopname;
    }

    public static String getPqty() {
        return pqty;
    }

    public static void setPqty(String pqty) {
        InOutJafzaGlobal.pqty = pqty;
    }

    public static String getPdeldate() {
        return pdeldate;
    }

    public static void setPdeldate(String pdeldate) {
        InOutJafzaGlobal.pdeldate = pdeldate;
    }

    public static String getPtrfdate() {
        return ptrfdate;
    }

    public static void setPtrfdate(String ptrfdate) {
        InOutJafzaGlobal.ptrfdate = ptrfdate;
    }

    public static String getPtoteid() {
        return ptoteid;
    }

    public static void setPtoteid(String ptoteid) {
        InOutJafzaGlobal.ptoteid = ptoteid;
    }

    public static String getPremarks() {
        return premarks;
    }

    public static void setPremarks(String premarks) {
        InOutJafzaGlobal.premarks = premarks;
    }

    public static String getPpreparedby() {
        return ppreparedby;
    }

    public static void setPpreparedby(String ppreparedby) {
        InOutJafzaGlobal.ppreparedby = ppreparedby;
    }

    public static String getReprintTrfno() {
        return reprintTrfno;
    }

    public static void setReprintTrfno(String reprintTrfno) {
        InOutJafzaGlobal.reprintTrfno = reprintTrfno;
    }

    public static String getReprintShop() {
        return reprintShop;
    }

    public static void setReprintShop(String reprintShop) {
        InOutJafzaGlobal.reprintShop = reprintShop;
    }

    public static String getReprintToteid() {
        return reprintToteid;
    }

    public static void setReprintToteid(String reprintToteid) {
        InOutJafzaGlobal.reprintToteid = reprintToteid;
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
