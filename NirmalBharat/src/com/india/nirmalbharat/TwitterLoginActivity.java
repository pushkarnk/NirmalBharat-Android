package com.india.nirmalbharat;

import org.apache.http.Header;
import com.india.nirmalbharat.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterLoginActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitterlogin);
		String html = "<a href=" + StringUtil.authURL +  ">Authentication URL</a>";
		TextView tv = (TextView)findViewById(R.id.twitterAuthURL);
		tv.setText(Html.fromHtml(html));
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}	
	
	public void submitPIN(View view) {
		RequestParams postParams = new RequestParams();
		postParams.add("pin", ((EditText)findViewById(R.id.twitterPIN)).getText().toString());
		postParams.add("username", StringUtil.currentUser);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");
		client.post("http://nirmalbharat.mybluemix.net/rest/twauth", postParams, new AsyncHttpResponseHandler(){

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
					Intent homeIntent = new Intent(getApplicationContext(),TestTwitterConnection.class);
			        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(homeIntent); 
				} else {
					Intent homeIntent = new Intent(getApplicationContext(),TwitterReLoginActivity.class);
			        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(homeIntent);  					
				}
			}
			
		});
	}
}