package com.example.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {
    
    private final BluetoothSocket socket;
    private final BluetoothDevice selectedDevice;
    private static final String TAG = "com.johnbourgeois.capstone";
    TelephonyManager tManager;
    private final UUID uuid;
    
    ArrayAdapter<String> listAdapter;
    ListView listView;
    TextView dataView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    IntentFilter filter;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);

        // Get uuid
        tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        uuid = tManager.getDeviceId();
        // uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

 
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        devices = new ArrayList<BluetoothDevice>();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    Log.i(TAG, "Device found.");
                    
                    for (int a = 0; a < pairedDevices.size(); a++) {
                        if (device.getName().equals(pairedDevices.get(a))) {
                            Log.i("tag", "Paired Devices");
                            break;
                        }
                    }
                    
                    listAdapter.add(device.getName() + " " + device.getAddress());
                    if (btAdapter.getState() == btAdapter.STATE_OFF) {
                        turnOnBT();
                    }
                }
            }
        };
                
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
                
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
        
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "No bluetooth detected", 0).show();
            finish();
        } else {
            if (!btAdapter.isEnabled()) {
                turnOnBT();
            }
            
            getPairedDevices();
            startDiscovery();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void startDiscovery() {
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();

        if (devicesArray.size()>0) {
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());
            }
        }
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        selectedDevice = devices.get(arg2);
        socket = device.createRfcommSocketToServiceRecord(uuid);
        cndt = new ConnectedThread(socket);
        cndt.run();
        cndt.sendMessage(socket, "Send me dat data, bitch");
    }
}
