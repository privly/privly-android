package ly.priv.mobile.api.gui.microblogs;

import java.util.ArrayList;
import java.util.List;

import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.Index;
import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import ly.priv.mobile.grabbers.TwitterUtil;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Clas for login and showing twets from Twitter
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class MicroblogListPostsFragment extends SherlockFragment implements
		OnScrollListener {
	private static final String TAG = "MicroblogListPostsFragment";
	private ProgressBar mProgressBar;
	private View mFooterView;
	private ListView mListViewPosts;
	private ListMicroblogAdapter mListMicroblogAdapter;
	private int mPage = 1;
	private boolean mIsLoading;
	private ArrayList<Post> mPosts;
	private IMicroblogs mIMicroblogs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView ");
		View view = inflater.inflate(R.layout.activity_list, container, false);
		mFooterView = (View) inflater.inflate(R.layout.loading_layout, null);
		initializeComponent(view);
		new GetPostsTask().execute(mPage);
		return view;
	}

	/**
	 * Initialize component
	 * 
	 * @param view
	 */
	private void initializeComponent(View view) {
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Twitter);
		mListViewPosts = ((ListView) view.findViewById(R.id.lView));
		//mListViewPosts.addFooterView(mFooterView);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mListViewPosts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// String url = "";
				// URLEntity[] entities = mPosts.get(position)
				// .getURLEntities();
				// for (int i = 0; i < entities.length; i++) {
				// // Since urls are shortened by the twitter t.co
				// // service, get the expanded urls to search for
				// // Privly links
				// url += "  " + entities[i].getExpandedURL();
				// }
				//
				// ArrayList<String> listOfUrls =
				// Utilities.fetchPrivlyUrls(url);
				// if (listOfUrls.size() > 0) {
				// FragmentTransaction transaction = getActivity()
				// .getSupportFragmentManager().beginTransaction();
				// ShowContent showContent = new ShowContent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("listOfLinks", listOfUrls);
				// showContent.setArguments(bundle);
				// transaction.replace(R.id.container, showContent);
				// transaction.addToBackStack(null);
				// transaction.commit();
				// } else {
				// Toast.makeText(getActivity(),
				// R.string.message_not_containe_privly_link,
				// Toast.LENGTH_SHORT).show();
				// }
			}
		});
		mListViewPosts.setOnScrollListener(this);
		mPosts = new ArrayList<Post>();
		mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(), mPosts);
		mListViewPosts.setAdapter(mListMicroblogAdapter);
	}

	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.layout.menu_layout_microblog, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Item click listener for options menu.
	 * <p>
	 * relogin
	 * </p>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.logout:
			mIMicroblogs.logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * AsyncTask for get Access Token and get tweets
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class GetPostsTask extends AsyncTask<Integer, Void, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "GetPostsTask");
			if(mPage==1)
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<Post> doInBackground(Integer... params) {
			return mIMicroblogs.getPost(params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<Post> posts) {
			if (posts != null) {
				mPosts.addAll(posts);
				mListMicroblogAdapter.notifyDataSetChanged();
				if (mPage > 1) {
					mListViewPosts.setSelection(mPosts.size() - 1);
					mListViewPosts.removeFooterView(mFooterView);
					mIsLoading = false;
				}
			}
			if(mPage==1)
			mProgressBar.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mListMicroblogAdapter == null)
			return;

		if (mListMicroblogAdapter.getCount() == 0)
			return;

		int l = visibleItemCount + firstVisibleItem;
		if (l >= totalItemCount && !mIsLoading) {
			// It is time to add new data. We call the listener
			mListViewPosts.addFooterView(mFooterView);
			mIsLoading = true;
			mPage++;
			new GetPostsTask().execute(mPage);
		}

	}

	/**
	 * @param mIMicroblogs
	 *            the mIMicroblogs to set
	 */
	public void setIMicroblogs(IMicroblogs iMicroblogs) {
		this.mIMicroblogs = iMicroblogs;
	}

}
