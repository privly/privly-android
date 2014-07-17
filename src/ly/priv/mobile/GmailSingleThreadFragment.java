package ly.priv.mobile;

import java.util.ArrayList;

import ly.priv.mobile.gui.ShowContentFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

public class GmailSingleThreadFragment extends SherlockFragment{
	ListView mailsListView;
	String currentThreadId;
	EmailThreadObject currentThread;
	ArrayList<String> messages;
	StringBuilder builder;
	
	public GmailSingleThreadFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_list, container, false);
		mailsListView = (ListView) view.findViewById(R.id.lView);
		currentThread = getArguments().getParcelable("currentThread");
		//currentThread = ((MainActivity) getActivity()).getCurrentThread();
		messages = new ArrayList<String>();
		for (Message m: currentThread.getMessages()){
			if (m.getPayload().getMimeType().contains("multipart")){
				builder = new StringBuilder();
				for (MessagePart part: m.getPayload().getParts()){
					if (part.getMimeType().contains("multipart")){
						for (MessagePart part2: part.getParts()){
							if (part2.getMimeType().equals("text/plain")){
								builder.append(new String(Base64.decodeBase64(part2.getBody().getData())));
//								Log.d("part2", 
//										new String(Base64.decodeBase64(part2.getBody().getData())));
							}
						}
					}
					
					else if (part.getMimeType().equals("text/plain")){
						builder.append(new String(Base64.decodeBase64(part.getBody().getData())));
							//Log.d("part", 
									//new String(Base64.decodeBase64(part.getBody().getData())));
					}
				}
				messages.add(builder.toString());
			}
			//if mimetype is not multipart, there is just one part for each message
			else {
				messages.add(new String(Base64.decodeBase64(m.getPayload().getBody().getData())));
//				Log.d(m.getPayload().getMimeType(), 
//						new String(Base64.decodeBase64(m.getPayload().getBody().getData())));
			}
		}

//		for (int i=0; i< messages.size();i++){
//			Log.d(i+"",messages.get(i));
//		}
		mailsListView.setAdapter(new ListSingleMailThreadAdapter(getActivity(), messages));
		mailsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ArrayList<String> listOfUrls = Utilities.fetchPrivlyUrls(messages
						.get(position));
				if (listOfUrls.size() > 0) {
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
		return view;
	}
}