package ly.priv.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ly.priv.mobile.PrivlyLinkStorageContract.LinksDb;

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
	 * Show Toast on screen.
	 *
	 * @param {Context} context Context of the class which calls this method.
	 * @param {String} textToToast
	 * @param {String} longToast
	 */
	public static void showToast(Context context, String textToToast,
			Boolean longToast) {
		if (longToast)
			Toast.makeText(context, textToToast, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Returns HTML string which will be loaded in the webview in Share
	 * Activity.
	 *
	 * @param {String} url
	 * @return {String} html Returns the HTML String which is used to display
	 *         Privly link in the WebView
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
	 * @param {String} url The Url to be displayed in the WebWiev
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
	 *         string.
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

	/**
	 * Checks if a link from a source already exists in the database.
	 *
	 * @param context
	 * @param id
	 * @param sourceOfLink
	 * @return exists
	 */
	public static Boolean ifLinkExistsInDb(Context context, String url,
			String sourceOfLink) {
		Boolean exists = false;
		LinksDbHelper mDbHelper = new LinksDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Log.d("SQL", "SELECT * FROM " + LinksDb.TABLE_NAME + " WHERE "
				+ LinksDb.COLUMN_NAME_LINK + " like '" + url + "' AND "
				+ LinksDb.COLUMN_NAME_SOURCE + " like '" + sourceOfLink + "'");
		Cursor cursor = db.rawQuery("SELECT * FROM " + LinksDb.TABLE_NAME
				+ " WHERE " + LinksDb.COLUMN_NAME_LINK + " like '" + url
				+ "' AND " + LinksDb.COLUMN_NAME_SOURCE + " like '"
				+ sourceOfLink + "'", null);
		if (cursor.getCount() > 0)
			exists = true;

		Log.d("cursor count", String.valueOf(cursor.getCount()));

		Log.d("exists", exists.toString());
		db.close();
		cursor.close();
		return exists;

	}

	/**
	 * Copy database from application's private storage to external storage.
	 * Useful for debugging.
	 */

	public static void copyDb() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//ly.priv.mobile//databases//PrivlyLinks.db";
				String backupDBPath = "PrivlyLinks.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);
				FileChannel src = null;
				FileChannel dst = null;
				if (currentDB.exists()) {
					try {
						src = new FileInputStream(currentDB).getChannel();
						dst = new FileOutputStream(backupDB).getChannel();
						dst.transferFrom(src, 0, src.size());
						src.close();
						dst.close();
					} catch (Exception e) {

					} finally {
						try {
							src.close();
						} catch (Exception e) {
						}
						try {
							dst.close();
						} catch (Exception e) {
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Insert Links into the Database.
	 *
	 * @param {Context} context Application Context
	 * @param {String} source Source of Privly Link (FACEBOOK, TWITTER etc)
	 * @param {String} url Privly Link
	 * @param {String} id unique identifier on the Source Server of the message
	 * @param {String} userName Name of the user who sent the message / email
	 *        /tweet.
	 *
	 */

	public static void insertIntoDb(Context context, String source, String url,
			String id, String userName) {
		LinksDbHelper mDbHelper = new LinksDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put(LinksDb.COLUMN_NAME_SOURCE, source);
		contentValues.put(LinksDb.COLUMN_NAME_LINK, url);

		contentValues.put(LinksDb.COLUMN_NAME_SOURCE_ID, id);
		contentValues.put(LinksDb.COLUMN_NAME_FROM, userName);
		try {
			long newRowId = db.insert(LinksDb.TABLE_NAME, null, contentValues);
			Log.d("newrowid", String.valueOf(newRowId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentValues.clear();
		db.close();
	}
}
