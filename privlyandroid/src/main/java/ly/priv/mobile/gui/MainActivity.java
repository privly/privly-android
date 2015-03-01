package ly.priv.mobile.gui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;

import java.util.ArrayList;
import java.util.Arrays;

import ly.priv.mobile.EmailThreadObject;
import ly.priv.mobile.GmailLinkGrabberService;
import ly.priv.mobile.R;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsFragment;
import ly.priv.mobile.api.gui.socialnetworks.ListUsersFragment;
import ly.priv.mobile.grabbers.FaceBookGrabberService;
import ly.priv.mobile.grabbers.TwitterGrabberService;

/**
 * This activity holds all the fragments which are intended to have a navigation
 * drawer. Also provides an interface to pass on twitter login data to
 * appropriate fragment.
 * 
 * @author Gitanshu
 * 
 */
public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";
	Uri uri;
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle hamburger;
	private static CharSequence mTitle;
	ArrayList<String> createList, readList;
	EmailThreadObject currentThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate MainActivity");
		setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		mTitle = getTitle();
//		getActionBar().setDisplayOptions(
//				ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME
//						| ActionBar.DISPLAY_HOME_AS_UP);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		hamburger = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerLayout.setDrawerListener(hamburger);
		createList = new ArrayList<String>(
				Arrays.asList("PlainPost", "Message"));
		readList = new ArrayList<String>(Arrays.asList("Gmail", "Facebook",
				"Twitter"));
		ArrayList<DrawerObject> drawerItems = new ArrayList<DrawerObject>();
		DrawerObject obj = new DrawerObject();
		obj.setType("NavItem");
		obj.setTitle(getString(R.string.history));
		obj.setIcon(new IconDrawable(this, IconValue.fa_list_alt)
				.colorRes(R.color.white));
		drawerItems.add(obj);

		obj = new DrawerObject();
		obj.setType("header");
		obj.setSectionheader(getString(R.string.createListViewLabel));
		drawerItems.add(obj);

		for (String s : createList) {
			obj = new DrawerObject();
			obj.setType("NavItem");
			obj.setTitle(s);
			if (s.equals("PlainPost")) {
				obj.setIcon(new IconDrawable(this, IconValue.fa_eye)
						.colorRes(R.color.white));
			} else if (s.equals("Message")) {
				obj.setIcon(new IconDrawable(this, IconValue.fa_eye_slash)
						.colorRes(R.color.white));
			}
			drawerItems.add(obj);
		}

		obj = new DrawerObject();
		obj.setType("header");
		obj.setSectionheader(getString(R.string.readListViewLabel));
		drawerItems.add(obj);

		for (String s : readList) {
			obj = new DrawerObject();
			obj.setType("NavItem");
			obj.setTitle(s);
			if (s.equals("Gmail")) {
				obj.setIcon(new IconDrawable(this, IconValue.fa_envelope_square)
						.colorRes(R.color.white));
			} else if (s.equals("Facebook")) {
				obj.setIcon(new IconDrawable(this, IconValue.fa_facebook_square)
						.colorRes(R.color.white));
			} else if (s.equals("Twitter")) {
				obj.setIcon(new IconDrawable(this, IconValue.fa_twitter_square)
						.colorRes(R.color.white));
			}
			drawerItems.add(obj);
		}

		NavigationDrawerAdapter drawerAdapter = new NavigationDrawerAdapter(
				this, drawerItems);
		mDrawerList.setAdapter(drawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		uri = getIntent().getData();
		if (uri != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new TwitterGrabberService()).commit();
		} else {
			if (savedInstanceState == null) {
				setTitle(getString(R.string.history));
				Log.d("index", "beforetransaction");
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new IndexFragment()).commit();
			}
		}
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

	/**
	 * The twitter api returns the login data in form of an intent which can be
	 * captured by the activity using onNewIntent method. When the intent is
	 * received, the MainActivity sends the intent to TwitterLinkGrabberService
	 * through the NewIntentListener interface.
	 */
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
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (hamburger.onOptionsItemSelected(getMenuItem(item))) {
			return true;
		}
		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}

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

	// Override getMenuItem for Action Bar Sherlock compatibility
	// Reference: https://github.com/tobykurien/SherlockNavigationDrawer/blob
	// /master/src/com/example/android/navigationdrawerexample/MainActivity.java
	private android.view.MenuItem getMenuItem(final MenuItem item) {
		return new android.view.MenuItem() {
			@Override
			public int getItemId() {
				return item.getItemId();
			}

			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean collapseActionView() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean expandActionView() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ActionProvider getActionProvider() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public View getActionView() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public char getAlphabeticShortcut() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getGroupId() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Drawable getIcon() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Intent getIntent() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ContextMenuInfo getMenuInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public char getNumericShortcut() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getOrder() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SubMenu getSubMenu() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CharSequence getTitle() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CharSequence getTitleCondensed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasSubMenu() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isActionViewExpanded() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isCheckable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isChecked() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isVisible() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public android.view.MenuItem setActionProvider(
					ActionProvider actionProvider) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setActionView(View view) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setActionView(int resId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setCheckable(boolean checkable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setChecked(boolean checked) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setEnabled(boolean enabled) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIcon(Drawable icon) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIcon(int iconRes) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setIntent(Intent intent) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setNumericShortcut(char numericChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setOnActionExpandListener(
					OnActionExpandListener listener) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setOnMenuItemClickListener(
					OnMenuItemClickListener menuItemClickListener) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setShortcut(char numericChar,
					char alphaChar) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setShowAsAction(int actionEnum) {
				// TODO Auto-generated method stub

			}

			@Override
			public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitle(CharSequence title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitle(int title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setTitleCondensed(CharSequence title) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public android.view.MenuItem setVisible(boolean visible) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	// Override setTitle to use Action Bar Sherlock
	@Override
	public void setTitle(CharSequence title) {
		Log.d("setTitle", title.toString());
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public void setTitle(int resId) {
		Log.d("setTitle", getString(resId));
        getSupportActionBar().setTitle(getString(resId));
	}

}
