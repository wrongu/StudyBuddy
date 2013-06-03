package com.titaniumjellyfish.studybuddy;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class RoomDef extends Activity implements OnMapClickListener {
	private static final LatLng BAKER_BERRY = new LatLng(43.70546, -72.28884);
	private static final int DEFAULT_MAP_ZOOM = 18;
	
	View mapView;
	GoogleMap mMap;
	Marker newRoom;
	String roomName;
	String features;
	int noise;
	int crowdedness;
	int capacity;
	double lat;
	double lng;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_def);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BAKER_BERRY, DEFAULT_MAP_ZOOM));
		
		mMap.setOnMapClickListener(this);
		
		mapView = findViewById(R.id.room_def_mapview);
		
		AutoCompleteTextView nameText = ((AutoCompleteTextView) findViewById(R.id.room_def_name));
		String[] building_names = getResources().getStringArray(R.array.campus_buildings);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, building_names);
		nameText.setAdapter(adapter);
		
		SeekBar noiseSeekbar = (SeekBar) findViewById(R.id.noiseSeekbar);
		noiseSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekbar, int position, boolean fromUser) {
				noise = position;
				TextView noiseLabel = (TextView) findViewById(R.id.noiseBarLabel);
				String label = getString(R.string.ui_room_def_noise) + " ("	+ (noise+1) + ": ";
				switch (noise) {
					case 0:
						label += "Silent)";
						break;
					case 1:
						label += "White Noise)";
						break;
					case 2:
						label += "Noisy)";
						break;
					case 3:
						label += "Distracting)";
						break;
				}
				noiseLabel.setText(label);
			}
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
		});
		
		SeekBar crowdSeekbar = (SeekBar) findViewById(R.id.crowdSeekbar);
		crowdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekbar, int position, boolean fromUser) {
				crowdedness = position;
				TextView crowdLabel = (TextView) findViewById(R.id.crowdBarLabel);
				String label = getString(R.string.ui_room_def_crowd) + " ("	+ (crowdedness+1) + ": ";
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
	}
	
	/**
	 * Sets marker on map, gets lat/lng of room to add
	 * @param point
	 */
	public void onMapClick(LatLng point) {
		if (newRoom != null)
			newRoom.remove();
		newRoom = mMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
		
		lat = point.latitude;
		lng = point.longitude;
	}
	
	/**
	 * Checks if there is any missing information
	 * @return true if all fields are filled
	 */
	private boolean fieldsFilled() {
		return !(roomName == null || features == null || newRoom == null);
	}
	
	/**
	 * Saves new room data to local db
	 * Will not save if all fields aren't filled
	 * @param view
	 */
	public void onClickSave(View view) {
		roomName = ((AutoCompleteTextView) findViewById(R.id.room_def_name)).getText().toString();
		features = ((EditText) findViewById(R.id.room_def_features)).getText().toString();

		if(fieldsFilled()) {
			//insert to DB
			Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show();
			finish();
		}
		else
			Toast.makeText(this, "Please fill in every entry", Toast.LENGTH_SHORT).show();
	}
	
	public void onClickCancel(View view) {
		Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
  }
}