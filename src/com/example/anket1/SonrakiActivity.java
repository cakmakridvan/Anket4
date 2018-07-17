package com.example.anket1;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.net.NetworkInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SonrakiActivity extends Activity {

	static int x = 0; // must be static

	Bundle get_data;
	int bas_i;
	TextView txt, text2;

	// Button secretbtn;

	String Type;
	int TypeID;
	int GrpID;
	int id;
	
	int c,d;
	ProgressDialog dialog;
	boolean connected = false;

	int sorusayisi = 0;
	int cevaplanan = 0;
	boolean hata = false;

	int akis_soru_sorusayisi = 0;

	String last_Type_inmethod;

	Bundle data_gonder;

	// Dynamic Buttons

	Button btn1;
	Button btn2;
	Button btn3;
	Button btn4;
	Button btn5;

	private static Handler handler;
	private Runnable runnable;

	Timer timer;

	int userInteractionTimeout;

	Thread thread;
	
	String jsonMainNode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sonraki_olay);

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

		btn1 = new Button(getApplicationContext());
		btn2 = new Button(getApplicationContext());
		btn3 = new Button(getApplicationContext());
		btn4 = new Button(getApplicationContext());
		btn5 = new Button(getApplicationContext());

		txt = (TextView) findViewById(R.id.textView1);

		// secretbtn = (Button)findViewById(R.id.secret_button);
		//
		// secretbtn.setOnClickListener(this);
		// secretbtn.setVisibility(View.VISIBLE);
		// secretbtn.setBackgroundColor(Color.TRANSPARENT);

		get_data = getIntent().getExtras();
		bas_i = get_data.getInt("data_sonraki_soru_id");
		// txt.setText(""+i);
		// Log.i("get_data_from_sonraki_Activity", ""+i);

		data_gonder = new Bundle();

		gonder();

	}

	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();

		userInteractionTimeout = 0;

		// Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);

	}

	public void gonder() {

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7407/AnketServis.asmx/Question");

					httppost.setHeader("Content-type", "application/json");
					httppost.setHeader("Accept", "application/json");

					JSONObject jsonparameter = new JSONObject();

					final GlobalClass globalVariable_soru = (GlobalClass) getApplicationContext();

					try {

						int type_id = Akis_TypeID();
						int grp_id = Akis_GrpID();

						Log.i("Akis_TypeID()", "" + type_id);
						Log.i("Akis_GrpID()", "" + grp_id);

						jsonparameter.put("ID", type_id);
						jsonparameter.put("GrpID", grp_id);

						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));

						Log.i("httppost", "" + httppost);

						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						String responseText = EntityUtils.toString(entity);
						Log.i("@responceQuestion", "" + responseText);

						try {
							JSONObject returndata = new JSONObject(responseText);
							JSONArray jsonMainNode = returndata
									.optJSONArray("d");
							int lengthJsonArr = jsonMainNode.length();
							Log.i("@lengthJsonQuestion", "" + lengthJsonArr);
							sorusayisi = lengthJsonArr;

							// Quetion_ID
							final int question_id = Akis_TypeID();
							Log.i("Question_id", "" + question_id);

							for (int i = 0; i < sorusayisi; i++) {

								if (i == sorusayisi)
									break;
								JSONObject jsonChildNode = jsonMainNode
										.getJSONObject(i);

								try {

									final String Text = jsonChildNode
											.optString("Text");

									globalVariable_soru.setSorumuz(Text);

									Log.i("Log_banner_Text", "" + Text);

									final int cevap_id = jsonChildNode
											.optInt("CevapID");

									final List<String> answer = Cevaplar(cevap_id);

									Log.i("Answers", "" + answer);

									Log.i("Log_banner_ID", "" + cevap_id);

									final int cevap_sayisi = jsonChildNode
											.optInt("CevapSayisi");

									Log.i("Log_cevap_sayisi", "" + cevap_sayisi);

									if (i == 0) {
										txt.post(new Runnable() {
											@Override
											public void run() {
												try {

													final RelativeLayout lm = (RelativeLayout) findViewById(R.id.genelLayout);

													txt.setText(Text);

													if (cevap_sayisi == 2) {

														btn1.setBackgroundResource(R.drawable.stylebutton_iyi);
//														btn1.setText(answer
//																.get(0));
														btn1.setTextSize(34);
														btn1.setGravity(Gravity.CENTER);
														btn1.setTextColor(Color.BLACK);
														btn1.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn2.setClickable(false);

																Cevapla(1,
																		question_id);

															}

														});

														btn2.setBackgroundResource(R.drawable.stylebutton_cokiyi);
//														btn2.setText(answer
//																.get(1));
														btn2.setTextSize(34);
														btn2.setGravity(Gravity.CENTER);
														btn2.setTextColor(Color.BLACK);
														btn2.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn1.setClickable(false);

																Cevapla(2,
																		question_id);

															}

														});

														LinearLayout ll = (LinearLayout) findViewById(R.id.buttonlayout);

														ll.setOrientation(LinearLayout.VERTICAL);
														ll.setGravity(Gravity.CENTER);
														LayoutParams lp = new LayoutParams(
																new LayoutParams(
																		160,
																		160));

														ll.addView(btn1, lp);
														ll.addView(btn2, lp);
														lm.addView(ll);

													}

													if (cevap_sayisi == 3) {

//														btn1.setText(answer
//																.get(0));
														btn1.setBackgroundResource(R.drawable.stylebutton_iyi);
														btn1.setTextSize(34);
														btn1.setGravity(Gravity.CENTER);
														btn1.setTextColor(Color.BLACK);
														btn1.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn2.setClickable(false);
																btn3.setClickable(false);

																Cevapla(1,
																		question_id);
																// Intent inm =
																// new
																// Intent(getApplicationContext(),BitisMesage.class);
																// startActivity(inm);

															}

														});

