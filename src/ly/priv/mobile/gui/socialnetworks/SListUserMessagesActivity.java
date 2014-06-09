package ly.priv.mobile.gui.socialnetworks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

public class SListUserMessagesActivity extends SherlockFragment implements OnRefreshListener {

	private static final String TAG = "SListUserMessagesActivity";
	private ArrayList<SMessage> mListUserMess;
	private ListUserMessagesAdapter mListUserMessagesAdapter;
	private ListView mListViewUserMessages;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressBar mProgressBar;
	private Session mSession;
	private String mDialogID;
	private String mFaceBookUserId;
	private String mNextUrlForLoadingMessages;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list_pull_refrash, container, false);
		mListViewUserMessages = ((ListView) view.findViewById(R.id.lView_refresh));
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData_refresh);
		mDialogID =  getArguments().getString("DialogID");	
		mListUserMess =new ArrayList<SMessage>();
		Values values = new Values(getActivity());
		mFaceBookUserId = values.getFacebookID();
		mSession=Session.getActiveSession();
		Log.d(TAG, mDialogID);
		if(mSession!=null && mSession.isOpened()){
			getListOfMessagesFromFaceBook();
		}		

		mListViewUserMessages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayList<String> listOfUrls = Utilities.fetchPrivlyUrls(mListUserMess.get(position).getMessage());
				if(listOfUrls.size()>0){
				FragmentTransaction transaction = getActivity()
				 .getSupportFragmentManager().beginTransaction();
				 ShowContent showContent =new ShowContent();
				 Bundle bundle = new Bundle();
				 bundle.putStringArrayList("listOfLinks", listOfUrls);
				 showContent.setArguments(bundle);
				 transaction.replace(R.id.container,
						 showContent);
				 transaction.addToBackStack(null);
				 transaction.commit();
				}else{
					Toast.makeText(getActivity(), R.string.message_not_containe_privly_link,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		return view;
	}
	
	/**
	 * this method is used by the facebook API
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSession != null) {
			mSession.onActivityResult(getActivity(), requestCode, resultCode,
					data);
		}


	}


	/**
	 * Get inbox from FaceBook
	 */
	private void getListOfMessagesFromFaceBook() {
		Log.d(TAG, "getListOfMessagesFromFaceBook");
		mProgressBar.setVisibility(View.VISIBLE);

		Bundle params = new Bundle();
		params.putString("fields",
				"comments.fields(from.fields(id,picture),message,created_time)");
		// params.putString("limit", "1");
		Request request = Request.newGraphPathRequest(mSession, mDialogID,
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
						 GraphObject graphObject = response.getGraphObject();
						 try {			
							JSONObject jsonObjectComments =graphObject.getInnerJSONObject().getJSONObject("comments");
							JSONArray comments = jsonObjectComments.getJSONArray("data");
							Gson gson = new Gson();
							Type collectionType = new TypeToken<List<SMessage>>(){}.getType();
							mListUserMess=gson.fromJson(comments.toString(), collectionType);	
							for (SMessage mess : mListUserMess) {
								Log.d(TAG, mess.toString());
							}
							mNextUrlForLoadingMessages=jsonObjectComments.getJSONObject("paging").getString("next");
						} catch (JSONException e) {
								e.printStackTrace();
						}
						 
						if (mListUserMess != null) {
							mListUserMessagesAdapter = new ListUserMessagesAdapter(
									getActivity(), mListUserMess);
							mListViewUserMessages
									.setAdapter(mListUserMessagesAdapter);
							mListViewUserMessages.setSelection(mListUserMessagesAdapter.getCount() - 1);
						}
						mProgressBar.setVisibility(View.INVISIBLE);
					}
				});
		request.setParameters(params);
		request.executeAsync();
	}
	
	
	private class FetchFaceBookNextMessages extends AsyncTask<String, Void, ArrayList<SMessage>> {


		@Override
		protected void onPreExecute() {
	
		}

		@Override
		protected ArrayList<SMessage> doInBackground(String... urls) {
			String fbResponse="";
			ArrayList<SMessage> sMessages=null;
			try {
				// Make GET Request
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(urls[0]);
				HttpResponse responseGet = client.execute(get);
				HttpEntity resEntityGet = responseGet.getEntity();
				if (resEntityGet != null) {
					fbResponse = EntityUtils.toString(resEntityGet);

				}
				JSONObject jsonObjectComments =new JSONObject(fbResponse);
				JSONArray comments = jsonObjectComments.getJSONArray("data");
				Gson gson = new Gson();
				Type collectionType = new TypeToken<List<SMessage>>(){}.getType();	
				sMessages =gson.fromJson(comments.toString(), collectionType);
				mNextUrlForLoadingMessages=jsonObjectComments.getJSONObject("paging").getString("next");
			}

			catch (Exception e) {
				e.printStackTrace();
			}

			return sMessages;
		}

		@Override
		protected void onPostExecute(ArrayList<SMessage> result) {
			Integer lastIndex =mListUserMess.size();
			result.addAll(mListUserMess);			
			mListUserMess=result;					
			if (mListUserMess != null) {
				mListUserMessagesAdapter = new ListUserMessagesAdapter(
						getActivity(), mListUserMess);
				mListViewUserMessages
						.setAdapter(mListUserMessagesAdapter);				
				mListViewUserMessages.setSelection(lastIndex);
			}			
			mSwipeRefreshLayout.setRefreshing(false);
		}
		
	}
	
	
	@Override
	public void onRefresh() {
		 mSwipeRefreshLayout.setRefreshing(true);
		//getNextListOfMessagesFromFaceBook();
		FetchFaceBookNextMessages faceBookNextMessages =new FetchFaceBookNextMessages();
		faceBookNextMessages.execute(mNextUrlForLoadingMessages);
	}
}