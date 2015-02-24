package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public abstract class Bug implements Parcelable
{
    private final GeoPoint mPoint;


    Bug(GeoPoint point)
    {
        mPoint = point;
    }


    Bug(Parcel parcel)
    {
        mPoint = new GeoPoint(parcel.readInt(), parcel.readInt());
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(mPoint.getLatitudeE6());
        parcel.writeInt(mPoint.getLongitudeE6());
    }


    public GeoPoint getPoint()
    {
        return mPoint;
    }


    public abstract Drawable getIcon();
}
