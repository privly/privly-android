package ly.priv.mobile.gui.microblogs;

import java.util.ArrayList;
import java.util.List;

import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

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
public class MicroblogListPostsActivity extends SherlockFragment {
	private static final String TAG = "MicroblogListPostsActivity";
	private ProgressBar mProgressBar;
	private ListView mListViewPosts;

	private ArrayList<Post> mListPosts;
	private ListMicroblogAdapter mListMicroblogAdapter;
	private Values mValues;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView ");
		View view = inflater.inflate(R.layout.activity_list, container, false);
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
	private void initializeComponent(View view) {
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Twitter);
		mListViewPosts = ((ListView) view.findViewById(R.id.lView));
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);

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
	 * AsyncTask for get Access Token
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class TwitterGetAccessTokenTask extends AsyncTask<String, String, List<twitter4j.Status> > {

		@Override
		protected void onPostExecute(List<twitter4j.Status>  statuses) {
			Log.d(TAG, "TwitterGetAccessTokenTask");			
			if (statuses != null){			
			    Log.d(TAG, "Showing home timeline.");
			    mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(),
						  (ArrayList<twitter4j.Status>) statuses); 
			    mListViewPosts.setAdapter(mListMicroblogAdapter); 
			}
				
			mProgressBar.setVisibility(View.INVISIBLE);
		}

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
				List<twitter4j.Status> statuses = TwitterUtil.getInstance().getTwitter().getHomeTimeline();
				return statuses;
			} catch (TwitterException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
