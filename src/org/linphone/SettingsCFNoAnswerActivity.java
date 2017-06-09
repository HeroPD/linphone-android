/**
 * 
 */
package org.linphone;

import io.karim.MaterialRippleLayout;
import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;

/**
 * @author Showtime
 *
 */
public class SettingsCFNoAnswerActivity extends BaseActivity {

	private Button backButton;
	private Button saveButton;
	private Switch cfnaSwitch;
	private EditText phoneNumber;
	private TextView number;
	private MaterialRippleLayout numberContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title = getString(R.string.mnp_no_answer);
		setProgressView();
		
		OauthRequest request = new OauthRequest(
				UserControl.getCallForwardingNoAnswer(), RequestType.GET);
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
								.getJSONObject("CallForwardingNoAnswer");
						cfnaSwitch.setChecked(active.getBoolean("active"));
						if (!active.isNull("forwardToPhoneNumber")){
							phoneNumber.setText(active
									.getString("forwardToPhoneNumber"));
						}
						number.setText(active.getString("numberOfRings"));
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

		setContentView(R.layout.settings_cf_no_answer_activity);
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

		cfnaSwitch = (Switch) findViewById(R.id.switch_cf_na);
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		number = (TextView) findViewById(R.id.number);
		numberContainer = (MaterialRippleLayout) findViewById(R.id.number_of_rings);
		
		final LayoutInflater inflater = getLayoutInflater();
		
		
		numberContainer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View chooseDialog =  inflater.inflate(R.layout.choose_dialog, null);
				int cur = Integer.parseInt(number.getText().toString());
				final ChooseDialogAdapter adapter = new ChooseDialogAdapter(cur);
				ListView chooseListView = (ListView) chooseDialog.findViewById(R.id.choose_list);
				chooseListView.setAdapter(adapter);
				chooseListView.smoothScrollToPosition(cur-2);
				final MaterialDialog mMaterialDialog = new MaterialDialog(SettingsCFNoAnswerActivity.this);
				mMaterialDialog.setTitle("Number Of Rings")
						.setContentView(chooseDialog)
						.setPositiveButton("CHOOSE", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								number.setText(""+adapter.current);
								mMaterialDialog.dismiss();
							}
						});
				mMaterialDialog.show();
			}
		});
	}

	private void save() {

		String value = "";

		if (cfnaSwitch.isChecked()) {
			value = "true";
		} else {
			value = "false";
		}
		String body = "{\"data\":\"{\\\"CallForwardingNoAnswer\\\" : {\\\"@xmlns\\\" : \\\"http://schema.broadsoft.com/xsi\\\",\\\"active\\\" :"+value+",\\\"forwardToPhoneNumber\\\" : \\\""+phoneNumber.getText().toString()+"\\\",\\\"numberOfRings\\\" : "+number.getText().toString()+"}}\"}";
		saveRequest(body, UserControl.getCallForwardingNoAnswerPut(), RequestType.POST, "No answer");
		
	}
	
	
	public class ChooseDialogAdapter extends BaseAdapter{

		
		public int current;
		
		
		public ChooseDialogAdapter(int cur){
			current = cur;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 9;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.choose_dialog_item, parent, false);
			}
			MaterialRippleLayout container = (MaterialRippleLayout) convertView.findViewById(R.id.container);
			final RadioButton button = (RadioButton) convertView.findViewById(R.id.radio);
			button.setText(""+(position+2));
			button.setClickable(false);
			if (current == position+2){
				button.setChecked(true);
			}else{
				button.setChecked(false);
			}
			
			container.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (button.isChecked()){
						button.setChecked(false);
					}else{
						button.setChecked(true);
					}
					current = position+2;
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
		
	}
}
