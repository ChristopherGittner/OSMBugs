package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.Comment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapdustParser
{
    public static ArrayList<MapdustBug> parse(InputStream stream)
    {
        ArrayList<MapdustBug> bugs = new ArrayList<>();
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));
            String line = reader.readLine();

            /* Little Tweak since this was sometimes missing in Answer. Only on Android not on Desktop */
            if (!line.endsWith("\"}}"))
            {
                line += "\"}}";
            }
            JSONObject json = new JSONObject(line);
            JSONArray bugArray = json.getJSONArray("features");
            for (int i = 0; i != bugArray.length(); ++i)
            {
                JSONObject bug = bugArray.getJSONObject(i);
                long id = bug.getLong("id");

                JSONObject geometry = bug.getJSONObject("geometry");
                double lon = geometry.getJSONArray("coordinates").getDouble(0);
                double lat = geometry.getJSONArray("coordinates").getDouble(1);

                JSONObject property = bug.getJSONObject("properties");
                MapdustBug.STATE state;
                switch (property.getInt("status"))
                {
                    case 2:
                        state = MapdustBug.STATE.CLOSED;
                        break;
                    case 3:
                        state = MapdustBug.STATE.IGNORED;
                        break;
                    default:
                        state = MapdustBug.STATE.OPEN;
                        break;
                }

                ArrayList<Comment> comments = new ArrayList<>();
                JSONArray commentArray = property.getJSONArray("comments");
                for (int n = 0; n != commentArray.length(); ++n)
                {
                    JSONObject comment = commentArray.getJSONObject(n);
                    comments.add(new Comment(comment.getString("comment")));
                }

                String description = property.getString("description");
                String type_const = property.getString("type");

                int typeInt;
                switch (type_const)
                {
                    case "wrong_turn":
                        typeInt = MapdustBug.WRONG_TURN;
                        break;
                    case "bad_routing":
                        typeInt = MapdustBug.BAD_ROUTING;
                        break;
                    case "oneway_road":
                        typeInt = MapdustBug.ONEWAY_ROAD;
                        break;
                    case "blocked_street":
                        typeInt = MapdustBug.BLOCKED_STREET;
                        break;
                    case "missing_street":
                        typeInt = MapdustBug.MISSING_STREET;
                        break;
                    case "wrong_roundabout":
                        typeInt = MapdustBug.ROUNDABOUT_ISSUE;
                        break;
                    case "missing_speedlimit":
                        typeInt = MapdustBug.MISSING_SPEED_INFO;
                        break;
                    default:
                        typeInt = MapdustBug.OTHER;
                        break;
                }

                bugs.add(new MapdustBug(lat, lon, id, typeInt, description, comments, state));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return bugs;
    }


    public static ArrayList<Comment> parseSingleBugForComments(InputStream stream)
    {
        ArrayList<Comment> comments = new ArrayList<>();
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));
            String line = reader.readLine();

            /*
             * Little Tweak since this was sometimes missing in Answer. Only on Android not on
             * Desktop
             */
            if (!line.endsWith("\"}}"))
            {
                line += "\"}}";
            }

            JSONObject json = new JSONObject(line);

            JSONObject properties = json.getJSONObject("properties");

            JSONArray commentsArray = properties.getJSONArray("comments");
            for (int i = 0; i != commentsArray.length(); ++i)
            {
                comments.add(new Comment(
                        commentsArray.getJSONObject(i).getString("comment"),
                        commentsArray.getJSONObject(i).getString("nickname")));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return comments;
    }
}
