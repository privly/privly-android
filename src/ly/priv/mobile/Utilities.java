package ly.priv.mobile;

import android.content.Context;
import android.widget.Toast;

public class Utilities {
	
	public static boolean isValidEmail(String email_to_check)
	{
		String email_pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		if(email_to_check.matches(email_pattern))
			return true;
		else
			return false;		
	}
	
	public static void showToast(Context c, String Text_to_toast)
	{
		Toast.makeText(c, Text_to_toast, Toast.LENGTH_SHORT).show();
	}

}
