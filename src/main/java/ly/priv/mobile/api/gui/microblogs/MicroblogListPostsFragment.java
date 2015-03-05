package ly.priv.mobile.api.gui.microblogs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.gui.fragments.ShowContentFragment;
import ly.priv.mobile.utils.Utilities;

/**
 * Clas for showing posts for microblogs
 * <p>
 * For using this api you must realize interface
 * {@link ly.priv.mobile.api.gui.microblogs.IMicroblogs} and set it with method
 * <code>setIMicroblogs</code>
 * </p>
 * <p>
 * <ul>
 * <li>If privly link contained in message then Redirect User to
 * {@link ly.priv.mobile.gui.fragments.ShowContentFragment} ShowContent Activity</li> *
 * </ul>
 * </p>
 *
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 */
public class MicroblogListPostsFragment extends Fragment implements
        OnScrollListener {
    private final String TAG = getClass().getSimpleName();
    private ProgressBar mProgressBar;
    private View mFooterView;
    private ListView mListViewPosts;
    private ListMicroblogAdapter mListMicroblogAdapter;
    private int mPage;
    private boolean mIsLoading;
    private ArrayList<Post> mPosts;
    private IMicroblogs mIMicroblogs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mIMicroblogs.setTitle(getActivity());
        mFooterView = (View) inflater.inflate(R.layout.loading_layout, null);
        initializeComponent(view);
        new GetPostsTask().execute(mPage);
        return view;
    }

    /**
     * Initialize component
     *
     * @param view
     */
    private void initializeComponent(View view) {
        setHasOptionsMenu(true);
        mListViewPosts = ((ListView) view.findViewById(R.id.lView));
        mListViewPosts.addFooterView(mFooterView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoadingData);
        mListViewPosts.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ArrayList<String> listOfUrls = Utilities.fetchPrivlyUrls(mPosts
                        .get(position).getMessage());
                if (listOfUrls.size() > 0) {
                    mPage = 1;
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    ShowContentFragment showContent = new ShowContentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("listOfLinks", listOfUrls);
                    showContent.setArguments(bundle);
                    transaction.replace(R.id.container, showContent);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(),
                            R.string.message_not_containe_privly_link,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mListViewPosts.setOnScrollListener(this);
        mPosts = new ArrayList<Post>();
        mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(), mPosts);
        mListViewPosts.setAdapter(mListMicroblogAdapter);
        mPage = 1;
    }

    /**
     * Inflate options menu with the layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout_microblog, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Item click listener for options menu.
     * <p>
     * logout
     * </p>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mIMicroblogs.logout(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * AsyncTask for get posts
     *
     * @author Ivan Metla e-mail: metlaivan@gmail.com
     */
    class GetPostsTask extends AsyncTask<Integer, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            if (mPage == 1)
                mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Post> doInBackground(Integer... params) {
            return mIMicroblogs.getPost(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            if (posts != null) {
                mPosts.addAll(posts);
                mListMicroblogAdapter.notifyDataSetChanged();
                if (mPage > 1) {
                    mListViewPosts.setSelection(mPosts.size() - 1);
                    mListViewPosts.removeFooterView(mFooterView);
                    mIsLoading = false;
                }
            }
            if (mPage == 1)
                mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Set Interface IMicroblogs
     *
     * @param iMicroblogs the mIMicroblogs to set
     */
    public void setIMicroblogs(IMicroblogs iMicroblogs) {
        this.mIMicroblogs = iMicroblogs;
    }

    // overridden methods for implementing endless loading
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mListMicroblogAdapter == null)
            return;

        if (mListMicroblogAdapter.getCount() == 0)
            return;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !mIsLoading) {
            // It is time to add new data. We call the listener
            mListViewPosts.addFooterView(mFooterView);
            mIsLoading = true;
            mPage++;
            new GetPostsTask().execute(mPage);
        }

    }

}
