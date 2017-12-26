package com.android.gastove.service;

import android.content.Context;
import android.util.Log;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;

public class BluetoothMsg {
	
	private static BluetoothService BluteoothService;
	private static Context Context;
	
	public static void initBluetoothMsg(BluetoothService mBluetoothService) {
		BluteoothService = mBluetoothService;
	}

	public static void initBluetoothMsg(Context mContext) {
		Context = mContext;
	}
	
	public static void sendMsg(String message) {
		// Check that we're actually connected before trying anything
		if (BluteoothService.getState() != BluetoothService.STATE_CONNECTED) {
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			BluteoothService.write(send);
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
 		    new AlarmsThread(Context).start();
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
}
