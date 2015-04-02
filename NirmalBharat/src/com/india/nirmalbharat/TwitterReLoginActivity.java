package com.india.nirmalbharat;

import com.india.nirmalbharat.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class TwitterReLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitterrelogin);
	}	
	
	public void twitterLoginRetry(View view) {
		RadioButton retryYes = (RadioButton)findViewById(R.id.twitter_login_retry);
		if(retryYes.isChecked()) {
			Toast.makeText(getApplicationContext(), "Contacting Twitter. Please wait...", Toast.LENGTH_LONG).show();
			AsyncHttpClient client = new AsyncHttpClient();	
			client.get("http://nirmalbharat.mybluemix.net/rest/twauth" ,new AsyncHttpResponseHandler() {       
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String response = new String(responseBody);
					if(!response.equals("OAuth Error")) {							
						
						StringUtil.authURL = response;	
					  	Intent homeIntent = new Intent(getApplicationContext(),TwitterLoginActivity.class);
				        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				        startActivity(homeIntent);  
					}
					else
						Toast.makeText(getApplicationContext(), "Authentication Error", Toast.LENGTH_LONG).show();	            
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					if(statusCode == 404){
						Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
					}  
					else if(statusCode == 500){
						Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
					} 
					else{
						Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_LONG).show();
					}
				}
			});			
		}
		else {
			Intent homeIntent = new Intent(getApplicationContext(),CheckinActivity.class);
	        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(homeIntent);  
		}
	}
}
