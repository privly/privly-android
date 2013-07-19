package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

        // Checks if the base url is set, if not redirects to settings page else
        // to the login page.
        // This can be extended to a splash screen
        Values values = new Values(getApplicationContext());
        prefsName = values.getPrefsName();
        SharedPreferences settings = getSharedPreferences(prefsName, 0);
        String base_url = settings.getString("base_url", null);

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