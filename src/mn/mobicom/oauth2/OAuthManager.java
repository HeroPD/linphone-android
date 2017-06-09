package mn.mobicom.oauth2;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class OAuthManager<T> {

	private static final String TAG = OAuthManager.class.getSimpleName();

	private Activity ctx;
	private String url;
	private String appendStr;
	private OAuthCallback<T> callback;
	private Gson gson;
	private Class<T> c;
	private OAuthTask task;

	public OAuthManager(Activity ctx, String url, String appendStr, Class<T> c,
			OAuthCallback<T> callback) {
		this.url = url;
		this.appendStr = appendStr;
		this.callback = callback;
		this.c = c;
		gson = new Gson();
		this.ctx = ctx;
	}

	public void useResource() {
		callback.onStarted();
		task = new OAuthTask();
		task.execute();
	}

	public void abort() {
		task.cancel(true);
	}

	private class OAuthTask extends AsyncTask<Void, Void, String> {
		OAuthException e;

		@Override
		protected String doInBackground(Void... params) {
//			if (appendStr.length() > 0) {
//				appendStr = "?" + appendStr;
//			}
			try {
				String downloadData = OAuthClient.useResource(url , appendStr,
						ctx);
				return downloadData;
			} catch (OAuthException e) {

				if (e.getMessage().equals("expired_token")) {
					try {
						OAuthClient.refreshToken(ctx);
						String downloadData = OAuthClient.useResource(url
								, appendStr, ctx);
						return downloadData;
					} catch (OAuthException e1) {
						this.e = e1;
						return "";
					}
				} else {
					this.e = e;

					return "a";
				}

			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Log.v(TAG," "+result);
			if (result.length() == 0) {
				callback.onAuthFailure(e);
				return;
			}
			if (result.length() == 1) {
				callback.onFailure(e);
				return;
			}
			callback.onSuccess(result);
			try {
				T item = gson.fromJson(result, c);
				callback.onSuccess(item);
			} catch (Exception e) {
				Exception ea = new Exception(
						"Json format ex");
				callback.onFailure(ea);
				Log.v("OAuthManager", e.getMessage());
			}
		}
	}
}
