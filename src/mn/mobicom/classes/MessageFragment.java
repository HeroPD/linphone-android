package mn.mobicom.classes;

import io.karim.MaterialRippleLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import org.linphone.BaseFragment;
import org.linphone.LinphoneContact;
import org.linphone.MenuActivity;
import org.linphone.MessageWriteActivity;
import org.linphone.R;
import org.linphone.ui.Progress;
import org.linphone.ui.SwipyRefreshLayout;
import org.linphone.ui.SwipyRefreshLayout.OnRefreshListener;
import org.linphone.ui.SwipyRefreshLayoutDirection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobinet.model.LinkedNumbers;
import com.mobinet.model.LinkedNumbers.NumberListItem;
import com.mobinet.model.VoipInboxModel;
import com.mobinet.model.VoipInboxModel.InboxMessageListItem;
import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

public class MessageFragment extends BaseFragment {

	private static final String TAG = MessageFragment.class.getSimpleName();
	private SwipyRefreshLayout swipyRefreshLayout;
	private ListView messageListView;
	private MessageListAdapter adapter;
	private ImageButton writeNew;
	private Button chat;
	private ImageButton menuButton;
	private List<NumberListItem> messageList;
	private List<MessageData> data;
	private LayoutInflater mInflater;
	private Progress progress;
	private TextView noMessage;
	private ProgressBar progressBar;

	public class MessageData {

		public String number;
		public List<InboxMessageListItem> messages;

		public void sort() {

		}

	}

