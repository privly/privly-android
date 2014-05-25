package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SListUserMessagesActivity extends SherlockFragment {
	private ArrayList<SMessage> mListUserMess;
	private ListUserMessagesAdapter mListUserMessagesAdapter;
	private ListView mListViewUserMessages;


	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		this.mListViewUserMessages = ((ListView)view.findViewById(R.id.lView));
		mListUserMess=(ArrayList<SMessage>)getArguments().getSerializable(
		        "UserMessages");
		System.out.println(mListUserMess.get(0).getMessage());
		if (this.mListUserMess != null) {
			this.mListUserMessagesAdapter = new ListUserMessagesAdapter(
					getActivity(), this.mListUserMess);
			this.mListViewUserMessages
					.setAdapter(this.mListUserMessagesAdapter);
		}
		return view;
	}
}