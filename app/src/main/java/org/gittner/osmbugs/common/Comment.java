package org.gittner.osmbugs.common;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable
{
    public static final Creator<Comment> CREATOR = new Parcelable.Creator<Comment>()
    {
        @Override
        public Comment createFromParcel(Parcel source)
        {
            return new Comment(source);
        }


        @Override
        public Comment[] newArray(int size)
        {
            return new Comment[size];
        }
    };

    /* Holds the Text of the Comment */
    private String mText;

    /* Holds the Username of the Creator */
    private String mUsername;


    public Comment()
    {
        this("", "");
    }


    public Comment(String text, String username)
    {
        mText = text;
        mUsername = username;
    }


    public Comment(String text)
    {
        this(text, "");
    }


    public Comment(Parcel parcel)
    {
        mText = parcel.readString();
        mUsername = parcel.readString();
    }


    public String getText()
    {
        return mText;
    }


    public void setText(String text)
    {
        mText = text;
    }


    public String getUsername()
    {
        return mUsername;
    }


    public void setUsername(String username)
    {
        mUsername = username;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(mText);
        parcel.writeString(mUsername);
    }
}
