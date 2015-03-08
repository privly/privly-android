package ly.priv.mobile.gui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.LobsterTextView;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.utils.Values;

public class LoginActivity extends Activity {
    ViewSwitcher switcher;
    TextView contentServerTextView;
    EditText contentServerEditText, pwdEditText;
    AutoCompleteTextView emailAddressEditText;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    ImageButton saveContentServerButton;
    ProgressBar progressBar;
    Button loginButton;
    private Values mValues;
    String mContentServerDomain, authToken;
    SharedPreferences prefs;
    String prefName = "MyPref";
    String usernameextract;
    String mEmailAddress;
    private String LOGTAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        switcher = (ViewSwitcher) findViewById(R.id.content_server_switcher);
        contentServerTextView = (TextView) findViewById(R.id.content_server_view);
        saveContentServerButton = (ImageButton) findViewById(R.id.save_content_server);
        contentServerEditText = (EditText) findViewById(R.id.content_server_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginButton = (Button) findViewById(R.id.btn_login);
        emailAddressEditText = (AutoCompleteTextView) findViewById(R.id.email_edit_text);
        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        if(readPrefValues())
        {
            if (EMAIL_PATTERN.matcher(usernameextract).matches()) {
                emailSet.add(usernameextract);
            }
        }
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        emailAddressEditText.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
        pwdEditText = (EditText) findViewById(R.id.pwd_edit_text);
        mValues = new Values(LoginActivity.this);
        mContentServerDomain = mValues.getContentServer();
        authToken = mValues.getAuthToken();
        if (authToken != null) {
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            saveContentServerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveContentServer(v);
                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities
                            .isDataConnectionAvailable(getApplicationContext())) {
                       mEmailAddress = emailAddressEditText.getText().toString().trim();
                        String mPassword = pwdEditText.getText().toString();
                        // Check if Email is Valid using RegEx and Password
                        // and is not null
                        if (!Utilities.isValidEmail(mEmailAddress))
                            Utilities
                                    .showToast(
                                            getApplicationContext(),
                                            getString(R.string.please_enter_a_valid_email_id),
                                            false);
                        else if (mPassword.equalsIgnoreCase(""))
                            Utilities
                                    .showToast(
                                            getApplicationContext(),
                                            getString(R.string.please_enter_a_valid_password),
                                            false);
                        else {
                            mValues.setUserName(mEmailAddress);
                            VerifyLoginCredentialsTask task = new VerifyLoginCredentialsTask(mEmailAddress, mPassword);
                            task.execute(mValues.getContentServer() + ConstantValues.TOKEN_AUTHENTICATION_ENDPOINT);
                        }
                    } else
                        Utilities.showToast(getApplicationContext(),
                                getString(R.string.no_internet_connection),
                                true);
                }
            });
        }
    }
    public Boolean readPrefValues() {
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        usernameextract = prefs.getString("username", "");
        if(usernameextract.equals(""))
            return false;
        else
            return true;
    }

    public void editContentServer(View v) {
        switcher.showNext();
    }

    private void updateContentServerViews() {
        contentServerTextView.setText("Logging in to " + mValues.getContentServer() + ".\nTap here to change");
        contentServerEditText.setText(mValues.getContentServer());
    }

    public void saveContentServer(View v) {
        mValues.setContentServer(contentServerEditText.getText().toString());
        updateContentServerViews();
        switcher.showPrevious();
    }

    private void updateViewStates(Boolean isLoggingIn) {
        if (isLoggingIn) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            emailAddressEditText.setEnabled(false);
            pwdEditText.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            pwdEditText.setEnabled(true);
            emailAddressEditText.setEnabled(true);
        }
    }

    private class VerifyLoginCredentialsTask extends AsyncTask<String, Void, String> {
        String mUserName;
        String mPassword;

        public VerifyLoginCredentialsTask(String mEmailAddress, String mPassword) {
            this.mUserName = mEmailAddress;
            this.mPassword = mPassword;
        }

        @Override
        protected void onPreExecute() {
            updateViewStates(true);
        }

        @Override
        protected String doInBackground(String... urls) {
            String mLoginResponse = null;
            for (String url : urls) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                // NameValuePairs for POST Request
                nameValuePairs.add(new BasicNameValuePair(ConstantValues.POST_PARAM_NAME_EMAIL, mUserName));
                nameValuePairs
                        .add(new BasicNameValuePair(ConstantValues.POST_PARAM_NAME_PWD, mPassword));
                try {
                    // Setting Up for a secure connection
                    HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                    DefaultHttpClient client = new DefaultHttpClient();
                    SchemeRegistry registry = new SchemeRegistry();
                    SSLSocketFactory socketFactory = SSLSocketFactory
                            .getSocketFactory();
                    socketFactory
                            .setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                    registry.register(new Scheme("https", socketFactory, 443));
                    SingleClientConnManager mgr = new SingleClientConnManager(
                            client.getParams(), registry);
                    DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
                            client.getParams());
                    HttpsURLConnection
                            .setDefaultHostnameVerifier(hostnameVerifier);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    mLoginResponse = EntityUtils.toString(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mLoginResponse;
        }

        @Override
        protected void onPostExecute(String mLoginResponse) {
            Log.d(LOGTAG, "mLoginResponse : " + mLoginResponse);
            if (mLoginResponse != null) {
                try {
                    JSONObject jObject = new JSONObject(mLoginResponse);
                    if (!jObject.has("error") && jObject.has("auth_key")) {
                        //Authentication was successful. Save the auth_token.
                        progressBar.setVisibility(View.INVISIBLE);
                        String authToken = jObject.getString("auth_key");
                        mValues.setAuthToken(authToken);
                        mValues.setUserVerifiedAtLogin(true);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username",mEmailAddress );
                        //---saves the values---
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, jObject.getString(ConstantValues.AUTH_ERROR_KEY), Toast.LENGTH_LONG).show();
                        updateViewStates(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateViewStates(false);
                }
            } else {
                //If mLoginResponse is null, some exception occurred while making the authentication request
                updateViewStates(false);
            }
        }
    }

}
