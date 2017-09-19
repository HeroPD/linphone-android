package mn.mobicom.oauth2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class OAuthClient {

	public static final String TAG = "OAUTH";

	public static final String client_id = "f5dew6eilfL3WJFi"; // APP_ID
	public static final String client_secret = "fJ#@i fjlsjfiowj lfJSFIOw2lf jsi oiweofjlksjfows"; // APP_SECRET
	public static final String redirect_uri = "com.mobinet.mnp75://oauth"; // REDIRECT_URI –±—É—Ü–∞—Ö —Ö–æ–ª–±–æ–æ—Å
	public static String refresh_token; // refresh_token
	public static String access_token; // access_token
	public static boolean isLogin = false;
	public static TokenListener tokenListener = new TokenListener() {

		@Override
		public void refreshToken(String refreshToken) {
		}

		@Override
		public void accessToken(String accessToken) {
		}
	};

	/*
	 * @param scope –∞—à–∏–≥–ª–∞—Ö —Å—ç—Ä–≤–∏—Å“Ø“Ø–¥ authorizaiton code –∞–≤–∞—Ö —Ñ—É–Ω–∫—Ü
	 * 
	 * @return browser –¥—É—É–¥–∞—Ö —Ö–æ–ª–±–æ–æ—Å
	 * 
	 * @exception OAuthException OAuth 2.0 –∞–ª–¥–∞–∞ –≥–∞—Ä—Å–∞–Ω “Ø–µ–¥
	 * 
	 * @see OAuthException
	 */
	public static String authorize(String... scope) throws OAuthException {
		String authorization = "https://api.mobicom.mn/oauth/authorization/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s";
		StringBuilder sc = new StringBuilder();
		for (String s : scope) {
			if (s != null) {
				if (sc.length() != 0)
					sc.append(" ");
				sc.append(s);
			}
		}
		// https://accounts.mobicom.mn/logout.html?continue=
		// https%3A%2F%2Fapi.mobicom.mn%2Foauth%2Fauthorization%2Fauth%3Fresponse_type%3Dcode%26client_id=3f9b06e9612f4c40%26redirect_uri=mn.mobicom.mobicombranded://oauth&logout=true
		try {
			return String
					.format(authorization,
							client_id == null ? "" : URLEncoder.encode(
									client_id, "UTF-8"),
							redirect_uri == null ? "" : URLEncoder.encode(
									redirect_uri, "UTF-8"), URLEncoder.encode(
									sc.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Log.e(TAG, "", e);
			throw new OAuthApplicationException("–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
		}
	}

	public static String logout() throws OAuthException {
		String logouturl = "https://z-accounts.mobicom.mn/logout.html?continue=%s";
		String authorization = "https://api.mobicom.mn/oauth/authorization/auth?client_id=%s&redirect_uri=%s&response_type=code&logout=true";
		try {
			// Log.v("LOGOUT URL", String.format(
			// authorization,
			// client_id == null ? "" : URLEncoder.encode(client_id,
			// "UTF-8"),
			// redirect_uri == null ? "" : URLEncoder.encode(redirect_uri,
			// "UTF-8")));
			String cont = String.format(
					authorization,
					client_id == null ? "" : URLEncoder.encode(client_id,
							"UTF-8"),
					redirect_uri == null ? "" : URLEncoder.encode(redirect_uri,
							"UTF-8"));
			return String.format(logouturl, URLEncoder.encode(cont, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Log.e(TAG, "", e);
			throw new OAuthApplicationException("–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
		}
	}

	/*
	 * @param code authorization_code access token –±–æ–ª–æ–Ω refresh token –∞–≤–∞—Ö
	 * —Ñ—É–Ω–∫—Ü
	 * 
	 * @exception OAuthException OAuth 2.0 –∞–ª–¥–∞–∞ –≥–∞—Ä—Å–∞–Ω “Ø–µ–¥
	 * 
	 * @see OAuthException
	 */
	public static void getTokens(String code) throws OAuthException {
		String url = "https://api.mobicom.mn/oauth/authorization/token";
		String content = "client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code&code=%s";
		try {
			content = String.format(
					content,
					client_id == null ? "" : URLEncoder.encode(client_id,
							"UTF-8"),// ///////
					client_secret == null ? "" : URLEncoder.encode(
							client_secret, "UTF-8"),// /////////
					redirect_uri == null ? "" : URLEncoder.encode(redirect_uri,
							"UTF-8"),// //////////////////
					code == null ? "" : URLEncoder.encode(code, "UTF-8"));
			Map<String, String> header = new HashMap<String, String>();
			header.put("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			HttpResponse response = RequestSender.POST(url + "?" + content, "",
					header);
			
			Log.d("RESPONSE TOKE", response.getCode()+"-"+response.getBody());
			if (response.getCode() == 200) {
				JSONObject json = new JSONObject(response.getBody());
				refresh_token = json.getString("refresh_token");
				if (tokenListener != null)
					tokenListener.refreshToken(refresh_token);
				access_token = json.getString("access_token");
				if (tokenListener != null)
					tokenListener.accessToken(access_token);
			} else if ((response.getCode() / 100) == 4) {
				JSONObject json = new JSONObject(response.getBody());
				String error = json.getString("error");
				// Log.e(TAG + "getTokens", error);
				if (error != null) {
					if (error.contains("unauthorized_client")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					} else if (error.contains("invalid_grant")) {
						throw new OAuthAuthorizationException(
								"–ê—Ö–∏–Ω –Ω—ç–≤—Ç—Ä—ç—Ö —à–∞–∞—Ä–¥–ª–∞–≥–∞—Ç–∞–π");
					} else if (error.contains("invalid")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					} else if (error.contains("unsupported")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					}
				}
				throw new OAuthException("–¢–∞ –∞—Ö–∏–Ω –æ—Ä–æ–ª–¥–æ–Ω–æ —É—É");
			} else {
				throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
			}
		} catch (UnsupportedEncodingException e) {
			// Log.e(TAG, "", e);
			throw new OAuthApplicationException("–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
		} catch (JSONException e) {
			// Log.e(TAG, "Parsing json response", e);
			throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
		} catch (IOException e) {
			// Log.e(TAG, "Sending authorization_code request", e);
			throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
		}
	}

	/*
	 * Token refresh —Ö–∏–π—Ö —Ñ—É–Ω–∫—Ü
	 * 
	 * @exception OAuthException OAuth 2.0 –∞–ª–¥–∞–∞ –≥–∞—Ä—Å–∞–Ω “Ø–µ–¥
	 * 
	 * @see OAuthException
	 */
	public static void refreshToken(Activity ctx) throws OAuthException {
		String url = "https://api.mobicom.mn/oauth/authorization/token";
		String content = "client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=refresh_token&refresh_token=%s";
		try {
			content = String.format(
					content,
					client_id == null ? "" : URLEncoder.encode(client_id,
							"UTF-8"),// ///////
					client_secret == null ? "" : URLEncoder.encode(
							client_secret, "UTF-8"),// /////////
					redirect_uri == null ? "" : URLEncoder.encode(redirect_uri,
							"UTF-8"),// //////////////////
					refresh_token == null ? "" : URLEncoder.encode(
							refresh_token, "UTF-8"));
			Map<String, String> header = new HashMap<String, String>();
			header.put("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			HttpResponse response = RequestSender.POST(url, content, header );
			if (response.getCode() == 200) {
				JSONObject json = new JSONObject(response.getBody());
				access_token = json.getString("access_token");
				if (tokenListener != null)
					tokenListener.accessToken(access_token);
			} else if ((response.getCode() / 100) == 4) {

				JSONObject json = new JSONObject(response.getBody());
				String error = json.getString("error");
				// Log.e(TAG, error);
				if (error != null) {
					// if (!isLogin) {
					// GlobalMethods.showLoginActivity((Activity) ctx, true);
					// } else
					//InternalStorageTools.logOut(ctx);
					Intent broadcastintent = new Intent();
					broadcastintent.setAction("com.package.ACTION_LOGOUT");
					ctx.sendBroadcast(broadcastintent);
					
					if (error.contains("unauthorized_client")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					} else if (error.contains("invalid_grant")) {
						throw new OAuthAuthorizationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					} else if (error.contains("invalid")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					} else if (error.contains("unsupported")) {
						throw new OAuthApplicationException(
								"–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
					}
				}
				throw new OAuthException("–¢–∞ –∞—Ö–∏–Ω –æ—Ä–æ–ª–¥–æ–Ω–æ —É—É");
			} else {
				throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
			}
		} catch (UnsupportedEncodingException e) {
			// Log.e(TAG, "", e);
			// User user = InternalStorageTools.readUser(ctx);
			// user.autoLogin = false;
			// user.isLogged = false;
			// InternalStorageTools.saveUser(user, ctx);
			throw new OAuthApplicationException("–ê–ª–¥–∞–∞—Ç–∞–π –ø—Ä–æ–≥—Ä–∞–º –±–∞–π–Ω–∞");
		} catch (JSONException e) {
			if (!isLogin) {
				//GlobalMethods.showLoginActivity((Activity) ctx, true);
			} else {
				throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
			}
			// //Log.e(TAG, "Parsing json response", e);
			// throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
		} catch (IOException e) {
			// Log.e(TAG, "Sending refresh_token request", e);
			throw new OAuthException("–¢–∞ –∏–Ω—Ç–µ—Ä–Ω–µ—Ç —Ö–æ–ª–±–æ–ª—Ç–æ–æ —à–∞–ª–≥–∞–Ω–∞ —É—É");
		}
	}

	/*
	 * Token refresh —Ö–∏–π—Ö —Ñ—É–Ω–∫—Ü
	 * 
	 * @exception OAuthException OAuth 2.0 –∞–ª–¥–∞–∞ –≥–∞—Ä—Å–∞–Ω “Ø–µ–¥
	 * 
	 * @see OAuthException
	 */
	public static String changePassword() {
		String url = "https://accounts.mobicom.mn/security.html";
		return url;
	}

	/*
	 * @param url resource-–Ω url
	 * 
	 * @return resource resposne
	 */
	public static String useResource(String url,String append ,  Activity ctx)
			throws OAuthException {
		Map<String, String> header = new HashMap<String, String>();
		if (refresh_token == null) {

			if (!isLogin) {
//				GlobalMethods.showLoginActivity((Activity) ctx, true);
			}
			throw new OAuthAuthorizationException(
					"Refresh token-g авах хэрэгтэй");
		}
		if (access_token == null) {
			throw new OAuthTokenException("Access token-g авах хэрэгтэй");
		}
		header.put("Authorization", "Bearer " + access_token);

		header.put("Content-Type","application/json");
//		header.put(C, value)
		Log.d("Authorization", header.get("Authorization"));
		try {
			HttpResponse response = RequestSender.GET(url, header);
			if (response.getCode() == 200) {
				Log.v(TAG,response.getBody());
				return response.getBody();
			} else if ((response.getCode() / 100) == 4) {
				JSONObject json = new JSONObject(response.getBody());
				String error = json.getString("error");
				String error_desc = json.getString("error_description");
				Log.e(TAG, error);
				Log.e(TAG, response.getBody());
				if (error != null) {
					if (error.contains("insufficient_scope")) {
						// User user = InternalStorageTools.readUser(ctx);
						// user.autoLogin = false;
						// user.isLogged = false;
						// InternalStorageTools.saveUser(user, ctx);
						throw new OAuthApplicationException(
								"insufficient_scope");
					} else if (error.contains("quota_exceeded")) {
						throw new OAuthException(error_desc);
					} else if (error.contains("invalid_token")
							|| error.contains("expired_token")) {
						refreshToken(ctx);
						return useResource(url, append ,ctx);
						// throw new OAuthTokenException("expired_token");
					} else if (error.contains("invalid")) {
						throw new OAuthApplicationException(
								"Алдаатай програм байна");
					}
				}
				throw new OAuthException("Та ахин оролдоно уу");
			} else {
				throw new OAuthException("Та интернет холболтоо шалгана уу");
			}
		} catch (UnsupportedEncodingException e) {
			// Log.e(TAG, "", e);
			// User user = InternalStorageTools.readUser(ctx);
			// user.autoLogin = false;
			// user.isLogged = false;
			// InternalStorageTools.saveUser(user, ctx);

			throw new OAuthApplicationException("Алдаатай програм байна");
		} catch (JSONException e) {
			// Log.e(TAG, "Parsing json response", e);
			throw new OAuthException("Та интернет холболтоо шалгана уу");
		} catch (IOException e) {
			// Log.e(TAG, "Sending resource request", e);
			throw new OAuthException("Та интернет холболтоо шалгана уу");
		} catch (OAuthException e) {
			throw e;
		} catch (Exception e) {
			// Log.e(TAG, "Sending resource request", e);
			throw new OAuthException("Та интернет холболтоо шалгана уу");
		}
	}
}
