package com.android.gastove.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPrefsData {
	String TAG = "SharedPrefsData";
	Context mContext;
	SharedPreferences mSharedData=null;
	SharedPreferences.Editor mEditor =null;
	public SharedPrefsData(Context context) {
		mContext = context;
		mSharedData = PreferenceManager.getDefaultSharedPreferences(mContext);
		mEditor = mSharedData.edit();
	}
    public void saveSharedData(String key,String value) {
         mEditor.putString(key, value);
         mEditor.commit();
    }
    
    public void saveSharedData(String key,boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
   }
    public String getSharedData(String key){
    	String value = null;
    	try {
    		value = mSharedData.getAll().get(key).toString();
    	}catch(Exception e){
    		Log.e(TAG, "null");
    		return Const.NO_DATA;
    	}
    	return value;
    }
    
    public float getSharedDaFloat(String key){
    	float value = 1.0f;
    	try {
    		value = (Float) mSharedData.getAll().get(key);
    	}catch(Exception e){
    		Log.e(TAG, "null");
    	}
    	return value;
    }
    
    public boolean getSharedDaBool(String key){
    	boolean value = false;
    	try {
    		value = (Boolean) mSharedData.getAll().get(key);
    	}catch(Exception e){
    		Log.e(TAG, "null");
    	}
    	return value;
    }
}
