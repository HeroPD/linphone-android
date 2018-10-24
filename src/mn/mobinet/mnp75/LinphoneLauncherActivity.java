/*
LinphoneLauncherActivity.java
Copyright (C) 2011  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package mn.mobinet.mnp75;

import static android.content.Intent.ACTION_MAIN;

import org.json.JSONObject;
import mn.mobinet.mnp75.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.mobinet.model.VoipSipModel;
import com.rey.material.widget.Button;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.Encryption;
import mn.mobicom.oauth2.OAuthClient;
import mn.mobicom.oauth2.OauthRequest;

/**
 *
 * Launch Linphone main activity when Service is ready.
 *
 * @author Guillaume Beraudo
 *
 */
public class LinphoneLauncherActivity extends Activity {

	private static final String TAG = LinphoneLauncherActivity.class.getSimpleName();
	public static final int RequestCodeConnect = 2001;
	private Handler mHandler;
	private ServiceWaitThread mServiceThread;
	private WebView web;
	private Button mobi_button_language;
	private String code;
	public static String sipNumber;
	public static String sipPass;
	public static HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	public boolean connectingToSip = false;

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			final MaterialDialog mMaterialDialog = new MaterialDialog(context);
			mMaterialDialog
					.setTitle("NO INTERNET CONNECTION")
					.setMessage("Check your wifi or 3G network is working?")
					.setPositiveButton(context.getString(R.string.mnp_ok),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									mMaterialDialog.dismiss();
								}
							});

			mMaterialDialog.show();

			return false;
		} else
			return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobi_launcher);
		web = (WebView) findViewById(R.id.login_webview);
		mobi_button_language = (Button) findViewById(R.id.mobi_language);
		mobi_button_language.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences pref = getApplicationContext()
						.getSharedPreferences("mn.mobinet.mnp", 0); // 0 - for
				// private
				// mode
				SharedPreferences.Editor editor = pref.edit();
				if (pref.getInt("language", 0) == 0) {

					editor.putInt("language", 1);
					changeLanguage("en");
					mobi_button_language.setText("MN");

				} else {
					editor.putInt("language", 0);
					changeLanguage("mn");
					mobi_button_language.setText("EN");
				}
				editor.commit();
			}
		});
		WebSettings settings = web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("utf-8");
		settings.setUserAgentString("User-Agent\tMozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1");
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("URL", "shouldOverrideUrlLoading " + url);
				if (url.startsWith("com.mobinet.mnp75")) {
					String[] urls1 = url.split("\\?");
					String[] urls2 = urls1[1].split("&");
					HashMap<String, String> parametr = new HashMap<String, String>();
					for (String pr : urls2) {
						String get[] = pr.split("=");
						if (get.length > 1) {
							parametr.put(get[0], get[1]);
						} else if (get.length == 1){
							parametr.put(get[0], "");
						}
					}
					if (parametr.containsKey("code")) {
						code = parametr.get("code");
					}
					if (parametr.containsKey("systems")) {
						String service = parametr.get("systems");
						String[] services = service.split("%2C");
						for (String item : services) {
							if (item.equalsIgnoreCase("mobinetphone")) {
								UserControl.userType = UserControl.SipUserType.MNP75;
							}
						}

						OAuthClientTask task = new OAuthClientTask();
						if (isNetworkConnected(LinphoneLauncherActivity.this))
							task.execute(code);
//						else if (service.equalsIgnoreCase("mobileoffice")) {
//							UserControl.userType = SipUserType.MOBILEOFFICE;
//						}
					}
				} else {
					view.loadUrl(url);
				}
				return true;
			}

