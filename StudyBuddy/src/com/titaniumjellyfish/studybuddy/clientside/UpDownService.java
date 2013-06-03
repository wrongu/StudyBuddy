package com.titaniumjellyfish.studybuddy.clientside;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.titaniumjellyfish.studybuddy.clientside.HttpHelper.HttpDataWriter;
import com.titaniumjellyfish.studybuddy.clientside.HttpHelper.HttpResponseHandler;
import com.titaniumjellyfish.studybuddy.database.CommentEntry;
import com.titaniumjellyfish.studybuddy.database.Globals;
import com.titaniumjellyfish.studybuddy.database.NoiseEntry;
import com.titaniumjellyfish.studybuddy.database.RoomLocationEntry;
import com.titaniumjellyfish.studybuddy.database.RoomLocationEntry.RoomLocationReturn;
import com.titaniumjellyfish.studybuddy.database.TitaniumDb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class UpDownService extends IntentService {
	
	public UpDownService(){
		this(UpDownService.class.getName());
	}
	
	public UpDownService(String name) {
		super(name);
	}

	TitaniumDb db;

	private static final JsonFactory jFactory = new JsonFactory();
	private long id = 0L;
	private boolean id_set = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null; // new ServiceBinder();
	}

	TitaniumDb getHelper(){
		if (db == null){
			db = OpenHelperManager.getHelper(this, TitaniumDb.class);
		}
		return db;
	}

	@Override
	public void onDestroy(){
		OpenHelperManager.releaseHelper();
		super.onDestroy();
	}

	public void syncDatastore(){
		if(!id_set){
			SharedPreferences prefs = getSharedPreferences(Globals.PREFS_SERVER, Context.MODE_PRIVATE);
			id = prefs.getLong(Globals.PARAM_DEVICE_ID, -1L);
			id_set = true;
		}
		uploadToDatastore();
		downloadFromDatastore();
	}

	private void uploadToDatastore(){
		try {
			postRoomData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downloadFromDatastore(){
		try {
			// handle large json response (in second parameter?)
			HttpResponseHandler handler = new HttpResponseHandler() {
				@Override
				public void handle(InputStream inStream) {

					try {
						// parse json response stream
						JsonParser p = jFactory.createParser(inStream);
						Dao<RoomLocationEntry, Long> roomDao = getHelper().dao(RoomLocationEntry.class, Long.class);
						Dao<CommentEntry, Long> comDao = getHelper().dao(CommentEntry.class, Long.class);
						Dao<NoiseEntry, Long> noiseDao = getHelper().dao(NoiseEntry.class, Long.class);

						while(p.nextToken() != JsonToken.END_OBJECT){
							String fieldName = p.getCurrentName();
							if(Globals.PARAM_DEVICE_ID.equals(fieldName)){
								p.nextToken();
								long new_id = p.getLongValue();
								if(new_id != id){
									id = new_id;
									SharedPreferences prefs = getSharedPreferences(Globals.PREFS_SERVER, Context.MODE_PRIVATE);
									SharedPreferences.Editor editor = prefs.edit();
									editor.putLong(Globals.PARAM_DEVICE_ID, new_id);
									editor.commit();
								}
							} else if(Globals.PARAM_JSON_DATA.equals(fieldName)){
								p.nextToken(); // take the ']'
								while(p.nextToken() != JsonToken.END_ARRAY){
									RoomLocationReturn entry = RoomLocationEntry.fromServerJson(p);


									//									comDao.deleteBuilder()


									try {
										roomDao.createOrUpdate(entry.entry);

										comDao.delete(entry.entry.getCommentEntries());
										noiseDao.delete(entry.entry.getNoiseEntries());

									} catch (SQLException e) {
										Log.e(Globals.LOGTAG, "Couldn't save downloded entry to database!", e);
										e.printStackTrace();
									}
								}
							}
						}

					} catch (JsonParseException e) {
						Log.e(Globals.LOGTAG, "download error parsing json:\n" + e.getMessage());
					} catch (IOException e) {
						Log.e(Globals.LOGTAG, "download error with connection:\n" + e.getMessage());
					} catch (SQLException e){
						Log.e(Globals.LOGTAG, "Couldn't open DAO", e);
					}
				}
			};

			// write data: device id in params
			HttpDataWriter writer = new HttpDataWriter() {
				@Override
				public void write(OutputStream outStream) {
					try {
						String param_dev_id = Globals.PARAM_DEVICE_ID + "=" + String.valueOf(id); 
						outStream.write(param_dev_id.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};

			HttpHelper.postWithHandlers(Globals.SERVER_URL + "/upload", writer, handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void postRoomData() throws IOException {

		// do nothing with response
		HttpResponseHandler handler = new HttpResponseHandler() {
			@Override
			public void handle(InputStream inStream) { }
		};
		// write data: json stream writing
		HttpDataWriter writer = new HttpDataWriter() {
			@Override
			public void write(OutputStream outStream) {
				String param_type = Globals.PARAM_UPLOAD_TYPE + "=" + Globals.UPLOAD_TYPE_ROOM;
				try {
					outStream.write(param_type.getBytes());

					String param_data = "&" + Globals.PARAM_JSON_DATA + "=";
					outStream.write(param_data.getBytes());

					JsonGenerator g = jFactory.createGenerator(outStream);
					writeAllLocalDataJson(g);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		HttpHelper.postWithHandlers(Globals.SERVER_URL + "/upload", writer, handler);
	}

	private void writeAllLocalDataJson(JsonGenerator g) {
		try {
			Dao<RoomLocationEntry, Long> roomDao = getHelper().dao(RoomLocationEntry.class, Long.class);
			List<RoomLocationEntry> all_rooms = roomDao.queryForAll();
			g.writeStartArray();
			for(RoomLocationEntry entry : all_rooms) entry.writeJsonIfLocal(g);
			g.writeEndArray();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(Globals.LOGTAG, "UPDOWN: beginning server sync");
		syncDatastore();
		Log.d(Globals.LOGTAG, "UPDOWN: finished server sync");
	}
}
