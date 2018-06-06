package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

public class OsmNoteComment extends Comment implements Parcelable
{
    public static final Creator<OsmNoteComment> CREATOR = new Creator<OsmNoteComment>()
    {
        @Override
        public OsmNoteComment createFromParcel(Parcel source)
        {
            return new OsmNoteComment(source);
        }


        @Override
        public OsmNoteComment[] newArray(int size)
        {
            return new OsmNoteComment[size];
        }
    };

    /* Holds the creation date */
    private String mDate = "";


    public OsmNoteComment()
    {
        this("", "", "");
    }


    public OsmNoteComment(String text, String username, String date)
    {
        super(text, username);
        mDate = date;
    }


    public OsmNoteComment(Parcel parcel)
    {
        super(parcel);
        mDate = parcel.readString();
    }


    public String getDate()
    {
        return mDate;
    }


    public void setDate(String date)
    {
        mDate = date;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        super.writeToParcel(parcel, flags);
        parcel.writeString(mDate);
    }
}
