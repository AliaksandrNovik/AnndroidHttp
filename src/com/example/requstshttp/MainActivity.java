package com.example.requstshttp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;








import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	String myJSON;
	String updatedJson;

	private static final String TAG_RESULTS="result";
	private static final String TAG_ID = "id";
	private static final String TAG_DATE = "date";
	private static final String TAG_TIME = "time";
	private static final String TAG_MESSAGE_TYPE ="messageType";
	private static final String TAG_MESSAGE_TEXT ="messageText";


	JSONArray messages = null;

	ArrayList<HashMap<String, String>> messageList;
	ArrayList <HashMap<String, String>>mylist_title;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		messageList = new ArrayList<HashMap<String,String>>();
		mylist_title = new ArrayList<HashMap<String,String>>();
		getData();
	}

	protected void initTitleList() {
		HashMap<String,String> titleRow = new HashMap<String,String>();

		titleRow.put(TAG_ID, "Id");
		titleRow.put(TAG_TIME, "Time");
		titleRow.put(TAG_MESSAGE_TYPE, "Type");
		titleRow.put(TAG_DATE, "Date");
		titleRow.put(TAG_MESSAGE_TEXT, "Text");
		mylist_title.add(titleRow);
	}


	protected void showList(){
		try {
			JSONArray jsonArr = new JSONArray(myJSON);

			for(int i=0; i<jsonArr.length(); i++){
				JSONObject c = jsonArr.getJSONObject(i);
				String id = c.getString(TAG_ID);
				String time = c.getString(TAG_TIME);
				String type = c.getString(TAG_MESSAGE_TYPE);
				String date = c.getString(TAG_DATE);
				String gender = c.getString(TAG_MESSAGE_TEXT);

				HashMap<String,String> persons = new HashMap<String,String>();

				persons.put(TAG_ID,id);
				persons.put(TAG_TIME,time);
				persons.put(TAG_MESSAGE_TYPE,type);
				persons.put(TAG_DATE,date);
				persons.put(TAG_MESSAGE_TEXT,gender);
				messageList.add(persons);
			}
			initTitleList();
			setContentView(R.layout.activity_main);
			ListAdapter adapter = new SimpleAdapter(
					this, messageList, R.layout.list_item,
					new String[]{TAG_ID,TAG_TIME,TAG_MESSAGE_TYPE, TAG_DATE, TAG_MESSAGE_TEXT},
					new int[]{R.id.id, R.id.time, R.id.messageType, R.id.date,  R.id.messageText}
					);

			
			ListView list = (ListView) findViewById(R.id.listView2);
			
			list.setAdapter(adapter);

			ListAdapter adapterTitle = new SimpleAdapter(this, mylist_title, R.layout.list_item, 
					new String[]{TAG_ID,TAG_TIME,TAG_MESSAGE_TYPE, TAG_DATE, TAG_MESSAGE_TEXT},
					new int[]{R.id.id, R.id.time, R.id.messageType, R.id.date,  R.id.messageText}
					);
			ListView listTitle = (ListView) findViewById(R.id.id);
			listTitle.setAdapter(adapterTitle);


		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void getData(){
		class GetDataJSON extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
				HttpGet httppget = new HttpGet("http://tomcat7-aliaksandrnovik.rhcloud.com/Requests");
				// Depends on your web service
				httppget.setHeader("Content-type", "application/json");

				InputStream inputStream = null;
				String result = null;
				try {
					HttpResponse response = httpclient.execute(httppget);
					HttpEntity entity = response.getEntity();

					inputStream = entity.getContent();
					// json is UTF-8 by default
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();

					String line = null;
					while ((line = reader.readLine()) != null)
					{
						sb.append(line + "\n");
					}
					result = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					// Oops
				}
				finally {
					try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
				}
				updatedJson = result;
				return result;
			}

			@Override
			protected void onPostExecute(String result){
				myJSON=result;
				showList();
			}
		}
		GetDataJSON g = new GetDataJSON();
		g.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		  switch(item.getItemId()) {
	        case R.id.action0:
	        	myJSON = updatedJson;
	        	messageList.clear();
	        	mylist_title.clear();
	        	getData();
	            return true;
	        }

		return super.onOptionsItemSelected(item);
	}

}
