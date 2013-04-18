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

public class settings extends Activity {
    /** Called when the activity is first created. */
	public static final String prefs_name = "prefs_file";
	String base_url;
	Button save;
	Intent go_to_login;
	EditText base_e;
	SharedPreferences settings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings_layout);
        Log.d("Settings", "Settings");
        go_to_login = new Intent(this, login.class);
        save = (Button)findViewById(R.id.save);
		base_e = (EditText)findViewById(R.id.base_);

        settings = getSharedPreferences(prefs_name, 0);
        base_url = settings.getString("base_url", null);
        
        if(base_url!=null)
        	base_e.setText(base_url);
        
        save.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				base_e = (EditText)findViewById(R.id.base_);
				base_url = base_e.getText().toString();
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putString("base_url", base_url );
		        editor.commit();
		        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
		        startActivity(go_to_login);
			}
		});
        
        
    }
    
}