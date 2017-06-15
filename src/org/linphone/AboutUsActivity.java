/**
 * 
 */
package org.linphone;

import com.rey.material.widget.Button;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * @author Showtime
 *
 */
public class AboutUsActivity extends Activity{
	
	private Button backButton;
	private TextView mobicom;
	private TextView mobinet;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);

		mobicom = (TextView) findViewById(R.id.mobicom);
		mobicom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mobicom.mn"));
				startActivity(browserIntent);
			}
		});
		mobinet = (TextView) findViewById(R.id.mobinet);
		mobinet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mobinet.mn"));
				startActivity(browserIntent);
			}
		});

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}

}
