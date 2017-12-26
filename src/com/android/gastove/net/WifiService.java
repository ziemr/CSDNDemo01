package com.android.gastove.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.service.PritLog;
import com.android.gastove.ui.IndexFrgmt;
import com.android.gastove.util.Const;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WifiService {
    // Debugging
    private static final String TAG = "WifiService";
    private static final boolean D = true;
    
 // Member fields
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;   // for server
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private Context mContext;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    public static final String SOCKET_CALLS = "A";
    public static final String SOCKET_REMARK = "B";
    public static final String SOCKET_CONTACTINFO = "C";
    public static final String SOCKET_PUPWINMAGE = "D";
    public static final String SOCKET_PUPWINCONTENT = "E";
    public static final String SOCKET_RECORDIN = "F";
    public static final String SOCKET_RECORDTODAY = "G";
    public static final String SOCKET_RECORDTODAYINDEX = "H";
    public static final String SOCKET_TANAS_TABLECOUNT = "ct"; //for record
    
    private static WifiService mWifiService;
    
	public static WifiService getInstance(Context context, Handler handler) {
		PritLog.d("WifiService-getInstance");
		if (mWifiService == null) {
			mWifiService = new WifiService(context, handler);
	        //for READ or WRITE
	        WIFIMsg.initWIFIMsg(context,mWifiService);
		}
		return mWifiService;
	}
    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public WifiService(Context context, Handler handler) {
    	PritLog.d("WifiService-WifiService");
//        mAdapter = BluetoothAdapter.getDefaultAdapter();
    	mContext =context;
    	mHandler = handler;
        setState(STATE_NONE);
    }
    
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Const.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
//        if (mInsecureAcceptThread == null) {
//            mInsecureAcceptThread = new AcceptThread(false);
//            mInsecureAcceptThread.start();
//        }
    }
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect() {
        if (D) Log.d(TAG, "connect to: ");

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread();
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(Socket socket) {
        if (D) Log.d(TAG, "connected, Socket Type:");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Const.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Const.DEVICE_NAME, "");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }
    
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private ServerSocket mmServerSocket = null;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            ServerSocket tmp = null;
//            mSocketType = secure ? "Secure":"Insecure";
//
//            // Create a new listening server socket
//            try {
//                if (secure) {
//                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
//                        MY_UUID_SECURE);
////                } else {
////                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
////                            NAME_INSECURE, MY_UUID_INSECURE);
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
//            }
//            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);
            ServerSocket tmp = null;
            Socket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                	tmp = new ServerSocket(0);
					
					SocketAddress address = null;	
					if(!tmp.isBound())	
					{
						tmp.bind(address, 0);
					}
					//serverSocket.getLocalPort()
                	mmServerSocket = tmp;
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                } catch (Exception e) {
                	Log.e("1111111111",e.toString());
                	android.os.Process.killProcess(android.os.Process.myPid());
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (WifiService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket);
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private Socket mmSocket;
        private DBOperator mdOperator;
        Socket tmp = null;
        public ConnectThread() {
//            	tmp = new Socket(InetAddress.getByName("192.168.1.77"),2011);
        	mdOperator = new DBOperator(mContext);
			tmp = new Socket();
            mmSocket = tmp;
        }

        public void run() {
//            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + "mSocketType");

            // Always cancel discovery because it will slow down a connection
//            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
//                mmSocket.connect();
//                tmp = new Socket(InetAddress.getByName("192.168.1.77"),2011);
            	String IP = mdOperator.getSharedDataValue(Const.SHARED_DEVICE_IP);
            	if (Const.NO_DATA.equals(IP)) return;
            	mmSocket.connect(new InetSocketAddress(InetAddress.getByName(IP),2011));
            } catch (IOException e) {
            	Log.e(TAG,"create() failed", e);
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() "+
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }
            mmSocket = tmp;

            // Reset the ConnectThread because we're done
            synchronized (WifiService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    } 
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Const.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Const.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
//        BluetoothService.this.start();
        setState(STATE_LISTEN);
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Const.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Const.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
//        BluetoothService.this.start();
        setState(STATE_LISTEN);
    }
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final Socket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(Socket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
//            	tmpIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				tmpOut = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer;
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
            	byte[] tmpbuffer = new byte[1024];
                try {
                	buffer = tmpbuffer;
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
//                    WifiMsg.readMsg(buffer, bytes);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Const.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                    
//                    WifiMsg.readMsgHandleRunnable(tmpbuffer, bytes);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Const.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }
    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        

        
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }
        setState(STATE_NONE);
    }
}
