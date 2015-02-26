package org.gittner.osmbugs.bugs;

import android.content.Context;
import android.content.Intent;
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
        mPoint = new GeoPoint(parcel.readInt(), parcel.readInt());
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(mPlatform.getName());

        parcel.writeInt(mPoint.getLatitudeE6());
        parcel.writeInt(mPoint.getLongitudeE6());
    }


    public Platform getPlatform()
    {
        return mPlatform;
    }


    public Intent createEditor(Context context)
    {
        return mPlatform.createEditor(context, this);
    }


    public GeoPoint getPoint()
    {
        return mPoint;
    }


    public abstract Drawable getIcon();
}
