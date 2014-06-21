package ly.priv.mobile.api.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;
import android.os.AsyncTask;
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

/**
 * Fragment for showing dialogs with last message in each dialog
 * <p>
 * When click on item of list redirect usesr to
 * {@link ly.priv.mobile.api.gui.socialnetworks.ListUserMessagesFragment}.
 * <p>
 * For using this api you must realize interface
 * {@link ly.priv.mobile.api.gui.socialnetworks.ISocialNetworks} and set it with
 * method <code>setISocialNetworks</code>
 * </p>
 * </p>
 * <p>
 * <b>Dependencies :</b>
 * <ul>
 * <li>/privly-android/libs/android-support-v4.jar</li>
 * </ul>
 * </p>
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class ListUsersFragment extends SherlockFragment {
	private static final String TAG = "SListUsersActivity";
	private ArrayList<SUser> mListUserMess;
	private ListUsersAdapter mListUserMessagesAdapter;
	private ListView mListViewUsers;
	private ProgressBar mProgressBar;
	private ISocialNetworks mISocialNetworks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "Creating ListUsersFragment");
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
				ListUserMessagesFragment sListUserMessagesActivity = new ListUserMessagesFragment();
				sListUserMessagesActivity.setmISocialNetworks(mISocialNetworks);
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
		new GetListOfUsers().execute();

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
			mISocialNetworks.logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Set Interface ISocialNetworks
	 * 
	 * @param mISocialNetworks
	 *            the mISocialNetworks to set
	 */
	public void setISocialNetworks(ISocialNetworks mISocialNetworks) {
		this.mISocialNetworks = mISocialNetworks;
	}

	/**
	 * AsyncTask for getting list of dialogs with last message in each dialog
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	private class GetListOfUsers extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mListUserMess = mISocialNetworks.getListOfUsers();
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			if (mListUserMess != null) {
				mListUserMessagesAdapter = new ListUsersAdapter(getActivity(),
						mListUserMess);
				mListViewUsers.setAdapter(mListUserMessagesAdapter);
			}
			mProgressBar.setVisibility(View.INVISIBLE);
			super.onPostExecute(result);
		}

	}
}