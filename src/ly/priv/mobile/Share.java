package ly.priv.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        EditText privlyUrlTextView = (EditText)findViewById(R.id.newUrlEditText);
        privlyUrlTextView.setText(newPrivlyUrl); 
        
        Button shareButton = (Button)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(Intent.ACTION_SEND);
		        intent.setType("text/plain");
		        intent.putExtra(Intent.EXTRA_TEXT, newPrivlyUrl);

		        try {
		          startActivity(Intent.createChooser(intent, "Select an action"));
		        } catch (android.content.ActivityNotFoundException ex) {
		          // (handle error)
		        }
			}
		});
    } 
    
}