package org.linphone;

import org.linphone.LinphoneManager.AddressType;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
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
	private LinphoneCoreListenerBase mListener;

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
				address.setText("18002222");
				LinphoneManager.getInstance().newOutgoingCall(address);
			}
		});
		
		container2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType("message/rfc822");
			    email.putExtra(Intent.EXTRA_EMAIL,new String[] { "customercare@mobinet.mn"});
			    email.putExtra(Intent.EXTRA_SUBJECT,"");
			    email.putExtra(Intent.EXTRA_TEXT, "");
			    startActivityForResult(Intent.createChooser(email, "Choose an Email client:"),
						1);
			}
		});

		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
				if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
					startActivity(new Intent(HelpActivity.this, CallOutgoingActivity.class));
				}
			}
		};

	}

	@Override
	protected void onResume() {
		super.onResume();
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}
	}

}
