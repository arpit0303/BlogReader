package com.arpit.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	public static final int Number_of_posts = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;
	private ProgressBar mProgressBar;
	private static final String hashmap_title = "title";
	private static final String hashmap_value = "author";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        
        //checking network availability 
        if(isNetworkAvailable()){
        	mProgressBar.setVisibility(View.VISIBLE);
        	GetBlogPostTask getblogpost = new GetBlogPostTask();
            getblogpost.execute();
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Network UnAvailable", Toast.LENGTH_LONG).show();
        }

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
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	try {
    		JSONArray jsonposts = mBlogData.getJSONArray("posts");
        	JSONObject post = jsonposts.getJSONObject(position);
			String BlogUrl = post.getString("url");
			
			//Intent intent = new Intent(Intent.ACTION_VIEW);
			Intent intent = new Intent(this,BlogWebViewActivity.class);
			intent.setData(Uri.parse(BlogUrl));
			startActivity(intent);
			
		} catch (JSONException e) {
			Log.e(TAG,"Exception caught: ",e);
		}
    }
    

	public void updateList() {
		mProgressBar.setVisibility(View.INVISIBLE);
		
		if(mBlogData == null){
			//Alert Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops! Sorry!");
			builder.setMessage("There is a problem getting data from the blog");
			builder.setPositiveButton(android.R.string.ok, null);
			AlertDialog Dialog = builder.create();
			Dialog.show();
			
			//empty TextView
			TextView mEmptyTextView = (TextView) getListView().getEmptyView();
			mEmptyTextView.setText(getString(R.string.no_item));
		}
		else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				
				//mBlogPostTitles = new String[jsonPosts.length()];
				ArrayList<HashMap<String, String>> blogposts = new ArrayList<HashMap<String,String>>();
				
				for(int i=0;i<jsonPosts.length();i++)
				{
					JSONObject jsonpost = jsonPosts.getJSONObject(i);
					String title = jsonpost.getString(hashmap_title);
					//convert any HTML syntex to normal text
					title = Html.fromHtml(title).toString();
					//mBlogPostTitles[i] = title;
					String author = jsonpost.getString(hashmap_value);
					author = Html.fromHtml(author).toString();
					HashMap<String, String> blogpost = new HashMap<String, String>();
					blogpost.put(hashmap_title, title);
					blogpost.put(hashmap_value, author);
					
					blogposts.add(blogpost);
				}
				
				String[] keys = {hashmap_title,hashmap_value};
				int[] ids = {android.R.id.text1,android.R.id.text2};
				SimpleAdapter adapter = new SimpleAdapter(this, blogposts, android.R.layout.simple_list_item_2, keys, ids);
				setListAdapter(adapter);
				
			} catch (JSONException e) {
				Log.e(TAG, "exception caught: ", e);
			}
			catch (Exception e){
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
				//connecting to a URL
				URL blogFeedurl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="+ Number_of_posts);
			    HttpURLConnection connection = (HttpURLConnection) blogFeedurl.openConnection();
	            connection.connect();
	            
	            //Get the response code which said connection is done or not
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
			//After getting data Update the List
			updateList();
		}
    	
    }

}
