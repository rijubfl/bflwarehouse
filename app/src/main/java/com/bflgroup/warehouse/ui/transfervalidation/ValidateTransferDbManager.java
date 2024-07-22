package com.bflgroup.warehouse.ui.transfervalidation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

public class ValidateTransferDbManager {
    private SQLiteDatabase sqlDB;
    static final String dBName="TRANSFER";
    static final String tableName="GrnTransferItems";
    static final String colItemcode="Itemcode";
    static final String colTrfQty="TrfQty";
    static final String colScanfQty="ScanfQty";
    static final String colDiffQty="DiffQty";
    static final int DBVersion=2;
    static final String createTable="CREATE TABLE "+tableName+"("+colItemcode+" text,"+colScanfQty+" int,"+colTrfQty+" int)";

    static class DatabaseHelperUser extends SQLiteOpenHelper {
        Context context;
        DatabaseHelperUser(Context context){
            super(context,dBName,null,DBVersion);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(createTable);
            Toast.makeText(context,"Table  created",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("drop table if exists "+tableName);
            onCreate(db);
        }
    }

    public ValidateTransferDbManager(Context context){
        DatabaseHelperUser db=new DatabaseHelperUser(context);
        sqlDB=db.getWritableDatabase();
    }

    public long insertData(ContentValues values){
        long id= sqlDB.insert(tableName,"",values);
        return id; //fail 0 or less
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs,String groupBy, String sortOrder){
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(tableName);
        Cursor cursor=qb.query(sqlDB,projection,selection,selectionArgs,groupBy,null,sortOrder);
        return cursor;
    }

    public Cursor queryAll(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(tableName);
        Cursor cursor=qb.query(sqlDB,projection,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    public int delete(String selection,String[] selectionArgs){
        int count=sqlDB.delete(tableName,selection,selectionArgs);
        return count;
    }

    public  int Update(ContentValues values,String Selection,String[] SelectionArgs) {
        int count=sqlDB.update(tableName,values,Selection,SelectionArgs);
        return count;
    }
}
