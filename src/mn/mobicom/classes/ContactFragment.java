/**
 * 
 */
package mn.mobicom.classes;


import me.drakeet.materialdialog.MaterialDialog;
import org.linphone.ContactsManager;
import org.linphone.LinphoneContact;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneManager.AddressType;
import org.linphone.LinphoneNumberOrAddress;
import org.linphone.LinphoneUtils;
import org.linphone.MessageWriteActivity;
import org.linphone.R;
import org.linphone.compatibility.Compatibility;
import org.linphone.ui.AddressText;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

/**
 * @author Showtime
 *
 */
public class ContactFragment extends Fragment {

	private ListView contactListView;
	private ContactDetailAdapter adapter;
	public static LinphoneContact contact;
	private ImageButton headerEditButton;
	private ImageButton headerRemoveButton;
	private ImageView headerImageView;
	private Button backButton;

	public static ContactFragment newInstance() {
		ContactFragment fragment = new ContactFragment();

		return fragment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.contact_fragment, container,
				false);
		headerEditButton = (ImageButton) view.findViewById(R.id.header_edit);

		headerEditButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = Compatibility.prepareEditContactIntent(Integer
						.parseInt(contact.getAndroidId()));
				startActivity(intent);
			}
		});

		headerRemoveButton = (ImageButton) view
				.findViewById(R.id.header_remove);
		headerRemoveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				removeDialog();
			}
		});

		backButton = (Button) view.findViewById(R.id.back_button);
		backButton.setText(contact.getFullName());
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		contactListView = (ListView) view.findViewById(R.id.contact_list_view);

		adapter = new ContactDetailAdapter(contact, getActivity());

		ViewGroup header = (ViewGroup) inflater.inflate(
				R.layout.contact_list_header, contactListView, false);
		contactListView.addHeaderView(header, null, false);

		headerImageView = (ImageView) header
				.findViewById(R.id.header_image_view);

		contactListView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (contact.hasPhoto()) {
			LinphoneUtils.setThumbnailPictureFromUri(getActivity(), headerImageView, contact.getPhotoUri());
		} else {
			headerImageView.setImageResource(R.drawable.unknown_small);
		}

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public void removeDialog() {

		final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
		mMaterialDialog.setTitle(getString(R.string.mnp_remove_dialog_title))
				.setMessage(getString(R.string.mnp_remove_dialog_message))
				.setPositiveButton(getString(R.string.mnp_ok), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteExistingContact();
						mMaterialDialog.dismiss();
						getActivity().onBackPressed();
					}
				}).setNegativeButton(getString(R.string.mnp_cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMaterialDialog.dismiss();
			}
		});

		mMaterialDialog.show();
	}

	private void deleteExistingContact() {
		ContactsManager.getInstance().delete(contact.getAndroidId());
	}

	public class ContactDetailAdapter extends BaseAdapter {

		/**
		 * 
		 */
		public LinphoneContact contact;
		public Context context;

		public ContactDetailAdapter(LinphoneContact contact, Context context) {
			// TODO Auto-generated constructor stub
			this.contact = contact;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contact.getNumbersOrAddresses().size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.contact_list_item,
						parent, false);
			}
			final LinphoneNumberOrAddress number = contact.getNumbersOrAddresses().get(position);
			Button call = (Button) convertView.findViewById(R.id.call_button);
			call.setText(number.getValue());
			call.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AddressType address = new AddressText(getActivity(), null);
					address.setDisplayedName(contact.getFullName());
					address.setText(number.getValue());
					LinphoneManager.getInstance().newOutgoingCall(address);
				}
			});

			ImageButton message = (ImageButton) convertView
					.findViewById(R.id.message_button);
			message.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					MessageWriteFragment.number = number.getValue();
					Intent intent = new Intent(getActivity(),
							MessageWriteActivity.class);
					Bundle b = new Bundle();
					b.putString("name", number.getValue());
					intent.putExtras(b);
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

}
