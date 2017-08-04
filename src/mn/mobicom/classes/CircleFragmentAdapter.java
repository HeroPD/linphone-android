package mn.mobicom.classes;

import java.util.HashMap;
import java.util.List;

import org.linphone.mediastream.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobinet.model.PromData;

public class CircleFragmentAdapter extends FragmentPagerAdapter{
    private List<PromData.data> proms;
    private HashMap<Integer, Fragment> dataOfProm;
    private Context context;
    public CircleFragmentAdapter(FragmentManager fm ,Context context, List<PromData.data> promotionList) {
        super(fm);
        this.proms = promotionList;
        dataOfProm = new HashMap<Integer, Fragment>();
        this.context = context;
//        Log.d("PROM",""+promotionList.size() +" "+ proms.get(0).title_mn );
    }

    @Override
    public Fragment getItem(int position) {
    	if (dataOfProm.containsKey(position)){
    		return dataOfProm.get(position);
    	}
        SharedPreferences pref = this.context.getSharedPreferences(
                "mn.mobinet.mnp", 0);
        if (pref.getInt("language", 0) == 0) {
            dataOfProm.put(position, new TestFragment(proms.get(position).picture, proms.get(position).title_mn, proms.get(position).description_mn));
        } else {
            dataOfProm.put(position, new TestFragment(proms.get(position).picture, proms.get(position).title_en, proms.get(position).description_en));
        }

        return dataOfProm.get(position);
    }

    @Override
    public int getCount() {
        return proms.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return proms.get(position).title_mn;
    }

}
