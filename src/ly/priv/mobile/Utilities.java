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
	 * @param emailToCheck
	 * @return boolean
	 */
	public static boolean isValidEmail(String emailToCheck)
	{
		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		if(emailToCheck.matches(emailPattern))
			return true;
		else
			return false;		
	}
	
	/**
	 * Show Toast on screen. 
	 * @param c Context of the class which calls this method. 
	 * @param textToToast
	 * @param longToast
	 */
	public static void showToast(Context c, String textToToast, Boolean longToast)
	{
		if (longToast)
			Toast.makeText(c, textToToast, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(c, textToToast, Toast.LENGTH_SHORT).show();
	}
}