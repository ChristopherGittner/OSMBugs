
package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String mText;

    public Comment(String text) {
        mText = text;
    }

    public Comment(Parcel parcel) {
        mText = parcel.readString();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mText);
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
