package org.linphone;

import org.linphone.LinphoneManager.AddressType;
import org.linphone.ui.AddressText;

import io.karim.MaterialRippleLayout;

import com.rey.material.widget.Button;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends Activity {

	private Button backButton;
	private MaterialRippleLayout container1;
	private MaterialRippleLayout container2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);

		backButton = (Button) findViewById(R.id.back_button);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		container1 = (MaterialRippleLayout) findViewById(R.id.container);
		container2 = (MaterialRippleLayout) findViewById(R.id.container1);
		
		container1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent call = new Intent(Intent.ACTION_DIAL);
//				call.setData(Uri.parse("tel:2222"));
//				startActivity(call);
				AddressType address = new AddressText(HelpActivity.this, null);
				address.setText("2222");
				LinphoneManager.getInstance().newOutgoingCall(address);
			}
		});
		
		container2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent email = new Intent(Intent.ACTION_SEND);
			    email.putExtra(Intent.EXTRA_EMAIL,new String[] { "info@mobinet.mn"});
			    email.putExtra(Intent.EXTRA_SUBJECT,"");
			    email.putExtra(Intent.EXTRA_TEXT,"");

			    startActivityForResult(Intent.createChooser(email, "Choose an Email client:"),
			                        1);
			}
		});

	}

}
