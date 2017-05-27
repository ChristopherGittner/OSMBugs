package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.platforms.Platforms;
import org.osmdroid.util.GeoPoint;

public abstract class Bug implements Parcelable
{
    private final GeoPoint mPoint;

    private final Platform mPlatform;


    Bug(final GeoPoint point, final Platform platform)
    {
        mPoint = point;
        mPlatform = platform;
    }


    Bug(Parcel parcel)
    {
        mPlatform = Platforms.byName(parcel.readString());
        mPoint = new GeoPoint(parcel.readDouble(), parcel.readDouble());
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(mPlatform.getName());

        parcel.writeDouble(mPoint.getLatitude());
        parcel.writeDouble(mPoint.getLongitude());
    }


    public Platform getPlatform()
    {
        return mPlatform;
    }


    public GeoPoint getPoint()
    {
        return mPoint;
    }


    public abstract Drawable getIcon();
}
