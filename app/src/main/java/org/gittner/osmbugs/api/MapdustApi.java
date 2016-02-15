package org.gittner.osmbugs.api;

import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapdustApi implements BugApi<MapdustBug>
{
    private static final String API_KEY = "ae58b0b4aa3f876265a4d5f29167b73c";

    public static OkHttpClient mOkHttpClient = new OkHttpClient();

    public ArrayList<MapdustBug> downloadBBox(BoundingBoxE6 bBox)
    {
        Request request = new Request.Builder()
                .url(!Settings.isDebugEnabled() ? "http://www.mapdust.com/api/getBugs?" : "http://st.www.mapdust.com/api/getBugs?")
                .post(new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("bbox", String.format(
                                Locale.US,
                                "%f,%f,%f,%f",
                                bBox.getLonEastE6() / 1000000.0,
                                bBox.getLatSouthE6() / 1000000.0,
                                bBox.getLonWestE6() / 1000000.0,
                                bBox.getLatNorthE6() / 1000000.0))
                        .add("comments", "1")
                        .add("ft", getMapdustSelectionString())
                        .add("fs", getMapdustEnabledTypesString())
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            /* No Content */
            if (response.code() == 204)
            {
                return new ArrayList<>();
            }

            if (response.code() != 200)
            {
                return null;
            }

            return MapdustParser.parse(response.body().byteStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private String getMapdustSelectionString()
    {
        String result = "";
        if (Settings.Mapdust.isWrongTurnEnabled())
        {
            result += "wrong_turn,";
        }
        if (Settings.Mapdust.isBadRoutingEnabled())
        {
            result += "bad_routing,";
        }
        if (Settings.Mapdust.isOnewayRoadEnabled())
        {
            result += "oneway_road,";
        }
        if (Settings.Mapdust.isBlockedStreetEnabled())
        {
            result += "blocked_street,";
        }
        if (Settings.Mapdust.isMissingStreetEnabled())
        {
            result += "missing_street,";
        }
        if (Settings.Mapdust.isRoundaboutIssueEnabled())
        {
            result += "wrong_roundabout,";
        }
        if (Settings.Mapdust.isMissingSpeedInfoEnabled())
        {
            result += "missing_speedlimit,";
        }
        if (Settings.Mapdust.isOtherEnabled())
        {
            result += "other,";
        }
        if (result.endsWith(","))
        {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }


    private String getMapdustEnabledTypesString()
    {
        String result = "";
        if (Settings.Mapdust.isShowOpenEnabled())
        {
            result += "1,";
        }
        if (Settings.Mapdust.isShowClosedEnabled())
        {
            result += "2,";
        }
        if (Settings.Mapdust.isShowIgnoredEnabled())
        {
            result += "3,";
        }
        if (result.endsWith(","))
        {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }


    public boolean commentBug(long id, String comment, String nickname)
    {
        Request request = new Request.Builder()
                .url(!Settings.isDebugEnabled() ? "http://www.mapdust.com/api/commentBug?" : "http://st.www.mapdust.com/api/commentBug?")
                .post(new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("id", String.valueOf(id))
                        .add("comment", comment)
                        .add("nickname", nickname)
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            /* Mapdust returns 201 for commentBug as Success */
            return response.code() != 201;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean changeBugStatus(long id, MapdustBug.STATE state, String comment, String username)
    {
        String sState = "";
        switch (state)
        {
            case OPEN:
                sState= "1";
                break;

            case CLOSED:
                sState = "2";
                break;

            default:
                sState = "3";
                break;
        }

        Request request = new Request.Builder()
                .url(!Settings.isDebugEnabled() ? "http://www.mapdust.com/api/changeBugStatus?" : "http://st.www.mapdust.com/api/changeBugStatus?")
                .post(new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("id", String.valueOf(id))
                        .add("comment", comment)
                        .add("nickname", username)
                        .add("status", sState)
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            /* Mapdust returns 201 for changeBugStatus as Success */
            return response.code() != 201;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean addBug(GeoPoint position, int type, String description)
    {
        String sType = "";
        switch (type)
        {
            case MapdustBug.WRONG_TURN:
                sType = "wrong_turn";
                break;

            case MapdustBug.BAD_ROUTING:
                sType = "bad_routing";
                break;

            case MapdustBug.ONEWAY_ROAD:
                sType = "oneway_road";
                break;

            case MapdustBug.BLOCKED_STREET:
                sType = "blocked_street";
                break;

            case MapdustBug.MISSING_STREET:
                sType = "missing_street";
                break;

            case MapdustBug.ROUNDABOUT_ISSUE:
                sType = "wrong_roundabout";
                break;

            case MapdustBug.MISSING_SPEED_INFO:
                sType = "missing_speedlimit";
                break;

            case MapdustBug.OTHER:
                sType = "other";
                break;

            default:
                return false;
        }

        Request request = new Request.Builder()
                .url(!Settings.isDebugEnabled() ? "http://www.mapdust.com/api/addBug?" : "http://st.www.mapdust.com/api/addBug?")
                .post(new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("coordinates", String.format(
                                Locale.US,
                                "%f,%f",
                                position.getLongitudeE6() / 1000000.0,
                                position.getLatitudeE6() / 1000000.0))
                        .add("description", description)
                        .add("nickname", Settings.Mapdust.getUsername())
                        .add("type", sType)
                        .add("nickname", Settings.Mapdust.getUsername())
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            /* Mapdust returns 201 for addBug as Success */
            return response.code() != 201;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public ArrayList<Comment> retrieveComments(long id)
    {
        ArrayList<Comment> comments = new ArrayList<>();

        Request request = new Request.Builder()
                .url(!Settings.isDebugEnabled() ? "http://www.mapdust.com/api/getBug?" : "http://st.www.mapdust.com/api/getBug?")
                .post(new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("id", String.valueOf(id))
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            if (response.code() != 200)
            {
                return comments;
            }

            /* If Request was Successful, parse the Stream */
            comments.addAll(MapdustParser.parseSingleBugForComments(response.body().byteStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return comments;
    }
}
