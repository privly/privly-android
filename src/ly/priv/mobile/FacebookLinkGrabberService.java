package ly.priv.mobile;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;

/**
 * Authenticates user with Facebook and grabs Privly links from message inbox.
 * <p>
 * <ul>
 * <li>Creates a new Facebook Session.</li>
 * <li>Needs 'read_mailbox' permission from the user.</li>
 * <li>Makes an Async GET Request to graph url with the Facebook access token.</li>
 * <li>Parses the received json response and inserts any new Privly links to the
 * local database.</li>
 * <li>Redirect User to {@link ly.priv.mobile.ShowContent} ShowContent Activity</li>
 * </ul>
 * </p>
 *
 * @author Shivam Verma
 *
 */
public class FacebookLinkGrabberService extends SherlockFragment {
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/inbox?access_token=";
	String fbResponse = "";
	Session globalSession;
	final int THREAD_STARTING = 0;
	final int THREAD_COMPLETE = 1;
	ProgressDialog progressDialog;
	boolean pendingRequest;
	String contentServerDomain;
	Context context;
	Session session;
	final String SOURCE_FACEBOOK = "FACEBOOK";
	SherlockFragment current;
	
	public FacebookLinkGrabberService(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.link_grabber_service, container, false);
		context = getActivity();
		current = this;
		Log.d("OnCreateView", "OnCreateView");
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setCanceledOnTouchOutside(false);
		session = Session.getActiveSession();
		if (session == null) {
			Log.d("Session", "null");
			session = new Session.Builder(getActivity()).build();
			Session.setActiveSession(session);
			if (!session.isOpened()) {
				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add("read_mailbox");
				session.openForRead(new OpenRequest(this).setPermissions(
						permissions).setLoginBehavior(
						SessionLoginBehavior.SSO_WITH_FALLBACK));
				if (session.isOpened()) {
					FetchFbMessages task = new FetchFbMessages();
					task.execute();
				}
			} else {
				FetchFbMessages task = new FetchFbMessages();
				task.execute();
			}

		} else {
			FetchFbMessages task = new FetchFbMessages();
			task.execute();
		}
		return view;
	}
//	@Override
//	public void onResume() {
//		super.onResume();
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode,
				resultCode, data);
		Log.d("OnActivityResult", "Log");
		FetchFbMessages task = new FetchFbMessages();
		task.execute();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
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
				getActivity());
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
			try {
				JSONArray fbDataArray = null;
				JSONObject fbInbox = null;
				fbInbox = new JSONObject(fbResponse);
				Log.d("fbResponse", fbResponse);
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
																	getActivity(),
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
			dialog.dismiss();
			Bundle bundle = new Bundle();
			bundle.putString("contentSource", "FACEBOOK");

			// Redirect user to {@link ly.priv.mobile.ShowContent} ShowContent
			// Class
			Fragment showContent = new ShowContent();
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			showContent.setArguments(bundle);
			transaction.replace(R.id.container, showContent);
			transaction.commit();
			Log.d("fragments","showContent");
			//Intent showContentIntent = new Intent(
			//		getActivity(), ShowContent.class);
			//showContentIntent.putExtras(bundle);
			//startActivity(showContentIntent);
			// Clear this activity from stack so that the user is taken
			// to the Home Screen on back press
			//FacebookLinkGrabberService.this.finish();
		}
	}

}