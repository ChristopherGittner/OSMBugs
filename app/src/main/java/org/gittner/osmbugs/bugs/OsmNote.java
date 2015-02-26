package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.platforms.Platforms;
import org.gittner.osmbugs.statics.Images;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class OsmNote extends Bug
{
    public static final Creator<OsmNote> CREATOR = new Parcelable.Creator<OsmNote>()
    {
        @Override
        public OsmNote createFromParcel(Parcel source)
        {
            return new OsmNote(source);
        }


        @Override
        public OsmNote[] newArray(int size)
        {
            return new OsmNote[size];
        }
    };

    private final long mId;

    private final String mDescription;

    private final List<Comment> mComments;

    private STATE mState = STATE.OPEN;


    public OsmNote(
            double lat,
            double lon,
            long id,
            String description,
            List<Comment> comments,
            STATE state)
    {
        super(new GeoPoint(lat, lon), Platforms.OSM_NOTES);
        mId = id;
        mState = state;
        mDescription = description;
        mComments = comments;
    }


    private OsmNote(Parcel parcel)
    {
        super(parcel);
        mId = parcel.readLong();
        mDescription = parcel.readString();
        mComments = new ArrayList<>();
        int size = parcel.readInt();

        for (int i = 0; i != size; ++i)
        {
            Comment comment = new Comment(parcel);
            mComments.add(comment);
        }

        switch (parcel.readInt())
        {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.CLOSED;
                break;
        }
    }


    public long getId()
    {
        return mId;
    }


    public String getDescription()
    {
        return mDescription;
    }


    public List<Comment> getComments()
    {
        return mComments;
    }


    public STATE getState()
    {
        return mState;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(mId);
        parcel.writeString(mDescription);
        parcel.writeInt(mComments.size());

        for (int i = 0; i != mComments.size(); ++i)
        {
            mComments.get(i).writeToParcel(parcel, flags);
        }

        switch (mState)
        {
            case OPEN:
                parcel.writeInt(1);
                break;

            case CLOSED:
                parcel.writeInt(2);
                break;
        }
    }


    @Override
    public Drawable getIcon()
    {
        if (mState == STATE.CLOSED)
        {
            return Images.get(R.drawable.osm_notes_closed_bug);
        }

        return Images.get(R.drawable.osm_notes_open_bug);
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    public enum STATE
    {
        OPEN,
        CLOSED
    }
}
