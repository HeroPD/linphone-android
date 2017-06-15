package mn.mobicom.classes;

import java.util.HashMap;
import java.util.List;

import org.linphone.mediastream.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobinet.model.PromData;

public class CircleFragmentAdapter extends FragmentPagerAdapter{
    private List<PromData.data> proms;
    private HashMap<Integer, Fragment> dataOfProm;
    public CircleFragmentAdapter(FragmentManager fm , List<PromData.data> promotionList) {
        super(fm);
        this.proms = promotionList;
        dataOfProm = new HashMap<Integer, Fragment>();
        Log.d("PROM",""+promotionList.size() +" "+ proms.get(0).Title +" "+proms.get(1).Title );
    }

    @Override
    public Fragment getItem(int position) {
    	if (dataOfProm.containsKey(position)){
    		return dataOfProm.get(position);
    	}
    	dataOfProm.put(position, new TestFragment(proms.get(position).Picture,proms.get(position).Title,proms.get(position).Text));
        return dataOfProm.get(position);
    }

    @Override
    public int getCount() {
        return proms.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return proms.get(position).Title;
    }

}
