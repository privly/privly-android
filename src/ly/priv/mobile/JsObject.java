package ly.priv.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsObject {
	
	Context c;
	JsObject(Context calling_context)
	{
		c = calling_context;
	}
	
    @JavascriptInterface
    public String getDeviceVersion() { 
    	String deviceVersion= Build.VERSION.RELEASE;
    	Log.d("androidJSBridge Version Request",deviceVersion);
    	return deviceVersion; }
    
    @JavascriptInterface 
    public void receiveNewPrivlyURL(String url)
    {
    	Log.d("androidJSBridge URL Received", url);
    	Utilities.showToast(c, url, true);
    	Intent gotoShare = new Intent(c, Share.class);
    	gotoShare.putExtra("newPrivlyUrl", url);    
    	c.startActivity(gotoShare);
    }
    
    @JavascriptInterface 
    public String fetchPlatformName()
    {
    	Log.d("androidJSBridge Request", "Platform Identification");
    	return "ANDROID";
    }
    
    @JavascriptInterface 
    public String fetchAuthToken()
    {
    	SharedPreferences sharedPrefs;
    	
    	Values values = new Values();
        String prefs_name = values.getPrefsName();
        sharedPrefs = c.getSharedPreferences(prefs_name, 0);
        return sharedPrefs.getString("auth_token", null);
    }
    
    @JavascriptInterface 
    public String fetchDomainName()
    {
    	SharedPreferences sharedPrefs;
    	Values values = new Values();
        String prefs_name = values.getPrefsName();
        sharedPrefs = c.getSharedPreferences(prefs_name, 0);
        return sharedPrefs.getString("base_url", null);
    }
 }