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

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

/**
 * @author Showtime
 *
 */
public class SettingsCallerIDBlockingActivity extends BaseActivity{
	
	private Button backButton;
	private Button saveButton;
	private Switch callerIdSwitch;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		title = getString(R.string.mnp_caller_id_blocking);
		
		setProgressView();
		
		OauthRequest request = new OauthRequest(UserControl
				.getCallerIdBlock(), RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d("CALLER_ID_BLOCK", result);
				setMainView();
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result")==0){
						JSONObject data = new JSONObject(object.getString("data"));
						JSONObject active = data.getJSONObject("CallingLineIDDeliveryBlocking");
						callerIdSwitch.setChecked(active.getBoolean("active"));
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
	
	private void setMainView (){

		setContentView(R.layout.settings_caller_id_blocking_activity);
		
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
		
		callerIdSwitch = (Switch) findViewById(R.id.switch_caller_id);
		
	}
	
	private void save(){
		
		String value = "";
		
		if (callerIdSwitch.isChecked()){
			value = "true";
		}else{
			value = "false";
		}
		
		String body = "{\"data\":\"{\\\"CallingLineIDDeliveryBlocking\\\" : {\\\"@xmlns\\\" : \\\"http://schema.broadsoft.com/xsi\\\",\\\"active\\\" : "+value+"}}\"}";
		saveRequest(body, UserControl.getCallerIdBlockPut(), RequestType.POST, "Caller Id Block");
		
	}

}
