package com.bflgroup.warehouse.comm;

import android.graphics.Bitmap;

import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.UnsupportedEncodingException;

public class BarcodePrinting {
    String mainHead="BRANDS FOR LESS";

    private CPCLPrinter cpclPrinter;

    private Global objGlobal = Global.getInstance();

    public BarcodePrinting() {
        cpclPrinter = new CPCLPrinter();    //Default = English.
        //cpclPrinter = new CPCLPrinter("EUC-KR"); // Korean.
        //cpclPrinter = new CPCLPrinter("GB2312"); //Chinese.
        //WPC1256
    }

    public boolean PrintBarcodeImage(int count, int paper_type, Bitmap label) throws UnsupportedEncodingException {
        try {
            cpclPrinter.setForm(0, 200, 200, 406, 384, count);
            cpclPrinter.setMedia(paper_type);
            cpclPrinter.printBitmap(label, 0, 0);
            cpclPrinter.printForm();
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PrintBarcodeImage : " + e);
            return false;
        }
    }

    public boolean PrintBarcodeByte(byte[] printData) throws UnsupportedEncodingException {
        try {
            cpclPrinter.sendByte(printData);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("PrintBarcodeByte : " + e);
            return false;
        }
    }

    public byte[] getTransferPrint(String shop,String trfno,String boxno, String qty,String deldate, String trfdate,String toteid,String remarks,String preparedby) {
        String str1 = "1911A1802590011Tote        : " + toteid + "\r\n";
        String str2 = "1911A2403470043BRANDS FOR LESS" + "\r\n";
        String str3 = "1e6308200480107B"+ trfno + "\r\n";
        String str4 = "1911A1802950011Shop.     : " + shop + "\r\n";
        String str5 = "1911A1802270011Tnf. No.  : " + trfno + "\r\n";
        String str6 = "1911A1801580011Date.     : " + trfdate + "\r\n";
        String str7 = "1911A1801900011Quanity.  : " + qty + "\r\n";
        String str8 = "Q0001" + "\r\n";
        String str9 = "E" + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M1000\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0400;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002O0220\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, str1.getBytes());
        printData = addToDataVault(printData, str2.getBytes());
        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, str3.getBytes());
        printData = addToDataVault(printData, str4.getBytes());
        printData = addToDataVault(printData, str5.getBytes());
        printData = addToDataVault(printData, str6.getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, str7.getBytes());
        printData = addToDataVault(printData, str8.getBytes());
        printData = addToDataVault(printData, str9.getBytes());
        return printData;
    }

    public byte[] getUsaBox(String shop,String trfno,String boxno, String qty,String deldate, String trfdate,String toteid,String remarks,String preparedby) {
        String str1 = "1911A1802590011Tote        : SG00001" + "\r\n";
        String str2 = "1911A2403470043BRANDS FOR LESS" + "\r\n";
        String str3 = "1e6308200480107BFT00158952" + "\r\n";
        String str4 = "1911A1802950011Shop.     : BFLIBN" + "\r\n";
        String str5 = "1911A1802270011Tnf. No.  : FT00158952" + "\r\n";
        String str6 = "1911A1801580011Date.     : 10/01/2024" + "\r\n";
        String str7 = "1911A1801900011Quanity.  : 25" + "\r\n";
        String str8 = "Q0001" + "\r\n";
        String str9 = "E" + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M1000\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0400;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002O0220\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());

        printData = addToDataVault(printData, str1.getBytes());
        printData = addToDataVault(printData, str2.getBytes());
        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, str3.getBytes());
        printData = addToDataVault(printData, str4.getBytes());
        printData = addToDataVault(printData, str5.getBytes());
        printData = addToDataVault(printData, str6.getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, str7.getBytes());
        printData = addToDataVault(printData, str8.getBytes());
        printData = addToDataVault(printData, str9.getBytes());
        return printData;
    }

    public byte[] getUsaPallet(String palletno,String boxcnt,String remarks,String pallettype,String groupname,String preparedby,String trndate,String trntime) {
        String str1 = "1911A2403390037" + mainHead + "\r\n";
        String str2 = "1911A1400530012" + preparedby + "\r\n";
        String str3 = "1911A1400230012" + trndate + "\r\n";
        String str4 = "1911A1400230150" + trntime + "\r\n";
        String str5 = "1911A1401900012" + boxcnt + "\r\n";
        String str6 = "1911A2401450012" + palletno + "\r\n";
        String str7 = "1e8409402320022A" + palletno + "\r\n";
        String str8 = "1911A1401110012" + palletno + "\r\n";
        String str9 = "1911A1401110012" + pallettype + "\r\n";
        String str10 = "1911A1400830012" + remarks + "\r\n";
        String str11="Q0001\r\n";
        String str12="E\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0986\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0384;\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002V0\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002SG\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "PG\r\n".getBytes());
        printData = addToDataVault(printData, "pG\r\n".getBytes());
        printData = addToDataVault(printData, "SG\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, str1.getBytes());
        printData = addToDataVault(printData, str2.getBytes());
        printData = addToDataVault(printData, str3.getBytes());
        printData = addToDataVault(printData, str4.getBytes());
        printData = addToDataVault(printData, str5.getBytes());
        printData = addToDataVault(printData, str6.getBytes());
        printData = addToDataVault(printData, str7.getBytes());
        printData = addToDataVault(printData, str8.getBytes());
        printData = addToDataVault(printData, str9.getBytes());
        printData = addToDataVault(printData, str10.getBytes());
        printData = addToDataVault(printData, str11.getBytes());
        printData = addToDataVault(printData, str12.getBytes());
        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());
        return printData;
    }

    public byte[] getLabelWasNowHoneyWellTestPrint() {
        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200\r\n;".getBytes());
        printData = addToDataVault(printData, "\u0002O0220\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002d\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002L\r\n".getBytes());
        printData = addToDataVault(printData, "D11\r\n".getBytes());
        printData = addToDataVault(printData, "ySPM\r\n".getBytes());
        printData = addToDataVault(printData, "A2\r\n".getBytes());
        printData = addToDataVault(printData, "1911A1400560045TEST PRINT\r\n".getBytes());
        printData = addToDataVault(printData, "1e6303400190039C12340678\r\n".getBytes());
        printData = addToDataVault(printData, "Q0001\r\n".getBytes());
        printData = addToDataVault(printData, "E\r\n".getBytes());
        return printData;
    }

    private byte[] addToDataVault(byte[] src, byte[] data) {
        byte[] now;
        if ((src.length == 1) && (src[0] == 0)) {
            now = new byte[data.length];
            System.arraycopy(data, 0, now, 0, data.length);
        } else {
            now = new byte[src.length + data.length];
            System.arraycopy(src, 0, now, 0, src.length);
            System.arraycopy(data, 0, now, src.length, data.length);
        }
        return now;
    }





}