package ly.priv.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import ly.priv.mobile.PrivlyLinkStorageContract.LinksDb;

/**
 * This class displays the Home Activity for a user after authentication. Gives
 * the user options to Create New Privly posts or Read Privly Posts from his
 * social / email feed. Read option has not been implemented yet.
 *
 * @author Shivam Verma
 */
public class ShowContent extends Activity {
	/** Called when the activity is first created. */

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	public int swipeMinDistance;
	public int swipeThresholdVelocity;
	public int swipeMaxOffPath;
	WebView urlContentWebView;
	Cursor cursor;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_content);
		Log.d("tag", "numuou");

		View webView = findViewById(R.id.urlContentWebview);
		urlContentWebView = (WebView) webView;
		Log.d("tag", "num1");
		urlContentWebView.getSettings().setJavaScriptEnabled(true);
		urlContentWebView.addJavascriptInterface(new JsObject(this),
				"androidJsBridge");
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			urlContentWebView.getSettings()
					.setAllowUniversalAccessFromFileURLs(true);
		urlContentWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("JsApplication",
						cm.message() + " -- From line " + cm.lineNumber()
								+ " of " + cm.sourceId());
				return true;
			}
		});
		Log.d("tag", "num2");

		// Setup WebView to detect swipes.
		gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		webView.setOnTouchListener(gestureListener);
		Log.d("tag", "num3");

		LinksDbHelper mDbHelper = new LinksDbHelper(getApplicationContext());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Log.d("tag", "num4");
		File database = getApplicationContext().getDatabasePath(
				"PrivlyLinks.db");

		if (!database.exists()) {
			// Database does not exist so copy it from assets here
			Log.i("Database", "Not Found");
		} else {
			Log.i("Database", "Found");
		}

		String[] projection = { LinksDb._ID, LinksDb.COLUMN_NAME_LINK,
				LinksDb.COLUMN_NAME_SOURCE };

				cursor = db.query(LinksDb.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);
		Log.d("tag", "after cursor");
		int numRows = cursor.getCount();
		if (numRows > 0) {
			cursor.moveToFirst();
			Log.d("num_rows_2", Long.toString(numRows));

			loadUrlInWebview();
		}

	}

	void loadUrlInWebview() {
		String privlyLink;
		try {
			privlyLink = cursor.getString(cursor
					.getColumnIndex(LinksDb.COLUMN_NAME_LINK));
		} catch (Exception e) {
			privlyLink = "nothing";
			e.printStackTrace();
		}

		Log.d("tag", privlyLink);

		String url = privlyLink;
		try {
			url = URLEncoder.encode(url, "utf-8");
			Log.d("encode once", url);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("encoded URL", url);
		String urlForExtension = "";
		if (url.indexOf("privlyInjectableApplication=ZeroBin") > 0 || // deprecated
				url.indexOf("privlyApp=ZeroBin") > 0) {
			urlForExtension = "PrivlyApplications/ZeroBin/show.html?privlyOriginalURL="
					+ url;
		} else if (url.indexOf("privlyInjectableApplication=PlainPost") > 0 || // deprecated
				url.indexOf("privlyApp=PlainPost") > 0) {
			urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
					+ url;
		} else {
			urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
					+ url;
		}

		Log.d("urlForExtension", urlForExtension);

		urlContentWebView.loadUrl("file:///android_asset/" + urlForExtension);

	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				Values values = new Values(getApplicationContext());
				HashMap<String, Integer> valuesForSwipe = values
						.getValuesForSwipe();
				if (Math.abs(e1.getY() - e2.getY()) > valuesForSwipe
						.get("swipeMaxOffPath"))
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > valuesForSwipe
						.get("swipeMinDistance")
						&& Math.abs(velocityX) > valuesForSwipe
								.get("swipeThresholdVelocity")) {
					Toast.makeText(getApplicationContext(), "Left Swipe",
							Toast.LENGTH_SHORT).show();

					if (cursor.moveToNext()) {
						loadUrlInWebview();
					}

				} else if (e2.getX() - e1.getX() > valuesForSwipe
						.get("swipeMinDistance")
						&& Math.abs(velocityX) > valuesForSwipe
								.get("swipeThresholdVelocity")) {
					Toast.makeText(getApplicationContext(), "Right Swipe",
							Toast.LENGTH_SHORT).show();
					if (cursor.moveToPrevious()) {
						loadUrlInWebview();
					}
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent event) {
			Log.d("tag", "onDown: " + event.toString());
			return true;
		}

	}

}
