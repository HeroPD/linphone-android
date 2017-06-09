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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobinet.model.DirectoryModel;
import com.rey.material.widget.Button;

public class DirectoryActivity extends BaseActivity {

	private EditText searchField;
	private ImageView clearField;
	private Button backButton;
	private ListView directoryListView;

	private List<DirectoryModel> data;
	private List<DirectoryModel> listData;
	private DirectoryAdapter adapter;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title = getString(R.string.mnp_directory);
		mInflater = getLayoutInflater();
		data = new ArrayList<DirectoryModel>();
		listData = new ArrayList<DirectoryModel>();
		setProgressView();

		OauthRequest request = new OauthRequest(
				UserControl.getGroupDirectory(), RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d("SETTINGS", result);
				setMainContentView();

				JSONObject object;
				try {
					object = new JSONObject(result);

					if (object.getInt("result") == 0) {
						JSONObject dataString = new JSONObject(object
								.getString("data"));
						JSONObject dataGroup = dataString.getJSONObject("Group");
						if (!dataGroup.isNull("groupDirectory")) {
							JSONObject active = dataGroup
									.getJSONObject("groupDirectory");
							JSONArray directories = active
									.getJSONArray("directoryDetails");
							
							for (int i = 0 ; i < directories.length() ; i++) {
								
								JSONObject item = directories.getJSONObject(i);
								
								DirectoryModel model = new DirectoryModel();
								model.userId = item.get("userId").toString();
								model.firstName = item.get("firstName").toString();
								model.lastName = item.get("lastName").toString();
								model.extension = item.get("extension").toString();
								if (!item.isNull("additionalDetails")){
									if (!item.getJSONObject("additionalDetails").isNull("impId"))
									model.impId = item.getJSONObject("additionalDetails").getString("impId");
									if (!item.getJSONObject("additionalDetails").isNull("emailAddress"))
									model.email = item.getJSONObject("additionalDetails").getString("emailAddress");
								}
								data.add(model);
							}
						}
					}
					listData.addAll(data);
					adapter.notifyDataSetChanged();
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

			}
		}, "", this);
		request.execute();
	}

	@SuppressLint("DefaultLocale")
	private void setMainContentView() {

		setContentView(R.layout.directory_activity);

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				listData.clear();
				String changedText = arg0.toString().toLowerCase();
				
				if (changedText.length() > 0 ){
					
					for (DirectoryModel item : data){
						if (item.firstName.toLowerCase().contains(changedText) || item.lastName.toLowerCase().contains(changedText) || item.extension.toLowerCase().contains(changedText)){
							listData.add(item);
						}
					}
					
				}else{
					listData.addAll(data);
				}
				
				adapter.notifyDataSetChanged();
			}
		});
		clearField = (ImageView) findViewById(R.id.clearSearchField);
		clearField.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchField.setText("");
			}
		});
		directoryListView = (ListView) findViewById(R.id.directory_list_view);
		directoryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				View view = DirectoryActivity.this.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(backButton.getWindowToken(), 0);
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		adapter = new DirectoryAdapter();
		directoryListView.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(backButton.getWindowToken(), 0);
		}
		super.onBackPressed();
	}

	private static class ViewHolder {

		public TextView name;
		public TextView number;
		public ImageView icon;
		public TextView separator;
	}
	
	
	private class DirectoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
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
			final DirectoryModel item = listData.get(position);
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.contact_cell, parent,
						false);

				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView.findViewById(R.id.number);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.separator = (TextView) convertView
						.findViewById(R.id.separator);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.separator.setVisibility(View.GONE);
			holder.name.setText(item.firstName+" "+item.lastName);
			holder.number.setText(item.extension);
			MaterialRippleLayout rippleView = (MaterialRippleLayout) convertView
					.findViewById(R.id.ripple_view);
			rippleView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(DirectoryActivity.this, DirectoryDetailActivity.class);
					DirectoryDetailActivity.model = item;
					DirectoryActivity.this.startActivity(intent);
				}
			});
			
			return convertView;
		}

	}
}
