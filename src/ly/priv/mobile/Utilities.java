
package ly.priv.mobile;

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

    public static String getShareableHTML(String url){
        String html = "<a href=\""+url+"\">"+url+"</a>";
        return html;
    }

    public static Boolean isDataConnectionAvailable( Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
