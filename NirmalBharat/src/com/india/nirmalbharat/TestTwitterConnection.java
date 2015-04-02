package com.india.nirmalbharat;

import org.apache.http.Header;

import com.india.nirmalbharat.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TestTwitterConnection extends Activity {
	
	boolean tested = false;
	TextView message;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitterconn);
		message = (TextView)findViewById(R.id.messageView);
	}	
	
	public void testTweet(View view) {
		RequestParams postParams = new RequestParams();
		postParams.add("tweet","Hi there! I want a cleaner India.");
		postParams.add("username", StringUtil.currentUser);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");
		client.post("http://nirmalbharat.mybluemix.net/rest/tweet", postParams, new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int status, Header[] headers, byte[] body,
					Throwable error) {
				if(status == 404){
					Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
				}  
				else if(status == 500){
					Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
				} 
				else{
					Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_LONG).show();
				}
			}
		
			@Override
			public void onSuccess(int status, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				if(response.equals("PASS")){
					message.setText("Connection successfully tested!");
				} else {
					message.setText("Test failed. Never mind.");
				}
				tested = true;
			}
			
		});
	}

	
	public void continueToCheckin(View view) {
		if(!tested) {
			Toast.makeText(getApplicationContext(), "Please test your Twitter connection first", Toast.LENGTH_LONG).show();
			return;
		}
		Intent homeIntent = new Intent(getApplicationContext(),CheckinActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);  
	}

}
