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
import com.welie.blessed.BluetoothPeripheral;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FiveShotTraining implements TrainingMode{

    private final String gameModeCode = "SetGameMode1";
    private final TrainingModeEnum trainingMode = TrainingModeEnum.FiveShoot;

    private AppDatabase instance;
    private Training training;
    private List<Hit> hits;

    private String description;
    private boolean trainingStarted = false;

    public FiveShotTraining(Context context, List<BluetoothPeripheral> bluetoothPeripheralList)
    {
        description = context.getResources().getString(R.string.five_shoots_description);
        instance = AppDatabase.getInstance(context);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String characteristicNotify(BluetoothPeripheral peripheral, byte[] value) {

        trainingStarted = false;

        String tmp = new String(value);
        long durationInMillis = Long.parseLong(tmp); // example duration in milliseconds
        long minutes = (durationInMillis / 1000) / 60;
        long seconds = (durationInMillis / 1000) % 60;
        long milliseconds = durationInMillis % 1000;
        String formattedTime = String.format("%d:%02d:%03d", minutes, seconds, milliseconds);

        Hit hit = new Hit();
        hit.time = durationInMillis;
        hits.add(hit);

        return formattedTime;
    }

    @Override
    public boolean onCharacteristicWrite(BluetoothPeripheral bluetoothPeripheral, byte[] value) {
        return true;
    }

    @Override
    public void initiateTraining(List<BluetoothPeripheral> bluetoothPeripheralList) {
        if (!trainingStarted) {
            HomeFragment.messages.add(new Message(bluetoothPeripheralList.get(0), gameModeCode));
            trainingStarted = true;

            hits = new LinkedList<>();
        }
    }

    @Override
    public void saveTraining(TargetSize targetSize, int distance) {

        training = new Training();
        training.date = new Date();
        training.targetSize = targetSize;
        training.distance = distance;
        training.trainingMode = trainingMode;
        training.targets = 1;

        instance.trainingWithHitsDao().insertTrainingWithHits(training, hits);
    }
}
