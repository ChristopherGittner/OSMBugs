package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class OpenstreetmapNote extends Bug {

    public enum STATE {
        OPEN,
        CLOSED
    }

    private long mId;

    private String mDescription;

    private List<Comment> mComments;

    private STATE mState = STATE.OPEN;

    public OpenstreetmapNote(
            double lat,
            double lon,
            long id,
            String description,
            List<Comment> comments,
            STATE state) {

        super(new GeoPoint(lat, lon));

        mId = id;
        mState = state;
        mDescription = description;
        mComments = comments;
    }

    public OpenstreetmapNote(Parcel parcel) {
        super(parcel);

        mId = parcel.readLong();

        mDescription = parcel.readString();

        mComments = new ArrayList<>();
        int size = parcel.readInt();
        for(int i = 0; i != size; ++i)
        {
            Comment comment = new Comment(parcel);
            mComments.add(comment);
        }

        switch (parcel.readInt()) {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.CLOSED;
                break;
        }
    }

    public BugOverlayItem getOverlayItem() {
        return new BugOverlayItem(this);
    }

    @Override
    public Drawable getIcon() {
        if (mState == STATE.CLOSED)
            return Drawings.OpenstreetmapNotesClosed;

        return Drawings.OpenstreetmapNotesOpen;
    }

    public long getId() {
        return mId;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public STATE getState() {
        return mState;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeLong(mId);
        parcel.writeString(mDescription);

        parcel.writeInt(mComments.size());
        for(int i = 0; i != mComments.size(); ++i)
        {
            mComments.get(i).writeToParcel(parcel, flags);
        }

        switch (mState) {
            case OPEN:
                parcel.writeInt(1);
                break;

            case CLOSED:
                parcel.writeInt(2);
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OpenstreetmapNote> CREATOR = new Parcelable.Creator<OpenstreetmapNote>() {

        @Override
        public OpenstreetmapNote createFromParcel(Parcel source) {
            return new OpenstreetmapNote(source);
        }

        @Override
        public OpenstreetmapNote[] newArray(int size) {
            return new OpenstreetmapNote[size];
        }
    };
}
