package com.android.gastove.service;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import com.android.gastove.net.WIFIMsg;
import com.android.gastove.net.WifiService;
import com.android.gastove.udpmanager.ConnectionManager;
import com.android.gastove.udpmanager.Info;
import com.android.gastove.udpmanager.ServersSocket;
import com.android.gastove.udpmanager.ServersSocket.ClientDataCallBack;
import com.android.gastove.udpmanager.UDPSocketBroadCast;
import com.android.gastove.ui.LoginFrgmtActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.TelListener;
import com.android.gastove.util.Utils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class GastoveService extends Service {

	private static Context Context;
	private String TAG = "GastoveService"; 
	private BluetoothService mChatService = null;
	private WifiService mWifiService =null;
	private SharedPreferences mSharedData = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    
    private ISMNotification mISMNotification = null;
    
    private String DeviceMac = null;
	private UDPSocketBroadCast mBroadCast;
	private ServersSocket mServersSocket;
	
    // Name of the connected device
    private String mConnectedDeviceName = null;
    private Timer timer;
    Intent lockIntent = new Intent();
    private long delay = 1000 * 60 * 1; //1min
    private void StartTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
		        //start auto connect to device
//				context.sendBroadcast(new Intent().setAction(Const.ACT_ECO_CONTECT_TO_DEVICE));
			     if( new SharedPrefsData(Context).getSharedDaBool(Const.BUNDLE_LOCK_FLAG) ){
						Intent intent = new Intent();
						intent = new Intent(Context, LoginFrgmtActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Context.startActivity(intent);
			 }
			}
		},0, delay);
	}
	
    public void StopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	public  Context getAppContext() {
		if (Context == null) {
			Context = getApplicationContext();
		}
		return Context;
		
	}
	/**
	 * 客户端数据在这里处理
	 */
	private ClientDataCallBack clientData = new ClientDataCallBack() {

		@Override
		public void getClientData(int connectMode, String str) {
			switch (connectMode) {
			case Info.CONNECT_SUCCESS:// 连接成功
				sendCast(Info.CONNECT_SUCCESS, str);
				break;
			case Info.CONNECT_GETDATA:// 传输数据
				sendCast(Info.CONNECT_SUCCESS, str);
				break;
			case Info.CONNECT_FAIL:
				sendCast(Info.CONNECT_FAIL, str);
				break;
			}
		}

		private void sendCast(int flag, String str) {
			Intent intent = new Intent();
			intent.putExtra("flag", flag);
			intent.putExtra("str", str);
			intent.setAction("updata");
			sendBroadcast(intent);
		}
	};
	@Override
	public void onCreate() {
		super.onCreate();
        PritLog.d(TAG+"onCreate");
//        Context = getApplicationContext();
        getAppContext();
    	mISMNotification = new ISMNotification(Context);
    	
    	mSharedData = PreferenceManager.getDefaultSharedPreferences(Context);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //service running notification
        mISMNotification.putNotice(Const.net_connect_ng);
		
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(Context, mHandler);
        
        BluetoothMsg.initBluetoothMsg(mChatService);
        
        //for  wifi
        mWifiService = WifiService.getInstance(Context, mWifiHandler);
//        mWifiService = new WifiService(Context, mWifiHandler);
//        WIFIMsg.initWIFIMsg(Context,mWifiService);
        //ism`s function
		TelephonyManager telM = (TelephonyManager)Context.getSystemService(Context.TELEPHONY_SERVICE);
		telM.listen(new TelListener(Context), PhoneStateListener.LISTEN_CALL_STATE);
		//for udp
		try {
			String ip = ConnectionManager.getLocalIP();
			if (ip != null && !"".equals(ip)) {
				Info.SERVERSOCKET_IP = ip;
				mBroadCast = UDPSocketBroadCast.getInstance();
				mServersSocket = ServersSocket.getInstance();
//				mBroadCast.startUDP(Info.SERVERSOCKET_IP,
//						Info.SERVERSOCKET_PORT);
//				mServersSocket.startServer(clientData);
			} else {
				Toast.makeText(getApplicationContext(), "请检查网络设置",
						Toast.LENGTH_LONG).show();
//				stopSelf();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendUDP(){
		mBroadCast.startUDP(Info.SERVERSOCKET_IP,
				Info.SERVERSOCKET_PORT);
		mServersSocket.startServer(clientData);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PritLog.d(TAG+"onStartCommand");
		super.onStartCommand(intent, flags, startId);
		int result = START_STICKY;
		if (intent != null) {
//			EcomodeThead ecomodeThead = new EcomodeThead(intent);
//			ecomodeThead.start();
			handleEvents(Context, intent);
		} else {
			result = START_NOT_STICKY;
		}
		return result;
	}
    private void handleEvents(Context context, Intent intent) {
    	PritLog.d(TAG + "handleEvents()");
    	String action = intent.getAction();
    	PritLog.d(TAG + "handleEvents action" + action);
    	if (mWifiService!= null) 
    		if(mWifiService.getState() != WifiService.STATE_CONNECTED) {
    			mWifiService.connect();
    		}
		if (Const.ACT_GAS_LOCK.equals(action)) {
			if (intent.getBooleanExtra(Const.BUNDLE_LOCK_FLAG,false)) {
//				StartTimer();
			} else {
//				StopTimer();
			}	
		}
		if ("com.android.test.action".equals(action)) {
//			sendUDP();
		}
		
    }
    
    private class EcomodeThead extends Thread{
        Context mContext;
        Intent mIntent;
        public EcomodeThead(Intent intent){
            mContext = Context;
            mIntent  = intent;
        }
        @Override
        public void run(){
            handleEvents(mContext, mIntent);
        }
    }
	private void connectDevice(String address, boolean secure) {
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	PritLog.d(TAG + "===handleMessage Msg===");
            switch (msg.what) {
            case Const.MESSAGE_STATE_CHANGE:
            	PritLog.d("MESSAGE_STATE_CHANGE: ");
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	PritLog.d("STATE_CONNECTED: ");
//                	mISMNotification.putServerNotice(Const.ACT_ECO_NOTHING_TO_DO,Const.not_server_found);
                    break;
                case BluetoothService.STATE_CONNECTING:
                	PritLog.d("STATE_CONNECTING: ");
//                	mISMNotification.putServerNotice(Const.ACT_ECO_NOTHING_TO_DO,Const.not_server_connect);
                    break;
                case BluetoothService.STATE_LISTEN:
                	PritLog.d("STATE_LISTEN: ");
                	break;
                case BluetoothService.STATE_NONE:
                	PritLog.d("STATE_NONE: ");
//                	mISMNotification.putServerNotice(null,Const.not_server_device);
                    break;
                }
                break;
            case Const.MESSAGE_WRITE:
            	PritLog.d("MESSAGE_WRITE: ");
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                Utils.showToastDebug(Context, writeMessage);
                break;
            case Const.MESSAGE_READ:
            	PritLog.d("MESSAGE_READ: ");
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Utils.showToastDebug(Context,"Read:" + readMessage);
                break;
            case Const.MESSAGE_DEVICE_NAME:
            	PritLog.d("MESSAGE_DEVICE_NAME: "+msg.getData().getString(Const.DEVICE_NAME));
//                 save the connected device's name
                mConnectedDeviceName = msg.getData().getString(Const.DEVICE_NAME);
//                mISMNotification.setDeviceName(mConnectedDeviceName);
                Utils.showToast(Context, "Connected to "+ mConnectedDeviceName);
                break;
            case Const.MESSAGE_TOAST:
            	PritLog.d("MESSAGE_TOAST: "+ msg.getData().getString(Const.TOAST));
            	Utils.showToastDebug(Context, msg.getData().getString(Const.TOAST));
                break;
            }
        }
    };
    private final Handler mWifiHandler = new Handler() {
    	 @Override
         public void handleMessage(Message msg) {
         	PritLog.d(TAG + "===handleMessage Msg===");
             switch (msg.what) {
             case Const.MESSAGE_STATE_CHANGE:
             	PritLog.d("MESSAGE_STATE_CHANGE: ");
                 switch (msg.arg1) {
                 case BluetoothService.STATE_CONNECTED:
                 	PritLog.d("STATE_CONNECTED: ");
                 	mISMNotification.putNotice(Const.net_connect_ok);
                     break;
                 case BluetoothService.STATE_CONNECTING:
                 	PritLog.d("STATE_CONNECTING: ");
                 	mISMNotification.putNotice(Const.net_connect_ng);
                     break;
                 case BluetoothService.STATE_LISTEN:
                 	PritLog.d("STATE_LISTEN: ");
                 	mISMNotification.putNotice(Const.net_connect_ng);
                 	break;
                 case BluetoothService.STATE_NONE:
                 	PritLog.d("STATE_NONE: ");
                 	mISMNotification.putNotice(Const.net_connect_ng);
                     break;
                 }
                 break;
             case Const.MESSAGE_WRITE:
             	PritLog.d("MESSAGE_WRITE: ");
                 byte[] writeBuf = (byte[]) msg.obj;
                 // construct a string from the buffer
                 String writeMessage = new String(writeBuf);
                 Utils.showToast(Context, "Connected to "+ mConnectedDeviceName);
                 break;
             case Const.MESSAGE_READ:
             	PritLog.d("MESSAGE_READ: ");
                 byte[] readBuf = (byte[]) msg.obj;
                 // construct a string from the valid bytes in the buffer
                 String readMessage = new String(readBuf, 0, msg.arg1);
                 Toast.makeText(Context,"Read:" + readMessage, Toast.LENGTH_SHORT).show();
                 
                 //handle for receive
                 WIFIMsg.readMsgHandleRunnable(readMessage);
                 break;
             case Const.MESSAGE_DEVICE_NAME:
             	PritLog.d("MESSAGE_DEVICE_NAME: "+msg.getData().getString(Const.DEVICE_NAME));
//                  save the connected device's name
                 mConnectedDeviceName = msg.getData().getString(Const.DEVICE_NAME);
//                 mISMNotification.setDeviceName(mConnectedDeviceName);
                 Utils.showToast(Context, "Connected to "+ mConnectedDeviceName);
                 break;
             case Const.MESSAGE_TOAST:
             	PritLog.d("MESSAGE_TOAST: "+ msg.getData().getString(Const.TOAST));
             	Utils.showToastDebug(Context, msg.getData().getString(Const.TOAST));
                 break;
             }
         }
    };
	@Override
	public void onDestroy() {
		super.onDestroy();
		mISMNotification.removeNotice();
	}
}
