/**
 * 
 */
package org.linphone;

import io.karim.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.SettingsCFNoAnswerActivity.ChooseDialogAdapter;
import org.linphone.ui.Progress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;

/**
 * @author Showtime
 *
 */
public class SettingsSequantialActivity extends BaseActivity {

	public static final int REQUEST_EDIT = 1001;

	private ListView listView;
	private Button backButton;
	private Button saveButton;

	private List<Locations> locations;

	private Adapter adapter;

	private Switch activeSwitch;
	private Switch activeSwitch1;
	private Switch activeSwitch2;
	private Switch activeSwitch3;
	private TextView criteriaName;
	private MaterialRippleLayout numberOfRings;
	private TextView number;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		title = getString(R.string.mnp_sequential_ring);
		setProgressView();
		OauthRequest request = new OauthRequest(UserControl.getSequential(),
				RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d("CALLER_ID_BLOCK", result);
				setMainView();
				try {
					JSONObject object = new JSONObject(result);
					if (object.getInt("result") == 0) {
						JSONObject data = new JSONObject(object
								.getString("data"));
						JSONObject active = data
								.getJSONObject("SequentialRing");

						activeSwitch.setChecked(active
								.getBoolean("ringBaseLocationFirst"));
						activeSwitch1.setChecked(active
								.getBoolean("continueIfBaseLocationIsBusy"));
						activeSwitch2.setChecked(active
								.getBoolean("callerMayStopSearch"));
						number.setText(""
								+ active.getInt("baseLocationNumberOfRings"));
						JSONObject location1 = active
								.getJSONObject("location1");
						Locations loc1 = new Locations();
						if (!location1.isNull("address"))
							loc1.address = location1.getInt("address");
						loc1.number = location1.getInt("numberOfRings");
						loc1.answer = location1
								.getBoolean("answerConfirmationRequired");

						locations.add(loc1);
						JSONObject location2 = active
								.getJSONObject("location2");
						Locations loc2 = new Locations();
						if (!location2.isNull("address"))
							loc2.address = location2.getInt("address");
						loc2.number = location2.getInt("numberOfRings");
						loc2.answer = location2
								.getBoolean("answerConfirmationRequired");

						locations.add(loc2);
						JSONObject location3 = active
								.getJSONObject("location3");
						Locations loc3 = new Locations();
						if (!location3.isNull("address"))
							loc3.address = location3.getInt("address");
						loc3.number = location3.getInt("numberOfRings");
						loc3.answer = location3
								.getBoolean("answerConfirmationRequired");

						locations.add(loc3);

						JSONObject location4 = active
								.getJSONObject("location4");
						Locations loc4 = new Locations();
						if (!location4.isNull("address"))
							loc4.address = location4.getInt("address");
						loc4.number = location4.getInt("numberOfRings");
						loc4.answer = location4
								.getBoolean("answerConfirmationRequired");

						locations.add(loc4);
						JSONObject location5 = active
								.getJSONObject("location5");
						Locations loc5 = new Locations();
						if (!location5.isNull("address"))
							loc5.address = location5.getInt("address");
						loc5.number = location5.getInt("numberOfRings");
						loc5.answer = location5
								.getBoolean("answerConfirmationRequired");

						locations.add(loc5);
						if (!active.isNull("criteriaActivationList")) {
							JSONObject criteria = active.getJSONObject(
									"criteriaActivationList").getJSONObject(
									"criteriaActivation");
							criteriaName.setText(criteria
									.getString("criteriaName"));
							activeSwitch3.setChecked(criteria
									.getBoolean("active"));
						}

						adapter.notifyDataSetChanged();

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
	
	public void setMainView(){
		setContentView(R.layout.settings_sequantial_activity);
		locations = new ArrayList<SettingsSequantialActivity.Locations>();
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
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
		listView = (ListView) findViewById(R.id.listView);
		View header = getLayoutInflater().inflate(
				R.layout.settings_sequential_header, null);

		activeSwitch = (Switch) header.findViewById(R.id.switch_active);
		activeSwitch1 = (Switch) header.findViewById(R.id.switch_active1);
		activeSwitch2 = (Switch) header.findViewById(R.id.switch_active2);
		numberOfRings = (MaterialRippleLayout) header
				.findViewById(R.id.number_of_rings);
		number = (TextView) header.findViewById(R.id.number);
		final LayoutInflater inflater = getLayoutInflater();
		
		numberOfRings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View chooseDialog =  inflater.inflate(R.layout.choose_dialog, null);
				int cur = Integer.parseInt(number.getText().toString());
				final ChooseDialogAdapter adapter = new ChooseDialogAdapter(cur);
				ListView chooseListView = (ListView) chooseDialog.findViewById(R.id.choose_list);
				chooseListView.setAdapter(adapter);
				chooseListView.smoothScrollToPosition(cur-2);
				final MaterialDialog mMaterialDialog = new MaterialDialog(SettingsSequantialActivity.this);
				mMaterialDialog.setTitle(getString(R.string.mnp_number_of_rings))
						.setContentView(chooseDialog)
						.setPositiveButton(getString(R.string.mnp_choose), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								number.setText(""+adapter.current);
								mMaterialDialog.dismiss();
							}
						});
				mMaterialDialog.show();
			}
		});

		View footer = getLayoutInflater().inflate(
				R.layout.settings_sequential_footer, null);
		criteriaName = (TextView) footer.findViewById(R.id.criteria_text);
		activeSwitch3 = (Switch) footer.findViewById(R.id.switch_active);
		activeSwitch3.setEnabled(false);
		
		listView.addHeaderView(header);
		listView.addFooterView(footer);

		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		intent.putExtra("requestCode", requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case REQUEST_EDIT:

			int position = data.getExtras().getInt("position");
			Locations loc = locations.get(position);
			loc.address = Integer.parseInt(data.getExtras()
					.getString("address"));
			loc.answer = data.getExtras().getBoolean("answer");
			loc.number = Integer.parseInt(data.getExtras().getString(
					"numberofrings"));
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	private String getBoolString(Boolean value) {

		if (value)
			return "true";
		return "false";
	}

	private void save() {

		
		String jsonData = "";

		jsonData += "{\"SequentialRing\":{";
		jsonData += "\"@xmlns\":{\"$\":\"http://schema.broadsoft.com/xsi\"},";
		jsonData += "\"ringBaseLocationFirst\":{\"$\":"
				+ getBoolString(activeSwitch.isChecked()) + "},";
		jsonData += "\"baseLocationNumberOfRings\":{\"$\":"
				+ number.getText().toString() + "},";
		jsonData += "\"continueIfBaseLocationIsBusy\":{\"$\":"
				+ getBoolString(activeSwitch1.isChecked()) + "},";
		jsonData += "\"callerMayStopSearch\":{\"$\":"
				+ getBoolString(activeSwitch2.isChecked()) + "},";
		for (int i = 0; i < locations.size(); i++) {
			Locations loc = locations.get(i);

			if (loc.address != 0) {
				jsonData += "\"location" + (i + 1) + "\":{";
				jsonData += "\"address\":{\"$\":" + loc.address + "},";
				jsonData += "\"numberOfRings\":{\"$\":" + loc.number + "},";
				jsonData += "\"answerConfirmationRequired\":{\"$\":"
						+ getBoolString(loc.answer) + "}";
				jsonData += "}";
				boolean last = true;
				for (int j = i + 1; j < locations.size(); j++) {
					if (locations.get(j).address != 0) {
						last = false;
					}
				}
				if (!last)
					jsonData += ",";
			}
		}

		// jsonData += "\"criteriaActivationList\":{\"criteriaActivation\":{";
		// jsonData +=
		// "\"criteriaName\":"+criteriaName.getText().toString()+",";
		// jsonData += "\"active\":"+getBoolString(activeSwitch3.isChecked());
		// jsonData +="}}";
		jsonData += "}}";

		
		String body = "{\"data\":\"" + jsonData.replace("\"", "\\\"") + "\"}";
		saveRequest(body, UserControl.getSequentialPut(), RequestType.POST ,"Sequential Ring");
		
	}

	public class Locations {

		public int address;
		public int number;
		public boolean answer;

		public String toJson() {
			return "";
		}

	}

	public class ChooseDialogAdapter extends BaseAdapter {

		public int current;

		public ChooseDialogAdapter(int cur) {
			current = cur;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 19;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.choose_dialog_item, parent, false);
			}
			MaterialRippleLayout container = (MaterialRippleLayout) convertView
					.findViewById(R.id.container);
			final RadioButton button = (RadioButton) convertView
					.findViewById(R.id.radio);
			button.setText("" + (position + 2));
			button.setClickable(false);
			if (current == position + 2) {
				button.setChecked(true);
			} else {
				button.setChecked(false);
			}

			container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (button.isChecked()) {
						button.setChecked(false);
					} else {
						button.setChecked(true);
					}
					current = position + 2;
					notifyDataSetChanged();
				}
			});

			return convertView;
		}

	}

	class Adapter extends BaseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return locations.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final Locations locR = locations.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.settings_simultaneous_item, parent, false);
			}

			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText("Location " + (position + 1));
			MaterialRippleLayout container = (MaterialRippleLayout) convertView
					.findViewById(R.id.container);

			container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SettingsSequantialActivity.this,
							SettingsSequantialEditActivity.class);
					intent.putExtra("address", locR.address);
					intent.putExtra("answer", locR.answer);
					intent.putExtra("numberofring", locR.number);
					intent.putExtra("position", position);
					startActivityForResult(intent, REQUEST_EDIT);
				}
			});

			return convertView;
		}

	}
}
