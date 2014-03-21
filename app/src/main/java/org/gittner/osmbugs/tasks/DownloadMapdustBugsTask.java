package org.gittner.osmbugs.tasks;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetbugsBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.parser.KeeprightParser;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.parser.OpenstreetbugsParser;
import org.gittner.osmbugs.parser.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by christopher on 3/20/14.
 */
public abstract class DownloadMapdustBugsTask extends AsyncTask<BoundingBoxE6, Void, ArrayList<MapdustBug>> {

    @Override
    protected ArrayList<MapdustBug> doInBackground(BoundingBoxE6... bBoxes) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBoxes[0].getLonEastE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLatSouthE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLonWestE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLatNorthE6() / 1000000.0)));
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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected abstract void onPostExecute(ArrayList<MapdustBug> bugs);

    private String getMapdustSelectionString() {
        String result = "";

        if (Settings.Mapdust.isWrongTurnEnabled())
            result += "wrong_turn,";
        if (Settings.Mapdust.isBadRoutingenabled())
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

    private String getMapdustEnabledTypesString() {
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