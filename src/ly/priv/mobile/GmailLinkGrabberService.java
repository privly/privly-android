package ly.priv.mobile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.google.api.services.gmail.model.MessagePartHeader;
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
	ArrayList<EmailThreadObject> mailThreads;
	ProgressBar progressBar;
	OnItemClickListener listener;
	Thread currentThread;
	SharedPreferences sharedPrefs;
	String prefsName;
	ListMailThreadsAdapter threadAdapter;

	public GmailLinkGrabberService() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_list, container, false);
		threadListView = (ListView) view.findViewById(R.id.lView);
		progressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
		prefsName = ConstantValues.APP_PREFERENCES;
		sharedPrefs = getActivity().getSharedPreferences(prefsName, 0);
		threadListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				EmailThreadObject thread = mailThreads.get(arg2);
				Fragment mailThread = new GmailSingleThreadFragment();
				Bundle args = new Bundle();
				args.putParcelable("currentThread", thread);
				mailThread.setArguments(args);
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, mailThread);
				transaction.addToBackStack(null);
				transaction.commit();
			}

		});

		// Shows Account Picker with google accounts if not stored in shared
		// preferences
		Boolean accountFound = false;
		if (sharedPrefs.contains("gmailId")) {
			Account[] accounts = AccountManager.get(getActivity())
					.getAccounts();
			accountName = sharedPrefs.getString("gmailId", null);
			Log.d("accountName", accountName);
			for (Account a : accounts) {
				if (a.type.equals(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
						&& a.name.equals(accountName)) {
					accountFound = true;
					progressBar.setVisibility(View.VISIBLE);
					new getAuthToken().execute();
					break;
				}
			}
		}

		if (!accountFound) {
			Intent googlePicker = AccountPicker.newChooseAccountIntent(null,
					null, new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
					true, null, null, null, null);
			startActivityForResult(googlePicker, 1);
		}

		return view;
	}

	// Gets selected email account and runs getAuthToken AsyncTask for selected
	// account
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		progressBar.setVisibility(View.VISIBLE);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Editor editor = sharedPrefs.edit();
			editor.putString("gmailId", accountName);
			editor.commit();
			new getAuthToken().execute();
		}
	}

	// Gets oauth2 token using Play Services SDK and runs connectIMAP task after
	// receiving token
	public class getAuthToken extends AsyncTask<Void, Void, List<Thread>> {

		@Override
		protected List<Thread> doInBackground(Void... params) {
			try {
				String token = GoogleAuthUtil.getToken(getActivity(),
						accountName, GMAIL_SCOPE);
				GoogleCredential credential = new GoogleCredential()
						.setAccessToken(token);
				HttpTransport httpTransport = new NetHttpTransport();
				JsonFactory jsonFactory = new JacksonFactory();
				mailService = new Gmail.Builder(httpTransport, jsonFactory,
						credential).setApplicationName(APP_NAME).build();
				ListThreadsResponse threadsResponse;
				List<Thread> threads = null;
				try {
					threadsResponse = mailService.users().threads().list("me")
							.execute();
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
				generateBatch(result);
			}
		}

	}

	// async task to execute batch request that gets all threads
	public class getThreads extends AsyncTask<BatchRequest, Void, Void> {

		@Override
		protected Void doInBackground(BatchRequest... params) {
			try {
				params[0].execute();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void threads) {
			progressBar.setVisibility(View.GONE);
			Log.d("notify", "dataset changed");
			threadAdapter.notifyDataSetChanged();
		}

	}

	// generate batch request and execute async task to get all threads in
	// single request
	public void generateBatch(List<Thread> threadsList) {
		BatchRequest b = mailService.batch();
		mailThreads = new ArrayList<EmailThreadObject>();
		threadAdapter = new ListMailThreadsAdapter(getActivity(), mailThreads);
		threadListView.setAdapter(threadAdapter);
		JsonBatchCallback<Thread> bc = new JsonBatchCallback<Thread>() {

			@Override
			public void onSuccess(Thread t, HttpHeaders responseHeaders)
					throws IOException {
				int mailCount = t.getMessages().size();
				EmailThreadObject thread = new EmailThreadObject();
				if (mailCount > 1){
					thread.setMailCount(" (" + String.valueOf(mailCount) + ") ");
				} else {
					thread.setMailCount("");
				}				
				thread.setMessages(t.getMessages());
				thread.setId(t.getId());
				List<MessagePartHeader> headerFirst = t.getMessages().get(0)
						.getPayload().getHeaders();
				Log.d("processsing", t.getId());
				if (mailCount > 1) {
					List<MessagePartHeader> headerLast = t.getMessages()
							.get(mailCount - 1).getPayload().getHeaders();
					String senderFirst = null, senderLast, sender = null;
					for (MessagePartHeader m : headerFirst) {
						if (m.getName().equals("From")) {
							senderFirst = m.getValue();
							sender = senderFirst;
							if (senderFirst.contains(" ")) {
								senderFirst = senderFirst.substring(0,
										senderFirst.indexOf(' '));
							}
						} else if (m.getName().equals("Subject")) {
							thread.setMailSnippet(m.getValue());
						}
					}
					for (MessagePartHeader m : headerLast) {
						if (m.getName().equals("From")) {
							senderLast = m.getValue();
							if (senderLast.contains(" ")) {
								senderLast = senderLast.substring(0,
										senderLast.indexOf(' '));
							}
							if (!senderFirst.equals(senderLast)) {
								sender = senderFirst + "..." + senderLast;
							}
							Log.d("sender", sender);
							thread.setMailSender(sender);
						} else if (m.getName().equals("Date")) {
							thread.setMailTime(Utilities.getTimeForGmail(m
									.getValue()));
						}
					}
				} else {
					for (MessagePartHeader m : headerFirst) {
						if (m.getName().equals("From")) {
							thread.setMailSender(m.getValue());
						} else if (m.getName().equals("Date")) {
							thread.setMailTime(Utilities.getTimeForGmail(m
									.getValue()));
						} else if (m.getName().equals("Subject")) {
							thread.setMailSnippet(m.getValue());
						}
					}
				}
				mailThreads.add(thread);
			}

			@Override
			public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
					throws IOException {

			}
		};

		for (Thread thread : threadsList) {
			try {
				mailService.users().threads().get("me", thread.getId())
						.queue(b, bc);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		new getThreads().execute(b);
	}
}