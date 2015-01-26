package ly.priv.mobile;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import ly.priv.mobile.gui.LoginActivity;

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
import android.os.AsyncTask;
import android.util.Log;

/**
 * Verifies the validity of existing auth_token. If expired, redirect to
 * {@link ly.priv.mobile.gui.LoginActivity}
 * 
 * @author Shivam Verma
 * 
 */
public class VerifyAuthToken extends AsyncTask<String, Void, String> {
	private static final String TAG = "VerifyAuthToken";
	volatile ProgressDialog mDialog;
	private Activity mActivity;
	private String mLoginResponse;

	/**
	 * @param mDialog
	 */
	public VerifyAuthToken(Activity activity) {
		super();
		this.mDialog = new ProgressDialog(activity);
		mActivity = activity;
	}

	@Override
	protected void onPreExecute() {
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setMessage(mActivity.getString(R.string.verifying_session));
		mDialog.show();
	}

	@Override
	protected String doInBackground(String... urls) {

		String authenticatedUrl = Utilities
				.getGetRequestUrl(urls[0], mActivity);
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
			mLoginResponse = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return mLoginResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		mDialog.dismiss();
		try {
			JSONObject jObject = new JSONObject(mLoginResponse);
			if (!jObject.has("error") && jObject.has("auth_key")) {
				String authToken = jObject.getString("auth_key");
				Values values = new Values(mActivity);
				values.setAuthToken(authToken);
				values.setUserVerifiedAtLogin(false);
				Utilities.showToast(mActivity, mActivity
						.getString(R.string.good_to_go_select_an_option_),
						false);
			} else {
				Values values = new Values(mActivity);
				values.setAuthToken(null);
				Intent gotoLogin = new Intent(mActivity, LoginActivity.class);
				// Clear history stack. User should not be able to access
				// any activity since his session has expired.
				gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mActivity.startActivity(gotoLogin);
				Utilities.showToast(mActivity,
						mActivity.getString(R.string.your_session_has_expired),
						true);
			}
		} catch (Exception e) {
		}
	}
}
