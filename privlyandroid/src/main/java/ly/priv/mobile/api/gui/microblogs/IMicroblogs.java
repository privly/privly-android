package ly.priv.mobile.api.gui.microblogs;

import java.util.ArrayList;

/**
 * Interface for exchange data from grabber to GUI for microblogs
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */

public interface IMicroblogs {
	/**
	 * Get posts for microblogs
	 * 
	 * @param page
	 *            - page for loading data
	 * @return list of Post object
	 */
	public ArrayList<Post> getPost(int page);

	/**
	 * Logout from microblogs
	 */
	public void logout();

	/**
	 * Set title
	 */
	public void setTitle();
}
