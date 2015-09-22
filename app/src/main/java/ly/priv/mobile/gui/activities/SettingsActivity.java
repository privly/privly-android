package ly.priv.mobile.gui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Session;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.TwitterUtil;
import ly.priv.mobile.utils.Values;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private EditTextPreference changeContentServer;
    private Preference logoutFb;
    private Preference logoutGmail;
    private Preference logoutTwitter;
    private Values mValues;

    /**
     * Refer : http://stackoverflow.com/questions/17849193/how-to-add-action-bar-from-support-library-into-preferenceactivity
     * Refer : https://chromium.googlesource.com/android_tools/+/7200281446186c7192cb02f54dc2b38e02d705e5/sdk/extras/android/support/samples/Support7Demos/src/com/example/android/supportv7/app/AppCompatPreferenceActivity.java
     */
    private AppCompatDelegate mDelegate;

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mValues = Values.getInstance();
        setupSimplePreferencesScreen();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.settings);

        changeContentServer = (EditTextPreference) getPreferenceManager()
                .findPreference("base_url");
        logoutFb = getPreferenceManager().findPreference(
                "fbLogout");
        logoutGmail = getPreferenceManager().findPreference(
                "gmailLogout");
        logoutTwitter = getPreferenceManager().findPreference(
                "twitterLogout");

        changeContentServer.setText(mValues.getContentServer());
        changeContentServer.setSummary(mValues.getContentServer());
        changeContentServer.setPositiveButtonText("Re-login");
        changeContentServer
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        String contentServer = (String) newValue;
                        if (!contentServer.equalsIgnoreCase("")) {
                            mValues.setContentServer((String) newValue);
                            mValues.setAuthToken(null);
                            Intent goToLogin = new Intent(
                                    SettingsActivity.this,
                                    LoginActivity.class);
                            goToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(goToLogin);
                            return true;
                        } else {
                            Toast.makeText(SettingsActivity.this, "Please enter a valid content server address", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                });

        if (mValues.getGmailId() == null) {
            logoutGmail.setEnabled(false);
        } else {
            logoutGmail
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            mValues.clearGmailId();
                            logoutGmail.setEnabled(false);
                            return true;
                        }
                    });
        }


        if (Session.getActiveSession() == null) {
            logoutFb.setEnabled(false);
        } else {
            logoutFb.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Session mSession = Session.getActiveSession();
                    if (mSession != null) {
                        mSession.closeAndClearTokenInformation();
                        mSession = null;
                        Session.setActiveSession(mSession);
                        mValues.setFacebookID("");
                        logoutFb.setEnabled(false);
                    }
                    return true;
                }
            });
        }

        if (!mValues.getTwitterLoggedIn()) {
            logoutTwitter.setEnabled(false);
        } else {
            logoutTwitter
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            mValues.setTwitterLoggedIn(false);
                            mValues.setTwitterOauthToken("");
                            mValues.setTwitterOauthTokenSecret("");
                            TwitterUtil.getInstance().reset();
                            logoutTwitter.setEnabled(false);
                            return true;
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout_settings, menu);
        menu.findItem(R.id.logout).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_sign_out)
                        .colorRes(R.color.gray)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mValues.setAuthToken(null);
                Intent gotoLogin = new Intent(SettingsActivity.this, LoginActivity.class);
                gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(gotoLogin);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

}
