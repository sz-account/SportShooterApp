package com.example.dsp.trainingModes;

import android.content.Context;
import com.example.dsp.Message;
import com.example.dsp.R;
import com.example.dsp.trainingData.AppDatabase;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.Training;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import com.example.dsp.ui.home.HomeFragment;
import com.example.dsp.ui.shootingMenuFragment.DeviceStatus;
import com.welie.blessed.BluetoothPeripheral;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LineTraining  implements TrainingMode{

    private final String gameModeCode = "SetGameMode3";
    private final TrainingModeEnum trainingName = TrainingModeEnum.Line;
    private final String description;

    List<BluetoothPeripheral> bluetoothPeripheralList;
    private List<DeviceStatus> deviceStatusArrayList = new ArrayList<>();

    private int hitsCount = 0;
    private boolean gameStarted = false;
    private boolean gameInitializing = false;

    AppDatabase instance;
    private Training training;
    private List<Hit> hits;
    private long trainingId;

    public LineTraining(Context context, List<BluetoothPeripheral> bluetoothPeripheralList)
    {
        this.bluetoothPeripheralList = bluetoothPeripheralList;
        this.description = context.getResources().getString(R.string.line_training_description);
        instance = AppDatabase.getInstance(context);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String characteristicNotify(BluetoothPeripheral peripheral, byte[] value) {

        hitsCount ++;

        String tmp = new String(value);
        long durationInMillis = Long.parseLong(tmp);

        Hit hit = new Hit();
        hit.time = durationInMillis;
        hit.trainingId = trainingId;
        hits.add(hit);

        if (hitsCount == bluetoothPeripheralList.size())
        {
            gameStarted = false;
            gameInitializing = false;
            hitsCount = 0;
            long minutes = (durationInMillis / 1000) / 60;
            long seconds = (durationInMillis / 1000) % 60;
            long milliseconds = durationInMillis % 1000;
            String formattedTime = String.format("%d:%02d:%03d", minutes, seconds, milliseconds);


            for (int i = 0; i < bluetoothPeripheralList.size(); i++) {

                HomeFragment.messages.add(new Message(bluetoothPeripheralList.get(i), "End"));
                deviceStatusArrayList.set(i, DeviceStatus.Awaiting);
            }

            return formattedTime;
        }

        return null;
    }

    @Override
    public boolean onCharacteristicWrite(BluetoothPeripheral bluetoothPeripheral, byte[] value) {

        String tmp = new String(value);

        if (!gameStarted && !tmp.equals("End") ) {

            int index = bluetoothPeripheralList.indexOf(bluetoothPeripheral);
            deviceStatusArrayList.set(index, DeviceStatus.Ready);

            gameStarted = true;

            for (DeviceStatus v: deviceStatusArrayList) {
                if (v == DeviceStatus.Awaiting)
                    gameStarted = false;
            }
        }

        return gameStarted;
    }

    @Override
    public void initiateTraining(List<BluetoothPeripheral> bluetoothPeripheralList) {

        if (!gameInitializing) {

            gameInitializing = true;

            for (BluetoothPeripheral peripheral: bluetoothPeripheralList) {

                HomeFragment.messages.add(new Message(peripheral, gameModeCode));
                deviceStatusArrayList.add(DeviceStatus.Awaiting);
            }

            // Database
            trainingId = instance.trainingDao().getRowCount()  + 1;
            hits = new LinkedList<>();
        }
    }

    @Override
    public void saveTraining(TargetSize targetSize, int distance) {

        training = new Training();
        training.date = new Date();
        training.targetSize = targetSize;
        training.distance = distance;
        training.trainingMode = trainingName;
        training.targets = bluetoothPeripheralList.size();

        instance.trainingWithHitsDao().insertTrainingWithHits(training, hits);
    }
}
