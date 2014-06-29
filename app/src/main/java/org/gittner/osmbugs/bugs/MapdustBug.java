package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

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

    public MapdustBug(
            double lat,
            double lon,
            String title,
            String text,
            ArrayList<Comment> comments,
            int type,
            long id,
            STATE state) {

        super(title, text, comments, new GeoPoint(lat, lon));

        setType(type);
        setId(id);
        setState(state);
    }

    protected MapdustBug(Parcel parcel) {
        super(parcel);

        mId = parcel.readLong();
        mType = parcel.readInt();
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

    /* Get the Bugs State */
    public STATE getState() {
        return mState;
    }

    /* Set the Bugs State */
    public void setState(STATE state) {
        mState = state;
    }

    /* Get the Bugs Id */
    public long getId() {
        return mId;
    }

    /* Set the Bugs Id */
    public void setId(long id) {
        mId = id;
    }

    /* Get the Bugs Type */
    public int getType() {
        return mType;
    }

    /* Set the Bugs Type */
    public void setType(int type) {
        mType = type;
    }

    @Override
    public Drawable getMarker(int bitset) {
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
    public ArrayList<String> getSStates() {
        ArrayList<String> states = new ArrayList<String>();

        states.add(App.getContext().getString(R.string.open));

        if(mState == STATE.OPEN) {
            states.add(App.getContext().getString(R.string.closed));
            states.add(App.getContext().getString(R.string.software_bug));
        }

        return states;
    }

    @Override
    public boolean isCommitable(String newSState, String newComment) {

        /* Retrieve the new State */
        STATE newState = STATE.OPEN;
        if (newSState.equals(App.getContext().getString(R.string.closed))) {
            newState = STATE.CLOSED;
        } else if (newSState.equals(App.getContext().getString(R.string.software_bug))) {
            newState = STATE.IGNORED;
        }

        if(mState == STATE.OPEN) {
            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
        else if(mState == STATE.CLOSED) {
            if(newState != STATE.OPEN)
                return false;

            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
        else {
            if(newState != STATE.OPEN)
                return false;

            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
    }

    @Override
    public boolean commit(String newSState, String newComment) {

        /* Retrieve the new State */
        STATE newState = STATE.OPEN;
        if (newSState.equals(App.getContext().getString(R.string.closed))) {
            newState = STATE.CLOSED;
        } else if (newSState.equals(App.getContext().getString(R.string.software_bug))) {
            newState = STATE.IGNORED;
        }

        if (newState == mState) {
            MapdustApi.commentBug(mId, newComment, Settings.Mapdust.getUsername());
        } else if (newState != mState && !newComment.equals("")) {
            MapdustApi.changeBugStatus(mId, newState, newComment, Settings.Mapdust.getUsername());
        } else
            return false;

        return true;
    }

    @Override
    public boolean isCommentable() {
        return true;
    }

    public static boolean addNew(GeoPoint position, int type, String text) {
        return MapdustApi.addBug(position, text, type, Settings.Mapdust.getUsername());
    }

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeLong(mId);
        parcel.writeInt(mType);
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

    @Override
    public boolean willRetrieveExtraData() {
        return true;
    }

    @Override
    public void retrieveExtraData() {
        getComments().clear();
        getComments().addAll(MapdustApi.retrieveComments(mId));
    }

    /* Holds the Bugs State */
    private STATE mState = STATE.OPEN;

    /* Holds the Mapdust Id of this Bug */
    private long mId;

    /* Holds the Mapdust Type of this Bug */
    private int mType;
}
