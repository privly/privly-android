package ly.priv.mobile.gui.fragments;

import android.content.Intent;
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
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

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
    private ImageView mLeftArrow, mRightArrow;
    private Integer mId = 0;

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
        mPositionTV = (LobsterTextView) view.findViewById(R.id.position_tv);
        mRightArrow = (ImageView) view.findViewById(R.id.right_arrow);
        mLeftArrow = (ImageView) view.findViewById(R.id.left_arrow);
        mUrlContentWebView = (WebView) webView;
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
                //if the event is not consumed by the gesture detector then it is sent to the webview
                return mGestureDetector.onTouchEvent(event) || mUrlContentWebView.onTouchEvent(event);
            }
        };
        webView.setOnTouchListener(mGestureListener);

        //setting icons for the indicators
        mRightArrow.setImageDrawable(
                new IconDrawable(getActivity(), Iconify.IconValue.fa_angle_right)
                        .colorRes(R.color.privlyDark));

        mLeftArrow.setImageDrawable(
                new IconDrawable(getActivity(), Iconify.IconValue.fa_angle_left)
                        .colorRes(R.color.privlyDark));

        loadUrlInWebview(mId);
        setPagePosition();
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
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
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
                        Toast.makeText(getActivity(),
                                getString(R.string.loading_next_post),
                                Toast.LENGTH_SHORT).show();
                        loadUrlInWebview(mId);
                        setPagePosition();
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.this_is_the_last_post),
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
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
                        setPagePosition();
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.this_is_the_first_post),
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
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
    private void loadUrlInWebview(Integer id) {
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

    private void setPagePosition() {
        String positionString = (mId + 1) + "/" + mListOfLinks.size();
        mPositionTV.setText(positionString);

        if (mId == 0)
            mLeftArrow.setVisibility(View.INVISIBLE);
        else
            mLeftArrow.setVisibility(View.VISIBLE);

        if (mId == mListOfLinks.size() - 1)
            mRightArrow.setVisibility(View.INVISIBLE);
        else
            mRightArrow.setVisibility(View.VISIBLE);
    }

}
