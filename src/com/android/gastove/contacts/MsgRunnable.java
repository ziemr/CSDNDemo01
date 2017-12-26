package com.android.gastove.contacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.service.BluetoothMsg;
import com.android.gastove.util.Const;

public class MsgRunnable implements Runnable {
	private ArrayList<ContactInfoData> mTELArrays;
	private Iterator<ContactInfoData> mIterable;
	private ProgressDialog progressDialog;
	private DBOperator mOperator;
    private Handler handler = new Handler(){
  	  
        @Override  
        public void handleMessage(Message msg) {  
            //¹Ø±ÕProgressDialog  
        	progressDialog.dismiss();  
        }}; 
	
	public MsgRunnable(ArrayList<ContactInfoData> mArraysList, ProgressDialog pDialog,Context context) {
		mTELArrays = mArraysList;
		progressDialog = pDialog;
		mOperator = new DBOperator(context);
	}
	
	@Override
	public void run() {
		mIterable = new CopiedIterator(mTELArrays.iterator());
		int length = 1;
        progressDialog.setMax(mTELArrays.size());
		StringBuilder sb = new StringBuilder();
		ContactInfoData tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
//			sb.setLength(0);
//			sb.append("Contact");
//			sb.append(Const.KEY_DELIMITER);
//			sb.append(tmpBodyLine.getData(0));
//			sb.append(Const.KEY_DELIMITER);
//			sb.append(tmpBodyLine.getData(1));
//			Log.d("11111111","sendMsg : " + sb.toString());
//			BluetoothMsg.sendMsg(sb.toString());
			
			mOperator.insertContacts(tmpBodyLine.getData(0),tmpBodyLine.getData(1));
			progressDialog.setProgress(length++);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(0);
	}
	
	public class CopiedIterator implements Iterator {
		private Iterator iterator = null;

		public CopiedIterator(Iterator itr) {
			LinkedList list = new LinkedList();
			while (itr.hasNext()) {
				list.add(itr.next());
			}
			this.iterator = list.iterator();
		}

		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		public void remove( ) {
		  throw new UnsupportedOperationException("This is a read-only iterator.");
		  }

		public Object next() {
			return this.iterator.next();
		}
	}
}
