package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

import org.gittner.osmbugs.Helpers.Openstreetmap;

import java.util.ArrayList;
import java.util.List;

public class OsmoseElement implements Parcelable
{
    public static final Creator<OsmoseElement> CREATOR = new Creator<OsmoseElement>()
    {
        @Override
        public OsmoseElement createFromParcel(Parcel source)
        {
            return new OsmoseElement(source);
        }


        @Override
        public OsmoseElement[] newArray(int size)
        {
            return new OsmoseElement[size];
        }
    };

    private final List<OsmKeyValuePair> mTags = new ArrayList<>();
    private final List<OsmoseFix> mFixes = new ArrayList<>();

    private int mType = 0;
    private long mId = 0;


    public OsmoseElement()
    {
    }


    private OsmoseElement(Parcel parcel)
    {
        mType = parcel.readInt();
        mId = parcel.readLong();
        parcel.readList(mTags, OsmKeyValuePair.class.getClassLoader());
        parcel.readList(mFixes, OsmKeyValuePair.class.getClassLoader());
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(mType);
        parcel.writeLong(mId);
        parcel.writeList(mTags);
        parcel.writeList(mFixes);
    }


    @Override
    public String toString()
    {
        String s = "";
        switch (mType)
        {
            case Openstreetmap.TYPE_NODE:
                s += "Node <a href=http://www.openstreetmap.org/browse/node/" + mId + ">" + mId + "</a>";
                break;

            case Openstreetmap.TYPE_WAY:
                s += "Way <a href=http://www.openstreetmap.org/browse/way/" + mId + ">" + mId + "</a>";
                break;

            case Openstreetmap.TYPE_RELATION:
                s += "Relation <a href=http://www.openstreetmap.org/browse/relation/" + mId + ">" + mId + "</a>";
                break;
        }
        return s;
    }


    public int getType()
    {
        return mType;
    }


    public void setType(int type)
    {
        mType = type;
    }


    public long getId()
    {
        return mId;
    }


    public void setId(long id)
    {
        mId = id;
    }


    public List<OsmKeyValuePair> getTags()
    {
        return mTags;
    }


    public List<OsmoseFix> getFixes()
    {
        return mFixes;
    }
}
