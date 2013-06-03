package com.titaniumjellyfish.studybuddy.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable
public class CrowdEntry implements Parcelable {
	public static Parcelable.Creator<CrowdEntry> CREATOR = new SensorParcelCreator();
	
	@DatabaseField(generatedId=true)
	private long id;
	@DatabaseField
	private int crowd;
	@DatabaseField
	private long timestamp;
	@DatabaseField(foreign=true, foreignAutoRefresh = true,index=true)
	private RoomLocationEntry parent;
	
	public CrowdEntry() {
		id = -1L;
		crowd = -1;
		timestamp = System.currentTimeMillis();
	}
	
	//Setters and getters
	
	//ID
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setParent(RoomLocationEntry parent){
		this.parent = parent;
	}
	public RoomLocationEntry getParent(){
		return this.parent;
	}
	
	//Crowdedness 
	public int getCrowdedness() {
		return crowd;
	}
	
	public void setCrowdedness(int crowd) {
		this.crowd = crowd;	
	}
	//Time
	public long getTime() {
		return timestamp;
	}
	
	public void setTime(long time) {
		this.timestamp = time;
	}

	
	private static class SensorParcelCreator implements Parcelable.Creator<CrowdEntry> {

		@Override
		public CrowdEntry createFromParcel(Parcel source) {
			CrowdEntry entry = new CrowdEntry();
			entry.setId(source.readLong());
			entry.setCrowdedness(source.readInt());
			entry.setTime(source.readLong());
			return entry;
		}

		@Override
		public CrowdEntry[] newArray(int size) {
			return new CrowdEntry[size];
		}
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(crowd);
		dest.writeLong(timestamp);
	}
	
	
}