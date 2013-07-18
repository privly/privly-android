
package ly.priv.mobile;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class displays the Home Activity for a user after authentication. Gives
 * the user options to Create New Privly posts or Read Privly Posts from his
 * social / email feed. Read option has not been implemented yet.
 * 
 * @author Shivam Verma
 */
public class Home extends Activity {
    /** Called when the activity is first created. */
    ListView readListView, createListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        TextView createHeadingEditText = (TextView)findViewById(R.id.createNewHeadingTextView);
        TextView readHeadingEditText = (TextView)findViewById(R.id.readPostsHeadingTextView);
        Typeface lobster = Typeface.createFromAsset(getAssets(), "fonts/Lobster.ttf");
        createHeadingEditText.setTypeface(lobster);
        readHeadingEditText.setTypeface(lobster);

        /**
         * Create two ListViews which display create/read options.
         */
        final String[] arrCreate = {
                "PlainPost", "ZeroBin"
        };
        final String[] arrRead = {
                "GMail", "Facebook", "Twitter"
        };
        ArrayList createArrayList = new ArrayList<String>(Arrays.asList(arrCreate));
        ArrayList readArrayList = new ArrayList<String>(Arrays.asList(arrRead));

        createListView = (ListView)findViewById(R.id.create_listView);
        readListView = (ListView)findViewById(R.id.read_listView);

        ArrayAdapter<String> createArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item, createArrayList);
        ArrayAdapter<String> readArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
                readArrayList);

        createListView.setAdapter(createArrayAdapter);
        readListView.setAdapter(readArrayAdapter);

        /**
         * OnItemClickListener for creating posts ListView. The name of the
         * selected Posting app is sent with the intent to
         * {@link ly.priv.mobile.Home}
         */
        createListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (Utilities.isDataConnectionAvailable(getApplicationContext())) {
                    Intent gotoCreateNewPost = new Intent(getApplicationContext(), NewPost.class);
                    gotoCreateNewPost.putExtra("JsAppName", arrCreate[position]);
                    startActivity(gotoCreateNewPost);
                } else
                    Utilities.showToast(getApplicationContext(),
                            "Oops! Seems like there\'s no data connection.", true);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu_layout_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent gotoSettings = new Intent(this, Settings.class);
                startActivity(gotoSettings);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
