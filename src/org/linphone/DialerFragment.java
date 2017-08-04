package org.linphone;
/*
DialerFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import org.linphone.compatibility.Compatibility;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;
import org.linphone.ui.AddressAware;
import org.linphone.ui.AddressText;
import org.linphone.ui.CallButton;
import org.linphone.ui.EraseButton;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rey.material.widget.ImageButton;

/**
 * @author Sylvain Berfini
 */
public class DialerFragment extends Fragment {
	private static DialerFragment instance;
	private static boolean isCallTransferOngoing = false;

	private AddressAware numpad;
	private AddressText mAddress;
	private CallButton mCall;
	private OnClickListener addContactListener, cancelListener, transferListener;
	private boolean shouldEmptyAddressField = true;
	private ImageButton addContact;
	private ImageButton menuButton;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialer, container, false);

		addContact = (ImageButton) view.findViewById(R.id.add_contact_button);
		addContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinphoneActivity.instance().addContact(mAddress.getText().toString());
			}
		});

		menuButton = (ImageButton) view.findViewById(R.id.menu_button);
		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), MenuActivity.class));
			}
		});

		mAddress = (AddressText) view.findViewById(R.id.address);
		mAddress.setDialerFragment(this);

		EraseButton erase = (EraseButton) view.findViewById(R.id.erase);
		erase.setAddressWidget(mAddress);

		mCall = (CallButton) view.findViewById(R.id.Call);
		mCall.setAddressWidget(mAddress);
		if (LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
			if (isCallTransferOngoing) {
				mCall.setImageResource(R.drawable.transfer_call);
			} else {
				mCall.setImageResource(R.drawable.add_call);
			}
		} else {
			if (LinphoneManager.getLc().getVideoAutoInitiatePolicy()) {
				mCall.setImageResource(R.drawable.call_video_start);
			} else {
				mCall.setImageResource(R.drawable.call_audio_start);
			}
		}

		numpad = (AddressAware) view.findViewById(R.id.numpad);
		if (numpad != null) {
			numpad.setAddressWidget(mAddress);
		}

//		addContact.setEnabled(!(LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0));

		addContactListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinphoneActivity.instance().addContact(mAddress.getText().toString());
			}
		};
		cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};
		transferListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinphoneCore lc = LinphoneManager.getLc();
				if (lc.getCurrentCall() == null) {
					return;
				}
				lc.transferCall(lc.getCurrentCall(), mAddress.getText().toString());
				isCallTransferOngoing = false;
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};

		resetLayout(isCallTransferOngoing);

		if (getArguments() != null) {
			shouldEmptyAddressField = false;
			String number = getArguments().getString("SipUri");
			String displayName = getArguments().getString("DisplayName");
			String photo = getArguments().getString("PhotoUri");
			mAddress.setText(number);
			if (displayName != null) {
				mAddress.setDisplayedName(displayName);
			}
			if (photo != null) {
				mAddress.setPictureUri(Uri.parse(photo));
			}
		}

		instance = this;

		return view;
    }

	/**
	 * @return null if not ready yet
	 */
	public static DialerFragment instance() {
		return instance;
	}

	@Override
	public void onPause() {
		instance = null;
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		instance = this;

		if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().selectMenu(FragmentsAvailable.DIALER);
			LinphoneActivity.instance().updateDialerFragment(this);
			LinphoneActivity.instance().showStatusBar();
			LinphoneActivity.instance().hideTabBar(false);
		}

		boolean isOrientationLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		if(isOrientationLandscape && !getResources().getBoolean(R.bool.isTablet)) {
			((LinearLayout) numpad).setVisibility(View.GONE);
		} else {
			((LinearLayout) numpad).setVisibility(View.VISIBLE);
		}

		if (shouldEmptyAddressField) {
			mAddress.setText("");
		} else {
			shouldEmptyAddressField = true;
		}
		resetLayout(isCallTransferOngoing);
	}

	public void resetLayout(boolean callTransfer) {
		if (!LinphoneActivity.isInstanciated()) {
			return;
		}
		isCallTransferOngoing = LinphoneActivity.instance().isCallTransfer();
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc == null) {
			return;
		}

		if (lc.getCallsNb() > 0) {
			if (isCallTransferOngoing) {
				mCall.setImageResource(R.drawable.transfer_call);
				mCall.setExternalClickListener(transferListener);
			} else {
				mCall.setImageResource(R.drawable.add_call);
				mCall.resetClickListener();
			}
			addContact.setEnabled(true);
			addContact.setImageResource(R.drawable.cancel);
			addContact.setOnClickListener(cancelListener);
		} else {
			if (LinphoneManager.getLc().getVideoAutoInitiatePolicy()) {
				mCall.setImageResource(R.drawable.call_video_start);
			} else {
				mCall.setImageResource(R.drawable.bot_call_selector);
			}
//			addContact.setEnabled(false);
			addContact.setImageResource(R.drawable.bot_add_contact_selector);
			addContact.setOnClickListener(addContactListener);
			enableDisableAddContact();
		}
	}

	public void enableDisableAddContact() {
//		addContact.setEnabled(LinphoneManager.getLc().getCallsNb() > 0 || !mAddress.getText().toString().equals(""));
	}

	public void displayTextInAddressBar(String numberOrSipAddress) {
		shouldEmptyAddressField = false;
		mAddress.setText(numberOrSipAddress);
	}

	public void newOutgoingCall(String numberOrSipAddress) {
		displayTextInAddressBar(numberOrSipAddress);
		LinphoneManager.getInstance().newOutgoingCall(mAddress);
	}

	public void newOutgoingCall(Intent intent) {
		if (intent != null && intent.getData() != null) {
			String scheme = intent.getData().getScheme();
			if (scheme.startsWith("imto")) {
				mAddress.setText("sip:" + intent.getData().getLastPathSegment());
			} else if (scheme.startsWith("call") || scheme.startsWith("sip")) {
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			} else {
				Uri contactUri = intent.getData();
				String address = ContactsManager.getAddressOrNumberForAndroidContact(LinphoneService.instance().getContentResolver(), contactUri);
				if(address != null) {
					mAddress.setText(address);
				} else {
					Log.e("Unknown scheme: ", scheme);
					mAddress.setText(intent.getData().getSchemeSpecificPart());
				}
			}

			mAddress.clearDisplayedName();
			intent.setData(null);

			LinphoneManager.getInstance().newOutgoingCall(mAddress);
		}
	}
}