//														btn2.setText(answer
//																.get(1));
														btn2.setBackgroundResource(R.drawable.stylebutton_kotu);
														btn2.setTextSize(34);
														btn2.setGravity(Gravity.CENTER);
														btn2.setTextColor(Color.BLACK);
														btn2.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn1.setClickable(false);
																btn3.setClickable(false);

																Cevapla(2,
																		question_id);

															}

														});

//														btn3.setText(answer
//																.get(2));
														btn3.setBackgroundResource(R.drawable.stylebutton_cokiyi);
														btn3.setTextSize(34);
														btn3.setGravity(Gravity.CENTER);
														btn3.setTextColor(Color.BLACK);
														btn3.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn1.setClickable(false);
																btn2.setClickable(false);

																Cevapla(3,
																		question_id);

															}

														});

														LinearLayout ll = (LinearLayout) findViewById(R.id.buttonlayout);
														ll.setOrientation(LinearLayout.VERTICAL);
														ll.setGravity(Gravity.CENTER);
														LayoutParams lp = new LayoutParams(
																new LayoutParams(
																		150,
																		150));
//(390,120)
														ll.addView(btn1, lp);
														ll.addView(btn2, lp);
														ll.addView(btn3, lp);
														lm.addView(ll);

													}

													if (cevap_sayisi == 4) {

//														btn1.setText(answer
//																.get(0));
														btn1.setBackgroundResource(R.drawable.stylebutton_iyi);
														btn1.setTextSize(34);
														btn1.setGravity(Gravity.CENTER);
														btn1.setTextColor(Color.BLACK);
														btn1.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn2.setClickable(false);
																btn3.setClickable(false);
																btn4.setClickable(false);

																Cevapla(1,
																		question_id);

															}

														});

//														btn2.setText(answer
//																.get(1));
														btn2.setBackgroundResource(R.drawable.stylebutton_orta);
														btn2.setTextSize(34);
														btn2.setGravity(Gravity.CENTER);
														btn2.setTextColor(Color.BLACK);
														btn2.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn1.setClickable(false);
																btn3.setClickable(false);
																btn4.setClickable(false);
																// TODO
																// Auto-generated
																// method stub

																Cevapla(2,
																		question_id);

															}

														});

//														btn3.setText(answer
//																.get(2));
														btn3.setBackgroundResource(R.drawable.stylebutton_kotu);
														btn3.setTextSize(34);
														btn3.setGravity(Gravity.CENTER);
														btn3.setTextColor(Color.BLACK);
														btn3.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn1.setClickable(false);
																btn2.setClickable(false);
																btn4.setClickable(false);

																// TODO
																// Auto-generated
																// method stub

																Cevapla(3,
																		question_id);

															}
														});

