package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.Comment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class OsmoseParser {

    public static ArrayList<OsmoseBug> parse(InputStream stream) {
        ArrayList<OsmoseBug> bugs = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));

            String line = reader.readLine();

            JSONObject json = new JSONObject(line);

            JSONArray bugArray = json.getJSONArray("errors");

            for (int i = 0; i != bugArray.length(); ++i) {
                JSONArray bug = bugArray.getJSONArray(i);

                double lat = Double.valueOf(bug.getString(0));
                double lon = Double.valueOf(bug.getString(1));
                long id = Long.valueOf(bug.getString(2));
                int item = Integer.valueOf(bug.getString(3));
                String title = bug.getString(9);

                bugs.add(new OsmoseBug(lat, lon, id, item, title));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return bugs;
    }
}
