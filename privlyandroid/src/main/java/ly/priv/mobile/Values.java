package ly.priv.mobile;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.ViewConfiguration;

/**
 * Used to set/get values that should be accessible to all classes throughout
 * the application.
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
	private SharedPreferences mSharedPrefs;
	private Context mContext;

	public Values(Context callingContext) {
		mContext = callingContext;
		mSharedPrefs = mContext.getSharedPreferences(
				ConstantValues.APP_PREFERENCES, 0);
	}

	/**
	 * The content server URL
	 * 
	 * @return {String} baseUrl
	 */
	public String getContentServerDomain() {
		String baseUrl = mSharedPrefs.getString(
				ConstantValues.APP_PREFERENCES_BASE_URL, null);
		return baseUrl;
	}

	/**
	 * Returns Authentication token for user's session
	 * 
	 * @return {String} authToken
	 */
	public String getAuthToken() {
		String authToken = mSharedPrefs.getString(
				ConstantValues.APP_PREFERENCES_AUTH_TOKEN, null);
		return authToken;
	}

	/**
	 * Returns value of remember_me Flag
	 * 
	 * @return {Boolean} rememberMe
	 */
	public Boolean getRememberMe() {
		Boolean rememberMe = mSharedPrefs.getBoolean(
				ConstantValues.APP_PREFERENCES_REMEMBER_ME, false);
		return rememberMe;
	}

	/**
	 * Returns username of the currently logged in user.
	 * 
	 * @return userName
	 */
	public String getUserName() {
		String userName = mSharedPrefs.getString(
				ConstantValues.APP_PREFERENCES_UNAME, null);
		return userName;
	}

	/**
	 * Save authentication token to SharedPrefs
	 * 
	 * @param authToken
	 */
	public void setUserName(String userName) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.APP_PREFERENCES_UNAME, userName);
		editor.commit();
	}

	/**
	 * Save authentication token to SharedPrefs
	 * 
	 * @param authToken
	 */
	public void setAuthToken(String authToken) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.APP_PREFERENCES_AUTH_TOKEN, authToken);
		editor.commit();
	}

	/**
	 * Save content server to SharedPrefs
	 * 
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.APP_PREFERENCES_BASE_URL, baseUrl);
		editor.commit();
	}

	/**
	 * Set Remember me flag
	 * 
	 * @param rememberMe
	 */
	public void setRememberMe(Boolean rememberMe) {
		Editor editor = mSharedPrefs.edit();
		editor.putBoolean(ConstantValues.APP_PREFERENCES_REMEMBER_ME,
				rememberMe);
		editor.commit();
	}

	/**
	 * Returns value of verified_at_login flag. Use this to prevent re
	 * authentication at Home Screen.
	 * 
	 * @return {Boolean}
	 */
	public Boolean isUserVerifiedAtLogin() {
		return mSharedPrefs.getBoolean(
				ConstantValues.APP_PREFERENCES_VERIFIED_AT_LOGIN, false);
	}

	/**
	 * Sets the value of verified_at_login Flag.
	 * 
	 * @param {Boolean} bool
	 */
	public void setUserVerifiedAtLogin(Boolean bool) {
		Editor editor = mSharedPrefs.edit();
		editor.putBoolean(ConstantValues.APP_PREFERENCES_VERIFIED_AT_LOGIN,
				bool);
		editor.commit();
	}

	/**
	 * Get optimal parameter values to determine a swipe on a View.
	 * 
	 * @return HashMap<String, Integer>
	 */
	public HashMap<String, Integer> getValuesForSwipe() {
		HashMap<String, Integer> swipeValues = new HashMap<String, Integer>();
		ViewConfiguration vc = ViewConfiguration.get(mContext);
		swipeValues.put(ConstantValues.SWIPE_MIN_DISTANCE,
				vc.getScaledPagingTouchSlop());
		swipeValues.put(ConstantValues.SWIPE_THRESHOLD_VELOCITY,
				vc.getScaledMinimumFlingVelocity());
		swipeValues.put(ConstantValues.SWIPE_MAX_OFF_PATH,
				vc.getScaledMinimumFlingVelocity());
		return swipeValues;
	}

	// / -------- FaceBook------------------------
	/**
	 * Set FaceBook user ID
	 * 
	 * @param id
	 *            - Facebook user id
	 * @author Ivan Metla
	 */
	public void setFacebookID(String id) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.PREFERENCE_FACEBOOK_USER_ID, id);
		editor.commit();
	}

	/**
	 * Get Facebook User ID
	 * 
	 * @return Facebook user ID
	 * @author Ivan Metla
	 */
	public String getFacebookID() {
		return mSharedPrefs.getString(
				ConstantValues.PREFERENCE_FACEBOOK_USER_ID, "");
	}

	// / -------- Twitter------------------------
	/**
	 * Set Twitter Logged in
	 * 
	 * @param loggedIn
	 */
	public void setTwitterLoggedIn(boolean loggedIn) {
		Editor editor = mSharedPrefs.edit();
		editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,
				loggedIn);
		editor.commit();
	}

	/**
	 * Get Twitter Logged in
	 * 
	 * @return Twitter Logged in
	 */
	public boolean getTwitterLoggedIn() {
		return mSharedPrefs.getBoolean(
				ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
	}

	/**
	 * Set Twitter Oauth Token
	 * 
	 * @param token
	 */
	public void setTwitterOauthToken(String token) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, token);
		editor.commit();
	}

	/**
	 * Get Twitter Oauth Token
	 * 
	 * @return
	 */
	public String getTwitterOauthToken() {
		return mSharedPrefs.getString(
				ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
	}

	/**
	 * Set Twitter Oauth Token Secret
	 * 
	 * @param tokenSecret
	 */
	public void setTwitterOauthTokenSecret(String tokenSecret) {
		Editor editor = mSharedPrefs.edit();
		editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
				tokenSecret);
		editor.commit();
	}

	/**
	 * Get Twitter Oauth Token Secret
	 * 
	 * @return
	 */
	public String getTwitterOauthTokenSecret() {
		return mSharedPrefs.getString(
				ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
	}
}
