/**
 * 
 */
package org.linphone;

import org.linphone.mediastream.Log;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;

import com.rey.material.widget.Button;
import com.rey.material.widget.ImageButton;

/**
 * @author Showtime
 *
 */
public class MessageWriteActivity extends FragmentActivity {

	public interface MessageRefreshListener {

		void onRefresh();
	}

	public MessageRefreshListener messageRefreshListener;
	private Button backButton;
	private ImageButton headerRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_write_activity);

		Bundle b = getIntent().getExtras();
		String name = null;
		if (b != null)
			name = b.getString("name");
		else
			name = getResources().getString(R.string.mnp_write_new);
		Log.d("NAME", name);
		backButton = (Button) findViewById(R.id.back_button);
		headerRefresh = (ImageButton) findViewById(R.id.header_refresh);
		headerRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (messageRefreshListener != null)
					messageRefreshListener.onRefresh();
			}
		});
		backButton.setText(name);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

	}

	

	public Button getBackButton() {
		return backButton;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(backButton.getWindowToken(), 0);
		}
		super.onBackPressed();

	}

}
