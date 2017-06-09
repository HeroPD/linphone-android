/**
 * 
 */
package org.linphone;

import io.karim.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Showtime
 *
 */
public class SettingsSimultaneousActivity extends BaseActivity {

	private ListView listView;
	private Button backButton;
	private Button saveButton;

	private Switch activeSwitch;
	private Switch activeSwitch1;
	private List<LocationRing> locationsRing;
	private Adapter adapter;

	public static final int REQUEST_EDIT = 1;
	public static final int REQUEST_ADD = 2;
	public static final int RESULT_DELETE = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title = getString(R.string.mnp_simultaneous_ring);
		setProgressView();
		
		OauthRequest request = new OauthRequest(UserControl.getSimultaneous(),
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
								.getJSONObject("SimultaneousRingPersonal");
						activeSwitch.setChecked(active.getBoolean("active"));

						if (active.getString("incomingCalls").equals(
								"Ring for all Incoming Calls")) {
							activeSwitch1.setChecked(true);
						} else {
							activeSwitch1.setChecked(false);
						}
						if (!active.isNull("simRingLocations")) {
							JSONObject locationContainer = active
									.getJSONObject("simRingLocations");
							JSONArray locations = locationContainer
									.getJSONArray("simRingLocation");
							for (int i = 0; i < locations.length(); i++) {
								JSONObject item = locations.getJSONObject(i);
								LocationRing locItem = new LocationRing();
								locItem.address = item.getString("address");
								locItem.answerconfimation = item
										.getBoolean("answerConfirmationRequired");
								locationsRing.add(locItem);
							}

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
	
	private void setMainView(){

		setContentView(R.layout.settings_simultaneous_activity);
		locationsRing = new ArrayList<SettingsSimultaneousActivity.LocationRing>();
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
				R.layout.settings_simultaneous_header, null);

		activeSwitch = (Switch) header.findViewById(R.id.switch_active);
		activeSwitch1 = (Switch) header.findViewById(R.id.switch_active1);

		View footer = getLayoutInflater().inflate(
				R.layout.settings_simultaneous_footer, null);

		Button addLocation = (Button) footer.findViewById(R.id.addLocatin);
		addLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsSimultaneousActivity.this,
						SettingsSimultaneousEditActivity.class);
				startActivityForResult(intent, REQUEST_ADD);
			}
		});

		listView.addHeaderView(header);
		listView.addFooterView(footer);
		adapter = new Adapter();
		listView.setAdapter(adapter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#startActivityForResult(android.content.Intent,
	 * int)
	 */
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		intent.putExtra("requestCode", requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case RESULT_DELETE: {
			int position = data.getExtras().getInt("position");
			locationsRing.remove(position);
			adapter.notifyDataSetChanged();
		}

			break;
		case REQUEST_ADD:{
			LocationRing loc = new LocationRing();
			loc.address = data.getExtras().getString("address");
			loc.answerconfimation = data.getExtras().getBoolean("answer");
			locationsRing.add(loc);
			adapter.notifyDataSetChanged();
		}
			break;
		case REQUEST_EDIT:

			int position = data.getExtras().getInt("position");
			LocationRing loc = locationsRing.get(position);
			loc.address = data.getExtras().getString("address");
			loc.answerconfimation = data.getExtras().getBoolean("answer");
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	class LocationRing {
		public String address;
		public boolean answerconfimation;
	}

	static class ViewHolder {
		public TextView title;
	}
	
	private void save(){
		String value = "";

		if (activeSwitch.isChecked()) {
			value = "true";
		} else {
			value = "false";
		}
		String value1 = "";
		if (activeSwitch1.isChecked()) {
			value1 = "Ring for all Incoming Calls";
		} else {
			value1 = "Do not Ring if on a Call";
		}
		
		String list = "";
		
		
		for (int i = 0 ; i < locationsRing.size();i++) {
			LocationRing item = locationsRing.get(i);
			String tempValue = "";
			if (item.answerconfimation)
				tempValue = "true";
			else
				tempValue = "false";
			
			list +="{\\\"address\\\":"+item.address+",\\\"answerConfirmationRequired\\\":"+tempValue+"}";
			if (i != locationsRing.size()-1)
				list+=",";
			
		}
		
		String body = "{\"data\":\"{\\\"SimultaneousRingPersonal\\\" : {\\\"@xmlns\\\" : \\\"http://schema.broadsoft.com/xsi\\\",\\\"active\\\" : "
				+ value
				+ ",\\\"incomingCalls\\\" : \\\""
				+ value1
				+ "\\\",\\\"simRingLocations\\\":{\\\"simRingLocation\\\":["+list+"]}}}\"}";
		saveRequest(body, UserControl.getSimultaneousPut(), RequestType.POST ,"Simultaneous Ring");

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
			return locationsRing.size();
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
			final LocationRing locR = locationsRing.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.settings_simultaneous_item, parent, false);
			}

			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(locationsRing.get(position).address);
			MaterialRippleLayout container = (MaterialRippleLayout) convertView
					.findViewById(R.id.container);

			container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(
							SettingsSimultaneousActivity.this,
							SettingsSimultaneousEditActivity.class);
					intent.putExtra("address", locR.address);
					intent.putExtra("answer", locR.answerconfimation);
					intent.putExtra("position", position);
					startActivityForResult(intent, REQUEST_EDIT);
				}
			});

			return convertView;
		}

	}
}
