package ly.priv.mobile;

import ly.priv.mobile.PrivlyLinkStorageContract.LinksDb;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class for managing the database.
 *
 * @author Shivam Verma
 *
 */
public class LinksDbHelper extends SQLiteOpenHelper {
	/**
	 * Change Database version on upgrading the schema
	 */
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "PrivlyLinks.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ LinksDb.TABLE_NAME + " (" + LinksDb._ID + " INTEGER PRIMARY KEY,"
			+ LinksDb.COLUMN_NAME_SOURCE + TEXT_TYPE + COMMA_SEP
			+ LinksDb.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP
			+ LinksDb.COLUMN_NAME_SOURCE_ID + TEXT_TYPE + COMMA_SEP
			+ LinksDb.COLUMN_NAME_FROM + TEXT_TYPE + " );";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ LinksDb.TABLE_NAME;

	public LinksDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("CREATE STATEMENT", SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**
		 * Implement any upgrade policy here. Example
		 * db.execSQL(SQL_DELETE_ENTRIES); onCreate(db);
		 */

	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}