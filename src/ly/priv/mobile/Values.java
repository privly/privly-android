package ly.priv.mobile;

/**
 * 
 * @author Shivam Verma
 *	Values class is used to access values that should be accessible to 
 *	all classes throughout the application. 
 */
public final class Values {
	
	String prefs_name;
	
	Values()
	{
		prefs_name = "prefs_file";
	}
	
	/**
	 * 
	 * @return prefs_name The name of the SharedPreference File
	 */
	String getPrefs_name()
	{
		return prefs_name;
	}

}

/** 
Key Pair Values saved in shared preferences
uname - email id of user
pwd - password of the User
base_url - domain_name to which the user authorizes 
**/