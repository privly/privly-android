package ly.priv.mobile.gui.microblogs;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;




/**
 * Clas for login and showing twets from Twitter
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class MicroblogListPostsActivity extends SherlockFragment {
	private static final String TAG = "MicroblogListPostsActivity";
	private ArrayList<Post> mListPosts;
	private ListView mListViewPosts;
	private ListMicroblogAdapter mListMicroblogAdapter;
	private Values mValues;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.activity_list, container, false);
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Twitter);
		mValues = new Values(getActivity());
//	     if (!Utilities.IsNetworkAvailable(getActivity())) {
//	            AlertMessageBox.Show(getActivity(), "Internet connection", "A valid internet connection can't be established", AlertMessageBox.AlertMessageBoxIcon.Info);
//	           
//	        }
		/*
		 * this.mListViewPosts = ((ListView) view.findViewById(R.id.lView));
		 * mListPosts = new ArrayList<Post>(); mListPosts.add(new Post("name1",
		 * "Nic1", "1d", "Mess on twits", "")); mListPosts.add(new Post("name2",
		 * "Nic2", "3d", "Mess on twits", "")); mListPosts.add(new Post("name3",
		 * "Nic3", "4d", "Mess on twits", "")); mListPosts.add(new Post("name4",
		 * "Nic4", "5d", "Mess on twits", "")); if (mListPosts != null) {
		 * mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(),
		 * mListPosts); mListViewPosts.setAdapter(mListMicroblogAdapter); }
		 */
		logIn();		
		return view;
	}

	 private void initializeComponent() {
		 
	 }
/**
 * Method for run login or show tweets
 */
	private void logIn() {
		Log.d(TAG, "logIn");
		if (!mValues.getTwitterLoggedIn()) {
			new TwitterAuthenticateTask().execute();
			mValues.setTwitterLoggedIn(true);
		} else {
			Uri uri = getActivity().getIntent().getData();
	        if (uri != null && uri.toString().startsWith(ConstantValues.TWITTER_CALLBACK_URL)) {
	            String verifier = uri.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
	            new TwitterGetAccessTokenTask().execute(verifier);
	        } else{
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
			}

			@Override
			protected RequestToken doInBackground(String... params) {				
				return TwitterUtil.getInstance().getRequestToken();
			}
		}
		
		 class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

		        @Override
		        protected void onPostExecute(String userName) {
		        	Log.d(TAG, "TwitterGetAccessTokenTask");
		         if(userName!=null)
		        	Log.d(TAG, userName);
		        }

		        @Override
		        protected String doInBackground(String... params) {

		            Twitter twitter = TwitterUtil.getInstance().getTwitter();
		            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
		            if (!Utilities.isNullOrWhitespace(params[0])) {
		                try {
		                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);		                    
		                    mValues.setTwitterOauthToken(accessToken.getToken());
		                    mValues.setTwitterOauthTokenSecret(accessToken.getTokenSecret());
		                    return twitter.showUser(accessToken.getUserId()).getName();
		                } catch (TwitterException e) {
		                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		                }
		            } else {
		                String accessTokenString =mValues.getTwitterOauthToken();
		                String accessTokenSecret = mValues.getTwitterOauthToken();
		                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
		                try {
		                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
		                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
		                } catch (TwitterException e) {
		                    e.printStackTrace();  
		                }
		            }

		            return null;
		        }
		    }


}
