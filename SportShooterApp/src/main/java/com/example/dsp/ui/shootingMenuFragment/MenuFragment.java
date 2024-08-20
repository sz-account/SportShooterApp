package com.example.dsp.ui.shootingMenuFragment;

import android.bluetooth.BluetoothGattCharacteristic;

import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.GattStatus;

import java.util.ArrayList;

public interface MenuFragment {

    public void characteristicNotify(BluetoothPeripheral peripheral, byte[] value);
    public void onCharacteristicWrite(BluetoothPeripheral peripheral, byte[] value);
}