//														btn4.setText(answer
//																.get(3));
														btn4.setBackgroundResource(R.drawable.stylebutton_cokiyi);
														btn4.setTextSize(34);
														btn4.setGravity(Gravity.CENTER);
														btn4.setTextColor(Color.BLACK);
														btn4.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn1.setClickable(false);
																btn2.setClickable(false);
																btn3.setClickable(false);
																// TODO
																// Auto-generated
																// method stub
																Cevapla(4,
																		question_id);

															}
														});

														LinearLayout ll = (LinearLayout) findViewById(R.id.buttonlayout);
														ll.setOrientation(LinearLayout.VERTICAL);
														ll.setGravity(Gravity.CENTER);
														LayoutParams lp = new LayoutParams(
																new LayoutParams(
																		140,
																		140));

														ll.addView(btn1, lp);
														ll.addView(btn2, lp);
														ll.addView(btn3, lp);
														ll.addView(btn4, lp);
														lm.addView(ll);

													}

													if (cevap_sayisi == 5) {

//														btn1.setText(answer
//																.get(0));
														btn1.setBackgroundResource(R.drawable.stylebutton_iyi);
														btn1.setTextSize(34);
														btn1.setGravity(Gravity.CENTER);
														btn1.setTextColor(Color.BLACK);
														btn1.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn2.setClickable(false);
																btn3.setClickable(false);
																btn4.setClickable(false);
																btn5.setClickable(false);
																// TODO
																// Auto-generated
																// method stub

																Cevapla(1,
																		question_id);

															}

														});

//														btn2.setText(answer
//																.get(1));
														btn2.setBackgroundResource(R.drawable.stylebutton_orta);
														btn2.setTextSize(34);
														btn2.setGravity(Gravity.CENTER);
														btn2.setTextColor(Color.BLACK);
														btn2.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn1.setClickable(false);
																btn3.setClickable(false);
																btn4.setClickable(false);
																btn5.setClickable(false);

																// TODO
																// Auto-generated
																// method stub

																Cevapla(2,
																		question_id);

															}

														});

//														btn3.setText(answer
//																.get(2));
														btn3.setBackgroundResource(R.drawable.stylebutton_kotu);
														btn3.setTextSize(34);
														btn3.setGravity(Gravity.CENTER);
														btn3.setTextColor(Color.BLACK);
														btn3.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {

																btn1.setClickable(false);
																btn2.setClickable(false);
																btn4.setClickable(false);
																btn5.setClickable(false);

																// TODO
																// Auto-generated
																// method stub

																Cevapla(3,
																		question_id);

															}

														});

//														btn4.setText(answer
//																.get(3));
														btn4.setBackgroundResource(R.drawable.stylebutton_fena);
														btn4.setTextSize(34);
														btn4.setGravity(Gravity.CENTER);
														btn4.setTextColor(Color.BLACK);
														btn4.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn1.setClickable(false);
																btn2.setClickable(false);
																btn3.setClickable(false);
																btn5.setClickable(false);

																Cevapla(4,
																		question_id);

															}

														});

