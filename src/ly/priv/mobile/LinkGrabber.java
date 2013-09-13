package ly.priv.mobile;

import ly.priv.mobile.PrivlyLinkStorageContract.LinksDb;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class LinkGrabber extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		LinksDbHelper mDbHelper = new LinksDbHelper(getApplicationContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(LinksDb.COLUMN_NAME_SOURCE, "gmai");
		values.put(
				LinksDb.COLUMN_NAME_LINK,
				"https://privlyalpha.org/apps/PlainPost/show?privlyApp=PlainPost&privlyInject1=true&random_token=8b957e2bb7&privlyDataURL=https%3A%2F%2Fprivlyalpha.org%2Fposts%2F1027.json%3Frandom_token%3D8b957e2bb7#privlyInject1=true&p=p");

		long newRowId;
		newRowId = db.insert(LinksDb.TABLE_NAME, null, values);
		Log.d("returnID", Long.toString(newRowId));

	}
}
