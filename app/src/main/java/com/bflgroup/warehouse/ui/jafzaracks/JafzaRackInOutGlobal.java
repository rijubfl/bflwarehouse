package com.bflgroup.warehouse.ui.jafzaracks;

public class JafzaRackInOutGlobal {



        public static synchronized JafzaRackInOutGlobal getInstance() {
            if(instance == null){
                instance = new JafzaRackInOutGlobal();
            }
            return instance;
        }

        public static void setInstance(JafzaRackInOutGlobal instance) {
            instance = instance;
        }
        public static JafzaRackInOutGlobal instance;



        public static int getCellNo() {
            return CellNo;
        }

        public static void setCellNo(int cellNo) {
            CellNo = cellNo;
        }
        private static int CellNo;




}
