package com.bflgroup.warehouse.ui.ageingstocktaking;

import com.bflgroup.warehouse.comm.Global;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgeingStockTakingFile {

    private Global objGlobal = Global.getInstance();
    private  String FILE_NAME_FORMAT = "yyyyMMdd";

    public boolean saveToFile(String data) {
        try {
            String sOutFilesPath = "/storage/emulated/0/Download/BFL/StockTaking/";
            File filePath = new File(sOutFilesPath);
            if (!filePath.exists()) {
                boolean mkdir = filePath.mkdir();
                if (!mkdir) {
                    objGlobal.setErrorMessage("Failed: create output folder");
                    return false;
                }
            }
            String fileTimeStamp = GetCurrentDateTime(FILE_NAME_FORMAT);
            String fileName = String.format(Locale.getDefault(), "%s%s_%s.txt", sOutFilesPath, "RF", fileTimeStamp);
            Writer itemsOutput = new BufferedWriter(new FileWriter(fileName, true));
            itemsOutput.append("123456789");
            itemsOutput.close();
        } catch (Exception ex) {
            objGlobal.setErrorMessage(ex.toString());
            return false;
        }
        return true;
    }

    private   String GetCurrentDateTime(final String outputFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
        Date dateObj = new Date();
        return simpleDateFormat.format(dateObj);
    }

}