//														btn5.setText(answer
//																.get(4));
														btn5.setBackgroundResource(R.drawable.stylebutton_cokiyi);
														btn5.setTextSize(34);
														btn5.setGravity(Gravity.CENTER);
														btn5.setTextColor(Color.BLACK);
														btn5.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																btn1.setClickable(false);
																btn2.setClickable(false);
																btn3.setClickable(false);
																btn4.setClickable(false);

																Cevapla(5,
																		question_id);

															}

														});

														LinearLayout ll = (LinearLayout) findViewById(R.id.buttonlayout);
														ll.setOrientation(LinearLayout.VERTICAL);
														ll.setGravity(Gravity.CENTER);
														LayoutParams lp = new LayoutParams(
																new LayoutParams(
																		115,
																		115));

														ll.addView(btn1, lp);
														ll.addView(btn2, lp);
														ll.addView(btn3, lp);
														ll.addView(btn4, lp);
														ll.addView(btn5, lp);
														lm.addView(ll);

													}

												} catch (Exception e) {
													e.printStackTrace();

												}

											}
										});

									}

								} catch (Exception e) {

									Onceki_soru();
								}
							}

						} catch (JSONException e) {
							Onceki_soru();

						}

					} catch (Exception e) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Check your internet settings",
										Toast.LENGTH_SHORT).show();
							}
						});

						Onceki_soru();
						e.printStackTrace();
					}
				}

			}).start();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<String> Cevaplar(int cevabim_id) {

		List<String> getCevap = new ArrayList<String>();

		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://78.186.62.169:7407/AnketServis.asmx/Answers");

			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader("Accept", "application/json");

			JSONObject jsonparameter = new JSONObject();
			jsonparameter.put("ID", cevabim_id);

			httppost.setEntity(new StringEntity(jsonparameter.toString(),
					"UTF-8"));

			Log.i("httppost", "" + httppost);

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			Log.i("@responceAnswers", "" + responseText);

			try {

				JSONObject returndata = new JSONObject(responseText);
				JSONArray jsonMainNode = returndata.optJSONArray("d");
				int lengthJsonArr = jsonMainNode.length();
				Log.i("@lengthJsonAnswer", "" + lengthJsonArr);
				sorusayisi = lengthJsonArr;

				for (int i = 0; i < sorusayisi; i++) {

					if (i == sorusayisi)
						break;
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					try {
						final String value1 = jsonChildNode.optString("Value1");
						final String value2 = jsonChildNode.optString("Value2");
						final String value3 = jsonChildNode.optString("Value3");
						final String value4 = jsonChildNode.optString("Value4");
						final String value5 = jsonChildNode.optString("Value5");

						getCevap.add(value1);
						getCevap.add(value2);
						getCevap.add(value3);
						getCevap.add(value4);
						getCevap.add(value5);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} catch (Exception e) {
				// TODO: handle exception

			}

		} catch (Exception e) {
			// TODO: handle exception

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							"Check your internet settings",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
		return getCevap;

		// TODO Auto-generated method stub

	}

	private int Akis_TypeID() {
		try {

			JSONObject returndata = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://78.186.62.169:7407/AnketServis.asmx/Akis");
			httppost.setHeader("Content-type", "application/json");
			JSONObject jsonparameter = new JSONObject();
			final TextView textView1 = (TextView) findViewById(R.id.txt1);

			try {

				// jsonparameter.put("AnketID", "3");

				try {

					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String deviceID = telephonyManager.getDeviceId();
					jsonparameter.put("IMEI", deviceID);
				} catch (Exception e) {

				}

				httppost.setEntity(new StringEntity(jsonparameter.toString(),
						"UTF-8"));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);

				Log.i("@akis_responseString", "" + responseString);

				// final TextView textViewhdn1 = (TextView)
				// findViewById(R.id.txthdn1);

				try {
					returndata = new JSONObject(responseString);
					JSONArray jsonMainNode = returndata.optJSONArray("d");
					int lengthJsonArr = jsonMainNode.length();
					akis_soru_sorusayisi = lengthJsonArr;
					Log.i("Log_jsonlength", "" + akis_soru_sorusayisi);

					for (; bas_i < akis_soru_sorusayisi; bas_i++) {

						// if (i == sorusayisi)
						// break;
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(bas_i);

						try {

							Type = jsonChildNode.optString("Type").toString();
							Log.i("@Log_Type", "" + Type);

							if (Type.equals("Soru")) {

								// sonraki_soru_id = i;
								// Log.i("@Log_sonraki_id", ""+sonraki_soru_id);

								id = jsonChildNode.optInt("ID");
								Log.i("@Log_id", "" + id);

								GrpID = jsonChildNode.optInt("GrpID");
								Log.i("@Log_GrpID", "" + GrpID);

								TypeID = jsonChildNode.optInt("TypeID");
								Log.i("@Log_TypeID", "" + TypeID);

								break;

							}

							/*
							 * 
							 * if(Type.equals("Kullanýcý  Girisi")){
							 * 
							 * btn_anket.setOnClickListener(new
							 * OnClickListener() {
							 * 
							 * 
							 * 
							 * @Override public void onClick(View v) { // TODO
							 * Auto-generated method stub check = true;
							 * 
							 * Intent pass = new
							 * Intent(getApplicationContext(),KimlikEkrani
							 * .class); startActivity(pass); } });
							 * 
							 * 
							 * 
							 * }
							 */

							// if (i == 0) {
							// textView1.post(new Runnable() {
							// public void run() {
							// try {
							// // textView1.setText(Soru);
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							//
							// }
							// });

							// }

						} catch (Exception e) {

							// textView1.setText(globalVariable.getName());
							// Log.i("METIN", ""+globalVariable.getName());

							Log.i("ErrorMessage", "" + e.getMessage());

						}
					}

				} catch (JSONException e) {

				}

			} catch (Exception e) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return TypeID;

	}

	private void Cevapla(int i, int question_id) {
		// TODO Auto-generated method stub

		try {
			
			c = i;
			d = question_id;

/*			Thread.sleep(1000);

			Toast.makeText(getApplicationContext(), "Please Wait", 5000)
					.show();*/
			
			
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
					|| connectivityManager.getNetworkInfo(
							ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
				// we are connected to a network
				connected = true;
			} else
				connected = false;

			if (connected == true) {


				AsyncCallBanner banner = new AsyncCallBanner();
//				// Call execute
				banner.execute();
				
			} else
				Toast.makeText(getApplicationContext(),
						"Check your internet connection", 2000).show();
			


			

			// SonrakiSoruKontrol();

			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// private void SonrakiSoruKontrol() {
	// // TODO Auto-generated method stub
	//
	// Log.i("@Log_Kontrol_sorusayisi", ""+bas_i);
	// Log.i("@Log_Kontrol_sonraki_soru_id", ""+akis_soru_sorusayisi);
	//
	// // txt.setText(""+sonraki_soru_id+akis_soru_sorusayisi);
	//
	//
	//
	//
	// // int inc_sonraki_soru_id = sonraki_soru_id + 1;
	// // Log.i("inc_sonraki_soru_id", ""+inc_sonraki_soru_id);
	//
	//
	//
	//
	// // Intent pass_other_activty = new
	// Intent(getApplicationContext(),SonrakiActivity.class);
	// // data_gonder.putInt("data_sonraki_soru_id", inc_sonraki_soru_id);
	// // pass_other_activty.putExtras(data_gonder);
	// //
	// // startActivity(pass_other_activty);
	//
	//
	//
	//
	//
	// }

	private void getType() {
		// TODO Auto-generated method stub

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					JSONObject returndata = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7407/AnketServis.asmx/Akis");
					httppost.setHeader("Content-type", "application/json");
					JSONObject jsonparameter = new JSONObject();

					try {

						try {

							TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
							String deviceID = telephonyManager.getDeviceId();
							jsonparameter.put("IMEI", deviceID);
						} catch (Exception e) {

						}

						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						String responseString = EntityUtils.toString(entity);

						Log.i("@akis_responseString", "" + responseString);

						// final TextView textViewhdn1 = (TextView)
						// findViewById(R.id.txthdn1);

						try {

							int inc_bas_i = bas_i + 1;
							Log.i("inc_sonraki_soru_id", "" + inc_bas_i);

							returndata = new JSONObject(responseString);
							JSONArray jsonMainNode = returndata
									.optJSONArray("d");
							int lengthJsonArr = jsonMainNode.length();
							int sorusayisi = lengthJsonArr;
							Log.i("Log_jsonlength", "" + sorusayisi);

							for (; inc_bas_i < sorusayisi; inc_bas_i++) {

								// if (i == sorusayisi)
								// break;
								JSONObject jsonChildNode = jsonMainNode
										.getJSONObject(inc_bas_i);

								try {

									last_Type_inmethod = jsonChildNode
											.optString("Type").toString();

									Log.i("@Log_Type", "" + Type);

									if (last_Type_inmethod.equals("Soru")) {

										timer.cancel();

										Intent pass_other_activty = new Intent(
												getApplicationContext(),
												SonrakiActivity.class);
										data_gonder.putInt(
												"data_sonraki_soru_id",
												inc_bas_i);
										pass_other_activty
												.putExtras(data_gonder);

										pass_other_activty
												.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										startActivity(pass_other_activty);

										break;
									}

									if (last_Type_inmethod.equals("Bitis")) {

										timer.cancel();

										Intent pass_other_activty = new Intent(
												getApplicationContext(),
												BitisMesage.class);
										// data_gonder.putInt("data_sonraki_soru_id",
										// inc_sonraki_soru_id);
										// pass_other_activty.putExtras(data_gonder);

										pass_other_activty
												.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										startActivity(pass_other_activty);

										break;
									}

									if (last_Type_inmethod
											.equals("Kullanıcı Girisi")) {

										timer.cancel();

										Intent pass_other_activty = new Intent(
												getApplicationContext(),
												KullaniciGirisi1.class);
										data_gonder.putInt(
												"data_sonraki_soru_id",
												inc_bas_i);
										pass_other_activty
												.putExtras(data_gonder);

										pass_other_activty
												.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										startActivity(pass_other_activty);
										//

										break;
									}

									// if (i == 0) {
									// message.post(new Runnable() {
									// public void run() {
									// try {
									// message.setText(Type);
									// } catch (Exception e) {
									// e.printStackTrace();
									// }
									//
									// }
									// });

									// textViewhdn1.post(new Runnable() {
									// public void run() {
									// try {
									// textViewhdn1.setText(ID);
									// } catch (Exception e) {
									// e.printStackTrace();
									// }
									//
									// }
									// });
									// }

								} catch (Exception e) {

									Log.i("ErrorMessage", "" + e.getMessage());

								}
							}

						} catch (JSONException e) {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(
											getApplicationContext(),
											"Check your internet settings",
											Toast.LENGTH_SHORT).show();
								}
							});

						}

					} catch (Exception e) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Check your internet settings",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void Oyla(final int cevabim_id, final int questionn_id) {

		// TODO Auto-generated method stub

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7407/AnketServis.asmx/Oyla");

					httppost.setHeader("Content-type", "application/json");
					httppost.setHeader("Accept", "application/json");

					JSONObject jsonparameter = new JSONObject();

					try {

						jsonparameter.put("ID", questionn_id);
						jsonparameter.put("CevapID", cevabim_id);

						try {
							TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
							String deviceID = telephonyManager.getDeviceId();
							jsonparameter.put("IMEI", deviceID);
						} catch (Exception e) {
							jsonparameter.put("IMEI", "IMEI Yok");
						}
						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));

						httpclient.execute(httppost);

						/*
						 * runOnUiThread(new Runnable(){ public void run() {
						 * Toast.makeText(getApplicationContext(), "Gönderildi",
						 * Toast.LENGTH_SHORT).show(); } });
						 */

					} catch (Exception e) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Check your internet settings",
										Toast.LENGTH_SHORT).show();
							}
						});

						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	private class AsyncCallBanner extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
     		Connect();
			
