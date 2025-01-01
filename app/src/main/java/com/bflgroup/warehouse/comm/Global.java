package com.bflgroup.warehouse.comm;

import java.sql.Connection;
import java.util.List;

public class Global {

    public static Global instance;
    private static Connection connection;
    private static Connection cloudCon;
    private static String errorMessage;
    private static String serverIP;
    private static String roboServerIP;
    private static String blackBoxApiServerIP;
    private static String serverUid;
    private static String serverPass;
    private static String dbName;
    private static String cloudDbName;
    private static int userId;
    private static String userName;
    private static String warehouse;
    private static String location;
    private static String countryCode;
    private static String countryDbName;
    private static String exportCountryCode;
    private static String sealPrinterName;
    private static String userPrinterName;
    private static String fcCode;
    private static String deviceName;
    private static boolean enterQty;
    private static boolean hideKeyPad;

    private static String roboChuteStatusAPI;
    private static String roboChuteMapingAPI;
    private static String roboChuteStatusAPIToken;
    private static String roboChuteMapingAPIToken;
    private static String roboSortTaskAPI;
    private static String roboLabelInfoAPI;

    private static String wmsId;
    private static String empCode;
    private static String serverDate;
    private static String serverTime;
    private static String errorNo;
    private static String empName;
    private static String delDate;
    private static String workLocation;
    private static boolean skipBatchIn;
    private static List<String> activeMenuByUser;
    private static String shopName;
    private static String branchLetter;
    private static int maxTotInBin;
    private static String countryWiseBoxPrefix;
    private static String transferPrefixRobo;
    private static String transferPrefixPda;
    private List<String> bluetoothDevices;
    private static String bluetoothDevicesAvailable;

    private static String userAllowMixCategoryBuild;

    public static void setInstance(Global instance) {
        Global.instance = instance;
    }

