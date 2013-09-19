package ly.priv.mobile;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Displays the Home Activity for a user after authentication. Gives the user
 * options to Create New Privly posts or Read Privly Posts from his social /
 * email feed.
 *
 * @author Shivam Verma
 */
public class Home extends Activity {

	ListView readListView, createListView;
	String loginResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		TextView createHeadingEditText = (TextView) findViewById(R.id.createNewHeadingTextView);
		TextView readHeadingEditText = (TextView) findViewById(R.id.readPostsHeadingTextView);
		Typeface lobster = Typeface.createFromAsset(getAssets(),
				"fonts/Lobster.ttf");
		createHeadingEditText.setTypeface(lobster);
		readHeadingEditText.setTypeface(lobster);

		Values values = new Values(getApplicationContext());
		// Checks if the User has already been verified at the Login Screen. If
		// yes, prevents double authentication. If not, creates and executes a
		// VerifyAuthToken task.
		if (!values.isUserVerifiedAtLogin()) {
			VerifyAuthToken task = new VerifyAuthToken();
			task.execute(values.getContentServerDomain()
					+ "/token_authentications.json");
		} else
			values.setUserVerifiedAtLogin(false);

		// Create two ListViews which display create/read options.
		final String[] arrCreate = {"PlainPost", "ZeroBin"};
		final String[] arrRead = {"GMail", "Facebook", "Twitter"};
		ArrayList<String> createArrayList = new ArrayList<String>(
				Arrays.asList(arrCreate));
		ArrayList<String> readArrayList = new ArrayList<String>(
				Arrays.asList(arrRead));

		createListView = (ListView) findViewById(R.id.create_listView);
		readListView = (ListView) findViewById(R.id.read_listView);

		ArrayAdapter<String> createArrayAdapter = new ArrayAdapter<String>(
				this, R.layout.list_item, createArrayList);
		ArrayAdapter<String> readArrayAdapter = new ArrayAdapter<String>(this,
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
						.isDataConnectionAvailable(getApplicationContext())) {
					Intent gotoCreateNewPost = new Intent(
							getApplicationContext(), NewPost.class);
					gotoCreateNewPost
							.putExtra("JsAppName", arrCreate[position]);
					startActivity(gotoCreateNewPost);
				} else
					Utilities.showToast(getApplicationContext(),
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
					Toast.makeText(getApplicationContext(),
							"Sorry, Gmail hasn't been integrated yet.",
							Toast.LENGTH_LONG).show();
				} else if (position == 1) {
					Intent facebookLinkGrabberIntent = new Intent(Home.this,
							FacebookLinkGrabberService.class);
					startActivity(facebookLinkGrabberIntent);
				} else if (position == 2) {
					Intent twitterLinkGrabberIntent = new Intent(Home.this,
							TwitterLinkGrabberService.class);
					startActivity(twitterLinkGrabberIntent);
				}

			}
		});

	}
	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_layout_home, menu);
		return true;
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
				Intent gotoSettings = new Intent(this, Settings.class);
				startActivity(gotoSettings);
				return true;

			case R.id.logout :
				// Logs out User from Privly Application
				Values values = new Values(getApplicationContext());
				values.setAuthToken(null);
				values.setRememberMe(false);
				Intent gotoLogin = new Intent(this, Login.class);
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

		volatile ProgressDialog dialog = new ProgressDialog(Home.this);

		@Override
		protected void onPreExecute() {
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("Verifying session..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {

			String authenticatedUrl = Utilities.getGetRequestUrl(urls[0],
					getApplicationContext());
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
					Values values = new Values(getApplicationContext());
					values.setAuthToken(authToken);
					values.setUserVerifiedAtLogin(false);
					Utilities.showToast(getApplicationContext(),
							"Good to go! Select an option.", false);
				} else {
					Values values = new Values(getApplicationContext());
					values.setAuthToken(null);
					Intent gotoLogin = new Intent(getApplicationContext(),
							Login.class);
					// Clear history stack. User should not be able to access
					// any activity since his session has expired.
					gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(gotoLogin);
					Utilities.showToast(getApplicationContext(),
							"Your session has expired. Please login again.",
							true);
				}
			} catch (Exception e) {
			}
		}
	}

}
