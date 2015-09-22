package ly.priv.mobile.gui.activities;

import android.app.Activity;
import android.content.Intent;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.utils.Values;

public class LoginActivity extends Activity {
    ViewSwitcher switcher;
    TextView contentServerTextView;
    EditText contentServerEditText, pwdEditText;
    AutoCompleteTextView emailAddressEditText;
    ImageButton saveContentServerButton;
    ProgressBar progressBar;
    Button loginButton;
    private Values mValues;
    String mContentServerDomain, authToken;
    String mEmailAddress;
    private String LOGTAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mValues = Values.getInstance();
        switcher = (ViewSwitcher) findViewById(R.id.content_server_switcher);
        contentServerTextView = (TextView) findViewById(R.id.content_server_view);
        saveContentServerButton = (ImageButton) findViewById(R.id.save_content_server);
        contentServerEditText = (EditText) findViewById(R.id.content_server_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginButton = (Button) findViewById(R.id.btn_login);
        emailAddressEditText = (AutoCompleteTextView) findViewById(R.id.email_edit_text);
        Set<String> emailSet = Utilities.emailIdSuggestor(LoginActivity.this);
        emailAddressEditText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
        pwdEditText = (EditText) findViewById(R.id.pwd_edit_text);

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
            String mLoginResponse = "";
            String charset = "UTF-8";
            for (String url : urls) {
                Log.d(LOGTAG, "URL : " + url);
                try {
                    String query = String.format(ConstantValues.POST_PARAM_NAME_EMAIL + "=%s&" + ConstantValues.POST_PARAM_NAME_PWD + "=%s",
                            URLEncoder.encode(mUserName, charset),
                            URLEncoder.encode(mPassword, charset));
                    Log.d(LOGTAG, query);

                    HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
                    //Disabling auto redirect. Causes issues while generating new token.
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;");
                    connection.setRequestProperty("Content-Length", Integer.toString(query.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    OutputStream output = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 302) {
                        String redirectUrl = connection.getHeaderField("Location");
                        Log.d(LOGTAG, "Redirecting to : " + redirectUrl);
                        String cookies = connection.getHeaderField("Set-Cookie");
                        connection = (HttpsURLConnection) new URL(redirectUrl).openConnection();
                        connection.setRequestProperty("Cookie", cookies);
                        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        if (connection.getResponseCode() == 200) {
                            for (String line; (line = reader.readLine()) != null; ) {
                                Log.d(LOGTAG, line);
                                mLoginResponse += line;
                            }
                        }
                    } else {
                        Log.d(LOGTAG, "Response Code : " + responseCode);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                        mValues.setLastLoginEmailAddress(mEmailAddress);
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