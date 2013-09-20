package ly.priv.mobile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Allows the user to create new Privly Content. Integrates the Posting
 * Applications with the Android Application.
 *
 * @author Shivam Verma
 */
public class NewPost extends Activity {

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_post);
		Bundle bundle = getIntent().getExtras();

		// Fetch selected Js app's name and load the respective JS app into
		// WebView.

		if (bundle.getString("JsAppName") != null) {
			String JsAppName = bundle.getString("JsAppName");
			WebView w = (WebView) findViewById(R.id.webview_1);
			w.getSettings().setJavaScriptEnabled(true);
			w.addJavascriptInterface(new JsObject(this), "androidJsBridge");

			if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
				w.getSettings().setAllowUniversalAccessFromFileURLs(true);
			// Logs all Js Console messages on the logcat.
			w.setWebChromeClient(new WebChromeClient() {
				@Override
				public boolean onConsoleMessage(ConsoleMessage cm) {
					Log.d("JsApplication",
							cm.message() + " -- From line " + cm.lineNumber()
									+ " of " + cm.sourceId());
					return true;
				}
			});

			w.loadUrl("file:///android_asset/PrivlyApplications/" + JsAppName
					+ "/new.html");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_layout_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings :
				Intent gotoSettings = new Intent(this, Settings.class);
				startActivity(gotoSettings);
				return true;
			case R.id.logout :
				//Logout user from the Privly Android Application
				Values values = new Values(getApplicationContext());
				values.setAuthToken(null);
				values.setRememberMe(false);
				Intent gotoLogin = new Intent(this, Login.class);
				gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(gotoLogin);
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

}
