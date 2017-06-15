package mn.mobicom.classes;

/*
 HistoryFragment.java
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
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.linphone.BaseFragment;
import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

/**
 * @author Sylvain Berfini
 */
public class HistorySimpleFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener {
	private ListView historyList;
	private LayoutInflater mInflater;
	private TextView edit, ok, deleteAll, noCallHistory, noMissedCallHistory;
	private boolean onlyDisplayMissedCalls, isEditMode;
	private List<LinphoneCallLog> mLogs;
	private ImageButton editButton;
	private ImageButton removeAllButton;
	private Button doneButton;
	private Button toggleMissedButton;
	private LinearLayout bottomControl1;
	private LinearLayout bottomControl2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.history_simple, container, false);

		editButton = (ImageButton) view.findViewById(R.id.edit_button);
		editButton.setOnClickListener(this);
		removeAllButton = (ImageButton) view.findViewById(R.id.remove_button);
		removeAllButton.setOnClickListener(this);
		doneButton = (Button) view.findViewById(R.id.done_button);
		doneButton.setOnClickListener(this);
		toggleMissedButton = (Button) view.findViewById(R.id.toggle_missed);
		toggleMissedButton.setOnClickListener(this);
		noCallHistory = (TextView) view.findViewById(R.id.noCallHistory);
		noMissedCallHistory = (TextView) view
				.findViewById(R.id.noMissedCallHistory);

		historyList = (ListView) view.findViewById(R.id.historyList);
		historyList.setOnItemClickListener(this);
		registerForContextMenu(historyList);

		deleteAll = (TextView) view.findViewById(R.id.deleteAll);
		deleteAll.setOnClickListener(this);
		deleteAll.setVisibility(View.INVISIBLE);

		onlyDisplayMissedCalls = false;

		edit = (TextView) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);

		ok = (TextView) view.findViewById(R.id.ok);
		ok.setOnClickListener(this);

		bottomControl1 = (LinearLayout) view.findViewById(R.id.bottom_control1);
		bottomControl2 = (LinearLayout) view.findViewById(R.id.bottom_control2);

		return view;
	}

	private void removeNotMissedCallsFromLogs() {
		if (onlyDisplayMissedCalls) {
			List<LinphoneCallLog> missedCalls = new ArrayList<LinphoneCallLog>();
			for (LinphoneCallLog log : mLogs) {
				if (log.getStatus() == CallStatus.Missed) {
					missedCalls.add(log);
				}
			}
			mLogs = missedCalls;
		}
	}

	private boolean hideHistoryListAndDisplayMessageIfEmpty() {
		removeNotMissedCallsFromLogs();
		if (mLogs.isEmpty()) {
			if (onlyDisplayMissedCalls) {
				noMissedCallHistory.setVisibility(View.VISIBLE);
			} else {
				noCallHistory.setVisibility(View.VISIBLE);
			}
			historyList.setVisibility(View.GONE);
			return true;
		} else {
			noCallHistory.setVisibility(View.GONE);
			noMissedCallHistory.setVisibility(View.GONE);
			historyList.setVisibility(View.VISIBLE);
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
//
//		if (LinphoneActivity.isInstanciated()) {
//			LinphoneActivity.instance().selectMenu(FragmentsAvailable.HISTORY);
//
//			if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer)) {
//				LinphoneActivity.instance().hideStatusBar();
//			}
//		}
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!hideHistoryListAndDisplayMessageIfEmpty()) {
							historyList.setAdapter(new LogAdapter(
									getActivity(),mLogs , mInflater));
						}
					}
				});

			}
		});
		t.start();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, getString(R.string.delete));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		LinphoneCallLog log = mLogs.get(info.position);
		LinphoneManager.getLc().removeCallLog(log);
		mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
		if (!hideHistoryListAndDisplayMessageIfEmpty()) {
			if (historyList.getAdapter() != null) {
				LogAdapter adapter = (LogAdapter) historyList
						.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		
		LogAdapter adapter = (LogAdapter) historyList
				.getAdapter();
		
		int id = v.getId();
		if (id == R.id.toggle_missed) {
			int value = -1;
			if (v.getTag() instanceof String)
				value = Integer.parseInt((String) v.getTag());
			if (v.getTag() instanceof Integer)
				value = Integer.parseInt((String) v.getTag());

			Button tempButton = (Button) v;
			if (value == 0) {

				onlyDisplayMissedCalls = true;
				v.setTag("1");
				tempButton.setText(getString(R.string.mnp_all));

			} else {
				v.setTag("0");
				tempButton.setText(getString(R.string.mnp_missed));
				onlyDisplayMissedCalls = false;

				mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());

			}

		} else if (id == R.id.done_button) {
			hideDeleteAllButton();
			isEditMode = false;
			adapter.isEditMode = false;
		} else if (id == R.id.remove_button) {
			LinphoneManager.getLc().clearCallLogs();
			mLogs = new ArrayList<LinphoneCallLog>();
		} else if (id == R.id.edit_button) {
			showDeleteAllButton();
			isEditMode = true;
			adapter.isEditMode = true;
		}

		if (!hideHistoryListAndDisplayMessageIfEmpty()) {
			if (adapter != null) {
//				adapter.mLogs = mLogs;
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		if (isEditMode) {
			LinphoneCallLog log = mLogs.get(position);
			LinphoneManager.getLc().removeCallLog(log);
			mLogs.remove(position);
//			mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
			if (!hideHistoryListAndDisplayMessageIfEmpty()) {
				if (historyList.getAdapter() != null) {
					LogAdapter adapter1 = (LogAdapter) historyList
							.getAdapter();
					adapter1.notifyDataSetChanged();
				}
			}
		} else {
			if (LinphoneActivity.isInstanciated()) {
				LinphoneCallLog log = mLogs.get(position);
				LinphoneAddress address;
				if (log.getDirection() == CallDirection.Incoming) {
					address = log.getFrom();
				} else {
					address = log.getTo();
				}
				LinphoneActivity.instance().setAddresGoToDialerAndCall(
						address.asStringUriOnly(), address.getDisplayName(),
						null);
			}
		}
	}

	private void hideDeleteAllButton() {
		bottomControl1.setVisibility(View.VISIBLE);
		bottomControl2.setVisibility(View.GONE);
	}

	private void showDeleteAllButton() {
		bottomControl1.setVisibility(View.GONE);
		bottomControl2.setVisibility(View.VISIBLE);
	}
}