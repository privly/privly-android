package ly.priv.mobile.grabbers;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.Index;
import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import ly.priv.mobile.api.gui.microblogs.IMicroblogs;
import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsFragment;
import ly.priv.mobile.api.gui.microblogs.Post;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

public class TwitterGrabberService extends SherlockFragment implements IMicroblogs{
	private static final String TAG = TwitterGrabberService.class.getSimpleName();
	private static final int COUNT_OF_TWEETS=100;
	private Values mValues;
	private ProgressBar mProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView ");
		View view = inflater.inflate(R.layout.activity_list, container, false);	
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Login_Twitter);
		mValues = new Values(getActivity());
		mProgressBar = (ProgressBar) view
				.findViewById(R.id.pbLoadingData);		
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
	 * Run social GUI
	 */
	private void runSocialGui() {
		Log.d(TAG, "runSocialGui");
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		MicroblogListPostsFragment microblogListPostsFragment =new MicroblogListPostsFragment();
		microblogListPostsFragment.setIMicroblogs(this);
		transaction.replace(R.id.container, microblogListPostsFragment);
		// transaction.disallowAddToBackStack();
		transaction.addToBackStack(null);
		transaction.commit();
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
		protected RequestToken doInBackground(String... params) {
			return TwitterUtil.getInstance().getRequestToken();
		}
		
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
	}

	/**
	 * AsyncTask for get Access Token
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	class TwitterGetAccessTokenTask extends
			AsyncTask<String, Void, Void> {		
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "TwitterGetAccessTokenTask");
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... params) {
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
			return null;			
		}


		@Override
		protected void onPostExecute(Void result) {
			mProgressBar.setVisibility(View.INVISIBLE);
			runSocialGui();
		}


	}

	@Override
	public void logout() {
		Log.d(TAG, "logout()");
		mValues.setTwitterLoggedIn(false);
		mValues.setTwitterOauthToken("");
		mValues.setTwitterOauthTokenSecret("");
		TwitterUtil.getInstance().reset();
		getActivity().getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new Index()).commit();		
	}

	@Override
	public ArrayList<Post> getPost(int page) {
		Log.d(TAG, "getPost()");
		Paging paging = new Paging(page,COUNT_OF_TWEETS);
		try {
			List<twitter4j.Status> statuses = TwitterUtil.getInstance()
					.getTwitter().getHomeTimeline(paging);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
