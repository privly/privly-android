package ly.priv.mobile.utils;

import ly.priv.mobile.R;
import ly.priv.mobile.gui.LoginActivity;
import ly.priv.mobile.gui.ShareFragment;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.utils.Values;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Acts as a bridge between the Js of the Privly Posting and Reading
 * Applications and the native Android functions.
 * 
 * @author Shivam Verma
 */
public class JsObject {
	private static final String TAG = "JsObject";
	private Context mContext;
	static ProgressDialog dialog;

	/**
	 * sets current context as the context of the calling class.
	 * 
	 * @param callingContext
	 */
	public JsObject(Context callingContext) {
		mContext = callingContext;
	}

	/**
	 * @return deviceVersion {String} Version of Android running on the device.
	 */
	@JavascriptInterface
	public String getDeviceVersion() {
		String deviceVersion = Build.VERSION.RELEASE;
		Log.d(TAG, deviceVersion);
		return deviceVersion;
	}

	/**
	 * Shows the {@link ly.priv.mobile.gui.ShareFragment} Activity to the user
	 * on receiving a new Privly Url
	 * 
	 * @param url
	 *            The newly generated Privly Url
	 */
	@JavascriptInterface
	public void receiveNewPrivlyURL(String url) {
		Log.d(TAG, url);
		Utilities.showToast(mContext, url, true);
		Fragment gotoShare = new ShareFragment();
		Bundle args = new Bundle();
		args.putString(ConstantValues.NEW_PRIVLY_URL, url);
		gotoShare.setArguments(args);
		FragmentTransaction transaction = ((FragmentActivity) mContext)
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, gotoShare);
		transaction.commit();
	}

	/**
	 * Identify the platform the Js is running on.
	 * 
	 * @return "ANDROID"
	 */
	@JavascriptInterface
	public String fetchPlatformName() {
		Log.d(TAG, "Platform Identification");
		return "ANDROID";
	}

	/**
	 * Fetch logged in user's auth_token from the sharedPreferences
	 * 
	 * @return auth_token {String}
	 */
	@JavascriptInterface
	public String fetchAuthToken() {
		Values values = new Values(mContext);
		String auth_token = values.getAuthToken();
		return auth_token;
	}

	/**
	 * Fetch the domain name to which all Privly Requests are being made.
	 * 
	 * @return domainName {String}
	 */
	@JavascriptInterface
	public String fetchDomainName() {
		Values values = new Values(mContext);
		String domainName = values.getContentServer();
		return domainName;
	}

	@JavascriptInterface
	public void showWaitDialog(String message) {
		dialog = new ProgressDialog(mContext);
		dialog.setMessage(message);
		dialog.show();
	}

	@JavascriptInterface
	public void hideWaitDialog() {
		dialog.dismiss();
	}

	@JavascriptInterface
	public void showLoginActivity() {
		Intent gotoLogin = new Intent(mContext, LoginActivity.class);
		/**
		 * Set authToken null so that the Login Activity does not redirect the
		 * user to Home Activity.
		 */

		Values values = new Values(mContext);
		values.setAuthToken(null);
		gotoLogin.putExtra(ConstantValues.IS_REDIRECTED, true);

		// Clear the history stack. Once the user is redirected to the Login
		// Activity. The user should not be able to access previous activities.

		gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mContext.startActivity(gotoLogin);
	}

	/**
	 * Checks for data connection availability
	 * 
	 * @return {Boolean}
	 */
	@JavascriptInterface
	public String isDataConnectionAvailable() {
		Boolean dataConnectionAvailability = Utilities
				.isDataConnectionAvailable(mContext);
		if (dataConnectionAvailability)
			return "true";
		else
			return "false";
	}

	@JavascriptInterface
	public void showToast(String textToToast) {
		Utilities.showToast(mContext, textToToast, true);
	}
}
