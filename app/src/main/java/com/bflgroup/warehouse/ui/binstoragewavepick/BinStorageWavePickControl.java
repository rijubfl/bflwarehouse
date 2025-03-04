package com.bflgroup.warehouse.ui.binstoragewavepick;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.db.DBConnection;
import com.bflgroup.warehouse.ui.binstorageputaway.BinPutAwayGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BinStorageWavePickControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private BinPutAwayGlobal objBinPutAwayGlobal = BinPutAwayGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public BinStorageWavePickControl() {
        objGlobal.setDbName("RACKS");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("BinStorageWavePickControl : Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        objGlobal.setDbName("RACKS");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("BinStorageWavePickControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    List<String> loadBinStorageWavePickRack() {
        List<String> arr;
        arr = new ArrayList<String>();
        try {
            /*if (!dbConnection.insertUpdate("create table #zones(zoneid varchar(25))", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("insert into #zones select distinct d.Zones from tempdata.dbo.SIMProdReadyPalletsList a,usa.dbo.UPCBoxHead b,racks.dbo.BinRack c,racks.dbo.BinRackMaster d " +
                    "where a.BoxNo=b.BoxNo and b.Closed='N' and b.ToteID=c.ToteId and c.Location=d.Barcode and c.Location not in(select rack from racks.dbo.SkipWavePick where fix='N') ", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("insert into #zones select distinct d.Zones from tempdata.dbo.SIMProdReadyPalletsList a,BFLDATA.dbo.TcmboxesHeader b,racks.dbo.BinRack c,racks.dbo.BinRackMaster d " +
                    "where a.BoxNo=b.BoxNo and b.TotId=c.ToteId and c.Location=d.Barcode and c.Location not in(select rack from racks.dbo.SkipWavePick where fix='N') ", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("insert into #zones select distinct d.Zones from tempdata.dbo.SIMProdReadyPalletsList a," + objGlobal.getCountryDbName() + ".dbo.TransferHeader b,racks.dbo.BinRack c," +
                    "racks.dbo.BinRackMaster d where a.BoxNo=b.TrfNo and b.StoreIssue=c.ToteId and c.Location=d.Barcode and c.Location not in(select rack from racks.dbo.SkipWavePick where fix='N')", objGlobal.getConnection())) {
                return null;
            }
            rs = dbConnection.getResultSet("select distinct zoneid from #zones order by zoneid", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("zoneid").toString());
            }
            if (!dbConnection.insertUpdate("drop table #zones", objGlobal.getConnection())) {
                return null;
            }*/

            //01/11/2024
            rs = dbConnection.getResultSet("select distinct Zones from RACKS.dbo.BinRackMaster where Barcode in(select distinct Rack from " +
                    "tempdata.dbo.SIMProdReadyPalletsList where Rack<>'' and BoxNo not in(select ToteId from racks.dbo.SkipWavePick where Fix='N' union all " +
                    "select ToteId=BoxNo from racks.dbo.SkipWavePick where Fix='N')) and ISNULL(Zones,'')<>''", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("Zones"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickRackTicket:loadBinStorageWavePickRack:" + ex);
            return null;
        }
        return arr;
    }

    ArrayList<BinStorageWavePickTicket> loadBinStorageWaveDetails(String zoneId, String pickType, String div) {
        ArrayList<BinStorageWavePickTicket> listBinStorageWavePickTicket = new ArrayList<BinStorageWavePickTicket>();
        String order = "";
        try {
            listBinStorageWavePickTicket.clear();
            String validTy = "";
            if (!pickType.equals("ALL")) {
                validTy = "and CheckingType='" + pickType + "' ";
            }
            if (!div.equals("ALL")) {
                validTy = "and iDepartment='" + div + "' ";
            }
            if (!dbConnection.insertUpdate("create table #simBoxPick(ToteId varchar(20),BoxNo varchar(20),BoxPerc varchar(20),Location varchar(20),Text varchar(20),Color varchar(20),PickOrder varchar(20)," +
                    "Zones varchar(20),DoubleDeep int,CheckingType varchar(20),Vertical varchar(5),Horizontal varchar(5),iDepartment varchar(150))", objGlobal.getConnection())) {
                return null;
            }
            if (pickType.equals("ALL WINTER")) {
                if (!dbConnection.insertUpdate("insert into #simBoxPick(BoxNo,BoxPerc,CheckingType,iDepartment) select distinct BoxNo,BoxPerc=0,CheckingType='ALL WINTER',iDepartment='' from racks.dbo.BinRack where " +
                        "ToteId in(select ToteID from usa.dbo.UPCBoxHead where Closed='N' and PalletType in('RW') and isnull(ToteID,'')<>'')", objGlobal.getConnection())) {
                    return null;
                }
                if (!dbConnection.insertUpdate("insert into #simBoxPick(BoxNo,BoxPerc,CheckingType,iDepartment) select distinct BoxNo,BoxPerc=0,CheckingType='ALL WINTER',iDepartment='' from racks.dbo.BinRack where " +
                        "ToteId in(select distinct totid from bfldata.dbo.vR1Pallet where closed='N' and pallettype in('RW') and isnull(totid,'')<>'')", objGlobal.getConnection())) {
                    return null;
                }
            } else {
                if (!dbConnection.insertUpdate("insert into #simBoxPick(BoxNo,BoxPerc,CheckingType,iDepartment) select distinct BoxNo,BoxPerc=CONVERT(DECIMAL(10,2),BoxPerc),CheckingType,iDepartment from tempdata.dbo.SIMProdReadyPalletsList ", objGlobal.getConnection())) {
                    return null;
                }
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set ToteId='',Location='',Text='',Color='',PickOrder='',Zones='',DoubleDeep=''", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set ToteId=b.ToteID from #simBoxPick a,usa.dbo.upcboxhead b where a.BoxNo=b.BoxNo and b.Closed='N'", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set ToteId=c.TotId,CheckingType='TCM' from #simBoxPick a,bfldata.dbo.TCMBoxes b,BFLDATA.dbo.TcmboxesHeader c where a.BoxNo=b.BoxNo and a.BoxNo=c.Boxno and b.Closed='N'", objGlobal.getConnection())) {
                return null;
            }
            if (!objGlobal.getWorkLocation().equals("UAE")) {
                if (!dbConnection.insertUpdate("update #simBoxPick set ToteId=b.StoreIssue from #simBoxPick a," + objGlobal.getCountryDbName() + ".dbo.TransferHeader b where a.BoxNo=b.TrfNo", objGlobal.getConnection())) {
                    return null;
                }
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set ToteId=BoxNo where isnull(ToteId,'')=''", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set Location=b.Location from #simBoxPick a,RACKS.dbo.BinRack b where a.ToteId=b.ToteId", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set Location=b.Location from #simBoxPick a,RACKS.dbo.BinRack b where a.BoxNo=b.ToteId", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("update #simBoxPick set Text=b.Text,Color=B.Color,PickOrder=B.PickOrder,Zones=B.Zones,DoubleDeep=B.DoubleDeep,Vertical=B.Vertical,Horizontal=B.Horizontal from " +
                    "#simBoxPick a,racks.dbo.BinRackMaster b where a.Location=b.Barcode", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("delete from #simBoxPick where Location=''", objGlobal.getConnection())) {
                return null;
            }
            if (pickType.equals("ALL WINTER")) {
                if (!dbConnection.insertUpdate("delete from #simBoxPick where ToteId not like 'B%'", objGlobal.getConnection())) {
                    return null;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from #simBoxPick where ToteId in(select ToteId from SkipWavePick where fix='N')", objGlobal.getConnection())) {
                    return null;
                }
                if (!dbConnection.insertUpdate("delete from #simBoxPick where BoxNo in(select BoxNo from SkipWavePick where fix='N')", objGlobal.getConnection())) {
                    return null;
                }
            }
            if (!dbConnection.insertUpdate("delete from #simBoxPick where isnull(Zones,'')=''", objGlobal.getConnection())) {
                return null;
            }
            order = "Vertical,Horizontal,PickOrder,DoubleDeep";
            if (objGlobal.getWorkLocation().equals("KSA")) order = "Location";
            int rowno = 0;
            rs = dbConnection.getResultSet("select ToteId,BoxNo,BoxPerc,Location,Text,Color,PickOrder,Zones,DoubleDeep,CheckingType,rowNo=(ROW_NUMBER() OVER(ORDER BY " + order + ")) " +
                    "from #simBoxPick where Zones='" + zoneId + "' " + validTy + " order by " + order, objGlobal.getConnection());
            while (rs.next()) {
                rowno++;
                listBinStorageWavePickTicket.add(new BinStorageWavePickTicket(rs.getString("ToteId").toString().toUpperCase(),
                        rs.getString("BoxNo").toString(), rs.getString("BoxPerc").toString(),
                        rs.getString("Text").toString(), rs.getString("Color").toString(),
                        rs.getString("PickOrder").toString(), rs.getString("Zones").toString(),
                        rs.getString("DoubleDeep").toString(), rs.getString("rowNo").toString(),
                        rs.getString("CheckingType").toString(), rs.getString("Location").toString()));
            }
            if (!dbConnection.insertUpdate("drop table #simBoxPick", objGlobal.getConnection())) {
                return null;
            }

        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickControl:loadBinStorageWaveDetails:" + ex);
            return null;
        }
        return listBinStorageWavePickTicket;
    }

    public boolean skipWavePick(String location) {
        try {
            if (!dbConnection.insertUpdate("insert into SkipWavePick (Warehouse,ToteId,BoxNo,Rack,UserId,UserName,TrnDate,Fix) select Warehouse,ToteId,BoxNo," +
                    "location," + objGlobal.getUserId() + ",'" + objGlobal.getUserName() + "',getdate(),'N' from RACKS.dbo.BinRack where Location='" + location + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:ex:" + ex.toString());
            return false;
        }
    }

    List<String> loadPickType() {
        List<String> arr;
        arr = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        try{
            arr.add("ALL");
            rs = dbConnection.getResultSet("select distinct CheckingType from tempdata.dbo.SIMProdReadyPalletsList where CheckingType<>'' order by CheckingType", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("CheckingType").toString());
            }
            arr.add("ALL WINTER");
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickControl:loadPickType:" + ex.toString());
            return null;
        }
        return arr;
    }

    List<String> loadPickDivision() {
        List<String> arr;
        arr = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        try{
            arr.add("ALL");
            rs = dbConnection.getResultSet("select distinct iDepartment from TEMPDATA.dbo.SIMProdReadyPalletsList where iDepartment<>'' order by iDepartment", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("iDepartment").toString());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinStorageWavePickControl:loadPickDivision:" + ex.toString());
            return null;
        }
        return arr;
    }


}
