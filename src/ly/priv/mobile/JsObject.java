
package ly.priv.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * This class acts as a bridge between the Js of the Posting Applications and
 * the native Android functions.
 * 
 * @author Shivam Verma
 */
public class JsObject {

    Context c;

    SharedPreferences sharedPrefs;

    static ProgressDialog dialog;

    /**
     * @param callingContext sets current context as the context of the calling
     *            class.
     */
    JsObject(Context callingContext) {
        c = callingContext;
    }

    /**
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
     * @param url The newly generated Privly Url
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
        Values values = new Values(c);
        String auth_token = values.getauthToken();
        return auth_token;
    }

    /**
     * Fetch the domain name to which all Privly Requests are being made.
     * 
     * @return domainName {String}
     */
    @JavascriptInterface
    public String fetchDomainName() {
        Values values = new Values(c);
        String domainName = values.getBaseUrl();
        return domainName;
    }

    @JavascriptInterface
    public void showWaitDialog(String message) {
        dialog = new ProgressDialog(c);
        dialog.setMessage(message);
        dialog.show();
    }

    @JavascriptInterface
    public void hideWaitDialog() {
        dialog.dismiss();
    }

    @JavascriptInterface
    public void showLoginActivity() {
        Intent gotoLogin = new Intent(c, Login.class);
        /**
         * Set authToken null so that the Login Activity does not redirect the
         * user to Home Activity.
         */
        Editor e = sharedPrefs.edit();
        e.putString("auth_token", null);
        e.commit();
        gotoLogin.putExtra("isRedirected", true);
        /**
         * Clear the history stack. Once the user is redirected to the Login
         * Activity, there is no point in user being able to access previous
         * Activities.
         */
        gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(gotoLogin);
    }

    @JavascriptInterface
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)c
                .getSystemService(c.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @JavascriptInterface
    public void showToast(String textToToast) {
        Utilities.showToast(c, textToToast, true);
    }
}
