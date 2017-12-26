package com.android.gastove.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.gastove.MainActivity;
import com.android.gastove.R;
import com.android.gastove.net.structureRunnable;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.settings.ContactsInfo;
import com.android.gastove.settings.ContactsListActivity;
import com.android.gastove.settings.ContactsListActivitytest;
import com.android.gastove.util.Const;
import com.android.gastove.util.DataBackUp;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;


public class MoreActivity extends Activity implements OnClickListener{
	
	private RelativeLayout more_restore,more_databack,more_exit,more_contact_send,more_contacts,more_deldb,more_help;
	private ToggleButton isLock,iscallstatus;
	private TextView headTV;
	private String TAG = "MoreActivity";
	
	private DBOperator mDBOperator;
	private Context mContext;
	private String dbcalls;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_more);
		mContext = getApplicationContext();
		more_restore = (RelativeLayout)findViewById(R.id.more_restore);
		more_databack = (RelativeLayout)findViewById(R.id.more_databack);
		more_exit = (RelativeLayout)findViewById(R.id.more_exit);
		more_contacts = (RelativeLayout)findViewById(R.id.more_contacts);
		more_contact_send= (RelativeLayout)findViewById(R.id.more_contact_send);
		more_deldb= (RelativeLayout)findViewById(R.id.more_deldb);
		more_help= (RelativeLayout)findViewById(R.id.more_help);
		more_restore.setOnClickListener(this);
		more_databack.setOnClickListener(this);
		more_exit.setOnClickListener(this);
		more_contacts.setOnClickListener(this);
		more_contact_send.setOnClickListener(this);
		more_deldb.setOnClickListener(this);
		more_help.setOnClickListener(this);
		isLock =(ToggleButton)findViewById(R.id.togbut_islock);
