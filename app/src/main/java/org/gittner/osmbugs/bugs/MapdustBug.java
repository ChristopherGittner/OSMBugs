package org.gittner.osmbugs.bugs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MapdustBug extends Bug {

    /* All Mapdust Types */
    public static final int WRONGTURN = 1;
    public static final int BADROUTING = 2;
    public static final int ONEWAYROAD = 3;
    public static final int BLOCKEDSTREET = 4;
    public static final int MISSINGSTREET = 5;
    public static final int ROUNDABOUTISSUE = 6;
    public static final int MISSINGSPEEDINFO = 7;
    public static final int OTHER = 8;

    public enum STATE {
        OPEN,
        CLOSED,
        IGNORED
    }

    public static final int[] STATE_NAMES = {
            R.string.open,
            R.string.closed,
            R.string.ignored };


    private long mId;

    private int mType;

    private String mDescription;

    private List<Comment> mComments = null;

    private STATE mState = STATE.OPEN;

    public MapdustBug(
            double lat,
            double lon,
            long id,
            int type,
            String description,
            ArrayList<Comment> comments,
            STATE state) {

        super(new GeoPoint(lat, lon));

        mId = id;
        mType = type;
        mComments = comments;
        mDescription = description;
        mState = state;
    }

    protected MapdustBug(Parcel parcel) {
        super(parcel);

        mId = parcel.readLong();
        mType = parcel.readInt();
        mDescription = parcel.readString();

        mComments = new ArrayList<Comment>();
        int size = parcel.readInt();
        for (int i = 0; i != size; ++i) {
            mComments.add(new Comment(parcel));
        }

        switch (parcel.readInt()) {
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

    public static List<String> getStateNames(Context context) {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i != STATE_NAMES.length; ++i) {
            names.add(context.getString(STATE_NAMES[i]));
        }

        return names;
    }

    public static STATE getStateByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "string", context.getPackageName());

        if(context.getString(R.string.open).equals(name)) return STATE.OPEN;
        else if(context.getString(R.string.closed).equals(name)) return STATE.CLOSED;
        else if(context.getString(R.string.ignored).equals(name)) return STATE.IGNORED;

        return null;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public STATE getState() {
        return mState;
    }

    public long getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    @Override
    public Drawable getIcon() {
        if (getState() == STATE.CLOSED)
            return Drawings.MapdustClosed;
        else if (getState() == STATE.IGNORED)
            return Drawings.MapdustIgnored;
        else {
            switch (getType()) {
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

        return Drawings.MapdustOther;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeLong(mId);
        parcel.writeInt(mType);
        parcel.writeString(mDescription);

        parcel.writeInt(mComments.size());
        for (Comment comment : mComments)
            comment.writeToParcel(parcel, flags);

        switch (mState) {
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
}
