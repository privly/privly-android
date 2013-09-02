package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Redirects user to {@link ly.priv.mobile.Login} Login Screen or
 * {@link ly.priv.mobile.Settings} Settings Screen to setup the domain name with
 * which the application works.
 *
 * @author Shivam Verma
 */
public class PrivlyActivity extends Activity {
	/** Called when the activity is first created. */
	Intent gotoSettings, gotoLogin;

	String prefsName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Checks if the content server is set, if not, redirects to settings
		// page else
		// to the login page.
		Values values = new Values(getApplicationContext());
		String base_url = values.getBaseUrl();

		if (base_url == null) {
			gotoSettings = new Intent(getApplicationContext(), Settings.class);
			startActivity(gotoSettings);
		} else {
			gotoLogin = new Intent(getApplicationContext(), Login.class);
			startActivity(gotoLogin);
		}

		// finish current activity so that it doesn't remain in the history
		// stack.
		finish();
	}

}
