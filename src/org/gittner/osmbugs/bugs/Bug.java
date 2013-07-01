package org.gittner.osmbugs.bugs;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public abstract class Bug extends OverlayItem implements Parcelable{

    public enum STATE{
        OPEN,
        CLOSED,
        IGNORED
    }

    private STATE state_ = STATE.OPEN;
    private STATE newState_ = STATE.OPEN;
    private ArrayList<Comment> comments_ = null;
    private String newComment_ = "";

    protected Bug(String title, String text, ArrayList<Comment> comments, GeoPoint point, STATE state) {
        super(title, text, point);

        state_ = state;
        newState_ = state;
        comments_ = comments;
    }

    protected Bug(Parcel parcel) {
        super(parcel.readString(), parcel.readString(), GeoPoint.CREATOR.createFromParcel(parcel));

        comments_ = new ArrayList<Comment>();
        int size = parcel.readInt();
        for(int i = 0; i != size; ++i){
            comments_.add(new Comment(parcel));
        }

        newComment_ = parcel.readString();

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

        switch(parcel.readInt()) {
            case 1:
                newState_ = Bug.STATE.OPEN;
                break;
            case 2:
                newState_ = Bug.STATE.CLOSED;
                break;
            case 3:
                newState_ = Bug.STATE.IGNORED;
                break;
            default:
                newState_ = Bug.STATE.OPEN;
        }
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

        if(state_ == STATE.CLOSED && !isReopenable())
            return;

        if(state == newState_)
            return;

        newState_ = state;
    }

    public STATE getNewState() {
        return newState_;
    }

    public boolean hasNewState() {
        return state_ != newState_;
    }

    /* Send the Bug to the Server
     * Returns true if commit was successfully
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
        parcel.writeString(mDescription);
        mGeoPoint.writeToParcel(parcel, 0);

        parcel.writeInt(comments_.size());
        for(Comment comment : comments_)
            comment.writeToParcel(parcel, flags);

        parcel.writeString(newComment_);

        if(state_ == Bug.STATE.OPEN)
            parcel.writeInt(1);
        else if(state_ == Bug.STATE.CLOSED)
            parcel.writeInt(2);
        else if(state_ == Bug.STATE.IGNORED)
            parcel.writeInt(3);
        else
            parcel.writeInt(0);

        if(newState_ == Bug.STATE.OPEN)
            parcel.writeInt(1);
        else if(newState_ == Bug.STATE.CLOSED)
            parcel.writeInt(2);
        else if(newState_ == Bug.STATE.IGNORED)
            parcel.writeInt(3);
        else
            parcel.writeInt(0);
    }

    public String getNewComment() {
        return newComment_;
    }

    public void setNewComment(String text) {
        newComment_ = text;
    }

    public boolean hasNewComment() {
        return !newComment_.equals("");
    }

    /* These can be overriden to allow the Display of custom Text for a Bug State */
    public String getStringFromState(Context context, STATE state) {
        if(state == STATE.OPEN)
            return context.getString(R.string.open);
        else if(state == STATE.CLOSED)
            return context.getString(R.string.closed);
        else if(state == STATE.IGNORED)
            return context.getString(R.string.ignored);
        else
            return "";
    }

    public  Bug.STATE getStateFromString(Context context, String state) {
        if(state.equals(context.getString(R.string.closed)))
            return STATE.CLOSED;
        else if(state.equals(context.getString(R.string.ignored)))
            return STATE.IGNORED;
        else
            return STATE.OPEN;
    }
}
