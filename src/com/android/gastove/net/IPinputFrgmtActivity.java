package com.android.gastove.net;


import com.android.gastove.MainActivity;
import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.Const;
import com.android.gastove.util.InitAppData;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IPinputFrgmtActivity extends FragmentActivity implements
		OnClickListener, OnLongClickListener {
	
	private EditText ip_1,ip_2,ip_3,ip_4;
	private Button mLoginButton, mLoginError, mRegister;
	int selectIndex = 1;
	int tempSelect = selectIndex;
	
	
    private DBOperator mDbOperator;
    private Context mContext;
	private TextWatcher ipnum_watcher;
	
    private static IPinputFrgmtActivity mLoginFrgmt;
    
    public static IPinputFrgmtActivity getInstance(){
        if( mLoginFrgmt == null ){
        	mLoginFrgmt = new IPinputFrgmtActivity();
        }
        return mLoginFrgmt;
    }
    
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		
		mContext= getApplicationContext();
		mDbOperator = new DBOperator(mContext);	
		
		new InitAppData(mContext).doInitApp();
		setContentView(R.layout.activity_ipinput);
		ip_1 = (EditText) findViewById(R.id.ip_1);
		ip_2 = (EditText) findViewById(R.id.ip_2);
		ip_3 = (EditText) findViewById(R.id.ip_3);
		ip_4 = (EditText) findViewById(R.id.ip_4);
		
		initWatcher();

		mLoginButton = (Button) findViewById(R.id.login);
		mLoginError = (Button) findViewById(R.id.login_error);
		mRegister = (Button) findViewById(R.id.register);
		mLoginButton.setOnClickListener(this);
		mLoginError.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		
	}

	/**
	 * 手机号，密码输入控件公用这一个watcher
	 */
	private void initWatcher() {
		ip_1.setText("192");
		ip_2.setText("168");
		ip_3.requestFocus();
		ipnum_watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
//				et_pass.setText("");
				if (s.toString().length() > 0) {
//					bt_username_clear.setVisibility(View.VISIBLE);
				} else {
//					bt_username_clear.setVisibility(View.INVISIBLE);
				}
			}
		};

	}
	
	private void startMainAvtivity() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent();
				intent = new Intent(IPinputFrgmtActivity.this, MainActivity.class);
				IPinputFrgmtActivity.this.startActivity(intent);
				IPinputFrgmtActivity.this.finish();// 结束本Activity
			}
		}, 10000);// 设置执行时间
	} 
	
	private void contactIPOperator() {
		String ipart1 = ip_1.getText().toString().trim() + ".";
		String ipart2 = ip_2.getText().toString().trim() + ".";
		String ipart3 = ip_3.getText().toString().trim() + ".";
		String ipart4 = ip_4.getText().toString().trim();
		
		String IP = ipart1+ ipart2 + ipart3 + ipart4;
		
		if(IP.length()<12) {
			Utils.showToast(mContext, "IP地址输入错误");
			return;
		}
		
		if(!Const.NO_DATA.equals(mDbOperator.getSharedDataValue(Const.SHARED_DEVICE_IP))) {
		    mDbOperator.updateSharedData(Const.SHARED_DEVICE_IP, IP);
		} else {
			//handle later
			mDbOperator.insertSharedData(Const.SHARED_DEVICE_IP, IP);	
		}
//		Toast.makeText(mContext, IP, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.setAction("com.android.test.action");
        sendBroadcast(intent);
        
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.login: // 测试
			contactIPOperator();
//			startMainAvtivity();
			break;
		case R.id.login_error: // 无法登陆(忘记密码了吧)
//			new DataBackUp(LoginFrgmtActivity.this).doBackup();
			//for test
			Utils.showToast(mContext, "Connecting to PC");
			Intent intent = new Intent();
			intent.setAction("com.android.test.action");
	        sendBroadcast(intent);
			break;
		case R.id.register: // 注册新的用户
//			mDbOperator.clearCallsAll();
			Utils.showToast(mContext, "initing app");
//			mDbOperator.insertSharedData(Const.SHARED_DEVICE_IP, IP);
//			mDbOperator.insertSharedData(Const.SHARED_TRANS_START, "0");
//			mDbOperator.insertSharedData(Const.SHARED_TRANS_LAST, "0");
			//for test
			//for trans
			mDbOperator.insertSharedDataTrans(Const.TABLE_RecordTodayIndex, Const.KEY_TRANS_ZERO);
			mDbOperator.insertSharedDataTrans(Const.TABLE_RecordToday, Const.KEY_TRANS_ZERO);
			mDbOperator.insertSharedDataTrans(Const.TABLE_RecordIN, Const.KEY_TRANS_ZERO);
			
			mDbOperator.insertSharedDataTrans(Const.TABLE_remark, Const.KEY_TRANS_ZERO);
			mDbOperator.insertSharedDataTrans(Const.TABLE_PupWinContent, Const.KEY_TRANS_ZERO);
			mDbOperator.insertSharedDataTrans(Const.TABLE_Contacts, Const.KEY_TRANS_ZERO);
			break;

		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
