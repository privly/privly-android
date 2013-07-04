package ly.priv.mobile;

import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * 
 * @author Shivam Verma
 * 
 * This class displays the login screen. 
 * Allows the user to authenticate to a Privly Web Server by fetching the auth_token.  
 *
 */
public class Login extends Activity {
    /** Called when the activity is first created. */
	String uname, pwd; 
	Button loginButton;
	String loginResponse;
	String baseURL;
	EditText unameEditText;
	EditText pwdEditText;
	SharedPreferences settings;
	CheckBox rememberMeCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
//        Shared Preference File for storing the domain name, if not privly by default. 
//        Will be extended to store the username and password 
        
        Values values = new Values();
        String prefs_name = values.getPrefsName();
        settings = getSharedPreferences(prefs_name, 0);
        baseURL = settings.getString("base_url", null);
        
//        If no base domain has been defined, 
//        the user is taken to the login screen where he needs to add it.     
        
        if(baseURL == null)
        {
        	Intent settings_it = new Intent(this, Settings.class );
        	startActivity(settings_it);
        }
        else
        {
	        unameEditText = (EditText)findViewById(R.id.uname);
	        pwdEditText = (EditText) findViewById(R.id.pwd);
	        loginButton = (Button)findViewById(R.id.login);
	
//	        Fetch saved username and password and set it to respective text boxes
	        
	        String fetched_user_name = settings.getString("uname", null);
	        String fetched_pwd = settings.getString("pwd", null);
	        
	        if(fetched_pwd !=null && fetched_user_name !=null)
	        {
	        	Log.d("uname", fetched_user_name);
		        Log.d("pwd", fetched_pwd);
	        	unameEditText.setText(fetched_user_name);
	        	pwdEditText.setText(fetched_pwd);
	        }
	        
	        /**
	         * On Login Button Click, A POST Request is made to the server for authentication. 
	         * The Authentication Process is done using AsyncTask to 
	         * prevent blocking of UI Thread. 
	         **/
	        loginButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					uname = unameEditText.getText().toString();
					pwd = pwdEditText.getText().toString();
					rememberMeCheckBox = (CheckBox)findViewById(R.id.remember_me);
					
	//				Remove any unwanted spaces before and after the EmailID and Password
					uname = uname.trim();
					pwd = pwd.trim();
					
	//				Check if Email is Valid using RegEx and Password is not blank 
					if(!Utilities.isValidEmail(uname))
						Utilities.showToast(getApplicationContext(), "Please Enter a valid EMail ID", false);
					else if(pwd.equalsIgnoreCase(""))
						Utilities.showToast(getApplicationContext(), "Please Enter a valid Password", false);
					else
					{
						CheckLoginTask task = new CheckLoginTask();
						Log.d("url",baseURL+"/token_authentications.json" );
						task.execute(baseURL+"/token_authentications.json");
						if(rememberMeCheckBox.isChecked())
						{
							Editor editor = settings.edit();
							editor.putString("uname", uname);
							editor.putString("pwd", pwd);
							editor.commit();
						}
					}
				}		
			});
        }         
    }
    
 private class CheckLoginTask extends AsyncTask<String, Void, String> {
        
        private ProgressDialog Dialog = new ProgressDialog(Login.this);

        @Override
        protected void onPreExecute()
        {
            Dialog.setMessage("Verifying..");
            Dialog.show();
        }
        
        @Override
        protected String doInBackground(String... urls) {
        	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	Log.d("uname", uname);
        	Log.d("pwd", pwd);
        	
//        	NameValuePairs for POST Request
        	
                nameValuePairs.add(new BasicNameValuePair("email", uname));
                nameValuePairs.add(new BasicNameValuePair("password", pwd));
            
                try
                {
//                	Setting Up for a secure connection
                    
                    HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                    DefaultHttpClient client = new DefaultHttpClient();
                    SchemeRegistry registry = new SchemeRegistry();
                    SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                    socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                    registry.register(new Scheme("https", socketFactory, 443));
                    SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
                    DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

//                    Set verifier     
                    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

//                    Send http request
                    HttpPost httpPost = new HttpPost(baseURL+"/token_authentications.json");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    loginResponse = EntityUtils.toString(entity);
                    Log.d("entity", loginResponse);
                }
                catch(Exception e)
                {
                    Log.d("http_error",e.toString() );
                }
                finally
                {
                
                }

    		return loginResponse;
    		}

        @Override
        protected void onPostExecute(String result) 
        {
        	Dialog.dismiss();
//        	Toast.makeText(getApplicationContext(),loginResponse , Toast.LENGTH_LONG).show();
        	try
        	{
            	JSONObject jObject = new JSONObject(loginResponse);
            	if(!jObject.has("error") && jObject.has("auth_key"))
            	{
	            	String authToken = jObject.getString("auth_key");
	            	Log.d("auth_token", authToken);
		            	Editor e = settings.edit();
		            	e.putString("auth_token", authToken);
		            	e.commit();           	
	            	Intent gotoHome = new Intent(getApplicationContext(), Home.class);
	            	startActivity(gotoHome);
            	}
            	else
            		Utilities.showToast(getApplicationContext(), "Invalid Email Address or Password. Please Try Again!", true);
            	
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
        	
      }

    } 
    
}