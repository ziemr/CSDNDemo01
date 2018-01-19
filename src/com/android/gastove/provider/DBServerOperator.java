package com.android.gastove.provider;


import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;

public class DBServerOperator {
	private ContentResolver mResolver;
	private Uri baseUri;
	private static Uri providerUri;
	private Context mContext;

	static {
		android.net.Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(Const.AUTHORITY_SERVER);
		providerUri = builder.build();
	}

	public DBServerOperator(Context context) {
		try {
			mContext = context;
			mResolver = mContext.getContentResolver();
			baseUri = providerUri;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Uri getUri(String type, String table, String option) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		if (type != null)
			builder.appendQueryParameter("type", type);
		builder.appendQueryParameter("table", table);
		if (option != null)
			builder.appendQueryParameter("option", option);
		return builder.build();
	}
	public Uri getTableUri(String table) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		builder.appendQueryParameter("table", table);
		return builder.build();
	}
	
	
	public void insertRecordin(BodyLine mBodyLine) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

//		int count = gettableCount(Const.TABLE_RecordIN);

//		values.put("_id", count + 1);
		values.put(Const.RECORDIN_COLUMN_RECORDID, mBodyLine.getRecordid());
		values.put("phone", mBodyLine.getPhone());
		values.put("num", mBodyLine.getNum());
		values.put("data1", mBodyLine.getData1());
		values.put("data2", mBodyLine.getData2());
		values.put("data3", mBodyLine.getData3());
		//0104?
		values.put("data4", "0");
		// data5 ---> piece
		values.put("data5", "0");
		values.put("data6", mBodyLine.getRemark());
		values.put("date", mBodyLine.getDate());
		values.put("data7", "-");
//		values.put("modified", mBodyLine.getModified());
		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordIN, Const.KEY_TRANS_IN);
	}
}
