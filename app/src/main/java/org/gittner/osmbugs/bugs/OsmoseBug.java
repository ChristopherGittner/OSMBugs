package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.OsmoseEditActivity;
import org.gittner.osmbugs.statics.Images;
import org.osmdroid.util.GeoPoint;

public class OsmoseBug extends Bug {

    private static final String ICON_PREFIX = "osmose_marker_b_";

    private final int mItem;

    private final long mId;

    private final String mTitle;

    private String mFix = null;

    private long mOsmElement = 0;

    private int mOsmElementType = 0;

    public OsmoseBug(
            double lat,
            double lon,
            long id,
            int item,
            String title) {

        super(new GeoPoint(lat, lon));

        mId = id;
        mItem = item;
        mTitle = title;
    }

    private OsmoseBug(Parcel parcel) {
        super(parcel);

        mId = parcel.readLong();
        mItem = parcel.readInt();
        mTitle = parcel.readString();
        mFix = parcel.readString();
        mOsmElement = parcel.readLong();
        mOsmElementType = parcel.readInt();
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public Drawable getIcon() {
        return Images.getByName(ICON_PREFIX + mItem, R.drawable.osmose_marker_b_0);
    }

    @Override
    public Class<?> getEditorClass()
    {
        return OsmoseEditActivity.class;
    }

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeLong(mId);
        parcel.writeInt(mItem);
        parcel.writeString(mTitle);
        parcel.writeString(mFix);
        parcel.writeLong(mOsmElement);
        parcel.writeInt(mOsmElementType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OsmoseBug> CREATOR = new Creator<OsmoseBug>() {

        @Override
        public OsmoseBug createFromParcel(Parcel source) {
            return new OsmoseBug(source);
        }

        @Override
        public OsmoseBug[] newArray(int size) {
            return new OsmoseBug[size];
        }
    };

    public long getId() {
        return mId;
    }
}
