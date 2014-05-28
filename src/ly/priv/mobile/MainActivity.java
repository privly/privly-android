package ly.priv.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {
	Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new Home()).commit();
		}
	}

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
