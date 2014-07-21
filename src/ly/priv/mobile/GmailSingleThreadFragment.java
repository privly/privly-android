package ly.priv.mobile;

import java.util.ArrayList;
import java.util.List;

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
import com.google.api.services.gmail.model.MessagePartHeader;

public class GmailSingleThreadFragment extends SherlockFragment{
	ListView mailsListView;
	String currentThreadId;
	EmailThreadObject currentThread;
	ArrayList<SingleEmailObject> messages;
	StringBuilder builder;
	List<MessagePartHeader> mailHeaders;
	
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
		messages = new ArrayList<SingleEmailObject>();
		for (Message m: currentThread.getMessages()){
			SingleEmailObject mailObject = new SingleEmailObject();
			mailHeaders = m.getPayload().getHeaders();
			for (MessagePartHeader mHeader: mailHeaders){
				if (mHeader.getName().equals("From")){
					mailObject.setMailSender(mHeader.getValue());
				}
				else if (mHeader.getName().equals("Date")){
					mailObject.setMailTime(Utilities.getTimeForGmail(mHeader.getValue()));
				}
			}
			if (m.getPayload().getMimeType().contains("multipart")){
				builder = new StringBuilder();
				for (MessagePart part: m.getPayload().getParts()){
					if (part.getMimeType().contains("multipart")){
						for (MessagePart part2: part.getParts()){
							if (part2.getMimeType().equals("text/plain")){
								builder.append(new String(Base64.decodeBase64(part2.getBody().getData())));
							}
						}
					}
					
					else if (part.getMimeType().equals("text/plain")){
						builder.append(new String(Base64.decodeBase64(part.getBody().getData())));
					}
				}
				mailObject.setMailSnippet(builder.toString());
			}
			//if mimetype is not multipart, there is just one part for each message
			else {
				mailObject.setMailSnippet(new String(Base64.decodeBase64(m.getPayload().getBody().getData())));
			}
			messages.add(mailObject);
		}

		mailsListView.setAdapter(new ListSingleMailThreadAdapter(getActivity(), messages));
		mailsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ArrayList<String> listOfUrls = Utilities.fetchPrivlyUrls(messages
						.get(position).getMailSnippet());
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