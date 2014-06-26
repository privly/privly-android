package ly.priv.mobile;

import android.provider.BaseColumns;

/**
 * Defines Table and Column Names for the Privly Db.
 * 
 * @author Shivam Verma
 * 
 */

public final class PrivlyLinkStorageContract {

	/**
	 * To prevent someone from accidentally instantiating a contract class, give
	 * it an empty constructor
	 */
	public PrivlyLinkStorageContract() {
	}

	public static abstract class LinksDb implements BaseColumns {
		public static final String TABLE_NAME = "links";
		public static final String COLUMN_NAME_SOURCE = "source";
		public static final String COLUMN_NAME_LINK = "privlyLink";
		public static final String COLUMN_NAME_FROM = "userName";
		public static final String COLUMN_NAME_SOURCE_ID = "sourceID";
	}
}
