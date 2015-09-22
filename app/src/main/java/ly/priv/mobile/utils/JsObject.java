package ly.priv.mobile.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import ly.priv.mobile.R;
import ly.priv.mobile.gui.activities.LoginActivity;
import ly.priv.mobile.gui.fragments.SharePrivlyURLFragment;

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
    Values mValues;

    /**
     * sets current context as the context of the calling class.
     *
     * @param callingContext
     */
    public JsObject(Context callingContext) {
        mValues = Values.getInstance();
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
     * Shows the {@link ly.priv.mobile.gui.fragments.SharePrivlyURLFragment} to the user
     * on receiving a new Privly Url
     *
     * @param url The newly generated Privly Url
     */
    @JavascriptInterface
    public void receiveNewPrivlyURL(String url) {
        Log.d(TAG, url);
        SharePrivlyURLFragment sharePrivlyURLFragment = new SharePrivlyURLFragment();
        Bundle args = new Bundle();
        args.putString(ConstantValues.PRIVLY_URL_KEY, url);
        sharePrivlyURLFragment.setArguments(args);
        FragmentTransaction transaction = ((ActionBarActivity) mContext)
                .getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, sharePrivlyURLFragment).addToBackStack(null);
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
        String auth_token = mValues.getAuthToken();
        return auth_token;
    }

    /**
     * Fetch the domain name to which all Privly Requests are being made.
     *
     * @return domainName {String}
     */
    @JavascriptInterface
    public String fetchDomainName() {
        String domainName = mValues.getContentServer();
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
        // Set authToken null.
        mValues.setAuthToken(null);
        Intent gotoLogin = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(gotoLogin);
        Activity activity = (Activity) mContext;
        activity.finish();
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
