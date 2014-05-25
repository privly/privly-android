package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.Login;
import ly.priv.mobile.R;
import ly.priv.mobile.Settings;
import ly.priv.mobile.Values;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;

public class SListUsersActivity extends SherlockFragment {
	private static final String TAG = "SListUsersActivity";
	private ArrayList<SUser> mListUserMess;
	private ListUsersAdapter mListUserMessagesAdapter;
	private ListView mListViewUsers;
	private Session mSession;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Facebook);
		this.mListViewUsers = ((ListView) view.findViewById(R.id.lView));
		mSession = Session.getActiveSession();
		login();

		if (this.mListUserMess != null) {
			this.mListUserMessagesAdapter = new ListUsersAdapter(getActivity(),
					this.mListUserMess);
			this.mListViewUsers.setAdapter(this.mListUserMessagesAdapter);
		}

		mListViewUsers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				SListUserMessagesActivity sListUserMessagesActivity = new SListUserMessagesActivity();
				Bundle bundle = new Bundle();
				bundle.putSerializable("UserMessages",
						mListUserMess.get(position).getListUserMess());
				sListUserMessagesActivity.setArguments(bundle);
				transaction.replace(R.id.container, sListUserMessagesActivity);
				transaction.disallowAddToBackStack();
				transaction.commit();

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
			mSession=null;
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
					// FetchFbMessages task = new FetchFbMessages();
					// task.execute();
				}
			} else {
				// FetchFbMessages task = new FetchFbMessages();
				// task.execute();
			}

		} else {
			// FetchFbMessages task = new FetchFbMessages();
			// task.execute();
		}
	}
}