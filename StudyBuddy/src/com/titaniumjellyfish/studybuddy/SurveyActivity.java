package com.titaniumjellyfish.studybuddy;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SurveyActivity extends Activity {
	int crowdedness = 0;
	int productivity = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		
		SeekBar crowdSeekbar = (SeekBar) findViewById(R.id.crowdSeekbar);
		crowdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekbar, int position, boolean fromUser) {
				crowdedness = position;
				TextView crowdLabel = (TextView) findViewById(R.id.crowdBarLabel);
				String label = getString(R.string.ui_survey_crowd_label) + " ("	+ (crowdedness+1) + ": ";
				switch (crowdedness) {
					case 0:
						label += "Empty)";
						break;
					case 1:
						label += "Signs of life)";
						break;
					case 2:
						label += "Crowded)";
						break;
					case 3:
						label += "Full)";
						break;
				}
				crowdLabel.setText(label);
			}
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
		});
		
		SeekBar productivitySeekbar = (SeekBar) findViewById(R.id.productivitySeekbar);
		productivitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekbar, int position, boolean fromUser) {
				productivity = position;
				TextView crowdLabel = (TextView) findViewById(R.id.productivityBarLabel);
				String label = getString(R.string.ui_survey_productivity_label) + " ("	+ (productivity+1) + ": ";
				switch (productivity) {
					case 0:
						label += "Distracted)";
						break;
					case 1:
						label += "Barely)";
						break;
					case 2:
						label += "Average)";
						break;
					case 3:
						label += "Very)";
						break;
					case 4:
						label += "Crushed it)";
						break;
				}
				crowdLabel.setText(label);
			}
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
		});
	}
	
	public void onClickOk(View view) {	
		
		writeToDB();
		Toast.makeText(this, crowdedness +":"+productivity, Toast.LENGTH_SHORT).show();
    Bundle translateBundle =
        ActivityOptions.makeCustomAnimation(SurveyActivity.this,
        R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
		
		Intent i = new Intent(this, MainActivityGo.class);
		startActivity(i, translateBundle);
	}
	
	
	/**
	 * TODO
	 */
	private void writeToDB() {
		
	}

	public void onStartTrackingTouch(SeekBar arg0) {}
	public void onStopTrackingTouch(SeekBar arg0) {}
}
