package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.statics.Images;
import org.osmdroid.util.GeoPoint;

public class KeeprightBug extends Bug {

    private static final String ICON_PREFIX = "keepright_zap_";

    public static enum STATE {
        OPEN,
        IGNORED,
        IGNORED_TMP
    }

    private final int mId;
    private final int mSchema;
    private final int mType;
    private STATE mState = STATE.OPEN;
    private final String mTitle;
    private final String mDescription;
    private final String mComment;
    private final long mWay;

    public KeeprightBug(
            double lat,
            double lon,
            int id,
            int schema,
            int type,
            STATE state,
            String title,
            String description,
            String comment,
            long way) {

        super(new GeoPoint(lat, lon));

        mId = id;
        mSchema = schema;
        mType = type;
        mState = state;
        mTitle = title + " <a href=http://www.openstreetmap.org/browse/way/" + way + ">" + way + "</a>";
        mDescription = description;
        mComment = comment;
        mWay = way;
    }

    private KeeprightBug(Parcel parcel) {
        super(parcel);

        mId = parcel.readInt();
        mSchema = parcel.readInt();
        mType = parcel.readInt();
        mTitle = parcel.readString();
        mDescription = parcel.readString();
        mComment = parcel.readString();
        mWay = parcel.readLong();

        switch (parcel.readInt()) {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.IGNORED;
                break;

            case 3:
                mState = STATE.IGNORED_TMP;
                break;
        }
    }

    public int getId() {
        return mId;
    }

    public int getSchema() {
        return mSchema;
    }

    public STATE getState() {
        return mState;
    }

    public String getTitle() { return mTitle; }

    public String getDescription() {
        return mDescription;
    }

    public String getComment() {
        return mComment;
    }

    @Override
    public Drawable getIcon() {

        if (mState == STATE.IGNORED_TMP) {
            return Images.get(R.drawable.keepright_zap_closed);
        }
        else if (mState == STATE.IGNORED) {
            return Images.get(R.drawable.keepright_zap_ignored);
        }

        return Images.getByName(ICON_PREFIX + mType, R.drawable.keepright_zap);
    }

    public Drawable getOpenIcon()
    {
        return Images.getByName(ICON_PREFIX + mType, R.drawable.keepright_zap);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeInt(mId);
        parcel.writeInt(mSchema);
        parcel.writeInt(mType);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mComment);
        parcel.writeLong(mWay);

        switch (mState) {
            case OPEN:
                parcel.writeInt(1);
                break;

            case IGNORED:
                parcel.writeInt(2);
                break;

            case IGNORED_TMP:
                parcel.writeInt(3);
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KeeprightBug> CREATOR = new Parcelable.Creator<KeeprightBug>() {

        @Override
        public KeeprightBug createFromParcel(Parcel source) {
            return new KeeprightBug(source);
        }

        @Override
        public KeeprightBug[] newArray(int size) {
            return new KeeprightBug[size];
        }
    };


}
