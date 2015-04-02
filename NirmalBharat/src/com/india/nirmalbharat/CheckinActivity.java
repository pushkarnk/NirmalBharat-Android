package com.india.nirmalbharat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CheckinActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
	}	
	
	public void onClick(View view) {
		Intent homeIntent = new Intent(getApplicationContext(),CameraActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent); 
	}

}
