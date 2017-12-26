package com.android.gastove.service;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmsThread extends Thread {
	MediaPlayer mMediaPlayer;
	Context mContext;

	public AlarmsThread(Context mContext) {
		// TODO 自動生成されたコンストラクター・スタブ
		mMediaPlayer = new MediaPlayer();
		this.mContext = mContext;
	}

	@Override
	public void run() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	
		try {
			mMediaPlayer.setDataSource(mContext, alert);
//			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
//			audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
//					audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) ,
//					AudioManager.FLAG_SHOW_UI);
//			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(true);
				mMediaPlayer.prepare();
//			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.start();
		
		try {
			Thread.sleep(5000);
			mMediaPlayer.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void StopAlarmRing() {
		mMediaPlayer.stop();
	}

}
