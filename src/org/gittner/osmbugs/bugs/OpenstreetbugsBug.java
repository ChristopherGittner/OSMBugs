
package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class OpenstreetbugsBug extends Bug {

    private long id_;

    public OpenstreetbugsBug(
            double lat,
            double lon,
            String text,
            ArrayList<Comment> comments,
            long id,
            STATE state) {

        super("Openstreetbug", text, comments, new GeoPoint(lat, lon), state);

        setComments(comments);
        setId(id);
    }

    public OpenstreetbugsBug(Parcel parcel) {
        super(parcel);
        id_ = parcel.readLong();
    }

    /* Get the Bugs Id */
    public long getId() {
        return id_;
    }

    /* Set the Bugs Id */
    public void setId(long id) {
        id_ = id;
    }

    @Override
    public boolean commit() {

        if (hasNewComment()) {
            /* Upload a new Comment */
            HttpClient client = new DefaultHttpClient();

            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));
            arguments.add(new BasicNameValuePair("text", getNewComment() + " [" + Settings.Openstreetbugs.getUsername() + " ]"));

            HttpGet request = new HttpGet("http://openstreetbugs.schokokeks.org/api/0.1/editPOIexec?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success */
                if (response.getStatusLine().getStatusCode() != 200)
                    return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (hasNewState() && getNewState() == STATE.CLOSED) {
            /* Close the Bug */
            HttpClient client = new DefaultHttpClient();

            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));

            HttpGet request = new HttpGet("http://openstreetbugs.schokokeks.org/api/0.1/closePOIexec?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success */
                if (response.getStatusLine().getStatusCode() != 200)
                    return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /* Openstreetbugs can be commented */
    @Override
    public boolean isCommentable() {
        return true;
    }

    /* Openstreetbugs cannot be ignored */
    @Override
    public boolean isIgnorable() {
        return false;
    }

    /* Openstreetbugs cannot be reopened */
    @Override
    public boolean isReopenable() {
        return false;
    }

    @Override
    public Drawable getMarker(int bitset) {
        if (getState() == Bug.STATE.CLOSED)
            return Drawings.OpenstreetbugsDrawableClosed;

        return Drawings.OpenstreetbugsDrawableOpen;
    }

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeLong(id_);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OpenstreetbugsBug> CREATOR = new Parcelable.Creator<OpenstreetbugsBug>() {

        @Override
        public OpenstreetbugsBug createFromParcel(Parcel source) {
            return new OpenstreetbugsBug(source);
        }

        @Override
        public OpenstreetbugsBug[] newArray(int size) {
            return new OpenstreetbugsBug[size];
        }
    };
}
