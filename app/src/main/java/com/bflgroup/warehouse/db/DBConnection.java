package com.bflgroup.warehouse.db;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.text.TextUtils;

import com.bflgroup.warehouse.comm.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    Global objGlobal = Global.getInstance();
    String UserName, Password;

    @SuppressLint("NewApi")
    public boolean connectDb() {
        UserName = "BFLPDA";
        Password = "5U83zBc9V$05";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + objGlobal.getServerIP() + ";databaseName=" + objGlobal.getDbName() + ";user=" +
                    objGlobal.getServerUid() + ";password=" + objGlobal.getServerPass() + ";";
            objGlobal.setConnection(DriverManager.getConnection(connectionString));
            return true;
        } catch (SQLException se) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 1 : " + se);
            return false;
        } catch (ClassNotFoundException e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 2 : " + e);
            return false;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 3 : " + e);
            return false;
        }
    }

    @SuppressLint("NewApi")
    public Connection tmpConnectDb(String serverName, String dbName) {
        UserName = "BFLPDA";
        Password = "5U83zBc9V$05";
        Connection con = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + serverName + ";databaseName=" + dbName + ";user=" +
                    UserName + ";password=" + Password + ";";
            con = DriverManager.getConnection(connectionString);
            return con;
        } catch (SQLException se) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 1 : " + se.toString());
            return con;
        } catch (ClassNotFoundException e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 2 : " + e.toString());
            return con;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 3 : " + e.toString());
            return con;
        }
    }

    @SuppressLint("NewApi")
    public boolean connectCloudDb() {
        UserName = "BFL";
        Password = "LFBmct1971";
        if (TextUtils.isEmpty(objGlobal.getCloudDbName())) objGlobal.setCloudDbName("BFLDATA");
        String serverIp = "bfl-db-prod-im.cy4jmc1yawh1.ap-south-1.rds.amazonaws.com";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + serverIp + ";databaseName=" + objGlobal.getCloudDbName() + ";user=" +
                    UserName + ";password=" + Password + ";";
            objGlobal.setCloudCon(DriverManager.getConnection(connectionString));
            return true;
        } catch (SQLException se) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 1 : " + se.toString());
            return false;
        } catch (ClassNotFoundException e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 2 : " + e.toString());
            return false;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.connectDb(" + objGlobal.getServerIP() + ") : error here 3 : " + e.toString());
            return false;
        }
    }

    public boolean insertUpdate(String qry, Connection con) {
        boolean result = false;
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(qry);
            result = true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DBConnection.insertUpdate : " + ex.toString() + ", " + qry);
            return false;
        }
        return result;
    }

    public int insertUpdateInt(String qry, Connection con) {
        int result = 0;
        try {
            Statement stmt = con.createStatement();
            result = stmt.executeUpdate(qry);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DBConnection.insertUpdate : " + ex.toString() + ", " + qry);
            return result;
        }
        return result;
    }

    public boolean checkConnectionClosed() {
        try {
            if (objGlobal.getConnection().isClosed()) return false;
            if (objGlobal.getConnection() == null) return false;
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.checkConnectionClosed : " + e.toString());
            return false;
        }
    }

    public boolean checkCloudConnectionClosed() {
        try {
            if (objGlobal.getCloudCon().isClosed()) return false;
            if (objGlobal.getCloudCon() == null) return false;
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.checkCloudConnectionClosed : " + e.toString());
            return false;
        }
    }

    public ResultSet getResultSet(String query, Connection con) {
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            return rs;
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.getResultSet : " + e.toString());
            return null;
        }
    }

    public String stringReturn(Connection con, String tblName, String returnField, String searchField, String valu) {
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select " + returnField + " from " + tblName + " where " + searchField + "='" + valu + "'");
            if (rs.next())
                return rs.getString(returnField).toString();
            else
                return "";
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.stringReturn : " + e.toString());
            return "";
        }
    }

    public boolean getServerDateTime(Connection con) {
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select dt=convert(varchar,getdate(),103),tm=convert(varchar,getdate(),8)");
            if (rs.next()) {
                objGlobal.setServerDate(rs.getString("dt"));
                objGlobal.setServerTime(rs.getString("tm"));
                return true;
            } else {
                objGlobal.setErrorMessage("getServerDateTime : Date not found");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("getServerDateTime : Try : " + e.toString());
            return false;
        }
    }
}
