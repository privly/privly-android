
package ly.priv.mobile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Utilities class contains simple functions that should be used wherever
 * possible.
 * 
 * @author Shivam Verma
 */
public class Utilities {

    /**
     * Check validity of an EMail address using RegEx
     * 
     * @param emailToCheck
     * @return boolean
     */
    public static boolean isValidEmail(String emailToCheck) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (emailToCheck.matches(emailPattern))
            return true;
        else
            return false;
    }

    /**
     * Show Toast on screen.
     * 
     * @param context Context of the class which calls this method.
     * @param textToToast
     * @param longToast
     */
    public static void showToast(Context context, String textToToast, Boolean longToast) {
        if (longToast)
            Toast.makeText(context, textToToast, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns HTML string which will be loaded in the webview in Share
     * Activity.
     * 
     * @param url
     * @return html
     */
    public static String getShareableHTML(String url) {
        String html = "<a href=\"" + url + "\">" + url + "</a>";
        return html;
    }

    /**
     * Checks for data connection availability
     * 
     * @param context
     * @return true/false
     */
    public static Boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getGetRequestUrl(String url, Context context) {
        Values values = new Values(context);
        String authTokenString = "auth_token="+values.getAuthToken();
        if (url.indexOf("?") >= 0
                && (url.indexOf("?") < url.indexOf("#") || url.indexOf("#") == -1)) {
            return url.replace("?", "?" + authTokenString + "&");
            // else if there is an anchor
        } else if (url.indexOf("#") >= 0) {
            return url.replace("#", "?" + authTokenString + "#");
        } else {
            return url + "?" + authTokenString;
        }
    }
    
    public static String encodeURIComponent(String s)
    {
      String result = null;

      try
      {
        result = URLEncoder.encode(s, "UTF-8")
                           .replaceAll("\\+", "%20")
                           .replaceAll("\\%21", "!")
                           .replaceAll("\\%27", "'")
                           .replaceAll("\\%28", "(")
                           .replaceAll("\\%29", ")")
                           .replaceAll("\\%7E", "~");
      }

      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;
      }

      return result;
    }  

}
