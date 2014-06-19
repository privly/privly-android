package ly.priv.mobile.gui.socialnetworks;

import java.util.ArrayList;
import java.util.Map;

public interface ISocialNetworks {
	
	public ArrayList<SUser> getListOfUsers();
	
	public Map<String, Object> getListOfMessagesFromFaceBook(String dialogID);
}
