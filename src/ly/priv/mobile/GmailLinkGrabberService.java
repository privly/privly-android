package ly.priv.mobile;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.Message;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Session;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.code.samples.oauth2.OAuth2Authenticator;
import com.sun.mail.imap.IMAPStore;

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
	private static final String GMAIL_SCOPE = "oauth2:https://mail.google.com/";
	String accountName;

	public GmailLinkGrabberService() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.link_grabber_service, container,
				false);
		// Initialises OAuth2 SASL Provider
		OAuth2Authenticator.initialize();
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
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Log.d("Gmail", accountName);
			new getAuthToken().execute();
		}
	}

	// Gets oauth2 token using Play Services SDK and runs connectIMAP task after receiving token
	public class getAuthToken extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String token = null;
			try {
				token = GoogleAuthUtil.getToken(getActivity(), accountName,
						GMAIL_SCOPE);
				Log.d("token", token);
			} catch (UserRecoverableAuthException e) {
				startActivityForResult(e.getIntent(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}
			return token;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				new connectIMAP().execute(result);
			} else {
				Log.d("token", "is null");
			}
		}

	}

	// Connects to IMAP Server using JavaMail API
	// Fetches and prints folder list and subject of last email to log 
	public class connectIMAP extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String oAuthToken = params[0];
			try {
				IMAPStore imapStore = OAuth2Authenticator.connectToImap(
						"imap.gmail.com", 993, accountName, oAuthToken, true);
				Log.d("imap", "works");
				Folder folders[] = imapStore.getDefaultFolder().list("*");
				for (Folder f : folders) {
					Log.d("foldername", f.getFullName());
				}
				
				Folder folder = imapStore.getFolder("[Gmail]/All Mail");
				Log.d("Current folder", folder.toString());
				folder.open(Folder.READ_WRITE);
				Message messages[] = folder.getMessages();
				Log.d("Email count", folder.getMessageCount()+"");
				Log.d("Last Mail", messages[folder.getMessageCount()-1].getSubject());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}