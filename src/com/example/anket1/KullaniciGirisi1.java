package com.example.anket1;

import java.util.Timer;
import java.util.TimerTask;

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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class KullaniciGirisi1 extends Activity implements OnClickListener {

	TextView Giris_Banner;
	EditText Giris_No;
	Button Giris_Gonder;

	Bundle get_data_from_Soru;
	int akis_baslangic;

	String Type;

	String send_no;

	int status;

	String last_Type_inmethod;

	Bundle data_gonder;

	String Typem;
	int TypeID;

	int length;

	int inc_bas_i;

	Timer timer;

	int userInteractionTimeout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kullanici_girisi1);

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

		Giris_Banner = (TextView) findViewById(R.id.txt_kullanici_No);
		Giris_No = (EditText) findViewById(R.id.edt_kullanici_No);
		Giris_Gonder = (Button) findViewById(R.id.btn_Kullanici_yolla);

		get_data_from_Soru = getIntent().getExtras();
		akis_baslangic = get_data_from_Soru.getInt("data_sonraki_soru_id");

		data_gonder = new Bundle();

		// Giris_Banner.setText(""+akis_baslangic);

		Giris_Gonder.setOnClickListener(this);

		get_Input_Banner();

		inc_bas_i = akis_baslangic;

	}

	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();

		userInteractionTimeout = 0;

		// Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);

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

	private void get_Input_Banner() {
		// TODO Auto-generated method stub

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					JSONObject returndata = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7421/AnketServis.asmx/InputBanner");
					httppost.setHeader("Content-type", "application/json");
					JSONObject jsonparameter = new JSONObject();

					try {
						int get_Type_id = getTypeID();

						Log.i("get_Type_ID", "" + get_Type_id);

						jsonparameter.put("ID", get_Type_id);

						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						String responseString = EntityUtils.toString(entity);

						Log.i("@akis_responseString", "" + responseString);

						// final TextView textViewhdn1 = (TextView)
						// findViewById(R.id.txthdn1);

						final GlobalClass globalVariable_bitis = (GlobalClass) getApplicationContext();

						try {
							returndata = new JSONObject(responseString);
							JSONArray jsonMainNode = returndata
									.optJSONArray("d");
							int lengthJsonArr = jsonMainNode.length();
							int sorusayisi = lengthJsonArr;
							Log.i("Log_jsonlength", "" + sorusayisi);

							// Giris_No.setFilters(new InputFilter[] {new
							// InputFilter.LengthFilter(length)});

							for (int i = 0; i < sorusayisi; i++) {

								// if (i == sorusayisi)
								// break;
								JSONObject jsonChildNode = jsonMainNode
										.getJSONObject(i);

								try {

									Type = jsonChildNode.optString("Value")
											.toString();

									length = jsonChildNode.optInt("nInputChrc");

									Giris_No.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
											length) });

									Log.i("@Log_Type", "" + Type);

									// if (i == 0) {
									Giris_Banner.post(new Runnable() {
										@Override
										public void run() {
											try {
												Giris_Banner.setText(Type);
												globalVariable_bitis
														.setKullaniciGirisiBanner(Type);
											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									});

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

									Onceki_Soru_KG();

									Log.i("ErrorMessage", "" + e.getMessage());

								}
							}

						} catch (JSONException e) {
							Onceki_Soru_KG();
						}

					} catch (Exception e) {
						Onceki_Soru_KG();
					}

				}

			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getTypeID() {
		// TODO Auto-generated method stub

		try {

			JSONObject returndata = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://78.186.62.169:7421/AnketServis.asmx/Akis");
			httppost.setHeader("Content-type", "application/json");
			JSONObject jsonparameter = new JSONObject();

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
					int sorusayisi = lengthJsonArr;
					Log.i("Log_jsonAkış", "" + sorusayisi);

					for (; inc_bas_i < sorusayisi; inc_bas_i++) {

						// if (i == sorusayisi)
						// break;
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(inc_bas_i);

						try {
							Typem = jsonChildNode.optString("Type").toString();

							Log.i("@Log_Type", "" + Type);
							if (Typem.equals("Kullanıcı Girisi")) {

								TypeID = jsonChildNode.optInt("TypeID");
								Log.i("@Log_TypeID", "" + TypeID);

								break;
							}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_Kullanici_yolla:

			if (Giris_No.length() != length) {

				Toast.makeText(getApplicationContext(),
						"Lütfen numaranızı tam giriniz", Toast.LENGTH_SHORT)
						.show();
			}

			else {

				gonder();
				Log.i("AfterSend_status", "" + status);

				break;

			}

		}

	}

	public void gonder() {

		send_no = Giris_No.getText().toString();
		Log.i("TC", "" + send_no);

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7421/AnketServis.asmx/AddUser");

					httppost.setHeader("Content-type", "application/json");
					httppost.setHeader("Accept", "application/json");

					JSONObject jsonparameter = new JSONObject();

					try {

						jsonparameter.put("UserID", send_no);
						// jsonparameter.put("CevapID", CevapID);

						try {
							TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
							String deviceID = telephonyManager.getDeviceId();
							jsonparameter.put("IMEI", deviceID);
						} catch (Exception e) {
							jsonparameter.put("IMEI", "IMEI Yok");
						}
						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));

						Log.i("httppost", "" + httppost);

						HttpResponse response = httpclient.execute(httppost);
						// HttpEntity entity = response.getEntity();
						// String responseText = EntityUtils.toString(entity);
						status = response.getStatusLine().getStatusCode();

						if (status == 200) {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(KullaniciGirisi1.this,
											"Please Wait", 500).show();
								}
							});

							getType();

						}

						else {

							// Toast mesage uses in Thread wit post Handler

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(KullaniciGirisi1.this,
											"İşlem Başarısız", 1000).show();
								}
							});

						}

						// Log.i("@Status", ""+status);

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

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getType() {
		// TODO Auto-generated method stub

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					JSONObject returndata = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7421/AnketServis.asmx/Akis");
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

							inc_bas_i = akis_baslangic + 1;
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

										startActivity(pass_other_activty);
										//

										break;
									}

									// else{
									//
									// Intent i =
									// getBaseContext().getPackageManager()
									// .getLaunchIntentForPackage(
									// getBaseContext().getPackageName() );
									// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									// startActivity(i);
									//
									//
									// }

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

						}

					} catch (Exception e) {

					}

				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void Onceki_Soru_KG() {
		// TODO Auto-generated method stub

		try {
			final GlobalClass globalVariable_KGiris = (GlobalClass) getApplicationContext();

			// final TextView textViewhdn1 = (TextView)
			// findViewById(R.id.txthdn1);
			Giris_Banner.post(new Runnable() {
				@Override
				public void run() {
					try {

						Giris_Banner.setText(globalVariable_KGiris
								.getKullaniciGirisiBanner());

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

}
