package com.android.gastove.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.Const;
import com.android.gastove.util.CopiedIterator;
import com.android.gastove.util.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class structureRunnable implements Runnable {
	private Context context;
	private ProgressDialog progressDialog;
    private Handler handler = new Handler(){
  	  
        @Override  
        public void handleMessage(Message msg) {  
            //¹Ø±ÕProgressDialog  
        	progressDialog.dismiss();  
        }}; 
	
	public structureRunnable(Context con, ProgressDialog pDialog) {
		progressDialog = pDialog;
		context = con;
	}
	
	@Override
	public void run() {
		if(WIFIMsg.getWIFIStatus()!= WifiService.STATE_CONNECTED) {
//		    Utils.showToastDebug(context, "no  connect");
			handler.sendEmptyMessage(0);
			return;
		}
		remarkRunnalbe();
		contactsinfoRunnalbe();
		pupwinmageRunnalbe();
		pupwincontentRunnalbe();
//		recordtodayindexRunnalbe();
//		recordtodayRunnalbe();
//		recordinRunnalbe();
		
		
		handler.sendEmptyMessage(0);
	}

	private void remarkRunnalbe() {
		ArrayList<dataStructure.strRemark> mTELArrays = null;
		Iterator<dataStructure.strRemark> mIterable = null;
		
		mTELArrays = new DBOperator(context).getRemarkCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strRemark tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_REMARK);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeID());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeName());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		handler.sendEmptyMessage(0);
	}
	
	private void contactsinfoRunnalbe() {
		ArrayList<dataStructure.strContactsInfo> mTELArrays = null;
		Iterator<dataStructure.strContactsInfo> mIterable = null;
		
		mTELArrays = new DBOperator(context).getContactsInfoCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strContactsInfo tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_CONTACTINFO);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getConame());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTelphone());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getUsed());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getContracId());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		handler.sendEmptyMessage(0);
	}
	
	private void pupwinmageRunnalbe() {
		ArrayList<dataStructure.strPupWinMage> mTELArrays = null;
		Iterator<dataStructure.strPupWinMage> mIterable = null;
		
		mTELArrays = new DBOperator(context).getPupWinMageCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strPupWinMage tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_PUPWINMAGE);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypename());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void pupwincontentRunnalbe() {
		ArrayList<dataStructure.strPupWinContent> mTELArrays = null;
		Iterator<dataStructure.strPupWinContent> mIterable = null;
		
		mTELArrays = new DBOperator(context).getPupWinContentCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strPupWinContent tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_PUPWINCONTENT);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getContid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getContname());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*
	private void recordtodayindexRunnalbe() {
		ArrayList<dataStructure.strRecordtodayindex> mTELArrays = null;
		Iterator<dataStructure.strRecordtodayindex> mIterable = null;
		
		mTELArrays = new DBOperator(context).pushRecordtodayIndexCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strRecordtodayindex tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_RECORDTODAYINDEX);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTelphone());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getToday());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getUsed());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		handler.sendEmptyMessage(0);
	}
	
	private void recordtodayRunnalbe() {
		ArrayList<dataStructure.strRecordtoday> mTELArrays = null;
		Iterator<dataStructure.strRecordtoday> mIterable = null;
		
		mTELArrays = new DBOperator(context).pushRecordtodayCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strRecordtoday tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_RECORDTODAY);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getRecordid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTelphone());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getConame());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getToday());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getUsed());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getPay());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData4());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData5());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		handler.sendEmptyMessage(0);
	}
	
	private void recordinRunnalbe() {
		ArrayList<dataStructure.strRecordin> mTELArrays = null;
		Iterator<dataStructure.strRecordin> mIterable = null;
		
		mTELArrays = new DBOperator(context).pushRecordINCursor(null);
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strRecordin tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(WifiService.SOCKET_RECORDIN);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getRecordid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getPhone());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getNum());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData3());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData4());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData5());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData6());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData7());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				WIFIMsg.sendMsg(sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		handler.sendEmptyMessage(0);
	}
	*/
	
}
