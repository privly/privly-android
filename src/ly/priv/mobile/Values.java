
package ly.priv.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Values class is used to access values that should be accessible to all
 * classes throughout the application.
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
     * @return prefs_name The name of the SharedPreference File
     */
    String getPrefsName() {
        return prefsName;
    }

    /**
     * @return baseUrl The content server URL
     */
    String getBaseUrl() {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        String baseUrl = sharedPrefs.getString("base_url", null);
        return baseUrl;
    }

    /**
     * @return authToken Authentication token for user's session
     */
    String getAuthToken() {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        String authToken = sharedPrefs.getString("auth_token", null);
        return authToken;
    }

    /**
     * @return rememberMe Flag
     */
    Boolean getRememberMe() {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        Boolean rememberMe = sharedPrefs.getBoolean("remember_me", false);
        return rememberMe;
    }

    /**
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

}

/**
 * Key Pair Values saved in shared preferences
 * uname - email id of user
 * pwd -password of the User
 * base_url - domain_name to which the user authorizes
 * auth_token - authentication Key
 * remember_me - flag to check if the user checked remember me check box
 **/
