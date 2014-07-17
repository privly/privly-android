package ly.priv.mobile.gui;

import ly.priv.mobile.ConstantValues;
import ly.priv.mobile.JsObject;
import ly.priv.mobile.R;
import ly.priv.mobile.Values;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Allows the user to create new Privly Content. Integrates the Posting
 * Applications with the Android Application.
 * 
 * @author Shivam Verma
 */
public class NewPostFragment extends SherlockFragment {
	private static final String TAG = "NewPost";

	public NewPostFragment() {

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.new_post, container, false);
		Bundle bundle = getArguments();
		setHasOptionsMenu(true);
		// Fetch selected Js app's name and load the respective JS app into
		// WebView.

		if (bundle.getString(ConstantValues.JS_APP_NAME) != null) {
			String JsAppName = bundle.getString(ConstantValues.JS_APP_NAME);
			getActivity().setTitle(JsAppName);
			WebView w = (WebView) view.findViewById(R.id.webview_new_post);
			w.getSettings().setJavaScriptEnabled(true);
			w.addJavascriptInterface(new JsObject(getActivity()),
					"androidJsBridge");

			if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
				w.getSettings().setAllowUniversalAccessFromFileURLs(true);
			// Logs all Js Console messages on the logcat.
			w.setWebChromeClient(new WebChromeClient() {
				@Override
				public boolean onConsoleMessage(ConsoleMessage cm) {
					Log.d(TAG,
							cm.message() + " -- From line " + cm.lineNumber()
									+ " of " + cm.sourceId());
					return true;
				}
			});

			w.loadUrl("file:///android_asset/PrivlyApplications/" + JsAppName
					+ "/new.html");
			w.loadUrl("javascript: window.onload = function() {document.getElementsByClassName('navbar-toggle')[0].style.visibility = 'hidden';"
					+ "document.getElementsByClassName('collapse navbar-collapse')[0].style.visibility = 'hidden';}");

		}
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.layout.menu_layout_home, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent gotoSettings = new Intent(getActivity(),
					SettingsActivity.class);
			startActivity(gotoSettings);
			return true;
		case R.id.logout:
			// Logout user from the Privly Android Application
			Values values = new Values(getActivity());
			values.setAuthToken(null);
			values.setRememberMe(false);
			Intent gotoLogin = new Intent(getActivity(), LoginActivity.class);
			gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(gotoLogin);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
