package mn.mobicom.classes;

import io.karim.MaterialRippleLayout;
import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.UserControl.SipUserType;
import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AboutUsActivity;
import org.linphone.HelpActivity;
import org.linphone.LinphoneLauncherActivity;
import org.linphone.MNP75UserInfoActivity;
import org.linphone.PromDetailActivity;
import org.linphone.R;
import org.linphone.SettingsAcitivty;
import org.linphone.ui.Progress;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OtherFragment extends Fragment {

	public ListView listView;
	private WebView webView;
	protected Progress progress;
	private String names[];
	private int pics[];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = new Progress(getActivity());
		switch (UserControl.userType) {
			case MNP75:
				names = new String[] { getString(R.string.mnp_userinfo),
						getString(R.string.mnp_settins),
						getString(R.string.mnp_help),
						getString(R.string.mnp_about_us),
						getString(R.string.mnp_promotions),
						getString(R.string.mnp_disconnect)+" - "+LinphoneLauncherActivity.sipNumber,
						getString(R.string.mnp_logout) };
				pics = new int[] { R.drawable.other_profile,
						R.drawable.other_settings, R.drawable.other_help,
						R.drawable.other_about, R.drawable.other_promotion,
						R.drawable.other_disconnect,
						R.drawable.other_logout };
				break;
			case MOBILEOFFICE:
				names = new String[] { getString(R.string.mnp_settins),
						getString(R.string.mnp_help),
						getString(R.string.mnp_about_us),
						getString(R.string.mnp_connect),
						getString(R.string.mnp_logout) };
				pics = new int[] { R.drawable.other_settings,
						R.drawable.other_help, R.drawable.other_about,
						R.drawable.other_promotion,
						R.drawable.other_logout,R.drawable.other_logout };
				break;
			default:
				break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.mobi_other_fragment, container,
				false);
		listView = (ListView) view.findViewById(R.id.other_listview);
		listView.setAdapter(new OtherAdapter(getActivity()));
		webView = (WebView) view.findViewById(R.id.webView);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	class OtherAdapter extends BaseAdapter {

		Activity context;

		public OtherAdapter(Activity context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			public ImageView icon;
			public TextView title;
			public MaterialRippleLayout container;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				convertView = inflator.inflate(
						R.layout.mobi_other_listview_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) convertView
						.findViewById(R.id.other_list_icon);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.other_list_title);
				viewHolder.container = (MaterialRippleLayout) convertView
						.findViewById(R.id.container);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.title.setText(names[position]);
			viewHolder.icon.setImageResource(pics[position]);
			final int tempPosition;
			if (UserControl.userType == SipUserType.MNP75) {
				tempPosition = position - 1;
			} else {
				tempPosition = position;
			}

			viewHolder.container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					switch (tempPosition) {
					case -1:
						startActivity(new Intent(getActivity(),
								MNP75UserInfoActivity.class));

						break;
					case 0:

						startActivity(new Intent(getActivity(),
								SettingsAcitivty.class));

						break;
					case 1:

						startActivity(new Intent(getActivity(),
								HelpActivity.class));

						break;
					case 2:

						startActivity(new Intent(getActivity(),
								AboutUsActivity.class));

						break;
					case 3:
						startActivity(new Intent(getActivity(),
								PromDetailActivity.class));
						break;
					case 4:
						final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
						mMaterialDialog.setTitle(getResources().getString(R.string.mnp_disconnect_warning_header))
								.setMessage(getResources().getString(R.string.mnp_disconnect_warning))
								.setPositiveButton(getResources().getString(R.string.mnp_yes), new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										mMaterialDialog.dismiss();
										disconnect();
									}
								}).setNegativeButton(getResources().getString(R.string.mnp_no), new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										mMaterialDialog.dismiss();
									}
								});

						mMaterialDialog.show();
						break;
					case 5:
						progress.show();
						UserControl.logoutURL(webView, new UserControl.LogoutAction() {
							@Override
							public void loggedOut() {
								progress.dismiss();
								Intent broadcastintent = new Intent();
								broadcastintent.setAction("com.package.ACTION_LOGOUT");
								getActivity().sendBroadcast(broadcastintent);
								getActivity().finish();
							}
						});
						break;
					default:
						break;
					}
				}
			});
			return convertView;
		}

	}
	
	private void disconnect(){
		progress.show();
		OauthRequest request = new OauthRequest(UserControl.getConnect(),RequestType.DELETE
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
						UserControl.userType = SipUserType.DEFAULT;
						UserControl.logoutURL(webView, new UserControl.LogoutAction() {
							@Override
							public void loggedOut() {
								Intent broadcastintent = new Intent();
								broadcastintent.setAction("com.package.ACTION_LOGOUT");
								getActivity().sendBroadcast(broadcastintent);
								getActivity().finish();
							}
						});
					} else {
						if (!object.isNull("info")) {
							showAlert(object.getString("info"));
						} else {
							showAlert(getResources().getString(R.string.connectionerror));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showAlert(getResources().getString(R.string.connectionerror));
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
		},"" , getActivity());
		request.execute();
	}
	
	public void showAlert(String title){
		final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
		mMaterialDialog.setTitle(title)
				.setMessage(null)
				.setPositiveButton("OK", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mMaterialDialog.dismiss();
					}
				});

		mMaterialDialog.show();
	}

}
