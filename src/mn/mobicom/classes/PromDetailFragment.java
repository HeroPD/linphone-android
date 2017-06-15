package mn.mobicom.classes;

import org.linphone.LinphoneLauncherActivity;
import org.linphone.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PromDetailFragment extends Fragment {

	
	private String pic;
    private String title;
    private String text;
    
    
	public PromDetailFragment(String pic1 , String title1 , String text1){
    	
        pic = pic1;
        title = title1;
        text = text1;
    }
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.mobi_prom_detail, container, false);
    	ImageView imageView = (ImageView) view.findViewById(R.id.prom_d_image);
    	TextView titleTextView = (TextView) view.findViewById(R.id.prom_d_title);
        TextView textView = (TextView) view.findViewById(R.id.prom_d_text);
        titleTextView.setText(title);
        textView.setText(text);
        imageView.setImageBitmap(LinphoneLauncherActivity.imageCache.get("http://staticcss.mobicom.mn/large/"+pic));
        
        return view;
    }
}
