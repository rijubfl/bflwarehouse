package com.bflgroup.warehouse.ui.generatebarcode;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.printclass.BluetoothUtil;
import com.bflgroup.warehouse.printclass.SunmiPrintHelper;

public class GenerateBarcodePrint {
    private Global objGlobal = Global.getInstance();

    public boolean printMainInvoice(String invoiceNo) {
        if (!BluetoothUtil.isBlueToothPrinter) {
            return printGenerateBarcode(invoiceNo);
        } else {
            //printByBluTooth(content);
            return false;
        }
    }

    public boolean printGenerateBarcode(String invoiceNo) {
        try {
            if (!BluetoothUtil.isBlueToothPrinter) {
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
                SunmiPrintHelper.getInstance().printBarCode(invoiceNo, 8, 70, 2, 2);
                SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
                SunmiPrintHelper.getInstance().printQr(invoiceNo, 8, 3);
                SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
                SunmiPrintHelper.getInstance().printLine(3);
                return true;
            } else {
                //printByBluTooth(content);
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.getItemDetailsBarcodeRfid : " + e.toString());
            return false;
        }
    }
}
