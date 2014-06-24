package com.arpit.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	protected String[] mBlogPostTitles;
	public static final int Number_of_posts = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        if(isNetworkAvailable()){
        	GetBlogPostTask getblogpost = new GetBlogPostTask();
            getblogpost.execute();
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Network UnAvailable", Toast.LENGTH_LONG).show();
        }
  
        //Resources res = getResources();
        //mAndroidNames = res.getStringArray(R.array.Android_Names);
        
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAndroidNames);
        //setListAdapter(adapter);
    }


    private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = cm.getActiveNetworkInfo();
	
		Boolean isAvailable = false;
		if(networkinfo != null && networkinfo.isConnected()){
			isAvailable = true;
		}
		return isAvailable;
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
    

	public void updateList() {
		// TODO Auto-generated method stub
		if(mBlogData == null){
			//TODO: handle error
		}
		else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				mBlogPostTitles = new String[jsonPosts.length()];
				for(int i=0;i<jsonPosts.length();i++){
					JSONObject jsonpost = jsonPosts.getJSONObject(i);
					String title = jsonpost.getString("title");
					title = Html.fromHtml(title).toString();
					mBlogPostTitles[i] = title;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mBlogPostTitles);
				setListAdapter(adapter);
			} catch (JSONException e) {
				Log.e(TAG, "exception caught: ", e);
			}
		}
	}

    
    public class GetBlogPostTask extends AsyncTask<Object, Void, JSONObject>{

		@Override
		protected JSONObject doInBackground(Object... params) {
			int responseCode = -1;
			JSONObject jsonresponse = null;
			try {
				URL blogFeedurl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="+ Number_of_posts);
			    HttpURLConnection connection = (HttpURLConnection) blogFeedurl.openConnection();
	            connection.connect();
	            
	            responseCode = connection.getResponseCode();
	            
	            if(responseCode == HttpURLConnection.HTTP_OK)
	            {
	            	InputStream inputstream = connection.getInputStream();
	            	Reader reader =new InputStreamReader(inputstream);
	            	int contetntlength = connection.getContentLength();
	            	char[] charArray = new char[contetntlength];
	            	reader.read(charArray);
	            	String ReasponseData = new String(charArray);
	            	
	            	jsonresponse = new JSONObject(ReasponseData);
	            	//String status = jsonresponse.getString("status");
	            	//Log.v(TAG, status);
	            	
	            	//JSONArray jsonposts = jsonresponse.getJSONArray("posts");
	            	//for(int i=0; i<jsonposts.length(); i++){
	            		//JSONObject jp = jsonposts.getJSONObject(i);
	            		//String title = jp.getString("title");
	            		//Log.v(TAG, "Post "+i+": "+title);
	            	//}
	            }
	            else{
	            	Log.i(TAG, "unsuccessful HTTP Response code: "+ responseCode);
	            }   
	            
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
			return jsonresponse;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			mBlogData = result;
			updateList();
		}
    	
    }

}
