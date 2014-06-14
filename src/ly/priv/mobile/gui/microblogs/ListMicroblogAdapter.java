package ly.priv.mobile.gui.microblogs;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public class ListMicroblogAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private Activity mActivity;
	private ImageLoader mImageLoader;
	private ArrayList<twitter4j.Status> mListPosts;
	private twitter4j.Status mPost;

	public ListMicroblogAdapter(Activity activity, ArrayList<twitter4j.Status> list) {
		this.mActivity = activity;
		this.mListPosts = list;
		this.mImageLoader = new ImageLoader(
					this.mActivity.getApplicationContext());
		this.mImageLoader.setStub_id(R.drawable.ava);
	}

	public int getCount() {
		return this.mListPosts.size();
	}

	public Object getItem(int paramInt) {
		return Integer.valueOf(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = null;
		if (convertView == null) {
			inflater = this.mActivity.getLayoutInflater();
			vi = inflater.inflate(R.layout.item_microblog_list_users, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mName = ((TextView) vi.findViewById(R.id.tvUserName));
			viewHolder.mNic = ((TextView) vi.findViewById(R.id.tvNic));
			viewHolder.mMessage = ((TextView) vi.findViewById(R.id.tvMessage));
			viewHolder.mTine = ((TextView) vi.findViewById(R.id.tvTime));
			viewHolder.mAvatar = ((ImageView) vi.findViewById(R.id.ivAvaFriend));
			vi.setTag(viewHolder);
		} else {
			vi = convertView;
		}

		mPost = mListPosts.get(position);
		if (this.mPost != null) {
			ViewHolder holder = (ViewHolder) vi.getTag();
			holder.mName.setText(mPost.getUser().getName());
			holder.mNic.setText("@"+mPost.getUser().getScreenName());
			holder.mMessage.setText(mPost.getText());
			holder.mTine.setText(Utilities.getTimeForTwitter(mPost.getCreatedAt()));
			mImageLoader.DisplayImage(mPost.getUser().getMiniProfileImageURL(),
					holder.mAvatar);
		}
		return vi;
	}

	static class ViewHolder {
		protected ImageView mAvatar;
		protected TextView mMessage;
		protected TextView mName;
		protected TextView mNic;
		protected TextView mTine;
	}
}