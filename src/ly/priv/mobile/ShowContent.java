package ly.priv.mobile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * This class displays the Home Activity for a user after authentication. Gives
 * the user options to Create New Privly posts or Read Privly Posts from his
 * social / email feed. Read option has not been implemented yet.
 * 
 * @author Shivam Verma
 */
public class ShowContent extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_content);

		WebView urlContentWebView = (WebView) findViewById(R.id.urlContentWebview);
		String url = "https://privlyalpha.org/apps/PlainPost/show?privlyApp=PlainPost&privlyInject1=true&random_token=ec23a7da5c&privlyDataURL=https%3A%2F%2Fprivlyalpha.org%2Fposts%2F1020.json%3Frandom_token%3Dec23a7da5c";
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	url = Utilities.encodeURIComponent(url);
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
		urlContentWebView.getSettings().setJavaScriptEnabled(true);
		urlContentWebView.addJavascriptInterface(new JsObject(this),
				"androidJsBridge");
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			urlContentWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		urlContentWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("JsApplication",
						cm.message() + " -- From line " + cm.lineNumber()
								+ " of " + cm.sourceId());
				return true;
			}
		});
		urlContentWebView.loadUrl("file:///android_asset/" + urlForExtension);
	}

}
