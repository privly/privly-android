package ly.priv.mobile.gui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

import ly.priv.mobile.EmailThreadObject;
import ly.priv.mobile.GmailLinkGrabberService;
import ly.priv.mobile.R;
import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsFragment;
import ly.priv.mobile.api.gui.socialnetworks.ListUsersFragment;
import ly.priv.mobile.grabbers.FaceBookGrabberService;
import ly.priv.mobile.grabbers.TwitterGrabberService;
import ly.priv.mobile.gui.IndexFragment;
import ly.priv.mobile.gui.drawer.Header;
import ly.priv.mobile.gui.drawer.NavDrawerAdapter;
import ly.priv.mobile.gui.drawer.NavDrawerItem;
import ly.priv.mobile.gui.drawer.NavDrawerItemType;
import ly.priv.mobile.gui.drawer.PrivlyApplication;
import ly.priv.mobile.gui.drawer.ReadingApplication;
import ly.priv.mobile.gui.fragments.PrivlyApplicationFragment;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.Utilities;

/**
 * This activity holds all the fragments which are intended to have a navigation
 * drawer. Also provides an interface to pass on twitter login data to
 * appropriate fragment.
 *
 * @author Gitanshu
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    Uri uri;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle hamburger;
    EmailThreadObject currentThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate MainActivity");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        hamburger = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
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
        mDrawerLayout.setDrawerListener(hamburger);
        initNavigationDrawer();
        uri = getIntent().getData();
        if (uri != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TwitterGrabberService()).commit();
        } else {
            if (savedInstanceState == null) {
                PrivlyApplicationFragment messageFragment = new PrivlyApplicationFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ConstantValues.PRIVLY_APPLICATION_KEY, PrivlyApplication.MESSAGE_APP);
                messageFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, messageFragment)
                        .commit();
            }
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

        final ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
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
                                transaction.replace(R.id.container, fbGrabber);
                                transaction.commit();
                                break;
                            case ReadingApplication.TWITTER:
                                TwitterGrabberService tweetGrabber = new TwitterGrabberService();
                                transaction.replace(R.id.container, tweetGrabber, "Twitter");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                break;
                            case ReadingApplication.GMAIL:
                                GmailLinkGrabberService gmailGrabber = new GmailLinkGrabberService();
                                transaction.replace(R.id.container, gmailGrabber);
                                transaction.addToBackStack(null);
                                break;
                        }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(
                R.id.container);
        if (fragment instanceof ListUsersFragment
                || fragment instanceof MicroblogListPostsFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new IndexFragment()).commit();
            setTitle(getString(R.string.history));
        } else if (fragment instanceof IndexFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hamburger.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hamburger.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (hamburger.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
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

    /*
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position,
                                long id) {
            if (position == 0) {
                mDrawerLayout.closeDrawers();
                Fragment index = new IndexFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRedirected", true);
                index.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container, index);
                transaction.commit();
            }

            if (position >= 2 && position < 2 + createList.size()) {
                if (Utilities
                        .isDataConnectionAvailable(getApplicationContext())) {
                    mDrawerLayout.closeDrawers();
                    Fragment gotoCreateNewPost = new NewPostFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("JsAppName", createList.get(position - 2));
                    gotoCreateNewPost.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.container, gotoCreateNewPost);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else
                    Utilities.showToast(getApplicationContext(),
                            getString(R.string.no_internet_connection), true);
            }

            if (position == 2 + createList.size() + 1) {
                mDrawerLayout.closeDrawers();
                GmailLinkGrabberService gmailGrabber = new GmailLinkGrabberService();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container, gmailGrabber);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if (position == 2 + createList.size() + 2) {
                mDrawerLayout.closeDrawers();
                FaceBookGrabberService fbGrabber = new FaceBookGrabberService();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container, fbGrabber);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if (position == 2 + createList.size() + 3) {
                mDrawerLayout.closeDrawers();
                TwitterGrabberService twitGrabber = new TwitterGrabberService();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container, twitGrabber, "Twitter");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
    */
}
