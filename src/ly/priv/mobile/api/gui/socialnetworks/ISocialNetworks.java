package ly.priv.mobile.api.gui.socialnetworks;

import java.util.ArrayList;
import java.util.Map;

public interface ISocialNetworks {
	
	public ArrayList<SUser> getListOfUsers();
	
	public Map<String, Object> getListOfMessages(String dialogID);
	
	public Map<String, Object> fetchNextMessages(String url);
}
