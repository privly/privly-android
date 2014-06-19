package ly.priv.mobile.api.gui.socialnetworks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Showing messages in chose dialog
 * <p>
 * <ul>
 * <li>Get mDialogID from Bundle.</li>
 * <li>Get Facebook Session.</li>
 * <li>Makes a Request.newGraphPathRequest to graph api with the Facebook access
 * token.</li>
 * <li>Parses the received json response with Gson library</li>
 * <li>If privly link contained in message then Redirect User to
 * {@link ly.priv.mobile.ShowContent} ShowContent Activity</li>
 * </ul>
 * </p>
 * 
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
public class ListUserMessagesFragment extends SherlockFragment implements
		OnRefreshListener {
	private static final String TAG = "SListUserMessagesActivity";
	private ArrayList<SMessage> mListUserMess;
	private ListUserMessagesAdapter mListUserMessagesAdapter;
	private ListView mListViewUserMessages;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressBar mProgressBar;
	private String mDialogID;
	private String mNextUrlForLoadingMessages;
	private Boolean mflNoMoreMessage = false;
	private ISocialNetworks mISocialNetworks;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list_pull_refrash,
				container, false);
		mListViewUserMessages = ((ListView) view
				.findViewById(R.id.lView_refresh));
		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mProgressBar = (ProgressBar) view
				.findViewById(R.id.pbLoadingData_refresh);
		mProgressBar.setVisibility(View.VISIBLE);
		mDialogID = getArguments().getString("DialogID");
		mListViewUserMessages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayList<String> listOfUrls = Utilities
						.fetchPrivlyUrls(mListUserMess.get(position)
								.getMessage());
				if (listOfUrls.size() > 0) {
					FragmentTransaction transaction = getActivity()
							.getSupportFragmentManager().beginTransaction();
					ShowContent showContent = new ShowContent();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("listOfLinks", listOfUrls);
					showContent.setArguments(bundle);
					transaction.replace(R.id.container, showContent);
					transaction.addToBackStack(null);
					transaction.commit();
				} else {
					Toast.makeText(getActivity(),
							R.string.message_not_containe_privly_link,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		new getData().execute();
		return view;
	}

	private class getData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			HashMap<String, Object> res = (HashMap<String, Object>) mISocialNetworks
					.getListOfMessages(mDialogID);
			if (res != null) {
				mListUserMess = (ArrayList<SMessage>) res.get("Array");
				mNextUrlForLoadingMessages = (String) res.get("NextLink");
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			mListUserMessagesAdapter = new ListUserMessagesAdapter(
					getActivity(), mListUserMess);
			mListViewUserMessages.setAdapter(mListUserMessagesAdapter);
			mListViewUserMessages.setSelection(mListUserMessagesAdapter
					.getCount() - 1);
			mProgressBar.setVisibility(View.INVISIBLE);
			super.onPostExecute(result);
		}

	}

	
	/**
	 * AsyncTask for getting next messages for current DialogId
	 * 
	 * @author Ivan Metla e-mail: metlaivan@gmail.com
	 * 
	 */
	private class FetchFaceBookNextMessages extends
			AsyncTask<String, Void, ArrayList<SMessage>> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected ArrayList<SMessage> doInBackground(String... urls) {
			HashMap<String, Object> res = (HashMap<String, Object>) mISocialNetworks
					.fetchNextMessages(urls[0]);
			ArrayList<SMessage> sMessages =null;
			if (res != null) {
				sMessages = (ArrayList<SMessage>) res.get("Array");
				mNextUrlForLoadingMessages = (String) res.get("NextLink");
			}
			return sMessages;
		}

		@Override
		protected void onPostExecute(ArrayList<SMessage> result) {
			if (result != null) {
				mflNoMoreMessage=false;
				Integer pos = result.size() - 1;
				result.addAll(mListUserMess);
				mListUserMess = result;
				mListUserMessagesAdapter = new ListUserMessagesAdapter(
						getActivity(), mListUserMess);
				mListViewUserMessages.setAdapter(mListUserMessagesAdapter);
				mListViewUserMessages.setSelection(pos);
			} else {
				Toast.makeText(getActivity(), R.string.no_more_messages,
						Toast.LENGTH_SHORT).show();
				mflNoMoreMessage=true;
			}
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh
	 * ()
	 */
	@Override
	public void onRefresh() {
		Log.d(TAG, "onRefresh for SwipeRefreshLayout");
		if (!mflNoMoreMessage) {
			FetchFaceBookNextMessages faceBookNextMessages = new FetchFaceBookNextMessages();
			faceBookNextMessages.execute(mNextUrlForLoadingMessages);
		} else {
			Toast.makeText(getActivity(), R.string.no_more_messages,
					Toast.LENGTH_SHORT).show();
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

	/**
	 * @param mISocialNetworks
	 *            the mISocialNetworks to set
	 */
	public void setmISocialNetworks(ISocialNetworks mISocialNetworks) {
		this.mISocialNetworks = mISocialNetworks;
	}
}