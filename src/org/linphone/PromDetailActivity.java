package org.linphone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HTTP;
import mn.mobicom.classes.CircleFragmentAdapter;
import mn.mobicom.classes.CirclePageIndicator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mobinet.model.PromData;
import com.rey.material.widget.Button;

public class PromDetailActivity  extends FragmentActivity{
	
	public FrameLayout frame;
	private Button backButton ;
	public CircleFragmentAdapter mAdapter;
	public ViewPager mPager;
	public CirclePageIndicator mIndicator;
	public List<PromData.data> promotionList;
	private ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobi_prom_detail_main);
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		progress = (ProgressBar) findViewById(R.id.launcher_progress);
		mPager = (ViewPager) findViewById(R.id.pager);

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		
		JSONAsyncTask async = new JSONAsyncTask();
		if (LinphoneLauncherActivity.isNetworkConnected(this)) {
			async.execute();
		}
		
	}
	
	class JSONAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				// ------------------>>

				HttpGet httppost = new HttpGet(
						"http://staticcss.mobicom.mn/promlist.json");
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient
						.execute(httppost);
				BufferedReader in;
				// StatusLine stat = response.getStatusLine();
				String json = "";
				try {
					in = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent(), HTTP.UTF_8));

					String line = "";
					json = "";
					while ((line = in.readLine()) != null) {

						json += line;
					}

					if (response.getStatusLine().getStatusCode() == 200) {
						Log.d("Connections", json);
						return json;
					} else {
						// //Log.println(Log.INFO, AppDefines.NRANGE_TAG,
						// "Response code:" + response);
					}
				} catch (Exception e) {
					Log.d("SocketException", "Catch Excetion");
				}

			} catch (IOException e) {
				e.printStackTrace();
				Log.d("SocketException", "Catch Excetion");
			}

			return "";
		}

		protected void onPostExecute(String result) {
			Log.d("PROMOTION", result);
			progress.setVisibility(View.GONE);
			if (result != null) {
				Gson gson = new Gson();

				PromData dt = gson.fromJson(result, PromData.class);
				promotionList = dt.Promotions;

				mAdapter = new CircleFragmentAdapter(
						getSupportFragmentManager(), promotionList);
				mPager.setAdapter(mAdapter);
				mIndicator.setViewPager(mPager);
			}
		}
	}

}