    public static synchronized Global getInstance() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }

    public static String getCountryDbName() {
        return countryDbName;
    }

    public static void setCountryDbName(String countryDbName) {
        Global.countryDbName = countryDbName;
    }

    public static boolean getSkipBatchIn() {
        return skipBatchIn;
    }

    public static void setSkipBatchIn(boolean skipBatchIn) {
        Global.skipBatchIn = skipBatchIn;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        Global.warehouse = warehouse;
    }

    public static String getCountryCode() {
        return countryCode;
    }

    public static void setCountryCode(String countryCode) {
        Global.countryCode = countryCode;
    }

    public static String getExportCountryCode() {
        return exportCountryCode;
    }

    public static void setExportCountryCode(String exportCountryCode) {
        Global.exportCountryCode = exportCountryCode;
    }

    public static String getBluetoothDevicesAvailable() {
        return bluetoothDevicesAvailable;
    }

    public static void setBluetoothDevicesAvailable(String bluetoothDevicesAvailable) {
        Global.bluetoothDevicesAvailable = bluetoothDevicesAvailable;
    }

    public String getDelDate() {
        return delDate;
    }

    public void setDelDate(String delDate) {
        Global.delDate = delDate;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        Global.empName = empName;
    }

    public String getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(String errorNo) {
        Global.errorNo = errorNo;
    }

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        Global.serverDate = serverDate;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        Global.serverTime = serverTime;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        Global.empCode = empCode;
    }

    public String getWmsId() {
        return wmsId;
    }

    public void setWmsId(String wmsId) {
        Global.wmsId = wmsId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        Global.deviceName = deviceName;
    }

    public String getFcCode() {
        return fcCode;
    }

    public void setFcCode(String fcCode) {
        Global.fcCode = fcCode;
    }

    public String getSealPrinterName() {
        return sealPrinterName;
    }

    public void setSealPrinterName(String sealPrinterName) {
        Global.sealPrinterName = sealPrinterName;
    }

    public boolean getEnterQty() {
        return enterQty;
    }

    public void setEnterQty(boolean enterQty) {
        this.enterQty = enterQty;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getCloudCon() {
        return cloudCon;
    }

    public void setCloudCon(Connection cloudCon) {
        Global.cloudCon = cloudCon;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public static String getServerUid() {
        return serverUid;
    }

    public static void setServerUid(String serverUid) {
        Global.serverUid = serverUid;
    }

    public static String getServerPass() {
        return serverPass;
    }

    public static void setServerPass(String serverPass) {
        Global.serverPass = serverPass;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getHideKeyPad() {
        return hideKeyPad;
    }

    public void setHideKeyPad(boolean hideKeyPad) {
        this.hideKeyPad = hideKeyPad;
    }

    public static String getUserPrinterName() {
        return userPrinterName;
    }

    public static void setUserPrinterName(String userPrinterName) {
        Global.userPrinterName = userPrinterName;
    }

    public String getCloudDbName() {
        return cloudDbName;
    }

    public void setCloudDbName(String cloudDbName) {
        Global.cloudDbName = cloudDbName;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        Global.location = location;
    }

    public List<String> getActiveMenuByUser() {
        return activeMenuByUser;
    }

    public void setActiveMenuByUser(List<String> activeMenuByUser) {
        this.activeMenuByUser = activeMenuByUser;
    }

    public static String getWorkLocation() {
        return workLocation;
    }

    public static void setWorkLocation(String workLocation) {
        Global.workLocation = workLocation;
    }

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        Global.shopName = shopName;
    }

    public static String getBranchLetter() {
        return branchLetter;
    }

    public static void setBranchLetter(String branchLetter) {
        Global.branchLetter = branchLetter;
    }

    public static String getRoboServerIP() {
        return roboServerIP;
    }

    public static void setRoboServerIP(String roboServerIP) {
        Global.roboServerIP = roboServerIP;
    }

    public static int getMaxTotInBin() {
        return maxTotInBin;
    }

    public static void setMaxTotInBin(int maxTotInBin) {
        Global.maxTotInBin = maxTotInBin;
    }

    public List<String> getBluetoothDevices() {
        return bluetoothDevices;
    }

    public void setBluetoothDevices(List<String> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;
    }

    public static String getUserAllowMixCategoryBuild() {
        return userAllowMixCategoryBuild;
    }

    public static String getBlackBoxApiServerIP() {
        return blackBoxApiServerIP;
    }

    public static void setBlackBoxApiServerIP(String blackBoxApiServerIP) {
        Global.blackBoxApiServerIP = blackBoxApiServerIP;
    }

    public static void setUserAllowMixCategoryBuild(String userAllowMixCategoryBuild) {
        Global.userAllowMixCategoryBuild = userAllowMixCategoryBuild;
    }

    public static String getCountryWiseBoxPrefix() {
        return countryWiseBoxPrefix;
    }

    public static void setCountryWiseBoxPrefix(String countryWiseBoxPrefix) {
        Global.countryWiseBoxPrefix = countryWiseBoxPrefix;
    }

    public static String getTransferPrefixRobo() {
        return transferPrefixRobo;
    }

    public static void setTransferPrefixRobo(String transferPrefixRobo) {
        Global.transferPrefixRobo = transferPrefixRobo;
    }

    public static String getTransferPrefixPda() {
        return transferPrefixPda;
    }

    public static void setTransferPrefixPda(String transferPrefixPda) {
        Global.transferPrefixPda = transferPrefixPda;
    }

    public static String getRoboLabelInfoAPI() {
        return roboLabelInfoAPI;
    }

    public static void setRoboLabelInfoAPI(String roboLabelInfoAPI) {
        Global.roboLabelInfoAPI = roboLabelInfoAPI;
    }

    public static String getRoboSortTaskAPI() {
        return roboSortTaskAPI;
    }

    public static void setRoboSortTaskAPI(String roboSortTaskAPI) {
        Global.roboSortTaskAPI = roboSortTaskAPI;
    }

    public static String getRoboChuteMapingAPI() {
        return roboChuteMapingAPI;
    }

    public static void setRoboChuteMapingAPI(String roboChuteMapingAPI) {
        Global.roboChuteMapingAPI = roboChuteMapingAPI;
    }

    public static String getRoboChuteStatusAPI() {
        return roboChuteStatusAPI;
    }

    public static void setRoboChuteStatusAPI(String roboChuteStatusAPI) {
        Global.roboChuteStatusAPI = roboChuteStatusAPI;
    }

    public static String getRoboChuteStatusAPIToken() {
        return roboChuteStatusAPIToken;
    }

    public static void setRoboChuteStatusAPIToken(String roboChuteStatusAPIToken) {
        Global.roboChuteStatusAPIToken = roboChuteStatusAPIToken;
    }

    public static String getRoboChuteMapingAPIToken() {
        return roboChuteMapingAPIToken;
    }

    public static void setRoboChuteMapingAPIToken(String roboChuteMapingAPIToken) {
        Global.roboChuteMapingAPIToken = roboChuteMapingAPIToken;
    }
}