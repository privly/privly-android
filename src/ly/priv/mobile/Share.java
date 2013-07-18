
package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Shows the newly generated Privly URL and allows the user to share it to
 * various platfoms.
 * 
 * @author Shivam Verma
 */
public class Share extends Activity {
    /** Called when the activity is first created. */
    String newPrivlyUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        Utilities.showToast(this, "Share", false);
        Bundle bundle = getIntent().getExtras();
        newPrivlyUrl = bundle.getString("newPrivlyUrl");
        WebView urlWebview = (WebView)findViewById(R.id.urlWebview);
        urlWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        String html = Utilities.getShareableHTML(newPrivlyUrl);
        urlWebview.loadData(html, "text/html", "utf-8");

        Button shareButton = (Button)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {

            // Shows all sharing options with the following intent.
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, newPrivlyUrl);
                try {
                    startActivity(Intent.createChooser(intent, "Share Privly Url"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // (handle error)
                }
            }
        });
    }

}
