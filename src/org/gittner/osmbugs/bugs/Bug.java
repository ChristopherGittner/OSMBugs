package org.gittner.osmbugs.bugs;

import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class Bug extends OverlayItem implements Parcelable{
	
	public enum STATE{
		OPEN,
		CLOSED,
		IGNORED
	}
	
	protected STATE state_;
	protected ArrayList<Comment> comments_ = null;
	protected boolean commentAdded_ = false;
	
	protected Bug(String title, String text, ArrayList<Comment> comments, GeoPoint point) {
		super(title, text, point);
		
		comments_ = comments;
	}
	
	/* Get the Bugs Comments */
	public ArrayList<Comment> getComments(){
		return comments_;
	}
	
	/* Set the Bugs Comment */
	public void setComments(ArrayList<Comment> comments) {
		comments_ = comments;
	}
	
	/* Get the Bugs State */
	public STATE getState() {
		return state_;
	}
	
	/* Set the Bugs State */
	public void setState(STATE state) {
		if(state == STATE.IGNORED && !isIgnorable())
			return;
		
		state_ = state;
	}

	/* Send the Bug to the Server
	 * Returns true if commit was successfully
	 */
	public abstract boolean commit();
	
	/* Creates a New Bug on the Server */
	public abstract boolean addNew();

	/* Return true if it is possible to add a Comment to this Bug */
	public abstract boolean isCommentable();
	
	/* Return true if this Bug can be ignored additionally to open or closed state */
	public abstract boolean isIgnorable();

	/* Return true if the Bug state can be switched from Closed to Opened */
	public abstract boolean isReopenable();
	
	/* Fill the State spinner in the Bug Editor */
	public abstract ArrayList<String> getStateNames(Context context);
	
	/* Parcelable interface */
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(mTitle);
		parcel.writeString(mDescription);
		mGeoPoint.writeToParcel(parcel, 0);
		
		parcel.writeInt(comments_.size());
		for(Comment comment : comments_)
			comment.writeToParcel(parcel, flags);
		
		if(state_ == Bug.STATE.OPEN)
			parcel.writeInt(1);
		else if(state_ == Bug.STATE.CLOSED)
			parcel.writeInt(2);
		else if(state_ == Bug.STATE.IGNORED)
			parcel.writeInt(3);
		else
			parcel.writeInt(0);
	}
	
	protected Bug(Parcel parcel) {
		super(parcel.readString(), parcel.readString(), GeoPoint.CREATOR.createFromParcel(parcel));
		
		comments_ = new ArrayList<Comment>();
		int size = parcel.readInt();
		for(int i = 0; i != size; ++i){
			comments_.add(new Comment(parcel));
		}
		
		switch(parcel.readInt()) {
		case 1:
			state_ = Bug.STATE.OPEN;
			break;
		case 2:
			state_ = Bug.STATE.CLOSED;
			break;
		case 3:
			state_ = Bug.STATE.IGNORED;
			break;
		default:
			state_ = Bug.STATE.OPEN;
		}
	}

	public String getEditCommentText() {
		return "";
	}

	public void addComment(String text) {
		if(!commentAdded_ || comments_.size() == 0)
			comments_.add(new Comment(text));
		else
			comments_.get(comments_.size() - 1).setText(text);
		
		commentAdded_ = true;
	}
}
