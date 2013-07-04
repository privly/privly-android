package ly.priv.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * 
 * This class acts as a bridge between the Js of the Posting Applications and
 * the native Android functions.
 * 
 * @author Shivam Verma
 * 
 */
public class JsObject {

	Context c;

	/**
	 * 
	 * @param callingContext
	 *            sets current context as the context of the calling class.
	 */
	JsObject(Context callingContext) {
		c = callingContext;
	}

	/**
	 * 
	 * @return deviceVersion {String} Version of Android running on the device.
	 */
	@JavascriptInterface
	public String getDeviceVersion() {
		String deviceVersion = Build.VERSION.RELEASE;
		Log.d("androidJSBridge Version Request", deviceVersion);
		return deviceVersion;
	}

	/**
	 * Shows the Share screen {@link ly.priv.mobile.Share} to the user on
	 * receiving a new Privly Url
	 * 
	 * @param url
	 *            The newly generated Privly Url
	 */
	@JavascriptInterface
	public void receiveNewPrivlyURL(String url) {
		Log.d("androidJSBridge URL Received", url);
		Utilities.showToast(c, url, true);
		Intent gotoShare = new Intent(c, Share.class);
		gotoShare.putExtra("newPrivlyUrl", url);
		c.startActivity(gotoShare);
	}

	/**
	 * Identify the platform the Js is running on.
	 * 
	 * @return "ANDROID"
	 */
	@JavascriptInterface
	public String fetchPlatformName() {
		Log.d("androidJSBridge Request", "Platform Identification");
		return "ANDROID";
	}

	/**
	 * Fetch logged in user's auth_token from the sharedPreferences
	 * 
	 * @return auth_token {String}
	 */
	@JavascriptInterface
	public String fetchAuthToken() {
		SharedPreferences sharedPrefs;
		Values values = new Values();
		String prefs_name = values.getPrefsName();
		sharedPrefs = c.getSharedPreferences(prefs_name, 0);
		String auth_token = sharedPrefs.getString("auth_token", null);
		return auth_token;
	}

	/**
	 * Fetch the domain name to which all Privly Requests are being made.
	 * 
	 * @return domainName {String}
	 */
	@JavascriptInterface
	public String fetchDomainName() {
		SharedPreferences sharedPrefs;
		Values values = new Values();
		String prefs_name = values.getPrefsName();
		sharedPrefs = c.getSharedPreferences(prefs_name, 0);
		String domainName = sharedPrefs.getString("base_url", null);
		return domainName;
	}
}