package com.bflgroup.warehouse.ui.validatetoteid;

import android.content.Context;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.factorybuildbox.BuildBoxGlobal;
import com.bflgroup.warehouse.ui.factorybuildbox.BuildBoxShared;

import java.sql.ResultSet;

public class ValidateToteControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BuildBoxGlobal objBuildBoxGlobal = BuildBoxGlobal.getInstance();
    private ResultSet rs;
    private boolean b_Result;
    BuildBoxShared ObjBuildBoxShared;

    public ValidateToteControl() {
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


    public String checkTote(String toteId, Context context){
        String boxno = "";
        try {
            rs = dbConnection.getResultSet("select * from bfldata..BlueToteIDMaster where ToteID = '"+toteId.trim()+"'", objGlobal.getConnection());
            if (rs.next()) {
                rs = dbConnection.getResultSet("select boxno from usa.dbo.upcboxhead where ToteID='" + toteId + "' and Closed='N'", objGlobal.getConnection());
                if (rs.next()) {
                    //  objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("boxno").toString());
                    objGlobal.setErrorMessage("Invalid box or box is opened - " + rs.getString("boxno"));
                    boxno = rs.getString("boxno");
                } else {
                    rs = dbConnection.getResultSet("select distinct a.boxno from bfldata.dbo.TCMBoxes a,bfldata.dbo.TcmboxesHeader b where a.BoxNo=b.Boxno " +
                            "and b.TotId='" + toteId + "' and a.Closed='N'", objGlobal.getConnection());
                    if (rs.next()) {
                        // objBinPutAwayMultipleToteGlobal.setBoxNo(rs.getString("boxno").toString());
                        objGlobal.setErrorMessage("Invalid box or box is opened - " + rs.getString("boxno"));
                        boxno = rs.getString("boxno");

                    } else {
                        boxno = "";
                    }
                }
            }
            else{
                boxno = "INVALID";
            }
        }catch(Exception e){
            objGlobal.setErrorMessage(e.getMessage());
        }
        return boxno;
    }

//    public String checkTote(String toteid, Context context){
//        String msg = "";
//        try {
//            if (!checkConnection()) {
//                return "";
//            }
//            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
//                objGlobal.setErrorNo("transferReceipt:007");
//            }
//            rs = dbConnection.getResultSet("select * from bfldata..BlueToteIDMaster where ToteID = '"+toteid.trim()+"'", objGlobal.getConnection());
//            if (rs.next()){
//                ResultSet rs1 = dbConnection.getResultSet("select * from usa..upcBoxHead where ToteID ='"+toteid.trim()+"' and closed = 'N'", objGlobal.getConnection());
//                if(!rs1.next()){
//                    ResultSet rs2 = dbConnection.getResultSet("select * from bfldata..TcmboxesHeader where boxno in (select boxno from bfldata..tcmboxes where TotId = '"+toteid.trim()+"' and closed = 'Y') ", objGlobal.getConnection());
//                    if(!rs2.next()){
//                        okMessage("Alert", "Toteid is opened - "  +toteid, context);
//                        msg = "Toteid is opened - "  +toteid;
//                    }
//                    else{
//                      //  okMessage("Alert", "Box is closed - "  +toteid, context);
//                        msg = "Toteid is closed - "  +toteid;
//                    }
//                    okMessage("Alert", "Box is closed - "  +toteid, context);
//                  //  msg = "Box is closed - "  +toteid;
//                }else{
//                    okMessage("Alert", "This Toteid - " +toteid + " is opened", context);
//                    msg = "This Toteid - " +toteid + " is opened. Box no is - "+rs1.getString("Boxno");
//
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//       // okMessage("Alert","This item - " +toteid + " is not valid",context);
//        return msg;
//    }

    void okMessage(String title, String message, Context context) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }




}
