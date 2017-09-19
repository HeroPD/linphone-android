/**
 * 
 */
package mn.mobinet.mnp75;

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import mn.mobinet.mnp75.R;

/**
 * @author Showtime
 *
 */
public class SettingsSimultaneousEditActivity extends Activity {

	private int requestCode;
	private Button backButton;
	private Button doneButton;

	private EditText phoneNumber;
	private Switch switchActive;
	private Button deleteButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_simultaneous_edit_activity);

		requestCode = getIntent().getExtras().getInt("requestCode");
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		doneButton = (Button) findViewById(R.id.save_button);
		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add();
			}
		});

		switchActive = (Switch) findViewById(R.id.switch_active);
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		deleteButton = (Button) findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				delete();
			}
		});
		switch (requestCode) {
		case SettingsSimultaneousActivity.REQUEST_ADD:
			initAdd();
			break;
		case SettingsSimultaneousActivity.REQUEST_EDIT:
			initEdit();
			break;
		default:
			break;
		}

	}

	private void initAdd() {

		deleteButton.setVisibility(View.GONE);

	}

	private void initEdit() {
		doneButton.setVisibility(View.GONE);
		phoneNumber.setText(getIntent().getExtras().getString("address"));
		switchActive.setChecked(getIntent().getExtras().getBoolean("answer"));
	}

	private void delete() {
		Intent intent = getIntent();
		intent.putExtra("position", getIntent().getExtras().getInt("position"));
		setResult(SettingsSimultaneousActivity.RESULT_DELETE, intent);
		finish();
	}

	private void add() {
		Intent intent = getIntent();
		intent.putExtra("address", phoneNumber.getText().toString());
		intent.putExtra("answer", switchActive.isChecked());
		setResult(requestCode, intent);
		finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (requestCode == SettingsSimultaneousActivity.REQUEST_EDIT) {
			Intent intent = getIntent();
			intent.putExtra("address", phoneNumber.getText().toString());
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
