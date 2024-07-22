package com.bflgroup.warehouse.ui.checkpallets;

import android.content.Context;
import android.util.Log;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CheckPalletsControl {


    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();

    private ResultSet rs;
    private boolean b_Result;

    public CheckPalletsControl(Context context) {
        objGlobal.setDbName("BFLDATA");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("BuildBoxControl : Connection error");
        }

    }


    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("BFLDATA");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BuildBoxControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }



    public ArrayList<String>loadRackhistoryTechno() {
        ArrayList<String> arrayList = new ArrayList<>();

        try {
            String query = "select distinct rowno from racks..TechnoRackDet";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(rs.getString("rowno"));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }
    public ArrayList<String>loadRackhistory() {
        ArrayList<String> arrayList = new ArrayList<>();

        try {
            String query = "select distinct rowno from racks..warehouseRackDet";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(rs.getString("rowno"));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }
    public ArrayList<String>loadRackhistoryJafza() {
        ArrayList<String> arrayList = new ArrayList<>();

        try {
            String query = "select distinct rowno from racks..tmpwhracks";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(rs.getString("rowno"));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }

    public ArrayList<CheckPalletsItems>loadpalletstechno(String racks) {
        ArrayList<CheckPalletsItems> arrayList = new ArrayList<>();

        try {
            String query = "select * from racks..TechnoRackDet where rowno = '"+racks+"' order by cellno";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }

    public String loadpalletscount(String racks, String warehouse) {
        String Count = "";

        try {
            if(!dbConnection.insertUpdate("Drop table #temp", objGlobal.getConnection())){
                /// Log.e("Error",e.toString());
            }
            if(warehouse.equals("TECHNO")) {
                if (!dbConnection.insertUpdate("Drop table #temp", objGlobal.getConnection())) {
                    /// Log.e("Error",e.toString());
                }
                if (!dbConnection.insertUpdate("create table #temp (palletno varchar(50))", objGlobal.getConnection()))
                    ;
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno1 from racks..TechnoRackDet where rowno = '" + racks + "'", objGlobal.getConnection()))
                    ;
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno2 from racks..TechnoRackDet where rowno = '" + racks + "'", objGlobal.getConnection()))
                    ;


                String query = "select Count = count(distinct palletno) from #temp";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    // arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
                    Count = rs.getString("Count");
                }
            }else if(warehouse.equals("JAFZA")){
                if (!dbConnection.insertUpdate("Drop table #temp", objGlobal.getConnection())) {
                    /// Log.e("Error",e.toString());
                }
                if (!dbConnection.insertUpdate("create table #temp (palletno varchar(50))", objGlobal.getConnection()));
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno1 from racks..tmpwhracks where rowno = '" + racks + "'", objGlobal.getConnection()));                    ;
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno2 from racks..tmpwhracks where rowno = '" + racks + "'", objGlobal.getConnection()));


                String query = "select Count = count(distinct palletno) from #temp";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    // arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
                    Count = rs.getString("Count");
                }
            }else{
                if (!dbConnection.insertUpdate("Drop table #temp", objGlobal.getConnection())) {
                    /// Log.e("Error",e.toString());
                }
                if (!dbConnection.insertUpdate("create table #temp (palletno varchar(50))", objGlobal.getConnection()));
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno1 from racks..WarehouseRackDet where rowno = '" + racks + "'", objGlobal.getConnection())) ;
                if (!dbConnection.insertUpdate("insert into #temp select distinct palletno2 from racks..WarehouseRackDet where rowno = '" + racks + "'", objGlobal.getConnection())) ;


                String query = "select Count = count(distinct palletno) from #temp";
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    // arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
                    Count = rs.getString("Count");
                }
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }

        return Count;
    }

    public ArrayList<CheckPalletsItems>loadpalletsJafza(String racks) {
        ArrayList<CheckPalletsItems> arrayList = new ArrayList<>();

        try {
            String query = "select rowno,cellno, trndate='', palletNo1, palletNo2 from racks..tmpwhracks where rowno = '"+racks+"' order by cellno";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }
    public ArrayList<CheckPalletsItems>loadpallets(String racks) {
        ArrayList<CheckPalletsItems> arrayList = new ArrayList<>();

        try {
            String query = "select rowno,cellno, trndate='', palletNo1, palletNo2 from racks..WarehouseRackDet where rowno = '"+racks+"' order by cellno";
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            while (rs.next()) {
                arrayList.add(new CheckPalletsItems (rs.getString("rowno") +"-"+rs.getString("cellno"),rs.getString("TrnDate"),rs.getString("palletNo1"),rs.getString("palletNo2") ));
            }
        }catch(Exception e){
         Log.e("Error",e.toString());
        }


        return arrayList;
    }
}
