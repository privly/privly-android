package ly.priv.mobile.gui;

import ly.priv.mobile.R;
import ly.priv.mobile.Utilities;
import ly.priv.mobile.Values;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Shows the newly generated Privly URL and allows the user to share it to
 * various platforms.
 * 
 * @author Shivam Verma
 */
public class ShareFragment extends SherlockFragment {
	/** Called when the activity is first created. */
	private String mNewPrivlyUrl;

	public ShareFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.share, container, false);
		setHasOptionsMenu(true);
		TextView newUrlHeader = (TextView) view.findViewById(R.id.newUrlHeader);
		Typeface lobster = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lobster.ttf");
		newUrlHeader.setTypeface(lobster);

		// Receive new Privly URL from intent

		Bundle bundle = getArguments();
		mNewPrivlyUrl = bundle.getString("newPrivlyUrl");
		WebView urlWebview = (WebView) view.findViewById(R.id.urlWebview);
		/**
		 * Load HTML content of the form <a href="http://priv.ly#params">
		 * http://priv.ly#params </a> in the WebView
		 */
		String html = Utilities.getShareableHTML(mNewPrivlyUrl);
		urlWebview.loadData(html, "text/html", "utf-8");

		/**
		 * Show sharing intent on Share button Click
		 */
		Button shareButton = (Button) view.findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {

			// Shows all sharing options with the following intent.
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, mNewPrivlyUrl);
				try {
					startActivity(Intent.createChooser(intent,
							getString(R.string.share_privly_url)));
				} catch (android.content.ActivityNotFoundException ex) {
					// (handle error)
				}
			}
		});
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.layout.menu_layout_settings, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.logout:
			Values values = new Values(getActivity());
			values.setAuthToken(null);
			values.setRememberMe(false);
			Intent gotoLogin = new Intent(getActivity(), LoginActivity.class);
			gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(gotoLogin);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
