package com.bflgroup.warehouse.ui.logout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bflgroup.warehouse.R;
import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    private Global objGlobal = Global.getInstance();
    DBConnection dbConnection = new DBConnection();

    private ResultSet rs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        //this.finish();

        String query = "select * from bfldata..LoginUserPda where Username = '" + objGlobal.getUserName()+"'  and Pdadevicename = '"+ objGlobal.getDeviceName() +"' and Active = 'Y'";
        rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        try {
            if (rs.next()) {
                dbConnection.insertUpdate("delete from bfldata.dbo.LoginUserPda where Username = '" + objGlobal.getUserName()+"' and Pdadevicename = '"+ objGlobal.getDeviceName() +"' and Active = 'Y'", objGlobal.getConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
        return view;
    }
}