	public MessageFragment() {
		// TODO Auto-generated constructor stub
		// make request

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageList = new ArrayList<NumberListItem>();
		data = new ArrayList<MessageData>();
		progress = new Progress(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.mobi_message_fragment, container,
				false);
		noMessage = (TextView) view.findViewById(R.id.noMessage);
		progressBar = (ProgressBar) view.findViewById(R.id.message_progress);
		writeNew = (ImageButton) view.findViewById(R.id.new_message);
		writeNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MessageWriteActivity.class);
				startActivity(intent);
			}
		});
		chat = (Button) view.findViewById(R.id.chat);
		chat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

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

		messageListView = (ListView) view.findViewById(R.id.message_list);

		swipyRefreshLayout = (SwipyRefreshLayout) view
				.findViewById(R.id.swipyrefreshlayout);
		swipyRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction) {
				// TODO Auto-generated method stub
				retrieveData();
			}
		});

		adapter = new MessageListAdapter();
		messageListView.setAdapter(adapter);
		registerForContextMenu(messageListView);
		retrieveData();

		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, getString(R.string.mnp_copy_number));
		menu.add(0, v.getId(), 1, getString(R.string.mnp_delete));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		NumberListItem linkedNumber = messageList.get(info.position);
		switch (item.getOrder()) {
		case 0:
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(linkedNumber.number);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("text label", linkedNumber.number);
				clipboard.setPrimaryClip(clip);
			}
			break;
		case 1:

			progress.show();
			OauthRequest request = new OauthRequest(
					UserControl.removeLinkedNumber(linkedNumber.number),
					RequestType.GET);
			request.setOauthListener(new OauthRequest.OauthListener() {

				@Override
				public void onResultCodeWrong(String code) {
					// TODO Auto-generated method stub
					progress.dismiss();
				}

				@Override
				public void onResult(String result) {
					// TODO Auto-generated method stub
					progress.dismiss();
					messageList.remove(info.position);
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							adapter.notifyDataSetChanged();
						}
					});
				}

				@Override
				public void onErrorDialogOkClick() {
					// TODO Auto-generated method stub
					progress.dismiss();

				}

				@Override
				public void onBackgroundException(Exception e) {
					// TODO Auto-generated method stub
					progress.dismiss();

				}
			}, "", getActivity());
			request.execute();
			break;

		default:
			break;
		}

		return true;
	}

	private void retrieveData() {
		data.clear();
		messageList.clear();
		swipyRefreshLayout.setRefreshing(true);
		OauthRequest request = new OauthRequest(UserControl.getLinkedNumbers(0,
				50), RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d(TAG, result);
				Gson gson = new Gson();
				LinkedNumbers model = gson
						.fromJson(result, LinkedNumbers.class);
				if (model.numberList != null)
					for (NumberListItem item : model.numberList) {
						messageList.add(item);
					}
				refreshData();
				swipyRefreshLayout.setRefreshing(false);

			}

			@Override
			public void onBackgroundException(Exception e) {
				// TODO Auto-generated method stub
				if (getActivity() != null){
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							swipyRefreshLayout.setRefreshing(false);
                            refreshData();
						}
					});
				}
				e.printStackTrace();
			}

			@Override
			public void onResultCodeWrong(String code) {
				getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        swipyRefreshLayout.setRefreshing(false);
                        refreshData();
					}
				});
				// TODO Auto-generated method stub

			}

			@Override
			public void onErrorDialogOkClick() {
				// TODO Auto-generated method stub

			}
		}, "", getActivity());
		request.execute();
	}

	private void refreshData() {
		// Log.d(LinphoneActivity.TAG,""+messageList.size());
		swipyRefreshLayout.setRefreshing(false);
		// for (InboxMessageListItem inboxMessageListItem : messageList) {
		// // Log.d(LinphoneActivity.TAG,""+inboxMessageListItem.callednumber);
		// addItemToData(inboxMessageListItem);
		// }
		//
		// // for (MessageData messageData : data) {
		// // Log.d(LinphoneActivity.TAG,messageData.number);
		// // }
		// // Log.d(LinphoneActivity.TAG,""+data.size());
		//
		// SharedPreferences pref = getActivity().getSharedPreferences(
		// "MessagePref", 0); // 0 - for private mode
		// Editor editor = pref.edit();
		// GsonBuilder builder = new GsonBuilder();
		// Gson gson = builder.create();
		// String json = gson.toJson(data);
		// Log.d("TEST PREF MESSAGE CACHE", json);
		// editor.putString("cache_message", json);
		//
		// editor.commit();
		if (messageList.size() > 0){
			noMessage.setVisibility(View.GONE);
		}else{
			noMessage.setVisibility(View.VISIBLE);
		}
		progressBar.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}

	private void addItemToData(InboxMessageListItem item) {
		Log.d(TAG, "Adding to data");
		if (isNumberInData(item.callednumber)) {
			Log.d(TAG, "Adding to existing");
			for (MessageData messageData : data) {
				if (messageData.number.equals(item.callednumber)) {
					if (messageData.messages != null) {
						messageData.messages.add(item);
					} else {
						messageData.messages = new ArrayList<InboxMessageListItem>();
						messageData.messages.add(item);
					}
				}
			}

		} else {
			Log.d(TAG, "Adding to new");
			MessageData smsData = new MessageData();
			smsData.number = item.callednumber;
			smsData.messages = new ArrayList<InboxMessageListItem>();
			smsData.messages.add(item);
			data.add(smsData);
		}

	}

	private boolean isNumberInData(String number) {

		for (MessageData messageData : data) {
			if (messageData.number.equals(number)) {
				return true;
			}
		}
		return false;

	}

	static class ViewHolder {
		public TextView title;
		public TextView subTitle;
		public TextView date;
		public MaterialRippleLayout back;
		public TextView newMessageMark;
	}

	class MessageListAdapter extends BaseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return messageList.size();
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

			final ViewHolder holder;
			final NumberListItem smsData = messageList.get(position);

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.mobi_message_item,
						parent, false);

				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.subTitle = (TextView) convertView
						.findViewById(R.id.subtitle);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.back = (MaterialRippleLayout) convertView
						.findViewById(R.id.message_container);
				holder.newMessageMark = (TextView) convertView
						.findViewById(R.id.new_message_mark);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			LinphoneContact contact = LogAdapter.getContactName(getActivity(),
					smsData.number);

			String intentValue = null;
			if (contact != null) {
				holder.title.setText(contact.getFullName());
				intentValue = contact.getFullName();
			} else {
				holder.title.setText(smsData.number);
				intentValue = smsData.number;
			}

			if (smsData.totalNewMessage != 0) {
				holder.newMessageMark.setVisibility(View.VISIBLE);
				holder.newMessageMark.setText("" + smsData.totalNewMessage);
			} else {
				holder.newMessageMark.setVisibility(View.GONE);
			}

			final String intentValueFinal = intentValue;
			holder.subTitle.setText(smsData.text);
			Date date = null;
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();

			try {
				date = format.parse(smsData.date);
				cal.setTimeInMillis(date.getTime());
				holder.date.setText(LogAdapter.timestampToHumanDate(cal,
						getActivity()));

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			holder.back.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("ITEM CLICK", "" + intentValueFinal);
					smsData.totalNewMessage = 0;
					MessageWriteFragment.number = smsData.number;
					Intent intent = new Intent(getActivity(),
							MessageWriteActivity.class);
					Bundle b = new Bundle();
					b.putString("name", intentValueFinal);
					intent.putExtras(b);
					startActivity(intent);
				}
			});

			return convertView;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences pref = getActivity().getSharedPreferences(
				"MessagePref", 0); // 0 - for private mode
		String list = pref.getString("send_messages", null);

		Editor editor = pref.edit();
		if (list != null) {
			Log.d("TEST PREF", list);
			Gson gson = new Gson();
			VoipInboxModel model = gson.fromJson(list, VoipInboxModel.class);
			for (InboxMessageListItem item : model.message) {
				for (MessageData messageData : data) {
					if (messageData.number.equals(item.callednumber)) {
						messageData.messages.add(item);
					}
				}
			}

		}
		editor.clear();
		editor.commit();
	}

}
