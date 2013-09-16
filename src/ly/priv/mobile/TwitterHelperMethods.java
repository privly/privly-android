package ly.priv.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class TwitterHelperMethods {

	public Boolean isTwitterUserLoggedIn() {

		return true;
	}

	public static void setTwitterUserLoggedInStatus(Context context,
			Boolean status) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("twitter_login_status", status);
		editor.commit();
	}

	public static Boolean isTwitterUserLoggedIn(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getBoolean("twitter_login_status", false);
	}

	public static Boolean setTwitterAccessTokenValues(Context context,
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

	public static String getTwitterToken(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getString("twitter_token", null);

	}

	public static String getTwitterTokenSecret(Context context) {
		Values values = new Values(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				values.prefsName, 0);
		return sharedPreferences.getString("twitter_token_secret", null);

	}
}