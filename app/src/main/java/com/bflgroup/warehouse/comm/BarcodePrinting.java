package com.bflgroup.warehouse.comm;

import android.graphics.Bitmap;

import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.UnsupportedEncodingException;

public class BarcodePrinting {
    private CPCLPrinter cpclPrinter;

    private Global objGlobal = Global.getInstance();

    public BarcodePrinting() {
        //cpclPrinter = new CPCLPrinter();    //Default = English.
        //cpclPrinter = new CPCLPrinter("EUC-KR"); // Korean.
        cpclPrinter = new CPCLPrinter("GB2312"); //Chinese.
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

    public byte[] getLabelWasNowHoneyWell(String mainHead,String subHead, String itemCode, String itemName, String barcode,String trfno, String was, String now,String mark,String usid,String pQty) {
        String mainHead1 = "1911A1000800020" + mainHead + "\r\n";
        String trfNo1 = "1911A0600420122" + subHead + " - " + trfno + "\r\n";
        String itemName1 = "1911A0600730007" + itemName + "\r\n";
        String itemCode1 = "1911A0600410006" + itemCode + "\r\n";
        String was1 = "1911A1000100007" + was + "\r\n";
        String now1 = "1911A1200060092" + now + "\r\n";
        String barcode1 = "1e2101900520006C" + barcode + "\r\n";
        String barcode2 = "1W1j2101900520006" + barcode + "\r\n";
        String mark1 = "1911A0600260172" + mark + "\r\n";
        String usid1 = "1911A0600140172" + usid + "\r\n";
        String pQty1="Q000" + pQty + "\r\n";

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, "\u0002n\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002M0500\r\n".getBytes());
        printData = addToDataVault(printData, "\u0002KcLW0200;\r\n".getBytes());
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
        printData = addToDataVault(printData, mainHead1.getBytes());
        printData = addToDataVault(printData, itemName1.getBytes());
        printData = addToDataVault(printData, was1.getBytes());
        printData = addToDataVault(printData, "1911A0800240024WAS\r\n".getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, "1911A1000220110NOW\r\n".getBytes());
        printData = addToDataVault(printData, now1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, barcode2.getBytes());
        printData = addToDataVault(printData, itemCode1.getBytes());
        printData = addToDataVault(printData, "FB+\r\n".getBytes());
        printData = addToDataVault(printData, "1X1100100210071P0010001002100710012000600120006\r\n".getBytes());
        printData = addToDataVault(printData, trfNo1.getBytes());
        printData = addToDataVault(printData, "FB-\r\n".getBytes());
        printData = addToDataVault(printData, mark1.getBytes());
        printData = addToDataVault(printData, usid1.getBytes());
        printData = addToDataVault(printData, pQty1.getBytes());
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