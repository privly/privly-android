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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

	final String SOURCE_FACEBOOK = "FACEBOOK";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.link_grabber_service);
		context = getApplicationContext();
		Utilities.copyDb();
		Log.d("OnCreate", "OnCreate");
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		Session session = Session.getActiveSession();

		if (session == null) {
			session = openActiveSession(this, true, statusCallback,
					Arrays.asList("read_mailbox"));
		}
		Session.setActiveSession(session);
		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
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
	 * programatically. Skips the usage of Facebook Login button
	 *
	 * @param activity
	 * @param allowLoginUI
	 * @param callback
	 * @param permissions
	 * @return
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

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			globalSession = session;

			if (session.getAccessToken() != null) {

				// Since fetching of data from Facebook server and parsing it to
				// find Privly links is a high latency procedure, a new thread
				// is spawned
				FetchFbMessages task = new FetchFbMessages();
				task.execute();

			}
		}
	}

	private class FetchFbMessages extends AsyncTask<String, Void, String> {

		ProgressDialog dialog = new ProgressDialog(
				FacebookLinkGrabberService.this);
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Logging in..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(URL_PREFIX_FRIENDS
						+ globalSession.getAccessToken());
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
			dialog.dismiss();
			// Toast.makeText(getApplicationContext(),loginResponse ,
			// Toast.LENGTH_LONG).show();
			try {
				JSONArray fbDataArray = null;
				JSONObject fbInbox = null;
				fbInbox = new JSONObject(fbResponse);
				Log.d("fbInbox", fbInbox.toString());
				if (fbInbox.has("data")) {
					fbDataArray = fbInbox.getJSONArray("data");
					Log.d("fbDataArray", fbDataArray.toString());
					for (int i = 0; i < fbDataArray.length(); i++) {
						JSONObject allMessagesObject = fbDataArray
								.getJSONObject(i);
						if (allMessagesObject.has("comments")) {
							JSONObject commentsObject = new JSONObject(
									allMessagesObject.get("comments")
											.toString());
							Log.d("commentsObject", commentsObject.toString());
							if (commentsObject.has("data")) {
								JSONArray chatArray = commentsObject
										.getJSONArray("data");
								Log.d("chatArray", chatArray.toString());
								for (int j = 0; j < chatArray.length(); j++) {
									JSONObject messageObject = chatArray
											.getJSONObject(j);
									Log.d("messageObject",
											messageObject.toString());
									if (messageObject.has("message")) {
										String message = messageObject
												.getString("message");
										Log.d("message", message);
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
													Log.d("URL", url);
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
			Bundle bundle = new Bundle();

			bundle.putString("contentSource", "FACEBOOK");
			Intent showContentIntent = new Intent(
					FacebookLinkGrabberService.this, ShowContent.class);
			showContentIntent.putExtras(bundle);
			startActivity(showContentIntent); // Clear this activity
			// from stack so that the user is taken
			// to the Home Screen on back press
			FacebookLinkGrabberService.this.finish();
		}
	}

}