//			@Override
//			public void onReceivedSslError(WebView view,
//										   SslErrorHandler handler, SslError error) {
//				// super.onReceivedSslError(view, handler, error);
//				handler.proceed();
//			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
										String description, String failingUrl) {
				// Log.i(TAG, "error code: " + errorCode);
				// Log.i(TAG, "description: " + description);
				// Log.i(TAG, "failingUrl: " + failingUrl);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (web.canGoBack()) {
			web.goBack();
		} else {
			finish();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"mn.mobinet.mnp", 0);
		if (pref.getInt("language", 0) == 0) {
			mobi_button_language.setText("EN");
			changeLanguage("mn");
		} else {
			mobi_button_language.setText("MN");
			changeLanguage("en");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == RequestCodeConnect) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				connectToSip();
			}
		}
	}

	public void changeLanguage(String loc) {
		if (loc.equals("mn")){
			showLogin(false);
		}else{
			showLogin(true);
		}
		Locale locale = new Locale(loc);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	public void showLogin(boolean lang) {
		web.clearCache(true);
//		web.clearHistory();
		// voip%2Fsip+voip%2Fuserinfo+voip%2Fgetallmessage+voip%2Fdeletemessage+voip%2Flinkednumber+voip%2Fcallforward%2Fcheck+voip%2Fcallforward%2Foff+voip%2Fcallforward%2Fon+voip%2Fsms%2Fsend+voip%2Fsms%2Finbox+voip%2Fsms%2Foutbox+voip%2Fsms%2Fread+voip%2Fsms%2Findelete+voip%2Fsms%2Foutdelete+mo%2Fget_sip+mo%2Fget_profile+mo%2Fget_broadworksmobility+mo%2Fget_callforwardingalways+mo%2Fget_callforwardingbusy+mo%2Fget_callforwardingnoanswer+mo%2Fget_callforwardingnotreachable+mo%2Fget_calllogs+mo%2Fget_missed+mo%2Fget_placed+mo%2Fget_received+mo%2Fget_group+mo%2Fget_callinglineiddeliveryblocking+mo%2Fget_simultaneousringpersonal+mo%2Fget_sequentialring+mo%2Fput_broadworksmobility+mo%2Fput_callforwardingalways+mo%2Fput_callforwardingbusy+mo%2Fput_callforwardingnoanswer+mo%2Fput_callforwardingnotreachable+mo%2Fdelete_calllogs+mo%2Fdelete_missed+mo%2Fdelete_placed+mo%2Fdelete_received+mo%2Fput_callinglineiddeliveryblocking+mo%2Fput_simultaneousringpersonal+mo%2Fput_sequentialring+mo%2Fpost_callthrough
		if (lang)
			web.loadUrl("https://api.mobicom.mn/oauth/authorization/auth?client_id=f5dew6eilfL3WJFi&redirect_uri=com.mobinet.mnp75://oauth&response_type=code&scope=voip%2Fsip+connect%2Fvoip+voip%2Fuserinfo+voip%2Fcallforward%2Fcheck+voip%2Fcallforward%2Foff+voip%2Fcallforward%2Fon+voip%2Fsms%2Fsend+voip%2Fgetallmessage+voip%2Fdeletemessage+voip%2Flinkednumber+mo%2Fget_sip+mo%2Fget_profile+mo%2Fget_broadworksmobility+mo%2Fget_callforwardingalways+mo%2Fget_callforwardingbusy+mo%2Fget_callforwardingnoanswer+mo%2Fget_callforwardingnotreachable+mo%2Fget_calllogs+mo%2Fget_missed+mo%2Fget_placed+mo%2Fget_received+mo%2Fget_group+mo%2Fget_callinglineiddeliveryblocking+mo%2Fget_simultaneousringpersonal+mo%2Fget_sequentialring+mo%2Fput_broadworksmobility+mo%2Fput_callforwardingalways+mo%2Fput_callforwardingbusy+mo%2Fput_callforwardingnoanswer+mo%2Fput_callforwardingnotreachable+mo%2Fdelete_calllogs+mo%2Fdelete_missed+mo%2Fdelete_placed+mo%2Fdelete_received+mo%2Fput_callinglineiddeliveryblocking+mo%2Fput_simultaneousringpersonal+mo%2Fput_sequentialring+mo%2Fpost_callthrough&ln=en");
		else
			web.loadUrl("https://api.mobicom.mn/oauth/authorization/auth?client_id=f5dew6eilfL3WJFi&redirect_uri=com.mobinet.mnp75://oauth&response_type=code&scope=voip%2Fsip+connect%2Fvoip+voip%2Fuserinfo+voip%2Fcallforward%2Fcheck+voip%2Fcallforward%2Foff+voip%2Fcallforward%2Fon+voip%2Fsms%2Fsend+voip%2Fgetallmessage+voip%2Fdeletemessage+voip%2Flinkednumber+mo%2Fget_sip+mo%2Fget_profile+mo%2Fget_broadworksmobility+mo%2Fget_callforwardingalways+mo%2Fget_callforwardingbusy+mo%2Fget_callforwardingnoanswer+mo%2Fget_callforwardingnotreachable+mo%2Fget_calllogs+mo%2Fget_missed+mo%2Fget_placed+mo%2Fget_received+mo%2Fget_group+mo%2Fget_callinglineiddeliveryblocking+mo%2Fget_simultaneousringpersonal+mo%2Fget_sequentialring+mo%2Fput_broadworksmobility+mo%2Fput_callforwardingalways+mo%2Fput_callforwardingbusy+mo%2Fput_callforwardingnoanswer+mo%2Fput_callforwardingnotreachable+mo%2Fdelete_calllogs+mo%2Fdelete_missed+mo%2Fdelete_placed+mo%2Fdelete_received+mo%2Fput_callinglineiddeliveryblocking+mo%2Fput_simultaneousringpersonal+mo%2Fput_sequentialring+mo%2Fpost_callthrough&ln=mn");
	}

	protected void onServiceReady() {
		final Class<? extends Activity> classToStart;
		classToStart = LinphoneActivity.class;

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent newIntent = new Intent(LinphoneLauncherActivity.this, classToStart);
				Intent intent = getIntent();
                String msgShared = null;
				if (intent != null) {
					String action = intent.getAction();
					String type = intent.getType();
					newIntent.setData(intent.getData());
					if (Intent.ACTION_SEND.equals(action) && type != null) {
						if ("text/plain".equals(type) && intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                            msgShared = intent.getStringExtra(Intent.EXTRA_TEXT);
							newIntent.putExtra("msgShared", msgShared);
						}
					}
				}
				startActivity(newIntent);
                if (classToStart == LinphoneActivity.class && LinphoneActivity.isInstanciated() && msgShared != null) {
                    LinphoneActivity.instance().displayChat(null, msgShared);
                }
				finish();
			}
		}, 1000);
	}


	private class ServiceWaitThread extends Thread {
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException("waiting thread sleep() has been interrupted");
				}
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onServiceReady();
				}
			});
			mServiceThread = null;
		}
	}

	public static String STATE = "";
	public static Date STATEDATE = new Date(0);
	public static Date SIPDATE = new Date(0);
	private class OAuthClientTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				Log.d("CODE:", params[0]);
				OAuthClient.getTokens(params[0]);
			} catch (Exception ex) {
			}
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			if (UserControl.userType == UserControl.SipUserType.MNP75){
				connectToSip();
			}else{
				startActivityForResult(new Intent(LinphoneLauncherActivity.this,ConnectActivity.class), RequestCodeConnect);
			}
		}
	}


	public void connectToSip(){
		UserControl.userType = UserControl.SipUserType.MNP75;
		if (Calendar.getInstance().getTime().getTime() - STATEDATE.getTime() > 10000) {
			OauthRequest requestUserInfo = new OauthRequest(UserControl.getMNP75UserInfo(),
					OauthRequest.RequestType.GET);
			requestUserInfo.setOauthListener(new OauthRequest.OauthListener() {

				@Override
				public void onResult(String result) {
					// TODO Auto-generated method stub
					JSONObject object;
					try {
						object = new JSONObject(result);
						if (object.getInt("code") == 0) {
							STATE = object.getString("state");
							STATEDATE = Calendar.getInstance().getTime();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					Log.d("CALLER_ID_BLOCK", result);

				}

				@Override
				public void onBackgroundException(Exception e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onResultCodeWrong(String code) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onErrorDialogOkClick() {
					// TODO Auto-generated method stub
				}
			}, "", LinphoneLauncherActivity.this);
			requestUserInfo.execute();
		}
//		if (Calendar.getInstance().getTime().getTime() - SIPDATE.getTime() > 10000) {
			setContentView(R.layout.launcher);
			OauthRequest request = new OauthRequest(UserControl.getSipURL(),
					UserControl.getSipTYPE());
			request.setOauthListener(new OauthRequest.OauthListener() {

				@Override
				public void onResult(String result) {
					// TODO Auto-generated method stub

					Log.d("SIP", result);
					Gson gson = new Gson();

					VoipSipModel sip = gson
							.fromJson(result, VoipSipModel.class);

					try {
						Log.d(TAG, sip.info);
						String tmp = Encryption.decrypt(sip.info);
						android.util.Log
								.v(TAG, "info: " + tmp);
						String tmplist[] = tmp.split(":");
						android.util.Log.v(TAG, tmplist[0] + "+" + tmplist[1]);
						sipNumber = tmplist[0];
						sipPass = tmplist[1];
						SIPDATE = Calendar.getInstance().getTime();
						mHandler = new Handler();


						if (LinphoneService.isReady()) {
							onServiceReady();
						} else {
							// start linphone as background
							startService(new Intent(ACTION_MAIN).setClass(LinphoneLauncherActivity.this, LinphoneService.class));
							mServiceThread = new ServiceWaitThread();
							mServiceThread.start();
						}

						// LinphoneActivity.instance().logIn("75752085",
						// "Pass123456" , "ip-phone.mobinet.mn", false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onBackgroundException(Exception e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onResultCodeWrong(String code) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onErrorDialogOkClick() {
					// TODO Auto-generated method stub
//				initScreen();
				}
			}, "", LinphoneLauncherActivity.this);
			request.execute();
//		} else {
//			if (LinphoneService.isReady()) {
//				onServiceReady();
//			} else {
//				// start linphone as background
//				startService(new Intent(ACTION_MAIN).setClass(LinphoneLauncherActivity.this, LinphoneService.class));
//				mServiceThread = new ServiceWaitThread();
//				mServiceThread.start();
//			}
//		}
	}
}