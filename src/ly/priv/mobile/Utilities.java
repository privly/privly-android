package ly.priv.mobile;

import android.content.Context;
import android.widget.Toast;
/**
 * 
 * @author Shivam Verma
 * Utilities class contains simple functions that should be used 
 * wherever possible. 
 */
public class Utilities {
	
	/**
	 * Check validity of an EMail address using RegEx
	 * @param email_to_check
	 * @return boolean
	 */
	public static boolean isValidEmail(String email_to_check)
	{
		String email_pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		if(email_to_check.matches(email_pattern))
			return true;
		else
			return false;		
	}
	
	/**
	 * Show Toast on screen for Toast.LENGTH_SHORT time. 
	 * @param c Context of the class which calls this method. 
	 * @param Text_to_toast 
	 */
	public static void showToast(Context c, String Text_to_toast)
	{
		Toast.makeText(c, Text_to_toast, Toast.LENGTH_SHORT).show();
	}

}
