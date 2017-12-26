package com.android.gastove.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.Const;

public class TelListener extends PhoneStateListener {
	private WindowManager mManager;
	WindowManager.LayoutParams params;
	View mImageButton;
	View mDragButton;
	private Context _Context;
	protected float x;
	protected float y;
	protected float mTouchStartX;
	protected float mTouchStartY;
	View view;
	Boolean isDraw = false;
	SharedPreferences mSharedData = null;
	DBOperator mDBOperator;
	
	// Member object for the chat services
	public TelListener(Context context) {
		this._Context = context;
		mDBOperator = new DBOperator(context);
        mSharedData = PreferenceManager.getDefaultSharedPreferences(_Context);
		
		mManager = (WindowManager) _Context.getSystemService(Context.WINDOW_SERVICE);
		
		view = View.inflate(_Context,R.layout.incoming,null);
		mImageButton =  view.findViewById(R.id.INCOMINGFINDVIEW_NORL);
	    mDragButton = view.findViewById(R.id.INCOMINGFINDVIEW_DRAG);
	    
	    mImageButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				startDrag();
				return false;
			}
		});
		
		mImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//-- send find msg    then ringing
				String message = "FIND" +Const.KEY_DELIMITER + "RING";
			}
		});
		
		mDragButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() -25;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					endDrag();
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		String values = mDBOperator.getSharedDataValue(Const.KEY_CALLS);
		//1.home  2.send
		Utils.showToastDebug(_Context, "Tellister");
		if (Const.KEY_VALUE_FALSE.equals(values)) //send case
			return;
		
		super.onCallStateChanged(state, incomingNumber);
		switch(state) {
		    case TelephonyManager.CALL_STATE_RINGING :
			    drawFindView();
//			    String message = "TEL" + Const.KEY_DELIMITER + incomingNumber;
//			    sendMessage(incomingNumber);
			    mDBOperator.insertCalls(incomingNumber);
			    break;
		    case TelephonyManager.CALL_STATE_IDLE :
			    removeView();
			    break;
			default :
				break;
		}
	}

	private void removeView() {
		if (isDraw == true && mManager != null) {
			mManager.removeView(view);
		}
		mManager = null;
	}
	
	public void drawFindView() {
		mManager = (WindowManager) _Context.getSystemService(Context.WINDOW_SERVICE);
		// Check that we're actually connected before trying anything
//		if (_BluteoothService.getState() != BluetoothService.STATE_CONNECTED) {
//			return;
//	    }
		
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.flags =  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		
		//-- read position
		int paramsX = 0;
		int paramsY = 1;
		params.x = readSharedParams(paramsX);
		params.y = readSharedParams(paramsY);
		
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.RGBA_8888;
		
		mManager.addView(view, params);
		isDraw = true;
	}
	
	private void updateViewPosition() {
		params.x = (int) (x-mTouchStartX);
		params.y = (int ) (y-mTouchStartY);
		mManager.updateViewLayout(view, params);
	}
	
	private void startDrag() {
		mImageButton.setVisibility(View.GONE);
		mDragButton.setVisibility(View.VISIBLE);

//		final long downTime = SystemClock.uptimeMillis();
//		final MotionEvent event =  MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, mTouchStartX, mTouchStartY, 0);
//		Log.d("111111_event", "mTouchStartX = " +mTouchStartX +"; mTouchStartY = " + mTouchStartY);
//		mDragButton.onTouchEvent(event);
	}
	
	private void endDrag() {
		mDragButton.setVisibility(View.GONE);
		mImageButton.setVisibility(View.VISIBLE);
		//-- write position params.x and params.y
		writeSharedParams(params.x, params.y);
	}

	private void writeSharedParams(int x, int y) {
		SharedPreferences.Editor mEditor = mSharedData.edit();
        mEditor.putInt(Const.PARAMS_X, x);
        mEditor.putInt(Const.PARAMS_Y, y);
        mEditor.commit();
	}
	private int readSharedParams(int params) {
        int x,y;
		x = mSharedData.getInt(Const.PARAMS_X, 0);
        y = mSharedData.getInt(Const.PARAMS_Y, 0);
        if ( params == 0) {
        	return x;
        } else {
        	return y;
        }
	}
	public void sendMessage(String message) {
		// Check that we're actually connected before trying anything
//		if (_BluteoothService.getState() != BluetoothService.STATE_CONNECTED) {
//			return;
//		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
//			_BluteoothService.write(send);
		}
	}
}
