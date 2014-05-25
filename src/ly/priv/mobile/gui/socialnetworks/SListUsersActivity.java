package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ly.priv.mobile.Login;
import ly.priv.mobile.R;
import ly.priv.mobile.Settings;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
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

public class SListUsersActivity extends SherlockFragment {
	private static final String TAG = "SListUsersActivity";
	private ArrayList<SUser> mListUserMess;
	private ListUsersAdapter mListUserMessagesAdapter;
	private ListView mListViewUsers;
	private ProgressBar mProgressBar;
	private Session mSession;
	private String mFaceBookUserId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Facebook);
		this.mListViewUsers = ((ListView) view.findViewById(R.id.lView));
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mSession = Session.getActiveSession();
		mFaceBookUserId =Utilities.getFacebookID(getActivity());
		mListUserMess =new ArrayList<SUser>();
		login();

	

		mListViewUsers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// FragmentTransaction transaction = getActivity()
				// .getSupportFragmentManager().beginTransaction();
				// SListUserMessagesActivity sListUserMessagesActivity = new
				// SListUserMessagesActivity();
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("UserMessages",
				// mListUserMess.get(position).getListUserMess());
				// sListUserMessagesActivity.setArguments(bundle);
				// transaction.replace(R.id.container,
				// sListUserMessagesActivity);
				// transaction.disallowAddToBackStack();
				// transaction.commit();

			}
		});
		return view;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode,
				resultCode, data);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
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
		if (mSession == null) {
			mSession = new Session.Builder(getActivity()).build();
			Session.setActiveSession(mSession);
			if (!mSession.isOpened()) {
				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add("read_mailbox");
				Session.OpenRequest openRequest = new Session.OpenRequest(this);
				openRequest
						.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
				openRequest
						.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
				openRequest.setPermissions(permissions);
				mSession.openForRead(openRequest);				
				if (mSession.isOpened()) {
					getInboxFromFaceBook();
				}
			} else {
				getInboxFromFaceBook();
			}

		} else {
			getInboxFromFaceBook();
		}
	}

	/**
	 * Get inbox from FaceBook
	 */

	private void getInboxFromFaceBook() {
		Log.d(TAG, "getInboxFromFaceBook");

		mProgressBar.setVisibility(View.VISIBLE);
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Bundle params = new Bundle();
		params.putString("fields", "id,to.fields(id,name,picture),comments.order(chronological).limit(1)");
		//params.putString("limit", "1");
		Request request = Request.newGraphPathRequest(mSession, "me/inbox",
				new Request.Callback() {			
					@Override
					public void onCompleted(Response response) {												
						if (response.getError() != null) {							
							mProgressBar.setVisibility(View.INVISIBLE);
							AlertDialog dialog = Utilities.showDialog(getActivity(), getString(R.string.error_inbox));
							dialog.show();
							return;
						}						
						JSONArray listUsersWIthLastMessage = null;		
						try {							
							listUsersWIthLastMessage = response.getGraphObject()
									.getInnerJSONObject().getJSONArray("data");							
							
							for (int i = 0; i < listUsersWIthLastMessage.length(); i++) {
								SUser sUser =new SUser();
								JSONObject dialog = listUsersWIthLastMessage.getJSONObject(i);
								sUser.setDialogId(dialog.getString("id"));
								sUser.setTime(dialog.getString("updated_time"));								
								JSONArray to =dialog.getJSONObject("to").getJSONArray("data");
								for (int j = 0; j < to.length(); j++) {
									JSONObject oTo=to.getJSONObject(j);
									String id =oTo.getString("id");									
									if (!id.equals(mFaceBookUserId)){									
										sUser.setUserName(oTo.getString("name"));
										JSONObject pric =oTo.getJSONObject("picture").getJSONObject("data");
										sUser.setUrlToAvatar(pric.getString("url"));
										break;
									}
									
								}
								JSONObject comment =dialog.getJSONObject("comments").getJSONArray("data").getJSONObject(0);
								sUser.setLastUserMess(comment.getString("message"));
								mListUserMess.add(sUser);	
								Log.d(TAG, sUser.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (mListUserMess != null) {
							mListUserMessagesAdapter = new ListUsersAdapter(getActivity(),
									mListUserMess);
							mListViewUsers.setAdapter(mListUserMessagesAdapter);
						}
						mProgressBar.setVisibility(View.INVISIBLE);
					}
				});
		request.setParameters(params);
		request.executeAsync();
	}
}