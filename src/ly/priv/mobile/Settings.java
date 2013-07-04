package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class shows the Settings Activity. Currently, supports setting of the
 * domainName with which the application works.
 * 
 * @author Shivam Verma
 * 
 */
public class Settings extends Activity {
	/** Called when the activity is first created. */
	String prefsName;
	String baseURL;
	Button save;
	Intent gotoLogin;
	EditText urlEditText;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_layout);
		Log.d("Settings", "Settings");
		gotoLogin = new Intent(this, Login.class);
		save = (Button) findViewById(R.id.save);
		urlEditText = (EditText) findViewById(R.id.base_);

		Values values = new Values();
		prefsName = values.getPrefsName();
		settings = getSharedPreferences(prefsName, 0);
		baseURL = settings.getString("base_url", null);

		if (baseURL != null)
			urlEditText.setText(baseURL);

		// Saves the base url to Shared Preferences

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				urlEditText = (EditText) findViewById(R.id.base_);
				baseURL = urlEditText.getText().toString();
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("base_url", baseURL);
				editor.commit();
				Toast.makeText(getApplicationContext(), "Saved!",
						Toast.LENGTH_SHORT).show();

				// Redirect to Login once saved
				startActivity(gotoLogin);
			}
		});

	}

}