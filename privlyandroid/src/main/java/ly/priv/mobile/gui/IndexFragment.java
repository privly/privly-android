package ly.priv.mobile.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;

import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.JsObject;
import ly.priv.mobile.R;
import ly.priv.mobile.SettingsActivityNew;
import ly.priv.mobile.utils.Values;
import ly.priv.mobile.VerifyAuthToken;

/**
 * Displays the Index application for a user after authentication.
 * 
 * @author Gitanshu Sardana
 */

public class IndexFragment extends Fragment {
	private static final String TAG = "IndexFragment";
	private WebView mWebViewIndex;

	public IndexFragment() {

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.new_post, container, false);
		Log.d(TAG, "onCreate");
		String indexTitle = getString(R.string.history);
		getActivity().setTitle(indexTitle);
		container.removeAllViews();
		mWebViewIndex = (WebView) view.findViewById(R.id.webview_new_post);
		mWebViewIndex.getSettings().setJavaScriptEnabled(true);
        mWebViewIndex.getSettings().setDomStorageEnabled(true);
		Boolean isRedirected = null;
		try {
			isRedirected = getArguments().getBoolean(
					ConstantValues.IS_REDIRECTED);
		} catch (NullPointerException e) {
			Log.d(TAG, "Is Redirected >>> " + isRedirected);
			Values values = new Values(getActivity());
			// Checks if the User has already been verified at the Login Screen.
			// If yes, prevents re authentication. If not, creates and executes
			// a VerifyAuthToken task.
			if (!values.isUserVerifiedAtLogin()) {
				VerifyAuthToken task = new VerifyAuthToken(getActivity());
				task.execute(values.getContentServer()
						+ "/token_authentications.json");
			} else
				values.setUserVerifiedAtLogin(false);
		}
		loadIndex();
		return view;
	}

	@SuppressLint("NewApi")
	void loadIndex() {
		mWebViewIndex.addJavascriptInterface(new JsObject(getActivity()),
				"androidJsBridge");

		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			mWebViewIndex.getSettings().setAllowUniversalAccessFromFileURLs(
					true);
		}
		// Logs all Js Console messages on the logcat.
		mWebViewIndex.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d(TAG, cm.message() + " -- From line " + cm.lineNumber()
						+ " of " + cm.sourceId());
				return true;
			}
		});
		mWebViewIndex
				.loadUrl("file:///android_asset/PrivlyApplications/Index/new.html");
	}

	/**
	 * Inflate options menu with the layout
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.menu_layout_home, menu);
		menu.findItem(R.id.logout).setIcon(
				new IconDrawable(getActivity(), IconValue.fa_sign_out)
						.actionBarSize());
		menu.findItem(R.id.settings).setIcon(
				new IconDrawable(getActivity(), IconValue.fa_cog)
						.actionBarSize());
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Item click listener for options menu.
	 * <p>
	 * Redirect to {@link ly.priv.mobile.gui.SettingsActivity} Or
	 * {@link ly.priv.mobile.gui.LoginActivity}
	 * </p>
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.settings:
			Intent gotoSettings = new Intent(getActivity(),
					SettingsActivityNew.class);
			startActivity(gotoSettings);
			return true;

		case R.id.logout:
			// Logs out User from Privly Application
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
