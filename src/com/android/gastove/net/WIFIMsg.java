package com.android.gastove.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.service.PritLog;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;

public class WIFIMsg {
	
	private static WifiService wifiService;
	private static Context Context;
	
	public static void initWIFIMsg(Context mContext,WifiService mService) {
		PritLog.d("WIFIMsg-initWIFIMsg");
		wifiService = mService;
		Context = mContext;
	}
	public static int getWIFIStatus() {
		if (wifiService == null) return -1;
		return wifiService.getState();
	}
	public static void initBluetoothMsg(Context mContext) {
		Context = mContext;
	}
	
	public static void sendMsg(String message) throws UnsupportedEncodingException {
		// Check that we're actually connected before trying anything
		if (wifiService==null || wifiService.getState() != wifiService.STATE_CONNECTED) {
//			Toast.makeText(Context, "not contect", Toast.LENGTH_LONG);
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send;
//			try {
				send = message.getBytes("GBK");
				wifiService.write(send);
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	
    public static void readMsg(byte[] buffer,int bytes) {
 	   
 	   String readMessage = new String(buffer, 0, bytes);
 	   Log.d("11111111","readMsg : " + readMessage);
 	   String[] operatorStr = readMessage.split(Const.KEY_DELIMITER);
 
 	   String FLAG = operatorStr[0];
 	   if (FLAG.equals("TEL") ) {
 		   //
	        new DBOperator(Context).insertCalls(operatorStr[1]);
 	   } else if (FLAG.equals("FIND")) {
 		   //
// 		    new AlarmsThread(Context).start();
 	   } else if (FLAG.equals("Contact")) {
 		   //
 		   String name = operatorStr[1];
 		   String tel = operatorStr[2];
// 		   if(new DBOperator(Context).isContactsExist(tel)) 
// 			  Toast.makeText(Context, tel +" ÒÑ´æÔÚ", Toast.LENGTH_LONG).show();
// 		   else
 		     new DBOperator(Context).insertContacts(name,tel);
 	   }else {
 		  BodyLine mBodyData = new BodyLine();
 	 	   for(int i =0;i<5;i++) {
 	 	      mBodyData.setData(i, operatorStr[i]);
 	 	   }
 	 	   
 	 	   new DBOperator(Context).insertRecordin(mBodyData);
 	   }
    }
    
    public static void readMsgHandleRunnable(String readMessage) {
//       String readMessage = new String(buffer, 0, bytes);
  	   Log.d("11111111","readMsg : " + readMessage);
  	   String[] operatorStr = readMessage.split(Const.KEY_DELIMITER);
  
	   String FLAG = operatorStr[0];
	   if(FLAG.equals(Const.SOCKET_TRANS_TABLECOUNT)) {
		   String table = null;
 		   String tableCount = operatorStr[1];
// 		   String[] count = tableCount.split(Const.KEY_DELIMITER_S);
// 		   if (WifiService.SOCKET_RECORDIN.equals(operatorStr[1])) {
// 			   table = Const.TABLE_RecordIN;
// 		   }
// 		   
 		   
// 		  ProgressDialog	mProgressDialog = ProgressDialog.show(Context, "Loading...", "Please wait...", true, false);
//        new Thread(new recordHandleRunnable(Context,tableCount,mProgressDialog)).start();
          new Thread(new recordHandleRunnable(Context,tableCount)).start();
 		   
	   }
    }
}
