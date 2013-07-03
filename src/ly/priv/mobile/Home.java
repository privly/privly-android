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

public class Home extends Activity {
    /** Called when the activity is first created. */
	ListView readListView;
	ListView createListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        final String[] arrCreate = {"PlainPost", "ZeroBin"};        
        final String[] arrRead = {"GMail", "Facebook", "Twitter"};
        ArrayList createArrayList = new ArrayList<String>(Arrays.asList(arrCreate));
        ArrayList readArrayList = new ArrayList<String>(Arrays.asList(arrRead));

        createListView = (ListView) findViewById(R.id.create_listView);
        readListView = (ListView) findViewById(R.id.read_listView);
        
        ArrayAdapter<String> createArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, createArrayList); 
        ArrayAdapter<String> readArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, readArrayList); 
        
        createListView.setAdapter(createArrayAdapter);
        readListView.setAdapter(readArrayAdapter);
        
        createListView.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override 
        	public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            { 
        		Intent gotoCreateNewPost = new Intent(getApplicationContext(), NewPost.class);
        		gotoCreateNewPost.putExtra("JsAppName", arrCreate[position]);
        		startActivity(gotoCreateNewPost);
            }
		});

    } 
    
}