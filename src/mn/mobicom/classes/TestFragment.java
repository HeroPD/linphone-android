package mn.mobicom.classes;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mn.mobinet.mnp75.LinphoneLauncherActivity;

import mn.mobinet.mnp75.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public final class TestFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private String pic;
    private String title;
    private String text;
    
    public TestFragment(String pic1 , String title1 , String text1){
    	
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
        
    	View view = inflater.inflate(R.layout.mobi_promotion, container, false);
    	ImageView imageView = (ImageView) view.findViewById(R.id.prom_image);
    	TextView titleTextView = (TextView) view.findViewById(R.id.prom_title);
    	TextView textView = (TextView) view.findViewById(R.id.prom_d_text);
        String imageDomain = "https://z-shop.mobicom.mn/";
    	if (LinphoneLauncherActivity.imageCache.get(imageDomain+pic) == null){
    		new ImageLoadTask(imageDomain+pic, imageView).execute();
    	}else{
    		imageView.setImageBitmap(LinphoneLauncherActivity.imageCache.get(imageDomain+pic));
    	}
        titleTextView.setText(title);
        textView.setText(text);
        return view;
    }
    
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                LinphoneLauncherActivity.imageCache.put(this.url, myBitmap);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
