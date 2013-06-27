package org.gittner.osmbugs.bugs;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
	private String text_;
	
	public Comment(String text){
		text_ = text;
	}
	
	public Comment(Parcel parcel){
		text_ = parcel.readString();
	}
	
	public String getText(){
		return text_;
	}
	
	public void setText(String text){
		text_ = text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(text_);
	}
	
	public static final Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

		@Override
		public Comment createFromParcel(Parcel source) {
			return new Comment(source);
		}

		@Override
		public Comment[] newArray(int size) {
			return new Comment[size];
		}    	
    };
}
