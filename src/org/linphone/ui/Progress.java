/**
 * 
 */
package org.linphone.ui;

import org.linphone.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

/**
 * @author Showtime
 *
 */
public class Progress extends Dialog{

	/**
	 * @param context
	 */
	public Progress(Context context) {
		super(context , R.style.Theme_Dialog );
		// TODO Auto-generated constructor stub
		
	}
	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		
	}

}
