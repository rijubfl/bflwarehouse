package com.bflgroup.warehouse.ui.pricecheck;

public class PriceCheckScanDetail {

    public static PriceCheckScanDetail instance;
    private static String itemcode;
    private static String description;
    private static String group;
    private static float oldPrice;
    private static float discPerc;
    private static float price;
    private static int stock;
    private static String itemType;
    private static String department;
    private static String division;
    private static String message;
    private static String boxType;
    public static String getBoxType() {
        return boxType;
    }

    public static void setBoxType(String boxType) {
        PriceCheckScanDetail.boxType = boxType;
    }

    public static String getBoxno() {
        return boxno;
    }

    public static void setBoxno(String boxno) {
        PriceCheckScanDetail.boxno = boxno;
    }

    private static String boxno;

    public static String getItemcode() {
        return itemcode;
    }

    public static void setItemcode(String itemcode) {
        PriceCheckScanDetail.itemcode = itemcode;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        PriceCheckScanDetail.description = description;
    }

    public static String getGroup() {
        return group;
    }

    public static void setGroup(String group) {
        PriceCheckScanDetail.group = group;
    }

    public static float getPrice() {
        return price;
    }

    public static void setPrice(float price) {
        PriceCheckScanDetail.price = price;
    }

    public static String getItemType() {
        return itemType;
    }

    public static int getStock() {
        return stock;
    }

    public static void setStock(int stock) {
        PriceCheckScanDetail.stock = stock;
    }

    public static void setItemType(String itemType) {
        PriceCheckScanDetail.itemType = itemType;
    }

    public static String getDepartment() {
        return department;
    }

    public static void setDepartment(String department) {
        PriceCheckScanDetail.department = department;
    }

    public static String getDivision() {
        return division;
    }

    public static void setDivision(String division) {
        PriceCheckScanDetail.division = division;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        PriceCheckScanDetail.message = message;
    }

    public static float getOldPrice() {
        return oldPrice;
    }

    public static void setOldPrice(float oldPrice) {
        PriceCheckScanDetail.oldPrice = oldPrice;
    }

    public static float getDiscPerc() {
        return discPerc;
    }

    public static void setDiscPerc(float discPerc) {
        PriceCheckScanDetail.discPerc = discPerc;
    }


    public static synchronized PriceCheckScanDetail getInstance() {
        if (instance == null) {
            instance = new PriceCheckScanDetail();
        }
        return instance;
    }
}