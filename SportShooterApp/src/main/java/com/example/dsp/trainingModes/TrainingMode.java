package com.example.dsp.trainingModes;

import com.example.dsp.trainingData.enums.TargetSize;
import com.welie.blessed.BluetoothPeripheral;
import java.util.List;

public interface TrainingMode {

    public String getDescription();
    public String characteristicNotify(BluetoothPeripheral peripheral, byte[] value);
    public boolean onCharacteristicWrite(BluetoothPeripheral bluetoothPeripheral, byte[] value);
    public void initiateTraining(List<BluetoothPeripheral> bluetoothPeripheralList);
    public void saveTraining(TargetSize targetSize, int distance);
}

