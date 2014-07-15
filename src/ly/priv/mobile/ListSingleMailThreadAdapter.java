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
	private ArrayList<String> mListMails;
	private String mailText;

	public ListSingleMailThreadAdapter(Activity activity, ArrayList<String> list) {
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		mailText = mListMails.get(position);
		viewHolder.mailBody.setText(mailText);
		return convertView;
	}

	static class ViewHolder {
		protected TextView mailBody;
	}
}