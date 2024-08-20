package com.example.dsp.ui.shootingMenuFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.dsp.R;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingModes.TrainingMode;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.welie.blessed.BluetoothPeripheral;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class TrainingScreenFragment extends Fragment {


    private ArrayList<BluetoothPeripheral> peripheralsList;
    private TrainingMode trainingMode;
    private TargetSize targetSize = TargetSize.Medium;
    private EditText textField;

    // ------------------------ Timer Stuff ---------------------------------------
    private TextView textViewTimer;
    private boolean running;
    private long time = System.currentTimeMillis();

    public TrainingScreenFragment() {
        // Required empty public constructor
    }

    public TrainingScreenFragment(ArrayList<BluetoothPeripheral> peripheralsList, TrainingMode trainingMode) {
        this.peripheralsList = peripheralsList;
        this.trainingMode = trainingMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_game_screen, container, false);

        TextView description = inflate.findViewById(R.id.textViewGameModeDescription);
        description.setText(trainingMode.getDescription());

        textViewTimer = inflate.findViewById(R.id.textViewTimer);

        TextInputLayout textInputLayout = inflate.findViewById(R.id.textField);
        textField = textInputLayout.getEditText();


        Button buttonStart = inflate.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> {
            trainingMode.initiateTraining(peripheralsList);
        });


        MaterialButtonToggleGroup toggleButtonGroup = inflate.findViewById(R.id.toggleButton);
        toggleButtonGroup.check(R.id.buttonMedium);
        toggleButtonGroup.addOnButtonCheckedListener((toggleButton, checkedId, isChecked) -> {
            switch (checkedId) {
                case R.id.buttonLarge:
                    targetSize = TargetSize.Large;
                    break;
                case R.id.buttonMedium:
                    targetSize = TargetSize.Medium;
                    break;
                case R.id.buttonSmall:
                    targetSize = TargetSize.Small;
                    break;
            }

        });

        runTimer();

        return inflate;
    }

    public void onCharacteristicWrite(BluetoothPeripheral bluetoothPeripheral, byte[] value) // Zacznij grę jeżeli wszystke urządzenia są ready
    {
        boolean start = trainingMode.onCharacteristicWrite(bluetoothPeripheral, value);

        if (start)
            startTimer();
    }

    public void characteristicNotify(BluetoothPeripheral peripheral, byte[] value)
    {
        String time = trainingMode.characteristicNotify(peripheral, value);

        if (time != null)
        {
            stopTimer();
            textViewTimer.setText(time);
            int number = Integer.parseInt(textField.getText().toString());
            trainingMode.saveTraining(targetSize, number);
        }
    }



    // --------------------------- TIMER ----------------------------------
    private void startTimer()
    {
        running = true;
        time = System.currentTimeMillis();
    }

    private void stopTimer()
    {
        running = false;
    }

    private String getTimeString()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS", Locale.ENGLISH);
        return formatter.format(System.currentTimeMillis() - time);
    }


    private void runTimer() {

        // Creates a new Handler
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run() {


                if (running) {
                    textViewTimer.setText(getTimeString() );
                }

                handler.postDelayed(this, 10);
            }
        });
    }
}