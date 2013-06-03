package com.titaniumjellyfish.studybuddy.database;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
//import com.julien.blanchet.cs65.databasetestapp.Globals;

@DatabaseTable
public class RoomLocationEntry implements Parcelable{

	public static Parcelable.Creator<RoomLocationEntry> CREATOR =
			new RoomLocationParcelCreator();

	@DatabaseField(generatedId=true)
	private long id; // phone-specific
	
	@DatabaseField
	private double latitude;
	@DatabaseField
	private double longitude;
	@DatabaseField(uniqueCombo=true)
	private String buildingName;
	@DatabaseField(uniqueCombo=true)
	private String roomName;
	@DatabaseField
	private double productivity;
	@DatabaseField
	private double capacity;
	@DatabaseField
	private double crowd;
	@DatabaseField
	private int n_surveys;
	@DatabaseField
	private double my_productivity;
	@DatabaseField
	private double my_capacity;
	@DatabaseField
	private double my_crowd;
	@DatabaseField
	private int my_n_surveys;
	@DatabaseField
	private boolean is_local;

	//	@ForeignCollectionField
	//	private ForeignCollection<CrowdEntry> crowdEntries;
	@ForeignCollectionField
	private ForeignCollection<NoiseEntry> noiseEntries;
	@ForeignCollectionField
	private ForeignCollection<CommentEntry> commentEntries;


	public RoomLocationEntry(){
		id = -1L;
		latitude = 0.0D;
		longitude = 0.0D;
		buildingName = "";
		roomName = "";
		productivity = Globals.PRODUCTIVITY_AVERAGE;
		capacity = Globals.CAPACITY_SINGLE;
		crowd = Globals.CROWD_LOW;
	}

	public double getProductivity() {
		return (productivity*n_surveys + my_productivity*my_n_surveys) / (n_surveys + my_n_surveys);
	}
	

	public void setProductivity(double productivity) {
		this.productivity = productivity;
	}

	//	public ForeignCollection<CrowdEntry> getCrowdEntries(){
	//		return crowdEntries;
	//	}
	public ForeignCollection<NoiseEntry> getNoiseEntries(){
		return noiseEntries;
	}
	public ForeignCollection<CommentEntry> getCommentEntries(){
		return commentEntries;
	}

