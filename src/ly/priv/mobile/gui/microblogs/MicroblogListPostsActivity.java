package ly.priv.mobile.gui.microblogs;

import java.util.ArrayList;

import ly.priv.mobile.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class MicroblogListPostsActivity extends SherlockFragment {
	private ArrayList<Post> mListPosts;
	private ListView mListViewPosts;
	private ListMicroblogAdapter mListMicroblogAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		this.mListViewPosts = ((ListView) view.findViewById(R.id.lView));
		mListPosts = new ArrayList<Post>();
		mListPosts.add(new Post("name1", "Nic1", "1d", "Mess on twits", ""));
		mListPosts.add(new Post("name2", "Nic2", "3d", "Mess on twits", ""));
		mListPosts.add(new Post("name3", "Nic3", "4d", "Mess on twits", ""));
		mListPosts.add(new Post("name4", "Nic4", "5d", "Mess on twits", ""));
		if (mListPosts != null) {
			mListMicroblogAdapter = new ListMicroblogAdapter(getActivity(),
					mListPosts);
			mListViewPosts.setAdapter(mListMicroblogAdapter);
		}
		return view;
	}

}
