package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;

import ly.priv.mobile.R;

import com.actionbarsherlock.app.SherlockFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SListUsersActivity extends SherlockFragment {
	private ArrayList<SUser> mListUserMess;
	private ListUsersAdapter mListUserMessagesAdapter;
	private ListView mListViewUsers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);
		this.mListViewUsers = ((ListView) view.findViewById(R.id.lView));
	
		ArrayList<SMessage> listSMessage = new ArrayList<SMessage>();
		mListUserMess = new ArrayList<SUser>();
		listSMessage.add(new SMessage("hid", "13.11.1991", "", true));
		listSMessage.add(new SMessage("hid", "13.11.1991", "", false));
		listSMessage.add(new SMessage("hid", "13.11.1991", "", false));
		listSMessage.add(new SMessage("hid", "13.11.1991", "", true));
		mListUserMess.add(new SUser("name",
				"hi. What are you doing", "13.11.2014", "",
				listSMessage));
		mListUserMess.add(new SUser("name1",
				"hi. What are you doing", "13.01.2014", "",
				listSMessage));
		mListUserMess.add(new SUser("name2",
				"hi. What are you doing", "13.12.2014", "",
				listSMessage));
			if (this.mListUserMess != null) {
				this.mListUserMessagesAdapter = new ListUsersAdapter(getActivity(),
						this.mListUserMess);
				this.mListViewUsers.setAdapter(this.mListUserMessagesAdapter);
			}
	
		mListViewUsers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Intent localIntent = new Intent(getActivity().getApplicationContext(),
//						SListUserMessagesActivity.class);
//				localIntent.putExtra("UserMessages", mListUserMess
//						.get(position).getListUserMess());
//				startActivity(localIntent);

			}
		});
		return view;
		
	}

}