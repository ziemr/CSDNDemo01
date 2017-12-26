package com.android.gastove.ui;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import com.android.gastove.R;
import com.android.gastove.calls.RecentCallsListActivity;
import com.android.gastove.net.IPinputFrgmtActivity;
import com.android.gastove.net.WIFIMsg;
import com.android.gastove.net.WifiService;
import com.android.gastove.net.recordHandleRunnable;
import com.android.gastove.net.structureRunnable;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.test.HttpURLConActivity;
import com.android.gastove.test.netActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;
import com.android.gastove.warehouse.IndexWHRecordFrgmtActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MenuLeftFragment extends Fragment implements OnClickListener
{

	private ImageView one,two,three,moresettings;
	private Context mContext;
	private RadioGroup radioGroup;
	private RelativeLayout more_pupheight,radioGroup_relatvelayout,morehelp;
	private ToggleButton isShowing;
	private String TAG = "MenuLeftFragment";
	
	private DBOperator mOperator;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mOperator = new DBOperator(mContext);
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}
	View mView = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
	    mView = inflater.inflate(R.layout.meun_left_layout, container, false);
		one = (ImageView) mView.findViewById(R.id.one);
		two= (ImageView) mView.findViewById(R.id.two);
        three = (ImageView) mView.findViewById(R.id.three);
        moresettings = (ImageView)mView.findViewById(R.id.menuleft_moresetting);
        morehelp = (RelativeLayout) mView.findViewById(R.id.more_help);
        more_pupheight= (RelativeLayout) mView.findViewById(R.id.more_pupheigh);
        more_pupheight.setOnClickListener(this);
        moresettings.setOnClickListener(this);
        morehelp.setOnClickListener(this);
        radioGroup_relatvelayout = (RelativeLayout) mView.findViewById(R.id.radioGroup_relatvelayout); 
        isShowing =(ToggleButton)mView.findViewById(R.id.togbut_isShowing);
        
		one.setOnClickListener(this);
		two.setOnClickListener(this);

		two.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				startActivity(new Intent(mContext, IPinputFrgmtActivity.class));
				return false;
			}
		});

		three.setOnClickListener(this);
