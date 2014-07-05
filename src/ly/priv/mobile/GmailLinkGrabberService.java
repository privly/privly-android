package ly.priv.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Thread;

/**
 * Authenticates user with Gmail and grabs Privly links from message inbox.
 * <p>
 * <ul>
 * <li>Shows Account Picker</li>
 * <li>Asks user for permission to access mails first time</li>
 * <li>Gets the access token using Play Services SDK</li>
 * <li>Uses access token to login to IMAP to fetch emails</li>
 * </ul>
 * </p>
 * 
 * @author Gitanshu Sardana
 * 
 */
public class GmailLinkGrabberService extends SherlockFragment {
	private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
	private static final String APP_NAME = "Privly Gmail";
	String accountName;
	Gmail mailService;
	ListView threadListView;
	ArrayList<Thread> mailThreads;
	ProgressBar progressBar;

	public GmailLinkGrabberService() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_list, container,
				false);
		threadListView = (ListView) view.findViewById(R.id.lView);
		// Shows Account Picker with google accounts
		Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
				new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, true,
				null, null, null, null);
		startActivityForResult(googlePicker, 1);
		return view;
	}

	// Gets selected email account and runs getAuthToken AsyncTask for selected account
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		progressBar = (ProgressBar) getActivity().findViewById(R.id.pbLoadingData);
		progressBar.setEnabled(true);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Log.d("Gmail", accountName);
			new getAuthToken().execute();
		}
	}

	// Gets oauth2 token using Play Services SDK and runs connectIMAP task after receiving token
	public class getAuthToken extends AsyncTask<Void, Void, List<Thread>> {

		@Override
		protected List<Thread> doInBackground(Void... params) {
			try {
				String token = GoogleAuthUtil.getToken(getActivity(), accountName,
						GMAIL_SCOPE);
				GoogleCredential credential = new GoogleCredential().setAccessToken(token);
				HttpTransport httpTransport = new NetHttpTransport();
			    JsonFactory jsonFactory = new JacksonFactory();
			    mailService = new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName(APP_NAME).build();
			    ListThreadsResponse threadsResponse;
			    List<Thread> threads =  null;
				try {
					threadsResponse = mailService.users().threads().list("me").execute();
					threads = threadsResponse.getThreads();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return threads;
			} catch (UserRecoverableAuthException e) {
				startActivityForResult(e.getIntent(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Thread> result) {
			if (result != null) {
				Log.d("thread", result.get(0).getId());
				new getThreads().execute(result);
			} else {
				Log.d("token", "is null");
			}
		}

	}
	
	public class getThreads extends AsyncTask<List<Thread>, Void, ArrayList<Thread>>{

		@Override
		protected void onPreExecute(){
			mailThreads = new ArrayList<Thread>();
		}
		
		@Override
		protected ArrayList<Thread> doInBackground(List<Thread>... params) {
			BatchRequest b = mailService.batch();
			JsonBatchCallback<Thread> bc = new JsonBatchCallback<Thread>() {

				@Override
				public void onSuccess(Thread t, HttpHeaders responseHeaders)
						throws IOException {
					// TODO Auto-generated method stub
					mailThreads.add(t);
				}

				@Override
				public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
						throws IOException {
					// TODO Auto-generated method stub
					
				}
			};
			try {
				for (Thread thread: params[0]){
					mailService.users().threads().get("me", thread.getId()).queue(b, bc);
				}
				b.execute();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return mailThreads;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Thread> threads) {
			if (threads != null) {
				progressBar.setEnabled(false);
				ListMailThreadsAdapter threadAdapter = new ListMailThreadsAdapter(getActivity(), threads);
				threadListView.setAdapter(threadAdapter);
			} else {
				Log.d("token", "is null");
			}
		}
		
	}
}