package ly.priv.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity holds all the fragments which are intended to have a navigation
 * drawer. Also provides an interface to pass on twitter login data to
 * appropriate fragment.
 * 
 * @author Gitanshu
 * 
 */
public class MainActivity extends SherlockFragmentActivity {
	Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// loads 'Home' as the default fragment
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new Home()).commit();
		}
	}

	/**
	 * The twitter api returns the login data in form of an intent which can be captured
	 * by the activity using onNewIntent method. 
	 * When the intent is received, the MainActivity sends the intent to 
	 * TwitterLinkGrabberService through the NewIntentListener interface.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		NewIntentListener newIntentListener = (NewIntentListener) this
				.getSupportFragmentManager().findFragmentByTag("Twitter");
		newIntentListener.onNewIntentRead(intent);
	}

	public interface NewIntentListener {
		public void onNewIntentRead(Intent intent);
	}
}
