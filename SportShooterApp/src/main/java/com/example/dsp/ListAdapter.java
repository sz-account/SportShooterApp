package com.example.dsp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.welie.blessed.BluetoothPeripheral;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<BluetoothPeripheral> {

    public ListAdapter(Context context, ArrayList<BluetoothPeripheral> deviceArrayList)
    {
        super(context, R.layout.list_item, deviceArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        BluetoothPeripheral item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, container, false);
        }

        TextView deviceName = convertView.findViewById(R.id.deviceName);
        TextView deviceAddress = convertView.findViewById(R.id.deviceAddress);

        deviceName.setText(item.getName());
        deviceAddress.setText(item.getAddress());

        return convertView;
    }
}
