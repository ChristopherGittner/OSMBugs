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
import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OpenstreetmapNotesApi;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class OpenstreetmapNote extends Bug {

    public enum STATE {
        OPEN,
        CLOSED
    }

    public OpenstreetmapNote(
            double lat,
            double lon,
            String text,
            ArrayList<Comment> comments,
            long id,
            STATE state) {

        super("Openstreetmap Note", text, comments, new GeoPoint(lat, lon));

        setId(id);
        setState(state);
    }

    public OpenstreetmapNote(Parcel parcel) {
        super(parcel);
        mId = parcel.readLong();
        switch (parcel.readInt()) {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.CLOSED;
                break;
        }
    }

    /* Get the Bugs State */
    public STATE getState() {
        return mState;
    }

    /* Set the Bugs State */
    public void setState(STATE state) {
        mState = state;
    }

    /* Get the Bugs Id */
    public long getId() {
        return mId;
    }

    /* Set the Bugs Id */
    public void setId(long id) {
        mId = id;
    }

    @Override
    public ArrayList<String> getSStates() {
        ArrayList<String> states = new ArrayList<String>();

        if(mState == STATE.OPEN) {
            states.add(App.getContext().getString(R.string.open));
            states.add(App.getContext().getString(R.string.closed));
        }

        return states;
    }

    @Override
    public boolean isCommitable(String newSState, String newComment) {
        if(mState == STATE.OPEN && !newComment.equals(""))
            return true;

        return false;
    }

    @Override
    public boolean commit(String newSState, String newComment) {

        /* Retrieve the new State */
        STATE newState = STATE.OPEN;
        if (newSState.equals(App.getContext().getString(R.string.closed))) {
            newState = STATE.CLOSED;
        }

        if (newComment.equals(""))
            return false;

        if (newState == STATE.OPEN) {
            OpenstreetmapNotesApi.addComment(
                    mId,
                    Settings.OpenstreetmapNotes.getUsername(),
                    Settings.OpenstreetmapNotes.getPassword(),
                    newComment);
        } else if (!newComment.equals("") && mState == STATE.OPEN && newState == STATE.CLOSED) {
            OpenstreetmapNotesApi.closeBug(
                    mId,
                    Settings.OpenstreetmapNotes.getUsername(),
                    Settings.OpenstreetmapNotes.getPassword(),
                    newComment);
        } else
            return false;

        return true;
    }

    @Override
    public boolean isCommentable() {
        if (mState == STATE.OPEN)
            return true;

        return false;
    }

    @Override
    public Drawable getMarker(int bitset) {
        if (mState == STATE.CLOSED)
            return Drawings.OpenstreetmapNotesClosed;

        return Drawings.OpenstreetmapNotesOpen;
    }

    public static boolean addNew(GeoPoint position, String text) {

        DefaultHttpClient client = new DefaultHttpClient();

        /* Add the Authentication Details if we have a username in the Preferences */
        if (!Settings.OpenstreetmapNotes.getUsername().equals("")) {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(Settings.OpenstreetmapNotes.getUsername(),
                            Settings.OpenstreetmapNotes.getPassword())
            );
        }

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
        arguments.add(new BasicNameValuePair("lat",
                String.valueOf(position.getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("lon",
                String.valueOf(position.getLongitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("text", text));

        HttpPost request;

        if (!Settings.isDebugEnabled())
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

        parcel.writeLong(mId);
        switch (mState) {
            case OPEN:
                parcel.writeInt(1);
                break;

            case CLOSED:
                parcel.writeInt(2);
                break;
        }
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

    /* Holds the Bugs State */
    private STATE mState = STATE.OPEN;

    /* Holds the Openstreetmap Notes Id of this Bug */
    private long mId;
}
