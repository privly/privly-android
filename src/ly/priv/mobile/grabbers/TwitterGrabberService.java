package ly.priv.mobile.grabbers;

import ly.priv.mobile.R;
import ly.priv.mobile.api.gui.microblogs.IMicroblogs;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

public class TwitterGrabberService extends SherlockFragment implements IMicroblogs{
	private static final String TAG = TwitterGrabberService.class.getSimpleName();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list, container, false);	
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(R.string.privly_Login_Twitter);
		
		
		return view;
		
	}
}
