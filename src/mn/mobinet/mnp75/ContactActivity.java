/**
 * 
 */
package mn.mobinet.mnp75;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import mn.mobinet.mnp75.R;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;

/**
 * @author Showtime
 *
 */
public class ContactActivity extends FragmentActivity {
	
	private LinphoneCoreListenerBase mListener;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity);
		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
				if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
					startActivity(new Intent(ContactActivity.this, CallOutgoingActivity.class));
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

	public void editContact(int id)
	{
		Intent intent = Compatibility.prepareEditContactIntent(id);
		startActivity(intent);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	
	
	
	

}
