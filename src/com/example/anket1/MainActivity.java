package com.example.anket1;

import java.util.ArrayList;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements OnClickListener {

	SharedPreferences pref;
	SharedPreferences.Editor ed;

//	Timer timer;

	int userInteractionTimeout;

	WakeLock wakeLock;

	String Log_id;
	String Icerik;
	int Banner_Grp_id;
	int Banner_id;
	Button btn_anket;
	Button btn_main_secret_button;
	

	int id;
	int GrpID;
	String Type;
	int TypeID;

	boolean check = false;

	ProgressDialog progressBar;

	Bundle data_gonder;
	int i;

	int sorusayisi = 0;
	int cevaplanan = 0;
	Boolean hata = false;
	ArrayList<Sorular> _soru = new ArrayList<Sorular>();

	String Typem;
	int TypemID;

	ArrayList<Integer> get_ID = new ArrayList<Integer>();
	ArrayList<Integer> get_GrpID = new ArrayList<Integer>();
	ArrayList<String> get_Type = new ArrayList<String>();
	ArrayList<Integer> get_TypeID = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);

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

			
			

			setContentView(R.layout.mainscreen);

/*			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (userInteractionTimeout == 60) {
						// Do your @Override
						Intent intent = new Intent(getApplicationContext(),
								AndroidVideoPlayer.class);
						intent.putExtra("EXIT", false);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

					}
					userInteractionTimeout++;
				}
			}, 0, 1000);*/

			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
					"Full Wake Lock");

			// login_kontrol();

			  Banner_process();
			 //SorulariGetir();

			btn_anket = (Button) findViewById(R.id.button_anket);
			btn_anket.setOnClickListener(this);

			btn_main_secret_button = (Button) findViewById(R.id.main_secret_button);
			btn_main_secret_button.setOnClickListener(this);
			btn_main_secret_button.setVisibility(View.VISIBLE);
			btn_main_secret_button.setBackgroundColor(Color.TRANSPARENT);
 
		


			data_gonder = new Bundle();

			// Button button_orta = (Button) findViewById(R.id.bttn_orta);
			// button_orta.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			// Cevapla(v, "4");
			// }
			// });

			// Button button_kotu = (Button) findViewById(R.id.bttn_kotu);
			// button_kotu.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			// Cevapla(v, "5");
			// }
			// });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();

		userInteractionTimeout = 0;

		// Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);

	}

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

	// public void login_kontrol() {
	//
	//
	//
	// try {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	//
	// JSONObject returndata = null;
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(
	// "http://78.186.62.169:7421/AnketServis.asmx/LogControl");
	// httppost.setHeader("Content-type", "application/json");
	// JSONObject jsonparameter = new JSONObject();
	//
	//
	// try {
	//
	//
	//
	// httppost.setEntity(new StringEntity(jsonparameter
	// .toString(), "UTF-8"));
	// HttpResponse response = httpclient.execute(httppost);
	// HttpEntity entity = response.getEntity();
	// String responseString = EntityUtils.toString(entity);
	//
	// Log.i("@get_login", ""+responseString);
	//
	//
	//
	// try {
	// returndata = new JSONObject(responseString);
	// JSONArray jsonMainNode = returndata
	// .optJSONArray("d");
	// int lengthJsonArr = jsonMainNode.length();
	// sorusayisi = lengthJsonArr;
	//
	// for (int i = 0; i <= 1; i++) {
	//
	// if (i == sorusayisi)
	// break;
	// JSONObject jsonChildNode = jsonMainNode
	// .getJSONObject(i);
	//
	// try {
	//
	// Log_id = jsonChildNode
	// .optString("LogID").toString();
	// Log.i("@Log_idd", ""+Log_id);
	//
	// }
	//
	//
	// catch (Exception e) {
	// // hata = true;
	// // Onceki_soru();
	//
	//
	// }
	// }
	//
	// } catch (JSONException e) {
	// // hata = true;
	// // Onceki_soru();
	//
	// }
	//
	// } catch (Exception e) {
	// // hata = true;
	// // Onceki_soru();
	//
	// }
	//
	// }
	// }).start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// public void Cevapla(View v, String cevap) {
	// try {
	//
	// Thread.sleep(1500);
	//
	// cevaplanan++;
	// final TextView txthdn = (TextView) findViewById(R.id.txthdn1);
	// String c = txthdn.getText().toString();
	// Oyla(c, cevap);
	//
	// kontrol();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// public void kontrol() {
	// try {
	// if (cevaplanan == sorusayisi && hata == false) {
	// cevaplanan = 0;
	// // startActivity(new Intent(MainActivity.this, second.class));
	// tesekkur();
	// } else if (hata) {
	// hata = false;
	// cevaplanan = 0;
	// // startActivity(new Intent(MainActivity.this, second.class));
	// tesekkur();
	// } else {
	// final TextView textView1 = (TextView) findViewById(R.id.txt1);
	// final TextView textViewhdn1 = (TextView) findViewById(R.id.txthdn1);
	// textView1.post(new Runnable() {
	// public void run() {
	// try {
	// textView1.setText(_soru.get(cevaplanan).Metin);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	//
	// textViewhdn1.post(new Runnable() {
	// public void run() {
	// try {
	// textViewhdn1.setText(_soru.get(cevaplanan).ID);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// tesekkur();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// public void tesekkur() {
	// try {
	//
	// new Thread(new Runnable() {
	//
	// public void run() {
	// try {
	// final LinearLayout imgView = (LinearLayout)
	// findViewById(R.id.imageView1);
	// final LinearLayout lnr_1 = (LinearLayout) findViewById(R.id.lnr_1);
	// imgView.post(new Runnable() {
	// public void run() {
	// try {
	// imgView.setVisibility(View.VISIBLE);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// lnr_1.post(new Runnable() {
	// public void run() {
	// try {
	// lnr_1.setVisibility(LinearLayout.GONE);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	//
	// try {
	// Thread.sleep(2000);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// imgView.post(new Runnable() {
	// public void run() {
	// try {
	// imgView.setVisibility(View.GONE);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// lnr_1.post(new Runnable() {
	// public void run() {
	// try {
	// lnr_1.setVisibility(LinearLayout.VISIBLE);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// SorulariGetir();
	//
	// }

	// private void Oyla(final String ID, final String CevapID) {
	// try {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	//
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(
	// "http://78.186.62.169:7421/AnketServis.asmx/Oyla");
	//
	// httppost.setHeader("Content-type", "application/json");
	// httppost.setHeader("Accept", "application/json");
	//
	// JSONObject jsonparameter = new JSONObject();
	//
	// try {
	//
	// jsonparameter.put("ID", ID);
	// jsonparameter.put("CevapID", CevapID);
	//
	//
	// try {
	// TelephonyManager telephonyManager = (TelephonyManager)
	// getSystemService(Context.TELEPHONY_SERVICE);
	// String deviceID=telephonyManager.getDeviceId();
	// jsonparameter.put("IMEI", deviceID);
	// } catch (Exception e) {
	// jsonparameter.put("IMEI", "IMEI Yok");
	// }
	// httppost.setEntity(new StringEntity(jsonparameter
	// .toString(), "UTF-8"));
	//
	// httpclient.execute(httppost);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void SorulariGetir() {
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
					final TextView textView1 = (TextView) findViewById(R.id.txt1);
					final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

					try {

						try {

							TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
							String deviceID = telephonyManager.getDeviceId();
							Log.i("IMEI NUMBER", deviceID);

							jsonparameter.put("IMEI", deviceID);
						} catch (Exception e) {

						}

						// jsonparameter.put("AnketID", "3");

						httppost.setEntity(new StringEntity(jsonparameter
								.toString(), "UTF-8"));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						String responseString = EntityUtils.toString(entity);

						Log.i("@akis_responseString", "" + responseString);

						// final TextView textViewhdn1 = (TextView)
						// findViewById(R.id.txthdn1);

						try {
							returndata = new JSONObject(responseString);
							JSONArray jsonMainNode = returndata
									.optJSONArray("d");
							int lengthJsonArr = jsonMainNode.length();
							sorusayisi = lengthJsonArr;
							Log.i("Log_jsonlength", "" + sorusayisi);

							for (i = 0; i < sorusayisi; i++) {

								// if (i == sorusayisi)
								// break;
								JSONObject jsonChildNode = jsonMainNode
										.getJSONObject(i);

								try {

									// id = jsonChildNode.optInt("ID");
									// Log.i("@Log_id", ""+id);
									//
									// GrpID = jsonChildNode.optInt("GrpID");
									// Log.i("@Log_GrpID", ""+GrpID);

									Type = jsonChildNode.optString("Type")
											.toString();
									Log.i("@Log_Type", "" + Type);

									// TypeID = jsonChildNode.optInt("TypeID");
									// Log.i("@Log_TypeID", ""+TypeID);

									if (Type.equals("Kullanıcı Girisi")) {

										// TODO Auto-generated method stub
										check = true;

										Intent pass = new Intent(
												getApplicationContext(),
												KullaniciGirisi1.class);
										data_gonder.putInt(
												"data_sonraki_soru_id", i);
										pass.putExtras(data_gonder);

										pass.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										startActivity(pass);

										break;

									}

									if (Type.equals("Soru")) {

										// TODO Auto-generated method stub
										check = true;

										Intent pass = new Intent(
												getApplicationContext(),
												SonrakiActivity.class);
										data_gonder.putInt(
												"data_sonraki_soru_id", i);
										pass.putExtras(data_gonder);

										pass.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										startActivity(pass);

										break;

									}

									final String ID = jsonChildNode.optString(
											"ID").toString();

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

									// _soru.add(new Sorular(ID, Type));
									// Log.i("_soru", ""+_soru.get(0));
									// globalVariable.setBannerName(Icerik);
									// globalVariable.setSoruID(ID);
									//
									// globalVariable.setName(Type);
									// Log.i("@LOG1_GETNAME",
									// globalVariable.getName());

								} catch (Exception e) {

									// textView1.setText(globalVariable.getName());
									// Log.i("METIN",
									// ""+globalVariable.getName());

									Log.i("ErrorMessage", "" + e.getMessage());

								}
							}

						} catch (JSONException e) {

							// textView1.setText(globalVariable.getName());
							// Log.i("METIN1", ""+globalVariable.getName());
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

						// textView1.setText(globalVariable.getName());
						// Log.i("METIN2", ""+globalVariable.getName());
					}

				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	
	

	
	
	
	private void Banner_process() {
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					JSONObject returndata = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7407/AnketServis.asmx/Banner");
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

						final TextView textView1 = (TextView) findViewById(R.id.txt1);
						final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

						try {
							returndata = new JSONObject(responseString);
							JSONArray jsonMainNode = returndata
									.optJSONArray("d");
							int lengthJsonArr = jsonMainNode.length();
							int sorusayisi = lengthJsonArr;
							Log.i("Log_jsonlength", "" + sorusayisi);

							for (int i = 0; i < sorusayisi; i++) {

								// if (i == sorusayisi)
								// break;
								JSONObject jsonChildNode = jsonMainNode
										.getJSONObject(i);

								try {

									// Banner_Grp_id = jsonChildNode
									// .optInt("GrupID");
									//
									// Log.i("Log_banner_grup_id", ""
									// + Banner_Grp_id);
									//
									// Banner_id = jsonChildNode.optInt("ID");

									Icerik = jsonChildNode.optString("Icerik")
											.toString();
									Log.i("@Log_Type", "" + Icerik);

									// if (i == 0) {
									textView1.post(new Runnable() {
										@Override
										public void run() {
											try {
												textView1.setText(Icerik);
												globalVariable
														.setBannerName(Icerik);
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

									Onceki_soru();

									Log.i("ErrorMessage", "" + e.getMessage());

								}
							}

						} catch (JSONException e) {
							Onceki_soru();
						}

					} catch (Exception e) {
						Onceki_soru();
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
					"http://78.186.62.169:7407/AnketServis.asmx/Akis");
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

					for (int i = 0; i < sorusayisi; i++) {

						// if (i == sorusayisi)
						// break;
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						try {
							Typem = jsonChildNode.optString("Type").toString();

							Log.i("@Log_Type", "" + Typem);
							if (Typem.equals("Karşılama")) {

								TypemID = jsonChildNode.optInt("TypeID");
								Log.i("@Log_TypeID", "" + TypemID);

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

		return TypemID;
	}

	public void Onceki_soru() {
		try {
			final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
			final TextView textView1 = (TextView) findViewById(R.id.txt1);
			// final TextView textViewhdn1 = (TextView)
			// findViewById(R.id.txthdn1);
			textView1.post(new Runnable() {
				@Override
				public void run() {
					try {

						textView1.setText(globalVariable.getBannerName());

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

	public class Sorular {
		String ID;
		String Metin;

		public Sorular(String _ID, String _Metin) {
			ID = _ID;
			Metin = _Metin;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_anket:

//			timer.cancel();

			Toast.makeText(getApplicationContext(), "Lütfen Bekleyiniz", 1000)
					.show();

			SorulariGetir();

			break;

		case R.id.main_secret_button:

//			timer.cancel();

			Intent main_secret_transaction = new Intent(
					getApplicationContext(), SecretButon.class);
			main_secret_transaction.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(main_secret_transaction);

			break;



		}

	}

	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// switch (v.getId()) {
	//
	//
	//
	// case R.id.button_anket:
	//
	// // if(!Log_id.equals("")){
	// //
	// // Log.i("@Log_id_onClick", ""+Log_id);
	// //
	// // Intent pass = new Intent(this,KimlikEkrani.class);
	// // startActivity(pass);
	// // }
	//
	//
	//
	// Intent pass = new Intent(this,KimlikEkrani.class);
	// startActivity(pass);
	//
	// // Toast.makeText(getApplicationContext(), "Girilebilir",
	// Toast.LENGTH_SHORT).show();
	//
	//
	//
	// // else{
	// //
	// //// Toast.makeText(getApplicationContext(), "Girilmez",
	// Toast.LENGTH_SHORT).show();
	// // Intent pass_sorular = new
	// Intent(getApplicationContext(),DEneme.class);
	// // startActivity(pass_sorular);
	// // }
	//
	// break;
	//
	//
	// }
	//
	// }

}