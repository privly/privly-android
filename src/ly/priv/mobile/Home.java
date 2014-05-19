package ly.priv.mobile;

import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

/**
 * Displays the Home Activity for a user after authentication. Gives the user
 * options to Create New Privly posts or Read Privly Posts from his social /
 * email feed.
 *
 * @author Shivam Verma
 */
public class Home extends SherlockFragment {

	ListView readListView, createListView;
	String loginResponse;

	public Home(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.home, container, false);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_home);
		container.removeAllViews();
		TextView createHeadingEditText = (TextView) view.findViewById(R.id.createNewHeadingTextView);
		TextView readHeadingEditText = (TextView) view.findViewById(R.id.readPostsHeadingTextView);
		Typeface lobster = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lobster.ttf");
		createHeadingEditText.setTypeface(lobster);
		readHeadingEditText.setTypeface(lobster);

		try {
			Boolean isRedirected = getArguments().getBoolean("isRedirected");
		} catch (NullPointerException e) {
			Values values = new Values(getActivity());
			// Checks if the User has already been verified at the Login Screen.
			// If yes, prevents re authentication. If not, creates and executes
			// a VerifyAuthToken task.
			if (!values.isUserVerifiedAtLogin()) {
				VerifyAuthToken task = new VerifyAuthToken();
				task.execute(values.getContentServerDomain()
						+ "/token_authentications.json");
			} else
				values.setUserVerifiedAtLogin(false);
		}

		// Create two ListViews which display create/read options.
		final String[] arrCreate = {"PlainPost", "ZeroBin"};
		final String[] arrRead = {"GMail", "Facebook", "Twitter"};
		ArrayList<String> createArrayList = new ArrayList<String>(
				Arrays.asList(arrCreate));
		ArrayList<String> readArrayList = new ArrayList<String>(
				Arrays.asList(arrRead));

		createListView = (ListView) view.findViewById(R.id.create_listView);
		readListView = (ListView) view.findViewById(R.id.read_listView);

		ArrayAdapter<String> createArrayAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.list_item, createArrayList);
		ArrayAdapter<String> readArrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item, readArrayList);

		createListView.setAdapter(createArrayAdapter);
		readListView.setAdapter(readArrayAdapter);

		// OnItemClickListener for creating posts ListView. The name of the
		// selected Posting app is sent with the intent to {@link
		// ly.priv.mobile.NewPost} Activity.
		createListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (Utilities
						.isDataConnectionAvailable(getActivity())) {
					Fragment gotoCreateNewPost = new NewPost();
					Bundle bundle = new Bundle();
					bundle.putString("JsAppName", arrCreate[position]);
					gotoCreateNewPost.setArguments(bundle);
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.container, gotoCreateNewPost);
					transaction.addToBackStack("home");
					transaction.commit();
				} else
					Utilities.showToast(getActivity(),
							"Oops! Seems like there\'s no data connection.",
							true);
			}
		});

		// OnItemClickListener for Reading posts ListView. Redirects User to
		// LinkGrabber Service of the respective platform.
		// For Twitter - {@link ly.priv.mobile.TwitterLinkGrabberService}
		// For Facebook - {@link ly.priv.mobile.FacebookLinkGrabberService}
		readListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0) {
					Toast.makeText(getActivity(),
							"Sorry, Gmail hasn't been integrated yet.",
							Toast.LENGTH_LONG).show();
				} else if (position == 1) {
					FacebookLinkGrabberService fbGrabber = new FacebookLinkGrabberService();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.container, fbGrabber);
					transaction.disallowAddToBackStack();
					transaction.commit();
				} else if (position == 2) {
					TwitterLinkGrabberService twitGrabber = new TwitterLinkGrabberService();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.container, twitGrabber, "Twitter");
					transaction.disallowAddToBackStack();
					transaction.commit();
				}

			}
		});
		Log.d("fragments", "Home");
		return view;
	}
	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.layout.menu_layout_home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Item click listener for options menu.
	 * <p>
	 * Redirect to {@link ly.priv.mobile.Settings} Or
	 * {@link ly.priv.mobile.Login}
	 * </p>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.settings :
				Intent gotoSettings = new Intent(getActivity(), Settings.class);
				startActivity(gotoSettings);
				return true;

			case R.id.logout :
				// Logs out User from Privly Application
				Values values = new Values(getActivity());
				values.setAuthToken(null);
				values.setRememberMe(false);
				Intent gotoLogin = new Intent(getActivity(), Login.class);
				gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(gotoLogin);
				return true;

			default :
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Verifies the validity of existing auth_token. If expired, redirect to
	 * {@link ly.priv.mobile.Login}
	 *
	 * @author Shivam Verma
	 *
	 */
	private class VerifyAuthToken extends AsyncTask<String, Void, String> {

		volatile ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("Verifying session..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {

			String authenticatedUrl = Utilities.getGetRequestUrl(urls[0],
					getActivity());
			try {
				// Setting Up for a secure connection
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				DefaultHttpClient client = new DefaultHttpClient();
				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory
						.getSocketFactory();
				socketFactory
						.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(
						client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
						client.getParams());
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
				HttpGet httpget = new HttpGet(authenticatedUrl);
				HttpResponse response = httpClient.execute(httpget);
				HttpEntity entity = response.getEntity();
				loginResponse = EntityUtils.toString(entity);
			} catch (Exception e) {
			} finally {

			}

			return loginResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				JSONObject jObject = new JSONObject(loginResponse);
				if (!jObject.has("error") && jObject.has("auth_key")) {
					String authToken = jObject.getString("auth_key");
					Values values = new Values(getActivity());
					values.setAuthToken(authToken);
					values.setUserVerifiedAtLogin(false);
					Utilities.showToast(getActivity(),
							"Good to go! Select an option.", false);
				} else {
					Values values = new Values(getActivity());
					values.setAuthToken(null);
					Intent gotoLogin = new Intent(getActivity(),
							Login.class);
					// Clear history stack. User should not be able to access
					// any activity since his session has expired.
					gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(gotoLogin);
					Utilities.showToast(getActivity(),
							"Your session has expired. Please login again.",
							true);
				}
			} catch (Exception e) {
			}
		}
	}

}
