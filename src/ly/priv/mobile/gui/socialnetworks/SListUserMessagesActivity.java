package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ly.priv.mobile.R;
import ly.priv.mobile.ShowContent;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

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
				 FragmentTransaction transaction = getActivity()
				 .getSupportFragmentManager().beginTransaction();
				 ShowContent showContent =new ShowContent();
				 Bundle bundle = new Bundle();
				 bundle.putStringArrayList("listOfLinks", Utilities.fetchPrivlyUrls(mListUserMess.get(position).getMessage()));
				 showContent.setArguments(bundle);
				 transaction.replace(R.id.container,
						 showContent);
				 transaction.addToBackStack(null);
				 transaction.commit();

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
							for (int i = 0; i < comments.length(); i++) {
								JSONObject comment = comments.getJSONObject(i);
								SMessage sMmessage =new SMessage();
								String message =comment.getString("message");
								ArrayList<String> listUrl =Utilities.fetchPrivlyUrls(message);
								
								sMmessage.setMessage(message);
								sMmessage.setTime(Utilities.getTime(comment.getString("created_time")));	
								JSONObject from = comment.getJSONObject("from");
								String id =from.getString("id");
								sMmessage.setIsMyMessage(id.equals(mFaceBookUserId));
								JSONObject picture = from.getJSONObject("picture");
								sMmessage.setUrlToAvatar(picture.getJSONObject("data").getString("url"));
								mListUserMess.add(sMmessage);
							}
							
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