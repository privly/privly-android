package ly.priv.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;

public class ListMailThreadsAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private Activity mActivity;
	private ArrayList<EmailThreadObject> mListThreads;
	private EmailThreadObject mailThread;

	public ListMailThreadsAdapter(Activity activity, ArrayList<EmailThreadObject> list) {
		this.mActivity = activity;
		this.mListThreads = list;
	}

	public int getCount() {
		return this.mListThreads.size();
	}

	public Object getItem(int paramInt) {
		return this.mListThreads.size();
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			inflater = this.mActivity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_mail_list_threads, null);
			viewHolder = new ViewHolder();
			viewHolder.mailSender = (TextView) convertView
					.findViewById(R.id.mailSender);
			viewHolder.mailCount = (TextView) convertView.findViewById(R.id.mailCount);
			viewHolder.mailSnippet = (TextView) convertView
					.findViewById(R.id.mailSnippet);
			viewHolder.mailTime = (TextView) convertView.findViewById(R.id.mailTime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		mailThread = mListThreads.get(position);
		viewHolder.mailCount.setText(mailThread.getMailCount());
		viewHolder.mailSender.setText(mailThread.getMailSender());
		viewHolder.mailSnippet.setText(mailThread.getMailSnippet());
		viewHolder.mailTime.setText(mailThread.getMailTime());
		return convertView;
	}
	
//	public static final CharSequence formatSameDayTimeCustom(Context context, long then) {
//	    if (DateUtils.isToday(then)) {
//	        return android.text.format.DateFormat.getTimeFormat(context).format(new Date(then));
//	    }
//	    else {
//	        final String format = android.text.format.DateFormat.getDateFormat(context).toString();
//	        return new SimpleDateFormat(format).format(new Date(then));
//	    }
//	}

	static class ViewHolder {
		protected TextView mailSnippet;
		protected TextView mailSender;
		protected TextView mailCount;
		protected TextView mailTime;
	}
}