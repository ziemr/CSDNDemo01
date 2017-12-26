package com.android.gastove;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.service.BluetoothMsg;
import com.android.gastove.service.PritLog;
import com.android.gastove.ui.LoginFrgmtActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.InitAppData;
import com.nineoldandroids.view.ViewHelper;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private DrawerLayout mDrawerLayout;
	private TabPageIndicator mIndicator;
	private static ViewPager mViewPager;
	private static FragmentPagerAdapter mAdapter;
	private static FragmentManager FM;
	private static Context mContext;
	private Button headsetting;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_ENABLE_BT = 3;

	private ImageView headicon;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PritLog.d(TAG+"onCreate()");
		setContentView(R.layout.activity_main);
		//step1---->
//		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // If the adapter is null, then Bluetooth is not supported
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
        FM = getSupportFragmentManager();
        mContext = getApplicationContext();
        //step1----->
//		BluetoothMsg.initBluetoothMsg(getApplicationContext());
		mIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
		mViewPager = (ViewPager) findViewById(R.id.id_pager);
		headicon = (ImageView) findViewById(R.id.headIcon);
		headsetting = (Button) findViewById(R.id.head_btn);
		mAdapter = new TabAdapter(FM,mContext);
		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager, 0);
		headicon.setOnClickListener(this);
		headsetting.setVisibility(View.VISIBLE);
		headsetting.setOnClickListener(this);
		
		initView();
		initEvents();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//case -- home
		if(Const.KEY_VALUE_TRUE.equals(new DBOperator(mContext).getSharedDataValue(Const.KEY_CALLS)))  return;
		PritLog.d(TAG + "onStart()");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		//step1---->
//		if (!mBluetoothAdapter.isEnabled()) {
//			Intent enableIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//			// Otherwise, setup the chat session
//		} else {
//			// if (mChatService == null) setupChat();
//			Intent intent = new Intent();
//			intent.setAction(Const.ACT_ECO_START_RESET);
//			sendBroadcast(intent);
//		}
		
		//step1------>

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public static void setViewPagerItem(int item) {
		mViewPager.setCurrentItem(item);
	}
	
	public static void setViewAdaper() {
		if(mAdapter != null) mAdapter = null;
		mAdapter = new TabAdapter(FM , mContext);
		mViewPager.setAdapter(mAdapter);
	}
	public void OpenRightMenu(View view) {
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.RIGHT);
	}

	private void initEvents() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;
				float scale = 1 - slideOffset;
				float rightScale = 0.8f + scale * 0.2f;
				if (drawerView.getTag().equals("LEFT")) {

					/*
					 * float leftScale = 1 - 0.3f * scale;
					 * 
					 * ViewHelper.setScaleX(mMenu, leftScale);
					 * ViewHelper.setScaleY(mMenu, leftScale);
					 * ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
					 * ViewHelper.setTranslationX(mContent,
					 * mMenu.getMeasuredWidth() * (1 - scale));
					 * ViewHelper.setPivotX(mContent, 0);
					 * ViewHelper.setPivotY(mContent,
					 * mContent.getMeasuredHeight() / 2); mContent.invalidate();
					 * ViewHelper.setScaleX(mContent, rightScale);
					 * ViewHelper.setScaleY(mContent, rightScale);
					 */
					ViewHelper.setTranslationX(mContent,
							mMenu.getMeasuredWidth() * (1 - scale));
				} else {
					ViewHelper.setTranslationX(mContent,
							-mMenu.getMeasuredWidth() * slideOffset);
					ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				}

			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
			}
		});
	}

	private void lockout() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent();
				intent = new Intent(MainActivity.this, LoginFrgmtActivity.class);
				MainActivity.this.startActivity(intent);
				// LoginFrgmtActivity.this.finish();// 结束本Activity
			}
		}, 500);
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.RIGHT);
	}

	long exitTime = 0;// 退出时间

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO 按两次返回键退出应用程序
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 判断间隔时间 大于2秒就退出应用
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// 应用名
				String applicationName = getResources().getString(
						R.string.app_name);
				String msg = "再按一次返回键退出" + applicationName;
				// String msg1 = "再按一次返回键回到桌面";
				Toast.makeText(MainActivity.this, msg, 0).show();
				// 计算两次返回键按下的时间差
				exitTime = System.currentTimeMillis();
			} else {
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				MainActivity.this.startActivity(mHomeIntent);
				// 关闭应用程序
				finish();
				// 返回桌面操作
				// Intent home = new Intent(Intent.ACTION_MAIN);
				// home.addCategory(Intent.CATEGORY_HOME);
				// startActivity(home);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private static final String TAG = "MainActivity";
	private static final boolean D = true;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.headIcon:
			lockout();
			break;
		case R.id.head_btn:
			OpenRightMenu(v);
			break;
		default:
			break;
		}
	}

}
