/**
 * 
 */
package mn.mobinet.mnp75;

import io.karim.MaterialRippleLayout;

import com.rey.material.widget.Button;

import mn.mobicom.classes.UserControl;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import mn.mobinet.mnp75.R;

/**
 * @author Showtime
 *
 */
public class SettingsAcitivty extends FragmentActivity {

	private Button backButton;
	private ListView listView;
	private String mnp75[];
	private String mobileOffice[];
	private SettingsAdapter adapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		mnp75 = new String[] { getString(R.string.mnp_call_forwarding) };
		mobileOffice = new String[] { getString(R.string.mnp_call_through) , getString(R.string.mnp_caller_id_blocking), getString(R.string.mnp_no_answer),
				getString(R.string.mnp_always), getString(R.string.mnp_not_reachable), getString(R.string.mnp_busy),
				getString(R.string.mnp_simultaneous_ring), getString(R.string.mnp_sequential_ring),
				getString(R.string.mnp_mobile_office_mobility)};
		listView = (ListView) findViewById(R.id.settings_list);
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		switch (UserControl.userType) {
		case MNP75:
			initiliazeMNP75();

			break;
		case MOBILEOFFICE:
			initiliazeMobileOffice();

			break;

		default:
			break;
		}

	}

	public void initiliazeMNP75() {

		adapter = new SettingsAdapter(mnp75);
		listView.setAdapter(adapter);
	}

	public void initiliazeMobileOffice() {
		adapter = new SettingsAdapter(mobileOffice);
		listView.setAdapter(adapter);
	}

	class ViewHolder {

		public TextView name;
		public TextView separator;
		public MaterialRippleLayout back;
	}

	class SettingsAdapter extends BaseAdapter {

		private String[] data;

		/**
		 * 
		 */
		public SettingsAdapter(String[] data) {
			// TODO Auto-generated constructor stub
			this.data = data;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data[arg0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
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
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflator = getLayoutInflater();
				convertView = inflator.inflate(R.layout.settings_item, parent,
						false);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.setting_title);
				holder.separator = (TextView) convertView
						.findViewById(R.id.separator);
				holder.back = (MaterialRippleLayout) convertView
						.findViewById(R.id.setting_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Log.d("DATA", data[position]);
			holder.name.setText(data[position]);
			switch (UserControl.userType) {
			case MNP75:
				switch (position) {
				case 0:
					visibleSection(getString(R.string.mnp_header_call_forwarding), holder.separator);
					break;

				default:
					break;
				}
				break;
			case MOBILEOFFICE:
				switch (position) {
				case 0:
					visibleSection(getString(R.string.mnp_general), holder.separator);
					break;
				case 1:
					visibleSection(getString(R.string.mnp_header_call_option), holder.separator);
					break;
				case 2:
					visibleSection(getString(R.string.mnp_header_call_forwarding), holder.separator);
					break;
				case 6:
					visibleSection(getString(R.string.mnp_header_incoming_call), holder.separator);
					break;
				case 8:
					visibleSection(getString(R.string.mnp_header_mobile_office), holder.separator);
					break;
				default:
					holder.separator.setVisibility(View.GONE);
					break;
				}
				break;

			default:
				break;
			}

			holder.back.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (UserControl.userType) {
					case MOBILEOFFICE:

						switch (position) {
						case 0:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCallThroughActivity.class));
							break;
						case 1:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCallerIDBlockingActivity.class));
							break;
						case 2:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCFNoAnswerActivity.class));
							break;
						case 3:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCFAlwaysActivity.class));
							break;
						case 4:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCFNotReachableAvtivity.class));
							break;
						case 5:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCFBusyActivity.class));
							break;
						case 6:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsSimultaneousActivity.class));
							break;
						case 7:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsSequantialActivity.class));
							break;
						case 8:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsMobilityActivity.class));
							break;
						default:
							break;
						}

						break;
					case MNP75:

						switch (position) {
						case 0:
							startActivity(new Intent(SettingsAcitivty.this,
									SettingsCFVoipActivity.class));
							break;
						default:
							break;
						}

						break;

					default:
						break;
					}
				}
			});

			return convertView;
		}

		private void visibleSection(String name, TextView sep) {
			sep.setText(name);
			sep.setVisibility(View.VISIBLE);
		}
	}

}
