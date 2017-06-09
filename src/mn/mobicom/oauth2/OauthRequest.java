package mn.mobicom.oauth2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.drakeet.materialdialog.MaterialDialog;

import org.json.JSONObject;
import org.linphone.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class OauthRequest extends AsyncTask<String, Void, String> {

	public enum RequestType {

		POST, GET,DELETE, DEFAULT
	}

	public interface OauthListener {

		public void onBackgroundException(Exception e);

		public void onResult(String result);

		public void onResultCodeWrong(String code);

		public void onErrorDialogOkClick();

	}

	private OauthListener listener;
	private String url;
	private RequestType rType;
	private String body;
	private Activity context;

	public void setOauthListener(OauthListener listener, String body,
			Activity context) {

		this.listener = listener;
		this.body = body;
		this.context = context;
	}

	public OauthRequest(String url, RequestType type) {

		this.url = url;
		this.rType = type;

	}

	public boolean isNetworkConnected(final Activity context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			context.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					final OauthListener listenerLocal = listener;
					final MaterialDialog mMaterialDialog = new MaterialDialog(
							context);
					mMaterialDialog
							.setTitle("NO INTERNET CONNECTION")
							.setMessage(
									"Check your wifi or 3G network is working?")
							.setPositiveButton("OK",
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											if (listenerLocal != null)
												listenerLocal.onErrorDialogOkClick();
											mMaterialDialog.dismiss();
										}
									});

					mMaterialDialog.show();
				}
			});

			return false;
		} else
			return true;
	}

	public class NoInternetException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NoInternetException(String message) {
			super(message);
		}
	}
	private ConnectionHttp con;
	@Override
	protected String doInBackground(String... params) {
		try {

			// ------------------>>
			con = new ConnectionHttp();
			if (isNetworkConnected(context)) {
				switch (rType) {
				case POST:
					return con.connectionFactory(context , url, body);
				case GET:
					return con.connectionFactory(context , url, null);
				case DELETE:
					return con.connectionFactory(context , url, "delete");
				default:
					break;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(String result) {

		try {

			if (result != null) {
				Log.d("Oauth:", result);

				JSONObject model = new JSONObject(result);

				if (!model.isNull("error")) {
					if (model.getString("error").equals("expired_token")) {

						RefreshTokenTask task = new RefreshTokenTask(this);
						task.execute();

					} else {
						final OauthListener listenerLocal = listener;
						
						final MaterialDialog mMaterialDialog = new MaterialDialog(
								context);
						mMaterialDialog
								.setTitle(model.getString("error"))
								.setCanceledOnTouchOutside(false)
								.setMessage(
										model.getString("error_description"))
								.setPositiveButton("OK",
										new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												if (listenerLocal != null)
													listenerLocal.onErrorDialogOkClick();
												mMaterialDialog.dismiss();

											}
										});

						mMaterialDialog.show();

					}

				} else {

					if (listener != null) {
						if ((!model.isNull("code") && model.getInt("code") == 0)) {
							listener.onResult(result);
						} else if ((!model.isNull("result") && (model
								.getInt("result") == 0 || model
								.getInt("result") == 201))) {
							listener.onResult(result);
						} else {
							if (listener != null)
								listener.onBackgroundException(new Exception());
							if (!model.isNull("info")){
								final MaterialDialog mMaterialDialog = new MaterialDialog(
										context);
								mMaterialDialog
										.setTitle(null)
										.setCanceledOnTouchOutside(false)
										.setMessage(model.getString("info"))
										.setPositiveButton("OK", new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mMaterialDialog.dismiss();
											}
										});

								mMaterialDialog.show();
							}else{
								final MaterialDialog mMaterialDialog = new MaterialDialog(
										context);
								mMaterialDialog
										.setTitle(context.getString(R.string.error))
										.setCanceledOnTouchOutside(false)
										.setMessage(context.getString(R.string.connectionerror))
										.setPositiveButton("OK", new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mMaterialDialog.dismiss();
											}
										});

								mMaterialDialog.show();
							}
						}
					}
				}

			} else {

				if (listener != null)
					listener.onBackgroundException(new Exception());
				final MaterialDialog mMaterialDialog = new MaterialDialog(
						context);
				mMaterialDialog
						.setTitle(context.getString(R.string.error))
						.setCanceledOnTouchOutside(false)
						.setMessage(context.getString(R.string.connectionerror))
						.setPositiveButton("OK", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mMaterialDialog.dismiss();
							}
						});

				mMaterialDialog.show();

			}
		} catch (Exception e) {
			if (listener != null)
				listener.onBackgroundException(e);
		}

	}

	public class RefreshTokenTask extends AsyncTask<Void, Void, Void> {

		private OauthRequest request;

		/**
		 * 
		 */
		public RefreshTokenTask(OauthRequest request) {
			// TODO Auto-generated constructor stub
			this.request = request;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				OAuthClient.refreshToken(context);
				OauthRequest req = new OauthRequest(request.url, request.rType);
				req.setOauthListener(request.listener, request.body, context);
				req.execute();
			} catch (OAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	public class ConnectionHttp {

		public static final String TAG = "NETWORK";
		private URL obj;
		private HttpsURLConnection con;

		public String connectionFactory(Context ctx, String url,
				String postParam) throws Exception {
			Log.d(TAG, url);
			obj = new URL(url);

			con = (HttpsURLConnection) obj.openConnection();
			con.setConnectTimeout(10000);
			con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", "Android");
			con.setRequestProperty("Connection", "Keep-Alive");

			if (OAuthClient.access_token != null) {
				con.setRequestProperty("Authorization", "Bearer "
						+ OAuthClient.access_token);
			}
			// For POST only - START
			if (postParam != null && postParam.equalsIgnoreCase("delete")){
				con.setRequestMethod("DELETE");
			}else if (postParam != null) {
				Log.d(TAG,postParam);
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				OutputStream os = con.getOutputStream();
				os.write(postParam.getBytes());
				os.flush();
				os.close();
				// For POST only - END
			} else {
				con.setRequestMethod("GET");
			}

			int responseCode = con.getResponseCode();

			Log.d(TAG, responseCode + "-" + con.getResponseMessage());
			
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return response.toString();
			} else {
				if (listener != null)
					listener.onResultCodeWrong(""+responseCode);
				Log.d("RESPONSE CODE", ""+responseCode);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return response.toString();
			}
		}

		public void cancelRequest() {
			con.disconnect();
		}
	}
}
