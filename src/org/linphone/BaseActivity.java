/**
 * 
 */
package org.linphone;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.ui.Progress;

import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import com.rey.material.widget.Button;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author Showtime
 *
 */
public class BaseActivity extends Activity{

	Button progressBack ;
	public String title = "custom" ;
	protected Progress progress;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		progress = new Progress(this);
	}
	
	public void setProgressView (){
		setContentView(R.layout.progress);
		progressBack = (Button) findViewById(R.id.back_button);
		progressBack.setText(title);
		progressBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
	
	
	public void setErrorView(String error){
		setContentView(R.layout.error);
		progressBack = (Button) findViewById(R.id.back_button);
		progressBack.setText(title);
		progressBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
	
	
	public void backOnMainThread(){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				onBackPressed();
			}
		});
	}
	
	public void showAlertDialog(String title , String message){
		final MaterialDialog mMaterialDialog = new MaterialDialog(this);
		mMaterialDialog.setTitle(title)
				.setMessage(message)
				.setPositiveButton("OK", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (progress.isShowing())
							progress.dismiss();
						finish();
						mMaterialDialog.dismiss();
					}
				});

		mMaterialDialog.show();
	}
	
	
	public void showSavedDialog(String title){
		final MaterialDialog mMaterialDialog = new MaterialDialog(this);
		mMaterialDialog.setTitle(BaseActivity.this.getString(R.string.mnp_settins))
				.setMessage(BaseActivity.this.getString(R.string.mnp_saved))
				.setPositiveButton("OK", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (progress.isShowing())
							progress.dismiss();
						finish();
						mMaterialDialog.dismiss();
					}
				});

		mMaterialDialog.show();
	}
	
	public void saveRequest(String body , String url , RequestType type , final String dialogTitle){
		progress.show();
		OauthRequest request = new OauthRequest(url,type
				);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d("SETTINGS", result);
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result") == 0) {
						showSavedDialog(dialogTitle);
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
				backOnMainThread();
				progress.dismiss();
				
			}
		},body , this);
		request.execute();
		
	}
	
	
	
}
