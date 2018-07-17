package com.example.anket1;

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
import android.util.Log;
import android.widget.TextView;

public class BitisMesage extends Activity {

	String Type;
	int TypeID;
	TextView message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toastmesage);

		message = (TextView) findViewById(R.id.textView1);

		Check_Next_Flow();

		Thread timer = new Thread() {

			@Override
			public void run() {
				try {

					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					// Intent openMainActivity = new
					// Intent(getApplicationContext(),MainActivity.class);
					// startActivity(openMainActivity);
				}

			}
		};
		timer.start();

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
							Type = jsonChildNode.optString("Type").toString();

							Log.i("@Log_Type", "" + Type);
							if (Type.equals("Bitis")) {

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

	private void Check_Next_Flow() {
		// TODO Auto-generated method stub

		// int i_check_flow = bas_i + 1;

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {

					JSONObject returndata = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://78.186.62.169:7407/AnketServis.asmx/Exit");
					httppost.setHeader("Content-type", "application/json");
					JSONObject jsonparameter = new JSONObject();
					final GlobalClass globalVariable_bitis = (GlobalClass) getApplicationContext();

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

									Type = jsonChildNode.optString("Value")
											.toString();
									Log.i("@Log_Type", "" + Type);

									// if (i == 0) {
									message.post(new Runnable() {
										@Override
										public void run() {
											try {
												message.setText(Type);
												globalVariable_bitis
														.setBitisBanner(Type);
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

	public void Onceki_soru() {
		try {
			final GlobalClass globalVariable_bit = (GlobalClass) getApplicationContext();
			final TextView textView1 = (TextView) findViewById(R.id.textView1);
			// final TextView textViewhdn1 = (TextView)
			// findViewById(R.id.txthdn1);
			textView1.post(new Runnable() {
				@Override
				public void run() {
					try {

						textView1.setText(globalVariable_bit.getBitisBanner());

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
