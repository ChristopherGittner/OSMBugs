package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.platforms.Platforms;
import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

public abstract class Bug implements Parcelable
{
    private final GeoPoint mPoint;

    private final Platform mPlatform;

    private final DateTime mCreationDate;


    Bug(final GeoPoint point, final Platform platform, final DateTime creationDate)
    {
        mPoint = point;
        mPlatform = platform;
        mCreationDate = creationDate;
    }


    Bug(Parcel parcel)
    {
        mPlatform = Platforms.byName(parcel.readString());
        mPoint = new GeoPoint(parcel.readDouble(), parcel.readDouble());
        mCreationDate = DateTime.parse(parcel.readString());
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(mPlatform.getName());

        parcel.writeDouble(mPoint.getLatitude());
        parcel.writeDouble(mPoint.getLongitude());
        parcel.writeString(mCreationDate.toString());
    }


    public Platform getPlatform()
    {
        return mPlatform;
    }


    public GeoPoint getPoint()
    {
        return mPoint;
    }


    public DateTime getCreationDate()
    {
        return mCreationDate;
    }


    public abstract Drawable getIcon();
}
