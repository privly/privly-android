
package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class shows the Settings Activity. Currently, supports setting of the
 * domainName with which the application works.
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
        Typeface lobster = Typeface.createFromAsset(getAssets(), "fonts/Lobster.ttf");
        baseUrlHeading.setTypeface(lobster);
        Log.d("Settings", "Settings");
        gotoLogin = new Intent(this, Login.class);
        save = (Button)findViewById(R.id.save);
        urlEditText = (EditText)findViewById(R.id.baseUrlEditText);

        values = new Values(getApplicationContext());
        baseUrl = values.getBaseUrl();

        if (baseUrl != null)
            urlEditText.setText(baseUrl);

        // Saves the base url to Shared Preferences

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                baseUrl = urlEditText.getText().toString();
                if (!baseUrl.equalsIgnoreCase("")) {
                    values.setBaseUrl(baseUrl);
                    Toast.makeText(getApplicationContext(), "Saved! Please login now",
                            Toast.LENGTH_SHORT).show();
                    // Set authToken as null and redirect to login. This'll make
                    // sure that the user is authenticated with the new content
                    // server.
                    Values values = new Values(getApplicationContext());
                    values.setAuthToken(null);
                    startActivity(gotoLogin);
                } else
                    Utilities.showToast(getApplicationContext(), "Please enter a valid URL", true);
            }
        });

    }
}
