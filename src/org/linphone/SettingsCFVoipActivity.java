/**
 * 
 */
package org.linphone;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

/**
 * @author Showtime
 *
 */
public class SettingsCFVoipActivity extends BaseActivity{

	
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
		title = getString(R.string.mnp_call_forwarding);
		setProgressView();
		
		OauthRequest request = new OauthRequest(
				UserControl.getCallForwardCheck(), RequestType.POST);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				setMainView();
				Log.d("CALLER_ID_BLOCK", result);
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result") == 0) {
						String data = object.getString("callfwnumber");
						boolean cf_act ;
						if (data.length() > 0){
							cf_act = true;
						}else{
							cf_act = false;
						}
						activeSwitch.setChecked(cf_act);
						forwardNumber.setText(data);
						

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

		setContentView(R.layout.settings_cf_voip_activity);

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

		String url = "";
		String body = "";
		if (activeSwitch.isChecked()) {
			
			url = UserControl.getCallForwadingOn();
			body = "{\"callfwnumber\":\""+forwardNumber.getText().toString()+"\"}";
		} else {
			url = UserControl.getCallForwadingOff();
		}
		
		saveRequest(body, url , RequestType.POST, "Call Forwarding");
		
	}
}
