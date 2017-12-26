package com.android.gastove.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.gastove.R;
import com.android.gastove.util.Const;

public class ISMNotification {
    private Context mContext = null;
    public static final int NOTIFICATION_ONGONING = 1001;
    public static final int NOTIFICATION_NORMAL = 1002;
    private Notification notification = null;
    private CharSequence contentText = null;
    int icon=0;
    
    public ISMNotification(Context context) {
        mContext = context;
    }
    
    public void putNotice(int index) {
        Boolean onGoing = true;
        String tmpStr = null;
		switch (index) {
		case Const.net_connect_ng:
			tmpStr = mContext.getString(R.string.not_server);
			icon = R.drawable.ism_notification_yellow;
			break;
		case Const.net_connect_ok:
			tmpStr = mContext.getString(R.string.not_server_found);
			icon = R.drawable.ism_notification;
			break;
		default:
			tmpStr = mContext.getString(R.string.ism_notifi_msg);
			break;
		}
        
        contentText = tmpStr;
        NotificationManager mNotificationManager = (NotificationManager)
        mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = createNotification(null,onGoing);
        mNotificationManager.notify(onGoing ? NOTIFICATION_ONGONING : NOTIFICATION_NORMAL, notification);
    }
    
    // Notification削除
    public void removeNotice() {
        NotificationManager mNotificationManager = (NotificationManager)
        mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ONGONING);
    }

    private Notification createNotification(String action,Boolean onGoing) {
        
//        if (mode==EcoModeConst.ECOMODE1) { // 通常モード（来ないけど）
//            ;
//        } else if (mode==EcoModeConst.ECOMODE3) { // 技あり
//            icon = R.drawable.eco_pictograph_wazaari;
//        } else if (mode==EcoModeConst.ECOMODE4){ // お助け
//            icon = R.drawable.eco_pictograph_otasuke;
//        } else {
////          EcoLog.log(EcoLog.ERROR, TAG, "createNotification() error.");
//        }
//        icon = R.drawable.ism_notification;
        Notification notification = new Notification();

        notification.icon = icon;
        notification.when = 0;
        
        Intent notificationIntent = new Intent();
        
	    notificationIntent.setAction(action);
		
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,0,notificationIntent,0);
        CharSequence contentTitle = mContext.getString(R.string.ism_notifi_title);
        notification.setLatestEventInfo(mContext,contentTitle,contentText,contentIntent);

//        //  フラグを設定しないと通知領域に表示されてしまう
//        notification.flags = notification.flags
//            | Notification.FLAG_NO_CLEAR        // クリアボタンを表示しない（ユーザにクリアさせない）
//            | Notification.FLAG_ONGOING_EVENT;  // 「実行中」領域に表示する
        
        notification.flags = onGoing ? notification.flags| Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT
        		                    :  notification.flags| Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;
        notification.number = 0;
        return notification;
    }}
