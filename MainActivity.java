package com.example.finalblue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

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
import android.widget.EditText;
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
    
    
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
    EditText editText;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       	 
        
        
        // TODO Auto-generated method stub
        // connectNew=(Button)findViewById(R.id.bConnectNew);
        listView = (ListView)findViewById(R.id.listView1);
        dataView = (TextView)findViewById(R.id.dV);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        editText = (EditText) findViewById(R.id.userInput);
        EditText editText = (EditText) findViewById(R.id.userInput);
    	//String input = editText.getText().toString();
        //dataView = (TextView)findViewById(R.id.dataView);
        
        
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
    
    private String ButtonHit(){
    	EditText editText = (EditText) findViewById(R.id.userInput);
    	String input = editText.getText().toString();
    	
    	return(input);
    	
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
      
    	String input = editText.getText().toString();
            
        dataView.setText("Response: ");

        //for (int i = 0; i < 5; i++) {
        	ConnectThread connect = new ConnectThread(selectedDevice, dataView, input);
        	connect.run(); 
        //}
    }
}