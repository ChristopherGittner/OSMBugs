package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

public class OsmKeyValuePair implements Parcelable
{
    public static final Creator<OsmKeyValuePair> CREATOR = new Creator<OsmKeyValuePair>()
    {
        @Override
        public OsmKeyValuePair createFromParcel(Parcel source)
        {
            return new OsmKeyValuePair(source);
        }


        @Override
        public OsmKeyValuePair[] newArray(int size)
        {
            return new OsmKeyValuePair[size];
        }
    };

    private String mKey = "";
    private String mValue = "";


    public OsmKeyValuePair()
    {
    }


    public OsmKeyValuePair(String key, String value)
    {
        mKey = key;
        mValue = value;
    }


    public OsmKeyValuePair(Parcel parcel)
    {
        mKey = parcel.readString();
        mValue = parcel.readString();
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(mKey);
        parcel.writeString(mValue);
    }


    @Override
    public String toString()
    {
        return "\"" + mKey + "\" : \"" + mValue + "\"";
    }


    public String getKey()
    {
        return mKey;
    }


    public void setKey(String key)
    {
        mKey = key;
    }


    public String getValue()
    {
        return mValue;
    }


    public void setValue(String value)
    {
        mValue = value;
    }
}
