package com.bflgroup.warehouse.ui.ageingstocktaking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingReports;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingReportsItemSearch;
import com.bflgroup.warehouse.ui.ageingstocktaking.model.AgeingStockTakingScanItems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AgeingStockTakingDbManager {

    private Global objGlobal = Global.getInstance();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();
    private DBConnection dbConnection = new DBConnection();
    private AgeingStockTakingFile objAgeingStockTakingFile = new AgeingStockTakingFile();

    ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems = new ArrayList<AgeingStockTakingScanItems>();
    ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports = new ArrayList<AgeingStockTakingReports>();
    ArrayList<AgeingStockTakingReportsItemSearch> listAgeingStockTakingReportsItemSearch = new ArrayList<AgeingStockTakingReportsItemSearch>();

    private boolean b_Result;
    private SQLiteDatabase sqlDB;
    static final String dBName = "AGEING";
    static final int DBVersion = 2;
    static final String createTableStockTaking = "create table StockTaking (trndate text,trntime text, scan text,itemcode text,quantity int,zoneid text,result text,srid text,export text,username text)";

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoiceControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    static class DatabaseHelperUser extends SQLiteOpenHelper {
        Context context;
        DatabaseHelperUser(Context context) {
            super(context, dBName, null, DBVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String str = "";
            db.execSQL(createTableStockTaking);
            str = "StockTaking, Created, ";
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*db.execSQL("drop table if exists StockTaking");
            onCreate(db);*/
        }
    }

    public AgeingStockTakingDbManager(Context context) {
        DatabaseHelperUser db = new DatabaseHelperUser(context);
        sqlDB = db.getWritableDatabase();
    }

    public long insertData(ContentValues values) {
        long id = sqlDB.insert("StockTaking", "", values);
        return id; //fail 0 or less
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("StockTaking");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public int delete(String selection, String[] selectionArgs) {
        int count = sqlDB.delete("StockTaking", selection, selectionArgs);
        return count;
    }

    public int update(ContentValues values, String Selection, String[] SelectionArgs) {
        int count = sqlDB.update("StockTaking", values, Selection, SelectionArgs);
        return count;
    }

    public boolean deleteAllLocalDb(){
        try {
            int del = delete(null,null);
            if(del<=0)
                return false;
        } catch(Exception ex){
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:deleteAllLocalDb:"+ex.toString());
            return false;
        }
        return true;
    }

    public boolean saveScanToLocaldb(String scan, int qty, String zoneid, String result) {
        Date date = new Date();
        SimpleDateFormat cDateF = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat cTimeF = new SimpleDateFormat("HH:mm:ss");
        String cDate = cDateF.format(date);
        String cTime = cTimeF.format(date);
        String srid = String.valueOf(System.currentTimeMillis());
        String data = "";
        try {
            ContentValues values = new ContentValues();
            values.put("trndate", cDate);
            values.put("trntime", cTime);
            values.put("scan", scan);
            values.put("itemcode", seperateBarcode(scan));
            values.put("quantity", qty);
            values.put("zoneid", zoneid);
            values.put("result", result);
            values.put("srid", srid);
            values.put("Export", "N");
            values.put("UserName", objGlobal.getUserName());
            long id = sqlDB.insert("StockTaking", "", values);
            if (id > 0) {
                /*data = "| " + cDate + " | " + cTime + " | " + scan + " | " + seperateBarcode(scan) + " | " + qty + " | " + zoneid + " | " + result + " | " + srid + " |";
                b_Result = objAgeingStockTakingFile.saveToFile(data);
                if(!b_Result) {
                    return false;
                }*/
            } else {
                objGlobal.setErrorMessage("AgeingStockTakingDbManager:saveScanToLocaldb : Data not inserted");
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:saveScanToLocaldb : " + ex.toString());
            return false;
        }
        return true;
    }

    String seperateBarcode(String barcode) {
        String[] parts;
        String part1;
        int i;
        barcode = barcode.replace("'","");
        if (barcode.contains("/")) {
            parts = barcode.split("/");
            part1 = parts[0];
        } else {
            part1 = barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }

    ArrayList<AgeingStockTakingScanItems> loadAgingStockTakingItems(String limit, Context context) {
        try {
            listAgeingStockTakingScanItems.clear();
            String path = context.getDatabasePath(dBName).getPath();
            String sqlQry = "select itemcode,quantity,trndate,trntime,result from stocktaking order by trndate desc,trntime desc limit " + limit;
            if (sqlDB == null) {
                sqlDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            }
            Cursor cursor = sqlDB.rawQuery(sqlQry, null);
            if (cursor.moveToFirst()) {
                do {
                    listAgeingStockTakingScanItems.add(new AgeingStockTakingScanItems(cursor.getString(0), cursor.getInt(1),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadAgingStockTakingItems:" + ex.toString());
            return null;
        }
        return listAgeingStockTakingScanItems;
    }

    ArrayList<AgeingStockTakingReports> loadAgingStockTakingRpt(Context context,String ord) {
        objAgeingStockTakingGlobal.setTotal(0);
        double tot=0;
        try {
            String ords="";
            if(ord.equals("User")) ords=" order by username";
            if(ord.equals("Zone")) ords=" order by zoneid";
            if(ord.equals("Quantity")) ords=" order by qty";
            listAgeingStockTakingReports.clear();
            String path = context.getDatabasePath(dBName).getPath();
            String sqlQry = "select username,zoneid,sum(quantity) from stocktaking group by zoneid,username " + ords;
            if (sqlDB == null) {
                sqlDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            }
            Cursor cursor = sqlDB.rawQuery(sqlQry, null);
            if (cursor.moveToFirst()) {
                do {
                    listAgeingStockTakingReports.add(new AgeingStockTakingReports(cursor.getString(0), cursor.getString(1), cursor.getInt(2)));
                    tot=tot+cursor.getInt(2);
                } while (cursor.moveToNext());
            }
            objAgeingStockTakingGlobal.setTotal(tot);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadAgingStockTakingRpt:" + ex.toString());
            return null;
        }
        return listAgeingStockTakingReports;
    }

    ArrayList<AgeingStockTakingReportsItemSearch> loadAgingStockTakingRptItems(Context context, String itemcode) {
        objAgeingStockTakingGlobal.setTotal(0);
        double tot=0;
        try {
            listAgeingStockTakingReportsItemSearch.clear();
            String sqlQry="";
            String path = context.getDatabasePath(dBName).getPath();
            if(itemcode.isEmpty()){
                sqlQry = "select itemcode,srid,quantity from stocktaking order by srid desc";
            } else {
                sqlQry = "select itemcode,srid,quantity from stocktaking where itemcode='" + itemcode + "' order by srid desc";
            }
            if (sqlDB == null) {
                sqlDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            }
            Cursor cursor = sqlDB.rawQuery(sqlQry, null);
            if (cursor.moveToFirst()) {
                do {
                    listAgeingStockTakingReportsItemSearch.add(new AgeingStockTakingReportsItemSearch(cursor.getString(0), cursor.getString(1)));
                    tot=tot+cursor.getInt(2);
                } while (cursor.moveToNext());
            }
            objAgeingStockTakingGlobal.setTotal(tot);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadAgingStockTakingRptItems:" + ex.toString());
            return null;
        }
        return listAgeingStockTakingReportsItemSearch;
    }

    public boolean exportToMainServer(Context context) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return false;
        }
        try {
            String path = context.getDatabasePath(dBName).getPath();
            String sqlQry = "select trndate,trntime,scan,itemcode,quantity,zoneid,result,srid,export from stocktaking where export='N'";
            if (sqlDB == null) {
                sqlDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            }
            Cursor cursor = sqlDB.rawQuery(sqlQry, null);
            if (cursor.moveToFirst()) {
                do {
                    b_Result = dbConnection.insertUpdate("insert into stocktaking(Trndate,Time1,username,itemcode,Quantity,ZoneID,UserId,Device,ScanBarcode,SrId,Result) " +
                            "values ('" + cursor.getString(0) + "','" + cursor.getString(1) + "','" + objGlobal.getUserName() + "'," +
                            "'" + cursor.getString(3) + "'," + cursor.getInt(4) + ",'" + cursor.getString(5) + "'," +
                            "" + objGlobal.getUserId() + ",'" + objGlobal.getDeviceName() + "','" + cursor.getString(2) + "'," +
                            "'" + cursor.getString(7) + "','" + cursor.getString(6) + "')", objGlobal.getConnection());
                    if (b_Result) {
                        sqlDB.execSQL("update stocktaking set export='Y' where SrId='" + cursor.getString(7) + "'");
                    } else {
                        return false;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:exportToMainServer:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean deleteMainServer(Context context,String srid) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("deleteMainServer: Connection error");
            return false;
        }
        try {
            b_Result = dbConnection.insertUpdate("insert into stocktakingdel select cast(getdate() as varchar)+', User:" + objGlobal.getUserName() + "',* from stocktaking where srid='" + srid + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            b_Result = dbConnection.insertUpdate("delete from stocktaking where device='" + objGlobal.getDeviceName() + "' and srid='" + srid + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            sqlDB.execSQL("delete from stocktaking where SrId='" + srid + "'");

        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:exportToMainServer:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadScannedCountTotal(){
        try{
            String[] projection={"sum(quantity) as total"};
            Cursor cursor=query(projection,null,null,null,null);
            if (cursor.moveToFirst()) {
                objAgeingStockTakingGlobal.setTotalScan(cursor.getDouble(cursor.getColumnIndex("total")));
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadScannedCountTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadScannedCountExportTotal(){
        try{
            String[] projection={"sum(quantity) as total"};
            Cursor cursor=query(projection,"export='Y'",null,null,null);
            if (cursor.moveToFirst()) {
                objAgeingStockTakingGlobal.setTotalScanExport(cursor.getDouble(cursor.getColumnIndex("total")));
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadScannedCountExportTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean validateForDelete(String delPass){
        if(delPass.isEmpty()){
            objGlobal.setErrorMessage("Please Enter password to delete");
            return false;
        }
        if(!delPass.equals("951357")){
            objGlobal.setErrorMessage("Invalid password");
            return false;
        }
        try{
           /* b_Result = loadScannedCountTotal();
            if (!b_Result) {
                return false;
            }
            b_Result = loadScannedCountExportTotal();
            if (!b_Result) {
                return false;
            }
            if(objAgeingStockTakingGlobal.getTotalScan()!=objAgeingStockTakingGlobal.getTotalScanExport()){
                objGlobal.setErrorMessage("Please export before delete");
                return false;
            }*/
        } catch(Exception ex) {
            objGlobal.setErrorMessage("AgeingStockTakingDbManager:loadScannedCountExportTotal:" + ex.toString());
            return false;
        }
        return true;
    }

}
