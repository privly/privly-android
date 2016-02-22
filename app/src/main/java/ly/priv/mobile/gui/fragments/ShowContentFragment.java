package ly.priv.mobile.gui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import ly.priv.mobile.R;
import ly.priv.mobile.gui.activities.LoginActivity;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.JsObject;
import ly.priv.mobile.utils.LobsterTextView;
import ly.priv.mobile.utils.Values;

/**
 * Displays the Home Activity for a user after authentication.
 * <p/>
 * <p/>
 * <ul>
 * <li>Receive source name from the intent</li>
 * <li>Enable JavaScript for the WebView.</li>
 * <li>Enable JavaScript Interface</li>
 * <li>Setup swipe gesture detector for WebView. Used to move backward and
 * forward through the links Db for the specifc source, Facebook and Twitter</li>
 * <li>Load links for the particular source and load them in the reading
 * application using the WebView</li>
 * </ul>
 * <p/>
 *
 * @author Shivam Verma
 */
public class ShowContentFragment extends Fragment {
    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "ShowContent";
    private GestureDetector mGestureDetector;
    private View.OnTouchListener mGestureListener;
    private WebView mUrlContentWebView;
    private LobsterTextView mPositionTV;
    private ArrayList<String> mListOfLinks;
    private Integer mId = 0;
    private ArrayList<ImageView> mIndicators;

