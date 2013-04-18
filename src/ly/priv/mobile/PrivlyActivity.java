package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PrivlyActivity extends Activity {
    /** Called when the activity is first created. */
	Intent go_to_settings, go_to_login;
	public static final String prefs_name = "prefs_file";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences settings = getSharedPreferences(prefs_name, 0);
        //editor.putBoolean("silentMode", mSilentMode);
        String base_url = settings.getString("base_url", null);
        if(base_url == null)
        {
        	go_to_settings = new Intent(getApplicationContext(), settings.class );
        	Log.d("ujdsjkfhs", go_to_settings.toString());
        	Toast.makeText(getApplicationContext(), "yotyu"+go_to_settings, Toast.LENGTH_LONG).show();
        	
        		startActivity(go_to_settings);
        	
        	
        		Toast.makeText(getApplicationContext(), "yo", Toast.LENGTH_SHORT).show();
        	
        }
        else
        {
        	go_to_login = new Intent(getApplicationContext(), login.class);
        	startActivity(go_to_login);
        }    
    }
    
}