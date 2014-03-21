package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class MapdustBug extends Bug {

    /* All Mapdust Types */
    public static final int WRONGTURN = 1;
    public static final int BADROUTING = 2;
    public static final int ONEWAYROAD = 3;
    public static final int BLOCKEDSTREET = 4;
    public static final int MISSINGSTREET = 5;
    public static final int ROUNDABOUTISSUE = 6;
    public static final int MISSINGSPEEDINFO = 7;
    public static final int OTHER = 8;

    public enum STATE {
        OPEN,
        CLOSED,
        IGNORED
    }

    public MapdustBug(
            double lat,
            double lon,
            String title,
            String text,
            ArrayList<Comment> comments,
            int type,
            long id,
            STATE state) {

        super(title, text, comments, new GeoPoint(lat, lon));

        setType(type);
        setId(id);
        setState(state);
    }

    protected MapdustBug(Parcel parcel) {
        super(parcel);

        mId = parcel.readLong();
        mType = parcel.readInt();
        switch (parcel.readInt()) {
            case 1:
                mState = STATE.OPEN;
                break;

            case 2:
                mState = STATE.CLOSED;
                break;

            case 3:
                mState = STATE.IGNORED;
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

    /* Get the Bugs Type */
    public int getType() {
        return mType;
    }

    /* Set the Bugs Type */
    public void setType(int type) {
        mType = type;
    }

    @Override
    public Drawable getMarker(int bitset) {
        if (getState() == STATE.CLOSED)
            return Drawings.MapdustClosed;
        else if (getState() == STATE.IGNORED)
            return Drawings.MapdustIgnored;
        else {
            switch (getType()) {
                case 1:
                    return Drawings.MapdustWrongTurn;
                case 2:
                    return Drawings.MapdustBadRouting;
                case 3:
                    return Drawings.MapdustOnewayRoad;
                case 4:
                    return Drawings.MapdustBlockedStreet;
                case 5:
                    return Drawings.MapdustMissingStreet;
                case 6:
                    return Drawings.MapdustRoundaboutIssue;
                case 7:
                    return Drawings.MapdustMissingSpeedInfo;
                case 8:
                    return Drawings.MapdustOther;
            }
        }

        return Drawings.MapdustOther;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public ArrayList<String> getSStates() {
        ArrayList<String> states = new ArrayList<String>();

        states.add(App.getContext().getString(R.string.open));

        if(mState == STATE.OPEN) {
            states.add(App.getContext().getString(R.string.closed));
            states.add(App.getContext().getString(R.string.software_bug));
        }

        return states;
    }

    @Override
    public boolean isCommitable(String newSState, String newComment) {

        /* Retrieve the new State */
        STATE newState = STATE.OPEN;
        if (newSState.equals(App.getContext().getString(R.string.closed))) {
            newState = STATE.CLOSED;
        } else if (newSState.equals(App.getContext().getString(R.string.software_bug))) {
            newState = STATE.IGNORED;
        }

        if(mState == STATE.OPEN) {
            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
        else if(mState == STATE.CLOSED) {
            if(newState != STATE.OPEN)
                return false;

            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
        else {
            if(newState != STATE.OPEN)
                return false;

            if(!newComment.equals(""))
                return true;
            else
                return false;
        }
    }

    @Override
    public boolean commit(String newSState, String newComment) {

        /* Retrieve the new State */
        STATE newState = STATE.OPEN;
        if (newSState.equals(App.getContext().getString(R.string.closed))) {
            newState = STATE.CLOSED;
        } else if (newSState.equals(App.getContext().getString(R.string.software_bug))) {
            newState = STATE.IGNORED;
        }

        if (newState == mState) {
            /* Upload a new Comment */
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));
            arguments.add(new BasicNameValuePair("comment", newComment));
            arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

            HttpPost request;
            if (Settings.isDebugEnabled())
                request =
                        new HttpPost("http://st.www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request =
                        new HttpPost("http://www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success */
                /* Mapdust returns 201 for commentBug as Success */
                if (response.getStatusLine().getStatusCode() != 201)
                    return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (newState != mState && !newComment.equals("")) {
            /* Upload a Status Change along with a comment (Required) */
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));

            if (newState == STATE.OPEN)
                arguments.add(new BasicNameValuePair("status", "1"));
            else if (newState == STATE.CLOSED)
                arguments.add(new BasicNameValuePair("status", "2"));
            else
                arguments.add(new BasicNameValuePair("status", "3"));

            arguments.add(new BasicNameValuePair("comment", newComment));
            arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

            HttpPost request;
            if (Settings.isDebugEnabled())
                request =
                        new HttpPost("http://st.www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request =
                        new HttpPost("http://www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success */
                /* Mapdust returns 201 for changeBugStatus as Success */
                if (response.getStatusLine().getStatusCode() != 201)
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

    @Override
    public boolean isCommentable() {
        return true;
    }

    public static boolean addNew(GeoPoint position, int type, String text) {
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

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("coordinates", String.valueOf(position.getLongitudeE6() / 1000000.0) + "," + String.valueOf(position.getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("description", text));
        switch (type) {
            case WRONGTURN:
                arguments.add(new BasicNameValuePair("type", "wrong_turn"));
                break;

            case BADROUTING:
                arguments.add(new BasicNameValuePair("type", "bad_routing"));
                break;

            case ONEWAYROAD:
                arguments.add(new BasicNameValuePair("type", "oneway_road"));
                break;

            case BLOCKEDSTREET:
                arguments.add(new BasicNameValuePair("type", "blocked_street"));
                break;

            case MISSINGSTREET:
                arguments.add(new BasicNameValuePair("type", "missing_street"));
                break;

            case ROUNDABOUTISSUE:
                arguments.add(new BasicNameValuePair("type", "wrong_roundabout"));
                break;

            case MISSINGSPEEDINFO:
                arguments.add(new BasicNameValuePair("type", "missing_speedlimit"));
                break;

            case OTHER:
                arguments.add(new BasicNameValuePair("type", "other"));
                break;

            default:
                return false;
        }
        arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

        Log.w("", "http://st.www.mapdust.com/api/addBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        HttpPost request;
        if (Settings.isDebugEnabled())
            request = new HttpPost("http://st.www.mapdust.com/api/addBug?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://www.mapdust.com/api/addBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute commit */
            HttpResponse response = client.execute(request);

            /* Check result for Success */
            /* Mapdust returns 201 for addBug as Success */
            if (response.getStatusLine().getStatusCode() != 201)
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
        parcel.writeInt(mType);
        switch (mState) {
            case OPEN:
                parcel.writeInt(1);
                break;

            case CLOSED:
                parcel.writeInt(2);
                break;

            case IGNORED:
                parcel.writeInt(3);
                break;
        }
    }

    public static final Creator<MapdustBug> CREATOR = new Parcelable.Creator<MapdustBug>() {

        @Override
        public MapdustBug createFromParcel(Parcel source) {
            return new MapdustBug(source);
        }

        @Override
        public MapdustBug[] newArray(int size) {
            return new MapdustBug[size];
        }
    };

    @Override
    public boolean willRetrieveExtraData() {
        return true;
    }

    @Override
    public void retrieveExtraData() {

        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("id", String.valueOf(mId)));

        HttpGet request;

        if (Settings.isDebugEnabled())
            request = new HttpGet("http://st.www.mapdust.com/api/getBug?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://www.mapdust.com/api/getBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return;

            /* If Request was Successful, parse the Stream */
            getComments().clear();
            getComments().addAll(MapdustParser.parseSingleBugForComments(response.getEntity().getContent()));

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Holds the Bugs State */
    private STATE mState = STATE.OPEN;

    /* Holds the Mapdust Id of this Bug */
    private long mId;

    /* Holds the Mapdust Type of this Bug */
    private int mType;
}
