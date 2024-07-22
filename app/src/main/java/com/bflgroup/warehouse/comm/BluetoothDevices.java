package com.bflgroup.warehouse.comm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDevices {

    private Global objGlobal = Global.getInstance();

    public boolean loadBluetoothDevicesArray() {
        List<String> arr;
        arr=new ArrayList<String>();
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    arr.add("--Select--");
                    for (BluetoothDevice device : pairedDevices) {
                        arr.add(device.getAddress());
                    }
                } else {
                    objGlobal.setErrorMessage("No device paired");
                    return false;
                }
            } else {
                objGlobal.setErrorMessage("BluetoothAdapter.STATE_ON");
                return false;
            }
        } else {
            objGlobal.setErrorMessage("Please Enable Bluetooth");
            return false;
        }
        objGlobal.setBluetoothDevices(arr);
        return true;
    }

    private static String bluetoothDeviceTOMacFind(String selectedDevice, Context context) {
        String DeviceAddress = null;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth Connection Failed", Toast.LENGTH_LONG).show();
            Log.e("Bluetooth ", "not found");
        }
        if (mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String devicename = device.getName();
                    if (devicename.equals(selectedDevice)) {
                        DeviceAddress = device.getAddress();
                    }
                }
            }
        }
        return DeviceAddress;
    }


}
