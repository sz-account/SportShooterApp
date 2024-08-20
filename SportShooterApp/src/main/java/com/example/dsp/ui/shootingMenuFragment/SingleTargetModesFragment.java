package com.example.dsp.ui.shootingMenuFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dsp.R;
import com.example.dsp.trainingModes.FiveShotTraining;
import com.example.dsp.trainingModes.ReflexTrainingSingle;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;


public class SingleTargetModesFragment extends Fragment implements MenuFragment {

    private ArrayList<BluetoothPeripheral> peripheralsList;
    private TrainingScreenFragment trainingScreenFragment;

    public SingleTargetModesFragment() {
        // Required empty public constructor
    }

    public SingleTargetModesFragment(ArrayList<BluetoothPeripheral> peripheralsList) {
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
        View inflate = inflater.inflate(R.layout.fragment_single_target_modes, container, false);


        // --------------------------------------- Buttons -----------------------------------------------
        Button buttonGameMode5Shoots =  inflate.findViewById(R.id.buttonGameMode5Shoots);
        buttonGameMode5Shoots.setOnClickListener(v -> {

            trainingScreenFragment = new TrainingScreenFragment(peripheralsList, new FiveShotTraining(getActivity(), peripheralsList)); // Create Fragment
            getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, // Replace fragment
                    trainingScreenFragment, null).addToBackStack(null).commit();
        });

        Button buttonGameModeReactionShoot =  inflate.findViewById(R.id.buttonGameModeReactionShoot);
        buttonGameModeReactionShoot.setOnClickListener(v -> {

            trainingScreenFragment = new TrainingScreenFragment(peripheralsList, new ReflexTrainingSingle(getActivity(), peripheralsList)); // Create Fragment
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