package ly.priv.mobile.api.gui.microblogs;

import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Interface for exchange data from grabber to GUI for microblogs
 *
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 */

public interface IMicroblogs {
    /**
     * Get posts for microblogs
     *
     * @param page - page for loading data
     * @return list of Post object
     */
    public ArrayList<Post> getPost(int page);

    /**
     * Logout from microblogs
     */
    public void logout(FragmentActivity fragmentActivity);

    /**
     * Set title
     */
    public void setTitle(FragmentActivity fragmentActivity);
}
