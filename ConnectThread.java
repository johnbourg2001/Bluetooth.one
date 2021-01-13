
package com.example.finalblue;
// package com.johnbourgeios.capstone;


import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final String TAG = "com.johnbourgeois.capstone";
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    TextView tv;
    String ms;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    /**
     * ConnectThread constructor
     */

    
    public ConnectThread(BluetoothDevice device, TextView ntv, String message) {
    	
    	ms = message;
    	tv = ntv;
        mmDevice = device;
        BluetoothSocket tmp = null;
        
        Log.i(TAG, "In [ConnectThread] Constructor");

        try {
        	tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.i(TAG, "In [ConnectThread] constructor's try state");
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        
        mmSocket = tmp;
    }


    /**
     * ConnectThread Run
     */

    public void run() {

        Log.i(TAG, "In [ConnectThread] Run()");
        
        try {
            mmSocket.connect();
            Log.i(TAG, "Socket connected.");
            
        } catch (IOException connectException) {
            Log.e(TAG, connectException.toString());

            try {
                mmSocket.close();
                Log.i(TAG, "Socket closed.");
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            
            return;
        }
        
        

        for (int i = 0; i < 5; i++) {
        	ConnectedThread cdt = new ConnectedThread(mmSocket, tv, ms);
        	Log.i(TAG,"in for");
        	cdt.run();
        }
        cancel();
        
        
    }

    /**
     * ConnectThread Cancel
     * Will cancel an in-progress connection, and close the socket
     */

    public void cancel() {
        Log.i(TAG, "In [ConnectThread] Cancel()");

        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
}
