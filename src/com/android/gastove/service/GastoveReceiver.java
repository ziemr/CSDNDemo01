package com.android.gastove.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.Const;


public class GastoveReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		if(Const.KEY_VALUE_FALSE.equals(new DBOperator(context).getSharedDataValue(Const.KEY_CALLS))) {
		
		String action = intent.getAction();
		
//		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
//            startService(context, intent, Const.ACT_ECO_START_BOOT_COMPLETED);
//		} else 
			if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
			startService(context, intent, Const.ACT_ECO_BLUETOOTH_STATE_CHANGED);
		} else if(Const.ACT_ECO_START_RESET.equals(action)) {
			startService(context, intent, action);
		} else if("com.android.test.action".equals(action)){
			startService(context, intent, action);
		} else if(Const.ACT_GAS_LOCK.equals(action)) {
			startService(context, intent, action);
		} else if(Const.ACT_GAS_SCREEN_OFF.equals(action) || 
				  Intent.ACTION_SCREEN_ON.equals(action)  ||
				  intent.ACTION_USER_PRESENT.equals(action)) {
			startService(context, intent, action);
		} 
//		}
	}
    private void startService(Context context, Intent intext, String action){
        Intent serviceIntent = new Intent(context, GastoveService.class);
        serviceIntent.setAction(action);
        serviceIntent.putExtras(intext);
        context.startService(serviceIntent);
    }
    private void startApp(final Context context, String action) {
        /* startActivity */
//        Intent intent = new Intent(context, DeviceListActivity.class);
//        intent.setAction(action);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        PritLog.d("startApp() action=" + action);
    }

    private void sendBroadcast(final Context context, String action) {
        /* sendBroadcast */
        Intent sendIntent = new Intent();
        sendIntent.setAction(action);
        context.sendBroadcast(sendIntent);
        PritLog.d("sendBroadcast() action=" + action);
    }
    private void setDataSettings(final int index, boolean value) {
        PritLog.d("setDataSettings() index=" + index + ", value=" + value);
        if(value){
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        mDbOperate.setFirstScreenStatus(index, Const.CHECK_ON);
                    } catch (Exception e) {
                        PritLog.e("setDataSettings()", e);
                    }
                }
            })).start();
        }
    }
}
