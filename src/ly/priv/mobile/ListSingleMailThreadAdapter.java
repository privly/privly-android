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

public class ListSingleMailThreadAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private Activity mActivity;
	private ArrayList<SingleEmailObject> mListMails;
	private SingleEmailObject mailObject;

	public ListSingleMailThreadAdapter(Activity activity, ArrayList<SingleEmailObject> list) {
		this.mActivity = activity;
		this.mListMails = list;
	}

	public int getCount() {
		return this.mListMails.size();
	}

	public Object getItem(int paramInt) {
		return this.mListMails.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			inflater = this.mActivity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.single_mail, null);
			viewHolder = new ViewHolder();
			viewHolder.mailBody = (TextView) convertView
					.findViewById(R.id.mailBody);
			viewHolder.mailSender = (TextView) convertView
					.findViewById(R.id.mailSender);
			viewHolder.mailTime = (TextView) convertView
					.findViewById(R.id.mailTime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		mailObject = mListMails.get(position);
		viewHolder.mailTime.setText(mailObject.getMailTime());
		viewHolder.mailBody.setText(mailObject.getMailSnippet());
		viewHolder.mailSender.setText(mailObject.getMailSender());
		return convertView;
	}

	static class ViewHolder {
		protected TextView mailBody;
		protected TextView mailSender;
		protected TextView mailTime;
	}
}