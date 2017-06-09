/**
 * 
 */
package org.linphone;

import me.drakeet.materialdialog.MaterialDialog;

import org.linphone.SettingsSequantialActivity.ChooseDialogAdapter;

import io.karim.MaterialRippleLayout;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Showtime
 *
 */
public class SettingsSequantialEditActivity extends Activity{
	
	private int requestCode;
	private Button backButton;
	
	private EditText phoneNumber;
	private Switch switchActive;
	private MaterialRippleLayout container;
	private TextView numberOfRings;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_sequantial_edit_activity);
		
		requestCode = getIntent().getExtras().getInt("requestCode");
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		container = (MaterialRippleLayout) findViewById(R.id.number_of_rings);
		numberOfRings = (TextView) findViewById(R.id.number);
		switchActive = (Switch) findViewById(R.id.switch_active);
		container.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View chooseDialog =  SettingsSequantialEditActivity.this.getLayoutInflater().inflate(R.layout.choose_dialog, null);
				int cur = Integer.parseInt(numberOfRings.getText().toString());
				final ChooseDialogAdapter adapter = new ChooseDialogAdapter(cur);
				ListView chooseListView = (ListView) chooseDialog.findViewById(R.id.choose_list);
				chooseListView.setAdapter(adapter);
				chooseListView.smoothScrollToPosition(cur-2);
				final MaterialDialog mMaterialDialog = new MaterialDialog(SettingsSequantialEditActivity.this);
				mMaterialDialog.setTitle(getString(R.string.mnp_number_of_rings))
						.setContentView(chooseDialog)
						.setPositiveButton(getString(R.string.mnp_choose), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								numberOfRings.setText(""+adapter.current);
								mMaterialDialog.dismiss();
							}
						});
				mMaterialDialog.show();
			}
		});
		
		
		phoneNumber.setText(getIntent().getExtras().getInt("address") == 0 ? "" : ""+getIntent().getExtras().getInt("address"));
		switchActive.setChecked(getIntent().getExtras().getBoolean("answer"));
		numberOfRings.setText(""+getIntent().getExtras().getInt("numberofring"));
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (requestCode == SettingsSequantialActivity.REQUEST_EDIT) {
			Intent intent = getIntent();
			intent.putExtra("numberofrings", numberOfRings.getText().toString());
			intent.putExtra("address", phoneNumber.getText().toString().length() == 0 ? "0" : phoneNumber.getText().toString());
			intent.putExtra("answer", switchActive.isChecked());
			intent.putExtra("position",
					getIntent().getExtras().getInt("position"));
			setResult(requestCode, intent);
			finish();
		} else {
			super.onBackPressed();
		}
	}
}
