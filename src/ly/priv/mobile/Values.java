
package ly.priv.mobile;

/**
 * Values class is used to access values that should be accessible to all
 * classes throughout the application.
 * 
 * @author Shivam Verma
 */
public final class Values {

    String prefsName;

    Values() {
        prefsName = "prefsFile";
    }

    /**
     * @return prefs_name The name of the SharedPreference File
     */
    String getPrefsName() {
        return prefsName;
    }

}

/**
 * Key Pair Values saved in shared preferences uname - email id of user pwd -
 * password of the User base_url - domain_name to which the user authorizes
 * auth_token - authentication Key
 **/
