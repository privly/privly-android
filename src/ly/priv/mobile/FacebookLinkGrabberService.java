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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_link_grabber_service);

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

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case THREAD_STARTING:
				progressDialog
						.setMessage("Checking for new links from Facebook Inbox");
				progressDialog.show();
				Log.d("start", "start");
				break;

			case THREAD_COMPLETE:
				progressDialog.hide();
				Log.d("omplete", "complete");
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
				new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = Message.obtain();
						msg.what = THREAD_STARTING;
						handler.sendMessage(msg);
						try {
							HttpClient client = new DefaultHttpClient();

							HttpGet get = new HttpGet(URL_PREFIX_FRIENDS
									+ globalSession.getAccessToken());
							HttpResponse responseGet = client.execute(get);
							HttpEntity resEntityGet = responseGet.getEntity();

							if (resEntityGet != null) {
								// do something with the response
								fbResponse = EntityUtils.toString(resEntityGet);
								Log.d("fbResponse", fbResponse.toString());
							}

							try {
								JSONObject fbInbox = new JSONObject(fbResponse);
								JSONArray fbDataArray = fbInbox
										.getJSONArray("data");
								int lengthDataArray = fbDataArray.length();
								for (int i = 0; i < lengthDataArray; i++) {
									JSONObject conversation = fbDataArray
											.getJSONObject(i);
									// Log.d("TAG", conversation.toString());
									if (conversation.has("comments")) {
										JSONObject comments = new JSONObject(
												conversation.get("comments")
														.toString());
										if (comments.has("data")) {
											JSONArray messagesArray = comments
													.getJSONArray("data");
											// Log.d("messageArray",
											// messagesArray.toString());
											int lengthMessagesArray = messagesArray
													.length();
											for (int j = 0; j < lengthMessagesArray; j++) {
												JSONObject messageObject = messagesArray
														.getJSONObject(j);
												// Log.d("message",
												// messageObject.toString());
												String message = null;
												if (messageObject
														.has("message")) {
													message = messageObject
															.getString("message");
												}

												String fbId = messageObject
														.getString("id");
												JSONObject fromObject = messageObject
														.getJSONObject("from");
												String fromName = fromObject
														.getString("name");
												Log.d("message", fromName
														+ " : " + message);
											}

										}
										Message msgFinal = Message.obtain();
										msgFinal.what = THREAD_COMPLETE;
										handler.sendMessage(msgFinal);

									}

								}
							} catch (Exception exception) {
								exception.printStackTrace();
							}

						} catch (Exception e) {
							Log.d("http_error", e.toString());
						} finally {

						}

					}
				}).start();

			}

		}
	}
}