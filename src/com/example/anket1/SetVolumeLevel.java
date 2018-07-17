package com.example.anket1;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SetVolumeLevel extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private SeekBar volumeSeekbar = null;
	private AudioManager audioManager = null;

	Button go_back,btn_wifi;
	// Button delete_video_file;

	File SDCardRoot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.setvolume);
		go_back = (Button) findViewById(R.id.btn_geri);
		go_back.setOnClickListener(this);
		
		btn_wifi = (Button) findViewById(R.id.btn_wifi);
		btn_wifi.setOnClickListener(this);

		// delete_video_file = (Button) findViewById(R.id.delete_video_file);
		// delete_video_file.setOnClickListener(this);

		// SDCardRoot = new File("/mnt/sdcard/" + "/videos");

		initControls();
	}

	private void initControls() {
		try {
			volumeSeekbar = (SeekBar) findViewById(R.id.seekBar);
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			volumeSeekbar.setMax(audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			volumeSeekbar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));

			volumeSeekbar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
						}

						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
						}

						@Override
						public void onProgressChanged(SeekBar arg0,
								int progress, boolean arg2) {
							audioManager.setStreamVolume(
									AudioManager.STREAM_MUSIC, progress, 0);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stubs

		switch (v.getId()) {
		case R.id.btn_geri:

			// Toast.makeText(getApplicationContext(), "Please Wait",
			// 2000)
			// .show();
			//
			// Intent go_video_player = new Intent(getApplicationContext(),
			// AndroidVideoPlayer.class);
			// startActivity(go_video_player);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Intent i = getBaseContext().getPackageManager()
					.getLaunchIntentForPackage(
							getBaseContext().getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			break;
			
		case R.id.btn_wifi:

			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			//beep(5);
			
			//audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                   // AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);


			break;

		/*
		 * case R.id.delete_video_file:
		 * 
		 * if (SDCardRoot.exists()) {
		 * 
		 * deleteDirectory(SDCardRoot);
		 * 
		 * Toast.makeText(getApplicationContext(), "Dosya Silindi", 1000)
		 * .show(); } else if (!SDCardRoot.exists()) {
		 * 
		 * Toast.makeText(getApplicationContext(), "Dosya Bulunamadı",
		 * 1000).show();
		 * 
		 * }
		 * 
		 * break;
		 */

		default:
			break;
		}

	}

	/*
	 * public static boolean deleteDirectory(File path) { if (path.exists()) {
	 * File[] files = path.listFiles(); if (files == null) { return true; } for
	 * (int i = 0; i < files.length; i++) { if (files[i].isDirectory()) {
	 * deleteDirectory(files[i]); } else { files[i].delete(); } } } return
	 * (path.delete()); }
	 */

}
