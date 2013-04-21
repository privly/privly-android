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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class login extends Activity {
    /** Called when the activity is first created. */
	String uname, pwd; 
	Button login;
	String login_response;
	String base_url;
	EditText uname_edit;
	EditText pwd_e;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
//        Shared Preference File for storing the domain name, if not privly by default. 
//        Will be extended to store the username and password 
        
        Values values = new Values();
        String prefs_name = values.getPrefs_name();
        SharedPreferences settings = getSharedPreferences(prefs_name, 0);
        base_url = settings.getString("base_url", null);
        
//        If no base domain has been defined, the user is taken to the login screen where he needs to add it.     
        
        if(base_url == null)
        {
        	Intent settings_it = new Intent(this, settings.class );
        	startActivity(settings_it);
        }
        
        uname_edit = (EditText)findViewById(R.id.uname);
        pwd_e = (EditText) findViewById(R.id.pwd);
        login = (Button)findViewById(R.id.login);

        
//        On Login Button Click, A POST Request is made to the server for authentication. 
//        The Authentication Process is done using AsyncTask to prevent blocking of UI Thread. 
        
        login.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				uname = uname_edit.getText().toString();
				pwd = pwd_e.getText().toString();
				CheckLoginTask task = new CheckLoginTask();
				Log.d("url",base_url+"/token_authentications.json" );
				task.execute(base_url+"/token_authentications.json");
			}
		});
              
    }
    
 private class CheckLoginTask extends AsyncTask<String, Void, String> {
        
        private ProgressDialog Dialog = new ProgressDialog(login.this);

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
                    HttpPost httpPost = new HttpPost(base_url+"/token_authentications.json");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    login_response = EntityUtils.toString(entity);
                    Log.d("entity", login_response);
                }
                catch(Exception e)
                {
                    Log.d("http_error",e.toString() );
                }
                finally
                {
                
                }

    		return login_response;
    		}

        @Override
        protected void onPostExecute(String result) 
        {
        	Dialog.dismiss();
        	Toast.makeText(getApplicationContext(),login_response , Toast.LENGTH_LONG).show();
      }

    } 
    
}