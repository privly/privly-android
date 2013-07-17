
package ly.priv.mobile;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
                Intent gotoCreateNewPost = new Intent(getApplicationContext(), NewPost.class);
                gotoCreateNewPost.putExtra("JsAppName", arrCreate[position]);
                startActivity(gotoCreateNewPost);
            }
        });

    }

}
