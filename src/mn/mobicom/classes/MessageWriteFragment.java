package mn.mobicom.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mn.mobicom.oauth2.OauthRequest;
import mn.mobicom.oauth2.OauthRequest.RequestType;

import mn.mobinet.mnp75.MessageWriteActivity;
import mn.mobinet.mnp75.R;
import org.linphone.mediastream.Log;
import org.linphone.ui.Progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobinet.model.BaseModel;
import com.mobinet.model.LinkedNumbers;
import com.mobinet.model.LinkedNumbers.NumberListItem;

@SuppressLint("SimpleDateFormat")
public class MessageWriteFragment extends Fragment {

	private static final String TAG = MessageWriteNewAdapter.class.getSimpleName();
	private ListView messageList;
	private Button send;
	private EditText toNumber;
	private EditText message;
	private TextView characterCounter;
	private LinearLayout numberContainer;
	private ProgressBar messageProgress;
	MessageWriteNewAdapter adapter;
	List<NumberListItem> data;
	public static String number;
	private LayoutInflater mInflater;
	private boolean waitRefresh = false;
	private Progress progress;

	public MessageWriteFragment() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<NumberListItem>();
		progress = new Progress(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.mobi_message_write_fragment,
				container, false);
		mInflater = inflater;
		messageList = (ListView) view.findViewById(R.id.mobi_message_list);
		send = (Button) view.findViewById(R.id.mobi_send);
		toNumber = (EditText) view.findViewById(R.id.mobi_to_number);
		message = (EditText) view.findViewById(R.id.mobi_text_message);
		characterCounter = (TextView) view.findViewById(R.id.character_counter);
		numberContainer = (LinearLayout) view
				.findViewById(R.id.mobi_to_number_container);
		messageProgress = (ProgressBar) view
				.findViewById(R.id.message_progress);
		order(data);
		adapter = new MessageWriteNewAdapter(data);
		messageList.setAdapter(adapter);