	public double getCapacity() {
		return (capacity*n_surveys + my_capacity*my_n_surveys) / (n_surveys + my_n_surveys);
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	
	
	public double getMyCrowd() {
		return (crowd*n_surveys + my_crowd*my_n_surveys) / (n_surveys + my_n_surveys);
	}
	
	public double getCrowd(){
		return crowd;
	}
	public void setCrowd(double crowd) {
		this.crowd = crowd;
	}

	public long getId() {
		return id;
	}
	//	public void setId(long id) {
	//		this.id = id;
	//	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	// Local-ness
	public boolean getLocal(){
		return is_local;
	}

	public void setLocal(boolean local){
		this.is_local = local;
	}
	// Num surveys
	public int getSurveys(){
		return n_surveys + my_n_surveys;
	}
	public void setSurveys(int s){
		this.n_surveys = s;
	}



	// Stuff for the parcelable interface
	@Override
	public int describeContents() {
		// no special flags
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(buildingName);
		dest.writeString(roomName);
		dest.writeDouble(productivity);
		dest.writeDouble(capacity);
		dest.writeDouble(crowd);
		dest.writeByte(is_local ? (byte) 1 : (byte) 0);
	}

	private static class RoomLocationParcelCreator implements Parcelable.Creator<RoomLocationEntry>{

		@Override
		public RoomLocationEntry createFromParcel(Parcel source) {
			RoomLocationEntry mEntry = new RoomLocationEntry();
			mEntry.setLatitude(source.readDouble());
			mEntry.setLongitude(source.readDouble());
			mEntry.setBuildingName(source.readString());
			mEntry.setRoomName(source.readString());
			mEntry.setProductivity(source.readInt());
			mEntry.setCapacity(source.readInt());
			mEntry.setCrowd(source.readInt());
			mEntry.setLocal(source.readByte() == (byte) 1);
			return mEntry;
		}

		@Override
		public RoomLocationEntry[] newArray(int size) {
			return new RoomLocationEntry[size];
		}

	}


	// CONSTANTS COPIED FROM ROOM.JAVA
	private static final String KEY_LATITUDE  	= "room_lat";
	private static final String KEY_LONGITUDE 	= "room_lng";
	private static final String KEY_BUILDING  	= "room_building";
	private static final String KEY_ROOM 		= "room_name";
	private static final String KEY_NOISE  		= "room_noise";
	private static final String KEY_INTERPOLATED = "room_noise_interp";
	private static final String KEY_RATING       = "room_rating";
	private static final String KEY_CAPACITY  	= "room_capacity";
	private static final String KEY_CROWD 		= "room_crowd";
	private static final String KEY_NUM_SURVEYS  = "num_surveys";
	private static final String KEY_COMMENTS		= "room_comments";

	public static class RoomLocationReturn{
		public RoomLocationEntry entry;
		public List<NoiseEntry> noises;
		public List<CommentEntry> comments;
	}
	
	
	public static RoomLocationReturn fromServerJson(JsonParser p) throws JsonParseException, IOException{

		RoomLocationReturn ret = new RoomLocationReturn();
		
		RoomLocationEntry entry = new RoomLocationEntry();

		while(p.nextToken() != JsonToken.END_OBJECT){

			String fieldName = p.getCurrentName();
			if(KEY_BUILDING.equals(fieldName)){
				p.nextToken();
				entry.setBuildingName(p.getText());
			} else if(KEY_ROOM.equals(fieldName)){
				p.nextToken();
				entry.setRoomName(p.getText());
			} else if(KEY_LATITUDE.equals(fieldName)){
				p.nextToken();
				entry.setLatitude(p.getDoubleValue());
			} else if(KEY_LONGITUDE.equals(fieldName)){
				p.nextToken();
				entry.setLongitude(p.getDoubleValue());
			} else if(KEY_RATING.equals(fieldName)){
				p.nextToken();
				// We assume that rating <--> productivity (they are interchangeable)
				entry.setProductivity(p.getDoubleValue());
			} else if(KEY_CAPACITY.equals(fieldName)){
				p.nextToken();
				entry.setCapacity(p.getDoubleValue());
			} else if(KEY_CROWD.equals(fieldName)){
				p.nextToken();
				entry.setCrowd(p.getDoubleValue());
			} else if (KEY_NUM_SURVEYS.equals(fieldName)){
				p.nextToken();
				entry.setSurveys(p.getIntValue());

			} else if(KEY_INTERPOLATED.equals(fieldName)){
				p.nextToken(); // take the "[" token
				List<NoiseEntry> noises = new ArrayList<NoiseEntry>();
				while(p.nextToken() != JsonToken.END_ARRAY){
					noises.add(NoiseEntry.fromServerJson(p, entry));
				}
				ret.noises = noises;
			} else if(KEY_COMMENTS.equals(fieldName)){
				p.nextToken(); // take the "[" token
				List<CommentEntry> comments = new ArrayList<CommentEntry>();
				while(p.nextToken() != JsonToken.END_ARRAY){
					comments.add(CommentEntry.fromServerJson(p, entry));
				}
				ret.comments = comments;
			} 
		}
		entry.setLocal(false);
		
		ret.entry = entry;
		
		return ret;
	}

	public void writeJsonIfLocal(JsonGenerator g) throws JsonGenerationException, IOException{
		if(this.is_local){
			g.writeStartObject();
			g.writeFieldName(KEY_LATITUDE);
			g.writeNumber(this.latitude);
			g.writeFieldName(KEY_LONGITUDE);
			g.writeNumber(this.longitude);
			g.writeFieldName(KEY_BUILDING);
			g.writeString(this.buildingName);
			g.writeFieldName(KEY_ROOM);
			g.writeString(this.roomName);
			g.writeFieldName(KEY_RATING);
			g.writeNumber(this.my_productivity);
			g.writeFieldName(KEY_CAPACITY);
			g.writeNumber(this.my_capacity);
			g.writeFieldName(KEY_CROWD);
			g.writeNumber(this.my_crowd);
			g.writeFieldName(KEY_NUM_SURVEYS);
			g.writeNumber(this.my_n_surveys);
			g.writeFieldName(KEY_COMMENTS);
			g.writeStartArray();
			
			CloseableIterator<CommentEntry> commIterator = this.commentEntries.closeableIterator();
			while(commIterator.hasNext())
				commIterator.next().writeJsonIfLocal(g);
			try {
				commIterator.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			g.writeEndArray();
			g.writeFieldName(KEY_NOISE);
			g.writeStartArray();
			
			CloseableIterator<NoiseEntry> cIterator = this.noiseEntries.closeableIterator();
			while(cIterator.hasNext())
				cIterator.next().writeJsonIfLocal(g);
			try {
				cIterator.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			g.writeEndArray();
			g.writeEndObject();
		}
	}
}
