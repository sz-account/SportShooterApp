package com.example.dsp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.dsp.ListAdapter;
import com.example.dsp.Message;
import com.example.dsp.MessageStatus;
import com.example.dsp.R;
import com.example.dsp.databinding.FragmentHomeBinding;
import com.example.dsp.trainingData.AppDatabase;
import com.example.dsp.ui.shootingMenuFragment.MenuFragment;
import com.example.dsp.ui.shootingMenuFragment.MultiTargetsModesFragment;
import com.example.dsp.ui.shootingMenuFragment.SingleTargetModesFragment;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.ConnectionState;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.WriteType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MenuFragment menuFragment;

    //  ------------------ BlueTooth ----------------------
    private BluetoothCentralManager central;
    private boolean isScanning = false;
    private final String[] names = new String[] {"Czujnik"};
    private final String LOGTAG = "Woda";
    static public LinkedList<Message> messages = new LinkedList<>();
    static public final UUID uuid_service = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    static public final UUID uuid_characteristic = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");

    private ArrayList<BluetoothPeripheral> peripheralsList = new ArrayList<>();
    private ListView listView;
    private ListAdapter deviceListAdapter;

    // ----------------- Permissions -------------------------
    private final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    int requestCodePermissions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 1. Check Permissions
        Log.i(LOGTAG, "Checking Permissions");
        if (!hasPermissions(getActivity(), permissions)) {
            requestPermissions(permissions, requestCodePermissions);
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create BluetoothCentral and receive callbacks on the main thread
        central = new BluetoothCentralManager(getActivity(), bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
        deviceListAdapter = new ListAdapter(getActivity(), peripheralsList);
        listView = root.findViewById(R.id.deviceListView);
        listView.setAdapter(deviceListAdapter);

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run() {

                for( int i = 0; i < messages.size(); i++)
                {
                    Message msg = messages.get(i);

                    if (msg.messageStatus == MessageStatus.RECEIVED) {
                        messages.remove(i);
                        i--;
                    }
                    else if(msg.peripheral.getState() == ConnectionState.CONNECTED && msg.resendTimer <= 0) {
                        msg.resendTimer = 10;
                        BluetoothGattCharacteristic characteristic = msg.peripheral.getCharacteristic(uuid_service, uuid_characteristic);
                        if (characteristic != null) {
                            msg.peripheral.writeCharacteristic(characteristic, msg.message.getBytes(), WriteType.WITH_RESPONSE);
                        }
                    }

                    msg.resendTimer--;
                }
                handler.postDelayed(this, 100);
            }
        });

        // ----------------------- Buttons -----------------------------
        Button buttonSingleTarget =  root.findViewById(R.id.buttonSingleTarget);
        buttonSingleTarget.setOnClickListener(v -> {

            SingleTargetModesFragment singleTargetModesFragment = new SingleTargetModesFragment(peripheralsList); // Create Fragment
            menuFragment = singleTargetModesFragment;
            getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, // Replace fragment
                    singleTargetModesFragment, null).addToBackStack(null).commit();
        });

        Button buttonMultiTargets =  root.findViewById(R.id.buttonMultiTarget);
        buttonMultiTargets.setOnClickListener(v -> {

            MultiTargetsModesFragment multiTargetsModesFragment = new MultiTargetsModesFragment(peripheralsList); // Create Fragment
            menuFragment = multiTargetsModesFragment;
            getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main, // Replace fragment
                    multiTargetsModesFragment, null).addToBackStack(null).commit();
        });

        Button buttonSearch = (Button) root.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(v -> {

            if (!isScanning) {
                isScanning = true;
                central.scanForPeripheralsWithNames(names);
            }
        });

        // Create database
        AppDatabase instance = AppDatabase.getInstance(getActivity());


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // -------------- Permissions ---------------------
    private boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == requestCodePermissions) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    // -------------- BLUETOOTH ---------------------
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, ScanResult scanResult) {

            central.stopScan();
            Log.i(LOGTAG, "Found device: " + scanResult.getDevice().getName() + " / " + scanResult.getDevice().getAddress());

            central.autoConnectPeripheral(peripheral, bluetoothPeripheralCallback);
            isScanning = false;
        }

        @Override
        public void onConnectedPeripheral(BluetoothPeripheral peripheral) {
            Log.i(LOGTAG, "Device connected: " + peripheral.getName());

            BluetoothGattCharacteristic characteristic = peripheral.getCharacteristic(uuid_service, uuid_characteristic);
            peripheral.setNotify(characteristic, true);
            if(!peripheralsList.contains(peripheral)) {

                peripheralsList.add(peripheral);
                deviceListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onDisconnectedPeripheral(@NonNull BluetoothPeripheral peripheral, @NonNull HciStatus status) {
            super.onDisconnectedPeripheral(peripheral, status);

            Log.i(LOGTAG, "Device status: " + peripheral.getState().value );
            central.autoConnectPeripheral(peripheral, bluetoothPeripheralCallback);
        }

        @Override
        public void onConnectionFailed(BluetoothPeripheral peripheral, HciStatus status) {
            Log.i(LOGTAG, "Failed to connect: " + peripheral.getName());
        }
    };

    private final BluetoothPeripheralCallback bluetoothPeripheralCallback = new BluetoothPeripheralCallback() {

        @Override
        public void onCharacteristicUpdate(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, final GattStatus status)
        {
            menuFragment.characteristicNotify(peripheral, value);
        }

        @Override
        public void onCharacteristicWrite(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, final GattStatus status)
        {
            if (status == GattStatus.SUCCESS) {
                String tmp = new String(value);
                Log.i(LOGTAG, "Message Send Sucessfully: " + new String(value));

                changeStatus(peripheral, new String(value));

                menuFragment.onCharacteristicWrite(peripheral, value);

            }
        }
    };

    private void changeStatus(BluetoothPeripheral peripheral, String message)
    {
        for (Message msg: messages) {
            if (msg.peripheral.equals(peripheral) && msg.message.equals(message))
                msg.messageStatus = MessageStatus.RECEIVED;
        }
    }
}