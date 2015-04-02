package com.india.nirmalbharat;

import java.io.File;
import java.io.FileInputStream;
import com.india.nirmalbharat.util.AppContext;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onClick(View view) {
		 File f = new File("nbuser");
		 if(f.exists()) {
			 try {
				FileInputStream fis = new FileInputStream(f);
				byte[] data = new byte[16];
				fis.read(data);
				AppContext.username = new String(data);
				fis.close();
				Toast.makeText(getApplicationContext(), "Hi! "+ AppContext.username, Toast.LENGTH_LONG).show();
				Intent homeIntent = new Intent(getApplicationContext(),CheckinActivity.class);
			        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(homeIntent);
			} catch (Exception e) {			
				e.printStackTrace();
			}
		 }
		 else {
			 Intent homeIntent = new Intent(getApplicationContext(),SignupActivity.class);
			 homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(homeIntent);
		 }
	}
}
