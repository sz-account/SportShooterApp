package com.example.dsp;

import android.bluetooth.BluetoothGattCharacteristic;

import com.welie.blessed.BluetoothPeripheral;

public class Message {

    public BluetoothPeripheral peripheral;
    public String message;
    public MessageStatus messageStatus = MessageStatus.NEW;
    public int resendTimer = 0;

    public Message(BluetoothPeripheral peripheral, String message) {

        this.peripheral = peripheral;
        this.message = message;
    }
}
