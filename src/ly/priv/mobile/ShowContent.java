package ly.priv.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import ly.priv.mobile.PrivlyLinkStorageContract.LinksDb;

/**
 * Displays the Home Activity for a user after authentication.
 *
 * <p>
 * <ul>
 * <li>Receive source name from the intent</li>
 * <li>Enable JavaScript for the WebView.</li>
 * <li>Enable JavaScript Interface</li>
 * <li>Setup swipe gesture detector for WebView. Used to move backward and
 * forward through the links Db for the specifc source, Facebook and Twitter</li>
 * <li>Load links for the particular source and load them in the reading
 * application using the WebView</li>
 * </ul>
 * <p>
 *
 * @author Shivam Verma
 */
public class ShowContent extends SherlockFragment {
	/** Called when the activity is first created. */

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	public int swipeMinDistance;
	public int swipeThresholdVelocity;
	public int swipeMaxOffPath;
	WebView urlContentWebView;
	Cursor cursor;
	String contentSource;
	
	public ShowContent(){
		
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.show_content, container, false);
		//Bundle bundle = this.getIntent().getExtras();
		contentSource = getArguments().getString("contentSource");
		getActivity().setTitle(contentSource);
		View webView = view.findViewById(R.id.urlContentWebview);
		urlContentWebView = (WebView) webView;
		setHasOptionsMenu(true);
		urlContentWebView.getSettings().setJavaScriptEnabled(true);

		// Add JavaScript Interface to the WebView. This enables the JS to
		// access Java functions defined in the JsObject Class
		urlContentWebView.addJavascriptInterface(new JsObject(getActivity()),
				"androidJsBridge");

		// Sets whether JavaScript running in the context of a file scheme URL
		// should be allowed to access content from any origin. This includes
		// access to content from other file scheme URLs.
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			urlContentWebView.getSettings()
					.setAllowUniversalAccessFromFileURLs(true);

		// Setup WebView to detect swipes.
		gestureDetector = new GestureDetector(getActivity(), new SwipeGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		webView.setOnTouchListener(gestureListener);

		// Fetch links for the particular source from the database.
		LinksDbHelper mDbHelper = new LinksDbHelper(getActivity());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		File database = getActivity().getDatabasePath(
				"PrivlyLinks.db");

		// Check if database exists, If not, redirect to Home Screen. Else, load
		// links from Db.
		if (!database.exists()) {
			Toast.makeText(getActivity(),
					"No Privly Links found for" + contentSource,
					Toast.LENGTH_LONG).show();
			Fragment goToIndex = new Index();
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			Bundle bundle_2 = new Bundle();
			bundle_2.putBoolean("isRedirected", true);
			goToIndex.setArguments(bundle_2);
			Log.d("fragments", "go index");
			transaction.replace(R.id.container, goToIndex);
			transaction.commit();

		} else {
			cursor = db.rawQuery("SELECT * FROM " + LinksDb.TABLE_NAME
					+ " WHERE " + LinksDb.COLUMN_NAME_SOURCE + "= '"
					+ contentSource + "'", null);

			int numRows = cursor.getCount();
			if (numRows > 0) {
				cursor.moveToFirst();
				loadUrlInWebview();
			} else {
				Toast.makeText(getActivity(),
						"No Privly Links found for " + contentSource,
						Toast.LENGTH_LONG).show();
				Fragment goToIndex = new Index();
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				Bundle bundle_2 = new Bundle();
				bundle_2.putBoolean("isRedirected", true);
				goToIndex.setArguments(bundle_2);
				Log.d("fragments", "go home");
				transaction.replace(R.id.container, goToIndex);
				transaction.commit();
			}

		}
		Log.d("fragments", "Inside Show");
		return view;
	}

	/**
	 * Swipe gesture listener.
	 * <p>
	 * <ul>
	 * <li>Moves the Db cursor back and forth depending on the swipe.</li>
	 * <li>Calls loadUrlInWebView() method.</li>
	 * </ul>
	 * </p>
	 *
	 */
	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				Values values = new Values(getActivity());
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
						Toast.makeText(getActivity(),
								"Loading Next Post", Toast.LENGTH_SHORT).show();

					if (cursor.moveToNext()) {
						loadUrlInWebview();
					}

				} else if (e2.getX() - e1.getX() > valuesForSwipe
						.get("swipeMinDistance")
						&& Math.abs(velocityX) > valuesForSwipe
								.get("swipeThresholdVelocity")) {
					if (!cursor.isFirst())
						Toast.makeText(getActivity(),
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

	/**
	 * Loads a Privly URL into the Reading Application.
	 *
	 * <p>
	 * <ul>
	 * <li>Fetch link from Database Cursor</li>
	 * <li>Encode URL</li>
	 * <li>Create URL for Reading App</li>
	 * <li>Load URL into the WebView</li>
	 * </ul>
	 * </p>
	 *
	 */
	void loadUrlInWebview() {
		String privlyLink;
		try {
			privlyLink = cursor.getString(cursor
					.getColumnIndex(LinksDb.COLUMN_NAME_LINK));
		} catch (Exception e) {
			privlyLink = "nothing";
			e.printStackTrace();
		}

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
		urlContentWebView.loadUrl("file:///android_asset/" + urlForExtension);

	}

	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//MenuInflater menuInflater = getMenuInflater();
		inflater.inflate(R.layout.menu_layout_show_content, menu);
		//return true;
	}

	/**
	 * Item click listener for options menu.
	 * <p>
	 * Redirect to {@link ly.priv.mobile.Settings} Or
	 * {@link ly.priv.mobile.Login}
	 * </p>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.logout :
				// Logs out User from Privly Application
				Values values = new Values(getActivity());
				values.setAuthToken(null);
				values.setRememberMe(false);
				Intent gotoLogin = new Intent(getActivity(), Login.class);
				gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(gotoLogin);
				return true;

			default :
				return super.onOptionsItemSelected(item);
		}
	}

}
