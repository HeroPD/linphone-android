/**
 * 
 */
package org.linphone;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * @author Showtime
 *
 */
public class SettingsCFNotReachableAvtivity extends BaseActivity {

	private Button backButton;
	private Button saveButton;
	private Switch activeSwitch;
	private EditText forwardNumber;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title = getString(R.string.mnp_not_reachable);
		setProgressView();
		
		initActivity();

	}
	
	private void initActivity(){
		OauthRequest request = new OauthRequest(
				UserControl.getCallForwardingNotReachable(), RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				setMainView();
				Log.d("CALLER_ID_BLOCK", result);
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result") == 0) {
						JSONObject data = new JSONObject(object
								.getString("data"));
						JSONObject active = data
								.getJSONObject("CallForwardingNotReachable");
						activeSwitch.setChecked(active.getBoolean("active"));
						if (!active.isNull("forwardToPhoneNumber")){
							forwardNumber.setText(active
									.getString("forwardToPhoneNumber"));
						}
						
						
					}
				} catch (JSONException e) {
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
				backOnMainThread();
				
			}
		}, "", this);
		request.execute();
	}
	
	private void setMainView(){

		setContentView(R.layout.settings_cf_not_reachable_activity);

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				save();
			}
		});
		activeSwitch = (Switch) findViewById(R.id.switch_active);
		forwardNumber = (EditText) findViewById(R.id.phone_number);

	}

	private void save() {

		String value = "";

		if (activeSwitch.isChecked()) {
			value = "true";
		} else {
			value = "false";
		}

		String body = "{\"data\":\"{\\\"CallForwardingNotReachable\\\" : {\\\"@xmlns\\\" : \\\"http://schema.broadsoft.com/xsi\\\",\\\"active\\\" : "
				+ value
				+ ",\\\"forwardToPhoneNumber\\\" : \\\""
				+ forwardNumber.getText().toString()
				+ "\\\"}}\"}";
		saveRequest(body, UserControl.getCallForwardingNotReachablePut(), RequestType.POST, "Not reachable");
		
	}
}
