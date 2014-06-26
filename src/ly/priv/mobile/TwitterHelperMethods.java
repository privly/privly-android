package ly.priv.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Provides access to methods required by the TwitterLinkGrabberService
 * 
 * @author Shivam Verma
 * 
 */
public class TwitterHelperMethods {

	/**
	 * Saves the login status of a twitter user into SharedPreferences.
	 * 
	 * Example :
	 * <p>
	 * Key Values pair saved in SharedPrefrences
	 * <li>twitter_login_status : Login status.</li>
	 * <li>twitter_token : twitter access token</li>
	 * <li>twitter_token_secret : twitter access token secret</li>
	 * </p>
	 * 
	 * @param {Context} context Context of the calling Activity
	 * @param {Boolean} status Logged in status to be set.
	 */
	public static void setTwitterUserLoggedInStatus(Context context,
			Boolean status) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("twitter_login_status", status);
		editor.commit();
	}

	/**
	 * Returns the login status of a twitter user.
	 * 
	 * @param {Context} context Context of the calling Activity
	 * @return {Boolean}
	 */
	public static Boolean isTwitterUserLoggedIn(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getBoolean("twitter_login_status", false);
	}

	/**
	 * Save the twitter access token and token secret values to
	 * SharedPreferences
	 * 
	 * @param {Context} context Context of the Calling Activity
	 * @param {String} token Token value fetched after making a request to the
	 *        twitter api.
	 * @param {String} tokenSecret Token Secret value fetched after making a
	 *        request to twitter api.
	 * @return
	 */
	public static Boolean saveTwitterAccessTokenValues(Context context,
			String token, String tokenSecret) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		Log.d("TAG", token);
		Log.d("TAG", tokenSecret);
		Editor editor = sharedPreferences.edit();
		editor.putString("twitter_token", token);
		editor.putString("twitter_token_secret", tokenSecret);
		editor.commit();
		return true;
	}

	/**
	 * Returns the token string after fetching it from SharedPreferences
	 * 
	 * @param {Context} context Context of the calling Activity
	 * @return {String} Token string
	 */
	public static String getTwitterToken(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getString("twitter_token", null);

	}

	/**
	 * Returns the token secret string after fetching it from SharedPreferences
	 * 
	 * @param {Context} context Context of the calling Activity
	 * @return {String} Token secret string
	 */
	public static String getTwitterTokenSecret(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getString("twitter_token_secret", null);

	}
}