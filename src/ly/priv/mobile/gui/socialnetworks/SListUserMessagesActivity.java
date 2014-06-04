package ly.priv.mobile.gui.socialnetworks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SListUserMessagesActivity extends SherlockFragment {
	private static final String TAG = "SListUserMessagesActivity";
	private ArrayList<SMessage> mListUserMess;
	private ListUserMessagesAdapter mListUserMessagesAdapter;
	private ListView mListViewUserMessages;
	private ProgressBar mProgressBar;
	private Session mSession;
	private String mDialogID;
	private String mFaceBookUserId;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		mListViewUserMessages = ((ListView) view.findViewById(R.id.lView));
		mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		mDialogID =  getArguments().getString("DialogID");	
		mListUserMess =new ArrayList<SMessage>();
		Values values = new Values(getActivity());
		mFaceBookUserId = values.getFacebookID();
		mSession=Session.getActiveSession();
		Log.d(TAG, mDialogID);
		if(mSession!=null && mSession.isOpened()){
			getDialogFromFaceBook();
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
	private void getDialogFromFaceBook() {
		Log.d(TAG, "getDialogFromFaceBook");
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
							JSONArray comments = graphObject.getInnerJSONObject().getJSONObject("comments").getJSONArray("data");
							Gson gson = new Gson();
							Type collectionType = new TypeToken<List<SMessage>>(){}.getType();
							mListUserMess=gson.fromJson(comments.toString(), collectionType);						
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
}