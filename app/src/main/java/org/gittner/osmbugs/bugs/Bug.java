package org.gittner.osmbugs.bugs;

import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.common.Comment;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public abstract class Bug extends OverlayItem implements Parcelable {

    protected Bug(String title, String text, ArrayList<Comment> comments, GeoPoint point) {
        super(title, text, point);

        mComments = comments;
    }

    protected Bug(Parcel parcel) {
        super(parcel.readString(), parcel.readString(), GeoPoint.CREATOR.createFromParcel(parcel));

        mComments = new ArrayList<Comment>();
        int size = parcel.readInt();
        for (int i = 0; i != size; ++i) {
            mComments.add(new Comment(parcel));
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

    /* Returns all possible States for this Bug */
    public abstract ArrayList<String> getSStates();

    /* Return true if the Bug is commitable */
    public abstract boolean isCommitable(String newSState, String newComment);

    /* Send the Bug to the Server Returns true if commit was successfully */
    public abstract boolean commit(String newSState, String newComment);

    /* Return true if it is possible to add a Comment to this Bug */
    public abstract boolean isCommentable();

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mTitle);
        parcel.writeString(getSnippet());
        mGeoPoint.writeToParcel(parcel, 0);

        parcel.writeInt(mComments.size());
        for (Comment comment : mComments)
            comment.writeToParcel(parcel, flags);
    }

    /* Override this to retrieve extra Data before displaying the Bug in the Editor */
    public boolean willRetrieveExtraData() {
        return false;
    }

    public void retrieveExtraData() { }

    /* Holds all Comments of this Bug */
    private ArrayList<Comment> mComments = null;
}
