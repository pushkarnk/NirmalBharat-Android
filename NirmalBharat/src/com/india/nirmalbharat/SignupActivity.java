package com.india.nirmalbharat;


import com.loopj.android.http.AsyncHttpResponseHandler;
import com.india.nirmalbharat.util.AppContext;
import com.india.nirmalbharat.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SignupActivity extends Activity{
	
	ProgressDialog progressDialog;
	TextView errorMessage;
	String usernamefh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		progressDialog = new ProgressDialog(this);
	    progressDialog.setMessage("Please wait...");
	    progressDialog.setCancelable(false);	   
	}
	
	public void skipSignup(View view) {
    	Intent homeIntent = new Intent(getApplicationContext(),CheckinActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
	}
	
	public void signUpNewUser(View view) {
		EditText usernameEdit = (EditText)findViewById(R.id.signupEditUsername);
		String username = usernameEdit.getText().toString();
		usernamefh = username;
		EditText emailEdit = (EditText)findViewById(R.id.signupEditEmail);
		String email = emailEdit.getText().toString();
		if(StringUtil.isNotNull(email) && StringUtil.isNotNull(email)){	         
            if(StringUtil.validate(email)){
        		registerUser(username, email);
            } 
            else{
                Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
		
	}

	private void registerUser(String username, String email) {
		RequestParams request = new RequestParams();
		request.add("username", username);
		request.add("emailid", email);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-type", "application/x-www-form-urlencoded");
		client.post("http://nirmalbharat.mybluemix.net/rest/useradd",request ,new AsyncHttpResponseHandler() {       
            @Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            	String response = new String(responseBody);
                progressDialog.hide();                       
                if(response.equals("DONE")){                	
                	AppContext.username = usernamefh;
                	Intent homeIntent = new Intent(getApplicationContext(),RegSuccessActivity.class);
        	        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	        startActivity(homeIntent);                	
                } 
                else{
                	Toast.makeText(getApplicationContext(), "Username exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog 
                progressDialog.hide();
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }  
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                } 
                else{
                    Toast.makeText(getApplicationContext(), "statusCode="+statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
	}
	
}
