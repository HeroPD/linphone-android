/**
 * 
 */
package org.linphone;

import com.rey.material.widget.Button;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Showtime
 *
 */
public class AboutUsActivity extends Activity{
	
	private Button backButton;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);
		
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