//			File file = new File(fpath);
//			 
//			// If file does not exists, then create it
//			if (!file.exists()) {
//				try {
//					file.createNewFile();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
// 
//			
//			try {
//				FileWriter 	fw = new FileWriter(file,true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write("soru_id:" + soru_id + "\t" + "cevap_id:" + cevap_id +  "\t" + formattedDate + "\n");
//				bw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
// 
//			Log.d("Suceess","Sucess");
			
			return null;
		}

		private void Connect() {
			// TODO Auto-generated method stub

			JSONObject returndata = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://78.186.62.169:7407/AnketServis.asmx/Oyla");
			httppost.setHeader("Content-type", "application/json");
			JSONObject jsonparameter = new JSONObject();

			try {

				try {

					jsonparameter.put("ID", d);
					jsonparameter.put("CevapID", c);

					try {
						TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String deviceID = telephonyManager.getDeviceId();
						jsonparameter.put("IMEI", deviceID);
					} catch (Exception e) {
						jsonparameter.put("IMEI", "IMEI Yok");
					}

					httppost.setEntity(new StringEntity(jsonparameter.toString(),
							"UTF-8"));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					String responseString = EntityUtils.toString(entity);

					Log.i("@banner_responseString", "" + responseString);

					try {
						returndata = new JSONObject(responseString);
						jsonMainNode = returndata.optString("d");

					} catch (JSONException e) {

					}

				} catch (Exception e) {

				}

			} catch (Exception e) {

			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");

			if (jsonMainNode.equals("true")) {
//
				waitm3();

			} else {

				Toast.makeText(getApplicationContext(), "Oy gitmedi", 2000)
						.show();
			}

			dialog.dismiss();
		}

		private void waitm3() {
			// TODO Auto-generated method stub

			try {

				timer.cancel();

				Thread.sleep(1000);

				// Toast.makeText(getApplicationContext(), "Lütfen Bekleyiniz",
				// 5000)
				// .show();

                getType();

				// SonrakiSoruKontrol();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
			dialog = new ProgressDialog(SonrakiActivity.this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.setMessage("Lütfen bekleyiniz");

			dialog.setIndeterminate(true);

			dialog.show();
		}

	}

	private int Akis_GrpID() {
		try {

			JSONObject returndata = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://78.186.62.169:7407/AnketServis.asmx/Akis");
			httppost.setHeader("Content-type", "application/json");
			JSONObject jsonparameter = new JSONObject();
			final TextView textView1 = (TextView) findViewById(R.id.txt1);

			try {

				// jsonparameter.put("AnketID", "3");

				try {

					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String deviceID = telephonyManager.getDeviceId();
					jsonparameter.put("IMEI", deviceID);
				} catch (Exception e) {

				}

				httppost.setEntity(new StringEntity(jsonparameter.toString(),
						"UTF-8"));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);

				Log.i("@akis_responseString", "" + responseString);

				// final TextView textViewhdn1 = (TextView)
				// findViewById(R.id.txthdn1);

				try {
					returndata = new JSONObject(responseString);
					JSONArray jsonMainNode = returndata.optJSONArray("d");
					int lengthJsonArr = jsonMainNode.length();
					sorusayisi = lengthJsonArr;
					Log.i("Log_jsonlength", "" + sorusayisi);

					for (; bas_i < sorusayisi; bas_i++) {

						// if (i == sorusayisi)
						// break;
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(bas_i);

						try {
							Type = jsonChildNode.optString("Type").toString();

							Log.i("@Log_Type", "" + Type);
							if (Type.equals("Soru")) {

								id = jsonChildNode.optInt("ID");
								Log.i("@Log_id", "" + id);

								GrpID = jsonChildNode.optInt("GrpID");
								Log.i("@Log_GrpID", "" + GrpID);

								TypeID = jsonChildNode.optInt("TypeID");
								Log.i("@Log_TypeID", "" + TypeID);

								break;
							}

							/*
							 * 
							 * if(Type.equals("Kullanýcý  Girisi")){
							 * 
							 * btn_anket.setOnClickListener(new
							 * OnClickListener() {
							 * 
							 * 
							 * 
							 * @Override public void onClick(View v) { // TODO
							 * Auto-generated method stub check = true;
							 * 
							 * Intent pass = new
							 * Intent(getApplicationContext(),KimlikEkrani
							 * .class); startActivity(pass); } });
							 * 
							 * 
							 * 
							 * }
							 */

							// if (i == 0) {
							// textView1.post(new Runnable() {
							// public void run() {
							// try {
							// // textView1.setText(Soru);
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							//
							// }
							// });

							// }

						} catch (Exception e) {

							// textView1.setText(globalVariable.getName());
							// Log.i("METIN", ""+globalVariable.getName());

							Log.i("ErrorMessage", "" + e.getMessage());

						}
					}

				} catch (JSONException e) {

				}

			} catch (Exception e) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return GrpID;

	}

	public void Onceki_soru() {
		try {
			final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
			final TextView txt_soru = (TextView) findViewById(R.id.textView1);
			// final TextView textViewhdn1 = (TextView)
			// findViewById(R.id.txthdn1);
			txt_soru.post(new Runnable() {
				@Override
				public void run() {
					try {

						txt_soru.setText(globalVariable.getSorumuz());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// textViewhdn1.post(new Runnable() {
			// public void run() {
			// try {
			// textViewhdn1.setText(globalVariable.getSoruID());
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// switch (v.getId()) {
	// case R.id.secret_button:
	//
	// startActivity(new Intent(getApplicationContext(),SecretButon.class));
	//
	// break;
	//
	// }
	//
	// }

}
