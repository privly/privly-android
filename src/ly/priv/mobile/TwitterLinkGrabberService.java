package ly.priv.mobile;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Grabs Privly links from a twitter user's timeline and stores them in the
 * local database.
 *
 * @author Shivam Verma
 *
 */
public class TwitterLinkGrabberService extends Activity {

	// TwitterProperties
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;

	// Create a new App on https://dev.twitter.com/apps/new for the following
	// credentials
	public final static String consumerKey = "9C0j9rlHFXvnNVeoNnbA";
	public final static String consumerSecret = "mHw3TWz63Eemq9Jk8SoIBLeYyGWGWmsgHs7KIkA";
	protected static final String SOURCE_TWITTER = "TWITTER";

	private final String CALLBACKURL = "x-oauthflow-twitter://privlyT4JCallback";
	final int THREAD_STARTING = 0;
	final int THREAD_COMPLETE = 1;
	String url;

	private Twitter twitter;
	AccessToken accessToken;
	String verifier;
	Context context;
	ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("TAG", "onCreate");
		setContentView(R.layout.link_grabber_service);
		context = getApplicationContext();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		// Checks if the user has already authenticated with the application. If
		// not, start authentication process, else fetch Twitter Token from
		// TwitterHelperMethods
		if (!TwitterHelperMethods.isTwitterUserLoggedIn(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					loginToTwitter();
				}
			}).start();
		} else {
			String twitterToken = TwitterHelperMethods
					.getTwitterToken(getApplicationContext());
			String twitterTokenSecret = TwitterHelperMethods
					.getTwitterTokenSecret(getApplicationContext());
			Log.d("tokenSecret", twitterTokenSecret);
			if (twitterToken != null && twitterTokenSecret != null) {
				// This sets up the twitter object which can successfully make
				// requests to the twitter api.
				setUpTwitter(twitterToken, twitterTokenSecret);
				long userId = accessToken.getUserId();
				getTweets(userId);
			} else {
				loginToTwitter();
			}

		}

	}

	/**
	 * Fires up an Intent which allows the twitter user to grant permission to
	 * the the Privly app. The User is then redirected to the callback url.
	 */
	private void loginToTwitter() {
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey,
					consumerSecret);
			httpOauthprovider = new DefaultOAuthProvider(
					"https://twitter.com/oauth/request_token",
					"https://twitter.com/oauth/access_token",
					"https://twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, CALLBACKURL);

			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl)));
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Handles the new intent received after the user authorizes the Privly app
	 * with twitter permissions.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

			verifier = uri
					.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						httpOauthprovider.retrieveAccessToken(
								httpOauthConsumer, verifier);

						if (TwitterHelperMethods.setTwitterAccessTokenValues(
								context, httpOauthConsumer.getToken(),
								httpOauthConsumer.getTokenSecret())) {
							TwitterHelperMethods.setTwitterUserLoggedInStatus(
									context, true);
						}

						setUpTwitter(httpOauthConsumer.getToken(),
								httpOauthConsumer.getTokenSecret());
						long userId = accessToken.getUserId();
						getTweets(userId);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();
		}

	}

	/**
	 * Creates a new twitter AccessToken Object and then sets up the twitter
	 * object with AccessToken and ConsumerKey and Consumer Secret.
	 *
	 * @param {String} token
	 * @param {String} tokenSecret
	 *
	 */
	private void setUpTwitter(String token, String tokenSecret) {
		accessToken = new AccessToken(token, tokenSecret);
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(accessToken);
	}

	/**
	 * Handler for the new thread spawned. Maintains progressDialog while
	 * fetching inbox messages from Facebook.
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case THREAD_STARTING :
					progressDialog
							.setMessage("Fetching new Privly links from your Twitter timeline");
					progressDialog.show();
					break;

				case THREAD_COMPLETE :
					progressDialog.dismiss();
					Bundle bundle = new Bundle();
					bundle.putString("contentSource", "TWITTER");
					Intent showContentIntent = new Intent(
							TwitterLinkGrabberService.this, ShowContent.class);
					showContentIntent.putExtras(bundle);
					startActivity(showContentIntent);
					// Clear this activity from stack so that the user is taken
					// to the Home Screen on back press
					TwitterLinkGrabberService.this.finish();

			}
		}
	};

	/**
	 * Fetch tweets from the twitter timeline. Looks for any Privly links in the
	 * tweets and store them in the Db.
	 *
	 * @param {long} userId Twitter user id for which we want to fetch the
	 *        timeline.
	 */
	public void getTweets(long userId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.what = THREAD_STARTING;
				handler.sendMessage(msg);
				try {
					List<Status> statuses;
					statuses = twitter.getHomeTimeline();

					for (Status status : statuses) {
						URLEntity[] entities = status.getURLEntities();
						for (int i = 0; i < entities.length; i++) {
							// Since urls are shortened by the twitter t.co
							// service, get the expanded urls to search for
							// Privly links
							url = entities[i].getExpandedURL();
						}
						ArrayList<String> listOfUrls = Utilities
								.fetchPrivlyUrls(url);
						if (!listOfUrls.isEmpty()) {
							Iterator<String> iter = listOfUrls.iterator();
							while (iter.hasNext()) {
								String url = iter.next();
								// Checks if the link already exists in local
								// Db. If not, Insert into Db.
								if (!Utilities.ifLinkExistsInDb(
										getApplicationContext(), url,
										SOURCE_TWITTER)) {
									Log.d("INSERTING URL", url);
									Utilities.insertIntoDb(context,
											SOURCE_TWITTER, url,
											String.valueOf(status.getId()),
											status.getUser().getScreenName());
								}

							}
						}
					}
				} catch (TwitterException te) {
				}
				Message msgFinal = Message.obtain();
				msgFinal.what = THREAD_COMPLETE;
				handler.sendMessage(msgFinal);

			}
		}).start();

	}
}