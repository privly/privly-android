package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public class ListUserMessagesAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private Activity mActivity;
	private ImageLoader mImageLoader;
	private ArrayList<SMessage> mListUsserMessages;
	private SMessage mMessage;

	public ListUserMessagesAdapter(Activity activity, ArrayList<SMessage> list) {
		this.mActivity = activity;
		this.mListUsserMessages = list;
		this.mImageLoader = new ImageLoader(
				this.mActivity.getApplicationContext());
		this.mImageLoader.setStub_id(R.drawable.ava);
	}

	public int getCount() {
		return this.mListUsserMessages.size();
	}

	public Object getItem(int paramInt) {
		return Integer.valueOf(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		this.mMessage = ((SMessage) this.mListUsserMessages.get(position));
		View vi = null;
		if (this.mMessage.IsMyMessage()) {
			if (convertView == null) {
				inflater = this.mActivity.getLayoutInflater();
				vi = inflater
						.inflate(
								R.layout.item_socialnetwork_list_user_messages_to,
								null);
				ViewHolder holder = new ViewHolder();
				holder.mMessage = ((TextView) vi.findViewById(R.id.tvMessageTo));
				holder.mTine = ((TextView) vi.findViewById(R.id.tvTimeTo));
				vi.setTag(holder);
			} else {
				vi = convertView;
			}
		} else if (convertView == null) {
			inflater = this.mActivity.getLayoutInflater();
			vi = inflater.inflate(
					R.layout.item_socialnetwork_list_user_messages_from, null);
			ViewHolder holder = new ViewHolder();
			holder.mMessage = ((TextView) vi.findViewById(R.id.tvMessageFrom));
			holder.mTine = ((TextView) vi.findViewById(R.id.tvTimeFrom));
			holder.mAvatar = ((ImageView) vi.findViewById(R.id.ivAvaFriendFrom));
			vi.setTag(holder);
		} else {
			vi = convertView;
		}

		if (this.mMessage != null) {
			ViewHolder holder = (ViewHolder) vi.getTag();
			holder.mMessage.setText(this.mMessage.getMessage());
			holder.mTine.setText(this.mMessage.getTime());
			if (!this.mMessage.IsMyMessage())
				this.mImageLoader.DisplayImage(this.mMessage.getUrlToAvatar(),
						holder.mAvatar);
		}
		return vi;

	}

	static class ViewHolder {
		protected ImageView mAvatar;
		protected TextView mMessage;
		protected TextView mTine;
	}
}
