package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class shows the Settings Activity. Currently, supports setting of the
 * domain name with which the application works.
 *
 * @author Shivam Verma
 */
public class Settings extends Activity {
	/** Called when the activity is first created. */
	String prefsName, baseUrl;
	Button save;
	Intent gotoLogin;
	EditText urlEditText;
	SharedPreferences settings;
	Values values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_layout);

		TextView baseUrlHeading = (TextView) findViewById(R.id.enterBaseUrlHeading);
		Typeface lobster = Typeface.createFromAsset(getAssets(),
				"fonts/Lobster.ttf");
		baseUrlHeading.setTypeface(lobster);
		Log.d("Settings", "Settings");
		gotoLogin = new Intent(this, Login.class);
		save = (Button) findViewById(R.id.save);
		urlEditText = (EditText) findViewById(R.id.baseUrlEditText);

		values = new Values(getApplicationContext());
		baseUrl = values.getContentServerDomain();

		if (baseUrl != null)
			urlEditText.setText(baseUrl);

		// Saves the content server domain to Shared Preferences
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				baseUrl = urlEditText.getText().toString();
				if (!baseUrl.equalsIgnoreCase("")) {
					values.setBaseUrl(baseUrl);
					Toast.makeText(getApplicationContext(),
							"Saved! Please login now", Toast.LENGTH_SHORT)
							.show();

					// Set authToken as null and redirect to login. This'll make
					// sure that the user is authenticated with the new content
					// server.
					Values values = new Values(getApplicationContext());
					values.setAuthToken(null);
					startActivity(gotoLogin);
				} else
					Utilities.showToast(getApplicationContext(),
							"Please enter a valid URL", true);
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
