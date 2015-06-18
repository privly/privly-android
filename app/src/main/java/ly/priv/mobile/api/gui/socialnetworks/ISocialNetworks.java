package ly.priv.mobile.api.gui.socialnetworks;

import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Interface for exchange data from grabber to GUI for social networks
 *
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 *
 */
public interface ISocialNetworks {
	// constants for exchange data
	public static final String ARRAY = "Array";
	public static final String NEXTLINK = "NextLink";

	/**
	 * Get inbox(List of users with last message and time)
	 *
	 * @return list of SUser object
	 */
	public ArrayList<SUser> getListOfUsers();

	/**
	 * Get list of messages for dialog id
	 *
	 * @param dialogID
	 *            - dialog id
	 * @return Map with 2 item
	 *
	 *         <p>
	 *         <b>For example:</b>
	 *         </p>
	 *         <p>
	 *         String nextUrlForLoadingMessages = "";
	 *         </p>
	 *         <p>
	 *         ArrayList<SMessage> listOfUsersMessage = new
	 *         ArrayList<SMessage>();
	 *         </p>
	 *         <p>
	 *         Map<String, Object> res = new HashMap<String, Object>();
	 *         </p>
	 *         <p>
	 *         res.put(ARRAY, listOfUsersMessage);
	 *         </p>
	 *         <p>
	 *         res.put(NEXTLINK, nextUrlForLoadingMessages);
	 *         </p>
	 *
	 */
	public Map<String, Object> getListOfMessages(String dialogID);

	/**
	 * Get list of next messages for pull and refresh
	 *
	 * @param url
	 *            - link for next messages
	 * @return Map with 2 item
	 *
	 *         <p>
	 *         <b>For example:</b>
	 *         </p>
	 *         <p>
	 *         String nextUrlForLoadingMessages = "";
	 *         </p>
	 *         <p>
	 *         ArrayList<SMessage> listOfUsersMessage = new
	 *         ArrayList<SMessage>();
	 *         </p>
	 *         <p>
	 *         Map<String, Object> res = new HashMap<String, Object>();
	 *         </p>
	 *         <p>
	 *         res.put(ARRAY, listOfUsersMessage);
	 *         </p>
	 *         <p>
	 *         res.put(NEXTLINK, nextUrlForLoadingMessages);
	 *         </p>
	 *
	 */
	public Map<String, Object> fetchNextMessages(String url);

	/**
	 * Logout from social networks
	 */
	public void logout(FragmentActivity fragmentActivity);

	/**
	 * Set title
	 */
	public void setTitle(FragmentActivity fragmentActivity);
}
