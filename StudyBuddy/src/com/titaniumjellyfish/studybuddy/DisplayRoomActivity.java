package com.titaniumjellyfish.studybuddy;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.titaniumjellyfish.studybuddy.SensorCollectionState.Current;
import com.titaniumjellyfish.studybuddy.SensorService.SensorBinder;
import com.titaniumjellyfish.studybuddy.database.Globals;

/**
 * Displays data for a single room entry
 * TODO start noise data collection service
 * TODO noise/crowd forecast values are hardcoded until database accessible
 * @author Wesley
 *
 */
public class DisplayRoomActivity extends Activity {
	private static final LatLng BAKER_BERRY = new LatLng(43.70546, -72.28884);
	private static final int MINI_MAP_ZOOM = 17;
	
	public boolean mIsBound;
	public SensorService mSensorService;
	private IntentFilter mServiceReceiverFilter;
	private SensorCollectionState sensorCollectionState;
	
	GoogleMap mMap;
	TextView noiseLevel;
	ImageView noiseJelly;
	TextView crowdLevel;
	ImageView crowdJelly;
	RatingBar roomRating;
	Context mContext;
	
	private BroadcastReceiver mTargetRoomReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getAction()!= null && intent.getAction() == Globals.ACTION_GEOFENCE_TRANSITION && 
					sensorCollectionState.getState() == Current.JELLY) {
				//show ui for data collelction
			}
			
			if (intent.getAction()!= null && intent.getAction() == Intent.ACTION_USER_PRESENT && 
					!sensorCollectionState.getTakenSurvey() && sensorCollectionState.getEnteredJelly()) {

				//---pew pew fire intent to start new Survey
				Intent surveyIntent = new Intent();
			}
			
			if (intent.getAction() != null && intent.getAction() == Globals.ACTION_GOOGLE_PLAY_ERROR) {
				Bundle extras = intent.getExtras();
				int errorCode = extras.getInt(Globals.CONNECTION_ERROR_CODE);
				showErrorDialog(errorCode);
			}
			
		}		
	};
	
	
	/**
	 * TODO start foreground sensor service here
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(Globals.LOGTAG, "Display onCreate");
		setContentView(R.layout.activity_display_room);
		
		mContext = this;
		
		Button back = (Button) findViewById(R.id.display_room_back_button);
		back.getBackground().setColorFilter(Color.parseColor("#33B5E5"), PorterDuff.Mode.MULTIPLY);
		
		//Get map and disable interactions
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setAllGesturesEnabled(false);		
		
		//Possibility: Live updating noise bar when inside room
		noiseLevel = (TextView) findViewById(R.id.displayRoomNoiseLabel);
		crowdLevel = (TextView) findViewById(R.id.displayRoomCrowdLabel);
		roomRating = (RatingBar) findViewById(R.id.displayRoomRatingbar);

	  noiseJelly = (ImageView) findViewById(R.id.display_room_noisejelly);
	  crowdJelly = (ImageView) findViewById(R.id.display_room_crowdjelly);

		
		loadContent(getIntent().getExtras());
		
		//Register receiver
		mServiceReceiverFilter = new IntentFilter();
		mServiceReceiverFilter.addAction(Globals.ACTION_GEOFENCE_TRANSITION);
		mServiceReceiverFilter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(mTargetRoomReceiver, mServiceReceiverFilter);
		
		//Start service
		doBindService();
		Intent mIntent = new Intent(this, SensorService.class);
		Bundle servExtras = new Bundle();
//		servExtras.putDouble(Globals.KEY_SENSOR_LATITUDE, latitude);
//		servExtras.putDouble(Globals.KEY_SENSOR_LONGITUDE, longitude);
//		servExtras.putString(Globals.KEY_SENSOR_BUILDING, buildingName);
//		serviceExtras.putString(Globals.KEY_SENSOR_ROOM, roomName);
		//----- put address of object into extras serviceExtras.putExtra(Globals.KEY_SENSOR_COLLECTION_STATE, sensorCollectionState);
		
		//Put the extras into the bundle for the service to know the target
		//mIntent.putExtras(servExtras);
		checkGoogleConnection();
		this.startService(mIntent);
		
	}
	
	@Override
	protected void onResume() {
		doBindService();
		registerReceiver(mTargetRoomReceiver, mServiceReceiverFilter);
		super.onResume();
	}
	
	@Override 
	protected void onPause() {
		unregisterReceiver(mTargetRoomReceiver);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(mSensorService != null) {
			mSensorService.stopForeground(true);
			doUnbindService();
			stopService(new Intent(this, SensorService.class));
		}
		super.onDestroy();
	}
	
	/**
	 * Gets the room data and sets views
	 * TODO actual data getting
	 * 
	 * @param data (should probably be RoomLocationEntry when merged)
	 */
	private void loadContent(Bundle data) {
		//Set Page Title
		this.setTitle("Baker-Berry");
		
		//Disable map interaction
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BAKER_BERRY, MINI_MAP_ZOOM));
		
		mMap.addMarker(new MarkerOptions()
		.position(BAKER_BERRY)
		.title("Baker Berry")
		.snippet("1"));
		
		noiseLevel.setText("NOISY");
	  noiseJelly.setImageResource(R.drawable.jelly_red);
		//noiseLevel.setBackgroundColor(Color.RED);
		
		crowdLevel.setText("CROWDED");
	  crowdJelly.setImageResource(R.drawable.jelly_orange);
		//crowdLevel.setBackgroundColor(Color.ORANGE);
		
		getForecast();
		
		roomRating.setEnabled(false);
		roomRating.setRating((float)4.5);
	}
	
	/**
	 * Fills the noise and crowd forecast bars
	 * TODO Get values from database
	 */
	private void getForecast() {
		String[] hours = new String[6];
		int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for(int i = 0; i < hours.length; i++) {
			if(now + i > 12) 
				hours[i] = Integer.toString((now + i) % 12) + "PM";
			else
				hours[i] = Integer.toString(now + i) + "AM";
		}
		
		((TextView) findViewById(R.id.displayRoomNoiseTime1)).setText(hours[0]);
		((TextView) findViewById(R.id.displayRoomNoiseTime2)).setText(hours[1]);
		((TextView) findViewById(R.id.displayRoomNoiseTime3)).setText(hours[2]);
		((TextView) findViewById(R.id.displayRoomNoiseTime4)).setText(hours[3]);
		((TextView) findViewById(R.id.displayRoomNoiseTime5)).setText(hours[4]);
		((TextView) findViewById(R.id.displayRoomNoiseTime6)).setText(hours[5]);
		
		((TextView) findViewById(R.id.displayRoomCrowdTime1)).setText(hours[0]);
		((TextView) findViewById(R.id.displayRoomCrowdTime2)).setText(hours[1]);
		((TextView) findViewById(R.id.displayRoomCrowdTime3)).setText(hours[2]);
		((TextView) findViewById(R.id.displayRoomCrowdTime4)).setText(hours[3]);
		((TextView) findViewById(R.id.displayRoomCrowdTime5)).setText(hours[4]);
		((TextView) findViewById(R.id.displayRoomCrowdTime6)).setText(hours[5]);
	}
	
	/**
	 * onClick for button to go back to BrowseRoomActivity
	 * @param v
	 */
	public void onBackClicked(View v) {
    Bundle translateBundle =
        ActivityOptions.makeCustomAnimation(DisplayRoomActivity.this,
        		R.anim.slide_in_right, R.anim.slide_out_right).toBundle();
    
		Intent i = new Intent(this, BrowseRoomActivity.class);
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
  
  private ServiceConnection connection = new ServiceConnection() {
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mSensorService = ((SensorBinder) service).getService();
		sensorCollectionState = mSensorService.getCollectionStateObject();
		
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		mSensorService = null;
		
	}
	
};

  private void doBindService() {
	  if( !mIsBound) {
		  bindService(new Intent(this, SensorService.class), connection, Context.BIND_AUTO_CREATE);
		  mIsBound = true;
	  }
  }
  
  private void doUnbindService() {
	  if(mIsBound) {
		  unbindService(connection);
		  mIsBound = false;
	  }
  }
  
  //============== Methods to error check GooglePlayServices======
  
  private void resolveConnectionError(ConnectionResult connectionResult) {
	     if (connectionResult.hasResolution()) {
	            try {

	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        this,
	                        Globals.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	                /*
	                * Thrown if Google Play services canceled the original
	                * PendingIntent
	                */

	            } catch (IntentSender.SendIntentException e) {

	                // Log the error
	                e.printStackTrace();
	            }
	        } else {

	            // If no resolution is available, display a dialog to the user with the error.
	            showErrorDialog(connectionResult.getErrorCode());
	        }
  }
  
  private boolean checkGoogleConnection() {
	  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	  
	  if (resultCode == ConnectionResult.SUCCESS) {
		  return true;
	  }
	  else {
		  showErrorDialog(resultCode);
		  return false;
	  }
  }
  
  private void showErrorDialog(int errorCode) {

      // Get the error dialog from Google Play services
      Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
          errorCode,
          this,
          Globals.CONNECTION_FAILURE_RESOLUTION_REQUEST);

      // If Google Play services can provide an error dialog
      if (errorDialog != null) {

          // Create a new DialogFragment in which to show the error dialog
          ErrorDialogFragment errorFragment = new ErrorDialogFragment();

          // Set the dialog in the DialogFragment
          errorFragment.setDialog(errorDialog);

          // Show the error dialog in the DialogFragment
          errorFragment.show(getFragmentManager(), Globals.APP_TAG);
      }
  }

  /**
   * Define a DialogFragment to display the error dialog generated in
   * showErrorDialog.
   */
  public static class ErrorDialogFragment extends DialogFragment {

      // Global field to contain the error dialog
      private Dialog mDialog;

      /**
       * Default constructor. Sets the dialog field to null
       */
      public ErrorDialogFragment() {
          super();
          mDialog = null;
      }

      /**
       * Set the dialog to display
       *
       * @param dialog An error dialog
       */
      public void setDialog(Dialog dialog) {
          mDialog = dialog;
      }

      /*
       * This method must return a Dialog to the DialogFragment.
       */
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
          return mDialog;
      }
  }
}
