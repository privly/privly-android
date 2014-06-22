package ly.priv.mobile;

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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Displays the Index application for a user after authentication. 
 *
 * @author Gitanshu Sardana
 */
@SuppressLint("NewApi")
public class Index extends SherlockFragment {

	ListView readListView, createListView;
	String loginResponse;
	WebView w;

	public Index(){
		Log.d("jsgfwef","kwjebfkjef");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.new_post, container, false);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_home);
		container.removeAllViews();
		w = (WebView) view.findViewById(R.id.webview_1);
		w.getSettings().setJavaScriptEnabled(true);
		w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		Boolean isRedirected = null;
		try {
			isRedirected = getArguments().getBoolean("isRedirected");
			getActivity().setTitle("Index");
			loadIndex();
		} catch (NullPointerException e) {
			Log.d("isRedirected",""+isRedirected);
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
				loadIndex();
		}
		return view;
	}
	
	void loadIndex(){
		w.addJavascriptInterface(new JsObject(getActivity()), "androidJsBridge");

		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			w.getSettings().setAllowUniversalAccessFromFileURLs(true);
		// Logs all Js Console messages on the logcat.
		w.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("JsApplication",
						cm.message() + " -- From line " + cm.lineNumber()
								+ " of " + cm.sourceId());
				return true;
			}
		});
		w.loadUrl("file:///android_asset/PrivlyApplications/Index/new.html");
	}

	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
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
					loadIndex();
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
