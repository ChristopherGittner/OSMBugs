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

public class MapdustBug extends Bug
{
    /* All Mapdust Types */
    public static final int WRONG_TURN = 1;
    public static final int BAD_ROUTING = 2;
    public static final int ONEWAY_ROAD = 3;
    public static final int BLOCKED_STREET = 4;
    public static final int MISSING_STREET = 5;
    public static final int ROUNDABOUT_ISSUE = 6;
    public static final int MISSING_SPEED_INFO = 7;
    public static final int OTHER = 8;

    public static final Creator<MapdustBug> CREATOR = new Parcelable.Creator<MapdustBug>()
    {
        @Override
        public MapdustBug createFromParcel(Parcel source)
        {
            return new MapdustBug(source);
        }


        @Override
        public MapdustBug[] newArray(int size)
        {
            return new MapdustBug[size];
        }
    };

    private final long mId;

    private final int mType;

    private final String mDescription;

    private List<Comment> mComments = null;

    private STATE mState = STATE.OPEN;


    public MapdustBug(
            double lat,
            double lon,
            long id,
            int type,
            String description,
            ArrayList<Comment> comments,
            STATE state)
    {
        super(new GeoPoint(lat, lon), Platforms.MAPDUST);
        mId = id;
        mType = type;
        mComments = comments;
        mDescription = description;
        mState = state;
    }


    private MapdustBug(Parcel parcel)
    {
        super(parcel);
        mId = parcel.readLong();
        mType = parcel.readInt();
        mDescription = parcel.readString();
        mComments = new ArrayList<>();
        int size = parcel.readInt();
        for (int i = 0; i != size; ++i)
        {
            mComments.add(new Comment(parcel));
        }

        switch (parcel.readInt())
        {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.CLOSED;
                break;

            case 3:
                mState = STATE.IGNORED;
                break;
        }
    }


    public String getDescription()
    {
        return mDescription;
    }


    public void setComments(List<Comment> comments)
    {
        mComments = comments;
    }


    public long getId()
    {
        return mId;
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
        parcel.writeLong(mId);
        parcel.writeInt(mType);
        parcel.writeString(mDescription);
        parcel.writeInt(mComments.size());
        for (Comment comment : mComments)
        {
            comment.writeToParcel(parcel, flags);
        }
        switch (mState)
        {
            case OPEN:
                parcel.writeInt(1);
                break;
            case CLOSED:
                parcel.writeInt(2);
                break;
            case IGNORED:
                parcel.writeInt(3);
                break;
        }
    }


    @Override
    public Drawable getIcon()
    {
        if (getState() == STATE.CLOSED)
        {
            return Images.get(R.drawable.mapdust_bug_green);
        }
        else if (getState() == STATE.IGNORED)
        {
            return Images.get(R.drawable.mapdust_bug_gray);
        }
        else
        {
            switch (getType())
            {
                case 1:
                    return Images.get(R.drawable.mapdust_wrong_turn);
                case 2:
                    return Images.get(R.drawable.mapdust_bad_routing);
                case 3:
                    return Images.get(R.drawable.mapdust_oneway_road);
                case 4:
                    return Images.get(R.drawable.mapdust_blocked_street);
                case 5:
                    return Images.get(R.drawable.mapdust_missing_street);
                case 6:
                    return Images.get(R.drawable.mapdust_roundabout_issue);
                case 7:
                    return Images.get(R.drawable.mapdust_missing_speed_info);
                case 8:
                    return Images.get(R.drawable.mapdust_other);
            }
        }
        return Images.get(R.drawable.mapdust_other);
    }


    public STATE getState()
    {
        return mState;
    }


    int getType()
    {
        return mType;
    }


    public enum STATE
    {
        OPEN,
        CLOSED,
        IGNORED
    }
}
