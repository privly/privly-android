package ly.priv.mobile.gui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

import ly.priv.mobile.GmailLinkGrabberService;
import ly.priv.mobile.R;
import ly.priv.mobile.grabbers.FaceBookGrabberService;
import ly.priv.mobile.grabbers.TwitterGrabberService;
import ly.priv.mobile.gui.drawer.Header;
import ly.priv.mobile.gui.drawer.NavDrawerAdapter;
import ly.priv.mobile.gui.drawer.NavDrawerItem;
import ly.priv.mobile.gui.drawer.NavDrawerItemType;
import ly.priv.mobile.gui.drawer.PrivlyApplication;
import ly.priv.mobile.gui.drawer.ReadingApplication;
import ly.priv.mobile.gui.fragments.PrivlyApplicationFragment;
import ly.priv.mobile.gui.fragments.ShowContentFragment;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.utils.Values;

public class MainActivity extends ActionBarActivity {
    private final String TAG = getClass().getSimpleName();
    Uri uri;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        initNavigationDrawer();
        uri = getIntent().getData();
        if (uri != null) {
            if (uri.getHost().equalsIgnoreCase("privlyT4JCallback")) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TwitterGrabberService()).commit();
                return;
            }
            else {
                //checking if its a Privly App link
                ArrayList<String> links = Utilities.fetchPrivlyUrls(uri.toString());
                if (!links.isEmpty()) {
                    Bundle linkBundle = new Bundle();
                    linkBundle.putStringArrayList("listOfLinks", links);
                    ShowContentFragment showContentFragment = new ShowContentFragment();
                    showContentFragment.setArguments(linkBundle);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, showContentFragment).commit();
                    return;
                } else {
                    //Can't handle the link, sending user back to inception with a Toast
                    Toast.makeText(this, "Can't handle the link in Privly! Open it in browser.", Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                }
            }
        }

        if (savedInstanceState == null) {
            PrivlyApplicationFragment messageFragment = new PrivlyApplicationFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ConstantValues.PRIVLY_APPLICATION_KEY, PrivlyApplication.MESSAGE_APP);
            messageFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, messageFragment).addToBackStack(null)
                    .commit();
        }

    }

    private void initNavigationDrawer() {

        Header privlyApplicationsHeader = new Header("Privly Applications");
        NavDrawerItem headerNavItem = new NavDrawerItem(NavDrawerItemType.HEADER, privlyApplicationsHeader);

        PrivlyApplication messageApplication = new PrivlyApplication(PrivlyApplication.MESSAGE_APP, Utilities.getFilePathURLFromAppName(PrivlyApplication.MESSAGE_APP), new IconDrawable(this, Iconify.IconValue.fa_envelope_square).colorRes(R.color.gray));
        NavDrawerItem messageNavItem = new NavDrawerItem(NavDrawerItemType.PRIVLY_APPLICATION, messageApplication);

        PrivlyApplication plainPostApplication = new PrivlyApplication(PrivlyApplication.PLAINPOST_APP, Utilities.getFilePathURLFromAppName(PrivlyApplication.MESSAGE_APP), new IconDrawable(this, Iconify.IconValue.fa_envelope_square).colorRes(R.color.gray));
        NavDrawerItem plainPostNavItem = new NavDrawerItem(NavDrawerItemType.PRIVLY_APPLICATION, plainPostApplication);

        PrivlyApplication historyApplication = new PrivlyApplication(PrivlyApplication.HISTORY_APP, Utilities.getFilePathURLFromAppName(PrivlyApplication.HISTORY_APP), new IconDrawable(this, Iconify.IconValue.fa_list_alt).colorRes(R.color.gray));
        NavDrawerItem historyNavItem = new NavDrawerItem(NavDrawerItemType.PRIVLY_APPLICATION, historyApplication);

        Header webConnectionsHeader = new Header("Connect Privly with");
        NavDrawerItem webConnectionHeaderItem = new NavDrawerItem(NavDrawerItemType.HEADER, webConnectionsHeader);

        ReadingApplication facebookReadingApplication = new ReadingApplication(ReadingApplication.FACEBOOK, new IconDrawable(this, Iconify.IconValue.fa_facebook_square).colorRes(R.color.gray));
        NavDrawerItem facebookNavItem = new NavDrawerItem(NavDrawerItemType.READING_APPLICATION, facebookReadingApplication);

        ReadingApplication twitterReadingApplication = new ReadingApplication(ReadingApplication.TWITTER, new IconDrawable(this, Iconify.IconValue.fa_twitter_square).colorRes(R.color.gray));
        NavDrawerItem twitterNavItem = new NavDrawerItem(NavDrawerItemType.READING_APPLICATION, twitterReadingApplication);

        ReadingApplication gmailReadingApplication = new ReadingApplication(ReadingApplication.GMAIL, new IconDrawable(this, Iconify.IconValue.fa_envelope_square).colorRes(R.color.gray));
        NavDrawerItem gmailNavItem = new NavDrawerItem(NavDrawerItemType.READING_APPLICATION, gmailReadingApplication);

        final ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();
        navDrawerItems.add(headerNavItem);
        navDrawerItems.add(messageNavItem);
        navDrawerItems.add(plainPostNavItem);
        navDrawerItems.add(historyNavItem);
        navDrawerItems.add(webConnectionHeaderItem);
        navDrawerItems.add(facebookNavItem);
        navDrawerItems.add(twitterNavItem);
        navDrawerItems.add(gmailNavItem);
        NavDrawerAdapter navDrawerAdapter = new NavDrawerAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(navDrawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavDrawerItem navDrawerItem = navDrawerItems.get(position);
                switch (navDrawerItem.getType()) {
                    case NavDrawerItemType.PRIVLY_APPLICATION:
                        mDrawerLayout.closeDrawers();
                        PrivlyApplicationFragment privlyApplicationFragment = new PrivlyApplicationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantValues.PRIVLY_APPLICATION_KEY, ((PrivlyApplication) navDrawerItem.getObject()).getName());
                        privlyApplicationFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, privlyApplicationFragment)
                                .commit();
                        break;
                    case NavDrawerItemType.READING_APPLICATION:
                        mDrawerLayout.closeDrawers();
                        FragmentTransaction transaction = getSupportFragmentManager()
                                .beginTransaction();
                        switch (((ReadingApplication) navDrawerItem.getObject()).getName()) {
                            case ReadingApplication.FACEBOOK:
                                FaceBookGrabberService fbGrabber = new FaceBookGrabberService();
                                transaction.add(R.id.container, fbGrabber).addToBackStack(null);
                                transaction.commit();
                                break;
                            case ReadingApplication.TWITTER:
                                TwitterGrabberService tweetGrabber = new TwitterGrabberService();
                                transaction.add(R.id.container, tweetGrabber, "Twitter").addToBackStack(null);
                                transaction.commit();
                                break;
                            case ReadingApplication.GMAIL:
                                GmailLinkGrabberService gmailGrabber = new GmailLinkGrabberService();
                                transaction.add(R.id.container, gmailGrabber).addToBackStack(null);
                                transaction.commit();
                                break;
                        }
                        break;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof PrivlyApplicationFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout_home, menu);
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_gear)
                        .colorRes(R.color.gray)
                        .actionBarSize());
        menu.findItem(R.id.logout).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_sign_out)
                        .colorRes(R.color.gray)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.settings:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.logout:
                    Values values = new Values(MainActivity.this);
                    values.setAuthToken(null);
                    Intent gotoLogin = new Intent(MainActivity.this, LoginActivity.class);
                    gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gotoLogin);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The twitter api returns the login data in form of an intent which can be
     * captured by the activity using onNewIntent method. When the intent is
     * received, the MainActivity sends the intent to TwitterLinkGrabberService
     * through the NewIntentListener interface.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NewIntentListener newIntentListener = (NewIntentListener) this
                .getSupportFragmentManager().findFragmentByTag("Twitter");
        newIntentListener.onNewIntentRead(intent);
    }

    public interface NewIntentListener {
        public void onNewIntentRead(Intent intent);
    }
}