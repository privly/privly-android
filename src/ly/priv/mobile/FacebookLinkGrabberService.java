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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		setContentView(R.layout.facebook_link_grabber_service);
		context = getApplicationContext();
		progressDialog = new ProgressDialog(this);

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
		Exception exception = new Exception();
		statusCallback.call(Session.getActiveSession(), Session
				.getActiveSession().getState(), exception);
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
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

	/**
	 * Handles for the new thread spawned. Maintains progressDialog while
	 * fetching inbox messages from Facebook.
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case THREAD_STARTING :

					break;

				case THREAD_COMPLETE :

					Bundle bundle = new Bundle();
					bundle.putString("contentSource", "FACEBOOK");
					Intent showContentIntent = new Intent(
							FacebookLinkGrabberService.this, ShowContent.class);
					// showContentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					showContentIntent.putExtras(bundle);
					startActivity(showContentIntent);
					FacebookLinkGrabberService.this.finish();

			}
		}
	};

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			globalSession = session;
			Log.d("accesstoken", session.getAccessToken().toString());

			if (session.getAccessToken() != null) {

				// Since fetching of data from Facebook server and parsing it to
				// find Privly links is a high latency procedure, a new thread
				// is spawned
				new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = Message.obtain();
						msg.what = THREAD_STARTING;
						handler.sendMessage(msg);
//						progressDialog
//								.setMessage("Checking for new links from Facebook Inbox");
//						progressDialog.show();
						try {
							String messageId = null;
							HttpClient client = new DefaultHttpClient();

							HttpGet get = new HttpGet(URL_PREFIX_FRIENDS
									+ globalSession.getAccessToken());
							HttpResponse responseGet = client.execute(get);
							HttpEntity resEntityGet = responseGet.getEntity();

							if (resEntityGet != null) {
								fbResponse = EntityUtils.toString(resEntityGet);
							}
							JSONArray messagesArray = fetchJsonMessagesArrayFromFacebookResponse(fbResponse);
							int lengthMessagesArray = messagesArray.length();
							for (int j = 0; j < lengthMessagesArray; j++) {
								JSONObject messageObject = messagesArray
										.getJSONObject(j);
								String message = null;
								if (messageObject.has("message")) {
									message = messageObject
											.getString("message");
									messageId = messageObject.getString("id");
									Log.d("messageId", messageId);
								}

								JSONObject fromObject = messageObject
										.getJSONObject("from");
								String userName = fromObject.getString("name");
								ArrayList<String> listOfUrls = Utilities
										.fetchPrivlyUrls(message);

								if (!listOfUrls.isEmpty()) {
									Iterator<String> iter = listOfUrls
											.iterator();
									while (iter.hasNext()) {
										String url = iter.next();
										if (!Utilities.ifLinkExists(
												getApplicationContext(), url,
												SOURCE_FACEBOOK)) {

											Utilities.insertIntoDb(context,
													SOURCE_FACEBOOK, url,
													messageId, userName);
										}

									}
								}

							}

							Message msgFinal = Message.obtain();
							msgFinal.what = THREAD_COMPLETE;
//							progressDialog.hide();
							handler.sendMessage(msgFinal);

						}

						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();

			}
		}

		/**
		 * Parse theFacebook response to a JSONArray containing messages.
		 *
		 * @param {String} response Facebook reply on making a GET Request for
		 *        inbox
		 * @return {JSONArray} messagesArray
		 */
		JSONArray fetchJsonMessagesArrayFromFacebookResponse(String response) {
			try {
				JSONArray fbDataArray = null;
				JSONArray messagesArray = null;
				JSONObject conversation = null;
				JSONObject comments = null;
				JSONObject fbInbox = new JSONObject(response);

				if (fbInbox.has("data")) {
					fbDataArray = fbInbox.getJSONArray("data");
					int lengthDataArray = fbDataArray.length();
					for (int i = 0; i < lengthDataArray; i++) {
						conversation = fbDataArray.getJSONObject(i);
						if (conversation.has("comments")) {
							comments = new JSONObject(conversation.get(
									"comments").toString());
							if (comments.has("data")) {
								messagesArray = comments.getJSONArray("data");
								return messagesArray;
							}

						}

					}

				}
				return messagesArray;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}