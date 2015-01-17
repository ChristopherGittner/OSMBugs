package org.gittner.osmbugs.api;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class MapdustApi {

    private static final String API_KEY = "ae58b0b4aa3f876265a4d5f29167b73c";

    public static ArrayList<MapdustBug> downloadBBox(BoundingBoxE6 bBox) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<>();

        arguments.add(new BasicNameValuePair("key", API_KEY));
        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBox.getLonEastE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLatSouthE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLonWestE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLatNorthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("comments", "1"));
        arguments.add(new BasicNameValuePair("ft", getMapdustSelectionString()));
        arguments.add(new BasicNameValuePair("fs", getMapdustEnabledTypesString()));

        HttpGet request;

        if (Settings.isDebugEnabled())
            request = new HttpGet("http://st.www.mapdust.com/api/getBugs?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://www.mapdust.com/api/getBugs?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return null;

            /* If Request was Successful, parse the Stream */
            return MapdustParser.parse(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean commentBug(long id, String comment, String nickname)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<>();
        arguments.add(new BasicNameValuePair("key", API_KEY));
        arguments.add(new BasicNameValuePair("id", String.valueOf(id)));
        arguments.add(new BasicNameValuePair("comment", comment));
        arguments.add(new BasicNameValuePair("nickname", nickname));

        HttpPost request;
        if (Settings.isDebugEnabled())
            request = new HttpPost("http://st.www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));

        Log.d("", "http://st.www.mapdust.com/api/commentBug?" + URLEncodedUtils.format(arguments, "utf-8"));

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

        return true;
    }

    public static boolean changeBugStatus(long id, MapdustBug.STATE state, String comment, String username)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<>();
        arguments.add(new BasicNameValuePair("key", API_KEY));
        arguments.add(new BasicNameValuePair("id", String.valueOf(id)));

        switch (state) {
            case OPEN:
                arguments.add(new BasicNameValuePair("status", "1"));
                break;

            case CLOSED:
                arguments.add(new BasicNameValuePair("status", "2"));
                break;

            default:
                arguments.add(new BasicNameValuePair("status", "3"));
                break;
        }

        arguments.add(new BasicNameValuePair("comment", comment));
        arguments.add(new BasicNameValuePair("nickname", username));

        HttpPost request;
        if (Settings.isDebugEnabled())
            request = new HttpPost("http://st.www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://www.mapdust.com/api/changeBugStatus?" + URLEncodedUtils.format(arguments, "utf-8"));

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

        return true;
    }

    public static boolean addBug(GeoPoint position, int type, String description)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<>();

        arguments.add(new BasicNameValuePair("key", API_KEY));
        arguments.add(new BasicNameValuePair("coordinates", String.valueOf(position.getLongitudeE6() / 1000000.0) + "," + String.valueOf(position.getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("description", description));
        switch (type) {
            case MapdustBug.WRONG_TURN:
                arguments.add(new BasicNameValuePair("type", "wrong_turn"));
                break;

            case MapdustBug.BAD_ROUTING:
                arguments.add(new BasicNameValuePair("type", "bad_routing"));
                break;

            case MapdustBug.ONEWAY_ROAD:
                arguments.add(new BasicNameValuePair("type", "oneway_road"));
                break;

            case MapdustBug.BLOCKED_STREET:
                arguments.add(new BasicNameValuePair("type", "blocked_street"));
                break;

            case MapdustBug.MISSING_STREET:
                arguments.add(new BasicNameValuePair("type", "missing_street"));
                break;

            case MapdustBug.ROUNDABOUT_ISSUE:
                arguments.add(new BasicNameValuePair("type", "wrong_roundabout"));
                break;

            case MapdustBug.MISSING_SPEED_INFO:
                arguments.add(new BasicNameValuePair("type", "missing_speedlimit"));
                break;

            case MapdustBug.OTHER:
                arguments.add(new BasicNameValuePair("type", "other"));
                break;

            default:
                return false;
        }
        arguments.add(new BasicNameValuePair("nickname", Settings.Mapdust.getUsername()));

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

    public static ArrayList<Comment> retrieveComments(long id)
    {
        ArrayList<Comment> comments = new ArrayList<>();

        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<>();

        arguments.add(new BasicNameValuePair("key", API_KEY));
        arguments.add(new BasicNameValuePair("id", String.valueOf(id)));

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
                return comments;

            /* If Request was Successful, parse the Stream */
            comments.addAll(MapdustParser.parseSingleBugForComments(response.getEntity().getContent()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return comments;
    }

    private static String getMapdustSelectionString() {
        String result = "";

        if (Settings.Mapdust.isWrongTurnEnabled())
            result += "wrong_turn,";
        if (Settings.Mapdust.isBadRoutingEnabled())
            result += "bad_routing,";
        if (Settings.Mapdust.isOnewayRoadEnabled())
            result += "oneway_road,";
        if (Settings.Mapdust.isBlockedStreetEnabled())
            result += "blocked_street,";
        if (Settings.Mapdust.isMissingStreetEnabled())
            result += "missing_street,";
        if (Settings.Mapdust.isRoundaboutIssueEnabled())
            result += "wrong_roundabout,";
        if (Settings.Mapdust.isMissingSpeedInfoEnabled())
            result += "missing_speedlimit,";
        if (Settings.Mapdust.isOtherEnabled())
            result += "other,";

        if (result.endsWith(","))
            result = result.substring(0, result.length() - 1);

        return result;
    }

    private static String getMapdustEnabledTypesString() {
        String result = "";

        if (Settings.Mapdust.isShowOpenEnabled())
            result += "1,";
        if (Settings.Mapdust.isShowClosedEnabled())
            result += "2,";
        if (Settings.Mapdust.isShowIgnoredEnabled())
            result += "3,";

        if (result.endsWith(","))
            result = result.substring(0, result.length() - 1);

        return result;
    }
}
