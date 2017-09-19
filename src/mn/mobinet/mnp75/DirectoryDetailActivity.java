package mn.mobinet.mnp75;

import mn.mobicom.classes.ContactFragment;
import mn.mobicom.classes.LogAdapter;
import mn.mobicom.classes.MessageWriteFragment;

import mn.mobinet.mnp75.R;
import org.linphone.compatibility.Compatibility;
import org.linphone.ui.AddressText;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobinet.model.DirectoryModel;
import com.rey.material.widget.Button;

public class DirectoryDetailActivity extends Activity implements OnClickListener{
	
	public static DirectoryModel model;
	
	

	private Button backButton;
	private RelativeLayout callRow;
	private RelativeLayout messageRow;
	private RelativeLayout addContactRow;
	private RelativeLayout viewContactRow;

	private TextView name;
	private TextView number;
	
	private TextView firstname;
	private TextView lastname;
	private TextView email;
	
	private LinphoneContact contactName;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.directory_detail_activity);

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setText(model.firstName+" "+model.lastName);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		callRow = (RelativeLayout) findViewById(R.id.call_back);
		callRow.setOnClickListener(this);

		messageRow = (RelativeLayout) findViewById(R.id.message);
		messageRow.setOnClickListener(this);

		addContactRow = (RelativeLayout) findViewById(R.id.add_contact);
		addContactRow.setOnClickListener(this);

		viewContactRow = (RelativeLayout) findViewById(R.id.view_contact);
		viewContactRow.setOnClickListener(this);


		name = (TextView) findViewById(R.id.name);
		number = (TextView) findViewById(R.id.number);
		firstname = (TextView) findViewById(R.id.firstname);
		lastname = (TextView) findViewById(R.id.lastname);
		email = (TextView) findViewById(R.id.email);
		
		firstname.setText(model.firstName);
		lastname.setText(model.lastName);
		email.setText(model.email);
		
		contactName = LogAdapter
				.getContactName(this, model.extension);
		if (contactName != null) {
			name.setText(getString(R.string.mnp_call)+" " + contactName.getFullName());
			viewContactRow.setVisibility(View.VISIBLE);
			addContactRow.setVisibility(View.GONE);
		} else {
			viewContactRow.setVisibility(View.GONE);
			addContactRow.setVisibility(View.VISIBLE);
		}
		number.setText(model.extension);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		switch (id) {
		case R.id.call_back:

			LinphoneManager.AddressType address = new AddressText(this, null);
			if (contactName != null)
				address.setDisplayedName(contactName.getFullName());
			address.setText(model.extension);
			LinphoneManager.getInstance().newOutgoingCall(address);

			break;
		case R.id.message:
			
			MessageWriteFragment.number = model.extension;
			Intent intent1 = new Intent(this, MessageWriteActivity.class);
			Bundle b = new Bundle();
			if (contactName != null) {
				b.putString("name", contactName.getFullName());
			} else {
				b.putString("name", model.extension);
			}

			intent1.putExtras(b);
			startActivity(intent1);
			break;
		case R.id.view_contact:
			ContactFragment.contact = contactName;
			this.startActivity(new Intent(this, ContactActivity.class));
			break;
		case R.id.add_contact:
			Intent intent = Compatibility.prepareAddContactIntent(model.extension);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
