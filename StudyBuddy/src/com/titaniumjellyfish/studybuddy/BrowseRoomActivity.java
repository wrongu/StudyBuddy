package com.titaniumjellyfish.studybuddy;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.titaniumjellyfish.studybuddy.database.Globals;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Queries database
 * Displays map with markers of best rooms from query
 * Shows list of best rooms under map in ListView
 * 
 * TODO database query
 * TODO custom ListView entry xml
 * TODO remove button in xml when merged, temp to access DisplayRoomActivity
 * @author Wesley
 *
 */
public class BrowseRoomActivity extends Activity implements OnMarkerClickListener {
	private static final LatLng BAKER_BERRY = new LatLng(43.70546, -72.28884);
	private static final int DEFAULT_MAP_ZOOM = 18;
	
	private final int SPINNER_NOISE = 0;
	private final int SPINNER_CROWD = 1;
	
	GoogleMap mMap;
	RoomEntriesCursorAdapter mCursorAdapter;
	ListView mRoomView;
	Context mContext; 
	
	/**
	 * 
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_room);
		
		mContext = this;
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BAKER_BERRY, DEFAULT_MAP_ZOOM));
		
		int filter = getPreferences(MODE_PRIVATE).getInt(Globals.KEY_FILTER, SPINNER_NOISE);
		
		mCursorAdapter = new RoomEntriesCursorAdapter(this, null);
		mRoomView = (ListView) this.findViewById(R.id.browseListView);		
		
		switch(filter) {
		case SPINNER_NOISE:
			//TODO Get results based on quietest location
			
			break;
		case SPINNER_CROWD:
			//TODO Get results based on least crowded location
			
			break;
		default:
			break;
		}
		
		mRoomView.setAdapter(mCursorAdapter);
		//TODO this will break because CursorAdapter not written yet
		//setRoomMarkers(); 
		
		mRoomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, final View view,
          int position, long id) {
      	mRoomView.getItemAtPosition(position);
        Cursor c = mCursorAdapter.getCursor();
        c.moveToPosition(position);
      	
        /**
        RoomLocationEntry mEntry = new RoomLocationEntry();
        mEntry.setId(c.getLong(c.getColumnIndex(RoomLocationTable.KEY_ROWID)));
				mEntry.setLatitude(c.getDouble(c.getColumnIndex(RoomLocationTable.KEY_LATITUDE)));
				mEntry.setLongitude(c.getDouble(c.getColumnIndex(RoomLocationTable.KEY_LONGITUDE)));
				mEntry.setBuildingName(c.getString(c.getColumnIndex(RoomLocationTable.KEY_BUILDING)));
				mEntry.setRoomName(c.getString(c.getColumnIndex(RoomLocationTable.KEY_ROOM)));
				mEntry.setWifi(c.getInt(c.getColumnIndex(RoomLocationTable.KEY_WIFI)));
				mEntry.setNoise(c.getInt(c.getColumnIndex(RoomLocationTable.KEY_NOISE)));
				mEntry.setProductivity(c.getInt(c.getColumnIndex(RoomLocationTable.KEY_PRODUCTIVITY)));
				mEntry.setCapacity(c.getInt(c.getColumnIndex(RoomLocationTable.KEY_CAPACITY)));
				mEntry.setRating(c.getDouble(c.getColumnIndex(RoomLocationTable.KEY_RATING)));
				mEntry.setUser(c.getString(c.getColumnIndex(RoomLocationTable.KEY_USER)));
				mEntry.setCrowd(c.getInt(c.getColumnIndex(RoomLocationTable.KEY_CROWD)));
				mEntry.setTimestamp(c.getLong(c.getColumnIndex(RoomLocationTable.KEY_TIMESTAMP)));
				
				**/
				//fire intent to start SensorService
				Intent serviceIntent = new Intent(mContext, SensorService.class);
				//serviceIntent.putExtra(mContext.getString(R.string.extras_room_location), mEntry);
				((BrowseRoomActivity) mContext).startService(serviceIntent);
				
				// fire intent to display this entry
				//Intent intent = new Intent(mContext, DisplayRoomLocationEntry.class);
				
				//intent.putExtra(mContext.getString(R.string.extras_room_location), mEntry);
				//startActivity(intent);
      }
		});
	}
	
	/**
	 * Place location markers for listed rooms on map
	 * Room name contained in marker title, rowid contained in snippet
	 */
	private void setRoomMarkers() {
		Cursor c = mCursorAdapter.getCursor();
		
		do {
			double lat = c.getDouble(c.getColumnIndex("KEY_LATITUDE"));
			double lng = c.getDouble(c.getColumnIndex("KEY_LONGITUDE"));
			
			mMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title(c.getString(c.getColumnIndex("KEY_ROOM")))
				.snippet(c.getString(c.getColumnIndex("KEY_ROWID"))));
		} while((c.moveToNext()));
	}
	
	/**
	 * On marker click, smooth scroll to room position on list
	 */
	public boolean onMarkerClick(Marker marker) {
		int position = Integer.parseInt(marker.getSnippet());
		mRoomView.smoothScrollToPosition(position);
		return true;
	}
	
	public void gotoDisplayRoom(View v) {
		
		Intent i = new Intent(this, DisplayRoomActivity.class);
		
    Bundle translateBundle =
        ActivityOptions.makeCustomAnimation(BrowseRoomActivity.this,
        R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
		
		startActivity(i, translateBundle);
	}
	
	/**
	 * Adds slide animation to up button click
	 */
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
	
	/**
	 * Overrides finish to add slide animation
	 */
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
  }
	
	/**
	 * TODO custom listview
	 */
	private class RoomEntriesCursorAdapter extends CursorAdapter {
		
		private LayoutInflater mInflater;

		public RoomEntriesCursorAdapter(Context context, Cursor c) {
			super(context, c, 0);
			mInflater = LayoutInflater.from(context);
		}
		
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return mInflater.inflate(android.R.layout.two_line_list_item, null);
		}
		
	}
}
