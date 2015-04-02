package com.india.nirmalbharat;

import com.india.nirmalbharat.util.AppContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class RatingActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
	}
	
	public void gotoComment(View view) {
		
		if(((RadioButton)findViewById(R.id.filth)).isChecked()) {
			AppContext.score="1";
			move();
		}
		else if(((RadioButton)findViewById(R.id.dirt)).isChecked()) {
			AppContext.score="2";
			move();
		}
		else if(((RadioButton)findViewById(R.id.unclean)).isChecked()) {
			AppContext.score="3";
			move();
		}
		else if(((RadioButton)findViewById(R.id.clean)).isChecked()) {
			AppContext.score="4";
			move();
		}
		else if(((RadioButton)findViewById(R.id.beauty)).isChecked()) {				
			AppContext.score="5";
			move();
		}
		else
			Toast.makeText(this, "Please rate the location" , Toast.LENGTH_LONG).show();
	}
	
	private void move() {
		Intent homeIntent = new Intent(getApplicationContext(),CommentingActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent); 
	}
}