		message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				characterCounter.setText("" + s.length() + "/154");
			}
		});

		final MessageWriteActivity mact = (MessageWriteActivity) getActivity();
		mact.messageRefreshListener = new MessageWriteActivity.MessageRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (number != null) {
					if (!waitRefresh)
						retrieveData();
				}
			}
		};

		if (number == null) {
			numberContainer.setVisibility(View.VISIBLE);
		}

		retrieveData();

		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CheckState.checkState(getActivity(), new CheckState.StateCreateOrUse() {
					@Override
					public void action() {
						String localValue = null;
						String localNumber = null;
						String localSms = null;
						if (number == null) {
							localNumber = toNumber.getText().toString();

						} else {
							localNumber = number;
						}

						localSms = message.getText().toString();
						localValue = "{\"callednumbers\":\"" + localNumber
								+ "\",\"sms\":\"" + localSms + "\"}";

						final String infoNumber = localNumber;
						final String infoSms = localSms;
						OauthRequest request = new OauthRequest(UserControl.getSend(),
								RequestType.POST);
						request.setOauthListener(new OauthRequest.OauthListener() {

							@Override
							public void onResult(String result) {
								// TODO Auto-generated method stub
								Log.d("SENDING MESSAGE", result);
								Gson gson = new Gson();
								BaseModel model = gson
										.fromJson(result, BaseModel.class);

								if (model.result == 0) {

									if (number == null) {
										numberContainer.setVisibility(View.INVISIBLE);
										MessageWriteActivity act = (MessageWriteActivity) getActivity();
										act.getBackButton().setText(
												toNumber.getText().toString());
										number = toNumber.getText().toString();
									}

									if (number != null) {
										if (!waitRefresh)
											retrieveData();
									}
								}

							}

							@Override
							public void onBackgroundException(Exception e) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onResultCodeWrong(String code) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onErrorDialogOkClick() {
								// TODO Auto-generated method stub

							}
						}, localValue, getActivity());
						request.execute();
						message.setText("");
					}
				});
			}
		});
		message.clearFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(message.getWindowToken(), 0);

		registerForContextMenu(messageList);
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, getString(R.string.mnp_copy_text));
		menu.add(0, v.getId(), 1, getString(R.string.mnp_delete));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		NumberListItem smsData = data.get(info.position);

		switch (item.getOrder()) {
		case 0:
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(smsData.text);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("text label", smsData.text);
				clipboard.setPrimaryClip(clip);
			}
			break;
		case 1:

			progress.show();
			OauthRequest request = new OauthRequest(UserControl.removeSms(
					smsData.direction + smsData.id, number), RequestType.GET);
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
					Log.d("LOGRESULT", result);
					data.remove(info.position);
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
		waitRefresh = true;
		data.clear();
		messageProgress.setVisibility(View.VISIBLE);
		OauthRequest request = new OauthRequest(UserControl.getAllMessages(0,
				20, number), RequestType.GET);
		request.setOauthListener(new OauthRequest.OauthListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				Log.d(TAG, result);
				Gson gson = new Gson();
				LinkedNumbers model = gson
						.fromJson(result, LinkedNumbers.class);

				data.addAll(model.numberList);
				order(data);
				adapter.notifyDataSetChanged();
				messageProgress.setVisibility(View.GONE);
			}

			@Override
			public void onBackgroundException(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
				messageProgress.setVisibility(View.GONE);
			}

			@Override
			public void onResultCodeWrong(String code) {

				// TODO Auto-generated method stub
				messageProgress.setVisibility(View.GONE);
			}

			@Override
			public void onErrorDialogOkClick() {
				// TODO Auto-generated method stub

			}
		}, "", getActivity());
		request.execute();
		waitRefresh = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		number = null;
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
	}

	private void sort(List<NumberListItem> data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();

		boolean swapped = true;
		int j = 0;
		NumberListItem temp;

		while (swapped) {
			swapped = false;
			j++;
			for (int i = 0; i < data.size() - j; i++) {

				Date date1 = null;
				Date date2 = null;
				try {
					dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
					date1 = dateFormat.parse(data.get(i).date);
					date2 = dateFormat.parse(data.get(i + 1).date);

					if (date1.compareTo(date2) > 0) {
						temp = data.get(i);
						data.set(i, data.get(i + 1));
						data.set(i + 1, temp);
						swapped = true;
					}
				} catch (Exception e) {

				}
			}
		}

	}

	private List<NumberListItem> order(List<NumberListItem> data) {

		sort(data);

		SimpleDateFormat dateFormat = new SimpleDateFormat();
		String tempDate = null;
		for (int i = data.size() - 1; i >= 0; i--) {

			data.get(i).visible = false;
			Date date = null;
			try {
				dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
				date = dateFormat.parse(data.get(i).date);
				if (tempDate != null) {
					dateFormat.applyPattern("yyyy-MM-dd");
					String temp = dateFormat.format(date);
					if (!tempDate.equals(temp)) {

						if (0 < i && i < data.size()) {
							data.get(i + 1).visible = true;

						}
					}
					tempDate = temp;
				} else {
					dateFormat.applyPattern("yyyy-MM-dd");
					tempDate = dateFormat.format(date);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (data.size() > 0)
			data.get(0).visible = true;

		Log.d("" + data.size());
		return data;
	}

	private static class ViewHolder1 {
		public TextView messageTextView;
		public TextView timeTextView;
		public TextView topDate;

	}

	private static class ViewHolder2 {
		public ImageView messageStatus;
		public TextView messageTextView;
		public TextView timeTextView;
		public TextView topDate;

	}

	class MessageWriteNewAdapter extends ArrayAdapter<NumberListItem> {

		List<NumberListItem> dataSource;

		public MessageWriteNewAdapter(List<NumberListItem> dataSource) {
			super(getActivity(), 0, dataSource);
			this.dataSource = dataSource;
		}

		private String dateTemp = null;
		private int positionTemp;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.BaseAdapter#getItemViewType(int)
		 */
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			NumberListItem item = dataSource.get(position);
			return item.direction.equals("out") ? 1 : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			int viewType = this.getItemViewType(position);

			NumberListItem message = dataSource.get(position);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			try {
				date = dateFormat.parse(message.date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (viewType) {
			case 0: {
				ViewHolder1 holder1;
				View v = convertView;

				if (v == null || !(v.getTag() instanceof ViewHolder1)) {
					v = mInflater.inflate(R.layout.chat_user1_item, parent,
							false);
					holder1 = new ViewHolder1();

					holder1.messageTextView = (TextView) v
							.findViewById(R.id.message_text);
					holder1.timeTextView = (TextView) v
							.findViewById(R.id.time_text);
					holder1.topDate = (TextView) v.findViewById(R.id.top_date);
					v.setTag(holder1);
				} else {
					holder1 = (ViewHolder1) v.getTag();

				}

				holder1.messageTextView.setText(message.text);
				if (date != null) {
					dateFormat.applyPattern("HH:mm");
					holder1.timeTextView.setText(dateFormat.format(date));
				}
				if (message.visible)
					setTextViewDate(holder1.topDate, message);
				else
					holder1.topDate.setVisibility(View.GONE);
				return v;
			}

			case 1: {

				ViewHolder2 holder2;
				View view = convertView;
				if (view == null || !(view.getTag() instanceof ViewHolder2)) {
					view = mInflater.inflate(R.layout.chat_user2_item, parent,
							false);

					holder2 = new ViewHolder2();

					holder2.messageTextView = (TextView) view
							.findViewById(R.id.message_text);
					holder2.timeTextView = (TextView) view
							.findViewById(R.id.time_text);
					holder2.messageStatus = (ImageView) view
							.findViewById(R.id.user_reply_status);
					holder2.topDate = (TextView) view
							.findViewById(R.id.top_date);

					holder2.messageStatus = (ImageView) view
							.findViewById(R.id.user_reply_status);

					view.setTag(holder2);

				} else {
					holder2 = (ViewHolder2) view.getTag();

				}

				holder2.messageTextView.setText(message.text);
				// //holder2.messageTextView.setText(message.getMessageText());
				if (date != null) {
					dateFormat.applyPattern("HH:mm");
					holder2.timeTextView.setText(dateFormat.format(date));
				}
				if (message.visible)
					setTextViewDate(holder2.topDate, message);
				else
					holder2.topDate.setVisibility(View.GONE);

				if (message.errorMessage != null) {
					holder2.messageStatus.setImageResource(R.drawable.failed);
				} else {
					if (message.status == 3){
						holder2.messageStatus.setImageResource(R.drawable.ic_double_tick);
					}else{
						holder2.messageStatus.setImageResource(R.drawable.ic_single_tick);
					}
				}
				return view;
			}

			}
			return null;

		}

		public void setTextViewDate(TextView textView, NumberListItem item) {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			try {
				date = dateFormat.parse(item.date);
				dateFormat.applyPattern("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				textView.setText(LogAdapter.timestampToHumanDate(cal,
						getActivity()));
				textView.setVisibility(View.VISIBLE);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
