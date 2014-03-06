package ly.priv.mobile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Displays the login screen. Allows the user to authenticate to a Privly Web
 * Server by fetching the auth_token.
 *
 * @author Shivam Verma
 */
public class Login extends Activity {
	/** Called when the activity is first created. */
	String userName, password, loginResponse, contentServerDomain;
	Button loginButton;
	EditText unameEditText, pwdEditText;
	SharedPreferences sharedPrefs;
	CheckBox rememberMeCheckBox;
	Values values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Utilities.setHederFont(this);
		// Shared Preference File for storing the domain name, if not
		// https://privlyalpha.org by
		// default.
		TextView loginHeader = (TextView) findViewById(R.id.loginHeader);
		Typeface lobster = Typeface.createFromAsset(getAssets(),
				"fonts/Lobster.ttf");
		loginHeader.setTypeface(lobster);
		values = new Values(getApplicationContext());
		contentServerDomain = values.getContentServerDomain();

		String prefsName = values.getPrefsName();
		sharedPrefs = getSharedPreferences(prefsName, 0);

		// If no content server has been defined,
		// the user is taken to the settings screen where he needs to add it.
		String authToken = values.getAuthToken();
		if (contentServerDomain == null) {
			Intent settings_it = new Intent(this, Settings.class);
			startActivity(settings_it);
			finish();
		} else {

			// Checks if the user selected the Remember Me option while loggin
			// in. If Yes, Redirect to Home Screen.
			Boolean rememberMe = values.getRememberMe();
			if (rememberMe && authToken != null) {
				Intent gotoHome = new Intent(getApplicationContext(),
						Home.class);

				// Clear activities stack. User wont be able to access Login
				// Screen on back button press. Since he is already logged in.
				gotoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(gotoHome);
			} else {
				unameEditText = (EditText) findViewById(R.id.uname);
				pwdEditText = (EditText) findViewById(R.id.pwd);
				loginButton = (Button) findViewById(R.id.login);

				// Set OnClickListener for Login Button. Executes a new
				// CheckLoginTask()
				loginButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						// Check for data connection availability before making
						// authentication request
						if (Utilities
								.isDataConnectionAvailable(getApplicationContext())) {
							userName = unameEditText.getText().toString();
							password = pwdEditText.getText().toString();
							rememberMeCheckBox = (CheckBox) findViewById(R.id.remember_me);

							// Remove any unwanted spaces before and after the
							// EmailID and Password
							userName = userName.trim();
							password = password.trim();

							// Check if Email is Valid using RegEx and Password
							// and is not null
							if (!Utilities.isValidEmail(userName))
								Utilities.showToast(getApplicationContext(),
										"Please Enter a valid EMail ID", false);
							else if (password.equalsIgnoreCase(""))
								Utilities.showToast(getApplicationContext(),
										"Please Enter a valid Password", false);
							else {
								CheckLoginTask task = new CheckLoginTask();
								task.execute(contentServerDomain
										+ "/token_authentications.json");

								// Flag to check if Remember me option was
								// selected
								Editor editor = sharedPrefs.edit();
								editor.putString("uname", userName);
								if (rememberMeCheckBox.isChecked()) {
									editor.putBoolean("remember_me", true);
									editor.commit();
								} else {
									editor.putBoolean("remember_me", false);
									editor.commit();
								}
							}
						} else
							Utilities
									.showToast(
											getApplicationContext(),
											"Oops! Seems like there\'s no Data connection.",
											true);
					}
				});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_layout_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.settings :
				Intent gotoSettings = new Intent(this, Settings.class);
				startActivity(gotoSettings);
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Verify user credentials and login. Redirects to
	 * {@link ly.priv.mobile.Home} Home Activity after successful login.
	 *
	 * @author Shivam Verma
	 *
	 */
	private class CheckLoginTask extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(Login.this);

		@Override
		protected void onPreExecute() {

			// Show Progress dialog
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("Logging in..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			for (String url : urls) {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				// NameValuePairs for POST Request
				nameValuePairs.add(new BasicNameValuePair("email", userName));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));
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
					HttpsURLConnection
							.setDefaultHostnameVerifier(hostnameVerifier);
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httpPost);
					HttpEntity entity = response.getEntity();
					loginResponse = EntityUtils.toString(entity);
				} catch (Exception e) {
				}
			}
			return loginResponse;
		}

		/**
		 * Saves auth_token to the SharedPreferences and redirects to Home
		 * Screen.
		 */
		@Override
		protected void onPostExecute(String result) {

			// Dismiss progress dialog
			dialog.dismiss();
			try {
				JSONObject jObject = new JSONObject(loginResponse);
				if (!jObject.has("error") && jObject.has("auth_key")) {
					String authToken = jObject.getString("auth_key");
					Values values = new Values(getApplicationContext());
					// Save auth_token
					values.setAuthToken(authToken);
					// Set flag that the user has been verified at the Login
					// Screen
					values.setUserVerifiedAtLogin(true);
					Intent gotoHome = new Intent(getApplicationContext(),
							Home.class);
					// Clear history stack. You dont want the user to be able to
					// access the Login Scren again, since he's already logged
					// in.
					gotoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(gotoHome);
				} else
					Utilities
							.showToast(
									getApplicationContext(),
									"Invalid Email Address or Password. Please Try Again!",
									true);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}
