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
import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class OpenstreetbugsBug extends Bug {

    public enum STATE {
        OPEN,
        CLOSED
    }

    public OpenstreetbugsBug(
            double lat,
            double lon,
            String text,
            ArrayList<Comment> comments,
            long id,
            STATE state) {

        super("Openstreetbug", text, comments, new GeoPoint(lat, lon));

        setComments(comments);
        setId(id);
        setState(state);
    }

    public OpenstreetbugsBug(Parcel parcel) {
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

        if(mState != STATE.OPEN)
            states.add(App.getContext().getString(R.string.open));

        if(mState != STATE.CLOSED)
            states.add(App.getContext().getString(R.string.closed));

        return states;
    }

    @Override
    public boolean isCommitable(String newSState, String newComment) {
        if(mState == STATE.OPEN)
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

        if (!newComment.equals("")) {
            /* Upload a new Comment */
            HttpClient client = new DefaultHttpClient();

            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));
            arguments.add(new BasicNameValuePair("text", newComment + " [" + Settings.Openstreetbugs.getUsername() + " ]"));

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

        if (mState == STATE.OPEN && newState == STATE.CLOSED) {
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

    @Override
    public Drawable getMarker(int bitset) {
        if (mState == STATE.CLOSED)
            return Drawings.OpenstreetbugsDrawableClosed;

        return Drawings.OpenstreetbugsDrawableOpen;
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

    /* Holds the Bugs State */
    private STATE mState = STATE.OPEN;

    /* Holds the Openstreetbugs Id of this Bug */
    private long mId;
}
