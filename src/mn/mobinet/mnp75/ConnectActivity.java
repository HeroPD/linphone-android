package mn.mobinet.mnp75;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;
import mn.mobinet.mnp75.R;
import org.linphone.ui.Progress;

import com.rey.material.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

public class ConnectActivity extends FragmentActivity {
	
	private Button backButton;
	private Button connectButton;
	private Button logoutButton;
    private WebView webView;
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
                UserControl.logoutURL(webView, new UserControl.LogoutAction() {
                    @Override
                    public void loggedOut() {
                        Intent broadcastintent = new Intent();
                        broadcastintent.setAction("com.package.ACTION_LOGOUT");
                        ConnectActivity.this.sendBroadcast(broadcastintent);
                        ConnectActivity.this.finish();
                    }
                });
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
		logoutButton = (Button) findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                UserControl.logoutURL(webView, new UserControl.LogoutAction() {
                    @Override
                    public void loggedOut() {
                        Intent broadcastintent = new Intent();
                        broadcastintent.setAction("com.package.ACTION_LOGOUT");
                        ConnectActivity.this.sendBroadcast(broadcastintent);
                        ConnectActivity.this.finish();
                    }
                });
			}
		});
        webView = (WebView)findViewById(R.id.webView);
	}

    @Override
    public void onBackPressed() {
        progress.show();
        UserControl.logoutURL(webView, new UserControl.LogoutAction() {
            @Override
            public void loggedOut() {
                progress.dismiss();
                Intent broadcastintent = new Intent();
                broadcastintent.setAction("com.package.ACTION_LOGOUT");
                ConnectActivity.this.sendBroadcast(broadcastintent);
                ConnectActivity.this.finish();
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
