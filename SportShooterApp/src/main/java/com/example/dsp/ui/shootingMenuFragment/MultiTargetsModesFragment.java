package com.example.dsp.ui.shootingMenuFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.dsp.R;
import com.example.dsp.trainingModes.LineTraining;
import com.example.dsp.trainingModes.ReflexTraining;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;


public class MultiTargetsModesFragment extends Fragment implements MenuFragment {

    private ArrayList<BluetoothPeripheral> peripheralsList;
    private TrainingScreenFragment trainingScreenFragment;

    public MultiTargetsModesFragment() {
        // Required empty public constructor
    }

    public MultiTargetsModesFragment(ArrayList<BluetoothPeripheral> peripheralsList) {
        this.peripheralsList = peripheralsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_multi_targets_modes, container, false);


        // --------------------------------------- Buttons -----------------------------------------------
        Button buttonGameModeLine =  inflate.findViewById(R.id.buttonGameModeLine);
        buttonGameModeLine.setOnClickListener(v -> {
            trainingScreenFragment = new TrainingScreenFragment(peripheralsList, new LineTraining(getActivity(), peripheralsList)); // Create Fragment
            getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, // Replace fragment
                    trainingScreenFragment, null).addToBackStack(null).commit();
        });

        Button buttonGameModeReaction =  inflate.findViewById(R.id.buttonGameModeReaction);
        buttonGameModeReaction.setOnClickListener(v -> {
            trainingScreenFragment = new TrainingScreenFragment(peripheralsList, new ReflexTraining(getActivity(), peripheralsList)); // Create Fragment
            getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, // Replace fragment
                    trainingScreenFragment, null).addToBackStack(null).commit();
        });

        return inflate;
    }

    @Override
    public void characteristicNotify(BluetoothPeripheral peripheral, byte[] value)
    {
        if (trainingScreenFragment != null)
        {
            trainingScreenFragment.characteristicNotify(peripheral, value);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothPeripheral peripheral, byte[] value) {
        if (trainingScreenFragment != null)
        {
            trainingScreenFragment.onCharacteristicWrite(peripheral, value);
        }
    }
}