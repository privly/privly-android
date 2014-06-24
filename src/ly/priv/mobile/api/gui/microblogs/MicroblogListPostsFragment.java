package ly.priv.mobile.api.gui.microblogs;

import java.util.ArrayList;
import java.util.List;

import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		OnRefreshListener {
	private static final String TAG = "MicroblogListPostsActivity";
	private ProgressBar mProgressBar;
	private ListView mListViewPosts;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListMicroblogAdapter mListMicroblogAdapter;
	private Values mValues;
	private int mPage = 1;
	private ArrayList<twitter4j.Status> mPosts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView ");
		View view = inflater.inflate(R.layout.activity_list_pull_refrash,
				container, false);
		initializeComponent(view);
		mValues = new Values(getActivity());
		if (!Utilities.isDataConnectionAvailable(getActivity())) {
			Log.d(TAG, getString(R.string.no_internet_connection));
			Utilities.showToast(getActivity(),
					getString(R.string.no_internet_connection), true);
		} else {
			logIn();
		}
		return view;
	}

	/**
	 * Initialize component
	 * 
	 * @param view
	 */
	// API level 11
	// TODO fix it next release ( reduce API level to 10), delete
	// 'mSwipeRefreshLayout.setRotation(180);'
	@SuppressLint("NewApi")
	private void initializeComponent(View view) {
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Twitter);
		mListViewPosts = ((ListView) view.findViewById(R.id.lView_refresh));
		mProgressBar = (ProgressBar) view
				.findViewById(R.id.pbLoadingData_refresh);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setRotation(180);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mListViewPosts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String url = "";
				URLEntity[] entities = mPosts.get(mPosts.size() - position - 1)
						.getURLEntities();
				for (int i = 0; i < entities.length; i++) {
					// Since urls are shortened by the twitter t.co
					// service, get the expanded urls to search for
					// Privly links
					url += "  " + entities[i].getExpandedURL();
				}

				ArrayList<String> listOfUrls = Utilities.fetchPrivlyUrls(url);
				if (listOfUrls.size() > 0) {
					FragmentTransaction transaction = getActivity()
							.getSupportFragmentManager().beginTransaction();
					ShowContent showContent = new ShowContent();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("listOfLinks", listOfUrls);
					showContent.setArguments(bundle);
					transaction.replace(R.id.container, showContent);
					transaction.addToBackStack(null);
					transaction.commit();
				} else {
					Toast.makeText(getActivity(),
							R.string.message_not_containe_privly_link,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
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
			mValues.setTwitterLoggedIn(false);
			mValues.setTwitterOauthToken("");
			mValues.setTwitterOauthTokenSecret("");
			TwitterUtil.getInstance().reset();
			logIn();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method for run login or show tweets
	 */
	private void logIn() {
		Log.d(TAG, "logIn");
		mProgressBar.setVisibility(View.VISIBLE);
		if (!mValues.getTwitterLoggedIn()) {
			new TwitterAuthenticateTask().execute();
			mValues.setTwitterLoggedIn(true);
		} else {
			Uri uri = getActivity().getIntent().getData();
			if (uri != null
					&& uri.toString().startsWith(
							ConstantValues.TWITTER_CALLBACK_URL)) {
				String verifier = uri
						.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
				new TwitterGetAccessTokenTask().execute(verifier);
			} else {
				new TwitterGetAccessTokenTask().execute("");
			}
		}
	}

	/**
	 * AsyncTask for logIn to Twitter
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class TwitterAuthenticateTask extends
			AsyncTask<String, String, RequestToken> {

		@Override
		protected void onPostExecute(RequestToken requestToken) {
			Log.d(TAG, "TwitterAuthenticateTask");
			if (requestToken != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(requestToken.getAuthenticationURL()));
				startActivity(intent);
			}
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		@Override
		protected RequestToken doInBackground(String... params) {
			return TwitterUtil.getInstance().getRequestToken();
		}
	}

	/**
	 * AsyncTask for get Access Token and get tweets
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class TwitterGetAccessTokenTask extends
			AsyncTask<String, String, List<twitter4j.Status>> {

		@Override
		protected List<twitter4j.Status> doInBackground(String... params) {

			Twitter twitter = TwitterUtil.getInstance().getTwitter();
			RequestToken requestToken = TwitterUtil.getInstance()
					.getRequestToken();
			AccessToken accessToken = null;
			if (!Utilities.isNullOrWhitespace(params[0])) {
				try {
					accessToken = twitter.getOAuthAccessToken(requestToken,
							params[0]);
					mValues.setTwitterOauthToken(accessToken.getToken());
					mValues.setTwitterOauthTokenSecret(accessToken
							.getTokenSecret());
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				String accessTokenString = mValues.getTwitterOauthToken();
				String accessTokenSecret = mValues.getTwitterOauthTokenSecret();
				accessToken = new AccessToken(accessTokenString,
						accessTokenSecret);
				TwitterUtil.getInstance().setTwitterFactory(accessToken);
			}

			try {
				Paging paging = new Paging(mPage);
				List<twitter4j.Status> statuses = TwitterUtil.getInstance()
						.getTwitter().getHomeTimeline(paging);
				return statuses;
			} catch (TwitterException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> statuses) {
			Log.d(TAG, "TwitterGetAccessTokenTask");
			if (statuses != null) {
				Log.d(TAG, "Showing home timeline.");
				for (twitter4j.Status status : statuses) {
					Log.d(TAG, status.toString());
				}
				mPosts = new ArrayList<twitter4j.Status>(statuses);
				mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(),
						mPosts);
				mListViewPosts.setAdapter(mListMicroblogAdapter);
				mListViewPosts.setSelection(statuses.size() - 1);
			}

			mProgressBar.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onRefresh() {
		Log.d(TAG, "onRefresh for SwipeRefreshLayout");
		mPage++;
		new TwitterGetTwets().execute(mPage);
	}

	/**
	 * AsyncTask for get next tweets
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class TwitterGetTwets extends
			AsyncTask<Integer, Void, List<twitter4j.Status>> {

		@Override
		protected List<twitter4j.Status> doInBackground(Integer... params) {
			try {
				Paging paging = new Paging(mPage);
				List<twitter4j.Status> statuses = TwitterUtil.getInstance()
						.getTwitter().getHomeTimeline(paging);
				return statuses;
			} catch (TwitterException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> statuses) {
			Log.d(TAG, "TwitterGetAccessTokenTask");
			if (statuses != null) {
				Log.d(TAG, "Showing" + mPage + " page home timeline.");
				Integer pos = statuses.size() - 1;
				mPosts.addAll(statuses);
				mListMicroblogAdapter.notifyDataSetChanged();
				mListViewPosts.setSelection(pos);
				

			}
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

}
