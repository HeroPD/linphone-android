package org.linphone;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.ui.Progress;

import com.rey.material.widget.Button;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConnectActivity extends FragmentActivity {
	
	private Button backButton;
	private Button connectButton;
	private EditText phonenumber;
	private EditText password;
	protected Progress progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_activity);
		progress = new Progress(this);
		phonenumber = (EditText) findViewById(R.id.phonenumber);
		password = (EditText) findViewById(R.id.password);
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connect();
			}
		});
	}
	
	private void connect(){
		progress.show();
		String body = "{\"voipnumber\":\""+phonenumber.getText().toString()+"\",\"password\":\""+password.getText().toString()+"\"}";
		OauthRequest request = new OauthRequest(UserControl.getConnect(),RequestType.POST
				);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				progress.dismiss();
				Log.d("CONNECT", result);
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("code") == 0) {
						setResult(RESULT_OK);
						finish();
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
		},body , this);
		request.execute();
	}
}
