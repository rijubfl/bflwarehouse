package com.bflgroup.warehouse.ui.rack;

public class RackInOutGlobal {



        public static synchronized RackInOutGlobal getInstance() {
            if(instance == null){
                instance = new RackInOutGlobal();
            }
            return instance;
        }

        public static void setInstance(RackInOutGlobal instance) {
            instance = instance;
        }
        public static RackInOutGlobal instance;



        public static int getCellNo() {
            return CellNo;
        }

        public static void setCellNo(int cellNo) {
            CellNo = cellNo;
        }
        private static int CellNo;




}
