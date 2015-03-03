package ly.priv.mobile.api.gui.socialnetworks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.gui.activities.MainActivity;
import ly.priv.mobile.gui.ShowContentFragment;

/**
 * Showing messages in chose dialog
 * <p>
 * <ul>
 * <li>Get mDialogID from Bundle.</li>
 * <li>If privly link contained in message then Redirect User to
 * {@link ly.priv.mobile.gui.ShowContentFragment} ShowContent Activity</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Dependencies :
 * <ul>
 * <li>/privly-android/libs/android-support-v4.jar</li>
 * </ul>
 * </p>
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class ListUserMessagesFragment extends Fragment implements
		OnScrollListener {
	private static final String TAG = "SListUserMessagesActivity";
	private ArrayList<SMessage> mListUserMess;
	private ListUserMessagesAdapter mListUserMessagesAdapter;
	private ListView mListViewUserMessages;
	private ProgressBar mProgressBar;
	private String mDialogID;
	private String mNextUrlForLoadingMessages;
	private Boolean mflNoMoreMessage = false;
	private ISocialNetworks mISocialNetworks;
	private MainActivity mActivity;
	private View mHeaderView;
	private boolean mIsLoading;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		mHeaderView = (View) inflater.inflate(R.layout.loading_layout, null);
		mISocialNetworks.setTitle();
		mListViewUserMessages = ((ListView) view.findViewById(R.id.lView));
		mListViewUserMessages.setOnScrollListener(this);
		mListViewUserMessages.addHeaderView(mHeaderView);
		mHeaderView.setVisibility(View.INVISIBLE);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mProgressBar.setVisibility(View.VISIBLE);
		mDialogID = getArguments().getString("DialogID");
		mListViewUserMessages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayList<String> listOfUrls = Utilities
						.fetchPrivlyUrls(mListUserMess.get(position - 1)
								.getMessage());
				if (listOfUrls.size() > 0) {
					FragmentTransaction transaction = mActivity
							.getSupportFragmentManager().beginTransaction();
					ShowContentFragment showContent = new ShowContentFragment();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("listOfLinks", listOfUrls);
					showContent.setArguments(bundle);
					transaction.replace(R.id.container, showContent);
					transaction.addToBackStack(null);
					transaction.commit();
				} else {
					Toast.makeText(mActivity,
							R.string.message_not_containe_privly_link,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		new GetListOfMessages().execute();
		return view;
	}

	/**
	 * AsyncTask for geting messages in chose dialog
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	private class GetListOfMessages extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			HashMap<String, Object> res = (HashMap<String, Object>) mISocialNetworks
					.getListOfMessages(mDialogID);
			if (res != null) {
				mListUserMess = (ArrayList<SMessage>) res
						.get(ISocialNetworks.ARRAY);
				mNextUrlForLoadingMessages = (String) res
						.get(ISocialNetworks.NEXTLINK);
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			if (mListUserMess != null && mActivity != null) {
				mListUserMessagesAdapter = new ListUserMessagesAdapter(
						mActivity, mListUserMess);
				mListViewUserMessages.setAdapter(mListUserMessagesAdapter);
				mListViewUserMessages.setSelection(mListUserMessagesAdapter
						.getCount() - 1);
			}
			mProgressBar.setVisibility(View.INVISIBLE);
			super.onPostExecute(result);
		}

	}

	/**
	 * AsyncTask for getting next messages for current DialogId
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	private class FetchFaceBookNextMessages extends
			AsyncTask<String, Void, ArrayList<SMessage>> {

		@Override
		protected void onPreExecute() {
			mIsLoading = true;
			mHeaderView.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<SMessage> doInBackground(String... urls) {
			HashMap<String, Object> res = (HashMap<String, Object>) mISocialNetworks
					.fetchNextMessages(urls[0]);
			ArrayList<SMessage> sMessages = null;
			if (res != null) {
				sMessages = (ArrayList<SMessage>) res
						.get(ISocialNetworks.ARRAY);
				mNextUrlForLoadingMessages = (String) res
						.get(ISocialNetworks.NEXTLINK);
			}
			return sMessages;
		}

		@Override
		protected void onPostExecute(ArrayList<SMessage> result) {
			if (result != null) {
				mflNoMoreMessage = false;
				Integer pos = result.size() - 1;
				result.addAll(mListUserMess);
				mListUserMess = new ArrayList<SMessage>(result);
				mListUserMessagesAdapter = new ListUserMessagesAdapter(
						mActivity, mListUserMess);
				mListViewUserMessages.setAdapter(mListUserMessagesAdapter);
				mListViewUserMessages.setSelection(pos);
			} else {
				Toast.makeText(mActivity, R.string.no_more_messages,
						Toast.LENGTH_SHORT).show();
				mflNoMoreMessage = true;
			}
			mIsLoading = false;
			mHeaderView.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * Set Interface ISocialNetworks
	 * 
	 * @param mISocialNetworks
	 *            the mISocialNetworks to set
	 */
	public void setmISocialNetworks(ISocialNetworks mISocialNetworks) {
		this.mISocialNetworks = mISocialNetworks;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mListUserMessagesAdapter == null)
			return;

		if (mListUserMessagesAdapter.getCount() == 0)
			return;
		Log.d(TAG, "totalItemCount=" + totalItemCount);
		Log.d(TAG, "visibleItemCount=" + visibleItemCount);
		Log.d(TAG, "firstVisibleItem=" + firstVisibleItem);
		if (totalItemCount == visibleItemCount)
			return;
		if (firstVisibleItem == 0 && !mIsLoading) {
			// It is time to add new data. We call the listener
			if (!mflNoMoreMessage) {
				FetchFaceBookNextMessages faceBookNextMessages = new FetchFaceBookNextMessages();
				faceBookNextMessages.execute(mNextUrlForLoadingMessages);
			} else {
				Toast.makeText(mActivity, R.string.no_more_messages,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
}