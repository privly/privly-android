package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.Values;
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
	private Values mValues;
	private String mFaceBookId;

	public ListUserMessagesAdapter(Activity activity, ArrayList<SMessage> list) {
		this.mActivity = activity;
		this.mListUsserMessages = list;
		this.mImageLoader = new ImageLoader(
				mActivity.getApplicationContext());
		this.mImageLoader.setStub_id(R.drawable.ava);
		this.mValues=new Values(mActivity);
		this.mFaceBookId=mValues.getFacebookID();
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
		mMessage =  mListUsserMessages.get(position);
		View vi = null;		
		if (convertView == null) {
			ViewHolder holder = new ViewHolder();			
			if (mMessage.getId().equals(mFaceBookId)) {
				inflater = mActivity.getLayoutInflater();
				vi = inflater
						.inflate(
								R.layout.item_socialnetwork_list_user_messages_to,
								null);

				holder.mMessage = ((TextView) vi.findViewById(R.id.tvMessageTo));
				holder.mTine = ((TextView) vi.findViewById(R.id.tvTimeTo));
				holder.mAvatar= new ImageView(mActivity);
			} else {
				inflater = mActivity.getLayoutInflater();
				vi = inflater.inflate(
						R.layout.item_socialnetwork_list_user_messages_from,
						null);
				holder.mMessage = ((TextView) vi
						.findViewById(R.id.tvMessageFrom));
				holder.mTine = ((TextView) vi.findViewById(R.id.tvTimeFrom));
				holder.mAvatar = ((ImageView) vi
						.findViewById(R.id.ivAvaFriendFrom));
			}
			vi.setTag(holder);
		} else {
			vi = convertView;
		}
		
		if (mMessage != null) {
			ViewHolder holder = (ViewHolder) vi.getTag();
			holder.mMessage.setText(mMessage.getMessage());
			holder.mTine.setText(mMessage.getTime());
			if (!mMessage.getId().equals(mFaceBookId))
				mImageLoader.DisplayImage(mMessage.getUrlToAvatar(),
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
