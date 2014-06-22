package ly.priv.mobile.grabbers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import ly.priv.mobile.api.gui.socialnetworks.ISocialNetworks;
import ly.priv.mobile.api.gui.socialnetworks.ListUsersFragment;
import ly.priv.mobile.api.gui.socialnetworks.SMessage;
import ly.priv.mobile.api.gui.socialnetworks.SUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Fragment for login,logout get inbox and list of messages for chose dialog id
 * For FaceBook
 * <p>
 * <ul>
 * <li>Creates a new Facebook Session.</li>
 * <li>Needs 'read_mailbox' permission from the user.</li>
 * <li>Makes an Async GET Request to graph url with the Facebook access token.</li>
 * <li>Parses the received json response.</li>
 * <li>Implement interface ISocialNetworks.</li>
 * </ul>
 * </p>
 * <p>
 * Dependencies :
 * <ul>
 * <li>/privly-android/libs/gson.jar</li>
 * <li>FaceBookSDK</li>
 * </ul>
 * </p>
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class FaceBookGrabberService extends SherlockFragment implements
		ISocialNetworks {
	private static final String TAG = FaceBookGrabberService.class.getName();
	private ArrayList<SUser> mListUserMess;
	private String mFaceBookUserId;
	private Session mSession;
	private Values mValues;
	private Session.StatusCallback mSessionStatusCallback;
	private ListUsersFragment mSListUsersActivity;
	private ProgressBar mProgressBar;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "Creating " + TAG);
		View view = inflater.inflate(R.layout.activity_list, container, false);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Login_Facebook);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mProgressBar.setVisibility(View.VISIBLE);
		mValues = new Values(getActivity());
		mSessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};
		login();
		return view;
	}

	/**
	 * Run social GUI
	 */
	private void runSocialGui() {
		Log.d(TAG, "runSocialGui");
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		mSListUsersActivity = new ListUsersFragment();		
		mSListUsersActivity.setISocialNetworks(this);
		transaction.replace(R.id.container, mSListUsersActivity);
		//transaction.disallowAddToBackStack();
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/**
	 * Login in FaceBook
	 */
	private void login() {
		Log.d(TAG, "login");
		mSession = Session.getActiveSession();
		if (mSession == null) {
			Log.d(TAG, "mSession == null");
			mSession = new Session.Builder(getActivity()).build();
			Session.setActiveSession(mSession);
			if (!mSession.isOpened()) {
				Log.d(TAG, "!mSession.isOpened()");
				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add("read_mailbox");
				mSession.addCallback(mSessionStatusCallback);
				Session.OpenRequest openRequest = new Session.OpenRequest(
						FaceBookGrabberService.this);
				openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
				openRequest
						.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
				openRequest.setPermissions(permissions);
				mSession.openForRead(openRequest);
			} else {
				runSocialGui();
			}
		} else {
			runSocialGui();
		}
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	/**
	 * this method is used by the facebook API
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode,
				resultCode, data);
	}
	
	/**
	 * Manages the session state change. This method is called after the
	 * <code>login</code> method.
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if (session != mSession) {
			return;
		}

		if (state.isOpened()) {
			// Log in just happened.
			Log.d(TAG, "session opened");
			if (mValues.getFacebookID().equals("")) {
				makeMeRequest();
			} else {
				runSocialGui();
			}
		} else if (state.isClosed()) {
			// Log out just happened. Update the UI.
			Log.d(TAG, "session closed");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	/**
	 * Method for get information about me
	 */
	private void makeMeRequest() {
		Log.d(TAG, "makeMeRequest");
		mProgressBar.setVisibility(View.VISIBLE);
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Bundle params = new Bundle();
		params.putString("fields", "id");
		Request request = Request.newMeRequest(mSession,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (response.getError() != null) {
							mProgressBar.setVisibility(View.INVISIBLE);
							AlertDialog dialog = Utilities.showDialog(
									getActivity(),
									getString(R.string.error_inbox));
							dialog.show();
							return;
						}
						if (user != null) {

							mValues.setFacebookID(user.getId());
						}
						mProgressBar.setVisibility(View.INVISIBLE);
						runSocialGui();
					}
				});
		request.setParameters(params);
		request.executeAsync();

	}

	@Override
	public ArrayList<SUser> getListOfUsers() {
		Log.d(TAG, "getListOfUsers");
		mListUserMess = new ArrayList<SUser>();
		// mProgressBar.setVisibility(View.VISIBLE);
		mFaceBookUserId = mValues.getFacebookID();
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Bundle params = new Bundle();
		params.putString("fields",
				"id,to.fields(id,name,picture),comments.order(chronological).limit(1)");
		// params.putString("limit", "1");
		Request request = Request.newGraphPathRequest(mSession, "me/inbox",
				null);
		request.setParameters(params);
		Response response = request.executeAndWait();
		if (response.getError() != null) {
			Log.e(TAG, response.getError().getErrorMessage());
			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog dialog = Utilities.showDialog(
								getActivity(), getString(R.string.error_inbox));
						dialog.show();
					}
				});
			return mListUserMess;
		}
		JSONArray listUsersWIthLastMessage = null;
		try {
			listUsersWIthLastMessage = response.getGraphObject()
					.getInnerJSONObject().getJSONArray("data");

			for (int i = 0; i < listUsersWIthLastMessage.length(); i++) {
				SUser sUser = new SUser();
				JSONObject dialog = listUsersWIthLastMessage.getJSONObject(i);
				sUser.setDialogId(dialog.getString("id"));
				sUser.setTime(Utilities.getTimeForFacebook(dialog
						.getString("updated_time")));
				JSONArray to = dialog.getJSONObject("to").getJSONArray("data");
				for (int j = 0; j < to.length(); j++) {
					JSONObject oTo = to.getJSONObject(j);
					String id = oTo.getString("id");
					if (!id.equals(mFaceBookUserId)) {
						sUser.setUserName(oTo.getString("name"));
						JSONObject pic = oTo.getJSONObject("picture")
								.getJSONObject("data");
						sUser.setUrlToAvatar(pic.getString("url"));
						break;
					}

				}
				JSONObject comment = dialog.getJSONObject("comments")
						.getJSONArray("data").getJSONObject(0);
				sUser.setLastUserMess(comment.getString("message"));
				mListUserMess.add(sUser);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mListUserMess;
	}

	@Override
	public Map<String, Object> getListOfMessages(String dialogID) {
		Log.d(TAG, "getListOfMessagesFromFaceBook");
		String nextUrlForLoadingMessages = "";
		ArrayList<SMessage> listOfUsersMessage = new ArrayList<SMessage>();
		Map<String, Object> res = new HashMap<String, Object>();
		Bundle params = new Bundle();
		params.putString("fields",
				"comments.fields(from.fields(id,picture),message,created_time)");
		// params.putString("limit", "1");
		Request request = Request.newGraphPathRequest(mSession, dialogID, null);
		request.setParameters(params);
		Response response = request.executeAndWait();
		if (response.getError() != null) {
			Log.e(TAG, response.getError().getErrorMessage());
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					mProgressBar.setVisibility(View.INVISIBLE);
					AlertDialog dialog = Utilities.showDialog(getActivity(),
							getString(R.string.error_inbox));
					dialog.show();
				}
			});
			return null;
		}
		GraphObject graphObject = response.getGraphObject();
		try {
			JSONObject jsonObjectComments = graphObject.getInnerJSONObject()
					.getJSONObject("comments");
			JSONArray comments = jsonObjectComments.getJSONArray("data");
			Gson gson = new Gson();
			Type collectionType = new TypeToken<List<SMessage>>() {
			}.getType();
			listOfUsersMessage = gson.fromJson(comments.toString(),
					collectionType);
			nextUrlForLoadingMessages = jsonObjectComments.getJSONObject(
					"paging").getString("next");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		res.put("Array", listOfUsersMessage);
		res.put("NextLink", nextUrlForLoadingMessages);
		return res;
	}

	@Override
	public Map<String, Object> fetchNextMessages(String url) {
		String nextUrlForLoadingMessages = "";
		ArrayList<SMessage> listOfUsersMessage = new ArrayList<SMessage>();
		Map<String, Object> res = new HashMap<String, Object>();
		String fbResponse = "";
		try {
			// Make GET Request
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			if (resEntityGet != null) {
				fbResponse = EntityUtils.toString(resEntityGet);

			}
			JSONObject jsonObjectComments = new JSONObject(fbResponse);
			JSONArray comments = jsonObjectComments.getJSONArray("data");
			if (comments.length() != 0) {
				Gson gson = new Gson();
				Type collectionType = new TypeToken<List<SMessage>>() {
				}.getType();
				listOfUsersMessage = gson.fromJson(comments.toString(),
						collectionType);
				nextUrlForLoadingMessages = jsonObjectComments.getJSONObject(
						"paging").getString("next");
			} else {
				listOfUsersMessage = null;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		res.put("Array", listOfUsersMessage);
		res.put("NextLink", nextUrlForLoadingMessages);
		return res;
	}

	@Override
	public void logout() {
		mSession.closeAndClearTokenInformation();
		mSession = null;
		Session.setActiveSession(mSession);
		mValues.setFacebookID("");
		login();
	}

}
