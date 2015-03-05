package ly.priv.mobile.grabbers;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ly.priv.mobile.R;
import ly.priv.mobile.api.gui.microblogs.IMicroblogs;
import ly.priv.mobile.api.gui.microblogs.MicroblogListPostsFragment;
import ly.priv.mobile.api.gui.microblogs.Post;
import ly.priv.mobile.gui.drawer.PrivlyApplication;
import ly.priv.mobile.gui.fragments.PrivlyApplicationFragment;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.TwitterUtil;
import ly.priv.mobile.utils.Utilities;
import ly.priv.mobile.utils.Values;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Fragment for login,logout get posts for Twitter
 * <p>
 * <ul>
 * <li>Implement interface IMicroblogs.</li>
 * </ul>
 * </p>
 * <p>
 * Dependencies :
 * <ul>
 * <li>/privly-android/libs/twitter4j-core-4.0.1.jar</li>
 * </ul>
 * </p>
 *
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 */
public class TwitterGrabberService extends Fragment implements
        IMicroblogs {
    private static final String TAG = TwitterGrabberService.class
            .getSimpleName();
    private static final int COUNT_OF_TWEETS = 100;
    private Values mValues;
    private ProgressBar mProgressBar;
    private WebView twitterLoginWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");
        View view = inflater.inflate(R.layout.activity_login_twitter,
                container, false);
        setTitle(getActivity());
        mValues = new Values(getActivity());
        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
        twitterLoginWebView = (WebView) view.findViewById(R.id.wvLoginTwitters);
        twitterLoginWebView.setBackgroundColor(Color.TRANSPARENT);
        twitterLoginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(ConstantValues.TWITTER_CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    String verifier = uri
                            .getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
                    mValues.setTwitterLoggedIn(true);
                    new TwitterGetAccessTokenTask().execute(verifier);
                    return true;
                }
                return false;
            }
        });
        if (!Utilities.isDataConnectionAvailable(getActivity())) {
            Log.d(TAG, getString(R.string.no_internet_connection));
            Utilities.showToast(getActivity(),
                    getString(R.string.no_internet_connection), true);
        } else {
            logIn();
        }
        return view;
    }

    /**
     * Run social GUI
     */
    private void runSocialGui() {
        getActivity().getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        MicroblogListPostsFragment microblogListPostsFragment = new MicroblogListPostsFragment();
        microblogListPostsFragment.setIMicroblogs(this);
        transaction.replace(R.id.container, microblogListPostsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Method for run login or show tweets
     */
    private void logIn() {
        Log.d(TAG, "logIn");
        mProgressBar.setVisibility(View.VISIBLE);
        if (!mValues.getTwitterLoggedIn()) {
            new TwitterAuthenticateTask().execute();
        } else {
            Uri uri = getActivity().getIntent().getData();
            if (uri != null
                    && uri.toString().startsWith(
                    ConstantValues.TWITTER_CALLBACK_URL)) {
                String verifier = uri
                        .getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
                new TwitterGetAccessTokenTask().execute(verifier);
            } else {
                new TwitterGetAccessTokenTask().execute("");
            }
        }
    }

    /**
     * AsyncTask for logIn to Twitter
     *
     * @author Ivan Metla e-mail: metlaivan@gmail.com
     */
    class TwitterAuthenticateTask extends
            AsyncTask<String, String, RequestToken> {

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            Log.d(TAG, "TwitterAuthenticateTask");
            if (requestToken != null) {
                twitterLoginWebView
                        .loadUrl(requestToken.getAuthenticationURL());
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * AsyncTask for get Access Token
     *
     * @author Ivan Metla e-mail: metlaivan@gmail.com
     */
    class TwitterGetAccessTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "TwitterGetAccessTokenTask");
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance()
                    .getRequestToken();
            AccessToken accessToken = null;
            if (!Utilities.isNullOrWhitespace(params[0])) {
                try {
                    accessToken = twitter.getOAuthAccessToken(requestToken,
                            params[0]);
                    mValues.setTwitterOauthToken(accessToken.getToken());
                    mValues.setTwitterOauthTokenSecret(accessToken
                            .getTokenSecret());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            } else {
                String accessTokenString = mValues.getTwitterOauthToken();
                String accessTokenSecret = mValues.getTwitterOauthTokenSecret();
                accessToken = new AccessToken(accessTokenString,
                        accessTokenSecret);
                TwitterUtil.getInstance().setTwitterFactory(accessToken);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            runSocialGui();
        }

    }

    // Overridden method for implementing IMicroblogs
    @Override
    public void logout(FragmentActivity fragmentActivity) {
        Log.d(TAG, "logout()");
        mValues.setTwitterLoggedIn(false);
        mValues.setTwitterOauthToken("");
        mValues.setTwitterOauthTokenSecret("");
        TwitterUtil.getInstance().reset();
        PrivlyApplicationFragment messageFragment = new PrivlyApplicationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantValues.PRIVLY_APPLICATION_KEY, PrivlyApplication.MESSAGE_APP);
        messageFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.container, messageFragment);
        transaction.commit();
    }

    @Override
    public ArrayList<Post> getPost(int page) {
        Log.d(TAG, "getPost()");
        Paging paging = new Paging(page, COUNT_OF_TWEETS);
        ArrayList<Post> listPost = new ArrayList<Post>();
        try {
            List<twitter4j.Status> statuses = TwitterUtil.getInstance()
                    .getTwitter().getHomeTimeline(paging);
            for (twitter4j.Status status : statuses) {
                String url = "";
                URLEntity[] entities = status.getURLEntities();
                for (int i = 0; i < entities.length; i++) {
                    // Since urls are shortened by the twitter t.co
                    // service, get the expanded urls to search for
                    // Privly links
                    url += "  " + entities[i].getExpandedURL();
                }
                listPost.add(new Post(status.getUser().getName(), status
                        .getUser().getScreenName(), status.getCreatedAt(),
                        status.getText() + url, status.getUser()
                        .getBiggerProfileImageURL()));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return listPost;
    }

    @Override
    public void setTitle(FragmentActivity fragmentActivity) {
        fragmentActivity.setTitle(R.string.privly_Twitter);
    }
}
