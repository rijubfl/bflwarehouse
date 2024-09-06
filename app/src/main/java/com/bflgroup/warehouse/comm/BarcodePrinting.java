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
        String str01="\u0002n\r\n";
        String str02="\u0002M1000\r\n";
        String str03="\u0002KcLW0400;\r\n";
        String str04="\u0002O0220\r\n";
        String str05="\u0002d\r\n";
        String str06="\u0002L\r\n";
        String str07="D11\r\n";
        String str08="ySPM\r\n";
        String str09="A2\r\n";
        String str10 = "1911A1802590011Tote        : " + toteid + "\r\n";
        String str11 = "1911A2403470043" + mainHead + "\r\n";
        String str12 = "1e6308200480107B"+ trfno + "\r\n";
        String str13 = "1911A1802950011Shop.     : " + shop + "\r\n";
        String str14 = "1911A1802270011Trf. No.  : " + trfno + "\r\n";
        String str15 = "1911A1801580011Date.     : " + trfdate + "\r\n";
        String str16 = "1911A1801900011Quanity.  : " + qty + "\r\n";
        String str17 = "Q0001" + "\r\n";
        String str18 = "E" + "\r\n";

        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, str01.getBytes());
        printData = addToDataVault(printData, str02.getBytes());
        printData = addToDataVault(printData, str03.getBytes());
        printData = addToDataVault(printData, str04.getBytes());
        printData = addToDataVault(printData, str05.getBytes());
        printData = addToDataVault(printData, str06.getBytes());
        printData = addToDataVault(printData, str07.getBytes());
        printData = addToDataVault(printData, str08.getBytes());
        printData = addToDataVault(printData, str09.getBytes());
        printData = addToDataVault(printData, str10.getBytes());
        printData = addToDataVault(printData, str11.getBytes());
        printData = addToDataVault(printData, str12.getBytes());
        printData = addToDataVault(printData, str13.getBytes());
        printData = addToDataVault(printData, str14.getBytes());
        printData = addToDataVault(printData, str15.getBytes());
        printData = addToDataVault(printData, str16.getBytes());
        printData = addToDataVault(printData, str17.getBytes());
        printData = addToDataVault(printData, str18.getBytes());
        return printData;
    }

    public byte[] getUsaBoxPrint(String boxno,String pallettype,String typename, String qty,String date, String time,String remarks,String preparedby) {
        String str01 = "\u0002n\r\n";
        String str02 = "\u0002M0986\r\n";
        String str03 = "\u0002KcLW0384\r\n";
        String str04 = "\u0002V0\r\n";
        String str05 = "\u0002SG\r\n";
        String str06 = "\u0002d\r\n";
        String str07 = "\u0002L\r\n";
        String str08= "D11\r\n";
        String str09 = "PG\r\n";
        String str10 = "pG\r\n";
        String str11 = "SG\r\n";
        String str12 = "ySPM\r\n";
        String str13 = "A2\r\n";
        String str14 = "1911A2403270019" + mainHead + "\r\n";
        String str15 = "1911A1800790020" + pallettype + "\r\n";
        String str16 = "1911A1800790087" + typename + "\r\n";
        String str17 = "1911A1200560041Qty:" + qty + "\r\n";
        String str18 = "1911A1200260041" + date + "\r\n";
        String str19 = "1911A1200260220" + time +"\r\n";
        String str20 = "1911A1802480019" + date + "\r\n";
        String str21 = "1911A2401200019Box No." + "\r\n";
        String str22 = "1911A2401200150" + boxno + "\r\n";
        String str23 = "1e8405601760022A" + boxno + "\r\n";
        String str24 = "Q0004\r\n";
        String str25 = "E\r\n";

        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, str01.getBytes());
        printData = addToDataVault(printData, str02.getBytes());
        printData = addToDataVault(printData, str03.getBytes());
        printData = addToDataVault(printData, str04.getBytes());
        printData = addToDataVault(printData, str05.getBytes());
        printData = addToDataVault(printData, str06.getBytes());
        printData = addToDataVault(printData, str07.getBytes());
        printData = addToDataVault(printData, str08.getBytes());
        printData = addToDataVault(printData, str09.getBytes());
        printData = addToDataVault(printData, str10.getBytes());
        printData = addToDataVault(printData, str11.getBytes());
        printData = addToDataVault(printData, str12.getBytes());
        printData = addToDataVault(printData, str13.getBytes());
        printData = addToDataVault(printData, str14.getBytes());
        printData = addToDataVault(printData, str15.getBytes());
        printData = addToDataVault(printData, str16.getBytes());
        printData = addToDataVault(printData, str17.getBytes());
        printData = addToDataVault(printData, str18.getBytes());
        printData = addToDataVault(printData, str19.getBytes());
        printData = addToDataVault(printData, str20.getBytes());
        printData = addToDataVault(printData, str21.getBytes());
        printData = addToDataVault(printData, str22.getBytes());
        printData = addToDataVault(printData, str23.getBytes());
        printData = addToDataVault(printData, str24.getBytes());
        printData = addToDataVault(printData, str25.getBytes());
        return printData;
    }

    public byte[] getUsaPalletPrint(String palletno,String boxcnt,String remarks,String pallettype, String typename,String groupname,String preparedby,String trndate,String trntime) {
        String str01 = "\u0002n\r\n";
        String str02 = "\u0002M0986\r\n";
        String str03 = "\u0002KcLW0384;\r\n";
        String str04 = "\u0002V0\r\n";
        String str05 = "\u0002SG\r\n";
        String str06 = "\u0002d\r\n";
        String str07 = "\u0002L\r\n";
        String str08 = "D11\r\n";
        String str09 = "PG\r\n";
        String str10 = "pG\r\n";
        String str11 = "SG\r\n";
        String str12 = "ySPM\r\n";
        String str13 = "A2\r\n";
        String str14 = "1911A2403390037" + mainHead + "\r\n";
        String str15 = "1911A1400530012Prepared By : " + preparedby + "\r\n";
        String str16 = "1911A1400230012" + trndate + "\r\n";
        String str17 = "1911A1400230150" + trntime + "\r\n";
        String str18 = "1911A1401900012Box Cnt: " + boxcnt + "\r\n";
        String str19 = "1911A2401450012Pallet No: " + palletno + "\r\n";
        String str20 = "1e8409402320022A" + palletno     + "\r\n";
        String str21 = "1911A1401110012Pallet Type : " + typename + "\r\n";
        String str22 = "1911A1400830012Remarks : " + remarks + "\r\n";
        String str23 = "1911A2400110300***\r\n";
        String str24 = "Q0004\r\n";
        String str25 = "E\r\n";

        //printData = addToDataVault(printData, "FB+\r\n".getBytes());
        //printData = addToDataVault(printData, "FB-\r\n".getBytes());

        byte[] printData = new byte[]{0};
        printData = addToDataVault(printData, str01.getBytes());
        printData = addToDataVault(printData, str02.getBytes());
        printData = addToDataVault(printData, str03.getBytes());
        printData = addToDataVault(printData, str04.getBytes());
        printData = addToDataVault(printData, str05.getBytes());
        printData = addToDataVault(printData, str06.getBytes());
        printData = addToDataVault(printData, str07.getBytes());
        printData = addToDataVault(printData, str08.getBytes());
        printData = addToDataVault(printData, str09.getBytes());
        printData = addToDataVault(printData, str10.getBytes());
        printData = addToDataVault(printData, str11.getBytes());
        printData = addToDataVault(printData, str12.getBytes());
        printData = addToDataVault(printData, str13.getBytes());
        printData = addToDataVault(printData, str14.getBytes());
        printData = addToDataVault(printData, str15.getBytes());
        printData = addToDataVault(printData, str16.getBytes());
        printData = addToDataVault(printData, str17.getBytes());
        printData = addToDataVault(printData, str18.getBytes());
        printData = addToDataVault(printData, str19.getBytes());
        printData = addToDataVault(printData, str20.getBytes());
        printData = addToDataVault(printData, str21.getBytes());
        printData = addToDataVault(printData, str22.getBytes());
        printData = addToDataVault(printData, str23.getBytes());
        printData = addToDataVault(printData, str24.getBytes());
        printData = addToDataVault(printData, str25.getBytes());
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