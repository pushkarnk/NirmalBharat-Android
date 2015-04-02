package com.india.nirmalbharat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.india.nirmalbharat.util.AppContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class CommentingActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commenting);		
	}

	private boolean validate() {
		EditText text = (EditText)findViewById(R.id.CommentText);
		if(text.getText().toString().equals("")) {
			Toast.makeText(this, "Please enter a comment" , Toast.LENGTH_LONG).show();
			return false;
		}		
		if(text.getText().toString().length() > 140) {
			Toast.makeText(this, "Kindly restrict to 140 characters" , Toast.LENGTH_LONG).show();
			return false;
		}		
		return true;
	}

	public void hideKeyBoard(View view) {
		EditText text = (EditText)findViewById(R.id.CommentText);
		if(text.isFocused()) {
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
		}
	}
	
	public void submit(View view) {
		if(!validate())
			return;
		if(AppContext.username==null)
			AppContext.username = "Anonymous";
		EditText text = (EditText)findViewById(R.id.CommentText);
		AppContext.comment = text.getText().toString();
		Switch twitterSwitch = (Switch)findViewById(R.id.tweet);
		AppContext.tweet = twitterSwitch.isChecked() && !AppContext.username.equals("Anonymous")? "Yes":"No";						
		MultipartEntity requestParams = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			buildParams(requestParams);			
			new MultipartSender().execute(requestParams);		
		} catch (Exception e ) {
			Toast.makeText(this, e.toString() , Toast.LENGTH_LONG).show();
		}
		Intent homeIntent = new Intent(getApplicationContext(),WheelingActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent); 
	}
	
	private void buildParams(MultipartEntity requestParams) {
		
		try {
			requestParams.addPart("username", new StringBody(AppContext.username));
			requestParams.addPart("comment", new StringBody(AppContext.comment));
			requestParams.addPart("size", new StringBody(Long.toString(new File(AppContext.fileURL).length())));			
			requestParams.addPart("score", new StringBody(AppContext.score));
			requestParams.addPart("latitude", new StringBody(AppContext.latitude));
			requestParams.addPart("longitude", new StringBody(AppContext.longitude));
			requestParams.addPart("tweet", new StringBody(AppContext.tweet));
			requestParams.addPart("file", new FileBody(new File(AppContext.fileURL)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}						
	}
	
	private void logRequest(MultipartEntity params) {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/NirmalBharat");
		dir.mkdirs();								
		String fileName = String.format("request.txt");				
		File outFile = new File(dir, fileName);
		try {		
			FileOutputStream outStream = new FileOutputStream(outFile);
			StringBuffer sb = new StringBuffer();
			sb.append("\nusername=" + AppContext.username);
			sb.append("\nfile=" + AppContext.fileURL);
			sb.append("\nsize=" + new File(AppContext.fileURL).length());
			sb.append("\nscore="+AppContext.score);
			sb.append("\nlatitude="+AppContext.latitude);
			sb.append("\nlongitude="+AppContext.longitude);
			sb.append("\ntweet="+AppContext.tweet);
			sb.append("\ncomment="+AppContext.comment);
			byte [] requestBytes = sb.toString().getBytes();
			outStream.write(requestBytes);
			outStream.flush();
			outStream.close();	
			Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(Uri.fromFile(outFile));
			sendBroadcast(mediaScanIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	class MultipartSender extends AsyncTask<MultipartEntity,Void,String> {
		StringBuilder s = new StringBuilder();		
		@Override
		protected String doInBackground(MultipartEntity... params) {
			logRequest(params[0]);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://nirmalbharat.mybluemix.net/rest/mcleaninfo");
			httpPost.setEntity(params[0]);
			HttpResponse response;
			try {
				response = httpClient.execute(httpPost);
				BufferedReader reader;
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));			
				String sResponse;				
				while ((sResponse = reader.readLine()) != null)
					s = s.append(sResponse);
			} catch (Exception e) {	
					s.append(e.getStackTrace()[0].getMethodName()+e.getStackTrace()[0].getLineNumber());					
					e.printStackTrace();
					return "null";
			}
			
			return "";
		}
		
		@Override
        protected void onPostExecute(String result) {
			if (s.toString().equals("PASS")) {
		    	Toast.makeText(CommentingActivity.this, "Uplod done" , Toast.LENGTH_LONG).show();
		    	moveToThankYou();
		    }		    	
		    else if(s.toString().equals("FAIL")){
		    	Toast.makeText(CommentingActivity.this, "Upload failed. Try again.", Toast.LENGTH_LONG).show();
		    	moveToDisplay();
		    }
		    else {
		    	Toast.makeText(CommentingActivity.this, s.toString(), Toast.LENGTH_LONG).show();
		    }
        }
		
		private void moveToThankYou() {
			Intent homeIntent = new Intent(getApplicationContext(),ThankingActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
		}
		
		private void moveToDisplay() {
			Intent homeIntent = new Intent(getApplicationContext(),DisplayActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
		}
		
	}
		
}
