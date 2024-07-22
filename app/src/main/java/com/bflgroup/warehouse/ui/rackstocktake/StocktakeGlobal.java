package com.bflgroup.warehouse.ui.rackstocktake;

import com.bflgroup.warehouse.ui.binstorageputawaymultipletote.BinPutAwayMultipleToteGlobal;

public class StocktakeGlobal {


        public static synchronized StocktakeGlobal getInstance() {
            if(instance == null){
                instance = new StocktakeGlobal();
            }
            return instance;
        }

        public static void setInstance(StocktakeGlobal instance) {
            instance = instance;
        }
        public static StocktakeGlobal instance;
    private static String boxNo;
    private static String Palletno1;
    private static String Palletno2;


        public static int getCellNo() {
            return CellNo;
        }

    public static String getBoxNo() {
        return boxNo;
    }

    public static void setBoxNo(String boxNo) {
        StocktakeGlobal.boxNo = boxNo;
    }

    public static String getPalletNo1() {
        return Palletno1;
    }

    public static String getPalletNo2() {
        return Palletno2;
    }

    public static void setPalletNo1(String Palletno1) {
        StocktakeGlobal.Palletno1 = Palletno1;
    }

    public static void setPalletNo2(String Palletno2) {
        StocktakeGlobal.Palletno2 = Palletno2;
    }

        public static void setCellNo(int cellNo) {
            CellNo = cellNo;
        }
        private static int CellNo;



}
