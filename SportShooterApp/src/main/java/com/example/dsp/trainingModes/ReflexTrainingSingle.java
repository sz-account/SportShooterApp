package com.example.dsp.trainingModes;
import android.content.Context;
import com.example.dsp.Message;
import com.example.dsp.R;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.TrainingService;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import com.example.dsp.ui.home.HomeFragment;
import com.welie.blessed.BluetoothPeripheral;
import java.util.LinkedList;
import java.util.List;

public class ReflexTrainingSingle implements TrainingMode{

    private final String gameModeCode = "SetGameMode2";
    private final TrainingModeEnum trainingMode = TrainingModeEnum.ReflexSingle;
    private final String description;
    private boolean gameStarted = false;

    // Database
    private List<Hit> hits;
    private final Context context;

    public ReflexTrainingSingle(Context context, List<BluetoothPeripheral> bluetoothPeripheralList)
    {
        description = context.getResources().getString(R.string.reaction_shoot_instruction);
        this.context = context;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String characteristicNotify(BluetoothPeripheral peripheral, byte[] value) {

        if(gameStarted == true) {

            gameStarted = false;

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
        }else {
            return null;
        }
    }

    @Override
    public boolean onCharacteristicWrite(BluetoothPeripheral bluetoothPeripheral, byte[] value) {
        return false;
    }

    @Override
    public void initiateTraining(List<BluetoothPeripheral> bluetoothPeripheralList) {

        if (!gameStarted) {
            HomeFragment.messages.add(new Message(bluetoothPeripheralList.get(0), gameModeCode));
            gameStarted = true;

            hits = new LinkedList<>();
        }
    }

    @Override
    public void saveTraining(TargetSize targetSize, int distance) {

        TrainingService trainingService = new TrainingService(context);
        trainingService.insert(targetSize,distance,trainingMode, 1, hits);
    }
}
