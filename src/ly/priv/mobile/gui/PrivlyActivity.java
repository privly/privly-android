package ly.priv.mobile.gui;

import ly.priv.mobile.R;
import ly.priv.mobile.Values;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Redirects user to {@link ly.priv.mobile.gui.LoginActivity} Login Screen or
 * {@link ly.priv.mobile.gui.SettingsActivity} Settings Screen to setup the
 * domain name with which the application works.
 * 
 * @author Shivam Verma
 */
public class PrivlyActivity extends Activity {
	/** Called when the activity is first created. */
	private Intent mGotoSettings, mGotoLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Checks if the content server is set, if not, redirects to settings
		// page else
		// to the login page.
		Values values = new Values(getApplicationContext());
		String base_url = values.getContentServerDomain();

		if (base_url == null) {
			mGotoSettings = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(mGotoSettings);
		} else {
			mGotoLogin = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(mGotoLogin);
		}

		// finish current activity so that it doesn't remain in the history
		// stack.
		finish();
	}

}
