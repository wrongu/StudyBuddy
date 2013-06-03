package com.titaniumjellyfish.studybuddy;

import java.util.Calendar;
import java.util.Date;
import com.titaniumjellyfish.studybuddy.clientside.UpDownService;
import com.titaniumjellyfish.studybuddy.database.Globals;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

/**
 * Start screen
 * Spinner to select search parameter, button goes to BrowseRoomActivity
 * 
 * @author Wesley
 *
 */
public class MainActivityGo extends Activity {
	private Date lastUpdated;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_go);
		
		//Set custom button color
		
		long dateMillis = this.getPreferences(MODE_PRIVATE).getLong(Globals.KEY_LAST_UPDATED, 0);
		lastUpdated = new Date(dateMillis);
		
		//Update local database if necessary
		if(shouldUpdate()) 
			updateAgglomerate();
		
		//Start the service to sync with the server
//		startService(new Intent(this, UpDownService.class));
	}

	/**
	 * 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
	            finish();
	            return true;
	        case R.id.settings_room_def:
	        	Intent intent = new Intent(this, RoomDef.class);
	        	
	      		
	          Bundle translateBundle =
	              ActivityOptions.makeCustomAnimation(MainActivityGo.this,
	              R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
	          
	        	startActivity(intent, translateBundle);
	        	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	/**
	 * Launches room BrowseRoomActivity with filter results
	 * @param v
	 */
	public void onGoClicked(View v) {
		Spinner filterSpinner = (Spinner) findViewById(R.id.spinnerResultsFilter);
		int filter = filterSpinner.getSelectedItemPosition();

		SharedPreferences.Editor dataEditor = getPreferences(MODE_PRIVATE).edit();
		dataEditor.putInt(Globals.KEY_FILTER, filter);
		dataEditor.commit();
		
    Bundle translateBundle =
        ActivityOptions.makeCustomAnimation(MainActivityGo.this,
        R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
    
		Intent searchLaunch = new Intent(this, BrowseRoomActivity.class);
		startActivity(searchLaunch, translateBundle);
	}
	
	public void onSurveyClicked(View v) {
    Bundle translateBundle =
        ActivityOptions.makeCustomAnimation(MainActivityGo.this,
        R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
    
		Intent surveyLaunch = new Intent(this, SurveyActivity.class);
		startActivity(surveyLaunch, translateBundle);
	}
	
	/**
	 * Check if agglomerate data needs to be updated
	 * Needs update every 24 hours
	 * Note: this is clunky, if anyone has a better way, improve pls
	 * @return true if updated needed
	 */
	private boolean shouldUpdate() {
		if (lastUpdated == null) 
			return true;
		else {
			Calendar cal = Calendar.getInstance();
			Date today = new Date(System.currentTimeMillis());
			cal.setTime(lastUpdated);
			cal.add(Calendar.DATE, 1);
			
			return cal.getTime().before(today);
		}
	}
	
	/**
	 * Updates agglomerate data from central server
	 * Start server sync service
	 * TODO
	 */
	private void updateAgglomerate() {
		lastUpdated = Calendar.getInstance().getTime();
		
		SharedPreferences.Editor dataEditor = getPreferences(MODE_PRIVATE).edit();
		dataEditor.putLong(Globals.KEY_LAST_UPDATED, System.currentTimeMillis());
		dataEditor.commit();
		
		//startService(new Intent(this, UpDownService.class));
	}
	
	public void doSync(View v){
		startService(new Intent(this, UpDownService.class));
	}
}