    public ShowContentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.show_content, container, false);
        getActivity().setTitle(R.string.show_content);
        mListOfLinks = getArguments().getStringArrayList("listOfLinks");
        View webView = view.findViewById(R.id.urlContentWebview);
        mUrlContentWebView = (WebView) webView;
        mPositionTV = (LobsterTextView) view.findViewById(R.id.position_tv);
        setHasOptionsMenu(true);
        mUrlContentWebView.getSettings().setJavaScriptEnabled(true);

        // Add JavaScript Interface to the WebView. This enables the JS to
        // access Java functions defined in the JsObject Class
        mUrlContentWebView.addJavascriptInterface(new JsObject(getActivity()),
                "androidJsBridge");

        // Sets whether JavaScript running in the context of a file scheme URL
        // should be allowed to access content from any origin. This includes
        // access to content from other file scheme URLs.
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
            mUrlContentWebView.getSettings()
                    .setAllowUniversalAccessFromFileURLs(true);

        // Setup WebView to detect swipes.
        mGestureDetector = new GestureDetector(getActivity(),
                new SwipeGestureDetector());
        mGestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
        webView.setOnTouchListener(mGestureListener);

        loadUrlInWebview(mId);
        addIndicators(view);
        selectIndicator(mId);
        Log.d(TAG, "Inside Show");
        return view;
    }

    /**
     * Swipe gesture listener.
     * <p>
     * <ul>
     * <li>Moves the Db cursor back and forth depending on the swipe.</li>
     * <li>Calls loadUrlInWebView() method.</li>
     * </ul>
     * </p>
     */
    class SwipeGestureDetector extends SimpleOnGestureListener {

        // Touch events to the webview will be intercepted by the GestureListener
        // To ensure that scrolling of the webview is being done properly, we intercept the onScroll event and the parameters onto the webview to scroll
        // Similarly, we do the same for the onFling event
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            // Passing the onFling parameters to flingScroll the webview
            mUrlContentWebView.flingScroll(-1*(int)velocityX,-1*(int)velocityY);

            try {
                Values values = Values.getInstance();
                HashMap<String, Integer> valuesForSwipe = values
                        .getValuesForSwipe();
                if (Math.abs(e1.getY() - e2.getY()) > valuesForSwipe
                        .get(ConstantValues.SWIPE_MAX_OFF_PATH))
                    return false;
                if (e1.getX() - e2.getX() > valuesForSwipe
                        .get(ConstantValues.SWIPE_MIN_DISTANCE)
                        && Math.abs(velocityX) > valuesForSwipe
                        .get(ConstantValues.SWIPE_THRESHOLD_VELOCITY)) {
                    if (mId < mListOfLinks.size() - 1) {
                        mId++;
                        loadUrlInWebview(mId);
                        Toast.makeText(getActivity(),
                                getString(R.string.loading_next_post),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.this_is_a_last_post),
                                Toast.LENGTH_SHORT).show();
                    }

                } else if (e2.getX() - e1.getX() > valuesForSwipe
                        .get(ConstantValues.SWIPE_MIN_DISTANCE)
                        && Math.abs(velocityX) > valuesForSwipe
                        .get(ConstantValues.SWIPE_THRESHOLD_VELOCITY)) {

                    if (mId > 0) {
                        mId--;
                        Toast.makeText(getActivity(),
                                getString(R.string.loading_previous_post),
                                Toast.LENGTH_SHORT).show();
                        loadUrlInWebview(mId);
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.this_is_a_first_post),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            selectIndicator(mId);
            return false;
        }

        // This method should always return true to detect swipes.
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        // This method is required for horizontally scrolling in the web view (When content of the Privly Message/Plain Post is long)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mUrlContentWebView.scrollBy((int)distanceX,(int)distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * Loads a Privly URL into the Reading Application.
     * <p/>
     * <p>
     * <ul>
     * <li>Fetch link from Database Cursor</li>
     * <li>Encode URL</li>
     * <li>Create URL for Reading App</li>
     * <li>Load URL into the WebView</li>
     * </ul>
     * </p>
     */
    void loadUrlInWebview(Integer id) {
        Log.d(TAG, "loadUrlInWebview");
        String url = mListOfLinks.get(id);
        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlForExtension = "";
        if (url.indexOf("privlyInjectableApplication%3DMessage") > 0 || // deprecated
                url.indexOf("privlyApp%3DMessage") > 0) {
            urlForExtension = "PrivlyApplications/Message/show.html?privlyOriginalURL="
                    + url;
        } else if (url.indexOf("privlyInjectableApplication%3DPlainPost") > 0 || // deprecated
                url.indexOf("privlyApp%3DPlainPost") > 0) {
            urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
                    + url;
        } else {
            urlForExtension = "PrivlyApplications/PlainPost/show.html?privlyOriginalURL="
                    + url;
        }
        mUrlContentWebView.loadUrl("file:///android_asset/" + urlForExtension);
    }

    /**
     * Inflate options menu with the layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_layout_show_content, menu);
    }

    /**
     * Item click listener for options menu.
     * <p>
     * Redirect to {@link ly.priv.mobile.gui.activities.SettingsActivity} Or
     * {@link ly.priv.mobile.gui.activities.LoginActivity}
     * </p>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                // Logs out User from Privly Application
                Values values = Values.getInstance();
                values.setAuthToken(null);
                Intent gotoLogin = new Intent(getActivity(), LoginActivity.class);
                gotoLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(gotoLogin);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Adding Visual Clue Indicator elements so the user will know how many messages are present
    public void addIndicators(View view) {
        mIndicators = new ArrayList<ImageView>();
        LinearLayout indicatorContainer = (LinearLayout)view.findViewById(R.id.indicator_container);

        for(int i = 0; i < mListOfLinks.size(); i++) {
            ImageView indicator = new ImageView(getActivity());
            indicator.setImageDrawable(getResources().getDrawable(R.drawable.unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            indicatorContainer.addView(indicator, params);
            mIndicators.add(indicator);
        }
    }

    // Setting the visual indicator
    public void selectIndicator(int index) {
        Resources res = getResources();
        Drawable selected = res.getDrawable(R.drawable.selected_indicator);
        Drawable unselected = res.getDrawable(R.drawable.unselected_indicator);
        for(int i = 0; i < mListOfLinks.size(); i++) {
            if(i == index)
                mIndicators.get(i).setImageDrawable(selected);
            else
                mIndicators.get(i).setImageDrawable(unselected);
        }

        String positionString = (index+1) + "/" + mListOfLinks.size();
        mPositionTV.setText(positionString);
    }

}
