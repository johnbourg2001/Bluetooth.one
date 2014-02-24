package com.example.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
//import com.example.bluetooth.ConnectThread;
//import com.example.bluetooth.ConnectedThread;

public class MainActivity extends Activity implements OnItemClickListener {
	
	private static final String TAG = "com.johnb.bluetooth";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
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
	//ArrayList<BluetoothDevice> ;
	IntentFilter filter;
	BroadcastReceiver receiver;
	
	Handler mHandler = new Handler() {
	
		public void handleMessage(int flag, Message msg){
			
			// Maybe comment this guy out
			super.handleMessage(msg);
			
			switch(flag){
				case SUCCESS_CONNECT:
					// DO something
					ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
					Toast.makeText(getApplicationContext(), "Connected", 0).show();
					String s = " succesful connected";
					Log.i("tag", s);
					
					connectedThread.write(s.getBytes());
					break;
					
			
				case MESSAGE_READ:
					byte[] readBuf = (byte[])msg.obj;
					String string = new String(readBuf);
					Toast.makeText(getApplicationContext(), string, 0).show();
//					Log.i("tag", string);
					
					break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// TODO Auto-generated method stub
		//connectNew=(Button)findViewById(R.id.bConnectNew);
		listView=(ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		devices = new ArrayList<BluetoothDevice>();
		receiver = new BroadcastReceiver(){
		
			@Override
			public void onReceive(Context context, Intent intent){
				String action = intent.getAction();
									
						if(BluetoothDevice.ACTION_FOUND.equals(action)){
							BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
							devices.add(device);
							Log.i("tag", "device found");
							String s = "";
							
							for(int a = 0; a < pairedDevices.size(); a++){
								if(device.getName().equals(pairedDevices.get(a))){
									
									Log.i("tag", "Paired Devicees");
									s = "(Paired)";
									break;
								}
							}
							
							listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
						}
						
						else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
							
						}
						
						else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
							
							
							
						}
						
						else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
							if(btAdapter.getState() == btAdapter.STATE_OFF){
								turnOnBT();
							}
						}
						
					}
				};
				
				registerReceiver(receiver, filter);
				filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
				registerReceiver(receiver, filter);
				filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				registerReceiver(receiver, filter);
				filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
				registerReceiver(receiver, filter);
		
		if(btAdapter==null){
			Toast.makeText(getApplicationContext(), "No bluetooth detected", 0).show();
			finish();
		}
		else{
			if(!btAdapter.isEnabled()){
				turnOnBT();
			}
			
			getPairedDevices();
			startDiscovery();
		}
		
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
		if(devicesArray.size()>0){
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
		if(resultCode == RESULT_CANCELED){
			Toast.makeText(getApplicationContext(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		BluetoothDevice selectedDevice = devices.get(arg2);
		
		if(btAdapter.isDiscovering()){
			btAdapter.cancelDiscovery();
			
		}
		if(listAdapter.getItem(arg2).contains("Paired")){
			
			//BluetoothDevice selectedDevice = devices.get(arg2);
			Toast.makeText(getApplicationContext(), "device is paired", 0).show();
			
			ConnectThread connect = new ConnectThread(selectedDevice);
			connect.start();
			Log.i("tag", "in click Listener");
		}
			
			//BluetoothDevice selectedDevice = devices.get(arg2);
			Toast.makeText(getApplicationContext(), "device is not paired", 0).show();
			ConnectThread connect = new ConnectThread(selectedDevice);
			connect.run();
			String s = "Paired";
			listAdapter.add(selectedDevice.getName()+" "+s+" "+"\n"+selectedDevice.getAddress());
	}
	
	
	public class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	    	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	        Log.i(TAG, "Connected");
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	            Log.i("tag", "try state");
	        } catch (IOException e) {
	        	Log.e(TAG, e.toString());
	        }
	        mmSocket = tmp;
	        //run();
	        
	    }
	    
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	    	btAdapter.cancelDiscovery();
	    	Log.i(TAG, "In run");
	        try {
	        	
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	            
	        } catch (IOException connectException) {
	        	
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	            	Log.e(TAG, e.toString());
	            }
	            return;
	        }
	        
	        // Do work to manage the connection (in a separate thread)
	        Message completeMessage = mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket);
	        mHandler.handleMessage(SUCCESS_CONNECT, completeMessage);
	        //Log.i("tag", "run" + completeMessage.toString());
	        completeMessage.sendToTarget();
	    }
	    
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) {
	        	Log.e(TAG, e.toString());
	        }
	    }
	}

	public class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	    
//	    private final BluetoothSocket MainActivityS;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) {
	        	Log.e(TAG, e.toString());
	        }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	        run();
	    }
	 
	    public void run() {
	        byte[] buffer;  // buffer store for the stream        
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                
	            	// Read from the InputStream
	            	buffer = new byte[1024];
	                bytes = mmInStream.read(buffer);
	               
	                // Send the obtained bytes to the UI activity
	                Message completedMessage = mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer);
	                mHandler.handleMessage(MESSAGE_READ, completedMessage);
	                //Log.i("tag", completedMessage.toString());
	                
	                /*
	                
	                mHanlder.handleMessage(MESSAGE_READ, bytes);
	                
	                */
	                
	                completedMessage.sendToTarget();
	                
	                //DataView = completedMessage.toString());
	                
	             TextView DataView = (TextView)findViewById(R.id.dataView);
	             DataView.setText(completedMessage.toString());
	             
	                
	                
	            } catch (IOException e) {
	            	Log.e(TAG, e.toString());
	                break;
	            }
	        }	
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) {
	        	Log.e(TAG, e.toString());
	        }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) {
	        	Log.e(TAG, e.toString());
	        }
	    }
	}
	
	
}
