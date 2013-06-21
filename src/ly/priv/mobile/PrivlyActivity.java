package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PrivlyActivity extends Activity {
    /** Called when the activity is first created. */
	Intent go_to_settings, go_to_login;
	String prefs_name; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        Checks if the base url is set, if not redirects to settings page else to the login page. 
//        This can be extended to a splash screen 
        Values values = new Values();
        prefs_name = values.getPrefs_name();
        SharedPreferences settings = getSharedPreferences(prefs_name, 0);
        String base_url = settings.getString("base_url", null);
                
        if(base_url == null)
        {
        	go_to_settings = new Intent(getApplicationContext(), settings.class );
        	startActivity(go_to_settings);
        	
        }
        else
        {
        	go_to_login = new Intent(getApplicationContext(), login.class);
        	startActivity(go_to_login);
        }    
    }
    
}