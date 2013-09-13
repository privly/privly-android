package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * Shows the newly generated Privly URL and allows the user to share it to
 * various platforms.
 *
 * @author Shivam Verma
 */
public class Share extends Activity {
	/** Called when the activity is first created. */
	String newPrivlyUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);

		TextView newUrlHeader = (TextView) findViewById(R.id.newUrlHeader);
		Typeface lobster = Typeface.createFromAsset(getAssets(),
				"fonts/Lobster.ttf");
		newUrlHeader.setTypeface(lobster);

		// Receive new Privly URL from intent

		Bundle bundle = getIntent().getExtras();
		newPrivlyUrl = bundle.getString("newPrivlyUrl");
		WebView urlWebview = (WebView) findViewById(R.id.urlWebview);
		/**
		 * Load HTML content of the form <a href="http://priv.ly#params">
		 * http://priv.ly#params </a> in the WebView
		 */
		String html = Utilities.getShareableHTML(newPrivlyUrl);
		urlWebview.loadData(html, "text/html", "utf-8");

		/**
		 * Show sharing intent on Share button Click
		 */
		Button shareButton = (Button) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {

			// Shows all sharing options with the following intent.
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, newPrivlyUrl);
				try {
					startActivity(Intent.createChooser(intent,
							"Share Privly Url"));
				} catch (android.content.ActivityNotFoundException ex) {
					// (handle error)
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_layout_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.logout :
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
