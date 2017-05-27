package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OsmoseFix implements Parcelable
{
    public static final Creator<OsmoseFix> CREATOR = new Creator<OsmoseFix>()
    {
        @Override
        public OsmoseFix createFromParcel(Parcel source)
        {
            return new OsmoseFix(source);
        }


        @Override
        public OsmoseFix[] newArray(int size)
        {
            return new OsmoseFix[size];
        }
    };

    private final List<OsmKeyValuePair> mAdd = new ArrayList<>();

    private final List<OsmKeyValuePair> mDelete = new ArrayList<>();

    private final List<OsmKeyValuePair> mModify = new ArrayList<>();


    public OsmoseFix()
    {
    }


    private OsmoseFix(Parcel parcel)
    {
        parcel.readList(mAdd, OsmKeyValuePair.class.getClassLoader());
        parcel.readList(mDelete, OsmKeyValuePair.class.getClassLoader());
        parcel.readList(mModify, OsmKeyValuePair.class.getClassLoader());
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeList(mAdd);
        parcel.writeList(mDelete);
        parcel.writeList(mModify);
    }


    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (OsmKeyValuePair tag : mAdd)
        {
            s.append("Add:\n").append(tag.toString()).append("\n");
        }
        for (OsmKeyValuePair tag : mDelete)
        {
            s.append("Delete:\n").append(tag.toString()).append("\n");
        }
        for (OsmKeyValuePair tag : mModify)
        {
            s.append("Modify:\n").append(tag.toString()).append("\n");
        }
        return s.toString();
    }


    public List<OsmKeyValuePair> getAdd()
    {
        return mAdd;
    }


    public List<OsmKeyValuePair> getDelete()
    {
        return mDelete;
    }


    public List<OsmKeyValuePair> getModify()
    {
        return mModify;
    }
}
