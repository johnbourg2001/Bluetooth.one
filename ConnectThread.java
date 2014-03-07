package com.example.finalblue;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
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
    
    // [todo] - Get actual UUID of each device rather than set to a static value
    // http://stackoverflow.com/questions/5088474/how-can-i-get-the-uuid-of-my-android-phone-in-an-application
//    TelephonyManager tManager = (TelephonyManager)getApplicationContext.getSystemService(Context.TELEPHONY_SERVICE);
//    String uuid = tManager.getDeviceId();
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID = UUID.fromString("1234");
    
    /**
     * ConnectThread constructor
     */
    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        Log.i(TAG, "In [ConnectThread] Constructor");

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
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
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            
            ConnectedThread cdt = new ConnectedThread(mmSocket, listView, inpt, dataView, editText);
            cdt.run();
            
        } catch (IOException connectException) {
            Log.e(TAG, connectException.toString());

            // Unable to connect
            // Lose the socket and get out
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return;
        }
        
        // Do work to manage the connection (in a separate thread)
//        Handler mHandler = new Handler(Looper.getMainLooper());
//        Message completeMessage = mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket);
//        Log.i(TAG, "run" + completeMessage.toString());
        //completeMessage.sendToTarget();
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
