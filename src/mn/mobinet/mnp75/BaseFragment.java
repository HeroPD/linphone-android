package mn.mobinet.mnp75;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

public class BaseFragment extends Fragment {
	public LinphoneActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = (LinphoneActivity) this.getActivity();
	}

	//
	// public void setTitle(int imageId, String titleText) {
	// View view = getActionView();
	// if (view != null) {
	// ImageView image = (ImageView) view.findViewById(R.id.image_title);
	// TextView text = (TextView) view.findViewById(R.id.text_title);
	// image.setImageResource(imageId);
	// text.setText(titleText);
	// }
	//
	// GlobalTools.applyFonts(view, GlobalTools.ttfAgFuturaMon);
	// }
	//
	// public View getActionView() {
	// return mActivity.geta
	// return null;
	// }

	public boolean onBackPressed() {
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	public void myOnKeyDown(int key_code , KeyEvent event) {
		// do whatever you want here
		
		Log.d("KEYCODE", ""+key_code);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