//		isLock.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				Log.i(TAG, "开关按钮状态=" + isChecked);
//				if (isChecked) {
//					
//					MoreActivity.this.sendBroadcast(new Intent().setAction(Const.ACT_GAS_LOCK).putExtra(Const.BUNDLE_LOCK_FLAG, true));
//					// hideing
//				} else {
//					MoreActivity.this.sendBroadcast(new Intent().setAction(Const.ACT_GAS_LOCK).putExtra(Const.BUNDLE_LOCK_FLAG, false));
//				}
//				Log.i("togglebutton", "" + isChecked);
//			}
//		});
		iscallstatus =(ToggleButton)findViewById(R.id.togbut_iscallstatus);
		iscallstatus.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(TAG, "开关按钮状态=" + isChecked);
				if (isChecked) {
					new DBOperator(mContext).updateSharedData(Const.KEY_CALLS, Const.KEY_VALUE_TRUE);
					more_contact_send.setVisibility(View.VISIBLE);
					Utils.showToast(mContext, getString(R.string.more_calls_home));
					// hideing
				} else {
					new DBOperator(mContext).updateSharedData(Const.KEY_CALLS, Const.KEY_VALUE_FALSE);
					more_contact_send.setVisibility(View.GONE);
					Utils.showToast(mContext, getString(R.string.more_calls_send));
				}
				Log.i("togglebutton", "" + isChecked);
			}
		});
		headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(getString(R.string.more));
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbcalls = new DBOperator(getApplicationContext()).getSharedDataValue(Const.KEY_CALLS);
		if (Const.KEY_VALUE_TRUE.equals(dbcalls)) {
			iscallstatus.setChecked(true);
			more_contact_send.setVisibility(View.VISIBLE);
		} else {
			iscallstatus.setChecked(false);
			more_contact_send.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_restore:
			RestoreDialog();
			break;
		case R.id.more_databack :
			doBackUpData();
			break;
		case R.id.more_contact_send :
//			DelDB_SendConDialog(getString(R.string.dialog_mms_sendcon));
			Intent intentsendcon = new Intent(MoreActivity.this,ContactsListActivity.class);
			startActivity(intentsendcon);
			MoreActivity.this.finish();
			break;
		case R.id.more_contacts :
			Intent intentContact = new Intent(MoreActivity.this,ContactsInfo.class);
			startActivity(intentContact);
			MoreActivity.this.finish();
			break;
		case R.id.more_help :
			Intent intentDBinfo = new Intent(MoreActivity.this,DatebaseInfoActivity.class);
			startActivity(intentDBinfo);
//			MoreActivity.this.finish();
			break;
		case R.id.more_deldb :
			DelDB_SendConDialog(getString(R.string.dialog_mms_deldb));
			break;
		case R.id.more_exit:
//			MoreActivity.this.finish();
			//check connect
			
			ProgressDialog	mProgressDialog = ProgressDialog.show(MoreActivity.this, "Loading...", "Please wait...for struct", true, false);
            new Thread(new structureRunnable(mContext,mProgressDialog)).start();
			break;
			
		}
	}
	private void doBackUpData() {
    	String dialogmessage = getString(R.string.dialog_mms_backup)+ Const.KEY_NEXTLINE+ getString(R.string.dialog_mms_backup_time);
    	try {
    		dialogmessage += new SharedPrefsData(MoreActivity.this).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
    	}catch(Exception e){
    		dialogmessage += "never buckup";
    	}
       
    	new AlertDialog.Builder(MoreActivity.this)
        .setIcon(android.R.drawable.ic_dialog_alert)   
        .setTitle(R.string.dialog_title_warnning)   
        .setMessage(dialogmessage)
        .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(new DataBackUp(MoreActivity.this).doBackup()) {
					new SharedPrefsData(MoreActivity.this).saveSharedData
					(Const.SHAREPREFS_DATABUCKUP_TIME, Utils.systemFrmtTime("yy/MM/dd HH:mm"));
				}
			}
		})
        .setNegativeButton(android.R.string.cancel, null)
        .create().show();
    }
	private void RestoreDialog() {
		String dialogmessage = getString(R.string.dialog_mms_restore)+ Const.KEY_NEXTLINE + getString(R.string.dialog_mms_restore_time);
    	try {
    		dialogmessage += new SharedPrefsData(MoreActivity.this).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
    	}catch(Exception e){
    		dialogmessage += "...";
    	}
    	
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final TextView et_dialog_mms = (TextView) alertDialogView.findViewById(R.id.text_mms);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		
		et_dialog_mms.setVisibility(View.VISIBLE);
		et_dialog_mms.setText(dialogmessage);
		alertDialog.setTitle(getResources().getString(R.string.dialog_title_warnning));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Log.i(TAG, "getReNameDialogPositiveButton start");
						String inputstr = et_dialog_confirmphoneguardpswd.getText().toString().trim();
						if (inputstr.equals("999")) {
							new DataBackUp(MoreActivity.this).doRestore();
						} else {
							Toast.makeText(getApplicationContext(), "PassWord is error", Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		tempDialog.show();
	}
	
	private void DelDB_SendConDialog(String msg) {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final TextView et_dialog_mms = (TextView) alertDialogView.findViewById(R.id.text_mms);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		et_dialog_confirmphoneguardpswd.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_dialog_mms.setVisibility(View.VISIBLE);
		et_dialog_mms.setText(msg);
		alertDialog.setTitle(getResources().getString(R.string.dialog_title_warnning));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Log.i(TAG, "getReNameDialogPositiveButton start");
						String inputstr = et_dialog_confirmphoneguardpswd.getText().toString().trim();
						if (Const.PAS_DEL_DB.endsWith(inputstr)) {
							handle();
//							new DBOperator(getApplicationContext()).DangerWarning();
						} else {
							Toast.makeText(mContext, "PassWord is error", Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		tempDialog.show();
	}
	private void handle() {
		ProgressDialog progressDialog = ProgressDialog.show(MoreActivity.this, getString(R.string.dialog_title_warnning), getString(R.string.dialog_mms_handle), true, false);
		new Thread(new MmsgRunnable(mContext,progressDialog)).start();
	}
	}
	class MmsgRunnable implements Runnable {
		private Context context;
		ProgressDialog progressDialog = null;
	    private Handler handler = new Handler(){
	  	
	        @Override  
	        public void handleMessage(Message msg) { 
			switch (msg.what) {
			case 0:
				progressDialog.setMessage("恢复成功，正在重新启动");
				break;
			case 1:
				//关闭ProgressDialog  
	        	progressDialog.dismiss();
	        	new DBOperator(context).insertSharedData(Const.KEY_INIT, Const.KEY_VALUE_TRUE);
	        	Intent intent = new Intent();
	        	intent.setClassName("com.android.gastove","com.android.gastove.ui.LoginFrgmtActivity" );
	        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	context.startActivity(intent);
				break;
			default:
				break;
			}
	            
	        }}; 
		
		public MmsgRunnable(Context context ,ProgressDialog pDialog) {
			progressDialog = pDialog;
			this.context = context;
		}
		
		@Override
		public void run() {
				try {
					new DBOperator(context).DangerWarning();
					handler.sendEmptyMessage(0);
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			handler.sendEmptyMessage(1);
		}
}
