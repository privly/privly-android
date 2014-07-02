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
	 * Get first post for microblogs
	 * 
	 * @return list of Post object
	 */
	public ArrayList<Post> getFirstPost();
	/**
	 * Get next post for microblogs
	 * 
	 * @return list of Post object
	 */
	public ArrayList<Post> getNextPost();
	
}
