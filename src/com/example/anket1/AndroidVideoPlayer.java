package com.example.anket1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

//Implement SurfaceHolder interface to Play video
//Implement this interface to receive information about changes to the surface
public class AndroidVideoPlayer extends Activity implements
		SurfaceHolder.Callback, OnClickListener {

	SharedPreferences app_preferences;
	SharedPreferences.Editor editor;
	String old_value_Icerik;
	String old_value_Icerik2;

	String Icerik = ""; // Url
	String Icerik2 = ""; // Name
	HttpGet httpget;
	String jsn = "";

	VideoView vw;

	private static final String TAG = "UpdaterService";
	File SDCardRoot;
	File SDCardRootCheck;
	Handler asyncHandler = new Handler();

	String link = "http://htvimg.hurriyet.com.tr/HurriyetTV/Videos/99714/480/F8BF7AF1.jpg";
	String videoLink = "http://hurriyettv.cubecdn.net/2014/11/20/htv_99711_240p.mp4";
	String videoLink2 = "http://hurriyettv.cubecdn.net/2013/03/12/Viplay_14560_240p.mp4";
	String videoLink3 = "http://hurriyettv.cubecdn.net/2013/03/12/Viplay_14462_240p.mp4";
	String videoLink4 = "http://www.youtube.com/watch?v=C0DPdy98e4c";
	String name = "vidyo2.mp4";
	String name2 = "vidyo23.mp4";
	String name3 = "vidyo43.mp4";
	String name4 = "vidyo53.mp4";

	WakeLock wakeLock;

	MediaPlayer mediaPlayer;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean pausing = false;;

	Timer timer;

	int maxVolume = 50;

	int userInteractionTimeout;

	Button sound;

	int currentVolume;

	String url = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

	/** Called when the activity is first created. */

	ConnectivityManager cm;

	NetworkInfo activeNetwork;
	boolean isConnected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		try {
			// REQUIRES ROOT

			Build.VERSION_CODES vc = new Build.VERSION_CODES();
			Build.VERSION vr = new Build.VERSION();
			String ProcID = "79"; // HONEYCOMB AND OLDER

			// v.RELEASE //4.0.3
			if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
				ProcID = "42"; // ICS AND NEWER
			}

			// REQUIRES ROOT
			Process proc = Runtime.getRuntime().exec( 
					new String[] {
							"su",
							"-c",
							"service call activity " + ProcID
									+ " s16 com.android.systemui" }); // WAS
																		// 79
			proc.waitFor();

		} catch (Exception ex) {
			// Toast.LENGTH_LONG).show();
		}

		setContentView(R.layout.main);

		app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

		old_value_Icerik2 = app_preferences.getString("value_name", "");

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		vw = (VideoView) findViewById(R.id.videoview);

		// it checks internet connection........
		cm = (ConnectivityManager) getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);

		activeNetwork = cm.getActiveNetworkInfo();
		isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		Log.i("@mle", "" + isConnected);

		SDCardRoot = new File("/mnt/sdcard/" + "/videos");
		if (!SDCardRoot.exists()) {
			SDCardRoot.mkdir();
		}
		final File[] videos = SDCardRoot.listFiles();
		for (int i = 0; i < videos.length; i++) {
			videos[i].setExecutable(true);
			Log.d("blaaa", "............." + videos[i].canExecute());
			Log.d("blaaa", "............." + videos[i].getAbsolutePath());
		}

		if (isConnected == true) {

			AsyncCallBanner banner = new AsyncCallBanner();
			// Call execute
			try {
				banner.execute().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// old_value_Icerik = app_preferences.getString("value_url",
			// Icerik);
			// Log.i("Video_Url_old_Value", "" + old_value_Icerik);

			Log.i("Video_name_old_Value", "" + old_value_Icerik2);

			editor = app_preferences.edit();
			editor.putString("value_name", Icerik2);
			editor.commit(); // Very important

			Log.i("Video Url:", "" + Icerik);
			Log.i("Video Name:", "" + Icerik2);

			// TODO Auto-generated method stub

			// deleteDirectory(SDCardRoot);

			SDCardRootCheck = new File("/mnt/sdcard/" + "/videos/" + Icerik2);

			Log.i("SDCardCeck", "" + "" + SDCardRootCheck.exists());

			if (!SDCardRootCheck.exists()) {

				new DownloadTask(this).execute();

			} else if (SDCardRootCheck.exists()) {

				vw.setVideoPath("/mnt/sdcard/videos/" + Icerik2);
				vw.start();

				// video finish listener
				vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) { // not playVideo
						// playVideo();

						mp.start();
					}
				});

			}
		} else if (isConnected == false) {

			vw.setVideoPath("/mnt/sdcard/videos/" + old_value_Icerik2);
			vw.start();

			// video finish listener
			vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) { // not playVideo
					// playVideo();

					mp.start();
				}
			});

		}

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"Full Wake Lock");

		sound = (Button) findViewById(R.id.btn_volume);
		sound.setOnClickListener(this);
		sound.setVisibility(View.VISIBLE);
		sound.setBackgroundColor(Color.TRANSPARENT);

		// AudioManager audio = (AudioManager)
		// getSystemService(Context.AUDIO_SERVICE);
		// currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		/*
		 * timer = new Timer(); timer.scheduleAtFixedRate(new TimerTask() {
		 * 
		 * @Override public void run() { if (userInteractionTimeout == 15) { //
		 * Do your @Override
		 * 
		 * timer.cancel(); Intent intent = new Intent(getApplicationContext(),
		 * MainActivity.class); intent.putExtra("EXIT", false);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent);
		 * 
		 * } userInteractionTimeout++; } }, 0, 1000);
		 */

		getWindow().setFormat(PixelFormat.UNKNOWN);

		// Displays a video file.

		// String uriPath = "android.resource://com.example.anket1/"
		// + R.raw.ucz_video;
		// Uri uri = Uri.parse(uriPath);
		// mVideoView.setVideoURI(uri);

		// mVideoView.requestFocus();

		/*
		 * if (isConnected == true) {
		 * 
		 * mVideoView.setVideoPath(url);
		 * 
		 * mVideoView.start();
		 * 
		 * // video finish listener mVideoView .setOnCompletionListener(new
		 * MediaPlayer.OnCompletionListener() {
		 * 
		 * @Override public void onCompletion(MediaPlayer mp) { // not playVideo
		 * // playVideo();
		 * 
		 * mp.start(); } });
		 * 
		 * }
		 * 
		 * else if (isConnected == false) {
		 * 
		 * String uriPath = "android.resource://com.example.anket1/" +
		 * R.raw.ucz_video; Uri uri = Uri.parse(uriPath);
		 * mVideoView.setVideoURI(uri); mVideoView.start();
		 * 
		 * // video finish listener mVideoView .setOnCompletionListener(new
		 * MediaPlayer.OnCompletionListener() {
		 * 
		 * @Override public void onCompletion(MediaPlayer mp) { // not playVideo
		 * // playVideo();
		 * 
		 * mp.start(); } }); }
		 */

	}

	public class DownloadTask extends AsyncTask<Void, Void, String> {

		private final ProgressDialog progressDialog;

		private final Context context;

		/**
		 * 
		 * @param context
		 * @param pdfDoc
		 *            the document of the PDF
		 */
		public DownloadTask(Context context) {
			this.context = context;

			progressDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Video Güncelleniyor...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();

			deleteDirectory(SDCardRoot);

			SDCardRoot.mkdir();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			downloadFiles(Icerik, Icerik2);
			return null;
			// download here
		}

		@Override
		protected void onPostExecute(final String result) {
			progressDialog.dismiss();
			vw.setVideoPath("/mnt/sdcard/videos/" + Icerik2);
			vw.start();

			// video finish listener
			vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) { // not playVideo
					// playVideo();

					mp.start();
				}
			});

		}
	}

	synchronized boolean downloadFiles(String link, String videoName) {
		try {
			URL url = new URL(link); // you can write here any link

			long startTime = System.currentTimeMillis();
			Log.d("VideoManager", "download begining");
			Log.d("VideoManager", "download url:" + url);
			Log.d("VideoManager", "downloaded file name:" + videoName);
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(new File(SDCardRoot,
					videoName));
			fos.write(baf.toByteArray());
			fos.close();
			Log.d("VideoManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getDataFromPortal(String remoteUrl, String myString) {
		String data = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(remoteUrl + myString);

		try {
			HttpResponse response1 = httpclient.execute(httpGet);
			InputStream is = response1.getEntity().getContent();

			data = getStringFromInputStream(is);
			return data;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "................internet yok catch 1");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "................internet yok catch 2");
			e.printStackTrace();
		}

		// The underlying HTTP connection is still held by the response
		// object
		// to allow the response content to be streamed directly from the
		// network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST either fully consume the response content or abort
		// request
		// execution by calling HttpGet#releaseConnection().
		return data;
	}

	private String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	// ////////// for Parsing data
	private class AsyncCallBanner extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
			Connect();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");
			// txt1.setText(Icerik);
			Log.i("Last value of Icerik:", "" + Icerik);
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
			dialog = new ProgressDialog(AndroidVideoPlayer.this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.setMessage("Please Wait");

			dialog.setIndeterminate(true);

			dialog.show();
		}

	}

	public String makeURL(String sayi) {
		Log.i("@Log", "MakeUrl1");
		StringBuilder urlString = new StringBuilder();
		// urlString
		// .append("http://176.41.232.106:8989/Playland/webresources/playland/");
		// Log.i("@Log", "MakeUrl2");
		urlString.append(sayi);
		Log.i("@Log", "MakeUrl3");
		return urlString.toString();

	}

	private void Connect() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:7421/AnketServis.asmx/Video");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();

		try {

			// jsonparameter.put("AnketID", "3");

			httppost.setEntity(new StringEntity(jsonparameter.toString(),
					"UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);

			Log.i("@banner_responseString", "" + responseString);

			try {
				returndata = new JSONObject(responseString);
				JSONArray jsonMainNode = returndata.optJSONArray("d");
				int lengthJsonArr = jsonMainNode.length();
				Log.i("@lengthJson", "" + lengthJsonArr);
				int sorusayisi = lengthJsonArr;

				for (int i = 0; i <= 1; i++) {

					if (i == sorusayisi)
						break;
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					try {

						Icerik = jsonChildNode.optString("Path").toString(); // URL

						Icerik2 = jsonChildNode.optString("VName").toString(); // Video
																				// Name

					} catch (Exception e) {

					}
				}

			} catch (JSONException e) {

			}

		} catch (Exception e) {

		}

	}

	public void getInfo(String result) {

		try {

			Log.i("@Log", "GetInfo");
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			Log.i("@Log", "GetInfogiris");
			String encodedString = json.getString("Soru");

			Log.i("txt1", "" + encodedString);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// //////////// end of Parsing

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wakeLock.release();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:

			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra("EXIT", false);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);

			break;

		/*
		 * case MotionEvent.ACTION_MOVE: // finger moves on the screen break;
		 * 
		 * case MotionEvent.ACTION_UP: // finger leaves the screen break;
		 */
		}

		// tell the system that we handled the event and no further processing
		// is required
		return true;
	}

	/*
	 * @Override public void onUserInteraction() { // TODO Auto-generated method
	 * stub super.onUserInteraction();
	 * 
	 * userInteractionTimeout = 0;
	 * 
	 * // Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);
	 * 
	 * }
	 */

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_volume:

			Intent pas = new Intent(getApplicationContext(),
					SetVolumeLevel.class);
			pas.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(pas);

			break;

		default:
			break;
		}

	}
}