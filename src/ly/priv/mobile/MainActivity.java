package ly.priv.mobile;

import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsActivity;
import ly.priv.mobile.api.gui.socialnetworks.ListUsersFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		if (uri != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MicroblogListPostsActivity())
					.commit();
		} else {
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new Home()).commit();
			}
		}

	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.container);
		if (fragment instanceof ListUsersFragment) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new Home()).commit();
		} else {
			super.onBackPressed();
		}
	}
}
