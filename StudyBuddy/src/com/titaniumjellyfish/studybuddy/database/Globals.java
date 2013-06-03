package com.titaniumjellyfish.studybuddy.database;

public class Globals {
	// RoomLocationTable
	public static final String KEY_RL_ROWID 	 	= "_id";
	public static final String KEY_RL_LATITUDE  	= "lat";
	public static final String KEY_RL_LONGITUDE 	= "lng";
	public static final String KEY_RL_BUILDING  	= "building";
	public static final String KEY_RL_ROOM 		 	= "room";
	public static final String KEY_RL_WIFI 	 		= "wifi";
	public static final String KEY_RL_NOISE     	= "noise_level";
	public static final String KEY_RL_PRODUCTIVITY 	= "productivity";
	public static final String KEY_RL_CAPACITY  	= "capacity";
	public static final String KEY_RL_RATING 		= "rating";
	public static final String KEY_RL_USER 			= "user";
	public static final String KEY_RL_CROWD 		= "crowd";
	public static final String KEY_RL_TIMESTAMP		= "timestamp";

	//AgglomerateTable
	public static final String KEY_AT_ROWID 	 	= "_id";
	public static final String KEY_AT_LATITUDE  	= "lat";
	public static final String KEY_AT_LONGITUDE 	= "lng";
	public static final String KEY_AT_BUILDING  	= "building";
	public static final String KEY_AT_ROOM 		 	= "room";
	public static final String KEY_AT_WIFI 	 		= "wifi";
	public static final String KEY_AT_NOISE     	= "noise_level";
	public static final String KEY_AT_CAPACITY  	= "capacity";
	public static final String KEY_AT_RATING 		= "rating";
	public static final String KEY_AT_CROWD 		= "crowd";
	public static final String KEY_AT_COMMENT		= "comment";
	
	//TREND DATA
	public static final int TREND_FREQUENCY = 24;
	
	//COMMENTS
	public static final int MAX_NUM_COMMENTS = 10;
	
	// NOISE LEVELS
	public static final int NOISE_SIELENT = 0;
	public static final int NOISE_WHITE   = 1;
	public static final int NOISE_LOUD    = 2;
	public static final int NOISE_DISTRACTING = 3;
//	public static final String[] NOISE_LEVELS = {"silent", "white noise", "loud", "distracting"};
	// PRODUCTIVITY LEVELS
	public static final int PRODUCTIVITY_DISTRACTED = 0;
	public static final int PRODUCTIVITY_LOW 		= 1;
	public static final int PRODUCTIVITY_AVERAGE    = 2;
	public static final int PRODUCTIVITY_VERY       = 3;
	public static final int PRODUCTIVITY_MAXIMUM 	= 4;
//	public static final String[] PRODUCTIVITY_LEVELS = {"distracted", "barely", "average", "very", "crushed it"};
	// SIZE/CAPACITY
	public static final int CAPACITY_SINGLE 	= 0;
	public static final int CAPACITY_PARTNER	= 1;
	public static final int CAPACITY_GROUP 		= 2;
//	public static final String[] CAPACITY_LEVELS = {"single", "partner", "group"};
	// CROWD DENSITY
	public static final int CROWD_EMPTY = 0;
	public static final int CROWD_LOW 	= 1;
	public static final int CROWD_HIGH	= 2;
//	public static final String[] CROWD_LEVELS = {"empty", "sparse", "crowded"};
	
	// SERVER GLOBALS COPY
	public static final String SERVER_URL = "http://129.170.210.128:8888";
	public static final String PREFS_SERVER = "sprefs_studybuddy_server";
	public static final String PARAM_UPLOAD_TYPE = "upload_type";
	public static final String UPLOAD_TYPE_NOISE = "ut_noise";
	public static final String UPLOAD_TYPE_ROOM = "ut_room";
	
	public static final String PARAM_JSON_DATA = "json_data";
	public static final String PARAM_DEVICE_ID = "dev_id";
	
	public static final String LOGTAG = "studybuddy";
	
	// LOCATION CLIENT
	public static final int UPDATE_INTERVAL_MILLISECONDS = 20000;
	
	// UI KEYS
	public static final String KEY_FILTER = "key_filter";
	public static final String KEY_LAST_UPDATED = "last_updated";
	
	//SENSOR EXTRA KETS
	public static final String KEY_SENSOR_LATITUDE = "sensor_lat";
	public static final String KEY_SENSOR_LONGITUDE = "sensor_long";
	public static final String KEY_SENSOR_ROOM = "sensor_room";
	public static final String KEY_SENSOR_BUILDING = "sensor_building";
	public static final String KEY_SENSOR_IN_TARGET_ROOM = "inTargetRoom";
	public static final String KEY_SENSOR_COLLECTION_STATE = "storage object";
	
	//Sensor Broadcast
	public static final String ACTION_GEOFENCE_TRANSITION = "moved in or out of room";
	public static final String ACTION_GOOGLE_PLAY_ERROR = "google error";
	
	public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final String APP_TAG = "StudyBuddy";
	public static final String CONNECTION_ERROR_CODE = "google play error";
	
	/*
	public static String parseNoiseLevel(int n){
		if(n >= 0 && n < NOISE_LEVELS.length)
			return NOISE_LEVELS[n];
		else
			return "";
	}
	
	public static String parseProductivityLevel(int p){
		if(p >= 0 && p < PRODUCTIVITY_LEVELS.length)
			return PRODUCTIVITY_LEVELS[p];
		else
			return "";
	}
	
	public static String parseCapacityLevel(int c){
		if(c >= 0 && c < CAPACITY_LEVELS.length)
			return CAPACITY_LEVELS[c];
		else
			return "";
	}
	
	public static String parseCrowd_Level(int c){
		if(c >= 0 && c < CROWD_LEVELS.length)
			return CROWD_LEVELS[c];
		else
			return "";
	}
	*/
}
