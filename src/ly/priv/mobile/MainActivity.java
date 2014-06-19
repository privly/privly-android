package ly.priv.mobile;

import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {
	Uri uri;
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate MainActivity");
		uri = getIntent().getData();
		if(uri!=null){
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new MicroblogListPostsActivity()).commit();
		}else{
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new Home()).commit();
			}
		}	
		
	}
}
