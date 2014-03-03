
package com.example.bluetooth;
// package com.johnbourgeios.capstone;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
    
    private static final String TAG = "com.johnbourgeois.capstone";

    // [todo] - Get actual UUID of each device rather than set to a static value
    // http://stackoverflow.com/questions/5088474/how-can-i-get-the-uuid-of-my-android-phone-in-an-application
    //TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    //String uuid = tManager.getDeviceId();
    //private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID = UUID.fromString("1234");
    

    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    
    ArrayAdapter<String> listAdapter;
    //Button connectNew;
    ListView listView;
    TextView dataView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    //ArrayList<BluetoothDevice>;
    IntentFilter filter;
    BroadcastReceiver receiver;

    // Handler mHandler = new Handler() {
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        // https://developer.android.com/training/multiple-threads/communicate-ui.html
        // @Override <-- maybe?
    	public void handleMessage(int flag, Message msg){
            
            // Run default handleMessage (if exists?)
            super.handleMessage(msg);
            
            switch (flag) {
                case SUCCESS_CONNECT:
                    // [todo] - Double check that the bluetooth arg is actual of type "Bluetooth socket"
                    //ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj, dataView);
                    Toast.makeText(getApplicationContext(), "Connected", 0).show();
                    String s = "Succesful connected";
                    Log.i(TAG, s);
                    //connectedThread.write(s.getBytes());
                    break;
                    
            
                case MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    String string = new String(readBuf);
                    Toast.makeText(getApplicationContext(), string, 0).show();
                    Log.i(TAG, string);
                    break;
            }
        }
        
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // TODO Auto-generated method stub
        // connectNew=(Button)findViewById(R.id.bConnectNew);
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        dataView = (TextView)findViewById(R.id.dataView);
        dataView.setText("hi");
        
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
                    String s = "";
                    
                    for(int a = 0; a < pairedDevices.size(); a++){
                        if (device.getName().equals(pairedDevices.get(a))) {
                            
                            Log.i("tag", "Paired Devices");
                            s = "(Paired)";
                            break;
                        }
                    }
                    
                    listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    // Discovery has started
                    Log.i(TAG, "Discovery started");

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    // Discovery has finished
                    Log.i(TAG, "Discovery finished");

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    // Discovery state changed
                    Log.i(TAG, "Discovery state changed");

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void startDiscovery() {
        // TODO Auto-generated method stub
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void turnOnBT() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    private void getPairedDevices() {
        // TODO Auto-generated method stub
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

        Log.i(TAG, "In click Listener"); // Debug
       
        // TODO Auto-generated method stub
        BluetoothDevice selectedDevice = devices.get(arg2);

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        /*
        Intent intent = new Intent(this, ShowOtherScreen.class);
        startActivity(intent);
		*/
        // BluetoothDevice selectedDevice = devices.get(arg2);
        //Toast.makeText(getApplicationContext(), "Device is not paired", 0).show();
        
        ConnectThread connect = new ConnectThread(selectedDevice, dataView, listView);
        connect.run();
        
  
        //String s = "Paired";
        //listAdapter.add(selectedDevice.getName()+" "+s+" "+"\n"+selectedDevice.getAddress());
    }
}