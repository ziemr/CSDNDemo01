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
import com.android.gastove.provider.DBOperator;
import com.android.gastove.settings.ContactsInfo;
import com.android.gastove.settings.ContactsListActivity;
import com.android.gastove.settings.ContactsListActivitytest;
import com.android.gastove.util.Const;
import com.android.gastove.util.DataBackUp;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;


public class DatebaseInfoActivity extends Activity implements OnClickListener{
	
//	private RelativeLayout more_restore,more_databack,more_exit,more_contact_send,more_contacts,more_deldb,more_help;
	private ToggleButton isLock,iscallstatus;
	private TextView headTV;
	private String TAG = "MoreActivity";
	
	private DBOperator mDBOperator;
	private Context mContext;
	private String dbcalls;
	private TextView txt_more_restore,txt_more_databack,txt_more_exit,txt_more_contact_send,txt_more_contacts,txt_more_deldb,txt_more_help;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dbinfo);
		mContext = getApplicationContext();
		mDBOperator = new DBOperator(mContext);
		txt_more_restore = (TextView)findViewById(R.id.txt_more_restore);
		txt_more_databack = (TextView)findViewById(R.id.txt_more_databack);
		txt_more_exit = (TextView)findViewById(R.id.txt_more_exit);
		txt_more_contacts = (TextView)findViewById(R.id.txt_more_contacts);
		txt_more_contact_send= (TextView)findViewById(R.id.txt_more_contact_send);
		txt_more_deldb= (TextView)findViewById(R.id.txt_more_deldb);
		txt_more_help= (TextView)findViewById(R.id.txt_more_help);
//		more_restore.setOnClickListener(this);
//		more_databack.setOnClickListener(this);
//		more_exit.setOnClickListener(this);
//		more_contacts.setOnClickListener(this);
//		more_contact_send.setOnClickListener(this);
//		more_deldb.setOnClickListener(this);
//		more_help.setOnClickListener(this);
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
//		iscallstatus.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				Log.i(TAG, "开关按钮状态=" + isChecked);
//				if (isChecked) {
//					new DBOperator(mContext).updateSharedData(Const.KEY_CALLS, Const.KEY_VALUE_TRUE);
//					more_contact_send.setVisibility(View.VISIBLE);
//					Utils.showToast(mContext, getString(R.string.more_calls_home));
//					// hideing
//				} else {
//					new DBOperator(mContext).updateSharedData(Const.KEY_CALLS, Const.KEY_VALUE_FALSE);
//					more_contact_send.setVisibility(View.GONE);
//					Utils.showToast(mContext, getString(R.string.more_calls_send));
//				}
//				Log.i("togglebutton", "" + isChecked);
//			}
//		});
		headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText("------");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		txt_more_restore.setText("RECORDTODAYINDEX  "+mDBOperator.gettableCount(Const.TABLE_RecordTodayIndex));//RECORDTODAYINDEX
		txt_more_databack.setText("WHRECORDIN  "+mDBOperator.gettableCount(Const.TABLE_WHRecordIN));//WHRECORDIN
		txt_more_exit.setText("RECORDTODAYINDEX  "+mDBOperator.gettableCount(Const.TABLE_RecordTodayIndex));
		txt_more_contacts.setText("RECORDTODAY  "+mDBOperator.gettableCount(Const.TABLE_RecordToday)); //RECORDTODAY
		txt_more_contact_send.setText("RECORDIN  "+mDBOperator.gettableCount(Const.TABLE_RecordIN)); //recordin
		txt_more_deldb.setText("WHRECORDINDEX  "+mDBOperator.gettableCount(Const.TABLE_WHRecordIndex));//WHRECORDINDEX
		txt_more_help.setText("WINCONTENT  "+mDBOperator.gettableCount(Const.TABLE_PupWinContent)); //WINCONTENT
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
			Intent intentsendcon = new Intent(DatebaseInfoActivity.this,ContactsListActivity.class);
			startActivity(intentsendcon);
			DatebaseInfoActivity.this.finish();
			break;
		case R.id.more_contacts :
			Intent intentContact = new Intent(DatebaseInfoActivity.this,ContactsInfo.class);
			startActivity(intentContact);
			DatebaseInfoActivity.this.finish();
			break;
		case R.id.more_help :
			
			break;
		case R.id.more_deldb :
			DelDB_SendConDialog(getString(R.string.dialog_mms_deldb));
			break;
		case R.id.more_exit:
			DatebaseInfoActivity.this.finish();
			break;
			
		}
	}
	private void doBackUpData() {
    	String dialogmessage = getString(R.string.dialog_mms_backup)+ Const.KEY_NEXTLINE+ getString(R.string.dialog_mms_backup_time);
    	try {
    		dialogmessage += new SharedPrefsData(DatebaseInfoActivity.this).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
    	}catch(Exception e){
    		dialogmessage += "never buckup";
    	}
       
    	new AlertDialog.Builder(DatebaseInfoActivity.this)
        .setIcon(android.R.drawable.ic_dialog_alert)   
        .setTitle(R.string.dialog_title_warnning)   
        .setMessage(dialogmessage)
        .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(new DataBackUp(DatebaseInfoActivity.this).doBackup()) {
					new SharedPrefsData(DatebaseInfoActivity.this).saveSharedData
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
    		dialogmessage += new SharedPrefsData(DatebaseInfoActivity.this).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
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
							new DataBackUp(DatebaseInfoActivity.this).doRestore();
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
						String inputstr = et_dialog_confirmphoneguardpswd.getText().toString().trim();
						if (Const.PAS_DEL_DB.endsWith(inputstr)) {
							
							
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
}
