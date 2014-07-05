package ly.priv.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.google.api.services.gmail.model.Thread;

public class ListMailThreadsAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private Activity mActivity;
	private ImageLoader mImageLoader;
	private ArrayList<Thread> mListThreads;
	private Thread mailThread;

	public ListMailThreadsAdapter(Activity activity,
			ArrayList<Thread> list) {
		this.mActivity = activity;
		this.mListThreads = list;
		this.mImageLoader = new ImageLoader(
				this.mActivity.getApplicationContext());
		this.mImageLoader.setStub_id(R.drawable.ava);
	}

	public int getCount() {
		return this.mListThreads.size();
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

		mailThread = mListThreads.get(position);
		if (this.mailThread != null) {
			ViewHolder holder = (ViewHolder) vi.getTag();
			holder.mName.setText(mailThread.getSnippet());
//			holder.mNic.setText("@" + mPost.getUser().getScreenName());
//			holder.mMessage.setText(mPost.getText());
//			holder.mTine.setText(Utilities.getTimeForTwitter(mPost
//					.getCreatedAt()));
//			mImageLoader.DisplayImage(mPost.getUser()
//					.getBiggerProfileImageURL(), holder.mAvatar);
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