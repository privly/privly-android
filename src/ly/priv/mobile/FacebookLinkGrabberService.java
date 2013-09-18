package ly.priv.mobile;

import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Authenticates user with Facebook and grabs Privly links from message inbox.
 * <p>
 * <ul>
 * <li>Creates a new Facebook Session.</li>
 * <li>Needs 'read_mailbox' permission from the user.</li>
 * <li>Makes an Async GET Request to graph url with the Facebook access token.</li>
 * <li>Parses the received json response and inserts any new Privly links to the
 * local database.</li>
 * </ul>
 * </p>
 *
 * @author Shivam Verma
 *
 */
public class FacebookLinkGrabberService extends Activity {
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/inbox?access_token=";
	String fbResponse = "";
	Session globalSession;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	final int THREAD_STARTING = 0;
	final int THREAD_COMPLETE = 1;
	ProgressDialog progressDialog;
	boolean pendingRequest;
	String contentServerDomain;
	Context context;
	Session session;
	final String SOURCE_FACEBOOK = "FACEBOOK";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.link_grabber_service);
		context = getApplicationContext();
		Log.d("OnCreate", "OnCreate");
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		session = Session.getActiveSession();

		if (session == null) {
			// Set the permissions required for facebook access. 'read_mailbox'
			// in this case.
			Log.d("session = null", "true");
			session = openActiveSession(this, true, statusCallback,
					Arrays.asList("read_mailbox"));
			Session.setActiveSession(session);

		} else {
			Log.d("session", "not null");
			FetchFbMessages task = new FetchFbMessages();
			task.execute();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		Session.getActiveSession().addCallback(statusCallback);
		Log.d("OnResume", "OnResume");
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	/**
	 * Custom implementation of openActiveSession to ask for Read Permissions
	 * programatically. Skips the usage of Facebook Login button.
	 *
	 * @param activity
	 * @param allowLoginUI
	 * @param callback
	 * @param permissions
	 * @return null
	 */
	private static Session openActiveSession(Activity activity,
			boolean allowLoginUI, StatusCallback callback,
			List<String> permissions) {
		OpenRequest openRequest = new OpenRequest(activity).setPermissions(
				permissions).setCallback(callback);
		Session session = new Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())
				|| allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	/**
	 * Callback method. Executed on any change in session.
	 */
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {

			if (session.getAccessToken() != null) {

				// Since fetching of data from Facebook server and parsing it to
				// find Privly links is a high latency procedure, we use an
				// AsyncTask.
				FetchFbMessages task = new FetchFbMessages();
				task.execute();

			}
		}
	}

	/**
	 * Fetches new Privly links from message inbox.
	 * <p>
	 * <ul>
	 * <li>Make a get request to the Facebook Graph API with the session's
	 * access token to retrieve the last 25 inbox messages.</li>
	 * <li>Parse JSON response to search for Privly links.</li>
	 * <li>Insert new links into the local Db.</li>
	 * </ul>
	 * </p>
	 *
	 *
	 */
	private class FetchFbMessages extends AsyncTask<String, Void, String> {

		volatile ProgressDialog dialog = new ProgressDialog(
				FacebookLinkGrabberService.this);
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Checking for new Privly links from your Facebook inbox..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				// Make GET Request
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(URL_PREFIX_FRIENDS
						+ session.getAccessToken());
				HttpResponse responseGet = client.execute(get);
				HttpEntity resEntityGet = responseGet.getEntity();
				if (resEntityGet != null) {
					fbResponse = EntityUtils.toString(resEntityGet);

				}

			}

			catch (Exception e) {
				e.printStackTrace();
			}

			return fbResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			// Parse JSON to search for Privly links and then insert into Db if
			// new links found
			int numOfLinksAdded = 0;
			try {
				JSONArray fbDataArray = null;
				JSONObject fbInbox = null;
				fbInbox = new JSONObject(fbResponse);
				if (fbInbox.has("data")) {
					fbDataArray = fbInbox.getJSONArray("data");
					for (int i = 0; i < fbDataArray.length(); i++) {
						JSONObject allMessagesObject = fbDataArray
								.getJSONObject(i);
						if (allMessagesObject.has("comments")) {
							JSONObject commentsObject = new JSONObject(
									allMessagesObject.get("comments")
											.toString());
							if (commentsObject.has("data")) {
								JSONArray chatArray = commentsObject
										.getJSONArray("data");
								for (int j = 0; j < chatArray.length(); j++) {
									JSONObject messageObject = chatArray
											.getJSONObject(j);
									if (messageObject.has("message")) {
										String message = messageObject
												.getString("message");
										String messageId = messageObject
												.getString("id");
										if (messageObject.has("from")) {
											JSONObject from = messageObject
													.getJSONObject("from");
											String userName = from
													.getString("name");
											ArrayList<String> listOfUrls = Utilities
													.fetchPrivlyUrls(message);

											if (!listOfUrls.isEmpty()) {
												Iterator<String> iter = listOfUrls
														.iterator();
												while (iter.hasNext()) {
													String url = iter.next();
													if (!Utilities
															.ifLinkExistsInDb(
																	getApplicationContext(),
																	url,
																	SOURCE_FACEBOOK)) {

														Utilities
																.insertIntoDb(
																		context,
																		SOURCE_FACEBOOK,
																		url,
																		messageId,
																		userName);
														numOfLinksAdded++;
													}

												}
											}
										}

									}

								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Toast.makeText(
					getApplicationContext(),
					String.valueOf(numOfLinksAdded)
							+ " new Privly links fetched from your Facebook inbox",
					Toast.LENGTH_LONG).show();
			dialog.dismiss();
			Bundle bundle = new Bundle();
			bundle.putString("contentSource", "FACEBOOK");

			// Redirect user to ShowContent Class
			Intent showContentIntent = new Intent(
					FacebookLinkGrabberService.this, ShowContent.class);
			showContentIntent.putExtras(bundle);
			startActivity(showContentIntent);
			// Clear this activity from stack so that the user is taken
			// to the Home Screen on back press
			FacebookLinkGrabberService.this.finish();
		}
	}

}