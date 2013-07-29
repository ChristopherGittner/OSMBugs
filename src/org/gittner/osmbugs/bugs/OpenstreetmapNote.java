
package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class OpenstreetmapNote extends Bug {

    private long id_;

    public OpenstreetmapNote(
            double lat,
            double lon,
            String text,
            ArrayList<Comment> comments,
            long id,
            STATE state) {

        super("Openstreetmap Note", text, comments, new GeoPoint(lat, lon), state);

        setId(id);
    }

    public OpenstreetmapNote(Parcel parcel) {
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

        if (!hasNewComment())
            return false;

        if (hasNewComment() && !hasNewState()) {
            /* Only Upload a new Comment */
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add the Authentication Details if we have a username in the Preferences */
            if (!Settings.OpenstreetmapNotes.getUsername().equals("")) {
                client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(Settings.OpenstreetmapNotes.getUsername(),
                                Settings.OpenstreetmapNotes.getPassword()));
            }

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("text", getNewComment()));

            HttpPost request;
            if (!Settings.DEBUG)
                request = new HttpPost("http://api.openstreetmap.org/api/0.6/notes/" + id_ + "/comment?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request = new HttpPost("http://api06.dev.openstreetmap.org/api/0.6/notes/" + id_ + "/comment?" + URLEncodedUtils.format(arguments, "utf-8"));

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
        } else if (hasNewComment() && hasNewState() && getNewState() == STATE.CLOSED) {
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add the Authentication Details if we have a username in the Preferences */
            if (!Settings.OpenstreetmapNotes.getUsername().equals("")) {
                client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(Settings.OpenstreetmapNotes.getUsername(),
                                Settings.OpenstreetmapNotes.getPassword()));
            }

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("text", getNewComment()));

            HttpPost request;
            if (!Settings.DEBUG)
                request = new HttpPost("http://api.openstreetmap.org/api/0.6/notes/" + id_ + "/close?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request = new HttpPost("http://api06.dev.openstreetmap.org/api/0.6/notes/" + id_ + "/close?" + URLEncodedUtils.format(arguments, "utf-8"));

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
        } else
            return false;

        return true;
    }

    /* Openstreetmap Notes can be commented */
    @Override
    public boolean isCommentable() {
        if (getState() == STATE.OPEN)
            return true;

        return false;
    }

    /* Openstreetmap Notes cannot be ignored */
    @Override
    public boolean isIgnorable() {
        return false;
    }

    /* Openstreetmap Notes cannot be reopened */
    @Override
    public boolean isReopenable() {
        return false;
    }

    @Override
    public Drawable getMarker(int bitset) {
        if (getState() == Bug.STATE.CLOSED)
            return Drawings.OpenstreetmapNotesClosed;

        return Drawings.OpenstreetmapNotesOpen;
    }

    public static boolean addNew(GeoPoint position, String text) {

        DefaultHttpClient client = new DefaultHttpClient();

        /* Add the Authentication Details if we have a username in the Preferences */
        if (!Settings.OpenstreetmapNotes.getUsername().equals("")) {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(Settings.OpenstreetmapNotes.getUsername(),
                            Settings.OpenstreetmapNotes.getPassword()));
        }

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
        arguments.add(new BasicNameValuePair("lat",
                String.valueOf(position.getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("lon",
                String.valueOf(position.getLongitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("text", text));

        HttpPost request;

        if (!Settings.DEBUG)
            request = new HttpPost("http://api.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://api06.dev.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));

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

        return true;
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

    public static final Creator<OpenstreetmapNote> CREATOR = new Parcelable.Creator<OpenstreetmapNote>() {

        @Override
        public OpenstreetmapNote createFromParcel(Parcel source) {
            return new OpenstreetmapNote(source);
        }

        @Override
        public OpenstreetmapNote[] newArray(int size) {
            return new OpenstreetmapNote[size];
        }
    };
}
