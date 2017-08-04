package org.linphone;

import java.util.ArrayList;
import java.util.List;

import mn.mobicom.classes.UserControl;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rey.material.widget.Button;

public class MNP75UserInfoActivity extends BaseActivity {

	private Button backButton;
	private ListView infoListView;
	private List<MNP75UserInfoActivity.InfoMnp75> items;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mInflater = getLayoutInflater();
		title = getString(R.string.mnp_userinfo);
		setProgressView();

		OauthRequest request = new OauthRequest(UserControl.getMNP75UserInfo(),
				RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				setMainView(result);
				Log.d("CALLER_ID_BLOCK", result);

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

	private void setMainView(String result) {

		setContentView(R.layout.mnp75_userinfo_activity);
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		items = new ArrayList<MNP75UserInfoActivity.InfoMnp75>();
		try {
			JSONObject object = new JSONObject(result);
			if (object.getInt("code") == 0) {

				items.add(new InfoMnp75(getString(R.string.mnp_lastname), object
						.getString("lastName")));
				items.add(new InfoMnp75(getString(R.string.mnp_firstname), object
						.getString("firstName")));
				items.add(new InfoMnp75(getString(R.string.mnp_email), object.getString("email")));
				items.add(new InfoMnp75(getString(R.string.mnp_unit), object.getString("unit")));
				items.add(new InfoMnp75(getString(R.string.mnp_valid_date), object.getString("endDate")));
				items.add(new InfoMnp75(getString(R.string.mnp_type), object
						.getString("isPrepaid")));
				LinphoneLauncherActivity.STATE = object.getString("state");
				items.add(new InfoMnp75(getString(R.string.mnp_status), object.getString("state")));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		infoListView = (ListView) findViewById(R.id.info_list);

		InfoAdapter adapter = new InfoAdapter();
		infoListView.setAdapter(adapter);
	}

	class InfoMnp75 {

		public String title;
		public String info;

		public InfoMnp75(String t, String i) {
			title = t;
			info = i;
		}
	}

	class InfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View view = (View) mInflater.inflate(R.layout.info_list_item,
					parent, false);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView info = (TextView) view.findViewById(R.id.info);
			
			title.setText(items.get(position).title);
			info.setText(items.get(position).info);
			return view;
		}

	}
}