//		three.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				ProgressDialog	mProgressDialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait...for struct", true, false);
//	            new Thread(new structureRunnable(mContext,mProgressDialog)).start();
//				return false;
//			}
//		});
		radioGroup = (RadioGroup) mView.findViewById(R.id.radioGroup_pupheight);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String toastStr = getString(R.string.rid_pupheight);
				SharedPrefsData mSharedData = new SharedPrefsData(mContext);
				switch (checkedId) {
				case R.id.radioGroup_18:
//					mSharedData.saveSharedData(Const.BOTTOM_POPWINDOW_HEIGHT, value);
					mOperator.updateSharedData(Const.KEY_POPHEIGHT, Const.BOTTOM_POPWINDOW_HEIGHT_18);
					toastStr += String.valueOf(Const.PUPWIN_CONTENT_ARRAY_MAX);
					break;
				case R.id.radioGroup_12:
					mSharedData.saveSharedData(Const.BOTTOM_POPWINDOW_HEIGHT, Const.BOTTOM_POPWINDOW_HEIGHT_12);
					mOperator.updateSharedData(Const.KEY_POPHEIGHT, Const.BOTTOM_POPWINDOW_HEIGHT_12);
					toastStr += String.valueOf(Const.PUPWIN_CONTENT_NUM_12);
					break;
				case R.id.radioGroup_15:
					mSharedData.saveSharedData(Const.BOTTOM_POPWINDOW_HEIGHT, Const.BOTTOM_POPWINDOW_HEIGHT_15);
					mOperator.updateSharedData(Const.KEY_POPHEIGHT, Const.BOTTOM_POPWINDOW_HEIGHT_15);
					toastStr += String.valueOf(Const.PUPWIN_CONTENT_NUM_15);
					break;
				default:
					break;
				}
				Utils.showToast(mContext, toastStr);
			}
		});
		isShowing.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(TAG, "开关按钮状态=" + isChecked);

				if (isChecked) {
//					Toast.makeText(getActivity(), "tue", Toast.LENGTH_LONG).show();
					// hideing
				} else {
					passwordDialog(buttonView);
				}
				Log.i("togglebutton", "" + isChecked);
			}
		});
		return mView;
	}
	private void passwordDialog(final CompoundButton view) {
		String dialogmessage = getString(R.string.pwd);
    	
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(getActivity(), R.layout.dialog_rename, null);
		final TextView et_dialog_mms = (TextView) alertDialogView.findViewById(R.id.text_mms);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		
		et_dialog_mms.setVisibility(View.VISIBLE);
		et_dialog_mms.setText(dialogmessage + "999 ");
		alertDialog.setTitle(getResources().getString(R.string.dialog_title_warnning));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Log.i(TAG, "getReNameDialogPositiveButton start");
						String str = et_dialog_confirmphoneguardpswd.getText().toString().trim();
						if (str.equals("999")) {
							//show
							view.setChecked(false);
						} else {
							view.setChecked(true);
							Toast.makeText(getActivity().getApplicationContext(), "PassWord is error", Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		tempDialog.show();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.one:
	        //tep1-->
//			intent.setClass(mContext, netActivity.class);
//			startActivity(intent);
	        //tep1---<
//			new SharedPrefsData(mContext).saveSharedData(Const.SHARED_IN_OUT, Const.SHARED_OUT);
//			MainActivity.setViewAdaper();
			
	
//			if(Const.KEY_VALUE_TRUE.equals(new DBOperator(mContext).getSharedDataValue(Const.KEY_CALLS))) {
			intent.setClass(mContext, RecentCallsListActivity.class);
			startActivity(intent);
			
//			}
			break;
		case R.id.two:
			
//			new SharedPrefsData(mContext).saveSharedData(Const.SHARED_IN_OUT, Const.SHARED_IN);
//			intent.setClass(mContext, WarHosDataTree.class);
//			MainActivity.setViewAdaper();
			DBOperator dbOperator = new DBOperator(mContext);
			String flag = dbOperator.getSharedDataValue(Const.TABLE_RecordIN);
			if (Const.KEY_TRANS_IN.endsWith(flag)) {
				//trans for recordin insert
				StringBuilder sb = new StringBuilder();
				sb.setLength(0);
				sb.append(Const.SOCKET_TRANS_TABLECOUNT);
//				sb.append(Const.KEY_DELIMITER);
//				sb.append(WifiService.SOCKET_RECORDIN);
				try {
					WIFIMsg.sendMsg(sb.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				WifiMsg.readMsg(buffer, bytes);
				
				
				 //kind 01/10
//				ProgressDialog	mProgressDialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", true, false);
//	            new Thread(new recordHandleRunnable(mContext,mProgressDialog)).start();
			} else if(Const.KEY_TRANS_UP.equals(flag)) {
				
			}
           
			break;
		case R.id.three:
//			new DBOperator(mContext).Delete();
			intent.setClass(mContext, IndexWHRecordFrgmtActivity.class);
			startActivity(intent);
			
//			int k = new DBOperator(mContext).testforDB();
//			Utils.showToast(mContext, String.valueOf(k));
//			new DBOperator(mContext).deleteRecordfortest();
			break;
		case R.id.more_help:
//			Intent intentR = new Intent();
			intent.setClass(mContext, ContactGroupActivity.class);
//			intentR.putExtra(Const.BUNDLE_RECORD_ID, recordid);
			startActivity(intent);
			break;
		case R.id.more_pupheigh:
			if (radioGroup_relatvelayout.getVisibility() == View.GONE) {
				String temp = new DBOperator(mContext)
				.getSharedDataValue(Const.KEY_POPHEIGHT);
				
		        String temparr[] = temp.split(Const.KEY_DELIMITER);
		        int radioID = 0;
				switch (Integer.parseInt(temparr[1])) {
				case 12:
					radioID = R.id.radioGroup_12;
					break;
				case 15:
					radioID = R.id.radioGroup_15;
					break;
				case 18:
					radioID = R.id.radioGroup_18;

					break;
				default:
					break;

				}
		        ((RadioButton) mView.findViewById(radioID)).setChecked(true);   
        //		radioGroup.check(radioID);
		
				radioGroup_relatvelayout.setVisibility(View.VISIBLE);
			} else {
				radioGroup_relatvelayout.setVisibility(View.GONE);
			}
			break;
		case R.id.menuleft_moresetting:
			intent.setClass(mContext, MoreActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
