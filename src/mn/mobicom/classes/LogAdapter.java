/**
 * 
 */
package mn.mobicom.classes;

import io.karim.MaterialRippleLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import mn.mobinet.mnp75.CallLogDetailActivity;
import mn.mobinet.mnp75.LinphoneContact;
import mn.mobinet.mnp75.LinphoneManager;
import mn.mobinet.mnp75.LinphoneManager.AddressType;
import mn.mobinet.mnp75.R;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.ui.AddressText;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rey.material.widget.ImageButton;

/**
 * @author Showtime
 *
 */
public class LogAdapter extends BaseAdapter{

	private Context context;
	public List<LinphoneCallLog> mLogs;
	public boolean isEditMode;
	private LayoutInflater mInflater;
	
	
	public LogAdapter(Context aContext , List<LinphoneCallLog> logs , LayoutInflater inflater) {
		// TODO Auto-generated constructor stub
		this.context = aContext;
		this.mLogs = logs;
		this.mInflater = inflater;
	}
	
	
	static public LinphoneContact getContactName(Context context, String number) {

	    String name = null;
	    String id = null;

	    // define the columns I want the query to return
	    String[] projection = new String[] {
	            ContactsContract.PhoneLookup.DISPLAY_NAME,
	            ContactsContract.PhoneLookup._ID};

	    // encode the phone number and build the filter URI
	    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

	    // query time
	    Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

	    if(cursor != null) {
	        
	    	if (cursor.moveToFirst()) {
	    		name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
	    		id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
	        }
	        
	        cursor.close();
	    }
	    if (id == null)
	    	return null;
		LinphoneContact lc = new LinphoneContact();
		lc.setAndroidId(id);
		lc.setFullName(name);
	    return lc;
	}
	
	public static String timestampToHumanDate(Calendar cal , Context ctx) {
		SimpleDateFormat dateFormat;
		if (isToday(cal)) {
			return ctx.getString(R.string.mnp_today);
		} else if (isYesterday(cal)) {
			return ctx.getString(R.string.mnp_yesterday);
		} else {
			dateFormat = new SimpleDateFormat(ctx.getResources().getString(
					R.string.history_date_format));
		}

		return dateFormat.format(cal.getTime());
	}

	private static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}

		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
					.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR));
	}

	private static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	private static boolean isYesterday(Calendar cal) {
		Calendar yesterday = Calendar.getInstance();
		yesterday.roll(Calendar.DAY_OF_MONTH, -1);
		return isSameDay(cal, yesterday);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLogs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mLogs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

	    if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.history_cell_simple, parent , false);

	        holder = new ViewHolder();
	        holder.title = (TextView) convertView.findViewById(R.id.sipUri);
	        holder.subTitle = (TextView) convertView.findViewById(R.id.subTitle);
	        holder.logDay = (TextView) convertView.findViewById(R.id.timeStamp);
	        holder.logTime = (TextView) convertView.findViewById(R.id.time);
	        holder.background = (MaterialRippleLayout) convertView.findViewById(R.id.ripple_view);
	        holder.detail = (ImageButton) convertView.findViewById(R.id.detail);
	        holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
	        holder.deleteContainer = (MaterialRippleLayout) convertView.findViewById(R.id.delete_container);
	        holder.detailContainer = (MaterialRippleLayout) convertView.findViewById(R.id.detail_container);
	        
	        convertView.setTag(holder);
	    } else {
	        holder = (ViewHolder)convertView.getTag();
	    }
	    
	    
	    final LinphoneCallLog log = mLogs.get(position);
		long timestamp = log.getTimestamp();
		final LinphoneAddress address;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar logTime = Calendar.getInstance();
		logTime.setTimeInMillis(timestamp);
		holder.logDay.setText(timestampToHumanDate(logTime, context));
		holder.logTime.setText(sdf.format(logTime.getTime()));
		if (log.getDirection() == CallDirection.Incoming) {
			address = log.getFrom();
			if (log.getStatus() == CallStatus.Missed) {
				holder.subTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.call_log_missed, 0 , 0, 0);
				holder.subTitle.setText(context.getString(R.string.mnp_missed_call));
			} else {
				holder.subTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.call_log_incoming, 0 , 0, 0); 
				holder.subTitle.setText(context.getString(R.string.mnp_incoming_call));
			}
		} else {
			address = log.getTo();
			holder.subTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.call_log_outgoing, 0 , 0, 0); 
			holder.subTitle.setText(context.getString(R.string.mnp_outgoing_call));
		}
		
		LinphoneContact contactName = getContactName(context, address.getUserName());
		if (contactName != null)
			holder.title.setText(contactName.getFullName());
		else
			holder.title.setText(address.getUserName());
		
	    if (isEditMode) {
			holder.delete.setVisibility(View.VISIBLE);
			holder.detail.setVisibility(View.GONE);
			holder.deleteContainer.setVisibility(View.VISIBLE);
			holder.detailContainer.setVisibility(View.GONE);
			holder.delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.d("","delete click");
					LinphoneCallLog log = mLogs.get(position);
					LinphoneManager.getLc().removeCallLog(log);
					mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
					notifyDataSetChanged();
				}
			});
		} else {
			holder.delete.setVisibility(View.GONE);
			holder.detail.setVisibility(View.VISIBLE);
			holder.deleteContainer.setVisibility(View.GONE);
			holder.detailContainer.setVisibility(View.VISIBLE);
			holder.detail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, CallLogDetailActivity.class);
					CallLogDetailActivity.log = log;
					context.startActivity(intent);
				}
			});
		}
	    
	    final LinphoneContact tempContactName = contactName;
	    final LinphoneAddress tempAddress = address;
	    holder.background.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddressType address = new AddressText(context,
						null);
				if (tempContactName!= null)
				address.setDisplayedName(tempContactName.getFullName());
				address.setText(tempAddress.getUserName());
				LinphoneManager.getInstance().newOutgoingCall(address);
			}
		});
		
		return convertView;
	}

	
	private static class ViewHolder{
		
		public TextView title;
		public TextView subTitle;
		public MaterialRippleLayout background;
		public TextView logDay;
		public TextView logTime;
		public ImageButton detail;
		public ImageButton delete;
		public MaterialRippleLayout detailContainer;
		public MaterialRippleLayout deleteContainer;
	}
	
}
