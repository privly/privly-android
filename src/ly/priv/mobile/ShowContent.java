package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
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
 * social / email feed.
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
	String contentSource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_content);
		Bundle bundle = this.getIntent().getExtras();
		contentSource = bundle.getString("contentSource");
		View webView = findViewById(R.id.urlContentWebview);
		urlContentWebView = (WebView) webView;

		urlContentWebView.getSettings().setJavaScriptEnabled(true);
		urlContentWebView.addJavascriptInterface(new JsObject(this),
				"androidJsBridge");
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			urlContentWebView.getSettings()
					.setAllowUniversalAccessFromFileURLs(true);

		// Setup WebView to detect swipes.
		gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		webView.setOnTouchListener(gestureListener);

		LinksDbHelper mDbHelper = new LinksDbHelper(getApplicationContext());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		File database = getApplicationContext().getDatabasePath(
				"PrivlyLinks.db");

		if (!database.exists()) {

			Log.i("Database", "Not Found");
		} else {
			Log.i("Database", "Found");
		}

		cursor = db.rawQuery("SELECT * FROM " + LinksDb.TABLE_NAME + " WHERE "
				+ LinksDb.COLUMN_NAME_SOURCE + "= '" + contentSource + "'",
				null);

		int numRows = cursor.getCount();
		if (numRows > 0) {
			cursor.moveToFirst();
			loadUrlInWebview();
		} else {
			Toast.makeText(getApplicationContext(),
					"No Privly Links found for" + contentSource,
					Toast.LENGTH_LONG).show();
			Intent goToHome = new Intent(this, Home.class);
			startActivity(goToHome);
			finish();
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
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String urlForExtension = "";
		if (url.indexOf("privlyInjectableApplication%3DZeroBin") > 0 || // deprecated
				url.indexOf("privlyApp%3DZeroBin") > 0) {
			urlForExtension = "PrivlyApplications/ZeroBin/show.html?privlyOriginalURL="
					+ url;
		} else if (url.indexOf("privlyInjectableApplication%3DPlainPost") > 0 || // deprecated
				url.indexOf("privlyApp%3DPlainPost") > 0) {
			urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
					+ url;
		} else {
			urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
					+ url;
		}
		Log.d("url for extension", url);
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
				if (e1.getX() - e2.getX() > valuesForSwipe
						.get("swipeMinDistance")
						&& Math.abs(velocityX) > valuesForSwipe
								.get("swipeThresholdVelocity")) {
					if (!cursor.isLast())
						Toast.makeText(getApplicationContext(),
								"Loading Next Post", Toast.LENGTH_SHORT).show();

					if (cursor.moveToNext()) {
						loadUrlInWebview();
					}

				} else if (e2.getX() - e1.getX() > valuesForSwipe
						.get("swipeMinDistance")
						&& Math.abs(velocityX) > valuesForSwipe
								.get("swipeThresholdVelocity")) {
					if (!cursor.isFirst())
						Toast.makeText(getApplicationContext(),
								"Loading Previous Post", Toast.LENGTH_SHORT)
								.show();
					if (cursor.moveToPrevious()) {
						loadUrlInWebview();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		// This method should always return true to detect swipes.
		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

	}

}
