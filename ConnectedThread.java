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
    
    // [todo] - Get actual UUID of each device rather than set to a static value
    // http://stackoverflow.com/questions/5088474/how-can-i-get-the-uuid-of-my-android-phone-in-an-application
    // TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    // String uuid = tManager.getDeviceId();
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID = UUID.fromString("1234");

    /**
     * ConnectedThread Constructor
     */

    public ConnectedThread(BluetoothSocket socket, TextView dataView, ListView listView) {
        mmSocket = socket;
        lv = listView;
        
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
        dv = dataView;
        
        // [todo] - Call this function from outside the function
        // should not be in its own constructor
    }

    
    /**
     * ConnectedThread Run
     */
 
    public void run() {
    	
    	int i = 0;
    	Character buffer1[] = new Character[6];
        // Keep listening to the InputStream until an exception occurs
        // [todo] - Make this more robust, don't rely on a break to end the loop.
        
    	
    	
    		while (true) {
    			try {
    				byte[] buffer;  // buffer store for the stream        
    				int bytes; // bytes returned from read()
                // Read from the InputStream
    				buffer = new byte[1024];
                
    				try {
                	
    					InputStreamReader isr = new InputStreamReader(mmInStream, "UTF8");
                	
    					char output;
                	
    					output = (char)isr.read();
    					String tm;
    					//tm = output.toString();
                	
    					char x = (char)output;
                	
                	
	                	buffer1[i] = x;
	                	Log.i(TAG,"" + (char)buffer1[i] + i);
	                	if(i == 5){
	                		break;
	                	}
	                	
	                	/*if(i == 5){
	                		
	                		Log.i(TAG, "" + buffer1[0]+ ""+ buffer[1]);
	                		/*
	                		for(int z = 0; z < i; z++){
	                			Log.i(TAG, "" + buffer1[3]);
	                		}
	                		
	                		break;
	                	}
	                	*/
	                	
	                	
	                	 
	                	/*
	                	if (buffer.length < 5) {
	                		// Add x to buffer
	                		
	                	} else {
	                		// Log or display buffer
	                		// Clear buffer
	                		
	                	}
	                	*/
	                	i++;
	                    
	                } catch (IOException e) {
	                	Log.e(TAG, e.toString());
	                } 
	                
	                
	                
	                //bytes = mmInStream.read(buffer);
	               
	                //Send the obtained bytes to the UI activity
	                //Handler mHandler = new Handler(Looper.getMainLooper());
	
	                //Handler mHandler = new Handler();
	//                Message completedMessage = mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer);
	            
	                
	                //completedMessage.sendToTarget();
	                
	                // DataView = completedMessage.toString());
	                // [issue] - Guessing this will crash because R is not in scope
	//                 TextView DataView = (TextView)findViewById(R.id.dataView);
	//                 DataView.setText(completedMessage.toString());
	             
	            }finally{
	            	
	            }
	        }
    		dv.setText(buffer1[0] + "" + buffer1[1] + "" + buffer1[2]  + "" + buffer1[3] + "" + buffer1[4] + "" + buffer1[5]);	    	
    		
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
    }
}