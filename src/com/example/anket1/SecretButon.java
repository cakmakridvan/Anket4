package com.example.anket1;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecretButon extends Activity implements OnClickListener {

	EditText edt_secret;
	Button btn_secret;
	Button btn_secret_geri,btn_wifi;
	TextView charge_stuation;
	int status;
	String info;
	int level;
	
	Timer timer;
	int userInteractionTimeout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secret_btn);

		edt_secret = (EditText) findViewById(R.id.secret_editText);
		btn_secret = (Button) findViewById(R.id.secret_button);
		btn_secret_geri = (Button) findViewById(R.id.secret_geri_buton);
		charge_stuation = (TextView) findViewById(R.id.charge_state);
		this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		btn_secret.setOnClickListener(this);
		btn_secret_geri.setOnClickListener(this);
		
		btn_wifi = (Button) findViewById(R.id.btn_wifi);
		btn_wifi.setOnClickListener(this);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (userInteractionTimeout == 60) {
					// Do your @Override
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					intent.putExtra("EXIT", false);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

				}
				userInteractionTimeout++;
			}
		}, 0, 1000);
		

		if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA)) {
			Toast.makeText(getApplicationContext(), "3g", 1000).show();
			Log.d("Type", "3g");// for 3g HSDPA networktype will be return as
								// per testing(real) in device with 3g enable
								// data
			// and speed will also matters to decide 3g network type
		} else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP)) {
			Toast.makeText(getApplicationContext(), "4g", 1000).show();
			Log.d("Type", "4g"); // /No specification for the 4g but from wiki
									// i found(HSPAP used in 4g)
									// http://goo.gl/bhtVT
		} else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS)) {
			Toast.makeText(getApplicationContext(), "GPRS", 1000).show();
			Log.d("Type", "GPRS");
		} else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE)) {
			Toast.makeText(getApplicationContext(), "Edge 2g", 1000).show();
			Log.d("Type", "EDGE 2g");
		}
	}

	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			level = intent.getIntExtra("level", 0);
			info = "Battery Level: " + level + "%\n";
			// charge_stuation.setText(String.valueOf(level) + "%");
			status = intent.getIntExtra("status", 0);
			info += ("Status: " + getStatusString(status) + "\n");
			charge_stuation.setText(info);

		}
	};

	private String getStatusString(int status) {
		String statusString = "Unknown";

		switch (status) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			statusString = "Charging";
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			statusString = "Discharging";
			break;
		case BatteryManager.BATTERY_STATUS_FULL:
			statusString = "Full";
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			statusString = "Not Charging";
			break;
		}

		return statusString;
	}

	// Motion
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View view = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (view instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	@Override
	public void onClick(View v) {

		String getText = edt_secret.getText().toString();

		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.secret_button:

			if (getText.equals("Spintek")) {

				// Intent i = getBaseContext().getPackageManager()
				// .getLaunchIntentForPackage( getBaseContext().getPackageName()
				// );
				// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(i);

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

				edt_secret.setText("");
				
				timer.cancel();
			}

			else
				Toast.makeText(getApplicationContext(), "Şifre yanlış",
						Toast.LENGTH_SHORT).show();

			break;

		case R.id.secret_geri_buton:

			// finish();
			// overridePendingTransition(0, 0);

			Intent start_again = new Intent(getApplicationContext(),
					MainActivity.class);
			start_again.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(start_again);
			
			timer.cancel();

			break;
			
		case R.id.btn_wifi:

			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			//beep(5);
			
			//audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                   // AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);


			break;

		}

	}

}
