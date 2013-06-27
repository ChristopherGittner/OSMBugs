package org.gittner.osmbugs.bugs;

import java.util.ArrayList;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.statics.Drawings;
import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class MapdustBug extends Bug {
	
	private long id_;
	private int type_;
	
	public static int WRONGTURN = 1;
	public static int BADROUTING = 2;
	public static int ONEWAYROAD = 3;
	public static int BLOCKEDSTREET = 4;
	public static int MISSINGSTREET = 5;
	public static int ROUNDABOUTISSUE = 6;
	public static int MISSINGSPEEDINFO = 7;
	public static int OTHER = 8;
	
	public MapdustBug(double lat, double lon, String title, String text, ArrayList<Comment> comments, int type, long id, Bug.STATE state) {
		super(title, text, comments, new GeoPoint(lat, lon));
		
		setType(type);
		setId(id);
		setState(state);
	}

	protected MapdustBug(Parcel parcel) {
		super(parcel);
		
		id_ = parcel.readLong();
		type_ = parcel.readInt();
	}
	
	/* Get the Bugs Id */
	public long getId() {
		return id_;
	}
	
	/* Set the Bugs Id */
	public void setId(long id) {
		id_ = id;
	}
	
	/* Get the Bugs Type */
	public int getType() {
		return type_;
	}
	
	/* Set the Bugs Type */
	public void setType(int type) {
		type_ = type;
	}
	
	@Override
	public Drawable getMarker(int bitset) {
		if(getState() == Bug.STATE.CLOSED)
			return Drawings.MapdustClosed;
		else if(getState() == Bug.STATE.IGNORED)
			return Drawings.MapdustIgnored;
		else {
			switch(getType()) {
			case 1:
				return Drawings.MapdustWrongTurn;
			case 2:
				return Drawings.MapdustBadRouting;
			case 3:
				return Drawings.MapdustOnewayRoad;
			case 4:
				return Drawings.MapdustBlockedStreet;
			case 5:
				return Drawings.MapdustMissingStreet;
			case 6:
				return Drawings.MapdustRoundaboutIssue;
			case 7:
				return Drawings.MapdustMissingSpeedInfo;
			case 8:
				return Drawings.MapdustOther;
			}
		}
		
		return Drawings.KeeprightDrawable100;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public boolean commit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCommentable() {
		//TODO: Temporary
		return false;
	}

	@Override
	public boolean isIgnorable() {
		return false;
	}
	
	@Override
	public boolean isReopenable() {
		return false;
	}

	@Override
	public boolean addNew() {
		return false;
	}

	/* Parcelable interface */
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		
		parcel.writeLong(id_);
		parcel.writeInt(type_);
	}
	
    public static final Creator<MapdustBug> CREATOR = new Parcelable.Creator<MapdustBug>() {

		@Override
		public MapdustBug createFromParcel(Parcel source) {
			return new MapdustBug(source);
		}

		@Override
		public MapdustBug[] newArray(int size) {
			return new MapdustBug[size];
		}    	
    };

	@Override
	public ArrayList<String> getStateNames(Context context) {
		ArrayList<String> states = new ArrayList<String>();
		states.add(context.getString(R.string.open));
		states.add(context.getString(R.string.closed));
		states.add(context.getString(R.string.ignored));
		
		return states;
	}
}
