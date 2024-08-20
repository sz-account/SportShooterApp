package com.example.dsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.dsp.trainingModes.LineTraining;
import com.example.dsp.trainingModes.ReflexTraining;
import com.example.dsp.trainingModes.ReflexTrainingSingle;
import com.welie.blessed.BluetoothPeripheral;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class TrainingTest {

    @Test
    public void training_reflexTariningSingleMode() {

        byte[] array = {54,49,51};

        BluetoothPeripheral p1 = mock(BluetoothPeripheral.class);
        List<BluetoothPeripheral> bluetoothPeripheralList = new LinkedList<>();
        bluetoothPeripheralList.add(p1);

        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();

        ReflexTrainingSingle reflexTrainingSingle = new ReflexTrainingSingle(context, bluetoothPeripheralList);

        String response = reflexTrainingSingle.characteristicNotify(p1, array);

        assertNull(response);

        reflexTrainingSingle.initiateGame(bluetoothPeripheralList);

        response = reflexTrainingSingle.characteristicNotify(p1, array);

        assertEquals("0:00:613", response);
    }

    @Test
    public void training_LineTrainingMode() {

        byte[] array = {54,49,51};

        BluetoothPeripheral p1 = mock(BluetoothPeripheral.class);
        BluetoothPeripheral p2 = mock(BluetoothPeripheral.class);
        BluetoothPeripheral p3 = mock(BluetoothPeripheral.class);

        List<BluetoothPeripheral> bluetoothPeripheralList = new LinkedList<>();
        bluetoothPeripheralList.add(p1);
        bluetoothPeripheralList.add(p2);
        bluetoothPeripheralList.add(p3);

        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();

        LineTraining lineTraining = new LineTraining(context, bluetoothPeripheralList);

        lineTraining.initiateGame(bluetoothPeripheralList);

        boolean response = lineTraining.onCharacteristicWrite(p1, array);
        assertFalse(response);

        response = lineTraining.onCharacteristicWrite(p2, array);
        assertFalse(response);

        response = lineTraining.onCharacteristicWrite(p2, array);
        assertFalse(response);

        response = lineTraining.onCharacteristicWrite(p3, array);
        assertTrue(response);

        String response2 = lineTraining.characteristicNotify(p1, array);
        assertNull(response2);

        response2 = lineTraining.characteristicNotify(p2, array);
        assertNull(response2);

        response2 = lineTraining.characteristicNotify(p3, array);
        assertEquals("0:00:613", response2);
    }

    public byte[] longToByteArray(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}