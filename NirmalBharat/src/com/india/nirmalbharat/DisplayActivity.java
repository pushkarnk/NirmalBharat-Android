package com.india.nirmalbharat;

import com.india.nirmalbharat.util.AppContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DisplayActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		ImageView mImageView;
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(BitmapFactory.decodeFile(AppContext.fileURL));
		Toast.makeText(this, AppContext.latitude + "," + AppContext.longitude, Toast.LENGTH_LONG).show();
	}
	
	public void retakePic(View view) {
		Intent homeIntent = new Intent(getApplicationContext(),CameraActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent); 
	}
	
	public void gotoRate(View view) {
		Intent homeIntent = new Intent(getApplicationContext(),RatingActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

}
