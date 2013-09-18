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
 * <p>
 * Dependencies :
 * <ul>
 * <li>/privly-android/libs/signpost-commonshttp4-1.2.1.2.jar</li>
 * <li>/privly-android/libs/signpost-core-1.2.1.2.jar</li>
 * <li>/privly-android/libs/twitter4j-core-3.0.3.jar</li>
 * </ul>
 * </p>
 *
 * <p>
 * <ul>
 * <li>Checks if a user has already granted permissions to the application.</li>
 * <li>If yes, fetch the Access Token data using TwitterHelperMethods Class. If
 * not, Redirect user to authentication link</li>
 * <li>Setup the Twitter Object using Access Token and Consumer Data : Consumer
 * Key and Consumer Secret</li>
 * </ul>
 * </p>
 *
 * @author Shivam Verma
 *
 */
public class TwitterLinkGrabberService extends Activity {

	int numberOfLinks = 0;
	// TwitterProperties
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;

	// Create a new App on https://dev.twitter.com/apps/new for the following
	// credentials
	public final static String consumerKey = "9C0j9rlHFXvnNVeoNnbA";
	public final static String consumerSecret = "mHw3TWz63Eemq9Jk8SoIBLeYyGWGWmsgHs7KIkA";

	private static final String SOURCE_TWITTER = "TWITTER";

	// this custom callback url launches the Privly android application after
	// twitter authorisation
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

						if (TwitterHelperMethods.saveTwitterAccessTokenValues(
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
	 * object with AccessToken, ConsumerKey and Consumer Secret.
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
	volatile Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case THREAD_STARTING :
					progressDialog
							.setMessage("Checking for new Privly links from your Twitter timeline..");
					progressDialog.show();
					break;

				case THREAD_COMPLETE :
					progressDialog.dismiss();
					Toast.makeText(
							getApplicationContext(),
							String.valueOf(numberOfLinks)
									+ " new Privly links fetched from your Twitter timeline",
							Toast.LENGTH_LONG).show();
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
									numberOfLinks++;
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