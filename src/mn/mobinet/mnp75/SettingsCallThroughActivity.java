package mn.mobinet.mnp75;

import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import mn.mobinet.mnp75.R;

public class SettingsCallThroughActivity extends Activity{

	private Button backButton;
	private Button saveButton;
	private Switch activeSwitch;
	private EditText forwardNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.settings_call_through_activity);
		
		

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
				SharedPreferences pref = getApplicationContext().getSharedPreferences(LinphoneLauncherActivity.sipNumber, 0); // 0 - for private mode
				Editor editor = pref.edit();
				editor.putBoolean("callthrough", activeSwitch.isChecked());
				editor.putString("number", forwardNumber.getText().toString());
				editor.commit();
				onBackPressed();
			}
		});
		activeSwitch = (Switch) findViewById(R.id.switch_active);
		forwardNumber = (EditText) findViewById(R.id.phone_number);
		SharedPreferences pref = getApplicationContext().getSharedPreferences(LinphoneLauncherActivity.sipNumber, 0);
		if (pref.getBoolean("callthrough", false)){
			activeSwitch.setChecked(true);
			forwardNumber.setText(pref.getString("number", ""));
		}else{
			activeSwitch.setChecked(false);
		}
	}
}
