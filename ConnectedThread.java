package com.example.finalblue;
// package com.johnbourgeios.capstone;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    // private Handler threadHandler = new Handler();
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static final String TAG = "com.johnbourgeois.capstone";
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    TextView tv;
    EditText editText;
    
        
    /**
     * ConnectedThread Constructor
     */

    public ConnectedThread(BluetoothSocket socket, TextView ntv, String message) {
    	
        mmSocket = socket;
        tv = ntv;
        
        Log.i(TAG, "Sending message.");
        sendMessage(mmSocket, message);
        Log.i(TAG, "Sent message.");
        
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
            
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        
    }

    
    /**
     * 
     * ConnectedThread Run
     */
 
    public void run() {
    	
    	InputStreamReader isr = null;
    	Log.i(TAG, "cndt run");
    	
        try {
			isr = new InputStreamReader(mmInStream, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
        
        while (true) {
        try {
        	char output = (char)isr.read(); // Sample isr 
        	
        	if (output == '~') {
        		return;
        	}
        	
        	CharSequence cs = tv.getText();
        	tv.setText(cs.toString() + String.valueOf(output));
        	Log.i(TAG, String.valueOf(output)); // Log output                    	
        } catch (IOException e) {
        	Log.e(TAG, e.toString());
        }
        }
	}
    			
	
    /**
     * [ConnectedThread Write]
     * Call this from the main activity to send data to the remote device 
     * @param bytes [description]
     */

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * [ConnectedThread Cancel]
     * Call this from the main activity to shutdown the connection
     */

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
    
    private void sendMessage(BluetoothSocket socket, String msg) {
    	
    	OutputStream outStream;
    	
    	try {
    		outStream = socket.getOutputStream();
    		byte[] byteString = msg.getBytes();
    		outStream.write(byteString);
    	} catch (IOException e) {
    		Log.d("BLUETOOTH_COMMS", e.getMessage());
    	}
    }
    
}
