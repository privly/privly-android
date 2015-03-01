package ly.priv.mobile.gui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;


/**
 * Shows the Settings Activity. Currently, supports setting of the domain name
 * with which the application works.
 * 
 * @author Shivam Verma
 */
public class SettingsActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	private static final String TAG = "SettingsActivity";
	private String mBaseUrl;
	private Button mSave;
	private Intent mGotoLogin;
	private EditText mUrlEditText;
	private Values mValues;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		getSupportActionBar().hide();
		TextView baseUrlHeading = (TextView) findViewById(R.id.enterBaseUrlHeading);
		Typeface lobster = Typeface.createFromAsset(getAssets(),
				"fonts/Lobster.ttf");
		baseUrlHeading.setTypeface(lobster);
		Log.d(TAG, "Settings");
		mGotoLogin = new Intent(this, LoginActivity.class);
		mSave = (Button) findViewById(R.id.save);
		mUrlEditText = (EditText) findViewById(R.id.baseUrlEditText);

		mValues = new Values(getApplicationContext());
		mBaseUrl = mValues.getContentServerDomain();

		if (mBaseUrl != null)
			mUrlEditText.setText(mBaseUrl);

		// Saves the content server domain to Shared Preferences
		mSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mBaseUrl = mUrlEditText.getText().toString();
				if (!mBaseUrl.equalsIgnoreCase("")) {
					mValues.setBaseUrl(mBaseUrl);
					Utilities.showToast(getApplicationContext(),
							getString(R.string.saved_please_login_now), true);

					// Set authToken as null and redirect to login. This'll make
					// sure that the user is authenticated with the new content
					// server.
					mValues.setAuthToken(null);
					startActivity(mGotoLogin);
				} else
					Utilities.showToast(getApplicationContext(),
							getString(R.string.please_enter_a_valid_url), true);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_layout_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.logout:
			mValues.setAuthToken(null);
			mValues.setRememberMe(false);
			Intent gotoLogin = new Intent(this, LoginActivity.class);
			gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(gotoLogin);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
