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
 * <p>
 * Dependencies :
 * <ul>
 * <li>/privly-android/libs/gson.jar</li>
 * <li>/privly-android/libs/android-support-v4.jar</li>
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
	private Session mSession;
	private ISocialNetworks mISocialNetworks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		setHasOptionsMenu(true);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Facebook);
		mListViewUsers = ((ListView) view.findViewById(R.id.lView));
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mProgressBar.setVisibility(View.VISIBLE);
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
		mListUserMess = mISocialNetworks.getListOfUsers();
		mListUserMessagesAdapter = new ListUsersAdapter(getActivity(),
				mListUserMess);
		mListViewUsers.setAdapter(mListUserMessagesAdapter);
		mProgressBar.setVisibility(View.INVISIBLE);
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
			// login();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * @param mISocialNetworks
	 *            the mISocialNetworks to set
	 */
	public void setmISocialNetworks(ISocialNetworks mISocialNetworks) {
		this.mISocialNetworks = mISocialNetworks;
	}

	/**
	 * @return the mProgressBar
	 */
	public void setProgressBarStatus(int v) {
		mProgressBar.setVisibility(v);
	}

}