
package ly.priv.mobile;

import android.content.Context;
import android.content.SharedPreferences;

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

    String getBaseUrl()
    {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        String baseURL = sharedPrefs.getString("base_url", null);
        return baseURL;
    }

    String getauthToken()
    {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        String authToken = sharedPrefs.getString("auth_token", null);
        return authToken;
    }

    Boolean getRememberMe()
    {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        Boolean rememberMe = sharedPrefs.getBoolean("remember_me", false);
        return rememberMe;
    }

    String getUserName()
    {
        sharedPrefs = context.getSharedPreferences(prefsName, 0);
        String userName = sharedPrefs.getString("uname", null);
        return userName;
    }

}

/**
 * Key Pair Values saved in shared preferences
 * uname - email id of user
 * pwd -password of the User
 * base_url - domain_name to which the user authorizes
 * auth_token - authentication Key
 **/
