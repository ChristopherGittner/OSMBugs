package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

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
    private DateTime mCreationDate;


    public OsmNoteComment()
    {
        this("", "", DateTime.now());
    }


    public OsmNoteComment(String text, String username, DateTime date)
    {
        super(text, username);
        mCreationDate = date;
    }


    public OsmNoteComment(Parcel parcel)
    {
        super(parcel);
        mCreationDate = DateTime.parse(parcel.readString());
    }


    public DateTime getCreationDate()
    {
        return mCreationDate;
    }


    public void setCreationDate(DateTime date)
    {
        mCreationDate = date;
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
        parcel.writeString(mCreationDate.toString());
    }
}
