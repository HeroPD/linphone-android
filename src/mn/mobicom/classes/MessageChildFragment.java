package mn.mobicom.classes;

import java.util.List;

import mn.mobinet.mnp75.BaseFragment;
import mn.mobinet.mnp75.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobinet.model.VoipInboxModel;
import com.mobinet.model.VoipInboxModel.InboxMessageListItem;

public class MessageChildFragment extends BaseFragment {

	public VoipInboxModel result;
	public ListView messageListView;
	public MessageChildAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.mobi_message_child_fragment,
				container, false);
		messageListView = (ListView) view.findViewById(R.id.mobi_message_list);
		if (result != null) {
			adapter = new MessageChildAdapter(result.message);
			messageListView.setAdapter(adapter);
			messageListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub

						}
					});
		}
		return view;
	}

	class MessageChildAdapter extends ArrayAdapter<InboxMessageListItem> {

		List<InboxMessageListItem> dataSource;

		public MessageChildAdapter(List<InboxMessageListItem> dataSource) {
			super(getActivity(), R.layout.mobi_message_inbox_list_item,
					dataSource);
			this.dataSource = dataSource;
		}

		class ViewHolder {
			public ImageView icon;
			public TextView title;
			public TextView subTitle;
			public TextView date;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflator = getActivity().getLayoutInflater();
				convertView = inflator.inflate(
						R.layout.mobi_message_inbox_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) convertView
						.findViewById(R.id.inbox_icon);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.inbox_title);
				viewHolder.subTitle = (TextView) convertView
						.findViewById(R.id.inbox_desc);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.inbox_date);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			InboxMessageListItem tempData = (InboxMessageListItem) dataSource
					.get(position);
			viewHolder.title.setText(tempData.callednumber);
			viewHolder.icon.setImageResource(R.drawable.other_about);
			viewHolder.subTitle.setText(tempData.sms);
			viewHolder.date.setText(tempData.date);
			return convertView;
		}
	}

}
