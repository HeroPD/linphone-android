/*
CallButton.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

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
package org.linphone.ui;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneLauncherActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.R;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.rey.material.widget.ImageButton;

import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.CheckState;
import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;

/**
 * @author Guillaume Beraudo
 */
public class CallButton extends ImageButton implements OnClickListener, AddressAware {

	private AddressText mAddress;
	public void setAddressWidget(AddressText a) { mAddress = a; }

	public void setExternalClickListener(OnClickListener e) { setOnClickListener(e); }
	public void resetClickListener() { setOnClickListener(this); }
	public Context context;
	protected Progress progress;

	public CallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		progress = new Progress(context);
		setOnClickListener(this);
	}

	public void onClick(View v) {
		SharedPreferences pref = context.getApplicationContext().getSharedPreferences(LinphoneLauncherActivity.sipNumber, 0);
		if (pref.getBoolean("callthrough", false)){
			final CharSequence[] items = {context.getString(R.string.mnp_call), context.getString(R.string.mnp_call_through)};

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
						case 0:
							normalCall();
							break;
						case 1:
							callTrhough();
							break;

						default:
							break;
					}
				}


			});
			AlertDialog alert = builder.create();
			alert.show();
		}else{
			normalCall();
		}
	}

	private void callTrhough() {
		// TODO Auto-generated method stub
		progress.show();
		SharedPreferences pref = context.getApplicationContext().getSharedPreferences(LinphoneLauncherActivity.sipNumber, 0);
		OauthRequest request = new OauthRequest(UserControl.getCallThrough(), OauthRequest.RequestType.POST
		);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				progress.dismiss();
				Log.d("CALLTHROUGH", result);
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result") == 201) {

						JSONObject data = new JSONObject(object.getString("data"));
						JSONObject isdn = data.getJSONObject("IMRNInfo");

						Intent call = new Intent(Intent.ACTION_DIAL);
						call.setData(Uri.parse("tel:" + isdn.getString("imrn")));
						context.startActivity(call);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onBackgroundException(Exception e) {
				// TODO Auto-generated method stub
				progress.dismiss();
			}

			@Override
			public void onResultCodeWrong(String code) {
				// TODO Auto-generated method stub
				progress.dismiss();
			}

			@Override
			public void onErrorDialogOkClick() {
				// TODO Auto-generated method stub
				progress.dismiss();

			}
		}, "{\"data\": \"{\\\"data1\\\" : \\\"" + mAddress.getText().toString() + "\\\", \\\"data2\\\" : \\\"" + pref.getString("number", "") + "\\\"}\"}", (Activity) context);
		request.execute();

	}

	private void normalCall(){
		CheckState.checkState(context, new CheckState.StateCreateOrUse() {
			@Override
			public void action() {
				try {
					if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
						if (mAddress.getText().length() > 0) {
							LinphoneManager.getInstance().newOutgoingCall(mAddress);
						} else {
							if (LinphonePreferences.instance().isBisFeatureEnabled()) {
								LinphoneCallLog[] logs = LinphoneManager.getLc().getCallLogs();
								LinphoneCallLog log = null;
								for (LinphoneCallLog l : logs) {
									if (l.getDirection() == CallDirection.Outgoing) {
										log = l;
										break;
									}
								}
								if (log == null) {
									return;
								}

								LinphoneProxyConfig lpc = LinphoneManager.getLc().getDefaultProxyConfig();
								if (lpc != null && log.getTo().getDomain().equals(lpc.getDomain())) {
									mAddress.setText(log.getTo().getUserName());
								} else {
									mAddress.setText(log.getTo().asStringUriOnly());
								}
								mAddress.setSelection(mAddress.getText().toString().length());
								mAddress.setDisplayedName(log.getTo().getDisplayName());
							}
						}
					}
				} catch (LinphoneCoreException e) {
					LinphoneManager.getInstance().terminateCall();
					onWrongDestinationAddress();
				}
			}
		});
	}
	
	protected void onWrongDestinationAddress() {
		Toast.makeText(getContext()
				,String.format(getResources().getString(R.string.warning_wrong_destination_address),mAddress.getText().toString())
				,Toast.LENGTH_LONG).show();
	}
}
