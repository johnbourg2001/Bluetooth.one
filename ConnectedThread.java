package com.example.bluetooth;
// package com.johnbourgeios.capstone;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private Handler threadHandler = new Handler();
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static final String TAG = "com.johnbourgeois.capstone";
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    TextView dv;
    ListView lv;
    

    /**
     * ConnectedThread Constructor
     */

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
    }

    
    /**
     * ConnectedThread Run
     */
 
    public void run() {
    	
        int i = 0;
    	Character buffer1[] = new Character[6];
    	
        while (true) {
          try {
            byte[] buffer;  // buffer store for the stream        
            int bytes; // bytes returned from read()
            buffer = new byte[1024];
              
            try {
              InputStreamReader isr = new InputStreamReader(mmInStream, "UTF8");
              char output;
              output = (char)isr.read();
              String tm;
              char x = (char)output;
              
              buffer1[i] = x;
              Log.i(TAG,"" + (char)buffer1[i] + i);
              if (i == 5) {
                  break;
              }
              i++;
                  
              } catch (IOException e) {
                  Log.e(TAG, e.toString());
              } 
	        }
    	// Handle message here	
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
          byte[] byteString = (msg + " ").getBytes();
          //stringAsBytes[byteString.length − 1] = 0;
          outStream.write(byteString);
    	} catch (IOException e) {
          Log.d("BLUETOOTH_COMMS", e.getMessage());
    	}
        return;
    }
}
