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
import org.linphone.SettingsCFNoAnswerActivity.ChooseDialogAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;

/**
 * @author Showtime
 *
 */
public class SettingsMobilityActivity extends BaseActivity {

	private Button backButton;
	private Button saveButton;
	private Switch activeSwitch;
	private EditText forwardNumber;
	private TextView number;
	private MaterialRippleLayout numberContainer;

	private Boolean allowCall;
	private Boolean preventCall;
	private Boolean includeShared;
	private Boolean includeBroad;
	private Boolean includeExecutive;
	private Boolean enableAlerting;
	private Boolean primary;
	private String identityUri;
	private String checkedString ;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title = getString(R.string.mnp_mobile_office_mobility);
		setProgressView();
		OauthRequest request = new OauthRequest(UserControl.getMobility(),
				RequestType.GET);
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
								.getJSONObject("BroadWorksMobility");
						activeSwitch.setChecked(active.getBoolean("active"));
						if (!active.isNull("mobileIdentity")) {
							JSONObject mobile = active
									.getJSONObject("mobileIdentity");
							if (!mobile.isNull("mobileNumber")) {
								forwardNumber.setText(mobile
										.getString("mobileNumber"));
								enableAlerting = mobile
										.getBoolean("enableAlerting");
								primary = mobile.getBoolean("primary");
								identityUri = mobile.getString("identityUri");
							} else {
								saveButton.setVisibility(View.GONE);
							}

						}

						allowCall = active
								.getBoolean("allowCallAnchoringControl");
						preventCall = active
								.getBoolean("preventCallsToOwnMobiles");

						if (!active.isNull("profileIdentity")) {

							JSONObject profile = active
									.getJSONObject("profileIdentity");

							if (!profile.isNull("profileAlertingPolicy")) {

								JSONObject alert = profile
										.getJSONObject("profileAlertingPolicy");
								number.setText(alert.getString("devicesToRing"));
								includeShared = alert
										.getBoolean("includeSharedCallAppearance");
								includeBroad = alert
										.getBoolean("includeBroadWorksAnywhere");
								includeExecutive = alert
										.getBoolean("includeExecutiveAssistant");
							}

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

		setContentView(R.layout.settings_mobility_activity);

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
		number = (TextView) findViewById(R.id.number);
		numberContainer = (MaterialRippleLayout) findViewById(R.id.number_of_rings);

		final LayoutInflater inflater = getLayoutInflater();
		
		numberContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View chooseDialog = inflater.inflate(
						R.layout.settings_mobility_dialog, null);

				final RadioButton radio1 = (RadioButton) chooseDialog
						.findViewById(R.id.radioButton1);
				final RadioButton radio2 = (RadioButton) chooseDialog
						.findViewById(R.id.radioButton2);
				final RadioButton radio3 = (RadioButton) chooseDialog
						.findViewById(R.id.radioButton3);

				String ptr = number.getText().toString();
				
				if (ptr.equals("Fixed")) {
					radio1.setChecked(true);
				} else if (ptr.equals("Mobile")) {
					radio2.setChecked(true);
				} else if (ptr.equals("Both")) {
					radio3.setChecked(true);
				}
				
				radio1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {
							checkedString = "Fixed";
							radio2.setChecked(false);
							radio3.setChecked(false);
						}
					}
				});
				radio2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {
							checkedString = "Mobile";
							radio1.setChecked(false);
							radio3.setChecked(false);
						}
					}
				});
				radio3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {
							checkedString = "Both";
							radio1.setChecked(false);
							radio2.setChecked(false);
						}
					}
				});

				final MaterialDialog mMaterialDialog = new MaterialDialog(
						SettingsMobilityActivity.this);
				mMaterialDialog
						.setTitle(getString(R.string.mnp_phone_to_ring))
						.setContentView(chooseDialog)
						.setPositiveButton(getString(R.string.mnp_choose),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										number.setText(checkedString);
										mMaterialDialog.dismiss();
									}
								});
				mMaterialDialog.show();
			}
		});

	}

	private String getBoolString(Boolean value) {

		if (value)
			return "true";
		return "false";
	}

	private void save() {

		String jsonData = "";

		jsonData += "{\"BroadWorksMobility\":{";
		jsonData += "\"@xmlns\":\"http://schema.broadsoft.com/xsi\",";
		jsonData += "\"active\":" + getBoolString(activeSwitch.isChecked())
				+ ",";
		// jsonData
		// +="\"preventCallsToOwnMobiles\":"+getBoolString(preventCall)+",";
		// jsonData
		// +="\"allowCallAnchoringControl\":"+getBoolString(allowCall)+",";
		// jsonData +="\"mobileIdentity\":{";
		// jsonData
		// +="\"mobileNumber\":"+forwardNumber.getText().toString()+",";
		// jsonData +="\"identityUri\":\""+identityUri+"\",";
		// jsonData +="\"enableAlerting\":"+getBoolString(enableAlerting)+",";
		// jsonData +="\"primary\":"+getBoolString(primary)+"},";
		jsonData += "\"profileIdentity\":{\"profileAlertingPolicy\":{";
		// jsonData
		// +="\"includeSharedCallAppearance\":"+getBoolString(includeShared)+",";
		// jsonData
		// +="\"includeBroadWorksAnywhere\":"+getBoolString(includeBroad)+",";
		// jsonData +="\"includeExecutiveAssistant\":"+includeExecutive+",";
		jsonData += "\"devicesToRing\":\"" + number.getText().toString()
				+ "\"}}}}";
		String body = "{\"data\":\"" + jsonData.replace("\"", "\\\"") + "\"}";
		saveRequest(body, UserControl.getMobilityPut(), RequestType.POST, "Mobility");
		
	}
}
