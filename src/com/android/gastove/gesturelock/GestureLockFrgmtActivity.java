package com.android.gastove.gesturelock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.gastove.MainActivity;
import com.android.gastove.R;
import com.android.gastove.gesturelock.GestureLockView.OnGestureFinishListener;
import com.android.gastove.ui.LoginFrgmtActivity;
import com.android.gastove.ui.testFrgmtActivity;
import com.android.gastove.util.DataBackUp;
import com.polites.android.GestureImageViewListener;

public class GestureLockFrgmtActivity extends FragmentActivity implements OnClickListener {
	
	private Animation animation;
	private float x,y,currentX,tempX;
	private GestureLockView view;
	
	private Button mLoginButton, mLoginError, mRegister, ONLYTEST;
	
	public class GestureFinish implements OnGestureFinishListener {

		@Override
		public void OnGestureFinish(boolean success, String key) {
			if (success) {
				startMainAvtivity();
			}
				
		}
		
	}
	private void startMainAvtivity() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent();
				intent = new Intent(GestureLockFrgmtActivity.this, MainActivity.class);
				GestureLockFrgmtActivity.this.startActivity(intent);
				GestureLockFrgmtActivity.this.finish();// 结束本Activity
			}
		}, 500);// 设置执行时间
	} 
	@Override
	public void onCreate(Bundle state) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(state);
		// //不显示系统的标题栏
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN );
		setContentView(R.layout.activity_login_gesture);
		
		view = (GestureLockView) findViewById(R.id.gesturelockview);
		view.setOnGestureFinishListener(new GestureFinish());
		view.setKey("0124678");  //Z
		
		mLoginButton = (Button) findViewById(R.id.login);
		mLoginError = (Button) findViewById(R.id.login_error);
		mRegister = (Button) findViewById(R.id.register);
		mLoginError.setOnClickListener(this);
		mRegister.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e("action", "down");
//			Log.e("count", root.getChildCount()+"");
			x = event.getX();
			y = event.getY();
//			firstView = root.getChildAt(1);
			
//			Log.e("view", firstView.toString());
			//Log.e("x", x+"");
			//Log.e("y", y+"");
			break;
		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			tempX = currentX - x;
//			if (tempX < 0) {
//				params1.leftMargin = (int) tempX;
//			}else {
//				params1.rightMargin = (int) -tempX;
//			}
//			firstView.setLayoutParams(params1);
			break;
		case MotionEvent.ACTION_UP:
			Log.e("action", "up");
//			animation.reset();
//			firstView.startAnimation(animation);
			break;
		}
		return true;
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.register: // 登陆
//			intent.setClass(LoginFrgmtActivity.this,
//					 testFrgmtActivity.class);
//					LoginFrgmtActivity.this.startActivity(intent);
//			new  DataBackUp(getApplicationContext()).doBackup(); 
			break;
		case R.id.login_error:
//			new  DataBackUp(getApplicationContext()).doRestore(); 
			break;
		}
	}
	
}
