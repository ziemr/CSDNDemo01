package com.android.gastove.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import com.android.gastove.R;
import com.android.gastove.net.structureRunnable;
import com.android.gastove.net.dataStructure;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.settings.ContactsInfoSort;
import com.android.gastove.settings.PoPupWidowsDataTree;
import com.android.gastove.settings.RemarkActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.DataBackUp;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;
import com.android.gastove.warehouse.WarHosDataTree;
import com.android.gastove.warehouse.WarHosMainFrgmtActivity;

public class MenuRightFragment extends Fragment implements OnClickListener
{
	
	private ImageView setting_contact;
	private ImageView setting_leaf;
	private ImageView setting_bluetooth;
	private ImageView setting_warehouse;
	
	private Context mContext;
    private OnClickListener setting_contactListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(mContext, ContactsInfoSort.class);
//			intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
//			intent.putExtra(Const.BUNDLE_TOADY,time.substring(0,10) );
			startActivity(intent);
		}
	};
    private OnClickListener setting_leafListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
//			intent.setClass(mContext, PoPupWidowsDataTree.class);
			//test
			intent.setClass(mContext, WarHosMainFrgmtActivity.class);
//			intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
//			intent.putExtra(Const.BUNDLE_TOADY,time.substring(0,10) );
			startActivity(intent);
		}
	};
	
    private OnClickListener setting_bluetoothListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setAction("com.android.test.action");
            getActivity().sendBroadcast(intent);
//            Toast.makeText(getActivity(), "reStart", Toast.LENGTH_LONG);
//            mFragmentManager.popBackStack();
            
		}
	};
	
//	private FragmentManager mFragmentManager; 
//	private FragmentTransaction fragmentTransaction; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
//		mFragmentManager = getFragmentManager(); 
//		fragmentTransaction = mFragmentManager.beginTransaction();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View mView = inflater.inflate(R.layout.menu_right_layout, container, false);
		setting_contact = (ImageView) mView.findViewById(R.id.setting_contact);
//		setting_contact.setOnClickListener(setting_contactListener);
		
		setting_leaf= (ImageView) mView.findViewById(R.id.setting_Leaf);
//		setting_leaf.setOnClickListener(setting_leafListener);
		
		setting_bluetooth = (ImageView) mView.findViewById(R.id.setting_bluetooth);
//		setting_bluetooth.setOnClickListener(setting_bluetoothListener);
		
		setting_warehouse = (ImageView) mView.findViewById(R.id.setting_warehouse);
		
		setting_contact.setOnClickListener(this);
		setting_leaf.setOnClickListener(this);
		setting_bluetooth.setOnClickListener(this);
		setting_warehouse.setOnClickListener(this);
		return mView;
	}
    private void doBackUpData() {
    	String dialogmessage = getString(R.string.dialog_mms_backup)+ Const.KEY_NEXTLINE+ getString(R.string.dialog_mms_backup_time);
    	try {
    		dialogmessage += new SharedPrefsData(getActivity()).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
    	}catch(Exception e){
    		dialogmessage += "never buckup";
    	}
       
    	new AlertDialog.Builder(getActivity())
        .setIcon(android.R.drawable.ic_dialog_alert)   
        .setTitle(R.string.dialog_title_warnning)   
        .setMessage(dialogmessage)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(new DataBackUp(mContext).doBackup()) {
					new SharedPrefsData(getActivity()).saveSharedData
					(Const.SHAREPREFS_DATABUCKUP_TIME, Utils.systemFrmtTime("yy/MM/dd HH:mm"));
				}
			}
		})
        .setNegativeButton(   
                "No", null)
         .create().show();
    }
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.setting_contact:
			intent.setClass(mContext, ContactsInfoSort.class);
			startActivity(intent);
			break;
		case R.id.setting_Leaf:
			intent.setClass(mContext, PoPupWidowsDataTree.class);
			startActivity(intent);
			break;
		case R.id.setting_bluetooth:
			intent.setAction(Const.ACT_ECO_START_RESET);
            getActivity().sendBroadcast(intent);
            
//			new DBOperator(mContext).insertCalls("18953186557");
//			new DBOperator(mContext).insertCalls("18953186556");
			intent.setClass(mContext, WarHosDataTree.class);
			startActivity(intent);
			break;
		case R.id.setting_warehouse:
//			intent.setClass(mContext, WarHosMainFrgmtActivity.class);
//			intent.setClass(mContext, DeviceListActivity.class);
			intent.setClass(mContext, RemarkActivity.class);
			startActivity(intent);
			
			
			
			
//			doBackUpData();
			
			break;
		default:
			break;
		}
	}
}
