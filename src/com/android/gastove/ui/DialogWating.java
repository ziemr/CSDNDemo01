package com.android.gastove.ui;

import com.android.gastove.net.WifiService;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class DialogWating {

	private static DialogWating mWating;
	public static DialogWating getInstance(Activity activity) {
		if (mWating == null) {
			mWating = new DialogWating();
		}
		return mWating;
	}
	
//	pub
//	
//	ProgressDialog	mProgressDialog = ProgressDialog.show(Context, "Loading...", "Please wait...", true, false);
}
