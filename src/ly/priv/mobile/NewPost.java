package ly.priv.mobile;

import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class NewPost extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("JsAppName")!=null)
        {
    		String JsAppName = bundle.getString("JsAppName");
        	 WebView w = (WebView)findViewById(R.id.webview_1);
             w.getSettings().setJavaScriptEnabled(true); 
             w.addJavascriptInterface(new JsObject(getApplicationContext()), "androidJsBridge");
             if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) 
             	  w.getSettings().setAllowUniversalAccessFromFileURLs(true);
             w.setWebChromeClient(new WebChromeClient() {
             	  public boolean onConsoleMessage(ConsoleMessage cm) {
             	    Log.d("JsApplication", cm.message() + " -- From line "
             	                         + cm.lineNumber() + " of "
             	                         + cm.sourceId() );
             	    return true;
             	  }
             	});
             
             w.loadUrl("file:///android_asset/PrivlyApplications/"+JsAppName+"/new.html");
        }
        
    } 
    
}