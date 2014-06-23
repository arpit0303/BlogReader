package com.arpit.listview_blog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	protected String[] mBlogPostTitles;
	public static final int Number_of_posts = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        
        GetBlogPostTask getblogpost = new GetBlogPostTask();
        getblogpost.execute();
        
        //Resources res = getResources();
        //mAndroidNames = res.getStringArray(R.array.Android_Names);
        
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAndroidNames);
        //setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public class GetBlogPostTask extends AsyncTask<Object, Void, String>{

		@Override
		protected String doInBackground(Object... params) {
			int responseCode = -1;
			try {
				URL blogFeedurl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="+ Number_of_posts);
			    HttpsURLConnection connection = (HttpsURLConnection) blogFeedurl.openConnection();
	            connection.connect();
	            
	            responseCode = connection.getResponseCode();
	            Log.i(TAG, "code: "+ responseCode);
	            
	        } 
	        catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Exception caught: ",e);
			}
	        catch (IOException e) {
	        	Log.e(TAG, "Exception caught: ",e);
			}
	        catch (Exception e) {
	        	Log.e(TAG, "Exception caught: ",e);
			}
			return "code: "+ responseCode;
		}
    	
    }

}
