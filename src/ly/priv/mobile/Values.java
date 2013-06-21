package ly.priv.mobile;


public final class Values {
	
	String prefs_name;
	
	Values()
	{
		prefs_name = "prefs_file";
	}
	
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