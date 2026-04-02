package com.bflgroup.warehouse.ui.binstoragewavepick;

public class BinStorageWavePickGlobal {

    public static BinStorageWavePickGlobal instance;
    private static String wavePercentage;
    private static String waveRemarks;

    public static synchronized BinStorageWavePickGlobal getInstance() {
        if(instance == null){
            instance = new BinStorageWavePickGlobal();
        }
        return instance;
    }

    public static String getWavePercentage() {
        return wavePercentage;
    }

    public static void setWavePercentage(String wavePercentage) {
        BinStorageWavePickGlobal.wavePercentage = wavePercentage;
    }

    public static String getWaveRemarks() {
        return waveRemarks;
    }

    public static void setWaveRemarks(String waveRemarks) {
        BinStorageWavePickGlobal.waveRemarks = waveRemarks;
    }
}
