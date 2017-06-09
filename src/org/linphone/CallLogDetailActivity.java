/**
 * 
 */
package org.linphone;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import mn.mobicom.classes.ContactFragment;
import mn.mobicom.classes.LogAdapter;
import mn.mobicom.classes.MessageWriteFragment;
import mn.mobicom.classes.MessageFragment.MessageData;

import org.linphone.LinphoneManager.AddressType;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.ui.AddressText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobinet.model.VoipInboxModel.InboxMessageListItem;
import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Showtime
 *
 */
public class CallLogDetailActivity extends FragmentActivity implements
		OnClickListener {

	public static LinphoneCallLog log;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */

	private Button backButton;
	private ImageButton headerRemove;

	private RelativeLayout callRow;
	private RelativeLayout messageRow;
	private RelativeLayout addContactRow;
	private RelativeLayout viewContactRow;
	private RelativeLayout deleteLogRow;

	private TextView name;
	private TextView number;
	// private TextView messageRow;
	// private TextView addContactRow;
	// private TextView viewContactRow;
	// private TextView deleteLogRow;

	private android.widget.TextView logDay;
	private android.widget.TextView logTime;
	private android.widget.TextView callDirection;
	private android.widget.TextView callDuration;
	private LinphoneAddress tempAddress;
	private LinphoneContact contactName;

	private LinphoneCoreListenerBase mListener;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.calllog_detail_activity);

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		headerRemove = (ImageButton) findViewById(R.id.header_remove);
		headerRemove.setOnClickListener(this);

		logDay = (android.widget.TextView) findViewById(R.id.timeStamp);
		logTime = (android.widget.TextView) findViewById(R.id.time);
		callDirection = (android.widget.TextView) findViewById(R.id.call_direction);
		callDuration = (android.widget.TextView) findViewById(R.id.call_duration);

		long timestamp = log.getTimestamp();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar logT = Calendar.getInstance();
		logT.setTimeInMillis(timestamp);
		logDay.setText(timestampToHumanDate(logT));
		logTime.setText(sdf.format(logT.getTime()));

		if (log.getDirection() == CallDirection.Incoming) {
			tempAddress = log.getFrom();
			if (log.getStatus() == CallStatus.Missed) {
				callDirection.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.call_log_missed, 0, 0, 0);
				callDirection.setText(getString(R.string.mnp_missed_call));
			} else {
				callDirection.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.call_log_incoming, 0, 0, 0);
				callDirection.setText(getString(R.string.mnp_incoming_call));
			}
		} else {
			tempAddress = log.getTo();
			callDirection.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.call_log_outgoing, 0, 0, 0);
			callDirection.setText(getString(R.string.mnp_outgoing_call));
		}
		callDuration.setText(log.getCallDuration() + "s");

		callRow = (RelativeLayout) findViewById(R.id.call_back);
		callRow.setOnClickListener(this);

		messageRow = (RelativeLayout) findViewById(R.id.message);
		messageRow.setOnClickListener(this);

		addContactRow = (RelativeLayout) findViewById(R.id.add_contact);
		addContactRow.setOnClickListener(this);

		viewContactRow = (RelativeLayout) findViewById(R.id.view_contact);
		viewContactRow.setOnClickListener(this);

		deleteLogRow = (RelativeLayout) findViewById(R.id.delete);
		deleteLogRow.setOnClickListener(this);

		name = (TextView) findViewById(R.id.name);
		number = (TextView) findViewById(R.id.number);
		contactName = LogAdapter.getContactName(this, tempAddress.getUserName());
		if (contactName != null) {
			name.setText(getString(R.string.mnp_call)+" " + contactName.getFullName());
			viewContactRow.setVisibility(View.VISIBLE);
			addContactRow.setVisibility(View.GONE);
		} else {
			viewContactRow.setVisibility(View.GONE);
			addContactRow.setVisibility(View.VISIBLE);
		}
		number.setText(tempAddress.getUserName());

		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
				if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
					startActivity(new Intent(CallLogDetailActivity.this, CallOutgoingActivity.class));
				}
			}
		};

		// messageRow = (TextView) findViewById(R.id.ripple_message);
		// addContactRow = (TextView) findViewById(R.id.ripple_add_contact);
		// viewContactRow = (TextView) findViewById(R.id.ripple_view_contact);
		// deleteLogRow = (TextView) findViewById(R.id.ripple_delete);

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

	public void removeDialog() {

		LinphoneManager.getLc().removeCallLog(log);
		onBackPressed();
//		final MaterialDialog mMaterialDialog = new MaterialDialog(this);
//		mMaterialDialog.setTitle("REMOVE")
//				.setMessage("Are you sure you want remove?")
//				.setPositiveButton("OK", new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						LinphoneManager.getLc().removeCallLog(log);
//						mMaterialDialog.dismiss();
//						onBackPressed();
//					}
//				}).setNegativeButton("CANCEL", new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						mMaterialDialog.dismiss();
//					}
//				});
//
//		mMaterialDialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	private String timestampToHumanDate(Calendar cal) {
		SimpleDateFormat dateFormat;
		if (isToday(cal)) {
			return getString(R.string.mnp_today);
		} else if (isYesterday(cal)) {
			return getString(R.string.mnp_yesterday);
		} else {
			dateFormat = new SimpleDateFormat(getResources().getString(
					R.string.history_date_format));
		}

		return dateFormat.format(cal.getTime());
	}

	private boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}

		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
					.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR));
	}

	private boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	private boolean isYesterday(Calendar cal) {
		Calendar yesterday = Calendar.getInstance();
		yesterday.roll(Calendar.DAY_OF_MONTH, -1);
		return isSameDay(cal, yesterday);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		switch (id) {
		case R.id.call_back:

			AddressType address = new AddressText(this, null);
			if (contactName != null)
				address.setDisplayedName(contactName.getFullName());
			address.setText(tempAddress.getUserName());
			LinphoneManager.getInstance().newOutgoingCall(address);

			break;
		case R.id.message:
			
			MessageWriteFragment.number = tempAddress.getUserName();
			Intent intent1 = new Intent(this, MessageWriteActivity.class);
			Bundle b = new Bundle();
			if (contactName != null) {
				b.putString("name", contactName.getFullName());
			} else {
				b.putString("name", tempAddress.getUserName());
			}

			intent1.putExtras(b);
			startActivity(intent1);
			break;
		case R.id.view_contact:
			contactName.refresh();
			ContactFragment.contact = contactName;
			this.startActivity(new Intent(this, ContactActivity.class));
			break;
		case R.id.add_contact:
			Intent intent = Compatibility.prepareAddContactIntent(tempAddress.getUserName());
			startActivity(intent);
			break;
		case R.id.delete:
			removeDialog();
			break;
		case R.id.header_remove:
			removeDialog();
			break;

		default:
			break;
		}
	}

}
