package ly.priv.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.ViewConfiguration;

import java.util.HashMap;

/**
 * Used to set/get values that should be accessible to all
 * classes throughout the application.
 *
 * <p>
 * Key Pair Values saved in shared preferences
 * <li>uname : email id of user</li>
 * <li>pwd :password of the User</li>
 * <li>base_url : domain_name to which the user authorises</li>
 * <li>auth_token : authentication Key</li>
 * <li>remember_me : flag to check if the user checked remember me check box</li>
 * <li>verified_at_login : flag to check if user has been authenticated at
 * login. If true, the auth_token is not verified in Home Activity</li>
 * </p>
 *
 * @author Shivam Verma
 */
public final class Values {

	String prefsName;
	SharedPreferences sharedPrefs;
	Context context;

	Values(Context callingContext) {
		prefsName = "prefsFile";
		context = callingContext;
	}

	/**
	 * The name of the SharedPreference File
	 *
	 * @return {String} prefs_name
	 */
	String getPrefsName() {
		return prefsName;
	}

	/**
	 * The content server URL
	 *
	 * @return {String} baseUrl
	 */
	String getContentServerDomain() {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		String baseUrl = sharedPrefs.getString("base_url", null);
		return baseUrl;
	}

	/**
	 * Returns Authentication token for user's session
	 *
	 * @return {String} authToken
	 */
	String getAuthToken() {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		String authToken = sharedPrefs.getString("auth_token", null);
		return authToken;
	}

	/**
	 * Returns value of remember_me Flag
	 *
	 * @return {Boolean} rememberMe
	 */
	Boolean getRememberMe() {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		Boolean rememberMe = sharedPrefs.getBoolean("remember_me", false);
		return rememberMe;
	}

	/**
	 * Returns username of the currently logged in user.
	 *
	 * @return userName
	 */
	String getUserName() {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		String userName = sharedPrefs.getString("uname", null);
		return userName;
	}

	/**
	 * Save authentication token to SharedPrefs
	 *
	 * @param authToken
	 */
	void setAuthToken(String authToken) {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		Editor editor = sharedPrefs.edit();
		editor.putString("auth_token", authToken);
		editor.commit();
	}

	/**
	 * Save content server to SharedPrefs
	 *
	 * @param baseUrl
	 */
	void setBaseUrl(String baseUrl) {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		Editor editor = sharedPrefs.edit();
		editor.putString("base_url", baseUrl);
		editor.commit();
	}

	/**
	 * Set Remember me flag
	 *
	 * @param rememberMe
	 */
	void setRememberMe(Boolean rememberMe) {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean("remember_me", rememberMe);
		editor.commit();
	}

	/**
	 * Returns value of verified_at_login flag. Use this to prevent re
	 * authentication at Home Screen.
	 *
	 * @return {Boolean}
	 */
	Boolean isUserVerifiedAtLogin() {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		return sharedPrefs.getBoolean("verified_at_login", false);

	}

	/**
	 * Sets the value of verified_at_login Flag.
	 *
	 * @param {Boolean} bool
	 */
	void setUserVerifiedAtLogin(Boolean bool) {
		sharedPrefs = context.getSharedPreferences(prefsName, 0);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean("verified_at_login", bool);
		editor.commit();
	}

	/**
	 * Get optimal parameter values to determine a swipe on a View.
	 *
	 * @return HashMap<String, Integer>
	 */
	HashMap<String, Integer> getValuesForSwipe() {

		HashMap<String, Integer> swipeValues = new HashMap<String, Integer>();
		ViewConfiguration vc = ViewConfiguration.get(context);
		swipeValues.put("swipeMinDistance", vc.getScaledPagingTouchSlop());
		swipeValues.put("swipeThresholdVelocity",
				vc.getScaledMinimumFlingVelocity());
		swipeValues.put("swipeMaxOffPath", vc.getScaledMinimumFlingVelocity());
		return swipeValues;
	}
}

/**
 *
 **/
