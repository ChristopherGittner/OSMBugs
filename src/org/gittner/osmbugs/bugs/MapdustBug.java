package org.gittner.osmbugs.bugs;

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
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.util.ArrayList;

public class MapdustBug extends Bug {

    private long id_;
    private int type_;

    public static int WRONGTURN = 1;
    public static int BADROUTING = 2;
    public static int ONEWAYROAD = 3;
    public static int BLOCKEDSTREET = 4;
    public static int MISSINGSTREET = 5;
    public static int ROUNDABOUTISSUE = 6;
    public static int MISSINGSPEEDINFO = 7;
    public static int OTHER = 8;

    public MapdustBug(double lat, double lon, String title, String text, ArrayList<Comment> comments, int type, long id, Bug.STATE state) {
        super(title, text, comments, new GeoPoint(lat, lon), state);

        setType(type);
        setId(id);
    }

    protected MapdustBug(Parcel parcel) {
        super(parcel);

        id_ = parcel.readLong();
        type_ = parcel.readInt();
    }

    /* Get the Bugs Id */
    public long getId() {
        return id_;
    }

    /* Set the Bugs Id */
    public void setId(long id) {
        id_ = id;
    }

    /* Get the Bugs Type */
    public int getType() {
        return type_;
    }

    /* Set the Bugs Type */
    public void setType(int type) {
        type_ = type;
    }

    @Override
    public Drawable getMarker(int bitset) {
        if(getState() == Bug.STATE.CLOSED)
            return Drawings.MapdustClosed;
        else if(getState() == Bug.STATE.IGNORED)
            return Drawings.MapdustIgnored;
        else {
            switch(getType()) {
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

        return Drawings.KeeprightDrawable100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean commit() {

        if(!hasNewComment())
            return false;

        if(!hasNewState() && hasNewComment()){
            /* Upload a new Comment */
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));
            arguments.add(new BasicNameValuePair("comment", getNewComment()));
            arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

            HttpPost request;
            if(Settings.DEBUG)
                request = new HttpPost("http://st.www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request = new HttpPost("http://www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success*/
                /* Mapdust returns 201 for commentBug as Success */
                if(response.getStatusLine().getStatusCode() != 201)
                    return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if(hasNewState() && hasNewComment()){
            /* Upload a Status Change along with a comment (Required) */
            DefaultHttpClient client = new DefaultHttpClient();

            /* Add all Arguments */
            ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
            arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
            arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));

            if(getNewState() == Bug.STATE.OPEN)
                arguments.add(new BasicNameValuePair("status", "1"));
            else if(getNewState() == Bug.STATE.CLOSED)
                arguments.add(new BasicNameValuePair("status", "2"));
            else
                arguments.add(new BasicNameValuePair("status", "3"));

            arguments.add(new BasicNameValuePair("comment", getNewComment()));
            arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

            HttpPost request;
            if(Settings.DEBUG)
                request = new HttpPost("http://st.www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));
            else
                request = new HttpPost("http://www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));

            try {
                /* Execute commit */
                HttpResponse response = client.execute(request);

                /* Check result for Success*/
                /* Mapdust returns 201 for changeBugStatus as Success */
                if(response.getStatusLine().getStatusCode() != 201)
                    return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;

        return true;
    }

    @Override
    public boolean isCommentable() {
        return true;
    }

    @Override
    public boolean isClosable() {
        if(getState() == STATE.OPEN)
            return true;

        return false;
    }

    @Override
    public boolean isIgnorable() {
        if(getState() == STATE.OPEN)
            return true;

        return false;
    }

    @Override
    public boolean isReopenable() {
        if(getState() == STATE.CLOSED || getState() == STATE.IGNORED)
            return true;

        return false;
    }

    public static boolean addNew(GeoPoint position, String text) {
        //TODO: Make it possible to add other Bug Types than "other"
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add the Authentication Details if we have a username in the Preferences */
        if(!Settings.OpenstreetmapNotes.getUsername().equals("")) {
            client.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(Settings.OpenstreetmapNotes.getUsername(),
                            Settings.OpenstreetmapNotes.getPassword()));
        }

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("coordinates", String.valueOf(position.getLongitudeE6() / 1000000.0) +
                "," + String.valueOf(position.getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("description", text));
        arguments.add(new BasicNameValuePair("type", "other"));
        arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

        HttpPost request;
        if(Settings.DEBUG)
            request = new HttpPost("http://st.www.mapdust.com/api/addBug?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://www.mapdust.com/api/addBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute commit */
            HttpResponse response = client.execute(request);

            /* Check result for Success*/
            /* Mapdust returns 201 for addBug as Success */
            if(response.getStatusLine().getStatusCode() != 201)
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
        parcel.writeInt(type_);
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
    public String getStringFromState(Context context, STATE state) {
        if(state == STATE.OPEN)
            return context.getString(R.string.open);
        else if(state == STATE.CLOSED)
            return context.getString(R.string.closed);
        else if(state == STATE.IGNORED)
            return context.getString(R.string.software_bug);
        else
            return "";
    }

    @Override
    public  Bug.STATE getStateFromString(Context context, String state) {
        if(state.equals(context.getString(R.string.closed)))
            return STATE.CLOSED;
        else if(state.equals(context.getString(R.string.software_bug)))
            return STATE.IGNORED;
        else
            return STATE.OPEN;
    }

    @Override
    public boolean willRetrieveExtraData() {
        return true;
    }

    @Override
    public void retrieveExtraData() {

        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("id", String.valueOf(id_)));

        HttpGet request;

        if(Settings.DEBUG)
            request = new HttpGet("http://st.www.mapdust.com/api/getBug?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://www.mapdust.com/api/getBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if(response.getStatusLine().getStatusCode() != 200)
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
}
