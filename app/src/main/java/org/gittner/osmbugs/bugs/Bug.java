package org.gittner.osmbugs.bugs;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public abstract class Bug extends OverlayItem implements Parcelable {

    public enum STATE {
        OPEN, CLOSED, IGNORED
    }

    protected Bug(String title, String text, ArrayList<Comment> comments, GeoPoint point, STATE state) {
        super(title, text, point);

        mState = state;
        mNewState = state;
        mComments = comments;
    }

    protected Bug(Parcel parcel) {
        super(parcel.readString(), parcel.readString(), GeoPoint.CREATOR.createFromParcel(parcel));

        mComments = new ArrayList<Comment>();
        int size = parcel.readInt();
        for (int i = 0; i != size; ++i) {
            mComments.add(new Comment(parcel));
        }

        mNewComment = parcel.readString();

        switch (parcel.readInt()) {
            case 1:
                mState = Bug.STATE.OPEN;
                break;
            case 2:
                mState = Bug.STATE.CLOSED;
                break;
            case 3:
                mState = Bug.STATE.IGNORED;
                break;
            default:
                mState = Bug.STATE.OPEN;
        }

        switch (parcel.readInt()) {
            case 1:
                mNewState = Bug.STATE.OPEN;
                break;
            case 2:
                mNewState = Bug.STATE.CLOSED;
                break;
            case 3:
                mNewState = Bug.STATE.IGNORED;
                break;
            default:
                mNewState = Bug.STATE.OPEN;
        }
    }

    /* Get the Bugs Comments */
    public ArrayList<Comment> getComments() {
        return mComments;
    }

    /* Set the Bugs Comment */
    public void setComments(ArrayList<Comment> comments) {
        mComments = comments;
    }

    /* Get the Bugs State */
    public STATE getState() {
        return mState;
    }

    /* Set the Bugs State */
    public void setState(STATE state) {
        if (state == STATE.IGNORED && !isIgnorable())
            return;

        if (mState == STATE.CLOSED && !isReopenable())
            return;

        if (state == mNewState)
            return;

        mNewState = state;
    }

    public STATE getNewState() {
        return mNewState;
    }

    public boolean hasNewState() {
        return mState != mNewState;
    }

    /*
     * Send the Bug to the Server Returns true if commit was successfully
     */
    public abstract boolean commit();

    /* Return true if it is possible to add a Comment to this Bug */
    public abstract boolean isCommentable();

    /* Return true if this Bug can be ignored additionally to open or closed state */
    public abstract boolean isIgnorable();

    /* Return true if the Bug is closable */
    public boolean isClosable() {
        return true;
    }

    /* Return true if the Bug state can be switched from Closed to Opened */
    public abstract boolean isReopenable();

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mTitle);
        parcel.writeString(getSnippet());
        mGeoPoint.writeToParcel(parcel, 0);

        parcel.writeInt(mComments.size());
        for (Comment comment : mComments)
            comment.writeToParcel(parcel, flags);

        parcel.writeString(mNewComment);

        if (mState == Bug.STATE.OPEN)
            parcel.writeInt(1);
        else if (mState == Bug.STATE.CLOSED)
            parcel.writeInt(2);
        else if (mState == Bug.STATE.IGNORED)
            parcel.writeInt(3);
        else
            parcel.writeInt(0);

        if (mNewState == Bug.STATE.OPEN)
            parcel.writeInt(1);
        else if (mNewState == Bug.STATE.CLOSED)
            parcel.writeInt(2);
        else if (mNewState == Bug.STATE.IGNORED)
            parcel.writeInt(3);
        else
            parcel.writeInt(0);
    }

    public String getNewComment() {
        return mNewComment;
    }

    public void setNewComment(String text) {
        mNewComment = text;
    }

    public boolean hasNewComment() {
        return !mNewComment.equals("");
    }

    /* These can be overriden to allow the Display of custom Text for a Bug State */
    public String getStringFromState(Context context, STATE state) {
        if (state == STATE.OPEN)
            return context.getString(R.string.open);
        else if (state == STATE.CLOSED)
            return context.getString(R.string.closed);
        else if (state == STATE.IGNORED)
            return context.getString(R.string.ignored);
        else
            return "";
    }

    public Bug.STATE getStateFromString(Context context, String state) {
        if (state.equals(context.getString(R.string.closed)))
            return STATE.CLOSED;
        else if (state.equals(context.getString(R.string.ignored)))
            return STATE.IGNORED;
        else
            return STATE.OPEN;
    }

    /* Override this to retrieve extra Data before displaying the Bug in the Editor */
    public boolean willRetrieveExtraData() {
        return false;
    }

    public void retrieveExtraData() {
    }

    /* Holds the current state of this Bug */
    private STATE mState = STATE.OPEN;

    /* Holds the new State of the Bug */
    private STATE mNewState = STATE.OPEN;

    /* Holds all Comments of this Bug */
    private ArrayList<Comment> mComments = null;

    /* Holds the new Comment of this Bug*/
    private String mNewComment = "";
}
