package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

/**
 * Fragment for showing dialogs with last message in dialog
 * <p>
 * <ul>
 * <li>Creates a new Facebook Session.</li>
 * <li>Needs 'read_mailbox' permission from the user.</li>
 * <li>Makes an Async GET Request to graph url with the Facebook access token.</li>
 * <li>Parses the received json response and showing in mListViewUsers.</li>
 * <li>Redirect User to {@link ly.priv.mobile.gui.SListUserMessagesActivity}
 * SListUserMessagesActivity Fragment</li>
 * </ul>
 * </p>
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class SListUsersActivity extends SherlockFragment {
	private static final String TAG = "SListUsersActivity";
	private ArrayList<SUser> mListUserMess;
	private ListUsersAdapter mListUserMessagesAdapter;
	private ListView mListViewUsers;
	private ProgressBar mProgressBar;
	private String mFaceBookUserId;
	private Session mSession;
	private Values mValues;
	private Session.StatusCallback mSessionStatusCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Facebook);
		mListViewUsers = ((ListView) view.findViewById(R.id.lView));
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mValues = new Values(getActivity());
		mFaceBookUserId = mValues.getFacebookID();
		mListViewUsers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				SListUserMessagesActivity sListUserMessagesActivity = new SListUserMessagesActivity();
				Bundle bundle = new Bundle();
				bundle.putString("DialogID", mListUserMess.get(position)
						.getDialogId());
				sListUserMessagesActivity.setArguments(bundle);
				transaction.replace(R.id.container, sListUserMessagesActivity);
				// transaction.disallowAddToBackStack();
				transaction.addToBackStack(null);
				transaction.commit();

			}
		});

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
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.layout.menu_layout_slistusers, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Item click listener for options menu.
	 * <p>
	 * relogin
	 * </p>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.logout:
			mSession.closeAndClearTokenInformation();
			mSession = null;
			Session.setActiveSession(mSession);
			login();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Login in FaceBook
	 */
	private void login() {
		mListUserMess = new ArrayList<SUser>();
		mListViewUsers.setAdapter(null);
		mSession = Session.getActiveSession();
		if (mSession == null) {
			mSession = new Session.Builder(getActivity()).build();
			Session.setActiveSession(mSession);
			if (!mSession.isOpened()) {
				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add("read_mailbox");
				mSession.addCallback(mSessionStatusCallback);
				Session.OpenRequest openRequest = new Session.OpenRequest(
						SListUsersActivity.this);
				openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
				openRequest
						.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
				openRequest.setPermissions(permissions);
				mSession.openForRead(openRequest);
			} else {
				getInboxFromFaceBook();
			}

		} else {
			getInboxFromFaceBook();
		}
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
			makeMeRequest();
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
	 * 
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
						getInboxFromFaceBook();
					}
				});
		request.setParameters(params);
		request.executeAsync();
	}

	/**
	 * Get inbox from FaceBook
	 */
	private void getInboxFromFaceBook() {
		Log.d(TAG, "getInboxFromFaceBook");
		mProgressBar.setVisibility(View.VISIBLE);
		mFaceBookUserId = mValues.getFacebookID();
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Bundle params = new Bundle();
		params.putString("fields",
				"id,to.fields(id,name,picture),comments.order(chronological).limit(1)");
		// params.putString("limit", "1");
		Request request = Request.newGraphPathRequest(mSession, "me/inbox",
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						if (response.getError() != null) {
							Log.e(TAG, response.getError().getErrorMessage());
							mProgressBar.setVisibility(View.INVISIBLE);
							AlertDialog dialog = Utilities.showDialog(
									getActivity(),
									getString(R.string.error_inbox));
							dialog.show();
							return;
						}
						JSONArray listUsersWIthLastMessage = null;
						try {
							listUsersWIthLastMessage = response
									.getGraphObject().getInnerJSONObject()
									.getJSONArray("data");

							for (int i = 0; i < listUsersWIthLastMessage
									.length(); i++) {
								SUser sUser = new SUser();
								JSONObject dialog = listUsersWIthLastMessage
										.getJSONObject(i);
								sUser.setDialogId(dialog.getString("id"));
								sUser.setTime(Utilities.getTime(dialog
										.getString("updated_time")));
								JSONArray to = dialog.getJSONObject("to")
										.getJSONArray("data");
								for (int j = 0; j < to.length(); j++) {
									JSONObject oTo = to.getJSONObject(j);
									String id = oTo.getString("id");
									if (!id.equals(mFaceBookUserId)) {
										sUser.setUserName(oTo.getString("name"));
										JSONObject pic = oTo.getJSONObject(
												"picture")
												.getJSONObject("data");
										sUser.setUrlToAvatar(pic
												.getString("url"));
										break;
									}

								}
								JSONObject comment = dialog
										.getJSONObject("comments")
										.getJSONArray("data").getJSONObject(0);
								sUser.setLastUserMess(comment
										.getString("message"));
								mListUserMess.add(sUser);
								// Log.d(TAG, sUser.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (mListUserMess != null) {
							Log.d(TAG, "set adapter");
							mListUserMessagesAdapter = new ListUsersAdapter(
									getActivity(), mListUserMess);
							mListViewUsers.setAdapter(mListUserMessagesAdapter);
						}
						mProgressBar.setVisibility(View.INVISIBLE);
					}
				});
		request.setParameters(params);
		request.executeAsync();
	}

}