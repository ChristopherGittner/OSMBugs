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
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.parser.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by christopher on 3/20/14.
 */
public abstract class DownloadOpenstreetmapNotesTask extends AsyncTask<BoundingBoxE6, Void, ArrayList<OpenstreetmapNote>> {

    @Override
    protected ArrayList<OpenstreetmapNote> doInBackground(BoundingBoxE6... bBoxes) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBoxes[0].getLonWestE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLatSouthE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLonEastE6() / 1000000.0) + ","
                + String.valueOf(bBoxes[0].getLatNorthE6() / 1000000.0)));

        if (Settings.OpenstreetmapNotes.isShowOnlyOpenEnabled())
            arguments.add(new BasicNameValuePair("closed", "0"));

        arguments.add(new BasicNameValuePair("limit",
                String.valueOf(Settings.OpenstreetmapNotes.getBugLimit())));

        HttpGet request;

        if (!Settings.isDebugEnabled())
            request = new HttpGet("http://api.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://api06.dev.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return null;

            /* If Request was Successful, parse the Stream */
            return OpenstreetmapNotesParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected abstract void onPostExecute(ArrayList<OpenstreetmapNote> bugs);
}