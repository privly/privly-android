package ly.priv.mobile.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ly.priv.mobile.R;

/**
 * Contains simple functions that should be used wherever possible.
 *
 * @author Shivam Verma
 */
public class Utilities {
    /**
     * Check validity of an EMail address using RegEx
     *
     * @param {String} emailToCheck
     * @return {Boolean}
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
     * Suggest email addresses of the user using RegEx at Login screen
     *
     * @param {Context} context Context of the calling class.
     * @param {Boolean} flag that determines whether the last login should be added
     * @param {String}  username that comes from last login
     * @return {Boolean}
     */
    public static Set<String> emailIdSuggestor(Context context, Boolean b, String username) {
        Account[] accounts = AccountManager.get(context).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        if (b) {
            if (Utilities.isValidEmail(username)) {
                emailSet.add(username);
            }
        }
        for (Account account : accounts) {
            if (Utilities.isValidEmail(account.name)) {
                emailSet.add(account.name);
            }
        }
        return emailSet;
    }

    /**
     * Show Toast on screen.
     *
     * @param {Context} context Context of the calling class.
     * @param {String}  textToToast
     * @param {String}  longToast
     */
    public static Boolean showToast(Context context, String textToToast,
                                    Boolean longToast) {
        if (longToast) {
            Toast.makeText(context, textToToast, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * Show dialog on screen.
     *
     * @param activity
     * @param mess
     * @return
     */
    public static AlertDialog showDialog(final Activity activity, String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_info_title);
        builder.setCancelable(true);
        builder.setMessage(mess);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Returns HTML string which will be loaded in the webview in Share
     * Activity.
     *
     * @param {String} url
     * @return {String} html Returns the HTML String which is used to display
     * Privly link in the WebView
     */
    public static String getShareableHTML(String url) {
        String html = "<a href=\"" + url + "\">" + url + "</a>";
        return html;
    }

    /**
     * Checks for data connection availability
     *
     * @param {Context} context
     * @return {Boolean}
     */
    public static Boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Appends the current auth_token to any url.
     *
     * @param {String}  url The Url to be displayed in the WebWiev
     * @param {Context} context Calling Context
     * @return {Boolean}
     */
    public static String getGetRequestUrl(String url, Context context) {
        Values values = new Values(context);
        String authTokenString = "auth_token=" + values.getAuthToken();
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

    /**
     * This method uses regex to find out any Privly URLs in a given String
     *
     * @param {String} message
     * @return {ArrayList<String>} listOfUrls List of Privly Urls contained in a
     * string.
     */
    public static ArrayList<String> fetchPrivlyUrls(String message) {
        ArrayList<String> listOfUrls = new ArrayList<String>();
        String regEx = "(https?://)?[^ ]*privlyInject1[^ ]*";
        Pattern pattern = Pattern.compile(regEx);

        if (message != null) {
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                listOfUrls.add(matcher.group());
            }
        }
        return listOfUrls;
    }

    public static void setHederFont(Activity activity) {
        TextView hederText = (TextView) activity.findViewById(R.id.twHederText);
        Typeface lobster = Typeface.createFromAsset(activity.getAssets(),
                "fonts/Lobster.ttf");
        hederText.setTypeface(lobster);
    }

    /**
     * Conversion Facebook time into local time
     *
     * @param time
     * @return
     * @author Ivan Metla
     */
    public static String getTimeForFacebook(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar fDate = Calendar.getInstance();
        fDate.setTime(date);
        Calendar curDate = Calendar.getInstance();
        if (fDate.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
            if (curDate.get(Calendar.DAY_OF_YEAR) == fDate
                    .get(Calendar.DAY_OF_YEAR)) {
                simpleDateFormat = new SimpleDateFormat("HH:mm");
            } else if (curDate.get(Calendar.WEEK_OF_YEAR) == fDate
                    .get(Calendar.WEEK_OF_YEAR)) {
                simpleDateFormat = new SimpleDateFormat("E, HH:mm");
            } else
                simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
        }
        return simpleDateFormat.format(fDate.getTime());
    }

    /**
     * Conversion Twitter time into local time
     *
     * @return
     * @paramtime
     * @author Ivan Metla
     */
    public static String getTimeForTwitter(Date date) {
        SimpleDateFormat simpleDateFormat;

        Calendar tDate = Calendar.getInstance();
        tDate.setTime(date);
        Calendar curDate = Calendar.getInstance();
        if (tDate.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
            if (curDate.get(Calendar.DAY_OF_YEAR) == tDate
                    .get(Calendar.DAY_OF_YEAR)) {
                simpleDateFormat = new SimpleDateFormat("HH:mm");
            } else if (curDate.get(Calendar.WEEK_OF_YEAR) == tDate
                    .get(Calendar.WEEK_OF_YEAR)) {
                simpleDateFormat = new SimpleDateFormat("E, HH:mm");
            } else
                simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
        }
        return simpleDateFormat.format(tDate.getTime());
    }

    public static String getTimeForGmail(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);

            Calendar fDate = Calendar.getInstance();
            fDate.setTime(date);
            Calendar curDate = Calendar.getInstance();
            if (fDate.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
                if (curDate.get(Calendar.DAY_OF_YEAR) == fDate
                        .get(Calendar.DAY_OF_YEAR)) {
                    simpleDateFormat = new SimpleDateFormat("HH:mm");
                } else if (curDate.get(Calendar.WEEK_OF_YEAR) == fDate
                        .get(Calendar.WEEK_OF_YEAR)) {
                    simpleDateFormat = new SimpleDateFormat("E, HH:mm");
                } else
                    simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm");
            } else {
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
            }
            return simpleDateFormat.format(fDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();

            return "1800 BC";
        }

    }

    /**
     * Check for Null or Whitespace
     *
     * @param string
     * @return
     */
    public static boolean isNullOrWhitespace(String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty();
    }

    public static String getFilePathURLFromAppName(String appName) {
        return "file:///android_asset/PrivlyApplications/" + appName
                + "/new.html";
    }
}