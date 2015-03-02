package ly.priv.mobile.api.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.Values;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

/**
 * Adapter for ListViewUserMessages. It is showing messages for specific
 * DialogId.
 * 
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class ListUserMessagesAdapter extends BaseAdapter {
	private static final int TYPE_ITEM_TO = 0;
	private static final int TYPE_ITEM_FROM = 1;
	private static final int TYPE_MAX_COUNT = 2;
	private LayoutInflater mInflater = null;
	private Activity mActivity;
	private ImageLoader mImageLoader;
	private ArrayList<SMessage> mListUsserMessages;
	private SMessage mMessage;
	private Values mValues;
	private String mFaceBookId;

	/**
	 * Constructor for ListUserMessagesAdapter
	 * 
	 * @param activity
	 * @param list
	 */
	public ListUserMessagesAdapter(Activity activity, ArrayList<SMessage> list) {
		this.mActivity = activity;
		this.mListUsserMessages = list;
		this.mImageLoader = new ImageLoader(mActivity);
		this.mImageLoader.setStub_id(R.drawable.ava);
		this.mValues = new Values(mActivity);
		this.mFaceBookId = mValues.getFacebookID();
		this.mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.mListUsserMessages.size();
	}

	@Override
	public Object getItem(int paramInt) {
		return Integer.valueOf(paramInt);
	}

	@Override
	public long getItemId(int paramInt) {
		return paramInt;
	}

	@Override
	public int getItemViewType(int position) {
		mMessage = mListUsserMessages.get(position);
		if (mMessage.getId().equals(mFaceBookId)) {
			return TYPE_ITEM_TO;
		} else {
			return TYPE_ITEM_FROM;
		}

	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		mMessage = mListUsserMessages.get(position);
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_ITEM_TO:
				convertView = mInflater
						.inflate(
								R.layout.item_socialnetwork_list_user_messages_to,
								null);
				holder.mMessage = ((TextView) convertView
						.findViewById(R.id.tvMessageTo));
				holder.mTine = ((TextView) convertView
						.findViewById(R.id.tvTimeTo));
				holder.mAvatar = new ImageView(mActivity);
				break;

			case TYPE_ITEM_FROM:
				convertView = mInflater.inflate(
						R.layout.item_socialnetwork_list_user_messages_from,
						null);
				holder.mMessage = ((TextView) convertView
						.findViewById(R.id.tvMessageFrom));
				holder.mTine = ((TextView) convertView
						.findViewById(R.id.tvTimeFrom));
				holder.mAvatar = ((ImageView) convertView
						.findViewById(R.id.ivAvaFriendFrom));
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mMessage != null) {
			holder.mMessage.setText(mMessage.getMessage());
			holder.mTine.setText(mMessage.getTime());
			if (!mMessage.getId().equals(mFaceBookId))
				mImageLoader.DisplayImage(mMessage.getUrlToAvatar(),
						holder.mAvatar);
		}
		return convertView;

	}

	static class ViewHolder {
		protected ImageView mAvatar;
		protected TextView mMessage;
		protected TextView mTine;
	}
}
